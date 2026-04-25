#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
主测试运行脚本
支持运行所有测试、指定标记测试、并行执行、失败重试、报告生成等功能
"""
import argparse
import json
import os
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple

import requests

from tests.test_runner import (
    TestConfiguration,
    TestRunner,
    TestSuite,
    TestModule,
    TestMarker,
    Environment,
    ExecutionMode,
    create_test_runner,
)


class Colors:
    RED = '\033[91m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    MAGENTA = '\033[95m'
    CYAN = '\033[96m'
    WHITE = '\033[97m'
    RESET = '\033[0m'
    BOLD = '\033[1m'


class TestRunnerCLI:
    def __init__(self):
        self.project_root = Path(__file__).parent
        self.tests_dir = self.project_root / "tests"
        self.report_dir = self.tests_dir / "report"
        self.allure_results_dir = self.report_dir / "allure-results"
        self.coverage_dir = self.report_dir / "coverage"
        
        self.report_dir.mkdir(parents=True, exist_ok=True)
        self.allure_results_dir.mkdir(parents=True, exist_ok=True)
        self.coverage_dir.mkdir(parents=True, exist_ok=True)
        
        self.results = {
            "total": 0,
            "passed": 0,
            "failed": 0,
            "skipped": 0,
            "errors": 0,
            "duration": 0,
            "failed_tests": [],
            "retry_count": 0
        }
    
    def print_banner(self, text: str, color: str = Colors.CYAN):
        width = 70
        print(f"\n{color}{Colors.BOLD}")
        print("=" * width)
        print(f"  {text.center(width - 4)}")
        print("=" * width)
        print(f"{Colors.RESET}\n")
    
    def print_section(self, text: str, color: str = Colors.BLUE):
        print(f"\n{color}{Colors.BOLD}[{text}]{Colors.RESET}")
        print("-" * 60)
    
    def check_dependencies(self) -> bool:
        self.print_section("检查依赖", Colors.BLUE)
        
        required_packages = [
            "pytest", "requests", "pytest-html", "pytest-cov",
            "allure-pytest", "python-dotenv", "pytest-xdist",
            "pytest-rerunfailures"
        ]
        
        missing_packages = []
        for package in required_packages:
            try:
                module_name = package.replace("-", "_")
                if package == "pytest-html":
                    module_name = "pytest_html"
                elif package == "pytest-xdist":
                    module_name = "xdist"
                elif package == "pytest-rerunfailures":
                    module_name = "rerunfailures"
                __import__(module_name)
                print(f"  {Colors.GREEN}✓{Colors.RESET} {package}")
            except ImportError:
                print(f"  {Colors.RED}✗{Colors.RESET} {package} (缺失)")
                missing_packages.append(package)
        
        if missing_packages:
            print(f"\n{Colors.YELLOW}检测到缺失的依赖包，正在安装...{Colors.RESET}")
            try:
                subprocess.run(
                    [sys.executable, "-m", "pip", "install"] + missing_packages,
                    check=True,
                    capture_output=True
                )
                print(f"{Colors.GREEN}依赖安装完成{Colors.RESET}")
                return True
            except subprocess.CalledProcessError as e:
                print(f"{Colors.RED}依赖安装失败: {e}{Colors.RESET}")
                return False
        
        return True
    
    def check_backend_service(self, base_url: str = "http://localhost:8080") -> bool:
        self.print_section("检查后端服务", Colors.BLUE)
        
        try:
            response = requests.get(f"{base_url}/api/health", timeout=5)
            if response.status_code == 200:
                print(f"  {Colors.GREEN}✓{Colors.RESET} 后端服务运行中: {base_url}")
                return True
        except requests.exceptions.RequestException:
            pass
        
        print(f"  {Colors.YELLOW}⚠{Colors.RESET} 后端服务未运行: {base_url}")
        
        start_service = input(f"\n  是否启动后端服务? (y/n): ").strip().lower()
        if start_service == 'y':
            return self.start_backend_service()
        
        print(f"  {Colors.YELLOW}将继续运行测试，但可能会失败{Colors.RESET}")
        return True
    
    def start_backend_service(self) -> bool:
        self.print_section("启动后端服务", Colors.BLUE)
        
        try:
            if (self.project_root / "docker-compose.yml").exists():
                print("  使用 Docker Compose 启动服务...")
                subprocess.Popen(
                    ["docker-compose", "up", "-d"],
                    cwd=str(self.project_root),
                    stdout=subprocess.DEVNULL,
                    stderr=subprocess.DEVNULL
                )
                time.sleep(10)
                return self.check_backend_service()
            elif (self.project_root / "mvnw").exists():
                print("  使用 Maven 启动服务...")
                subprocess.Popen(
                    [str(self.project_root / "mvnw"), "spring-boot:run"],
                    cwd=str(self.project_root),
                    stdout=subprocess.DEVNULL,
                    stderr=subprocess.DEVNULL
                )
                time.sleep(15)
                return self.check_backend_service()
            else:
                print(f"  {Colors.RED}无法自动启动后端服务{Colors.RESET}")
                return False
        except Exception as e:
            print(f"  {Colors.RED}启动后端服务失败: {e}{Colors.RESET}")
            return False
    
    def init_test_data(self) -> bool:
        self.print_section("初始化测试数据", Colors.BLUE)
        
        try:
            from tests.fixtures.test_data import TestDataInitializer
            initializer = TestDataInitializer()
            initializer.init_all()
            print(f"  {Colors.GREEN}✓{Colors.RESET} 测试数据初始化完成")
            return True
        except Exception as e:
            print(f"  {Colors.YELLOW}⚠{Colors.RESET} 测试数据初始化跳过: {e}")
            return True
    
    def build_pytest_command(
        self,
        test_paths: List[str] = None,
        markers: List[str] = None,
        parallel: int = 1,
        retry: int = 0,
        retry_delay: int = 2,
        coverage: bool = False,
        report: bool = False,
        verbose: bool = True,
        load_balance: bool = False
    ) -> List[str]:
        cmd = [sys.executable, "-m", "pytest"]
        
        if test_paths:
            cmd.extend(test_paths)
        else:
            cmd.append(str(self.tests_dir / "test_*.py"))
        
        cmd.extend([
            "-v" if verbose else "-q",
            "--tb=short",
            f"--alluredir={self.allure_results_dir}",
            f"--html={self.report_dir / 'report.html'}",
            "--self-contained-html",
        ])
        
        if markers:
            marker_expr = " or ".join(markers)
            cmd.extend(["-m", marker_expr])
        
        if parallel > 1:
            cmd.extend(["-n", str(parallel)])
            if load_balance:
                cmd.append("--dist=loadfile")
        
        if retry > 0:
            cmd.extend([
                "--reruns", str(retry),
                "--reruns-delay", str(retry_delay)
            ])
        
        if coverage:
            cmd.extend([
                "--cov=src",
                f"--cov-report=html:{self.coverage_dir}",
                f"--cov-report=xml:{self.coverage_dir / 'coverage.xml'}",
                "--cov-report=term-missing"
            ])
        
        cmd.extend([
            "-W", "ignore::DeprecationWarning",
            "-p", "no:warnings"
        ])
        
        return cmd
    
    def run_tests(
        self,
        test_paths: List[str] = None,
        markers: List[str] = None,
        parallel: int = 1,
        retry: int = 0,
        retry_delay: int = 2,
        coverage: bool = False,
        report: bool = False,
        load_balance: bool = False
    ) -> Tuple[int, Dict]:
        self.print_banner("开始执行测试")
        
        cmd = self.build_pytest_command(
            test_paths=test_paths,
            markers=markers,
            parallel=parallel,
            retry=retry,
            retry_delay=retry_delay,
            coverage=coverage,
            report=report,
            load_balance=load_balance
        )
        
        print(f"\n{Colors.CYAN}执行命令:{Colors.RESET}")
        print(f"  {' '.join(cmd)}\n")
        
        start_time = time.time()
        
        try:
            result = subprocess.run(
                cmd,
                cwd=str(self.project_root),
                capture_output=False,
                text=True
            )
            
            end_time = time.time()
            duration = end_time - start_time
            
            self.results["duration"] = duration
            self.results["return_code"] = result.returncode
            
            return result.returncode, self.results
            
        except Exception as e:
            print(f"\n{Colors.RED}测试执行失败: {e}{Colors.RESET}")
            return 1, self.results
    
    def parse_test_results(self) -> Dict:
        results_file = self.allure_results_dir / "results.json"
        if results_file.exists():
            with open(results_file, 'r', encoding='utf-8') as f:
                return json.load(f)
        
        return {
            "total": self.results.get("total", 0),
            "passed": self.results.get("passed", 0),
            "failed": self.results.get("failed", 0),
            "skipped": self.results.get("skipped", 0),
            "duration": self.results.get("duration", 0)
        }
    
    def print_summary(self, results: Dict):
        self.print_banner("测试结果汇总")
        
        total = results.get("total", 0)
        passed = results.get("passed", 0)
        failed = results.get("failed", 0)
        skipped = results.get("skipped", 0)
        errors = results.get("errors", 0)
        duration = results.get("duration", 0)
        
        pass_rate = (passed / total * 100) if total > 0 else 0
        
        print(f"\n{Colors.BOLD}测试统计:{Colors.RESET}")
        print(f"  总测试数:   {total}")
        print(f"  {Colors.GREEN}通过:       {passed}{Colors.RESET}")
        print(f"  {Colors.RED}失败:       {failed}{Colors.RESET}")
        print(f"  {Colors.YELLOW}跳过:       {skipped}{Colors.RESET}")
        print(f"  {Colors.MAGENTA}错误:       {errors}{Colors.RESET}")
        print(f"  通过率:     {pass_rate:.1f}%")
        print(f"  执行时间:   {duration:.2f}秒")
        
        if failed > 0:
            print(f"\n{Colors.RED}{Colors.BOLD}失败的测试:{Colors.RESET}")
            for test in results.get("failed_tests", []):
                print(f"  - {test}")
        
        print(f"\n{Colors.BOLD}报告位置:{Colors.RESET}")
        print(f"  HTML报告:   {self.report_dir / 'report.html'}")
        print(f"  Allure报告: {self.allure_results_dir}")
        if Path(self.coverage_dir).exists():
            print(f"  覆盖率报告: {self.coverage_dir / 'index.html'}")
    
    def generate_allure_report(self) -> bool:
        self.print_section("生成Allure报告", Colors.BLUE)
        
        try:
            subprocess.run(
                ["allure", "generate", str(self.allure_results_dir), "-o", str(self.report_dir / "allure-report"), "--clean"],
                check=True,
                capture_output=True
            )
            print(f"  {Colors.GREEN}✓{Colors.RESET} Allure报告生成成功")
            print(f"  位置: {self.report_dir / 'allure-report' / 'index.html'}")
            return True
        except FileNotFoundError:
            print(f"  {Colors.YELLOW}⚠{Colors.RESET} Allure命令行工具未安装")
            print(f"  安装方法: npm install -g allure-commandline")
            return False
        except subprocess.CalledProcessError as e:
            print(f"  {Colors.RED}✗{Colors.RESET} Allure报告生成失败: {e}")
            return False
    
    def cleanup(self):
        self.print_section("清理测试数据", Colors.BLUE)
        
        try:
            from tests.fixtures.test_data import TestDataInitializer
            initializer = TestDataInitializer()
            initializer.cleanup()
            print(f"  {Colors.GREEN}✓{Colors.RESET} 测试数据清理完成")
        except Exception as e:
            print(f"  {Colors.YELLOW}⚠{Colors.RESET} 测试数据清理跳过: {e}")
    
    def run_with_config(self, config: TestConfiguration) -> int:
        test_paths = None
        markers = []
        
        if config.suite:
            suite_dir = self.tests_dir / config.suite.value
            if suite_dir.exists():
                test_paths = [str(suite_dir)]
        
        if config.module:
            markers.append(config.module.value)
        
        if config.marker:
            markers.append(config.marker.value)
        
        return_code, results = self.run_tests(
            test_paths=test_paths,
            markers=markers if markers else None,
            parallel=config.parallel,
            retry=config.retry,
            retry_delay=config.retry_delay,
            coverage=config.coverage,
            report=True,
            load_balance=config.parallel > 1
        )
        
        return return_code


def parse_arguments():
    parser = argparse.ArgumentParser(
        description="宠物服务平台测试运行脚本",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例用法:
  python run_tests.py --all                    # 运行所有测试
  python run_tests.py --suite unit             # 只运行单元测试
  python run_tests.py --suite integration      # 只运行集成测试
  python run_tests.py --module user            # 只运行用户端测试
  python run_tests.py --module merchant        # 只运行商家端测试
  python run_tests.py --marker smoke           # 只运行冒烟测试
  python run_tests.py --marker regression      # 只运行回归测试
  python run_tests.py --env test               # 使用测试环境配置
  python run_tests.py --parallel 4             # 并行执行测试（4个进程）
  python run_tests.py --retry 3                # 失败重试3次
  python run_tests.py --report --coverage      # 生成HTML报告和覆盖率报告
  python run_tests.py --config test_config.json # 从配置文件加载
        """
    )
    
    test_group = parser.add_argument_group("测试选择")
    test_group.add_argument(
        "--suite", type=str, choices=[s.value for s in TestSuite],
        help="选择测试套件 (unit/integration/performance/security)"
    )
    test_group.add_argument(
        "--module", type=str, choices=[m.value for m in TestModule],
        help="选择测试模块 (user/merchant/admin/public)"
    )
    test_group.add_argument(
        "--marker", type=str, choices=[m.value for m in TestMarker],
        help="选择测试标记 (smoke/regression/auth/appointment/product_order/isolation/performance/security)"
    )
    test_group.add_argument(
        "--all", action="store_true",
        help="运行所有测试"
    )
    
    env_group = parser.add_argument_group("环境配置")
    env_group.add_argument(
        "--env", type=str, choices=[e.value for e in Environment], default='dev',
        help="选择测试环境 (dev/test/prod)"
    )
    env_group.add_argument(
        "--config", type=str,
        help="从配置文件加载测试配置 (JSON格式)"
    )
    env_group.add_argument(
        "--base-url", type=str, default="http://localhost:8080",
        help="后端服务地址 (默认: http://localhost:8080)"
    )
    
    exec_group = parser.add_argument_group("执行选项")
    exec_group.add_argument(
        "--parallel", type=int, default=1,
        help="并行执行数量 (默认: 1)"
    )
    exec_group.add_argument(
        "--retry", type=int, default=0,
        help="失败重试次数 (默认: 0)"
    )
    exec_group.add_argument(
        "--retry-delay", type=int, default=2,
        help="失败重试延迟秒数 (默认: 2)"
    )
    exec_group.add_argument(
        "--load-balance", action="store_true",
        help="启用负载均衡（并行执行时）"
    )
    exec_group.add_argument(
        "--verbose", "-v", action="store_true",
        help="详细输出"
    )
    
    report_group = parser.add_argument_group("报告选项")
    report_group.add_argument(
        "--report", action="store_true",
        help="生成HTML报告"
    )
    report_group.add_argument(
        "--coverage", action="store_true",
        help="生成覆盖率报告"
    )
    report_group.add_argument(
        "--allure", action="store_true",
        help="生成Allure报告 (需要安装allure命令行工具)"
    )
    report_group.add_argument(
        "--report-path", type=str,
        help="报告输出路径"
    )
    
    other_group = parser.add_argument_group("其他选项")
    other_group.add_argument(
        "--skip-deps", action="store_true",
        help="跳过依赖检查"
    )
    other_group.add_argument(
        "--skip-backend-check", action="store_true",
        help="跳过后端服务检查"
    )
    other_group.add_argument(
        "--skip-init", action="store_true",
        help="跳过测试数据初始化"
    )
    other_group.add_argument(
        "--cleanup", action="store_true",
        help="测试完成后清理测试数据"
    )
    other_group.add_argument(
        "--notify", type=str,
        help="测试完成后发送通知的Webhook URL"
    )
    
    return parser.parse_args()


def main():
    args = parse_arguments()
    
    cli = TestRunnerCLI()
    
    cli.print_banner("宠物服务平台测试运行器", Colors.MAGENTA)
    
    if not args.skip_deps:
        if not cli.check_dependencies():
            sys.exit(1)
    
    if not args.skip_backend_check:
        if not cli.check_backend_service(args.base_url):
            sys.exit(1)
    
    if not args.skip_init:
        cli.init_test_data()
    
    config = TestConfiguration(
        suite=TestSuite(args.suite) if args.suite else None,
        module=TestModule(args.module) if args.module else None,
        marker=TestMarker(args.marker) if args.marker else None,
        env=Environment(args.env),
        parallel=args.parallel,
        retry=args.retry,
        retry_delay=args.retry_delay,
        verbose=args.verbose,
        coverage=args.coverage,
        base_url=args.base_url,
        report_path=Path(args.report_path) if args.report_path else None,
    )
    
    if args.config:
        config.load_from_file(args.config)
    
    config.load_from_env()
    
    if not config.validate():
        print(f"{Colors.RED}配置验证失败{Colors.RESET}")
        sys.exit(1)
    
    if args.all:
        config.suite = None
        config.module = None
        config.marker = None
    
    return_code = cli.run_with_config(config)
    
    if args.allure:
        cli.generate_allure_report()
    
    cli.print_summary(cli.results)
    
    if args.cleanup:
        cli.cleanup()
    
    if args.notify:
        try:
            import requests
            stats = cli.results
            message = {
                "text": "测试执行完成",
                "attachments": [
                    {
                        "color": "good" if stats.get('passed', 0) > stats.get('failed', 0) else "danger",
                        "fields": [
                            {"title": "总测试数", "value": str(stats.get('total', 0)), "short": True},
                            {"title": "通过数", "value": str(stats.get('passed', 0)), "short": True},
                            {"title": "失败数", "value": str(stats.get('failed', 0)), "short": True},
                            {"title": "执行时间", "value": f"{stats.get('duration', 0):.2f}秒", "short": True},
                        ]
                    }
                ]
            }
            response = requests.post(args.notify, json=message, timeout=10)
            if response.status_code == 200:
                print(f"{Colors.GREEN}通知发送成功{Colors.RESET}")
            else:
                print(f"{Colors.RED}通知发送失败: {response.status_code}{Colors.RESET}")
        except Exception as e:
            print(f"{Colors.RED}通知发送失败: {e}{Colors.RESET}")
    
    cli.print_banner("测试执行完成", Colors.GREEN if return_code == 0 else Colors.RED)
    
    sys.exit(return_code)


if __name__ == "__main__":
    main()

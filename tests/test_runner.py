#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
测试运行器模块
提供测试配置、执行、结果收集和报告生成功能
"""

import json
import os
import subprocess
import sys
import time
from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from pathlib import Path
from typing import Any, Dict, List, Optional, Union


class TestSuite(Enum):
    UNIT = "unit"
    INTEGRATION = "integration"
    PERFORMANCE = "performance"
    SECURITY = "security"


class TestModule(Enum):
    USER = "user"
    MERCHANT = "merchant"
    ADMIN = "admin"
    PUBLIC = "public"


class TestMarker(Enum):
    SMOKE = "smoke"
    REGRESSION = "regression"
    AUTH = "auth"
    APPOINTMENT = "appointment"
    PRODUCT_ORDER = "product_order"
    ISOLATION = "isolation"
    PERFORMANCE = "performance"
    SECURITY = "security"


class ExecutionMode(Enum):
    LOCAL = "local"
    DISTRIBUTED = "distributed"
    CI_CD = "ci_cd"


class Environment(Enum):
    DEV = "dev"
    TEST = "test"
    PROD = "prod"


@dataclass
class TestConfiguration:
    suite: Optional[TestSuite] = None
    module: Optional[TestModule] = None
    marker: Optional[TestMarker] = None
    env: Environment = Environment.DEV
    parallel: int = 1
    retry: int = 0
    retry_delay: int = 2
    verbose: bool = True
    coverage: bool = False
    report_path: Optional[Path] = None
    base_url: str = "http://localhost:8080"
    timeout: int = 30
    execution_mode: ExecutionMode = ExecutionMode.LOCAL
    custom_config: Dict[str, Any] = field(default_factory=dict)

    def load_from_file(self, path: Union[str, Path]) -> "TestConfiguration":
        config_path = Path(path)
        if not config_path.exists():
            raise FileNotFoundError(f"配置文件不存在: {config_path}")

        with open(config_path, "r", encoding="utf-8") as f:
            config_data = json.load(f)

        if "suite" in config_data:
            self.suite = TestSuite(config_data["suite"])
        if "module" in config_data:
            self.module = TestModule(config_data["module"])
        if "marker" in config_data:
            self.marker = TestMarker(config_data["marker"])
        if "env" in config_data:
            self.env = Environment(config_data["env"])
        if "parallel" in config_data:
            self.parallel = config_data["parallel"]
        if "retry" in config_data:
            self.retry = config_data["retry"]
        if "retry_delay" in config_data:
            self.retry_delay = config_data["retry_delay"]
        if "verbose" in config_data:
            self.verbose = config_data["verbose"]
        if "coverage" in config_data:
            self.coverage = config_data["coverage"]
        if "report_path" in config_data:
            self.report_path = Path(config_data["report_path"])
        if "base_url" in config_data:
            self.base_url = config_data["base_url"]
        if "timeout" in config_data:
            self.timeout = config_data["timeout"]
        if "execution_mode" in config_data:
            self.execution_mode = ExecutionMode(config_data["execution_mode"])
        if "custom" in config_data:
            self.custom_config = config_data["custom"]

        return self

    def load_from_env(self) -> "TestConfiguration":
        env_mapping = {
            "TEST_SUITE": ("suite", TestSuite),
            "TEST_MODULE": ("module", TestModule),
            "TEST_MARKER": ("marker", TestMarker),
            "TEST_ENV": ("env", Environment),
            "TEST_PARALLEL": ("parallel", int),
            "TEST_RETRY": ("retry", int),
            "TEST_RETRY_DELAY": ("retry_delay", int),
            "TEST_VERBOSE": ("verbose", lambda x: x.lower() == "true"),
            "TEST_COVERAGE": ("coverage", lambda x: x.lower() == "true"),
            "TEST_REPORT_PATH": ("report_path", Path),
            "TEST_BASE_URL": ("base_url", str),
            "TEST_TIMEOUT": ("timeout", int),
            "TEST_EXECUTION_MODE": ("execution_mode", ExecutionMode),
        }

        for env_key, (attr, converter) in env_mapping.items():
            value = os.getenv(env_key)
            if value is not None:
                try:
                    converted_value = converter(value)
                    setattr(self, attr, converted_value)
                except (ValueError, KeyError) as e:
                    print(f"警告: 无法转换环境变量 {env_key}={value}: {e}")

        return self

    def validate(self) -> bool:
        errors = []

        if self.parallel < 1:
            errors.append("并行进程数必须大于等于1")

        if self.retry < 0:
            errors.append("重试次数不能为负数")

        if self.retry_delay < 0:
            errors.append("重试延迟不能为负数")

        if self.timeout < 1:
            errors.append("超时时间必须大于0秒")

        if not self.base_url.startswith(("http://", "https://")):
            errors.append("base_url 必须以 http:// 或 https:// 开头")

        if errors:
            for error in errors:
                print(f"配置错误: {error}")
            return False

        return True

    def to_dict(self) -> Dict[str, Any]:
        return {
            "suite": self.suite.value if self.suite else None,
            "module": self.module.value if self.module else None,
            "marker": self.marker.value if self.marker else None,
            "env": self.env.value,
            "parallel": self.parallel,
            "retry": self.retry,
            "retry_delay": self.retry_delay,
            "verbose": self.verbose,
            "coverage": self.coverage,
            "report_path": str(self.report_path) if self.report_path else None,
            "base_url": self.base_url,
            "timeout": self.timeout,
            "execution_mode": self.execution_mode.value,
            "custom_config": self.custom_config,
        }


@dataclass
class TestResultItem:
    test_id: str
    test_name: str
    status: str
    duration: float = 0.0
    error_message: Optional[str] = None
    error_traceback: Optional[str] = None
    retry_count: int = 0
    timestamp: str = field(default_factory=lambda: datetime.now().isoformat())


@dataclass
class TestResult:
    results: List[TestResultItem] = field(default_factory=list)
    start_time: Optional[float] = None
    end_time: Optional[float] = None

    def add_result(self, test_id: str, result: TestResultItem) -> None:
        self.results.append(result)

    def get_statistics(self) -> Dict[str, Any]:
        total = len(self.results)
        passed = sum(1 for r in self.results if r.status == "passed")
        failed = sum(1 for r in self.results if r.status == "failed")
        skipped = sum(1 for r in self.results if r.status == "skipped")
        error = sum(1 for r in self.results if r.status == "error")

        pass_rate = (passed / total * 100) if total > 0 else 0

        duration = 0.0
        if self.start_time and self.end_time:
            duration = self.end_time - self.start_time

        return {
            "total": total,
            "passed": passed,
            "failed": failed,
            "skipped": skipped,
            "error": error,
            "pass_rate": round(pass_rate, 2),
            "duration": round(duration, 2),
            "start_time": self.start_time,
            "end_time": self.end_time,
        }

    def get_failed_tests(self) -> List[TestResultItem]:
        return [r for r in self.results if r.status in ("failed", "error")]

    def export(self, format: str = "json", output_path: Optional[Path] = None) -> str:
        if format == "json":
            data = {
                "statistics": self.get_statistics(),
                "results": [
                    {
                        "test_id": r.test_id,
                        "test_name": r.test_name,
                        "status": r.status,
                        "duration": r.duration,
                        "error_message": r.error_message,
                        "error_traceback": r.error_traceback,
                        "retry_count": r.retry_count,
                        "timestamp": r.timestamp,
                    }
                    for r in self.results
                ],
            }
            output = json.dumps(data, indent=2, ensure_ascii=False)

        elif format == "junit":
            lines = ['<?xml version="1.0" encoding="UTF-8"?>']
            stats = self.get_statistics()
            lines.append(
                f'<testsuite tests="{stats["total"]}" '
                f'failures="{stats["failed"]}" '
                f'errors="{stats["error"]}" '
                f'skipped="{stats["skipped"]}" '
                f'time="{stats["duration"]}">'
            )

            for r in self.results:
                lines.append(f'  <testcase name="{r.test_name}" classname="{r.test_id}" time="{r.duration}">')
                if r.status in ("failed", "error"):
                    error_type = "error" if r.status == "error" else "failure"
                    error_msg = r.error_message or ""
                    error_tb = r.error_traceback or ""
                    lines.append(f'    <{error_type} message="{error_msg}"><![CDATA[{error_tb}]]></{error_type}>')
                elif r.status == "skipped":
                    lines.append("    <skipped/>")
                lines.append("  </testcase>")

            lines.append("</testsuite>")
            output = "\n".join(lines)

        else:
            raise ValueError(f"不支持的导出格式: {format}")

        if output_path:
            output_path = Path(output_path)
            output_path.parent.mkdir(parents=True, exist_ok=True)
            with open(output_path, "w", encoding="utf-8") as f:
                f.write(output)

        return output


class TestRunner:
    def __init__(self, config: TestConfiguration):
        self.config = config
        self.project_root = Path(__file__).parent.parent
        self.tests_dir = self.project_root / "tests"
        self.report_dir = self.tests_dir / "report"
        self.allure_results_dir = self.report_dir / "allure-results"
        self.coverage_dir = self.report_dir / "coverage"

        self.report_dir.mkdir(parents=True, exist_ok=True)
        self.allure_results_dir.mkdir(parents=True, exist_ok=True)
        self.coverage_dir.mkdir(parents=True, exist_ok=True)

        self.result = TestResult()

    def load_tests(
        self,
        suite: Optional[TestSuite] = None,
        module: Optional[TestModule] = None,
        marker: Optional[TestMarker] = None,
    ) -> List[str]:
        test_paths = []

        target_suite = suite or self.config.suite
        target_module = module or self.config.module
        target_marker = marker or self.config.marker

        if target_suite:
            suite_dir = self.tests_dir / target_suite.value
            if suite_dir.exists():
                test_paths.append(str(suite_dir))

        if target_module:
            for suite_dir in self.tests_dir.iterdir():
                if suite_dir.is_dir() and suite_dir.name not in (
                    "report",
                    "logs",
                    "testdata",
                    "fixtures",
                    "assertions",
                    "performance",
                    "__pycache__",
                ):
                    for test_file in suite_dir.glob(f"*{target_module.value}*.py"):
                        test_paths.append(str(test_file))

        if not test_paths:
            test_paths.append(str(self.tests_dir))

        return test_paths

    def run_tests(self, parallel: Optional[int] = None, retry: Optional[int] = None) -> int:
        target_parallel = parallel or self.config.parallel
        target_retry = retry or self.config.retry

        cmd = self._build_pytest_command(target_parallel, target_retry)

        print(f"\n执行命令: {' '.join(cmd)}\n")

        self.result.start_time = time.time()

        try:
            process = subprocess.Popen(
                cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, cwd=str(self.project_root)
            )

            for line in process.stdout:
                print(line, end="")
                self._parse_test_output(line)

            process.wait()
            return_code = process.returncode

        except Exception as e:
            print(f"测试执行失败: {e}")
            return_code = 1

        self.result.end_time = time.time()

        return return_code

    def _build_pytest_command(self, parallel: int, retry: int) -> List[str]:
        cmd = [sys.executable, "-m", "pytest"]

        test_paths = self.load_tests()
        cmd.extend(test_paths)

        cmd.extend(
            [
                "-v" if self.config.verbose else "-q",
                "--tb=short",
                f"--alluredir={self.allure_results_dir}",
                f"--html={self.report_dir / 'report.html'}",
                "--self-contained-html",
            ]
        )

        if self.config.marker:
            cmd.extend(["-m", self.config.marker.value])

        if parallel > 1:
            cmd.extend(["-n", str(parallel), "--dist=loadfile"])

        if retry > 0:
            cmd.extend(["--reruns", str(retry), "--reruns-delay", str(self.config.retry_delay)])

        if self.config.coverage:
            cmd.extend(
                [
                    "--cov=src",
                    f"--cov-report=html:{self.coverage_dir}",
                    f"--cov-report=xml:{self.coverage_dir / 'coverage.xml'}",
                    "--cov-report=term-missing",
                ]
            )

        cmd.extend(["-W", "ignore::DeprecationWarning", "-p", "no:warnings"])

        return cmd

    def _parse_test_output(self, line: str) -> None:
        if "::" in line and ("PASSED" in line or "FAILED" in line or "SKIPPED" in line or "ERROR" in line):
            parts = line.split("::")
            if len(parts) >= 2:
                test_id = parts[-1].split()[0]
                test_name = test_id

                if "PASSED" in line:
                    status = "passed"
                elif "FAILED" in line:
                    status = "failed"
                elif "SKIPPED" in line:
                    status = "skipped"
                elif "ERROR" in line:
                    status = "error"
                else:
                    status = "unknown"

                result_item = TestResultItem(test_id=test_id, test_name=test_name, status=status)
                self.result.add_result(test_id, result_item)

    def collect_results(self) -> TestResult:
        return self.result

    def generate_report(self, format: str = "html", output_path: Optional[Path] = None) -> Path:
        if format == "html":
            report_path = output_path or (self.report_dir / "report.html")
            return report_path

        elif format == "json":
            report_path = output_path or (self.report_dir / "results.json")
            self.result.export(format="json", output_path=report_path)
            return report_path

        elif format == "junit":
            report_path = output_path or (self.report_dir / "junit.xml")
            self.result.export(format="junit", output_path=report_path)
            return report_path

        elif format == "allure":
            try:
                subprocess.run(
                    [
                        "allure",
                        "generate",
                        str(self.allure_results_dir),
                        "-o",
                        str(self.report_dir / "allure-report"),
                        "--clean",
                    ],
                    check=True,
                    capture_output=True,
                )
                return self.report_dir / "allure-report" / "index.html"
            except FileNotFoundError:
                print("警告: Allure命令行工具未安装")
                print("安装方法: npm install -g allure-commandline")
                return None
            except subprocess.CalledProcessError as e:
                print(f"Allure报告生成失败: {e}")
                return None

        else:
            raise ValueError(f"不支持的报告格式: {format}")

    def send_notification(self, webhook_url: Optional[str] = None) -> bool:
        stats = self.result.get_statistics()

        message = {
            "text": f"测试执行完成",
            "attachments": [
                {
                    "color": (
                        "good" if stats["pass_rate"] >= 80 else "warning" if stats["pass_rate"] >= 60 else "danger"
                    ),
                    "fields": [
                        {"title": "总测试数", "value": str(stats["total"]), "short": True},
                        {"title": "通过数", "value": str(stats["passed"]), "short": True},
                        {"title": "失败数", "value": str(stats["failed"]), "short": True},
                        {"title": "跳过数", "value": str(stats["skipped"]), "short": True},
                        {"title": "通过率", "value": f"{stats['pass_rate']}%", "short": True},
                        {"title": "执行时间", "value": f"{stats['duration']}秒", "short": True},
                    ],
                }
            ],
        }

        if webhook_url:
            try:
                import requests

                response = requests.post(webhook_url, json=message, timeout=10)
                return response.status_code == 200
            except Exception as e:
                print(f"发送通知失败: {e}")
                return False
        else:
            print("\n测试通知:")
            print(json.dumps(message, indent=2, ensure_ascii=False))
            return True


def create_test_runner(
    suite: Optional[str] = None,
    module: Optional[str] = None,
    marker: Optional[str] = None,
    env: str = "dev",
    parallel: int = 1,
    retry: int = 0,
    config_file: Optional[str] = None,
) -> TestRunner:
    config = TestConfiguration(
        suite=TestSuite(suite) if suite else None,
        module=TestModule(module) if module else None,
        marker=TestMarker(marker) if marker else None,
        env=Environment(env),
        parallel=parallel,
        retry=retry,
    )

    if config_file:
        config.load_from_file(config_file)

    config.load_from_env()

    if not config.validate():
        raise ValueError("配置验证失败")

    return TestRunner(config)


if __name__ == "__main__":
    runner = create_test_runner(suite="unit", parallel=2, retry=1)

    return_code = runner.run_tests()

    result = runner.collect_results()
    stats = result.get_statistics()

    print(f"\n测试统计:")
    print(f"  总数: {stats['total']}")
    print(f"  通过: {stats['passed']}")
    print(f"  失败: {stats['failed']}")
    print(f"  通过率: {stats['pass_rate']}%")

    runner.generate_report(format="html")
    runner.send_notification()

    sys.exit(return_code)

"""
性能测试运行脚本
自动运行性能测试，生成报告，验证性能基准
"""

import argparse
import json
import os
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from config import get_performance_config, performance_config


class PerformanceTestRunner:
    """性能测试运行器"""

    def __init__(self, test_type: str = "load", headless: bool = True):
        self.test_type = test_type
        self.headless = headless
        self.config = get_performance_config(test_type)
        self.report_dir = Path(performance_config.REPORT_CONFIG["output_dir"])
        self.report_dir.mkdir(parents=True, exist_ok=True)

        self.timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.html_report = self.report_dir / f"performance_report_{self.timestamp}.html"
        self.stats_csv = self.report_dir / f"stats_{self.timestamp}.csv"
        self.failures_csv = self.report_dir / f"failures_{self.timestamp}.csv"

    def check_locust_installed(self) -> bool:
        """检查Locust是否已安装"""
        try:
            result = subprocess.run(["locust", "--version"], capture_output=True, text=True)
            return result.returncode == 0
        except FileNotFoundError:
            return False

    def install_locust(self):
        """安装Locust"""
        print("正在安装Locust...")
        subprocess.run([sys.executable, "-m", "pip", "install", "locust"], check=True)
        print("Locust安装完成")

    def build_command(self, user_class: str = None) -> list:
        """构建Locust命令"""
        cmd = [
            "locust",
            "-f",
            os.path.join(os.path.dirname(__file__), "locustfile.py"),
            "--host",
            self.config.API_BASE_URL,
        ]

        if user_class:
            cmd.extend(["-u", user_class])

        if self.headless:
            cmd.extend(
                [
                    "--headless",
                    "-u",
                    str(self.config.USERS),
                    "-r",
                    str(self.config.SPAWN_RATE),
                    "-t",
                    self.config.RUN_TIME,
                    "--html",
                    str(self.html_report),
                    "--csv",
                    str(self.stats_csv).replace(f"_{self.timestamp}", ""),
                    "--only-summary",
                ]
            )

        return cmd

    def run_test(self, user_class: str = None) -> dict:
        """运行性能测试"""
        print("\n" + "=" * 70)
        print(f"性能测试配置")
        print("=" * 70)
        print(f"测试类型: {self.test_type}")
        print(f"API地址: {self.config.API_BASE_URL}")
        print(f"并发用户数: {self.config.USERS}")
        print(f"用户生成速率: {self.config.SPAWN_RATE}/s")
        print(f"运行时间: {self.config.RUN_TIME}")
        print(f"报告目录: {self.report_dir}")
        print("=" * 70 + "\n")

        cmd = self.build_command(user_class)

        print(f"执行命令: {' '.join(cmd)}\n")

        start_time = time.time()

        try:
            result = subprocess.run(cmd, capture_output=True, text=True, cwd=os.path.dirname(__file__))

            end_time = time.time()
            duration = end_time - start_time

            return {
                "success": result.returncode == 0,
                "returncode": result.returncode,
                "stdout": result.stdout,
                "stderr": result.stderr,
                "duration": duration,
                "html_report": str(self.html_report) if self.headless else None,
                "stats_csv": str(self.stats_csv) if self.headless else None,
            }
        except Exception as e:
            return {
                "success": False,
                "error": str(e),
            }

    def run_all_tests(self) -> dict:
        """运行所有性能测试"""
        results = {}

        test_classes = [
            ("UserLoginUser", "用户登录接口"),
            ("MerchantLoginUser", "商家登录接口"),
            ("CreateAppointmentUser", "创建预约接口"),
            ("CreateProductOrderUser", "创建订单接口"),
            ("QueryAppointmentsUser", "查询预约接口"),
            ("QueryProductOrdersUser", "查询订单接口"),
            ("MixedScenarioUser", "混合场景测试"),
        ]

        for class_name, description in test_classes:
            print(f"\n{'=' * 70}")
            print(f"运行测试: {description} ({class_name})")
            print("=" * 70)

            result = self.run_test(class_name)
            results[class_name] = {
                "description": description,
                "result": result,
            }

            if result.get("success"):
                print(f"✓ {description} 完成")
            else:
                print(f"✗ {description} 失败: {result.get('error', result.get('stderr', 'Unknown error'))}")

        return results

    def validate_performance(self, stats_data: dict) -> dict:
        """验证性能基准"""
        validation_results = {}

        thresholds = performance_config.PERFORMANCE_THRESHOLDS

        for endpoint, metrics in stats_data.items():
            endpoint_thresholds = thresholds.get(endpoint, {})
            validation = {
                "endpoint": endpoint,
                "passed": True,
                "details": [],
            }

            if "max_response_time_ms" in endpoint_thresholds:
                max_rt = endpoint_thresholds["max_response_time_ms"]
                actual_max = metrics.get("max_response_time", 0)
                if actual_max > max_rt:
                    validation["passed"] = False
                    validation["details"].append(f"最大响应时间 {actual_max:.2f}ms 超过阈值 {max_rt}ms")

            if "max_avg_response_time_ms" in endpoint_thresholds:
                max_avg = endpoint_thresholds["max_avg_response_time_ms"]
                actual_avg = metrics.get("avg_response_time", 0)
                if actual_avg > max_avg:
                    validation["passed"] = False
                    validation["details"].append(f"平均响应时间 {actual_avg:.2f}ms 超过阈值 {max_avg}ms")

            if "min_rps" in endpoint_thresholds:
                min_rps = endpoint_thresholds["min_rps"]
                actual_rps = metrics.get("rps", 0)
                if actual_rps < min_rps:
                    validation["passed"] = False
                    validation["details"].append(f"RPS {actual_rps:.2f} 低于阈值 {min_rps}")

            if "max_error_rate" in endpoint_thresholds:
                max_error = endpoint_thresholds["max_error_rate"]
                actual_error = metrics.get("fail_ratio", 0)
                if actual_error > max_error:
                    validation["passed"] = False
                    validation["details"].append(f"错误率 {actual_error * 100:.2f}% 超过阈值 {max_error * 100:.2f}%")

            validation_results[endpoint] = validation

        return validation_results

    def generate_summary_report(self, results: dict) -> str:
        """生成摘要报告"""
        report_lines = [
            "# 性能测试报告",
            f"\n生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
            f"\n## 测试配置\n",
            f"- 测试类型: {self.test_type}",
            f"- API地址: {self.config.API_BASE_URL}",
            f"- 并发用户数: {self.config.USERS}",
            f"- 用户生成速率: {self.config.SPAWN_RATE}/s",
            f"- 运行时间: {self.config.RUN_TIME}",
            "\n## 测试结果\n",
        ]

        for class_name, data in results.items():
            description = data["description"]
            result = data["result"]

            status = "✓ 通过" if result.get("success") else "✗ 失败"
            report_lines.append(f"### {description}\n")
            report_lines.append(f"状态: {status}")

            if result.get("duration"):
                report_lines.append(f"耗时: {result['duration']:.2f}秒")

            if result.get("html_report"):
                report_lines.append(f"HTML报告: {result['html_report']}")

            if result.get("stats_csv"):
                report_lines.append(f"统计数据: {result['stats_csv']}")

            report_lines.append("")

        return "\n".join(report_lines)

    def save_summary_report(self, results: dict):
        """保存摘要报告"""
        report_content = self.generate_summary_report(results)
        report_file = self.report_dir / f"summary_report_{self.timestamp}.md"

        with open(report_file, "w", encoding="utf-8") as f:
            f.write(report_content)

        print(f"\n摘要报告已保存: {report_file}")
        return report_file


def main():
    parser = argparse.ArgumentParser(description="运行性能测试")
    parser.add_argument(
        "--type",
        "-t",
        choices=["load", "stress", "spike"],
        default="load",
        help="测试类型: load(负载测试), stress(压力测试), spike(峰值测试)",
    )
    parser.add_argument("--headless", action="store_true", default=True, help="无头模式运行")
    parser.add_argument("--ui", action="store_true", help="启动Web UI模式")
    parser.add_argument("--class", "-c", dest="user_class", help="指定测试类名称")
    parser.add_argument("--all", action="store_true", help="运行所有测试")
    parser.add_argument("--users", "-u", type=int, help="并发用户数")
    parser.add_argument("--spawn-rate", "-r", type=int, help="用户生成速率")
    parser.add_argument("--run-time", type=str, help="运行时间(如: 60s, 5m, 1h)")

    args = parser.parse_args()

    runner = PerformanceTestRunner(test_type=args.type, headless=not args.ui)

    if not runner.check_locust_installed():
        print("Locust未安装")
        runner.install_locust()

    if args.users:
        runner.config.USERS = args.users
    if args.spawn_rate:
        runner.config.SPAWN_RATE = args.spawn_rate
    if args.run_time:
        runner.config.RUN_TIME = args.run_time

    if args.all:
        results = runner.run_all_tests()
        runner.save_summary_report(results)

        print("\n" + "=" * 70)
        print("性能测试完成")
        print("=" * 70)

        success_count = sum(1 for r in results.values() if r["result"].get("success"))
        total_count = len(results)
        print(f"成功: {success_count}/{total_count}")

    elif args.user_class:
        result = runner.run_test(args.user_class)

        if result.get("success"):
            print("\n性能测试完成")
            if result.get("html_report"):
                print(f"HTML报告: {result['html_report']}")
        else:
            print(f"\n性能测试失败: {result.get('error', result.get('stderr', 'Unknown error'))}")
            sys.exit(1)
    else:
        result = runner.run_test()

        if result.get("success"):
            print("\n性能测试完成")
            if result.get("html_report"):
                print(f"HTML报告: {result['html_report']}")
        else:
            print(f"\n性能测试失败: {result.get('error', result.get('stderr', 'Unknown error'))}")
            sys.exit(1)


if __name__ == "__main__":
    main()

"""
自动修复和重试机制
"""

import argparse
import json
import logging
import re
import subprocess
import time
from dataclasses import asdict, dataclass, field
from datetime import datetime
from logging.handlers import RotatingFileHandler
from pathlib import Path
from typing import Dict, List, Optional, Tuple

from tests.auto_fix_config import AutoFixConfig, ErrorType, RetryConfig
from tests.config import config


@dataclass
class TestFailure:
    test_name: str
    error_message: str
    error_type: ErrorType
    traceback: str
    timestamp: str = field(default_factory=lambda: datetime.now().isoformat())
    request_data: Optional[Dict] = None
    response_data: Optional[Dict] = None
    fix_suggestions: List[str] = field(default_factory=list)
    retry_count: int = 0
    fixed: bool = False


@dataclass
class RetryResult:
    test_name: str
    attempt: int
    success: bool
    error_message: Optional[str] = None
    duration: float = 0.0
    timestamp: str = field(default_factory=lambda: datetime.now().isoformat())


class ErrorLogger:
    """错误日志记录器"""

    def __init__(self, log_dir: str = None):
        self.log_dir = Path(log_dir or AutoFixConfig.LOG_CONFIG["log_dir"])
        self.log_dir.mkdir(parents=True, exist_ok=True)

        self.error_logger = self._setup_logger(
            "error_logger", self.log_dir / AutoFixConfig.LOG_CONFIG["error_log_file"]
        )

        self.retry_logger = self._setup_logger(
            "retry_logger", self.log_dir / AutoFixConfig.LOG_CONFIG["retry_log_file"]
        )

    def _setup_logger(self, name: str, log_file: Path) -> logging.Logger:
        logger = logging.getLogger(name)
        logger.setLevel(logging.DEBUG)

        if logger.handlers:
            logger.handlers.clear()

        handler = RotatingFileHandler(
            log_file,
            maxBytes=AutoFixConfig.LOG_CONFIG["max_log_size"],
            backupCount=AutoFixConfig.LOG_CONFIG["backup_count"],
            encoding="utf-8",
        )

        formatter = logging.Formatter(
            "%(asctime)s - %(name)s - %(levelname)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S"
        )
        handler.setFormatter(formatter)
        logger.addHandler(handler)

        return logger

    def log_error(self, failure: TestFailure):
        self.error_logger.error(
            f"Test: {failure.test_name}\n"
            f"Error Type: {failure.error_type.value}\n"
            f"Message: {failure.error_message}\n"
            f"Traceback: {failure.traceback}\n"
            f"Request: {json.dumps(failure.request_data, ensure_ascii=False, indent=2) if failure.request_data else 'N/A'}\n"
            f"Response: {json.dumps(failure.response_data, ensure_ascii=False, indent=2) if failure.response_data else 'N/A'}\n"
            f"{'=' * 80}"
        )

    def log_retry(self, result: RetryResult):
        level = logging.INFO if result.success else logging.WARNING
        self.retry_logger.log(
            level,
            f"Test: {result.test_name}\n"
            f"Attempt: {result.attempt}\n"
            f"Success: {result.success}\n"
            f"Duration: {result.duration:.2f}s\n"
            f"Error: {result.error_message or 'N/A'}\n"
            f"{'=' * 80}",
        )

    def save_error_report(self, failures: List[TestFailure], report_file: str = None):
        report_file = Path(report_file or self.log_dir / AutoFixConfig.LOG_CONFIG["report_file"])

        failures_data = []
        for f in failures:
            failure_dict = asdict(f)
            failure_dict["error_type"] = f.error_type.value
            failures_data.append(failure_dict)

        report_data = {
            "generated_at": datetime.now().isoformat(),
            "total_failures": len(failures),
            "summary": self._generate_summary(failures),
            "failures": failures_data,
        }

        with open(report_file, "w", encoding="utf-8") as f:
            json.dump(report_data, f, ensure_ascii=False, indent=2)

        self.error_logger.info(f"Error report saved to {report_file}")

    def _generate_summary(self, failures: List[TestFailure]) -> Dict:
        error_type_counts = {}
        for failure in failures:
            error_type = failure.error_type.value
            error_type_counts[error_type] = error_type_counts.get(error_type, 0) + 1

        return {
            "error_types": error_type_counts,
            "fixed_count": sum(1 for f in failures if f.fixed),
            "unfixed_count": sum(1 for f in failures if not f.fixed),
        }


class FailedTestDetector:
    """失败测试检测器"""

    def __init__(self):
        self.error_patterns = AutoFixConfig.ERROR_PATTERNS

    def parse_pytest_output(self, output: str) -> List[TestFailure]:
        failures = []

        failure_pattern = re.compile(r"FAILED\s+(.+?)\s+-\s+(.+?)(?:\n|$)", re.MULTILINE)

        for match in failure_pattern.finditer(output):
            test_name = match.group(1).strip()
            error_message = match.group(2).strip()

            error_type = AutoFixConfig.get_error_type_from_message(error_message)

            failure = TestFailure(
                test_name=test_name,
                error_message=error_message,
                error_type=error_type,
                traceback=self._extract_traceback(output, test_name),
            )

            failures.append(failure)

        return failures

    def _extract_traceback(self, output: str, test_name: str) -> str:
        traceback_pattern = re.compile(
            rf"{re.escape(test_name)}.*?(?={re.escape(test_name)}|FAILED|PASSED|=+$)", re.DOTALL
        )

        match = traceback_pattern.search(output)
        if match:
            return match.group(0).strip()

        return ""

    def categorize_failure(self, failure: TestFailure) -> ErrorType:
        return AutoFixConfig.get_error_type_from_message(failure.error_message)

    def extract_request_response(
        self, failure: TestFailure, test_context: Dict = None
    ) -> Tuple[Optional[Dict], Optional[Dict]]:
        if test_context:
            failure.request_data = test_context.get("request")
            failure.response_data = test_context.get("response")
        return failure.request_data, failure.response_data


class FixSuggestionGenerator:
    """修复建议生成器"""

    def __init__(self):
        self.fix_suggestions = AutoFixConfig.FIX_SUGGESTIONS

    def generate_suggestions(self, failure: TestFailure) -> List[str]:
        suggestion = AutoFixConfig.get_fix_suggestion(failure.error_type)

        formatted_actions = []
        for action in suggestion.actions:
            formatted_action = self._format_action(action)
            formatted_actions.append(formatted_action)

        failure.fix_suggestions = formatted_actions
        return formatted_actions

    def _format_action(self, action: str) -> str:
        replacements = {
            "{api_base_url}": config.API_BASE_URL,
            "{timeout}": str(config.TIMEOUT),
            "{test_username}": config.TEST_USER.get("username", "N/A"),
            "{test_password}": config.TEST_USER.get("password", "N/A"),
            "{db_host}": config.DATABASE_CONFIG.get("host", "N/A"),
            "{db_port}": str(config.DATABASE_CONFIG.get("port", "N/A")),
            "{db_name}": config.DATABASE_CONFIG.get("database", "N/A"),
        }

        for placeholder, value in replacements.items():
            action = action.replace(placeholder, value)

        return action

    def generate_fix_report(self, failure: TestFailure) -> str:
        suggestion = AutoFixConfig.get_fix_suggestion(failure.error_type)

        report = [
            f"\n{'=' * 80}",
            "测试失败修复建议",
            f"{'=' * 80}",
            f"\n测试名称: {failure.test_name}",
            f"错误类型: {suggestion.title}",
            f"错误描述: {suggestion.description}",
            f"错误信息: {failure.error_message}",
            "\n修复建议:",
        ]

        for suggestion_text in failure.fix_suggestions:
            report.append(f"  {suggestion_text}")

        if failure.traceback:
            report.append("\n堆栈跟踪:")
            report.append(f"  {failure.traceback[:500]}...")

        report.append(f"{'=' * 80}\n")

        return "\n".join(report)


class AutoFixRunner:
    """自动修复和重试运行器"""

    def __init__(self, retry_config: RetryConfig = None, logger: ErrorLogger = None):
        self.retry_config = retry_config or AutoFixConfig.get_retry_config()
        self.logger = logger or ErrorLogger()
        self.detector = FailedTestDetector()
        self.suggestion_generator = FixSuggestionGenerator()

        self.failures: List[TestFailure] = []
        self.retry_results: List[RetryResult] = []

    def run_tests(self, test_path: str = "tests/", extra_args: List[str] = None) -> Tuple[bool, str]:
        cmd = ["python", "-m", "pytest", test_path, "-v", "--tb=short"]

        if extra_args:
            cmd.extend(extra_args)

        try:
            result = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8", timeout=300)

            output = result.stdout + "\n" + result.stderr
            success = result.returncode == 0

            return success, output

        except subprocess.TimeoutExpired:
            return False, "Test execution timeout"
        except Exception as e:
            return False, f"Error running tests: {str(e)}"

    def detect_failures(self, test_output: str) -> List[TestFailure]:
        failures = self.detector.parse_pytest_output(test_output)

        for failure in failures:
            self.suggestion_generator.generate_suggestions(failure)
            self.logger.log_error(failure)

        self.failures.extend(failures)
        return failures

    def should_retry(self, failure: TestFailure) -> bool:
        if failure.retry_count >= self.retry_config.max_retries:
            return False

        return failure.error_type in self.retry_config.retry_on_errors

    def calculate_delay(self, attempt: int) -> int:
        if not self.retry_config.exponential_backoff:
            return self.retry_config.retry_delay

        delay = self.retry_config.retry_delay * (2 ** (attempt - 1))
        return min(delay, self.retry_config.max_delay)

    def retry_test(self, test_name: str, attempt: int) -> RetryResult:
        start_time = time.time()

        cmd = ["python", "-m", "pytest", test_name, "-v", "--tb=short"]

        try:
            result = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8", timeout=60)

            success = result.returncode == 0
            error_message = None if success else result.stdout

            duration = time.time() - start_time

            retry_result = RetryResult(
                test_name=test_name, attempt=attempt, success=success, error_message=error_message, duration=duration
            )

            self.logger.log_retry(retry_result)
            self.retry_results.append(retry_result)

            return retry_result

        except Exception as e:
            duration = time.time() - start_time
            retry_result = RetryResult(
                test_name=test_name, attempt=attempt, success=False, error_message=str(e), duration=duration
            )

            self.logger.log_retry(retry_result)
            self.retry_results.append(retry_result)

            return retry_result

    def run_with_retry(self, test_path: str = "tests/", extra_args: List[str] = None) -> Dict:
        print(f"\n{'=' * 80}")
        print("开始运行测试...")
        print(f"{'=' * 80}\n")

        success, output = self.run_tests(test_path, extra_args)

        if success:
            print("\n✅ 所有测试通过！")
            return {"success": True, "failures": [], "retry_results": [], "summary": "All tests passed"}

        print("\n❌ 发现失败的测试，正在分析...")
        failures = self.detect_failures(output)

        if not failures:
            print("\n⚠️  无法解析失败信息")
            return {"success": False, "failures": [], "retry_results": [], "summary": "Failed to parse test failures"}

        print(f"\n发现 {len(failures)} 个失败的测试")

        for failure in failures:
            print(self.suggestion_generator.generate_fix_report(failure))

        retryable_failures = [f for f in failures if self.should_retry(f)]

        if not retryable_failures:
            print("\n没有可重试的测试")
            self._generate_final_report()
            return {
                "success": False,
                "failures": self.failures,
                "retry_results": self.retry_results,
                "summary": "No retryable failures",
            }

        print(f"\n开始重试 {len(retryable_failures)} 个测试...")

        for failure in retryable_failures:
            failure.retry_count = 0

            for attempt in range(1, self.retry_config.max_retries + 1):
                failure.retry_count = attempt

                delay = self.calculate_delay(attempt)
                print(f"\n重试测试: {failure.test_name} (第 {attempt}/{self.retry_config.max_retries} 次)")
                print(f"等待 {delay} 秒后重试...")
                time.sleep(delay)

                retry_result = self.retry_test(failure.test_name, attempt)

                if retry_result.success:
                    failure.fixed = True
                    print(f"✅ 测试修复成功: {failure.test_name}")
                    break
                else:
                    print(f"❌ 重试失败: {failure.test_name}")

            if not failure.fixed:
                print(f"⚠️  测试修复失败，已达到最大重试次数: {failure.test_name}")

        self._generate_final_report()

        all_fixed = all(f.fixed for f in retryable_failures)

        return {
            "success": all_fixed,
            "failures": self.failures,
            "retry_results": self.retry_results,
            "summary": self._generate_summary(),
        }

    def _generate_final_report(self):
        report_dir = Path(AutoFixConfig.REPORT_CONFIG["report_dir"])
        report_dir.mkdir(parents=True, exist_ok=True)

        self.logger.save_error_report(self.failures, report_dir / AutoFixConfig.REPORT_CONFIG["json_report"])

        self._generate_html_report(report_dir / AutoFixConfig.REPORT_CONFIG["html_report"])

    def _generate_html_report(self, report_file: Path):
        html_content = f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>自动修复测试报告</title>
    <style>
        body {{
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }}
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }}
        h1 {{
            color: #333;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }}
        .summary {{
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }}
        .summary-item {{
            display: inline-block;
            margin-right: 30px;
        }}
        .summary-label {{
            font-weight: bold;
            color: #666;
        }}
        .summary-value {{
            font-size: 24px;
            font-weight: bold;
            margin-left: 10px;
        }}
        .success {{ color: #4CAF50; }}
        .error {{ color: #f44336; }}
        .warning {{ color: #ff9800; }}
        .failure-card {{
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 15px;
            padding: 15px;
        }}
        .failure-header {{
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }}
        .error-type {{
            display: inline-block;
            padding: 2px 8px;
            border-radius: 3px;
            font-size: 12px;
            margin-left: 10px;
        }}
        .api_error {{ background-color: #ffebee; color: #c62828; }}
        .auth_error {{ background-color: #fff3e0; color: #e65100; }}
        .network_error {{ background-color: #e3f2fd; color: #1565c0; }}
        .data_error {{ background-color: #f3e5f5; color: #6a1b9a; }}
        .suggestions {{
            background-color: #e8f5e9;
            padding: 10px;
            border-radius: 4px;
            margin-top: 10px;
        }}
        .suggestion-item {{
            margin: 5px 0;
            padding-left: 20px;
        }}
        .timestamp {{
            color: #999;
            font-size: 12px;
        }}
    </style>
</head>
<body>
    <div class="container">
        <h1>自动修复测试报告</h1>

        <div class="summary">
            <div class="timestamp">生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</div>
            <br><br>
            <div class="summary-item">
                <span class="summary-label">总失败数:</span>
                <span class="summary-value error">{len(self.failures)}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">已修复:</span>
                <span class="summary-value success">{sum(1 for f in self.failures if f.fixed)}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">未修复:</span>
                <span class="summary-value error">{sum(1 for f in self.failures if not f.fixed)}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">重试次数:</span>
                <span class="summary-value warning">{len(self.retry_results)}</span>
            </div>
        </div>

        <h2>失败详情</h2>
"""

        for failure in self.failures:
            error_type_class = failure.error_type.value
            html_content += f"""
        <div class="failure-card">
            <div class="failure-header">
                {failure.test_name}
                <span class="error-type {error_type_class}">{failure.error_type.value}</span>
                {'<span class="error-type success">已修复</span>' if failure.fixed else ''}
            </div>
            <div><strong>错误信息:</strong> {failure.error_message}</div>
            <div class="suggestions">
                <strong>修复建议:</strong>
                {''.join(f'<div class="suggestion-item">{s}</div>' for s in failure.fix_suggestions)}
            </div>
            <div class="timestamp">重试次数: {failure.retry_count}</div>
        </div>
"""

        html_content += """
    </div>
</body>
</html>
"""

        with open(report_file, "w", encoding="utf-8") as f:
            f.write(html_content)

        print(f"\n📊 HTML报告已生成: {report_file}")

    def _generate_summary(self) -> str:
        total = len(self.failures)
        fixed = sum(1 for f in self.failures if f.fixed)
        unfixed = total - fixed

        return (
            f"Total failures: {total}, "
            f"Fixed: {fixed}, "
            f"Unfixed: {unfixed}, "
            f"Retry attempts: {len(self.retry_results)}"
        )


def main():
    parser = argparse.ArgumentParser(description="自动修复和重试测试")
    parser.add_argument("--test-path", default="tests/", help="测试路径（默认: tests/）")
    parser.add_argument("--max-retries", type=int, default=3, help="最大重试次数（默认: 3）")
    parser.add_argument("--retry-delay", type=int, default=5, help="重试延迟秒数（默认: 5）")
    parser.add_argument("--no-exponential-backoff", action="store_true", help="禁用指数退避")
    parser.add_argument("--pytest-args", nargs="*", help="额外的pytest参数")

    args = parser.parse_args()

    retry_config = RetryConfig(
        max_retries=args.max_retries, retry_delay=args.retry_delay, exponential_backoff=not args.no_exponential_backoff
    )

    runner = AutoFixRunner(retry_config=retry_config)

    result = runner.run_with_retry(test_path=args.test_path, extra_args=args.pytest_args)

    if result["success"]:
        print("\n✅ 所有测试已成功修复！")
        exit(0)
    else:
        print("\n❌ 部分测试修复失败，请查看报告了解详情")
        exit(1)


if __name__ == "__main__":
    main()

"""
自动修复机制使用示例
"""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from tests.auto_fix import AutoFixRunner, ErrorLogger, FailedTestDetector, FixSuggestionGenerator, TestFailure
from tests.auto_fix_config import AutoFixConfig, ErrorType


def example_parse_pytest_output():
    print("\n" + "=" * 80)
    print("示例1: 解析pytest输出")
    print("=" * 80)

    pytest_output = """
============================= test session starts =============================
collected 3 items

tests/test_example.py F                                                 [ 33%]
tests/test_example.py F                                                 [ 66%]
tests/test_example.py .                                                 [100%]

================================== FAILURES ===================================
_____________________________ test_api_connection _____________________________
    def test_api_connection():
        response = client.get('/api/users')
>       assert response.status_code == 200
E       AssertionError: Expected 200 but got 500
E       Connection refused

tests/test_example.py:10: AssertionError

_____________________________ test_user_login _____________________________
    def test_user_login():
        response = client.post('/api/auth/login', json={'username': 'test', 'password': 'test'})
>       assert response.status_code == 200
E       AssertionError: Expected 200 but got 401
E       Unauthorized: Invalid token

tests/test_example.py:20: AssertionError
=========================== short test summary info ============================
FAILED tests/test_example.py::test_api_connection - AssertionError: Expected 200 but got 500
FAILED tests/test_example.py::test_user_login - AssertionError: Expected 200 but got 401
========================= 2 failed, 1 passed in 0.05s =========================
"""

    detector = FailedTestDetector()
    failures = detector.parse_pytest_output(pytest_output)

    print(f"\n检测到 {len(failures)} 个失败的测试:")
    for failure in failures:
        print(f"\n  测试名称: {failure.test_name}")
        print(f"  错误类型: {failure.error_type.value}")
        print(f"  错误信息: {failure.error_message[:100]}...")


def example_generate_fix_suggestions():
    print("\n" + "=" * 80)
    print("示例2: 生成修复建议")
    print("=" * 80)

    failure = TestFailure(
        test_name="test_user_login",
        error_message="401 Unauthorized: Invalid token",
        error_type=ErrorType.AUTH_ERROR,
        traceback="AssertionError at line 20",
    )

    suggestion_generator = FixSuggestionGenerator()
    suggestions = suggestion_generator.generate_suggestions(failure)

    print(f"\n测试: {failure.test_name}")
    print(f"错误类型: {failure.error_type.value}")
    print("\n修复建议:")
    for suggestion in suggestions:
        print(f"  {suggestion}")


def example_error_logging():
    print("\n" + "=" * 80)
    print("示例3: 错误日志记录")
    print("=" * 80)

    logger = ErrorLogger(log_dir="tests/logs")

    failure = TestFailure(
        test_name="test_api_endpoint",
        error_message="Connection refused",
        error_type=ErrorType.NETWORK_ERROR,
        traceback="ConnectionError at line 15",
        request_data={"method": "GET", "url": "/api/users"},
        response_data={"status_code": None, "error": "Connection refused"},
    )

    suggestion_generator = FixSuggestionGenerator()
    suggestion_generator.generate_suggestions(failure)

    logger.log_error(failure)
    print("\n✅ 错误日志已保存到 tests/logs/errors.log")


def example_retry_mechanism():
    print("\n" + "=" * 80)
    print("示例4: 重试机制")
    print("=" * 80)

    from tests.auto_fix_config import RetryConfig

    retry_config = RetryConfig(max_retries=3, retry_delay=2, exponential_backoff=True, max_delay=30)

    runner = AutoFixRunner(retry_config=retry_config)

    failure = TestFailure(
        test_name="test_timeout",
        error_message="Timeout error",
        error_type=ErrorType.TIMEOUT_ERROR,
        traceback="TimeoutError at line 25",
    )

    should_retry = runner.should_retry(failure)
    print(f"\n测试: {failure.test_name}")
    print(f"错误类型: {failure.error_type.value}")
    print(f"是否应该重试: {should_retry}")

    if should_retry:
        print("\n重试延迟计算:")
        for attempt in range(1, retry_config.max_retries + 1):
            delay = runner.calculate_delay(attempt)
            print(f"  第 {attempt} 次重试延迟: {delay} 秒")


def example_full_workflow():
    print("\n" + "=" * 80)
    print("示例5: 完整工作流程")
    print("=" * 80)

    print("\n完整工作流程包括:")
    print("  1. 运行测试")
    print("  2. 检测失败的测试")
    print("  3. 记录错误日志")
    print("  4. 生成修复建议")
    print("  5. 自动重试失败的测试")
    print("  6. 生成最终报告")

    print("\n命令行使用:")
    print("  python tests/auto_fix.py --test-path tests/ --max-retries 3")

    print("\nPython API使用:")
    print("""
    from tests.auto_fix import AutoFixRunner
    from tests.auto_fix_config import RetryConfig

    retry_config = RetryConfig(max_retries=3, retry_delay=5)
    runner = AutoFixRunner(retry_config=retry_config)

    result = runner.run_with_retry(test_path="tests/")

    if result["success"]:
        print("所有测试已修复！")
    else:
        print("部分测试修复失败")
    """)


def example_error_type_detection():
    print("\n" + "=" * 80)
    print("示例6: 错误类型检测")
    print("=" * 80)

    test_errors = [
        "Connection refused",
        "Timeout error",
        "401 Unauthorized",
        "403 Forbidden",
        "404 Not Found",
        "500 Internal Server Error",
        "AssertionError: Expected 200 but got 400",
        "Database connection failed",
    ]

    print("\n错误类型检测示例:")
    for error_msg in test_errors:
        error_type = AutoFixConfig.get_error_type_from_message(error_msg)
        print(f"  '{error_msg[:40]}...' -> {error_type.value}")


def main():
    print("\n" + "=" * 80)
    print("自动修复机制使用示例")
    print("=" * 80)

    example_parse_pytest_output()
    example_generate_fix_suggestions()
    example_error_logging()
    example_retry_mechanism()
    example_error_type_detection()
    example_full_workflow()

    print("\n" + "=" * 80)
    print("示例运行完成！")
    print("=" * 80)
    print("\n更多信息请查看:")
    print("  - tests/auto_fix.py - 主要功能实现")
    print("  - tests/auto_fix_config.py - 配置文件")
    print("  - tests/logs/ - 日志文件目录")
    print("  - tests/report/ - 报告文件目录")


if __name__ == "__main__":
    main()

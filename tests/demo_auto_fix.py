"""
自动修复机制完整演示
演示所有核心功能的使用
"""
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from tests.auto_fix import (
    AutoFixRunner,
    FailedTestDetector,
    ErrorLogger,
    FixSuggestionGenerator,
    TestFailure,
    RetryResult
)
from tests.auto_fix_config import (
    AutoFixConfig,
    ErrorType,
    RetryConfig
)


def print_header(title: str):
    print("\n" + "="*80)
    print(title)
    print("="*80)


def demo_error_detection():
    """演示错误检测功能"""
    print_header("演示1: 错误检测")
    
    pytest_output = """
============================= test session starts =============================
collected 5 items

tests/test_api.py F                                                     [ 20%]
tests/test_auth.py F                                                    [ 40%]
tests/test_database.py F                                                [ 60%]
tests/test_network.py F                                                 [ 80%]
tests/test_validation.py F                                              [100%]

================================== FAILURES ===================================
_____________________________ test_api_endpoint _____________________________
    def test_api_endpoint():
        response = client.get('/api/users')
>       assert response.status_code == 200
E       AssertionError: Expected 200 but got 500
E       Internal Server Error

_____________________________ test_user_authentication _____________________________
    def test_user_authentication():
        response = client.post('/api/auth/login', json={'username': 'test', 'password': 'test'})
>       assert response.status_code == 200
E       AssertionError: Expected 200 but got 401
E       Unauthorized: Invalid token

_____________________________ test_database_connection _____________________________
    def test_database_connection():
        result = db.query("SELECT * FROM users")
>       assert result is not None
E       DatabaseError: Connection refused

_____________________________ test_network_timeout _____________________________
    def test_network_timeout():
        response = client.get('/api/slow-endpoint', timeout=1)
>       assert response.status_code == 200
E       TimeoutError: Request timed out after 1 seconds

_____________________________ test_data_validation _____________________________
    def test_data_validation():
        response = client.post('/api/users', json={'email': 'invalid-email'})
>       assert response.status_code == 201
E       AssertionError: Expected 201 but got 400
E       Validation error: Invalid email format

=========================== short test summary info ============================
FAILED tests/test_api.py::test_api_endpoint - AssertionError: Expected 200 but got 500
FAILED tests/test_auth.py::test_user_authentication - AssertionError: Expected 200 but got 401
FAILED tests/test_database.py::test_database_connection - DatabaseError: Connection refused
FAILED tests/test_network.py::test_network_timeout - TimeoutError: Request timed out
FAILED tests/test_validation.py::test_data_validation - AssertionError: Expected 201 but got 400
========================= 5 failed in 2.35s =========================
"""
    
    detector = FailedTestDetector()
    failures = detector.parse_pytest_output(pytest_output)
    
    print(f"\n检测到 {len(failures)} 个失败的测试:")
    for i, failure in enumerate(failures, 1):
        print(f"\n{i}. {failure.test_name}")
        print(f"   错误类型: {failure.error_type.value}")
        print(f"   错误信息: {failure.error_message[:60]}...")
    
    return failures


def demo_fix_suggestions(failures):
    """演示修复建议生成"""
    print_header("演示2: 修复建议生成")
    
    generator = FixSuggestionGenerator()
    
    for failure in failures[:3]:  # 只演示前3个
        suggestions = generator.generate_suggestions(failure)
        report = generator.generate_fix_report(failure)
        print(report)


def demo_error_logging(failures):
    """演示错误日志记录"""
    print_header("演示3: 错误日志记录")
    
    logger = ErrorLogger(log_dir="tests/logs")
    
    for failure in failures[:2]:  # 只演示前2个
        logger.log_error(failure)
    
    print("\n✅ 错误日志已保存到 tests/logs/errors.log")
    
    logger.save_error_report(failures)
    print("✅ 错误报告已保存到 tests/logs/auto_fix_report.json")


def demo_retry_mechanism():
    """演示重试机制"""
    print_header("演示4: 重试机制")
    
    retry_config = RetryConfig(
        max_retries=3,
        retry_delay=2,
        exponential_backoff=True,
        max_delay=30
    )
    
    runner = AutoFixRunner(retry_config=retry_config)
    
    test_failures = [
        TestFailure(
            test_name="test_network_timeout",
            error_message="Timeout error",
            error_type=ErrorType.TIMEOUT_ERROR,
            traceback="TimeoutError at line 25"
        ),
        TestFailure(
            test_name="test_auth_error",
            error_message="401 Unauthorized",
            error_type=ErrorType.AUTH_ERROR,
            traceback="AssertionError at line 30"
        )
    ]
    
    print("\n重试策略测试:")
    for failure in test_failures:
        should_retry = runner.should_retry(failure)
        print(f"\n测试: {failure.test_name}")
        print(f"错误类型: {failure.error_type.value}")
        print(f"是否应该重试: {'✅ 是' if should_retry else '❌ 否'}")
        
        if should_retry:
            print("重试延迟计算:")
            for attempt in range(1, retry_config.max_retries + 1):
                delay = runner.calculate_delay(attempt)
                print(f"  第 {attempt} 次重试: 延迟 {delay} 秒")


def demo_error_type_detection():
    """演示错误类型检测"""
    print_header("演示5: 错误类型自动检测")
    
    test_errors = [
        ("Connection refused", "网络连接错误"),
        ("Timeout error", "请求超时"),
        ("401 Unauthorized", "认证错误"),
        ("403 Forbidden", "权限错误"),
        ("404 Not Found", "API资源未找到"),
        ("500 Internal Server Error", "API服务错误"),
        ("400 Bad Request", "参数验证错误"),
        ("Database connection failed", "数据库错误"),
        ("AssertionError: Expected 200", "数据断言错误"),
    ]
    
    print("\n错误类型自动检测:")
    for error_msg, description in test_errors:
        error_type = AutoFixConfig.get_error_type_from_message(error_msg)
        suggestion = AutoFixConfig.get_fix_suggestion(error_type)
        print(f"\n错误: {error_msg}")
        print(f"类型: {error_type.value}")
        print(f"描述: {suggestion.description}")


def demo_full_workflow():
    """演示完整工作流程"""
    print_header("演示6: 完整工作流程")
    
    print("\n自动修复完整工作流程:")
    print("1. 运行测试")
    print("   └─ python -m pytest tests/ -v")
    print("\n2. 检测失败的测试")
    print("   └─ 解析pytest输出")
    print("   └─ 提取错误信息")
    print("   └─ 分类错误类型")
    print("\n3. 记录错误日志")
    print("   └─ 保存到 tests/logs/errors.log")
    print("   └─ 记录请求和响应数据")
    print("\n4. 生成修复建议")
    print("   └─ 根据错误类型生成建议")
    print("   └─ 显示修复步骤")
    print("\n5. 自动重试失败的测试")
    print("   └─ 使用指数退避策略")
    print("   └─ 记录重试结果")
    print("\n6. 生成最终报告")
    print("   └─ JSON格式报告")
    print("   └─ HTML可视化报告")
    
    print("\n命令行使用:")
    print("  python tests/auto_fix.py --max-retries 3 --retry-delay 5")
    
    print("\nPython API使用:")
    print("""
    from tests.auto_fix import AutoFixRunner
    from tests.auto_fix_config import RetryConfig
    
    retry_config = RetryConfig(max_retries=3, retry_delay=5)
    runner = AutoFixRunner(retry_config=retry_config)
    result = runner.run_with_retry(test_path="tests/")
    """)


def demo_ci_cd_integration():
    """演示CI/CD集成"""
    print_header("演示7: CI/CD集成")
    
    print("\nCI/CD集成方式:")
    print("\n1. GitHub Actions:")
    print("   - 工作流文件: .github/workflows/auto_fix_tests.yml")
    print("   - 自动运行测试")
    print("   - 自动修复和重试")
    print("   - 上传测试报告")
    
    print("\n2. Jenkins Pipeline:")
    print("""
    pipeline {
        stage('Run Auto Fix Tests') {
            steps {
                sh 'python tests/ci_integration.py'
            }
        }
    }
    """)
    
    print("\n3. GitLab CI:")
    print("""
    auto_fix_tests:
      script:
        - python tests/ci_integration.py
      artifacts:
        paths:
          - tests/report/
    """)
    
    print("\n环境变量配置:")
    print("  MAX_RETRIES=3")
    print("  RETRY_DELAY=5")
    print("  EXPONENTIAL_BACKOFF=true")
    print("  MAX_DELAY=60")


def main():
    """主演示函数"""
    print("\n" + "="*80)
    print("自动修复和重试机制 - 完整功能演示")
    print("="*80)
    
    failures = demo_error_detection()
    
    demo_fix_suggestions(failures)
    
    demo_error_logging(failures)
    
    demo_retry_mechanism()
    
    demo_error_type_detection()
    
    demo_full_workflow()
    
    demo_ci_cd_integration()
    
    print_header("演示完成")
    
    print("\n✅ 所有功能演示完成！")
    print("\n生成的文件:")
    print("  - tests/logs/errors.log (错误日志)")
    print("  - tests/logs/retries.log (重试日志)")
    print("  - tests/logs/auto_fix_report.json (JSON报告)")
    
    print("\n更多信息请查看:")
    print("  - tests/AUTO_FIX_README.md (详细使用文档)")
    print("  - tests/AUTO_FIX_SUMMARY.md (实现总结)")
    print("  - tests/auto_fix_example.py (更多示例)")
    
    print("\n开始使用:")
    print("  python tests/auto_fix.py --test-path tests/ --max-retries 3")


if __name__ == "__main__":
    main()

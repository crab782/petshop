"""
自动修复机制功能测试
"""
import pytest
import json
from pathlib import Path
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


class TestFailedTestDetector:
    """测试失败检测器"""
    
    def test_parse_pytest_output(self):
        pytest_output = """
FAILED tests/test_example.py::test_api - AssertionError: Expected 200
FAILED tests/test_example.py::test_auth - AssertionError: 401 Unauthorized
        """
        
        detector = FailedTestDetector()
        failures = detector.parse_pytest_output(pytest_output)
        
        assert len(failures) == 2
        assert failures[0].test_name == "tests/test_example.py::test_api"
        assert failures[1].test_name == "tests/test_example.py::test_auth"
    
    def test_categorize_failure(self):
        failure = TestFailure(
            test_name="test_connection",
            error_message="Connection refused",
            error_type=ErrorType.UNKNOWN_ERROR,
            traceback=""
        )
        
        detector = FailedTestDetector()
        error_type = detector.categorize_failure(failure)
        
        assert error_type == ErrorType.NETWORK_ERROR
    
    def test_error_type_detection(self):
        test_cases = [
            ("Connection refused", ErrorType.NETWORK_ERROR),
            ("Timeout error", ErrorType.TIMEOUT_ERROR),
            ("401 Unauthorized", ErrorType.AUTH_ERROR),
            ("403 Forbidden", ErrorType.PERMISSION_ERROR),
            ("404 Not Found", ErrorType.API_ERROR),
            ("500 Internal Server Error", ErrorType.DATABASE_ERROR),
            ("AssertionError", ErrorType.DATA_ERROR),
        ]
        
        for error_msg, expected_type in test_cases:
            error_type = AutoFixConfig.get_error_type_from_message(error_msg)
            assert error_type == expected_type, f"Failed for: {error_msg}"


class TestFixSuggestionGenerator:
    """测试修复建议生成器"""
    
    def test_generate_suggestions(self):
        failure = TestFailure(
            test_name="test_login",
            error_message="401 Unauthorized",
            error_type=ErrorType.AUTH_ERROR,
            traceback=""
        )
        
        generator = FixSuggestionGenerator()
        suggestions = generator.generate_suggestions(failure)
        
        assert len(suggestions) > 0
        assert any("用户名" in s for s in suggestions)
    
    def test_format_action(self):
        generator = FixSuggestionGenerator()
        action = "API地址: {api_base_url}"
        formatted = generator._format_action(action)
        
        assert "{api_base_url}" not in formatted
        assert "http" in formatted


class TestErrorLogger:
    """测试错误日志记录器"""
    
    def test_log_error(self, tmp_path):
        logger = ErrorLogger(log_dir=str(tmp_path))
        
        failure = TestFailure(
            test_name="test_example",
            error_message="Test error",
            error_type=ErrorType.API_ERROR,
            traceback="Test traceback"
        )
        
        logger.log_error(failure)
        
        log_file = tmp_path / "errors.log"
        assert log_file.exists()
        
        content = log_file.read_text(encoding='utf-8')
        assert "test_example" in content
        assert "Test error" in content
    
    def test_log_retry(self, tmp_path):
        logger = ErrorLogger(log_dir=str(tmp_path))
        
        result = RetryResult(
            test_name="test_example",
            attempt=1,
            success=True,
            duration=1.5
        )
        
        logger.log_retry(result)
        
        log_file = tmp_path / "retries.log"
        assert log_file.exists()
        
        content = log_file.read_text(encoding='utf-8')
        assert "test_example" in content
        assert "Success: True" in content
    
    def test_save_error_report(self, tmp_path):
        logger = ErrorLogger(log_dir=str(tmp_path))
        
        failures = [
            TestFailure(
                test_name="test1",
                error_message="Error 1",
                error_type=ErrorType.API_ERROR,
                traceback=""
            ),
            TestFailure(
                test_name="test2",
                error_message="Error 2",
                error_type=ErrorType.AUTH_ERROR,
                traceback="",
                fixed=True
            )
        ]
        
        logger.save_error_report(failures)
        
        report_file = tmp_path / "auto_fix_report.json"
        assert report_file.exists()
        
        with open(report_file, 'r', encoding='utf-8') as f:
            report = json.load(f)
        
        assert report['total_failures'] == 2
        assert report['summary']['fixed_count'] == 1
        assert report['summary']['unfixed_count'] == 1


class TestAutoFixRunner:
    """测试自动修复运行器"""
    
    def test_should_retry(self):
        retry_config = RetryConfig(
            max_retries=3,
            retry_on_errors=[ErrorType.NETWORK_ERROR, ErrorType.TIMEOUT_ERROR]
        )
        
        runner = AutoFixRunner(retry_config=retry_config)
        
        network_failure = TestFailure(
            test_name="test_network",
            error_message="Connection refused",
            error_type=ErrorType.NETWORK_ERROR,
            traceback=""
        )
        
        auth_failure = TestFailure(
            test_name="test_auth",
            error_message="401 Unauthorized",
            error_type=ErrorType.AUTH_ERROR,
            traceback=""
        )
        
        assert runner.should_retry(network_failure) is True
        assert runner.should_retry(auth_failure) is False
    
    def test_should_not_retry_after_max(self):
        retry_config = RetryConfig(max_retries=3)
        runner = AutoFixRunner(retry_config=retry_config)
        
        failure = TestFailure(
            test_name="test",
            error_message="Error",
            error_type=ErrorType.NETWORK_ERROR,
            traceback="",
            retry_count=3
        )
        
        assert runner.should_retry(failure) is False
    
    def test_calculate_delay_no_backoff(self):
        retry_config = RetryConfig(
            retry_delay=5,
            exponential_backoff=False
        )
        
        runner = AutoFixRunner(retry_config=retry_config)
        
        assert runner.calculate_delay(1) == 5
        assert runner.calculate_delay(2) == 5
        assert runner.calculate_delay(3) == 5
    
    def test_calculate_delay_with_backoff(self):
        retry_config = RetryConfig(
            retry_delay=2,
            exponential_backoff=True,
            max_delay=60
        )
        
        runner = AutoFixRunner(retry_config=retry_config)
        
        assert runner.calculate_delay(1) == 2
        assert runner.calculate_delay(2) == 4
        assert runner.calculate_delay(3) == 8
        assert runner.calculate_delay(10) == 60  # max_delay


class TestRetryConfig:
    """测试重试配置"""
    
    def test_default_config(self):
        config = RetryConfig()
        
        assert config.max_retries == 3
        assert config.retry_delay == 5
        assert config.exponential_backoff is True
        assert config.max_delay == 60
    
    def test_custom_config(self):
        config = RetryConfig(
            max_retries=5,
            retry_delay=10,
            exponential_backoff=False,
            max_delay=120
        )
        
        assert config.max_retries == 5
        assert config.retry_delay == 10
        assert config.exponential_backoff is False
        assert config.max_delay == 120


class TestAutoFixConfig:
    """测试自动修复配置"""
    
    def test_error_patterns_exist(self):
        assert len(AutoFixConfig.ERROR_PATTERNS) > 0
    
    def test_fix_suggestions_exist(self):
        assert len(AutoFixConfig.FIX_SUGGESTIONS) > 0
        assert ErrorType.NETWORK_ERROR in AutoFixConfig.FIX_SUGGESTIONS
        assert ErrorType.AUTH_ERROR in AutoFixConfig.FIX_SUGGESTIONS
    
    def test_log_config(self):
        assert "log_dir" in AutoFixConfig.LOG_CONFIG
        assert "error_log_file" in AutoFixConfig.LOG_CONFIG
        assert "retry_log_file" in AutoFixConfig.LOG_CONFIG
    
    def test_get_retry_config(self):
        config = AutoFixConfig.get_retry_config()
        
        assert isinstance(config, RetryConfig)
        assert config.max_retries > 0
        assert config.retry_delay > 0


if __name__ == "__main__":
    pytest.main([__file__, "-v"])

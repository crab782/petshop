"""
性能测试模块

该模块提供了宠物服务平台的性能测试功能，包括：
- 性能测试配置
- 性能测试报告生成
- Locust性能测试场景
"""

from .performance_config import (
    PerformanceTestConfig,
    UserLoadConfig,
    MerchantLoadConfig,
    AdminLoadConfig,
    PerformanceBenchmark,
    TestScenario,
    get_default_config,
    create_custom_config,
)

from .performance_reporter import (
    PerformanceReporter,
    PerformanceMetrics,
    PerformanceReport,
    create_test_results_from_locust,
)

__all__ = [
    "PerformanceTestConfig",
    "UserLoadConfig",
    "MerchantLoadConfig",
    "AdminLoadConfig",
    "PerformanceBenchmark",
    "TestScenario",
    "get_default_config",
    "create_custom_config",
    "PerformanceReporter",
    "PerformanceMetrics",
    "PerformanceReport",
    "create_test_results_from_locust",
]

"""
性能测试配置模块

该模块定义了性能测试的各种配置参数，包括：
- 基础性能测试配置
- 用户负载配置
- 商家负载配置
- 管理员负载配置
"""

from dataclasses import dataclass, field
from typing import Dict, List, Optional
from enum import Enum


class TestScenario(Enum):
    """测试场景枚举"""
    USER_LOGIN = "user_login"
    USER_BROWSE_SERVICES = "user_browse_services"
    USER_CREATE_ORDER = "user_create_order"
    USER_QUERY_ORDERS = "user_query_orders"
    MERCHANT_LOGIN = "merchant_login"
    MERCHANT_MANAGE_SERVICES = "merchant_manage_services"
    MERCHANT_PROCESS_ORDERS = "merchant_process_orders"
    ADMIN_LOGIN = "admin_login"
    ADMIN_QUERY_USERS = "admin_query_users"
    ADMIN_AUDIT_MERCHANTS = "admin_audit_merchants"
    MIXED_USER_BEHAVIOR = "mixed_user_behavior"
    MIXED_MERCHANT_BEHAVIOR = "mixed_merchant_behavior"
    MIXED_ADMIN_BEHAVIOR = "mixed_admin_behavior"


@dataclass
class PerformanceTestConfig:
    """
    性能测试基础配置类
    
    定义了性能测试的基本参数和性能基准
    """
    max_response_time_simple: int = 500
    max_response_time_complex: int = 2000
    min_throughput: int = 100
    max_error_rate: float = 0.01
    concurrent_users: int = 100
    spawn_rate: int = 10
    test_duration: int = 300
    base_url: str = "http://localhost:8080"
    api_prefix: str = "/api"
    
    def get_full_url(self, endpoint: str) -> str:
        """获取完整的API URL"""
        return f"{self.base_url}{self.api_prefix}{endpoint}"
    
    def validate_config(self) -> bool:
        """验证配置参数是否合理"""
        if self.concurrent_users <= 0:
            raise ValueError("并发用户数必须大于0")
        if self.spawn_rate <= 0:
            raise ValueError("孵化率必须大于0")
        if self.test_duration <= 0:
            raise ValueError("测试持续时间必须大于0")
        if self.max_error_rate < 0 or self.max_error_rate > 1:
            raise ValueError("错误率必须在0-1之间")
        if self.min_throughput <= 0:
            raise ValueError("最小吞吐量必须大于0")
        return True
    
    def to_dict(self) -> Dict:
        """将配置转换为字典"""
        return {
            "max_response_time_simple": self.max_response_time_simple,
            "max_response_time_complex": self.max_response_time_complex,
            "min_throughput": self.min_throughput,
            "max_error_rate": self.max_error_rate,
            "concurrent_users": self.concurrent_users,
            "spawn_rate": self.spawn_rate,
            "test_duration": self.test_duration,
            "base_url": self.base_url,
            "api_prefix": self.api_prefix,
        }


@dataclass
class UserLoadConfig(PerformanceTestConfig):
    """
    用户端负载配置
    
    定义了用户端性能测试的特定参数
    """
    user_login_weight: int = 10
    user_browse_weight: int = 40
    user_create_order_weight: int = 20
    user_query_order_weight: int = 30
    user_wait_time_min: int = 1
    user_wait_time_max: int = 5
    user_test_scenarios: List[TestScenario] = field(default_factory=lambda: [
        TestScenario.USER_LOGIN,
        TestScenario.USER_BROWSE_SERVICES,
        TestScenario.USER_CREATE_ORDER,
        TestScenario.USER_QUERY_ORDERS,
        TestScenario.MIXED_USER_BEHAVIOR,
    ])
    
    def get_user_weights(self) -> Dict[str, int]:
        """获取用户行为权重"""
        return {
            "login": self.user_login_weight,
            "browse": self.user_browse_weight,
            "create_order": self.user_create_order_weight,
            "query_order": self.user_query_order_weight,
        }
    
    def to_dict(self) -> Dict:
        """将配置转换为字典"""
        base_dict = super().to_dict()
        base_dict.update({
            "user_login_weight": self.user_login_weight,
            "user_browse_weight": self.user_browse_weight,
            "user_create_order_weight": self.user_create_order_weight,
            "user_query_order_weight": self.user_query_order_weight,
            "user_wait_time_min": self.user_wait_time_min,
            "user_wait_time_max": self.user_wait_time_max,
            "user_test_scenarios": [s.value for s in self.user_test_scenarios],
        })
        return base_dict


@dataclass
class MerchantLoadConfig(PerformanceTestConfig):
    """
    商家端负载配置
    
    定义了商家端性能测试的特定参数
    """
    merchant_login_weight: int = 10
    merchant_manage_services_weight: int = 40
    merchant_process_orders_weight: int = 50
    merchant_wait_time_min: int = 2
    merchant_wait_time_max: int = 8
    merchant_test_scenarios: List[TestScenario] = field(default_factory=lambda: [
        TestScenario.MERCHANT_LOGIN,
        TestScenario.MERCHANT_MANAGE_SERVICES,
        TestScenario.MERCHANT_PROCESS_ORDERS,
        TestScenario.MIXED_MERCHANT_BEHAVIOR,
    ])
    
    def get_merchant_weights(self) -> Dict[str, int]:
        """获取商家行为权重"""
        return {
            "login": self.merchant_login_weight,
            "manage_services": self.merchant_manage_services_weight,
            "process_orders": self.merchant_process_orders_weight,
        }
    
    def to_dict(self) -> Dict:
        """将配置转换为字典"""
        base_dict = super().to_dict()
        base_dict.update({
            "merchant_login_weight": self.merchant_login_weight,
            "merchant_manage_services_weight": self.merchant_manage_services_weight,
            "merchant_process_orders_weight": self.merchant_process_orders_weight,
            "merchant_wait_time_min": self.merchant_wait_time_min,
            "merchant_wait_time_max": self.merchant_wait_time_max,
            "merchant_test_scenarios": [s.value for s in self.merchant_test_scenarios],
        })
        return base_dict


@dataclass
class AdminLoadConfig(PerformanceTestConfig):
    """
    管理员端负载配置
    
    定义了管理员端性能测试的特定参数
    """
    admin_login_weight: int = 10
    admin_query_users_weight: int = 40
    admin_audit_merchants_weight: int = 50
    admin_wait_time_min: int = 3
    admin_wait_time_max: int = 10
    admin_test_scenarios: List[TestScenario] = field(default_factory=lambda: [
        TestScenario.ADMIN_LOGIN,
        TestScenario.ADMIN_QUERY_USERS,
        TestScenario.ADMIN_AUDIT_MERCHANTS,
        TestScenario.MIXED_ADMIN_BEHAVIOR,
    ])
    
    def get_admin_weights(self) -> Dict[str, int]:
        """获取管理员行为权重"""
        return {
            "login": self.admin_login_weight,
            "query_users": self.admin_query_users_weight,
            "audit_merchants": self.admin_audit_merchants_weight,
        }
    
    def to_dict(self) -> Dict:
        """将配置转换为字典"""
        base_dict = super().to_dict()
        base_dict.update({
            "admin_login_weight": self.admin_login_weight,
            "admin_query_users_weight": self.admin_query_users_weight,
            "admin_audit_merchants_weight": self.admin_audit_merchants_weight,
            "admin_wait_time_min": self.admin_wait_time_min,
            "admin_wait_time_max": self.admin_wait_time_max,
            "admin_test_scenarios": [s.value for s in self.admin_test_scenarios],
        })
        return base_dict


@dataclass
class PerformanceBenchmark:
    """
    性能基准配置
    
    定义了性能测试的基准指标
    """
    response_time_avg_threshold: int = 300
    response_time_p95_threshold: int = 800
    response_time_p99_threshold: int = 1500
    throughput_threshold: int = 100
    error_rate_threshold: float = 0.01
    cpu_usage_threshold: float = 80.0
    memory_usage_threshold: float = 85.0
    
    def check_response_time(self, avg_time: float, p95_time: float, p99_time: float) -> Dict[str, bool]:
        """检查响应时间是否符合基准"""
        return {
            "avg_time_ok": avg_time <= self.response_time_avg_threshold,
            "p95_time_ok": p95_time <= self.response_time_p95_threshold,
            "p99_time_ok": p99_time <= self.response_time_p99_threshold,
        }
    
    def check_throughput(self, throughput: float) -> bool:
        """检查吞吐量是否符合基准"""
        return throughput >= self.throughput_threshold
    
    def check_error_rate(self, error_rate: float) -> bool:
        """检查错误率是否符合基准"""
        return error_rate <= self.error_rate_threshold
    
    def to_dict(self) -> Dict:
        """将基准配置转换为字典"""
        return {
            "response_time_avg_threshold": self.response_time_avg_threshold,
            "response_time_p95_threshold": self.response_time_p95_threshold,
            "response_time_p99_threshold": self.response_time_p99_threshold,
            "throughput_threshold": self.throughput_threshold,
            "error_rate_threshold": self.error_rate_threshold,
            "cpu_usage_threshold": self.cpu_usage_threshold,
            "memory_usage_threshold": self.memory_usage_threshold,
        }


def get_default_config(scenario: TestScenario) -> PerformanceTestConfig:
    """
    根据测试场景获取默认配置
    
    Args:
        scenario: 测试场景枚举值
    
    Returns:
        对应的配置实例
    """
    config_map = {
        TestScenario.USER_LOGIN: UserLoadConfig(),
        TestScenario.USER_BROWSE_SERVICES: UserLoadConfig(),
        TestScenario.USER_CREATE_ORDER: UserLoadConfig(),
        TestScenario.USER_QUERY_ORDERS: UserLoadConfig(),
        TestScenario.MERCHANT_LOGIN: MerchantLoadConfig(),
        TestScenario.MERCHANT_MANAGE_SERVICES: MerchantLoadConfig(),
        TestScenario.MERCHANT_PROCESS_ORDERS: MerchantLoadConfig(),
        TestScenario.ADMIN_LOGIN: AdminLoadConfig(),
        TestScenario.ADMIN_QUERY_USERS: AdminLoadConfig(),
        TestScenario.ADMIN_AUDIT_MERCHANTS: AdminLoadConfig(),
        TestScenario.MIXED_USER_BEHAVIOR: UserLoadConfig(),
        TestScenario.MIXED_MERCHANT_BEHAVIOR: MerchantLoadConfig(),
        TestScenario.MIXED_ADMIN_BEHAVIOR: AdminLoadConfig(),
    }
    return config_map.get(scenario, PerformanceTestConfig())


def create_custom_config(
    scenario: TestScenario,
    concurrent_users: Optional[int] = None,
    spawn_rate: Optional[int] = None,
    test_duration: Optional[int] = None,
    **kwargs
) -> PerformanceTestConfig:
    """
    创建自定义配置
    
    Args:
        scenario: 测试场景
        concurrent_users: 并发用户数
        spawn_rate: 孵化率
        test_duration: 测试持续时间
        **kwargs: 其他配置参数
    
    Returns:
        自定义配置实例
    """
    config = get_default_config(scenario)
    
    if concurrent_users is not None:
        config.concurrent_users = concurrent_users
    if spawn_rate is not None:
        config.spawn_rate = spawn_rate
    if test_duration is not None:
        config.test_duration = test_duration
    
    for key, value in kwargs.items():
        if hasattr(config, key):
            setattr(config, key, value)
    
    config.validate_config()
    return config

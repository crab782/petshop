"""
测试数据管理模块

该模块提供了完整的测试数据管理功能，包括：
- 测试数据构建器（TestDataBuilder）：使用工厂模式创建测试数据
- 测试数据管理器（TestDataManager）：管理测试数据的生命周期
- 测试数据清理工具：提供数据清理功能

使用示例：
    from tests.testdata import TestDataBuilder, TestDataManager

    # 构建测试数据
    builder = TestDataBuilder()
    user_data = builder.build_user(username="test_user")

    # 管理测试数据
    manager = TestDataManager()
    user = manager.prepare_test_data('user', username="test_user")
    # ... 测试代码 ...
    manager.cleanup_test_data(user['id'])
"""

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import (
    cleanup_all,
    cleanup_appointments,
    cleanup_merchants,
    cleanup_orders,
    cleanup_products,
    cleanup_reviews,
    cleanup_services,
    cleanup_users,
)
from tests.testdata.data_manager import TestDataManager

__all__ = [
    "TestDataBuilder",
    "TestDataManager",
    "cleanup_users",
    "cleanup_merchants",
    "cleanup_services",
    "cleanup_products",
    "cleanup_appointments",
    "cleanup_orders",
    "cleanup_reviews",
    "cleanup_all",
]

__version__ = "1.0.0"

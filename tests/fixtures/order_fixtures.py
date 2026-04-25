"""
订单相关测试数据Fixtures

提供预约和商品订单测试数据的创建、管理和清理功能。
"""

from decimal import Decimal
from typing import Any, Dict, List

import pytest

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import cleanup_appointments, cleanup_orders
from tests.testdata.data_manager import TestDataManager


@pytest.fixture(scope="function")
def test_appointment() -> Dict[str, Any]:
    """
    创建测试预约（function作用域）

    每个测试函数都会创建一个新的测试预约。

    Returns:
        Dict[str, Any]: 预约数据字典

    使用示例:
        def test_appointment_creation(test_appointment):
            assert test_appointment['appointment_time'] is not None
            assert test_appointment['status'] == 'pending'
    """
    builder = TestDataBuilder()
    appointment_data = builder.build_appointment()
    return appointment_data


@pytest.fixture(scope="function")
def test_product_order() -> Dict[str, Any]:
    """
    创建测试商品订单（function作用域）

    每个测试函数都会创建一个新的测试商品订单。

    Returns:
        Dict[str, Any]: 商品订单数据字典

    使用示例:
        def test_product_order_creation(test_product_order):
            assert test_product_order['order_no'] is not None
            assert test_product_order['status'] == 'pending'
    """
    builder = TestDataBuilder()
    order_data = builder.build_order()
    return order_data


@pytest.fixture(scope="function")
def test_appointment_with_user(test_user) -> Dict[str, Any]:
    """
    创建带用户的测试预约（function作用域）

    创建一个关联到用户的测试预约。

    Args:
        test_user: 测试用户fixture

    Returns:
        Dict[str, Any]: 包含用户信息的预约数据字典

    使用示例:
        def test_appointment_user_relation(test_appointment_with_user):
            assert test_appointment_with_user['user_id'] is not None
    """
    builder = TestDataBuilder()
    user_id = test_user.get("id", 1)
    appointment_data = builder.build_appointment(user_id=user_id)
    return appointment_data


@pytest.fixture(scope="function")
def test_order_with_user(test_user) -> Dict[str, Any]:
    """
    创建带用户的测试订单（function作用域）

    创建一个关联到用户的测试订单。

    Args:
        test_user: 测试用户fixture

    Returns:
        Dict[str, Any]: 包含用户信息的订单数据字典

    使用示例:
        def test_order_user_relation(test_order_with_user):
            assert test_order_with_user['user_id'] is not None
    """
    builder = TestDataBuilder()
    user_id = test_user.get("id", 1)
    order_data = builder.build_order(user_id=user_id)
    return order_data


@pytest.fixture(scope="function")
def clean_appointments(db_connection):
    """
    清理测试预约数据（function作用域）

    测试结束后自动清理创建的预约数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_appointment_cleanup(clean_appointments):
            # 创建测试预约
            # 测试结束后自动清理
    """
    appointment_ids = []

    yield appointment_ids

    if appointment_ids and db_connection:
        cleanup_appointments(appointment_ids, db_connection)


@pytest.fixture(scope="function")
def clean_orders(db_connection):
    """
    清理测试订单数据（function作用域）

    测试结束后自动清理创建的订单数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_order_cleanup(clean_orders):
            # 创建测试订单
            # 测试结束后自动清理
    """
    order_ids = []

    yield order_ids

    if order_ids and db_connection:
        cleanup_orders(order_ids, db_connection)


@pytest.fixture(scope="function")
def test_appointment_with_cleanup(db_connection, test_appointment) -> Dict[str, Any]:
    """
    创建测试预约并自动清理（function作用域）

    创建测试预约并在测试结束后自动清理数据库中的预约记录。

    Args:
        db_connection: 数据库连接fixture
        test_appointment: 测试预约fixture

    Returns:
        Dict[str, Any]: 预约数据字典
    """
    yield test_appointment

    if db_connection and "id" in test_appointment:
        cleanup_appointments([test_appointment["id"]], db_connection)


@pytest.fixture(scope="function")
def test_order_with_cleanup(db_connection, test_product_order) -> Dict[str, Any]:
    """
    创建测试订单并自动清理（function作用域）

    创建测试订单并在测试结束后自动清理数据库中的订单记录。

    Args:
        db_connection: 数据库连接fixture
        test_product_order: 测试订单fixture

    Returns:
        Dict[str, Any]: 订单数据字典
    """
    yield test_product_order

    if db_connection and "id" in test_product_order:
        cleanup_orders([test_product_order["id"]], db_connection)


@pytest.fixture(scope="function")
def test_order_data_manager() -> TestDataManager:
    """
    获取订单数据管理器（function作用域）

    提供TestDataManager实例，用于更复杂的订单数据管理场景。

    Returns:
        TestDataManager: 数据管理器实例

    使用示例:
        def test_complex_order_scenario(test_order_data_manager):
            order = test_order_data_manager.prepare_test_data('order')
            # 进行复杂操作
            test_order_data_manager.cleanup_all_test_data()
    """
    manager = TestDataManager()
    return manager


@pytest.fixture(scope="function")
def test_order_builder() -> TestDataBuilder:
    """
    获取订单数据构建器（function作用域）

    提供TestDataBuilder实例，用于自定义订单数据构建。

    Returns:
        TestDataBuilder: 数据构建器实例

    使用示例:
        def test_custom_order(test_order_builder):
            order = test_order_builder.build_order(
                total_price=Decimal('500.00'),
                status='paid'
            )
            assert order['total_price'] == Decimal('500.00')
    """
    return TestDataBuilder()


@pytest.fixture(scope="function")
def test_pending_appointment() -> Dict[str, Any]:
    """
    创建待处理状态测试预约（function作用域）

    创建一个状态为pending的测试预约，这是默认状态。

    Returns:
        Dict[str, Any]: 待处理预约数据字典
    """
    builder = TestDataBuilder()
    appointment_data = builder.build_appointment(status="pending")
    return appointment_data


@pytest.fixture(scope="function")
def test_confirmed_appointment() -> Dict[str, Any]:
    """
    创建已确认状态测试预约（function作用域）

    创建一个状态为confirmed的测试预约。

    Returns:
        Dict[str, Any]: 已确认预约数据字典
    """
    builder = TestDataBuilder()
    appointment_data = builder.build_appointment(status="confirmed")
    return appointment_data


@pytest.fixture(scope="function")
def test_completed_appointment() -> Dict[str, Any]:
    """
    创建已完成状态测试预约（function作用域）

    创建一个状态为completed的测试预约。

    Returns:
        Dict[str, Any]: 已完成预约数据字典
    """
    builder = TestDataBuilder()
    appointment_data = builder.build_appointment(status="completed")
    return appointment_data


@pytest.fixture(scope="function")
def test_cancelled_appointment() -> Dict[str, Any]:
    """
    创建已取消状态测试预约（function作用域）

    创建一个状态为cancelled的测试预约。

    Returns:
        Dict[str, Any]: 已取消预约数据字典
    """
    builder = TestDataBuilder()
    appointment_data = builder.build_appointment(status="cancelled")
    return appointment_data


@pytest.fixture(scope="function")
def test_pending_order() -> Dict[str, Any]:
    """
    创建待支付状态测试订单（function作用域）

    创建一个状态为pending的测试订单，这是默认状态。

    Returns:
        Dict[str, Any]: 待支付订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(status="pending")
    return order_data


@pytest.fixture(scope="function")
def test_paid_order() -> Dict[str, Any]:
    """
    创建已支付状态测试订单（function作用域）

    创建一个状态为paid的测试订单。

    Returns:
        Dict[str, Any]: 已支付订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(status="paid")
    return order_data


@pytest.fixture(scope="function")
def test_shipped_order() -> Dict[str, Any]:
    """
    创建已发货状态测试订单（function作用域）

    创建一个状态为shipped的测试订单。

    Returns:
        Dict[str, Any]: 已发货订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(status="shipped")
    return order_data


@pytest.fixture(scope="function")
def test_completed_order() -> Dict[str, Any]:
    """
    创建已完成状态测试订单（function作用域）

    创建一个状态为completed的测试订单。

    Returns:
        Dict[str, Any]: 已完成订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(status="completed")
    return order_data


@pytest.fixture(scope="function")
def test_cancelled_order() -> Dict[str, Any]:
    """
    创建已取消状态测试订单（function作用域）

    创建一个状态为cancelled的测试订单。

    Returns:
        Dict[str, Any]: 已取消订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(status="cancelled")
    return order_data


@pytest.fixture(scope="function")
def test_high_value_order() -> Dict[str, Any]:
    """
    创建高价值测试订单（function作用域）

    创建一个高价值订单，用于测试订单金额边界情况。

    Returns:
        Dict[str, Any]: 高价值订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(total_price=Decimal("9999.00"), status="paid")
    return order_data


@pytest.fixture(scope="function")
def test_low_value_order() -> Dict[str, Any]:
    """
    创建低价值测试订单（function作用域）

    创建一个低价值订单，用于测试订单金额边界情况。

    Returns:
        Dict[str, Any]: 低价值订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(total_price=Decimal("1.00"), status="pending")
    return order_data


@pytest.fixture(scope="function")
def test_wechat_pay_order() -> Dict[str, Any]:
    """
    创建微信支付测试订单（function作用域）

    创建一个使用微信支付的测试订单。

    Returns:
        Dict[str, Any]: 微信支付订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(pay_method="wechat", status="paid")
    return order_data


@pytest.fixture(scope="function")
def test_alipay_order() -> Dict[str, Any]:
    """
    创建支付宝支付测试订单（function作用域）

    创建一个使用支付宝支付的测试订单。

    Returns:
        Dict[str, Any]: 支付宝支付订单数据字典
    """
    builder = TestDataBuilder()
    order_data = builder.build_order(pay_method="alipay", status="paid")
    return order_data


@pytest.fixture(scope="function")
def test_appointment_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试预约（function作用域）

    使用TestDataBuilder的批量构建功能创建多个预约。

    Returns:
        List[Dict[str, Any]]: 预约数据列表

    使用示例:
        def test_batch_appointments(test_appointment_batch):
            assert len(test_appointment_batch) == 10
    """
    builder = TestDataBuilder()
    appointments = builder.build_batch("appointment", 10, status="pending")
    return appointments


@pytest.fixture(scope="function")
def test_order_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试订单（function作用域）

    使用TestDataBuilder的批量构建功能创建多个订单。

    Returns:
        List[Dict[str, Any]]: 订单数据列表

    使用示例:
        def test_batch_orders(test_order_batch):
            assert len(test_order_batch) == 10
    """
    builder = TestDataBuilder()
    orders = builder.build_batch("order", 10, status="pending")
    return orders

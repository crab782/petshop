"""
服务相关测试数据Fixtures

提供服务测试数据的创建、管理和清理功能。
"""

from decimal import Decimal
from typing import Any, Dict, List

import pytest

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import cleanup_services
from tests.testdata.data_manager import TestDataManager


@pytest.fixture(scope="function")
def test_service() -> Dict[str, Any]:
    """
    创建测试服务（function作用域）

    每个测试函数都会创建一个新的测试服务。

    Returns:
        Dict[str, Any]: 服务数据字典

    使用示例:
        def test_service_creation(test_service):
            assert test_service['name'] is not None
            assert test_service['price'] > 0
    """
    builder = TestDataBuilder()
    service_data = builder.build_service()
    return service_data


@pytest.fixture(scope="function")
def test_services() -> List[Dict[str, Any]]:
    """
    创建多个测试服务（function作用域）

    创建一组测试服务，用于需要多个服务的测试场景。

    Returns:
        List[Dict[str, Any]]: 服务数据列表

    使用示例:
        def test_service_list(test_services):
            assert len(test_services) >= 3
            for service in test_services:
                assert service['name'] is not None
    """
    builder = TestDataBuilder()
    services = []

    services.append(builder.build_service(name="service_1", price=Decimal("99.00"), category="美容"))
    services.append(builder.build_service(name="service_2", price=Decimal("199.00"), category="医疗"))
    services.append(builder.build_service(name="service_3", price=Decimal("299.00"), category="寄养"))

    return services


@pytest.fixture(scope="function")
def test_service_with_merchant(test_merchant) -> Dict[str, Any]:
    """
    创建带商家的测试服务（function作用域）

    创建一个关联到商家的测试服务。

    Args:
        test_merchant: 测试商家fixture

    Returns:
        Dict[str, Any]: 包含商家信息的服务数据字典

    使用示例:
        def test_service_merchant_relation(test_service_with_merchant):
            assert test_service_with_merchant['merchant_id'] is not None
    """
    builder = TestDataBuilder()
    merchant_id = test_merchant.get("id", 1)
    service_data = builder.build_service(merchant_id=merchant_id)
    return service_data


@pytest.fixture(scope="function")
def clean_services(db_connection):
    """
    清理测试服务数据（function作用域）

    测试结束后自动清理创建的服务数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_service_cleanup(clean_services):
            # 创建测试服务
            # 测试结束后自动清理
    """
    service_ids = []

    yield service_ids

    if service_ids and db_connection:
        cleanup_services(service_ids, db_connection)


@pytest.fixture(scope="function")
def test_service_with_cleanup(db_connection, test_service) -> Dict[str, Any]:
    """
    创建测试服务并自动清理（function作用域）

    创建测试服务并在测试结束后自动清理数据库中的服务记录。

    Args:
        db_connection: 数据库连接fixture
        test_service: 测试服务fixture

    Returns:
        Dict[str, Any]: 服务数据字典
    """
    yield test_service

    if db_connection and "id" in test_service:
        cleanup_services([test_service["id"]], db_connection)


@pytest.fixture(scope="function")
def test_service_data_manager() -> TestDataManager:
    """
    获取服务数据管理器（function作用域）

    提供TestDataManager实例，用于更复杂的服务数据管理场景。

    Returns:
        TestDataManager: 数据管理器实例

    使用示例:
        def test_complex_service_scenario(test_service_data_manager):
            service = test_service_data_manager.prepare_test_data('service')
            # 进行复杂操作
            test_service_data_manager.cleanup_all_test_data()
    """
    manager = TestDataManager()
    return manager


@pytest.fixture(scope="function")
def test_service_builder() -> TestDataBuilder:
    """
    获取服务数据构建器（function作用域）

    提供TestDataBuilder实例，用于自定义服务数据构建。

    Returns:
        TestDataBuilder: 数据构建器实例

    使用示例:
        def test_custom_service(test_service_builder):
            service = test_service_builder.build_service(
                name="custom_service",
                price=Decimal('150.00'),
                duration=90
            )
            assert service['name'] == "custom_service"
    """
    return TestDataBuilder()


@pytest.fixture(scope="function")
def test_service_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试服务（function作用域）

    使用TestDataBuilder的批量构建功能创建多个服务。

    Returns:
        List[Dict[str, Any]]: 服务数据列表

    使用示例:
        def test_batch_services(test_service_batch):
            assert len(test_service_batch) == 10
    """
    builder = TestDataBuilder()
    services = builder.build_batch("service", 10, status="enabled")
    return services


@pytest.fixture(scope="function")
def test_enabled_service() -> Dict[str, Any]:
    """
    创建启用状态测试服务（function作用域）

    创建一个状态为enabled的测试服务，这是默认状态。

    Returns:
        Dict[str, Any]: 启用服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(status="enabled")
    return service_data


@pytest.fixture(scope="function")
def test_disabled_service() -> Dict[str, Any]:
    """
    创建禁用状态测试服务（function作用域）

    创建一个状态为disabled的测试服务，用于测试服务状态相关功能。

    Returns:
        Dict[str, Any]: 禁用服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(status="disabled")
    return service_data


@pytest.fixture(scope="function")
def test_grooming_service() -> Dict[str, Any]:
    """
    创建美容类测试服务（function作用域）

    创建一个宠物美容类服务。

    Returns:
        Dict[str, Any]: 美容服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(
        name="宠物美容服务",
        description="专业宠物美容，包括洗澡、修剪、造型等",
        price=Decimal("128.00"),
        duration=90,
        category="美容",
    )
    return service_data


@pytest.fixture(scope="function")
def test_medical_service() -> Dict[str, Any]:
    """
    创建医疗类测试服务（function作用域）

    创建一个宠物医疗类服务。

    Returns:
        Dict[str, Any]: 医疗服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(
        name="宠物医疗服务",
        description="专业宠物医疗，包括体检、疫苗接种、疾病治疗等",
        price=Decimal("299.00"),
        duration=60,
        category="医疗",
    )
    return service_data


@pytest.fixture(scope="function")
def test_boarding_service() -> Dict[str, Any]:
    """
    创建寄养类测试服务（function作用域）

    创建一个宠物寄养类服务。

    Returns:
        Dict[str, Any]: 寄养服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(
        name="宠物寄养服务",
        description="专业宠物寄养，提供舒适的住宿环境和专业照顾",
        price=Decimal("88.00"),
        duration=1440,
        category="寄养",
    )
    return service_data


@pytest.fixture(scope="function")
def test_training_service() -> Dict[str, Any]:
    """
    创建训练类测试服务（function作用域）

    创建一个宠物训练类服务。

    Returns:
        Dict[str, Any]: 训练服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(
        name="宠物训练服务",
        description="专业宠物训练，包括基础训练、行为纠正等",
        price=Decimal("199.00"),
        duration=120,
        category="训练",
    )
    return service_data


@pytest.fixture(scope="function")
def test_expensive_service() -> Dict[str, Any]:
    """
    创建高价测试服务（function作用域）

    创建一个高价服务，用于测试价格边界情况。

    Returns:
        Dict[str, Any]: 高价服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(name="高级VIP服务", price=Decimal("9999.00"), duration=240)
    return service_data


@pytest.fixture(scope="function")
def test_cheap_service() -> Dict[str, Any]:
    """
    创建低价测试服务（function作用域）

    创建一个低价服务，用于测试价格边界情况。

    Returns:
        Dict[str, Any]: 低价服务数据字典
    """
    builder = TestDataBuilder()
    service_data = builder.build_service(name="基础服务", price=Decimal("9.90"), duration=30)
    return service_data

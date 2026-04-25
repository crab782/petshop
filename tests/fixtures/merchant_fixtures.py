"""
商家相关测试数据Fixtures

提供商家测试数据的创建、管理和清理功能。
"""

from typing import Any, Dict, List

import pytest

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import cleanup_merchants
from tests.testdata.data_manager import TestDataManager


@pytest.fixture(scope="function")
def test_merchant() -> Dict[str, Any]:
    """
    创建测试商家（function作用域）

    每个测试函数都会创建一个新的测试商家。

    Returns:
        Dict[str, Any]: 商家数据字典

    使用示例:
        def test_merchant_login(test_merchant):
            assert test_merchant['name'] is not None
            assert test_merchant['email'] is not None
    """
    builder = TestDataBuilder()
    merchant_data = builder.build_merchant()
    return merchant_data


@pytest.fixture(scope="session")
def test_merchant_session() -> Dict[str, Any]:
    """
    创建商家会话（session作用域）

    整个测试会话期间只创建一次，适用于需要长期存在的商家数据。

    Returns:
        Dict[str, Any]: 商家数据字典

    使用示例:
        def test_multiple_merchant_scenarios(test_merchant_session):
            # 所有测试共享同一个商家数据
            assert test_merchant_session['id'] is not None
    """
    builder = TestDataBuilder()
    merchant_data = builder.build_merchant(name="session_test_merchant", email="session_merchant@test.com")
    return merchant_data


@pytest.fixture(scope="function")
def test_merchant_token(http_client, test_merchant) -> str:
    """
    获取商家Token（function作用域）

    创建测试商家并获取认证Token。

    Args:
        http_client: HTTP客户端fixture
        test_merchant: 测试商家fixture

    Returns:
        str: 商家认证Token

    使用示例:
        def test_authenticated_merchant_request(test_merchant_token):
            headers = {"Authorization": f"Bearer {test_merchant_token}"}
            # 使用Token发送请求
    """
    response = http_client.post(
        "/api/auth/merchant/login", json_data={"phone": test_merchant["phone"], "password": test_merchant["password"]}
    )

    if response.status_code == 200:
        token = response.json().get("data", {}).get("token")
        if token:
            return token

    pytest.skip("无法获取商家Token")


@pytest.fixture(scope="function")
def test_merchants() -> List[Dict[str, Any]]:
    """
    创建多个测试商家（function作用域）

    创建一组测试商家，用于需要多个商家的测试场景。

    Returns:
        List[Dict[str, Any]]: 商家数据列表

    使用示例:
        def test_merchant_list(test_merchants):
            assert len(test_merchants) >= 3
            for merchant in test_merchants:
                assert merchant['name'] is not None
    """
    builder = TestDataBuilder()
    merchants = []

    merchants.append(builder.build_merchant(name="merchant_1", email="merchant1@test.com"))
    merchants.append(builder.build_merchant(name="merchant_2", email="merchant2@test.com"))
    merchants.append(builder.build_merchant(name="merchant_3", email="merchant3@test.com"))

    return merchants


@pytest.fixture(scope="function")
def clean_merchants(db_connection):
    """
    清理测试商家数据（function作用域）

    测试结束后自动清理创建的商家数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_merchant_cleanup(clean_merchants):
            # 创建测试商家
            # 测试结束后自动清理
    """
    merchant_ids = []

    yield merchant_ids

    if merchant_ids and db_connection:
        cleanup_merchants(merchant_ids, db_connection)


@pytest.fixture(scope="function")
def test_merchant_with_cleanup(db_connection, test_merchant) -> Dict[str, Any]:
    """
    创建测试商家并自动清理（function作用域）

    创建测试商家并在测试结束后自动清理数据库中的商家记录。

    Args:
        db_connection: 数据库连接fixture
        test_merchant: 测试商家fixture

    Returns:
        Dict[str, Any]: 商家数据字典
    """
    yield test_merchant

    if db_connection and "id" in test_merchant:
        cleanup_merchants([test_merchant["id"]], db_connection)


@pytest.fixture(scope="function")
def test_merchant_data_manager() -> TestDataManager:
    """
    获取商家数据管理器（function作用域）

    提供TestDataManager实例，用于更复杂的商家数据管理场景。

    Returns:
        TestDataManager: 数据管理器实例

    使用示例:
        def test_complex_merchant_scenario(test_merchant_data_manager):
            merchant = test_merchant_data_manager.prepare_test_data('merchant')
            # 进行复杂操作
            test_merchant_data_manager.cleanup_all_test_data()
    """
    manager = TestDataManager()
    return manager


@pytest.fixture(scope="function")
def test_merchant_builder() -> TestDataBuilder:
    """
    获取商家数据构建器（function作用域）

    提供TestDataBuilder实例，用于自定义商家数据构建。

    Returns:
        TestDataBuilder: 数据构建器实例

    使用示例:
        def test_custom_merchant(test_merchant_builder):
            merchant = test_merchant_builder.build_merchant(
                name="custom_merchant",
                email="custom@test.com",
                phone="13900000000"
            )
            assert merchant['name'] == "custom_merchant"
    """
    return TestDataBuilder()


@pytest.fixture(scope="function")
def test_merchant_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试商家（function作用域）

    使用TestDataBuilder的批量构建功能创建多个商家。

    Returns:
        List[Dict[str, Any]]: 商家数据列表

    使用示例:
        def test_batch_merchants(test_merchant_batch):
            assert len(test_merchant_batch) == 10
    """
    builder = TestDataBuilder()
    merchants = builder.build_batch("merchant", 10, status="approved")
    return merchants


@pytest.fixture(scope="function")
def test_pending_merchant() -> Dict[str, Any]:
    """
    创建待审核状态测试商家（function作用域）

    创建一个状态为pending的测试商家，用于测试商家审核相关功能。

    Returns:
        Dict[str, Any]: 待审核商家数据字典
    """
    builder = TestDataBuilder()
    merchant_data = builder.build_merchant(status="pending")
    return merchant_data


@pytest.fixture(scope="function")
def test_rejected_merchant() -> Dict[str, Any]:
    """
    创建已拒绝状态测试商家（function作用域）

    创建一个状态为rejected的测试商家，用于测试商家状态相关功能。

    Returns:
        Dict[str, Any]: 已拒绝商家数据字典
    """
    builder = TestDataBuilder()
    merchant_data = builder.build_merchant(status="rejected")
    return merchant_data


@pytest.fixture(scope="function")
def test_approved_merchant() -> Dict[str, Any]:
    """
    创建已通过状态测试商家（function作用域）

    创建一个状态为approved的测试商家，这是默认状态。

    Returns:
        Dict[str, Any]: 已通过商家数据字典
    """
    builder = TestDataBuilder()
    merchant_data = builder.build_merchant(status="approved")
    return merchant_data


@pytest.fixture(scope="function")
def test_merchant_with_services(test_merchant, test_services) -> Dict[str, Any]:
    """
    创建带服务的测试商家（function作用域）

    创建一个商家并关联多个测试服务。

    Args:
        test_merchant: 测试商家fixture
        test_services: 测试服务fixture

    Returns:
        Dict[str, Any]: 包含服务信息的商家数据字典
    """
    merchant_data = test_merchant.copy()
    merchant_data["services"] = test_services
    return merchant_data


@pytest.fixture(scope="function")
def test_merchant_with_products(test_merchant, test_products) -> Dict[str, Any]:
    """
    创建带商品的测试商家（function作用域）

    创建一个商家并关联多个测试商品。

    Args:
        test_merchant: 测试商家fixture
        test_products: 测试商品fixture

    Returns:
        Dict[str, Any]: 包含商品信息的商家数据字典
    """
    merchant_data = test_merchant.copy()
    merchant_data["products"] = test_products
    return merchant_data

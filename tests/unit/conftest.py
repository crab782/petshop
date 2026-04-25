"""
单元测试配置文件

提供单元测试专用的pytest fixtures，包括：
- unit_http_client: 单元测试专用的HTTP客户端
- unit_token_manager: 单元测试专用的Token管理器
- clean_database: 清理数据库的fixture
- test_data_builder: 测试数据构建器
"""

import logging
from typing import Generator

import pytest

from tests.config import config
from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import DataCleanup
from tests.utils import HTTPClient, TokenManager

logger = logging.getLogger(__name__)


@pytest.fixture(scope="function")
def unit_http_client() -> Generator[HTTPClient, None, None]:
    """
    单元测试专用的HTTP客户端

    每个测试函数都会创建一个新的客户端实例，确保测试之间的隔离性。

    Yields:
        HTTPClient: HTTP客户端实例

    使用示例:
        def test_example(unit_http_client):
            response = unit_http_client.get("/api/users")
            assert response.status_code == 200
    """
    client = HTTPClient()
    logger.info("创建单元测试HTTP客户端")
    yield client
    client.close()
    logger.info("关闭单元测试HTTP客户端")


@pytest.fixture(scope="function")
def unit_token_manager() -> TokenManager:
    """
    单元测试专用的Token管理器

    每个测试函数都会创建一个新的Token管理器实例。

    Returns:
        TokenManager: Token管理器实例

    使用示例:
        def test_example(unit_token_manager):
            unit_token_manager.set_token("user", "test_token")
            token = unit_token_manager.get_token("user")
            assert token == "test_token"
    """
    manager = TokenManager()
    logger.info("创建单元测试Token管理器")
    return manager


@pytest.fixture(scope="function")
def clean_database():
    """
    清理数据库的fixture

    在测试执行前后清理测试数据，确保测试环境的干净状态。

    Yields:
        DataCleanup: 数据清理工具实例

    使用示例:
        def test_example(clean_database):
            # 测试前数据库已清理
            # 执行测试操作
            # 测试后会自动清理
            pass
    """
    cleanup = DataCleanup()

    logger.info("开始清理测试数据")
    cleanup.cleanup_all()

    yield cleanup

    logger.info("测试完成，清理测试数据")
    cleanup.cleanup_all()


@pytest.fixture(scope="function")
def test_data_builder() -> TestDataBuilder:
    """
    测试数据构建器

    提供便捷的测试数据构建方法，支持自定义字段和默认值。

    Returns:
        TestDataBuilder: 测试数据构建器实例

    使用示例:
        def test_example(test_data_builder):
            user_data = test_data_builder.build_user(username="test_user")
            assert user_data["username"] == "test_user"
    """
    builder = TestDataBuilder()
    logger.info("创建测试数据构建器")
    return builder


@pytest.fixture(scope="function")
def test_user(unit_http_client: HTTPClient, test_data_builder: TestDataBuilder):
    """
    创建测试用户

    自动创建一个测试用户，并在测试结束后清理。

    Args:
        unit_http_client: HTTP客户端
        test_data_builder: 测试数据构建器

    Yields:
        dict: 创建的用户数据，包含token

    使用示例:
        def test_example(test_user):
            assert test_user["token"] is not None
            user_id = test_user["user"]["id"]
    """
    user_data = test_data_builder.build_user()

    response = unit_http_client.post("/api/auth/register", json_data=user_data)

    if response.status_code in [200, 201]:
        created_user = response.json().get("data", {})

        login_response = unit_http_client.post(
            "/api/auth/login", json_data={"phone": user_data["phone"], "password": user_data["password"]}
        )

        token = None
        if login_response.status_code == 200:
            token = login_response.json().get("data", {}).get("token")

        yield {
            "user": created_user,
            "token": token,
            "credentials": {"phone": user_data["phone"], "password": user_data["password"]},
        }
    else:
        pytest.skip(f"无法创建测试用户: {response.text}")


@pytest.fixture(scope="function")
def test_merchant(unit_http_client: HTTPClient, test_data_builder: TestDataBuilder):
    """
    创建测试商家

    自动创建一个测试商家，并在测试结束后清理。

    Args:
        unit_http_client: HTTP客户端
        test_data_builder: 测试数据构建器

    Yields:
        dict: 创建的商家数据，包含token

    使用示例:
        def test_example(test_merchant):
            assert test_merchant["token"] is not None
            merchant_id = test_merchant["merchant"]["id"]
    """
    merchant_data = test_data_builder.build_merchant()

    response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

    if response.status_code in [200, 201]:
        created_merchant = response.json().get("data", {})

        login_response = unit_http_client.post(
            "/api/auth/merchant/login",
            json_data={"phone": merchant_data["phone"], "password": merchant_data["password"]},
        )

        token = None
        if login_response.status_code == 200:
            token = login_response.json().get("data", {}).get("token")

        yield {
            "merchant": created_merchant,
            "token": token,
            "credentials": {"phone": merchant_data["phone"], "password": merchant_data["password"]},
        }
    else:
        pytest.skip(f"无法创建测试商家: {response.text}")


@pytest.fixture(scope="function")
def test_service(unit_http_client: HTTPClient, test_merchant: dict, test_data_builder: TestDataBuilder):
    """
    创建测试服务

    自动创建一个测试服务，并在测试结束后清理。

    Args:
        unit_http_client: HTTP客户端
        test_merchant: 测试商家数据
        test_data_builder: 测试数据构建器

    Yields:
        dict: 创建的服务数据

    使用示例:
        def test_example(test_service):
            assert test_service["id"] is not None
    """
    merchant_id = test_merchant["merchant"].get("id")
    service_data = test_data_builder.build_service(merchant_id=merchant_id)

    response = unit_http_client.post(
        "/api/merchant/services", json_data=service_data, headers={"Authorization": f"Bearer {test_merchant['token']}"}
    )

    if response.status_code in [200, 201]:
        created_service = response.json().get("data", {})
        yield created_service
    else:
        pytest.skip(f"无法创建测试服务: {response.text}")


@pytest.fixture(scope="function")
def test_product(unit_http_client: HTTPClient, test_merchant: dict, test_data_builder: TestDataBuilder):
    """
    创建测试商品

    自动创建一个测试商品，并在测试结束后清理。

    Args:
        unit_http_client: HTTP客户端
        test_merchant: 测试商家数据
        test_data_builder: 测试数据构建器

    Yields:
        dict: 创建的商品数据

    使用示例:
        def test_example(test_product):
            assert test_product["id"] is not None
    """
    merchant_id = test_merchant["merchant"].get("id")
    product_data = test_data_builder.build_product(merchant_id=merchant_id)

    response = unit_http_client.post(
        "/api/merchant/products", json_data=product_data, headers={"Authorization": f"Bearer {test_merchant['token']}"}
    )

    if response.status_code in [200, 201]:
        created_product = response.json().get("data", {})
        yield created_product
    else:
        pytest.skip(f"无法创建测试商品: {response.text}")


def pytest_configure(config):
    """
    配置单元测试的pytest markers
    """
    config.addinivalue_line("markers", "unit_api: mark test as unit API test")
    config.addinivalue_line("markers", "unit_user: mark test as unit user API test")
    config.addinivalue_line("markers", "unit_merchant: mark test as unit merchant API test")
    config.addinivalue_line("markers", "unit_product: mark test as unit product API test")
    config.addinivalue_line("markers", "unit_service: mark test as unit service API test")
    config.addinivalue_line("markers", "unit_order: mark test as unit order API test")
    config.addinivalue_line("markers", "unit_review: mark test as unit review API test")
    config.addinivalue_line("markers", "unit_auth: mark test as unit authentication test")
    config.addinivalue_line("markers", "unit_crud: mark test as unit CRUD operation test")
    config.addinivalue_line("markers", "unit_validation: mark test as unit validation test")
    config.addinivalue_line("markers", "unit_error: mark test as unit error handling test")


@pytest.fixture(scope="function", autouse=True)
def setup_unit_test(request):
    """
    单元测试的setup和teardown钩子

    自动为每个单元测试记录日志信息。
    """
    test_name = request.node.name
    logger.info("=" * 60)
    logger.info(f"开始单元测试: {test_name}")
    logger.info("=" * 60)

    yield

    logger.info("=" * 60)
    logger.info(f"完成单元测试: {test_name}")
    logger.info("=" * 60)


@pytest.fixture(scope="function")
def api_context():
    """
    API测试上下文

    提供一个字典用于在测试中存储和共享数据。

    Returns:
        dict: 测试上下文字典

    使用示例:
        def test_example(api_context):
            api_context["user_id"] = 123
            api_context["token"] = "test_token"
    """
    return {"user_token": None, "merchant_token": None, "admin_token": None, "test_data": {}}

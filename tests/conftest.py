"""
Pytest配置文件

集成所有测试数据fixtures，提供统一的测试环境配置。
"""

from typing import Generator

import allure
import pymysql
import pytest

from tests.config import config
from tests.fixtures.merchant_fixtures import *
from tests.fixtures.order_fixtures import *
from tests.fixtures.product_fixtures import *
from tests.fixtures.review_fixtures import *
from tests.fixtures.service_fixtures import *
from tests.fixtures.user_fixtures import *
from tests.utils import HTTPClient, TokenManager, log_test_case


@pytest.fixture(scope="session")
def base_url() -> str:
    return config.API_BASE_URL


@pytest.fixture(scope="session")
def http_client() -> Generator[HTTPClient, None, None]:
    client = HTTPClient()
    yield client
    client.close()


@pytest.fixture(scope="session")
def token_manager() -> TokenManager:
    return TokenManager()


@pytest.fixture(scope="session")
def user_credentials() -> dict:
    return config.TEST_USER


@pytest.fixture(scope="session")
def merchant_credentials() -> dict:
    return config.TEST_MERCHANT


@pytest.fixture(scope="session")
def admin_credentials() -> dict:
    return config.TEST_ADMIN


@pytest.fixture(scope="session")
def user_token(http_client: HTTPClient, user_credentials: dict) -> str:
    response = http_client.post(
        "/api/auth/login", json_data={"phone": user_credentials["phone"], "password": user_credentials["password"]}
    )

    if response.status_code == 200:
        token = response.json().get("data", {}).get("token")
        if token:
            return token

    pytest.skip("Unable to get user token")


@pytest.fixture(scope="session")
def merchant_token(http_client: HTTPClient, merchant_credentials: dict) -> str:
    response = http_client.post(
        "/api/auth/merchant/login",
        json_data={"phone": merchant_credentials["phone"], "password": merchant_credentials["password"]},
    )

    if response.status_code == 200:
        token = response.json().get("data", {}).get("token")
        if token:
            return token

    pytest.skip("Unable to get merchant token")


@pytest.fixture(scope="session")
def admin_token(http_client: HTTPClient, admin_credentials: dict) -> str:
    response = http_client.post(
        "/api/auth/admin/login",
        json_data={"username": admin_credentials["username"], "password": admin_credentials["password"]},
    )

    if response.status_code == 200:
        token = response.json().get("data", {}).get("token")
        if token:
            return token

    pytest.skip("Unable to get admin token")


@pytest.fixture(autouse=True)
def setup_test_case(request):
    test_name = request.node.name
    log_test_case(test_name)
    yield


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    outcome = yield
    report = outcome.get_result()

    if report.when == "call":
        if report.failed:
            try:
                allure.attach(str(report.longrepr), name="Failure Details", attachment_type=allure.attachment_type.TEXT)
            except Exception:
                pass


def pytest_configure(config):
    config.addinivalue_line("markers", "smoke: mark test as smoke test")
    config.addinivalue_line("markers", "regression: mark test as regression test")
    config.addinivalue_line("markers", "api: mark test as API test")
    config.addinivalue_line("markers", "performance: mark test as performance test")
    config.addinivalue_line("markers", "user: mark test as user-related test")
    config.addinivalue_line("markers", "merchant: mark test as merchant-related test")
    config.addinivalue_line("markers", "admin: mark test as admin-related test")
    config.addinivalue_line("markers", "security: mark test as security test")
    config.addinivalue_line("markers", "isolation: mark test as data isolation test")
    config.addinivalue_line("markers", "auth: mark test as authentication test")
    config.addinivalue_line("markers", "appointment: mark test as appointment/service order test")
    config.addinivalue_line("markers", "product_order: mark test as product order test")
    config.addinivalue_line("markers", "slow: mark test as slow running test")
    config.addinivalue_line("markers", "integration: mark test as integration test")
    config.addinivalue_line("markers", "unit: mark test as unit test")


@pytest.fixture(scope="function")
def api_context():
    return {"user_token": None, "merchant_token": None, "admin_token": None, "test_data": {}}


@pytest.fixture(scope="session")
def db_connection() -> Generator:
    """
    数据库连接fixture（session作用域）

    提供数据库连接对象，整个测试会话期间只创建一次连接。

    Yields:
        数据库连接对象

    使用示例:
        def test_database_query(db_connection):
            cursor = db_connection.cursor()
            cursor.execute("SELECT * FROM user LIMIT 1")
            result = cursor.fetchone()
            assert result is not None
    """
    connection = None
    try:
        connection = pymysql.connect(
            host=config.DATABASE_CONFIG["host"],
            port=config.DATABASE_CONFIG["port"],
            user=config.DATABASE_CONFIG["user"],
            password=config.DATABASE_CONFIG["password"],
            database=config.DATABASE_CONFIG["database"],
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor,
        )
        yield connection
    except Exception as e:
        pytest.fail(f"数据库连接失败: {str(e)}")
    finally:
        if connection:
            connection.close()


@pytest.fixture(scope="function")
def db_transaction(db_connection):
    """
    数据库事务fixture（function作用域）

    提供事务支持，测试结束后自动回滚，确保测试数据隔离。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        数据库连接对象

    使用示例:
        def test_with_transaction(db_transaction):
            # 在事务中执行操作
            cursor = db_transaction.cursor()
            cursor.execute("INSERT INTO user (username) VALUES ('test')")
            # 测试结束后自动回滚
    """
    try:
        db_connection.begin()
        yield db_connection
    finally:
        db_connection.rollback()


@pytest.fixture(autouse=True, scope="function")
def auto_cleanup_test_data(request, db_connection):
    """
    自动清理测试数据fixture（function作用域，自动执行）

    自动检测测试函数中创建的测试数据，并在测试结束后清理。
    此fixture会自动应用到所有测试函数。

    Args:
        request: pytest请求对象
        db_connection: 数据库连接fixture

    Yields:
        None
    """
    test_data_ids = {
        "user_ids": [],
        "merchant_ids": [],
        "service_ids": [],
        "product_ids": [],
        "appointment_ids": [],
        "order_ids": [],
        "review_ids": [],
    }

    yield test_data_ids

    if db_connection:
        from tests.testdata.data_cleanup import (
            cleanup_appointments,
            cleanup_merchants,
            cleanup_orders,
            cleanup_products,
            cleanup_reviews,
            cleanup_services,
            cleanup_users,
        )

        try:
            if test_data_ids["review_ids"]:
                cleanup_reviews(test_data_ids["review_ids"], db_connection)

            if test_data_ids["order_ids"]:
                cleanup_orders(test_data_ids["order_ids"], db_connection)

            if test_data_ids["appointment_ids"]:
                cleanup_appointments(test_data_ids["appointment_ids"], db_connection)

            if test_data_ids["product_ids"]:
                cleanup_products(test_data_ids["product_ids"], db_connection)

            if test_data_ids["service_ids"]:
                cleanup_services(test_data_ids["service_ids"], db_connection)

            if test_data_ids["merchant_ids"]:
                cleanup_merchants(test_data_ids["merchant_ids"], db_connection)

            if test_data_ids["user_ids"]:
                cleanup_users(test_data_ids["user_ids"], db_connection)
        except Exception as e:
            print(f"清理测试数据时出错: {str(e)}")

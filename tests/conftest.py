"""
Pytest配置文件
"""
import pytest
import allure
from typing import Generator

from tests.config import config
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
        "/api/auth/login",
        json_data={
            "phone": user_credentials["phone"],
            "password": user_credentials["password"]
        }
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
        json_data={
            "phone": merchant_credentials["phone"],
            "password": merchant_credentials["password"]
        }
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
        json_data={
            "username": admin_credentials["username"],
            "password": admin_credentials["password"]
        }
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
                allure.attach(
                    str(report.longrepr),
                    name="Failure Details",
                    attachment_type=allure.attachment_type.TEXT
                )
            except Exception:
                pass


def pytest_configure(config):
    config.addinivalue_line(
        "markers", "smoke: mark test as smoke test"
    )
    config.addinivalue_line(
        "markers", "regression: mark test as regression test"
    )
    config.addinivalue_line(
        "markers", "api: mark test as API test"
    )
    config.addinivalue_line(
        "markers", "performance: mark test as performance test"
    )
    config.addinivalue_line(
        "markers", "user: mark test as user-related test"
    )
    config.addinivalue_line(
        "markers", "merchant: mark test as merchant-related test"
    )
    config.addinivalue_line(
        "markers", "admin: mark test as admin-related test"
    )
    config.addinivalue_line(
        "markers", "security: mark test as security test"
    )
    config.addinivalue_line(
        "markers", "isolation: mark test as data isolation test"
    )
    config.addinivalue_line(
        "markers", "auth: mark test as authentication test"
    )
    config.addinivalue_line(
        "markers", "appointment: mark test as appointment/service order test"
    )
    config.addinivalue_line(
        "markers", "product_order: mark test as product order test"
    )
    config.addinivalue_line(
        "markers", "slow: mark test as slow running test"
    )
    config.addinivalue_line(
        "markers", "integration: mark test as integration test"
    )
    config.addinivalue_line(
        "markers", "unit: mark test as unit test"
    )


@pytest.fixture(scope="function")
def api_context():
    return {
        "user_token": None,
        "merchant_token": None,
        "admin_token": None,
        "test_data": {}
    }

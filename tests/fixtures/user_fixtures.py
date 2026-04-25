"""
用户相关测试数据Fixtures

提供用户测试数据的创建、管理和清理功能。
"""

from typing import Any, Dict, List

import pytest

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import cleanup_users
from tests.testdata.data_manager import TestDataManager


@pytest.fixture(scope="function")
def test_user() -> Dict[str, Any]:
    """
    创建测试用户（function作用域）

    每个测试函数都会创建一个新的测试用户。

    Returns:
        Dict[str, Any]: 用户数据字典

    使用示例:
        def test_user_login(test_user):
            assert test_user['username'] is not None
            assert test_user['email'] is not None
    """
    builder = TestDataBuilder()
    user_data = builder.build_user()
    return user_data


@pytest.fixture(scope="session")
def test_user_session() -> Dict[str, Any]:
    """
    创建用户会话（session作用域）

    整个测试会话期间只创建一次，适用于需要长期存在的用户数据。

    Returns:
        Dict[str, Any]: 用户数据字典

    使用示例:
        def test_multiple_scenarios(test_user_session):
            # 所有测试共享同一个用户数据
            assert test_user_session['id'] is not None
    """
    builder = TestDataBuilder()
    user_data = builder.build_user(username="session_test_user", email="session_user@test.com")
    return user_data


@pytest.fixture(scope="function")
def test_user_token(http_client, test_user) -> str:
    """
    获取用户Token（function作用域）

    创建测试用户并获取认证Token。

    Args:
        http_client: HTTP客户端fixture
        test_user: 测试用户fixture

    Returns:
        str: 用户认证Token

    使用示例:
        def test_authenticated_request(test_user_token):
            headers = {"Authorization": f"Bearer {test_user_token}"}
            # 使用Token发送请求
    """
    response = http_client.post(
        "/api/auth/login", json_data={"phone": test_user["phone"], "password": test_user["password"]}
    )

    if response.status_code == 200:
        token = response.json().get("data", {}).get("token")
        if token:
            return token

    pytest.skip("无法获取用户Token")


@pytest.fixture(scope="function")
def test_users() -> List[Dict[str, Any]]:
    """
    创建多个测试用户（function作用域）

    创建一组测试用户，用于需要多个用户的测试场景。

    Returns:
        List[Dict[str, Any]]: 用户数据列表

    使用示例:
        def test_user_list(test_users):
            assert len(test_users) >= 3
            for user in test_users:
                assert user['username'] is not None
    """
    builder = TestDataBuilder()
    users = []

    users.append(builder.build_user(username="user_1", email="user1@test.com"))
    users.append(builder.build_user(username="user_2", email="user2@test.com"))
    users.append(builder.build_user(username="user_3", email="user3@test.com"))

    return users


@pytest.fixture(scope="function")
def clean_users(db_connection):
    """
    清理测试用户数据（function作用域）

    测试结束后自动清理创建的用户数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_user_cleanup(clean_users):
            # 创建测试用户
            # 测试结束后自动清理
    """
    user_ids = []

    yield user_ids

    if user_ids and db_connection:
        cleanup_users(user_ids, db_connection)


@pytest.fixture(scope="function")
def test_user_with_cleanup(db_connection, test_user) -> Dict[str, Any]:
    """
    创建测试用户并自动清理（function作用域）

    创建测试用户并在测试结束后自动清理数据库中的用户记录。

    Args:
        db_connection: 数据库连接fixture
        test_user: 测试用户fixture

    Returns:
        Dict[str, Any]: 用户数据字典
    """
    yield test_user

    if db_connection and "id" in test_user:
        cleanup_users([test_user["id"]], db_connection)


@pytest.fixture(scope="function")
def test_user_data_manager() -> TestDataManager:
    """
    获取用户数据管理器（function作用域）

    提供TestDataManager实例，用于更复杂的用户数据管理场景。

    Returns:
        TestDataManager: 数据管理器实例

    使用示例:
        def test_complex_user_scenario(test_user_data_manager):
            user = test_user_data_manager.prepare_test_data('user')
            # 进行复杂操作
            test_user_data_manager.cleanup_all_test_data()
    """
    manager = TestDataManager()
    return manager


@pytest.fixture(scope="function")
def test_user_builder() -> TestDataBuilder:
    """
    获取用户数据构建器（function作用域）

    提供TestDataBuilder实例，用于自定义用户数据构建。

    Returns:
        TestDataBuilder: 数据构建器实例

    使用示例:
        def test_custom_user(test_user_builder):
            user = test_user_builder.build_user(
                username="custom_user",
                email="custom@test.com",
                phone="13900000000"
            )
            assert user['username'] == "custom_user"
    """
    return TestDataBuilder()


@pytest.fixture(scope="function")
def test_user_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试用户（function作用域）

    使用TestDataBuilder的批量构建功能创建多个用户。

    Returns:
        List[Dict[str, Any]]: 用户数据列表

    使用示例:
        def test_batch_users(test_user_batch):
            assert len(test_user_batch) == 10
    """
    builder = TestDataBuilder()
    users = builder.build_batch("user", 10, status="active")
    return users


@pytest.fixture(scope="function")
def test_inactive_user() -> Dict[str, Any]:
    """
    创建非活跃状态测试用户（function作用域）

    创建一个状态为inactive的测试用户，用于测试用户状态相关功能。

    Returns:
        Dict[str, Any]: 非活跃用户数据字典
    """
    builder = TestDataBuilder()
    user_data = builder.build_user(status="inactive")
    return user_data


@pytest.fixture(scope="function")
def test_admin_user() -> Dict[str, Any]:
    """
    创建管理员测试用户（function作用域）

    创建一个具有管理员权限的测试用户。

    Returns:
        Dict[str, Any]: 管理员用户数据字典
    """
    builder = TestDataBuilder()
    user_data = builder.build_user(username="admin_test", email="admin@test.com", role="admin")
    return user_data

"""
单元测试基类

提供所有API测试的基类，包含通用的测试方法、断言方法和辅助方法。
"""

import logging
import time
from decimal import Decimal
from typing import Any, Callable, Dict, Optional

import requests

from tests.assertions import AssertionBuilder
from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import DataCleanup
from tests.utils import HTTPClient

logger = logging.getLogger(__name__)


class BaseAPITest:
    """
    所有API测试的基类

    提供通用的测试方法、断言方法和辅助方法，简化测试代码编写。

    使用示例:
        class TestUserAPI(BaseAPITest):
            def test_get_users(self, unit_http_client):
                response = unit_http_client.get("/api/users")
                self.assert_response_success(response)
                self.assert_status_code(response, 200)
    """

    def __init__(self):
        self.data_builder = TestDataBuilder()
        self.data_cleanup = DataCleanup()
        self._created_resources = {
            "users": [],
            "merchants": [],
            "services": [],
            "products": [],
            "appointments": [],
            "orders": [],
        }

    def assert_response_success(self, response: requests.Response) -> None:
        """
        断言响应成功

        验证响应状态码为200或201，且响应体包含success=true或code=200

        Args:
            response: HTTP响应对象

        Raises:
            AssertionError: 如果响应不成功

        使用示例:
            response = http_client.get("/api/users")
            self.assert_response_success(response)
        """
        assert response.status_code in [
            200,
            201,
        ], f"Expected success status code, but got {response.status_code}. Response: {response.text}"

        try:
            json_data = response.json()
            if "success" in json_data:
                assert json_data["success"] is True, f"Expected success=true, but got {json_data['success']}"
            elif "code" in json_data:
                assert json_data["code"] in [200, 0], f"Expected code=200 or 0, but got {json_data['code']}"
        except Exception:
            pass

    def assert_response_error(self, response: requests.Response, expected_code: int) -> None:
        """
        断言响应错误

        验证响应状态码和错误码符合预期

        Args:
            response: HTTP响应对象
            expected_code: 期望的错误码

        Raises:
            AssertionError: 如果错误码不匹配

        使用示例:
            response = http_client.get("/api/users/999")
            self.assert_response_error(response, 404)
        """
        assert (
            response.status_code == expected_code
        ), f"Expected error code {expected_code}, but got {response.status_code}. Response: {response.text}"

    def assert_response_data(self, response: requests.Response, expected_data: Dict) -> None:
        """
        断言响应数据

        验证响应体中的data字段包含预期的数据

        Args:
            response: HTTP响应对象
            expected_data: 期望的数据字典

        Raises:
            AssertionError: 如果数据不匹配

        使用示例:
            response = http_client.get("/api/users/1")
            self.assert_response_data(response, {"id": 1, "username": "test"})
        """
        json_data = response.json()
        assert "data" in json_data, "Response does not contain 'data' field"

        actual_data = json_data["data"]
        for key, value in expected_data.items():
            assert key in actual_data, f"Key '{key}' not found in response data"
            assert actual_data[key] == value, f"Expected '{key}' to be {value}, but got {actual_data[key]}"

    def assert_status_code(self, response: requests.Response, expected_code: int) -> None:
        """
        断言状态码

        验证HTTP状态码符合预期

        Args:
            response: HTTP响应对象
            expected_code: 期望的状态码

        Raises:
            AssertionError: 如果状态码不匹配

        使用示例:
            response = http_client.get("/api/users")
            self.assert_status_code(response, 200)
        """
        AssertionBuilder(response).assert_status_code(expected_code)

    def assert_response_time(self, response: requests.Response, max_time: float) -> None:
        """
        断言响应时间

        验证响应时间不超过最大值

        Args:
            response: HTTP响应对象
            max_time: 最大响应时间（秒）

        Raises:
            AssertionError: 如果响应时间超过最大值

        使用示例:
            response = http_client.get("/api/users")
            self.assert_response_time(response, 2.0)
        """
        actual_time = response.elapsed.total_seconds()
        assert actual_time <= max_time, f"Response time {actual_time}s exceeded maximum {max_time}s"

    def assert_json_schema(self, response: requests.Response, schema: Dict) -> None:
        """
        断言JSON Schema

        验证响应体符合指定的JSON Schema

        Args:
            response: HTTP响应对象
            schema: JSON Schema定义

        Raises:
            AssertionError: 如果不符合Schema

        使用示例:
            schema = {
                "type": "object",
                "properties": {
                    "id": {"type": "integer"},
                    "username": {"type": "string"}
                },
                "required": ["id", "username"]
            }
            response = http_client.get("/api/users/1")
            self.assert_json_schema(response, schema)
        """
        AssertionBuilder(response).assert_json_schema(schema)

    def create_test_user(self, http_client: HTTPClient, **kwargs) -> Dict[str, Any]:
        """
        创建测试用户

        创建一个测试用户并返回用户数据和token

        Args:
            http_client: HTTP客户端
            **kwargs: 自定义用户字段

        Returns:
            Dict[str, Any]: 包含用户数据和token的字典

        使用示例:
            user = self.create_test_user(http_client, username="testuser")
            assert user["token"] is not None
        """
        user_data = self.data_builder.build_user(**kwargs)

        response = http_client.post("/api/auth/register", json_data=user_data)

        if response.status_code not in [200, 201]:
            raise Exception(f"Failed to create test user: {response.text}")

        created_user = response.json().get("data", {})
        user_id = created_user.get("id")

        login_response = http_client.post(
            "/api/auth/login", json_data={"phone": user_data["phone"], "password": user_data["password"]}
        )

        token = None
        if login_response.status_code == 200:
            token = login_response.json().get("data", {}).get("token")

        if user_id:
            self._created_resources["users"].append(user_id)

        return {
            "user": created_user,
            "token": token,
            "credentials": {"phone": user_data["phone"], "password": user_data["password"]},
        }

    def create_test_merchant(self, http_client: HTTPClient, **kwargs) -> Dict[str, Any]:
        """
        创建测试商家

        创建一个测试商家并返回商家数据和token

        Args:
            http_client: HTTP客户端
            **kwargs: 自定义商家字段

        Returns:
            Dict[str, Any]: 包含商家数据和token的字典

        使用示例:
            merchant = self.create_test_merchant(http_client, name="Test Shop")
            assert merchant["token"] is not None
        """
        merchant_data = self.data_builder.build_merchant(**kwargs)

        response = http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        if response.status_code not in [200, 201]:
            raise Exception(f"Failed to create test merchant: {response.text}")

        created_merchant = response.json().get("data", {})
        merchant_id = created_merchant.get("id")

        login_response = http_client.post(
            "/api/auth/merchant/login",
            json_data={"phone": merchant_data["phone"], "password": merchant_data["password"]},
        )

        token = None
        if login_response.status_code == 200:
            token = login_response.json().get("data", {}).get("token")

        if merchant_id:
            self._created_resources["merchants"].append(merchant_id)

        return {
            "merchant": created_merchant,
            "token": token,
            "credentials": {"phone": merchant_data["phone"], "password": merchant_data["password"]},
        }

    def create_test_service(
        self, http_client: HTTPClient, merchant_token: str, merchant_id: int, **kwargs
    ) -> Dict[str, Any]:
        """
        创建测试服务

        创建一个测试服务并返回服务数据

        Args:
            http_client: HTTP客户端
            merchant_token: 商家token
            merchant_id: 商家ID
            **kwargs: 自定义服务字段

        Returns:
            Dict[str, Any]: 创建的服务数据

        使用示例:
            service = self.create_test_service(
                http_client,
                merchant_token,
                merchant_id,
                name="Test Service"
            )
            assert service["id"] is not None
        """
        service_data = self.data_builder.build_service(merchant_id=merchant_id, **kwargs)

        response = http_client.post(
            "/api/merchant/services", json_data=service_data, headers={"Authorization": f"Bearer {merchant_token}"}
        )

        if response.status_code not in [200, 201]:
            raise Exception(f"Failed to create test service: {response.text}")

        created_service = response.json().get("data", {})
        service_id = created_service.get("id")

        if service_id:
            self._created_resources["services"].append(service_id)

        return created_service

    def create_test_product(
        self, http_client: HTTPClient, merchant_token: str, merchant_id: int, **kwargs
    ) -> Dict[str, Any]:
        """
        创建测试商品

        创建一个测试商品并返回商品数据

        Args:
            http_client: HTTP客户端
            merchant_token: 商家token
            merchant_id: 商家ID
            **kwargs: 自定义商品字段

        Returns:
            Dict[str, Any]: 创建的商品数据

        使用示例:
            product = self.create_test_product(
                http_client,
                merchant_token,
                merchant_id,
                name="Test Product"
            )
            assert product["id"] is not None
        """
        product_data = self.data_builder.build_product(merchant_id=merchant_id, **kwargs)

        response = http_client.post(
            "/api/merchant/products", json_data=product_data, headers={"Authorization": f"Bearer {merchant_token}"}
        )

        if response.status_code not in [200, 201]:
            raise Exception(f"Failed to create test product: {response.text}")

        created_product = response.json().get("data", {})
        product_id = created_product.get("id")

        if product_id:
            self._created_resources["products"].append(product_id)

        return created_product

    def cleanup_test_data(self) -> None:
        """
        清理测试数据

        清理所有在测试中创建的资源

        使用示例:
            def teardown_method(self):
                self.cleanup_test_data()
        """
        logger.info("开始清理测试数据")

        for resource_type, resource_ids in self._created_resources.items():
            if resource_ids:
                logger.info(f"清理{resource_type}: {resource_ids}")
                try:
                    self.data_cleanup.cleanup_by_ids(resource_type, resource_ids)
                except Exception as e:
                    logger.error(f"清理{resource_type}失败: {e}")

        self._created_resources = {
            "users": [],
            "merchants": [],
            "services": [],
            "products": [],
            "appointments": [],
            "orders": [],
        }

        logger.info("测试数据清理完成")

    def get_auth_headers(self, token: str) -> Dict[str, str]:
        """
        获取认证头

        生成包含Authorization的HTTP头

        Args:
            token: 认证token

        Returns:
            Dict[str, str]: 包含Authorization的HTTP头字典

        使用示例:
            headers = self.get_auth_headers(user_token)
            response = http_client.get("/api/user/profile", headers=headers)
        """
        return {"Authorization": f"Bearer {token}", "Content-Type": "application/json", "Accept": "application/json"}

    def wait_for_condition(self, condition: Callable[[], bool], timeout: float = 10.0, interval: float = 0.5) -> bool:
        """
        等待条件满足

        等待指定条件返回True，超时后返回False

        Args:
            condition: 条件函数，返回bool
            timeout: 超时时间（秒）
            interval: 检查间隔（秒）

        Returns:
            bool: 条件是否在超时前满足

        使用示例:
            def check_order_status():
                response = http_client.get(f"/api/orders/{order_id}")
                return response.json()["data"]["status"] == "completed"

            success = self.wait_for_condition(check_order_status, timeout=30)
            assert success, "Order not completed in time"
        """
        start_time = time.time()

        while time.time() - start_time < timeout:
            if condition():
                return True
            time.sleep(interval)

        return False

    def log_response(self, response: requests.Response) -> None:
        """
        记录响应详情

        记录HTTP响应的详细信息，包括状态码、响应时间和响应体

        Args:
            response: HTTP响应对象

        使用示例:
            response = http_client.get("/api/users")
            self.log_response(response)
        """
        logger.info(f"状态码: {response.status_code}")
        logger.info(f"响应时间: {response.elapsed.total_seconds()}s")
        logger.info(f"响应头: {dict(response.headers)}")
        try:
            logger.info(f"响应体: {response.json()}")
        except Exception:
            logger.info(f"响应文本: {response.text}")

    def assert_pagination(
        self,
        response: requests.Response,
        expected_page: int = None,
        expected_size: int = None,
        expected_total: int = None,
    ) -> None:
        """
        断言分页数据

        验证响应中的分页信息

        Args:
            response: HTTP响应对象
            expected_page: 期望的页码（可选）
            expected_size: 期望的每页大小（可选）
            expected_total: 期望的总记录数（可选）

        Raises:
            AssertionError: 如果分页信息不匹配

        使用示例:
            response = http_client.get("/api/users?page=1&size=10")
            self.assert_pagination(response, expected_page=1, expected_size=10)
        """
        json_data = response.json()
        data = json_data.get("data", {})

        if "page" in data and expected_page is not None:
            assert data["page"] == expected_page, f"Expected page {expected_page}, but got {data['page']}"

        if "size" in data and expected_size is not None:
            assert data["size"] == expected_size, f"Expected size {expected_size}, but got {data['size']}"

        if "total" in data and expected_total is not None:
            assert data["total"] == expected_total, f"Expected total {expected_total}, but got {data['total']}"

    def assert_list_not_empty(self, response: requests.Response) -> None:
        """
        断言列表非空

        验证响应中的列表数据不为空

        Args:
            response: HTTP响应对象

        Raises:
            AssertionError: 如果列表为空

        使用示例:
            response = http_client.get("/api/users")
            self.assert_list_not_empty(response)
        """
        json_data = response.json()
        data = json_data.get("data", {})

        if isinstance(data, list):
            assert len(data) > 0, "Response list is empty"
        elif "items" in data:
            assert len(data["items"]) > 0, "Response items list is empty"
        elif "list" in data:
            assert len(data["list"]) > 0, "Response list is empty"
        else:
            raise AssertionError("Cannot find list data in response")

    def assert_field_type(self, response: requests.Response, field_name: str, expected_type: type) -> None:
        """
        断言字段类型

        验证响应中指定字段的数据类型

        Args:
            response: HTTP响应对象
            field_name: 字段名
            expected_type: 期望的类型

        Raises:
            AssertionError: 如果类型不匹配

        使用示例:
            response = http_client.get("/api/users/1")
            self.assert_field_type(response, "id", int)
            self.assert_field_type(response, "username", str)
        """
        AssertionBuilder(response).assert_field_type(field_name, expected_type)

    def assert_required_fields(self, response: requests.Response, required_fields: list) -> None:
        """
        断言必需字段存在

        验证响应中包含所有必需字段

        Args:
            response: HTTP响应对象
            required_fields: 必需字段列表

        Raises:
            AssertionError: 如果缺少必需字段

        使用示例:
            response = http_client.get("/api/users/1")
            self.assert_required_fields(response, ["id", "username", "email"])
        """
        AssertionBuilder(response).assert_required_fields(required_fields)

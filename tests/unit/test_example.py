"""
单元测试示例

演示如何使用单元测试基础结构编写测试用例
"""

import pytest

from tests.unit import BaseAPITest


@pytest.mark.unit_api
@pytest.mark.unit_user
class TestUserAPI(BaseAPITest):
    """用户API单元测试示例"""

    def test_user_registration(self, unit_http_client, test_data_builder):
        """
        测试用户注册

        演示：
        1. 使用test_data_builder构建测试数据
        2. 使用unit_http_client发送请求
        3. 使用断言方法验证响应
        """
        user_data = test_data_builder.build_user(username="test_user_example", email="test@example.com")

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_response_success(response)
        self.assert_status_code(response, 201)
        self.assert_required_fields(response, ["id", "username", "email"])

    def test_user_login(self, unit_http_client, test_user):
        """
        测试用户登录

        演示：
        1. 使用test_user fixture获取已创建的用户
        2. 使用用户凭证登录
        3. 验证token返回
        """
        assert test_user["token"] is not None, "Login should return a token"

        response = unit_http_client.get("/api/user/profile", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_response_data(response, {"phone": test_user["credentials"]["phone"]})

    def test_get_user_list(self, unit_http_client):
        """
        测试获取用户列表

        演示：
        1. 测试分页功能
        2. 验证响应时间
        """
        response = unit_http_client.get("/api/users", params={"page": 1, "size": 10})

        self.assert_response_success(response)
        self.assert_response_time(response, 2.0)
        self.assert_pagination(response, expected_page=1, expected_size=10)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
class TestMerchantAPI(BaseAPITest):
    """商家API单元测试示例"""

    def test_merchant_registration(self, unit_http_client, test_data_builder):
        """测试商家注册"""
        merchant_data = test_data_builder.build_merchant(name="测试商家示例", contact_person="测试联系人")

        response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        self.assert_response_success(response)
        self.assert_status_code(response, 201)

    def test_merchant_login(self, unit_http_client, test_merchant):
        """测试商家登录"""
        assert test_merchant["token"] is not None, "Merchant login should return a token"

        response = unit_http_client.get("/api/merchant/profile", headers=self.get_auth_headers(test_merchant["token"]))

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_service
class TestServiceAPI(BaseAPITest):
    """服务API单元测试示例"""

    def test_create_service(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建服务"""
        merchant_id = test_merchant["merchant"].get("id")
        service_data = test_data_builder.build_service(merchant_id=merchant_id, name="测试服务", price=99.99)

        response = unit_http_client.post(
            "/api/merchant/services", json_data=service_data, headers=self.get_auth_headers(test_merchant["token"])
        )

        self.assert_response_success(response)
        self.assert_field_type(response, "id", int)
        self.assert_field_type(response, "price", (int, float))

    def test_get_service_list(self, unit_http_client, test_service):
        """测试获取服务列表"""
        response = unit_http_client.get("/api/services")

        self.assert_response_success(response)
        self.assert_list_not_empty(response)

    def test_get_service_detail(self, unit_http_client, test_service):
        """测试获取服务详情"""
        service_id = test_service.get("id")

        response = unit_http_client.get(f"/api/services/{service_id}")

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "price", "merchant_id"])


@pytest.mark.unit_api
@pytest.mark.unit_product
class TestProductAPI(BaseAPITest):
    """商品API单元测试示例"""

    def test_create_product(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建商品"""
        merchant_id = test_merchant["merchant"].get("id")
        product_data = test_data_builder.build_product(merchant_id=merchant_id, name="测试商品", price=49.99, stock=100)

        response = unit_http_client.post(
            "/api/merchant/products", json_data=product_data, headers=self.get_auth_headers(test_merchant["token"])
        )

        self.assert_response_success(response)

    def test_get_product_list(self, unit_http_client, test_product):
        """测试获取商品列表"""
        response = unit_http_client.get("/api/products")

        self.assert_response_success(response)
        self.assert_response_time(response, 2.0)

    def test_update_product_stock(self, unit_http_client, test_merchant, test_product):
        """测试更新商品库存"""
        product_id = test_product.get("id")
        update_data = {"stock": 50}

        response = unit_http_client.put(
            f"/api/merchant/products/{product_id}",
            json_data=update_data,
            headers=self.get_auth_headers(test_merchant["token"]),
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"stock": 50})


@pytest.mark.unit_api
@pytest.mark.unit_auth
class TestAuthenticationAPI(BaseAPITest):
    """认证API单元测试示例"""

    def test_invalid_login(self, unit_http_client, test_data_builder):
        """测试无效登录"""
        invalid_credentials = {"phone": "invalid_phone", "password": "wrong_password"}

        response = unit_http_client.post("/api/auth/login", json_data=invalid_credentials)

        self.assert_response_error(response, 401)

    def test_access_without_token(self, unit_http_client):
        """测试无token访问受保护资源"""
        response = unit_http_client.get("/api/user/profile")

        self.assert_response_error(response, 401)

    def test_access_with_invalid_token(self, unit_http_client):
        """测试无效token访问"""
        headers = self.get_auth_headers("invalid_token_12345")

        response = unit_http_client.get("/api/user/profile", headers=headers)

        self.assert_response_error(response, 401)


@pytest.mark.unit_api
@pytest.mark.unit_validation
class TestValidationAPI(BaseAPITest):
    """数据验证单元测试示例"""

    def test_register_with_invalid_email(self, unit_http_client, test_data_builder):
        """测试使用无效邮箱注册"""
        user_data = test_data_builder.build_user(email="invalid_email")

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_response_error(response, 400)

    def test_register_with_invalid_phone(self, unit_http_client, test_data_builder):
        """测试使用无效手机号注册"""
        user_data = test_data_builder.build_user(phone="invalid_phone")

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_response_error(response, 400)

    def test_create_service_with_negative_price(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建价格为负数的服务"""
        merchant_id = test_merchant["merchant"].get("id")
        service_data = test_data_builder.build_service(merchant_id=merchant_id, price=-100)

        response = unit_http_client.post(
            "/api/merchant/services", json_data=service_data, headers=self.get_auth_headers(test_merchant["token"])
        )

        self.assert_response_error(response, 400)


@pytest.mark.unit_api
@pytest.mark.unit_crud
class TestCRUDOperations(BaseAPITest):
    """CRUD操作单元测试示例"""

    def test_create_read_update_delete_user(self, unit_http_client, test_data_builder):
        """
        测试完整的CRUD流程

        演示：
        1. Create - 创建用户
        2. Read - 读取用户
        3. Update - 更新用户
        4. Delete - 删除用户
        """
        user_data = test_data_builder.build_user()

        create_response = unit_http_client.post("/api/auth/register", json_data=user_data)
        self.assert_response_success(create_response)
        user_id = create_response.json()["data"]["id"]

        login_response = unit_http_client.post(
            "/api/auth/login", json_data={"phone": user_data["phone"], "password": user_data["password"]}
        )
        token = login_response.json()["data"]["token"]

        read_response = unit_http_client.get(f"/api/users/{user_id}", headers=self.get_auth_headers(token))
        self.assert_response_success(read_response)

        update_data = {"username": "updated_username"}
        update_response = unit_http_client.put(
            f"/api/users/{user_id}", json_data=update_data, headers=self.get_auth_headers(token)
        )
        self.assert_response_success(update_response)

        delete_response = unit_http_client.delete(f"/api/users/{user_id}", headers=self.get_auth_headers(token))
        self.assert_status_code(delete_response, 204)

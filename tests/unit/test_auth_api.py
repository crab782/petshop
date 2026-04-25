"""
认证API单元测试

测试认证相关的所有API接口，包括：
- 用户登录/注册/登出
- 商家登录/注册
- 管理员登录/注册
- 用户信息管理
- 密码管理
- 验证码和密码重置

使用BaseAPITest基类和断言工具进行测试验证。
"""

from typing import Any, Dict

import allure
import pytest

from tests.assertions import AssertionBuilder
from tests.unit.base_test import BaseAPITest


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestUserLoginAPI(BaseAPITest):
    """用户登录接口测试"""

    @allure.story("用户登录")
    @allure.title("测试用户登录成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.parametrize("login_type", ["phone", "email"])
    def test_user_login_success(self, unit_http_client, test_user, login_type):
        """
        测试用户登录成功

        验证点：
        1. 返回状态码200
        2. 响应包含token
        3. token不为空且为字符串类型
        4. 响应包含用户基本信息
        """
        if login_type == "phone":
            login_identifier = test_user["credentials"]["phone"]
        else:
            login_identifier = test_user["user"].get("email", test_user["credentials"]["phone"])

        response = unit_http_client.post(
            "/api/auth/login",
            json_data={"loginIdentifier": login_identifier, "password": test_user["credentials"]["password"]},
        )

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        data = json_data["data"]
        assert "token" in data, "响应缺少token字段"
        assert data["token"], "token为空"
        assert isinstance(data["token"], str), "token类型错误"

        self.log_response(response)

    @allure.story("用户登录")
    @allure.title("测试用户登录失败 - 错误密码")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("wrong_password", ["wrongpassword", "WrongPassword123", "123456", "", "   "])
    def test_user_login_wrong_password(self, unit_http_client, test_user, wrong_password):
        """
        测试用户登录失败 - 错误密码

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/login",
            json_data={"loginIdentifier": test_user["credentials"]["phone"], "password": wrong_password},
        )

        self.assert_status_code(response, 401)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("用户登录")
    @allure.title("测试用户登录失败 - 用户不存在")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("identifier", ["nonexistent_user_12345", "99999999999", "nonexistent@test12345.com"])
    def test_user_login_nonexistent_user(self, unit_http_client, identifier):
        """
        测试用户登录失败 - 用户不存在

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/login", json_data={"loginIdentifier": identifier, "password": "somepassword123"}
        )

        self.assert_status_code(response, 401)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("用户登录")
    @allure.title("测试用户登录失败 - 参数缺失")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "login_data,missing_field",
        [
            ({"password": "password123"}, "loginIdentifier"),
            ({"loginIdentifier": "13800138000"}, "password"),
            ({}, "all"),
        ],
    )
    def test_user_login_missing_parameters(self, unit_http_client, login_data, missing_field):
        """
        测试用户登录失败 - 参数缺失

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        response = unit_http_client.post("/api/auth/login", json_data=login_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("用户登录")
    @allure.title("测试用户登录失败 - 无效手机号格式")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("invalid_phone", ["123", "abc123", "1234567890123456", "not_a_phone"])
    def test_user_login_invalid_phone_format(self, unit_http_client, invalid_phone):
        """
        测试用户登录失败 - 无效手机号格式

        验证点：
        1. 返回状态码400或401
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/login", json_data={"loginIdentifier": invalid_phone, "password": "password123"}
        )

        assert response.status_code in [400, 401], f"预期状态码400或401，实际为{response.status_code}"

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestUserRegisterAPI(BaseAPITest):
    """用户注册接口测试"""

    @allure.story("用户注册")
    @allure.title("测试用户注册成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_user_register_success(self, unit_http_client, test_data_builder):
        """
        测试用户注册成功

        验证点：
        1. 返回状态码201
        2. 响应包含token
        3. 响应包含用户信息
        """
        user_data = test_data_builder.build_user()

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_status_code(response, 201)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        data = json_data["data"]
        assert "token" in data, "响应缺少token字段"
        assert data["token"], "token为空"

        self.log_response(response)

    @allure.story("用户注册")
    @allure.title("测试用户注册失败 - 用户名已存在")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_register_duplicate_username(self, unit_http_client, test_user, test_data_builder):
        """
        测试用户注册失败 - 用户名已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        user_data = test_data_builder.build_user(username=test_user["user"].get("username"))

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("用户注册")
    @allure.title("测试用户注册失败 - 邮箱已存在")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_register_duplicate_email(self, unit_http_client, test_user, test_data_builder):
        """
        测试用户注册失败 - 邮箱已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        user_data = test_data_builder.build_user(email=test_user["user"].get("email"))

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("用户注册")
    @allure.title("测试用户注册失败 - 手机号已存在")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_register_duplicate_phone(self, unit_http_client, test_user, test_data_builder):
        """
        测试用户注册失败 - 手机号已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        user_data = test_data_builder.build_user(phone=test_user["credentials"]["phone"])

        response = unit_http_client.post("/api/auth/register", json_data=user_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("用户注册")
    @allure.title("测试用户注册失败 - 参数验证失败")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "invalid_data,description",
        [
            ({"phone": "13800138000"}, "缺少密码"),
            ({"phone": "13800138000", "password": "123"}, "密码太短"),
            ({"phone": "", "password": "password123"}, "手机号为空"),
            ({"phone": "invalid", "password": "password123"}, "手机号格式错误"),
            ({"email": "invalid_email", "password": "password123"}, "邮箱格式错误"),
        ],
    )
    def test_user_register_validation_failure(self, unit_http_client, invalid_data, description):
        """
        测试用户注册失败 - 参数验证失败

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        response = unit_http_client.post("/api/auth/register", json_data=invalid_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, f"响应缺少错误信息 - {description}"

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestMerchantLoginAPI(BaseAPITest):
    """商家登录接口测试"""

    @allure.story("商家登录")
    @allure.title("测试商家登录成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_login_success(self, unit_http_client, test_merchant):
        """
        测试商家登录成功

        验证点：
        1. 返回状态码200
        2. 响应包含token
        3. token不为空且为字符串类型
        """
        response = unit_http_client.post(
            "/api/auth/merchant/login",
            json_data={
                "loginIdentifier": test_merchant["credentials"]["phone"],
                "password": test_merchant["credentials"]["password"],
            },
        )

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        data = json_data["data"]
        assert "token" in data, "响应缺少token字段"
        assert data["token"], "token为空"
        assert isinstance(data["token"], str), "token类型错误"

        self.log_response(response)

    @allure.story("商家登录")
    @allure.title("测试商家登录失败 - 错误密码")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_login_wrong_password(self, unit_http_client, test_merchant):
        """
        测试商家登录失败 - 错误密码

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/merchant/login",
            json_data={"loginIdentifier": test_merchant["credentials"]["phone"], "password": "wrongpassword123"},
        )

        self.assert_status_code(response, 401)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("商家登录")
    @allure.title("测试商家登录失败 - 商家不存在")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_login_nonexistent(self, unit_http_client):
        """
        测试商家登录失败 - 商家不存在

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/merchant/login",
            json_data={"loginIdentifier": "nonexistent_merchant_12345", "password": "somepassword123"},
        )

        self.assert_status_code(response, 401)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("商家登录")
    @allure.title("测试商家登录失败 - 商家未审核")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_login_not_approved(self, unit_http_client, test_data_builder):
        """
        测试商家登录失败 - 商家未审核

        验证点：
        1. 返回状态码401或403
        2. 响应包含错误信息
        """
        merchant_data = test_data_builder.build_merchant(status="pending")

        register_response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        if register_response.status_code in [200, 201]:
            login_response = unit_http_client.post(
                "/api/auth/merchant/login",
                json_data={"loginIdentifier": merchant_data["phone"], "password": merchant_data["password"]},
            )

            assert login_response.status_code in [401, 403], f"预期状态码401或403，实际为{login_response.status_code}"
        else:
            pytest.skip("无法创建待审核商家")

        self.log_response(register_response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestMerchantRegisterAPI(BaseAPITest):
    """商家注册接口测试"""

    @allure.story("商家注册")
    @allure.title("测试商家注册成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_register_success(self, unit_http_client, test_data_builder):
        """
        测试商家注册成功

        验证点：
        1. 返回状态码201
        2. 响应包含商家信息
        """
        merchant_data = test_data_builder.build_merchant()

        response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        self.assert_status_code(response, 201)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        self.log_response(response)

    @allure.story("商家注册")
    @allure.title("测试商家注册失败 - 商家名称已存在")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_register_duplicate_name(self, unit_http_client, test_merchant, test_data_builder):
        """
        测试商家注册失败 - 商家名称已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        merchant_data = test_data_builder.build_merchant(name=test_merchant["merchant"].get("name"))

        response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("商家注册")
    @allure.title("测试商家注册失败 - 邮箱已存在")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_register_duplicate_email(self, unit_http_client, test_merchant, test_data_builder):
        """
        测试商家注册失败 - 邮箱已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        merchant_data = test_data_builder.build_merchant(email=test_merchant["merchant"].get("email"))

        response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("商家注册")
    @allure.title("测试商家注册失败 - 手机号已存在")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_register_duplicate_phone(self, unit_http_client, test_merchant, test_data_builder):
        """
        测试商家注册失败 - 手机号已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        merchant_data = test_data_builder.build_merchant(phone=test_merchant["credentials"]["phone"])

        response = unit_http_client.post("/api/auth/merchant/register", json_data=merchant_data)

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestAdminLoginAPI(BaseAPITest):
    """管理员登录接口测试"""

    @allure.story("管理员登录")
    @allure.title("测试管理员登录成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_admin_login_success(self, unit_http_client, test_data_builder):
        """
        测试管理员登录成功

        验证点：
        1. 返回状态码200
        2. 响应包含token
        3. token不为空且为字符串类型

        注意：需要先创建管理员账号
        """
        admin_data = test_data_builder.build_admin()

        register_response = unit_http_client.post("/api/auth/admin/register", json_data=admin_data)

        if register_response.status_code in [200, 201]:
            login_response = unit_http_client.post(
                "/api/auth/admin/login",
                json_data={"loginIdentifier": admin_data.get("username"), "password": admin_data["password"]},
            )

            self.assert_status_code(login_response, 200)
            self.assert_response_success(login_response)

            json_data = login_response.json()
            assert "data" in json_data, "响应缺少data字段"

            data = json_data["data"]
            assert "token" in data, "响应缺少token字段"
            assert data["token"], "token为空"
        else:
            pytest.skip("无法创建管理员账号")

        self.log_response(login_response)

    @allure.story("管理员登录")
    @allure.title("测试管理员登录失败 - 错误密码")
    @allure.severity(allure.severity_level.NORMAL)
    def test_admin_login_wrong_password(self, unit_http_client, test_data_builder):
        """
        测试管理员登录失败 - 错误密码

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        admin_data = test_data_builder.build_admin()

        register_response = unit_http_client.post("/api/auth/admin/register", json_data=admin_data)

        if register_response.status_code in [200, 201]:
            login_response = unit_http_client.post(
                "/api/auth/admin/login",
                json_data={"loginIdentifier": admin_data.get("username"), "password": "wrongpassword123"},
            )

            self.assert_status_code(login_response, 401)
        else:
            pytest.skip("无法创建管理员账号")

        self.log_response(login_response)

    @allure.story("管理员登录")
    @allure.title("测试管理员登录失败 - 管理员不存在")
    @allure.severity(allure.severity_level.NORMAL)
    def test_admin_login_nonexistent(self, unit_http_client):
        """
        测试管理员登录失败 - 管理员不存在

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/admin/login",
            json_data={"loginIdentifier": "nonexistent_admin_12345", "password": "somepassword123"},
        )

        self.assert_status_code(response, 401)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestAdminRegisterAPI(BaseAPITest):
    """管理员注册接口测试"""

    @allure.story("管理员注册")
    @allure.title("测试管理员注册成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_admin_register_success(self, unit_http_client, test_data_builder):
        """
        测试管理员注册成功

        验证点：
        1. 返回状态码201
        2. 响应包含管理员信息
        """
        admin_data = test_data_builder.build_admin()

        response = unit_http_client.post("/api/auth/admin/register", json_data=admin_data)

        self.assert_status_code(response, 201)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        self.log_response(response)

    @allure.story("管理员注册")
    @allure.title("测试管理员注册失败 - 用户名已存在")
    @allure.severity(allure.severity_level.NORMAL)
    def test_admin_register_duplicate_username(self, unit_http_client, test_data_builder):
        """
        测试管理员注册失败 - 用户名已存在

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        admin_data = test_data_builder.build_admin()

        first_response = unit_http_client.post("/api/auth/admin/register", json_data=admin_data)

        if first_response.status_code in [200, 201]:
            second_response = unit_http_client.post("/api/auth/admin/register", json_data=admin_data)

            self.assert_status_code(second_response, 400)

            json_data = second_response.json()
            assert "message" in json_data or "error" in json_data, "响应缺少错误信息"
        else:
            pytest.skip("无法创建第一个管理员账号")

        self.log_response(second_response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestLogoutAPI(BaseAPITest):
    """用户登出接口测试"""

    @allure.story("用户登出")
    @allure.title("测试用户登出成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_logout_success(self, unit_http_client, test_user):
        """
        测试用户登出成功

        验证点：
        1. 返回状态码200
        2. 响应包含成功标识
        """
        headers = self.get_auth_headers(test_user["token"])

        response = unit_http_client.post("/api/auth/logout", headers=headers)

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        self.log_response(response)

    @allure.story("用户登出")
    @allure.title("测试未认证登出")
    @allure.severity(allure.severity_level.NORMAL)
    def test_logout_unauthorized(self, unit_http_client):
        """
        测试未认证登出

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.post("/api/auth/logout")

        self.assert_status_code(response, 401)

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestUserInfoAPI(BaseAPITest):
    """用户信息接口测试"""

    @allure.story("用户信息")
    @allure.title("测试获取当前用户信息成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_user_info_success(self, unit_http_client, test_user):
        """
        测试获取当前用户信息成功

        验证点：
        1. 返回状态码200
        2. 响应包含用户基本信息
        3. 用户信息与登录用户一致
        """
        headers = self.get_auth_headers(test_user["token"])

        response = unit_http_client.get("/api/auth/userinfo", headers=headers)

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        data = json_data["data"]
        assert "id" in data or "username" in data or "phone" in data, "响应缺少用户基本信息"

        self.log_response(response)

    @allure.story("用户信息")
    @allure.title("测试未认证访问用户信息")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_user_info_unauthorized(self, unit_http_client):
        """
        测试未认证访问用户信息

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.get("/api/auth/userinfo")

        self.assert_status_code(response, 401)

        self.log_response(response)

    @allure.story("用户信息")
    @allure.title("测试更新用户信息成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_user_info_success(self, unit_http_client, test_user, test_data_builder):
        """
        测试更新用户信息成功

        验证点：
        1. 返回状态码200
        2. 响应包含更新后的用户信息
        """
        headers = self.get_auth_headers(test_user["token"])

        update_data = {
            "username": test_data_builder._generate_unique_string("updated_user"),
            "avatar": "https://example.com/avatar/new.jpg",
        }

        response = unit_http_client.put("/api/auth/userinfo", json_data=update_data, headers=headers)

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        self.log_response(response)

    @allure.story("用户信息")
    @allure.title("测试未认证更新用户信息")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_user_info_unauthorized(self, unit_http_client):
        """
        测试未认证更新用户信息

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        update_data = {"username": "new_username"}

        response = unit_http_client.put("/api/auth/userinfo", json_data=update_data)

        self.assert_status_code(response, 401)

        self.log_response(response)

    @allure.story("用户信息")
    @allure.title("测试更新用户信息参数验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "invalid_data,description",
        [({"email": "invalid_email"}, "邮箱格式错误"), ({"phone": "123"}, "手机号格式错误"), ({}, "空数据")],
    )
    def test_update_user_info_validation(self, unit_http_client, test_user, invalid_data, description):
        """
        测试更新用户信息参数验证

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        headers = self.get_auth_headers(test_user["token"])

        response = unit_http_client.put("/api/auth/userinfo", json_data=invalid_data, headers=headers)

        assert response.status_code in [400, 200], f"预期状态码400或200，实际为{response.status_code} - {description}"

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestChangePasswordAPI(BaseAPITest):
    """修改密码接口测试"""

    @allure.story("修改密码")
    @allure.title("测试修改密码成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_change_password_success(self, unit_http_client, test_user):
        """
        测试修改密码成功

        验证点：
        1. 返回状态码200
        2. 响应包含成功标识
        3. 新密码可以登录
        """
        headers = self.get_auth_headers(test_user["token"])

        new_password = "newpassword123"

        response = unit_http_client.put(
            "/api/auth/password",
            json_data={"oldPassword": test_user["credentials"]["password"], "newPassword": new_password},
            headers=headers,
        )

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        login_response = unit_http_client.post(
            "/api/auth/login",
            json_data={"loginIdentifier": test_user["credentials"]["phone"], "password": new_password},
        )

        self.assert_status_code(login_response, 200)

        self.log_response(response)

    @allure.story("修改密码")
    @allure.title("测试修改密码失败 - 旧密码错误")
    @allure.severity(allure.severity_level.NORMAL)
    def test_change_password_wrong_old_password(self, unit_http_client, test_user):
        """
        测试修改密码失败 - 旧密码错误

        验证点：
        1. 返回状态码400或401
        2. 响应包含错误信息
        """
        headers = self.get_auth_headers(test_user["token"])

        response = unit_http_client.put(
            "/api/auth/password",
            json_data={"oldPassword": "wrongoldpassword", "newPassword": "newpassword123"},
            headers=headers,
        )

        assert response.status_code in [400, 401], f"预期状态码400或401，实际为{response.status_code}"

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("修改密码")
    @allure.title("测试修改密码失败 - 新密码格式错误")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("invalid_password", ["123", "", "   "])
    def test_change_password_invalid_new_password(self, unit_http_client, test_user, invalid_password):
        """
        测试修改密码失败 - 新密码格式错误

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        headers = self.get_auth_headers(test_user["token"])

        response = unit_http_client.put(
            "/api/auth/password",
            json_data={"oldPassword": test_user["credentials"]["password"], "newPassword": invalid_password},
            headers=headers,
        )

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("修改密码")
    @allure.title("测试修改密码失败 - 未认证访问")
    @allure.severity(allure.severity_level.NORMAL)
    def test_change_password_unauthorized(self, unit_http_client):
        """
        测试修改密码失败 - 未认证访问

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.put(
            "/api/auth/password", json_data={"oldPassword": "oldpassword123", "newPassword": "newpassword123"}
        )

        self.assert_status_code(response, 401)

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestSendVerifyCodeAPI(BaseAPITest):
    """发送验证码接口测试"""

    @allure.story("发送验证码")
    @allure.title("测试发送验证码成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_send_verify_code_success(self, unit_http_client, test_user):
        """
        测试发送验证码成功

        验证点：
        1. 返回状态码200
        2. 响应包含成功标识
        """
        response = unit_http_client.post(
            "/api/auth/sendVerifyCode", json_data={"email": test_user["user"].get("email")}
        )

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        self.log_response(response)

    @allure.story("发送验证码")
    @allure.title("测试发送验证码失败 - 手机号格式错误")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("invalid_email", ["invalid_email", "", "not_an_email"])
    def test_send_verify_code_invalid_email(self, unit_http_client, invalid_email):
        """
        测试发送验证码失败 - 邮箱格式错误

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        response = unit_http_client.post("/api/auth/sendVerifyCode", json_data={"email": invalid_email})

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("发送验证码")
    @allure.title("测试发送验证码失败 - 发送频率限制")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.skip(reason="需要测试发送频率限制功能")
    def test_send_verify_code_rate_limit(self, unit_http_client, test_user):
        """
        测试发送验证码失败 - 发送频率限制

        验证点：
        1. 第一次发送成功
        2. 短时间内第二次发送失败
        """
        email = test_user["user"].get("email")

        first_response = unit_http_client.post("/api/auth/sendVerifyCode", json_data={"email": email})

        self.assert_status_code(first_response, 200)

        second_response = unit_http_client.post("/api/auth/sendVerifyCode", json_data={"email": email})

        assert second_response.status_code in [400, 429], f"预期状态码400或429，实际为{second_response.status_code}"

        self.log_response(second_response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestResetPasswordAPI(BaseAPITest):
    """重置密码接口测试"""

    @allure.story("重置密码")
    @allure.title("测试重置密码成功")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.skip(reason="需要先获取验证码")
    def test_reset_password_success(self, unit_http_client, test_user):
        """
        测试重置密码成功

        验证点：
        1. 返回状态码200
        2. 响应包含成功标识
        3. 新密码可以登录

        注意：需要先获取验证码
        """
        verify_code = "123456"
        new_password = "newpassword123"

        response = unit_http_client.post(
            "/api/auth/resetPassword",
            json_data={"email": test_user["user"].get("email"), "verifyCode": verify_code, "password": new_password},
        )

        self.assert_status_code(response, 200)
        self.assert_response_success(response)

        self.log_response(response)

    @allure.story("重置密码")
    @allure.title("测试重置密码失败 - 验证码错误")
    @allure.severity(allure.severity_level.NORMAL)
    def test_reset_password_wrong_verify_code(self, unit_http_client, test_user):
        """
        测试重置密码失败 - 验证码错误

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息
        """
        response = unit_http_client.post(
            "/api/auth/resetPassword",
            json_data={
                "email": test_user["user"].get("email"),
                "verifyCode": "wrong_code",
                "password": "newpassword123",
            },
        )

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)

    @allure.story("重置密码")
    @allure.title("测试重置密码失败 - 验证码过期")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.skip(reason="需要使用过期的验证码")
    def test_reset_password_expired_verify_code(self, unit_http_client, test_user):
        """
        测试重置密码失败 - 验证码过期

        验证点：
        1. 返回状态码400
        2. 响应包含错误信息

        注意：需要使用过期的验证码
        """
        expired_code = "expired_code"

        response = unit_http_client.post(
            "/api/auth/resetPassword",
            json_data={
                "email": test_user["user"].get("email"),
                "verifyCode": expired_code,
                "password": "newpassword123",
            },
        )

        self.assert_status_code(response, 400)

        json_data = response.json()
        assert "message" in json_data or "error" in json_data, "响应缺少错误信息"

        self.log_response(response)


@allure.feature("认证API")
@pytest.mark.auth
@pytest.mark.unit_auth
class TestAuthenticationSecurity(BaseAPITest):
    """认证安全测试"""

    @allure.story("安全测试")
    @allure.title("测试SQL注入防护")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    @pytest.mark.parametrize(
        "malicious_payload",
        ["admin'--", "admin' OR '1'='1", "admin'; DROP TABLE users;--", "' OR 1=1--", "1' AND '1'='1"],
    )
    def test_sql_injection_protection(self, unit_http_client, malicious_payload):
        """
        测试SQL注入防护

        验证点：
        1. 返回状态码400或401
        2. 响应不包含SQL关键字
        3. 响应不包含敏感错误信息
        """
        response = unit_http_client.post(
            "/api/auth/login", json_data={"loginIdentifier": malicious_payload, "password": "anypassword"}
        )

        assert response.status_code in [400, 401], f"SQL注入测试失败，状态码: {response.status_code}"

        response_text = response.text.lower()
        assert "sql" not in response_text, f"响应中包含SQL关键字，可能存在SQL注入漏洞"
        assert "error" not in response_text or "invalid" in response_text, f"响应中包含敏感错误信息"

        self.log_response(response)

    @allure.story("安全测试")
    @allure.title("测试XSS防护")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    @pytest.mark.parametrize(
        "xss_payload",
        [
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "javascript:alert('XSS')",
            "<svg onload=alert('XSS')>",
        ],
    )
    def test_xss_protection(self, unit_http_client, xss_payload):
        """
        测试XSS防护

        验证点：
        1. 返回状态码400或401
        2. 响应不包含未转义的脚本标签
        3. 响应不包含未转义的alert函数
        """
        response = unit_http_client.post(
            "/api/auth/login", json_data={"loginIdentifier": xss_payload, "password": "anypassword"}
        )

        assert response.status_code in [400, 401], f"XSS测试失败，状态码: {response.status_code}"

        response_text = response.text
        assert "<script>" not in response_text.lower(), f"响应中包含未转义的script标签"
        assert "alert(" not in response_text, f"响应中包含未转义的alert函数"

        self.log_response(response)

    @allure.story("安全测试")
    @allure.title("测试无效Token访问")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_token_access(self, unit_http_client):
        """
        测试无效Token访问

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        headers = self.get_auth_headers("invalid_token_12345")

        response = unit_http_client.get("/api/auth/userinfo", headers=headers)

        self.assert_status_code(response, 401)

        self.log_response(response)

    @allure.story("安全测试")
    @allure.title("测试无Token访问受保护接口")
    @allure.severity(allure.severity_level.NORMAL)
    def test_no_token_access_protected_endpoint(self, unit_http_client):
        """
        测试无Token访问受保护接口

        验证点：
        1. 返回状态码401
        2. 响应包含错误信息
        """
        response = unit_http_client.get("/api/auth/userinfo")

        self.assert_status_code(response, 401)

        self.log_response(response)

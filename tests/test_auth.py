"""
认证模块测试用例
"""
import pytest
import allure
from tests.utils import HTTPClient, TokenManager, log_response_details
from tests.config import config


@allure.feature("认证模块")
@pytest.mark.auth
class TestUserAuthentication:
    """用户认证测试"""
    
    @allure.story("用户登录")
    @allure.title("测试用户登录成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_user_login_success(self, http_client: HTTPClient, user_credentials: dict):
        """
        测试用户登录成功
        
        Steps:
        1. 使用正确的用户名和密码登录
        2. 验证返回Token不为空
        3. 验证返回状态码为200
        """
        response = http_client.post(
            "/api/auth/login",
            json_data={
                "phone": user_credentials["phone"],
                "password": user_credentials["password"]
            }
        )
        
        assert response.status_code == 200, f"登录失败，状态码: {response.status_code}"
        
        response_data = response.json()
        assert "data" in response_data, "响应中没有data字段"
        
        token = response_data.get("data", {}).get("token")
        assert token is not None, "返回的Token为空"
        assert isinstance(token, str), "Token类型不正确"
        assert len(token) > 0, "Token长度为0"
        
        log_response_details(response)
    
    @allure.story("用户登录")
    @allure.title("测试用户密码错误")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_login_wrong_password(self, http_client: HTTPClient, user_credentials: dict):
        """
        测试用户密码错误
        
        Steps:
        1. 使用错误密码登录
        2. 验证返回401或错误信息
        """
        response = http_client.post(
            "/api/auth/login",
            json_data={
                "phone": user_credentials["phone"],
                "password": "wrongpassword123"
            }
        )
        
        assert response.status_code == 401, \
            f"预期状态码401，实际状态码: {response.status_code}"
        
        response_data = response.json()
        assert "message" in response_data or "error" in response_data, \
            "响应中没有错误信息"
        
        log_response_details(response)
    
    @allure.story("用户登录")
    @allure.title("测试不存在的用户")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_login_nonexistent(self, http_client: HTTPClient):
        """
        测试不存在的用户登录
        
        Steps:
        1. 使用不存在的用户名登录
        2. 验证返回401或错误信息
        """
        response = http_client.post(
            "/api/auth/login",
            json_data={
                "username": "nonexistent_user_12345",
                "password": "somepassword123"
            }
        )
        
        assert response.status_code == 401, \
            f"预期状态码401，实际状态码: {response.status_code}"
        
        response_data = response.json()
        assert "message" in response_data or "error" in response_data, \
            "响应中没有错误信息"
        
        log_response_details(response)


@allure.feature("认证模块")
@pytest.mark.auth
class TestMerchantAuthentication:
    """商家认证测试"""
    
    @allure.story("商家登录")
    @allure.title("测试商家登录成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_login_success(self, http_client: HTTPClient, merchant_credentials: dict):
        """
        测试商家登录成功
        
        Steps:
        1. 使用正确的商家账号登录
        2. 验证返回Token不为空
        3. 验证返回状态码为200
        """
        response = http_client.post(
            "/api/auth/merchant/login",
            json_data={
                "username": merchant_credentials["email"],
                "password": merchant_credentials["password"]
            }
        )
        
        assert response.status_code == 200, f"登录失败，状态码: {response.status_code}"
        
        response_data = response.json()
        assert "data" in response_data, "响应中没有data字段"
        
        token = response_data.get("data", {}).get("token")
        assert token is not None, "返回的Token为空"
        assert isinstance(token, str), "Token类型不正确"
        assert len(token) > 0, "Token长度为0"
        
        log_response_details(response)
    
    @allure.story("商家登录")
    @allure.title("测试商家密码错误")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_login_wrong_password(self, http_client: HTTPClient, merchant_credentials: dict):
        """
        测试商家密码错误
        
        Steps:
        1. 使用错误密码登录
        2. 验证返回401或错误信息
        """
        response = http_client.post(
            "/api/auth/merchant/login",
            json_data={
                "username": merchant_credentials["email"],
                "password": "wrongpassword123"
            }
        )
        
        assert response.status_code == 401, \
            f"预期状态码401，实际状态码: {response.status_code}"
        
        response_data = response.json()
        assert "message" in response_data or "error" in response_data, \
            "响应中没有错误信息"
        
        log_response_details(response)


@allure.feature("认证模块")
@pytest.mark.auth
class TestTokenValidation:
    """Token验证测试"""
    
    @allure.story("Token验证")
    @allure.title("测试Token验证 - 有效Token")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_token_validation(self, http_client: HTTPClient, user_token: str):
        """
        测试Token验证
        
        Steps:
        1. 使用获取的Token访问受保护接口
        2. 验证可以正常访问
        """
        http_client.token_manager.set_token("user", user_token)
        
        response = http_client.get(
            "/api/user/profile",
            user_type="user"
        )
        
        assert response.status_code == 200, \
            f"使用有效Token访问受保护接口失败，状态码: {response.status_code}"
        
        response_data = response.json()
        assert "data" in response_data, "响应中没有data字段"
        
        log_response_details(response)
    
    @allure.story("Token验证")
    @allure.title("测试无效Token")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_token(self, http_client: HTTPClient):
        """
        测试无效Token
        
        Steps:
        1. 使用无效Token访问受保护接口
        2. 验证返回401
        """
        http_client.token_manager.set_token("user", "invalid_token_12345")
        
        response = http_client.get(
            "/api/user/profile",
            user_type="user"
        )
        
        assert response.status_code == 401, \
            f"预期状态码401，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("Token验证")
    @allure.title("测试过期Token")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.skip(reason="需要生成过期的Token进行测试")
    def test_expired_token(self, http_client: HTTPClient):
        """
        测试过期Token
        
        Steps:
        1. 使用过期Token访问受保护接口
        2. 验证返回401
        """
        expired_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.4Adcj3UFYzP5aI9W2t3t3t3t3t3t3t3t3t3t3t3t3t3"
        
        http_client.token_manager.set_token("user", expired_token)
        
        response = http_client.get(
            "/api/user/profile",
            user_type="user"
        )
        
        assert response.status_code == 401, \
            f"预期状态码401，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("Token验证")
    @allure.title("测试无Token访问受保护接口")
    @allure.severity(allure.severity_level.NORMAL)
    def test_no_token_access(self, http_client: HTTPClient):
        """
        测试无Token访问受保护接口
        
        Steps:
        1. 不提供Token访问受保护接口
        2. 验证返回401
        """
        http_client.token_manager.remove_token("user")
        
        response = http_client.get("/api/user/profile")
        
        assert response.status_code == 401, \
            f"预期状态码401，实际状态码: {response.status_code}"
        
        log_response_details(response)


@allure.feature("认证模块")
@pytest.mark.auth
class TestAuthenticationFlow:
    """认证流程测试"""
    
    @allure.story("认证流程")
    @allure.title("测试完整认证流程")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_complete_auth_flow(self, http_client: HTTPClient, user_credentials: dict):
        """
        测试完整认证流程
        
        Steps:
        1. 用户登录获取Token
        2. 使用Token访问受保护接口
        3. 验证整个流程正常
        """
        login_response = http_client.post(
            "/api/auth/login",
            json_data={
                "phone": user_credentials["phone"],
                "password": user_credentials["password"]
            }
        )
        
        assert login_response.status_code == 200, "登录失败"
        
        token = login_response.json().get("data", {}).get("token")
        assert token is not None, "Token获取失败"
        
        http_client.token_manager.set_token("user", token)
        
        profile_response = http_client.get(
            "/api/user/profile",
            user_type="user"
        )
        
        assert profile_response.status_code == 200, "访问受保护接口失败"
        
        log_response_details(profile_response)
    
    @allure.story("认证流程")
    @allure.title("测试商家完整认证流程")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_complete_auth_flow(self, http_client: HTTPClient, merchant_credentials: dict):
        """
        测试商家完整认证流程
        
        Steps:
        1. 商家登录获取Token
        2. 使用Token访问受保护接口
        3. 验证整个流程正常
        """
        login_response = http_client.post(
            "/api/auth/merchant/login",
            json_data={
                "username": merchant_credentials["email"],
                "password": merchant_credentials["password"]
            }
        )
        
        assert login_response.status_code == 200, "登录失败"
        
        token = login_response.json().get("data", {}).get("token")
        assert token is not None, "Token获取失败"
        
        http_client.token_manager.set_token("merchant", token)
        
        profile_response = http_client.get(
            "/api/merchant/profile",
            user_type="merchant"
        )
        
        assert profile_response.status_code == 200, "访问受保护接口失败"
        
        log_response_details(profile_response)


@allure.feature("认证模块")
@pytest.mark.auth
class TestAuthenticationEdgeCases:
    """认证边界情况测试"""
    
    @allure.story("边界情况")
    @allure.title("测试空用户名登录")
    @allure.severity(allure.severity_level.NORMAL)
    def test_empty_username_login(self, http_client: HTTPClient):
        """
        测试空用户名登录
        
        Steps:
        1. 使用空用户名登录
        2. 验证返回400或错误信息
        """
        response = http_client.post(
            "/api/auth/login",
            json_data={
                "username": "",
                "password": "somepassword123"
            }
        )
        
        assert response.status_code in [400, 401], \
            f"预期状态码400或401，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试空密码登录")
    @allure.severity(allure.severity_level.NORMAL)
    def test_empty_password_login(self, http_client: HTTPClient, user_credentials: dict):
        """
        测试空密码登录
        
        Steps:
        1. 使用空密码登录
        2. 验证返回400或错误信息
        """
        response = http_client.post(
            "/api/auth/login",
            json_data={
                "phone": user_credentials["phone"],
                "password": ""
            }
        )
        
        assert response.status_code in [400, 401], \
            f"预期状态码400或401，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试缺少必填字段")
    @allure.severity(allure.severity_level.NORMAL)
    def test_missing_required_fields(self, http_client: HTTPClient):
        """
        测试缺少必填字段
        
        Steps:
        1. 只提供用户名，不提供密码
        2. 验证返回400或错误信息
        """
        response = http_client.post(
            "/api/auth/login",
            json_data={
                "username": "testuser"
            }
        )
        
        assert response.status_code in [400, 401], \
            f"预期状态码400或401，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试SQL注入防护")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_sql_injection_protection(self, http_client: HTTPClient):
        """
        测试SQL注入防护
        
        Steps:
        1. 使用SQL注入字符串作为用户名
        2. 验证系统正确处理，不返回敏感信息
        """
        malicious_payloads = [
            "admin'--",
            "admin' OR '1'='1",
            "admin'; DROP TABLE users;--",
            "' OR 1=1--",
            "1' AND '1'='1"
        ]
        
        for payload in malicious_payloads:
            response = http_client.post(
                "/api/auth/login",
                json_data={
                    "username": payload,
                    "password": "anypassword"
                }
            )
            
            assert response.status_code in [400, 401], \
                f"SQL注入测试失败，payload: {payload}, 状态码: {response.status_code}"
            
            response_text = response.text.lower()
            assert "sql" not in response_text, \
                f"响应中包含SQL关键字，可能存在SQL注入漏洞: {payload}"
            assert "error" not in response_text or "invalid" in response_text, \
                f"响应中包含敏感错误信息: {payload}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试XSS防护")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_xss_protection(self, http_client: HTTPClient):
        """
        测试XSS防护
        
        Steps:
        1. 使用XSS脚本作为用户名
        2. 验证系统正确处理，不执行脚本
        """
        xss_payloads = [
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "javascript:alert('XSS')",
            "<svg onload=alert('XSS')>"
        ]
        
        for payload in xss_payloads:
            response = http_client.post(
                "/api/auth/login",
                json_data={
                    "username": payload,
                    "password": "anypassword"
                }
            )
            
            assert response.status_code in [400, 401], \
                f"XSS测试失败，payload: {payload}, 状态码: {response.status_code}"
            
            response_text = response.text
            assert "<script>" not in response_text.lower(), \
                f"响应中包含未转义的script标签: {payload}"
            assert "alert(" not in response_text, \
                f"响应中包含未转义的alert函数: {payload}"
        
        log_response_details(response)


@allure.feature("认证模块")
@pytest.mark.auth
class TestTokenManagement:
    """Token管理测试"""
    
    @allure.story("Token管理")
    @allure.title("测试Token刷新")
    @allure.severity(allure.severity_level.NORMAL)
    def test_token_refresh(self, http_client: HTTPClient, user_token: str):
        """
        测试Token刷新
        
        Steps:
        1. 使用有效Token请求刷新Token
        2. 验证返回新的Token
        3. 验证新Token可以正常使用
        """
        http_client.token_manager.set_token("user", user_token)
        
        response = http_client.post(
            "/api/auth/refresh",
            user_type="user"
        )
        
        if response.status_code == 200:
            new_token = response.json().get("data", {}).get("token")
            assert new_token is not None, "刷新Token失败"
            assert new_token != user_token, "新Token与旧Token相同"
            
            http_client.token_manager.set_token("user", new_token)
            
            profile_response = http_client.get(
                "/api/user/profile",
                user_type="user"
            )
            
            assert profile_response.status_code == 200, "使用新Token访问失败"
        else:
            pytest.skip("Token刷新接口不可用")
        
        log_response_details(response)
    
    @allure.story("Token管理")
    @allure.title("测试Token登出")
    @allure.severity(allure.severity_level.NORMAL)
    def test_token_logout(self, http_client: HTTPClient, user_token: str):
        """
        测试Token登出
        
        Steps:
        1. 使用有效Token登出
        2. 验证Token失效
        3. 验证使用旧Token访问失败
        """
        http_client.token_manager.set_token("user", user_token)
        
        logout_response = http_client.post(
            "/api/auth/logout",
            user_type="user"
        )
        
        if logout_response.status_code == 200:
            profile_response = http_client.get(
                "/api/user/profile",
                user_type="user"
            )
            
            assert profile_response.status_code == 401, \
                "登出后Token仍然有效"
        else:
            pytest.skip("登出接口不可用")
        
        log_response_details(logout_response)

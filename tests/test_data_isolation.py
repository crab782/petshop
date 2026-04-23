"""
数据隔离验证测试
测试用户、商家、管理员之间的数据隔离和权限控制
"""
import pytest
import allure
from typing import Dict, Any

from tests.utils import HTTPClient, TokenManager, log_response_details, log_test_case
from tests.fixtures.test_data import TestDataGenerator, TestData


@allure.feature("数据隔离")
@pytest.mark.security
@pytest.mark.isolation
class TestUserDataIsolation:
    """用户数据隔离测试"""

    @allure.story("用户数据隔离")
    @allure.title("测试用户无法访问其他用户的订单")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建两个测试用户（用户A和用户B）
    2. 用户A创建一个订单
    3. 用户B尝试访问用户A的订单
    4. 验证返回403或404
    """)
    def test_user_cannot_access_other_user_orders(self, http_client: HTTPClient):
        """
        测试用户A无法访问用户B的订单
        
        验证点：
        - 用户B访问用户A的订单时应返回403或404
        - 确保数据隔离正确实施
        """
        log_test_case("test_user_cannot_access_other_user_orders", "测试用户数据隔离 - 订单")
        
        generator_a = TestDataGenerator(http_client)
        generator_b = TestDataGenerator(http_client)
        
        try:
            user_a = generator_a.create_test_user()
            assert user_a, "创建用户A失败"
            
            user_b = generator_b.create_test_user()
            assert user_b, "创建用户B失败"
            
            merchant_gen = TestDataGenerator(http_client)
            merchant = merchant_gen.create_test_merchant()
            assert merchant, "创建测试商家失败"
            
            product = merchant_gen.create_test_product(merchant.get("id"))
            assert product, "创建测试商品失败"
            
            address_a = generator_a.create_test_address(user_a["id"])
            assert address_a, "创建用户A地址失败"
            
            order_a = generator_a.create_test_order(product["id"], address_a["id"])
            assert order_a, "创建用户A订单失败"
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            response = http_client.get(
                f"/api/user/orders/{order_a['id']}",
                user_type="user_b"
            )
            
            assert response.status_code in [403, 404], \
                f"用户B不应能访问用户A的订单，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
        finally:
            generator_a.cleanup_test_data()
            generator_b.cleanup_test_data()
            merchant_gen.cleanup_test_data()

    @allure.story("用户数据隔离")
    @allure.title("测试用户无法访问其他用户的预约")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建两个测试用户（用户A和用户B）
    2. 用户A创建一个预约
    3. 用户B尝试访问用户A的预约
    4. 验证返回403或404
    """)
    def test_user_cannot_access_other_user_appointments(self, http_client: HTTPClient):
        """
        测试用户A无法访问用户B的预约
        
        验证点：
        - 用户B访问用户A的预约时应返回403或404
        - 确保预约数据隔离正确实施
        """
        log_test_case("test_user_cannot_access_other_user_appointments", "测试用户数据隔离 - 预约")
        
        generator_a = TestDataGenerator(http_client)
        generator_b = TestDataGenerator(http_client)
        
        try:
            user_a = generator_a.create_test_user()
            assert user_a, "创建用户A失败"
            
            user_b = generator_b.create_test_user()
            assert user_b, "创建用户B失败"
            
            merchant_gen = TestDataGenerator(http_client)
            merchant = merchant_gen.create_test_merchant()
            assert merchant, "创建测试商家失败"
            
            service = merchant_gen.create_test_service(merchant.get("id"))
            assert service, "创建测试服务失败"
            
            pet_a = generator_a.create_test_pet(user_a["id"])
            assert pet_a, "创建用户A宠物失败"
            
            appointment_a = generator_a.create_test_appointment(service["id"], pet_a["id"])
            assert appointment_a, "创建用户A预约失败"
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            response = http_client.get(
                f"/api/user/appointments/{appointment_a['id']}",
                user_type="user_b"
            )
            
            assert response.status_code in [403, 404], \
                f"用户B不应能访问用户A的预约，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
        finally:
            generator_a.cleanup_test_data()
            generator_b.cleanup_test_data()
            merchant_gen.cleanup_test_data()

    @allure.story("用户数据隔离")
    @allure.title("测试用户无法访问其他用户的宠物")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建两个测试用户（用户A和用户B）
    2. 用户A创建一个宠物
    3. 用户B尝试访问用户A的宠物
    4. 验证返回403或404
    """)
    def test_user_cannot_access_other_user_pets(self, http_client: HTTPClient):
        """
        测试用户A无法访问用户B的宠物
        
        验证点：
        - 用户B访问用户A的宠物时应返回403或404
        - 确保宠物数据隔离正确实施
        """
        log_test_case("test_user_cannot_access_other_user_pets", "测试用户数据隔离 - 宠物")
        
        generator_a = TestDataGenerator(http_client)
        generator_b = TestDataGenerator(http_client)
        
        try:
            user_a = generator_a.create_test_user()
            assert user_a, "创建用户A失败"
            
            user_b = generator_b.create_test_user()
            assert user_b, "创建用户B失败"
            
            pet_a = generator_a.create_test_pet(user_a["id"])
            assert pet_a, "创建用户A宠物失败"
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            response = http_client.get(
                f"/api/user/pets/{pet_a['id']}",
                user_type="user_b"
            )
            
            assert response.status_code in [403, 404], \
                f"用户B不应能访问用户A的宠物，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            update_response = http_client.put(
                f"/api/user/pets/{pet_a['id']}",
                json_data={
                    "name": "modified_by_other_user",
                    "type": "狗",
                    "breed": "金毛",
                    "age": 3,
                    "gender": "male"
                },
                user_type="user_b"
            )
            
            assert update_response.status_code in [403, 404], \
                f"用户B不应能修改用户A的宠物，实际状态码: {update_response.status_code}"
            
            log_response_details(update_response)
            
        finally:
            generator_a.cleanup_test_data()
            generator_b.cleanup_test_data()

    @allure.story("用户数据隔离")
    @allure.title("测试用户无法访问其他用户的地址")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_user_cannot_access_other_user_addresses(self, http_client: HTTPClient):
        """
        测试用户A无法访问用户B的地址
        """
        log_test_case("test_user_cannot_access_other_user_addresses", "测试用户数据隔离 - 地址")
        
        generator_a = TestDataGenerator(http_client)
        generator_b = TestDataGenerator(http_client)
        
        try:
            user_a = generator_a.create_test_user()
            assert user_a, "创建用户A失败"
            
            user_b = generator_b.create_test_user()
            assert user_b, "创建用户B失败"
            
            address_a = generator_a.create_test_address(user_a["id"])
            assert address_a, "创建用户A地址失败"
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            response = http_client.get(
                f"/api/user/addresses/{address_a['id']}",
                user_type="user_b"
            )
            
            assert response.status_code in [403, 404], \
                f"用户B不应能访问用户A的地址，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
        finally:
            generator_a.cleanup_test_data()
            generator_b.cleanup_test_data()


@allure.feature("数据隔离")
@pytest.mark.security
@pytest.mark.isolation
class TestMerchantDataIsolation:
    """商家数据隔离测试"""

    @allure.story("商家数据隔离")
    @allure.title("测试商家无法访问其他商家的订单")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建两个测试商家（商家A和商家B）
    2. 商家A的店铺有订单
    3. 商家B尝试访问商家A的订单
    4. 验证返回403或404
    """)
    def test_merchant_cannot_access_other_merchant_orders(self, http_client: HTTPClient):
        """
        测试商家A无法访问商家B的订单
        
        验证点：
        - 商家B访问商家A的订单时应返回403或404
        - 确保商家订单数据隔离正确实施
        """
        log_test_case("test_merchant_cannot_access_other_merchant_orders", "测试商家数据隔离 - 订单")
        
        merchant_gen_a = TestDataGenerator(http_client)
        merchant_gen_b = TestDataGenerator(http_client)
        user_gen = TestDataGenerator(http_client)
        
        try:
            merchant_a = merchant_gen_a.create_test_merchant()
            assert merchant_a, "创建商家A失败"
            
            merchant_b = merchant_gen_b.create_test_merchant()
            assert merchant_b, "创建商家B失败"
            
            user = user_gen.create_test_user()
            assert user, "创建测试用户失败"
            
            product_a = merchant_gen_a.create_test_product(merchant_a["id"])
            assert product_a, "创建商家A商品失败"
            
            address = user_gen.create_test_address(user["id"])
            assert address, "创建用户地址失败"
            
            order_a = user_gen.create_test_order(product_a["id"], address["id"])
            assert order_a, "创建订单失败"
            
            http_client.token_manager.set_token("merchant_b", merchant_b["token"])
            
            response = http_client.get(
                f"/api/merchant/orders/{order_a['id']}",
                user_type="merchant_b"
            )
            
            assert response.status_code in [403, 404], \
                f"商家B不应能访问商家A的订单，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
        finally:
            merchant_gen_a.cleanup_test_data()
            merchant_gen_b.cleanup_test_data()
            user_gen.cleanup_test_data()

    @allure.story("商家数据隔离")
    @allure.title("测试商家无法访问其他商家的预约")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建两个测试商家（商家A和商家B）
    2. 商家A的店铺有预约
    3. 商家B尝试访问商家A的预约
    4. 验证返回403或404
    """)
    def test_merchant_cannot_access_other_merchant_appointments(self, http_client: HTTPClient):
        """
        测试商家A无法访问商家B的预约
        
        验证点：
        - 商家B访问商家A的预约时应返回403或404
        - 确保商家预约数据隔离正确实施
        """
        log_test_case("test_merchant_cannot_access_other_merchant_appointments", "测试商家数据隔离 - 预约")
        
        merchant_gen_a = TestDataGenerator(http_client)
        merchant_gen_b = TestDataGenerator(http_client)
        user_gen = TestDataGenerator(http_client)
        
        try:
            merchant_a = merchant_gen_a.create_test_merchant()
            assert merchant_a, "创建商家A失败"
            
            merchant_b = merchant_gen_b.create_test_merchant()
            assert merchant_b, "创建商家B失败"
            
            user = user_gen.create_test_user()
            assert user, "创建测试用户失败"
            
            service_a = merchant_gen_a.create_test_service(merchant_a["id"])
            assert service_a, "创建商家A服务失败"
            
            pet = user_gen.create_test_pet(user["id"])
            assert pet, "创建用户宠物失败"
            
            appointment_a = user_gen.create_test_appointment(service_a["id"], pet["id"])
            assert appointment_a, "创建预约失败"
            
            http_client.token_manager.set_token("merchant_b", merchant_b["token"])
            
            response = http_client.get(
                f"/api/merchant/appointments/{appointment_a['id']}",
                user_type="merchant_b"
            )
            
            assert response.status_code in [403, 404], \
                f"商家B不应能访问商家A的预约，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
        finally:
            merchant_gen_a.cleanup_test_data()
            merchant_gen_b.cleanup_test_data()
            user_gen.cleanup_test_data()

    @allure.story("商家数据隔离")
    @allure.title("测试商家无法访问其他商家的服务")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建两个测试商家（商家A和商家B）
    2. 商家A创建一个服务
    3. 商家B尝试修改商家A的服务
    4. 验证返回403或404
    """)
    def test_merchant_cannot_access_other_merchant_services(self, http_client: HTTPClient):
        """
        测试商家A无法访问商家B的服务
        
        验证点：
        - 商家B访问商家A的服务时应返回403或404
        - 商家B修改商家A的服务时应返回403或404
        - 确保商家服务数据隔离正确实施
        """
        log_test_case("test_merchant_cannot_access_other_merchant_services", "测试商家数据隔离 - 服务")
        
        merchant_gen_a = TestDataGenerator(http_client)
        merchant_gen_b = TestDataGenerator(http_client)
        
        try:
            merchant_a = merchant_gen_a.create_test_merchant()
            assert merchant_a, "创建商家A失败"
            
            merchant_b = merchant_gen_b.create_test_merchant()
            assert merchant_b, "创建商家B失败"
            
            service_a = merchant_gen_a.create_test_service(merchant_a["id"])
            assert service_a, "创建商家A服务失败"
            
            http_client.token_manager.set_token("merchant_b", merchant_b["token"])
            
            response = http_client.get(
                f"/api/merchant/services/{service_a['id']}",
                user_type="merchant_b"
            )
            
            assert response.status_code in [403, 404], \
                f"商家B不应能访问商家A的服务，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
            update_response = http_client.put(
                f"/api/merchant/services/{service_a['id']}",
                json_data={
                    "name": "modified_by_other_merchant",
                    "description": "被其他商家修改",
                    "price": 999.99,
                    "duration": 60
                },
                user_type="merchant_b"
            )
            
            assert update_response.status_code in [403, 404], \
                f"商家B不应能修改商家A的服务，实际状态码: {update_response.status_code}"
            
            log_response_details(update_response)
            
            delete_response = http_client.delete(
                f"/api/merchant/services/{service_a['id']}",
                user_type="merchant_b"
            )
            
            assert delete_response.status_code in [403, 404], \
                f"商家B不应能删除商家A的服务，实际状态码: {delete_response.status_code}"
            
            log_response_details(delete_response)
            
        finally:
            merchant_gen_a.cleanup_test_data()
            merchant_gen_b.cleanup_test_data()

    @allure.story("商家数据隔离")
    @allure.title("测试商家无法访问其他商家的商品")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_merchant_cannot_access_other_merchant_products(self, http_client: HTTPClient):
        """
        测试商家A无法访问商家B的商品
        """
        log_test_case("test_merchant_cannot_access_other_merchant_products", "测试商家数据隔离 - 商品")
        
        merchant_gen_a = TestDataGenerator(http_client)
        merchant_gen_b = TestDataGenerator(http_client)
        
        try:
            merchant_a = merchant_gen_a.create_test_merchant()
            assert merchant_a, "创建商家A失败"
            
            merchant_b = merchant_gen_b.create_test_merchant()
            assert merchant_b, "创建商家B失败"
            
            product_a = merchant_gen_a.create_test_product(merchant_a["id"])
            assert product_a, "创建商家A商品失败"
            
            http_client.token_manager.set_token("merchant_b", merchant_b["token"])
            
            response = http_client.get(
                f"/api/merchant/products/{product_a['id']}",
                user_type="merchant_b"
            )
            
            assert response.status_code in [403, 404], \
                f"商家B不应能访问商家A的商品，实际状态码: {response.status_code}"
            
            log_response_details(response)
            
            update_response = http_client.put(
                f"/api/merchant/products/{product_a['id']}",
                json_data={
                    "name": "modified_by_other_merchant",
                    "description": "被其他商家修改",
                    "price": 999.99,
                    "stock": 100
                },
                user_type="merchant_b"
            )
            
            assert update_response.status_code in [403, 404], \
                f"商家B不应能修改商家A的商品，实际状态码: {update_response.status_code}"
            
            log_response_details(update_response)
            
        finally:
            merchant_gen_a.cleanup_test_data()
            merchant_gen_b.cleanup_test_data()


@allure.feature("数据隔离")
@pytest.mark.security
@pytest.mark.isolation
class TestRoleIsolation:
    """角色隔离测试"""

    @allure.story("角色隔离")
    @allure.title("测试用户无法访问商家管理接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建一个测试用户
    2. 用户尝试访问商家管理接口
    3. 验证返回403
    """)
    def test_user_cannot_access_merchant_api(self, http_client: HTTPClient):
        """
        测试用户无法访问商家管理接口
        
        验证点：
        - 用户访问商家管理接口时应返回403
        - 确保角色权限隔离正确实施
        """
        log_test_case("test_user_cannot_access_merchant_api", "测试角色隔离 - 用户访问商家API")
        
        generator = TestDataGenerator(http_client)
        
        try:
            user = generator.create_test_user()
            assert user, "创建测试用户失败"
            
            merchant_endpoints = [
                "/api/merchant/services",
                "/api/merchant/products",
                "/api/merchant/orders",
                "/api/merchant/appointments",
                "/api/merchant/profile"
            ]
            
            for endpoint in merchant_endpoints:
                response = http_client.get(endpoint, user_type="user")
                
                assert response.status_code in [401, 403], \
                    f"用户不应能访问商家接口 {endpoint}，实际状态码: {response.status_code}"
                
                log_response_details(response)
            
        finally:
            generator.cleanup_test_data()

    @allure.story("角色隔离")
    @allure.title("测试商家无法访问管理员接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 创建一个测试商家
    2. 商家尝试访问管理员接口
    3. 验证返回403
    """)
    def test_merchant_cannot_access_admin_api(self, http_client: HTTPClient):
        """
        测试商家无法访问管理员接口
        
        验证点：
        - 商家访问管理员接口时应返回403
        - 确保角色权限隔离正确实施
        """
        log_test_case("test_merchant_cannot_access_admin_api", "测试角色隔离 - 商家访问管理员API")
        
        generator = TestDataGenerator(http_client)
        
        try:
            merchant = generator.create_test_merchant()
            assert merchant, "创建测试商家失败"
            
            admin_endpoints = [
                "/api/admin/users",
                "/api/admin/merchants",
                "/api/admin/services",
                "/api/admin/products",
                "/api/admin/reviews",
                "/api/admin/announcements"
            ]
            
            for endpoint in admin_endpoints:
                response = http_client.get(endpoint, user_type="merchant")
                
                assert response.status_code in [401, 403], \
                    f"商家不应能访问管理员接口 {endpoint}，实际状态码: {response.status_code}"
                
                log_response_details(response)
            
        finally:
            generator.cleanup_test_data()

    @allure.story("角色隔离")
    @allure.title("测试用户无法访问管理员接口")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_user_cannot_access_admin_api(self, http_client: HTTPClient):
        """
        测试用户无法访问管理员接口
        """
        log_test_case("test_user_cannot_access_admin_api", "测试角色隔离 - 用户访问管理员API")
        
        generator = TestDataGenerator(http_client)
        
        try:
            user = generator.create_test_user()
            assert user, "创建测试用户失败"
            
            admin_endpoints = [
                "/api/admin/users",
                "/api/admin/merchants",
                "/api/admin/services",
                "/api/admin/products"
            ]
            
            for endpoint in admin_endpoints:
                response = http_client.get(endpoint, user_type="user")
                
                assert response.status_code in [401, 403], \
                    f"用户不应能访问管理员接口 {endpoint}，实际状态码: {response.status_code}"
                
                log_response_details(response)
            
        finally:
            generator.cleanup_test_data()

    @allure.story("角色隔离")
    @allure.title("测试未认证用户无法访问受保护接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.description("""
    测试步骤：
    1. 不提供Token访问受保护接口
    2. 验证返回401
    """)
    def test_unauthenticated_access(self, http_client: HTTPClient):
        """
        测试未认证用户无法访问受保护接口
        
        验证点：
        - 未登录用户访问受保护接口时应返回401
        - 确保认证机制正确实施
        """
        log_test_case("test_unauthenticated_access", "测试未认证访问")
        
        http_client.token_manager.clear_all()
        
        protected_endpoints = [
            ("/api/user/profile", "get"),
            ("/api/user/pets", "get"),
            ("/api/user/orders", "get"),
            ("/api/user/appointments", "get"),
            ("/api/merchant/profile", "get"),
            ("/api/merchant/services", "get"),
            ("/api/merchant/products", "get"),
            ("/api/admin/users", "get"),
            ("/api/admin/merchants", "get")
        ]
        
        for endpoint, method in protected_endpoints:
            if method == "get":
                response = http_client.get(endpoint)
            elif method == "post":
                response = http_client.post(endpoint, json_data={})
            else:
                continue
            
            assert response.status_code == 401, \
                f"未认证用户不应能访问受保护接口 {endpoint}，实际状态码: {response.status_code}"
            
            log_response_details(response)


@allure.feature("数据隔离")
@pytest.mark.security
@pytest.mark.isolation
class TestDataModificationIsolation:
    """数据修改隔离测试"""

    @allure.story("数据修改隔离")
    @allure.title("测试用户无法修改其他用户的数据")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_user_cannot_modify_other_user_data(self, http_client: HTTPClient):
        """
        测试用户无法修改其他用户的数据
        """
        log_test_case("test_user_cannot_modify_other_user_data", "测试数据修改隔离")
        
        generator_a = TestDataGenerator(http_client)
        generator_b = TestDataGenerator(http_client)
        
        try:
            user_a = generator_a.create_test_user()
            assert user_a, "创建用户A失败"
            
            user_b = generator_b.create_test_user()
            assert user_b, "创建用户B失败"
            
            pet_a = generator_a.create_test_pet(user_a["id"])
            assert pet_a, "创建用户A宠物失败"
            
            address_a = generator_a.create_test_address(user_a["id"])
            assert address_a, "创建用户A地址失败"
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            update_pet_response = http_client.put(
                f"/api/user/pets/{pet_a['id']}",
                json_data={
                    "name": "hacked_pet",
                    "type": "猫",
                    "breed": "英短",
                    "age": 2,
                    "gender": "female"
                },
                user_type="user_b"
            )
            
            assert update_pet_response.status_code in [403, 404], \
                f"用户B不应能修改用户A的宠物，实际状态码: {update_pet_response.status_code}"
            
            update_address_response = http_client.put(
                f"/api/user/addresses/{address_a['id']}",
                json_data={
                    "contactName": "hacked_address",
                    "phone": "13800138000",
                    "province": "上海市",
                    "city": "上海市",
                    "district": "浦东新区",
                    "detailAddress": "被黑客修改的地址"
                },
                user_type="user_b"
            )
            
            assert update_address_response.status_code in [403, 404], \
                f"用户B不应能修改用户A的地址，实际状态码: {update_address_response.status_code}"
            
            delete_pet_response = http_client.delete(
                f"/api/user/pets/{pet_a['id']}",
                user_type="user_b"
            )
            
            assert delete_pet_response.status_code in [403, 404], \
                f"用户B不应能删除用户A的宠物，实际状态码: {delete_pet_response.status_code}"
            
        finally:
            generator_a.cleanup_test_data()
            generator_b.cleanup_test_data()

    @allure.story("数据修改隔离")
    @allure.title("测试商家无法修改其他商家的数据")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_merchant_cannot_modify_other_merchant_data(self, http_client: HTTPClient):
        """
        测试商家无法修改其他商家的数据
        """
        log_test_case("test_merchant_cannot_modify_other_merchant_data", "测试商家数据修改隔离")
        
        merchant_gen_a = TestDataGenerator(http_client)
        merchant_gen_b = TestDataGenerator(http_client)
        
        try:
            merchant_a = merchant_gen_a.create_test_merchant()
            assert merchant_a, "创建商家A失败"
            
            merchant_b = merchant_gen_b.create_test_merchant()
            assert merchant_b, "创建商家B失败"
            
            service_a = merchant_gen_a.create_test_service(merchant_a["id"])
            assert service_a, "创建商家A服务失败"
            
            product_a = merchant_gen_a.create_test_product(merchant_a["id"])
            assert product_a, "创建商家A商品失败"
            
            http_client.token_manager.set_token("merchant_b", merchant_b["token"])
            
            update_service_response = http_client.put(
                f"/api/merchant/services/{service_a['id']}",
                json_data={
                    "name": "hacked_service",
                    "description": "被其他商家修改",
                    "price": 0.01,
                    "duration": 1
                },
                user_type="merchant_b"
            )
            
            assert update_service_response.status_code in [403, 404], \
                f"商家B不应能修改商家A的服务，实际状态码: {update_service_response.status_code}"
            
            update_product_response = http_client.put(
                f"/api/merchant/products/{product_a['id']}",
                json_data={
                    "name": "hacked_product",
                    "description": "被其他商家修改",
                    "price": 0.01,
                    "stock": 99999
                },
                user_type="merchant_b"
            )
            
            assert update_product_response.status_code in [403, 404], \
                f"商家B不应能修改商家A的商品，实际状态码: {update_product_response.status_code}"
            
            delete_service_response = http_client.delete(
                f"/api/merchant/services/{service_a['id']}",
                user_type="merchant_b"
            )
            
            assert delete_service_response.status_code in [403, 404], \
                f"商家B不应能删除商家A的服务，实际状态码: {delete_service_response.status_code}"
            
            delete_product_response = http_client.delete(
                f"/api/merchant/products/{product_a['id']}",
                user_type="merchant_b"
            )
            
            assert delete_product_response.status_code in [403, 404], \
                f"商家B不应能删除商家A的商品，实际状态码: {delete_product_response.status_code}"
            
        finally:
            merchant_gen_a.cleanup_test_data()
            merchant_gen_b.cleanup_test_data()


@allure.feature("数据隔离")
@pytest.mark.security
@pytest.mark.isolation
class TestCrossRoleDataAccess:
    """跨角色数据访问测试"""

    @allure.story("跨角色访问")
    @allure.title("测试商家无法以用户身份访问用户数据")
    @allure.severity(allure.severity_level.NORMAL)
    def test_merchant_cannot_access_user_data_as_user(self, http_client: HTTPClient):
        """
        测试商家无法以用户身份访问用户私有数据
        """
        log_test_case("test_merchant_cannot_access_user_data_as_user", "测试跨角色数据访问")
        
        user_gen = TestDataGenerator(http_client)
        merchant_gen = TestDataGenerator(http_client)
        
        try:
            user = user_gen.create_test_user()
            assert user, "创建用户失败"
            
            merchant = merchant_gen.create_test_merchant()
            assert merchant, "创建商家失败"
            
            pet = user_gen.create_test_pet(user["id"])
            assert pet, "创建宠物失败"
            
            http_client.token_manager.set_token("merchant", merchant["token"])
            
            response = http_client.get(
                f"/api/user/pets/{pet['id']}",
                user_type="merchant"
            )
            
            assert response.status_code in [401, 403, 404], \
                f"商家不应能以用户身份访问用户宠物，实际状态码: {response.status_code}"
            
        finally:
            user_gen.cleanup_test_data()
            merchant_gen.cleanup_test_data()

    @allure.story("跨角色访问")
    @allure.title("测试用户无法查看其他用户的敏感信息")
    @allure.severity(allure.severity_level.NORMAL)
    def test_user_cannot_see_other_user_sensitive_info(self, http_client: HTTPClient):
        """
        测试用户无法查看其他用户的敏感信息（如密码、完整手机号等）
        """
        log_test_case("test_user_cannot_see_other_user_sensitive_info", "测试敏感信息保护")
        
        generator_a = TestDataGenerator(http_client)
        generator_b = TestDataGenerator(http_client)
        
        try:
            user_a = generator_a.create_test_user()
            assert user_a, "创建用户A失败"
            
            user_b = generator_b.create_test_user()
            assert user_b, "创建用户B失败"
            
            http_client.token_manager.set_token("user_b", user_b["token"])
            
            response = http_client.get("/api/user/profile", user_type="user_b")
            
            assert response.status_code == 200, "获取用户B个人信息失败"
            
            profile_data = response.json()
            
            if "data" in profile_data:
                user_data = profile_data["data"]
                
                assert "password" not in user_data or user_data["password"] is None, \
                    "响应中不应包含密码字段"
                
                if "phone" in user_data and user_data["phone"]:
                    phone = user_data["phone"]
                    assert phone != user_a.get("phone"), \
                        "不应能看到其他用户的完整手机号"
            
            log_response_details(response)
            
        finally:
            generator_a.cleanup_test_data()
            generator_b.cleanup_test_data()

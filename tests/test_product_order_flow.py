"""
商品订单流程测试
测试商品订单的状态流转和业务逻辑
"""
import pytest
import allure
from datetime import datetime
from tests.utils import HTTPClient, TokenManager, log_response_details
from tests.fixtures.test_data import TestDataGenerator, setup_test_environment, teardown_test_environment, TestData


@allure.feature("商品订单模块")
@pytest.mark.product_order
class TestProductOrderFlow:
    """商品订单流程测试"""
    
    @pytest.fixture(scope="class")
    def test_data(self, http_client: HTTPClient):
        """准备测试数据"""
        test_data = setup_test_environment(
            create_services=False,
            create_products=True,
            create_pets=False,
            create_addresses=True,
            create_appointments=False,
            create_orders=False
        )
        
        yield test_data
        
        teardown_test_environment(test_data)
    
    @allure.story("创建订单")
    @allure.title("测试创建商品订单")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.user
    def test_create_product_order(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建商品订单
        
        Steps:
        1. 用户创建订单
        2. 验证状态为pending
        3. 验证返回订单ID和订单编号
        """
        if not test_data.product_ids or not test_data.address_ids:
            pytest.skip("缺少必要的商品或地址数据")
        
        product_id = test_data.product_ids[0]
        address_id = test_data.address_ids[0]
        
        order_data = {
            "items": [
                {
                    "productId": product_id,
                    "quantity": 2
                }
            ],
            "addressId": address_id,
            "notes": "测试商品订单备注"
        }
        
        response = http_client.post(
            "/api/user/orders",
            json_data=order_data,
            user_type="user"
        )
        
        assert response.status_code in [200, 201], \
            f"创建订单失败，状态码: {response.status_code}"
        
        response_data = response.json()
        assert "data" in response_data, "响应中没有data字段"
        
        order_id = response_data.get("data", {}).get("id") or \
                   response_data.get("data", {}).get("orderId")
        
        assert order_id is not None, "返回的订单ID为空"
        
        order_no = response_data.get("data", {}).get("orderNo")
        assert order_no is not None, "返回的订单编号为空"
        
        order_status = response_data.get("data", {}).get("status")
        assert order_status == "pending", \
            f"订单状态应为pending，实际为: {order_status}"
        
        test_data.order_ids.append(order_id)
        
        log_response_details(response)
    
    @allure.story("支付订单")
    @allure.title("测试用户支付订单")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.user
    def test_user_pay_order(self, http_client: HTTPClient, test_data: TestData):
        """
        测试用户支付订单
        
        Steps:
        1. 用户支付pending状态的订单
        2. 验证状态变为paid
        3. 验证支付时间和交易ID
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_data = {
            "payMethod": "wechat",
            "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
        }
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data=pay_data,
            user_type="user"
        )
        
        assert response.status_code == 200, \
            f"用户支付订单失败，状态码: {response.status_code}"
        
        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")
        
        assert updated_status == "paid", \
            f"订单状态应为paid，实际为: {updated_status}"
        
        paid_at = response_data.get("data", {}).get("paidAt")
        assert paid_at is not None, "支付时间为空"
        
        transaction_id = response_data.get("data", {}).get("transactionId")
        assert transaction_id is not None, "交易ID为空"
        
        log_response_details(response)
    
    @allure.story("商家发货")
    @allure.title("测试商家发货")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_ship_order(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家发货
        
        Steps:
        1. 商家发货paid状态的订单
        2. 填写物流公司和物流单号
        3. 验证状态变为shipped
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        
        assert pay_response.status_code == 200, "支付订单失败"
        
        ship_data = {
            "logisticsCompany": "顺丰速运",
            "logisticsNumber": f"SF{datetime.now().strftime('%Y%m%d%H%M%S')}"
        }
        
        response = http_client.put(
            f"/api/merchant/orders/{order_id}/ship",
            json_data=ship_data,
            user_type="merchant"
        )
        
        assert response.status_code == 200, \
            f"商家发货失败，状态码: {response.status_code}"
        
        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")
        
        assert updated_status == "shipped", \
            f"订单状态应为shipped，实际为: {updated_status}"
        
        shipped_at = response_data.get("data", {}).get("shippedAt")
        assert shipped_at is not None, "发货时间为空"
        
        logistics_company = response_data.get("data", {}).get("logisticsCompany")
        assert logistics_company is not None, "物流公司为空"
        
        logistics_number = response_data.get("data", {}).get("logisticsNumber")
        assert logistics_number is not None, "物流单号为空"
        
        log_response_details(response)
    
    @allure.story("确认收货")
    @allure.title("测试用户确认收货")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.user
    def test_user_confirm_receipt(self, http_client: HTTPClient, test_data: TestData):
        """
        测试用户确认收货
        
        Steps:
        1. 用户确认收货shipped状态的订单
        2. 验证状态变为completed
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        assert pay_response.status_code == 200, "支付订单失败"
        
        ship_response = http_client.put(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={
                "logisticsCompany": "顺丰速运",
                "logisticsNumber": f"SF{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="merchant"
        )
        assert ship_response.status_code == 200, "发货失败"
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/confirm",
            user_type="user"
        )
        
        assert response.status_code == 200, \
            f"用户确认收货失败，状态码: {response.status_code}"
        
        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")
        
        assert updated_status == "completed", \
            f"订单状态应为completed，实际为: {updated_status}"
        
        completed_at = response_data.get("data", {}).get("completedAt")
        assert completed_at is not None, "完成时间为空"
        
        log_response_details(response)
    
    @allure.story("取消订单")
    @allure.title("测试用户取消pending订单")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.user
    def test_user_cancel_pending_order(self, http_client: HTTPClient, test_data: TestData):
        """
        测试用户取消pending订单
        
        Steps:
        1. 用户取消pending状态的订单
        2. 验证状态变为cancelled
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/cancel",
            user_type="user"
        )
        
        assert response.status_code == 200, \
            f"用户取消订单失败，状态码: {response.status_code}"
        
        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")
        
        assert updated_status == "cancelled", \
            f"订单状态应为cancelled，实际为: {updated_status}"
        
        cancelled_at = response_data.get("data", {}).get("cancelledAt")
        assert cancelled_at is not None, "取消时间为空"
        
        log_response_details(response)
    
    @allure.story("取消订单")
    @allure.title("测试商家取消paid订单")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_cancel_paid_order(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家取消paid订单
        
        Steps:
        1. 商家取消paid状态的订单
        2. 验证状态变为cancelled
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        
        assert pay_response.status_code == 200, "支付订单失败"
        
        response = http_client.put(
            f"/api/merchant/orders/{order_id}/cancel",
            json_data={"reason": "缺货退款"},
            user_type="merchant"
        )
        
        assert response.status_code == 200, \
            f"商家取消订单失败，状态码: {response.status_code}"
        
        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")
        
        assert updated_status == "cancelled", \
            f"订单状态应为cancelled，实际为: {updated_status}"
        
        cancelled_at = response_data.get("data", {}).get("cancelledAt")
        assert cancelled_at is not None, "取消时间为空"
        
        log_response_details(response)
    
    @allure.story("非法状态转换")
    @allure.title("测试非法状态转换 - 已完成")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_status_transition_completed(self, http_client: HTTPClient, test_data: TestData):
        """
        测试非法状态转换（已完成）
        
        Steps:
        1. 尝试修改completed状态的订单
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        assert pay_response.status_code == 200, "支付订单失败"
        
        ship_response = http_client.put(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={
                "logisticsCompany": "顺丰速运",
                "logisticsNumber": f"SF{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="merchant"
        )
        assert ship_response.status_code == 200, "发货失败"
        
        confirm_response = http_client.put(
            f"/api/user/orders/{order_id}/confirm",
            user_type="user"
        )
        assert confirm_response.status_code == 200, "确认收货失败"
        
        response = http_client.put(
            f"/api/merchant/orders/{order_id}/cancel",
            json_data={"reason": "测试取消"},
            user_type="merchant"
        )
        
        assert response.status_code in [400, 403, 409], \
            f"预期状态码400/403/409，实际状态码: {response.status_code}"
        
        response_data = response.json()
        assert "message" in response_data or "error" in response_data, \
            "响应中没有错误信息"
        
        log_response_details(response)
    
    @allure.story("非法状态转换")
    @allure.title("测试非法状态转换 - 已取消")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_status_transition_cancelled(self, http_client: HTTPClient, test_data: TestData):
        """
        测试非法状态转换（已取消）
        
        Steps:
        1. 尝试修改cancelled状态的订单
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        cancel_response = http_client.put(
            f"/api/user/orders/{order_id}/cancel",
            user_type="user"
        )
        assert cancel_response.status_code == 200, "取消订单失败"
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        
        assert response.status_code in [400, 403, 409], \
            f"预期状态码400/403/409，实际状态码: {response.status_code}"
        
        response_data = response.json()
        assert "message" in response_data or "error" in response_data, \
            "响应中没有错误信息"
        
        log_response_details(response)
    
    @allure.story("完整流程")
    @allure.title("测试完整订单流程")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_complete_product_order_flow(self, http_client: HTTPClient, test_data: TestData):
        """
        测试完整订单流程
        
        Steps:
        1. 创建订单 → pending
        2. 用户支付 → paid
        3. 商家发货 → shipped
        4. 用户确认收货 → completed
        5. 验证每个步骤的状态转换
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        get_response = http_client.get(
            f"/api/user/orders/{order_id}",
            user_type="user"
        )
        
        if get_response.status_code == 200:
            initial_status = get_response.json().get("data", {}).get("status")
            assert initial_status == "pending", \
                f"初始状态应为pending，实际为: {initial_status}"
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        
        assert pay_response.status_code == 200, "支付订单失败"
        paid_status = pay_response.json().get("data", {}).get("status")
        assert paid_status == "paid", \
            f"支付后状态应为paid，实际为: {paid_status}"
        
        ship_response = http_client.put(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={
                "logisticsCompany": "顺丰速运",
                "logisticsNumber": f"SF{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="merchant"
        )
        
        assert ship_response.status_code == 200, "发货失败"
        shipped_status = ship_response.json().get("data", {}).get("status")
        assert shipped_status == "shipped", \
            f"发货后状态应为shipped，实际为: {shipped_status}"
        
        confirm_response = http_client.put(
            f"/api/user/orders/{order_id}/confirm",
            user_type="user"
        )
        
        assert confirm_response.status_code == 200, "确认收货失败"
        completed_status = confirm_response.json().get("data", {}).get("status")
        assert completed_status == "completed", \
            f"确认收货后状态应为completed，实际为: {completed_status}"
        
        log_response_details(confirm_response)


@allure.feature("商品订单模块")
@pytest.mark.product_order
class TestProductOrderDataIsolation:
    """商品订单数据隔离测试"""
    
    @pytest.fixture(scope="class")
    def test_data(self, http_client: HTTPClient):
        """准备测试数据"""
        test_data = setup_test_environment(
            create_services=False,
            create_products=True,
            create_pets=False,
            create_addresses=True,
            create_appointments=False,
            create_orders=False
        )
        
        yield test_data
        
        teardown_test_environment(test_data)
    
    @allure.story("数据隔离")
    @allure.title("测试用户只能操作自己的订单")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_user_can_only_operate_own_orders(self, http_client: HTTPClient, test_data: TestData):
        """
        测试用户只能操作自己的订单
        
        Steps:
        1. 创建另一个用户
        2. 第一个用户创建订单
        3. 第二个用户尝试操作第一个用户的订单
        4. 验证操作失败
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        other_user = generator.create_test_user()
        if not other_user:
            pytest.skip("无法创建第二个测试用户")
        
        http_client.token_manager.set_token("user", other_user.get("token"))
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/cancel",
            user_type="user"
        )
        
        assert response.status_code in [403, 404], \
            f"预期状态码403或404，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("数据隔离")
    @allure.title("测试商家只能操作自己的订单")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_merchant_can_only_operate_own_orders(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家只能操作自己的订单
        
        Steps:
        1. 创建另一个商家
        2. 第一个商家的店铺创建订单
        3. 第二个商家尝试操作第一个商家的订单
        4. 验证操作失败
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        assert pay_response.status_code == 200, "支付订单失败"
        
        other_merchant = generator.create_test_merchant()
        if not other_merchant:
            pytest.skip("无法创建第二个测试商家")
        
        http_client.token_manager.set_token("merchant", other_merchant.get("token"))
        
        response = http_client.put(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={
                "logisticsCompany": "顺丰速运",
                "logisticsNumber": f"SF{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="merchant"
        )
        
        assert response.status_code in [403, 404], \
            f"预期状态码403或404，实际状态码: {response.status_code}"
        
        log_response_details(response)


@allure.feature("商品订单模块")
@pytest.mark.product_order
class TestProductOrderEdgeCases:
    """商品订单边界情况测试"""
    
    @pytest.fixture(scope="class")
    def test_data(self, http_client: HTTPClient):
        """准备测试数据"""
        test_data = setup_test_environment(
            create_services=False,
            create_products=True,
            create_pets=False,
            create_addresses=True,
            create_appointments=False,
            create_orders=False
        )
        
        yield test_data
        
        teardown_test_environment(test_data)
    
    @allure.story("边界情况")
    @allure.title("测试创建订单 - 不存在的商品")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_nonexistent_product(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建订单 - 不存在的商品
        
        Steps:
        1. 使用不存在的商品ID创建订单
        2. 验证返回错误
        """
        if not test_data.address_ids:
            pytest.skip("缺少地址数据")
        
        order_data = {
            "items": [
                {
                    "productId": 99999,
                    "quantity": 1
                }
            ],
            "addressId": test_data.address_ids[0],
            "notes": "测试订单"
        }
        
        response = http_client.post(
            "/api/user/orders",
            json_data=order_data,
            user_type="user"
        )
        
        assert response.status_code in [400, 404], \
            f"预期状态码400或404，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试创建订单 - 不存在的地址")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_nonexistent_address(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建订单 - 不存在的地址
        
        Steps:
        1. 使用不存在的地址ID创建订单
        2. 验证返回错误
        """
        if not test_data.product_ids:
            pytest.skip("缺少商品数据")
        
        order_data = {
            "items": [
                {
                    "productId": test_data.product_ids[0],
                    "quantity": 1
                }
            ],
            "addressId": 99999,
            "notes": "测试订单"
        }
        
        response = http_client.post(
            "/api/user/orders",
            json_data=order_data,
            user_type="user"
        )
        
        assert response.status_code in [400, 404], \
            f"预期状态码400或404，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试创建订单 - 数量为0")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_zero_quantity(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建订单 - 数量为0
        
        Steps:
        1. 使用数量为0创建订单
        2. 验证返回错误
        """
        if not test_data.product_ids or not test_data.address_ids:
            pytest.skip("缺少必要的商品或地址数据")
        
        order_data = {
            "items": [
                {
                    "productId": test_data.product_ids[0],
                    "quantity": 0
                }
            ],
            "addressId": test_data.address_ids[0],
            "notes": "测试订单"
        }
        
        response = http_client.post(
            "/api/user/orders",
            json_data=order_data,
            user_type="user"
        )
        
        assert response.status_code in [400, 422], \
            f"预期状态码400或422，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试创建订单 - 缺少必填字段")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_missing_fields(self, http_client: HTTPClient):
        """
        测试创建订单 - 缺少必填字段
        
        Steps:
        1. 不提供商品列表创建订单
        2. 验证返回错误
        """
        order_data = {
            "addressId": 1,
            "notes": "测试订单"
        }
        
        response = http_client.post(
            "/api/user/orders",
            json_data=order_data,
            user_type="user"
        )
        
        assert response.status_code in [400, 422], \
            f"预期状态码400或422，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试支付订单 - 非pending状态")
    @allure.severity(allure.severity_level.NORMAL)
    def test_pay_order_not_pending(self, http_client: HTTPClient, test_data: TestData):
        """
        测试支付订单 - 非pending状态
        
        Steps:
        1. 支付已支付的订单
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        assert pay_response.status_code == 200, "第一次支付失败"
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "alipay",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        
        assert response.status_code in [400, 403, 409], \
            f"预期状态码400/403/409，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试发货 - 非paid状态")
    @allure.severity(allure.severity_level.NORMAL)
    def test_ship_order_not_paid(self, http_client: HTTPClient, test_data: TestData):
        """
        测试发货 - 非paid状态
        
        Steps:
        1. 发货pending状态的订单
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        response = http_client.put(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={
                "logisticsCompany": "顺丰速运",
                "logisticsNumber": f"SF{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="merchant"
        )
        
        assert response.status_code in [400, 403, 409], \
            f"预期状态码400/403/409，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试确认收货 - 非shipped状态")
    @allure.severity(allure.severity_level.NORMAL)
    def test_confirm_order_not_shipped(self, http_client: HTTPClient, test_data: TestData):
        """
        测试确认收货 - 非shipped状态
        
        Steps:
        1. 确认收货paid状态的订单
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data
        
        order = generator.create_test_order()
        if not order:
            pytest.skip("无法创建测试订单")
        
        order_id = order.get("id")
        
        pay_response = http_client.put(
            f"/api/user/orders/{order_id}/pay",
            json_data={
                "payMethod": "wechat",
                "transactionId": f"TXN{datetime.now().strftime('%Y%m%d%H%M%S')}"
            },
            user_type="user"
        )
        assert pay_response.status_code == 200, "支付订单失败"
        
        response = http_client.put(
            f"/api/user/orders/{order_id}/confirm",
            user_type="user"
        )
        
        assert response.status_code in [400, 403, 409], \
            f"预期状态码400/403/409，实际状态码: {response.status_code}"
        
        log_response_details(response)
    
    @allure.story("边界情况")
    @allure.title("测试创建订单 - 多个商品")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_multiple_products(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建订单 - 多个商品
        
        Steps:
        1. 创建包含多个商品的订单
        2. 验证订单创建成功
        3. 验证订单明细正确
        """
        if len(test_data.product_ids) < 2 or not test_data.address_ids:
            pytest.skip("缺少足够的商品或地址数据")
        
        order_data = {
            "items": [
                {
                    "productId": test_data.product_ids[0],
                    "quantity": 2
                },
                {
                    "productId": test_data.product_ids[1],
                    "quantity": 1
                }
            ],
            "addressId": test_data.address_ids[0],
            "notes": "测试多商品订单"
        }
        
        response = http_client.post(
            "/api/user/orders",
            json_data=order_data,
            user_type="user"
        )
        
        assert response.status_code in [200, 201], \
            f"创建多商品订单失败，状态码: {response.status_code}"
        
        response_data = response.json()
        order_id = response_data.get("data", {}).get("id") or \
                   response_data.get("data", {}).get("orderId")
        
        assert order_id is not None, "返回的订单ID为空"
        
        test_data.order_ids.append(order_id)
        
        log_response_details(response)

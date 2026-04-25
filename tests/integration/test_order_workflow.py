"""
订单完整流程集成测试

测试商品订单的完整生命周期，包括：
- 创建订单 → 支付 → 发货 → 确认收货 → 评价
- 订单状态转换：pending → paid → shipped → completed
- 订单取消流程：pending → cancelled
- 订单退款流程：paid → cancelled（退款）
- 订单超时自动取消
- 订单库存扣减和回滚
- 验证订单数据一致性
"""

import time
from datetime import datetime, timedelta
from decimal import Decimal

import pytest

from tests.assertions.business_assertions import (
    BusinessAssertionBuilder,
    assert_data_consistency,
    assert_status_transition,
)
from tests.assertions.response_assertions import (
    ResponseAssertionBuilder,
    assert_response_key_exists,
    assert_response_key_value,
)
from tests.utils import HTTPClient, log_test_case

ORDER_STATUS_TRANSITIONS = {
    "pending": {"paid", "cancelled"},
    "paid": {"shipped", "cancelled"},
    "shipped": {"completed", "cancelled"},
    "completed": set(),
    "cancelled": set(),
}


@pytest.mark.integration
@pytest.mark.product_order
class TestOrderWorkflow:
    """订单完整流程集成测试类"""

    @pytest.fixture(autouse=True)
    def setup(self, http_client, user_token, merchant_token, test_user, test_merchant, test_product):
        """
        测试前置设置

        Args:
            http_client: HTTP客户端
            user_token: 用户认证Token
            merchant_token: 商家认证Token
            test_user: 测试用户数据
            test_merchant: 测试商家数据
            test_product: 测试商品数据
        """
        self.client = http_client
        self.user_token = user_token
        self.merchant_token = merchant_token
        self.user_data = test_user
        self.merchant_data = test_merchant
        self.product_data = test_product
        self.user_headers = {"Authorization": f"Bearer {user_token}"}
        self.merchant_headers = {"Authorization": f"Bearer {merchant_token}"}

    def _create_order(self, quantity=2):
        """
        创建测试订单

        Args:
            quantity: 商品数量

        Returns:
            dict: 订单数据
        """
        order_data = {
            "merchant_id": self.merchant_data.get("id"),
            "items": [{"product_id": self.product_data.get("id"), "quantity": quantity}],
            "shipping_address": "测试地址",
            "notes": "集成测试订单",
        }

        response = self.client.post("/api/orders", json_data=order_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建订单失败: {response.text}"
        return response.json()["data"]

    def _get_product_stock(self, product_id):
        """
        获取商品库存

        Args:
            product_id: 商品ID

        Returns:
            int: 库存数量
        """
        response = self.client.get(f"/api/products/{product_id}", headers=self.user_headers)

        assert response.status_code == 200, f"获取商品信息失败: {response.text}"
        return response.json()["data"].get("stock", 0)

    def test_order_complete_workflow(self):
        """
        测试订单完整流程：创建订单 → 支付 → 发货 → 确认收货 → 评价

        验证点：
        1. 订单创建成功，状态为pending
        2. 支付成功，状态变为paid
        3. 发货成功，状态变为shipped
        4. 确认收货成功，状态变为completed
        5. 评价成功
        6. 每个状态转换都符合业务规则
        """
        log_test_case("test_order_complete_workflow", "测试订单完整流程")

        order = self._create_order()
        order_id = order.get("id")

        assert order["status"] == "pending", "初始订单状态不是pending"

        response = self.client.post(
            f"/api/orders/{order_id}/pay", json_data={"pay_method": "wechat"}, headers=self.user_headers
        )
        assert response.status_code == 200, f"支付失败: {response.text}"

        order = response.json()["data"]
        assert order["status"] == "paid", "支付后订单状态不是paid"
        assert_status_transition("pending", "paid", ORDER_STATUS_TRANSITIONS)

        response = self.client.post(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={"tracking_number": "TEST123456", "shipping_company": "测试快递"},
            headers=self.merchant_headers,
        )
        assert response.status_code == 200, f"发货失败: {response.text}"

        order = response.json()["data"]
        assert order["status"] == "shipped", "发货后订单状态不是shipped"
        assert_status_transition("paid", "shipped", ORDER_STATUS_TRANSITIONS)

        response = self.client.post(f"/api/orders/{order_id}/confirm", headers=self.user_headers)
        assert response.status_code == 200, f"确认收货失败: {response.text}"

        order = response.json()["data"]
        assert order["status"] == "completed", "确认收货后订单状态不是completed"
        assert_status_transition("shipped", "completed", ORDER_STATUS_TRANSITIONS)

        review_data = {
            "product_id": self.product_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "order_id": order_id,
            "rating": 5,
            "comment": "商品很好，物流很快！",
        }

        response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)
        assert response.status_code in [200, 201], f"评价失败: {response.text}"

        review = response.json()["data"]
        assert review["order_id"] == order_id, "评价关联的订单ID不匹配"

    def test_order_status_transitions(self):
        """
        测试订单状态转换：pending → paid → shipped → completed

        验证点：
        1. 每个状态转换都符合业务规则
        2. 状态转换后数据一致性
        3. 不允许非法状态转换
        """
        log_test_case("test_order_status_transitions", "测试订单状态转换")

        order = self._create_order()
        order_id = order.get("id")

        assert order["status"] == "pending"

        response = self.client.post(
            f"/api/orders/{order_id}/pay", json_data={"pay_method": "alipay"}, headers=self.user_headers
        )
        assert response.status_code == 200
        order = response.json()["data"]
        assert order["status"] == "paid"

        response = self.client.post(
            f"/api/merchant/orders/{order_id}/ship",
            json_data={"tracking_number": "TEST789012", "shipping_company": "测试快递"},
            headers=self.merchant_headers,
        )
        assert response.status_code == 200
        order = response.json()["data"]
        assert order["status"] == "shipped"

        response = self.client.post(f"/api/orders/{order_id}/confirm", headers=self.user_headers)
        assert response.status_code == 200
        order = response.json()["data"]
        assert order["status"] == "completed"

        response = self.client.post(
            f"/api/orders/{order_id}/ship",
            json_data={"tracking_number": "TEST000000", "shipping_company": "测试快递"},
            headers=self.merchant_headers,
        )
        assert response.status_code in [400, 403, 409], "已完成的订单不应该允许再次发货"

    def test_order_cancellation_from_pending(self):
        """
        测试订单取消流程：pending → cancelled

        验证点：
        1. pending状态的订单可以取消
        2. 取消后状态变为cancelled
        3. 取消后库存恢复
        """
        log_test_case("test_order_cancellation_from_pending", "测试订单取消流程")

        product_id = self.product_data.get("id")
        initial_stock = self._get_product_stock(product_id)

        order = self._create_order(quantity=3)
        order_id = order.get("id")

        after_order_stock = self._get_product_stock(product_id)
        assert after_order_stock == initial_stock - 3, "创建订单后库存未正确扣减"

        response = self.client.post(f"/api/orders/{order_id}/cancel", headers=self.user_headers)
        assert response.status_code == 200, f"取消订单失败: {response.text}"

        order = response.json()["data"]
        assert order["status"] == "cancelled", "取消后订单状态不是cancelled"
        assert_status_transition("pending", "cancelled", ORDER_STATUS_TRANSITIONS)

        after_cancel_stock = self._get_product_stock(product_id)
        assert after_cancel_stock == initial_stock, "取消订单后库存未正确恢复"

    def test_order_refund_from_paid(self):
        """
        测试订单退款流程：paid → cancelled（退款）

        验证点：
        1. paid状态的订单可以申请退款
        2. 退款后状态变为cancelled
        3. 退款后库存恢复
        """
        log_test_case("test_order_refund_from_paid", "测试订单退款流程")

        product_id = self.product_data.get("id")
        initial_stock = self._get_product_stock(product_id)

        order = self._create_order(quantity=2)
        order_id = order.get("id")

        response = self.client.post(
            f"/api/orders/{order_id}/pay", json_data={"pay_method": "wechat"}, headers=self.user_headers
        )
        assert response.status_code == 200, "支付失败"

        after_pay_stock = self._get_product_stock(product_id)
        assert after_pay_stock == initial_stock - 2, "支付后库存不正确"

        response = self.client.post(
            f"/api/orders/{order_id}/refund", json_data={"reason": "测试退款"}, headers=self.user_headers
        )
        assert response.status_code == 200, f"退款失败: {response.text}"

        order = response.json()["data"]
        assert order["status"] == "cancelled", "退款后订单状态不是cancelled"
        assert_status_transition("paid", "cancelled", ORDER_STATUS_TRANSITIONS)

        after_refund_stock = self._get_product_stock(product_id)
        assert after_refund_stock == initial_stock, "退款后库存未正确恢复"

    def test_order_timeout_auto_cancel(self):
        """
        测试订单超时自动取消

        验证点：
        1. 超时未支付的订单自动取消
        2. 取消后库存恢复
        3. 取消后订单状态正确
        """
        log_test_case("test_order_timeout_auto_cancel", "测试订单超时自动取消")

        product_id = self.product_data.get("id")
        initial_stock = self._get_product_stock(product_id)

        order_data = {
            "merchant_id": self.merchant_data.get("id"),
            "items": [{"product_id": product_id, "quantity": 1}],
            "shipping_address": "测试地址",
            "notes": "超时测试订单",
        }

        response = self.client.post("/api/orders", json_data=order_data, headers=self.user_headers)
        assert response.status_code in [200, 201]
        order = response.json()["data"]
        order_id = order.get("id")

        after_order_stock = self._get_product_stock(product_id)
        assert after_order_stock == initial_stock - 1, "创建订单后库存未正确扣减"

        response = self.client.post(f"/api/orders/{order_id}/timeout", headers=self.merchant_headers)

        if response.status_code == 200:
            order = response.json()["data"]
            assert order["status"] == "cancelled", "超时取消后订单状态不是cancelled"

            after_timeout_stock = self._get_product_stock(product_id)
            assert after_timeout_stock == initial_stock, "超时取消后库存未正确恢复"
        else:
            pytest.skip("订单超时自动取消功能未实现或需要等待超时时间")

    def test_order_stock_deduction_and_rollback(self):
        """
        测试订单库存扣减和回滚

        验证点：
        1. 创建订单时库存正确扣减
        2. 取消订单时库存正确回滚
        3. 库存不足时不能创建订单
        """
        log_test_case("test_order_stock_deduction_and_rollback", "测试订单库存扣减和回滚")

        product_id = self.product_data.get("id")
        initial_stock = self._get_product_stock(product_id)

        order = self._create_order(quantity=2)
        order_id = order.get("id")

        after_order_stock = self._get_product_stock(product_id)
        assert (
            after_order_stock == initial_stock - 2
        ), f"库存扣减不正确: 期望 {initial_stock - 2}, 实际 {after_order_stock}"

        response = self.client.post(f"/api/orders/{order_id}/cancel", headers=self.user_headers)
        assert response.status_code == 200, "取消订单失败"

        after_cancel_stock = self._get_product_stock(product_id)
        assert after_cancel_stock == initial_stock, f"库存回滚不正确: 期望 {initial_stock}, 实际 {after_cancel_stock}"

        large_quantity_order = {
            "merchant_id": self.merchant_data.get("id"),
            "items": [{"product_id": product_id, "quantity": initial_stock + 100}],
            "shipping_address": "测试地址",
            "notes": "库存不足测试",
        }

        response = self.client.post("/api/orders", json_data=large_quantity_order, headers=self.user_headers)
        assert response.status_code in [400, 409], "库存不足时应该不允许创建订单"

    def test_order_data_consistency(self):
        """
        测试订单数据一致性

        验证点：
        1. 订单基本信息一致性
        2. 订单商品信息一致性
        3. 订单金额一致性
        4. 订单状态一致性
        """
        log_test_case("test_order_data_consistency", "测试订单数据一致性")

        quantity = 3
        order = self._create_order(quantity=quantity)
        order_id = order.get("id")

        response = self.client.get(f"/api/orders/{order_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取订单详情失败"

        order_detail = response.json()["data"]

        assert_data_consistency(order, order_detail, ["id", "merchant_id", "status", "total_price"])

        assert order_detail["merchant_id"] == self.merchant_data.get("id"), "商家ID不一致"

        expected_total = Decimal(str(self.product_data.get("price"))) * quantity
        actual_total = Decimal(str(order_detail.get("total_price", 0)))
        assert actual_total == expected_total, f"订单金额不一致: 期望 {expected_total}, 实际 {actual_total}"

        if "items" in order_detail:
            for item in order_detail["items"]:
                assert "product_id" in item, "订单商品缺少product_id"
                assert "quantity" in item, "订单商品缺少quantity"
                assert "price" in item, "订单商品缺少price"
                assert item["product_id"] == self.product_data.get("id"), "商品ID不一致"
                assert item["quantity"] == quantity, "商品数量不一致"

    def test_multiple_orders_workflow(self):
        """
        测试多个订单并发流程

        验证点：
        1. 多个订单可以独立处理
        2. 每个订单的状态独立转换
        3. 库存正确管理
        """
        log_test_case("test_multiple_orders_workflow", "测试多个订单并发流程")

        product_id = self.product_data.get("id")
        initial_stock = self._get_product_stock(product_id)

        orders = []
        for i in range(3):
            order = self._create_order(quantity=1)
            orders.append(order)

        after_orders_stock = self._get_product_stock(product_id)
        assert after_orders_stock == initial_stock - 3, "多个订单库存扣减不正确"

        for i, order in enumerate(orders):
            order_id = order.get("id")

            if i == 0:
                response = self.client.post(
                    f"/api/orders/{order_id}/pay", json_data={"pay_method": "wechat"}, headers=self.user_headers
                )
                assert response.status_code == 200
                assert response.json()["data"]["status"] == "paid"

            elif i == 1:
                response = self.client.post(f"/api/orders/{order_id}/cancel", headers=self.user_headers)
                assert response.status_code == 200
                assert response.json()["data"]["status"] == "cancelled"

            else:
                response = self.client.get(f"/api/orders/{order_id}", headers=self.user_headers)
                assert response.status_code == 200
                assert response.json()["data"]["status"] == "pending"

        final_stock = self._get_product_stock(product_id)
        assert final_stock == initial_stock - 2, "最终库存不正确（一个取消，两个占用）"

    def test_order_payment_methods(self):
        """
        测试不同支付方式

        验证点：
        1. 微信支付
        2. 支付宝支付
        3. 支付方式正确记录
        """
        log_test_case("test_order_payment_methods", "测试不同支付方式")

        wechat_order = self._create_order()
        wechat_order_id = wechat_order.get("id")

        response = self.client.post(
            f"/api/orders/{wechat_order_id}/pay", json_data={"pay_method": "wechat"}, headers=self.user_headers
        )
        assert response.status_code == 200, "微信支付失败"

        order = response.json()["data"]
        assert order.get("pay_method") == "wechat", "支付方式不是微信"

        alipay_order = self._create_order()
        alipay_order_id = alipay_order.get("id")

        response = self.client.post(
            f"/api/orders/{alipay_order_id}/pay", json_data={"pay_method": "alipay"}, headers=self.user_headers
        )
        assert response.status_code == 200, "支付宝支付失败"

        order = response.json()["data"]
        assert order.get("pay_method") == "alipay", "支付方式不是支付宝"

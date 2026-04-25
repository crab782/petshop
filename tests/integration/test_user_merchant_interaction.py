"""
用户商家交互集成测试

测试用户与商家之间的完整交互流程，包括：
- 用户浏览商家服务
- 用户预约商家服务
- 用户购买商家商品
- 用户对商家服务评价
- 用户对商家商品评价
- 商家查看用户订单
- 商家处理用户预约
- 商家回复用户评价
- 验证数据一致性
"""

from datetime import datetime, timedelta
from decimal import Decimal

import pytest

from tests.assertions.business_assertions import BusinessAssertionBuilder, assert_data_consistency
from tests.assertions.response_assertions import (
    ResponseAssertionBuilder,
    assert_response_key_exists,
    assert_response_key_value,
)
from tests.utils import HTTPClient, log_test_case


@pytest.mark.integration
@pytest.mark.user
@pytest.mark.merchant
class TestUserMerchantInteraction:
    """用户商家交互集成测试类"""

    @pytest.fixture(autouse=True)
    def setup(self, http_client, user_token, merchant_token, test_user, test_merchant, test_service, test_product):
        """
        测试前置设置

        Args:
            http_client: HTTP客户端
            user_token: 用户认证Token
            merchant_token: 商家认证Token
            test_user: 测试用户数据
            test_merchant: 测试商家数据
            test_service: 测试服务数据
            test_product: 测试商品数据
        """
        self.client = http_client
        self.user_token = user_token
        self.merchant_token = merchant_token
        self.user_data = test_user
        self.merchant_data = test_merchant
        self.service_data = test_service
        self.product_data = test_product
        self.user_headers = {"Authorization": f"Bearer {user_token}"}
        self.merchant_headers = {"Authorization": f"Bearer {merchant_token}"}

    def test_user_browse_merchant_services(self):
        """
        测试用户浏览商家服务

        验证点：
        1. 用户能成功获取商家服务列表
        2. 服务列表包含正确的商家信息
        3. 服务数据完整性
        """
        log_test_case("test_user_browse_merchant_services", "测试用户浏览商家服务")

        merchant_id = self.merchant_data.get("id")
        response = self.client.get(f"/api/services/merchant/{merchant_id}", headers=self.user_headers)

        assert response.status_code == 200, f"获取商家服务列表失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        services = json_data["data"]
        assert isinstance(services, list), "服务数据不是列表类型"

        for service in services:
            assert "id" in service, "服务缺少id字段"
            assert "name" in service, "服务缺少name字段"
            assert "price" in service, "服务缺少price字段"
            assert "merchant_id" in service, "服务缺少merchant_id字段"
            assert service["merchant_id"] == merchant_id, "服务商家ID不匹配"

    def test_user_book_merchant_service(self):
        """
        测试用户预约商家服务

        验证点：
        1. 用户能成功创建预约
        2. 预约状态正确
        3. 预约数据一致性
        """
        log_test_case("test_user_book_merchant_service", "测试用户预约商家服务")

        appointment_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_time": (datetime.now() + timedelta(days=1)).isoformat(),
            "notes": "集成测试预约",
        }

        response = self.client.post("/api/appointments", json_data=appointment_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建预约失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        appointment = json_data["data"]
        assert appointment["status"] == "pending", "预约状态不是pending"
        assert appointment["service_id"] == appointment_data["service_id"], "服务ID不匹配"
        assert appointment["merchant_id"] == appointment_data["merchant_id"], "商家ID不匹配"

        return appointment

    def test_user_purchase_merchant_product(self):
        """
        测试用户购买商家商品

        验证点：
        1. 用户能成功创建订单
        2. 订单状态正确
        3. 订单金额正确
        4. 订单数据一致性
        """
        log_test_case("test_user_purchase_merchant_product", "测试用户购买商家商品")

        order_data = {
            "merchant_id": self.merchant_data.get("id"),
            "items": [{"product_id": self.product_data.get("id"), "quantity": 2}],
            "shipping_address": "测试地址",
            "notes": "集成测试订单",
        }

        response = self.client.post("/api/orders", json_data=order_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建订单失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        order = json_data["data"]
        assert order["status"] == "pending", "订单状态不是pending"
        assert order["merchant_id"] == order_data["merchant_id"], "商家ID不匹配"

        expected_total = Decimal(str(self.product_data.get("price"))) * 2
        actual_total = Decimal(str(order.get("total_price", 0)))
        assert actual_total == expected_total, f"订单金额不匹配: 期望 {expected_total}, 实际 {actual_total}"

        return order

    def test_user_review_merchant_service(self):
        """
        测试用户对商家服务评价

        验证点：
        1. 用户能成功创建服务评价
        2. 评价数据完整性
        3. 评价关联正确
        """
        log_test_case("test_user_review_merchant_service", "测试用户对商家服务评价")

        appointment = self.test_user_book_merchant_service()

        review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": appointment.get("id"),
            "rating": 5,
            "comment": "服务非常好，强烈推荐！",
        }

        response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建评价失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        review = json_data["data"]
        assert review["rating"] == review_data["rating"], "评分不匹配"
        assert review["service_id"] == review_data["service_id"], "服务ID不匹配"
        assert review["merchant_id"] == review_data["merchant_id"], "商家ID不匹配"

        return review

    def test_user_review_merchant_product(self):
        """
        测试用户对商家商品评价

        验证点：
        1. 用户能成功创建商品评价
        2. 评价数据完整性
        3. 评价关联正确
        """
        log_test_case("test_user_review_merchant_product", "测试用户对商家商品评价")

        order = self.test_user_purchase_merchant_product()

        review_data = {
            "product_id": self.product_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "order_id": order.get("id"),
            "rating": 4,
            "comment": "商品质量不错，物流很快。",
        }

        response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建商品评价失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        review = json_data["data"]
        assert review["rating"] == review_data["rating"], "评分不匹配"

        return review

    def test_merchant_view_user_orders(self):
        """
        测试商家查看用户订单

        验证点：
        1. 商家能成功获取订单列表
        2. 订单列表包含正确的商家订单
        3. 订单数据完整性
        """
        log_test_case("test_merchant_view_user_orders", "测试商家查看用户订单")

        order = self.test_user_purchase_merchant_product()

        response = self.client.get("/api/merchant/orders", headers=self.merchant_headers)

        assert response.status_code == 200, f"获取商家订单列表失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        orders = json_data["data"]
        assert isinstance(orders, list), "订单数据不是列表类型"

        order_ids = [o.get("id") for o in orders]
        assert order.get("id") in order_ids, "创建的订单不在商家订单列表中"

    def test_merchant_handle_user_appointment(self):
        """
        测试商家处理用户预约

        验证点：
        1. 商家能成功确认预约
        2. 预约状态正确转换
        3. 预约数据一致性
        """
        log_test_case("test_merchant_handle_user_appointment", "测试商家处理用户预约")

        appointment = self.test_user_book_merchant_service()
        appointment_id = appointment.get("id")

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
        )

        assert response.status_code == 200, f"确认预约失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        updated_appointment = json_data["data"]
        assert updated_appointment["status"] == "confirmed", "预约状态不是confirmed"
        assert updated_appointment["id"] == appointment_id, "预约ID不匹配"

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/complete", headers=self.merchant_headers
        )

        assert response.status_code == 200, f"完成预约失败: {response.text}"

        json_data = response.json()
        completed_appointment = json_data["data"]
        assert completed_appointment["status"] == "completed", "预约状态不是completed"

    def test_merchant_reply_user_review(self):
        """
        测试商家回复用户评价

        验证点：
        1. 商家能成功回复评价
        2. 回复数据完整性
        3. 回复关联正确
        """
        log_test_case("test_merchant_reply_user_review", "测试商家回复用户评价")

        review = self.test_user_review_merchant_service()
        review_id = review.get("id")

        reply_data = {"reply_content": "感谢您的好评，我们会继续努力提供更好的服务！"}

        response = self.client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=self.merchant_headers
        )

        assert response.status_code == 200, f"回复评价失败: {response.text}"

        json_data = response.json()
        assert "data" in json_data, "响应缺少data字段"

        updated_review = json_data["data"]
        assert "reply_content" in updated_review, "回复内容不存在"
        assert updated_review["reply_content"] == reply_data["reply_content"], "回复内容不匹配"

    def test_data_consistency_across_interaction(self):
        """
        测试交互过程中的数据一致性

        验证点：
        1. 用户数据一致性
        2. 商家数据一致性
        3. 订单数据一致性
        4. 评价数据一致性
        """
        log_test_case("test_data_consistency_across_interaction", "测试交互过程中的数据一致性")

        appointment = self.test_user_book_merchant_service()
        order = self.test_user_purchase_merchant_product()
        review = self.test_user_review_merchant_service()

        response = self.client.get(f"/api/appointments/{appointment.get('id')}", headers=self.user_headers)
        assert response.status_code == 200, "获取预约详情失败"
        appointment_detail = response.json()["data"]

        assert_data_consistency(appointment, appointment_detail, ["id", "service_id", "merchant_id", "status"])

        response = self.client.get(f"/api/orders/{order.get('id')}", headers=self.user_headers)
        assert response.status_code == 200, "获取订单详情失败"
        order_detail = response.json()["data"]

        assert_data_consistency(order, order_detail, ["id", "merchant_id", "status"])

        response = self.client.get(f"/api/reviews/{review.get('id')}", headers=self.user_headers)
        assert response.status_code == 200, "获取评价详情失败"
        review_detail = response.json()["data"]

        assert_data_consistency(review, review_detail, ["id", "service_id", "merchant_id", "rating"])

    def test_complete_user_merchant_workflow(self):
        """
        测试完整的用户商家交互流程

        完整流程：
        1. 用户浏览商家服务
        2. 用户预约服务
        3. 商家确认预约
        4. 商家完成服务
        5. 用户评价服务
        6. 商家回复评价
        7. 验证整个流程的数据一致性
        """
        log_test_case("test_complete_user_merchant_workflow", "测试完整的用户商家交互流程")

        response = self.client.get(f"/api/services/merchant/{self.merchant_data.get('id')}", headers=self.user_headers)
        assert response.status_code == 200, "浏览服务失败"

        appointment_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_time": (datetime.now() + timedelta(days=1)).isoformat(),
            "notes": "完整流程测试预约",
        }

        response = self.client.post("/api/appointments", json_data=appointment_data, headers=self.user_headers)
        assert response.status_code in [200, 201], "创建预约失败"
        appointment = response.json()["data"]
        appointment_id = appointment.get("id")

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
        )
        assert response.status_code == 200, "商家确认预约失败"

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/complete", headers=self.merchant_headers
        )
        assert response.status_code == 200, "商家完成服务失败"

        review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": appointment_id,
            "rating": 5,
            "comment": "完整流程测试评价",
        }

        response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)
        assert response.status_code in [200, 201], "用户评价失败"
        review = response.json()["data"]
        review_id = review.get("id")

        reply_data = {"reply_content": "感谢您的评价！"}

        response = self.client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=self.merchant_headers
        )
        assert response.status_code == 200, "商家回复评价失败"

        response = self.client.get(f"/api/appointments/{appointment_id}", headers=self.user_headers)
        assert response.status_code == 200, "验证预约数据失败"
        final_appointment = response.json()["data"]
        assert final_appointment["status"] == "completed", "最终预约状态不正确"

        response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
        assert response.status_code == 200, "验证评价数据失败"
        final_review = response.json()["data"]
        assert final_review["reply_content"] is not None, "评价回复不存在"

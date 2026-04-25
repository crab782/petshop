"""
商家端API单元测试

测试商家端的所有API接口，包括：
- 商家资料接口
- 服务管理接口
- 商品管理接口
- 分类管理接口
- 预约管理接口
- 订单管理接口
- 评价管理接口
- 店铺设置接口
- 统计接口

重点测试：
- 状态转换逻辑
- 批量操作功能
- 权限验证
- 数据验证和边界条件
"""

from datetime import datetime, timedelta
from decimal import Decimal

import pytest

from tests.unit import BaseAPITest


@pytest.mark.unit_api
@pytest.mark.unit_merchant
class TestMerchantProfileAPI(BaseAPITest):
    """商家资料接口测试"""

    def test_get_merchant_profile(self, unit_http_client, test_merchant):
        """测试获取商家资料"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/profile", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "phone", "email"])

        data = response.json()["data"]
        assert data["id"] == test_merchant["merchant"]["id"]

    def test_get_merchant_info(self, unit_http_client, test_merchant):
        """测试获取商家信息"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/info", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "contact_person"])

    def test_update_merchant_profile(self, unit_http_client, test_merchant):
        """测试更新商家资料"""
        headers = self.get_auth_headers(test_merchant["token"])

        update_data = {"name": "更新后的商家名称", "contact_person": "新联系人", "address": "新地址123号"}

        response = unit_http_client.put("/api/merchant/profile", json_data=update_data, headers=headers)

        self.assert_response_success(response)
        self.assert_response_data(response, {"name": "更新后的商家名称"})

    def test_update_merchant_info(self, unit_http_client, test_merchant):
        """测试更新商家信息"""
        headers = self.get_auth_headers(test_merchant["token"])

        update_data = {"contact_person": "更新的联系人", "phone": test_merchant["merchant"]["phone"]}

        response = unit_http_client.put("/api/merchant/info", json_data=update_data, headers=headers)

        self.assert_response_success(response)

    def test_update_merchant_profile_without_auth(self, unit_http_client):
        """测试无权限更新商家资料"""
        update_data = {"name": "未授权更新"}

        response = unit_http_client.put("/api/merchant/profile", json_data=update_data)

        self.assert_response_error(response, 401)

    def test_update_merchant_profile_with_invalid_data(self, unit_http_client, test_merchant):
        """测试使用无效数据更新商家资料"""
        headers = self.get_auth_headers(test_merchant["token"])

        invalid_data = {"phone": "invalid_phone_format", "email": "invalid_email"}

        response = unit_http_client.put("/api/merchant/profile", json_data=invalid_data, headers=headers)

        self.assert_response_error(response, 400)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_service
class TestMerchantServiceAPI(BaseAPITest):
    """服务管理接口测试"""

    def test_get_service_list(self, unit_http_client, test_merchant, test_service):
        """测试获取服务列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/services", headers=headers)

        self.assert_response_success(response)
        self.assert_list_not_empty(response)

    def test_get_service_list_with_pagination(self, unit_http_client, test_merchant):
        """测试分页获取服务列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/services", params={"page": 1, "size": 10}, headers=headers)

        self.assert_response_success(response)
        self.assert_pagination(response, expected_page=1, expected_size=10)

    def test_get_service_list_with_filter(self, unit_http_client, test_merchant):
        """测试筛选服务列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/services", params={"status": "enabled"}, headers=headers)

        self.assert_response_success(response)

    def test_create_service(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建服务"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_data = test_data_builder.build_service(
            merchant_id=merchant_id, name="测试服务", price=Decimal("99.99"), duration=60
        )

        response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "price"])

    def test_create_service_with_invalid_price(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建价格为负数的服务"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_data = test_data_builder.build_service(merchant_id=merchant_id, price=Decimal("-100.00"))

        response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_create_service_without_required_fields(self, unit_http_client, test_merchant):
        """测试创建服务缺少必填字段"""
        headers = self.get_auth_headers(test_merchant["token"])

        incomplete_data = {"description": "缺少名称和价格"}

        response = unit_http_client.post("/api/merchant/services", json_data=incomplete_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_update_service(self, unit_http_client, test_merchant, test_service):
        """测试更新服务"""
        headers = self.get_auth_headers(test_merchant["token"])
        service_id = test_service.get("id")

        update_data = {"name": "更新后的服务名称", "price": Decimal("199.99")}

        response = unit_http_client.put(f"/api/merchant/services/{service_id}", json_data=update_data, headers=headers)

        self.assert_response_success(response)
        self.assert_response_data(response, {"name": "更新后的服务名称"})

    def test_update_nonexistent_service(self, unit_http_client, test_merchant):
        """测试更新不存在的服务"""
        headers = self.get_auth_headers(test_merchant["token"])

        update_data = {"name": "更新不存在的服务"}

        response = unit_http_client.put("/api/merchant/services/999999", json_data=update_data, headers=headers)

        self.assert_response_error(response, 404)

    def test_delete_service(self, unit_http_client, test_merchant, test_data_builder):
        """测试删除服务"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_data = test_data_builder.build_service(merchant_id=merchant_id)
        create_response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)
        service_id = create_response.json()["data"]["id"]

        response = unit_http_client.delete(f"/api/merchant/services/{service_id}", headers=headers)

        self.assert_response_success(response)

    def test_get_service_detail(self, unit_http_client, test_merchant, test_service):
        """测试获取服务详情"""
        headers = self.get_auth_headers(test_merchant["token"])
        service_id = test_service.get("id")

        response = unit_http_client.get(f"/api/merchant/services/{service_id}", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "price", "duration"])

    def test_batch_update_service_status(self, unit_http_client, test_merchant, test_data_builder):
        """测试批量更新服务状态"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_ids = []
        for i in range(3):
            service_data = test_data_builder.build_service(merchant_id=merchant_id, name=f"批量测试服务_{i}")
            create_response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)
            service_ids.append(create_response.json()["data"]["id"])

        batch_data = {"ids": service_ids, "status": "disabled"}

        response = unit_http_client.put("/api/merchant/services/batch/status", json_data=batch_data, headers=headers)

        self.assert_response_success(response)

    def test_batch_delete_services(self, unit_http_client, test_merchant, test_data_builder):
        """测试批量删除服务"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_ids = []
        for i in range(3):
            service_data = test_data_builder.build_service(merchant_id=merchant_id, name=f"批量删除服务_{i}")
            create_response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)
            service_ids.append(create_response.json()["data"]["id"])

        batch_data = {"ids": service_ids}

        response = unit_http_client.delete("/api/merchant/services/batch", json_data=batch_data, headers=headers)

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_product
class TestMerchantProductAPI(BaseAPITest):
    """商品管理接口测试"""

    def test_get_product_list(self, unit_http_client, test_merchant, test_product):
        """测试获取商品列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/products", headers=headers)

        self.assert_response_success(response)
        self.assert_list_not_empty(response)

    def test_get_product_list_with_pagination(self, unit_http_client, test_merchant):
        """测试分页获取商品列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/products", params={"page": 1, "size": 10}, headers=headers)

        self.assert_response_success(response)
        self.assert_pagination(response, expected_page=1, expected_size=10)

    def test_get_product_list_with_stock_filter(self, unit_http_client, test_merchant):
        """测试按库存筛选商品列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/products", params={"stock_status": "low"}, headers=headers)

        self.assert_response_success(response)

    def test_create_product(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_data = test_data_builder.build_product(
            merchant_id=merchant_id, name="测试商品", price=Decimal("49.99"), stock=100
        )

        response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "price", "stock"])

    def test_create_product_with_negative_stock(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建库存为负数的商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_data = test_data_builder.build_product(merchant_id=merchant_id, stock=-10)

        response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_get_product_detail(self, unit_http_client, test_merchant, test_product):
        """测试获取商品详情"""
        headers = self.get_auth_headers(test_merchant["token"])
        product_id = test_product.get("id")

        response = unit_http_client.get(f"/api/merchant/products/{product_id}", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name", "price", "stock"])

    def test_update_product(self, unit_http_client, test_merchant, test_product):
        """测试更新商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        product_id = test_product.get("id")

        update_data = {"name": "更新后的商品名称", "price": Decimal("99.99"), "stock": 50}

        response = unit_http_client.put(f"/api/merchant/products/{product_id}", json_data=update_data, headers=headers)

        self.assert_response_success(response)
        self.assert_response_data(response, {"name": "更新后的商品名称"})

    def test_update_product_stock(self, unit_http_client, test_merchant, test_product):
        """测试更新商品库存"""
        headers = self.get_auth_headers(test_merchant["token"])
        product_id = test_product.get("id")

        update_data = {"stock": 200}

        response = unit_http_client.put(f"/api/merchant/products/{product_id}", json_data=update_data, headers=headers)

        self.assert_response_success(response)
        self.assert_response_data(response, {"stock": 200})

    def test_delete_product(self, unit_http_client, test_merchant, test_data_builder):
        """测试删除商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_data = test_data_builder.build_product(merchant_id=merchant_id)
        create_response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)
        product_id = create_response.json()["data"]["id"]

        response = unit_http_client.delete(f"/api/merchant/products/{product_id}", headers=headers)

        self.assert_response_success(response)

    def test_batch_update_product_status(self, unit_http_client, test_merchant, test_data_builder):
        """测试批量更新商品状态"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_ids = []
        for i in range(3):
            product_data = test_data_builder.build_product(merchant_id=merchant_id, name=f"批量测试商品_{i}")
            create_response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)
            product_ids.append(create_response.json()["data"]["id"])

        batch_data = {"ids": product_ids, "status": "disabled"}

        response = unit_http_client.put("/api/merchant/products/batch/status", json_data=batch_data, headers=headers)

        self.assert_response_success(response)

    def test_batch_delete_products(self, unit_http_client, test_merchant, test_data_builder):
        """测试批量删除商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_ids = []
        for i in range(3):
            product_data = test_data_builder.build_product(merchant_id=merchant_id, name=f"批量删除商品_{i}")
            create_response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)
            product_ids.append(create_response.json()["data"]["id"])

        batch_data = {"ids": product_ids}

        response = unit_http_client.delete("/api/merchant/products/batch", json_data=batch_data, headers=headers)

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
class TestMerchantCategoryAPI(BaseAPITest):
    """分类管理接口测试"""

    def test_get_category_list(self, unit_http_client, test_merchant):
        """测试获取分类列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/categories", headers=headers)

        self.assert_response_success(response)

    def test_create_category(self, unit_http_client, test_merchant):
        """测试创建分类"""
        headers = self.get_auth_headers(test_merchant["token"])

        category_data = {"name": "测试分类", "icon": "https://example.com/icon.jpg", "sort": 1}

        response = unit_http_client.post("/api/merchant/categories", json_data=category_data, headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "name"])

    def test_create_category_without_name(self, unit_http_client, test_merchant):
        """测试创建分类缺少名称"""
        headers = self.get_auth_headers(test_merchant["token"])

        category_data = {"icon": "https://example.com/icon.jpg"}

        response = unit_http_client.post("/api/merchant/categories", json_data=category_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_update_category(self, unit_http_client, test_merchant):
        """测试更新分类"""
        headers = self.get_auth_headers(test_merchant["token"])

        create_data = {"name": "待更新分类"}
        create_response = unit_http_client.post("/api/merchant/categories", json_data=create_data, headers=headers)
        category_id = create_response.json()["data"]["id"]

        update_data = {"name": "更新后的分类", "sort": 10}

        response = unit_http_client.put(
            f"/api/merchant/categories/{category_id}", json_data=update_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"name": "更新后的分类"})

    def test_delete_category(self, unit_http_client, test_merchant):
        """测试删除分类"""
        headers = self.get_auth_headers(test_merchant["token"])

        create_data = {"name": "待删除分类"}
        create_response = unit_http_client.post("/api/merchant/categories", json_data=create_data, headers=headers)
        category_id = create_response.json()["data"]["id"]

        response = unit_http_client.delete(f"/api/merchant/categories/{category_id}", headers=headers)

        self.assert_response_success(response)

    def test_update_category_status(self, unit_http_client, test_merchant):
        """测试更新分类状态"""
        headers = self.get_auth_headers(test_merchant["token"])

        create_data = {"name": "状态测试分类"}
        create_response = unit_http_client.post("/api/merchant/categories", json_data=create_data, headers=headers)
        category_id = create_response.json()["data"]["id"]

        status_data = {"status": "disabled"}

        response = unit_http_client.put(
            f"/api/merchant/categories/{category_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)

    def test_batch_update_category_status(self, unit_http_client, test_merchant):
        """测试批量更新分类状态"""
        headers = self.get_auth_headers(test_merchant["token"])

        category_ids = []
        for i in range(3):
            create_data = {"name": f"批量分类_{i}"}
            create_response = unit_http_client.post("/api/merchant/categories", json_data=create_data, headers=headers)
            category_ids.append(create_response.json()["data"]["id"])

        batch_data = {"ids": category_ids, "status": "disabled"}

        response = unit_http_client.put("/api/merchant/categories/batch/status", json_data=batch_data, headers=headers)

        self.assert_response_success(response)

    def test_batch_delete_categories(self, unit_http_client, test_merchant):
        """测试批量删除分类"""
        headers = self.get_auth_headers(test_merchant["token"])

        category_ids = []
        for i in range(3):
            create_data = {"name": f"批量删除分类_{i}"}
            create_response = unit_http_client.post("/api/merchant/categories", json_data=create_data, headers=headers)
            category_ids.append(create_response.json()["data"]["id"])

        batch_data = {"ids": category_ids}

        response = unit_http_client.delete("/api/merchant/categories/batch", json_data=batch_data, headers=headers)

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_appointment
class TestMerchantAppointmentAPI(BaseAPITest):
    """预约管理接口测试 - 重点测试状态转换"""

    def test_get_appointment_list(self, unit_http_client, test_merchant):
        """测试获取预约列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/appointments", headers=headers)

        self.assert_response_success(response)

    def test_get_appointment_list_with_status_filter(self, unit_http_client, test_merchant):
        """测试按状态筛选预约列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/appointments", params={"status": "pending"}, headers=headers)

        self.assert_response_success(response)

    def test_get_appointment_list_with_date_range(self, unit_http_client, test_merchant):
        """测试按日期范围筛选预约列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        start_date = (datetime.now() - timedelta(days=7)).strftime("%Y-%m-%d")
        end_date = (datetime.now() + timedelta(days=7)).strftime("%Y-%m-%d")

        response = unit_http_client.get(
            "/api/merchant/appointments", params={"start_date": start_date, "end_date": end_date}, headers=headers
        )

        self.assert_response_success(response)

    def test_update_appointment_status_pending_to_confirmed(self, unit_http_client, test_merchant, test_appointment):
        """测试预约状态转换：pending -> confirmed"""
        headers = self.get_auth_headers(test_merchant["token"])
        appointment_id = test_appointment.get("id")

        status_data = {"status": "confirmed"}

        response = unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"status": "confirmed"})

    def test_update_appointment_status_confirmed_to_completed(self, unit_http_client, test_merchant, test_appointment):
        """测试预约状态转换：confirmed -> completed"""
        headers = self.get_auth_headers(test_merchant["token"])
        appointment_id = test_appointment.get("id")

        unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data={"status": "confirmed"}, headers=headers
        )

        status_data = {"status": "completed"}

        response = unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"status": "completed"})

    def test_update_appointment_status_pending_to_cancelled(self, unit_http_client, test_merchant, test_appointment):
        """测试预约状态转换：pending -> cancelled"""
        headers = self.get_auth_headers(test_merchant["token"])
        appointment_id = test_appointment.get("id")

        status_data = {"status": "cancelled"}

        response = unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"status": "cancelled"})

    def test_update_appointment_status_invalid_transition(self, unit_http_client, test_merchant, test_appointment):
        """测试无效的预约状态转换"""
        headers = self.get_auth_headers(test_merchant["token"])
        appointment_id = test_appointment.get("id")

        unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data={"status": "completed"}, headers=headers
        )

        status_data = {"status": "pending"}

        response = unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_error(response, 400)

    def test_get_recent_appointments(self, unit_http_client, test_merchant):
        """测试获取最近预约"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/appointments/recent", headers=headers)

        self.assert_response_success(response)

    def test_get_appointment_stats(self, unit_http_client, test_merchant):
        """测试获取预约统计"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/appointments/stats", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["total", "pending", "confirmed", "completed", "cancelled"])


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_order
class TestMerchantOrderAPI(BaseAPITest):
    """订单管理接口测试 - 重点测试状态转换"""

    def test_get_order_list(self, unit_http_client, test_merchant):
        """测试获取订单列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/orders", headers=headers)

        self.assert_response_success(response)

    def test_get_order_list_with_status_filter(self, unit_http_client, test_merchant):
        """测试按状态筛选订单列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/orders", params={"status": "paid"}, headers=headers)

        self.assert_response_success(response)

    def test_update_order_status(self, unit_http_client, test_merchant, test_product_order):
        """测试更新订单状态"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        status_data = {"status": "confirmed"}

        response = unit_http_client.put(
            f"/api/merchant/orders/{order_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)

    def test_get_order_detail(self, unit_http_client, test_merchant, test_product_order):
        """测试获取订单详情"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        response = unit_http_client.get(f"/api/merchant/orders/{order_id}", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "order_no", "status"])

    def test_update_product_order_status_pending_to_paid(self, unit_http_client, test_merchant, test_product_order):
        """测试商品订单状态转换：pending -> paid"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        status_data = {"status": "paid"}

        response = unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"status": "paid"})

    def test_update_product_order_status_paid_to_shipped(self, unit_http_client, test_merchant, test_product_order):
        """测试商品订单状态转换：paid -> shipped"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data={"status": "paid"}, headers=headers
        )

        status_data = {"status": "shipped"}

        response = unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"status": "shipped"})

    def test_update_product_order_status_shipped_to_completed(
        self, unit_http_client, test_merchant, test_product_order
    ):
        """测试商品订单状态转换：shipped -> completed"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data={"status": "paid"}, headers=headers
        )
        unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data={"status": "shipped"}, headers=headers
        )

        status_data = {"status": "completed"}

        response = unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_success(response)
        self.assert_response_data(response, {"status": "completed"})

    def test_update_product_order_status_invalid_transition(self, unit_http_client, test_merchant, test_product_order):
        """测试无效的商品订单状态转换"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data={"status": "completed"}, headers=headers
        )

        status_data = {"status": "pending"}

        response = unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_error(response, 400)

    def test_update_shipping_info(self, unit_http_client, test_merchant, test_product_order):
        """测试更新物流信息"""
        headers = self.get_auth_headers(test_merchant["token"])
        order_id = test_product_order.get("id")

        unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/status", json_data={"status": "paid"}, headers=headers
        )

        shipping_data = {"tracking_number": "SF1234567890", "shipping_company": "顺丰快递"}

        response = unit_http_client.put(
            f"/api/merchant/product-orders/{order_id}/shipping", json_data=shipping_data, headers=headers
        )

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_review
class TestMerchantReviewAPI(BaseAPITest):
    """评价管理接口测试"""

    def test_get_review_list(self, unit_http_client, test_merchant):
        """测试获取评价列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/reviews", headers=headers)

        self.assert_response_success(response)

    def test_get_review_list_with_rating_filter(self, unit_http_client, test_merchant):
        """测试按评分筛选评价列表"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/reviews", params={"rating": 5}, headers=headers)

        self.assert_response_success(response)

    def test_get_review_stats(self, unit_http_client, test_merchant):
        """测试获取评价统计"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/reviews/stats", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["total", "average_rating"])

    def test_get_review_detail(self, unit_http_client, test_merchant, test_review):
        """测试获取评价详情"""
        headers = self.get_auth_headers(test_merchant["token"])
        review_id = test_review.get("id")

        response = unit_http_client.get(f"/api/merchant/reviews/{review_id}", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["id", "rating", "comment"])

    def test_reply_review(self, unit_http_client, test_merchant, test_review):
        """测试回复评价"""
        headers = self.get_auth_headers(test_merchant["token"])
        review_id = test_review.get("id")

        reply_data = {"reply_content": "感谢您的评价，我们会继续努力！"}

        response = unit_http_client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=headers
        )

        self.assert_response_success(response)

    def test_reply_review_twice(self, unit_http_client, test_merchant, test_review):
        """测试重复回复评价"""
        headers = self.get_auth_headers(test_merchant["token"])
        review_id = test_review.get("id")

        reply_data = {"reply_content": "第一次回复"}
        unit_http_client.post(f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=headers)

        reply_data2 = {"reply_content": "第二次回复"}
        response = unit_http_client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data2, headers=headers
        )

        self.assert_response_error(response, 400)

    def test_delete_review(self, unit_http_client, test_merchant, test_review):
        """测试删除评价"""
        headers = self.get_auth_headers(test_merchant["token"])
        review_id = test_review.get("id")

        response = unit_http_client.delete(f"/api/merchant/reviews/{review_id}", headers=headers)

        self.assert_response_success(response)

    def test_get_recent_reviews(self, unit_http_client, test_merchant):
        """测试获取最近评价"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/reviews/recent", headers=headers)

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
class TestMerchantShopSettingsAPI(BaseAPITest):
    """店铺设置接口测试"""

    def test_change_password(self, unit_http_client, test_merchant):
        """测试修改密码"""
        headers = self.get_auth_headers(test_merchant["token"])

        password_data = {"old_password": test_merchant["credentials"]["password"], "new_password": "newpassword123"}

        response = unit_http_client.put("/api/merchant/password", json_data=password_data, headers=headers)

        self.assert_response_success(response)

    def test_change_password_with_wrong_old_password(self, unit_http_client, test_merchant):
        """测试使用错误旧密码修改密码"""
        headers = self.get_auth_headers(test_merchant["token"])

        password_data = {"old_password": "wrongpassword", "new_password": "newpassword123"}

        response = unit_http_client.put("/api/merchant/password", json_data=password_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_bind_phone(self, unit_http_client, test_merchant):
        """测试绑定手机号"""
        headers = self.get_auth_headers(test_merchant["token"])

        bind_data = {"phone": "13800138000", "code": "123456"}

        response = unit_http_client.put("/api/merchant/bind-phone", json_data=bind_data, headers=headers)

        self.assert_response_success(response)

    def test_bind_email(self, unit_http_client, test_merchant):
        """测试绑定邮箱"""
        headers = self.get_auth_headers(test_merchant["token"])

        bind_data = {"email": "newemail@example.com", "code": "123456"}

        response = unit_http_client.put("/api/merchant/bind-email", json_data=bind_data, headers=headers)

        self.assert_response_success(response)

    def test_send_verification_code(self, unit_http_client, test_merchant):
        """测试发送验证码"""
        headers = self.get_auth_headers(test_merchant["token"])

        code_data = {"type": "phone", "target": "13800138000"}

        response = unit_http_client.post("/api/merchant/send-code", json_data=code_data, headers=headers)

        self.assert_response_success(response)

    def test_get_shop_settings(self, unit_http_client, test_merchant):
        """测试获取店铺设置"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/shop/settings", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["name", "status"])

    def test_update_shop_settings(self, unit_http_client, test_merchant):
        """测试更新店铺设置"""
        headers = self.get_auth_headers(test_merchant["token"])

        settings_data = {
            "notification_settings": {"appointment_reminder": True, "order_reminder": True, "review_reminder": False}
        }

        response = unit_http_client.put("/api/merchant/shop/settings", json_data=settings_data, headers=headers)

        self.assert_response_success(response)

    def test_toggle_shop_status(self, unit_http_client, test_merchant):
        """测试切换店铺营业状态"""
        headers = self.get_auth_headers(test_merchant["token"])

        status_data = {"status": "closed"}

        response = unit_http_client.put("/api/merchant/shop/toggle-status", json_data=status_data, headers=headers)

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
class TestMerchantStatsAPI(BaseAPITest):
    """统计接口测试"""

    def test_get_dashboard_stats(self, unit_http_client, test_merchant):
        """测试获取商家首页统计数据"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/stats/dashboard", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(
            response, ["today_orders", "pending_appointments", "today_revenue", "average_rating"]
        )

    def test_get_revenue_stats(self, unit_http_client, test_merchant):
        """测试获取营收统计"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/merchant/stats/revenue", headers=headers)

        self.assert_response_success(response)
        self.assert_required_fields(response, ["total_revenue", "service_revenue", "product_revenue"])

    def test_get_revenue_stats_with_date_range(self, unit_http_client, test_merchant):
        """测试按日期范围获取营收统计"""
        headers = self.get_auth_headers(test_merchant["token"])

        start_date = (datetime.now() - timedelta(days=30)).strftime("%Y-%m-%d")
        end_date = datetime.now().strftime("%Y-%m-%d")

        response = unit_http_client.get(
            "/api/merchant/stats/revenue", params={"start_date": start_date, "end_date": end_date}, headers=headers
        )

        self.assert_response_success(response)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_auth
class TestMerchantPermissionAPI(BaseAPITest):
    """权限验证测试 - 重点测试商家只能管理自己的数据"""

    def test_access_other_merchant_service(self, unit_http_client, test_merchant, test_data_builder):
        """测试访问其他商家的服务"""
        headers = self.get_auth_headers(test_merchant["token"])

        other_merchant_data = test_data_builder.build_merchant(name="other_merchant")
        other_merchant_response = unit_http_client.post("/api/auth/merchant/register", json_data=other_merchant_data)

        if other_merchant_response.status_code in [200, 201]:
            other_merchant_id = other_merchant_response.json()["data"]["id"]

            service_data = test_data_builder.build_service(merchant_id=other_merchant_id)
            create_response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)

            assert create_response.status_code in [400, 403, 404]

    def test_update_other_merchant_product(self, unit_http_client, test_merchant, test_product):
        """测试更新其他商家的商品"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.put(
            f"/api/merchant/products/999999", json_data={"name": "尝试更新其他商家的商品"}, headers=headers
        )

        self.assert_response_error(response, 404)

    def test_delete_other_merchant_appointment(self, unit_http_client, test_merchant):
        """测试删除其他商家的预约"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.delete("/api/merchant/appointments/999999", headers=headers)

        self.assert_response_error(response, 404)

    def test_access_without_merchant_token(self, unit_http_client, test_user):
        """测试使用用户token访问商家接口"""
        headers = self.get_auth_headers(test_user["token"])

        response = unit_http_client.get("/api/merchant/services", headers=headers)

        self.assert_response_error(response, 403)

    def test_access_with_expired_token(self, unit_http_client):
        """测试使用过期token访问商家接口"""
        headers = self.get_auth_headers("expired_token_12345")

        response = unit_http_client.get("/api/merchant/profile", headers=headers)

        self.assert_response_error(response, 401)

    def test_access_with_invalid_token(self, unit_http_client):
        """测试使用无效token访问商家接口"""
        headers = self.get_auth_headers("invalid_token_12345")

        response = unit_http_client.get("/api/merchant/profile", headers=headers)

        self.assert_response_error(response, 401)


@pytest.mark.unit_api
@pytest.mark.unit_merchant
@pytest.mark.unit_validation
class TestMerchantValidationAPI(BaseAPITest):
    """数据验证和边界条件测试"""

    def test_create_service_with_empty_name(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建服务名称为空"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_data = test_data_builder.build_service(merchant_id=merchant_id, name="")

        response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_create_service_with_too_long_name(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建服务名称过长"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        service_data = test_data_builder.build_service(merchant_id=merchant_id, name="a" * 200)

        response = unit_http_client.post("/api/merchant/services", json_data=service_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_create_product_with_zero_price(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建价格为0的商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_data = test_data_builder.build_product(merchant_id=merchant_id, price=Decimal("0.00"))

        response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_create_product_with_large_stock(self, unit_http_client, test_merchant, test_data_builder):
        """测试创建库存超大的商品"""
        headers = self.get_auth_headers(test_merchant["token"])
        merchant_id = test_merchant["merchant"]["id"]

        product_data = test_data_builder.build_product(merchant_id=merchant_id, stock=999999999)

        response = unit_http_client.post("/api/merchant/products", json_data=product_data, headers=headers)

        self.assert_response_success(response)

    def test_update_appointment_with_invalid_status(self, unit_http_client, test_merchant, test_appointment):
        """测试更新预约为无效状态"""
        headers = self.get_auth_headers(test_merchant["token"])
        appointment_id = test_appointment.get("id")

        status_data = {"status": "invalid_status"}

        response = unit_http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status", json_data=status_data, headers=headers
        )

        self.assert_response_error(response, 400)

    def test_create_category_with_duplicate_name(self, unit_http_client, test_merchant):
        """测试创建重复名称的分类"""
        headers = self.get_auth_headers(test_merchant["token"])

        category_data = {"name": "重复分类"}
        unit_http_client.post("/api/merchant/categories", json_data=category_data, headers=headers)

        response = unit_http_client.post("/api/merchant/categories", json_data=category_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_reply_review_with_empty_content(self, unit_http_client, test_merchant, test_review):
        """测试回复评价内容为空"""
        headers = self.get_auth_headers(test_merchant["token"])
        review_id = test_review.get("id")

        reply_data = {"reply_content": ""}

        response = unit_http_client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=headers
        )

        self.assert_response_error(response, 400)

    def test_change_password_with_weak_password(self, unit_http_client, test_merchant):
        """测试修改为弱密码"""
        headers = self.get_auth_headers(test_merchant["token"])

        password_data = {"old_password": test_merchant["credentials"]["password"], "new_password": "123"}

        response = unit_http_client.put("/api/merchant/password", json_data=password_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_batch_operation_with_empty_ids(self, unit_http_client, test_merchant):
        """测试批量操作ID列表为空"""
        headers = self.get_auth_headers(test_merchant["token"])

        batch_data = {"ids": [], "status": "disabled"}

        response = unit_http_client.put("/api/merchant/services/batch/status", json_data=batch_data, headers=headers)

        self.assert_response_error(response, 400)

    def test_batch_operation_with_too_many_ids(self, unit_http_client, test_merchant):
        """测试批量操作ID列表过长"""
        headers = self.get_auth_headers(test_merchant["token"])

        batch_data = {"ids": list(range(1, 1001)), "status": "disabled"}

        response = unit_http_client.put("/api/merchant/services/batch/status", json_data=batch_data, headers=headers)

        self.assert_response_error(response, 400)

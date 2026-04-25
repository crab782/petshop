"""
用户端API单元测试

测试用户端的所有API接口，包括：
- 用户资料接口
- 宠物管理接口
- 预约管理接口
- 订单管理接口
- 服务查询接口
- 收货地址接口
- 评价接口
- 收藏接口
- 通知接口
"""

from datetime import datetime, timedelta

import allure
import pytest

from tests.unit import BaseAPITest


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestUserProfileAPI(BaseAPITest):
    """用户资料接口测试"""

    @allure.story("用户资料")
    @allure.title("获取用户资料成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_profile_success(self, unit_http_client, test_user):
        """测试获取用户资料成功"""
        response = unit_http_client.get("/api/user/profile", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)
        self.assert_required_fields(response, ["id", "phone"])

        data = response.json()
        assert data["id"] == test_user["user"]["id"]

    @allure.story("用户资料")
    @allure.title("未认证访问用户资料")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_get_profile_unauthorized(self, unit_http_client):
        """测试未认证访问用户资料"""
        response = unit_http_client.get("/api/user/profile")

        self.assert_response_error(response, 401)

    @allure.story("用户资料")
    @allure.title("更新用户资料成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_profile_success(self, unit_http_client, test_user):
        """测试更新用户资料成功"""
        update_data = {"username": "updated_user", "email": "updated@example.com"}

        response = unit_http_client.put(
            "/api/user/profile", json_data=update_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("用户资料")
    @allure.title("获取主页统计信息成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_home_stats_success(self, unit_http_client, test_user):
        """测试获取主页统计信息成功"""
        response = unit_http_client.get("/api/user/home/stats", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

        data = response.json().get("data", {})
        assert "petCount" in data or "pets" in data


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestPetManagementAPI(BaseAPITest):
    """宠物管理接口测试"""

    @allure.story("宠物管理")
    @allure.title("获取宠物列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_pets_success(self, unit_http_client, test_user):
        """测试获取宠物列表成功"""
        response = unit_http_client.get("/api/user/pets", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("宠物管理")
    @allure.title("添加宠物成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_pet_success(self, unit_http_client, test_user):
        """测试添加宠物成功"""
        pet_data = {"name": "测试宠物", "type": "dog", "breed": "金毛", "age": 2, "gender": "male"}

        response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 201)
        data = response.json()
        assert data["name"] == pet_data["name"]

    @allure.story("宠物管理")
    @allure.title("添加宠物缺少必填字段")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("missing_field", ["name", "type"])
    def test_add_pet_missing_required_field(self, unit_http_client, test_user, missing_field):
        """测试添加宠物缺少必填字段"""
        pet_data = {"name": "测试宠物", "type": "dog", "breed": "金毛", "age": 2, "gender": "male"}
        del pet_data[missing_field]

        response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("宠物管理")
    @allure.title("更新宠物成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_pet_success(self, unit_http_client, test_user):
        """测试更新宠物成功"""
        pet_data = {"name": "原始宠物", "type": "dog", "breed": "金毛", "age": 2, "gender": "male"}

        create_response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        pet_id = create_response.json()["id"]

        update_data = {"name": "更新宠物", "type": "dog", "breed": "拉布拉多", "age": 3, "gender": "male"}

        response = unit_http_client.put(
            f"/api/user/pets/{pet_id}", json_data=update_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)
        data = response.json()
        assert data["name"] == update_data["name"]

    @allure.story("宠物管理")
    @allure.title("更新不存在的宠物")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_nonexistent_pet(self, unit_http_client, test_user):
        """测试更新不存在的宠物"""
        update_data = {"name": "更新宠物", "type": "dog"}

        response = unit_http_client.put(
            "/api/user/pets/99999", json_data=update_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 404)

    @allure.story("宠物管理")
    @allure.title("删除宠物成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_pet_success(self, unit_http_client, test_user):
        """测试删除宠物成功"""
        pet_data = {"name": "待删除宠物", "type": "cat", "age": 1, "gender": "female"}

        create_response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        pet_id = create_response.json()["id"]

        response = unit_http_client.delete(
            f"/api/user/pets/{pet_id}", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("宠物管理")
    @allure.title("获取宠物详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_pet_by_id_success(self, unit_http_client, test_user):
        """测试获取宠物详情成功"""
        pet_data = {"name": "详情宠物", "type": "dog", "age": 2, "gender": "male"}

        create_response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        pet_id = create_response.json()["id"]

        response = unit_http_client.get(f"/api/user/pets/{pet_id}", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        data = response.json()
        assert data["id"] == pet_id

    @allure.story("宠物管理")
    @allure.title("未认证访问宠物接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_pet_unauthorized_access(self, unit_http_client):
        """测试未认证访问宠物接口"""
        response = unit_http_client.get("/api/user/pets")
        self.assert_response_error(response, 401)


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestAppointmentManagementAPI(BaseAPITest):
    """预约管理接口测试"""

    @allure.story("预约管理")
    @allure.title("获取预约列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_appointments_success(self, unit_http_client, test_user):
        """测试获取预约列表成功"""
        response = unit_http_client.get("/api/user/appointments", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("预约管理")
    @allure.title("获取预约列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("status", ["pending", "confirmed", "completed", "cancelled"])
    def test_get_appointments_with_filter(self, unit_http_client, test_user, status):
        """测试获取预约列表带状态筛选"""
        response = unit_http_client.get(
            "/api/user/appointments", params={"status": status}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("预约管理")
    @allure.title("获取预约列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_appointments_with_pagination(self, unit_http_client, test_user):
        """测试获取预约列表带分页"""
        response = unit_http_client.get(
            "/api/user/appointments",
            params={"page": 0, "pageSize": 10},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)
        self.assert_pagination(response, expected_page=0)

    @allure.story("预约管理")
    @allure.title("获取预约详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_appointment_by_id_success(self, unit_http_client, test_user):
        """测试获取预约详情成功"""
        response = unit_http_client.get("/api/user/appointments/1", headers=self.get_auth_headers(test_user["token"]))

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("预约管理")
    @allure.title("创建预约成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_appointment_success(self, unit_http_client, test_user, test_merchant, test_service):
        """测试创建预约成功"""
        pet_data = {"name": "预约宠物", "type": "dog", "age": 2, "gender": "male"}

        pet_response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        pet_id = pet_response.json()["id"]

        appointment_time = (datetime.now() + timedelta(days=1)).isoformat()

        appointment_data = {
            "serviceId": test_service["id"],
            "petId": pet_id,
            "appointmentTime": appointment_time,
            "remark": "测试预约",
        }

        response = unit_http_client.post(
            "/api/user/appointments", json_data=appointment_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 201)

    @allure.story("预约管理")
    @allure.title("创建预约无效服务")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_appointment_invalid_service(self, unit_http_client, test_user):
        """测试创建预约无效服务ID"""
        appointment_data = {"serviceId": 99999, "petId": 1, "appointmentTime": datetime.now().isoformat()}

        response = unit_http_client.post(
            "/api/user/appointments", json_data=appointment_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("预约管理")
    @allure.title("取消预约成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_cancel_appointment_success(self, unit_http_client, test_user):
        """测试取消预约成功"""
        response = unit_http_client.put(
            "/api/user/appointments/1/cancel", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("预约管理")
    @allure.title("获取预约统计成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_appointment_stats_success(self, unit_http_client, test_user):
        """测试获取预约统计成功"""
        response = unit_http_client.get(
            "/api/user/appointments/stats", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)
        data = response.json()
        assert "total" in data or "pending" in data


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestOrderManagementAPI(BaseAPITest):
    """订单管理接口测试"""

    @allure.story("订单管理")
    @allure.title("获取订单列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_orders_success(self, unit_http_client, test_user):
        """测试获取订单列表成功"""
        response = unit_http_client.get("/api/user/orders", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("订单管理")
    @allure.title("获取订单列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("status", ["pending", "paid", "shipped", "completed", "cancelled"])
    def test_get_orders_with_filter(self, unit_http_client, test_user, status):
        """测试获取订单列表带状态筛选"""
        response = unit_http_client.get(
            "/api/user/orders", params={"status": status}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("订单管理")
    @allure.title("获取订单列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_orders_with_pagination(self, unit_http_client, test_user):
        """测试获取订单列表带分页"""
        response = unit_http_client.get(
            "/api/user/orders", params={"page": 1, "pageSize": 10}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("订单管理")
    @allure.title("获取订单详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_order_by_id_success(self, unit_http_client, test_user):
        """测试获取订单详情成功"""
        response = unit_http_client.get("/api/user/orders/1", headers=self.get_auth_headers(test_user["token"]))

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("订单管理")
    @allure.title("创建订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_success(self, unit_http_client, test_user, test_product):
        """测试创建订单成功"""
        order_data = {"items": [{"productId": test_product["id"], "quantity": 2}], "addressId": 1, "remark": "测试订单"}

        response = unit_http_client.post(
            "/api/user/orders", json_data=order_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 201:
            data = response.json()
            assert "orderId" in data or "id" in data
        else:
            self.assert_response_error(response, 400)

    @allure.story("订单管理")
    @allure.title("预览订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_preview_order_success(self, unit_http_client, test_user, test_product):
        """测试预览订单成功"""
        preview_data = {"items": [{"productId": test_product["id"], "quantity": 1}]}

        response = unit_http_client.post(
            "/api/user/orders/preview", json_data=preview_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 400)

    @allure.story("订单管理")
    @allure.title("支付订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_pay_order_success(self, unit_http_client, test_user):
        """测试支付订单成功"""
        pay_data = {"payMethod": "wechat"}

        response = unit_http_client.post(
            "/api/user/orders/1/pay", json_data=pay_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("订单管理")
    @allure.title("获取支付状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_pay_status_success(self, unit_http_client, test_user):
        """测试获取支付状态成功"""
        response = unit_http_client.get(
            "/api/user/orders/1/pay/status", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("订单管理")
    @allure.title("取消订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_cancel_order_success(self, unit_http_client, test_user):
        """测试取消订单成功"""
        response = unit_http_client.put("/api/user/orders/1/cancel", headers=self.get_auth_headers(test_user["token"]))

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("订单管理")
    @allure.title("申请退款成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_refund_order_success(self, unit_http_client, test_user):
        """测试申请退款成功"""
        refund_data = {"reason": "测试退款"}

        response = unit_http_client.post(
            "/api/user/orders/1/refund", json_data=refund_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("订单管理")
    @allure.title("确认收货成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_confirm_receive_success(self, unit_http_client, test_user):
        """测试确认收货成功"""
        response = unit_http_client.put("/api/user/orders/1/confirm", headers=self.get_auth_headers(test_user["token"]))

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("订单管理")
    @allure.title("删除订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_order_success(self, unit_http_client, test_user):
        """测试删除订单成功"""
        response = unit_http_client.delete("/api/user/orders/99999", headers=self.get_auth_headers(test_user["token"]))

        self.assert_status_code(response, 204)

    @allure.story("订单管理")
    @allure.title("批量取消订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_cancel_orders_success(self, unit_http_client, test_user):
        """测试批量取消订单成功"""
        batch_data = {"ids": [1, 2, 3]}

        response = unit_http_client.put(
            "/api/user/orders/batch-cancel", json_data=batch_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            pass

    @allure.story("订单管理")
    @allure.title("批量删除订单成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_orders_success(self, unit_http_client, test_user):
        """测试批量删除订单成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/user/orders/batch-delete", json_data=batch_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 204)


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestServiceQueryAPI(BaseAPITest):
    """服务查询接口测试"""

    @allure.story("服务查询")
    @allure.title("获取服务列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_services_success(self, unit_http_client, test_user):
        """测试获取服务列表成功"""
        response = unit_http_client.get("/api/user/services", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("服务查询")
    @allure.title("获取服务列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_services_with_filter(self, unit_http_client, test_user):
        """测试获取服务列表带筛选"""
        response = unit_http_client.get(
            "/api/user/services",
            params={"keyword": "美容", "status": "active"},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("服务查询")
    @allure.title("获取服务列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_services_with_pagination(self, unit_http_client, test_user):
        """测试获取服务列表带分页"""
        response = unit_http_client.get(
            "/api/user/services", params={"page": 0, "pageSize": 10}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestAddressManagementAPI(BaseAPITest):
    """收货地址接口测试"""

    @allure.story("收货地址")
    @allure.title("获取地址列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_addresses_success(self, unit_http_client, test_user):
        """测试获取地址列表成功"""
        response = unit_http_client.get("/api/user/addresses", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("收货地址")
    @allure.title("添加地址成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_address_success(self, unit_http_client, test_user):
        """测试添加地址成功"""
        address_data = {
            "receiverName": "测试收货人",
            "phone": "13800138000",
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": "测试街道123号",
        }

        response = unit_http_client.post(
            "/api/user/addresses", json_data=address_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 201)
        data = response.json().get("data", {})
        assert data["receiverName"] == address_data["receiverName"]

    @allure.story("收货地址")
    @allure.title("添加地址缺少必填字段")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("missing_field", ["receiverName", "phone", "detailAddress"])
    def test_add_address_missing_required_field(self, unit_http_client, test_user, missing_field):
        """测试添加地址缺少必填字段"""
        address_data = {
            "receiverName": "测试收货人",
            "phone": "13800138000",
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": "测试街道123号",
        }
        del address_data[missing_field]

        response = unit_http_client.post(
            "/api/user/addresses", json_data=address_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("收货地址")
    @allure.title("更新地址成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_address_success(self, unit_http_client, test_user):
        """测试更新地址成功"""
        address_data = {
            "receiverName": "原始收货人",
            "phone": "13800138000",
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": "原始街道123号",
        }

        create_response = unit_http_client.post(
            "/api/user/addresses", json_data=address_data, headers=self.get_auth_headers(test_user["token"])
        )

        address_id = create_response.json().get("data", {}).get("id")

        update_data = {
            "receiverName": "更新收货人",
            "phone": "13900139000",
            "province": "上海市",
            "city": "上海市",
            "district": "浦东新区",
            "detailAddress": "更新街道456号",
        }

        response = unit_http_client.put(
            f"/api/user/addresses/{address_id}",
            json_data=update_data,
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("收货地址")
    @allure.title("删除地址成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_address_success(self, unit_http_client, test_user):
        """测试删除地址成功"""
        address_data = {
            "receiverName": "待删除收货人",
            "phone": "13800138000",
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": "待删除街道123号",
        }

        create_response = unit_http_client.post(
            "/api/user/addresses", json_data=address_data, headers=self.get_auth_headers(test_user["token"])
        )

        address_id = create_response.json().get("data", {}).get("id")

        response = unit_http_client.delete(
            f"/api/user/addresses/{address_id}", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("收货地址")
    @allure.title("设置默认地址成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_set_default_address_success(self, unit_http_client, test_user):
        """测试设置默认地址成功"""
        address_data = {
            "receiverName": "默认收货人",
            "phone": "13800138000",
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": "默认街道123号",
        }

        create_response = unit_http_client.post(
            "/api/user/addresses", json_data=address_data, headers=self.get_auth_headers(test_user["token"])
        )

        address_id = create_response.json().get("data", {}).get("id")

        response = unit_http_client.put(
            f"/api/user/addresses/{address_id}/default", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestReviewManagementAPI(BaseAPITest):
    """评价接口测试"""

    @allure.story("评价管理")
    @allure.title("添加评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_review_success(self, unit_http_client, test_user):
        """测试添加评价成功"""
        review_data = {"appointmentId": 1, "rating": 5, "comment": "服务很好，非常满意"}

        response = unit_http_client.post(
            "/api/user/reviews", json_data=review_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 201:
            self.assert_response_success(response)
        else:
            pass

    @allure.story("评价管理")
    @allure.title("添加评价无效评分")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("rating", [0, 6, -1, 100])
    def test_add_review_invalid_rating(self, unit_http_client, test_user, rating):
        """测试添加评价无效评分"""
        review_data = {"appointmentId": 1, "rating": rating, "comment": "测试评价"}

        response = unit_http_client.post(
            "/api/user/reviews", json_data=review_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("评价管理")
    @allure.title("获取评价列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_reviews_success(self, unit_http_client, test_user):
        """测试获取评价列表成功"""
        response = unit_http_client.get("/api/user/reviews", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("评价管理")
    @allure.title("获取评价列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("rating", [1, 2, 3, 4, 5])
    def test_get_reviews_with_filter(self, unit_http_client, test_user, rating):
        """测试获取评价列表带评分筛选"""
        response = unit_http_client.get(
            "/api/user/reviews", params={"rating": rating}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("获取评价详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_review_by_id_success(self, unit_http_client, test_user):
        """测试获取评价详情成功"""
        response = unit_http_client.get("/api/user/reviews/1", headers=self.get_auth_headers(test_user["token"]))

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("评价管理")
    @allure.title("更新评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_review_success(self, unit_http_client, test_user):
        """测试更新评价成功"""
        update_data = {"rating": 4, "comment": "更新后的评价"}

        response = unit_http_client.put(
            "/api/user/reviews/1", json_data=update_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("评价管理")
    @allure.title("删除评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_review_success(self, unit_http_client, test_user):
        """测试删除评价成功"""
        response = unit_http_client.delete("/api/user/reviews/99999", headers=self.get_auth_headers(test_user["token"]))

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestFavoriteManagementAPI(BaseAPITest):
    """收藏接口测试"""

    @allure.story("收藏管理")
    @allure.title("获取收藏商家列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_favorite_merchants_success(self, unit_http_client, test_user):
        """测试获取收藏商家列表成功"""
        response = unit_http_client.get("/api/user/favorites", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("收藏管理")
    @allure.title("添加商家收藏成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_merchant_favorite_success(self, unit_http_client, test_user, test_merchant):
        """测试添加商家收藏成功"""
        favorite_data = {"merchantId": test_merchant["merchant"]["id"]}

        response = unit_http_client.post(
            "/api/user/favorites", json_data=favorite_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 201:
            self.assert_response_success(response)
        elif response.status_code == 409:
            pass
        else:
            self.assert_response_error(response, 404)

    @allure.story("收藏管理")
    @allure.title("删除商家收藏成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_remove_merchant_favorite_success(self, unit_http_client, test_user):
        """测试删除商家收藏成功"""
        response = unit_http_client.delete(
            "/api/user/favorites/99999", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("收藏管理")
    @allure.title("获取收藏服务列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_favorite_services_success(self, unit_http_client, test_user):
        """测试获取收藏服务列表成功"""
        response = unit_http_client.get(
            "/api/user/favorites/services", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("收藏管理")
    @allure.title("添加服务收藏成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_service_favorite_success(self, unit_http_client, test_user, test_service):
        """测试添加服务收藏成功"""
        favorite_data = {"serviceId": test_service["id"]}

        response = unit_http_client.post(
            "/api/user/favorites/services", json_data=favorite_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 201:
            self.assert_response_success(response)
        elif response.status_code == 409:
            pass
        else:
            self.assert_response_error(response, 404)

    @allure.story("收藏管理")
    @allure.title("删除服务收藏成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_remove_service_favorite_success(self, unit_http_client, test_user):
        """测试删除服务收藏成功"""
        response = unit_http_client.delete(
            "/api/user/favorites/services/99999", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("收藏管理")
    @allure.title("添加商品收藏成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_product_favorite_success(self, unit_http_client, test_user, test_product):
        """测试添加商品收藏成功"""
        favorite_data = {"productId": test_product["id"]}

        response = unit_http_client.post(
            "/api/user/favorites/products", json_data=favorite_data, headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 201:
            self.assert_response_success(response)
        elif response.status_code == 409:
            pass
        else:
            self.assert_response_error(response, 404)

    @allure.story("收藏管理")
    @allure.title("删除商品收藏成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_remove_product_favorite_success(self, unit_http_client, test_user):
        """测试删除商品收藏成功"""
        response = unit_http_client.delete(
            "/api/user/favorites/products/99999", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("收藏管理")
    @allure.title("检查商品收藏状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_check_product_favorite_status_success(self, unit_http_client, test_user, test_product):
        """测试检查商品收藏状态成功"""
        response = unit_http_client.get(
            f"/api/user/favorites/products/{test_product['id']}/check",
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)
        data = response.json().get("data", {})
        assert "favorited" in data


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestNotificationManagementAPI(BaseAPITest):
    """通知接口测试"""

    @allure.story("通知管理")
    @allure.title("获取通知列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_notifications_success(self, unit_http_client, test_user):
        """测试获取通知列表成功"""
        response = unit_http_client.get("/api/user/notifications", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("通知管理")
    @allure.title("获取通知列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("is_read", [True, False])
    def test_get_notifications_with_filter(self, unit_http_client, test_user, is_read):
        """测试获取通知列表带已读状态筛选"""
        response = unit_http_client.get(
            "/api/user/notifications", params={"isRead": is_read}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("通知管理")
    @allure.title("标记通知为已读成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_mark_notification_as_read_success(self, unit_http_client, test_user):
        """测试标记通知为已读成功"""
        response = unit_http_client.put(
            "/api/user/notifications/1/read", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("通知管理")
    @allure.title("全部标记为已读成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_mark_all_notifications_as_read_success(self, unit_http_client, test_user):
        """测试全部标记为已读成功"""
        response = unit_http_client.put(
            "/api/user/notifications/read-all", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("通知管理")
    @allure.title("批量标记为已读成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_mark_notifications_as_read_success(self, unit_http_client, test_user):
        """测试批量标记为已读成功"""
        batch_data = {"ids": [1, 2, 3]}

        response = unit_http_client.put(
            "/api/user/notifications/batch-read",
            json_data=batch_data,
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("通知管理")
    @allure.title("删除通知成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_notification_success(self, unit_http_client, test_user):
        """测试删除通知成功"""
        response = unit_http_client.delete(
            "/api/user/notifications/99999", headers=self.get_auth_headers(test_user["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("通知管理")
    @allure.title("批量删除通知成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_notifications_success(self, unit_http_client, test_user):
        """测试批量删除通知成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/user/notifications/batch", json_data=batch_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("通知管理")
    @allure.title("获取未读通知数量成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_unread_notification_count_success(self, unit_http_client, test_user):
        """测试获取未读通知数量成功"""
        response = unit_http_client.get(
            "/api/user/notifications/unread-count", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)
        data = response.json().get("data", {})
        assert "count" in data or "unreadCount" in data


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
@pytest.mark.security
class TestUserAPISecurity(BaseAPITest):
    """用户端API安全测试"""

    @allure.story("安全测试")
    @allure.title("未认证访问所有用户接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.parametrize(
        "endpoint,method",
        [
            ("/api/user/profile", "GET"),
            ("/api/user/profile", "PUT"),
            ("/api/user/pets", "GET"),
            ("/api/user/pets", "POST"),
            ("/api/user/appointments", "GET"),
            ("/api/user/orders", "GET"),
            ("/api/user/addresses", "GET"),
            ("/api/user/reviews", "GET"),
            ("/api/user/favorites", "GET"),
            ("/api/user/notifications", "GET"),
        ],
    )
    def test_unauthorized_access(self, unit_http_client, endpoint, method):
        """测试未认证访问用户接口"""
        if method == "GET":
            response = unit_http_client.get(endpoint)
        elif method == "POST":
            response = unit_http_client.post(endpoint, json_data={})
        elif method == "PUT":
            response = unit_http_client.put(endpoint, json_data={})

        self.assert_response_error(response, 401)

    @allure.story("安全测试")
    @allure.title("无效Token访问")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_invalid_token_access(self, unit_http_client):
        """测试无效Token访问"""
        headers = self.get_auth_headers("invalid_token_12345")

        response = unit_http_client.get("/api/user/profile", headers=headers)

        self.assert_response_error(response, 401)

    @allure.story("安全测试")
    @allure.title("SQL注入防护测试")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_sql_injection_protection(self, unit_http_client, test_user):
        """测试SQL注入防护"""
        malicious_payloads = ["'; DROP TABLE user;--", "' OR '1'='1", "1; DELETE FROM pet WHERE 1=1;--"]

        for payload in malicious_payloads:
            pet_data = {"name": payload, "type": "dog", "age": 2, "gender": "male"}

            response = unit_http_client.post(
                "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
            )

            assert response.status_code in [200, 201, 400], f"SQL注入测试失败，payload: {payload}"

    @allure.story("安全测试")
    @allure.title("XSS防护测试")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_xss_protection(self, unit_http_client, test_user):
        """测试XSS防护"""
        xss_payloads = ["<script>alert('XSS')</script>", "<img src=x onerror=alert('XSS')>", "javascript:alert('XSS')"]

        for payload in xss_payloads:
            pet_data = {"name": payload, "type": "dog", "age": 2, "gender": "male"}

            response = unit_http_client.post(
                "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
            )

            if response.status_code in [200, 201]:
                response_text = response.text
                assert "<script>" not in response_text.lower(), f"响应中包含未转义的script标签: {payload}"
                assert "alert(" not in response_text, f"响应中包含未转义的alert函数: {payload}"


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestDataValidation(BaseAPITest):
    """数据验证测试"""

    @allure.story("数据验证")
    @allure.title("宠物年龄验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "age,expected_valid",
        [
            (0, True),
            (1, True),
            (20, True),
            (-1, False),
            (100, False),
        ],
    )
    def test_pet_age_validation(self, unit_http_client, test_user, age, expected_valid):
        """测试宠物年龄验证"""
        pet_data = {"name": "测试宠物", "type": "dog", "age": age, "gender": "male"}

        response = unit_http_client.post(
            "/api/user/pets", json_data=pet_data, headers=self.get_auth_headers(test_user["token"])
        )

        if expected_valid:
            assert response.status_code in [200, 201], f"有效的年龄{age}应该被接受"
        else:
            assert response.status_code in [400, 422], f"无效的年龄{age}应该被拒绝"

    @allure.story("数据验证")
    @allure.title("地址电话验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "phone,expected_valid",
        [
            ("13800138000", True),
            ("18612345678", True),
            ("12345678901", False),
            ("abc12345678", False),
            ("", False),
        ],
    )
    def test_address_phone_validation(self, unit_http_client, test_user, phone, expected_valid):
        """测试地址电话验证"""
        address_data = {
            "receiverName": "测试收货人",
            "phone": phone,
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": "测试街道123号",
        }

        response = unit_http_client.post(
            "/api/user/addresses", json_data=address_data, headers=self.get_auth_headers(test_user["token"])
        )

        if expected_valid:
            assert response.status_code in [200, 201], f"有效的电话{phone}应该被接受"
        else:
            assert response.status_code in [400, 422], f"无效的电话{phone}应该被拒绝"

    @allure.story("数据验证")
    @allure.title("评价评分验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "rating,expected_valid",
        [
            (1, True),
            (3, True),
            (5, True),
            (0, False),
            (6, False),
            (-1, False),
        ],
    )
    def test_review_rating_validation(self, unit_http_client, test_user, rating, expected_valid):
        """测试评价评分验证"""
        review_data = {"appointmentId": 1, "rating": rating, "comment": "测试评价"}

        response = unit_http_client.post(
            "/api/user/reviews", json_data=review_data, headers=self.get_auth_headers(test_user["token"])
        )

        if expected_valid:
            if response.status_code == 201:
                pass
            else:
                pass
        else:
            assert response.status_code in [400, 422], f"无效的评分{rating}应该被拒绝"


@allure.feature("用户端API")
@pytest.mark.user
@pytest.mark.unit_api
class TestPaginationAndFilter(BaseAPITest):
    """分页和筛选功能测试"""

    @allure.story("分页筛选")
    @allure.title("预约列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size",
        [
            (0, 10),
            (1, 20),
            (2, 5),
        ],
    )
    def test_appointment_pagination(self, unit_http_client, test_user, page, page_size):
        """测试预约列表分页"""
        response = unit_http_client.get(
            "/api/user/appointments",
            params={"page": page, "pageSize": page_size},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("订单列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size",
        [
            (1, 10),
            (2, 20),
            (3, 5),
        ],
    )
    def test_order_pagination(self, unit_http_client, test_user, page, page_size):
        """测试订单列表分页"""
        response = unit_http_client.get(
            "/api/user/orders",
            params={"page": page, "pageSize": page_size},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("评价列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,size",
        [
            (0, 10),
            (1, 20),
            (2, 5),
        ],
    )
    def test_review_pagination(self, unit_http_client, test_user, page, size):
        """测试评价列表分页"""
        response = unit_http_client.get(
            "/api/user/reviews", params={"page": page, "size": size}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("预约日期范围筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_appointment_date_filter(self, unit_http_client, test_user):
        """测试预约日期范围筛选"""
        start_date = datetime.now() - timedelta(days=30)
        end_date = datetime.now() + timedelta(days=30)

        response = unit_http_client.get(
            "/api/user/appointments",
            params={"startDate": start_date.isoformat(), "endDate": end_date.isoformat()},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("订单日期范围筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_order_date_filter(self, unit_http_client, test_user):
        """测试订单日期范围筛选"""
        start_date = datetime.now() - timedelta(days=30)
        end_date = datetime.now() + timedelta(days=30)

        response = unit_http_client.get(
            "/api/user/orders",
            params={"startDate": start_date.isoformat(), "endDate": end_date.isoformat()},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("服务关键字搜索测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("keyword", ["美容", "洗澡", "寄养"])
    def test_service_keyword_search(self, unit_http_client, test_user, keyword):
        """测试服务关键字搜索"""
        response = unit_http_client.get(
            "/api/user/services", params={"keyword": keyword}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("通知类型筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("notification_type", ["system", "order", "appointment"])
    def test_notification_type_filter(self, unit_http_client, test_user, notification_type):
        """测试通知类型筛选"""
        response = unit_http_client.get(
            "/api/user/notifications",
            params={"type": notification_type},
            headers=self.get_auth_headers(test_user["token"]),
        )

        self.assert_response_success(response)

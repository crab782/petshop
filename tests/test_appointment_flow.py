"""
服务订单流程测试
测试服务预约的状态流转和业务逻辑
"""

from datetime import datetime, timedelta

import allure
import pytest

from tests.fixtures.test_data import TestData, TestDataGenerator, setup_test_environment, teardown_test_environment
from tests.utils import HTTPClient, TokenManager, log_response_details


@allure.feature("服务订单模块")
@pytest.mark.appointment
class TestAppointmentFlow:
    """服务订单流程测试"""

    @pytest.fixture(scope="class")
    def test_data(self, http_client: HTTPClient):
        """准备测试数据"""
        test_data = setup_test_environment(
            create_services=True,
            create_products=False,
            create_pets=True,
            create_addresses=False,
            create_appointments=False,
            create_orders=False,
        )

        yield test_data

        teardown_test_environment(test_data)

    @allure.story("创建预约")
    @allure.title("测试创建服务预约")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.user
    def test_create_appointment(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建服务预约

        Steps:
        1. 用户创建预约
        2. 验证状态为pending
        3. 验证返回预约ID
        """
        if not test_data.service_ids or not test_data.pet_ids:
            pytest.skip("缺少必要的服务或宠物数据")

        service_id = test_data.service_ids[0]
        pet_id = test_data.pet_ids[0]

        appointment_time = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d %H:%M:%S")

        appointment_data = {
            "serviceId": service_id,
            "petId": pet_id,
            "appointmentTime": appointment_time,
            "notes": "测试预约备注",
        }

        response = http_client.post("/api/user/appointments", json_data=appointment_data, user_type="user")

        assert response.status_code in [200, 201], f"创建预约失败，状态码: {response.status_code}"

        response_data = response.json()
        assert "data" in response_data, "响应中没有data字段"

        appointment_id = response_data.get("data", {}).get("id") or response_data.get("data", {}).get("appointmentId")

        assert appointment_id is not None, "返回的预约ID为空"

        appointment_status = response_data.get("data", {}).get("status")
        assert appointment_status == "pending", f"预约状态应为pending，实际为: {appointment_status}"

        test_data.appointment_ids.append(appointment_id)

        log_response_details(response)

    @allure.story("商家确认预约")
    @allure.title("测试商家确认预约")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_confirm_appointment(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家确认预约

        Steps:
        1. 商家确认pending状态的预约
        2. 验证状态变为confirmed
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )

        assert response.status_code == 200, f"商家确认预约失败，状态码: {response.status_code}"

        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")

        assert updated_status == "confirmed", f"预约状态应为confirmed，实际为: {updated_status}"

        log_response_details(response)

    @allure.story("商家完成服务")
    @allure.title("测试商家完成服务")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.merchant
    def test_merchant_complete_appointment(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家完成服务

        Steps:
        1. 商家完成confirmed状态的预约
        2. 验证状态变为completed
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        confirm_response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )

        assert confirm_response.status_code == 200, "确认预约失败"

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "completed"},
            user_type="merchant",
        )

        assert response.status_code == 200, f"商家完成服务失败，状态码: {response.status_code}"

        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")

        assert updated_status == "completed", f"预约状态应为completed，实际为: {updated_status}"

        log_response_details(response)

    @allure.story("用户取消预约")
    @allure.title("测试用户取消pending预约")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.user
    def test_user_cancel_pending_appointment(self, http_client: HTTPClient, test_data: TestData):
        """
        测试用户取消pending预约

        Steps:
        1. 用户取消pending状态的预约
        2. 验证状态变为cancelled
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        response = http_client.put(f"/api/user/appointments/{appointment_id}/cancel", user_type="user")

        assert response.status_code == 200, f"用户取消预约失败，状态码: {response.status_code}"

        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")

        assert updated_status == "cancelled", f"预约状态应为cancelled，实际为: {updated_status}"

        log_response_details(response)

    @allure.story("商家取消预约")
    @allure.title("测试商家取消confirmed预约")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.merchant
    def test_merchant_cancel_confirmed_appointment(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家取消confirmed预约

        Steps:
        1. 商家取消confirmed状态的预约
        2. 验证状态变为cancelled
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        confirm_response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )

        assert confirm_response.status_code == 200, "确认预约失败"

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "cancelled"},
            user_type="merchant",
        )

        assert response.status_code == 200, f"商家取消预约失败，状态码: {response.status_code}"

        response_data = response.json()
        updated_status = response_data.get("data", {}).get("status")

        assert updated_status == "cancelled", f"预约状态应为cancelled，实际为: {updated_status}"

        log_response_details(response)

    @allure.story("非法状态转换")
    @allure.title("测试非法状态转换 - 已完成")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_status_transition_completed(self, http_client: HTTPClient, test_data: TestData):
        """
        测试非法状态转换（已完成）

        Steps:
        1. 尝试修改completed状态的预约
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        confirm_response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )
        assert confirm_response.status_code == 200, "确认预约失败"

        complete_response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "completed"},
            user_type="merchant",
        )
        assert complete_response.status_code == 200, "完成预约失败"

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "cancelled"},
            user_type="merchant",
        )

        assert response.status_code in [400, 403, 409], f"预期状态码400/403/409，实际状态码: {response.status_code}"

        response_data = response.json()
        assert "message" in response_data or "error" in response_data, "响应中没有错误信息"

        log_response_details(response)

    @allure.story("非法状态转换")
    @allure.title("测试非法状态转换 - 已取消")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_status_transition_cancelled(self, http_client: HTTPClient, test_data: TestData):
        """
        测试非法状态转换（已取消）

        Steps:
        1. 尝试修改cancelled状态的预约
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        cancel_response = http_client.put(f"/api/user/appointments/{appointment_id}/cancel", user_type="user")
        assert cancel_response.status_code == 200, "取消预约失败"

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )

        assert response.status_code in [400, 403, 409], f"预期状态码400/403/409，实际状态码: {response.status_code}"

        response_data = response.json()
        assert "message" in response_data or "error" in response_data, "响应中没有错误信息"

        log_response_details(response)

    @allure.story("完整流程")
    @allure.title("测试完整预约流程")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_complete_appointment_flow(self, http_client: HTTPClient, test_data: TestData):
        """
        测试完整预约流程

        Steps:
        1. 创建预约 → pending
        2. 商家确认 → confirmed
        3. 商家完成 → completed
        4. 验证每个步骤的状态转换
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        get_response = http_client.get(f"/api/user/appointments/{appointment_id}", user_type="user")

        if get_response.status_code == 200:
            initial_status = get_response.json().get("data", {}).get("status")
            assert initial_status == "pending", f"初始状态应为pending，实际为: {initial_status}"

        confirm_response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )

        assert confirm_response.status_code == 200, "确认预约失败"
        confirmed_status = confirm_response.json().get("data", {}).get("status")
        assert confirmed_status == "confirmed", f"确认后状态应为confirmed，实际为: {confirmed_status}"

        complete_response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "completed"},
            user_type="merchant",
        )

        assert complete_response.status_code == 200, "完成预约失败"
        completed_status = complete_response.json().get("data", {}).get("status")
        assert completed_status == "completed", f"完成后状态应为completed，实际为: {completed_status}"

        log_response_details(complete_response)


@allure.feature("服务订单模块")
@pytest.mark.appointment
class TestAppointmentDataIsolation:
    """服务订单数据隔离测试"""

    @pytest.fixture(scope="class")
    def test_data(self, http_client: HTTPClient):
        """准备测试数据"""
        test_data = setup_test_environment(
            create_services=True,
            create_products=False,
            create_pets=True,
            create_addresses=False,
            create_appointments=False,
            create_orders=False,
        )

        yield test_data

        teardown_test_environment(test_data)

    @allure.story("数据隔离")
    @allure.title("测试用户只能操作自己的预约")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_user_can_only_operate_own_appointments(self, http_client: HTTPClient, test_data: TestData):
        """
        测试用户只能操作自己的预约

        Steps:
        1. 创建另一个用户
        2. 第一个用户创建预约
        3. 第二个用户尝试操作第一个用户的预约
        4. 验证操作失败
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        other_user = generator.create_test_user()
        if not other_user:
            pytest.skip("无法创建第二个测试用户")

        http_client.token_manager.set_token("user", other_user.get("token"))

        response = http_client.put(f"/api/user/appointments/{appointment_id}/cancel", user_type="user")

        assert response.status_code in [403, 404], f"预期状态码403或404，实际状态码: {response.status_code}"

        log_response_details(response)

    @allure.story("数据隔离")
    @allure.title("测试商家只能操作自己的预约")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_merchant_can_only_operate_own_appointments(self, http_client: HTTPClient, test_data: TestData):
        """
        测试商家只能操作自己的预约

        Steps:
        1. 创建另一个商家
        2. 第一个商家的店铺创建预约
        3. 第二个商家尝试操作第一个商家的预约
        4. 验证操作失败
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        other_merchant = generator.create_test_merchant()
        if not other_merchant:
            pytest.skip("无法创建第二个测试商家")

        http_client.token_manager.set_token("merchant", other_merchant.get("token"))

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "confirmed"},
            user_type="merchant",
        )

        assert response.status_code in [403, 404], f"预期状态码403或404，实际状态码: {response.status_code}"

        log_response_details(response)


@allure.feature("服务订单模块")
@pytest.mark.appointment
class TestAppointmentEdgeCases:
    """服务订单边界情况测试"""

    @pytest.fixture(scope="class")
    def test_data(self, http_client: HTTPClient):
        """准备测试数据"""
        test_data = setup_test_environment(
            create_services=True,
            create_products=False,
            create_pets=True,
            create_addresses=False,
            create_appointments=False,
            create_orders=False,
        )

        yield test_data

        teardown_test_environment(test_data)

    @allure.story("边界情况")
    @allure.title("测试创建预约 - 不存在的服务")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_appointment_nonexistent_service(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建预约 - 不存在的服务

        Steps:
        1. 使用不存在的服务ID创建预约
        2. 验证返回错误
        """
        if not test_data.pet_ids:
            pytest.skip("缺少宠物数据")

        appointment_data = {
            "serviceId": 99999,
            "petId": test_data.pet_ids[0],
            "appointmentTime": "2024-12-31 14:00:00",
            "notes": "测试预约",
        }

        response = http_client.post("/api/user/appointments", json_data=appointment_data, user_type="user")

        assert response.status_code in [400, 404], f"预期状态码400或404，实际状态码: {response.status_code}"

        log_response_details(response)

    @allure.story("边界情况")
    @allure.title("测试创建预约 - 不存在的宠物")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_appointment_nonexistent_pet(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建预约 - 不存在的宠物

        Steps:
        1. 使用不存在的宠物ID创建预约
        2. 验证返回错误
        """
        if not test_data.service_ids:
            pytest.skip("缺少服务数据")

        appointment_data = {
            "serviceId": test_data.service_ids[0],
            "petId": 99999,
            "appointmentTime": "2024-12-31 14:00:00",
            "notes": "测试预约",
        }

        response = http_client.post("/api/user/appointments", json_data=appointment_data, user_type="user")

        assert response.status_code in [400, 404], f"预期状态码400或404，实际状态码: {response.status_code}"

        log_response_details(response)

    @allure.story("边界情况")
    @allure.title("测试创建预约 - 过去的时间")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_appointment_past_time(self, http_client: HTTPClient, test_data: TestData):
        """
        测试创建预约 - 过去的时间

        Steps:
        1. 使用过去的时间创建预约
        2. 验证返回错误
        """
        if not test_data.service_ids or not test_data.pet_ids:
            pytest.skip("缺少必要的服务或宠物数据")

        past_time = (datetime.now() - timedelta(days=1)).strftime("%Y-%m-%d %H:%M:%S")

        appointment_data = {
            "serviceId": test_data.service_ids[0],
            "petId": test_data.pet_ids[0],
            "appointmentTime": past_time,
            "notes": "测试预约",
        }

        response = http_client.post("/api/user/appointments", json_data=appointment_data, user_type="user")

        assert response.status_code in [400, 422], f"预期状态码400或422，实际状态码: {response.status_code}"

        log_response_details(response)

    @allure.story("边界情况")
    @allure.title("测试创建预约 - 缺少必填字段")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_appointment_missing_fields(self, http_client: HTTPClient):
        """
        测试创建预约 - 缺少必填字段

        Steps:
        1. 不提供服务ID创建预约
        2. 验证返回错误
        """
        appointment_data = {"petId": 1, "appointmentTime": "2024-12-31 14:00:00", "notes": "测试预约"}

        response = http_client.post("/api/user/appointments", json_data=appointment_data, user_type="user")

        assert response.status_code in [400, 422], f"预期状态码400或422，实际状态码: {response.status_code}"

        log_response_details(response)

    @allure.story("边界情况")
    @allure.title("测试状态转换 - 无效的状态值")
    @allure.severity(allure.severity_level.NORMAL)
    def test_invalid_status_value(self, http_client: HTTPClient, test_data: TestData):
        """
        测试状态转换 - 无效的状态值

        Steps:
        1. 尝试将预约状态设置为无效值
        2. 验证返回错误
        """
        generator = TestDataGenerator(http_client)
        generator.test_data = test_data

        appointment = generator.create_test_appointment()
        if not appointment:
            pytest.skip("无法创建测试预约")

        appointment_id = appointment.get("id")

        response = http_client.put(
            f"/api/merchant/appointments/{appointment_id}/status",
            json_data={"status": "invalid_status"},
            user_type="merchant",
        )

        assert response.status_code in [400, 422], f"预期状态码400或422，实际状态码: {response.status_code}"

        log_response_details(response)

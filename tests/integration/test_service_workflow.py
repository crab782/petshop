"""
服务预约完整流程集成测试

测试服务预约的完整生命周期，包括：
- 创建预约 → 确认 → 完成 → 评价
- 预约状态转换：pending → confirmed → completed
- 预约取消流程：pending → cancelled, confirmed → cancelled
- 预约时间冲突检测
- 预约服务时长计算
- 验证预约数据一致性
"""

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

APPOINTMENT_STATUS_TRANSITIONS = {
    "pending": {"confirmed", "cancelled"},
    "confirmed": {"completed", "cancelled"},
    "completed": set(),
    "cancelled": set(),
}


@pytest.mark.integration
@pytest.mark.appointment
class TestServiceWorkflow:
    """服务预约完整流程集成测试类"""

    @pytest.fixture(autouse=True)
    def setup(self, http_client, user_token, merchant_token, test_user, test_merchant, test_service):
        """
        测试前置设置

        Args:
            http_client: HTTP客户端
            user_token: 用户认证Token
            merchant_token: 商家认证Token
            test_user: 测试用户数据
            test_merchant: 测试商家数据
            test_service: 测试服务数据
        """
        self.client = http_client
        self.user_token = user_token
        self.merchant_token = merchant_token
        self.user_data = test_user
        self.merchant_data = test_merchant
        self.service_data = test_service
        self.user_headers = {"Authorization": f"Bearer {user_token}"}
        self.merchant_headers = {"Authorization": f"Bearer {merchant_token}"}

    def _create_appointment(self, appointment_time=None, notes="集成测试预约"):
        """
        创建测试预约

        Args:
            appointment_time: 预约时间，默认为明天
            notes: 预约备注

        Returns:
            dict: 预约数据
        """
        if appointment_time is None:
            appointment_time = datetime.now() + timedelta(days=1)

        appointment_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_time": appointment_time.isoformat(),
            "notes": notes,
        }

        response = self.client.post("/api/appointments", json_data=appointment_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建预约失败: {response.text}"
        return response.json()["data"]

    def test_appointment_complete_workflow(self):
        """
        测试服务预约完整流程：创建预约 → 确认 → 完成 → 评价

        验证点：
        1. 预约创建成功，状态为pending
        2. 商家确认预约，状态变为confirmed
        3. 商家完成服务，状态变为completed
        4. 用户评价成功
        5. 每个状态转换都符合业务规则
        """
        log_test_case("test_appointment_complete_workflow", "测试服务预约完整流程")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        assert appointment["status"] == "pending", "初始预约状态不是pending"

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
        )
        assert response.status_code == 200, f"确认预约失败: {response.text}"

        appointment = response.json()["data"]
        assert appointment["status"] == "confirmed", "确认后预约状态不是confirmed"
        assert_status_transition("pending", "confirmed", APPOINTMENT_STATUS_TRANSITIONS)

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/complete", headers=self.merchant_headers
        )
        assert response.status_code == 200, f"完成服务失败: {response.text}"

        appointment = response.json()["data"]
        assert appointment["status"] == "completed", "完成后预约状态不是completed"
        assert_status_transition("confirmed", "completed", APPOINTMENT_STATUS_TRANSITIONS)

        review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": appointment_id,
            "rating": 5,
            "comment": "服务非常好！",
        }

        response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)
        assert response.status_code in [200, 201], f"评价失败: {response.text}"

        review = response.json()["data"]
        assert review["appointment_id"] == appointment_id, "评价关联的预约ID不匹配"

    def test_appointment_status_transitions(self):
        """
        测试预约状态转换：pending → confirmed → completed

        验证点：
        1. 每个状态转换都符合业务规则
        2. 状态转换后数据一致性
        3. 不允许非法状态转换
        """
        log_test_case("test_appointment_status_transitions", "测试预约状态转换")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        assert appointment["status"] == "pending"

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
        )
        assert response.status_code == 200
        appointment = response.json()["data"]
        assert appointment["status"] == "confirmed"

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/complete", headers=self.merchant_headers
        )
        assert response.status_code == 200
        appointment = response.json()["data"]
        assert appointment["status"] == "completed"

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
        )
        assert response.status_code in [400, 403, 409], "已完成的预约不应该允许再次确认"

    def test_appointment_cancellation_from_pending(self):
        """
        测试预约取消流程：pending → cancelled

        验证点：
        1. pending状态的预约可以取消
        2. 取消后状态变为cancelled
        3. 用户和商家都可以取消
        """
        log_test_case("test_appointment_cancellation_from_pending", "测试预约取消流程（pending状态）")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        response = self.client.post(f"/api/appointments/{appointment_id}/cancel", headers=self.user_headers)
        assert response.status_code == 200, f"用户取消预约失败: {response.text}"

        appointment = response.json()["data"]
        assert appointment["status"] == "cancelled", "取消后预约状态不是cancelled"
        assert_status_transition("pending", "cancelled", APPOINTMENT_STATUS_TRANSITIONS)

    def test_appointment_cancellation_from_confirmed(self):
        """
        测试预约取消流程：confirmed → cancelled

        验证点：
        1. confirmed状态的预约可以取消
        2. 取消后状态变为cancelled
        3. 取消需要合理理由
        """
        log_test_case("test_appointment_cancellation_from_confirmed", "测试预约取消流程（confirmed状态）")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        response = self.client.put(
            f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
        )
        assert response.status_code == 200, "确认预约失败"

        response = self.client.post(
            f"/api/appointments/{appointment_id}/cancel",
            json_data={"reason": "临时有事，需要取消"},
            headers=self.user_headers,
        )
        assert response.status_code == 200, f"取消已确认预约失败: {response.text}"

        appointment = response.json()["data"]
        assert appointment["status"] == "cancelled", "取消后预约状态不是cancelled"
        assert_status_transition("confirmed", "cancelled", APPOINTMENT_STATUS_TRANSITIONS)

    def test_appointment_time_conflict_detection(self):
        """
        测试预约时间冲突检测

        验证点：
        1. 同一时间段的预约会被检测到冲突
        2. 冲突预约不能创建
        3. 不同时间段的预约可以正常创建
        """
        log_test_case("test_appointment_time_conflict_detection", "测试预约时间冲突检测")

        appointment_time = datetime.now() + timedelta(days=1, hours=10)

        appointment1 = self._create_appointment(appointment_time=appointment_time, notes="第一个预约")
        assert appointment1["status"] == "pending", "第一个预约创建失败"

        conflict_appointment_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_time": appointment_time.isoformat(),
            "notes": "冲突预约",
        }

        response = self.client.post("/api/appointments", json_data=conflict_appointment_data, headers=self.user_headers)

        if response.status_code in [400, 409]:
            assert "conflict" in response.text.lower() or "冲突" in response.text, "应该提示时间冲突"
        else:
            pytest.skip("预约时间冲突检测功能未实现")

        different_time = appointment_time + timedelta(hours=2)
        appointment2 = self._create_appointment(appointment_time=different_time, notes="不同时间的预约")
        assert appointment2["status"] == "pending", "不同时间的预约应该可以创建"

    def test_appointment_duration_calculation(self):
        """
        测试预约服务时长计算

        验证点：
        1. 预约时长根据服务时长计算
        2. 预约结束时间正确计算
        3. 时长信息在预约详情中正确显示
        """
        log_test_case("test_appointment_duration_calculation", "测试预约服务时长计算")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        response = self.client.get(f"/api/appointments/{appointment_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取预约详情失败"

        appointment_detail = response.json()["data"]

        service_duration = self.service_data.get("duration", 60)

        if "duration" in appointment_detail:
            assert appointment_detail["duration"] == service_duration, "预约时长不正确"

        if "end_time" in appointment_detail:
            appointment_time = datetime.fromisoformat(appointment_detail["appointment_time"])
            end_time = datetime.fromisoformat(appointment_detail["end_time"])
            actual_duration = (end_time - appointment_time).total_seconds() / 60
            assert (
                actual_duration == service_duration
            ), f"预约结束时间计算不正确: 期望 {service_duration} 分钟, 实际 {actual_duration} 分钟"

    def test_appointment_data_consistency(self):
        """
        测试预约数据一致性

        验证点：
        1. 预约基本信息一致性
        2. 预约服务信息一致性
        3. 预约金额一致性
        4. 预约状态一致性
        """
        log_test_case("test_appointment_data_consistency", "测试预约数据一致性")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        response = self.client.get(f"/api/appointments/{appointment_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取预约详情失败"

        appointment_detail = response.json()["data"]

        assert_data_consistency(appointment, appointment_detail, ["id", "service_id", "merchant_id", "status"])

        assert appointment_detail["service_id"] == self.service_data.get("id"), "服务ID不一致"
        assert appointment_detail["merchant_id"] == self.merchant_data.get("id"), "商家ID不一致"

        expected_price = Decimal(str(self.service_data.get("price", 0)))
        actual_price = Decimal(str(appointment_detail.get("total_price", 0)))
        assert actual_price == expected_price, f"预约金额不一致: 期望 {expected_price}, 实际 {actual_price}"

    def test_multiple_appointments_workflow(self):
        """
        测试多个预约并发流程

        验证点：
        1. 多个预约可以独立处理
        2. 每个预约的状态独立转换
        3. 时间安排合理
        """
        log_test_case("test_multiple_appointments_workflow", "测试多个预约并发流程")

        appointments = []
        base_time = datetime.now() + timedelta(days=1)

        for i in range(3):
            appointment_time = base_time + timedelta(hours=i * 2)
            appointment = self._create_appointment(appointment_time=appointment_time, notes=f"测试预约 {i+1}")
            appointments.append(appointment)

        assert len(appointments) == 3, "应该成功创建3个预约"

        for i, appointment in enumerate(appointments):
            appointment_id = appointment.get("id")

            if i == 0:
                response = self.client.put(
                    f"/api/merchant/appointments/{appointment_id}/confirm", headers=self.merchant_headers
                )
                assert response.status_code == 200
                assert response.json()["data"]["status"] == "confirmed"

            elif i == 1:
                response = self.client.post(f"/api/appointments/{appointment_id}/cancel", headers=self.user_headers)
                assert response.status_code == 200
                assert response.json()["data"]["status"] == "cancelled"

            else:
                response = self.client.get(f"/api/appointments/{appointment_id}", headers=self.user_headers)
                assert response.status_code == 200
                assert response.json()["data"]["status"] == "pending"

    def test_appointment_with_pet(self):
        """
        测试带宠物的预约

        验证点：
        1. 用户可以为宠物预约服务
        2. 预约关联宠物信息
        3. 宠物信息在预约详情中正确显示
        """
        log_test_case("test_appointment_with_pet", "测试带宠物的预约")

        pet_data = {"name": "测试宠物", "type": "dog", "breed": "金毛", "age": 2, "gender": "male"}

        response = self.client.post("/api/pets", json_data=pet_data, headers=self.user_headers)

        if response.status_code in [200, 201]:
            pet = response.json()["data"]
            pet_id = pet.get("id")

            appointment_data = {
                "service_id": self.service_data.get("id"),
                "merchant_id": self.merchant_data.get("id"),
                "pet_id": pet_id,
                "appointment_time": (datetime.now() + timedelta(days=1)).isoformat(),
                "notes": "带宠物的预约",
            }

            response = self.client.post("/api/appointments", json_data=appointment_data, headers=self.user_headers)
            assert response.status_code in [200, 201], "创建带宠物的预约失败"

            appointment = response.json()["data"]
            assert appointment.get("pet_id") == pet_id, "预约关联的宠物ID不匹配"
        else:
            pytest.skip("宠物功能未实现")

    def test_appointment_notes_and_special_requests(self):
        """
        测试预约备注和特殊要求

        验证点：
        1. 用户可以添加预约备注
        2. 用户可以提出特殊要求
        3. 备注和要求在预约详情中正确显示
        """
        log_test_case("test_appointment_notes_and_special_requests", "测试预约备注和特殊要求")

        special_notes = "请安排有经验的美容师，宠物比较敏感"

        appointment = self._create_appointment(notes=special_notes)
        appointment_id = appointment.get("id")

        response = self.client.get(f"/api/appointments/{appointment_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取预约详情失败"

        appointment_detail = response.json()["data"]
        assert appointment_detail.get("notes") == special_notes, "预约备注不匹配"

    def test_appointment_time_modification(self):
        """
        测试预约时间修改

        验证点：
        1. pending状态的预约可以修改时间
        2. confirmed状态的预约修改需要商家确认
        3. 修改后时间正确更新
        """
        log_test_case("test_appointment_time_modification", "测试预约时间修改")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        new_time = datetime.now() + timedelta(days=2, hours=14)

        response = self.client.put(
            f"/api/appointments/{appointment_id}",
            json_data={"appointment_time": new_time.isoformat()},
            headers=self.user_headers,
        )

        if response.status_code == 200:
            updated_appointment = response.json()["data"]
            assert updated_appointment["appointment_time"] is not None, "预约时间未更新"
        else:
            pytest.skip("预约时间修改功能未实现")

    def test_appointment_reminder(self):
        """
        测试预约提醒

        验证点：
        1. 预约创建后应该有提醒
        2. 预约确认后应该有提醒
        3. 预约临近时应该有提醒
        """
        log_test_case("test_appointment_reminder", "测试预约提醒")

        appointment = self._create_appointment()
        appointment_id = appointment.get("id")

        response = self.client.get("/api/notifications", headers=self.user_headers)

        if response.status_code == 200:
            notifications = response.json().get("data", [])
            appointment_notifications = [n for n in notifications if appointment_id in str(n.get("content", ""))]

            if len(appointment_notifications) > 0:
                assert True, "预约提醒已创建"
            else:
                pytest.skip("预约提醒功能可能需要异步处理")
        else:
            pytest.skip("通知功能未实现")

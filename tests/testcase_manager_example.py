"""
测试用例管理器使用示例

展示如何在实际测试中使用TestCaseManager和ExecutionStrategy。
"""

import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from testcase_manager import ExecutionStrategy, ExecutionStrategyType, TestCaseManager, get_test_case_manager, test_case

manager = get_test_case_manager()


@test_case(
    test_id="test_user_login_success",
    test_name="用户登录成功",
    test_module="auth",
    test_tags=["smoke", "auth", "P0"],
    test_priority="P0",
    test_description="测试用户使用正确的用户名和密码登录",
    test_steps=["发送登录请求", "验证响应状态码为200", "验证返回Token不为空"],
    test_expected_result="登录成功，返回Token",
    test_timeout=30,
)
def test_user_login_success(http_client, user_credentials):
    """测试用户登录成功"""
    response = http_client.post(
        "/api/auth/login", json_data={"phone": user_credentials["phone"], "password": user_credentials["password"]}
    )

    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert "token" in data["data"]

    manager.record_execution("test_user_login_success", "passed", execution_time=response.elapsed.total_seconds())


@test_case(
    test_id="test_user_login_invalid_password",
    test_name="用户登录失败-密码错误",
    test_module="auth",
    test_tags=["regression", "auth", "P1"],
    test_priority="P1",
    test_description="测试用户使用错误密码登录",
    test_steps=["发送登录请求（错误密码）", "验证响应状态码为401", "验证错误消息"],
    test_expected_result="登录失败，返回错误信息",
    test_dependencies=["test_user_login_success"],
    test_timeout=30,
)
def test_user_login_invalid_password(http_client, user_credentials):
    """测试用户登录失败-密码错误"""
    response = http_client.post(
        "/api/auth/login", json_data={"phone": user_credentials["phone"], "password": "wrongpassword"}
    )

    assert response.status_code == 401

    manager.record_execution(
        "test_user_login_invalid_password", "passed", execution_time=response.elapsed.total_seconds()
    )


@test_case(
    test_id="test_merchant_create_service",
    test_name="商家创建服务",
    test_module="merchant",
    test_tags=["regression", "api", "P1"],
    test_priority="P1",
    test_description="测试商家创建新服务",
    test_steps=["发送创建服务请求", "验证响应状态码为201", "验证服务数据正确"],
    test_expected_result="服务创建成功",
    test_timeout=30,
)
def test_merchant_create_service(http_client, merchant_token, service_data):
    """测试商家创建服务"""
    response = http_client.post(
        "/api/merchant/services", headers={"Authorization": f"Bearer {merchant_token}"}, json_data=service_data
    )

    assert response.status_code == 201
    data = response.json()
    assert "data" in data
    assert "id" in data["data"]

    manager.record_execution("test_merchant_create_service", "passed", execution_time=response.elapsed.total_seconds())


@test_case(
    test_id="test_user_book_appointment",
    test_name="用户预约服务",
    test_module="user",
    test_tags=["regression", "api", "P1"],
    test_priority="P1",
    test_description="测试用户预约服务",
    test_steps=["发送预约请求", "验证响应状态码为201", "验证预约数据正确"],
    test_expected_result="预约成功",
    test_dependencies=["test_user_login_success", "test_merchant_create_service"],
    test_timeout=30,
)
def test_user_book_appointment(http_client, user_token, appointment_data):
    """测试用户预约服务"""
    response = http_client.post(
        "/api/user/appointments", headers={"Authorization": f"Bearer {user_token}"}, json_data=appointment_data
    )

    assert response.status_code == 201
    data = response.json()
    assert "data" in data
    assert "id" in data["data"]

    manager.record_execution("test_user_book_appointment", "passed", execution_time=response.elapsed.total_seconds())


@test_case(
    test_id="test_admin_get_statistics",
    test_name="管理员获取统计数据",
    test_module="admin",
    test_tags=["smoke", "api", "P0"],
    test_priority="P0",
    test_description="测试管理员获取平台统计数据",
    test_steps=["发送获取统计数据请求", "验证响应状态码为200", "验证统计数据格式正确"],
    test_expected_result="返回统计数据",
    test_timeout=30,
)
def test_admin_get_statistics(http_client, admin_token):
    """测试管理员获取统计数据"""
    response = http_client.get("/api/admin/statistics", headers={"Authorization": f"Bearer {admin_token}"})

    assert response.status_code == 200
    data = response.json()
    assert "data" in data

    manager.record_execution("test_admin_get_statistics", "passed", execution_time=response.elapsed.total_seconds())


class TestUserRegistration:
    """用户注册测试类"""

    @test_case(
        test_id="test_user_register_success",
        test_name="用户注册成功",
        test_module="auth",
        test_tags=["smoke", "auth", "P0"],
        test_priority="P0",
        test_description="测试用户注册成功",
        test_steps=["发送注册请求", "验证响应状态码为201", "验证用户数据正确"],
        test_expected_result="注册成功",
        test_timeout=30,
    )
    def test_register_success(self, http_client, user_registration_data):
        """测试用户注册成功"""
        response = http_client.post("/api/auth/register", json_data=user_registration_data)

        assert response.status_code == 201
        data = response.json()
        assert "data" in data
        assert "id" in data["data"]

        manager.record_execution(
            "test_user_register_success", "passed", execution_time=response.elapsed.total_seconds()
        )

    @test_case(
        test_id="test_user_register_duplicate_phone",
        test_name="用户注册失败-手机号已存在",
        test_module="auth",
        test_tags=["regression", "auth", "P2"],
        test_priority="P2",
        test_description="测试用户注册时手机号已存在",
        test_steps=["发送注册请求（已存在的手机号）", "验证响应状态码为400", "验证错误消息"],
        test_expected_result="注册失败，返回错误信息",
        test_dependencies=["test_user_register_success"],
        test_timeout=30,
    )
    def test_register_duplicate_phone(self, http_client, user_registration_data):
        """测试用户注册失败-手机号已存在"""
        response = http_client.post("/api/auth/register", json_data=user_registration_data)

        assert response.status_code == 400

        manager.record_execution(
            "test_user_register_duplicate_phone", "passed", execution_time=response.elapsed.total_seconds()
        )


def test_execution_strategy_example():
    """执行策略使用示例"""
    manager = get_test_case_manager()
    strategy = ExecutionStrategy(manager)

    print("\n=== 执行策略示例 ===")

    print("\n1. 创建顺序执行计划:")
    sequential_plan = strategy.create_execution_plan(ExecutionStrategyType.SEQUENTIAL, tags=["smoke"])
    print(f"   计划ID: {sequential_plan.plan_id}")
    print(f"   测试数量: {len(sequential_plan.test_cases)}")
    print(f"   执行顺序: {sequential_plan.execution_order}")

    print("\n2. 创建优先级执行计划:")
    priority_plan = strategy.create_execution_plan(ExecutionStrategyType.PRIORITY_BASED, module="auth")
    print(f"   计划ID: {priority_plan.plan_id}")
    print(f"   测试数量: {len(priority_plan.test_cases)}")
    print(f"   执行顺序: {priority_plan.execution_order}")

    print("\n3. 创建依赖关系执行计划:")
    dependency_plan = strategy.create_execution_plan(ExecutionStrategyType.DEPENDENCY_BASED)
    print(f"   计划ID: {dependency_plan.plan_id}")
    print(f"   测试数量: {len(dependency_plan.test_cases)}")
    print(f"   执行顺序: {dependency_plan.execution_order}")

    print("\n4. 测试统计信息:")
    stats = manager.get_test_statistics()
    print(f"   总测试数: {stats['total']}")
    print(f"   按优先级: {stats['by_priority']}")
    print(f"   按模块: {stats['by_module']}")
    print(f"   按标记: {stats['by_tag']}")


def test_export_import_example():
    """导出导入示例"""
    manager = get_test_case_manager()

    print("\n=== 导出导入示例 ===")

    print("\n1. 导出测试用例:")
    exported_json = manager.export_test_cases("json")
    print(f"   导出的测试用例数量: {len(manager._test_cases)}")
    print(f"   JSON长度: {len(exported_json)} 字符")

    print("\n2. 创建新管理器并导入:")
    new_manager = TestCaseManager()
    imported_count = new_manager.import_test_cases(exported_json, "json")
    print(f"   成功导入: {imported_count} 个测试用例")

    print("\n3. 验证导入结果:")
    original_stats = manager.get_test_statistics()
    imported_stats = new_manager.get_test_statistics()
    print(f"   原始测试数: {original_stats['total']}")
    print(f"   导入后测试数: {imported_stats['total']}")
    assert original_stats["total"] == imported_stats["total"]
    print("   ✓ 导入验证成功")


def test_filter_example():
    """过滤测试用例示例"""
    manager = get_test_case_manager()

    print("\n=== 过滤测试用例示例 ===")

    print("\n1. 按优先级过滤 (P0):")
    p0_tests = manager.get_test_cases_by_priority("P0")
    for test in p0_tests:
        print(f"   - {test.test_id}: {test.test_name}")

    print("\n2. 按模块过滤 (auth):")
    auth_tests = manager.get_test_cases_by_module("auth")
    for test in auth_tests:
        print(f"   - {test.test_id}: {test.test_name}")

    print("\n3. 按标记过滤 (smoke):")
    smoke_tests = manager.get_test_cases_by_tag("smoke")
    for test in smoke_tests:
        print(f"   - {test.test_id}: {test.test_name}")

    print("\n4. 组合过滤 (tag=smoke AND priority=P0):")
    filtered_tests = manager.filter_test_cases(tags=["smoke"], priority="P0")
    for test in filtered_tests:
        print(f"   - {test.test_id}: {test.test_name}")

    print("\n5. 组合过滤 (tag=regression AND module=auth):")
    filtered_tests = manager.filter_test_cases(tags=["regression"], module="auth")
    for test in filtered_tests:
        print(f"   - {test.test_id}: {test.test_name}")


if __name__ == "__main__":
    print("=" * 60)
    print("测试用例管理器使用示例")
    print("=" * 60)

    test_execution_strategy_example()
    test_export_import_example()
    test_filter_example()

    print("\n" + "=" * 60)
    print("示例运行完成")
    print("=" * 60)

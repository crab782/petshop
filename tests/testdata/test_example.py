"""
测试数据管理模块使用示例

本文件展示了如何使用测试数据管理模块的各种功能。
"""

import pytest

from tests.testdata import TestDataBuilder, TestDataManager, cleanup_all, cleanup_merchants, cleanup_users


class TestTestDataBuilder:
    """测试数据构建器使用示例"""

    def test_build_user_with_defaults(self):
        """使用默认值构建用户数据"""
        builder = TestDataBuilder()
        user = builder.build_user()

        assert "username" in user
        assert "email" in user
        assert "password" in user
        assert user["status"] == "active"

    def test_build_user_with_custom_fields(self):
        """使用自定义字段构建用户数据"""
        builder = TestDataBuilder()
        user = builder.build_user(username="custom_user", email="custom@example.com", status="inactive")

        assert user["username"] == "custom_user"
        assert user["email"] == "custom@example.com"
        assert user["status"] == "inactive"

    def test_build_merchant(self):
        """构建商家数据"""
        builder = TestDataBuilder()
        merchant = builder.build_merchant(name="测试商家")

        assert merchant["name"] == "测试商家"
        assert merchant["status"] == "approved"

    def test_build_service(self):
        """构建服务数据"""
        builder = TestDataBuilder()
        service = builder.build_service(merchant_id=1, name="宠物美容", price=99.00)

        assert service["merchant_id"] == 1
        assert service["name"] == "宠物美容"
        assert service["price"] == 99.00

    def test_build_product(self):
        """构建商品数据"""
        builder = TestDataBuilder()
        product = builder.build_product(merchant_id=1, name="宠物食品", stock=100)

        assert product["merchant_id"] == 1
        assert product["name"] == "宠物食品"
        assert product["stock"] == 100

    def test_build_appointment(self):
        """构建预约数据"""
        builder = TestDataBuilder()
        appointment = builder.build_appointment(user_id=1, merchant_id=1, service_id=1, pet_id=1)

        assert appointment["user_id"] == 1
        assert appointment["status"] == "pending"

    def test_build_order(self):
        """构建订单数据"""
        builder = TestDataBuilder()
        order = builder.build_order(user_id=1, merchant_id=1, total_price=199.00)

        assert order["user_id"] == 1
        assert order["status"] == "pending"
        assert "order_no" in order

    def test_build_review(self):
        """构建评价数据"""
        builder = TestDataBuilder()
        review = builder.build_review(user_id=1, merchant_id=1, service_id=1, rating=5)

        assert review["user_id"] == 1
        assert review["rating"] == 5

    def test_build_pet(self):
        """构建宠物数据"""
        builder = TestDataBuilder()
        pet = builder.build_pet(user_id=1, name="小白", type="狗")

        assert pet["user_id"] == 1
        assert pet["name"] == "小白"
        assert pet["type"] == "狗"

    def test_build_address(self):
        """构建地址数据"""
        builder = TestDataBuilder()
        address = builder.build_address(user_id=1, contact_name="张三", province="北京市")

        assert address["user_id"] == 1
        assert address["contact_name"] == "张三"
        assert address["province"] == "北京市"

    def test_build_batch(self):
        """批量构建测试数据"""
        builder = TestDataBuilder()
        users = builder.build_batch("user", 5, status="active")

        assert len(users) == 5
        for user in users:
            assert user["status"] == "active"


class TestTestDataManager:
    """测试数据管理器使用示例"""

    def test_prepare_test_data(self):
        """准备测试数据"""
        manager = TestDataManager()
        user = manager.prepare_test_data("user", username="test_user")

        assert user["username"] == "test_user"
        assert manager.get_test_data(user.get("id", "default")) is not None

    def test_get_test_data(self):
        """获取测试数据"""
        manager = TestDataManager()
        user = manager.prepare_test_data("user")

        user_id = list(manager.get_all_test_data().keys())[0]
        retrieved_user = manager.get_test_data(user_id)

        assert retrieved_user is not None
        assert retrieved_user["username"] == user["username"]

    def test_cleanup_test_data(self):
        """清理测试数据"""
        manager = TestDataManager()
        user = manager.prepare_test_data("user")

        user_id = list(manager.get_all_test_data().keys())[0]
        result = manager.cleanup_test_data(user_id)

        assert result is True
        assert manager.get_test_data(user_id) is None

    def test_cleanup_all_test_data(self):
        """清理所有测试数据"""
        manager = TestDataManager()
        manager.prepare_test_data("user")
        manager.prepare_test_data("merchant")

        count = manager.cleanup_all_test_data()

        assert count == 2
        assert len(manager.get_all_test_data()) == 0

    def test_isolate_test_data_decorator(self):
        """测试数据隔离装饰器"""
        manager = TestDataManager()

        @manager.isolate_test_data
        def test_function():
            manager.prepare_test_data("user")
            manager.prepare_test_data("merchant")
            return len(manager.get_all_test_data())

        result = test_function()

        assert result == 2
        assert len(manager.get_all_test_data()) == 0

    def test_context_manager(self):
        """上下文管理器使用"""
        with TestDataManager() as manager:
            manager.prepare_test_data("user")
            manager.prepare_test_data("merchant")
            assert len(manager.get_all_test_data()) == 2

        assert len(manager.get_all_test_data()) == 0

    def test_get_test_data_by_type(self):
        """按类型获取测试数据"""
        manager = TestDataManager()
        manager.prepare_test_data("user")
        manager.prepare_test_data("user")
        manager.prepare_test_data("merchant")

        users = manager.get_test_data_by_type("user")
        merchants = manager.get_test_data_by_type("merchant")

        assert len(users) == 2
        assert len(merchants) == 1


class TestDataCleanup:
    """数据清理功能使用示例"""

    @pytest.fixture
    def mock_db_connection(self):
        """模拟数据库连接"""

        class MockConnection:
            def __init__(self):
                self.data = {}
                self.cursor_obj = MockCursor(self.data)

            def cursor(self):
                return self.cursor_obj

            def commit(self):
                pass

            def rollback(self):
                pass

        class MockCursor:
            def __init__(self, data):
                self.data = data
                self.rowcount = 0
                self.lastrowid = 0

            def execute(self, sql, params=None):
                self.rowcount = 1
                return self

            def fetchall(self):
                return []

            def close(self):
                pass

        return MockConnection()

    def test_cleanup_users(self, mock_db_connection):
        """清理用户数据"""
        result = cleanup_users([1, 2, 3], mock_db_connection)
        assert result >= 0

    def test_cleanup_merchants(self, mock_db_connection):
        """清理商家数据"""
        result = cleanup_merchants([1, 2], mock_db_connection)
        assert result >= 0

    def test_cleanup_all_with_confirm(self, mock_db_connection):
        """清理所有数据（需要确认）"""
        result = cleanup_all(mock_db_connection, confirm=True)
        assert result >= 0

    def test_cleanup_all_without_confirm(self, mock_db_connection):
        """清理所有数据（未确认，应抛出异常）"""
        with pytest.raises(ValueError, match="必须设置 confirm=True"):
            cleanup_all(mock_db_connection, confirm=False)


class TestIntegration:
    """集成测试示例"""

    def test_complete_workflow(self):
        """完整工作流程示例"""
        builder = TestDataBuilder()
        manager = TestDataManager()

        user = manager.prepare_test_data("user", username="integration_user")
        merchant = manager.prepare_test_data("merchant", name="测试商家")
        service = manager.prepare_test_data("service", merchant_id=merchant.get("id"), name="宠物美容")
        appointment = manager.prepare_test_data(
            "appointment", user_id=user.get("id"), merchant_id=merchant.get("id"), service_id=service.get("id")
        )

        assert len(manager.get_all_test_data()) == 4

        manager.cleanup_all_test_data()

        assert len(manager.get_all_test_data()) == 0


if __name__ == "__main__":
    pytest.main([__file__, "-v"])

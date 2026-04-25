"""
测试数据Fixtures使用示例

演示如何使用各种测试数据fixtures。
"""

from decimal import Decimal

import pytest


class TestUserFixtures:
    """用户fixtures测试示例"""

    def test_test_user(self, test_user):
        """测试test_user fixture"""
        assert test_user is not None
        assert "username" in test_user
        assert "email" in test_user
        assert "phone" in test_user
        assert "password" in test_user
        assert test_user["status"] == "active"

    def test_test_users(self, test_users):
        """测试test_users fixture"""
        assert len(test_users) >= 3
        for user in test_users:
            assert "username" in user
            assert "email" in user

    def test_test_user_builder(self, test_user_builder):
        """测试test_user_builder fixture"""
        custom_user = test_user_builder.build_user(username="custom_test_user", email="custom@test.com")
        assert custom_user["username"] == "custom_test_user"
        assert custom_user["email"] == "custom@test.com"

    def test_test_user_batch(self, test_user_batch):
        """测试test_user_batch fixture"""
        assert len(test_user_batch) == 10
        for user in test_user_batch:
            assert user["status"] == "active"


class TestMerchantFixtures:
    """商家fixtures测试示例"""

    def test_test_merchant(self, test_merchant):
        """测试test_merchant fixture"""
        assert test_merchant is not None
        assert "name" in test_merchant
        assert "contact_person" in test_merchant
        assert "phone" in test_merchant
        assert "email" in test_merchant
        assert test_merchant["status"] == "approved"

    def test_test_merchants(self, test_merchants):
        """测试test_merchants fixture"""
        assert len(test_merchants) >= 3
        for merchant in test_merchants:
            assert "name" in merchant
            assert "email" in merchant

    def test_test_pending_merchant(self, test_pending_merchant):
        """测试test_pending_merchant fixture"""
        assert test_pending_merchant["status"] == "pending"

    def test_test_rejected_merchant(self, test_rejected_merchant):
        """测试test_rejected_merchant fixture"""
        assert test_rejected_merchant["status"] == "rejected"


class TestServiceFixtures:
    """服务fixtures测试示例"""

    def test_test_service(self, test_service):
        """测试test_service fixture"""
        assert test_service is not None
        assert "name" in test_service
        assert "price" in test_service
        assert "duration" in test_service
        assert test_service["status"] == "enabled"

    def test_test_services(self, test_services):
        """测试test_services fixture"""
        assert len(test_services) >= 3
        for service in test_services:
            assert "name" in service
            assert "price" in service

    def test_test_grooming_service(self, test_grooming_service):
        """测试test_grooming_service fixture"""
        assert test_grooming_service["category"] == "美容"
        assert test_grooming_service["price"] == Decimal("128.00")

    def test_test_medical_service(self, test_medical_service):
        """测试test_medical_service fixture"""
        assert test_medical_service["category"] == "医疗"
        assert test_medical_service["price"] == Decimal("299.00")


class TestProductFixtures:
    """商品fixtures测试示例"""

    def test_test_product(self, test_product):
        """测试test_product fixture"""
        assert test_product is not None
        assert "name" in test_product
        assert "price" in test_product
        assert "stock" in test_product
        assert test_product["status"] == "enabled"

    def test_test_products(self, test_products):
        """测试test_products fixture"""
        assert len(test_products) >= 3
        for product in test_products:
            assert "name" in product
            assert "price" in product
            assert "stock" in product

    def test_test_in_stock_product(self, test_in_stock_product):
        """测试test_in_stock_product fixture"""
        assert test_in_stock_product["stock"] == 100
        assert test_in_stock_product["low_stock_threshold"] == 10

    def test_test_out_of_stock_product(self, test_out_of_stock_product):
        """测试test_out_of_stock_product fixture"""
        assert test_out_of_stock_product["stock"] == 0

    def test_test_food_product(self, test_food_product):
        """测试test_food_product fixture"""
        assert test_food_product["category"] == "食品"
        assert test_food_product["price"] == Decimal("128.00")


class TestOrderFixtures:
    """订单fixtures测试示例"""

    def test_test_appointment(self, test_appointment):
        """测试test_appointment fixture"""
        assert test_appointment is not None
        assert "appointment_time" in test_appointment
        assert "status" in test_appointment
        assert test_appointment["status"] == "pending"

    def test_test_product_order(self, test_product_order):
        """测试test_product_order fixture"""
        assert test_product_order is not None
        assert "order_no" in test_product_order
        assert "total_price" in test_product_order
        assert test_product_order["status"] == "pending"

    def test_test_confirmed_appointment(self, test_confirmed_appointment):
        """测试test_confirmed_appointment fixture"""
        assert test_confirmed_appointment["status"] == "confirmed"

    def test_test_completed_appointment(self, test_completed_appointment):
        """测试test_completed_appointment fixture"""
        assert test_completed_appointment["status"] == "completed"

    def test_test_paid_order(self, test_paid_order):
        """测试test_paid_order fixture"""
        assert test_paid_order["status"] == "paid"

    def test_test_shipped_order(self, test_shipped_order):
        """测试test_shipped_order fixture"""
        assert test_shipped_order["status"] == "shipped"


class TestReviewFixtures:
    """评价fixtures测试示例"""

    def test_test_review(self, test_review):
        """测试test_review fixture"""
        assert test_review is not None
        assert "rating" in test_review
        assert "comment" in test_review
        assert 1 <= test_review["rating"] <= 5

    def test_test_reviews(self, test_reviews):
        """测试test_reviews fixture"""
        assert len(test_reviews) >= 3
        for review in test_reviews:
            assert "rating" in review
            assert "comment" in review

    def test_test_five_star_review(self, test_five_star_review):
        """测试test_five_star_review fixture"""
        assert test_five_star_review["rating"] == 5

    def test_test_one_star_review(self, test_one_star_review):
        """测试test_one_star_review fixture"""
        assert test_one_star_review["rating"] == 1

    def test_test_positive_review(self, test_positive_review):
        """测试test_positive_review fixture"""
        assert test_positive_review["rating"] >= 4

    def test_test_negative_review(self, test_negative_review):
        """测试test_negative_review fixture"""
        assert test_negative_review["rating"] <= 2

    def test_test_review_with_reply(self, test_review_with_reply):
        """测试test_review_with_reply fixture"""
        assert test_review_with_reply["reply_content"] is not None
        assert test_review_with_reply["reply_time"] is not None


class TestCombinedFixtures:
    """组合fixtures测试示例"""

    def test_user_with_merchant(self, test_user, test_merchant):
        """测试用户和商家组合"""
        assert test_user is not None
        assert test_merchant is not None
        assert test_user["email"] != test_merchant["email"]

    def test_service_with_merchant(self, test_service_with_merchant):
        """测试带商家的服务"""
        assert test_service_with_merchant["merchant_id"] is not None

    def test_product_with_merchant(self, test_product_with_merchant):
        """测试带商家的商品"""
        assert test_product_with_merchant["merchant_id"] is not None

    def test_appointment_with_user(self, test_appointment_with_user):
        """测试带用户的预约"""
        assert test_appointment_with_user["user_id"] is not None

    def test_order_with_user(self, test_order_with_user):
        """测试带用户的订单"""
        assert test_order_with_user["user_id"] is not None

    def test_review_with_user(self, test_review_with_user):
        """测试带用户的评价"""
        assert test_review_with_user["user_id"] is not None


class TestDataBuilderFeatures:
    """TestDataBuilder功能测试示例"""

    def test_build_batch_users(self, test_user_builder):
        """测试批量构建用户"""
        users = test_user_builder.build_batch("user", 5, status="active")
        assert len(users) == 5
        for user in users:
            assert user["status"] == "active"

    def test_build_batch_merchants(self, test_merchant_builder):
        """测试批量构建商家"""
        merchants = test_merchant_builder.build_batch("merchant", 3, status="approved")
        assert len(merchants) == 3
        for merchant in merchants:
            assert merchant["status"] == "approved"

    def test_build_batch_services(self, test_service_builder):
        """测试批量构建服务"""
        services = test_service_builder.build_batch("service", 4, status="enabled")
        assert len(services) == 4
        for service in services:
            assert service["status"] == "enabled"

    def test_build_batch_products(self, test_product_builder):
        """测试批量构建商品"""
        products = test_product_builder.build_batch("product", 6, status="enabled")
        assert len(products) == 6
        for product in products:
            assert product["status"] == "enabled"


class TestDatabaseConnection:
    """数据库连接测试示例"""

    @pytest.mark.integration
    def test_db_connection(self, db_connection):
        """测试数据库连接"""
        assert db_connection is not None
        cursor = db_connection.cursor()
        cursor.execute("SELECT 1")
        result = cursor.fetchone()
        assert result is not None
        cursor.close()

    @pytest.mark.integration
    def test_db_transaction(self, db_transaction):
        """测试数据库事务"""
        cursor = db_transaction.cursor()
        cursor.execute("SELECT COUNT(*) as count FROM user")
        result = cursor.fetchone()
        assert result is not None
        cursor.close()


@pytest.mark.smoke
def test_smoke_test_fixtures(
    test_user, test_merchant, test_service, test_product, test_appointment, test_product_order, test_review
):
    """冒烟测试：验证所有基础fixtures可用"""
    assert test_user is not None
    assert test_merchant is not None
    assert test_service is not None
    assert test_product is not None
    assert test_appointment is not None
    assert test_product_order is not None
    assert test_review is not None

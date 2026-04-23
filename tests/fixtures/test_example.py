"""
测试数据准备脚本使用示例
"""
import pytest
from tests.fixtures import (
    TestData,
    TestDataGenerator,
    setup_test_environment,
    teardown_test_environment
)
from tests.utils import HTTPClient


class TestExampleWithTestData:
    """使用测试数据的示例测试类"""
    
    @pytest.fixture(scope="class")
    def test_data(self):
        """创建测试数据"""
        data = setup_test_environment(
            create_services=True,
            create_products=True,
            create_pets=True,
            create_addresses=True,
            create_appointments=False,
            create_orders=False
        )
        yield data
        teardown_test_environment(data)
    
    def test_user_created(self, test_data: TestData):
        """测试用户是否创建成功"""
        assert test_data.user_id is not None
        assert test_data.user_token is not None
    
    def test_merchant_created(self, test_data: TestData):
        """测试商家是否创建成功"""
        assert test_data.merchant_id is not None
        assert test_data.merchant_token is not None
    
    def test_services_created(self, test_data: TestData):
        """测试服务是否创建成功"""
        assert len(test_data.service_ids) > 0
    
    def test_products_created(self, test_data: TestData):
        """测试商品是否创建成功"""
        assert len(test_data.product_ids) > 0
    
    def test_pets_created(self, test_data: TestData):
        """测试宠物是否创建成功"""
        assert len(test_data.pet_ids) > 0
    
    def test_addresses_created(self, test_data: TestData):
        """测试地址是否创建成功"""
        assert len(test_data.address_ids) > 0


class TestManualDataCreation:
    """手动创建测试数据的示例"""
    
    def test_manual_user_creation(self):
        """手动创建测试用户"""
        generator = TestDataGenerator()
        
        user = generator.create_test_user()
        
        assert user.get("id") is not None
        assert user.get("token") is not None
        
        generator.cleanup_test_data()
    
    def test_manual_merchant_creation(self):
        """手动创建测试商家"""
        generator = TestDataGenerator()
        
        merchant = generator.create_test_merchant()
        
        assert merchant.get("id") is not None
        assert merchant.get("token") is not None
        
        generator.cleanup_test_data()
    
    def test_manual_service_creation(self):
        """手动创建测试服务"""
        generator = TestDataGenerator()
        
        generator.create_test_merchant()
        service = generator.create_test_service()
        
        assert service.get("id") is not None
        
        generator.cleanup_test_data()
    
    def test_manual_product_creation(self):
        """手动创建测试商品"""
        generator = TestDataGenerator()
        
        generator.create_test_merchant()
        product = generator.create_test_product()
        
        assert product.get("id") is not None
        
        generator.cleanup_test_data()


class TestWithExistingData:
    """使用现有测试数据的示例"""
    
    @pytest.fixture(scope="class")
    def http_client(self):
        """HTTP客户端"""
        client = HTTPClient()
        yield client
        client.close()
    
    def test_use_existing_user(self, http_client: HTTPClient):
        """使用现有测试用户"""
        generator = TestDataGenerator(http_client)
        
        user = generator.create_test_user()
        user_token = user.get("token")
        
        response = http_client.get(
            "/api/user/profile",
            user_type="user"
        )
        
        assert response.status_code == 200
        
        generator.cleanup_test_data()
    
    def test_use_existing_merchant(self, http_client: HTTPClient):
        """使用现有测试商家"""
        generator = TestDataGenerator(http_client)
        
        merchant = generator.create_test_merchant()
        merchant_token = merchant.get("token")
        
        response = http_client.get(
            "/api/merchant/profile",
            user_type="merchant"
        )
        
        assert response.status_code == 200
        
        generator.cleanup_test_data()


def test_quick_setup():
    """快速设置测试环境示例"""
    test_data = setup_test_environment()
    
    assert test_data.user_id is not None
    assert test_data.merchant_id is not None
    
    teardown_test_environment(test_data)


def test_custom_setup():
    """自定义设置测试环境示例"""
    test_data = setup_test_environment(
        create_services=True,
        create_products=True,
        create_pets=False,
        create_addresses=False,
        create_appointments=False,
        create_orders=False
    )
    
    assert test_data.user_id is not None
    assert test_data.merchant_id is not None
    assert len(test_data.service_ids) > 0
    assert len(test_data.product_ids) > 0
    assert len(test_data.pet_ids) == 0
    assert len(test_data.address_ids) == 0
    
    teardown_test_environment(test_data)


if __name__ == "__main__":
    pytest.main([__file__, "-v", "-s"])

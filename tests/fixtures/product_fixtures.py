"""
商品相关测试数据Fixtures

提供商品测试数据的创建、管理和清理功能。
"""

from decimal import Decimal
from typing import Any, Dict, List

import pytest

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import cleanup_products
from tests.testdata.data_manager import TestDataManager


@pytest.fixture(scope="function")
def test_product() -> Dict[str, Any]:
    """
    创建测试商品（function作用域）

    每个测试函数都会创建一个新的测试商品。

    Returns:
        Dict[str, Any]: 商品数据字典

    使用示例:
        def test_product_creation(test_product):
            assert test_product['name'] is not None
            assert test_product['price'] > 0
            assert test_product['stock'] >= 0
    """
    builder = TestDataBuilder()
    product_data = builder.build_product()
    return product_data


@pytest.fixture(scope="function")
def test_products() -> List[Dict[str, Any]]:
    """
    创建多个测试商品（function作用域）

    创建一组测试商品，用于需要多个商品的测试场景。

    Returns:
        List[Dict[str, Any]]: 商品数据列表

    使用示例:
        def test_product_list(test_products):
            assert len(test_products) >= 3
            for product in test_products:
                assert product['name'] is not None
    """
    builder = TestDataBuilder()
    products = []

    products.append(builder.build_product(name="product_1", price=Decimal("49.99"), stock=100, category="食品"))
    products.append(builder.build_product(name="product_2", price=Decimal("99.99"), stock=50, category="用品"))
    products.append(builder.build_product(name="product_3", price=Decimal("29.99"), stock=200, category="玩具"))

    return products


@pytest.fixture(scope="function")
def test_product_with_merchant(test_merchant) -> Dict[str, Any]:
    """
    创建带商家的测试商品（function作用域）

    创建一个关联到商家的测试商品。

    Args:
        test_merchant: 测试商家fixture

    Returns:
        Dict[str, Any]: 包含商家信息的商品数据字典

    使用示例:
        def test_product_merchant_relation(test_product_with_merchant):
            assert test_product_with_merchant['merchant_id'] is not None
    """
    builder = TestDataBuilder()
    merchant_id = test_merchant.get("id", 1)
    product_data = builder.build_product(merchant_id=merchant_id)
    return product_data


@pytest.fixture(scope="function")
def clean_products(db_connection):
    """
    清理测试商品数据（function作用域）

    测试结束后自动清理创建的商品数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_product_cleanup(clean_products):
            # 创建测试商品
            # 测试结束后自动清理
    """
    product_ids = []

    yield product_ids

    if product_ids and db_connection:
        cleanup_products(product_ids, db_connection)


@pytest.fixture(scope="function")
def test_product_with_cleanup(db_connection, test_product) -> Dict[str, Any]:
    """
    创建测试商品并自动清理（function作用域）

    创建测试商品并在测试结束后自动清理数据库中的商品记录。

    Args:
        db_connection: 数据库连接fixture
        test_product: 测试商品fixture

    Returns:
        Dict[str, Any]: 商品数据字典
    """
    yield test_product

    if db_connection and "id" in test_product:
        cleanup_products([test_product["id"]], db_connection)


@pytest.fixture(scope="function")
def test_product_data_manager() -> TestDataManager:
    """
    获取商品数据管理器（function作用域）

    提供TestDataManager实例，用于更复杂的商品数据管理场景。

    Returns:
        TestDataManager: 数据管理器实例

    使用示例:
        def test_complex_product_scenario(test_product_data_manager):
            product = test_product_data_manager.prepare_test_data('product')
            # 进行复杂操作
            test_product_data_manager.cleanup_all_test_data()
    """
    manager = TestDataManager()
    return manager


@pytest.fixture(scope="function")
def test_product_builder() -> TestDataBuilder:
    """
    获取商品数据构建器（function作用域）

    提供TestDataBuilder实例，用于自定义商品数据构建。

    Returns:
        TestDataBuilder: 数据构建器实例

    使用示例:
        def test_custom_product(test_product_builder):
            product = test_product_builder.build_product(
                name="custom_product",
                price=Decimal('88.88'),
                stock=500
            )
            assert product['name'] == "custom_product"
    """
    return TestDataBuilder()


@pytest.fixture(scope="function")
def test_product_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试商品（function作用域）

    使用TestDataBuilder的批量构建功能创建多个商品。

    Returns:
        List[Dict[str, Any]]: 商品数据列表

    使用示例:
        def test_batch_products(test_product_batch):
            assert len(test_product_batch) == 10
    """
    builder = TestDataBuilder()
    products = builder.build_batch("product", 10, status="enabled")
    return products


@pytest.fixture(scope="function")
def test_enabled_product() -> Dict[str, Any]:
    """
    创建启用状态测试商品（function作用域）

    创建一个状态为enabled的测试商品，这是默认状态。

    Returns:
        Dict[str, Any]: 启用商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(status="enabled")
    return product_data


@pytest.fixture(scope="function")
def test_disabled_product() -> Dict[str, Any]:
    """
    创建禁用状态测试商品（function作用域）

    创建一个状态为disabled的测试商品，用于测试商品状态相关功能。

    Returns:
        Dict[str, Any]: 禁用商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(status="disabled")
    return product_data


@pytest.fixture(scope="function")
def test_in_stock_product() -> Dict[str, Any]:
    """
    创建有库存测试商品（function作用域）

    创建一个库存充足的测试商品。

    Returns:
        Dict[str, Any]: 有库存商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(name="有库存商品", stock=100, low_stock_threshold=10)
    return product_data


@pytest.fixture(scope="function")
def test_low_stock_product() -> Dict[str, Any]:
    """
    创建低库存测试商品（function作用域）

    创建一个库存低于阈值的测试商品，用于测试库存预警功能。

    Returns:
        Dict[str, Any]: 低库存商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(name="低库存商品", stock=5, low_stock_threshold=10)
    return product_data


@pytest.fixture(scope="function")
def test_out_of_stock_product() -> Dict[str, Any]:
    """
    创建缺货测试商品（function作用域）

    创建一个库存为0的测试商品，用于测试缺货场景。

    Returns:
        Dict[str, Any]: 缺货商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(name="缺货商品", stock=0, low_stock_threshold=10)
    return product_data


@pytest.fixture(scope="function")
def test_food_product() -> Dict[str, Any]:
    """
    创建食品类测试商品（function作用域）

    创建一个宠物食品类商品。

    Returns:
        Dict[str, Any]: 食品商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(
        name="优质宠物粮", description="高品质天然宠物粮，营养均衡", price=Decimal("128.00"), stock=200, category="食品"
    )
    return product_data


@pytest.fixture(scope="function")
def test_supply_product() -> Dict[str, Any]:
    """
    创建用品类测试商品（function作用域）

    创建一个宠物用品类商品。

    Returns:
        Dict[str, Any]: 用品商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(
        name="宠物用品套装",
        description="包含食盆、水盆、玩具等常用用品",
        price=Decimal("88.00"),
        stock=150,
        category="用品",
    )
    return product_data


@pytest.fixture(scope="function")
def test_toy_product() -> Dict[str, Any]:
    """
    创建玩具类测试商品（function作用域）

    创建一个宠物玩具类商品。

    Returns:
        Dict[str, Any]: 玩具商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(
        name="互动玩具球", description="安全材质，适合宠物玩耍", price=Decimal("29.90"), stock=300, category="玩具"
    )
    return product_data


@pytest.fixture(scope="function")
def test_health_product() -> Dict[str, Any]:
    """
    创建保健品类测试商品（function作用域）

    创建一个宠物保健品类商品。

    Returns:
        Dict[str, Any]: 保健品商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(
        name="宠物营养补充剂",
        description="增强免疫力，改善毛发质量",
        price=Decimal("168.00"),
        stock=80,
        category="保健",
    )
    return product_data


@pytest.fixture(scope="function")
def test_expensive_product() -> Dict[str, Any]:
    """
    创建高价测试商品（function作用域）

    创建一个高价商品，用于测试价格边界情况。

    Returns:
        Dict[str, Any]: 高价商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(name="高端定制商品", price=Decimal("9999.00"), stock=10)
    return product_data


@pytest.fixture(scope="function")
def test_cheap_product() -> Dict[str, Any]:
    """
    创建低价测试商品（function作用域）

    创建一个低价商品，用于测试价格边界情况。

    Returns:
        Dict[str, Any]: 低价商品数据字典
    """
    builder = TestDataBuilder()
    product_data = builder.build_product(name="经济实惠商品", price=Decimal("1.99"), stock=1000)
    return product_data

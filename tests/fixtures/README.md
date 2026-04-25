# 测试数据Fixtures使用指南

## 概述

本目录包含了一系列测试数据fixtures，用于简化测试数据的创建和管理。所有fixtures都基于pytest的fixture机制，支持不同的作用域和自动清理功能。

## 目录结构

```
tests/fixtures/
├── __init__.py                    # 包初始化文件
├── user_fixtures.py               # 用户相关fixtures
├── merchant_fixtures.py           # 商家相关fixtures
├── service_fixtures.py            # 服务相关fixtures
├── product_fixtures.py            # 商品相关fixtures
├── order_fixtures.py              # 订单相关fixtures
├── review_fixtures.py             # 评价相关fixtures
├── test_fixtures_example.py       # 使用示例
└── README.md                      # 本文档
```

## Fixture列表

### 用户Fixtures (user_fixtures.py)

| Fixture名称 | 作用域 | 描述 |
|------------|--------|------|
| `test_user` | function | 创建单个测试用户 |
| `test_user_session` | session | 创建会话级测试用户 |
| `test_user_token` | function | 获取用户认证Token |
| `test_users` | function | 创建多个测试用户（3个） |
| `clean_users` | function | 清理测试用户数据 |
| `test_user_with_cleanup` | function | 创建用户并自动清理 |
| `test_user_data_manager` | function | 获取TestDataManager实例 |
| `test_user_builder` | function | 获取TestDataBuilder实例 |
| `test_user_batch` | function | 批量创建测试用户（10个） |
| `test_inactive_user` | function | 创建非活跃状态用户 |
| `test_admin_user` | function | 创建管理员用户 |

### 商家Fixtures (merchant_fixtures.py)

| Fixture名称 | 作用域 | 描述 |
|------------|--------|------|
| `test_merchant` | function | 创建单个测试商家 |
| `test_merchant_session` | session | 创建会话级测试商家 |
| `test_merchant_token` | function | 获取商家认证Token |
| `test_merchants` | function | 创建多个测试商家（3个） |
| `clean_merchants` | function | 清理测试商家数据 |
| `test_merchant_with_cleanup` | function | 创建商家并自动清理 |
| `test_merchant_data_manager` | function | 获取TestDataManager实例 |
| `test_merchant_builder` | function | 获取TestDataBuilder实例 |
| `test_merchant_batch` | function | 批量创建测试商家（10个） |
| `test_pending_merchant` | function | 创建待审核状态商家 |
| `test_rejected_merchant` | function | 创建已拒绝状态商家 |
| `test_approved_merchant` | function | 创建已通过状态商家 |
| `test_merchant_with_services` | function | 创建带服务的商家 |
| `test_merchant_with_products` | function | 创建带商品的商家 |

### 服务Fixtures (service_fixtures.py)

| Fixture名称 | 作用域 | 描述 |
|------------|--------|------|
| `test_service` | function | 创建单个测试服务 |
| `test_services` | function | 创建多个测试服务（3个） |
| `test_service_with_merchant` | function | 创建带商家的服务 |
| `clean_services` | function | 清理测试服务数据 |
| `test_service_with_cleanup` | function | 创建服务并自动清理 |
| `test_service_data_manager` | function | 获取TestDataManager实例 |
| `test_service_builder` | function | 获取TestDataBuilder实例 |
| `test_service_batch` | function | 批量创建测试服务（10个） |
| `test_enabled_service` | function | 创建启用状态服务 |
| `test_disabled_service` | function | 创建禁用状态服务 |
| `test_grooming_service` | function | 创建美容类服务 |
| `test_medical_service` | function | 创建医疗类服务 |
| `test_boarding_service` | function | 创建寄养类服务 |
| `test_training_service` | function | 创建训练类服务 |
| `test_expensive_service` | function | 创建高价服务 |
| `test_cheap_service` | function | 创建低价服务 |

### 商品Fixtures (product_fixtures.py)

| Fixture名称 | 作用域 | 描述 |
|------------|--------|------|
| `test_product` | function | 创建单个测试商品 |
| `test_products` | function | 创建多个测试商品（3个） |
| `test_product_with_merchant` | function | 创建带商家的商品 |
| `clean_products` | function | 清理测试商品数据 |
| `test_product_with_cleanup` | function | 创建商品并自动清理 |
| `test_product_data_manager` | function | 获取TestDataManager实例 |
| `test_product_builder` | function | 获取TestDataBuilder实例 |
| `test_product_batch` | function | 批量创建测试商品（10个） |
| `test_enabled_product` | function | 创建启用状态商品 |
| `test_disabled_product` | function | 创建禁用状态商品 |
| `test_in_stock_product` | function | 创建有库存商品 |
| `test_low_stock_product` | function | 创建低库存商品 |
| `test_out_of_stock_product` | function | 创建缺货商品 |
| `test_food_product` | function | 创建食品类商品 |
| `test_supply_product` | function | 创建用品类商品 |
| `test_toy_product` | function | 创建玩具类商品 |
| `test_health_product` | function | 创建保健品类商品 |
| `test_expensive_product` | function | 创建高价商品 |
| `test_cheap_product` | function | 创建低价商品 |

### 订单Fixtures (order_fixtures.py)

| Fixture名称 | 作用域 | 描述 |
|------------|--------|------|
| `test_appointment` | function | 创建单个测试预约 |
| `test_product_order` | function | 创建单个测试商品订单 |
| `test_appointment_with_user` | function | 创建带用户的预约 |
| `test_order_with_user` | function | 创建带用户的订单 |
| `clean_appointments` | function | 清理测试预约数据 |
| `clean_orders` | function | 清理测试订单数据 |
| `test_appointment_with_cleanup` | function | 创建预约并自动清理 |
| `test_order_with_cleanup` | function | 创建订单并自动清理 |
| `test_order_data_manager` | function | 获取TestDataManager实例 |
| `test_order_builder` | function | 获取TestDataBuilder实例 |
| `test_pending_appointment` | function | 创建待处理预约 |
| `test_confirmed_appointment` | function | 创建已确认预约 |
| `test_completed_appointment` | function | 创建已完成预约 |
| `test_cancelled_appointment` | function | 创建已取消预约 |
| `test_pending_order` | function | 创建待支付订单 |
| `test_paid_order` | function | 创建已支付订单 |
| `test_shipped_order` | function | 创建已发货订单 |
| `test_completed_order` | function | 创建已完成订单 |
| `test_cancelled_order` | function | 创建已取消订单 |
| `test_high_value_order` | function | 创建高价值订单 |
| `test_low_value_order` | function | 创建低价值订单 |
| `test_wechat_pay_order` | function | 创建微信支付订单 |
| `test_alipay_order` | function | 创建支付宝支付订单 |
| `test_appointment_batch` | function | 批量创建测试预约（10个） |
| `test_order_batch` | function | 批量创建测试订单（10个） |

### 评价Fixtures (review_fixtures.py)

| Fixture名称 | 作用域 | 描述 |
|------------|--------|------|
| `test_review` | function | 创建单个测试评价 |
| `test_reviews` | function | 创建多个测试评价（3个） |
| `test_review_with_user` | function | 创建带用户的评价 |
| `clean_reviews` | function | 清理测试评价数据 |
| `test_review_with_cleanup` | function | 创建评价并自动清理 |
| `test_review_data_manager` | function | 获取TestDataManager实例 |
| `test_review_builder` | function | 获取TestDataBuilder实例 |
| `test_five_star_review` | function | 创建五星好评 |
| `test_four_star_review` | function | 创建四星评价 |
| `test_three_star_review` | function | 创建三星评价 |
| `test_two_star_review` | function | 创建二星评价 |
| `test_one_star_review` | function | 创建一星差评 |
| `test_positive_review` | function | 创建正面评价 |
| `test_negative_review` | function | 创建负面评价 |
| `test_neutral_review` | function | 创建中性评价 |
| `test_review_with_reply` | function | 创建带回复的评价 |
| `test_review_without_reply` | function | 创建未回复的评价 |
| `test_long_comment_review` | function | 创建长评评价 |
| `test_short_comment_review` | function | 创建短评评价 |
| `test_review_batch` | function | 批量创建测试评价（10个） |
| `test_review_with_service` | function | 创建带服务的评价 |
| `test_review_with_appointment` | function | 创建带预约的评价 |
| `test_review_with_merchant` | function | 创建带商家的评价 |
| `test_mixed_rating_reviews` | function | 创建混合评分评价 |

## 使用示例

### 基本使用

```python
def test_user_creation(test_user):
    """测试用户创建"""
    assert test_user['username'] is not None
    assert test_user['email'] is not None
    assert test_user['status'] == 'active'
```

### 使用多个fixtures

```python
def test_user_merchant_relation(test_user, test_merchant):
    """测试用户和商家关系"""
    assert test_user['email'] != test_merchant['email']
```

### 使用带关联的fixtures

```python
def test_service_with_merchant(test_service_with_merchant):
    """测试带商家的服务"""
    assert test_service_with_merchant['merchant_id'] is not None
```

### 使用数据构建器

```python
def test_custom_user(test_user_builder):
    """测试自定义用户构建"""
    user = test_user_builder.build_user(
        username="custom_user",
        email="custom@test.com"
    )
    assert user['username'] == "custom_user"
```

### 批量创建数据

```python
def test_batch_users(test_user_builder):
    """测试批量创建用户"""
    users = test_user_builder.build_batch('user', 10, status='active')
    assert len(users) == 10
```

### 使用数据管理器

```python
def test_data_manager(test_user_data_manager):
    """测试数据管理器"""
    user = test_user_data_manager.prepare_test_data('user')
    # 进行操作
    test_user_data_manager.cleanup_all_test_data()
```

### 数据库事务测试

```python
def test_with_transaction(db_transaction):
    """测试数据库事务"""
    cursor = db_transaction.cursor()
    cursor.execute("INSERT INTO user (username) VALUES ('test')")
    # 测试结束后自动回滚
```

## 最佳实践

### 1. 选择合适的作用域

- **function**: 每个测试函数都需要独立的数据时使用
- **session**: 整个测试会话期间共享数据时使用
- **module**: 模块内共享数据时使用
- **class**: 类内共享数据时使用

### 2. 使用自动清理

```python
def test_with_cleanup(test_user_with_cleanup):
    """测试自动清理功能"""
    # 测试结束后自动清理数据
    pass
```

### 3. 组合使用fixtures

```python
def test_complex_scenario(
    test_user,
    test_merchant,
    test_service,
    test_appointment
):
    """测试复杂场景"""
    # 使用多个fixtures组合测试
    pass
```

### 4. 使用标记分类测试

```python
@pytest.mark.smoke
def test_smoke_test(test_user):
    """冒烟测试"""
    pass

@pytest.mark.integration
def test_integration(db_connection):
    """集成测试"""
    pass
```

## 注意事项

1. **数据隔离**: 每个测试应该独立，不依赖其他测试的数据
2. **自动清理**: 使用`*_with_cleanup` fixtures确保数据被清理
3. **事务回滚**: 使用`db_transaction` fixture确保数据库操作回滚
4. **性能考虑**: 批量创建数据时注意性能影响
5. **并发测试**: 避免在并发测试中使用session级别的fixtures

## 运行测试

```bash
# 运行所有fixtures测试
pytest tests/fixtures/test_fixtures_example.py

# 运行特定测试类
pytest tests/fixtures/test_fixtures_example.py::TestUserFixtures

# 运行特定测试方法
pytest tests/fixtures/test_fixtures_example.py::TestUserFixtures::test_test_user

# 运行冒烟测试
pytest -m smoke tests/fixtures/test_fixtures_example.py

# 运行集成测试
pytest -m integration tests/fixtures/test_fixtures_example.py
```

## 扩展指南

### 添加新的fixture

1. 在对应的`*_fixtures.py`文件中添加新fixture
2. 使用`@pytest.fixture`装饰器
3. 指定合适的作用域
4. 添加详细的文档字符串
5. 在`conftest.py`中导入（如果需要）

### 示例：添加新的用户fixture

```python
@pytest.fixture(scope="function")
def test_premium_user():
    """
    创建高级会员测试用户（function作用域）
    
    Returns:
        Dict[str, Any]: 高级会员用户数据字典
    """
    builder = TestDataBuilder()
    user_data = builder.build_user(
        username="premium_user",
        email="premium@test.com",
        membership="premium"
    )
    return user_data
```

## 相关文档

- [pytest fixture文档](https://docs.pytest.org/en/stable/fixture.html)
- [TestDataBuilder文档](../testdata/README.md)
- [TestDataManager文档](../testdata/README.md)
- [数据清理工具文档](../testdata/README.md)

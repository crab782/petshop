# 测试数据管理模块

完整的测试数据管理解决方案，提供测试数据的构建、管理和清理功能。

## 目录结构

```
tests/testdata/
├── __init__.py              # 模块初始化文件
├── data_builder.py          # 测试数据构建器
├── data_manager.py          # 测试数据管理器
├── data_cleanup.py          # 测试数据清理工具
├── test_example.py          # 使用示例
├── README.md                # 本文档
└── fixtures/                # 测试数据fixture目录
    ├── __init__.py
    └── README.md
```

## 功能特性

### 1. TestDataBuilder - 测试数据构建器

使用工厂模式创建各类测试数据，支持自定义字段和默认值。

**支持的数据类型**：
- `build_user()` - 用户数据
- `build_merchant()` - 商家数据
- `build_service()` - 服务数据
- `build_product()` - 商品数据
- `build_appointment()` - 预约数据
- `build_order()` - 订单数据
- `build_review()` - 评价数据
- `build_pet()` - 宠物数据
- `build_address()` - 地址数据

**使用示例**：

```python
from tests.testdata import TestDataBuilder

builder = TestDataBuilder()

# 使用默认值
user = builder.build_user()

# 自定义字段
user = builder.build_user(
    username="custom_user",
    email="custom@example.com",
    status="active"
)

# 批量构建
users = builder.build_batch('user', 5, status='active')
```

### 2. TestDataManager - 测试数据管理器

管理测试数据的完整生命周期，包括数据准备、获取、清理和隔离。

**主要功能**：
- `prepare_test_data()` - 准备测试数据
- `get_test_data()` - 获取测试数据
- `cleanup_test_data()` - 清理指定测试数据
- `cleanup_all_test_data()` - 清理所有测试数据
- `isolate_test_data` - 测试数据隔离装饰器
- `transaction_context()` - 事务上下文管理器

**使用示例**：

```python
from tests.testdata import TestDataManager

manager = TestDataManager()

# 准备测试数据
user = manager.prepare_test_data('user', username="test_user")

# 获取测试数据
user_id = list(manager.get_all_test_data().keys())[0]
retrieved_user = manager.get_test_data(user_id)

# 清理测试数据
manager.cleanup_test_data(user_id)

# 清理所有数据
manager.cleanup_all_test_data()
```

**使用装饰器自动管理数据生命周期**：

```python
@manager.isolate_test_data
def test_function():
    user = manager.prepare_test_data('user')
    # 测试代码...
    # 测试结束后自动清理
```

**使用上下文管理器**：

```python
with TestDataManager() as manager:
    user = manager.prepare_test_data('user')
    # 测试代码...
    # 退出时自动清理
```

### 3. 数据清理工具

提供各类测试数据的清理功能，支持级联删除。

**主要功能**：
- `cleanup_users()` - 清理用户数据（级联删除关联数据）
- `cleanup_merchants()` - 清理商家数据（级联删除关联数据）
- `cleanup_services()` - 清理服务数据
- `cleanup_products()` - 清理商品数据
- `cleanup_appointments()` - 清理预约数据
- `cleanup_orders()` - 清理订单数据
- `cleanup_reviews()` - 清理评价数据
- `cleanup_all()` - 清理所有测试数据

**使用示例**：

```python
from tests.testdata import cleanup_users, cleanup_all

# 清理指定用户
cleanup_users([1, 2, 3], db_connection)

# 清理所有数据（需要确认）
cleanup_all(db_connection, confirm=True)
```

## 完整使用示例

### 示例 1：基本使用

```python
from tests.testdata import TestDataBuilder, TestDataManager

# 构建测试数据
builder = TestDataBuilder()
user_data = builder.build_user(username="test_user")

# 管理测试数据
manager = TestDataManager()
user = manager.prepare_test_data('user', username="test_user")

# 测试代码...

# 清理数据
manager.cleanup_all_test_data()
```

### 示例 2：使用装饰器

```python
from tests.testdata import TestDataManager

manager = TestDataManager()

@manager.isolate_test_data
def test_user_creation():
    user = manager.prepare_test_data('user')
    # 测试代码...
    # 测试结束后自动清理
```

### 示例 3：使用上下文管理器

```python
from tests.testdata import TestDataManager

def test_with_context_manager():
    with TestDataManager() as manager:
        user = manager.prepare_test_data('user')
        merchant = manager.prepare_test_data('merchant')
        # 测试代码...
        # 退出时自动清理
```

### 示例 4：数据库集成

```python
import pymysql
from tests.testdata import TestDataManager

# 创建数据库连接
connection = pymysql.connect(
    host='localhost',
    user='root',
    password='password',
    database='test_db'
)

manager = TestDataManager()

# 准备测试数据并插入数据库
user = manager.prepare_test_data(
    'user',
    use_db=True,
    db_connection=connection,
    username="db_user"
)

# 测试代码...

# 清理数据库中的测试数据
manager.cleanup_all_test_data(db_connection=connection)

connection.close()
```

### 示例 5：事务管理

```python
from tests.testdata import TestDataManager

manager = TestDataManager()

# 使用事务上下文管理器
with manager.transaction_context(db_connection) as conn:
    user = manager.prepare_test_data(
        'user',
        use_db=True,
        db_connection=conn
    )
    # 测试代码...
    # 事务自动回滚
```

## 最佳实践

### 1. 数据隔离

使用装饰器或上下文管理器确保测试数据隔离：

```python
# 推荐：使用装饰器
@manager.isolate_test_data
def test_something():
    # 测试代码

# 或者：使用上下文管理器
with TestDataManager() as manager:
    # 测试代码
```

### 2. 命名规范

为测试数据使用有意义的名称：

```python
user = builder.build_user(username="test_user_for_login")
```

### 3. 批量操作

使用批量构建提高效率：

```python
users = builder.build_batch('user', 10, status='active')
```

### 4. 清理策略

- 使用装饰器自动清理
- 使用上下文管理器自动清理
- 手动清理时确保清理所有关联数据

### 5. 数据库事务

对于需要数据库操作的测试，使用事务确保数据隔离：

```python
with manager.transaction_context(db_connection) as conn:
    # 所有数据库操作在事务中
    # 测试结束后自动回滚
```

## 注意事项

1. **数据隔离**：确保测试数据不影响其他测试和生产数据
2. **清理数据**：测试结束后及时清理测试数据
3. **事务管理**：使用事务进行数据隔离
4. **性能考虑**：避免创建过多的测试数据
5. **并发测试**：注意并发测试时的数据冲突

## 扩展功能

### 自定义数据构建器

可以继承 `TestDataBuilder` 创建自定义构建器：

```python
from tests.testdata import TestDataBuilder

class CustomDataBuilder(TestDataBuilder):
    def build_custom_user(self, **kwargs):
        user = self.build_user(**kwargs)
        user['custom_field'] = 'custom_value'
        return user
```

### 自定义清理逻辑

可以扩展清理功能：

```python
from tests.testdata.data_cleanup import cleanup_users

def custom_cleanup_users(user_ids, db_connection):
    # 自定义清理逻辑
    # ...
    # 调用原有清理函数
    return cleanup_users(user_ids, db_connection)
```

## 测试

运行测试：

```bash
# 运行所有测试
pytest tests/testdata/test_example.py -v

# 运行特定测试
pytest tests/testdata/test_example.py::TestTestDataBuilder::test_build_user_with_defaults -v
```

## 版本历史

- v1.0.0 (2024-04-24)
  - 初始版本
  - 实现测试数据构建器
  - 实现测试数据管理器
  - 实现数据清理工具
  - 添加使用示例和文档

## 贡献指南

欢迎贡献代码和建议！请遵循以下步骤：

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。

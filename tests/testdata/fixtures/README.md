# 测试数据 Fixtures 目录

该目录用于存放测试数据 fixture 文件。

## 目录结构

```
fixtures/
├── __init__.py
├── user_fixtures.py        # 用户测试数据 fixtures
├── merchant_fixtures.py    # 商家测试数据 fixtures
├── service_fixtures.py     # 服务测试数据 fixtures
├── product_fixtures.py     # 商品测试数据 fixtures
├── appointment_fixtures.py # 预约测试数据 fixtures
├── order_fixtures.py       # 订单测试数据 fixtures
├── review_fixtures.py      # 评价测试数据 fixtures
├── pet_fixtures.py         # 宠物测试数据 fixtures
└── address_fixtures.py     # 地址测试数据 fixtures
```

## 使用方法

### 1. 创建 Fixture 文件

在每个 fixture 文件中定义测试数据：

```python
# user_fixtures.py
import pytest
from tests.testdata import TestDataBuilder

@pytest.fixture
def default_user():
    """默认用户 fixture"""
    builder = TestDataBuilder()
    return builder.build_user(
        username="testuser",
        email="test@example.com"
    )

@pytest.fixture
def admin_user():
    """管理员用户 fixture"""
    builder = TestDataBuilder()
    return builder.build_user(
        username="admin",
        email="admin@example.com",
        status="admin"
    )
```

### 2. 在测试中使用 Fixture

```python
# test_user.py
def test_user_creation(default_user):
    """测试用户创建"""
    assert default_user['username'] == "testuser"
    assert default_user['email'] == "test@example.com"

def test_admin_user(admin_user):
    """测试管理员用户"""
    assert admin_user['username'] == "admin"
    assert admin_user['status'] == "admin"
```

### 3. 使用 conftest.py 共享 Fixtures

在 `tests/conftest.py` 中导入 fixtures：

```python
# tests/conftest.py
from tests.testdata.fixtures.user_fixtures import *
from tests.testdata.fixtures.merchant_fixtures import *
```

## 最佳实践

1. **命名规范**：fixture 函数名应清晰描述其用途
2. **文档字符串**：为每个 fixture 添加文档字符串
3. **作用域**：根据需要设置 fixture 的作用域（function, class, module, session）
4. **依赖关系**：fixture 可以依赖其他 fixture
5. **清理工作**：使用 yield 进行清理工作

## 示例：带清理的 Fixture

```python
@pytest.fixture
def user_with_cleanup(db_connection):
    """带清理的用户 fixture"""
    builder = TestDataBuilder()
    user = builder.build_user()
    
    # 插入数据库
    cursor = db_connection.cursor()
    cursor.execute(
        "INSERT INTO user (username, email, password) VALUES (%s, %s, %s)",
        (user['username'], user['email'], user['password'])
    )
    db_connection.commit()
    user['id'] = cursor.lastrowid
    
    yield user
    
    # 清理数据
    cursor.execute("DELETE FROM user WHERE id = %s", (user['id'],))
    db_connection.commit()
    cursor.close()
```

## 注意事项

1. 不要在 fixture 中创建过多的测试数据
2. 确保 fixture 之间相互独立
3. 使用事务进行数据隔离
4. 定期清理不再使用的 fixture

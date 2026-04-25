# 单元测试基础结构

## 概述

该模块提供了单元测试的基础设施，包括：

- **BaseAPITest**: 所有API测试的基类，提供通用的测试方法、断言方法和辅助方法
- **单元测试专用fixtures**: 包括HTTP客户端、Token管理器、测试数据构建器等
- **断言工具**: 提供丰富的断言方法，支持链式调用
- **测试数据构建器**: 使用工厂模式创建各类测试数据

## 目录结构

```
tests/unit/
├── __init__.py          # 模块初始化文件
├── conftest.py          # pytest配置文件，定义fixtures
├── base_test.py         # 测试基类
├── test_example.py      # 示例测试文件
└── README.md            # 本文档
```

## 快速开始

### 1. 基本用法

```python
import pytest
from tests.unit import BaseAPITest

@pytest.mark.unit_api
class TestMyAPI(BaseAPITest):
    def test_example(self, unit_http_client):
        response = unit_http_client.get("/api/users")
        self.assert_response_success(response)
```

### 2. 使用测试数据构建器

```python
def test_create_user(self, unit_http_client, test_data_builder):
    # 使用默认值构建用户数据
    user_data = test_data_builder.build_user()
    
    # 自定义部分字段
    custom_user = test_data_builder.build_user(
        username="custom_user",
        email="custom@example.com"
    )
    
    response = unit_http_client.post("/api/auth/register", json_data=user_data)
    self.assert_response_success(response)
```

### 3. 使用预创建的测试资源

```python
def test_user_profile(self, unit_http_client, test_user):
    # test_user fixture自动创建用户并返回token
    assert test_user["token"] is not None
    
    response = unit_http_client.get(
        "/api/user/profile",
        headers=self.get_auth_headers(test_user["token"])
    )
    self.assert_response_success(response)
```

## 可用的Fixtures

### HTTP客户端

- **unit_http_client**: 单元测试专用的HTTP客户端（function作用域）

### Token管理

- **unit_token_manager**: 单元测试专用的Token管理器（function作用域）

### 数据清理

- **clean_database**: 清理数据库的fixture（function作用域）

### 测试数据构建

- **test_data_builder**: 测试数据构建器（function作用域）

### 预创建的测试资源

- **test_user**: 自动创建测试用户，返回用户数据和token
- **test_merchant**: 自动创建测试商家，返回商家数据和token
- **test_service**: 自动创建测试服务
- **test_product**: 自动创建测试商品

### 测试上下文

- **api_context**: 提供一个字典用于在测试中存储和共享数据

## BaseAPITest类方法

### 断言方法

#### assert_response_success(response)
断言响应成功，验证状态码为200或201

```python
response = http_client.get("/api/users")
self.assert_response_success(response)
```

#### assert_response_error(response, expected_code)
断言响应错误，验证状态码符合预期

```python
response = http_client.get("/api/users/999")
self.assert_response_error(response, 404)
```

#### assert_response_data(response, expected_data)
断言响应数据，验证data字段包含预期数据

```python
response = http_client.get("/api/users/1")
self.assert_response_data(response, {"id": 1, "username": "test"})
```

#### assert_status_code(response, expected_code)
断言状态码

```python
response = http_client.get("/api/users")
self.assert_status_code(response, 200)
```

#### assert_response_time(response, max_time)
断言响应时间不超过最大值

```python
response = http_client.get("/api/users")
self.assert_response_time(response, 2.0)
```

#### assert_json_schema(response, schema)
断言JSON Schema

```python
schema = {
    "type": "object",
    "properties": {
        "id": {"type": "integer"},
        "username": {"type": "string"}
    },
    "required": ["id", "username"]
}
response = http_client.get("/api/users/1")
self.assert_json_schema(response, schema)
```

#### assert_pagination(response, expected_page, expected_size, expected_total)
断言分页数据

```python
response = http_client.get("/api/users?page=1&size=10")
self.assert_pagination(response, expected_page=1, expected_size=10)
```

#### assert_list_not_empty(response)
断言列表非空

```python
response = http_client.get("/api/users")
self.assert_list_not_empty(response)
```

#### assert_field_type(response, field_name, expected_type)
断言字段类型

```python
response = http_client.get("/api/users/1")
self.assert_field_type(response, "id", int)
self.assert_field_type(response, "username", str)
```

#### assert_required_fields(response, required_fields)
断言必需字段存在

```python
response = http_client.get("/api/users/1")
self.assert_required_fields(response, ["id", "username", "email"])
```

### 测试数据创建方法

#### create_test_user(http_client, **kwargs)
创建测试用户

```python
user = self.create_test_user(http_client, username="testuser")
assert user["token"] is not None
```

#### create_test_merchant(http_client, **kwargs)
创建测试商家

```python
merchant = self.create_test_merchant(http_client, name="Test Shop")
assert merchant["token"] is not None
```

#### create_test_service(http_client, merchant_token, merchant_id, **kwargs)
创建测试服务

```python
service = self.create_test_service(
    http_client,
    merchant_token,
    merchant_id,
    name="Test Service"
)
assert service["id"] is not None
```

#### create_test_product(http_client, merchant_token, merchant_id, **kwargs)
创建测试商品

```python
product = self.create_test_product(
    http_client,
    merchant_token,
    merchant_id,
    name="Test Product"
)
assert product["id"] is not None
```

### 辅助方法

#### get_auth_headers(token)
获取认证头

```python
headers = self.get_auth_headers(user_token)
response = http_client.get("/api/user/profile", headers=headers)
```

#### wait_for_condition(condition, timeout, interval)
等待条件满足

```python
def check_order_status():
    response = http_client.get(f"/api/orders/{order_id}")
    return response.json()["data"]["status"] == "completed"

success = self.wait_for_condition(check_order_status, timeout=30)
assert success, "Order not completed in time"
```

#### cleanup_test_data()
清理测试数据

```python
def teardown_method(self):
    self.cleanup_test_data()
```

#### log_response(response)
记录响应详情

```python
response = http_client.get("/api/users")
self.log_response(response)
```

## Pytest Markers

单元测试模块定义了以下pytest markers：

- `@pytest.mark.unit_api`: 单元API测试
- `@pytest.mark.unit_user`: 单元用户API测试
- `@pytest.mark.unit_merchant`: 单元商家API测试
- `@pytest.mark.unit_product`: 单元商品API测试
- `@pytest.mark.unit_service`: 单元服务API测试
- `@pytest.mark.unit_order`: 单元订单API测试
- `@pytest.mark.unit_review`: 单元评价API测试
- `@pytest.mark.unit_auth`: 单元认证测试
- `@pytest.mark.unit_crud`: 单元CRUD操作测试
- `@pytest.mark.unit_validation`: 单元验证测试
- `@pytest.mark.unit_error`: 单元错误处理测试

## 运行测试

### 运行所有单元测试

```bash
pytest tests/unit/ -v
```

### 运行特定marker的测试

```bash
pytest tests/unit/ -m unit_user -v
pytest tests/unit/ -m unit_auth -v
```

### 运行特定文件

```bash
pytest tests/unit/test_example.py -v
```

### 运行特定测试方法

```bash
pytest tests/unit/test_example.py::TestUserAPI::test_user_login -v
```

### 生成覆盖率报告

```bash
pytest tests/unit/ --cov=src --cov-report=html
```

## 最佳实践

### 1. 测试隔离

每个测试应该独立运行，不依赖其他测试的结果：

```python
def test_create_user(self, unit_http_client, test_data_builder):
    # 每个测试使用新的数据
    user_data = test_data_builder.build_user()
    # 测试代码...
```

### 2. 使用有意义的测试名称

```python
# 好的命名
def test_user_registration_with_valid_data(self):
    pass

def test_user_registration_with_invalid_email(self):
    pass

# 不好的命名
def test_1(self):
    pass
```

### 3. 清理测试数据

在测试完成后清理创建的数据：

```python
class TestMyAPI(BaseAPITest):
    def teardown_method(self):
        self.cleanup_test_data()
```

### 4. 使用适当的断言

```python
# 使用具体的断言方法
self.assert_response_success(response)
self.assert_status_code(response, 200)

# 而不是通用的assert
assert response.status_code == 200
```

### 5. 添加适当的markers

```python
@pytest.mark.unit_api
@pytest.mark.unit_user
class TestUserAPI(BaseAPITest):
    pass
```

### 6. 记录响应详情

在调试时记录响应详情：

```python
def test_example(self, unit_http_client):
    response = unit_http_client.get("/api/users")
    self.log_response(response)  # 记录响应详情
    self.assert_response_success(response)
```

## 示例测试

查看 `test_example.py` 文件获取更多示例，包括：

- 用户API测试
- 商家API测试
- 服务API测试
- 商品API测试
- 认证API测试
- 数据验证测试
- CRUD操作测试

## 注意事项

1. **测试环境**: 确保测试环境已正确配置（数据库、API服务器等）
2. **数据隔离**: 每个测试应该使用独立的测试数据
3. **清理数据**: 测试完成后应该清理创建的数据
4. **响应时间**: 注意API响应时间，设置合理的超时
5. **并发测试**: 避免测试之间的数据竞争

## 相关文档

- [测试配置](../config.py)
- [HTTP客户端](../utils.py)
- [断言工具](../assertions/)
- [测试数据构建器](../testdata/data_builder.py)
- [数据清理工具](../testdata/data_cleanup.py)

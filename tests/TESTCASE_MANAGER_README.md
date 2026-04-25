# 测试用例管理器使用文档

## 概述

测试用例管理器（TestCaseManager）是一个强大的测试用例管理工具，提供了测试用例的注册、查询、过滤、执行策略等功能。

## 主要功能

### 1. 测试用例分类标记

- `smoke` - 冒烟测试
- `regression` - 回归测试
- `api` - API测试
- `unit` - 单元测试
- `integration` - 集成测试
- `performance` - 性能测试
- `security` - 安全测试

### 2. 测试用例优先级标记

- `P0` - 最高优先级（核心功能）
- `P1` - 高优先级（重要功能）
- `P2` - 中优先级（一般功能）
- `P3` - 低优先级（次要功能）

### 3. 测试用例模块标记

- `user` - 用户端测试
- `merchant` - 商家端测试
- `admin` - 平台端测试
- `public` - 公共API测试
- `auth` - 认证测试

## 使用方法

### 1. 基本使用

```python
from testcase_manager import TestCaseManager, get_test_case_manager

# 获取全局管理器实例
manager = get_test_case_manager()

# 注册测试用例
test_info = {
    'test_name': '用户登录成功',
    'test_module': 'auth',
    'test_tags': ['smoke', 'auth', 'P0'],
    'test_priority': 'P0',
    'test_description': '测试用户使用正确的用户名和密码登录',
    'test_steps': ['发送登录请求', '验证响应状态码', '验证返回Token'],
    'test_expected_result': '登录成功，返回Token',
    'test_dependencies': [],
    'test_timeout': 30
}

manager.register_test_case('test_user_login_001', test_info)
```

### 2. 使用装饰器注册测试用例

```python
from testcase_manager import test_case

@test_case(
    test_id='test_user_login_success',
    test_name='用户登录成功',
    test_module='auth',
    test_tags=['smoke', 'auth', 'P0'],
    test_priority='P0',
    test_description='测试用户使用正确的用户名和密码登录',
    test_steps=[
        '发送登录请求',
        '验证响应状态码为200',
        '验证返回Token不为空'
    ],
    test_expected_result='登录成功，返回Token',
    test_timeout=30
)
def test_user_login_success(http_client, user_credentials):
    """测试用户登录成功"""
    response = http_client.post(
        "/api/auth/login",
        json_data={
            "phone": user_credentials["phone"],
            "password": user_credentials["password"]
        }
    )
    
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert "token" in data["data"]
```

### 3. 查询测试用例

```python
# 获取单个测试用例
test_case = manager.get_test_case('test_user_login_001')

# 按优先级获取测试用例
p0_tests = manager.get_test_cases_by_priority('P0')

# 按模块获取测试用例
auth_tests = manager.get_test_cases_by_module('auth')

# 按标记获取测试用例
smoke_tests = manager.get_test_cases_by_tag('smoke')

# 组合过滤
filtered_tests = manager.filter_test_cases(
    tags=['smoke'],
    priority='P0',
    module='auth'
)
```

### 4. 执行策略

```python
from testcase_manager import ExecutionStrategy, ExecutionStrategyType

strategy = ExecutionStrategy(manager)

# 创建顺序执行计划
sequential_plan = strategy.create_execution_plan(
    ExecutionStrategyType.SEQUENTIAL,
    tags=['smoke']
)

# 创建优先级执行计划
priority_plan = strategy.create_execution_plan(
    ExecutionStrategyType.PRIORITY_BASED,
    module='auth'
)

# 创建依赖关系执行计划
dependency_plan = strategy.create_execution_plan(
    ExecutionStrategyType.DEPENDENCY_BASED
)

# 执行测试计划
results = strategy.execute_plan(plan)
```

### 5. 测试统计

```python
# 获取测试统计信息
stats = manager.get_test_statistics()
print(f"总测试数: {stats['total']}")
print(f"按优先级: {stats['by_priority']}")
print(f"按模块: {stats['by_module']}")
print(f"按标记: {stats['by_tag']}")
print(f"按状态: {stats['by_status']}")
```

### 6. 导出和导入

```python
# 导出测试用例为JSON
exported_json = manager.export_test_cases('json')

# 导入测试用例
imported_count = manager.import_test_cases(exported_json, 'json')
```

### 7. 记录执行结果

```python
# 记录测试执行结果
manager.record_execution(
    'test_user_login_001',
    'passed',
    execution_time=0.5,
    error_message=None
)

# 获取执行历史
history = manager.get_execution_history('test_user_login_001')
```

## 测试用例信息结构

```python
{
    'test_id': 'test_user_login_001',
    'test_name': '用户登录成功',
    'test_module': 'auth',
    'test_tags': ['smoke', 'auth', 'P0'],
    'test_priority': 'P0',
    'test_description': '测试用户使用正确的用户名和密码登录',
    'test_steps': ['发送登录请求', '验证响应状态码', '验证返回Token'],
    'test_expected_result': '登录成功，返回Token',
    'test_dependencies': [],
    'test_timeout': 30,
    'test_function': None,  # 测试函数对象
    'test_status': 'pending',  # pending/running/passed/failed/skipped
    'test_created_at': '2024-01-01T00:00:00',
    'test_updated_at': '2024-01-01T00:00:00',
    'test_execution_time': None,  # 执行时间（秒）
    'test_error_message': None  # 错误信息
}
```

## 执行策略类型

### 1. 顺序执行（Sequential）

按照测试用例注册的顺序依次执行。

### 2. 并行执行（Parallel）

并行执行测试用例（需要配合pytest-xdist等工具）。

### 3. 优先级执行（Priority Based）

按照优先级从高到低执行（P0 → P1 → P2 → P3）。

### 4. 依赖关系执行（Dependency Based）

按照测试用例的依赖关系执行，确保依赖的测试用例先执行。

## 最佳实践

### 1. 测试用例命名规范

- 使用有意义的测试ID：`test_<module>_<feature>_<scenario>_<number>`
- 例如：`test_user_login_success_001`

### 2. 测试用例标记

- 每个测试用例至少包含一个分类标记（smoke/regression等）
- 每个测试用例必须包含优先级标记（P0/P1/P2/P3）
- 每个测试用例必须包含模块标记（user/merchant/admin等）

### 3. 测试依赖管理

- 明确标注测试用例的依赖关系
- 避免循环依赖
- 使用依赖关系执行策略确保正确的执行顺序

### 4. 测试步骤文档

- 详细记录测试步骤
- 包含预期结果
- 便于测试失败时的排查

### 5. 执行结果记录

- 及时记录测试执行结果
- 包含执行时间和错误信息
- 便于后续分析和优化

## 与pytest集成

测试用例管理器与pytest完美集成，支持pytest的marker机制：

```python
# conftest.py
from testcase_manager import pytest_configure

def pytest_configure(config):
    from testcase_manager import pytest_configure as tc_pytest_configure
    tc_pytest_configure(config)
```

运行测试时可以按标记过滤：

```bash
# 运行所有冒烟测试
pytest -m smoke

# 运行所有P0优先级测试
pytest -m P0

# 运行所有auth模块测试
pytest -m auth

# 组合过滤
pytest -m "smoke and P0"
```

## 示例项目

完整的示例请参考：
- [testcase_manager.py](./testcase_manager.py) - 测试用例管理器实现
- [testcase_manager_example.py](./testcase_manager_example.py) - 使用示例

## 注意事项

1. 测试函数对象不会被导出到JSON（因为函数对象无法序列化）
2. 使用装饰器注册测试用例时，如果ID已存在会自动跳过
3. 执行策略中的并行执行需要配合pytest-xdist等工具使用
4. 建议在测试开始前清理执行历史，避免历史数据影响统计

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 支持测试用例注册、查询、过滤
- 支持多种执行策略
- 支持导出导入功能
- 支持执行结果记录和统计

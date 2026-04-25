# 认证API单元测试文档

## 文件信息

- **文件路径**: `tests/unit/test_auth_api.py`
- **创建日期**: 2026-04-24
- **测试框架**: pytest + allure
- **基类**: BaseAPITest

## 测试覆盖范围

### 1. 用户登录接口测试 (TestUserLoginAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_user_login_success | 测试用户登录成功（支持手机号/邮箱登录） | CRITICAL | smoke |
| test_user_login_wrong_password | 测试用户登录失败 - 错误密码 | NORMAL | - |
| test_user_login_nonexistent_user | 测试用户登录失败 - 用户不存在 | NORMAL | - |
| test_user_login_missing_parameters | 测试用户登录失败 - 参数缺失 | NORMAL | - |
| test_user_login_invalid_phone_format | 测试用户登录失败 - 无效手机号格式 | NORMAL | - |

**参数化测试**:
- 登录类型: phone, email
- 错误密码: 多种错误密码场景
- 用户不存在: 多种标识符类型
- 参数缺失: 多种缺失场景

### 2. 用户注册接口测试 (TestUserRegisterAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_user_register_success | 测试用户注册成功 | CRITICAL | smoke |
| test_user_register_duplicate_username | 测试用户注册失败 - 用户名已存在 | NORMAL | - |
| test_user_register_duplicate_email | 测试用户注册失败 - 邮箱已存在 | NORMAL | - |
| test_user_register_duplicate_phone | 测试用户注册失败 - 手机号已存在 | NORMAL | - |
| test_user_register_validation_failure | 测试用户注册失败 - 参数验证失败 | NORMAL | - |

**参数化测试**:
- 参数验证失败: 缺少密码、密码太短、手机号为空、手机号格式错误、邮箱格式错误

### 3. 商家登录接口测试 (TestMerchantLoginAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_merchant_login_success | 测试商家登录成功 | CRITICAL | smoke, merchant |
| test_merchant_login_wrong_password | 测试商家登录失败 - 错误密码 | NORMAL | merchant |
| test_merchant_login_nonexistent | 测试商家登录失败 - 商家不存在 | NORMAL | merchant |
| test_merchant_login_not_approved | 测试商家登录失败 - 商家未审核 | NORMAL | merchant |

### 4. 商家注册接口测试 (TestMerchantRegisterAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_merchant_register_success | 测试商家注册成功 | CRITICAL | smoke, merchant |
| test_merchant_register_duplicate_name | 测试商家注册失败 - 商家名称已存在 | NORMAL | merchant |
| test_merchant_register_duplicate_email | 测试商家注册失败 - 邮箱已存在 | NORMAL | merchant |
| test_merchant_register_duplicate_phone | 测试商家注册失败 - 手机号已存在 | NORMAL | merchant |

### 5. 管理员登录接口测试 (TestAdminLoginAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_admin_login_success | 测试管理员登录成功 | CRITICAL | smoke |
| test_admin_login_wrong_password | 测试管理员登录失败 - 错误密码 | NORMAL | - |
| test_admin_login_nonexistent | 测试管理员登录失败 - 管理员不存在 | NORMAL | - |

### 6. 管理员注册接口测试 (TestAdminRegisterAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_admin_register_success | 测试管理员注册成功 | CRITICAL | smoke |
| test_admin_register_duplicate_username | 测试管理员注册失败 - 用户名已存在 | NORMAL | - |

### 7. 用户登出接口测试 (TestLogoutAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_user_logout_success | 测试用户登出成功 | NORMAL | - |
| test_logout_unauthorized | 测试未认证登出 | NORMAL | - |

### 8. 用户信息接口测试 (TestUserInfoAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_get_user_info_success | 测试获取当前用户信息成功 | CRITICAL | smoke |
| test_get_user_info_unauthorized | 测试未认证访问用户信息 | NORMAL | - |
| test_update_user_info_success | 测试更新用户信息成功 | NORMAL | - |
| test_update_user_info_unauthorized | 测试未认证更新用户信息 | NORMAL | - |
| test_update_user_info_validation | 测试更新用户信息参数验证 | NORMAL | - |

**参数化测试**:
- 参数验证: 邮箱格式错误、手机号格式错误、空数据

### 9. 修改密码接口测试 (TestChangePasswordAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_change_password_success | 测试修改密码成功 | CRITICAL | smoke |
| test_change_password_wrong_old_password | 测试修改密码失败 - 旧密码错误 | NORMAL | - |
| test_change_password_invalid_new_password | 测试修改密码失败 - 新密码格式错误 | NORMAL | - |
| test_change_password_unauthorized | 测试修改密码失败 - 未认证访问 | NORMAL | - |

**参数化测试**:
- 新密码格式错误: 密码太短、空密码、空格密码

### 10. 发送验证码接口测试 (TestSendVerifyCodeAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_send_verify_code_success | 测试发送验证码成功 | NORMAL | - |
| test_send_verify_code_invalid_email | 测试发送验证码失败 - 邮箱格式错误 | NORMAL | - |
| test_send_verify_code_rate_limit | 测试发送验证码失败 - 发送频率限制 | NORMAL | skip |

**参数化测试**:
- 邮箱格式错误: 无效邮箱、空邮箱、非邮箱格式

### 11. 重置密码接口测试 (TestResetPasswordAPI)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_reset_password_success | 测试重置密码成功 | NORMAL | skip |
| test_reset_password_wrong_verify_code | 测试重置密码失败 - 验证码错误 | NORMAL | - |
| test_reset_password_expired_verify_code | 测试重置密码失败 - 验证码过期 | NORMAL | skip |

### 12. 认证安全测试 (TestAuthenticationSecurity)

| 测试用例 | 描述 | 优先级 | 标记 |
|---------|------|--------|------|
| test_sql_injection_protection | 测试SQL注入防护 | CRITICAL | security |
| test_xss_protection | 测试XSS防护 | CRITICAL | security |
| test_invalid_token_access | 测试无效Token访问 | NORMAL | - |
| test_no_token_access_protected_endpoint | 测试无Token访问受保护接口 | NORMAL | - |

**参数化测试**:
- SQL注入: 多种SQL注入payload
- XSS攻击: 多种XSS payload

## 测试统计

- **总测试类数**: 12
- **总测试用例数**: 约60+
- **参数化测试场景**: 15+
- **CRITICAL优先级**: 11
- **NORMAL优先级**: 49+
- **安全测试**: 4

## 测试特性

### 1. 使用BaseAPITest基类

所有测试类都继承自 `BaseAPITest`，可以使用以下方法：
- `assert_status_code()` - 断言状态码
- `assert_response_success()` - 断言响应成功
- `assert_response_error()` - 断言响应错误
- `get_auth_headers()` - 获取认证头
- `log_response()` - 记录响应详情

### 2. 使用断言工具

使用 `AssertionBuilder` 进行链式断言：
```python
AssertionBuilder(response) \
    .assert_status_code(200) \
    .assert_response_key_exists("data") \
    .assert_response_time(2.0)
```

### 3. 参数化测试

使用 `@pytest.mark.parametrize` 覆盖多种场景：
```python
@pytest.mark.parametrize("login_type", ["phone", "email"])
def test_user_login_success(self, unit_http_client, test_user, login_type):
    ...
```

### 4. Pytest Markers

使用以下markers标记测试：
- `@pytest.mark.auth` - 认证测试
- `@pytest.mark.unit_auth` - 单元认证测试
- `@pytest.mark.smoke` - 冒烟测试
- `@pytest.mark.merchant` - 商家相关测试
- `@pytest.mark.security` - 安全测试

### 5. Allure报告

使用Allure注解生成测试报告：
- `@allure.feature()` - 功能模块
- `@allure.story()` - 用户故事
- `@allure.title()` - 测试标题
- `@allure.severity()` - 严重程度

## 测试数据管理

### 1. 使用TestDataBuilder

通过 `test_data_builder` fixture创建测试数据：
```python
user_data = test_data_builder.build_user(
    username="custom_user",
    email="custom@example.com"
)
```

### 2. 使用测试Fixtures

- `test_user` - 创建测试用户
- `test_merchant` - 创建测试商家
- `unit_http_client` - HTTP客户端
- `test_data_builder` - 测试数据构建器

## 运行测试

### 运行所有认证测试
```bash
pytest tests/unit/test_auth_api.py -v
```

### 运行特定测试类
```bash
pytest tests/unit/test_auth_api.py::TestUserLoginAPI -v
```

### 运行冒烟测试
```bash
pytest tests/unit/test_auth_api.py -m smoke -v
```

### 运行安全测试
```bash
pytest tests/unit/test_auth_api.py -m security -v
```

### 生成Allure报告
```bash
pytest tests/unit/test_auth_api.py --alluredir=./allure-results
allure serve ./allure-results
```

## 注意事项

1. **测试隔离**: 每个测试函数使用独立的HTTP客户端和数据，确保测试之间互不影响
2. **数据清理**: 测试结束后会自动清理创建的测试数据
3. **异步测试**: 部分测试可能需要等待后端响应，使用合理的超时设置
4. **环境依赖**: 测试依赖后端API服务，确保后端服务正常运行
5. **跳过测试**: 部分测试标记为skip，需要特定条件才能运行

## 后续改进建议

1. **增加性能测试**: 添加响应时间验证
2. **增加并发测试**: 测试并发登录场景
3. **增加边界值测试**: 测试字段长度限制
4. **增加国际化测试**: 测试多语言错误消息
5. **增加日志验证**: 验证操作日志记录
6. **完善验证码测试**: 实现完整的验证码流程测试

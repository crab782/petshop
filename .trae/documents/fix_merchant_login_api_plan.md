# 修复商家登录API调用错误计划

## 问题分析

通过分析代码和用户提供的F12信息，发现以下问题：

### 1. 问题现象
- **注册成功**：商家注册到 `/api/auth/merchant/register` 返回 201 Created
- **登录失败**：商家登录到 `/api/auth/login` 返回 401 Unauthorized，错误信息 "User not found, please use phone number to login"

### 2. 问题原因
- **前端商家登录组件** `src/views/merchant/Login.vue:37`
  - 调用的是 `/api/auth/login`（用户登录API）
  - 应该是 `/api/auth/merchant/login`（商家登录API）

```javascript
// 当前错误代码
const response = await axios.post('/api/auth/login', {
  loginIdentifier: loginForm.value.phone,
  password: loginForm.value.password,
  role: 'merchant'
})
```

### 3. 后端API分析
- **用户登录API** `/api/auth/login`：从 user 表查询用户
- **商家登录API** `/api/auth/merchant/login`：从 merchant 表查询商家

所以商家账号（phone: 13912340001）存在于 merchant 表，但登录时调用了用户登录API去 user 表查询，自然找不到。

## 解决方案

修改前端商家登录组件，将登录API从 `/api/auth/login` 改为 `/api/auth/merchant/login`。

### 具体步骤

1. 修改 `src/views/merchant/Login.vue` 第37行
   - 将 `'/api/auth/login'` 改为 `'/api/auth/merchant/login'`

2. 验证修复效果
   - 使用商家账号登录，应该成功返回JWT令牌

## 风险评估

### 低风险
- 修改仅涉及API路径，不影响其他逻辑
- 商家登录API已存在且正常工作
- 修改后可以正常登录商家账号

## 预期结果

修复后，商家可以使用注册的手机号和密码成功登录商家端。
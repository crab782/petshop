# 修复平台端登录API调用错误计划

## 问题分析

通过检查代码，发现平台端登录页面与商家端存在相同的问题：

### 1. 问题现象
- **平台端登录** `/src/views/admin/Login.vue:37`
- 调用的是 `/api/auth/login`（用户登录API）
- 应该是 `/api/auth/admin/login`（管理员登录API）

### 2. 问题原因
- **前端平台端登录组件** `src/views/admin/Login.vue:37`
  - 调用的是 `/api/auth/login`（用户登录API）
  - 应该是 `/api/auth/admin/login`（管理员登录API）

```javascript
// 当前错误代码
const response = await request.post('/api/auth/login', {
  loginIdentifier: loginForm.value.phone,
  password: loginForm.value.password,
  role: 'admin'
})
```

### 3. 后端API分析
- **用户登录API** `/api/auth/login`：从 user 表查询用户
- **管理员登录API** `/api/auth/admin/login`：从 admin 表查询管理员

所以管理员账号（如 admin）存在于 admin 表，但登录时调用了用户登录API去 user 表查询，自然找不到。

## 解决方案

修改前端平台端登录组件，将登录API从 `/api/auth/login` 改为 `/api/auth/admin/login`。

### 具体步骤

1. 修改 `src/views/admin/Login.vue` 第37行
   - 将 `'/api/auth/login'` 改为 `'/api/auth/admin/login'`

2. 验证修复效果
   - 使用管理员账号登录，应该成功返回JWT令牌

## 风险评估

### 低风险
- 修改仅涉及API路径，不影响其他逻辑
- 管理员登录API已存在且正常工作
- 修改后可以正常登录管理员账号

## 预期结果

修复后，管理员可以使用账号和密码成功登录平台端。
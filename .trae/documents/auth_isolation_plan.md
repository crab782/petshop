# 认证状态隔离方案实施计划

## 问题分析

用户端、商家端和平台端在同一浏览器中登录时会互相覆盖登录状态，导致无法同时登录多个平台。需要实现三端认证状态的完全隔离，使它们在用户眼中看起来是三个独立的系统。

## 现状分析

### 已完成的工作
1. **API请求拦截器**：已在 `request.ts` 中实现基于路径的token选择逻辑
2. **商家端**：登录和logout功能已正确使用 `merchant_token`
3. **用户端**：登录和logout功能已正确使用 `user_token`
4. **平台端**：登录功能已正确使用 `admin_token`，但logout功能未实现token清除

### 待完成的工作
1. **平台端logout**：修复 `AdminLayout.vue` 中的 `handleLogout` 函数，添加清除 `admin_token` 的逻辑
2. **验证完整流程**：确保三端都能独立登录和退出，互不影响

## 实施步骤

### 步骤1：修复AdminLayout.vue的logout功能
- 更新 `AdminLayout.vue` 中的 `handleLogout` 函数
- 添加清除 `admin_token` 和 `adminInfo` 的逻辑
- 确保跳转到正确的平台端登录页面

### 步骤2：验证三端认证隔离
- 测试用户端、商家端、平台端的登录流程
- 验证同一浏览器中三个平台可以同时登录
- 测试各平台的退出功能，确保只清除对应平台的token
- 验证401错误处理是否正确跳转到对应平台的登录页

## 技术方案

### 1. 平台端logout功能修复
```typescript
const handleLogout = () => {
  localStorage.removeItem('admin_token')
  sessionStorage.removeItem('admin_token')
  localStorage.removeItem('adminInfo')
  sessionStorage.removeItem('adminInfo')
  router.push('/admin/login')
}
```

### 2. 认证状态隔离机制
- **用户端**：使用 `user_token` 和 `userInfo` 作为存储键
- **商家端**：使用 `merchant_token` 和 `merchant_info` 作为存储键
- **平台端**：使用 `admin_token` 和 `adminInfo` 作为存储键
- **API拦截器**：基于当前路径自动选择对应平台的token
- **401处理**：根据当前路径跳转到对应平台的登录页

## 验证标准

1. **独立登录**：同一浏览器中可以同时登录用户端、商家端和平台端
2. **独立退出**：退出某一平台时不影响其他平台的登录状态
3. **正确认证**：各平台API请求使用正确的token
4. **错误处理**：401错误时跳转到对应平台的登录页

## 风险评估

- **低风险**：修改仅限于logout功能，不涉及核心认证逻辑
- **兼容性**：保持现有API和存储结构，只添加缺失的logout逻辑
- **测试覆盖**：需要测试三端的登录/退出流程

## 实施时间

- 预计完成时间：30分钟
- 主要工作：修改单个文件的logout函数，然后进行验证测试
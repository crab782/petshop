# 移除商家端页面登录验证计划

## 问题分析

**问题现象**：访问 http://localhost:5173/merchant/home 时，虽然路由守卫已禁用，但页面仍被跳转到 /merchant/login。

**根本原因**：
1. `src/views/merchant/MerchantLayout.vue` 组件内部有登录验证逻辑
2. 第20-28行的 `checkSession()` 函数会检查 `merchant_token`
3. 如果没有token，会显示警告并跳转到登录页面
4. 第32行的 `loadMerchantInfo()` 函数会调用 `checkSession()`

## 解决方案

### 目标
- 移除商家端页面的登录验证
- 允许直接访问所有商家端页面，无需登录
- 保持用户端页面的登录验证逻辑不变

### 实施步骤

#### 步骤1：修改 MerchantLayout.vue 组件
- 移除 `checkSession()` 函数中的登录验证逻辑
- 修改 `loadMerchantInfo()` 函数，不再调用 `checkSession()`
- 确保即使没有token也能获取商家信息（使用mock数据）

#### 步骤2：验证修改效果
- 启动开发服务器
- 访问 http://localhost:5173/merchant/home
- 验证页面能正常加载，不会跳转到登录页面
- 验证商家信息能正常显示（使用mock数据）
- 验证其他商家端页面也能正常访问

#### 步骤3：测试用户端页面
- 确保用户端页面的登录验证逻辑不受影响
- 验证用户端页面功能正常

## 技术实现

### 修改 MerchantLayout.vue
1. 移除或修改 `checkSession()` 函数，使其始终返回 true
2. 修改 `loadMerchantInfo()` 函数，不再调用 `checkSession()`
3. 确保即使没有token，也能通过mock服务获取商家信息

### 预期结果

- ✅ 访问 http://localhost:5173/merchant/home 不再跳转到登录页面
- ✅ 商家端所有页面都可以直接访问
- ✅ 商家信息能正常显示（使用mock数据）
- ✅ 用户端页面的登录验证逻辑不受影响
- ✅ 页面功能完整，交互逻辑正确

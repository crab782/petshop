# 修复商家端店铺管理页面白屏问题计划

## 问题分析

通过分析代码和浏览器控制台错误，发现以下问题：

### 1. 问题现象
- 商家端登录成功后，点击左侧菜单的"店铺管理"，页面跳转到 `http://localhost:5173/merchant/profile` 后白屏
- 控制台显示错误："No match found for location with path"，说明路由不存在

### 2. 问题原因
- **前端商家Layout组件** `src/views/merchant/MerchantLayout.vue:118-121`
  - 菜单链接到 `index="/merchant/profile"`
  - 但是在路由配置中，商家端路由下没有 `/profile` 路径的定义

### 3. 路由配置分析
- **商家端路由** `src/router/index.ts:255-360`
  - 店铺管理相关路由：
    - `/merchant/shop/edit` (shop-edit/index.vue) - 店铺信息
    - `/merchant/shop/settings` (shop-settings/index.vue) - 店铺设置
  - 没有 `/merchant/profile` 路由

### 4. 控制台错误
- "No match found for location with path" - 路由不存在
- "The 'next' callback in navigation guards is deprecated" - 路由守卫警告（不影响功能）

## 解决方案

修改商家端Layout组件中的导航菜单，将"店铺管理"链接到正确的路由路径。

### 具体步骤

1. 修改 `src/views/merchant/MerchantLayout.vue` 第118-121行
   - 将 `index="/merchant/profile"` 改为 `index="/merchant/shop/edit"`

2. 修改下拉菜单中的"店铺设置"链接（第92行）
   - 将 `router.push('/merchant/profile')` 改为 `router.push('/merchant/shop/settings')`

3. 验证修复效果
   - 点击"店铺管理"菜单，应该跳转到店铺信息页面 `/merchant/shop/edit`
   - 点击下拉菜单的"店铺设置"，应该跳转到店铺设置页面 `/merchant/shop/settings`

## 风险评估

### 低风险
- 修改仅涉及路由链接，不影响其他逻辑
- 正确的路由路径已存在且正常工作
- 修改后可以正常访问店铺管理相关页面

## 预期结果

修复后，商家可以正常访问店铺管理相关页面，不再出现白屏现象。
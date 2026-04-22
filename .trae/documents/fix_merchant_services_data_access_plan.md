# 修复商家服务管理页面数据显示问题计划

## 问题分析
商家端服务管理页面（http://localhost:5173/merchant/services）无法正确显示服务数据，页面显示空白。虽然后端API返回了正确的数据（包含3个服务），但前端代码错误地处理了响应数据结构。

## 现状分析
- 后端API `/api/merchant/services` 返回了正确的数据结构：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [ ... ]  // 包含3个服务
  }
  ```
- `request.ts` 的响应拦截器已经返回了 `data.data`（即API响应中的data字段）
- 但前端代码在第71行又访问了 `.data`，导致获取到 `undefined`

## 问题代码
**位置**：`petshop-vue/src/views/merchant/merchant-services/index.vue` 第71行

**问题代码**：
```javascript
const res = await getMerchantServices()
let data = res.data || []  // 错误：res 已经是 data 对象
```

**修复后代码**：
```javascript
const res = await getMerchantServices()
let data = res || []  // 正确：res 已经是响应拦截器返回的 data
```

## 实施步骤

### 步骤1：修复数据访问逻辑
- 修改 `merchant-services/index.vue` 第71行
- 将 `res.data` 改为 `res`

### 步骤2：验证修复结果
- 刷新商家服务管理页面
- 确认页面能正确显示服务列表
- 确认搜索、筛选功能正常工作

## 技术方案
修改 `fetchServices` 函数中的数据访问方式，正确使用 `request.ts` 响应拦截器返回的数据结构。

## 风险评估
- **低风险**：修改仅限于数据访问逻辑，不涉及核心功能
- **兼容性**：保持现有API和功能不变
- **测试覆盖**：验证服务列表显示和搜索筛选功能

## 预期效果
- 服务管理页面能正确显示服务列表
- 搜索和筛选功能正常工作
- 批量操作功能正常工作
# 修复商家评价页面数据显示问题 Spec

## Why
商家评价页面（http://localhost:5173/merchant/reviews）无法正确显示评价数据，页面显示空白并提示"获取评价列表失败，请稍后重试"。虽然后端API返回了正确的数据，但前端代码错误地处理了响应数据结构。

## What Changes
- 修复 merchant-reviews/index.vue 中的数据访问逻辑
- 更新响应数据的处理方式，正确使用 request.ts 响应拦截器返回的数据结构

## Impact
- 受影响的规范：商家端评价管理功能
- 受影响的代码：petshop-vue/src/views/merchant/merchant-reviews/index.vue

## ADDED Requirements

### Requirement: 正确处理API响应数据
系统应正确处理 request.ts 响应拦截器返回的数据结构。

#### Scenario: 获取评价列表成功
- **WHEN** 商家访问评价列表页面
- **THEN** 系统应正确解析API响应数据并显示评价列表

#### Scenario: 获取评分统计成功
- **WHEN** 系统获取评分统计数据
- **THEN** 系统应正确解析统计数据显示评分分布

## MODIFIED Requirements

### Requirement: 数据访问逻辑修复
修复 merchant-reviews/index.vue 中的数据访问错误：

**问题代码**：
```javascript
const res = await getMerchantReviews(queryParams.value)
const data = res.data.data  // 错误：res 已经是 data 对象
```

**修复后代码**：
```javascript
const res = await getMerchantReviews(queryParams.value)
const data = res  // 正确：res 已经是响应拦截器返回的 data
```

**问题代码**：
```javascript
const statsRes = await request.get('/api/merchant/reviews/statistics')
const statsData = statsRes.data.data  // 错误：statsRes 已经是 data 对象
```

**修复后代码**：
```javascript
const statsRes = await request.get('/api/merchant/reviews/statistics')
const statsData = statsRes  // 正确：statsRes 已经是响应拦截器返回的 data
```

## REMOVED Requirements
无移除的需求。

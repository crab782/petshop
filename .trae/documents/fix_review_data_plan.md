# 修复商家评价页面数据显示问题计划

## 问题分析

通过分析代码和API响应，发现以下问题：

### 1. 问题现象
- API `/api/merchant/reviews` 返回 200，数据包含3条评价
- 前端显示"获取评价列表失败，请稍后重试"
- 页面显示空数据

### 2. 根本原因
前端的 `Review` 接口定义与后端返回的数据结构不匹配：

**后端返回**：
```json
{
  "id": 4007,
  "userId": 1,
  "merchantId": 4006,
  "serviceId": 4004,
  "appointmentId": 4005,
  "rating": 5,
  "comment": "服务非常好！",
  "replyContent": null,
  "replyTime": null,
  "createdAt": "2026-04-22T23:59:36",
  "status": "pending",
  "user": null,
  "merchant": null,
  "service": null,
  "appointment": null,
  "reply": null
}
```

**前端期望**（merchant.ts:57-71）：
```typescript
export interface Review {
  id: number
  userId: number
  userName: string           // 后端返回中不存在
  userAvatar?: string        // 后端返回中不存在
  serviceId: number
  serviceName: string         // 后端返回中不存在
  orderId: number            // 后端返回中不存在，应该是appointmentId
  rating: number
  content: string            // 后端返回中是comment
  reviewTime: string         // 后端返回中是createdAt
  replyStatus: 'replied' | 'pending'  // 后端返回中是status
  replyContent?: string
  replyTime?: string
}
```

### 3. 错误触发点
前端代码在 `fetchReviews` 函数中尝试访问 `data.content`，但后端返回的数据结构与前端接口不匹配，导致后续的数据处理出错，触发了 catch 块的错误提示。

## 解决方案

修改前端代码，使其能够处理后端返回的原始数据结构。

### 具体步骤

1. 修改 `merchant-reviews/index.vue` 中的 `fetchReviews` 函数
   - 适配后端返回的数据结构
   - 将后端字段映射到前端期望的字段

2. 修改表格渲染代码
   - 适配新的数据结构
   - 处理缺失的字段（如userName、serviceName等）

## 风险评估

### 低风险
- 修改仅涉及前端数据处理，不影响后端API
- 所有修改都添加了合理的默认值处理

## 预期结果

修复后，评价页面应正常显示3条评价数据，不再显示错误提示。
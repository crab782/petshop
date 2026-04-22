# 修复商家评价页面显示错误问题计划

## 问题分析

通过分析代码和API响应，发现以下问题：

### 1. 问题现象
- API `/api/merchant/reviews` 返回 200，数据为空
- API `/api/merchant/reviews/statistics` 返回 200，但数据结构与前端期望不一致
- 前端显示"获取评价列表失败，请稍后重试"

### 2. 问题原因
后端 `getReviewStatistics` 返回的数据结构与前端期望不匹配：

**后端返回**：
```json
{
  "averageRating": 0.0,
  "ratingDistribution": {
    "1": 0,
    "2": 0,
    "3": 0,
    "4": 0,
    "5": 0
  },
  "totalReviews": 0
}
```

**前端期望**（merchant-reviews/index.vue:89-96）：
```javascript
const statsData = statsRes.data.data
ratingDistribution.value = {
  five: statsData.fiveStarCount || 0,
  four: statsData.fourStarCount || 0,
  three: statsData.threeStarCount || 0,
  two: statsData.twoStarCount || 0,
  one: statsData.oneStarCount || 0
}
```

前端期望的字段名是 `fiveStarCount`, `fourStarCount`, `threeStarCount`, `twoStarCount`, `oneStarCount`，但后端返回的是 `ratingDistribution` 对象。

### 3. 错误处理问题
即使statistics API成功返回，但因为 `statsData.fiveStarCount` 是 `undefined`，导致后续数据处理出错，触发了 catch 块的错误提示。

## 解决方案

修改后端 `ReviewService.getReviewStatistics` 方法，使其返回与前端期望一致的数据结构。

### 具体步骤

1. 修改 `src/main/java/com/petshop/service/ReviewService.java` 的 `getReviewStatistics` 方法
   - 将 `ratingDistribution` 对象扁平化为 `fiveStarCount`, `fourStarCount`, `threeStarCount`, `twoStarCount`, `oneStarCount`

2. 验证修复效果
   - 刷新评价页面，应显示统计数据（全部为0）
   - 不再显示"获取评价列表失败"错误

## 风险评估

### 低风险
- 修改仅涉及返回数据的字段名，不影响业务逻辑
- 前端已有默认值处理（`|| 0`）

## 预期结果

修复后，评价页面应正常显示统计数据（全部为0），不再显示错误提示。
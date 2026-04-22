# 修复用户评价日期显示问题计划

## 问题分析

通过分析店铺详情页代码（`src/views/user/user-shop/index.vue`），发现以下问题：

1. **日期显示问题**：用户评价部分显示"Invalid Date"，这是因为日期格式化函数无法正确处理日期数据
2. **原因分析**：
   - `formatDate` 函数（第120-122行）直接使用 `new Date(date).toLocaleDateString('zh-CN')`
   - 当 `review.createTime` 为 `undefined`、`null` 或格式不正确时，会返回"Invalid Date"
   - 模板中直接调用 `formatDate(review.createTime)`（第338行），没有进行空值检查

## 解决方案

### 1. 修复 `formatDate` 函数
- 添加空值检查，当日期为 `undefined` 或 `null` 时返回空字符串或默认值
- 添加日期有效性检查，当日期格式不正确时返回空字符串或默认值

### 2. 具体修改步骤

#### 步骤1：修改 `formatDate` 函数
- 将第120-122行的函数修改为：
```javascript
const formatDate = (date: string) => {
  if (!date) return ''
  const d = new Date(date)
  return isNaN(d.getTime()) ? '' : d.toLocaleDateString('zh-CN')
}
```

#### 步骤2：验证修改效果
- 刷新店铺详情页：http://localhost:5174/user/shop/1
- 检查用户评价部分的日期显示是否正确

## 技术原理

1. **空值检查**：使用 `if (!date) return ''` 处理 `undefined`、`null`、空字符串等情况
2. **日期有效性检查**：使用 `isNaN(d.getTime())` 检查日期是否有效
3. **默认值处理**：当日期无效时返回空字符串，避免显示"Invalid Date"

## 风险评估

### 低风险
- 修改仅涉及前端显示逻辑，不影响后端API
- 所有修改都添加了合理的默认值，不会导致新的问题
- 修改范围小，仅影响用户评价的日期显示

### 注意事项
- 确保修改后的代码与现有代码风格保持一致
- 测试时需要确保各种数据状态下都能正常显示
- 检查是否有其他页面也存在类似的日期显示问题

## 预期结果

修复后，用户评价部分的日期将不再显示"Invalid Date"，而是显示正确的日期或空字符串，提升用户体验。
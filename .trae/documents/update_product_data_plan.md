# 更新商品数据计划

## 问题分析

通过分析店铺详情页代码（`src/views/user/user-shop/index.vue`），发现以下关键信息：

1. **商品数据来源**：商品名称和描述完全来自后端API返回的数据
2. **显示逻辑**：
   - 商品名称：`{{ product.name }}`（第270行）
   - 商品描述：`{{ product.description || '暂无描述' }}`（第271行）
3. **数据获取**：通过`getMerchantProducts(merchantId.value)`获取商品列表

## 结论

**无需修改前端代码**！商品名称和描述是从数据库中读取的，只要更新数据库中的商品数据，前端就会自动显示更新后的内容。

## 解决方案

### 1. 更新数据库中的商品数据
- **目标**：将商品名称从"猫粮"等改为"宠物猫"等
- **方法**：直接修改数据库中的`product`表
- **影响**：所有使用该商品数据的页面都会自动更新

### 2. 具体修改步骤

#### 步骤1：查看当前商品数据
```sql
SELECT id, merchant_id, name, description FROM product WHERE merchant_id = 1;
```

#### 步骤2：更新商品名称和描述
```sql
UPDATE product 
SET name = '宠物猫', description = '可爱的宠物猫' 
WHERE merchant_id = 1 AND name LIKE '%猫粮%';

-- 或者更新所有商品
UPDATE product 
SET name = CONCAT('宠物', SUBSTRING(name, 3)), 
    description = CONCAT('可爱的宠物', SUBSTRING(name, 3)) 
WHERE merchant_id = 1;
```

#### 步骤3：验证修改结果
```sql
SELECT id, name, description FROM product WHERE merchant_id = 1;
```

### 3. 验证前端显示
- 刷新店铺详情页：http://localhost:5174/user/shop/1
- 检查商品列表中的名称和描述是否已更新

## 技术原理

1. **前端数据绑定**：Vue 3的响应式系统会自动更新显示内容
2. **API数据获取**：每次进入页面都会重新从API获取最新数据
3. **数据流动**：数据库 → 后端API → 前端页面

## 风险评估

### 低风险
- 修改仅涉及数据库数据，不影响代码逻辑
- 操作简单，直接修改数据库记录
- 影响范围可控，仅影响指定商家的商品显示

### 注意事项
- 确保修改后的商品名称和描述符合业务需求
- 验证修改后所有相关页面都能正常显示
- 考虑是否需要同时更新商品图片等其他字段

## 预期结果

修改后，店铺详情页的商品列表将显示更新后的商品名称和描述，无需修改前端代码即可实现需求。
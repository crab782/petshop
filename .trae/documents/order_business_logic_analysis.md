# 订单模块业务逻辑分析报告

## 概述
宠物服务平台的订单系统分为两个独立的模块：
1. **服务订单（Appointment）**：用于预约店铺提供的服务（如宠物洗澡、美容、寄养等）
2. **商品订单（ProductOrder）**：用于购买店铺销售的商品（如宠物食品、用品等）

这两个订单系统是**完全分开存放**的，使用不同的数据库表和业务逻辑。

---

## 1. 服务订单（Appointment）业务流程

### 1.1 数据存储结构
- **表名**：`appointment`
- **主要字段**：
  - `id`：预约ID
  - `user_id`：用户ID
  - `merchant_id`：商家ID
  - `service_id`：服务ID
  - `pet_id`：宠物ID
  - `appointment_time`：预约时间
  - `status`：预约状态
  - `total_price`：总价
  - `notes`：备注

### 1.2 订单状态定义
服务订单有以下4种状态：
- **pending**：待处理（初始状态）
- **confirmed**：已确认
- **completed**：已完成
- **cancelled**：已取消

### 1.3 业务流程
```
用户预约服务 → pending（待处理）
     ↓
商家确认预约 → confirmed（已确认）
     ↓
服务完成 → completed（已完成）

或者：
用户/商家取消 → cancelled（已取消）
```

### 1.4 状态更新规则
根据 `isValidStatusTransition` 方法，状态转换有以下规则：

**允许的状态转换**：
- `pending` → `confirmed`（商家确认预约）
- `pending` → `cancelled`（取消预约）
- `confirmed` → `completed`（完成服务）
- `confirmed` → `cancelled`（取消预约）

**不允许的状态转换**：
- `completed` → 任何状态（已完成的服务不能更改）
- `cancelled` → 任何状态（已取消的服务不能恢复）

### 1.5 关键业务逻辑
1. **创建预约**：
   - 用户选择服务、宠物、预约时间
   - 系统自动设置状态为 `pending`
   - 总价从服务价格自动获取

2. **确认预约**：
   - 只有商家可以确认预约
   - 需要验证预约属于当前商家

3. **完成服务**：
   - 只有商家可以标记服务完成
   - 必须先确认预约才能完成

4. **取消预约**：
   - 用户和商家都可以取消
   - 只能取消 `pending` 或 `confirmed` 状态的预约
   - 已完成或已取消的预约不能再次取消

---

## 2. 商品订单（ProductOrder）业务流程

### 2.1 数据存储结构
商品订单使用两个表：

**主订单表（product_order）**：
- `id`：订单ID
- `order_no`：订单编号
- `user_id`：用户ID
- `merchant_id`：商家ID
- `total_price`：总价
- `freight`：运费
- `status`：订单状态
- `pay_method`：支付方式
- `shipping_address`：收货地址
- `logistics_company`：物流公司
- `logistics_number`：物流单号
- `transaction_id`：交易ID
- `paid_at`：支付时间
- `shipped_at`：发货时间
- `completed_at`：完成时间
- `cancelled_at`：取消时间

**订单明细表（product_order_item）**：
- `id`：明细ID
- `order_id`：订单ID
- `product_id`：商品ID
- `quantity`：数量
- `price`：单价

### 2.2 订单状态定义
商品订单有以下5种状态：
- **pending**：待支付（初始状态）
- **paid**：已支付
- **shipped**：已发货
- **completed**：已完成
- **cancelled**：已取消

### 2.3 业务流程
```
用户下单 → pending（待支付）
     ↓
用户支付 → paid（已支付）
     ↓
商家发货 → shipped（已发货）
     ↓
用户确认收货 → completed（已完成）

或者：
用户/商家取消 → cancelled（已取消）
```

### 2.4 状态更新规则
根据代码分析，商品订单的状态转换规则：

**允许的状态转换**：
- `pending` → `paid`（用户支付）
- `pending` → `cancelled`（取消订单）
- `paid` → `shipped`（商家发货）
- `paid` → `cancelled`（退款取消）
- `shipped` → `completed`（用户确认收货）

**不允许的状态转换**：
- `completed` → 任何状态（已完成的订单不能更改）
- `cancelled` → 任何状态（已取消的订单不能恢复）

### 2.5 关键业务逻辑
1. **创建订单**：
   - 用户选择商品、数量、收货地址
   - 系统自动生成订单编号
   - 系统自动设置状态为 `pending`
   - 计算总价（商品价格 × 数量 + 运费）

2. **支付订单**：
   - 用户选择支付方式
   - 支付成功后状态变为 `paid`
   - 记录支付时间和交易ID

3. **发货**：
   - 只有商家可以发货
   - 需要填写物流公司和物流单号
   - 发货后状态变为 `shipped`
   - 记录发货时间

4. **确认收货**：
   - 用户确认收货
   - 状态变为 `completed`
   - 记录完成时间

5. **取消订单**：
   - 用户和商家都可以取消
   - 不同状态的取消逻辑不同：
     - `pending` 状态：直接取消
     - `paid` 状态：需要退款处理
   - 记录取消时间

---

## 3. 订单存储方式总结

### 3.1 完全分开存储
服务订单和商品订单是**完全分开存放**的：

**服务订单**：
- 表：`appointment`
- 实体：`Appointment.java`
- 服务：`AppointmentService.java`
- 控制器：`MerchantApiController.java` 中的 appointments 相关接口

**商品订单**：
- 主表：`product_order`
- 明细表：`product_order_item`
- 实体：`ProductOrder.java`、`ProductOrderItem.java`
- 服务：`ProductOrderService.java`
- 控制器：`MerchantApiController.java` 中的 orders/product-orders 相关接口

### 3.2 为什么分开存储
1. **业务逻辑不同**：
   - 服务订单：预约时间、宠物信息、服务时长
   - 商品订单：物流信息、支付信息、订单明细

2. **状态流转不同**：
   - 服务订单：pending → confirmed → completed
   - 商品订单：pending → paid → shipped → completed

3. **关联关系不同**：
   - 服务订单：关联服务、宠物
   - 商品订单：关联商品、订单明细

4. **操作方式不同**：
   - 服务订单：预约、确认、完成
   - 商品订单：下单、支付、发货、收货

---

## 4. 关键业务规则总结

### 4.1 服务订单规则
1. 一个预约只能对应一个服务
2. 必须指定宠物
3. 必须指定预约时间
4. 状态转换有严格的顺序限制
5. 已完成或已取消的预约不能修改

### 4.2 商品订单规则
1. 一个订单可以包含多个商品（通过 product_order_item）
2. 需要收货地址
3. 支持物流跟踪
4. 状态转换有严格的顺序限制
5. 已完成或已取消的订单不能修改

### 4.3 共同规则
1. 都需要验证商家权限
2. 都有状态流转限制
3. 都记录创建时间和更新时间
4. 都可以取消（在允许的状态下）

---

## 5. API接口总结

### 5.1 服务订单API
- `GET /api/merchant/appointments`：获取商家的服务预约列表
- `PUT /api/merchant/appointments/{id}/status`：更新预约状态
- `GET /api/merchant/appointments/recent`：获取最近预约
- `GET /api/user/appointments`：获取用户的预约列表
- `POST /api/user/appointments`：创建预约
- `PUT /api/user/appointments/{id}/cancel`：取消预约

### 5.2 商品订单API
- `GET /api/merchant/orders`：获取商家的商品订单列表
- `PUT /api/merchant/orders/{id}/status`：更新订单状态
- `GET /api/merchant/product-orders`：获取商品订单列表
- `PUT /api/merchant/product-orders/{id}/status`：更新商品订单状态
- `GET /api/user/orders`：获取用户的订单列表
- `POST /api/user/orders`：创建订单
- `PUT /api/user/orders/{id}/pay`：支付订单
- `PUT /api/user/orders/{id}/confirm`：确认收货
- `PUT /api/user/orders/{id}/cancel`：取消订单

---

## 6. 建议和改进方向

### 6.1 当前设计的优点
1. 业务逻辑清晰，服务和商品分离
2. 状态流转有严格的验证
3. 权限控制完善

### 6.2 潜在改进方向
1. **添加状态转换日志**：记录每次状态变更的时间和操作人
2. **添加退款流程**：商品订单支付后取消需要退款处理
3. **添加评价关联**：服务完成后可以关联评价
4. **添加库存管理**：商品订单需要扣减库存
5. **添加通知机制**：状态变更时通知用户和商家

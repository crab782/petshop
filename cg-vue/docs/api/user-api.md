# 用户端API接口文档

## 概述

本文档描述了用户端28个页面所需的所有API接口。所有接口基于RESTful风格设计，返回JSON格式数据。

**基础URL**: `/api`

**认证方式**: Bearer Token (JWT)

---

## 接口分类

### 1. 认证相关接口 (auth.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 用户登录 | POST | /api/auth/login | ✅ 已存在 | 用户登录认证 |
| 用户注册 | POST | /api/auth/register | ✅ 已存在 | 用户注册 |
| 退出登录 | POST | /api/auth/logout | ✅ 已存在 | 退出登录 |
| 获取用户信息 | GET | /api/auth/userinfo | ✅ 已存在 | 获取当前用户信息 |
| 更新用户信息 | PUT | /api/auth/userinfo | ✅ 已存在 | 更新用户资料 |
| 修改密码 | PUT | /api/auth/password | ✅ 已存在 | 修改登录密码 |
| 发送验证码 | POST | /api/auth/sendVerifyCode | ✅ 已存在 | 发送邮箱验证码 |
| 重置密码 | POST | /api/auth/resetPassword | ✅ 已存在 | 重置密码 |

#### 1.1 用户登录

**请求**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

**响应**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 1.2 用户注册

**请求**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "email": "string",
  "phone": "string"
}
```

**响应**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 1.3 获取用户信息

**请求**
```http
GET /api/auth/userinfo
Authorization: Bearer {token}
```

**响应**
```json
{
  "id": 1,
  "username": "user1",
  "email": "user@example.com",
  "phone": "13800138000",
  "avatar": "https://example.com/avatar.jpg",
  "role": "user"
}
```

#### 1.4 更新用户信息

**请求**
```http
PUT /api/auth/userinfo
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "phone": "string",
  "avatar": "string"
}
```

#### 1.5 修改密码

**请求**
```http
PUT /api/auth/password
Authorization: Bearer {token}
Content-Type: application/json

{
  "oldPassword": "string",
  "newPassword": "string"
}
```

**响应**
```json
{
  "success": true,
  "message": "密码修改成功"
}
```

#### 1.6 发送验证码

**请求**
```http
POST /api/auth/sendVerifyCode
Content-Type: application/json

{
  "email": "string"
}
```

#### 1.7 重置密码

**请求**
```http
POST /api/auth/resetPassword
Content-Type: application/json

{
  "email": "string",
  "verifyCode": "string",
  "password": "string"
}
```

---

### 2. 宠物管理接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取宠物列表 | GET | /api/user/pets | ✅ 已存在 | 获取用户所有宠物 |
| 获取宠物详情 | GET | /api/user/pets/:id | ✅ 已存在 | 获取单个宠物信息 |
| 添加宠物 | POST | /api/user/pets | ✅ 已存在 | 新增宠物 |
| 更新宠物 | PUT | /api/user/pets/:id | ✅ 已存在 | 修改宠物信息 |
| 删除宠物 | DELETE | /api/user/pets/:id | ✅ 已存在 | 删除宠物 |

#### 2.1 获取宠物列表

**请求**
```http
GET /api/user/pets
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "name": "小白",
    "type": "dog",
    "breed": "金毛",
    "age": 3,
    "gender": "male",
    "avatar": "https://example.com/pet.jpg",
    "description": "活泼可爱",
    "weight": 25.5,
    "bodyType": "medium",
    "furColor": "golden",
    "personality": "friendly",
    "userId": 1
  }
]
```

#### 2.2 添加宠物

**请求**
```http
POST /api/user/pets
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "小白",
  "type": "dog",
  "breed": "金毛",
  "age": 3,
  "gender": "male",
  "avatar": "https://example.com/pet.jpg",
  "description": "活泼可爱"
}
```

#### 2.3 更新宠物

**请求**
```http
PUT /api/user/pets/:id
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "小白",
  "age": 4
}
```

---

### 3. 地址管理接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取地址列表 | GET | /api/user/addresses | ✅ 已存在 | 获取用户所有收货地址 |
| 添加地址 | POST | /api/user/addresses | ✅ 已存在 | 新增收货地址 |
| 更新地址 | PUT | /api/user/addresses/:id | ✅ 已存在 | 修改收货地址 |
| 删除地址 | DELETE | /api/user/addresses/:id | ✅ 已存在 | 删除收货地址 |
| 设置默认地址 | PUT | /api/user/addresses/:id/default | ✅ 已存在 | 设置默认收货地址 |

#### 3.1 获取地址列表

**请求**
```http
GET /api/user/addresses
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "userId": 1,
    "contactName": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detailAddress": "某某街道某某小区1号楼",
    "isDefault": true
  }
]
```

#### 3.2 添加地址

**请求**
```http
POST /api/user/addresses
Authorization: Bearer {token}
Content-Type: application/json

{
  "contactName": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detailAddress": "某某街道某某小区1号楼",
  "isDefault": false
}
```

---

### 4. 商家服务接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取商家列表 | GET | /api/merchants | ✅ 已存在 | 获取所有商家列表 |
| 获取商家详情 | GET | /api/merchant/:id | ✅ 已存在 | 获取商家详细信息 |
| 获取商家服务 | GET | /api/merchant/:id/services | ✅ 已存在 | 获取商家提供的服务 |
| 获取商家商品 | GET | /api/merchant/:id/products | ✅ 已存在 | 获取商家销售的商品 |
| 获取商家评价 | GET | /api/merchant/:id/reviews | ✅ 已存在 | 获取商家的用户评价 |
| 获取可用预约时段 | GET | /api/merchant/:id/available-slots | ✅ 已存在 | 获取商家可预约时段 |

#### 4.1 获取商家列表

**请求**
```http
GET /api/merchants?keyword=宠物&rating=4&sortBy=rating&page=1&pageSize=10
Authorization: Bearer {token}
```

**响应**
```json
{
  "data": [
    {
      "id": 1,
      "name": "宠物乐园",
      "logo": "https://example.com/logo.jpg",
      "address": "北京市朝阳区xxx",
      "phone": "010-12345678",
      "rating": 4.8,
      "serviceCount": 15,
      "isFavorited": false
    }
  ],
  "total": 100
}
```

#### 4.2 获取商家详情

**请求**
```http
GET /api/merchant/:id
Authorization: Bearer {token}
```

**响应**
```json
{
  "id": 1,
  "name": "宠物乐园",
  "logo": "https://example.com/logo.jpg",
  "contact": "张经理",
  "phone": "010-12345678",
  "address": "北京市朝阳区xxx",
  "description": "专业宠物服务",
  "rating": 4.8,
  "isFavorited": false
}
```

#### 4.3 获取可用预约时段

**请求**
```http
GET /api/merchant/:id/available-slots?date=2024-01-15
Authorization: Bearer {token}
```

**响应**
```json
{
  "date": "2024-01-15",
  "slots": [
    { "time": "09:00", "available": true },
    { "time": "10:00", "available": false },
    { "time": "11:00", "available": true }
  ],
  "workingHours": {
    "start": "09:00",
    "end": "18:00"
  }
}
```

---

### 5. 服务接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取服务列表 | GET | /api/services | ✅ 已存在 | 获取所有服务 |
| 获取服务详情 | GET | /api/services/:id | ✅ 已存在 | 获取服务详细信息 |
| 搜索服务 | GET | /api/services/search | ✅ 已存在 | 按关键字搜索服务 |
| 获取推荐服务 | GET | /api/services/recommended | ✅ 已存在 | 获取推荐服务列表 |

#### 5.1 获取服务列表

**请求**
```http
GET /api/services?type=grooming
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "name": "宠物美容",
    "description": "专业宠物美容服务",
    "price": 100,
    "duration": 60,
    "merchantId": 1,
    "merchantName": "宠物乐园",
    "category": "grooming"
  }
]
```

---

### 6. 商品购物接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取商品列表 | GET | /api/products | ✅ 已存在 | 获取商品列表 |
| 获取商品详情 | GET | /api/products/:id | ✅ 已存在 | 获取商品详细信息 |
| 搜索商品 | GET | /api/products/search | ✅ 已存在 | 按关键字搜索商品 |
| 获取商品评价 | GET | /api/products/:id/reviews | ✅ 已存在 | 获取商品评价列表 |
| 添加商品收藏 | POST | /api/user/favorites/products | ✅ 已存在 | 收藏商品 |
| 取消商品收藏 | DELETE | /api/user/favorites/products/:id | ✅ 已存在 | 取消收藏商品 |
| 检查商品收藏状态 | GET | /api/user/favorites/products/:id/check | ✅ 已存在 | 检查是否已收藏 |
| 加入购物车 | POST | /api/user/cart | ✅ 已存在 | 添加商品到购物车 |
| 获取购物车 | GET | /api/user/cart | ✅ 已存在 | 获取购物车列表 |
| 更新购物车商品 | PUT | /api/user/cart | ✅ 已存在 | 更新购物车商品数量 |
| 删除购物车商品 | DELETE | /api/user/cart/:id | ✅ 已存在 | 从购物车移除商品 |
| 批量删除购物车商品 | DELETE | /api/user/cart/batch | ✅ 已存在 | 批量移除购物车商品 |

#### 6.1 获取商品详情

**请求**
```http
GET /api/products/:id
Authorization: Bearer {token}
```

**响应**
```json
{
  "id": 1,
  "name": "宠物狗粮",
  "description": "高品质狗粮",
  "price": 99.9,
  "stock": 100,
  "merchantId": 1,
  "image": "https://example.com/product.jpg"
}
```

#### 6.2 加入购物车

**请求**
```http
POST /api/user/cart
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

#### 6.3 获取购物车

**请求**
```http
GET /api/user/cart
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "宠物狗粮",
    "productImage": "https://example.com/product.jpg",
    "price": 99.9,
    "quantity": 2,
    "merchantId": 1,
    "merchantName": "宠物乐园"
  }
]
```

---

### 7. 订单管理接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取订单列表 | GET | /api/user/orders | ✅ 已存在 | 获取用户所有订单 |
| 获取订单详情 | GET | /api/user/orders/:id | ✅ 已存在 | 获取订单详细信息 |
| 创建订单 | POST | /api/user/orders | ✅ 已存在 | 创建新订单 |
| 预览订单 | POST | /api/user/orders/preview | ✅ 已存在 | 预览订单信息 |
| 支付订单 | POST | /api/user/orders/:id/pay | ✅ 已存在 | 支付订单 |
| 获取支付状态 | GET | /api/user/orders/:id/pay/status | ✅ 已存在 | 获取订单支付状态 |
| 取消订单 | PUT | /api/user/orders/:id/cancel | ✅ 已存在 | 取消订单 |
| 退款订单 | POST | /api/user/orders/:id/refund | ✅ 已存在 | 申请退款 |
| 确认收货 | PUT | /api/user/orders/:id/confirm | ✅ 已存在 | 确认收货 |
| 删除订单 | DELETE | /api/user/orders/:id | ✅ 已存在 | 删除订单 |
| 批量取消订单 | PUT | /api/user/orders/batch-cancel | ✅ 已存在 | 批量取消订单 |
| 批量删除订单 | DELETE | /api/user/orders/batch-delete | ✅ 已存在 | 批量删除订单 |

#### 7.1 创建订单

**请求**
```http
POST /api/user/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": 1,
  "addressId": 1,
  "quantity": 2
}
```

#### 7.2 预览订单

**请求**
```http
POST /api/user/orders/preview
Authorization: Bearer {token}
Content-Type: application/json

{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

**响应**
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "宠物狗粮",
      "productImage": "https://example.com/product.jpg",
      "price": 99.9,
      "quantity": 2,
      "subtotal": 199.8
    }
  ],
  "productTotal": 199.8,
  "shippingFee": 10,
  "discount": 0,
  "totalAmount": 209.8
}
```

#### 7.3 获取订单详情

**请求**
```http
GET /api/user/orders/:id
Authorization: Bearer {token}
```

**响应**
```json
{
  "id": 1,
  "orderNo": "202401150001",
  "status": "paid",
  "createTime": "2024-01-15 10:00:00",
  "payTime": "2024-01-15 10:05:00",
  "totalPrice": 209.8,
  "freight": 10,
  "payMethod": "wechat",
  "remark": "请尽快发货",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "宠物狗粮",
      "productImage": "https://example.com/product.jpg",
      "price": 99.9,
      "quantity": 2,
      "subtotal": 199.8
    }
  ],
  "address": {
    "name": "张三",
    "phone": "13800138000",
    "address": "北京市朝阳区xxx"
  },
  "timeline": [
    { "status": "created", "time": "2024-01-15 10:00:00", "description": "订单创建" },
    { "status": "paid", "time": "2024-01-15 10:05:00", "description": "支付成功" }
  ]
}
```

#### 7.4 支付订单

**请求**
```http
POST /api/user/orders/:id/pay
Authorization: Bearer {token}
Content-Type: application/json

{
  "payMethod": "wechat"
}
```

#### 7.5 获取支付状态

**请求**
```http
GET /api/user/orders/:id/pay/status
Authorization: Bearer {token}
```

**响应**
```json
{
  "orderId": 1,
  "payStatus": "success",
  "payTime": "2024-01-15 10:05:00",
  "transactionId": "wx123456789"
}
```

---

### 8. 预约管理接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取预约列表 | GET | /api/user/appointments | ✅ 已存在 | 获取用户所有预约 |
| 获取预约详情 | GET | /api/user/appointments/:id | ✅ 已存在 | 获取预约详细信息 |
| 创建预约 | POST | /api/user/appointments | ✅ 已存在 | 创建新预约 |
| 取消预约 | PUT | /api/user/appointments/:id/cancel | ✅ 已存在 | 取消预约 |
| 获取预约统计 | GET | /api/user/appointments/stats | ✅ 已存在 | 获取预约统计数据 |

#### 8.1 创建预约

**请求**
```http
POST /api/user/appointments
Authorization: Bearer {token}
Content-Type: application/json

{
  "serviceId": 1,
  "petId": 1,
  "appointmentTime": "2024-01-15 10:00:00",
  "remark": "请准时"
}
```

#### 8.2 获取预约详情

**请求**
```http
GET /api/user/appointments/:id
Authorization: Bearer {token}
```

**响应**
```json
{
  "id": 1,
  "serviceId": 1,
  "serviceName": "宠物美容",
  "servicePrice": 100,
  "serviceDuration": 60,
  "merchantId": 1,
  "merchantName": "宠物乐园",
  "merchantPhone": "010-12345678",
  "merchantAddress": "北京市朝阳区xxx",
  "petId": 1,
  "petName": "小白",
  "petType": "dog",
  "appointmentTime": "2024-01-15 10:00:00",
  "status": "pending",
  "totalPrice": 100,
  "remark": "请准时",
  "createdAt": "2024-01-14 15:00:00",
  "updatedAt": "2024-01-14 15:00:00"
}
```

#### 8.3 获取预约统计

**请求**
```http
GET /api/user/appointments/stats
Authorization: Bearer {token}
```

**响应**
```json
{
  "total": 50,
  "pending": 5,
  "confirmed": 10,
  "completed": 30,
  "cancelled": 5
}
```

---

### 9. 评价管理接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 添加评价 | POST | /api/user/reviews | ✅ 已存在 | 添加服务/商品评价 |
| 获取用户评价列表 | GET | /api/user/reviews | ✅ 已存在 | 获取用户所有评价 |
| 获取评价详情列表 | GET | /api/user/reviews | ✅ 已存在 | 获取评价详情列表(带分页) |
| 更新评价 | PUT | /api/user/reviews/:id | ✅ 已存在 | 修改评价内容 |
| 删除评价 | DELETE | /api/user/reviews/:id | ✅ 已存在 | 删除评价 |

#### 9.1 添加评价

**请求**
```http
POST /api/user/reviews
Authorization: Bearer {token}
Content-Type: application/json

{
  "appointmentId": 1,
  "merchantId": 1,
  "serviceId": 1,
  "rating": 5,
  "comment": "服务很好，宠物很开心"
}
```

#### 9.2 获取用户评价列表

**请求**
```http
GET /api/user/reviews?type=service&startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {token}
```

**响应**
```json
{
  "data": [
    {
      "id": 1,
      "appointmentId": 1,
      "serviceName": "宠物美容",
      "merchantName": "宠物乐园",
      "merchantId": 1,
      "serviceId": 1,
      "rating": 5,
      "comment": "服务很好",
      "createdAt": "2024-01-15 12:00:00"
    }
  ],
  "total": 10
}
```

---

### 10. 收藏管理接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取收藏商家列表 | GET | /api/user/favorites | ✅ 已存在 | 获取用户收藏的商家 |
| 添加商家收藏 | POST | /api/user/favorites | ✅ 已存在 | 收藏商家 |
| 取消商家收藏 | DELETE | /api/user/favorites/:id | ✅ 已存在 | 取消收藏商家 |
| 获取收藏服务列表 | GET | /api/user/favorites/services | ✅ 已存在 | 获取用户收藏的服务 |
| 添加服务收藏 | POST | /api/user/favorites/services | ✅ 已存在 | 收藏服务 |
| 取消服务收藏 | DELETE | /api/user/favorites/services/:id | ✅ 已存在 | 取消收藏服务 |

#### 10.1 获取收藏商家列表

**请求**
```http
GET /api/user/favorites
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "merchantId": 1,
    "merchantName": "宠物乐园",
    "merchantLogo": "https://example.com/logo.jpg",
    "merchantAddress": "北京市朝阳区xxx",
    "merchantPhone": "010-12345678",
    "createdAt": "2024-01-15 10:00:00"
  }
]
```

---

### 11. 通知管理接口 (notification.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取通知列表 | GET | /api/user/notifications | ✅ 已存在 | 获取用户所有通知 |
| 标记已读 | PUT | /api/user/notifications/:id/read | ✅ 已存在 | 标记单条通知已读 |
| 全部标记已读 | PUT | /api/user/notifications/read-all | ✅ 已存在 | 标记所有通知已读 |
| 批量标记已读 | PUT | /api/user/notifications/batch-read | ✅ 已存在 | 批量标记通知已读 |
| 删除通知 | DELETE | /api/user/notifications/:id | ✅ 已存在 | 删除单条通知 |
| 批量删除通知 | DELETE | /api/user/notifications/batch | ✅ 已存在 | 批量删除通知 |
| 获取未读数量 | GET | /api/user/notifications/unread-count | ✅ 已存在 | 获取未读通知数量 |

#### 11.1 获取通知列表

**请求**
```http
GET /api/user/notifications?type=order&isRead=false
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "type": "order",
    "title": "订单支付成功",
    "summary": "您的订单已支付成功",
    "content": "订单号：202401150001，支付金额：209.8元",
    "isRead": false,
    "createTime": "2024-01-15 10:05:00"
  }
]
```

#### 11.2 批量标记已读

**请求**
```http
PUT /api/user/notifications/batch-read
Authorization: Bearer {token}
Content-Type: application/json

{
  "ids": [1, 2, 3]
}
```

---

### 12. 搜索接口 (search.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取搜索建议 | GET | /api/search/suggestions | ✅ 已存在 | 获取搜索建议 |
| 获取热门关键词 | GET | /api/search/hot-keywords | ✅ 已存在 | 获取热门搜索词 |
| 保存搜索历史 | POST | /api/user/search-history | ✅ 已存在 | 保存搜索记录 |
| 获取搜索历史 | GET | /api/user/search-history | ✅ 已存在 | 获取搜索历史 |
| 清空搜索历史 | DELETE | /api/user/search-history | ✅ 已存在 | 清空搜索历史 |
| 搜索商品 | GET | /api/products/search | ✅ 已存在 | 搜索商品 |
| 搜索服务 | GET | /api/services/search | ✅ 已存在 | 搜索服务 |

#### 12.1 获取搜索建议

**请求**
```http
GET /api/search/suggestions?keyword=宠物
Authorization: Bearer {token}
```

**响应**
```json
{
  "services": [
    {
      "id": 1,
      "name": "宠物美容",
      "price": 100,
      "merchantName": "宠物乐园"
    }
  ],
  "products": [
    {
      "id": 1,
      "name": "宠物狗粮",
      "price": 99.9
    }
  ],
  "merchants": [
    {
      "id": 1,
      "name": "宠物乐园",
      "rating": 4.8
    }
  ]
}
```

#### 12.2 获取热门关键词

**请求**
```http
GET /api/search/hot-keywords?limit=10
Authorization: Bearer {token}
```

**响应**
```json
[
  { "keyword": "宠物美容", "count": 1000 },
  { "keyword": "狗粮", "count": 800 }
]
```

---

### 13. 公告接口 (announcement.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取公告列表 | GET | /api/announcements | ✅ 已存在 | 获取所有公告 |
| 获取公告详情 | GET | /api/announcements/:id | ✅ 已存在 | 获取公告详细信息 |

#### 13.1 获取公告列表

**请求**
```http
GET /api/announcements
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "title": "系统升级公告",
    "content": "系统将于2024年1月20日进行升级...",
    "status": "published",
    "publishTime": "2024-01-15 10:00:00"
  }
]
```

---

### 14. 用户首页接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取首页统计 | GET | /api/user/home/stats | ✅ 已存在 | 获取首页统计数据 |
| 获取最近活动 | GET | /api/user/home/activities | ✅ 已存在 | 获取最近活动记录 |

#### 14.1 获取首页统计

**请求**
```http
GET /api/user/home/stats
Authorization: Bearer {token}
```

**响应**
```json
{
  "petCount": 3,
  "pendingAppointments": 2,
  "reviewCount": 15
}
```

#### 14.2 获取最近活动

**请求**
```http
GET /api/user/home/activities?limit=5
Authorization: Bearer {token}
```

**响应**
```json
[
  {
    "id": 1,
    "type": "appointment",
    "title": "预约了宠物美容服务",
    "time": "2024-01-15 10:00:00",
    "status": "pending",
    "statusColor": "warning",
    "relatedId": 1
  }
]
```

---

### 15. 用户已购服务接口 (user.ts)

| 接口名称 | 方法 | URL | 状态 | 用途 |
|---------|------|-----|------|------|
| 获取已购服务列表 | GET | /api/user/services | ✅ 已存在 | 获取用户已购买的服务 |

#### 15.1 获取已购服务列表

**请求**
```http
GET /api/user/services?keyword=美容&status=active&page=1&pageSize=10
Authorization: Bearer {token}
```

**响应**
```json
{
  "data": [
    {
      "id": 1,
      "name": "宠物美容套餐",
      "merchant": "宠物乐园",
      "merchantId": 1,
      "price": 299,
      "purchaseDate": "2024-01-10",
      "expiryDate": "2024-02-10",
      "status": "active",
      "category": "grooming",
      "serviceId": 1
    }
  ],
  "total": 5
}
```

---

## 接口统计

| 分类 | 接口数量 | 状态 |
|------|---------|------|
| 认证相关 | 8 | ✅ 完整 |
| 宠物管理 | 5 | ✅ 完整 |
| 地址管理 | 5 | ✅ 完整 |
| 商家服务 | 6 | ✅ 完整 |
| 服务接口 | 4 | ✅ 完整 |
| 商品购物 | 12 | ✅ 完整 |
| 订单管理 | 12 | ✅ 完整 |
| 预约管理 | 5 | ✅ 完整 |
| 评价管理 | 5 | ✅ 完整 |
| 收藏管理 | 6 | ✅ 完整 |
| 通知管理 | 7 | ✅ 完整 |
| 搜索接口 | 7 | ✅ 完整 |
| 公告接口 | 2 | ✅ 完整 |
| 用户首页 | 2 | ✅ 完整 |
| 已购服务 | 1 | ✅ 完整 |
| **总计** | **87** | **✅ 100%** |

---

## 错误码说明

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，请先登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 更新日志

| 日期 | 版本 | 更新内容 |
|------|------|---------|
| 2024-01-15 | 1.0.0 | 初始版本，完成所有用户端API接口文档 |

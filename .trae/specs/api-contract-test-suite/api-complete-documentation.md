# 宠物服务平台 - 完整API接口文档

> 生成时间：2026-04-21
> 项目路径：d:\j\cg\cg
> 文档类型：完整API接口文档

---

## 目录

- [1. 认证管理 (Auth)](#1-认证管理-auth)
- [2. 用户端API (User)](#2-用户端api-user)
- [3. 商家端API (Merchant)](#3-商家端api-merchant)
- [4. 平台端API (Admin)](#4-平台端api-admin)
- [5. 公共API (Public)](#5-公共api-public)
- [6. 服务API (Service)](#6-服务api-service)
- [7. 商品API (Product)](#7-商品api-product)
- [8. 商家公共API (Merchant Public)](#8-商家公共api-merchant-public)
- [9. 购物车API (Cart)](#9-购物车api-cart)
- [10. 公告API (Announcement)](#10-公告api-announcement)
- [11. 搜索API (Search)](#11-搜索api-search)
- [12. 通用响应结构](#12-通用响应结构)

---

## 1. 认证管理 (Auth)

### 1.1 用户登录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/login` |
| 方法 | POST |
| 权限 | permitAll |
| 请求参数 | `{username?: string, phone?: string, password: string}` |
| 响应类型 | `{token: string, user: UserDTO}` |
| 状态码 | 200(成功), 400(参数错误), 401(认证失败) |

### 1.2 用户注册

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/register` |
| 方法 | POST |
| 权限 | permitAll |
| 请求参数 | `{username?: string, password: string, email?: string, phone: string}` |
| 响应类型 | `{token: string, user: UserDTO}` |
| 状态码 | 201(创建成功), 400(参数错误) |

### 1.3 商家注册

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/merchant/register` |
| 方法 | POST |
| 权限 | permitAll |
| 请求参数 | `{password: string, email?: string, phone: string, contact_person?: string, name?: string, logo?: string, address?: string}` |
| 响应类型 | `{message: string}` |
| 状态码 | 201(创建成功), 400(参数错误) |

### 1.4 退出登录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/logout` |
| 方法 | POST |
| 权限 | permitAll |
| 请求参数 | 无 |
| 响应类型 | `{success: boolean}` |
| 状态码 | 200(成功) |

### 1.5 获取用户信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/userinfo` |
| 方法 | GET |
| 权限 | 需认证 |
| 请求参数 | 无 |
| 响应类型 | `UserDTO` |
| 状态码 | 200(成功), 401(未认证) |

### 1.6 更新用户信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/userinfo` |
| 方法 | PUT |
| 权限 | 需认证 |
| 请求参数 | `{username?: string, email?: string, phone?: string, avatar?: string, password?: string}` |
| 响应类型 | `UserDTO` |
| 状态码 | 200(成功), 400(参数错误) |

### 1.7 发送验证码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/sendVerifyCode` |
| 方法 | POST |
| 权限 | permitAll |
| 请求参数 | `{email: string}` |
| 响应类型 | `{success: boolean}` |
| 状态码 | 200(成功), 400(参数错误) |

### 1.8 重置密码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/resetPassword` |
| 方法 | POST |
| 权限 | permitAll |
| 请求参数 | `{email: string, verifyCode: string, password: string}` |
| 响应类型 | `{success: boolean}` |
| 状态码 | 200(成功), 400(参数错误) |

### 1.9 修改密码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/password` |
| 方法 | PUT |
| 权限 | 需认证 |
| 请求参数 | `{oldPassword: string, newPassword: string}` |
| 响应类型 | `{success: boolean, message: string}` |
| 状态码 | 200(成功), 400(参数错误) |

---

## 2. 用户端API (User)

### 2.1 个人资料

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/user/profile` | 获取个人资料 | 无 | `User` | 200, 401 |
| PUT | `/api/user/profile` | 更新个人资料 | `User` | `User` | 200, 401 |

### 2.2 首页统计

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/home/stats` | 获取首页统计 | 无 | `HomeStatsDTO` | 200, 401 |
| GET | `/api/user/home/activities` | 获取最近活动 | `limit`(int, 默认10) | `List<ActivityDTO>` | 200, 401 |

### 2.3 宠物管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/pets` | 获取宠物列表 | 无 | `List<Pet>` | 200, 401 |
| POST | `/api/user/pets` | 添加宠物 | `Pet`(body) | `Pet` | 201, 401 |
| GET | `/api/user/pets/{id}` | 获取宠物详情 | `id`(path) | `Pet` | 200, 401, 404 |
| PUT | `/api/user/pets/{id}` | 更新宠物 | `id`(path), `Pet`(body) | `Pet` | 200, 401, 404 |
| DELETE | `/api/user/pets/{id}` | 删除宠物 | `id`(path) | `Void` | 204, 401, 404 |

### 2.4 预约管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/appointments` | 获取预约列表 | `status`(可选), `keyword`(可选), `startDate`(可选), `endDate`(可选), `page`(默认0), `pageSize`(默认10) | `PageResponse<Appointment>` | 200, 401 |
| GET | `/api/user/appointments/{id}` | 获取预约详情 | `id`(path) | `AppointmentDTO` | 200, 401, 404 |
| POST | `/api/user/appointments` | 创建预约 | `{serviceId: number, petId: number, appointmentTime: string, notes?: string}`(body) | `Map<String, Object>` | 201, 400, 401 |
| PUT | `/api/user/appointments/{id}/cancel` | 取消预约 | `id`(path) | `Void` | 200, 401, 404 |
| GET | `/api/user/appointments/stats` | 获取预约统计 | 无 | `Map<String, Long>` | 200, 401 |

### 2.5 订单管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/orders` | 获取订单列表 | `status`(可选), `keyword`(可选), `startDate`(可选), `endDate`(可选), `page`(默认1), `pageSize`(默认10) | `PageResponse<OrderDTO>` | 200, 401 |
| GET | `/api/user/orders/{id}` | 获取订单详情 | `id`(path) | `OrderDTO` | 200, 401 |
| POST | `/api/user/orders` | 创建订单 | `{items: [{productId: number, quantity: number}], addressId: number, paymentMethod?: string, remark?: string}`(body) | `CreateOrderResponse` | 201, 401 |
| POST | `/api/user/orders/preview` | 预览订单 | `{items: [{productId: number, quantity: number}]}`(body) | `OrderPreviewDTO` | 200, 401 |
| POST | `/api/user/orders/{id}/pay` | 支付订单 | `id`(path), `{payMethod: string}`(body) | `PayResponse` | 200, 401 |
| GET | `/api/user/orders/{id}/pay/status` | 获取支付状态 | `id`(path) | `PayStatusResponse` | 200, 401 |
| PUT | `/api/user/orders/{id}/cancel` | 取消订单 | `id`(path) | `Void` | 200, 401 |
| POST | `/api/user/orders/{id}/refund` | 申请退款 | `id`(path), `{reason: string}`(body) | `Void` | 200, 401 |
| PUT | `/api/user/orders/{id}/confirm` | 确认收货 | `id`(path) | `Void` | 200, 401 |
| DELETE | `/api/user/orders/{id}` | 删除订单 | `id`(path) | `Void` | 204, 401 |
| PUT | `/api/user/orders/batch-cancel` | 批量取消 | `{ids: number[]}`(body) | `Void` | 200, 401 |
| DELETE | `/api/user/orders/batch-delete` | 批量删除 | `{ids: number[]}`(body) | `Void` | 204, 401 |

### 2.6 我的服务

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/services` | 获取已购买服务 | `status`(可选), `keyword`(可选), `page`(默认0), `pageSize`(默认10) | `PageResponse<UserPurchasedService>` | 200, 401 |

### 2.7 收货地址

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/addresses` | 获取地址列表 | 无 | `List<AddressDTO>` | 200, 401 |
| POST | `/api/user/addresses` | 添加地址 | `Address`(body) | `AddressDTO` | 201, 401 |
| PUT | `/api/user/addresses/{id}` | 更新地址 | `id`(path), `Address`(body) | `AddressDTO` | 200, 401, 404 |
| DELETE | `/api/user/addresses/{id}` | 删除地址 | `id`(path) | `Void` | 200, 401, 404 |
| PUT | `/api/user/addresses/{id}/default` | 设为默认地址 | `id`(path) | `AddressDTO` | 200, 401, 404 |

### 2.8 评价管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/reviews` | 获取评价列表 | `rating`(可选), `page`(默认0), `size`(默认10) | `Page<ReviewDTO>` | 200, 401 |
| GET | `/api/user/reviews/{id}` | 获取评价详情 | `id`(path) | `Review` | 200, 401, 404 |
| POST | `/api/user/reviews` | 添加评价 | `{appointmentId: number, rating: number, comment: string}`(body) | `Review` | 201, 400, 401, 404 |
| PUT | `/api/user/reviews/{id}` | 更新评价 | `id`(path), `{rating: number, comment: string}`(body) | `Review` | 200, 400, 401, 404 |
| DELETE | `/api/user/reviews/{id}` | 删除评价 | `id`(path) | `Void` | 200, 401, 404 |

### 2.9 收藏管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/favorites` | 获取收藏商家列表 | 无 | `List<FavoriteDTO>` | 200, 401 |
| POST | `/api/user/favorites` | 收藏商家 | `{merchantId: number}`(body) | `FavoriteDTO` | 201, 400, 401, 404 |
| DELETE | `/api/user/favorites/{id}` | 取消收藏商家 | `id`(path) | `Void` | 200, 401, 404 |
| GET | `/api/user/favorites/services` | 获取收藏服务列表 | 无 | `List<FavoriteServiceDTO>` | 200, 401 |
| POST | `/api/user/favorites/services` | 收藏服务 | `{serviceId: number}`(body) | `FavoriteServiceDTO` | 201, 400, 401, 404 |
| DELETE | `/api/user/favorites/services/{id}` | 取消收藏服务 | `id`(path) | `Void` | 200, 401, 404 |
| POST | `/api/user/favorites/products` | 收藏商品 | `{productId: number}`(body) | `FavoriteProductDTO` | 201, 400, 401, 404 |
| DELETE | `/api/user/favorites/products/{id}` | 取消收藏商品 | `id`(path) | `Void` | 200, 401, 404 |
| GET | `/api/user/favorites/products/{id}/check` | 检查商品收藏状态 | `id`(path) | `{isFavorited: boolean}` | 200, 401 |

### 2.10 通知管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/notifications` | 获取通知列表 | `type`(可选), `isRead`(可选) | `List<NotificationDTO>` | 200, 401 |
| PUT | `/api/user/notifications/{id}/read` | 标记已读 | `id`(path) | `Void` | 200, 401 |
| PUT | `/api/user/notifications/read-all` | 全部标记已读 | 无 | `Void` | 200, 401 |
| PUT | `/api/user/notifications/batch-read` | 批量标记已读 | `{ids: number[]}`(body) | `Void` | 200, 401 |
| DELETE | `/api/user/notifications/{id}` | 删除通知 | `id`(path) | `Void` | 200, 401 |
| DELETE | `/api/user/notifications/batch` | 批量删除通知 | `{ids: number[]}`(body) | `Void` | 200, 401 |
| GET | `/api/user/notifications/unread-count` | 获取未读数量 | 无 | `{count: number}` | 200, 401 |

---

## 3. 商家端API (Merchant)

### 3.1 商家资料

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/merchant/profile` | 获取商家资料 | 无 | `Merchant` | 200, 401 |
| GET | `/api/merchant/info` | 获取商家信息 | 无 | `Merchant` | 200, 401 |
| PUT | `/api/merchant/profile` | 更新商家资料 | `Merchant`(body) | `Merchant` | 200, 401 |
| PUT | `/api/merchant/info` | 更新商家信息 | `Merchant`(body) | `Merchant` | 200, 401 |

### 3.2 服务管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/services` | 获取服务列表 | 无 | `List<Service>` | 200, 401 |
| POST | `/api/merchant/services` | 添加服务 | `Service`(body) | `Service` | 201, 400, 401 |
| GET | `/api/merchant/services/{id}` | 获取服务详情 | `id`(path) | `Service` | 200, 401, 404 |
| PUT | `/api/merchant/services/{id}` | 更新服务 | `id`(path), `Service`(body) | `Service` | 200, 400, 401, 404 |
| DELETE | `/api/merchant/services/{id}` | 删除服务 | `id`(path) | `Void` | 200, 401, 404 |
| PUT | `/api/merchant/services/batch/status` | 批量更新服务状态 | `{ids: number[], status: string}`(body) | `Map<String, Object>` | 200, 400, 401 |
| DELETE | `/api/merchant/services/batch` | 批量删除服务 | `{ids: number[]}`(body) | `Map<String, Object>` | 200, 400, 401 |

### 3.3 预约管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/appointments` | 获取预约列表 | 无 | `List<Appointment>` | 200, 401 |
| PUT | `/api/merchant/appointments/{id}/status` | 更新预约状态 | `id`(path), `{status: string, rejectReason?: string}`(body) | `Appointment` | 200, 400, 401, 404 |
| GET | `/api/merchant/appointments/recent` | 获取最近预约 | `limit`(默认5) | `List<Appointment>` | 200, 401 |
| GET | `/api/merchant/appointment-stats` | 获取预约统计 | `startDate`(可选), `endDate`(可选) | `Map<String, Object>` | 200, 401 |
| GET | `/api/merchant/appointment-stats/export` | 导出预约统计 | `startDate`(可选), `endDate`(可选), `format`(默认excel) | `byte[]` | 200, 401 |

### 3.4 订单管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/orders` | 获取订单列表 | 无 | `List<ProductOrder>` | 200, 401 |
| PUT | `/api/merchant/orders/{id}/status` | 更新订单状态 | `id`(path), `{status: string}`(body) | `ProductOrder` | 200, 400, 401, 404 |
| GET | `/api/merchant/product-orders` | 获取商品订单 | `status`(可选), `page`(默认0), `pageSize`(默认10) | `List<ProductOrder>` | 200, 401 |
| PUT | `/api/merchant/product-orders/{id}/status` | 更新商品订单状态 | `id`(path), `{status: string}`(body) | `ProductOrder` | 200, 400, 401, 404 |
| PUT | `/api/merchant/product-orders/{id}/logistics` | 更新物流信息 | `id`(path), `{logisticsCompany: string, trackingNumber: string}`(body) | `ProductOrder` | 200, 400, 401, 404 |

### 3.5 商品管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/products` | 获取商品列表 | 无 | `List<Product>` | 200, 401 |
| POST | `/api/merchant/products` | 添加商品 | `Product`(body) | `Product` | 201, 401 |
| GET | `/api/merchant/products/{id}` | 获取商品详情 | `id`(path) | `Product` | 200, 401, 404 |
| PUT | `/api/merchant/products/{id}` | 更新商品 | `id`(path), `Product`(body) | `Product` | 200, 401, 404 |
| DELETE | `/api/merchant/products/{id}` | 删除商品 | `id`(path) | `Void` | 200, 401, 404 |
| GET | `/api/merchant/products/paged` | 分页获取商品 | `page`(默认0), `pageSize`(默认10), `sortBy`(默认createdAt), `sortDir`(默认desc), `status`(可选), `name`(可选), `category`(可选), `minPrice`(可选), `maxPrice`(可选), `stockStatus`(可选) | `PageResponse<Product>` | 200, 401 |
| PUT | `/api/merchant/products/{id}/status` | 更新商品状态 | `id`(path), `{status: string}`(body) | `Product` | 200, 400, 401, 404 |
| PUT | `/api/merchant/products/batch/status` | 批量更新商品状态 | `{ids: number[], status: string}`(body) | `Map<String, Object>` | 200, 400, 401 |
| DELETE | `/api/merchant/products/batch` | 批量删除商品 | `{ids: number[]}`(body) | `Map<String, Object>` | 200, 400, 401 |

### 3.6 分类管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/categories` | 获取分类列表 | 无 | `List<Category>` | 200, 401 |
| POST | `/api/merchant/categories` | 添加分类 | `Category`(body) | `Category` | 201, 400, 401 |
| PUT | `/api/merchant/categories/{id}` | 更新分类 | `id`(path), `Category`(body) | `Category` | 200, 400, 401, 404 |
| DELETE | `/api/merchant/categories/{id}` | 删除分类 | `id`(path) | `Void` | 200, 400, 401, 404 |
| PUT | `/api/merchant/categories/{id}/status` | 更新分类状态 | `id`(path), `{status: string}`(body) | `Category` | 200, 400, 401, 404 |
| PUT | `/api/merchant/categories/batch/status` | 批量更新分类状态 | `{ids: number[], status: string}`(body) | `Map<String, Object>` | 200, 400, 401 |
| DELETE | `/api/merchant/categories/batch` | 批量删除分类 | `{ids: number[]}`(body) | `Map<String, Object>` | 200, 400, 401 |

### 3.7 评价管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/reviews` | 获取评价列表（分页） | `page`(默认0), `size`(默认10), `sortBy`(默认createdAt), `sortDir`(默认desc), `rating`(可选), `keyword`(可选) | `PageResponse<Review>` | 200, 401 |
| GET | `/api/merchant/reviews/statistics` | 获取评价统计 | 无 | `Map<String, Object>` | 200, 401 |
| GET | `/api/merchant/reviews/{id}` | 获取评价详情 | `id`(path) | `Review` | 200, 401, 404 |
| PUT | `/api/merchant/reviews/{id}/reply` | 回复评价 | `id`(path), `{reply: string}`(body) | `Review` | 200, 400, 401, 404 |
| DELETE | `/api/merchant/reviews/{id}` | 删除评价 | `id`(path) | `Void` | 200, 401, 404 |
| GET | `/api/merchant/reviews/recent` | 获取最近评价 | `limit`(默认5) | `List<Review>` | 200, 401 |

### 3.8 账号管理

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| POST | `/api/merchant/change-password` | 修改密码 | `{oldPassword: string, newPassword: string}`(body) | `Void` | 200, 400, 401 |
| POST | `/api/merchant/bind-phone` | 绑定手机号 | `{phone: string, verifyCode: string}`(body) | `Void` | 200, 400, 401 |
| POST | `/api/merchant/bind-email` | 绑定邮箱 | `{email: string, verifyCode: string}`(body) | `Void` | 200, 400, 401 |
| POST | `/api/merchant/send-verify-code` | 发送验证码 | `{target: string, type: string}`(body) | `Void` | 200, 400, 401 |

### 3.9 统计分析

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/dashboard` | 获取首页统计 | 无 | `Map<String, Object>` | 200, 401 |
| GET | `/api/merchant/revenue-stats` | 获取营收统计 | `startDate`(可选), `endDate`(可选) | `Map<String, Object>` | 200, 401 |
| GET | `/api/merchant/revenue-stats/export` | 导出营收统计 | `startDate`(可选), `endDate`(可选) | `byte[]` | 200, 401 |

### 3.10 店铺设置

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/merchant/settings` | 获取店铺设置 | 无 | `Map<String, Object>` | 200, 401 |
| PUT | `/api/merchant/settings` | 更新店铺设置 | `MerchantSettings`(body) | `MerchantSettings` | 200, 401 |
| POST | `/api/merchant/settings/toggle-status` | 切换营业状态 | 无 | `Map<String, Object>` | 200, 401 |

---

## 4. 平台端API (Admin)

### 4.1 用户管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/users` | 获取用户列表 | 无 | `List<User>` | 200, 401 |
| DELETE | `/api/admin/users/{id}` | 删除用户 | `id`(path) | `Void` | 204, 401 |
| PUT | `/api/admin/users/{id}/status` | 更新用户状态 | `id`(path), `{status: string}`(body) | `User` | 200, 400, 401, 404 |
| PUT | `/api/admin/users/batch/status` | 批量更新用户状态 | `{ids: number[], status: string}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| DELETE | `/api/admin/users/batch` | 批量删除用户 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| GET | `/api/admin/users/recent` | 获取最近注册用户 | `page`(默认0), `pageSize`(默认10) | `PageResponse<User>` | 200, 401 |

### 4.2 商家管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/merchants` | 获取商家列表 | 无 | `List<Merchant>` | 200, 401 |
| GET | `/api/admin/merchants/{id}` | 获取商家详情 | `id`(path) | `MerchantDetailDTO` | 200, 401, 404 |
| PUT | `/api/admin/merchants/{id}/status` | 更新商家状态 | `id`(path), `{status: string}`(body) | `Merchant` | 200, 400, 401, 404 |
| DELETE | `/api/admin/merchants/{id}` | 删除商家 | `id`(path) | `Void` | 204, 401 |
| PUT | `/api/admin/merchants/batch/status` | 批量更新商家状态 | `{ids: number[], status: string}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| DELETE | `/api/admin/merchants/batch` | 批量删除商家 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| GET | `/api/admin/merchants/pending` | 获取待审核商家 | `page`(默认0), `pageSize`(默认10), `keyword`(可选) | `PageResponse<Merchant>` | 200, 401 |
| PUT | `/api/admin/merchants/{id}/audit` | 审核商家 | `id`(path), `{status: string, reason?: string}`(body) | `Merchant` | 200, 400, 401, 404 |

### 4.3 仪表盘

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/dashboard` | 获取仪表盘统计 | 无 | `DashboardStatsDTO` | 200, 401 |
| GET | `/api/admin/dashboard/pending-merchants` | 获取待审核商家（仪表盘） | `page`(默认0), `pageSize`(默认10) | `PageResponse<Merchant>` | 200, 401 |

### 4.4 公告管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/announcements` | 获取公告列表 | `page`(默认0), `pageSize`(默认10), `status`(可选) | `PageResponse<Announcement>` | 200, 401 |
| POST | `/api/admin/announcements` | 创建公告 | `Announcement`(body) | `Announcement` | 200, 401 |
| PUT | `/api/admin/announcements/{id}` | 更新公告 | `id`(path), `Announcement`(body) | `Announcement` | 200, 401, 404 |
| DELETE | `/api/admin/announcements/{id}` | 删除公告 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/announcements/{id}/publish` | 发布公告 | `id`(path) | `Announcement` | 200, 401, 404 |
| PUT | `/api/admin/announcements/{id}/unpublish` | 下架公告 | `id`(path) | `Announcement` | 200, 401, 404 |
| PUT | `/api/admin/announcements/batch/publish` | 批量发布公告 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| PUT | `/api/admin/announcements/batch/unpublish` | 批量下架公告 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| DELETE | `/api/admin/announcements/batch` | 批量删除公告 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |

### 4.5 商品管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/products` | 获取商品列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `status`(可选), `merchantId`(可选), `category`(可选) | `PageResponse<Product>` | 200, 401 |
| GET | `/api/admin/products/{id}` | 获取商品详情 | `id`(path) | `Product` | 200, 401, 404 |
| PUT | `/api/admin/products/{id}` | 更新商品 | `id`(path), `Product`(body) | `Product` | 200, 401, 404 |
| PUT | `/api/admin/products/{id}/status` | 更新商品状态 | `id`(path), `{status: string}`(body) | `Product` | 200, 400, 401, 404 |
| DELETE | `/api/admin/products/{id}` | 删除商品 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/products/batch/status` | 批量更新商品状态 | `{ids: number[], status: string}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| DELETE | `/api/admin/products/batch` | 批量删除商品 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |

### 4.6 服务管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/services` | 获取服务列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `status`(可选), `merchantId`(可选) | `PageResponse<Service>` | 200, 401 |
| PUT | `/api/admin/services/{id}/status` | 更新服务状态 | `id`(path), `{status: string}`(body) | `Service` | 200, 400, 401, 404 |
| DELETE | `/api/admin/services/{id}` | 删除服务 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/services/batch/status` | 批量更新服务状态 | `{ids: number[], status: string}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| DELETE | `/api/admin/services/batch` | 批量删除服务 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |

### 4.7 评价管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/reviews` | 获取评价列表 | `page`(默认0), `pageSize`(默认10), `rating`(可选), `keyword`(可选), `merchantId`(可选), `status`(可选) | `PageResponse<Review>` | 200, 401 |
| GET | `/api/admin/reviews/pending` | 获取待审核评价 | `page`(默认0), `pageSize`(默认10), `keyword`(可选) | `PageResponse<Review>` | 200, 401 |
| PUT | `/api/admin/reviews/{id}/audit` | 审核评价 | `id`(path), `{status: string, reason?: string}`(body) | `Review` | 200, 400, 401, 404 |
| PUT | `/api/admin/reviews/{id}/approve` | 批准评价 | `id`(path) | `Review` | 200, 401, 404 |
| PUT | `/api/admin/reviews/{id}/violation` | 标记评价违规 | `id`(path) | `Review` | 200, 401, 404 |
| DELETE | `/api/admin/reviews/{id}` | 删除评价 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/reviews/batch/status` | 批量更新评价状态 | `{ids: number[], status: string}`(body) | `Map<String, Integer>` | 200, 400, 401 |
| DELETE | `/api/admin/reviews/batch` | 批量删除评价 | `{ids: number[]}`(body) | `Map<String, Integer>` | 200, 400, 401 |

### 4.8 系统设置

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/admin/system/config` | 获取系统配置 | 无 | `Map<String, Object>` | 200, 401 |
| PUT | `/api/admin/system/config` | 更新系统配置 | `Map<String, Object>`(body) | `Map<String, Object>` | 200, 401 |
| GET | `/api/admin/system/settings` | 获取系统设置 | 无 | `SystemSettings` | 200, 401 |
| PUT | `/api/admin/system/settings` | 更新系统设置 | `SystemSettings`(body) | `SystemSettings` | 200, 401 |
| GET | `/api/admin/system/settings/email` | 获取邮件设置 | 无 | `Map<String, Object>` | 200, 401 |
| GET | `/api/admin/system/settings/sms` | 获取短信设置 | 无 | `Map<String, Object>` | 200, 401 |
| GET | `/api/admin/system/settings/payment` | 获取支付设置 | 无 | `Map<String, Object>` | 200, 401 |
| GET | `/api/admin/system/settings/upload` | 获取上传设置 | 无 | `Map<String, Object>` | 200, 401 |

### 4.9 活动管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/activities` | 获取活动列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `type`(可选), `status`(可选), `startDate`(可选), `endDate`(可选) | `PageResponse<Activity>` | 200, 401 |
| POST | `/api/admin/activities` | 创建活动 | `Activity`(body) | `Activity` | 200, 400, 401 |
| PUT | `/api/admin/activities/{id}` | 更新活动 | `id`(path), `Activity`(body) | `Activity` | 200, 400, 401, 404 |
| DELETE | `/api/admin/activities/{id}` | 删除活动 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/activities/{id}/status` | 更新活动状态 | `id`(path), `{status: string}`(body) | `Activity` | 200, 400, 401, 404 |

### 4.10 店铺审核

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/shops/pending` | 获取待审核店铺 | `page`(默认0), `pageSize`(默认10), `keyword`(可选) | `PageResponse<Merchant>` | 200, 401 |
| PUT | `/api/admin/shops/{id}/audit` | 审核店铺 | `id`(path), `{status: string, reason?: string}`(body) | `Merchant` | 200, 400, 401, 404 |

### 4.11 任务管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/tasks` | 获取任务列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `type`(可选), `status`(可选) | `PageResponse<ScheduledTask>` | 200, 401 |
| POST | `/api/admin/tasks` | 创建任务 | `ScheduledTask`(body) | `ScheduledTask` | 200, 400, 401 |
| PUT | `/api/admin/tasks/{id}` | 更新任务 | `id`(path), `ScheduledTask`(body) | `ScheduledTask` | 200, 400, 401, 404 |
| DELETE | `/api/admin/tasks/{id}` | 删除任务 | `id`(path) | `Void` | 204, 401, 404 |
| POST | `/api/admin/tasks/{id}/execute` | 执行任务 | `id`(path) | `Map<String, Object>` | 200, 400, 401, 404 |

---

## 5. 公共API (Public)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/public/services` | 获取所有服务 | 无 | `List<Service>` | 200 |
| GET | `/api/public/services/{id}` | 获取服务详情 | `id`(path) | `Service` | 200, 404 |
| GET | `/api/public/merchants` | 获取所有商家（仅approved） | 无 | `List<Merchant>` | 200 |
| GET | `/api/public/merchants/{id}` | 获取商家详情（仅approved） | `id`(path) | `Merchant` | 200, 404 |
| GET | `/api/public/merchants/{id}/services` | 获取商家服务 | `id`(path) | `List<Service>` | 200, 404 |

---

## 6. 服务API (Service)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/services` | 获取服务列表 | `name`(可选), `minPrice`(可选), `maxPrice`(可选), `minDuration`(可选), `maxDuration`(可选), `status`(可选), `page`(默认0), `pageSize`(默认10) | `List<Service>` | 200 |
| GET | `/api/services/{id}` | 获取服务详情 | `id`(path) | `Service` | 200, 404 |
| GET | `/api/services/search` | 搜索服务 | `keyword`(可选), `merchantId`(可选), `sortBy`(可选), `sortOrder`(可选), `page`(默认0), `pageSize`(默认10) | `List<Service>` | 200 |
| GET | `/api/services/recommended` | 获取推荐服务 | `limit`(可选, 默认10) | `List<Service>` | 200 |

---

## 7. 商品API (Product)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/products` | 获取商品列表 | `name`(可选), `minPrice`(可选), `maxPrice`(可选), `minStock`(可选), `status`(可选), `categoryId`(可选), `page`(默认0), `pageSize`(默认10) | `List<Product>` | 200 |
| GET | `/api/products/{id}` | 获取商品详情 | `id`(path) | `Product` | 200, 404 |
| GET | `/api/products/search` | 搜索商品 | `keyword`(可选), `merchantId`(可选), `sortBy`(可选), `sortOrder`(可选), `page`(默认0), `pageSize`(默认10) | `List<Product>` | 200 |
| GET | `/api/products/{id}/reviews` | 获取商品评价 | `id`(path), `rating`(可选), `page`(默认0), `pageSize`(默认10) | `List<Review>` | 200 |

---

## 8. 商家公共API (Merchant Public)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchants` | 获取商家列表 | `name`(可选), `status`(可选), `minRating`(可选), `page`(默认0), `pageSize`(默认10) | `List<Merchant>` | 200 |
| GET | `/api/merchants/search` | 搜索商家 | `keyword`(可选), `sortBy`(可选), `sortOrder`(可选), `page`(默认0), `pageSize`(默认10) | `List<Merchant>` | 200 |
| GET | `/api/merchant/{id}` | 获取商家详情 | `id`(path) | `Merchant` | 200, 404 |
| GET | `/api/merchant/{id}/services` | 获取商家服务 | `id`(path), `status`(可选), `page`(默认0), `pageSize`(默认10) | `List<Service>` | 200 |
| GET | `/api/merchant/{id}/products` | 获取商家商品 | `id`(path), `status`(可选), `page`(默认0), `pageSize`(默认10) | `List<Product>` | 200 |
| GET | `/api/merchant/{id}/reviews` | 获取商家评价 | `id`(path), `rating`(可选), `page`(默认0), `pageSize`(默认10) | `List<Review>` | 200 |
| GET | `/api/merchant/{id}/available-slots` | 获取可用预约时段 | `id`(path), `date`(必填) | `Map<String, List<String>>` | 200 |

---

## 9. 购物车API (Cart)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/cart` | 获取购物车 | 无 | `List<Cart>` | 200, 401 |
| POST | `/api/user/cart` | 添加到购物车 | `{productId: number, quantity: number}`(body) | `Cart` | 201, 400, 401, 404 |
| PUT | `/api/user/cart` | 更新购物车 | `{cartId: number, quantity: number}`(body) | `Cart` | 200, 400, 401 |
| DELETE | `/api/user/cart/{id}` | 移除购物车项 | `id`(path) | `Void` | 200, 401 |
| DELETE | `/api/user/cart/batch` | 批量移除 | `{ids: number[]}`(body) | `Void` | 200, 400, 401 |

---

## 10. 公告API (Announcement)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/announcements` | 获取公告列表（已发布） | 无 | `List<AnnouncementDTO>` | 200 |
| GET | `/api/announcements/{id}` | 获取公告详情 | `id`(path) | `AnnouncementDTO` | 200, 404 |

---

## 11. 搜索API (Search)

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 权限 | 状态码 |
|------|------|------|------|----------|------|--------|
| GET | `/api/search/suggestions` | 获取搜索建议 | `keyword`(必填) | `SearchSuggestionsDTO` | permitAll | 200 |
| GET | `/api/search/hot-keywords` | 获取热门搜索 | `limit`(默认10) | `List<HotKeywordDTO>` | permitAll | 200 |
| POST | `/api/user/search-history` | 保存搜索历史 | `{keyword: string}`(body) | `Void` | 需认证 | 200, 401 |
| GET | `/api/user/search-history` | 获取搜索历史 | `limit`(默认10) | `List<String>` | 需认证 | 200, 401 |
| DELETE | `/api/user/search-history` | 清空搜索历史 | 无 | `Void` | 需认证 | 204, 401 |

---

## 12. 通用响应结构

### 12.1 ApiResponse<T>

所有API统一响应包装类。

```json
{
  "code": 200,
  "message": "success",
  "data": T
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200表示成功 |
| message | String | 消息描述 |
| data | T | 业务数据 |

### 12.2 PageResponse<T>

分页响应结构。

```json
{
  "data": [T],
  "total": 100,
  "page": 0,
  "pageSize": 10,
  "totalPages": 10
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data | List<T> | 数据列表 |
| total | Long | 总记录数 |
| page | Integer | 当前页码（从0开始） |
| pageSize | Integer | 每页大小 |
| totalPages | Integer | 总页数 |
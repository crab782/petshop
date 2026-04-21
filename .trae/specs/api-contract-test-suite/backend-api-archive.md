# 后端API定义档案

> 生成时间：2026-04-21
> 项目路径：d:\j\cg\cg
> 扫描范围：src/main/java/com/petshop/controller/ 下所有Java控制器文件

---

## 目录

- [1. SecurityConfig 路径权限配置](#1-securityconfig-路径权限配置)
- [2. 通用响应结构](#2-通用响应结构)
- [3. API控制器详细定义](#3-api控制器详细定义)
  - [3.1 AuthApiController - 认证管理](#31-authapicontroller---认证管理)
  - [3.2 UserApiController - 用户端API](#32-userapicontroller---用户端api)
  - [3.3 MerchantApiController - 商家端API](#33-merchantapicontroller---商家端api)
  - [3.4 AdminApiController - 平台端API](#34-adminapicontroller---平台端api)
  - [3.5 PublicApiController - 公共API](#35-publicapicontroller---公共api)
  - [3.6 AnnouncementApiController - 公告API](#36-announcementapicontroller---公告api)
  - [3.7 SearchApiController - 搜索API](#37-searchapicontroller---搜索api)
  - [3.8 ServiceController - 服务API](#38-servicecontroller---服务api)
  - [3.9 ProductController - 商品API](#39-productcontroller---商品api)
  - [3.10 MerchantController (api) - 商家公共查询API](#310-merchantcontroller-api---商家公共查询api)
  - [3.11 CartController - 购物车API](#311-cartcontroller---购物车api)
  - [3.12 RedisLockController - Redis锁测试API](#312-redislockcontroller---redis锁测试api)
- [4. MVC控制器（页面路由）](#4-mvc控制器页面路由)
- [5. DTO数据结构定义](#5-dto数据结构定义)
- [6. 问题与风险标记](#6-问题与风险标记)

---

## 1. SecurityConfig 路径权限配置

配置文件：[SecurityConfig.java](file:///d:/j/cg/cg/src/main/java/com/petshop/config/SecurityConfig.java)

### 会话策略
- **SessionCreationPolicy**: STATELESS（无状态，使用JWT）

### CORS配置
- 允许来源：`http://localhost:5173`, `http://localhost:3000`, `http://127.0.0.1:5173`, `http://127.0.0.1:3000`
- 允许方法：GET, POST, PUT, DELETE, OPTIONS, PATCH
- 允许头：Authorization, Content-Type, X-Requested-With, Accept, Origin
- 暴露头：Authorization
- 允许凭证：true

### 路径权限规则

| 路径模式 | 权限 | 说明 |
|----------|------|------|
| `/`, `/login`, `/login.html`, `/register`, `/register.html` | permitAll | 首页和登录注册页 |
| `/register/user`, `/register/merchant` | permitAll | 用户/商家注册提交 |
| `/index.html`, `/admin-dashboard.html`, `/user-dashboard.html`, `/merchant-dashboard.html` | permitAll | 仪表盘页面 |
| `/merchant-add-service.html`, `/user-add-pet.html`, `/service-*.html` | permitAll | 服务相关页面 |
| `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-ui.html`, `/webjars/**` | permitAll | Swagger文档 |
| `/api/auth/login`, `/api/auth/register`, `/api/auth/logout` | permitAll | 认证接口 |
| `/api/auth/sendVerifyCode`, `/api/auth/resetPassword` | permitAll | 验证码和重置密码 |
| `/api/public/**` | permitAll | 公共API |
| `/api/services`, `/api/services/**` | permitAll | 服务API |
| `/api/products`, `/api/products/**` | permitAll | 商品API |
| `/api/merchants`, `/api/merchants/**` | permitAll | 商家公共API |
| `/api/merchant/**` | permitAll | **⚠️ 商家端API全部公开** |
| `/api/search/**` | permitAll | 搜索API |
| `/user/**` | permitAll | 用户MVC路由 |
| `/merchant/**` | permitAll | 商家MVC路由 |
| `/admin/**` | permitAll | 管理员MVC路由 |
| `/api/user/**` | **authenticated** | 用户端API需认证 |
| `/api/merchant-api/**` | **authenticated** | 商家端API需认证（但实际不存在此路径） |
| `/api/admin-api/**` | **authenticated** | 管理端API需认证（但实际不存在此路径） |
| 其他所有路径 | permitAll | 默认允许 |

---

## 2. 通用响应结构

### ApiResponse<T>

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

### PageResponse<T>

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
| data | List\<T\> | 数据列表 |
| total | Long | 总记录数 |
| page | Integer | 当前页码（从0开始） |
| pageSize | Integer | 每页大小 |
| totalPages | Integer | 总页数 |

---

## 3. API控制器详细定义

### 3.1 AuthApiController - 认证管理

**基础路径**: `/api/auth`
**控制器文件**: [AuthApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/AuthApiController.java)
**权限要求**: 大部分接口 permitAll（登录/注册/验证码/重置密码），userinfo/password 需认证

#### POST /api/auth/login - 用户登录

| 项目 | 详情 |
|------|------|
| 方法 | POST |
| 路径 | `/api/auth/login` |
| 权限 | permitAll |
| 请求体 | `LoginRequest` |
| 响应类型 | `ApiResponse<LoginResponse>` |
| 状态码 | 200(成功), 400(参数错误), 401(认证失败), 500(服务器错误) |

**请求参数 (LoginRequest)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| username | String | 否 | - | 用户名（与phone二选一） |
| phone | String | 否 | - | 手机号（与username二选一） |
| password | String | 是 | 非空 | 密码 |

**响应数据 (LoginResponse)**:

| 字段 | 类型 | 说明 |
|------|------|------|
| token | String | JWT令牌 |
| user | UserDTO | 用户信息 |

---

#### POST /api/auth/register - 用户注册

| 项目 | 详情 |
|------|------|
| 方法 | POST |
| 路径 | `/api/auth/register` |
| 权限 | permitAll |
| 请求体 | `RegisterRequest` |
| 响应类型 | `ApiResponse<LoginResponse>` |
| 状态码 | 201(创建成功), 400(参数错误), 500(服务器错误) |

**请求参数 (RegisterRequest)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| username | String | 否 | - | 用户名 |
| password | String | 是 | 最少6字符 | 密码 |
| email | String | 否 | - | 邮箱 |
| phone | String | 是 | 非空 | 手机号 |

---

#### POST /api/auth/logout - 用户登出

| 项目 | 详情 |
|------|------|
| 方法 | POST |
| 路径 | `/api/auth/logout` |
| 权限 | permitAll |
| 请求体 | 无 |
| 响应类型 | `ApiResponse<Map<String, Boolean>>` |
| 状态码 | 200(成功), 500(服务器错误) |

---

#### GET /api/auth/userinfo - 获取当前用户信息

| 项目 | 详情 |
|------|------|
| 方法 | GET |
| 路径 | `/api/auth/userinfo` |
| 权限 | 需认证（JWT） |
| 请求体 | 无 |
| 响应类型 | `ApiResponse<UserDTO>` |
| 状态码 | 200(成功), 401(未认证), 500(服务器错误) |

---

#### PUT /api/auth/userinfo - 更新用户信息

| 项目 | 详情 |
|------|------|
| 方法 | PUT |
| 路径 | `/api/auth/userinfo` |
| 权限 | 需认证（JWT） |
| 请求体 | `UserDTO` |
| 响应类型 | `ApiResponse<UserDTO>` |
| 状态码 | 200(成功), 400(参数错误), 500(服务器错误) |

---

#### PUT /api/auth/password - 修改密码

| 项目 | 详情 |
|------|------|
| 方法 | PUT |
| 路径 | `/api/auth/password` |
| 权限 | 需认证（JWT） |
| 请求体 | `ChangePasswordRequest` |
| 响应类型 | `ApiResponse<Map<String, Object>>` |
| 状态码 | 200(成功), 400(参数错误), 401(未认证), 500(服务器错误) |

**请求参数 (ChangePasswordRequest)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| oldPassword | String | 是 | 非空 | 原密码 |
| newPassword | String | 是 | 最少6字符 | 新密码 |

---

#### POST /api/auth/sendVerifyCode - 发送验证码

| 项目 | 详情 |
|------|------|
| 方法 | POST |
| 路径 | `/api/auth/sendVerifyCode` |
| 权限 | permitAll |
| 请求体 | `SendVerifyCodeRequest` |
| 响应类型 | `ApiResponse<Map<String, Boolean>>` |
| 状态码 | 200(成功), 400(参数错误), 500(服务器错误) |

**请求参数 (SendVerifyCodeRequest)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| email | String | 是 | 非空 | 邮箱地址 |

---

#### POST /api/auth/resetPassword - 重置密码

| 项目 | 详情 |
|------|------|
| 方法 | POST |
| 路径 | `/api/auth/resetPassword` |
| 权限 | permitAll |
| 请求体 | `ResetPasswordRequest` |
| 响应类型 | `ApiResponse<Map<String, Boolean>>` |
| 状态码 | 200(成功), 400(参数错误), 500(服务器错误) |

**请求参数 (ResetPasswordRequest)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| email | String | 是 | 非空 | 邮箱地址 |
| verifyCode | String | 是 | 非空 | 验证码 |
| password | String | 是 | 最少6字符 | 新密码 |

---

#### POST /api/auth/merchant/register - 商家注册

| 项目 | 详情 |
|------|------|
| 方法 | POST |
| 路径 | `/api/auth/merchant/register` |
| 权限 | permitAll |
| 请求体 | `MerchantRegisterRequest` |
| 响应类型 | `ApiResponse<Map<String, String>>` |
| 状态码 | 201(创建成功), 400(参数错误), 500(服务器错误) |

**请求参数 (MerchantRegisterRequest)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| username | String | 否 | - | 用户名 |
| password | String | 是 | 最少6字符 | 密码 |
| email | String | 否 | - | 邮箱 |
| phone | String | 是 | 非空 | 手机号 |
| contact_person | String | 否 | - | 联系人 |
| name | String | 否 | - | 商家名称 |
| logo | String | 否 | - | 商家Logo |
| address | String | 否 | - | 地址 |

---

### 3.2 UserApiController - 用户端API

**基础路径**: `/api/user`
**控制器文件**: [UserApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/UserApiController.java)
**权限要求**: SecurityConfig中 `/api/user/**` 为 authenticated；控制器内部通过 `getCurrentUser()` 方法手动校验JWT获取当前用户

#### 个人资料

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/user/profile` | 获取个人资料 | 无 | `User` | 200, 401 |
| PUT | `/api/user/profile` | 更新个人资料 | `User` | `User` | 200, 401 |

#### 首页统计

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/home/stats` | 获取首页统计 | 无 | `ApiResponse<HomeStatsDTO>` | 200, 401 |
| GET | `/api/user/home/activities` | 获取最近活动 | `limit`(int, 默认10) | `ApiResponse<List<ActivityDTO>>` | 200, 401 |

#### 宠物管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/pets` | 获取宠物列表 | 无 | `ApiResponse<List<Pet>>` | 200, 401 |
| POST | `/api/user/pets` | 添加宠物 | `Pet`(body) | `Pet` | 201, 401 |
| GET | `/api/user/pets/{id}` | 获取宠物详情 | `id`(path) | `Pet` | 200, 401, 404 |
| PUT | `/api/user/pets/{id}` | 更新宠物 | `id`(path), `Pet`(body) | `Pet` | 200, 401, 404 |
| DELETE | `/api/user/pets/{id}` | 删除宠物 | `id`(path) | `Void` | 204, 401, 404 |

#### 预约管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/appointments` | 获取预约列表 | `status`(可选), `keyword`(可选), `startDate`(可选, ISO_DATE_TIME), `endDate`(可选, ISO_DATE_TIME), `page`(默认0), `pageSize`(默认10) | `ApiResponse<Map<String, Object>>` | 200, 401 |
| GET | `/api/user/appointments/{id}` | 获取预约详情 | `id`(path) | `AppointmentDTO` | 200, 401, 404 |
| POST | `/api/user/appointments` | 创建预约 | `CreateAppointmentRequest`(body) | `Map<String, Object>` | 201, 400, 401 |
| PUT | `/api/user/appointments/{id}/cancel` | 取消预约 | `id`(path) | `Void` | 200, 401, 404 |
| GET | `/api/user/appointments/stats` | 获取预约统计 | 无 | `Map<String, Long>` | 200, 401 |

**CreateAppointmentRequest 请求体**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| serviceId | Integer | 是 | 服务ID |
| petId | Integer | 是 | 宠物ID |
| appointmentTime | String | 是 | 预约时间（ISO_LOCAL_DATE_TIME格式） |
| remark | String | 否 | 备注 |

#### 订单管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/orders` | 获取订单列表 | `status`(可选), `keyword`(可选), `startDate`(可选), `endDate`(可选), `page`(默认1), `pageSize`(默认10) | `ApiResponse<PageResponse<OrderDTO>>` | 200, 401 |
| GET | `/api/user/orders/{id}` | 获取订单详情 | `id`(path) | `OrderDTO` | 200, 401 |
| POST | `/api/user/orders` | 创建订单 | `CreateOrderRequest`(body) | `CreateOrderResponse` | 201, 401 |
| POST | `/api/user/orders/preview` | 预览订单 | `PreviewOrderRequest`(body) | `OrderPreviewDTO` | 200, 401 |
| POST | `/api/user/orders/{id}/pay` | 支付订单 | `id`(path), `PayOrderRequest`(body) | `PayResponse` | 200, 401 |
| GET | `/api/user/orders/{id}/pay/status` | 获取支付状态 | `id`(path) | `PayStatusResponse` | 200, 401 |
| PUT | `/api/user/orders/{id}/cancel` | 取消订单 | `id`(path) | `Void` | 200, 401 |
| POST | `/api/user/orders/{id}/refund` | 申请退款 | `id`(path), `RefundOrderRequest`(body) | `Void` | 200, 401 |
| PUT | `/api/user/orders/{id}/confirm` | 确认收货 | `id`(path) | `Void` | 200, 401 |
| DELETE | `/api/user/orders/{id}` | 删除订单 | `id`(path) | `Void` | 204, 401 |
| PUT | `/api/user/orders/batch-cancel` | 批量取消 | `BatchOperationRequest`(body) | `Void` | 200, 401 |
| DELETE | `/api/user/orders/batch-delete` | 批量删除 | `BatchOperationRequest`(body) | `Void` | 204, 401 |

**CreateOrderRequest 请求体**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| items | List\<OrderItemRequest\> | 是 | 订单商品列表 |
| items[].productId | Integer | 是 | 商品ID |
| items[].quantity | Integer | 是 | 数量 |
| addressId | Integer | 是 | 收货地址ID |
| paymentMethod | String | 否 | 支付方式 |
| remark | String | 否 | 备注 |

#### 我的服务

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/services` | 获取已购买服务 | `status`(可选), `keyword`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<Map<String, Object>>` | 200, 401 |

#### 收货地址

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/addresses` | 获取地址列表 | 无 | `ApiResponse<List<AddressDTO>>` | 200, 401 |
| POST | `/api/user/addresses` | 添加地址 | `Address`(body) | `ApiResponse<AddressDTO>` | 201, 401 |
| PUT | `/api/user/addresses/{id}` | 更新地址 | `id`(path), `Address`(body) | `ApiResponse<AddressDTO>` | 200, 401, 404 |
| DELETE | `/api/user/addresses/{id}` | 删除地址 | `id`(path) | `ApiResponse<Void>` | 200, 401, 404 |
| PUT | `/api/user/addresses/{id}/default` | 设为默认地址 | `id`(path) | `ApiResponse<AddressDTO>` | 200, 401, 404 |

#### 评价管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/reviews` | 获取评价列表 | `rating`(可选), `page`(默认0), `size`(默认10) | `ApiResponse<Page<ReviewDTO>>` | 200, 401, 500 |
| GET | `/api/user/reviews/{id}` | 获取评价详情 | `id`(path) | `ApiResponse<Review>` | 200, 401, 403, 404 |
| POST | `/api/user/reviews` | 添加评价 | `Map<String, Object>`(body: appointmentId, rating, comment) | `ApiResponse<Review>` | 201, 400, 401, 404, 500 |
| PUT | `/api/user/reviews/{id}` | 更新评价 | `id`(path), `Map<String, Object>`(body: rating, comment) | `ApiResponse<Review>` | 200, 400, 401, 403, 404, 500 |
| DELETE | `/api/user/reviews/{id}` | 删除评价 | `id`(path) | `ApiResponse<Void>` | 200, 401, 403, 404, 500 |

**添加评价请求体**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| appointmentId | Integer | 是 | - | 预约ID |
| rating | Integer | 是 | 1-5 | 评分 |
| comment | String | 否 | - | 评价内容 |

#### 收藏管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/favorites` | 获取收藏商家列表 | 无 | `ApiResponse<List<FavoriteDTO>>` | 200, 401, 500 |
| POST | `/api/user/favorites` | 收藏商家 | `Map<String, Integer>`(body: merchantId) | `ApiResponse<FavoriteDTO>` | 201, 400, 401, 404, 409, 500 |
| DELETE | `/api/user/favorites/{id}` | 取消收藏商家 | `id`(path) | `ApiResponse<Void>` | 200, 401, 404, 500 |
| GET | `/api/user/favorites/services` | 获取收藏服务列表 | 无 | `ApiResponse<List<FavoriteServiceDTO>>` | 200, 401, 500 |
| POST | `/api/user/favorites/services` | 收藏服务 | `Map<String, Integer>`(body: serviceId) | `ApiResponse<FavoriteServiceDTO>` | 201, 400, 401, 404, 409, 500 |
| DELETE | `/api/user/favorites/services/{id}` | 取消收藏服务 | `id`(path) | `ApiResponse<Void>` | 200, 401, 404, 500 |
| POST | `/api/user/favorites/products` | 收藏商品 | `Map<String, Integer>`(body: productId) | `ApiResponse<FavoriteProductDTO>` | 201, 400, 401, 404, 409, 500 |
| DELETE | `/api/user/favorites/products/{id}` | 取消收藏商品 | `id`(path) | `ApiResponse<Void>` | 200, 401, 404, 500 |
| GET | `/api/user/favorites/products/{id}/check` | 检查商品收藏状态 | `id`(path) | `ApiResponse<Map<String, Boolean>>` | 200, 401, 500 |

#### 通知管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/notifications` | 获取通知列表 | `type`(可选), `isRead`(可选, Boolean) | `ApiResponse<List<NotificationDTO>>` | 200, 401, 500 |
| PUT | `/api/user/notifications/{id}/read` | 标记已读 | `id`(path) | `ApiResponse<Void>` | 200, 401, 403, 500 |
| PUT | `/api/user/notifications/read-all` | 全部标记已读 | 无 | `ApiResponse<Void>` | 200, 401, 500 |
| PUT | `/api/user/notifications/batch-read` | 批量标记已读 | `BatchOperationRequest`(body) | `ApiResponse<Void>` | 200, 401, 500 |
| DELETE | `/api/user/notifications/{id}` | 删除通知 | `id`(path) | `ApiResponse<Void>` | 200, 401, 403, 500 |
| DELETE | `/api/user/notifications/batch` | 批量删除通知 | `BatchOperationRequest`(body) | `ApiResponse<Void>` | 200, 401, 500 |
| GET | `/api/user/notifications/unread-count` | 获取未读数量 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |

---

### 3.3 MerchantApiController - 商家端API

**基础路径**: `/api/merchant`
**控制器文件**: [MerchantApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/MerchantApiController.java)
**权限要求**: ⚠️ SecurityConfig中 `/api/merchant/**` 为 permitAll，但控制器内部通过 `session.getAttribute("merchant")` 手动校验

#### 商家资料

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/merchant/profile` | 获取商家资料 | 无 | `ApiResponse<Merchant>` | 200, 401, 500 |
| GET | `/api/merchant/info` | 获取商家信息 | 无 | `ApiResponse<Merchant>` | 200, 401, 500 |
| PUT | `/api/merchant/profile` | 更新商家资料 | `Merchant`(body) | `ApiResponse<Merchant>` | 200, 401, 500 |
| PUT | `/api/merchant/info` | 更新商家信息 | `Merchant`(body) | `ApiResponse<Merchant>` | 200, 401, 500 |

#### 服务管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/services` | 获取服务列表 | 无 | `ApiResponse<List<Service>>` | 200, 401, 500 |
| POST | `/api/merchant/services` | 添加服务 | `Service`(body) | `ApiResponse<Service>` | 201, 400, 401, 500 |
| GET | `/api/merchant/services/{id}` | 获取服务详情 | `id`(path) | `ApiResponse<Service>` | 200, 401, 403, 404, 500 |
| PUT | `/api/merchant/services/{id}` | 更新服务 | `id`(path), `Service`(body) | `ApiResponse<Service>` | 200, 400, 401, 403, 404, 500 |
| DELETE | `/api/merchant/services/{id}` | 删除服务 | `id`(path) | `ApiResponse<Void>` | 200, 401, 403, 404, 500 |
| PUT | `/api/merchant/services/batch/status` | 批量更新服务状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 500 |
| DELETE | `/api/merchant/services/batch` | 批量删除服务 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 404, 500 |

**添加/更新服务请求体 (Service)**:

| 字段 | 类型 | 必填 | 验证规则 | 说明 |
|------|------|------|----------|------|
| name | String | 是 | 非空 | 服务名称 |
| price | BigDecimal | 是 | >=0 | 价格 |
| duration | Integer | 是 | >0 | 时长（分钟） |
| description | String | 否 | - | 描述 |
| image | String | 否 | - | 图片 |
| status | String | 否 | - | 状态 |

#### 预约管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/appointments` | 获取预约列表 | 无 | `ApiResponse<List<Appointment>>` | 200, 401, 500 |
| PUT | `/api/merchant/appointments/{id}/status` | 更新预约状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Appointment>` | 200, 400, 401, 403, 404, 500 |
| GET | `/api/merchant/appointments/recent` | 获取最近预约 | `limit`(默认5) | `ApiResponse<List<Appointment>>` | 200, 401, 500 |
| GET | `/api/merchant/appointment-stats` | 获取预约统计 | `startDate`(可选), `endDate`(可选) | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |
| GET | `/api/merchant/appointment-stats/export` | 导出预约统计 | `startDate`(可选), `endDate`(可选), `format`(默认excel) | `byte[]` | 200, 401, 500 |

**预约状态流转规则**:
- pending → confirmed（待处理 → 已确认）
- pending → cancelled（待处理 → 已取消）
- confirmed → completed（已确认 → 已完成）
- confirmed → cancelled（已确认 → 已取消）
- completed/cancelled 不可再变更

#### 订单管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/orders` | 获取订单列表 | 无 | `ApiResponse<List<ProductOrder>>` | 200, 401, 500 |
| PUT | `/api/merchant/orders/{id}/status` | 更新订单状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<ProductOrder>` | 200, 400, 401, 403, 404, 500 |
| GET | `/api/merchant/product-orders` | 获取商品订单 | `status`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<ProductOrder>>` | 200, 401, 500 |
| PUT | `/api/merchant/product-orders/{id}/status` | 更新商品订单状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<ProductOrder>` | 200, 400, 401, 403, 404, 500 |
| PUT | `/api/merchant/product-orders/{id}/logistics` | 更新物流信息 | `id`(path), `Map<String, String>`(body: logisticsCompany, trackingNumber) | `ApiResponse<ProductOrder>` | 200, 400, 401, 403, 404, 500 |

**订单状态值**: pending, paid, shipped, completed, cancelled

#### 商品管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/products` | 获取商品列表 | 无 | `ApiResponse<List<Product>>` | 200, 401 |
| POST | `/api/merchant/products` | 添加商品 | `Product`(body) | `ApiResponse<Product>` | 201, 401 |
| GET | `/api/merchant/products/{id}` | 获取商品详情 | `id`(path) | `ApiResponse<Product>` | 200, 401, 404 |
| PUT | `/api/merchant/products/{id}` | 更新商品 | `id`(path), `Product`(body) | `ApiResponse<Product>` | 200, 401, 404 |
| DELETE | `/api/merchant/products/{id}` | 删除商品 | `id`(path) | `ApiResponse<Void>` | 200, 401, 404 |
| GET | `/api/merchant/products/paged` | 分页获取商品 | `page`(默认0), `pageSize`(默认10), `sortBy`(默认createdAt), `sortDir`(默认desc), `status`(可选), `name`(可选), `category`(可选), `minPrice`(可选), `maxPrice`(可选), `stockStatus`(可选) | `ApiResponse<Map<String, Object>>` | 200, 401 |
| PUT | `/api/merchant/products/{id}/status` | 更新商品状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Product>` | 200, 400, 401, 404 |
| PUT | `/api/merchant/products/batch/status` | 批量更新商品状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 404 |
| DELETE | `/api/merchant/products/batch` | 批量删除商品 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 404 |

#### 分类管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/categories` | 获取分类列表 | 无 | `ApiResponse<List<Category>>` | 200, 401, 500 |
| POST | `/api/merchant/categories` | 添加分类 | `Category`(body) | `ApiResponse<Category>` | 201, 400, 401, 500 |
| PUT | `/api/merchant/categories/{id}` | 更新分类 | `id`(path), `Category`(body) | `ApiResponse<Category>` | 200, 400, 401, 403, 404, 500 |
| DELETE | `/api/merchant/categories/{id}` | 删除分类 | `id`(path) | `ApiResponse<Void>` | 200, 400, 401, 403, 404, 500 |
| PUT | `/api/merchant/categories/{id}/status` | 更新分类状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Category>` | 200, 400, 401, 403, 404, 500 |
| PUT | `/api/merchant/categories/batch/status` | 批量更新分类状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 403, 404, 500 |
| DELETE | `/api/merchant/categories/batch` | 批量删除分类 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 403, 404, 500 |

#### 评价管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/reviews` | 获取评价列表（分页） | `page`(默认0), `size`(默认10), `sortBy`(默认createdAt), `sortDir`(默认desc), `rating`(可选), `keyword`(可选) | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |
| GET | `/api/merchant/reviews/statistics` | 获取评价统计 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |
| GET | `/api/merchant/reviews/{id}` | 获取评价详情 | `id`(path) | `ApiResponse<Review>` | 200, 401, 403, 404, 500 |
| PUT | `/api/merchant/reviews/{id}/reply` | 回复评价 | `id`(path), `Map<String, String>`(body: reply) | `ApiResponse<Review>` | 200, 400, 401, 403, 404, 500 |
| DELETE | `/api/merchant/reviews/{id}` | 删除评价 | `id`(path) | `ApiResponse<Void>` | 200, 401, 403, 404, 500 |
| GET | `/api/merchant/reviews/recent` | 获取最近评价 | `limit`(默认5) | `ApiResponse<List<Review>>` | 200, 401, 500 |

#### 账号管理

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| POST | `/api/merchant/change-password` | 修改密码 | `Map<String, String>`(body: oldPassword, newPassword) | `ApiResponse<Void>` | 200, 400, 401, 500 |
| POST | `/api/merchant/bind-phone` | 绑定手机号 | `Map<String, String>`(body: phone, verifyCode) | `ApiResponse<Void>` | 200, 400, 401, 500 |
| POST | `/api/merchant/bind-email` | 绑定邮箱 | `Map<String, String>`(body: email, verifyCode) | `ApiResponse<Void>` | 200, 400, 401, 500 |
| POST | `/api/merchant/send-verify-code` | 发送验证码 | `Map<String, String>`(body: target, type) | `ApiResponse<Void>` | 200, 400, 401, 500 |

#### 统计分析

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchant/dashboard` | 获取首页统计 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |
| GET | `/api/merchant/revenue-stats` | 获取营收统计 | `startDate`(可选), `endDate`(可选) | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |
| GET | `/api/merchant/revenue-stats/export` | 导出营收统计 | `startDate`(可选), `endDate`(可选) | `ApiResponse<List<Map<String, Object>>>` | 200, 401, 500 |

#### 店铺设置

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/merchant/settings` | 获取店铺设置 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |
| PUT | `/api/merchant/settings` | 更新店铺设置 | `MerchantSettings`(body) | `ApiResponse<MerchantSettings>` | 200, 401, 500 |
| POST | `/api/merchant/settings/toggle-status` | 切换营业状态 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401, 500 |

---

### 3.4 AdminApiController - 平台端API

**基础路径**: `/api/admin`
**控制器文件**: [AdminApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/AdminApiController.java)
**权限要求**: ⚠️ SecurityConfig中无 `/api/admin/**` 的明确规则（被 anyRequest().permitAll() 覆盖），控制器内部通过 `session.getAttribute("admin")` 手动校验

#### 用户管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/users` | 获取用户列表 | 无 | `List<User>` | 200, 401 |
| DELETE | `/api/admin/users/{id}` | 删除用户 | `id`(path) | `Void` | 204, 401 |
| PUT | `/api/admin/users/{id}/status` | 更新用户状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<User>` | 200, 400, 401, 404 |
| PUT | `/api/admin/users/batch/status` | 批量更新用户状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| DELETE | `/api/admin/users/batch` | 批量删除用户 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| GET | `/api/admin/users/recent` | 获取最近注册用户 | `page`(默认0), `pageSize`(默认10) | `ApiResponse<PageResponse<User>>` | 200, 401 |

**用户状态值**: active, disabled

#### 商家管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/merchants` | 获取商家列表 | 无 | `List<Merchant>` | 200, 401 |
| GET | `/api/admin/merchants/{id}` | 获取商家详情 | `id`(path) | `ApiResponse<MerchantDetailDTO>` | 200, 401, 404 |
| PUT | `/api/admin/merchants/{id}/status` | 更新商家状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Merchant>` | 200, 400, 401, 404 |
| DELETE | `/api/admin/merchants/{id}` | 删除商家 | `id`(path) | `Void` | 204, 401 |
| PUT | `/api/admin/merchants/batch/status` | 批量更新商家状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| DELETE | `/api/admin/merchants/batch` | 批量删除商家 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| GET | `/api/admin/merchants/pending` | 获取待审核商家 | `page`(默认0), `pageSize`(默认10), `keyword`(可选) | `ApiResponse<PageResponse<Merchant>>` | 200, 401 |
| PUT | `/api/admin/merchants/{id}/audit` | 审核商家 | `id`(path), `Map<String, String>`(body: status, reason) | `ApiResponse<Merchant>` | 200, 400, 401, 404 |

**商家审核状态值**: approved, rejected

#### 仪表盘

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/dashboard` | 获取仪表盘统计 | 无 | `ApiResponse<DashboardStatsDTO>` | 200, 401 |
| GET | `/api/admin/dashboard/pending-merchants` | 获取待审核商家（仪表盘） | `page`(默认0), `pageSize`(默认10) | `ApiResponse<PageResponse<Merchant>>` | 200, 401 |

**DashboardStatsDTO**:

| 字段 | 类型 | 说明 |
|------|------|------|
| userCount | long | 总用户数 |
| merchantCount | long | 总商家数 |
| orderCount | long | 总订单数 |
| serviceCount | long | 总服务数 |

#### 公告管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/announcements` | 获取公告列表 | `page`(默认0), `pageSize`(默认10), `status`(可选) | `ApiResponse<PageResponse<Announcement>>` | 200, 401 |
| POST | `/api/admin/announcements` | 创建公告 | `Announcement`(body) | `ApiResponse<Announcement>` | 200, 401 |
| PUT | `/api/admin/announcements/{id}` | 更新公告 | `id`(path), `Announcement`(body) | `ApiResponse<Announcement>` | 200, 401, 404 |
| DELETE | `/api/admin/announcements/{id}` | 删除公告 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/announcements/{id}/publish` | 发布公告 | `id`(path) | `ApiResponse<Announcement>` | 200, 401, 404 |
| PUT | `/api/admin/announcements/{id}/unpublish` | 下架公告 | `id`(path) | `ApiResponse<Announcement>` | 200, 401, 404 |
| PUT | `/api/admin/announcements/batch/publish` | 批量发布公告 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| PUT | `/api/admin/announcements/batch/unpublish` | 批量下架公告 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| DELETE | `/api/admin/announcements/batch` | 批量删除公告 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |

#### 商品管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/products` | 获取商品列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `status`(可选), `merchantId`(可选), `category`(可选) | `ApiResponse<PageResponse<Product>>` | 200, 401 |
| GET | `/api/admin/products/{id}` | 获取商品详情 | `id`(path) | `ApiResponse<Product>` | 200, 401, 404 |
| PUT | `/api/admin/products/{id}` | 更新商品 | `id`(path), `Product`(body) | `ApiResponse<Product>` | 200, 401, 404 |
| PUT | `/api/admin/products/{id}/status` | 更新商品状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Product>` | 200, 400, 401, 404 |
| DELETE | `/api/admin/products/{id}` | 删除商品 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/products/batch/status` | 批量更新商品状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| DELETE | `/api/admin/products/batch` | 批量删除商品 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |

**商品状态值**: enabled, disabled

#### 服务管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/services` | 获取服务列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `status`(可选), `merchantId`(可选) | `ApiResponse<PageResponse<Service>>` | 200, 401 |
| PUT | `/api/admin/services/{id}/status` | 更新服务状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Service>` | 200, 400, 401, 404 |
| DELETE | `/api/admin/services/{id}` | 删除服务 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/services/batch/status` | 批量更新服务状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| DELETE | `/api/admin/services/batch` | 批量删除服务 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |

#### 评价管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/reviews` | 获取评价列表 | `page`(默认0), `pageSize`(默认10), `rating`(可选), `keyword`(可选), `merchantId`(可选), `status`(可选) | `ApiResponse<PageResponse<Review>>` | 200, 401 |
| GET | `/api/admin/reviews/pending` | 获取待审核评价 | `page`(默认0), `pageSize`(默认10), `keyword`(可选) | `ApiResponse<PageResponse<Review>>` | 200, 401 |
| PUT | `/api/admin/reviews/{id}/audit` | 审核评价 | `id`(path), `Map<String, String>`(body: status, reason) | `ApiResponse<Review>` | 200, 400, 401, 404 |
| PUT | `/api/admin/reviews/{id}/approve` | 批准评价 | `id`(path) | `ApiResponse<Review>` | 200, 401, 404 |
| PUT | `/api/admin/reviews/{id}/violation` | 标记评价违规 | `id`(path) | `ApiResponse<Review>` | 200, 401, 404 |
| DELETE | `/api/admin/reviews/{id}` | 删除评价 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/reviews/batch/status` | 批量更新评价状态 | `Map<String, Object>`(body: ids, status) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |
| DELETE | `/api/admin/reviews/batch` | 批量删除评价 | `Map<String, Object>`(body: ids) | `ApiResponse<Map<String, Integer>>` | 200, 400, 401 |

**评价审核状态值**: approved, rejected

#### 系统设置

| 方法 | 路径 | 说明 | 请求体 | 响应类型 | 状态码 |
|------|------|------|--------|----------|--------|
| GET | `/api/admin/system/config` | 获取系统配置 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401 |
| PUT | `/api/admin/system/config` | 更新系统配置 | `Map<String, Object>`(body) | `ApiResponse<Map<String, Object>>` | 200, 401 |
| GET | `/api/admin/system/settings` | 获取系统设置 | 无 | `ApiResponse<SystemSettings>` | 200, 401 |
| PUT | `/api/admin/system/settings` | 更新系统设置 | `SystemSettings`(body) | `ApiResponse<SystemSettings>` | 200, 401 |
| GET | `/api/admin/system/settings/email` | 获取邮件设置 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401 |
| GET | `/api/admin/system/settings/sms` | 获取短信设置 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401 |
| GET | `/api/admin/system/settings/payment` | 获取支付设置 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401 |
| GET | `/api/admin/system/settings/upload` | 获取上传设置 | 无 | `ApiResponse<Map<String, Object>>` | 200, 401 |

**系统配置字段**: siteName, logo, contactEmail, contactPhone, icpNumber, siteDescription, siteKeywords, footerText

#### 活动管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/activities` | 获取活动列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `type`(可选), `status`(可选), `startDate`(可选), `endDate`(可选) | `ApiResponse<PageResponse<Activity>>` | 200, 401 |
| POST | `/api/admin/activities` | 创建活动 | `Activity`(body) | `ApiResponse<Activity>` | 200, 400, 401 |
| PUT | `/api/admin/activities/{id}` | 更新活动 | `id`(path), `Activity`(body) | `ApiResponse<Activity>` | 200, 400, 401, 404 |
| DELETE | `/api/admin/activities/{id}` | 删除活动 | `id`(path) | `Void` | 204, 401, 404 |
| PUT | `/api/admin/activities/{id}/status` | 更新活动状态 | `id`(path), `Map<String, String>`(body: status) | `ApiResponse<Activity>` | 200, 400, 401, 404 |

#### 店铺审核

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/shops/pending` | 获取待审核店铺 | `page`(默认0), `pageSize`(默认10), `keyword`(可选) | `ApiResponse<PageResponse<Merchant>>` | 200, 401 |
| PUT | `/api/admin/shops/{id}/audit` | 审核店铺 | `id`(path), `Map<String, String>`(body: status, reason) | `ApiResponse<Merchant>` | 200, 400, 401, 404 |

#### 任务管理

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/admin/tasks` | 获取任务列表 | `page`(默认0), `pageSize`(默认10), `keyword`(可选), `type`(可选), `status`(可选) | `ApiResponse<PageResponse<ScheduledTask>>` | 200, 401 |
| POST | `/api/admin/tasks` | 创建任务 | `ScheduledTask`(body) | `ApiResponse<ScheduledTask>` | 200, 400, 401 |
| PUT | `/api/admin/tasks/{id}` | 更新任务 | `id`(path), `ScheduledTask`(body) | `ApiResponse<ScheduledTask>` | 200, 400, 401, 404 |
| DELETE | `/api/admin/tasks/{id}` | 删除任务 | `id`(path) | `Void` | 204, 401, 404 |
| POST | `/api/admin/tasks/{id}/execute` | 执行任务 | `id`(path) | `ApiResponse<Map<String, Object>>` | 200, 400, 401, 404 |

---

### 3.5 PublicApiController - 公共API

**基础路径**: `/api/public`
**控制器文件**: [PublicApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/PublicApiController.java)
**权限要求**: permitAll

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/public/services` | 获取所有服务 | 无 | `List<Service>` | 200 |
| GET | `/api/public/services/{id}` | 获取服务详情 | `id`(path) | `Service` | 200, 404 |
| GET | `/api/public/merchants` | 获取所有商家（仅approved） | 无 | `List<Merchant>` | 200 |
| GET | `/api/public/merchants/{id}` | 获取商家详情（仅approved） | `id`(path) | `Merchant` | 200, 404 |
| GET | `/api/public/merchants/{id}/services` | 获取商家服务 | `id`(path) | `List<Service>` | 200, 404 |

---

### 3.6 AnnouncementApiController - 公告API

**基础路径**: `/api/announcements`
**控制器文件**: [AnnouncementApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/AnnouncementApiController.java)
**权限要求**: permitAll（被 anyRequest().permitAll() 覆盖）

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/announcements` | 获取公告列表（已发布） | 无 | `List<AnnouncementDTO>` | 200 |
| GET | `/api/announcements/{id}` | 获取公告详情 | `id`(path) | `AnnouncementDTO` | 200, 404 |

---

### 3.7 SearchApiController - 搜索API

**基础路径**: `/api`
**控制器文件**: [SearchApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/SearchApiController.java)
**权限要求**: `/api/search/**` permitAll；`/api/user/search-history` 需认证（session校验）

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 权限 | 状态码 |
|------|------|------|------|----------|------|--------|
| GET | `/api/search/suggestions` | 获取搜索建议 | `keyword`(必填) | `SearchSuggestionsDTO` | permitAll | 200 |
| GET | `/api/search/hot-keywords` | 获取热门搜索 | `limit`(默认10) | `List<HotKeywordDTO>` | permitAll | 200 |
| POST | `/api/user/search-history` | 保存搜索历史 | `Map<String, String>`(body: keyword) | `Void` | 需认证(session) | 200, 401 |
| GET | `/api/user/search-history` | 获取搜索历史 | `limit`(默认10) | `List<String>` | 需认证(session) | 200, 401 |
| DELETE | `/api/user/search-history` | 清空搜索历史 | 无 | `Void` | 需认证(session) | 204, 401 |

---

### 3.8 ServiceController - 服务API

**基础路径**: `/api/services`
**控制器文件**: [ServiceController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/ServiceController.java)
**权限要求**: permitAll

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/services` | 获取服务列表 | `name`(可选), `minPrice`(可选), `maxPrice`(可选), `minDuration`(可选), `maxDuration`(可选), `status`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Service>>` | 200 |
| GET | `/api/services/{id}` | 获取服务详情 | `id`(path) | `ApiResponse<Service>` | 200, 404 |
| GET | `/api/services/search` | 搜索服务 | `keyword`(可选), `merchantId`(可选), `sortBy`(可选), `sortOrder`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Service>>` | 200 |
| GET | `/api/services/recommended` | 获取推荐服务 | `limit`(可选, 默认10) | `ApiResponse<List<Service>>` | 200 |

---

### 3.9 ProductController - 商品API

**基础路径**: `/api/products`
**控制器文件**: [ProductController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/ProductController.java)
**权限要求**: permitAll

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/products` | 获取商品列表 | `name`(可选), `minPrice`(可选), `maxPrice`(可选), `minStock`(可选), `status`(可选), `categoryId`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Product>>` | 200 |
| GET | `/api/products/{id}` | 获取商品详情 | `id`(path) | `ApiResponse<Product>` | 200, 404 |
| GET | `/api/products/search` | 搜索商品 | `keyword`(可选), `merchantId`(可选), `sortBy`(可选), `sortOrder`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Product>>` | 200 |
| GET | `/api/products/{id}/reviews` | 获取商品评价 | `id`(path), `rating`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Review>>` | 200 |

---

### 3.10 MerchantController (api) - 商家公共查询API

**基础路径**: `/api`
**控制器文件**: [MerchantController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/MerchantController.java)
**权限要求**: permitAll

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/merchants` | 获取商家列表 | `name`(可选), `status`(可选), `minRating`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Merchant>>` | 200 |
| GET | `/api/merchants/search` | 搜索商家 | `keyword`(可选), `sortBy`(可选), `sortOrder`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Merchant>>` | 200 |
| GET | `/api/merchant/{id}` | 获取商家详情 | `id`(path) | `ApiResponse<Merchant>` | 200, 404 |
| GET | `/api/merchant/{id}/services` | 获取商家服务 | `id`(path), `status`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Service>>` | 200 |
| GET | `/api/merchant/{id}/products` | 获取商家商品 | `id`(path), `status`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Product>>` | 200 |
| GET | `/api/merchant/{id}/reviews` | 获取商家评价 | `id`(path), `rating`(可选), `page`(默认0), `pageSize`(默认10) | `ApiResponse<List<Review>>` | 200 |
| GET | `/api/merchant/{id}/available-slots` | 获取可用预约时段 | `id`(path), `date`(必填) | `ApiResponse<Map<String, List<String>>>` | 200 |

---

### 3.11 CartController - 购物车API

**基础路径**: `/api/user/cart`
**控制器文件**: [CartController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/CartController.java)
**权限要求**: 需认证（session校验 `session.getAttribute("user")`），SecurityConfig中 `/api/user/**` 为 authenticated

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| GET | `/api/user/cart` | 获取购物车 | 无 | `ApiResponse<List<Cart>>` | 200, 401 |
| POST | `/api/user/cart` | 添加到购物车 | `Map<String, Object>`(body: productId, quantity) | `ApiResponse<Cart>` | 201, 400, 401, 404 |
| PUT | `/api/user/cart` | 更新购物车 | `Map<String, Object>`(body: cartId, quantity) | `ApiResponse<Cart>` | 200, 400, 401 |
| DELETE | `/api/user/cart/{id}` | 移除购物车项 | `id`(path) | `ApiResponse<Void>` | 200, 401 |
| DELETE | `/api/user/cart/batch` | 批量移除 | `Map<String, List<Integer>>`(body: ids) | `ApiResponse<Void>` | 200, 400, 401 |

---

### 3.12 RedisLockController - Redis锁测试API

**基础路径**: `/api/redis-lock`
**控制器文件**: [RedisLockController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/RedisLockController.java)
**权限要求**: permitAll（被 anyRequest().permitAll() 覆盖）

| 方法 | 路径 | 说明 | 参数 | 响应类型 | 状态码 |
|------|------|------|------|----------|--------|
| POST | `/api/redis-lock/reduce-stock` | 测试扣减库存 | `productId`(Long, 必填), `quantity`(int, 必填) | `ApiResponse<Boolean>` | 200, 500 |

---

## 4. MVC控制器（页面路由）

这些控制器返回页面重定向，不提供REST API数据。

### HomeController

**基础路径**: `/`
**控制器文件**: [HomeController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/HomeController.java)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 重定向到 /index.html |
| GET | `/login` | 重定向到 /login.html |
| GET | `/register` | 重定向到 /register.html |
| GET | `/admin/login` | 重定向到 /admin-login.html |

### AuthController (MVC)

**基础路径**: `/`
**控制器文件**: [AuthController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/AuthController.java)

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/register/user` | 用户注册 | username, email, password, confirmPassword, phone |
| POST | `/register/merchant` | 商家注册 | name, contactPerson, phone, email, password, confirmPassword, address |
| POST | `/login` | 登录 | email, password, type(user/merchant/admin) |
| GET | `/logout` | 退出登录 | 无 |

### UserController (MVC)

**基础路径**: `/user`
**控制器文件**: [UserController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/UserController.java)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/dashboard` | 重定向到 /user-dashboard.html |
| GET | `/user/pets` | 重定向到 /user-pets.html |
| GET | `/user/pets/add` | 重定向到 /user-add-pet.html |
| POST | `/user/pets/add` | 添加宠物 |
| GET | `/user/appointments` | 重定向到 /user-appointments.html |
| GET | `/user/appointments/book` | 重定向到 /user-book-appointment.html |
| POST | `/user/appointments/book` | 创建预约 |
| GET | `/user/profile` | 重定向到 /user-profile.html |
| POST | `/user/profile/update` | 更新个人资料 |

### MerchantController (MVC)

**基础路径**: `/merchant`
**控制器文件**: [MerchantController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/MerchantController.java)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/merchant/dashboard` | 重定向到 /merchant-dashboard.html |
| GET | `/merchant/services` | 重定向到 /merchant-services.html |
| GET | `/merchant/services/add` | 重定向到 /merchant-add-service.html |
| POST | `/merchant/services/add` | 添加服务 |
| GET | `/merchant/appointments` | 重定向到 /merchant-appointments.html |
| POST | `/merchant/appointments/{id}/status` | 更新预约状态 |
| GET | `/merchant/profile` | 重定向到 /merchant-profile.html |
| POST | `/merchant/profile/update` | 更新商家资料 |

### AdminController (MVC)

**基础路径**: `/admin`
**控制器文件**: [AdminController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/AdminController.java)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/dashboard` | 重定向到 /admin-dashboard.html |
| GET | `/admin/users` | 重定向到 /admin-users.html |
| GET | `/admin/merchants` | 重定向到 /admin-merchants.html |
| POST | `/admin/merchants/{id}/status` | 更新商家状态 |
| POST | `/admin/users/{id}/delete` | 删除用户 |
| POST | `/admin/merchants/{id}/delete` | 删除商家 |

---

## 5. DTO数据结构定义

### 请求DTO

| DTO类 | 字段 | 用途 |
|-------|------|------|
| **LoginRequest** | username(String), phone(String), password(String) | 用户登录 |
| **RegisterRequest** | username(String), password(String), email(String), phone(String) | 用户注册 |
| **MerchantRegisterRequest** | username(String), password(String), email(String), phone(String), contact_person(String), name(String), logo(String), address(String) | 商家注册 |
| **ChangePasswordRequest** | oldPassword(String), newPassword(String) | 修改密码 |
| **SendVerifyCodeRequest** | email(String) | 发送验证码 |
| **ResetPasswordRequest** | email(String), verifyCode(String), password(String) | 重置密码 |
| **CreateAppointmentRequest** | serviceId(Integer), petId(Integer), appointmentTime(String), remark(String) | 创建预约 |
| **CreateOrderRequest** | items(List\<OrderItemRequest\>), addressId(Integer), paymentMethod(String), remark(String) | 创建订单 |
| **CreateOrderRequest.OrderItemRequest** | productId(Integer), quantity(Integer) | 订单商品项 |
| **PreviewOrderRequest** | - | 预览订单 |
| **PayOrderRequest** | payMethod(String) | 支付订单 |
| **RefundOrderRequest** | reason(String) | 申请退款 |
| **BatchOperationRequest** | ids(List\<Integer\>) | 批量操作 |
| **RoleRequest** | - | 角色请求 |

### 响应DTO

| DTO类 | 字段 | 用途 |
|-------|------|------|
| **ApiResponse\<T\>** | code(int), message(String), data(T) | 通用响应包装 |
| **PageResponse\<T\>** | data(List\<T\>), total(Long), page(Integer), pageSize(Integer), totalPages(Integer) | 分页响应 |
| **LoginResponse** | token(String), user(UserDTO) | 登录响应 |
| **UserDTO** | - | 用户信息 |
| **MerchantDTO** | - | 商家信息 |
| **MerchantDetailDTO** | - | 商家详情（含服务、商品、订单、评价） |
| **ServiceDTO** | - | 服务信息 |
| **ProductDTO** | - | 商品信息 |
| **ProductOrderDTO** | - | 商品订单信息 |
| **OrderDTO** | - | 订单信息 |
| **OrderItemDTO** | - | 订单项信息 |
| **OrderPreviewDTO** | - | 订单预览 |
| **OrderPreviewItemDTO** | - | 订单预览项 |
| **OrderAddressDTO** | - | 订单地址 |
| **OrderTimelineItemDTO** | - | 订单时间线 |
| **CreateOrderResponse** | - | 创建订单响应 |
| **PayResponse** | - | 支付响应 |
| **PayStatusResponse** | - | 支付状态响应 |
| **AppointmentDTO** | - | 预约信息 |
| **PetDTO** | - | 宠物信息 |
| **AddressDTO** | - | 地址信息 |
| **ReviewDTO** | - | 评价信息 |
| **FavoriteDTO** | - | 收藏商家信息 |
| **FavoriteServiceDTO** | - | 收藏服务信息 |
| **FavoriteProductDTO** | - | 收藏商品信息 |
| **NotificationDTO** | - | 通知信息 |
| **AnnouncementDTO** | - | 公告信息 |
| **CartDTO** | - | 购物车信息 |
| **HomeStatsDTO** | - | 用户首页统计 |
| **DashboardStatsDTO** | userCount(long), merchantCount(long), orderCount(long), serviceCount(long) | 管理端仪表盘统计 |
| **ActivityDTO** | - | 活动信息 |
| **UserPurchasedServiceDTO** | - | 用户已购买服务 |
| **UserDetailDTO** | - | 用户详情 |
| **SearchSuggestionsDTO** | - | 搜索建议 |
| **HotKeywordDTO** | - | 热门关键词 |

---

## 6. 问题与风险标记

### 🔴 严重问题

#### 6.1 商家端API完全公开访问

- **路径**: `/api/merchant/**`
- **SecurityConfig配置**: `permitAll`
- **实际控制**: 控制器内部通过 `session.getAttribute("merchant")` 手动校验
- **风险**: 由于使用JWT（STATELESS会话策略），session中不会存储merchant属性，导致所有商家端API的实际认证可能失效。任何未认证用户都可以直接访问商家端的所有接口。
- **建议**: 将 `/api/merchant/**` 改为 `authenticated`，并使用JWT认证而非session校验

#### 6.2 管理端API无SecurityConfig保护

- **路径**: `/api/admin/**`
- **SecurityConfig配置**: 无明确规则，被 `anyRequest().permitAll()` 覆盖
- **实际控制**: 控制器内部通过 `session.getAttribute("admin")` 手动校验
- **风险**: 与商家端相同，STATELESS会话策略下session属性不可用，管理端API可能完全暴露
- **建议**: 添加 `/api/admin/**` 的 `authenticated` 规则，使用JWT + 角色校验

#### 6.3 认证路径配置不匹配

- **配置的认证路径**: `/api/merchant-api/**` 和 `/api/admin-api/**` 设置为 `authenticated`
- **实际控制器路径**: `/api/merchant/**` 和 `/api/admin/**`
- **风险**: 认证规则配置在了不存在的路径上，实际需要保护的路径反而完全公开
- **建议**: 修正SecurityConfig中的路径匹配规则

### 🟡 中等问题

#### 6.4 认证方式不一致

- **UserApiController**: 使用JWT（SecurityContextHolder获取认证信息）
- **MerchantApiController**: 使用Session（session.getAttribute("merchant")）
- **AdminApiController**: 使用Session（session.getAttribute("admin")）
- **CartController**: 使用Session（session.getAttribute("user")）
- **SearchApiController**: 使用Session（session.getAttribute("user")）
- **风险**: 同一项目中混用JWT和Session认证，在STATELESS策略下Session方式可能不可靠
- **建议**: 统一使用JWT认证方式

#### 6.5 路径命名不一致

- **商家公共查询**: `/api/merchants`（列表）vs `/api/merchant/{id}`（详情）—— 复数/单数混用
- **PublicApiController**: `/api/public/merchants/{id}/services`
- **MerchantController(api)**: `/api/merchant/{id}/services`
- **风险**: 相同功能存在多个路径入口，容易造成混淆
- **建议**: 统一路径命名规范

#### 6.6 响应格式不统一

- 部分接口返回 `ApiResponse<T>` 包装
- 部分接口直接返回实体（如 `User`, `List<Merchant>`）
- 部分接口返回 `Void`（无响应体）
- 部分接口返回 `Map<String, Object>`
- **风险**: 前端需要处理多种响应格式
- **建议**: 统一使用 `ApiResponse<T>` 包装

#### 6.7 请求体使用Map而非DTO

- 多个接口使用 `Map<String, Object>` 或 `Map<String, String>` 作为请求体
- 涉及接口：状态更新、批量操作、收藏、购物车等
- **风险**: 缺乏类型安全和参数验证
- **建议**: 为每个接口创建专用的请求DTO

#### 6.8 分页参数不统一

- 部分接口 `page` 从0开始（如 AdminApiController）
- 部分接口 `page` 从1开始（如 UserApiController的订单列表）
- 部分接口使用 `page` + `pageSize`
- 部分接口使用 `page` + `size`
- **风险**: 前端分页逻辑容易出错
- **建议**: 统一分页参数命名和起始值

### 🟢 轻微问题

#### 6.9 控制器类名冲突

- 存在两个 `MerchantController`：
  - `com.petshop.controller.MerchantController`（MVC控制器）
  - `com.petshop.controller.api.MerchantController`（REST控制器）
- 通过 `@Controller("mvcMerchantController")` 区分，但容易混淆

#### 6.10 Swagger注解不完整

- `AuthApiController` 和 `AdminApiController` 有完整的Swagger注解
- `UserApiController` 和 `MerchantApiController` 缺少Swagger注解
- **建议**: 为所有API控制器补充Swagger注解

#### 6.11 商品评价接口未实现

- `ProductController.getProductReviews()` 直接返回空列表
- **建议**: 实现商品评价查询功能

#### 6.12 预约统计导出功能未实现

- `MerchantApiController.exportAppointmentStats()` 返回空字节数组
- **建议**: 实现导出功能或移除接口

#### 6.13 可用预约时段接口返回硬编码数据

- `MerchantController.getAvailableSlots()` 返回固定的时段数据
- **建议**: 根据商家营业时间和已预约情况动态计算

#### 6.14 商家端搜索历史接口路径归属不清

- `SearchApiController` 中 `/api/user/search-history` 路径在 `/api` 基础路径下
- 但功能属于用户端，路径应归入 UserApiController
- **建议**: 将搜索历史接口移至 UserApiController

---

## API统计汇总

| 控制器 | 基础路径 | API数量 | 权限方式 |
|--------|----------|---------|----------|
| AuthApiController | /api/auth | 8 | permitAll + JWT |
| UserApiController | /api/user | 37 | JWT (SecurityConfig authenticated) |
| MerchantApiController | /api/merchant | 38 | Session (⚠️ SecurityConfig permitAll) |
| AdminApiController | /api/admin | 42 | Session (⚠️ SecurityConfig permitAll) |
| PublicApiController | /api/public | 5 | permitAll |
| AnnouncementApiController | /api/announcements | 2 | permitAll |
| SearchApiController | /api | 5 | 混合 |
| ServiceController | /api/services | 4 | permitAll |
| ProductController | /api/products | 4 | permitAll |
| MerchantController (api) | /api | 7 | permitAll |
| CartController | /api/user/cart | 5 | Session (SecurityConfig authenticated) |
| RedisLockController | /api/redis-lock | 1 | permitAll |
| **总计** | - | **158** | - |

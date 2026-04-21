# 前端API调用档案文档

> 生成时间：2026-04-21
> 项目路径：petshop-vue/src
> 基础配置：baseURL = `VITE_API_BASE_URL || '/api'`，timeout = 10000ms

---

## 目录

- [1. 基础请求配置](#1-基础请求配置)
- [2. Auth 模块（认证鉴权）](#2-auth-模块认证鉴权)
- [3. User 模块（用户端）](#3-user-模块用户端)
- [4. Merchant 模块（商家端）](#4-merchant-模块商家端)
- [5. Admin 模块（平台端）](#5-admin-模块平台端)
- [6. Search 模块（搜索）](#6-search-模块搜索)
- [7. Notification 模块（通知）](#7-notification-模块通知)
- [8. Announcement 模块（公告）](#8-announcement-模块公告)
- [9. 直接API调用（非api/目录）](#9-直接api调用非api目录)
- [10. 问题汇总](#10-问题汇总)

---

## 1. 基础请求配置

**文件**：[request.ts](file:///d:/j/cg/cg/petshop-vue/src/api/request.ts)

| 配置项 | 值 |
|--------|-----|
| baseURL | `VITE_API_BASE_URL \|\| '/api'` |
| timeout | 10000ms |
| 认证方式 | Bearer Token（从 localStorage/sessionStorage 读取） |
| Token键名 | `token`、`merchant_token`（localStorage/sessionStorage） |

**请求拦截器**：自动在请求头添加 `Authorization: Bearer {token}`

**响应拦截器**：
- 成功：`code === 200 || code === 0` 时返回 `data.data`
- 401：清除token并跳转 `/merchant/login`
- 403/404/500：显示对应错误消息

---

## 2. Auth 模块（认证鉴权）

**文件**：[auth.ts](file:///d:/j/cg/cg/petshop-vue/src/api/auth.ts)

### 2.1 用户登录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/login` |
| 方法 | POST |
| 调用位置 | `views/login/Login.vue`（直接axios调用）、`api/auth.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

> ⚠️ **问题**：Login.vue中直接使用axios调用时，传参为 `{phone, password, role: 'user'}`，与api/auth.ts定义的 `{username, password}` 不一致

**响应类型**：`{ token: string }`

### 2.2 用户注册

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/register` |
| 方法 | POST |
| 调用位置 | `views/login/Register.vue`（直接axios调用）、`api/auth.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| email | string | 否 | 邮箱 |
| phone | string | 否 | 手机号 |

> ⚠️ **问题**：Register.vue中直接使用axios调用时，传参为 `{username, email, password, phone}`，与api定义一致

**响应类型**：`{ token: string }`

### 2.3 商家注册

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/merchant/register` |
| 方法 | POST |
| 调用位置 | `views/merchant/Register.vue`（通过api调用） |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| password | string | 是 | 密码 |
| email | string | 否 | 邮箱 |
| phone | string | 是 | 手机号 |
| contact_person | string | 否 | 联系人 |
| name | string | 否 | 商家名称 |
| logo | string | 否 | Logo |
| address | string | 否 | 地址 |

**响应类型**：`{ message: string }`

### 2.4 退出登录

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/logout` |
| 方法 | POST |
| 调用位置 | `api/auth.ts` |

**请求参数**：无

**响应类型**：void

### 2.5 获取用户信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/userinfo` |
| 方法 | GET |
| 调用位置 | `views/user/user-profile/index.vue`、`views/user/profile-edit/index.vue` |

**请求参数**：无

**响应类型**：`UserInfo`

```typescript
interface UserInfo {
  id: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  role: string
}
```

### 2.6 更新用户信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/userinfo` |
| 方法 | PUT |
| 调用位置 | `views/user/user-profile/index.vue`、`views/user/profile-edit/index.vue`（直接fetch调用） |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 否 | 用户名 |
| email | string | 否 | 邮箱 |
| phone | string | 否 | 手机号 |
| avatar | string | 否 | 头像 |
| password | string | 否 | 密码 |

> ⚠️ **问题**：profile-edit/index.vue中使用fetch直接调用，传参为 `{nickname, gender, phone, email, avatar, birthday}`，与api定义的 `{username, email, phone, avatar, password}` 字段不一致（nickname vs username，多了gender和birthday）

**响应类型**：void

### 2.7 发送验证码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/sendVerifyCode` |
| 方法 | POST |
| 调用位置 | `views/forgot-password/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱地址 |

**响应类型**：void

### 2.8 重置密码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/resetPassword` |
| 方法 | POST |
| 调用位置 | `views/forgot-password/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱 |
| verifyCode | string | 是 | 验证码 |
| password | string | 是 | 新密码 |

**响应类型**：void

### 2.9 修改密码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/auth/password` |
| 方法 | PUT |
| 调用位置 | `api/auth.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码 |

**响应类型**：`{ success: boolean; message: string }`

---

## 3. User 模块（用户端）

**文件**：[user.ts](file:///d:/j/cg/cg/petshop-vue/src/api/user.ts)

### 3.1 宠物管理

#### 3.1.1 获取宠物列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/pets` |
| 方法 | GET |
| 调用位置 | `views/user/user-pets/index.vue`、`views/user/appointment-confirm/index.vue` |

**请求参数**：无

**响应类型**：`Pet[]`

```typescript
interface Pet {
  id: number
  name: string
  type: string
  breed?: string
  age?: number
  gender?: string
  avatar?: string
  description?: string
  weight?: number
  bodyType?: string
  furColor?: string
  personality?: string
  userId: number
}
```

#### 3.1.2 获取宠物详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/pets/{id}` |
| 方法 | GET |
| 调用位置 | `views/user/pet-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 宠物ID |

**响应类型**：`Pet`

#### 3.1.3 添加宠物

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/pets` |
| 方法 | POST |
| 调用位置 | `views/user/pet-edit/index.vue` |

**请求参数**：`Omit<Pet, 'id'>`

**响应类型**：`Pet`

#### 3.1.4 更新宠物

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/pets/{id}` |
| 方法 | PUT |
| 调用位置 | `views/user/pet-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 宠物ID |

**请求参数**：`Partial<Pet>`

**响应类型**：`Pet`

#### 3.1.5 删除宠物

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/pets/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/user/user-pets/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 宠物ID |

**响应类型**：void

### 3.2 预约管理

#### 3.2.1 获取预约列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/appointments` |
| 方法 | GET |
| 调用位置 | `views/user/user-appointments/index.vue`、`views/user/user-book/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 否 | 状态筛选 |
| keyword | string | 否 | 关键字搜索 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：

```typescript
{
  total: number
  data: Appointment[]
  totalPages: number
  pageSize: number
  page: number
}
```

#### 3.2.2 获取预约详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/appointments/{id}` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 预约ID |

**响应类型**：`AppointmentDetail`

#### 3.2.3 创建预约

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/appointments` |
| 方法 | POST |
| 调用位置 | `views/user/appointment-confirm/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| serviceId | number | 是 | 服务ID |
| petId | number | 是 | 宠物ID |
| appointmentTime | string | 是 | 预约时间 |
| notes | string | 否 | 备注 |

**响应类型**：void

#### 3.2.4 取消预约

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/appointments/{id}/cancel` |
| 方法 | PUT |
| 调用位置 | `views/user/user-appointments/index.vue`、`views/user/user-book/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 预约ID |

**响应类型**：void

#### 3.2.5 获取预约统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/appointments/stats` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**请求参数**：无

**响应类型**：`AppointmentStats`

```typescript
interface AppointmentStats {
  total: number
  pending: number
  confirmed: number
  completed: number
  cancelled: number
}
```

### 3.3 服务相关

#### 3.3.1 获取服务列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/services` |
| 方法 | GET |
| 调用位置 | `views/user/service-list/index.vue`、`views/home/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | string | 否 | 服务类型 |

**响应类型**：`Service[]`

#### 3.3.2 获取服务详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/services/{id}` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

**响应类型**：`Service`

#### 3.3.3 搜索服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/services/search` |
| 方法 | GET |
| 调用位置 | `views/user/service-list/index.vue`、`views/user/search/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**响应类型**：`Service[]`

#### 3.3.4 获取推荐服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/services/recommended` |
| 方法 | GET |
| 调用位置 | `views/user/user-home/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量限制 |

**响应类型**：`Service[]`

### 3.4 商家相关

#### 3.4.1 获取商家信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/{id}` |
| 方法 | GET |
| 调用位置 | `views/user/appointment-confirm/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**响应类型**：`MerchantInfo`

#### 3.4.2 获取商家服务列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/{id}/services` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**响应类型**：`Service[]`

#### 3.4.3 获取商家评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/{id}/reviews` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**响应类型**：`MerchantReview[]`

#### 3.4.4 获取商家商品列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/{id}/products` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**响应类型**：`Product[]`

#### 3.4.5 获取商家可用时段

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/{merchantId}/available-slots` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | number | 是 | 商家ID |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | string | 是 | 日期 |

**响应类型**：`AvailableSlotsResponse`

#### 3.4.6 搜索商家

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchants/search` |
| 方法 | GET |
| 调用位置 | `views/user/search/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**响应类型**：`MerchantInfo[]`

#### 3.4.7 获取商家列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchants` |
| 方法 | GET |
| 调用位置 | `views/user/user-home/index.vue`、`views/user/user-merchant/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 关键字 |
| rating | number | 否 | 评分 |
| sortBy | string | 否 | 排序字段 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：`{ data: MerchantListItem[]; total: number }`

### 3.5 收藏管理

#### 3.5.1 添加商家收藏

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | number | 是 | 商家ID |

**响应类型**：`Favorite`

#### 3.5.2 获取收藏商家列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites` |
| 方法 | GET |
| 调用位置 | `views/user/user-favorites/index.vue` |

**请求参数**：无

**响应类型**：`FavoriteMerchant[]`

#### 3.5.3 取消商家收藏

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/{merchantId}` |
| 方法 | DELETE |
| 调用位置 | `views/user/user-favorites/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | number | 是 | 商家ID |

**响应类型**：void

#### 3.5.4 添加服务收藏

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/services` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| serviceId | number | 是 | 服务ID |

**响应类型**：void

#### 3.5.5 获取服务收藏列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/services` |
| 方法 | GET |
| 调用位置 | `views/user/user-favorites/index.vue` |

**请求参数**：无

**响应类型**：`FavoriteService[]`

#### 3.5.6 取消服务收藏

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/services/{serviceId}` |
| 方法 | DELETE |
| 调用位置 | `views/user/user-favorites/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| serviceId | number | 是 | 服务ID |

**响应类型**：void

#### 3.5.7 添加商品收藏

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/products` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |

**响应类型**：void

#### 3.5.8 取消商品收藏

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/products/{productId}` |
| 方法 | DELETE |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |

**响应类型**：void

#### 3.5.9 检查商品收藏状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/favorites/products/{productId}/check` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |

**响应类型**：`{ isFavorited: boolean }`

### 3.6 商品相关

#### 3.6.1 获取商品列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/products` |
| 方法 | GET |
| 调用位置 | `views/home/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 关键字 |
| category | string | 否 | 分类 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：`Product[]`

#### 3.6.2 获取商品详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/products/{id}` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**响应类型**：`Product`

#### 3.6.3 搜索商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/products/search` |
| 方法 | GET |
| 调用位置 | `views/user/search/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**响应类型**：`Product[]`

#### 3.6.4 获取商品评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/products/{productId}/reviews` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |

**响应类型**：`Review[]`

### 3.7 购物车

#### 3.7.1 获取购物车

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/cart` |
| 方法 | GET |
| 调用位置 | `views/user/user-cart/index.vue`、`views/user/UserLayout.vue` |

**请求参数**：无

**响应类型**：`CartItem[]`

```typescript
interface CartItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  merchantId: number
  merchantName: string
}
```

#### 3.7.2 添加到购物车

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/cart` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |
| quantity | number | 是 | 数量 |

**响应类型**：void

#### 3.7.3 更新购物车商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/cart` |
| 方法 | PUT |
| 调用位置 | `views/user/user-cart/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |
| quantity | number | 是 | 数量 |

**响应类型**：void

#### 3.7.4 移除购物车商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/cart/{productId}` |
| 方法 | DELETE |
| 调用位置 | `views/user/user-cart/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |

**响应类型**：void

#### 3.7.5 批量移除购物车商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/cart/batch` |
| 方法 | DELETE |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productIds | number[] | 是 | 商品ID数组 |

**响应类型**：void

### 3.8 订单管理

#### 3.8.1 创建订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders` |
| 方法 | POST |
| 调用位置 | `views/user/checkout/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | number | 是 | 商品ID |
| addressId | number | 是 | 地址ID |
| quantity | number | 是 | 数量 |

> ⚠️ **问题**：`createOrder`和`purchaseProduct`函数路径相同（`/api/user/orders`），参数也相同，存在重复定义

**响应类型**：void

#### 3.8.2 购买商品（重复）

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：同 3.8.1

> ⚠️ **问题**：与createOrder完全重复

#### 3.8.3 获取订单列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders` |
| 方法 | GET |
| 调用位置 | `views/user/user-orders/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 否 | 状态 |
| keyword | string | 否 | 关键字 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：

```typescript
{
  total: number
  data: ProductOrder[]
  totalPages: number
  pageSize: number
  page: number
}
```

#### 3.8.4 获取订单详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{id}` |
| 方法 | GET |
| 调用位置 | `views/user/pay/index.vue`、`views/user/order-detail/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**响应类型**：`OrderDetail`

#### 3.8.5 支付订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{orderId}/pay` |
| 方法 | POST |
| 调用位置 | `views/user/pay/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | number | 是 | 订单ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| payMethod | string | 是 | 支付方式 |

**响应类型**：void

#### 3.8.6 获取支付状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{orderId}/pay/status` |
| 方法 | GET |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | number | 是 | 订单ID |

**响应类型**：`PayStatusResponse`

#### 3.8.7 取消订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{id}/cancel` |
| 方法 | PUT |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**响应类型**：void

#### 3.8.8 退款

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{id}/refund` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | string | 是 | 退款原因 |

**响应类型**：void

#### 3.8.9 确认收货

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{id}/confirm` |
| 方法 | PUT |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**响应类型**：void

#### 3.8.10 删除订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/{id}` |
| 方法 | DELETE |
| 调用位置 | `api/user.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**响应类型**：void

#### 3.8.11 批量取消订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/batch-cancel` |
| 方法 | PUT |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 订单ID数组 |

**响应类型**：void

#### 3.8.12 批量删除订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/batch-delete` |
| 方法 | DELETE |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 订单ID数组 |

**响应类型**：void

#### 3.8.13 预览订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/orders/preview` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| items | `{productId: number; quantity: number}[]` | 是 | 商品列表 |

**响应类型**：`OrderPreview`

### 3.9 收货地址

#### 3.9.1 获取地址列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/addresses` |
| 方法 | GET |
| 调用位置 | `views/user/addresses/index.vue`、`views/user/checkout/index.vue` |

**请求参数**：无

**响应类型**：`Address[]`

```typescript
interface Address {
  id?: number
  userId?: number
  contactName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
}
```

#### 3.9.2 添加地址

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/addresses` |
| 方法 | POST |
| 调用位置 | `views/user/addresses/index.vue` |

**请求参数**：`Omit<Address, 'id'>`

**响应类型**：`Address`

#### 3.9.3 更新地址

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/addresses/{id}` |
| 方法 | PUT |
| 调用位置 | `views/user/addresses/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 地址ID |

**请求参数**：`Partial<Address>`

**响应类型**：`Address`

#### 3.9.4 删除地址

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/addresses/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/user/addresses/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 地址ID |

**响应类型**：void

#### 3.9.5 设为默认地址

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/addresses/{id}/default` |
| 方法 | PUT |
| 调用位置 | `views/user/addresses/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 地址ID |

**响应类型**：void

### 3.10 评价管理

#### 3.10.1 添加评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/reviews` |
| 方法 | POST |
| 调用位置 | `api/user.ts` |

**请求参数**：

```typescript
{
  appointmentId: number    // 必填
  merchantId: number       // 必填
  serviceId: number        // 必填
  rating: number           // 必填
  comment: string          // 必填
}
```

**响应类型**：void

#### 3.10.2 获取用户评价列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/reviews` |
| 方法 | GET |
| 调用位置 | `views/user/my-reviews/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | string | 否 | 评价类型 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

**响应类型**：`Review[]`

#### 3.10.3 获取用户评价列表（分页版）

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/reviews` |
| 方法 | GET |
| 调用位置 | `api/user.ts`（getUserReviewsList函数） |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| rating | number | 否 | 评分 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：`{ data: UserReview[]; total: number }`

> ⚠️ **问题**：`getUserReviews`和`getUserReviewsList`路径相同但参数和返回类型不同，可能造成混淆

#### 3.10.4 更新评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/reviews/{id}` |
| 方法 | PUT |
| 调用位置 | `views/user/my-reviews/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| rating | number | 是 | 评分 |
| comment | string | 是 | 评论内容 |

**响应类型**：void

#### 3.10.5 删除评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/reviews/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/user/my-reviews/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**响应类型**：void

### 3.11 用户首页

#### 3.11.1 获取首页统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/home/stats` |
| 方法 | GET |
| 调用位置 | `views/user/user-home/index.vue` |

**请求参数**：无

**响应类型**：`HomeStats`

```typescript
interface HomeStats {
  petCount: number
  pendingAppointments: number
  reviewCount: number
}
```

#### 3.11.2 获取最近活动

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/home/activities` |
| 方法 | GET |
| 调用位置 | `views/user/user-home/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量限制 |

**响应类型**：`Activity[]`

#### 3.11.3 获取已购买服务列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/services` |
| 方法 | GET |
| 调用位置 | `views/user/user-services/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 关键字 |
| status | string | 否 | 状态 |
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：`{ data: UserPurchasedService[]; total: number }`

---

## 4. Merchant 模块（商家端）

**文件**：[merchant.ts](file:///d:/j/cg/cg/petshop-vue/src/api/merchant.ts)

### 4.1 商家信息

#### 4.1.1 获取商家信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/info` |
| 方法 | GET |
| 调用位置 | `views/merchant/MerchantLayout.vue`、`views/merchant/shop-edit/index.vue` |

**请求参数**：无

**响应类型**：`MerchantInfo`

#### 4.1.2 更新商家信息

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/info` |
| 方法 | PUT |
| 调用位置 | `views/merchant/shop-edit/index.vue` |

**请求参数**：`Partial<MerchantInfo>`

**响应类型**：`MerchantInfo`

#### 4.1.3 修改密码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/change-password` |
| 方法 | POST |
| 调用位置 | `api/merchant.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码 |

**响应类型**：void

#### 4.1.4 绑定手机号

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/bind-phone` |
| 方法 | POST |
| 调用位置 | `api/merchant.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 是 | 手机号 |
| verifyCode | string | 是 | 验证码 |

**响应类型**：void

#### 4.1.5 绑定邮箱

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/bind-email` |
| 方法 | POST |
| 调用位置 | `api/merchant.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱 |
| verifyCode | string | 是 | 验证码 |

**响应类型**：void

#### 4.1.6 发送验证码

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/send-verify-code` |
| 方法 | POST |
| 调用位置 | `api/merchant.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | 'phone' \| 'email' | 是 | 验证类型 |
| value | string | 是 | 手机号或邮箱 |

**响应类型**：void

### 4.2 商家Dashboard

#### 4.2.1 获取Dashboard统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/dashboard` |
| 方法 | GET |
| 调用位置 | `views/merchant/home/index.vue`、`views/merchant/merchant-home/index.vue` |

**请求参数**：无

**响应类型**：`DashboardStats`

```typescript
interface DashboardStats {
  todayOrders: number
  pendingAppointments: number
  todayRevenue: number
  avgRating: number
  orderGrowth: number
  revenueGrowth: number
  ratingCount: number
}
```

#### 4.2.2 获取最近订单

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/appointments/recent` |
| 方法 | GET |
| 调用位置 | `api/merchant.ts` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量（默认5） |

**响应类型**：`RecentOrder[]`

#### 4.2.3 获取最近评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/reviews/recent` |
| 方法 | GET |
| 调用位置 | `api/merchant.ts` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量（默认5） |

**响应类型**：`RecentReview[]`

### 4.3 服务管理

#### 4.3.1 获取服务列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services` |
| 方法 | GET |
| 调用位置 | `views/merchant/services/index.vue` |

**请求参数**：无

**响应类型**：`MerchantService[]`

#### 4.3.2 获取服务详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services/{id}` |
| 方法 | GET |
| 调用位置 | `views/merchant/service-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

**响应类型**：`MerchantService`

#### 4.3.3 添加服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services` |
| 方法 | POST |
| 调用位置 | `views/merchant/service-edit/index.vue` |

**请求参数**：`Omit<MerchantService, 'id'>`

**响应类型**：`MerchantService`

#### 4.3.4 更新服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services/{id}` |
| 方法 | PUT |
| 调用位置 | `views/merchant/service-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

**请求参数**：`Partial<MerchantService>`

**响应类型**：`MerchantService`

#### 4.3.5 删除服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/merchant/services/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

**响应类型**：void

#### 4.3.6 批量更新服务状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/services/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 服务ID数组 |
| status | 'enabled' \| 'disabled' | 是 | 状态 |

**响应类型**：`MerchantService[]`

#### 4.3.7 批量删除服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/services/batch` |
| 方法 | DELETE |
| 调用位置 | `api/merchant.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 服务ID数组 |

**响应类型**：void

### 4.4 商品管理

#### 4.4.1 获取商品列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products` |
| 方法 | GET |
| 调用位置 | `api/merchant.ts` |

**请求参数**：无

**响应类型**：`Product[]`

#### 4.4.2 获取商品列表（分页）

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/paged` |
| 方法 | GET |
| 调用位置 | `views/merchant/merchant-products/index.vue` |

**查询参数**：`any`（ProductQuery类型）

```typescript
interface ProductQuery {
  page: number
  pageSize: number
  name?: string
  minPrice?: number
  maxPrice?: number
  stockStatus?: 'all' | 'in_stock' | 'out_of_stock'
  status?: 'all' | 'enabled' | 'disabled'
}
```

**响应类型**：`any`（ProductListResponse类型）

#### 4.4.3 获取商品详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/{id}` |
| 方法 | GET |
| 调用位置 | `views/merchant/product-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**响应类型**：`Product`

#### 4.4.4 添加商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products` |
| 方法 | POST |
| 调用位置 | `views/merchant/product-edit/index.vue` |

**请求参数**：`Omit<Product, 'id'>`

**响应类型**：`Product`

#### 4.4.5 更新商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/{id}` |
| 方法 | PUT |
| 调用位置 | `views/merchant/product-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**请求参数**：`Partial<Product>`

**响应类型**：`Product`

#### 4.4.6 删除商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/merchant/merchant-products/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**响应类型**：void

#### 4.4.7 更新商品状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/merchant-products/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | 'enabled' \| 'disabled' | 是 | 状态 |

**响应类型**：`Product`

#### 4.4.8 批量更新商品状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/merchant-products/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 商品ID数组 |
| status | 'enabled' \| 'disabled' | 是 | 状态 |

**响应类型**：`Product[]`

#### 4.4.9 批量删除商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/products/batch` |
| 方法 | DELETE |
| 调用位置 | `views/merchant/merchant-products/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 商品ID数组 |

**响应类型**：void

### 4.5 预约/订单管理

#### 4.5.1 获取预约列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/appointments` |
| 方法 | GET |
| 调用位置 | `views/merchant/merchant-orders/index.vue`、`views/merchant/appointments/index.vue`、`views/merchant/merchant-appointments/index.vue` |

**请求参数**：无

**响应类型**：`Appointment[]`

> ⚠️ **问题**：`getMerchantOrders`和`getMerchantAppointments`路径完全相同（`/api/merchant/appointments`），存在重复定义

#### 4.5.2 更新预约状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/appointments/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/merchant-orders/index.vue`、`views/merchant/appointments/index.vue`、`views/merchant/merchant-appointments/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 预约ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |
| rejectReason | string | 否 | 拒绝原因 |

**响应类型**：`Appointment`

> ⚠️ **问题**：`updateOrderStatus`和`updateAppointmentStatus`路径和参数完全相同，存在重复定义

### 4.6 商品订单管理

#### 4.6.1 获取商品订单列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/product-orders` |
| 方法 | GET |
| 调用位置 | `views/merchant/merchant-product-orders/index.vue` |

**请求参数**：无

**响应类型**：`ProductOrder[]`

#### 4.6.2 更新商品订单状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/product-orders/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/merchant-product-orders/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |

**响应类型**：`ProductOrder`

#### 4.6.3 更新商品订单物流

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/product-orders/{id}/logistics` |
| 方法 | PUT |
| 调用位置 | `views/merchant/merchant-product-orders/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| logisticsCompany | string | 是 | 物流公司 |
| trackingNumber | string | 是 | 物流单号 |

**响应类型**：`ProductOrder`

### 4.7 评价管理

#### 4.7.1 获取评价列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/reviews` |
| 方法 | GET |
| 调用位置 | `views/merchant/reviews/index.vue`、`views/merchant/merchant-reviews/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 否 | 页码（默认0） |
| size | number | 否 | 每页条数（默认10） |
| sortBy | string | 否 | 排序字段（默认createdAt） |
| sortDir | string | 否 | 排序方向（默认desc） |
| rating | number | 否 | 评分 |
| keyword | string | 否 | 关键字 |

**响应类型**：`any`

#### 4.7.2 获取评价统计 ⚠️直接调用

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/reviews/statistics` |
| 方法 | GET |
| 调用位置 | `views/merchant/merchant-reviews/index.vue`（直接使用request.get） |

**请求参数**：无

**响应类型**：未定义（直接使用request.get，未在api/merchant.ts中声明）

> ⚠️ **问题**：此API未在api/merchant.ts中声明，直接在组件中使用request.get调用

#### 4.7.3 回复评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/reviews/{id}/reply` |
| 方法 | PUT |
| 调用位置 | `views/merchant/reviews/index.vue`、`views/merchant/merchant-reviews/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reply | string | 是 | 回复内容 |

**响应类型**：`Review`

#### 4.7.4 删除评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/reviews/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/merchant/reviews/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**响应类型**：void

### 4.8 分类管理

#### 4.8.1 获取分类列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories` |
| 方法 | GET |
| 调用位置 | `views/merchant/categories/index.vue` |

**请求参数**：无

**响应类型**：`Category[]`

#### 4.8.2 添加分类

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories` |
| 方法 | POST |
| 调用位置 | `views/merchant/categories/index.vue` |

**请求参数**：`Omit<Category, 'id' | 'productCount' | 'createdAt'>`

**响应类型**：`Category`

#### 4.8.3 更新分类

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories/{id}` |
| 方法 | PUT |
| 调用位置 | `views/merchant/categories/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 分类ID |

**请求参数**：`Partial<Category>`

**响应类型**：`Category`

#### 4.8.4 删除分类

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/merchant/categories/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 分类ID |

**响应类型**：void

#### 4.8.5 更新分类状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/categories/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 分类ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | 'enabled' \| 'disabled' | 是 | 状态 |

**响应类型**：`Category`

#### 4.8.6 批量更新分类状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/merchant/categories/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 分类ID数组 |
| status | 'enabled' \| 'disabled' | 是 | 状态 |

**响应类型**：`Category[]`

#### 4.8.7 批量删除分类

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/categories/batch` |
| 方法 | DELETE |
| 调用位置 | `views/merchant/categories/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 分类ID数组 |

**响应类型**：void

### 4.9 统计报表

#### 4.9.1 获取营收统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/revenue-stats` |
| 方法 | GET |
| 调用位置 | `views/merchant/stats-revenue/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | string | 是 | 开始日期（前端计算） |
| endDate | string | 是 | 结束日期（前端计算） |

**请求参数（函数入参）**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | 'today' \| 'week' \| 'month' \| 'year' \| 'custom' | 是 | 时间范围类型 |
| startDate | string | 否 | 自定义开始日期 |
| endDate | string | 否 | 自定义结束日期 |

**响应类型**：`RevenueStats`

#### 4.9.2 导出营收统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/revenue-stats/export` |
| 方法 | GET |
| 调用位置 | `views/merchant/stats-revenue/index.vue` |

**查询参数**：同 4.9.1

**响应类型**：`Blob`（responseType: 'blob'）

#### 4.9.3 获取预约统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/appointment-stats` |
| 方法 | GET |
| 调用位置 | `views/merchant/stats-appointments/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | string | 是 | 开始日期（前端计算） |
| endDate | string | 是 | 结束日期（前端计算） |

**请求参数（函数入参）**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| timeRange | string | 是 | 时间范围类型 |
| startDate | string | 否 | 自定义开始日期 |
| endDate | string | 否 | 自定义结束日期 |

**响应类型**：`AppointmentStats`

#### 4.9.4 导出预约统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/appointment-stats/export` |
| 方法 | GET |
| 调用位置 | `views/merchant/stats-appointments/index.vue` |

**查询参数**：同 4.9.3

**响应类型**：`Blob`（responseType: 'blob'）

### 4.10 店铺设置

#### 4.10.1 获取商家设置

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/settings` |
| 方法 | GET |
| 调用位置 | `views/merchant/shop-settings/index.vue` |

**请求参数**：无

**响应类型**：`MerchantSettings`

#### 4.10.2 更新商家设置

| 属性 | 值 |
|------|-----|
| 路径 | `/api/merchant/settings` |
| 方法 | PUT |
| 调用位置 | `views/merchant/shop-settings/index.vue` |

**请求参数**：`MerchantSettings`

**响应类型**：`MerchantSettings`

---

## 5. Admin 模块（平台端）

**文件**：[admin.ts](file:///d:/j/cg/cg/petshop-vue/src/api/admin.ts)

### 5.1 Dashboard

#### 5.1.1 获取Dashboard统计

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/dashboard` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-dashboard/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：无

**响应类型**：`DashboardData`

```typescript
interface DashboardData {
  userCount: number
  merchantCount: number
  orderCount: number
  serviceCount: number
}
```

#### 5.1.2 获取最近注册用户 ⚠️直接调用

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/dashboard/recent-users` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-dashboard/index.vue`（直接fetch调用） |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：未在api/中声明

> ⚠️ **问题**：此API未在api/admin.ts中声明，直接在组件中使用fetch调用

#### 5.1.3 获取待审核商家 ⚠️直接调用

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/dashboard/pending-merchants` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-dashboard/index.vue`（直接fetch调用） |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：未在api/中声明

> ⚠️ **问题**：此API未在api/admin.ts中声明，直接在组件中使用fetch调用

#### 5.1.4 获取公告列表 ⚠️直接调用

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/dashboard/announcements` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-dashboard/index.vue`（直接fetch调用） |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |

**响应类型**：未在api/中声明

> ⚠️ **问题**：此API未在api/admin.ts中声明，直接在组件中使用fetch调用

### 5.2 用户管理

#### 5.2.1 获取所有用户

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-users/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：无

**响应类型**：`User[]`

#### 5.2.2 获取用户详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/{id}` |
| 方法 | GET |
| 调用位置 | `views/admin/user-detail/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

**响应类型**：`UserDetail`

#### 5.2.3 更新用户状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-users/index.vue`（直接fetch调用）、`views/admin/user-detail/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |

**响应类型**：`User`

#### 5.2.4 删除用户

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-users/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

**响应类型**：void

#### 5.2.5 批量更新用户状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-users/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 用户ID数组 |
| status | string | 是 | 状态 |

**响应类型**：void

#### 5.2.6 批量删除用户

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/users/batch` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-users/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 用户ID数组 |

**响应类型**：void

### 5.3 商家管理

#### 5.3.1 获取所有商家

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-merchants/index.vue`（直接fetch调用）、`views/admin/admin-services/index.vue`（直接fetch调用）、`views/admin/admin-products/index.vue`（直接fetch调用）、`views/admin/product-manage/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：无

**响应类型**：`Merchant[]`

#### 5.3.2 获取商家详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/{id}` |
| 方法 | GET |
| 调用位置 | `views/admin/merchant-detail/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**响应类型**：`MerchantDetail`

#### 5.3.3 更新商家状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/merchant-detail/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |

**响应类型**：`Merchant`

#### 5.3.4 审核商家

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/{id}/audit` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-merchants/index.vue`（直接fetch调用）、`views/admin/merchant-audit/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 审核状态 |
| reason | string | 否 | 拒绝原因 |

**响应类型**：`Merchant`

#### 5.3.5 删除商家

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-merchants/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商家ID |

**响应类型**：void

#### 5.3.6 获取待审核商家

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/pending` |
| 方法 | GET |
| 调用位置 | `views/admin/merchant-audit/index.vue`（直接fetch调用）、`api/admin.ts` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |
| keyword | string | 否 | 关键字 |

**响应类型**：`Merchant[]`

#### 5.3.7 批量更新商家状态 ⚠️直接调用

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-merchants/index.vue`（直接fetch调用） |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 商家ID数组 |
| status | string | 是 | 状态 |

**响应类型**：未在api/中声明

> ⚠️ **问题**：此API未在api/admin.ts中声明，直接在组件中使用fetch调用

#### 5.3.8 批量删除商家 ⚠️直接调用

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/merchants/batch` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-merchants/index.vue`（直接fetch调用） |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 商家ID数组 |

**响应类型**：未在api/中声明

> ⚠️ **问题**：此API未在api/admin.ts中声明，直接在组件中使用fetch调用

### 5.4 服务管理

#### 5.4.1 获取所有服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/services` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-services/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：无

**响应类型**：`AdminService[]`

#### 5.4.2 更新服务状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/services/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-services/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |

**响应类型**：`AdminService`

#### 5.4.3 删除服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/services/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-services/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

**响应类型**：void

#### 5.4.4 批量更新服务状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/services/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-services/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 服务ID数组 |
| status | string | 是 | 状态 |

**响应类型**：void

#### 5.4.5 批量删除服务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/services/batch` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-services/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 服务ID数组 |

**响应类型**：void

### 5.5 商品管理

#### 5.5.1 获取所有商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/products` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-products/index.vue`（直接fetch调用）、`views/admin/product-manage/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：无

**响应类型**：`Product[]`

> ⚠️ **问题**：`getAllProducts`和`getAllProductsForAdmin`路径完全相同（`/api/admin/products`），存在重复定义

#### 5.5.2 更新商品状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/products/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-products/index.vue`（直接fetch调用）、`views/admin/product-manage/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |

**响应类型**：`Product`

#### 5.5.3 删除商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/products/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-products/index.vue`（直接fetch调用）、`views/admin/product-manage/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 商品ID |

**响应类型**：void

#### 5.5.4 批量更新商品状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/products/batch/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-products/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 商品ID数组 |
| status | string | 是 | 状态 |

**响应类型**：void

#### 5.5.5 批量删除商品

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/products/batch` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-products/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 商品ID数组 |

**响应类型**：void

### 5.6 评价管理

#### 5.6.1 获取所有评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/reviews` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-reviews/index.vue`（直接fetch调用）、`api/admin.ts` |

**请求参数**：无

**响应类型**：`Review[]`

#### 5.6.2 删除评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/reviews/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-reviews/index.vue`（直接fetch调用）、`views/admin/review-audit/index.vue`（直接fetch调用）、`api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**响应类型**：void

#### 5.6.3 获取待审核评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/reviews/pending` |
| 方法 | GET |
| 调用位置 | `views/admin/review-audit/index.vue`（直接fetch调用）、`api/admin.ts`（getReviewsForAudit） |

**请求参数**：无

**响应类型**：`Review[]`

> ⚠️ **问题**：`getReviewsForAudit`在api/admin.ts中声明的路径是`/api/admin/reviews/audit`，但review-audit/index.vue中直接fetch调用的路径是`/api/admin/reviews/pending`，路径不一致

#### 5.6.4 审核通过评价

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/reviews/{id}/approve` |
| 方法 | PUT |
| 调用位置 | `api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**响应类型**：`Review`

> ⚠️ **问题**：review-audit/index.vue中直接fetch调用路径为`/api/admin/reviews/{id}/audit`（body: {status: 'approved'}），与api/admin.ts中声明的`/api/admin/reviews/{id}/approve`不一致

#### 5.6.5 标记评价违规

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/reviews/{id}/violation` |
| 方法 | PUT |
| 调用位置 | `api/admin.ts` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

**响应类型**：`Review`

> ⚠️ **问题**：review-audit/index.vue中直接fetch调用路径为`/api/admin/reviews/{id}/audit`（body: {status: 'violation', reason, remark}），与api/admin.ts中声明的`/api/admin/reviews/{id}/violation`不一致

### 5.7 公告管理

#### 5.7.1 获取公告列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-announcements/index.vue`、`views/user/user-announcements/index.vue` |

**请求参数**：无

**响应类型**：`Announcement[]`

#### 5.7.2 获取公告详情

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements/{id}` |
| 方法 | GET |
| 调用位置 | `views/admin/announcement-edit/index.vue`、`views/user/announcement-detail/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 公告ID |

**响应类型**：`Announcement`

#### 5.7.3 添加公告

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements` |
| 方法 | POST |
| 调用位置 | `views/admin/admin-announcements/index.vue`、`views/admin/announcement-edit/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | string | 是 | 标题 |
| content | string | 是 | 内容 |

**响应类型**：`Announcement`

#### 5.7.4 更新公告

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements/{id}` |
| 方法 | PUT |
| 调用位置 | `views/admin/announcement-edit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 公告ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | string | 是 | 标题 |
| content | string | 是 | 内容 |

**响应类型**：`Announcement`

#### 5.7.5 删除公告

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-announcements/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 公告ID |

**响应类型**：void

#### 5.7.6 发布公告

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements/{id}/publish` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-announcements/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 公告ID |

**请求参数**：`{}`（空对象）

**响应类型**：`Announcement`

#### 5.7.7 取消发布公告

| 属性 | 值 |
|------|-----|
| 路径 | `/api/announcements/{id}/unpublish` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-announcements/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 公告ID |

**请求参数**：`{}`（空对象）

**响应类型**：`Announcement`

### 5.8 店铺审核

#### 5.8.1 获取待审核店铺

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/shops/pending` |
| 方法 | GET |
| 调用位置 | `views/admin/shop-audit/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |
| keyword | string | 否 | 关键字 |

**响应类型**：`Shop[]`

#### 5.8.2 审核店铺

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/shops/{id}/audit` |
| 方法 | PUT |
| 调用位置 | `views/admin/shop-audit/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 店铺ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 审核状态 |
| reason | string | 否 | 拒绝原因 |

**响应类型**：`Shop`

### 5.9 系统设置

#### 5.9.1 获取系统配置

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/system/config` |
| 方法 | GET |
| 调用位置 | `api/admin.ts` |

**请求参数**：无

**响应类型**：`SystemConfig`

#### 5.9.2 更新系统配置

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/system/config` |
| 方法 | PUT |
| 调用位置 | `api/admin.ts` |

**请求参数**：`SystemConfig`

**响应类型**：`SystemConfig`

### 5.10 操作日志

#### 5.10.1 获取操作日志

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/operation-logs` |
| 方法 | GET |
| 调用位置 | `views/admin/logs/index.vue` |

**查询参数**：`any`

**响应类型**：`OperationLog[]`

#### 5.10.2 删除日志

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/operation-logs/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/logs/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 日志ID |

**响应类型**：void

#### 5.10.3 清空日志

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/operation-logs` |
| 方法 | DELETE |
| 调用位置 | `views/admin/logs/index.vue` |

**请求参数**：无

**响应类型**：void

### 5.11 角色权限

#### 5.11.1 获取角色列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/roles` |
| 方法 | GET |
| 调用位置 | `views/admin/roles/index.vue` |

**请求参数**：无

**响应类型**：`Role[]`

#### 5.11.2 添加角色

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/roles` |
| 方法 | POST |
| 调用位置 | `views/admin/roles/index.vue` |

**请求参数**：`Partial<Role>`

**响应类型**：`Role`

#### 5.11.3 更新角色

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/roles/{id}` |
| 方法 | PUT |
| 调用位置 | `views/admin/roles/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 角色ID |

**请求参数**：`Partial<Role>`

**响应类型**：`Role`

#### 5.11.4 删除角色

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/roles/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/roles/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 角色ID |

**响应类型**：void

#### 5.11.5 获取权限列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/permissions` |
| 方法 | GET |
| 调用位置 | `views/admin/roles/index.vue` |

**请求参数**：无

**响应类型**：`Permission[]`

### 5.12 活动管理

#### 5.12.1 获取活动列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/activities` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-activities/index.vue` |

**查询参数**：`any`

**响应类型**：`Activity[]`

#### 5.12.2 添加活动

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/activities` |
| 方法 | POST |
| 调用位置 | `views/admin/admin-activities/index.vue` |

**请求参数**：`Partial<Activity>`

**响应类型**：`Activity`

#### 5.12.3 更新活动

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/activities/{id}` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-activities/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 活动ID |

**请求参数**：`Partial<Activity>`

**响应类型**：`Activity`

#### 5.12.4 删除活动

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/activities/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-activities/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 活动ID |

**响应类型**：void

#### 5.12.5 切换活动状态

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/activities/{id}/status` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-activities/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 活动ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态 |

**响应类型**：`Activity`

### 5.13 任务管理

#### 5.13.1 获取任务列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/tasks` |
| 方法 | GET |
| 调用位置 | `views/admin/admin-tasks/index.vue` |

**查询参数**：`any`

**响应类型**：`Task[]`

#### 5.13.2 添加任务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/tasks` |
| 方法 | POST |
| 调用位置 | `views/admin/admin-tasks/index.vue` |

**请求参数**：`Partial<Task>`

**响应类型**：`Task`

#### 5.13.3 更新任务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/tasks/{id}` |
| 方法 | PUT |
| 调用位置 | `views/admin/admin-tasks/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

**请求参数**：`Partial<Task>`

**响应类型**：`Task`

#### 5.13.4 删除任务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/tasks/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/admin/admin-tasks/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

**响应类型**：void

#### 5.13.5 执行任务

| 属性 | 值 |
|------|-----|
| 路径 | `/api/admin/tasks/{id}/execute` |
| 方法 | POST |
| 调用位置 | `views/admin/admin-tasks/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

**响应类型**：`Task`

---

## 6. Search 模块（搜索）

**文件**：[search.ts](file:///d:/j/cg/cg/petshop-vue/src/api/search.ts)

### 6.1 获取搜索建议

| 属性 | 值 |
|------|-----|
| 路径 | `/api/search/suggestions` |
| 方法 | GET |
| 调用位置 | `api/search.ts` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**响应类型**：`SearchSuggestions`

```typescript
interface SearchSuggestions {
  services: Service[]
  products: Product[]
  merchants: MerchantInfo[]
}
```

### 6.2 获取热门搜索

| 属性 | 值 |
|------|-----|
| 路径 | `/api/search/hot-keywords` |
| 方法 | GET |
| 调用位置 | `api/search.ts` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量 |

**响应类型**：`HotKeyword[]`

### 6.3 保存搜索历史

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/search-history` |
| 方法 | POST |
| 调用位置 | `api/search.ts` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**响应类型**：void

### 6.4 获取搜索历史

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/search-history` |
| 方法 | GET |
| 调用位置 | `api/search.ts` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量 |

**响应类型**：`string[]`

### 6.5 清空搜索历史

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/search-history` |
| 方法 | DELETE |
| 调用位置 | `api/search.ts` |

**请求参数**：无

**响应类型**：void

---

## 7. Notification 模块（通知）

**文件**：[notification.ts](file:///d:/j/cg/cg/petshop-vue/src/api/notification.ts)

### 7.1 获取通知列表

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications` |
| 方法 | GET |
| 调用位置 | `views/user/notifications/index.vue` |

**查询参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | string | 否 | 通知类型 |
| isRead | boolean | 否 | 是否已读 |

**响应类型**：`Notification[]`

### 7.2 标记已读

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications/{id}/read` |
| 方法 | PUT |
| 调用位置 | `views/user/notifications/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 通知ID |

**响应类型**：void

### 7.3 全部标记已读

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications/read-all` |
| 方法 | PUT |
| 调用位置 | `views/user/notifications/index.vue` |

**请求参数**：无

**响应类型**：void

### 7.4 批量标记已读

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications/batch-read` |
| 方法 | PUT |
| 调用位置 | `views/user/notifications/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 通知ID数组 |

**响应类型**：void

### 7.5 删除通知

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications/{id}` |
| 方法 | DELETE |
| 调用位置 | `views/user/notifications/index.vue` |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 通知ID |

**响应类型**：void

### 7.6 批量删除通知

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications/batch` |
| 方法 | DELETE |
| 调用位置 | `views/user/notifications/index.vue` |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ids | number[] | 是 | 通知ID数组 |

**响应类型**：void

### 7.7 获取未读数量

| 属性 | 值 |
|------|-----|
| 路径 | `/api/user/notifications/unread-count` |
| 方法 | GET |
| 调用位置 | `api/notification.ts` |

**请求参数**：无

**响应类型**：`{ count: number }`

---

## 8. Announcement 模块（公告）

**文件**：[announcement.ts](file:///d:/j/cg/cg/petshop-vue/src/api/announcement.ts)

> 注：此模块的API已在 5.7 节详细记录，此处不再重复

---

## 9. 直接API调用（非api/目录）

以下是在Vue组件中直接使用axios或fetch进行的API调用，未通过`src/api/`目录下的封装函数。

### 9.1 Login.vue - 用户登录页

**文件**：[views/login/Login.vue](file:///d:/j/cg/cg/petshop-vue/src/views/login/Login.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/auth/login` | POST | axios.post | 用户登录，传参`{phone, password, role: 'user'}` |

> ⚠️ **问题**：直接使用axios而非api/auth.ts封装，且传参字段与api定义不一致（phone vs username，多了role字段）

### 9.2 Register.vue - 用户注册页

**文件**：[views/login/Register.vue](file:///d:/j/cg/cg/petshop-vue/src/views/login/Register.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/auth/register` | POST | axios.post | 用户注册，传参`{username, email, password, phone}` |

> ⚠️ **问题**：直接使用axios而非api/auth.ts封装

### 9.3 Merchant Login.vue - 商家登录页

**文件**：[views/merchant/Login.vue](file:///d:/j/cg/cg/petshop-vue/src/views/merchant/Login.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/auth/login` | POST | axios.post | 商家登录，传参`{phone, password, role: 'merchant'}` |

> ⚠️ **问题**：直接使用axios而非api/auth.ts封装，且传参字段与api定义不一致（phone vs username，多了role字段）

### 9.4 profile-edit/index.vue - 编辑个人资料

**文件**：[views/user/profile-edit/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/user/profile-edit/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/auth/userinfo` | PUT | fetch | 更新用户信息，传参`{nickname, gender, phone, email, avatar, birthday}` |

> ⚠️ **问题**：直接使用fetch而非api/auth.ts封装；传参字段与api定义不一致（nickname vs username，多了gender和birthday字段）；缺少Authorization头

### 9.5 merchant-reviews/index.vue - 商家评价

**文件**：[views/merchant/merchant-reviews/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/merchant/merchant-reviews/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/merchant/reviews/statistics` | GET | request.get | 获取评价统计 |

> ⚠️ **问题**：此API未在api/merchant.ts中声明，直接使用request实例调用

### 9.6 admin-dashboard/index.vue - 平台首页

**文件**：[views/admin/admin-dashboard/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/admin-dashboard/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/dashboard` | GET | fetch | 获取Dashboard统计 |
| `/api/admin/dashboard/recent-users` | GET | fetch | 获取最近注册用户 |
| `/api/admin/dashboard/pending-merchants` | GET | fetch | 获取待审核商家 |
| `/api/admin/dashboard/announcements` | GET | fetch | 获取公告列表 |

> ⚠️ **问题**：全部使用fetch直接调用，未通过api/admin.ts封装；后3个API在api/admin.ts中未声明；缺少Authorization头

### 9.7 admin-users/index.vue - 用户管理

**文件**：[views/admin/admin-users/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/admin-users/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/users` | GET | fetch | 获取用户列表 |
| `/api/admin/users/{id}` | DELETE | fetch | 删除用户 |
| `/api/admin/users/{id}/status` | PUT | fetch | 更新用户状态 |
| `/api/admin/users/batch/status` | PUT | fetch | 批量更新用户状态 |
| `/api/admin/users/batch` | DELETE | fetch | 批量删除用户 |

> ⚠️ **问题**：全部使用fetch直接调用，未通过api/admin.ts封装；缺少Authorization头

### 9.8 admin-merchants/index.vue - 商家管理

**文件**：[views/admin/admin-merchants/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/admin-merchants/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/merchants` | GET | fetch | 获取商家列表 |
| `/api/admin/merchants/{id}/audit` | PUT | fetch | 审核商家 |
| `/api/admin/merchants/{id}` | DELETE | fetch | 删除商家 |
| `/api/admin/merchants/batch/status` | PUT | fetch | 批量更新商家状态 |
| `/api/admin/merchants/batch` | DELETE | fetch | 批量删除商家 |

> ⚠️ **问题**：全部使用fetch直接调用；批量操作API在api/admin.ts中未声明；缺少Authorization头

### 9.9 admin-services/index.vue - 服务管理

**文件**：[views/admin/admin-services/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/admin-services/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/merchants` | GET | fetch | 获取商家列表（辅助数据） |
| `/api/admin/services` | GET | fetch | 获取服务列表 |
| `/api/admin/services/{id}/status` | PUT | fetch | 更新服务状态 |
| `/api/admin/services/{id}` | DELETE | fetch | 删除服务 |
| `/api/admin/services/batch/status` | PUT | fetch | 批量更新服务状态 |
| `/api/admin/services/batch` | DELETE | fetch | 批量删除服务 |

> ⚠️ **问题**：全部使用fetch直接调用；缺少Authorization头

### 9.10 admin-products/index.vue - 商品管理

**文件**：[views/admin/admin-products/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/admin-products/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/merchants` | GET | fetch | 获取商家列表（辅助数据） |
| `/api/admin/products` | GET | fetch | 获取商品列表 |
| `/api/admin/products/{id}/status` | PUT | fetch | 更新商品状态 |
| `/api/admin/products/{id}` | DELETE | fetch | 删除商品 |
| `/api/admin/products/batch/status` | PUT | fetch | 批量更新商品状态 |
| `/api/admin/products/batch` | DELETE | fetch | 批量删除商品 |

> ⚠️ **问题**：全部使用fetch直接调用；缺少Authorization头

### 9.11 admin-reviews/index.vue - 评价管理

**文件**：[views/admin/admin-reviews/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/admin-reviews/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/reviews` | GET | fetch | 获取评价列表 |
| `/api/admin/reviews/{id}` | DELETE | fetch | 删除评价 |

> ⚠️ **问题**：使用fetch直接调用；缺少Authorization头

### 9.12 user-detail/index.vue - 用户详情

**文件**：[views/admin/user-detail/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/user-detail/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/users/{id}` | GET | fetch | 获取用户详情 |
| `/api/admin/users/{id}/status` | PUT | fetch | 更新用户状态（禁用/启用） |

> ⚠️ **问题**：使用fetch直接调用；缺少Authorization头

### 9.13 merchant-detail/index.vue - 商家详情

**文件**：[views/admin/merchant-detail/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/merchant-detail/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/merchants/{id}` | GET | fetch | 获取商家详情 |
| `/api/admin/merchants/{id}/status` | PUT | fetch | 更新商家状态（禁用/启用） |

> ⚠️ **问题**：使用fetch直接调用；缺少Authorization头

### 9.14 merchant-audit/index.vue - 商家审核

**文件**：[views/admin/merchant-audit/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/merchant-audit/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/merchants/pending` | GET | fetch | 获取待审核商家 |
| `/api/admin/merchants/{id}/audit` | PUT | fetch | 审核商家（通过/拒绝） |

> ⚠️ **问题**：使用fetch直接调用；缺少Authorization头

### 9.15 review-audit/index.vue - 评价审核

**文件**：[views/admin/review-audit/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/review-audit/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/reviews/pending` | GET | fetch | 获取待审核评价 |
| `/api/admin/reviews/{id}/audit` | PUT | fetch | 审核评价（通过/违规） |
| `/api/admin/reviews/{id}` | DELETE | fetch | 删除评价 |

> ⚠️ **问题**：使用fetch直接调用；获取待审核评价的路径与api/admin.ts中声明的`/api/admin/reviews/audit`不一致；审核评价的路径与api/admin.ts中声明的`/api/admin/reviews/{id}/approve`和`/api/admin/reviews/{id}/violation`不一致；缺少Authorization头

### 9.16 product-manage/index.vue - 商品详情管理

**文件**：[views/admin/product-manage/index.vue](file:///d:/j/cg/cg/petshop-vue/src/views/admin/product-manage/index.vue)

| 路径 | 方法 | 调用方式 | 说明 |
|------|------|----------|------|
| `/api/admin/merchants` | GET | fetch | 获取商家列表 |
| `/api/admin/products` | GET | fetch | 获取商品列表 |
| `/api/admin/products/{id}/status` | PUT | fetch | 更新商品状态 |
| `/api/admin/products/{id}` | DELETE | fetch | 删除商品 |

> ⚠️ **问题**：使用fetch直接调用；缺少Authorization头

---

## 10. 问题汇总

### 10.1 严重问题

| 编号 | 问题 | 涉及文件 | 说明 |
|------|------|----------|------|
| S1 | 登录接口参数不一致 | Login.vue、merchant/Login.vue | 页面传参`{phone, password, role}`，api定义`{username, password}`，字段名不匹配 |
| S2 | 直接fetch调用缺少认证头 | 多个admin页面 | 使用原生fetch的PUT/DELETE请求未添加Authorization头，可能导致401错误 |
| S3 | 评价审核API路径不一致 | review-audit/index.vue vs api/admin.ts | 页面用`/api/admin/reviews/pending`和`/api/admin/reviews/{id}/audit`，api声明`/api/admin/reviews/audit`和`/api/admin/reviews/{id}/approve` |

### 10.2 中等问题

| 编号 | 问题 | 涉及文件 | 说明 |
|------|------|----------|------|
| M1 | 更新用户信息参数不一致 | profile-edit/index.vue | 页面传参`{nickname, gender, birthday}`，api定义`{username, password}` |
| M2 | 重复API函数定义 | user.ts | `createOrder`和`purchaseProduct`路径和参数完全相同 |
| M3 | 重复API函数定义 | merchant.ts | `getMerchantOrders`和`getMerchantAppointments`路径完全相同 |
| M4 | 重复API函数定义 | merchant.ts | `updateOrderStatus`和`updateAppointmentStatus`路径和参数完全相同 |
| M5 | 重复API函数定义 | admin.ts | `getAllProducts`和`getAllProductsForAdmin`路径完全相同 |
| M6 | 同路径不同参数/返回类型 | user.ts | `getUserReviews`和`getUserReviewsList`路径相同但参数和返回类型不同 |
| M7 | 未在api/中声明的API | merchant-reviews/index.vue | `/api/merchant/reviews/statistics`未在api/merchant.ts中声明 |
| M8 | 未在api/中声明的API | admin-dashboard/index.vue | 3个dashboard子API未在api/admin.ts中声明 |
| M9 | 未在api/中声明的API | admin-merchants/index.vue | 批量操作API未在api/admin.ts中声明 |

### 10.3 轻微问题

| 编号 | 问题 | 涉及文件 | 说明 |
|------|------|----------|------|
| L1 | 大量admin页面使用fetch直接调用 | 多个admin页面 | 应统一使用api/封装，保持一致性 |
| L2 | 登录页面使用axios直接调用 | Login.vue、Register.vue | 应统一使用api/封装 |
| L3 | 类型定义使用any | merchant.ts | `getMerchantProductsPaged`和`getMerchantReviews`返回类型为any |
| L4 | MerchantDetail类型未定义 | admin.ts | `getMerchantDetailById`返回`MerchantDetail`但该类型未在文件中定义 |
| L5 | 响应数据结构不一致 | 多处 | 部分API返回`{data, total}`，部分返回`{list, total}`，部分直接返回数组 |

### 10.4 API路径汇总统计

| 模块 | API数量 | api/目录声明 | 直接调用 |
|------|---------|-------------|----------|
| Auth | 9 | 9 | 3（重复） |
| User - 宠物 | 5 | 5 | 0 |
| User - 预约 | 5 | 5 | 0 |
| User - 服务 | 4 | 4 | 0 |
| User - 商家 | 7 | 7 | 0 |
| User - 收藏 | 9 | 9 | 0 |
| User - 商品 | 4 | 4 | 0 |
| User - 购物车 | 5 | 5 | 0 |
| User - 订单 | 13 | 13 | 0 |
| User - 地址 | 5 | 5 | 0 |
| User - 评价 | 5 | 5 | 0 |
| User - 首页 | 3 | 3 | 0 |
| Merchant - 信息 | 6 | 6 | 0 |
| Merchant - Dashboard | 3 | 3 | 0 |
| Merchant - 服务 | 7 | 7 | 0 |
| Merchant - 商品 | 9 | 9 | 0 |
| Merchant - 预约 | 2 | 2 | 0 |
| Merchant - 商品订单 | 3 | 3 | 0 |
| Merchant - 评价 | 4 | 3 | 1（未声明） |
| Merchant - 分类 | 7 | 7 | 0 |
| Merchant - 统计 | 4 | 4 | 0 |
| Merchant - 设置 | 2 | 2 | 0 |
| Admin - Dashboard | 4 | 1 | 3（未声明） |
| Admin - 用户 | 6 | 6 | 5（重复fetch） |
| Admin - 商家 | 8 | 6 | 7（重复fetch+2未声明） |
| Admin - 服务 | 5 | 5 | 5（重复fetch） |
| Admin - 商品 | 5 | 5 | 5（重复fetch） |
| Admin - 评价 | 5 | 5 | 3（重复fetch） |
| Admin - 公告 | 7 | 7 | 0 |
| Admin - 店铺审核 | 2 | 2 | 0 |
| Admin - 系统 | 2 | 2 | 0 |
| Admin - 日志 | 3 | 3 | 0 |
| Admin - 角色 | 5 | 5 | 0 |
| Admin - 活动 | 5 | 5 | 0 |
| Admin - 任务 | 5 | 5 | 0 |
| Search | 5 | 5 | 0 |
| Notification | 7 | 7 | 0 |
| **合计** | **184** | **168** | **42（含重复调用）** |

> 注：直接调用统计包含对已声明API的重复调用和未声明API的调用

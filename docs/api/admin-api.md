# 平台端API接口文档

## 概述

本文档描述了宠物服务平台平台端（管理员端）的API接口。所有接口都需要管理员身份认证。

### 基础信息

- **基础路径**: `/api/admin`
- **认证方式**: Session认证（登录后自动获取JSESSIONID Cookie）
- **内容类型**: `application/json`

### 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问（未登录或非管理员） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 用户管理

### 1.1 获取用户列表

获取系统中所有用户的列表。

- **接口路径**: `GET /api/admin/users`
- **认证**: 需要

#### 响应示例

```json
[
  {
    "id": 1,
    "username": "user1",
    "email": "user1@example.com",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

### 1.2 删除用户

根据用户ID删除指定用户。

- **接口路径**: `DELETE /api/admin/users/{id}`
- **认证**: 需要

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Integer | 是 | 用户ID |

#### 响应

- **204 No Content**: 删除成功
- **401 Unauthorized**: 未授权

---

## 2. 商家管理

### 2.1 获取商家列表

获取系统中所有商家的列表。

- **接口路径**: `GET /api/admin/merchants`
- **认证**: 需要

#### 响应示例

```json
[
  {
    "id": 1,
    "name": "宠物乐园",
    "contactPerson": "张经理",
    "phone": "13900139000",
    "email": "merchant@example.com",
    "address": "北京市朝阳区xxx",
    "logo": "https://example.com/logo.jpg",
    "status": "approved",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

### 2.2 获取商家详情

根据商家ID获取商家详细信息，包括服务、商品、订单、评价等。

- **接口路径**: `GET /api/admin/merchants/{id}`
- **认证**: 需要

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Integer | 是 | 商家ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "宠物乐园",
    "contactPerson": "张经理",
    "phone": "13900139000",
    "email": "merchant@example.com",
    "address": "北京市朝阳区xxx",
    "logo": "https://example.com/logo.jpg",
    "status": "approved",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00",
    "services": [],
    "products": [],
    "appointments": [],
    "reviews": [],
    "serviceCount": 10,
    "productCount": 20,
    "appointmentCount": 100,
    "reviewCount": 50,
    "averageRating": 4.5
  }
}
```

### 2.3 更新商家状态

更新指定商家的状态。

- **接口路径**: `PUT /api/admin/merchants/{id}/status`
- **认证**: 需要

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Integer | 是 | 商家ID |

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | 是 | 商家状态（approved/rejected/pending） |

### 2.4 删除商家

根据商家ID删除指定商家。

- **接口路径**: `DELETE /api/admin/merchants/{id}`
- **认证**: 需要

#### 响应

- **204 No Content**: 删除成功

### 2.5 批量更新商家状态

批量更新多个商家的状态。

- **接口路径**: `PUT /api/admin/merchants/batch/status`
- **认证**: 需要

#### 请求体

```json
{
  "ids": [1, 2, 3],
  "status": "approved"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "Successfully updated 3 merchants",
  "data": {
    "updatedCount": 3
  }
}
```

### 2.6 批量删除商家

批量删除多个商家。

- **接口路径**: `DELETE /api/admin/merchants/batch`
- **认证**: 需要

#### 请求体

```json
{
  "ids": [1, 2, 3]
}
```

### 2.7 获取待审核商家列表

分页获取待审核状态的商家列表。

- **接口路径**: `GET /api/admin/merchants/pending`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| pageSize | int | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键字 |

### 2.8 审核商家

审核商家入驻申请，通过或拒绝。

- **接口路径**: `PUT /api/admin/merchants/{id}/audit`
- **认证**: 需要

#### 请求体

```json
{
  "status": "approved",
  "reason": "资质审核通过"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | 是 | 审核状态（approved/rejected） |
| reason | String | 否 | 审核原因/备注 |

---

## 3. 仪表盘

### 3.1 获取仪表盘统计数据

获取平台首页统计数据。

- **接口路径**: `GET /api/admin/dashboard`
- **认证**: 需要

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userCount": 1000,
    "merchantCount": 50,
    "orderCount": 5000,
    "serviceCount": 200
  }
}
```

### 3.2 获取最近注册用户

分页获取最近注册的用户列表。

- **接口路径**: `GET /api/admin/dashboard/recent-users`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| pageSize | int | 否 | 10 | 每页数量 |

### 3.3 获取待审核商家（仪表盘）

- **接口路径**: `GET /api/admin/dashboard/pending-merchants`
- **认证**: 需要

### 3.4 获取公告列表（仪表盘）

- **接口路径**: `GET /api/admin/dashboard/announcements`
- **认证**: 需要

---

## 4. 商品管理

### 4.1 获取商品列表

分页获取商品列表，支持多条件筛选。

- **接口路径**: `GET /api/admin/products`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| pageSize | int | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键字 |
| status | String | 否 | - | 商品状态（enabled/disabled） |
| merchantId | Integer | 否 | - | 商家ID |
| category | String | 否 | - | 商品分类 |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "data": [
      {
        "id": 1,
        "name": "宠物狗粮",
        "description": "优质狗粮",
        "price": 99.00,
        "stock": 100,
        "image": "https://example.com/product.jpg",
        "status": "enabled",
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "page": 0,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

### 4.2 获取商品详情

- **接口路径**: `GET /api/admin/products/{id}`
- **认证**: 需要

### 4.3 更新商品信息

- **接口路径**: `PUT /api/admin/products/{id}`
- **认证**: 需要

#### 请求体

```json
{
  "name": "宠物狗粮",
  "description": "优质狗粮",
  "price": 99.00,
  "stock": 100,
  "image": "https://example.com/product.jpg",
  "status": "enabled"
}
```

### 4.4 更新商品状态

- **接口路径**: `PUT /api/admin/products/{id}/status`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | 是 | 商品状态（enabled/disabled） |

### 4.5 删除商品

- **接口路径**: `DELETE /api/admin/products/{id}`
- **认证**: 需要

### 4.6 批量更新商品状态

- **接口路径**: `PUT /api/admin/products/batch/status`
- **认证**: 需要

#### 请求体

```json
{
  "ids": [1, 2, 3],
  "status": "enabled"
}
```

### 4.7 批量删除商品

- **接口路径**: `DELETE /api/admin/products/batch`
- **认证**: 需要

---

## 5. 评价管理

### 5.1 获取评价列表

分页获取评价列表，支持多条件筛选。

- **接口路径**: `GET /api/admin/reviews`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| pageSize | int | 否 | 10 | 每页数量 |
| rating | Integer | 否 | - | 评分筛选（1-5） |
| keyword | String | 否 | - | 搜索关键字 |
| merchantId | Integer | 否 | - | 商家ID |
| status | String | 否 | - | 评价状态（approved/rejected/pending） |

### 5.2 获取待审核评价列表

- **接口路径**: `GET /api/admin/reviews/pending`
- **认证**: 需要

### 5.3 审核评价

- **接口路径**: `PUT /api/admin/reviews/{id}/audit`
- **认证**: 需要

#### 请求体

```json
{
  "status": "approved",
  "reason": "内容合规"
}
```

### 5.4 删除评价

- **接口路径**: `DELETE /api/admin/reviews/{id}`
- **认证**: 需要

### 5.5 批量更新评价状态

- **接口路径**: `PUT /api/admin/reviews/batch/status`
- **认证**: 需要

### 5.6 批量删除评价

- **接口路径**: `DELETE /api/admin/reviews/batch`
- **认证**: 需要

---

## 6. 系统配置

### 6.1 获取系统配置

获取网站基本配置信息。

- **接口路径**: `GET /api/admin/system/config`
- **认证**: 需要

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "siteName": "宠物服务平台",
    "logo": "https://example.com/logo.jpg",
    "contactEmail": "support@petshop.com",
    "contactPhone": "400-123-4567",
    "icpNumber": "京ICP备12345678号",
    "siteDescription": "专业的宠物服务平台",
    "siteKeywords": "宠物,宠物服务,宠物医院",
    "footerText": "© 2024 宠物服务平台"
  }
}
```

### 6.2 更新系统配置

- **接口路径**: `PUT /api/admin/system/config`
- **认证**: 需要

### 6.3 获取系统设置

获取邮件、短信、支付、上传等系统设置。

- **接口路径**: `GET /api/admin/system/settings`
- **认证**: 需要

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "emailSettings": {
      "smtp": "smtp.example.com",
      "port": 465,
      "username": "noreply@example.com",
      "from": "noreply@example.com"
    },
    "smsSettings": {
      "provider": "aliyun",
      "signName": "宠物服务平台"
    },
    "wechatPaySettings": {
      "appId": "wx1234567890",
      "mchId": "1234567890"
    },
    "alipaySettings": {
      "appId": "2021001234567890",
      "notifyUrl": "https://example.com/notify"
    },
    "uploadSettings": {
      "path": "/uploads",
      "maxFileSize": 10485760,
      "allowedFileTypes": "jpg,jpeg,png,gif,pdf"
    }
  }
}
```

### 6.4 更新系统设置

- **接口路径**: `PUT /api/admin/system/settings`
- **认证**: 需要

---

## 7. 活动管理

### 7.1 获取活动列表

分页获取活动列表。

- **接口路径**: `GET /api/admin/activities`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| pageSize | int | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键字 |
| type | String | 否 | - | 活动类型 |
| status | String | 否 | - | 活动状态（enabled/disabled） |
| startDate | String | 否 | - | 开始日期（格式：yyyy-MM-dd） |
| endDate | String | 否 | - | 结束日期（格式：yyyy-MM-dd） |

### 7.2 创建活动

- **接口路径**: `POST /api/admin/activities`
- **认证**: 需要

### 7.3 更新活动

- **接口路径**: `PUT /api/admin/activities/{id}`
- **认证**: 需要

### 7.4 删除活动

- **接口路径**: `DELETE /api/admin/activities/{id}`
- **认证**: 需要

### 7.5 更新活动状态

- **接口路径**: `PUT /api/admin/activities/{id}/status`
- **认证**: 需要

---

## 8. 店铺审核

### 8.1 获取待审核店铺列表

- **接口路径**: `GET /api/admin/shops/pending`
- **认证**: 需要

### 8.2 审核店铺

- **接口路径**: `PUT /api/admin/shops/{id}/audit`
- **认证**: 需要

#### 请求体

```json
{
  "status": "approved",
  "reason": "资质审核通过"
}
```

---

## 9. 定时任务管理

### 9.1 获取任务列表

- **接口路径**: `GET /api/admin/tasks`
- **认证**: 需要

#### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| pageSize | int | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键字 |
| type | String | 否 | - | 任务类型 |
| status | String | 否 | - | 任务状态 |

### 9.2 创建定时任务

- **接口路径**: `POST /api/admin/tasks`
- **认证**: 需要

### 9.3 更新定时任务

- **接口路径**: `PUT /api/admin/tasks/{id}`
- **认证**: 需要

### 9.4 删除定时任务

- **接口路径**: `DELETE /api/admin/tasks/{id}`
- **认证**: 需要

### 9.5 执行定时任务

手动执行指定的定时任务。

- **接口路径**: `POST /api/admin/tasks/{id}/execute`
- **认证**: 需要

---

## Swagger UI 访问

启动应用后，可以通过以下地址访问 Swagger UI：

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 注意事项

1. 所有接口都需要管理员身份认证，请在请求时携带有效的 Session Cookie
2. 分页参数 `page` 从 0 开始计数
3. 日期时间格式为 ISO 8601 标准（如：`2024-01-01T10:00:00`）
4. 批量操作时，如果部分操作失败，会返回错误信息，但已成功的操作不会回滚

# 商家端真实API使用文档

## 1. 概述

本文档系统性梳理和总结了商家端20个页面所实际使用到的真实后端API，包括API的端点URL、请求方法、主要请求参数及响应数据结构等关键信息。

## 2. API基础信息

### 2.1 基础URL
- 本地开发环境：`http://localhost:8080/api`
- 生产环境：根据实际部署情况确定

### 2.2 认证方式
- 使用Bearer Token认证
- Token存储在localStorage或sessionStorage中，键名为`merchant_token`
- 请求头格式：`Authorization: <token>`

### 2.3 响应格式

```json
{
  "code": 200, // 状态码，200表示成功
  "message": "操作成功", // 消息提示
  "data": {} // 响应数据
}
```

## 3. 商家信息API

### 3.1 获取商家信息
- **端点**：`/merchant/info`
- **方法**：GET
- **参数**：无
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "name": "宠物乐园",
    "contact_person": "张三",
    "phone": "13800138000",
    "email": "merchant@example.com",
    "address": "北京市朝阳区",
    "logo": "http://example.com/logo.png",
    "status": "approved",
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 3.2 更新商家信息
- **端点**：`/merchant/info`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | name | string | 是 | 商家名称 |
  | contact_person | string | 是 | 联系人 |
  | phone | string | 是 | 联系电话 |
  | email | string | 是 | 邮箱 |
  | address | string | 是 | 地址 |
  | logo | string | 否 | 商家Logo |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "name": "宠物乐园",
    "contact_person": "张三",
    "phone": "13800138000",
    "email": "merchant@example.com",
    "address": "北京市朝阳区",
    "logo": "http://example.com/logo.png",
    "status": "approved",
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

## 4. 服务管理API

### 4.1 获取服务列表
- **端点**：`/merchant/services`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | page | number | 是 | 页码（从0开始） |
  | size | number | 是 | 每页条数 |
  | name | string | 否 | 服务名称关键词 |
  | status | string | 否 | 服务状态 |
- **响应数据结构**：
  ```json
  {
    "content": [
      {
        "id": 1,
        "merchant_id": 1,
        "name": "宠物洗澡",
        "description": "专业宠物洗澡服务",
        "price": 50.00,
        "duration": 60,
        "image": "http://example.com/service1.png",
        "status": "enabled",
        "created_at": "2023-01-01T00:00:00Z",
        "updated_at": "2023-01-01T00:00:00Z"
      }
    ],
    "totalElements": 10,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
  ```

### 4.2 添加服务
- **端点**：`/merchant/services`
- **方法**：POST
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | name | string | 是 | 服务名称 |
  | description | string | 否 | 服务描述 |
  | price | number | 是 | 服务价格 |
  | duration | number | 是 | 服务时长（分钟） |
  | image | string | 否 | 服务图片 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "merchant_id": 1,
    "name": "宠物洗澡",
    "description": "专业宠物洗澡服务",
    "price": 50.00,
    "duration": 60,
    "image": "http://example.com/service1.png",
    "status": "enabled",
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 4.3 更新服务
- **端点**：`/merchant/services/{id}`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | name | string | 是 | 服务名称 |
  | description | string | 否 | 服务描述 |
  | price | number | 是 | 服务价格 |
  | duration | number | 是 | 服务时长（分钟） |
  | image | string | 否 | 服务图片 |
  | status | string | 否 | 服务状态 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "merchant_id": 1,
    "name": "宠物洗澡",
    "description": "专业宠物洗澡服务",
    "price": 50.00,
    "duration": 60,
    "image": "http://example.com/service1.png",
    "status": "enabled",
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 4.4 删除服务
- **端点**：`/merchant/services/{id}`
- **方法**：DELETE
- **参数**：无
- **响应数据结构**：
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": null
  }
  ```

## 5. 商品管理API

### 5.1 获取商品列表
- **端点**：`/merchant/products`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | page | number | 是 | 页码（从0开始） |
  | size | number | 是 | 每页条数 |
  | name | string | 否 | 商品名称关键词 |
  | status | string | 否 | 商品状态 |
- **响应数据结构**：
  ```json
  {
    "content": [
      {
        "id": 1,
        "merchant_id": 1,
        "name": "宠物粮食",
        "description": "高品质宠物粮食",
        "price": 100.00,
        "stock": 100,
        "image": "http://example.com/product1.png",
        "status": "enabled",
        "created_at": "2023-01-01T00:00:00Z",
        "updated_at": "2023-01-01T00:00:00Z"
      }
    ],
    "totalElements": 10,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
  ```

### 5.2 添加商品
- **端点**：`/merchant/products`
- **方法**：POST
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | name | string | 是 | 商品名称 |
  | description | string | 否 | 商品描述 |
  | price | number | 是 | 商品价格 |
  | stock | number | 是 | 商品库存 |
  | image | string | 否 | 商品图片 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "merchant_id": 1,
    "name": "宠物粮食",
    "description": "高品质宠物粮食",
    "price": 100.00,
    "stock": 100,
    "image": "http://example.com/product1.png",
    "status": "enabled",
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 5.3 更新商品
- **端点**：`/merchant/products/{id}`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | name | string | 是 | 商品名称 |
  | description | string | 否 | 商品描述 |
  | price | number | 是 | 商品价格 |
  | stock | number | 是 | 商品库存 |
  | image | string | 否 | 商品图片 |
  | status | string | 否 | 商品状态 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "merchant_id": 1,
    "name": "宠物粮食",
    "description": "高品质宠物粮食",
    "price": 100.00,
    "stock": 100,
    "image": "http://example.com/product1.png",
    "status": "enabled",
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 5.4 删除商品
- **端点**：`/merchant/products/{id}`
- **方法**：DELETE
- **参数**：无
- **响应数据结构**：
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": null
  }
  ```

## 6. 订单管理API

### 6.1 获取预约订单列表
- **端点**：`/merchant/appointments`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | page | number | 是 | 页码（从0开始） |
  | size | number | 是 | 每页条数 |
  | status | string | 否 | 订单状态 |
  | start_date | string | 否 | 开始日期 |
  | end_date | string | 否 | 结束日期 |
- **响应数据结构**：
  ```json
  {
    "content": [
      {
        "id": 1,
        "user_id": 1,
        "merchant_id": 1,
        "service_id": 1,
        "pet_id": 1,
        "appointment_time": "2023-01-01T10:00:00Z",
        "status": "pending",
        "total_price": 50.00,
        "notes": "需要特殊护理",
        "created_at": "2023-01-01T00:00:00Z",
        "updated_at": "2023-01-01T00:00:00Z",
        "user": {
          "id": 1,
          "username": "用户1"
        },
        "pet": {
          "id": 1,
          "name": "旺财"
        },
        "service": {
          "id": 1,
          "name": "宠物洗澡"
        }
      }
    ],
    "totalElements": 10,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
  ```

### 6.2 更新预约订单状态
- **端点**：`/merchant/appointments/{id}/status`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | status | string | 是 | 订单状态 |
  | reject_reason | string | 否 | 拒单原因 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "status": "confirmed",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 6.3 获取商品订单列表
- **端点**：`/merchant/product-orders`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | page | number | 是 | 页码（从0开始） |
  | size | number | 是 | 每页条数 |
  | status | string | 否 | 订单状态 |
  | start_date | string | 否 | 开始日期 |
  | end_date | string | 否 | 结束日期 |
- **响应数据结构**：
  ```json
  {
    "content": [
      {
        "id": 1,
        "user_id": 1,
        "merchant_id": 1,
        "total_price": 200.00,
        "status": "pending",
        "shipping_address": "北京市朝阳区",
        "created_at": "2023-01-01T00:00:00Z",
        "updated_at": "2023-01-01T00:00:00Z",
        "user": {
          "id": 1,
          "username": "用户1"
        },
        "items": [
          {
            "id": 1,
            "order_id": 1,
            "product_id": 1,
            "quantity": 2,
            "price": 100.00,
            "product": {
              "id": 1,
              "name": "宠物粮食"
            }
          }
        ]
      }
    ],
    "totalElements": 10,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
  ```

### 6.4 更新商品订单状态
- **端点**：`/merchant/product-orders/{id}/status`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | status | string | 是 | 订单状态 |
  | reject_reason | string | 否 | 拒单原因 |
  | tracking_number | string | 否 | 物流单号 |
  | logistics_company | string | 否 | 物流公司 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "status": "shipped",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

## 7. 评价管理API

### 7.1 获取评价列表
- **端点**：`/merchant/reviews`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | page | number | 是 | 页码（从0开始） |
  | size | number | 是 | 每页条数 |
  | rating | number | 否 | 评分 |
  | start_date | string | 否 | 开始日期 |
  | end_date | string | 否 | 结束日期 |
- **响应数据结构**：
  ```json
  {
    "content": [
      {
        "id": 1,
        "user_id": 1,
        "merchant_id": 1,
        "service_id": 1,
        "appointment_id": 1,
        "rating": 5,
        "comment": "服务非常好",
        "reply": "感谢您的评价",
        "created_at": "2023-01-01T00:00:00Z",
        "user": {
          "id": 1,
          "username": "用户1",
          "avatar": "http://example.com/avatar.png"
        },
        "service": {
          "id": 1,
          "name": "宠物洗澡"
        }
      }
    ],
    "totalElements": 10,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
  ```

### 7.2 获取评价统计
- **端点**：`/merchant/reviews/statistics`
- **方法**：GET
- **参数**：无
- **响应数据结构**：
  ```json
  {
    "average_rating": 4.5,
    "rating_distribution": {
      "5": 5,
      "4": 3,
      "3": 1,
      "2": 0,
      "1": 1
    }
  }
  ```

### 7.3 回复评价
- **端点**：`/merchant/reviews/{id}/reply`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | reply | string | 是 | 回复内容 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "reply": "感谢您的评价",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 7.4 删除评价
- **端点**：`/merchant/reviews/{id}`
- **方法**：DELETE
- **参数**：无
- **响应数据结构**：
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": null
  }
  ```

## 8. 店铺设置API

### 8.1 获取店铺设置
- **端点**：`/merchant/settings`
- **方法**：GET
- **参数**：无
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "merchant_id": 1,
    "is_open": true,
    "notification_settings": {
      "appointment": true,
      "order": true,
      "review": true,
      "sms": true,
      "email": true
    },
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

### 8.2 更新店铺设置
- **端点**：`/merchant/settings`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | is_open | boolean | 是 | 是否营业 |
  | notification_settings | object | 是 | 通知设置 |
- **响应数据结构**：
  ```json
  {
    "id": 1,
    "merchant_id": 1,
    "is_open": true,
    "notification_settings": {
      "appointment": true,
      "order": true,
      "review": true,
      "sms": true,
      "email": true
    },
    "created_at": "2023-01-01T00:00:00Z",
    "updated_at": "2023-01-01T00:00:00Z"
  }
  ```

## 9. 统计报表API

### 9.1 获取预约统计
- **端点**：`/merchant/statistics/appointments`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | start_date | string | 是 | 开始日期 |
  | end_date | string | 是 | 结束日期 |
  | interval | string | 是 | 统计间隔（day/week/month） |
- **响应数据结构**：
  ```json
  {
    "overview": {
      "total_appointments": 100,
      "completed_appointments": 80,
      "cancelled_appointments": 20,
      "cancellation_rate": 0.2,
      "growth_rate": 0.1
    },
    "trends": [
      {
        "date": "2023-01-01",
        "appointments": 10,
        "completed": 8,
        "cancelled": 2,
        "completion_rate": 0.8,
        "total_amount": 500.00
      }
    ],
    "service_stats": [
      {
        "service_id": 1,
        "service_name": "宠物洗澡",
        "appointment_count": 50,
        "percentage": 0.5
      }
    ],
    "time_stats": [
      {
        "time_slot": "10:00-12:00",
        "appointment_count": 30
      }
    ]
  }
  ```

### 9.2 导出预约统计
- **端点**：`/merchant/statistics/appointments/export`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | start_date | string | 是 | 开始日期 |
  | end_date | string | 是 | 结束日期 |
- **响应数据结构**：Excel文件

### 9.3 获取营收统计
- **端点**：`/merchant/statistics/revenue`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | start_date | string | 是 | 开始日期 |
  | end_date | string | 是 | 结束日期 |
  | interval | string | 是 | 统计间隔（day/week/month） |
- **响应数据结构**：
  ```json
  {
    "overview": {
      "total_revenue": 10000.00,
      "total_orders": 200,
      "average_order_value": 50.00,
      "growth_rate": 0.15
    },
    "trends": [
      {
        "date": "2023-01-01",
        "service_orders": 10,
        "service_revenue": 500.00,
        "product_orders": 5,
        "product_revenue": 300.00,
        "total_revenue": 800.00
      }
    ],
    "revenue_composition": {
      "service_revenue": 6000.00,
      "product_revenue": 4000.00
    },
    "top_services": [
      {
        "id": 1,
        "name": "宠物洗澡",
        "revenue": 3000.00,
        "count": 60
      }
    ],
    "top_products": [
      {
        "id": 1,
        "name": "宠物粮食",
        "revenue": 2000.00,
        "count": 20
      }
    ]
  }
  ```

### 9.4 导出营收统计
- **端点**：`/merchant/statistics/revenue/export`
- **方法**：GET
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | start_date | string | 是 | 开始日期 |
  | end_date | string | 是 | 结束日期 |
- **响应数据结构**：Excel文件

## 10. 账户管理API

### 10.1 修改密码
- **端点**：`/merchant/account/password`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | old_password | string | 是 | 旧密码 |
  | new_password | string | 是 | 新密码 |
- **响应数据结构**：
  ```json
  {
    "code": 200,
    "message": "密码修改成功",
    "data": null
  }
  ```

### 10.2 绑定手机号
- **端点**：`/merchant/account/phone`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | phone | string | 是 | 新手机号 |
  | code | string | 是 | 验证码 |
- **响应数据结构**：
  ```json
  {
    "code": 200,
    "message": "手机号绑定成功",
    "data": null
  }
  ```

### 10.3 绑定邮箱
- **端点**：`/merchant/account/email`
- **方法**：PUT
- **参数**：
  | 参数名 | 类型 | 必选 | 描述 |
  |-------|------|------|------|
  | email | string | 是 | 新邮箱 |
  | code | string | 是 | 验证码 |
- **响应数据结构**：
  ```json
  {
    "code": 200,
    "message": "邮箱绑定成功",
    "data": null
  }
  ```

## 11. 错误处理

### 11.1 常见错误码
- **401**：未授权，请重新登录
- **403**：拒绝访问
- **404**：请求资源不存在
- **500**：服务器错误

### 11.2 错误处理示例

```typescript
// 在API请求中添加错误处理
try {
  const result = await getMerchantServices({ page: 0, size: 10 });
  // 处理成功响应
} catch (error) {
  // 处理错误
  console.error('获取服务列表失败:', error);
  ElMessage.error('获取服务列表失败，请稍后重试');
}
```

## 12. 使用示例

### 12.1 获取服务列表

```typescript
import { getMerchantServices } from '@/api/merchant';

// 获取服务列表
const fetchServices = async () => {
  try {
    loading.value = true;
    const result = await getMerchantServices({
      page: pagination.value.current - 1, // 后端使用0-based分页
      size: pagination.value.size,
      name: searchForm.value.name,
      status: searchForm.value.status
    });
    services.value = result.content;
    pagination.value.total = result.totalElements;
  } catch (error) {
    console.error('获取服务列表失败:', error);
    ElMessage.error('获取服务列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};
```

### 12.2 添加服务

```typescript
import { addService } from '@/api/merchant';

// 添加服务
const handleAddService = async () => {
  try {
    await addService({
      name: form.value.name,
      description: form.value.description,
      price: form.value.price,
      duration: form.value.duration,
      image: form.value.image
    });
    ElMessage.success('服务添加成功');
    // 刷新服务列表
    fetchServices();
  } catch (error) {
    console.error('添加服务失败:', error);
    ElMessage.error('添加服务失败，请稍后重试');
  }
};
```

## 13. 总结

本文档详细梳理了商家端20个页面所使用的真实后端API，包括API的端点URL、请求方法、主要请求参数及响应数据结构等关键信息。通过本文档，前端开发人员可以了解如何正确集成和使用这些API，确保页面功能的正常运行。

在使用这些API时，需要注意以下几点：

1. 确保请求头中携带正确的认证token
2. 注意后端API的分页参数（从0开始）
3. 正确处理后端API的响应格式
4. 添加适当的错误处理和加载状态提示
5. 遵循项目的代码规范和最佳实践

通过正确集成和使用这些API，商家端页面将能够与真实后端服务进行正确交互，提供更好的用户体验。
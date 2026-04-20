# 商家端 API 审计报告

**审计日期**: 2026-04-20  
**审计范围**: MerchantApiController.java, MerchantController.java, merchant.ts  
**审计人员**: AI Assistant

---

## 一、审计概述

本次审计对商家端所有 API 接口进行了全面检查，包括 HTTP 方法正确性、URL 路径规范性、请求参数验证、响应格式一致性、错误处理完整性、状态码正确性以及前端兼容性。

### 审计文件
- 后端控制器: `src/main/java/com/petshop/controller/api/MerchantApiController.java`
- 后端控制器: `src/main/java/com/petshop/controller/MerchantController.java`
- 前端 API: `cg-vue/src/api/merchant.ts`
- 响应 DTO: `src/main/java/com/petshop/dto/ApiResponse.java`

---

## 二、发现的问题

### 2.1 严重问题 (P0) - 前后端参数传递不匹配

**问题描述**: 前端使用 JSON Body 发送参数，但后端使用 `@RequestParam` 接收参数，导致接口无法正常工作。

| 接口路径 | HTTP 方法 | 前端发送方式 | 后端接收方式 | 影响 |
|---------|----------|-------------|-------------|------|
| `/api/merchant/appointments/{id}/status` | PUT | JSON Body | @RequestParam | 无法更新预约状态 |
| `/api/merchant/orders/{id}/status` | PUT | JSON Body | @RequestParam | 无法更新订单状态 |
| `/api/merchant/product-orders/{id}/status` | PUT | JSON Body | @RequestParam | 无法更新商品订单状态 |
| `/api/merchant/products/{id}/status` | PUT | JSON Body | @RequestParam | 无法更新商品状态 |
| `/api/merchant/categories/{id}/status` | PUT | JSON Body | @RequestParam | 无法更新分类状态 |

**前端代码示例**:
```typescript
// cg-vue/src/api/merchant.ts
export const updateAppointmentStatus = (id: number, status: string, rejectReason?: string) => {
  return request.put<Appointment>(`/api/merchant/appointments/${id}/status`, { status, rejectReason })
}
```

**后端代码示例**:
```java
// MerchantApiController.java
@PutMapping("/appointments/{id}/status")
public ResponseEntity<ApiResponse<Appointment>> updateAppointmentStatus(
        @PathVariable Integer id,
        @RequestParam String status,  // 问题：使用 @RequestParam
        HttpSession session) {
```

**修复建议**: 将后端 `@RequestParam` 改为 `@RequestBody` 接收 JSON 对象，或创建专门的 DTO 类。

---

### 2.2 中等问题 (P1) - 重复接口

**问题描述**: 存在功能重复的接口，增加了维护成本。

| 接口 1 | 接口 2 | 功能描述 |
|--------|--------|---------|
| GET /api/merchant/profile | GET /api/merchant/info | 获取商家信息 |
| PUT /api/merchant/profile | PUT /api/merchant/info | 更新商家信息 |

**修复建议**: 统一使用 `/api/merchant/info` 接口，废弃 `/api/merchant/profile` 接口，或在文档中明确说明两者关系。

---

### 2.3 中等问题 (P1) - HTTP 方法不规范

**问题描述**: 部分接口的 HTTP 方法选择不符合 RESTful 规范。

| 接口路径 | 当前方法 | 建议方法 | 原因 |
|---------|---------|---------|------|
| `/api/merchant/settings/toggle-status` | POST | PUT | 状态更新操作应使用 PUT |
| `/api/merchant/change-password` | POST | PUT | 密码更新操作应使用 PUT |

**修复建议**: 考虑将状态更新操作改为 PUT 方法，或保持现状但在文档中说明原因。

---

### 2.4 低等问题 (P2) - 参数验证不完整

**问题描述**: 部分接口缺少必要的参数验证。

| 接口 | 缺失验证 | 风险 |
|------|---------|------|
| POST /api/merchant/products | 商品名称、价格验证 | 可能创建无效商品 |
| POST /api/merchant/categories | 分类名称验证 | 可能创建空名称分类 |
| POST /api/merchant/bind-phone | 手机号格式验证 | 可能绑定无效手机号 |
| POST /api/merchant/bind-email | 邮箱格式验证 | 可能绑定无效邮箱 |

**修复建议**: 
1. 添加 `@Valid` 注解和验证规则
2. 创建专门的 Request DTO 类并添加验证注解
3. 在 Service 层添加业务验证逻辑

---

### 2.5 低等问题 (P2) - 错误处理不一致

**问题描述**: 部分接口的错误处理方式不一致。

| 问题类型 | 示例接口 | 描述 |
|---------|---------|------|
| 异常捕获过于宽泛 | 多数接口 | 使用 `catch (Exception e)` 捕获所有异常 |
| 错误消息不统一 | 各接口 | 错误消息格式和内容不一致 |

**修复建议**:
1. 使用自定义异常类（如 `BadRequestException`, `ResourceNotFoundException`）
2. 统一错误消息格式
3. 添加错误码常量定义

---

### 2.6 建议改进 (P3) - 响应格式优化

**问题描述**: 分页接口的响应格式可以进一步优化。

**当前格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 1,
    "pageSize": 10,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**建议**: 统一使用 `PageResponse<T>` DTO 类，确保所有分页接口返回格式一致。

---

## 三、接口完整性检查

### 3.1 已实现的接口

| 模块 | 接口数量 | 状态 |
|------|---------|------|
| 商家资料 | 4 | ✅ 完整 |
| 服务管理 | 7 | ✅ 完整 |
| 预约管理 | 5 | ✅ 完整 |
| 订单管理 | 5 | ✅ 完整 |
| 商品管理 | 9 | ✅ 完整 |
| 分类管理 | 7 | ✅ 完整 |
| 评价管理 | 6 | ✅ 完整 |
| 账号管理 | 4 | ✅ 完整 |
| 统计分析 | 5 | ✅ 完整 |
| 店铺设置 | 3 | ✅ 完整 |

### 3.2 前后端接口对应检查

| 前端 API | 后端接口 | 状态 |
|---------|---------|------|
| getMerchantInfo | GET /api/merchant/info | ✅ |
| updateMerchantInfo | PUT /api/merchant/info | ✅ |
| getMerchantServices | GET /api/merchant/services | ✅ |
| addService | POST /api/merchant/services | ✅ |
| updateService | PUT /api/merchant/services/{id} | ✅ |
| deleteService | DELETE /api/merchant/services/{id} | ✅ |
| getServiceById | GET /api/merchant/services/{id} | ✅ |
| batchUpdateServiceStatus | PUT /api/merchant/services/batch/status | ✅ |
| batchDeleteServices | DELETE /api/merchant/services/batch | ✅ |
| getMerchantOrders | GET /api/merchant/orders | ✅ |
| updateOrderStatus | PUT /api/merchant/orders/{id}/status | ⚠️ 参数不匹配 |
| getMerchantProducts | GET /api/merchant/products | ✅ |
| addProduct | POST /api/merchant/products | ✅ |
| updateProduct | PUT /api/merchant/products/{id} | ✅ |
| deleteProduct | DELETE /api/merchant/products/{id} | ✅ |
| getProductById | GET /api/merchant/products/{id} | ✅ |
| getMerchantProductsPaged | GET /api/merchant/products/paged | ✅ |
| updateProductStatus | PUT /api/merchant/products/{id}/status | ⚠️ 参数不匹配 |
| batchUpdateProductStatus | PUT /api/merchant/products/batch/status | ✅ |
| batchDeleteProducts | DELETE /api/merchant/products/batch | ✅ |
| getMerchantAppointments | GET /api/merchant/appointments | ✅ |
| updateAppointmentStatus | PUT /api/merchant/appointments/{id}/status | ⚠️ 参数不匹配 |
| getCategories | GET /api/merchant/categories | ✅ |
| addCategory | POST /api/merchant/categories | ✅ |
| updateCategory | PUT /api/merchant/categories/{id} | ✅ |
| deleteCategory | DELETE /api/merchant/categories/{id} | ✅ |
| updateCategoryStatus | PUT /api/merchant/categories/{id}/status | ⚠️ 参数不匹配 |
| batchUpdateCategoryStatus | PUT /api/merchant/categories/batch/status | ✅ |
| batchDeleteCategories | DELETE /api/merchant/categories/batch | ✅ |
| getMerchantProductOrders | GET /api/merchant/product-orders | ✅ |
| updateProductOrderStatus | PUT /api/merchant/product-orders/{id}/status | ⚠️ 参数不匹配 |
| updateProductOrderLogistics | PUT /api/merchant/product-orders/{id}/logistics | ✅ |
| getMerchantReviews | GET /api/merchant/reviews | ✅ |
| replyReview | PUT /api/merchant/reviews/{id}/reply | ✅ |
| deleteReview | DELETE /api/merchant/reviews/{id} | ✅ |
| getMerchantRevenueStats | GET /api/merchant/revenue-stats | ✅ |
| exportRevenueStats | GET /api/merchant/revenue-stats/export | ✅ |
| getMerchantAppointmentStats | GET /api/merchant/appointment-stats | ✅ |
| exportAppointmentStats | GET /api/merchant/appointment-stats/export | ✅ |
| getMerchantSettings | GET /api/merchant/settings | ✅ |
| updateMerchantSettings | PUT /api/merchant/settings | ✅ |
| changePassword | POST /api/merchant/change-password | ✅ |
| bindPhone | POST /api/merchant/bind-phone | ✅ |
| bindEmail | POST /api/merchant/bind-email | ✅ |
| sendVerifyCode | POST /api/merchant/send-verify-code | ✅ |
| getMerchantDashboard | GET /api/merchant/dashboard | ✅ |
| getRecentOrders | GET /api/merchant/appointments/recent | ✅ |
| getRecentReviews | GET /api/merchant/reviews/recent | ✅ |

---

## 四、状态码使用检查

### 4.1 正确使用的状态码

| 状态码 | 使用场景 | 示例接口 |
|--------|---------|---------|
| 200 OK | 成功响应 | 所有 GET/PUT/DELETE 成功 |
| 201 Created | 资源创建成功 | POST /api/merchant/services |
| 400 Bad Request | 参数错误 | 参数验证失败 |
| 401 Unauthorized | 未认证 | 未登录访问 |
| 403 Forbidden | 无权限 | 操作他人资源 |
| 404 Not Found | 资源不存在 | 资源查找失败 |
| 500 Internal Server Error | 服务器错误 | 异常情况 |

### 4.2 状态码使用建议

| 接口 | 当前状态码 | 建议 |
|------|-----------|------|
| POST /api/merchant/services | 201 | ✅ 正确 |
| POST /api/merchant/products | 201 | ✅ 正确 |
| POST /api/merchant/categories | 201 | ✅ 正确 |
| PUT /api/merchant/*/status | 200 | ✅ 正确 |

---

## 五、安全性检查

### 5.1 认证检查

✅ 所有接口都进行了 Session 认证检查  
✅ 未认证请求返回 401 状态码

### 5.2 授权检查

✅ 所有资源操作都验证了所有权  
✅ 跨商家操作返回 403 状态码

### 5.3 安全建议

1. **密码修改**: 建议添加旧密码验证
2. **验证码**: 建议添加验证码有效期和频率限制
3. **敏感操作**: 建议添加操作日志记录

---

## 六、修复优先级建议

### P0 - 立即修复
1. 修复前后端参数传递不匹配问题（5个接口）

### P1 - 尽快修复
1. 清理重复接口
2. 规范 HTTP 方法使用

### P2 - 计划修复
1. 完善参数验证
2. 统一错误处理

### P3 - 持续改进
1. 优化响应格式
2. 完善接口文档

---

## 七、总结

### 审计结果统计

| 问题等级 | 数量 | 状态 |
|---------|------|------|
| P0 严重 | 1 | 需立即修复 |
| P1 中等 | 2 | 需尽快修复 |
| P2 低等 | 2 | 需计划修复 |
| P3 建议 | 1 | 持续改进 |

### 总体评价

商家端 API 整体设计合理，接口覆盖完整，认证授权机制健全。主要问题集中在前后端参数传递方式不匹配，这是一个严重的兼容性问题，需要立即修复。其他问题属于优化改进范畴，可以根据优先级逐步解决。

---

**审计完成时间**: 2026-04-20

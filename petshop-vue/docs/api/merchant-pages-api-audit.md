# 商家端页面 API 审计报告

> 审计时间：2026-04-21
> 审计范围：cg-vue/src/views/merchant/ 目录下 20 个页面
> 数据来源：静态代码分析（读取 .vue 文件及 @/api/merchant.ts、@/api/auth.ts）

---

## 1. home/index.vue

- **页面名称**：商家后台首页（旧版/简化版）
- **文件路径**：`cg-vue/src/views/merchant/home/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/dashboard` | GET | 无 | `DashboardStats` | 正常 |
| 2 | `/api/merchant/appointments/recent` | GET | `limit=5` | `RecentOrder[]` | 正常 |
| 3 | `/api/merchant/reviews/recent` | GET | `limit=5` | `RecentReview[]` | 正常 |

---

## 2. merchant-home/index.vue

- **页面名称**：商家后台首页（新版/Element Plus 版）
- **文件路径**：`cg-vue/src/views/merchant/merchant-home/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/dashboard` | GET | 无 | `DashboardStats` | 正常 |
| 2 | `/api/merchant/appointments/recent` | GET | `limit=5` | `RecentOrder[]` | 正常 |
| 3 | `/api/merchant/reviews/recent` | GET | `limit=5` | `RecentReview[]` | 正常 |

---

## 3. services/index.vue

- **页面名称**：服务管理（简化版）
- **文件路径**：`cg-vue/src/views/merchant/services/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/services` | GET | 无 | `MerchantService[]` | 正常 |
| 2 | `/api/merchant/services/:id` | DELETE | `id` | 无 | 正常 |
| 3 | `/api/merchant/services/batch/status` | PUT | `{ ids, status }` | `MerchantService[]` | 正常 |

---

## 4. merchant-services/index.vue

- **页面名称**：服务管理（完整版/弹窗编辑版）
- **文件路径**：`cg-vue/src/views/merchant/merchant-services/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/services` | GET | 无 | `MerchantService[]` | 正常 |
| 2 | `/api/merchant/services` | POST | `Omit<MerchantService, 'id'>` | `MerchantService` | 正常 |
| 3 | `/api/merchant/services/:id` | PUT | `id`, `Partial<MerchantService>` | `MerchantService` | 正常 |
| 4 | `/api/merchant/services/:id` | DELETE | `id` | 无 | 正常 |
| 5 | `/api/merchant/services/batch/status` | PUT | `{ ids, status }` | `MerchantService[]` | 正常 |
| 6 | `/api/merchant/services/batch` | DELETE | `{ ids }` | 无 | 正常 |

---

## 5. service-edit/index.vue

- **页面名称**：服务编辑（新增/编辑）
- **文件路径**：`cg-vue/src/views/merchant/service-edit/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/services/:id` | GET | `id` | `MerchantService` | 正常 |
| 2 | `/api/merchant/services` | POST | `Omit<MerchantService, 'id'>` | `MerchantService` | 正常 |
| 3 | `/api/merchant/services/:id` | PUT | `id`, `Partial<MerchantService>` | `MerchantService` | 正常 |

---

## 6. merchant-products/index.vue

- **页面名称**：商品管理
- **文件路径**：`cg-vue/src/views/merchant/merchant-products/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/products/paged` | GET | `{ page, size, name, status }` | 分页数据 | 正常 |
| 2 | `/api/merchant/products/:id` | DELETE | `id` | 无 | 正常 |
| 3 | `/api/merchant/products/:id/status` | PUT | `id`, `{ status }` | `Product` | 正常 |
| 4 | `/api/merchant/products/batch/status` | PUT | `{ ids, status }` | `Product[]` | 正常 |
| 5 | `/api/merchant/products/batch` | DELETE | `{ ids }` | 无 | 正常 |

---

## 7. product-edit/index.vue

- **页面名称**：商品编辑（新增/编辑）
- **文件路径**：`cg-vue/src/views/merchant/product-edit/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/products/:id` | GET | `id` | `Product` | 正常 |
| 2 | `/api/merchant/products` | POST | `Omit<Product, 'id'>` | `Product` | 正常 |
| 3 | `/api/merchant/products/:id` | PUT | `id`, `Partial<Product>` | `Product` | 正常 |

---

## 8. appointments/index.vue

- **页面名称**：预约列表（简化版）
- **文件路径**：`cg-vue/src/views/merchant/appointments/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/appointments` | GET | 无 | `Appointment[]` | 正常 |
| 2 | `/api/merchant/appointments/:id/status` | PUT | `id`, `{ status, rejectReason }` | `Appointment` | 正常 |

---

## 9. merchant-appointments/index.vue

- **页面名称**：服务预约订单（完整版/带导出）
- **文件路径**：`cg-vue/src/views/merchant/merchant-appointments/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/appointments` | GET | 无 | `Appointment[]` | 正常 |
| 2 | `/api/merchant/appointments/:id/status` | PUT | `id`, `{ status, rejectReason }` | `Appointment` | 正常 |

> 注：该页面包含前端导出功能（导出 CSV），但无后端导出 API 调用。

---

## 10. merchant-orders/index.vue

- **页面名称**：订单处理（服务订单）
- **文件路径**：`cg-vue/src/views/merchant/merchant-orders/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/appointments` | GET | 无 | `Order[]` | 正常 |
| 2 | `/api/merchant/appointments/:id/status` | PUT | `id`, `{ status, rejectReason }` | `Order` | 正常 |

---

## 11. merchant-product-orders/index.vue

- **页面名称**：商品订单管理
- **文件路径**：`cg-vue/src/views/merchant/merchant-product-orders/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/product-orders` | GET | 无 | `ProductOrder[]` | 正常 |
| 2 | `/api/merchant/product-orders/:id/status` | PUT | `id`, `{ status }` | `ProductOrder` | 正常 |
| 3 | `/api/merchant/product-orders/:id/logistics` | PUT | `id`, `{ logisticsCompany, trackingNumber }` | `ProductOrder` | 正常 |

---

## 12. reviews/index.vue

- **页面名称**：服务评价列表（卡片式/回复版）
- **文件路径**：`cg-vue/src/views/merchant/reviews/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/reviews` | GET | `{ page, size, rating, keyword }` | `ReviewListResponse` | 正常 |
| 2 | `/api/merchant/reviews/:id/reply` | PUT | `id`, `{ reply }` | `Review` | 正常 |
| 3 | `/api/merchant/reviews/:id` | DELETE | `id` | 无 | 正常 |

---

## 13. merchant-reviews/index.vue

- **页面名称**：评价管理（完整版/带统计）
- **文件路径**：`cg-vue/src/views/merchant/merchant-reviews/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/reviews` | GET | `{ page, size, rating, startDate, endDate, keyword }` | `ReviewListResponse` | 正常 |
| 2 | `/api/merchant/reviews/statistics` | GET | 无 | 评分统计数据 | 正常 |
| 3 | `/api/merchant/reviews/:id/reply` | PUT | `id`, `{ reply }` | `Review` | 正常 |
| 4 | `/api/merchant/reviews/:id` | DELETE | `id` | 无 | 正常 |

---

## 14. shop-edit/index.vue

- **页面名称**：店铺信息编辑
- **文件路径**：`cg-vue/src/views/merchant/shop-edit/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/info` | GET | 无 | `MerchantInfo` | 正常 |
| 2 | `/api/merchant/info` | PUT | `Partial<MerchantInfo>` | `MerchantInfo` | 正常 |

---

## 15. shop-settings/index.vue

- **页面名称**：店铺设置
- **文件路径**：`cg-vue/src/views/merchant/shop-settings/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/settings` | GET | 无 | `MerchantSettings` | 正常 |
| 2 | `/api/merchant/settings` | PUT | `MerchantSettings` | `MerchantSettings` | 正常 |
| 3 | `/api/merchant/info` | GET | 无 | `MerchantInfo` | 正常 |
| 4 | `/api/merchant/change-password` | POST | `{ oldPassword, newPassword }` | 无 | 正常 |
| 5 | `/api/merchant/bind-phone` | POST | `{ phone, verifyCode }` | 无 | 正常 |
| 6 | `/api/merchant/bind-email` | POST | `{ email, verifyCode }` | 无 | 正常 |
| 7 | `/api/merchant/send-verify-code` | POST | `{ type, value }` | 无 | 正常 |

---

## 16. categories/index.vue

- **页面名称**：商品分类管理
- **文件路径**：`cg-vue/src/views/merchant/categories/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/categories` | GET | 无 | `Category[]` | 正常 |
| 2 | `/api/merchant/categories` | POST | `Omit<Category, 'id' \| 'productCount' \| 'createdAt'>` | `Category` | 正常 |
| 3 | `/api/merchant/categories/:id` | PUT | `id`, `Partial<Category>` | `Category` | 正常 |
| 4 | `/api/merchant/categories/:id` | DELETE | `id` | 无 | 正常 |
| 5 | `/api/merchant/categories/:id/status` | PUT | `id`, `{ status }` | `Category` | 正常 |
| 6 | `/api/merchant/categories/batch/status` | PUT | `{ ids, status }` | `Category[]` | 正常 |
| 7 | `/api/merchant/categories/batch` | DELETE | `{ ids }` | 无 | 正常 |

---

## 17. stats-appointments/index.vue

- **页面名称**：预约统计
- **文件路径**：`cg-vue/src/views/merchant/stats-appointments/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/appointment-stats` | GET | `{ startDate, endDate }` | `AppointmentStats` | 正常 |
| 2 | `/api/merchant/appointment-stats/export` | GET | `{ startDate, endDate }`, `responseType: 'blob'` | `Blob` | 正常 |

---

## 18. stats-revenue/index.vue

- **页面名称**：营收统计
- **文件路径**：`cg-vue/src/views/merchant/stats-revenue/index.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/revenue-stats` | GET | `{ startDate, endDate }` | `RevenueStats` | 正常 |
| 2 | `/api/merchant/revenue-stats/export` | GET | `{ startDate, endDate }`, `responseType: 'blob'` | `Blob` | 正常 |

---

## 19. Register.vue

- **页面名称**：商家注册
- **文件路径**：`cg-vue/src/views/merchant/Register.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/auth/merchant/register` | POST | `MerchantRegisterData` | `{ message: string }` | 正常 |

---

## 20. Login.vue

- **页面名称**：商家端登录
- **文件路径**：`cg-vue/src/views/merchant/Login.vue`
- **API 调用**：

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | 无真实 API 调用 | — | — | — | **异常** |

> 注：该页面当前仅使用 `setTimeout` 模拟登录，无真实后端 API 调用。实际应调用 `/api/auth/login`（POST，参数 `{ username, password }`）。

---

## 汇总统计

| 页面 | API 数量 | 状态 |
|------|----------|------|
| home/index.vue | 3 | 正常 |
| merchant-home/index.vue | 3 | 正常 |
| services/index.vue | 3 | 正常 |
| merchant-services/index.vue | 6 | 正常 |
| service-edit/index.vue | 3 | 正常 |
| merchant-products/index.vue | 5 | 正常 |
| product-edit/index.vue | 3 | 正常 |
| appointments/index.vue | 2 | 正常 |
| merchant-appointments/index.vue | 2 | 正常 |
| merchant-orders/index.vue | 2 | 正常 |
| merchant-product-orders/index.vue | 3 | 正常 |
| reviews/index.vue | 3 | 正常 |
| merchant-reviews/index.vue | 4 | 正常 |
| shop-edit/index.vue | 2 | 正常 |
| shop-settings/index.vue | 7 | 正常 |
| categories/index.vue | 7 | 正常 |
| stats-appointments/index.vue | 2 | 正常 |
| stats-revenue/index.vue | 2 | 正常 |
| Register.vue | 1 | 正常 |
| Login.vue | 0 | **异常** |

**总计**：20 个页面，共 61 个 API 调用，其中 60 个正常，1 个异常（Login.vue 无真实 API）。

---

## 附录：所有涉及的后端 API 路径清单

### 商家模块（/api/merchant/*）

| API 路径 | 方法 | 被调用页面数 |
|----------|------|-------------|
| `/api/merchant/dashboard` | GET | 2 |
| `/api/merchant/appointments/recent` | GET | 2 |
| `/api/merchant/reviews/recent` | GET | 2 |
| `/api/merchant/services` | GET/POST | 3 |
| `/api/merchant/services/:id` | GET/PUT/DELETE | 3 |
| `/api/merchant/services/batch/status` | PUT | 2 |
| `/api/merchant/services/batch` | DELETE | 1 |
| `/api/merchant/products/paged` | GET | 1 |
| `/api/merchant/products/:id` | GET/PUT/DELETE | 2 |
| `/api/merchant/products/:id/status` | PUT | 1 |
| `/api/merchant/products/batch/status` | PUT | 1 |
| `/api/merchant/products/batch` | DELETE | 1 |
| `/api/merchant/appointments` | GET | 4 |
| `/api/merchant/appointments/:id/status` | PUT | 3 |
| `/api/merchant/product-orders` | GET | 1 |
| `/api/merchant/product-orders/:id/status` | PUT | 1 |
| `/api/merchant/product-orders/:id/logistics` | PUT | 1 |
| `/api/merchant/reviews` | GET | 2 |
| `/api/merchant/reviews/statistics` | GET | 1 |
| `/api/merchant/reviews/:id/reply` | PUT | 2 |
| `/api/merchant/reviews/:id` | DELETE | 2 |
| `/api/merchant/info` | GET/PUT | 2 |
| `/api/merchant/settings` | GET/PUT | 1 |
| `/api/merchant/change-password` | POST | 1 |
| `/api/merchant/bind-phone` | POST | 1 |
| `/api/merchant/bind-email` | POST | 1 |
| `/api/merchant/send-verify-code` | POST | 1 |
| `/api/merchant/categories` | GET/POST | 1 |
| `/api/merchant/categories/:id` | PUT/DELETE | 1 |
| `/api/merchant/categories/:id/status` | PUT | 1 |
| `/api/merchant/categories/batch/status` | PUT | 1 |
| `/api/merchant/categories/batch` | DELETE | 1 |
| `/api/merchant/appointment-stats` | GET | 1 |
| `/api/merchant/appointment-stats/export` | GET | 1 |
| `/api/merchant/revenue-stats` | GET | 1 |
| `/api/merchant/revenue-stats/export` | GET | 1 |

### 认证模块（/api/auth/*）

| API 路径 | 方法 | 被调用页面数 |
|----------|------|-------------|
| `/api/auth/merchant/register` | POST | 1 |
| `/api/auth/login` | POST | 0（Login.vue 未调用） |

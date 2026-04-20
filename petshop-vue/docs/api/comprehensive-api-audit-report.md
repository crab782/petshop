# 综合 API 审计汇总报告

> 生成日期：2026-04-21
> 审计范围：用户端、商家端、平台端三端全部页面
> 数据来源：`user-pages-api-audit.md`、`merchant-pages-api-audit.md`、`admin-pages-api-audit.md`

---

## 一、总体统计信息

### 1.1 三端概览

| 统计项 | 用户端 | 商家端 | 平台端 | **合计** |
|--------|--------|--------|--------|----------|
| 审计页面总数 | 27 | 20 | 20 | **67** |
| 涉及 API 文件 | 5 | 2 | 2 | **9** |
| API 调用总数 | 60+ | 61 | 66 | **187+** |
| 正常 API 数量 | 60+ | 60 | 66 | **186+** |
| 异常 API 数量 | 0 | 1 | 0 | **1** |
| 未使用 API 页面 | 0 | 0 | 2 | **2** |

### 1.2 状态分布

| 状态 | 数量 | 占比 |
|------|------|------|
| 正常 | 186+ | 99.5% |
| 异常 | 1 | 0.5% |
| 未使用 | 2（页面级） | - |

---

## 二、各端 API 分布情况

### 2.1 用户端 API 分布

**涉及 API 文件**：`user.ts`、`notification.ts`、`announcement.ts`、`search.ts`、`auth.ts`

| 功能模块 | API 数量 | 主要 API 路径前缀 |
|----------|----------|-------------------|
| 用户首页/个人中心 | 8 | `/api/user/home/*`, `/api/auth/userinfo` |
| 宠物管理 | 5 | `/api/user/pets/*` |
| 服务浏览/收藏 | 10 | `/api/services/*`, `/api/user/favorites/*` |
| 预约管理 | 6 | `/api/user/appointments/*` |
| 订单/购物车 | 14 | `/api/user/orders/*`, `/api/user/cart/*` |
| 商品/商家浏览 | 8 | `/api/products/*`, `/api/merchants/*`, `/api/merchant/*` |
| 评价管理 | 5 | `/api/user/reviews/*` |
| 地址管理 | 5 | `/api/user/addresses/*` |
| 搜索 | 7 | `/api/products/search`, `/api/services/search`, `/api/user/search-history` |
| 通知/公告 | 8 | `/api/user/notifications/*`, `/api/announcements/*` |

### 2.2 商家端 API 分布

**涉及 API 文件**：`merchant.ts`、`auth.ts`

| 功能模块 | API 数量 | 主要 API 路径前缀 |
|----------|----------|-------------------|
| 首页/仪表盘 | 6 | `/api/merchant/dashboard`, `/api/merchant/appointments/recent`, `/api/merchant/reviews/recent` |
| 服务管理 | 12 | `/api/merchant/services/*` |
| 商品管理 | 11 | `/api/merchant/products/*` |
| 预约/订单管理 | 10 | `/api/merchant/appointments/*`, `/api/merchant/product-orders/*` |
| 评价管理 | 7 | `/api/merchant/reviews/*` |
| 店铺管理 | 11 | `/api/merchant/info`, `/api/merchant/settings`, `/api/merchant/categories/*` |
| 统计报表 | 4 | `/api/merchant/appointment-stats/*`, `/api/merchant/revenue-stats/*` |
| 认证 | 1 | `/api/auth/merchant/register` |

### 2.3 平台端 API 分布

**涉及 API 文件**：`admin.ts`、`announcement.ts`

| 功能模块 | API 数量 | 主要 API 路径前缀 |
|----------|----------|-------------------|
| 仪表盘 | 4 | `/api/admin/dashboard/*` |
| 用户管理 | 7 | `/api/admin/users/*` |
| 商家管理 | 9 | `/api/admin/merchants/*`, `/api/admin/shops/*` |
| 服务/商品管理 | 16 | `/api/admin/services/*`, `/api/admin/products/*` |
| 评价管理 | 5 | `/api/admin/reviews/*` |
| 公告管理 | 7 | `/api/announcements/*` |
| 角色权限 | 5 | `/api/admin/roles/*`, `/api/admin/permissions` |
| 操作日志 | 3 | `/api/admin/operation-logs/*` |
| 活动管理 | 5 | `/api/admin/activities/*` |
| 任务管理 | 5 | `/api/admin/tasks/*` |

---

## 三、发现的主要问题

### 3.1 异常问题（1 项）

| 序号 | 问题描述 | 影响页面 | 严重程度 |
|------|----------|----------|----------|
| 1 | **商家端 Login.vue 无真实 API 调用** | `merchant/Login.vue` | 🔴 高 |

**详情**：商家端登录页面当前仅使用 `setTimeout` 模拟登录流程，未调用真实的后端登录 API（应为 `/api/auth/login` POST 请求）。这会导致商家无法正常登录系统。

### 3.2 代码不一致问题（严重）

| 序号 | 问题描述 | 影响范围 | 严重程度 |
|------|----------|----------|----------|
| 2 | **平台端大量页面导入 API 模块但未实际使用** | admin-*, user-detail, merchant-detail 等约 12 个页面 | 🟡 中 |

**详情**：平台端多个页面虽然通过 `import` 导入了 `@/api/admin` 中的 API 函数，但实际代码中直接使用 `fetch` 进行调用。这种代码与 API 模块的分离会导致：
- 维护困难，API 路径散落在各个页面中
- 难以统一处理请求拦截、错误处理、Token 刷新等逻辑
- 类型定义与实际调用不一致

### 3.3 未实现功能（2 项）

| 序号 | 问题描述 | 影响页面 | 严重程度 |
|------|----------|----------|----------|
| 3 | **平台端 admin-system 为纯静态页面** | `admin/admin-system/index.vue` | 🟡 中 |
| 4 | **AdminLayout.vue 退出登录无 API 调用** | `admin/AdminLayout.vue` | 🟢 低 |

**详情**：
- 系统设置页面仅做前端展示，保存功能使用模拟延迟，未对接真实的系统配置 API
- 平台端布局组件的退出登录功能仅做路由跳转，未调用登出 API 清除服务端 Session/Token

### 3.4 API 设计冗余

| 序号 | 问题描述 | 影响范围 | 严重程度 |
|------|----------|----------|----------|
| 5 | **商家审核存在两套相似 API** | `merchant-audit`, `shop-audit` | 🟢 低 |

**详情**：`merchant-audit/index.vue` 使用 `/api/admin/merchants/pending` 和 `/api/admin/merchants/${id}/audit`，而 `shop-audit/index.vue` 使用 `/api/admin/shops/pending` 和 `/api/admin/shops/${id}/audit`。两者功能高度重合，建议合并为一套 API。

### 3.5 前端导出功能依赖前端实现

| 序号 | 问题描述 | 影响范围 | 严重程度 |
|------|----------|----------|----------|
| 6 | **部分导出功能仅前端实现** | `merchant-appointments`, `logs` | 🟢 低 |

**详情**：`merchant-appointments` 页面的导出 CSV 功能在前端实现，未调用后端导出 API。虽然 `stats-appointments` 和 `stats-revenue` 已提供后端导出 API，但其他页面的导出功能实现方式不统一。

---

## 四、建议的改进措施

### 4.1 立即修复（高优先级）

1. **修复商家端登录 API**
   - 在 `merchant/Login.vue` 中添加真实的登录 API 调用
   - 调用 `/api/auth/login`（POST，参数 `{ username, password }`）
   - 处理登录成功后的 Token 存储和路由跳转

### 4.2 代码重构（中优先级）

2. **统一平台端 API 调用方式**
   - 将所有直接使用 `fetch` 的页面改为使用 `@/api/admin` 模块封装的函数
   - 好处：统一错误处理、请求拦截、类型安全、便于维护
   - 涉及页面：admin-users, user-detail, admin-merchants, merchant-detail, merchant-audit, admin-services, admin-products, product-manage, admin-reviews, review-audit

3. **完善系统设置功能**
   - 为 `admin-system/index.vue` 对接后端系统配置 API
   - 建议创建 `/api/admin/settings` 接口用于读取和保存系统配置
   - 配置项包括：网站基本信息、邮件 SMTP、短信服务、支付配置、文件上传等

### 4.3 API 优化（低优先级）

4. **合并冗余 API**
   - 将 `/api/admin/shops/*` 合并到 `/api/admin/merchants/*` 下
   - 统一商家入驻审核和店铺审核的概念

5. **统一导出功能实现**
   - 所有导出功能统一调用后端导出 API
   - 使用 `responseType: 'blob'` 接收文件流
   - 前端提供统一的导出按钮和加载状态

6. **完善退出登录功能**
   - 为三端布局组件的退出登录添加真实的 API 调用
   - 调用 `/api/auth/logout` 清除服务端会话
   - 同时清除前端存储的 Token 和用户信息

### 4.4 规范建议

7. **API 路径命名规范**
   - 统一使用 RESTful 风格
   - 复数名词表示资源（如 `/users` 而非 `/user`）
   - 动作使用 HTTP 方法表示（GET/POST/PUT/DELETE）

8. **增加 API 文档**
   - 为所有后端 API 编写 Swagger/OpenAPI 文档
   - 前端根据文档生成 TypeScript 类型定义

---

## 五、完整的 API 路径清单

### 5.1 认证模块（/api/auth/*）

| API 路径 | 方法 | 用户端 | 商家端 | 平台端 | 说明 |
|----------|------|--------|--------|--------|------|
| `/api/auth/userinfo` | GET | ✅ | - | - | 获取当前用户信息 |
| `/api/auth/userinfo` | PUT | ✅ | - | - | 更新当前用户信息 |
| `/api/auth/merchant/register` | POST | - | ✅ | - | 商家注册 |
| `/api/auth/login` | POST | - | ⚠️ | - | 用户/商家/管理员登录（商家端未调用） |
| `/api/auth/logout` | POST | - | - | - | 退出登录（三端均未实现） |

### 5.2 用户模块（/api/user/*）

| API 路径 | 方法 | 用户端 | 商家端 | 平台端 | 说明 |
|----------|------|--------|--------|--------|------|
| `/api/user/home/stats` | GET | ✅ | - | - | 用户首页统计 |
| `/api/user/home/activities` | GET | ✅ | - | - | 用户最近活动 |
| `/api/user/pets` | GET | ✅ | - | - | 获取宠物列表 |
| `/api/user/pets` | POST | ✅ | - | - | 添加宠物 |
| `/api/user/pets/${id}` | GET | ✅ | - | - | 获取宠物详情 |
| `/api/user/pets/${id}` | PUT | ✅ | - | - | 更新宠物信息 |
| `/api/user/pets/${id}` | DELETE | ✅ | - | - | 删除宠物 |
| `/api/user/appointments` | GET | ✅ | - | - | 获取预约列表 |
| `/api/user/appointments` | POST | ✅ | - | - | 创建预约 |
| `/api/user/appointments/${id}` | GET | ✅ | - | - | 获取预约详情 |
| `/api/user/appointments/${id}/cancel` | PUT | ✅ | - | - | 取消预约 |
| `/api/user/appointments/stats` | GET | ✅ | - | - | 获取预约统计 |
| `/api/user/services` | GET | ✅ | - | - | 获取已购买服务 |
| `/api/user/orders` | GET | ✅ | - | - | 获取商品订单列表 |
| `/api/user/orders` | POST | ✅ | - | - | 创建商品订单 |
| `/api/user/orders/${id}` | GET | ✅ | - | - | 获取订单详情 |
| `/api/user/orders/${id}/cancel` | PUT | ✅ | - | - | 取消订单 |
| `/api/user/orders/${id}/confirm` | PUT | ✅ | - | - | 确认收货 |
| `/api/user/orders/${id}/refund` | POST | ✅ | - | - | 申请退款 |
| `/api/user/orders/${orderId}/pay` | POST | ✅ | - | - | 发起支付 |
| `/api/user/orders/${orderId}/pay/status` | GET | ✅ | - | - | 查询支付状态 |
| `/api/user/orders/batch-cancel` | PUT | ✅ | - | - | 批量取消订单 |
| `/api/user/orders/batch-delete` | DELETE | ✅ | - | - | 批量删除订单 |
| `/api/user/orders/preview` | POST | ✅ | - | - | 订单预览 |
| `/api/user/addresses` | GET | ✅ | - | - | 获取收货地址 |
| `/api/user/addresses` | POST | ✅ | - | - | 添加收货地址 |
| `/api/user/addresses/${id}` | PUT | ✅ | - | - | 更新收货地址 |
| `/api/user/addresses/${id}` | DELETE | ✅ | - | - | 删除收货地址 |
| `/api/user/addresses/${id}/default` | PUT | ✅ | - | - | 设置默认地址 |
| `/api/user/reviews` | GET | ✅ | - | - | 获取评价列表 |
| `/api/user/reviews/${id}` | PUT | ✅ | - | - | 更新评价 |
| `/api/user/reviews/${id}` | DELETE | ✅ | - | - | 删除评价 |
| `/api/user/favorites` | GET | ✅ | - | - | 获取收藏商家列表 |
| `/api/user/favorites` | POST | ✅ | - | - | 收藏商家 |
| `/api/user/favorites/${merchantId}` | DELETE | ✅ | - | - | 取消收藏商家 |
| `/api/user/favorites/services` | GET | ✅ | - | - | 获取收藏服务列表 |
| `/api/user/favorites/services` | POST | ✅ | - | - | 收藏服务 |
| `/api/user/favorites/services/${serviceId}` | DELETE | ✅ | - | - | 取消收藏服务 |
| `/api/user/favorites/services/${serviceId}/check` | GET | ✅ | - | - | 检查是否已收藏服务 |
| `/api/user/favorites/products` | POST | ✅ | - | - | 收藏商品 |
| `/api/user/favorites/products/${productId}` | DELETE | ✅ | - | - | 取消收藏商品 |
| `/api/user/favorites/products/${productId}/check` | GET | ✅ | - | - | 检查是否已收藏商品 |
| `/api/user/cart` | GET | ✅ | - | - | 获取购物车 |
| `/api/user/cart` | POST | ✅ | - | - | 添加商品到购物车 |
| `/api/user/cart` | PUT | ✅ | - | - | 更新购物车商品数量 |
| `/api/user/cart/${productId}` | DELETE | ✅ | - | - | 删除购物车商品 |
| `/api/user/cart/batch` | DELETE | ✅ | - | - | 批量删除购物车商品 |
| `/api/user/notifications` | GET | ✅ | - | - | 获取通知列表 |
| `/api/user/notifications/${id}/read` | PUT | ✅ | - | - | 标记通知已读 |
| `/api/user/notifications/read-all` | PUT | ✅ | - | - | 标记所有通知已读 |
| `/api/user/notifications/batch-read` | PUT | ✅ | - | - | 批量标记通知已读 |
| `/api/user/notifications/${id}` | DELETE | ✅ | - | - | 删除通知 |
| `/api/user/notifications/batch` | DELETE | ✅ | - | - | 批量删除通知 |
| `/api/user/search-history` | GET | ✅ | - | - | 获取搜索历史 |
| `/api/user/search-history` | POST | ✅ | - | - | 保存搜索历史 |
| `/api/user/search-history` | DELETE | ✅ | - | - | 清空搜索历史 |

### 5.3 商家模块（/api/merchant/*）

| API 路径 | 方法 | 用户端 | 商家端 | 平台端 | 说明 |
|----------|------|--------|--------|--------|------|
| `/api/merchant/dashboard` | GET | - | ✅ | - | 商家仪表盘统计 |
| `/api/merchant/appointments/recent` | GET | - | ✅ | - | 最近预约订单 |
| `/api/merchant/reviews/recent` | GET | - | ✅ | - | 最近评价 |
| `/api/merchant/services` | GET/POST | - | ✅ | - | 服务列表/创建服务 |
| `/api/merchant/services/${id}` | GET/PUT/DELETE | - | ✅ | - | 服务详情/更新/删除 |
| `/api/merchant/services/batch/status` | PUT | - | ✅ | - | 批量更新服务状态 |
| `/api/merchant/services/batch` | DELETE | - | ✅ | - | 批量删除服务 |
| `/api/merchant/products/paged` | GET | - | ✅ | - | 分页获取商品列表 |
| `/api/merchant/products/${id}` | GET/PUT/DELETE | - | ✅ | - | 商品详情/更新/删除 |
| `/api/merchant/products/${id}/status` | PUT | - | ✅ | - | 更新商品状态 |
| `/api/merchant/products/batch/status` | PUT | - | ✅ | - | 批量更新商品状态 |
| `/api/merchant/products/batch` | DELETE | - | ✅ | - | 批量删除商品 |
| `/api/merchant/appointments` | GET | - | ✅ | - | 获取预约列表 |
| `/api/merchant/appointments/${id}/status` | PUT | - | ✅ | - | 更新预约状态 |
| `/api/merchant/product-orders` | GET | - | ✅ | - | 获取商品订单列表 |
| `/api/merchant/product-orders/${id}/status` | PUT | - | ✅ | - | 更新商品订单状态 |
| `/api/merchant/product-orders/${id}/logistics` | PUT | - | ✅ | - | 更新物流信息 |
| `/api/merchant/reviews` | GET | - | ✅ | - | 获取评价列表 |
| `/api/merchant/reviews/statistics` | GET | - | ✅ | - | 获取评价统计 |
| `/api/merchant/reviews/${id}/reply` | PUT | - | ✅ | - | 回复评价 |
| `/api/merchant/reviews/${id}` | DELETE | - | ✅ | - | 删除评价 |
| `/api/merchant/info` | GET/PUT | - | ✅ | - | 获取/更新商家信息 |
| `/api/merchant/settings` | GET/PUT | - | ✅ | - | 获取/更新店铺设置 |
| `/api/merchant/change-password` | POST | - | ✅ | - | 修改密码 |
| `/api/merchant/bind-phone` | POST | - | ✅ | - | 绑定手机号 |
| `/api/merchant/bind-email` | POST | - | ✅ | - | 绑定邮箱 |
| `/api/merchant/send-verify-code` | POST | - | ✅ | - | 发送验证码 |
| `/api/merchant/categories` | GET/POST | - | ✅ | - | 分类列表/创建分类 |
| `/api/merchant/categories/${id}` | PUT/DELETE | - | ✅ | - | 更新/删除分类 |
| `/api/merchant/categories/${id}/status` | PUT | - | ✅ | - | 更新分类状态 |
| `/api/merchant/categories/batch/status` | PUT | - | ✅ | - | 批量更新分类状态 |
| `/api/merchant/categories/batch` | DELETE | - | ✅ | - | 批量删除分类 |
| `/api/merchant/appointment-stats` | GET | - | ✅ | - | 预约统计数据 |
| `/api/merchant/appointment-stats/export` | GET | - | ✅ | - | 导出预约统计 |
| `/api/merchant/revenue-stats` | GET | - | ✅ | - | 营收统计数据 |
| `/api/merchant/revenue-stats/export` | GET | - | ✅ | - | 导出营收统计 |

### 5.4 平台管理模块（/api/admin/*）

| API 路径 | 方法 | 用户端 | 商家端 | 平台端 | 说明 |
|----------|------|--------|--------|--------|------|
| `/api/admin/dashboard` | GET | - | - | ✅ | 平台仪表盘统计 |
| `/api/admin/dashboard/recent-users` | GET | - | - | ✅ | 最近注册用户 |
| `/api/admin/dashboard/pending-merchants` | GET | - | - | ✅ | 待审核商家 |
| `/api/admin/dashboard/announcements` | GET | - | - | ✅ | 系统公告 |
| `/api/admin/users` | GET | - | - | ✅ | 用户列表 |
| `/api/admin/users/${id}` | GET/DELETE | - | - | ✅ | 用户详情/删除用户 |
| `/api/admin/users/${id}/status` | PUT | - | - | ✅ | 更新用户状态 |
| `/api/admin/users/batch/status` | PUT | - | - | ✅ | 批量更新用户状态 |
| `/api/admin/users/batch` | DELETE | - | - | ✅ | 批量删除用户 |
| `/api/admin/merchants` | GET | - | - | ✅ | 商家列表 |
| `/api/admin/merchants/${id}` | GET/DELETE | - | - | ✅ | 商家详情/删除商家 |
| `/api/admin/merchants/${id}/status` | PUT | - | - | ✅ | 更新商家状态 |
| `/api/admin/merchants/${id}/audit` | PUT | - | - | ✅ | 审核商家 |
| `/api/admin/merchants/pending` | GET | - | - | ✅ | 待审核商家列表 |
| `/api/admin/merchants/batch/status` | PUT | - | - | ✅ | 批量更新商家状态 |
| `/api/admin/merchants/batch` | DELETE | - | - | ✅ | 批量删除商家 |
| `/api/admin/services` | GET | - | - | ✅ | 服务列表 |
| `/api/admin/services/${id}` | DELETE | - | - | ✅ | 删除服务 |
| `/api/admin/services/${id}/status` | PUT | - | - | ✅ | 更新服务状态 |
| `/api/admin/services/batch/status` | PUT | - | - | ✅ | 批量更新服务状态 |
| `/api/admin/services/batch` | DELETE | - | - | ✅ | 批量删除服务 |
| `/api/admin/products` | GET | - | - | ✅ | 商品列表 |
| `/api/admin/products/${id}` | DELETE | - | - | ✅ | 删除商品 |
| `/api/admin/products/${id}/status` | PUT | - | - | ✅ | 更新商品状态 |
| `/api/admin/products/batch/status` | PUT | - | - | ✅ | 批量更新商品状态 |
| `/api/admin/products/batch` | DELETE | - | - | ✅ | 批量删除商品 |
| `/api/admin/reviews` | GET | - | - | ✅ | 评价列表 |
| `/api/admin/reviews/${id}` | DELETE | - | - | ✅ | 删除评价 |
| `/api/admin/reviews/pending` | GET | - | - | ✅ | 待审核评价列表 |
| `/api/admin/reviews/${id}/audit` | PUT | - | - | ✅ | 审核评价 |
| `/api/admin/roles` | GET/POST | - | - | ✅ | 角色列表/创建角色 |
| `/api/admin/roles/${id}` | PUT/DELETE | - | - | ✅ | 更新/删除角色 |
| `/api/admin/permissions` | GET | - | - | ✅ | 权限列表 |
| `/api/admin/operation-logs` | GET/DELETE | - | - | ✅ | 操作日志列表/清空日志 |
| `/api/admin/operation-logs/${id}` | DELETE | - | - | ✅ | 删除单条日志 |
| `/api/admin/activities` | GET/POST | - | - | ✅ | 活动列表/创建活动 |
| `/api/admin/activities/${id}` | PUT/DELETE | - | - | ✅ | 更新/删除活动 |
| `/api/admin/activities/${id}/status` | PUT | - | - | ✅ | 更新活动状态 |
| `/api/admin/tasks` | GET/POST | - | - | ✅ | 任务列表/创建任务 |
| `/api/admin/tasks/${id}` | PUT/DELETE | - | - | ✅ | 更新/删除任务 |
| `/api/admin/tasks/${id}/execute` | POST | - | - | ✅ | 立即执行任务 |
| `/api/admin/shops/pending` | GET | - | - | ✅ | 待审核店铺列表 |
| `/api/admin/shops/${id}/audit` | PUT | - | - | ✅ | 审核店铺 |

### 5.5 公共模块

| API 路径 | 方法 | 用户端 | 商家端 | 平台端 | 说明 |
|----------|------|--------|--------|--------|------|
| `/api/services` | GET | ✅ | - | - | 服务列表 |
| `/api/services/${id}` | GET | ✅ | - | - | 服务详情 |
| `/api/services/search` | GET | ✅ | - | - | 搜索服务 |
| `/api/services/recommended` | GET | ✅ | - | - | 推荐服务 |
| `/api/products/${id}` | GET | ✅ | - | - | 商品详情 |
| `/api/products/search` | GET | ✅ | - | - | 搜索商品 |
| `/api/products/${productId}/reviews` | GET | ✅ | - | - | 商品评价 |
| `/api/merchants` | GET | ✅ | - | - | 商家列表 |
| `/api/merchants/search` | GET | ✅ | - | - | 搜索商家 |
| `/api/merchant/${id}` | GET | ✅ | - | - | 商家详情 |
| `/api/merchant/${id}/services` | GET | ✅ | - | - | 商家服务列表 |
| `/api/merchant/${id}/products` | GET | ✅ | - | - | 商家商品列表 |
| `/api/merchant/${id}/reviews` | GET | ✅ | - | - | 商家评价列表 |
| `/api/merchant/${id}/available-slots` | GET | ✅ | - | - | 可预约时间段 |
| `/api/announcements` | GET | ✅ | - | ✅ | 公告列表 |
| `/api/announcements/${id}` | GET/PUT/DELETE | ✅ | - | ✅ | 公告详情/更新/删除 |
| `/api/announcements/${id}/publish` | PUT | - | - | ✅ | 发布公告 |
| `/api/announcements/${id}/unpublish` | PUT | - | - | ✅ | 下架公告 |
| `/api/search/hot-keywords` | GET | ✅ | - | - | 热门搜索关键词 |

---

## 六、API 复用情况分析

### 6.1 跨端复用的 API

| API 路径 | 复用端 | 说明 |
|----------|--------|------|
| `/api/announcements` | 用户端 + 平台端 | 公告列表和详情 |
| `/api/merchant/${id}` | 用户端 | 商家详情（多页面使用） |
| `/api/user/appointments` | 用户端（多页面） | 用户首页、预约列表、预约记录 |
| `/api/user/reviews` | 用户端（多页面） | 用户首页、个人中心、我的评价 |

### 6.2 高频使用 API（被 3 个以上页面调用）

| API 路径 | 调用页面数 | 所属端 |
|----------|------------|--------|
| `/api/user/pets` | 6 | 用户端 |
| `/api/user/appointments` | 4 | 用户端 |
| `/api/merchant/appointments` | 4 | 商家端 |
| `/api/user/favorites` | 4 | 用户端 |
| `/api/merchant/${id}` | 4 | 用户端 |
| `/api/merchant/services` | 3 | 商家端 |
| `/api/merchant/reviews` | 2 | 商家端 |
| `/api/user/orders` | 3 | 用户端 |
| `/api/user/reviews` | 4 | 用户端 |

---

## 七、总结

本次 API 审计覆盖了三端共 **67 个页面**，涉及 **187+ 个 API 调用**。整体来看：

- **健康度良好**：99.5% 的 API 调用状态正常，仅 1 个异常（商家端登录未实现真实 API 调用）
- **用户端最完整**：27 个页面全部有 API 对接，功能覆盖全面
- **商家端功能丰富**：20 个页面涵盖店铺运营全链路，但登录功能需要立即修复
- **平台端存在代码债务**：大量页面导入 API 模块但未实际使用，建议统一重构为使用封装的 API 模块
- **公共 API 设计合理**：公告、商家信息等 API 跨端复用，减少重复开发

**优先级建议**：
1. 🔴 **立即修复商家端登录 API**
2. 🟡 **统一平台端 API 调用方式（重构 fetch 为模块调用）**
3. 🟡 **完善平台端系统设置功能**
4. 🟢 **合并冗余 API（shops/merchants）**
5. 🟢 **统一导出功能实现方式**

---

*报告生成完成*

# 平台端页面 API 审计报告

> 生成时间: 2026-04-21
> 审计范围: cg-vue/src/views/admin/ 目录下所有页面

---

## 1. AdminLayout.vue - 平台布局组件

**文件路径**: `cg-vue/src/views/admin/AdminLayout.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 用途 | 状态 |
|------|----------|------|------|------|
| 无 | - | - | 该页面为纯布局组件，无 API 调用 | 未使用 |

### 说明
- 仅包含导航菜单和用户信息展示
- 退出登录功能仅做路由跳转，无 API 调用

---

## 2. admin-dashboard/index.vue - 平台首页

**文件路径**: `cg-vue/src/views/admin/admin-dashboard/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/dashboard` | GET | 无 | `{ code: 200, data: { userCount, merchantCount, orderCount } }` | 获取仪表盘统计数据 | 正常 |
| 2 | `/api/admin/dashboard/recent-users?page=0&pageSize=5` | GET | `page`, `pageSize` | `{ code: 200, data: { data: User[] } }` | 获取最近注册用户 | 正常 |
| 3 | `/api/admin/dashboard/pending-merchants?page=0&pageSize=3` | GET | `page`, `pageSize` | `{ code: 200, data: { data: Merchant[] } }` | 获取待审核商家 | 正常 |
| 4 | `/api/admin/dashboard/announcements?page=0&pageSize=3` | GET | `page`, `pageSize` | `{ code: 200, data: { data: Announcement[] } }` | 获取系统公告 | 正常 |

### 引用 API 模块
- 直接使用 `fetch` 调用，未引用 `@/api/admin`

### 说明
- 页面加载时并行调用 4 个 API 获取数据
- 使用 `Promise.all` 优化加载性能

---

## 3. admin-users/index.vue - 用户管理

**文件路径**: `cg-vue/src/views/admin/admin-users/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/users` | GET | 无 | `User[]` | 获取用户列表 | 正常 |
| 2 | `/api/admin/users/${id}` | DELETE | `id` (路径参数) | 无 | 删除单个用户 | 正常 |
| 3 | `/api/admin/users/batch/status` | PUT | `{ ids: number[], status: string }` | 无 | 批量更新用户状态 | 正常 |
| 4 | `/api/admin/users/batch` | DELETE | `{ ids: number[] }` | 无 | 批量删除用户 | 正常 |
| 5 | `/api/admin/users/${id}/status` | PUT | `{ status: string }` | 无 | 更新单个用户状态 | 正常 |

### 引用 API 模块
```typescript
import { getAllUsers, updateUserStatus, deleteUser, batchUpdateUserStatus, batchDeleteUsers } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 支持批量启用、禁用、删除操作

---

## 4. user-detail/index.vue - 用户详情

**文件路径**: `cg-vue/src/views/admin/user-detail/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/users/${id}` | GET | `id` (路径参数) | `User` | 获取用户详情 | 正常 |
| 2 | `/api/admin/users/${id}/status` | PUT | `{ status: 'active' \| 'disabled' }` | 无 | 启用/禁用用户账户 | 正常 |

### 引用 API 模块
```typescript
import { getUserDetailById, updateUserStatus, type UserDetail } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 包含用户基本信息和行为记录展示

---

## 5. admin-merchants/index.vue - 商家管理

**文件路径**: `cg-vue/src/views/admin/admin-merchants/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/merchants` | GET | 无 | `Merchant[]` | 获取商家列表 | 正常 |
| 2 | `/api/admin/merchants/${id}/audit` | PUT | `{ status: 'approved', reason?: string }` | 无 | 审核商家（通过/拒绝） | 正常 |
| 3 | `/api/admin/merchants/${id}` | DELETE | `id` (路径参数) | 无 | 删除商家 | 正常 |
| 4 | `/api/admin/merchants/batch/status` | PUT | `{ ids: number[], status: string }` | 无 | 批量更新商家状态 | 正常 |
| 5 | `/api/admin/merchants/batch` | DELETE | `{ ids: number[] }` | 无 | 批量删除商家 | 正常 |

### 引用 API 模块
```typescript
import { getAllMerchants, updateMerchantStatus, deleteMerchant, auditMerchant, type Merchant } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 支持批量审核、启用、禁用、删除操作

---

## 6. merchant-detail/index.vue - 商家详情

**文件路径**: `cg-vue/src/views/admin/merchant-detail/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/merchants/${id}` | GET | `id` (路径参数) | `Merchant` | 获取商家详情 | 正常 |
| 2 | `/api/admin/merchants/${id}/status` | PUT | `{ status: 'approved' \| 'disabled' }` | 无 | 启用/禁用店铺 | 正常 |

### 引用 API 模块
```typescript
import { getMerchantDetailById, updateMerchantStatus, type MerchantDetail } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 展示商家基本信息和统计数据

---

## 7. merchant-audit/index.vue - 商家入驻审核

**文件路径**: `cg-vue/src/views/admin/merchant-audit/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/merchants/pending?page&pageSize&keyword` | GET | `page`, `pageSize`, `keyword` (查询参数) | `Merchant[]` | 获取待审核商家列表 | 正常 |
| 2 | `/api/admin/merchants/${id}/audit` | PUT | `{ status: 'approved' \| 'rejected', reason?: string }` | 无 | 审核商家 | 正常 |

### 引用 API 模块
```typescript
import { getPendingMerchants, auditMerchant, type Merchant } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 支持批量通过和批量拒绝操作

---

## 8. admin-services/index.vue - 服务管理

**文件路径**: `cg-vue/src/views/admin/admin-services/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/merchants` | GET | 无 | `Merchant[]` | 获取商家列表（用于筛选） | 正常 |
| 2 | `/api/admin/services` | GET | 无 | `AdminService[]` | 获取服务列表 | 正常 |
| 3 | `/api/admin/services/${id}/status` | PUT | `{ status: 'enabled' \| 'disabled' }` | 无 | 更新服务状态 | 正常 |
| 4 | `/api/admin/services/${id}` | DELETE | `id` (路径参数) | 无 | 删除服务 | 正常 |
| 5 | `/api/admin/services/batch/status` | PUT | `{ ids: number[], status: string }` | 无 | 批量更新服务状态 | 正常 |
| 6 | `/api/admin/services/batch` | DELETE | `{ ids: number[] }` | 无 | 批量删除服务 | 正常 |

### 引用 API 模块
```typescript
import { getAllServices, deleteService, updateServiceStatus, batchUpdateServiceStatus, batchDeleteServices, getAllMerchants, type Merchant, type AdminService } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 支持按商家、价格区间、状态筛选
- 支持批量启用、禁用、删除

---

## 9. admin-products/index.vue - 商品管理

**文件路径**: `cg-vue/src/views/admin/admin-products/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/merchants` | GET | 无 | `Merchant[]` | 获取商家列表（用于筛选） | 正常 |
| 2 | `/api/admin/products` | GET | 无 | `Product[]` | 获取商品列表 | 正常 |
| 3 | `/api/admin/products/${id}/status` | PUT | `{ status: 'active' \| 'inactive' }` | 无 | 更新商品状态（上架/下架） | 正常 |
| 4 | `/api/admin/products/${id}` | DELETE | `id` (路径参数) | 无 | 删除商品 | 正常 |
| 5 | `/api/admin/products/batch/status` | PUT | `{ ids: number[], status: string }` | 无 | 批量更新商品状态 | 正常 |
| 6 | `/api/admin/products/batch` | DELETE | `{ ids: number[] }` | 无 | 批量删除商品 | 正常 |

### 引用 API 模块
```typescript
import { getAllProducts, updateProductStatus, deleteProduct, batchUpdateProductStatus, batchDeleteProducts, getAllMerchants, type Merchant, type Product } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 支持按商家、价格区间、库存状态、状态筛选
- 支持批量上架、下架、删除

---

## 10. product-manage/index.vue - 商品详情管理

**文件路径**: `cg-vue/src/views/admin/product-manage/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/merchants` | GET | 无 | `Merchant[]` | 获取商家列表 | 正常 |
| 2 | `/api/admin/products` | GET | 无 | `Product[]` | 获取商品列表 | 正常 |
| 3 | `/api/admin/products/${id}/status` | PUT | `{ status: 'active' \| 'inactive' }` | 无 | 更新商品状态 | 正常 |
| 4 | `/api/admin/products/${id}` | DELETE | `id` (路径参数) | 无 | 删除商品 | 正常 |

### 引用 API 模块
```typescript
import { getAllProductsForAdmin, updateProductStatus, deleteProduct, getAllMerchants, type Merchant, type Product } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 包含商品详情弹窗和编辑弹窗

---

## 11. admin-reviews/index.vue - 评价管理

**文件路径**: `cg-vue/src/views/admin/admin-reviews/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/reviews` | GET | 无 | `Review[]` | 获取评价列表 | 正常 |
| 2 | `/api/admin/reviews/${id}` | DELETE | `id` (路径参数) | 无 | 删除评价 | 正常 |

### 引用 API 模块
```typescript
import { getAllReviews, deleteReview, type Review } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 展示评价统计信息（总评价数、平均评分、好评率）
- 支持关键词搜索

---

## 12. review-audit/index.vue - 评价审核

**文件路径**: `cg-vue/src/views/admin/review-audit/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/reviews/pending` | GET | 无 | `Review[]` | 获取待审核评价列表 | 正常 |
| 2 | `/api/admin/reviews/${id}/audit` | PUT | `{ status: 'approved' \| 'violation', reason?: string, remark?: string }` | 无 | 审核评价 | 正常 |
| 3 | `/api/admin/reviews/${id}` | DELETE | `id` (路径参数) | 无 | 删除评价 | 正常 |

### 引用 API 模块
```typescript
import { getReviewsForAudit, approveReview, markReviewViolation, deleteReview, type Review } from '@/api/admin'
```

### 说明
- 导入的 API 函数未实际使用，页面直接使用 `fetch` 调用
- 支持批量通过和批量拒绝
- 违规处理需要选择违规原因

---

## 13. admin-announcements/index.vue - 公告管理

**文件路径**: `cg-vue/src/views/admin/admin-announcements/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/announcements` | GET | 无 | `Announcement[]` | 获取公告列表 | 正常 |
| 2 | `/api/announcements/${id}` | DELETE | `id` (路径参数) | 无 | 删除公告 | 正常 |
| 3 | `/api/announcements/${id}/publish` | PUT | `{}` | `Announcement` | 发布公告 | 正常 |
| 4 | `/api/announcements/${id}/unpublish` | PUT | `{}` | `Announcement` | 下架公告 | 正常 |

### 引用 API 模块
```typescript
import { getAnnouncements, addAnnouncement, updateAnnouncement, deleteAnnouncement, publishAnnouncement, unpublishAnnouncement } from '@/api/announcement'
```

### 说明
- 使用 `@/api/announcement` 模块的 API 函数
- 支持批量发布和批量下架

---

## 14. announcement-edit/index.vue - 发布公告/编辑公告

**文件路径**: `cg-vue/src/views/admin/announcement-edit/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/announcements/${id}` | GET | `id` (路径参数) | `Announcement` | 获取公告详情（编辑时） | 正常 |
| 2 | `/api/announcements` | POST | `{ title: string, content: string }` | `Announcement` | 添加公告 | 正常 |
| 3 | `/api/announcements/${id}` | PUT | `{ title: string, content: string }` | `Announcement` | 更新公告 | 正常 |

### 引用 API 模块
```typescript
import { getAnnouncementById, addAnnouncement, updateAnnouncement } from '@/api/announcement'
```

### 说明
- 使用 `@/api/announcement` 模块的 API 函数
- 支持新增和编辑两种模式

---

## 15. admin-system/index.vue - 系统设置

**文件路径**: `cg-vue/src/views/admin/admin-system/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 无 | - | - | 该页面为静态表单，无 API 调用 | 未使用 |

### 说明
- 当前为静态表单，仅做前端展示
- 包含基本设置、邮件设置、短信设置、支付设置四个标签页
- 保存功能使用模拟延迟，未对接真实 API

---

## 16. roles/index.vue - 角色权限管理

**文件路径**: `cg-vue/src/views/admin/roles/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/roles` | GET | 无 | `Role[]` | 获取角色列表 | 正常 |
| 2 | `/api/admin/permissions` | GET | 无 | `Permission[]` | 获取权限列表 | 正常 |
| 3 | `/api/admin/roles` | POST | `{ name, description, permissions }` | `Role` | 添加角色 | 正常 |
| 4 | `/api/admin/roles/${id}` | PUT | `{ name, description, permissions }` | `Role` | 更新角色 | 正常 |
| 5 | `/api/admin/roles/${id}` | DELETE | `id` (路径参数) | 无 | 删除角色 | 正常 |

### 引用 API 模块
```typescript
import { getRoles, addRole, updateRole, deleteRole, getPermissions } from '@/api/admin'
```

### 说明
- 使用 `@/api/admin` 模块的 API 函数
- 支持角色 CRUD 和权限配置
- 使用 ElTree 组件展示权限树

---

## 17. logs/index.vue - 操作日志

**文件路径**: `cg-vue/src/views/admin/logs/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/operation-logs` | GET | `{ type, username, startDate, endDate }` | `OperationLog[]` | 获取操作日志列表 | 正常 |
| 2 | `/api/admin/operation-logs/${id}` | DELETE | `id` (路径参数) | 无 | 删除单条日志 | 正常 |
| 3 | `/api/admin/operation-logs` | DELETE | 无 | 无 | 清空所有日志 | 正常 |

### 引用 API 模块
```typescript
import { getOperationLogs, deleteLog, clearLogs } from '@/api/admin'
```

### 说明
- 使用 `@/api/admin` 模块的 API 函数
- 支持按操作类型、操作人、时间范围筛选
- 支持导出 CSV 格式日志

---

## 18. admin-activities/index.vue - 活动管理

**文件路径**: `cg-vue/src/views/admin/admin-activities/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/activities` | GET | 无 | `Activity[]` | 获取活动列表 | 正常 |
| 2 | `/api/admin/activities` | POST | `{ name, type, description, startTime, endTime, status }` | `Activity` | 添加活动 | 正常 |
| 3 | `/api/admin/activities/${id}` | PUT | `{ name, type, description, startTime, endTime, status }` | `Activity` | 更新活动 | 正常 |
| 4 | `/api/admin/activities/${id}` | DELETE | `id` (路径参数) | 无 | 删除活动 | 正常 |
| 5 | `/api/admin/activities/${id}/status` | PUT | `{ status: string }` | `Activity` | 切换活动状态 | 正常 |

### 引用 API 模块
```typescript
import { getActivities, addActivity, updateActivity, deleteActivity, toggleActivityStatus } from '@/api/admin'
```

### 说明
- 使用 `@/api/admin` 模块的 API 函数
- 支持按活动名称、类型、状态、时间范围筛选
- 支持启用/禁用活动

---

## 19. admin-tasks/index.vue - 任务管理

**文件路径**: `cg-vue/src/views/admin/admin-tasks/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/tasks` | GET | `{ page, pageSize, name, type, status, startDate, endDate }` | `Task[]` | 获取任务列表 | 正常 |
| 2 | `/api/admin/tasks` | POST | `{ name, type, description, cronExpression, executeTime }` | `Task` | 添加任务 | 正常 |
| 3 | `/api/admin/tasks/${id}` | PUT | `{ name, type, description, cronExpression, executeTime }` | `Task` | 更新任务 | 正常 |
| 4 | `/api/admin/tasks/${id}` | DELETE | `id` (路径参数) | 无 | 删除任务 | 正常 |
| 5 | `/api/admin/tasks/${id}/execute` | POST | `id` (路径参数) | `Task` | 立即执行任务 | 正常 |

### 引用 API 模块
```typescript
import { getTasks, addTask, updateTask, deleteTask, executeTask, type Task } from '@/api/admin'
```

### 说明
- 使用 `@/api/admin` 模块的 API 函数
- 支持按任务名称、类型、状态、时间范围筛选
- 支持定时任务、一次性任务、循环任务

---

## 20. shop-audit/index.vue - 店铺审核

**文件路径**: `cg-vue/src/views/admin/shop-audit/index.vue`

### API 调用清单

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 用途 | 状态 |
|------|----------|------|----------|----------|------|------|
| 1 | `/api/admin/shops/pending` | GET | `{ page, pageSize, keyword }` | `{ data: Shop[], total: number }` | 获取待审核店铺列表 | 正常 |
| 2 | `/api/admin/shops/${id}/audit` | PUT | `{ status: 'approved' \| 'rejected', reason?: string }` | `Shop` | 审核店铺 | 正常 |

### 引用 API 模块
```typescript
import { getPendingShops, auditShop, type Shop } from '@/api/admin'
```

### 说明
- 使用 `@/api/admin` 模块的 API 函数
- 支持批量通过和批量拒绝
- 与 `merchant-audit` 页面功能类似但 API 路径不同

---

## API 汇总统计

### 按页面统计

| 页面 | API 数量 | 状态 |
|------|----------|------|
| AdminLayout.vue | 0 | 未使用 |
| admin-dashboard/index.vue | 4 | 正常 |
| admin-users/index.vue | 5 | 正常 |
| user-detail/index.vue | 2 | 正常 |
| admin-merchants/index.vue | 5 | 正常 |
| merchant-detail/index.vue | 2 | 正常 |
| merchant-audit/index.vue | 2 | 正常 |
| admin-services/index.vue | 6 | 正常 |
| admin-products/index.vue | 6 | 正常 |
| product-manage/index.vue | 4 | 正常 |
| admin-reviews/index.vue | 2 | 正常 |
| review-audit/index.vue | 3 | 正常 |
| admin-announcements/index.vue | 4 | 正常 |
| announcement-edit/index.vue | 3 | 正常 |
| admin-system/index.vue | 0 | 未使用 |
| roles/index.vue | 5 | 正常 |
| logs/index.vue | 3 | 正常 |
| admin-activities/index.vue | 5 | 正常 |
| admin-tasks/index.vue | 5 | 正常 |
| shop-audit/index.vue | 2 | 正常 |

### 总计
- **总页面数**: 20 个
- **使用 API 的页面**: 18 个
- **未使用 API 的页面**: 2 个 (AdminLayout.vue, admin-system/index.vue)
- **API 调用总数**: 约 66 个不同端点

### API 模块引用情况

| API 模块 | 引用次数 | 实际使用次数 |
|----------|----------|--------------|
| `@/api/admin` | 16 | 约 5 (大部分页面直接使用 fetch) |
| `@/api/announcement` | 3 | 3 |

### 注意事项

1. **代码不一致问题**: 大部分页面虽然导入了 `@/api/admin` 的 API 函数，但实际代码中直接使用 `fetch` 调用，建议统一使用 API 模块
2. **admin-system 页面**: 当前为纯静态页面，系统设置 API 未实现
3. **重复 API**: `merchant-audit` 和 `shop-audit` 功能类似但使用不同 API 路径，建议合并
4. **错误处理**: 大部分页面有基本的错误处理和用户提示

---

*报告生成完成*

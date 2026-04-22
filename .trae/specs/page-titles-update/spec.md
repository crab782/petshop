# Page Titles Update Spec

## Why
The current implementation displays "Vite App" as the browser tab title for all pages, which provides poor UX and SEO. Users cannot distinguish between pages based on tab titles, and search engines index generic titles.

## What Changes
- Add dynamic page title management using Vue Router's `meta` field and a navigation guard
- Update `index.html` to use a base title with suffix " - 宠物服务平台"
- Define consistent title patterns: `{Page Name} - 宠物服务平台`
- Update all 68 page routes with appropriate titles
- Implement title updates for edit/detail pages that include dynamic identifiers

## Impact
- Affected specs: User experience improvements
- Affected code:
  - `petshop-vue/index.html` - Base title update
  - `petshop-vue/src/router/index.ts` - Add title meta to all routes + navigation guard
  - `petshop-vue/src/main.ts` - Ensure router is imported before app mount

## ADDED Requirements

### Requirement: Dynamic Page Title System
The system SHALL display a unique, descriptive title in the browser tab for each page.

#### Pattern: `{Page Title} - 宠物服务平台`

#### Scenario: User views user homepage
- **WHEN** user navigates to `/user/home`
- **THEN** browser tab displays "首页 - 宠物服务平台"

#### Scenario: User views detail page with ID
- **WHEN** user navigates to `/user/orders/detail/123`
- **THEN** browser tab displays "订单详情 #123 - 宠物服务平台"

### Naming Convention
| Page Type | Pattern | Example |
|-----------|---------|---------|
| List pages | {Feature}管理 | "服务管理 - 宠物服务平台" |
| Detail pages | {Feature}详情 | "商品详情 - 宠物服务平台" |
| Edit/Create pages | {Feature}{Add/Edit} | "编辑服务 - 宠物服务平台" |
| Dashboard/Home | 首页 | "首页 - 宠物服务平台" |
| Auth pages | {Page Name} | "登录 - 宠物服务平台" |

## Page Title Assignments

### User-Side Pages (28 pages)
| Route | Page Title |
|-------|------------|
| /user/home | 首页 |
| /user/services | 我的服务 |
| /user/services/list | 服务列表 |
| /user/services/detail/:id | 服务详情 |
| /user/pets | 我的宠物 |
| /user/pets/add | 添加宠物 |
| /user/pets/edit/:id | 编辑宠物 |
| /user/appointments | 我的预约 |
| /user/appointments/book | 预约服务 |
| /user/appointments/confirm | 确认预约 |
| /user/profile | 个人中心 |
| /user/profile/edit | 编辑资料 |
| /user/announcements | 公告列表 |
| /user/announcements/detail/:id | 公告详情 |
| /user/shop/:id | 店铺详情 |
| /user/cart | 购物车 |
| /user/checkout | 确认订单 |
| /user/pay | 支付页 |
| /user/merchant/:id | 商家详情 |
| /user/favorites | 我的收藏 |
| /user/reviews | 服务评价 |
| /user/reviews/my | 我的评价 |
| /user/orders | 我的订单 |
| /user/orders/detail/:id | 订单详情 |
| /user/search | 搜索页 |
| /user/notifications | 消息通知 |
| /user/addresses | 收货地址 |
| /user/product/detail/:id | 商品详情 |

### Merchant-Side Pages (16 pages)
| Route | Page Title |
|-------|------------|
| /merchant/home | 商家首页 |
| /merchant/services | 服务管理 |
| /merchant/services/add | 添加服务 |
| /merchant/services/edit/:id | 编辑服务 |
| /merchant/orders | 服务订单 |
| /merchant/products | 商品管理 |
| /merchant/products/add | 添加商品 |
| /merchant/products/edit/:id | 编辑商品 |
| /merchant/appointments | 预约管理 |
| /merchant/product-orders | 商品订单 |
| /merchant/reviews | 评价管理 |
| /merchant/categories | 分类管理 |
| /merchant/shop/edit | 店铺信息 |
| /merchant/shop/settings | 店铺设置 |
| /merchant/stats/appointments | 预约统计 |
| /merchant/stats/revenue | 营收统计 |

### Admin-Side Pages (20 pages)
| Route | Page Title |
|-------|------------|
| /admin/dashboard | 管理后台 |
| /admin/users | 用户管理 |
| /admin/users/:id | 用户详情 |
| /admin/merchants | 商家管理 |
| /admin/merchants/:id | 商家详情 |
| /admin/merchants/audit | 商家审核 |
| /admin/services | 服务管理 |
| /admin/products | 商品管理 |
| /admin/products/manage | 商品详情 |
| /admin/announcements | 公告管理 |
| /admin/announcements/edit | 编辑公告 |
| /admin/announcements/edit/:id | 编辑公告 |
| /admin/system | 系统设置 |
| /admin/system/roles | 角色管理 |
| /admin/system/logs | 操作日志 |
| /admin/reviews | 评价管理 |
| /admin/reviews/audit | 评价审核 |
| /admin/shop/audit | 店铺审核 |
| /admin/activities | 活动管理 |
| /admin/tasks | 任务管理 |

### Auth Pages (4 pages)
| Route | Page Title |
|-------|------------|
| /login | 用户登录 |
| /register | 用户注册 |
| /merchant/login | 商家登录 |
| /merchant/register | 商家入驻 |

## Technical Implementation

### Approach 1: Router Meta + Navigation Guard (Recommended)
- Add `title` field to each route's `meta` object
- Use router's `beforeEach` guard to update `document.title`
- For dynamic routes, use route params in title

### Approach 2: Per-Component Title Setting
- Each page component sets its own title in `onMounted`
- More flexible but requires changes in 68 files

**Decision**: Use Approach 1 (Router Meta + Navigation Guard) for centralized management and consistency.

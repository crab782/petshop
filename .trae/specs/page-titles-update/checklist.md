# Checklist

## Configuration

- [x] `index.html` base title updated from "Vite App" to "宠物服务平台"

## Router Updates

- [x] Navigation guard added to `router/index.ts`
- [x] `title` meta field structure defined
- [x] Default title handling implemented

## User-Side Routes (28 pages)

- [x] `/user/home` → "首页"
- [x] `/user/services` → "我的服务"
- [x] `/user/services/list` → "服务列表"
- [x] `/user/services/detail/:id` → "服务详情"
- [x] `/user/pets` → "我的宠物"
- [x] `/user/pets/add` → "添加宠物"
- [x] `/user/pets/edit/:id` → "编辑宠物"
- [x] `/user/appointments` → "我的预约"
- [x] `/user/appointments/book` → "预约服务"
- [x] `/user/appointments/confirm` → "确认预约"
- [x] `/user/profile` → "个人中心"
- [x] `/user/profile/edit` → "编辑资料"
- [x] `/user/announcements` → "公告列表"
- [x] `/user/announcements/detail/:id` → "公告详情"
- [x] `/user/shop/:id` → "店铺详情"
- [x] `/user/cart` → "购物车"
- [x] `/user/checkout` → "确认订单"
- [x] `/user/pay` → "支付页"
- [x] `/user/merchant/:id` → "商家详情"
- [x] `/user/favorites` → "我的收藏"
- [x] `/user/reviews` → "服务评价"
- [x] `/user/reviews/my` → "我的评价"
- [x] `/user/orders` → "我的订单"
- [x] `/user/orders/detail/:id` → "订单详情"
- [x] `/user/search` → "搜索页"
- [x] `/user/notifications` → "消息通知"
- [x] `/user/addresses` → "收货地址"
- [x] `/user/product/detail/:id` → "商品详情"

## Merchant-Side Routes (16 pages)

- [x] `/merchant/home` → "商家首页"
- [x] `/merchant/services` → "服务管理"
- [x] `/merchant/services/add` → "添加服务"
- [x] `/merchant/services/edit/:id` → "编辑服务"
- [x] `/merchant/orders` → "服务订单"
- [x] `/merchant/products` → "商品管理"
- [x] `/merchant/products/add` → "添加商品"
- [x] `/merchant/products/edit/:id` → "编辑商品"
- [x] `/merchant/appointments` → "预约管理"
- [x] `/merchant/product-orders` → "商品订单"
- [x] `/merchant/reviews` → "评价管理"
- [x] `/merchant/categories` → "分类管理"
- [x] `/merchant/shop/edit` → "店铺信息"
- [x] `/merchant/shop/settings` → "店铺设置"
- [x] `/merchant/stats/appointments` → "预约统计"
- [x] `/merchant/stats/revenue` → "营收统计"

## Admin-Side Routes (20 pages)

- [x] `/admin/dashboard` → "管理后台"
- [x] `/admin/users` → "用户管理"
- [x] `/admin/users/:id` → "用户详情"
- [x] `/admin/merchants` → "商家管理"
- [x] `/admin/merchants/:id` → "商家详情"
- [x] `/admin/merchants/audit` → "商家审核"
- [x] `/admin/services` → "服务管理"
- [x] `/admin/products` → "商品管理"
- [x] `/admin/products/manage` → "商品详情"
- [x] `/admin/announcements` → "公告管理"
- [x] `/admin/announcements/edit` → "编辑公告"
- [x] `/admin/announcements/edit/:id` → "编辑公告"
- [x] `/admin/system` → "系统设置"
- [x] `/admin/system/roles` → "角色管理"
- [x] `/admin/system/logs` → "操作日志"
- [x] `/admin/reviews` → "评价管理"
- [x] `/admin/reviews/audit` → "评价审核"
- [x] `/admin/shop/audit` → "店铺审核"
- [x] `/admin/activities` → "活动管理"
- [x] `/admin/tasks` → "任务管理"

## Auth Routes (4 pages)

- [x] `/login` → "用户登录"
- [x] `/register` → "用户注册"
- [x] `/merchant/login` → "商家登录"
- [x] `/merchant/register` → "商家入驻"

## Verification

- [x] All routes have unique, descriptive titles
- [x] Title pattern consistent: "{Page Title} - 宠物服务平台"
- [x] Dynamic routes (/:id) include identifier in title when possible

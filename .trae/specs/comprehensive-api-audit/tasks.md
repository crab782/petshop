# Tasks

## 用户端页面API审计（28个页面）

- [ ] Task 1: 审计用户首页相关页面
  - [ ] SubTask 1.1: user-home/index.vue
  - [ ] SubTask 1.2: user-profile/index.vue
  - [ ] SubTask 1.3: profile-edit/index.vue

- [ ] Task 2: 审计宠物相关页面
  - [ ] SubTask 2.1: user-pets/index.vue
  - [ ] SubTask 2.2: pet-edit/index.vue

- [ ] Task 3: 审计服务相关页面
  - [ ] SubTask 3.1: user-services/index.vue
  - [ ] SubTask 3.2: service-list/index.vue
  - [ ] SubTask 3.3: service-detail/index.vue

- [ ] Task 4: 审计预约相关页面
  - [ ] SubTask 4.1: user-appointments/index.vue
  - [ ] SubTask 4.2: user-book/index.vue
  - [ ] SubTask 4.3: appointment-confirm/index.vue

- [ ] Task 5: 审计订单相关页面
  - [ ] SubTask 5.1: user-orders/index.vue
  - [ ] SubTask 5.2: order-detail/index.vue
  - [ ] SubTask 5.3: checkout/index.vue
  - [ ] SubTask 5.4: pay/index.vue

- [ ] Task 6: 审计商家相关页面
  - [ ] SubTask 6.1: user-merchant/index.vue
  - [ ] SubTask 6.2: user-shop/index.vue
  - [ ] SubTask 6.3: user-favorites/index.vue

- [ ] Task 7: 审计商品相关页面
  - [ ] SubTask 7.1: product-detail/index.vue
  - [ ] SubTask 7.2: user-cart/index.vue

- [ ] Task 8: 审计评价相关页面
  - [ ] SubTask 8.1: user-reviews/index.vue
  - [ ] SubTask 8.2: my-reviews/index.vue

- [ ] Task 9: 审计其他用户页面
  - [ ] SubTask 9.1: search/index.vue
  - [ ] SubTask 9.2: notifications/index.vue
  - [ ] SubTask 9.3: user-announcements/index.vue
  - [ ] SubTask 9.4: announcement-detail/index.vue
  - [ ] SubTask 9.5: addresses/index.vue

## 商家端页面API审计（20个页面）

- [ ] Task 10: 审计商家首页相关页面
  - [ ] SubTask 10.1: home/index.vue
  - [ ] SubTask 10.2: merchant-home/index.vue

- [ ] Task 11: 审计服务管理页面
  - [ ] SubTask 11.1: services/index.vue
  - [ ] SubTask 11.2: merchant-services/index.vue
  - [ ] SubTask 11.3: service-edit/index.vue

- [ ] Task 12: 审计商品管理页面
  - [ ] SubTask 12.1: merchant-products/index.vue
  - [ ] SubTask 12.2: product-edit/index.vue

- [ ] Task 13: 审计订单管理页面
  - [ ] SubTask 13.1: appointments/index.vue
  - [ ] SubTask 13.2: merchant-appointments/index.vue
  - [ ] SubTask 13.3: merchant-orders/index.vue
  - [ ] SubTask 13.4: merchant-product-orders/index.vue

- [ ] Task 14: 审计评价管理页面
  - [ ] SubTask 14.1: reviews/index.vue
  - [ ] SubTask 14.2: merchant-reviews/index.vue

- [ ] Task 15: 审计店铺管理页面
  - [ ] SubTask 15.1: shop-edit/index.vue
  - [ ] SubTask 15.2: shop-settings/index.vue
  - [ ] SubTask 15.3: categories/index.vue

- [ ] Task 16: 审计统计页面
  - [ ] SubTask 16.1: stats-appointments/index.vue
  - [ ] SubTask 16.2: stats-revenue/index.vue

- [ ] Task 17: 审计其他商家页面
  - [ ] SubTask 17.1: Register.vue
  - [ ] SubTask 17.2: Login.vue

## 平台端页面API审计（20个页面）

- [ ] Task 18: 审计平台首页
  - [ ] SubTask 18.1: admin-dashboard/index.vue

- [ ] Task 19: 审计用户管理页面
  - [ ] SubTask 19.1: admin-users/index.vue
  - [ ] SubTask 19.2: user-detail/index.vue

- [ ] Task 20: 审计商家管理页面
  - [ ] SubTask 20.1: admin-merchants/index.vue
  - [ ] SubTask 20.2: merchant-detail/index.vue
  - [ ] SubTask 20.3: merchant-audit/index.vue

- [ ] Task 21: 审计服务管理页面
  - [ ] SubTask 21.1: admin-services/index.vue

- [ ] Task 22: 审计商品管理页面
  - [ ] SubTask 22.1: admin-products/index.vue
  - [ ] SubTask 22.2: product-manage/index.vue

- [ ] Task 23: 审计评价管理页面
  - [ ] SubTask 23.1: admin-reviews/index.vue
  - [ ] SubTask 23.2: review-audit/index.vue

- [ ] Task 24: 审计公告管理页面
  - [ ] SubTask 24.1: admin-announcements/index.vue
  - [ ] SubTask 24.2: announcement-edit/index.vue

- [ ] Task 25: 审计系统管理页面
  - [ ] SubTask 25.1: admin-system/index.vue
  - [ ] SubTask 25.2: roles/index.vue
  - [ ] SubTask 25.3: logs/index.vue

- [ ] Task 26: 审计其他平台页面
  - [ ] SubTask 26.1: admin-activities/index.vue
  - [ ] SubTask 26.2: admin-tasks/index.vue
  - [ ] SubTask 26.3: shop-audit/index.vue

## 生成报告

- [ ] Task 27: 生成API审计报告
  - [ ] SubTask 27.1: 汇总所有API调用信息
  - [ ] SubTask 27.2: 生成详细的API文档
  - [ ] SubTask 27.3: 标记异常和未使用的API

# Task Dependencies
- Task 27 依赖 Task 1-26（需要所有页面审计完成后才能生成报告）

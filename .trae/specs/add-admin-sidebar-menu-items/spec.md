# 添加平台端左侧边栏菜单项并优化认证逻辑 Spec

## Why
平台端左侧边栏缺少多个重要功能的导航入口，用户需要手动输入URL才能访问这些页面。同时，某些页面在没有登录时会自动跳转到登录页，导致用户无法查看页面结构。

## What Changes
- 在 AdminLayout.vue 的左侧边栏菜单中添加7个新的菜单项
- 优化页面加载逻辑，在没有登录时显示页面结构而不是自动跳转

## Impact
- 受影响的规范：平台端导航系统
- 受影响的代码：
  - petshop-vue/src/views/admin/AdminLayout.vue
  - petshop-vue/src/views/admin/roles/index.vue
  - petshop-vue/src/views/admin/logs/index.vue
  - petshop-vue/src/views/admin/review-audit/index.vue
  - petshop-vue/src/views/admin/shop-audit/index.vue
  - petshop-vue/src/views/admin/admin-activities/index.vue
  - petshop-vue/src/views/admin/admin-tasks/index.vue

## ADDED Requirements

### Requirement: 平台端左侧边栏菜单扩展
系统应在平台端左侧边栏添加以下菜单项：

#### Scenario: 添加商家审核菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"商家审核"菜单项，点击后跳转到 /admin/merchants/audit

#### Scenario: 添加角色管理菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"角色管理"菜单项，点击后跳转到 /admin/system/roles

#### Scenario: 添加操作日志菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"操作日志"菜单项，点击后跳转到 /admin/system/logs

#### Scenario: 添加评价审核菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"评价审核"菜单项，点击后跳转到 /admin/reviews/audit

#### Scenario: 添加店铺审核菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"店铺审核"菜单项，点击后跳转到 /admin/shop/audit

#### Scenario: 添加活动管理菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"活动管理"菜单项，点击后跳转到 /admin/activities

#### Scenario: 添加任务管理菜单项
- **WHEN** 用户访问平台端页面
- **THEN** 左侧边栏显示"任务管理"菜单项，点击后跳转到 /admin/tasks

### Requirement: 页面加载优化
系统应在没有登录时也能显示页面结构，而不是自动跳转到登录页：

#### Scenario: 未登录访问页面
- **WHEN** 用户未登录访问平台端页面
- **THEN** 系统显示页面结构和错误提示，而不是自动跳转到登录页

## MODIFIED Requirements

### Requirement: 菜单项组织结构
菜单项应按照功能分组组织：

**现有菜单项**：
1. 后台首页
2. 用户管理
3. 商家管理
4. 服务管理
5. 商品管理
6. 预约管理
7. 宠物管理
8. 评价管理
9. 公告管理
10. 系统设置

**新增菜单项**：
- 商家审核（/admin/merchants/audit）
- 角色管理（/admin/system/roles）
- 操作日志（/admin/system/logs）
- 评价审核（/admin/reviews/audit）
- 店铺审核（/admin/shop/audit）
- 活动管理（/admin/activities）
- 任务管理（/admin/tasks）

## REMOVED Requirements
无移除的需求。

# 添加用户端左侧边栏菜单项 Spec

## Why
用户端左侧边栏缺少多个重要功能的导航入口，用户需要手动输入URL才能访问这些页面。需要添加服务列表、预约服务、编辑资料、公告列表、店铺详情、购物车、商家详情、服务评价、我的评价、搜索页、消息通知、收货地址等菜单项，提升用户体验和导航便利性。

## What Changes
- 在 UserLayout.vue 的左侧边栏菜单中添加12个新的菜单项
- 导入所需的图标组件
- 按照功能分组组织菜单项

## Impact
- 受影响的规范：用户端导航系统
- 受影响的代码：petshop-vue/src/views/user/UserLayout.vue

## ADDED Requirements

### Requirement: 用户端左侧边栏菜单扩展
系统应在用户端左侧边栏添加以下菜单项：

#### Scenario: 添加服务列表菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"服务列表"菜单项，点击后跳转到 /user/services/list

#### Scenario: 添加预约服务菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"预约服务"菜单项，点击后跳转到 /user/appointments/book

#### Scenario: 添加编辑资料菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"编辑资料"菜单项，点击后跳转到 /user/profile/edit

#### Scenario: 添加公告列表菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"公告列表"菜单项，点击后跳转到 /user/announcements

#### Scenario: 添加店铺详情菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"店铺详情"菜单项，点击后跳转到 /user/shop

#### Scenario: 添加购物车菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"购物车"菜单项，点击后跳转到 /user/cart

#### Scenario: 添加商家详情菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"商家详情"菜单项，点击后跳转到 /user/merchant/1

#### Scenario: 添加服务评价菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"服务评价"菜单项，点击后跳转到 /user/reviews

#### Scenario: 添加我的评价菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"我的评价"菜单项，点击后跳转到 /user/reviews/my

#### Scenario: 添加搜索页菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"搜索页"菜单项，点击后跳转到 /user/search

#### Scenario: 添加消息通知菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"消息通知"菜单项，点击后跳转到 /user/notifications

#### Scenario: 添加收货地址菜单项
- **WHEN** 用户访问用户端页面
- **THEN** 左侧边栏显示"收货地址"菜单项，点击后跳转到 /user/addresses

## MODIFIED Requirements

### Requirement: 菜单项组织结构
菜单项应按照功能分组组织：

**现有菜单项**：
1. 商店浏览
2. 服务浏览
3. 宠物管理
4. 预约管理
5. 订单管理
6. 收藏评价
7. 个人中心

**新增菜单项**：
- 服务列表（/user/services/list）
- 预约服务（/user/appointments/book）
- 编辑资料（/user/profile/edit）
- 公告列表（/user/announcements）
- 店铺详情（/user/shop）
- 购物车（/user/cart）
- 商家详情（/user/merchant/1）
- 服务评价（/user/reviews）
- 我的评价（/user/reviews/my）
- 搜索页（/user/search）
- 消息通知（/user/notifications）
- 收货地址（/user/addresses）

## REMOVED Requirements
无移除的需求。

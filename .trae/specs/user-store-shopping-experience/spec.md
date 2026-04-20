# 用户端商店浏览与购物车功能完善 Spec

## Why
用户端首页缺少商店浏览入口，无法从首页直接浏览和进入店铺；店铺详情页使用硬编码 mock 数据而非真实 API；购物车图标未在导航栏显示；支付流程未与后端对接。需要完善用户端的商店浏览、购物和支付全流程。

## What Changes
- 更新 AGENTS.md 中所有 `cg-vue` 引用为 `petshop-vue`
- 在用户端左侧菜单栏添加"商店浏览"导航入口
- 改造用户端首页为商店浏览首页，展示店铺卡片列表
- 改造店铺详情页使用真实 API 替代硬编码 mock 数据
- 在顶部导航栏添加购物车图标及商品数量徽标
- 完善购物车与支付模拟流程

## Impact
- Affected specs: cg-vue-restructure, user-frontend-revamp
- Affected code:
  - `AGENTS.md`
  - `petshop-vue/src/views/user/UserLayout.vue`
  - `petshop-vue/src/views/user/user-home/index.vue`
  - `petshop-vue/src/views/user/user-shop/index.vue`
  - `petshop-vue/src/views/user/user-cart/index.vue`
  - `petshop-vue/src/views/user/checkout/index.vue`
  - `petshop-vue/src/views/user/pay/index.vue`
  - `petshop-vue/src/api/user.ts`

## ADDED Requirements

### Requirement: 商店浏览首页
用户端首页 SHALL 展示已审核通过的商家列表，以卡片形式展示商家名称、Logo、地址、评分和服务数量。

#### Scenario: 用户浏览商店列表
- **WHEN** 用户访问 `/user/home`
- **THEN** 页面展示商家卡片列表，每个卡片包含商家名称、Logo、地址、评分
- **AND** 点击卡片跳转到对应商家详情页 `/user/merchant/{id}`

#### Scenario: 搜索商店
- **WHEN** 用户在首页搜索框输入关键词
- **THEN** 商家列表按关键词过滤显示

### Requirement: 导航栏购物车图标
用户端顶部导航栏 SHALL 显示购物车图标及商品数量徽标。

#### Scenario: 购物车有商品
- **WHEN** 购物车中有 N 件商品
- **THEN** 导航栏购物车图标显示数量 N 的徽标

#### Scenario: 点击购物车图标
- **WHEN** 用户点击购物车图标
- **THEN** 跳转到 `/user/cart` 页面

### Requirement: 支付模拟流程
用户端 SHALL 提供模拟支付流程，包含选择支付方式、模拟支付处理、支付结果反馈。

#### Scenario: 模拟支付成功
- **WHEN** 用户在支付页选择支付方式并点击确认支付
- **THEN** 系统模拟支付处理（2秒延迟），显示支付成功结果
- **AND** 跳转到订单详情页

## MODIFIED Requirements

### Requirement: 店铺详情页使用真实API
店铺详情页 SHALL 使用后端真实 API 获取商家信息、服务列表、商品列表和评价列表，而非硬编码 mock 数据。

#### Scenario: 加载店铺详情
- **WHEN** 用户访问 `/user/merchant/{id}`
- **THEN** 页面通过 API 获取商家信息、服务、商品和评价
- **AND** 移除所有硬编码 mock 数据和 DEV 环境判断逻辑

### Requirement: 左侧菜单导航更新
用户端左侧菜单 SHALL 包含"商店浏览"入口，指向 `/user/home`。

#### Scenario: 菜单显示
- **WHEN** 用户查看左侧菜单
- **THEN** 菜单包含"商店浏览"项，图标为 Shop，点击跳转 `/user/home`

## REMOVED Requirements
无

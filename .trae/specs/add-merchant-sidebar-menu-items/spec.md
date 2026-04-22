# 添加商家端左侧边栏菜单项 Spec

## Why
商家端左侧边栏缺少多个重要功能的导航入口，用户需要手动输入URL才能访问这些页面。需要添加服务订单、商品管理、商品订单、分类管理、店铺设置、预约统计和营收统计的菜单项，提升用户体验和导航便利性。

## What Changes
- 在 MerchantLayout.vue 的左侧边栏菜单中添加7个新的菜单项
- 导入所需的图标组件
- 按照功能分组组织菜单项

## Impact
- 受影响的规范：商家端导航系统
- 受影响的代码：petshop-vue/src/views/merchant/MerchantLayout.vue

## ADDED Requirements

### Requirement: 商家端左侧边栏菜单扩展
系统应在商家端左侧边栏添加以下菜单项：

#### Scenario: 添加服务订单菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"服务订单"菜单项，点击后跳转到 /merchant/orders

#### Scenario: 添加商品管理菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"商品管理"菜单项，点击后跳转到 /merchant/products

#### Scenario: 添加商品订单菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"商品订单"菜单项，点击后跳转到 /merchant/product-orders

#### Scenario: 添加分类管理菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"分类管理"菜单项，点击后跳转到 /merchant/categories

#### Scenario: 添加店铺设置菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"店铺设置"菜单项，点击后跳转到 /merchant/shop/settings

#### Scenario: 添加预约统计菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"预约统计"菜单项，点击后跳转到 /merchant/stats/appointments

#### Scenario: 添加营收统计菜单项
- **WHEN** 用户访问商家端页面
- **THEN** 左侧边栏显示"营收统计"菜单项，点击后跳转到 /merchant/stats/revenue

## MODIFIED Requirements

### Requirement: 菜单项组织结构
菜单项应按照功能分组组织：

**现有菜单项**：
1. 后台首页
2. 店铺管理
3. 预约订单列表
4. 服务管理
5. 服务评价列表

**新增菜单项**（按功能分组）：
- **订单管理组**：
  - 服务订单（/merchant/orders）
  - 商品订单（/merchant/product-orders）
- **商品管理组**：
  - 商品管理（/merchant/products）
  - 分类管理（/merchant/categories）
- **设置组**：
  - 店铺设置（/merchant/shop/settings）
- **统计组**：
  - 预约统计（/merchant/stats/appointments）
  - 营收统计（/merchant/stats/revenue）

## REMOVED Requirements
无移除的需求。

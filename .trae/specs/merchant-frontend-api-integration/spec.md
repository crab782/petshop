# 商家端前端API集成改造规格

## Why
商家端前端页面目前使用硬编码的模拟数据，无法与后端真实API进行数据交互。需要移除所有模拟数据，集成真实后端API，实现完整的数据交互功能，提升用户体验和系统可用性。

## What Changes
- 移除所有页面中的硬编码模拟数据
- 集成真实后端API接口实现数据交互
- 添加加载状态提示（loading状态）
- 实现完整的错误处理机制
- 添加表单验证和提交功能
- 实现状态管理和数据刷新

## Impact
- 受影响的页面：商家端20个页面
- 受影响的代码：
  - `cg-vue/src/views/merchant/**/*.vue` - 所有商家端页面组件
  - `cg-vue/src/api/merchant.ts` - API接口定义
  - `cg-vue/src/composables/` - 可复用的组合式函数

## ADDED Requirements

### Requirement: API数据集成
系统应将所有硬编码模拟数据替换为真实后端API调用。

#### Scenario: 页面数据加载
- **WHEN** 商家进入页面
- **THEN** 系统应调用对应API获取数据
- **AND** 显示加载状态直到数据返回
- **AND** 数据加载成功后渲染页面内容

#### Scenario: 表单提交
- **WHEN** 商家提交表单（添加/编辑服务、商品等）
- **THEN** 系统应调用对应的API接口
- **AND** 显示提交中状态
- **AND** 提交成功后显示成功提示并刷新列表
- **AND** 提交失败时显示错误信息

### Requirement: 加载状态管理
系统应为所有异步操作提供加载状态反馈。

#### Scenario: 页面加载状态
- **WHEN** 页面发起数据请求
- **THEN** 显示加载指示器（骨架屏或loading动画）
- **AND** 禁用相关操作按钮防止重复提交

#### Scenario: 操作进行中状态
- **WHEN** 用户执行操作（如删除、更新状态）
- **THEN** 按钮显示loading状态
- **AND** 操作完成后恢复正常状态

### Requirement: 错误处理机制
系统应实现完整的错误处理机制。

#### Scenario: API请求失败
- **WHEN** API请求失败（网络错误、服务器错误等）
- **THEN** 显示友好的错误提示信息
- **AND** 提供重试选项

#### Scenario: 表单验证错误
- **WHEN** 用户提交无效表单数据
- **THEN** 显示字段级别的验证错误信息
- **AND** 阻止表单提交

#### Scenario: 权限错误处理
- **WHEN** 用户未登录或会话过期
- **THEN** 重定向到登录页面
- **AND** 保存当前页面路径以便登录后返回

### Requirement: 数据状态管理
系统应正确管理页面数据状态。

#### Scenario: 数据刷新
- **WHEN** 用户执行操作成功（添加、编辑、删除）
- **THEN** 自动刷新相关数据列表
- **AND** 保持当前筛选和分页状态

## 页面改造清单

| 序号 | 页面名称 | 主要改造内容 |
|------|----------|--------------|
| 1 | MerchantLayout.vue | 商家信息API、退出登录 |
| 2 | Register.vue | 注册API、表单验证 |
| 3 | home/index.vue | 统计数据、订单列表、评价列表 |
| 4 | services/index.vue | 服务列表、搜索筛选、批量操作 |
| 5 | service-edit/index.vue | 服务详情、保存服务 |
| 6 | merchant-products/index.vue | 商品列表、搜索筛选、批量操作 |
| 7 | product-edit/index.vue | 商品详情、保存商品 |
| 8 | appointments/index.vue | 预约列表、状态更新 |
| 9 | merchant-orders/index.vue | 订单列表、状态更新 |
| 10 | merchant-product-orders/index.vue | 商品订单、发货功能 |
| 11 | reviews/index.vue | 评价列表、回复、删除 |
| 12 | shop-edit/index.vue | 店铺信息、保存 |
| 13 | shop-settings/index.vue | 店铺设置、密码修改 |
| 14 | categories/index.vue | 分类CRUD、批量操作 |
| 15 | stats-appointments/index.vue | 预约统计、导出 |
| 16 | stats-revenue/index.vue | 营收统计、导出 |
| 17 | merchant-home/index.vue | 同home/index.vue |
| 18 | merchant-appointments/index.vue | 同appointments/index.vue |
| 19 | merchant-services/index.vue | 同services/index.vue |
| 20 | merchant-reviews/index.vue | 同reviews/index.vue |

## 技术实现要求

### 组合式函数（Composables）
创建可复用的组合式函数：
- `useAsync.ts` - 异步操作状态管理（loading, error, data）
- `usePagination.ts` - 分页逻辑（page, pageSize, total, changePage）
- `useSearch.ts` - 搜索筛选逻辑（keyword, filters, debounce）
- `useForm.ts` - 表单处理逻辑（validation, submit, reset）

### 错误处理
- 使用全局错误拦截器处理通用错误
- 页面级别处理业务错误
- 表单级别处理验证错误

### 加载状态
- 页面级加载：使用骨架屏
- 操作级加载：按钮loading状态
- 局部加载：使用Element Plus的v-loading指令

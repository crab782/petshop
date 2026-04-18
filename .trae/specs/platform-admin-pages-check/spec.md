# 平台端页面全面检查与功能实现 - 产品需求文档

## 概述
- **摘要**：根据 `d:\j\cg\cg\AGENTS.md` 中详细说明的平台端20个页面及其对应的功能模块需求，对前端项目的平台端页面进行全面检查与功能实现。
- **目的**：系统梳理每个页面的具体功能模块定义、交互逻辑及技术要求，逐一核对现有实现情况，实现所有缺失的功能模块。
- **目标用户**：平台管理员

## 目标
- 系统梳理AGENTS.md中平台端20个页面的功能需求
- 逐一核对前端项目中对应页面的现有实现情况
- 识别功能缺失、实现偏差或未开发的模块
- 按照页面优先级顺序实现所有缺失的功能模块
- 确保每个模块的交互逻辑、数据处理及UI展示均符合规范要求

## 非目标（范围外）
- 不修改后端API接口
- 不修改商家端和用户端页面（仅限平台端）
- 不添加新的功能特性（仅实现AGENTS.md中已有的功能）

## 平台端页面功能模块汇总（共20个）

| 序号 | 页面名称 | 主要功能模块 |
|------|----------|--------------|
| 1 | AdminLayout.vue | 导航菜单、管理员信息、退出登录 |
| 2 | admin-dashboard/index.vue | 统计概览、快捷操作、最近数据 |
| 3 | admin-users/index.vue | 用户管理、状态管理、批量操作 |
| 4 | user-detail/index.vue | 用户详情、宠物、订单、评价 |
| 5 | admin-merchants/index.vue | 商家管理、状态管理、批量操作 |
| 6 | merchant-detail/index.vue | 商家详情、服务、商品、订单、评价 |
| 7 | merchant-audit/index.vue | 商家入驻审核、状态管理 |
| 8 | admin-services/index.vue | 服务管理、搜索筛选、批量操作 |
| 9 | admin-products/index.vue | 商品管理、搜索筛选、批量操作 |
| 10 | product-manage/index.vue | 商品详情、编辑操作 |
| 11 | admin-reviews/index.vue | 评价管理、审核、批量操作 |
| 12 | review-audit/index.vue | 评价审核、状态管理 |
| 13 | admin-announcements/index.vue | 公告管理、发布、批量操作 |
| 14 | announcement-edit/index.vue | 公告编辑、富文本、保存 |
| 15 | admin-system/index.vue | 系统设置、配置管理 |
| 16 | roles/index.vue | 角色管理、权限分配 |
| 17 | logs/index.vue | 操作日志、搜索筛选、导出 |
| 18 | admin-activities/index.vue | 活动管理、状态管理 |
| 19 | admin-tasks/index.vue | 任务管理、执行状态 |
| 20 | shop-audit/index.vue | 店铺审核、状态管理 |

## 功能需求详情

### FR-1: AdminLayout.vue - 平台布局组件
- 顶部导航栏：显示平台Logo、名称、通知图标、管理员头像下拉菜单
- 左侧菜单栏：根据权限显示菜单项（首页、用户管理、商家管理、服务管理、商品管理、订单管理、评价管理、系统设置等）
- 退出登录功能：清除登录状态并跳转到登录页

### FR-2: admin-dashboard/index.vue - 平台首页
- 统计概览卡片：总用户数、总商家数、今日订单数、本月营收
- 最近注册用户列表
- 待审核商家列表
- 系统公告
- 快捷操作入口

### FR-3: admin-users/index.vue - 用户管理
- 用户表格：用户ID、用户名、邮箱、电话、注册时间、状态
- 搜索和筛选
- 分页功能
- 批量操作：批量禁用、批量启用、批量删除

### FR-4: user-detail/index.vue - 用户详情
- 基本信息展示
- 宠物列表
- 订单记录
- 评价记录
- 操作功能

### FR-5: admin-merchants/index.vue - 商家管理
- 商家表格
- 搜索和筛选
- 分页功能
- 批量操作

### FR-6: merchant-detail/index.vue - 商家详情
- 基本信息
- 服务列表
- 商品列表
- 订单记录
- 评价记录

### FR-7: merchant-audit/index.vue - 商家入驻审核
- 待审核商家列表
- 审核操作：通过/拒绝

### FR-8: admin-services/index.vue - 服务管理
- 服务表格
- 搜索和筛选
- 批量操作

### FR-9: admin-products/index.vue - 商品管理
- 商品表格
- 搜索和筛选
- 批量操作

### FR-10: product-manage/index.vue - 商品详情
- 商品信息展示
- 编辑操作

### FR-11: admin-reviews/index.vue - 评价管理
- 评价表格
- 搜索和筛选
- 批量操作

### FR-12: review-audit/index.vue - 评价审核
- 待审核评价列表
- 审核操作

### FR-13: admin-announcements/index.vue - 公告管理
- 公告列表
- 添加公告
- 批量操作

### FR-14: announcement-edit/index.vue - 发布/编辑公告
- 公告表单
- 富文本编辑
- 保存功能

### FR-15: admin-system/index.vue - 系统设置
- 基本设置
- 邮件设置
- 短信设置
- 支付设置

### FR-16: roles/index.vue - 角色权限管理
- 角色列表
- 添加/编辑角色
- 权限设置

### FR-17: logs/index.vue - 操作日志
- 日志表格
- 搜索和筛选
- 导出功能

### FR-18: admin-activities/index.vue - 活动管理
- 活动列表
- 添加活动
- 搜索和筛选

### FR-19: admin-tasks/index.vue - 任务管理
- 任务列表
- 添加任务
- 搜索和筛选

### FR-20: shop-audit/index.vue - 店铺审核
- 待审核店铺列表
- 审核操作

## 验收标准

### AC-1: 所有20个页面均存在且可访问
- **Given**：用户访问平台端
- **When**：用户通过导航菜单访问任意页面
- **Then**：页面正常加载，显示对应功能模块
- **Verification**：`programmatic` - 路由配置完整

### AC-2: 每个页面的核心功能模块完整实现
- **Given**：用户访问具体页面
- **When**：用户进行相应操作
- **Then**：功能模块按预期工作
- **Verification**：`human-judgment` - 功能验收

### AC-3: 样式与旧项目保持一致
- **Given**：平台端所有页面
- **When**：页面渲染
- **Then**：样式与AGENTS.md中的功能定义一致
- **Verification**：`human-judgment` - 视觉验收

## 约束
- **技术栈**：Vue 3、TypeScript、Element Plus
- **图标**：Font Awesome
- **样式**：Tailwind CSS风格的自定义样式

## 假设
- 后端API接口已实现且可用
- 数据库表结构已按AGENTS.md定义创建
- 项目的路由配置已正确设置
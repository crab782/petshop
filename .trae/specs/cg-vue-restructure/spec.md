# 宠物服务系统前后端分离重构规范（第二阶段）

## Why

当前项目是一个复杂的宠物服务平台，包含：
1. **宠物垂直电商功能**：购物车、下单、支付等
2. **预约宠物服务功能**：类似医疗预约系统

第一阶段已完成基础页面建设，但仍缺少大量核心功能页面，需要继续补充完善。

## What Changes

补充以下缺失的功能页面：

### 1. 用户端新增页面

| 页面 | 路由路径 | 功能说明 |
|------|---------|---------|
| 首页 | /home | 含Banner轮播、推荐服务、推荐商品 |
| 忘记密码页 | /forgot-password | 邮箱验证、重置密码 |
| 公告详情页 | /user/announcement/:id | 查看公告详细内容 |
| 商品详情页 | /user/product/:id | 商品信息、立即购买 |
| 购物车页 | /user/cart | 购物车商品列表、修改数量、删除 |
| 下单页 | /user/checkout | 确认订单信息、选择地址 |
| 支付页 | /user/pay/:orderId | 模拟支付（余额/支付宝/微信） |
| 服务列表页 | /user/service-list | 分类展示服务（美容/寄养/体检） |
| 服务详情页 | /user/service/:id | 服务价格、时长、预约 |
| 预约确认页 | /user/appointment/confirm | 选择宠物、时间、确认预约 |
| 订单详情页 | /user/order/:id | 订单完整信息、状态跟踪 |
| 个人资料编辑页 | /user/profile/edit | 修改昵称、手机号、头像 |
| 新增/编辑宠物页 | /user/pet/edit | 添加或编辑宠物信息 |
| 地址管理页 | /user/addresses | 收货地址列表、添加、编辑、删除 |
| 我的评价页 | /user/my-reviews | 用户发表的所有评价 |
| 搜索页 | /user/search | 商品和服务搜索 |
| 消息通知页 | /user/notifications | 系统消息、订单通知 |

### 2. 商家端新增页面

| 页面 | 路由路径 | 功能说明 |
|------|---------|---------|
| 店铺信息编辑页 | /merchant/shop/edit | 编辑店铺名称、logo、简介 |
| 店铺设置页 | /merchant/shop/settings | 营业时间、休息日设置 |
| 商品列表页 | /merchant/products | 商品管理列表 |
| 新增/编辑商品页 | /merchant/product/edit | 添加或编辑商品 |
| 商品分类管理 | /merchant/categories | 商品分类增删改 |
| 服务项目列表 | /merchant/services | 服务项目管理 |
| 服务定价编辑 | /merchant/service/edit | 编辑服务价格、时长 |
| 订单处理页 | /merchant/orders | 订单列表、接单/拒单 |
| 回复评价页 | /merchant/reviews | 回复用户评价 |
| 营业额统计页 | /merchant/stats/revenue | 营业额趋势、统计 |
| 预约统计页 | /merchant/stats/appointments | 预约量统计、分析 |

### 3. 管理员端新增页面

| 页面 | 路由路径 | 功能说明 |
|------|---------|---------|
| 用户详情页 | /admin/user/:id | 用户详细信息、行为记录 |
| 商家审核页 | /admin/merchant/audit | 待审核商家列表、审核操作 |
| 店铺详情页 | /admin/merchant/:id | 商家详细信息、状态管理 |
| 店铺审核页 | /admin/shop/audit | 店铺入驻审核 |
| 商品上下架 | /admin/product/manage | 商品审核、上下架管理 |
| 发布/编辑公告页 | /admin/announcement/edit | 创建或编辑公告 |
| 角色权限管理 | /admin/roles | 系统角色配置、权限分配 |
| 操作日志页 | /admin/logs | 用户操作日志查询 |
| 评价审核页 | /admin/review/audit | 评价审核、删除违规评价 |

## 技术说明

### 项目特点
- **宠物垂直电商**：商品浏览、购物车、下单支付
- **预约服务系统**：类似医疗预约，选择时间、宠物、商家

### API设计考虑
- 电商相关：商品、购物车、订单、支付
- 服务预约：服务、时间段、预约确认
- 消息通知：系统通知、订单状态推送

### 页面复杂度分级

**高复杂度页面**：
- 首页（多模块、Banner轮播）
- 购物车页
- 下单/支付流程页
- 搜索页

**中复杂度页面**：
- 服务/商品详情页
- 订单详情页
- 各种列表页

**低复杂度页面**：
- 忘记密码页
- 地址管理页
- 消息通知页
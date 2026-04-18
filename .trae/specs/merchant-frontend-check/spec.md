# 宠物服务平台商家端页面功能检查与实现 - 产品需求文档

## 概述
- **摘要**：根据 `d:\j\cg\cg\AGENTS.md` 中详细定义的商家端20个页面功能模块需求，对前端项目进行全面检查，识别功能缺失或实现偏差，并按优先级实现所有缺失的功能模块，确保商家端功能完整且符合规范。
- **目的**：确保商家端所有页面功能符合AGENTS.md中的需求定义，实现完整的功能模块，提升商家用户体验和系统稳定性。
- **目标用户**：平台商家，需要使用商家管理系统进行日常运营和管理。

## 目标
- 系统梳理AGENTS.md中每个商家端页面的具体功能模块定义
- 逐一核对前端项目对应页面的现有实现情况
- 识别功能缺失、实现偏差或未开发的模块
- 按照页面优先级顺序实现所有缺失的功能模块
- 确保每个模块的交互逻辑、数据处理及UI展示符合规范要求

## 非目标（范围外）
- 不修改后端API接口（仅对接现有API）
- 不重新设计用户端页面
- 不修改平台端页面
- 不进行大规模的代码重构

## 背景与上下文
- 前端项目使用Vue 3 + TypeScript + Element Plus构建
- 后端项目使用Spring Boot框架
- 数据库使用MySQL，已建立完整的表结构
- AGENTS.md中详细定义了20个商家端页面的功能模块需求
- 需要确保前端功能与AGENTS.md规范一致

## 功能需求

### FR-1：商家注册功能
- 实现商家注册页面Register.vue
- 支持商家名称、联系人、电话、邮箱、密码、地址等字段
- 实现表单验证和错误提示
- 提交后状态默认为pending

### FR-2：商家首页功能
- 实现统计概览卡片（今日订单数、待处理预约数、今日收入、平均评分）
- 实现最近订单列表
- 实现最新评价列表
- 实现快捷操作入口

### FR-3：服务管理功能
- 实现服务列表页面services/index.vue和merchant-services/index.vue
- 支持服务的CRUD操作
- 实现搜索和筛选功能
- 实现分页功能

### FR-4：服务编辑功能
- 实现服务编辑页面service-edit/index.vue
- 支持新增和编辑服务
- 实现图片上传功能
- 实现表单验证

### FR-5：商品管理功能
- 实现商品列表页面merchant-products/index.vue
- 支持商品的CRUD操作
- 实现库存预警显示
- 实现搜索和筛选功能

### FR-6：商品编辑功能
- 实现商品编辑页面product-edit/index.vue
- 支持新增和编辑商品
- 实现库存管理功能
- 实现图片上传功能

### FR-7：预约订单管理功能
- 实现预约列表页面appointments/index.vue和merchant-appointments/index.vue
- 支持预约状态流转管理
- 实现搜索和筛选功能
- 实现分页和导出功能

### FR-8：服务订单管理功能
- 实现服务订单列表页面merchant-orders/index.vue
- 支持订单状态流转
- 实现订单详情查看

### FR-9：商品订单管理功能
- 实现商品订单列表页面merchant-product-orders/index.vue
- 支持发货管理功能
- 实现订单详情弹窗
- 实现物流信息显示

### FR-10：评价管理功能
- 实现评价列表页面reviews/index.vue和merchant-reviews/index.vue
- 实现评分统计功能
- 实现商家回复功能
- 实现搜索和筛选功能

### FR-11：店铺编辑功能
- 实现店铺编辑页面shop-edit/index.vue
- 支持店铺信息修改
- 实现图片上传功能

### FR-12：店铺设置功能
- 实现店铺设置页面shop-settings/index.vue
- 实现店铺状态管理
- 实现账户安全设置

### FR-13：分类管理功能
- 实现分类管理页面categories/index.vue
- 支持分类的CRUD操作
- 实现分类排序功能

### FR-14：预约统计功能
- 实现预约统计页面stats-appointments/index.vue
- 实现统计概览功能
- 实现预约趋势图展示

### FR-15：营收统计功能
- 实现营收统计页面stats-revenue/index.vue
- 实现营收概览功能
- 实现营收趋势图和构成分析

## 非功能需求
- **NFR-1**：页面加载速度快，响应迅速（加载时间<2秒）
- **NFR-2**：UI设计与AGENTS.md规范保持一致
- **NFR-3**：代码结构清晰，易于维护
- **NFR-4**：数据交互稳定，错误处理完善
- **NFR-5**：兼容主流浏览器

## 约束
- **技术**：Vue 3, TypeScript, Element Plus
- **API**：对接现有后端API接口
- **数据库**：使用MySQL数据库，参考AGENTS.md中的表结构定义

## 假设
- 后端API接口已正常实现并可用
- Element Plus组件库已集成到项目中
- 数据库表结构已按照cg.sql创建完成
- 开发服务器已正常运行

## 验收标准

### AC-1：功能完整性
- **给定**：商家访问商家端各页面
- **当**：执行各项操作
- **则**：所有功能正常运行，符合AGENTS.md中的功能定义
- **验证**：`programmatic`
- **备注**：逐项验证20个页面的功能模块

### AC-2：UI一致性
- **给定**：商家访问商家端各页面
- **当**：查看页面元素
- **则**：UI设计与AGENTS.md中的功能描述一致
- **验证**：`human-judgment`

### AC-3：数据交互正确性
- **给定**：商家执行数据操作
- **当**：提交表单或查询数据
- **则**：数据正确提交到后端API并返回正确结果
- **验证**：`programmatic`

### AC-4：表单验证完整性
- **给定**：商家填写表单
- **当**：提交表单但字段不符合要求
- **则**：显示相应的错误提示
- **验证**：`programmatic`

### AC-5：响应式布局
- **给定**：商家在不同设备上访问商家管理系统
- **当**：调整屏幕尺寸
- **则**：页面布局自适应，保持良好的用户体验
- **验证**：`human-judgment`

## 未解决问题
- [ ] 部分页面可能缺少对应的API接口
- [ ] 分类管理功能需要确认是否有对应的数据库表
- [ ] 统计图表功能需要确认是否需要引入额外的图表库

---

## 商家端20个页面功能模块对照表

| 序号 | 页面名称 | 文件路径 | 功能模块数 | 优先级 |
|------|----------|----------|-----------|--------|
| 1 | MerchantLayout.vue | merchant/MerchantLayout.vue | 3 | P0 |
| 2 | Register.vue | merchant/Register.vue | 4 | P0 |
| 3 | home/index.vue | merchant/home/index.vue | 4 | P1 |
| 4 | services/index.vue | merchant/services/index.vue | 5 | P1 |
| 5 | service-edit/index.vue | merchant/service-edit/index.vue | 4 | P1 |
| 6 | merchant-products/index.vue | merchant/merchant-products/index.vue | 5 | P1 |
| 7 | product-edit/index.vue | merchant/product-edit/index.vue | 5 | P1 |
| 8 | appointments/index.vue | merchant/appointments/index.vue | 5 | P1 |
| 9 | merchant-orders/index.vue | merchant/merchant-orders/index.vue | 4 | P2 |
| 10 | merchant-product-orders/index.vue | merchant/merchant-product-orders/index.vue | 6 | P2 |
| 11 | reviews/index.vue | merchant/reviews/index.vue | 5 | P2 |
| 12 | shop-edit/index.vue | merchant/shop-edit/index.vue | 4 | P2 |
| 13 | shop-settings/index.vue | merchant/shop-settings/index.vue | 5 | P2 |
| 14 | categories/index.vue | merchant/categories/index.vue | 5 | P2 |
| 15 | stats-appointments/index.vue | merchant/stats-appointments/index.vue | 6 | P3 |
| 16 | stats-revenue/index.vue | merchant/stats-revenue/index.vue | 6 | P3 |

## 商家端页面功能详细对照

### 1. MerchantLayout.vue
- **应有功能**：顶部导航栏、左侧菜单栏、退出登录
- **关联数据表**：merchant
- **优先级**：P0

### 2. Register.vue
- **应有功能**：注册表单（名称、联系人、电话、邮箱、密码、地址、Logo）、表单验证、提交注册
- **关联数据表**：merchant
- **优先级**：P0

### 3. home/index.vue
- **应有功能**：统计概览卡片、最近订单列表、最新评价列表、快捷操作入口
- **关联数据表**：appointment、review、user、service
- **优先级**：P1

### 4. services/index.vue & merchant-services/index.vue
- **应有功能**：服务表格、搜索筛选、分页、批量操作、添加服务
- **关联数据表**：service
- **优先级**：P1

### 5. service-edit/index.vue
- **应有功能**：服务信息表单、图片上传、表单验证、保存、返回列表
- **关联数据表**：service、merchant
- **优先级**：P1

### 6. merchant-products/index.vue
- **应有功能**：商品表格、搜索筛选、分页、库存预警、添加商品
- **关联数据表**：product
- **优先级**：P1

### 7. product-edit/index.vue
- **应有功能**：商品信息表单、库存管理、图片上传、表单验证、保存
- **关联数据表**：product、merchant
- **优先级**：P1

### 8. appointments/index.vue & merchant-appointments/index.vue
- **应有功能**：预约表格、状态标签、搜索筛选、分页、导出
- **关联数据表**：appointment、user、pet、service
- **优先级**：P1

### 9. merchant-orders/index.vue
- **应有功能**：订单表格、状态流转、搜索筛选、分页
- **关联数据表**：appointment、user、service
- **优先级**：P2

### 10. merchant-product-orders/index.vue
- **应有功能**：订单表格、发货管理、物流信息、订单详情弹窗、搜索筛选
- **关联数据表**：product_order、product_order_item、product、user
- **优先级**：P2

### 11. reviews/index.vue & merchant-reviews/index.vue
- **应有功能**：评价表格、评分统计、回复功能、搜索筛选、分页
- **关联数据表**：review、user、service、appointment
- **优先级**：P2

### 12. shop-edit/index.vue
- **应有功能**：店铺信息表单、图片上传、表单验证、保存
- **关联数据表**：merchant
- **优先级**：P2

### 13. shop-settings/index.vue
- **应有功能**：店铺信息展示、店铺状态管理、通知设置、账户安全
- **关联数据表**：merchant
- **优先级**：P2

### 14. categories/index.vue
- **应有功能**：分类列表、添加分类、编辑分类、分类排序、批量操作
- **关联数据表**：category
- **优先级**：P2

### 15. stats-appointments/index.vue
- **应有功能**：统计概览、预约趋势图、预约统计表格、服务类型统计、导出
- **关联数据表**：appointment、service
- **优先级**：P3

### 16. stats-revenue/index.vue
- **应有功能**：营收概览、营收趋势图、营收构成、营收统计表格、top排行、导出
- **关联数据表**：appointment、product_order、product_order_item
- **优先级**：P3

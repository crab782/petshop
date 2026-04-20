# Tasks

- [x] Task 1: 更新 AGENTS.md 中的项目名称引用
  - [x] SubTask 1.1: 将 AGENTS.md 中所有 `cg-vue` 替换为 `petshop-vue`
  - [x] SubTask 1.2: 确保路径引用 `d:\j\cg\cg\petshop-vue` 正确

- [x] Task 2: 更新 UserLayout.vue 导航
  - [x] SubTask 2.1: 在左侧菜单添加"商店浏览"入口（Shop 图标，路由 /user/home）
  - [x] SubTask 2.2: 在顶部导航栏添加购物车图标及商品数量徽标
  - [x] SubTask 2.3: 购物车图标点击跳转到 /user/cart
  - [x] SubTask 2.4: 添加购物车数量获取逻辑（调用 getCart API）

- [x] Task 3: 改造用户端首页为商店浏览首页
  - [x] SubTask 3.1: 调用 getMerchantList API 获取商家列表
  - [x] SubTask 3.2: 以卡片形式展示商家信息（名称、Logo、地址、评分、服务数量）
  - [x] SubTask 3.3: 点击商家卡片跳转到 /user/merchant/{id}
  - [x] SubTask 3.4: 添加搜索过滤功能
  - [x] SubTask 3.5: 保留原有统计概览和快捷操作区域

- [x] Task 4: 改造店铺详情页使用真实 API
  - [x] SubTask 4.1: 移除所有硬编码 mock 数据和 DEV 环境判断
  - [x] SubTask 4.2: 使用 getMerchantInfo API 获取商家信息
  - [x] SubTask 4.3: 使用 getMerchantServices API 获取服务列表
  - [x] SubTask 4.4: 使用 getMerchantProducts API 获取商品列表
  - [x] SubTask 4.5: 使用 getMerchantReviews API 获取评价列表
  - [x] SubTask 4.6: 完善宠物销售模块（商品加入购物车、立即购买）
  - [x] SubTask 4.7: 完善服务预约模块（选择宠物、选择时间、提交预约）

- [x] Task 5: 完善购物车与支付流程
  - [x] SubTask 5.1: 确保购物车页面正确调用后端 API（getCart、updateCartItem、removeFromCart）
  - [x] SubTask 5.2: 完善结算跳转到 checkout 页面
  - [x] SubTask 5.3: 完善 checkout 页面地址选择和订单提交
  - [x] SubTask 5.4: 完善 pay 页面模拟支付流程（选择支付方式、模拟处理、结果反馈）

# Task Dependencies
- Task 2 依赖 Task 1（菜单更新依赖项目名称确认）
- Task 3 依赖 Task 2（首页改造依赖菜单导航）
- Task 4 独立（店铺详情页改造）
- Task 5 依赖 Task 4（购物流程依赖店铺详情页的加入购物车功能）

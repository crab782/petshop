# 后端 API 实现 - 任务分解计划

## [x] Task 1: 实现商家注册 API 端点
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 在 AuthApiController.java 中添加 `/api/auth/merchant/register` 端点
  - 实现商家注册业务逻辑
  - 添加相应的请求和响应DTO
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: POST /api/auth/merchant/register 成功创建商家并返回 201 状态码
  - `programmatic` TR-1.2: POST /api/auth/merchant/register 验证必填字段
  - `programmatic` TR-1.3: POST /api/auth/merchant/register 验证邮箱唯一性
- **Notes**: 商家注册后状态默认为 'pending'，需要平台管理员审核

## [x] Task 2: 实现用户宠物详情 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 UserApiController.java 中添加 `/api/user/pets/{id}` 端点
  - 实现获取宠物详情的业务逻辑
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-2.1: GET /api/user/pets/{id} 成功返回宠物详情
  - `programmatic` TR-2.2: GET /api/user/pets/{id} 验证宠物归属
  - `programmatic` TR-2.3: GET /api/user/pets/{id} 处理宠物不存在的情况
- **Notes**: 确保只有宠物的主人可以访问宠物详情

## [x] Task 3: 实现购物车相关 API 端点
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 创建 CartController.java
  - 实现 `/api/user/cart` 相关端点
  - 实现购物车业务逻辑
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: GET /api/user/cart 成功返回购物车内容
  - `programmatic` TR-3.2: POST /api/user/cart 成功添加商品到购物车
  - `programmatic` TR-3.3: PUT /api/user/cart 成功更新购物车商品数量
  - `programmatic` TR-3.4: DELETE /api/user/cart/{id} 成功从购物车删除商品
  - `programmatic` TR-3.5: DELETE /api/user/cart/batch 成功批量删除购物车商品
- **Notes**: 需要创建购物车相关的数据模型和服务

## [x] Task 4: 实现用户购买服务 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 UserApiController.java 中添加 `/api/user/services` 端点
  - 实现获取用户购买服务的业务逻辑
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-4.1: GET /api/user/services 成功返回用户购买的服务列表
  - `programmatic` TR-4.2: GET /api/user/services 支持分页和筛选
- **Notes**: 需要关联用户和服务的购买记录

## [x] Task 5: 实现服务相关公共 API 端点
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 创建 ServiceController.java
  - 实现 `/api/services` 相关端点
  - 实现服务查询和搜索的业务逻辑
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-5.1: GET /api/services 成功返回服务列表
  - `programmatic` TR-5.2: GET /api/services/{id} 成功返回服务详情
  - `programmatic` TR-5.3: GET /api/services/search 成功搜索服务
  - `programmatic` TR-5.4: GET /api/services/recommended 成功返回推荐服务
- **Notes**: 需要实现服务的搜索和推荐算法

## [x] Task 6: 实现商家相关公共 API 端点
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 创建 MerchantController.java
  - 实现 `/api/merchants` 相关端点
  - 实现商家查询和搜索的业务逻辑
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-6.1: GET /api/merchants 成功返回商家列表
  - `programmatic` TR-6.2: GET /api/merchants/search 成功搜索商家
  - `programmatic` TR-6.3: GET /api/merchant/{id} 成功返回商家详情
  - `programmatic` TR-6.4: GET /api/merchant/{id}/services 成功返回商家服务
  - `programmatic` TR-6.5: GET /api/merchant/{id}/products 成功返回商家商品
  - `programmatic` TR-6.6: GET /api/merchant/{id}/reviews 成功返回商家评价
  - `programmatic` TR-6.7: GET /api/merchant/{id}/available-slots 成功返回可用预约时段
- **Notes**: 需要实现商家的搜索和筛选功能

## [x] Task 7: 实现商品相关公共 API 端点
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 创建 ProductController.java
  - 实现 `/api/products` 相关端点
  - 实现商品查询和搜索的业务逻辑
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-7.1: GET /api/products 成功返回商品列表
  - `programmatic` TR-7.2: GET /api/products/{id} 成功返回商品详情
  - `programmatic` TR-7.3: GET /api/products/search 成功搜索商品
  - `programmatic` TR-7.4: GET /api/products/{id}/reviews 成功返回商品评价
- **Notes**: 需要实现商品的搜索和筛选功能

## [x] Task 8: 实现商家信息 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加 `/api/merchant/info` 端点
  - 实现获取和更新商家信息的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-8.1: GET /api/merchant/info 成功返回商家信息
  - `programmatic` TR-8.2: PUT /api/merchant/info 成功更新商家信息
- **Notes**: 确保与现有的 `/api/merchant/profile` 端点功能一致

## [x] Task 9: 实现商家服务批量删除 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加 `/api/merchant/services/batch` 端点
  - 实现批量删除服务的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-9.1: DELETE /api/merchant/services/batch 成功批量删除服务
  - `programmatic` TR-9.2: DELETE /api/merchant/services/batch 验证服务归属
- **Notes**: 确保只有商家自己可以删除自己的服务

## [x] Task 10: 实现商家预约相关 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加预约相关端点
  - 实现获取最近预约和预约统计的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-10.1: GET /api/merchant/appointments/recent 成功返回最近预约
  - `programmatic` TR-10.2: GET /api/merchant/appointment-stats 成功返回预约统计
  - `programmatic` TR-10.3: GET /api/merchant/appointment-stats/export 成功导出预约统计
- **Notes**: 需要实现预约统计和导出功能

## [x] Task 11: 实现商家商品订单相关 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加商品订单相关端点
  - 实现获取商品订单和更新物流信息的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-11.1: GET /api/merchant/product-orders 成功返回商品订单
  - `programmatic` TR-11.2: PUT /api/merchant/product-orders/{id}/status 成功更新商品订单状态
  - `programmatic` TR-11.3: PUT /api/merchant/product-orders/{id}/logistics 成功更新物流信息
- **Notes**: 确保与现有的 `/api/merchant/orders` 端点功能一致

## [x] Task 12: 实现商家评价相关 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加评价相关端点
  - 实现回复评价和获取最近评价的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-12.1: PUT /api/merchant/reviews/{id}/reply 成功回复评价
  - `programmatic` TR-12.2: DELETE /api/merchant/reviews/{id} 成功删除评价
  - `programmatic` TR-12.3: GET /api/merchant/reviews/recent 成功返回最近评价
- **Notes**: 确保只有商家自己可以回复和删除自己的评价

## [x] Task 13: 实现商家营收统计相关 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加营收统计相关端点
  - 实现获取营收统计和导出功能的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-13.1: GET /api/merchant/revenue-stats 成功返回营收统计
  - `programmatic` TR-13.2: GET /api/merchant/revenue-stats/export 成功导出营收统计
- **Notes**: 需要实现营收统计和导出功能

## [x] Task 14: 实现商家设置相关 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加设置相关端点
  - 实现获取和更新商家设置的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-14.1: GET /api/merchant/settings 成功返回商家设置
  - `programmatic` TR-14.2: PUT /api/merchant/settings 成功更新商家设置
- **Notes**: 需要创建商家设置相关的数据模型和服务

## [x] Task 15: 实现商家账号相关 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加账号相关端点
  - 实现修改密码、绑定手机和邮箱的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-15.1: POST /api/merchant/change-password 成功修改密码
  - `programmatic` TR-15.2: POST /api/merchant/bind-phone 成功绑定手机号
  - `programmatic` TR-15.3: POST /api/merchant/bind-email 成功绑定邮箱
  - `programmatic` TR-15.4: POST /api/merchant/send-verify-code 成功发送验证码
- **Notes**: 确保与现有的认证相关端点功能一致

## [x] Task 16: 实现商家仪表盘 API 端点
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 在 MerchantApiController.java 中添加 `/api/merchant/dashboard` 端点
  - 实现获取商家仪表盘数据的业务逻辑
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-16.1: GET /api/merchant/dashboard 成功返回仪表盘数据
- **Notes**: 需要实现仪表盘数据的聚合和计算

## [ ] Task 17: 实现管理员相关 API 端点
- **Priority**: P2
- **Depends On**: None
- **Description**: 
  - 创建 AdminController.java
  - 实现管理员相关的 API 端点
  - 实现管理员业务逻辑
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-17.1: 验证管理员 API 端点的功能
- **Notes**: 需要实现管理员认证和授权

## [ ] Task 18: 实现公告相关 API 端点
- **Priority**: P2
- **Depends On**: None
- **Description**: 
  - 创建 AnnouncementController.java
  - 实现公告相关的 API 端点
  - 实现公告业务逻辑
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-18.1: 验证公告 API 端点的功能
- **Notes**: 需要创建公告相关的数据模型和服务

## [ ] Task 19: 实现通知相关 API 端点
- **Priority**: P2
- **Depends On**: None
- **Description**: 
  - 创建 NotificationController.java
  - 实现通知相关的 API 端点
  - 实现通知业务逻辑
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-19.1: 验证通知 API 端点的功能
- **Notes**: 需要创建通知相关的数据模型和服务

## [ ] Task 20: 实现搜索相关 API 端点
- **Priority**: P2
- **Depends On**: None
- **Description**: 
  - 创建 SearchController.java
  - 实现搜索相关的 API 端点
  - 实现搜索业务逻辑
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-20.1: 验证搜索 API 端点的功能
- **Notes**: 需要实现全文搜索功能
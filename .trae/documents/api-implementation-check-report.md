# 前端 API 与后端实现检查报告

## 检查概述

**任务**: 检查前端项目 API 调用与后端实现的对应关系，识别缺失的后端实现

**检查范围**:
- 前端 API 目录: `d:\j\cg\cg\cg-vue\src\api\`
- 后端控制器目录: `d:\j\cg\cg\src\main\java\com\petshop\controller\api\`

## 前端 API 文件分析

### 1. auth.ts - 认证相关 API

**前端调用的 API 端点:**
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/merchant/register` - 商家注册
- `POST /api/auth/logout` - 登出
- `GET /api/auth/userinfo` - 获取用户信息
- `PUT /api/auth/userinfo` - 更新用户信息
- `POST /api/auth/sendVerifyCode` - 发送验证码
- `POST /api/auth/resetPassword` - 重置密码
- `PUT /api/auth/password` - 修改密码

**后端实现状态:**
- ✅ 已实现: `/api/auth/login`, `/api/auth/register`, `/api/auth/logout`, `/api/auth/userinfo`, `/api/auth/password`, `/api/auth/sendVerifyCode`, `/api/auth/resetPassword`
- ❌ 未实现: `/api/auth/merchant/register` (商家注册)

### 2. user.ts - 用户相关 API

**前端调用的 API 端点:**
- `GET /api/user/pets` - 获取用户宠物列表
- `GET /api/user/pets/{id}` - 获取宠物详情
- `POST /api/user/pets` - 添加宠物
- `PUT /api/user/pets/{id}` - 更新宠物
- `DELETE /api/user/pets/{id}` - 删除宠物
- `GET /api/user/appointments` - 获取用户预约列表
- `POST /api/user/appointments` - 创建预约
- `PUT /api/user/appointments/{id}/cancel` - 取消预约
- `GET /api/user/appointments/{id}` - 获取预约详情
- `GET /api/user/appointments/stats` - 获取预约统计
- `GET /api/user/home/stats` - 获取首页统计
- `GET /api/user/home/activities` - 获取最近活动
- `GET /api/services` - 获取服务列表
- `GET /api/services/{id}` - 获取服务详情
- `GET /api/services/search` - 搜索服务
- `GET /api/services/recommended` - 获取推荐服务
- `GET /api/merchants` - 获取商家列表
- `GET /api/merchants/search` - 搜索商家
- `GET /api/merchant/{id}` - 获取商家详情
- `GET /api/merchant/{id}/services` - 获取商家服务
- `GET /api/merchant/{id}/products` - 获取商家商品
- `GET /api/merchant/{id}/reviews` - 获取商家评价
- `GET /api/merchant/{id}/available-slots` - 获取可用预约时段
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/search` - 搜索商品
- `GET /api/products/{id}/reviews` - 获取商品评价
- `GET /api/user/addresses` - 获取地址列表
- `POST /api/user/addresses` - 添加地址
- `PUT /api/user/addresses/{id}` - 更新地址
- `DELETE /api/user/addresses/{id}` - 删除地址
- `PUT /api/user/addresses/{id}/default` - 设置默认地址
- `POST /api/user/reviews` - 添加评价
- `GET /api/user/reviews` - 获取用户评价列表
- `PUT /api/user/reviews/{id}` - 更新评价
- `DELETE /api/user/reviews/{id}` - 删除评价
- `GET /api/user/favorites` - 获取收藏商家列表
- `POST /api/user/favorites` - 添加商家收藏
- `DELETE /api/user/favorites/{id}` - 删除商家收藏
- `GET /api/user/favorites/services` - 获取收藏服务列表
- `POST /api/user/favorites/services` - 添加服务收藏
- `DELETE /api/user/favorites/services/{id}` - 删除服务收藏
- `POST /api/user/favorites/products` - 添加商品收藏
- `DELETE /api/user/favorites/products/{id}` - 删除商品收藏
- `GET /api/user/favorites/products/{id}/check` - 检查商品收藏状态
- `GET /api/user/cart` - 获取购物车
- `POST /api/user/cart` - 添加到购物车
- `PUT /api/user/cart` - 更新购物车
- `DELETE /api/user/cart/{id}` - 从购物车删除
- `DELETE /api/user/cart/batch` - 批量从购物车删除
- `POST /api/user/orders` - 创建订单
- `GET /api/user/orders` - 获取订单列表
- `GET /api/user/orders/{id}` - 获取订单详情
- `POST /api/user/orders/{id}/pay` - 支付订单
- `GET /api/user/orders/{id}/pay/status` - 获取支付状态
- `PUT /api/user/orders/{id}/cancel` - 取消订单
- `POST /api/user/orders/{id}/refund` - 申请退款
- `PUT /api/user/orders/{id}/confirm` - 确认收货
- `DELETE /api/user/orders/{id}` - 删除订单
- `PUT /api/user/orders/batch-cancel` - 批量取消订单
- `DELETE /api/user/orders/batch-delete` - 批量删除订单
- `POST /api/user/orders/preview` - 订单预览
- `GET /api/user/services` - 获取用户购买的服务

**后端实现状态:**
- ✅ 已实现: `/api/user/pets`, `/api/user/appointments`, `/api/user/appointments/{id}`, `/api/user/addresses`, `/api/user/addresses/{id}`, `/api/user/reviews`, `/api/user/favorites`, `/api/user/orders`, `/api/user/orders/{id}`, `/api/user/home/stats`, `/api/user/home/activities`
- ❌ 未实现: `/api/user/pets/{id}` (获取宠物详情), `/api/user/cart` 相关接口, `/api/user/services` (用户购买的服务), `/api/merchant/{id}/available-slots` (可用预约时段), `/api/services` 相关接口, `/api/merchants` 相关接口, `/api/products` 相关接口

### 3. merchant.ts - 商家相关 API

**前端调用的 API 端点:**
- `GET /api/merchant/info` - 获取商家信息
- `PUT /api/merchant/info` - 更新商家信息
- `GET /api/merchant/services` - 获取商家服务列表
- `POST /api/merchant/services` - 添加服务
- `PUT /api/merchant/services/{id}` - 更新服务
- `DELETE /api/merchant/services/{id}` - 删除服务
- `GET /api/merchant/services/{id}` - 获取服务详情
- `PUT /api/merchant/services/batch/status` - 批量更新服务状态
- `DELETE /api/merchant/services/batch` - 批量删除服务
- `GET /api/merchant/appointments` - 获取商家预约列表
- `PUT /api/merchant/appointments/{id}/status` - 更新预约状态
- `GET /api/merchant/appointments/recent` - 获取最近预约
- `GET /api/merchant/appointment-stats` - 获取预约统计
- `GET /api/merchant/appointment-stats/export` - 导出预约统计
- `GET /api/merchant/products` - 获取商家商品列表
- `POST /api/merchant/products` - 添加商品
- `PUT /api/merchant/products/{id}` - 更新商品
- `DELETE /api/merchant/products/{id}` - 删除商品
- `GET /api/merchant/products/{id}` - 获取商品详情
- `GET /api/merchant/products/paged` - 分页获取商品
- `PUT /api/merchant/products/{id}/status` - 更新商品状态
- `PUT /api/merchant/products/batch/status` - 批量更新商品状态
- `DELETE /api/merchant/products/batch` - 批量删除商品
- `GET /api/merchant/product-orders` - 获取商家商品订单
- `PUT /api/merchant/product-orders/{id}/status` - 更新商品订单状态
- `PUT /api/merchant/product-orders/{id}/logistics` - 更新物流信息
- `GET /api/merchant/categories` - 获取分类列表
- `POST /api/merchant/categories` - 添加分类
- `PUT /api/merchant/categories/{id}` - 更新分类
- `DELETE /api/merchant/categories/{id}` - 删除分类
- `PUT /api/merchant/categories/{id}/status` - 更新分类状态
- `PUT /api/merchant/categories/batch/status` - 批量更新分类状态
- `DELETE /api/merchant/categories/batch` - 批量删除分类
- `GET /api/merchant/reviews` - 获取商家评价列表
- `PUT /api/merchant/reviews/{id}/reply` - 回复评价
- `DELETE /api/merchant/reviews/{id}` - 删除评价
- `GET /api/merchant/reviews/recent` - 获取最近评价
- `GET /api/merchant/revenue-stats` - 获取营收统计
- `GET /api/merchant/revenue-stats/export` - 导出营收统计
- `GET /api/merchant/settings` - 获取商家设置
- `PUT /api/merchant/settings` - 更新商家设置
- `POST /api/merchant/change-password` - 修改密码
- `POST /api/merchant/bind-phone` - 绑定手机号
- `POST /api/merchant/bind-email` - 绑定邮箱
- `POST /api/merchant/send-verify-code` - 发送验证码
- `GET /api/merchant/dashboard` - 获取商家仪表盘数据

**后端实现状态:**
- ✅ 已实现: `/api/merchant/profile` (获取商家资料), `/api/merchant/services`, `/api/merchant/appointments`, `/api/merchant/orders` (获取商品订单), `/api/merchant/products`, `/api/merchant/categories`, `/api/merchant/reviews`
- ❌ 未实现: `/api/merchant/info` (获取商家信息), `/api/merchant/info` (更新商家信息), `/api/merchant/appointments/recent` (获取最近预约), `/api/merchant/appointment-stats` (获取预约统计), `/api/merchant/appointment-stats/export` (导出预约统计), `/api/merchant/product-orders` (获取商家商品订单), `/api/merchant/product-orders/{id}/status` (更新商品订单状态), `/api/merchant/product-orders/{id}/logistics` (更新物流信息), `/api/merchant/reviews/{id}/reply` (回复评价), `/api/merchant/reviews/recent` (获取最近评价), `/api/merchant/revenue-stats` (获取营收统计), `/api/merchant/revenue-stats/export` (导出营收统计), `/api/merchant/settings` (获取商家设置), `/api/merchant/settings` (更新商家设置), `/api/merchant/change-password` (修改密码), `/api/merchant/bind-phone` (绑定手机号), `/api/merchant/bind-email` (绑定邮箱), `/api/merchant/send-verify-code` (发送验证码), `/api/merchant/dashboard` (获取商家仪表盘数据)

### 4. 其他前端 API 文件

**admin.ts** - 管理员相关 API
**announcement.ts** - 公告相关 API
**notification.ts** - 通知相关 API
**search.ts** - 搜索相关 API

## 后端控制器分析

### 1. AuthApiController.java

**已实现的端点:**
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 登出
- `GET /api/auth/userinfo` - 获取用户信息
- `PUT /api/auth/userinfo` - 更新用户信息
- `PUT /api/auth/password` - 修改密码
- `POST /api/auth/sendVerifyCode` - 发送验证码
- `POST /api/auth/resetPassword` - 重置密码

**缺失的端点:**
- `POST /api/auth/merchant/register` - 商家注册

### 2. UserApiController.java

**已实现的端点:**
- `GET /api/user/pets` - 获取用户宠物列表
- `POST /api/user/pets` - 添加宠物
- `PUT /api/user/pets/{id}` - 更新宠物
- `DELETE /api/user/pets/{id}` - 删除宠物
- `GET /api/user/appointments` - 获取用户预约列表
- `GET /api/user/appointments/{id}` - 获取预约详情
- `POST /api/user/appointments` - 创建预约
- `PUT /api/user/appointments/{id}/cancel` - 取消预约
- `GET /api/user/appointments/stats` - 获取预约统计
- `GET /api/user/home/stats` - 获取首页统计
- `GET /api/user/home/activities` - 获取最近活动
- `GET /api/user/addresses` - 获取地址列表
- `POST /api/user/addresses` - 添加地址
- `PUT /api/user/addresses/{id}` - 更新地址
- `DELETE /api/user/addresses/{id}` - 删除地址
- `PUT /api/user/addresses/{id}/default` - 设置默认地址
- `POST /api/user/reviews` - 添加评价
- `GET /api/user/reviews` - 获取用户评价列表
- `PUT /api/user/reviews/{id}` - 更新评价
- `DELETE /api/user/reviews/{id}` - 删除评价
- `GET /api/user/favorites` - 获取收藏商家列表
- `POST /api/user/favorites` - 添加商家收藏
- `DELETE /api/user/favorites/{id}` - 删除商家收藏
- `GET /api/user/favorites/services` - 获取收藏服务列表
- `POST /api/user/favorites/services` - 添加服务收藏
- `DELETE /api/user/favorites/services/{id}` - 删除服务收藏
- `POST /api/user/favorites/products` - 添加商品收藏
- `DELETE /api/user/favorites/products/{id}` - 删除商品收藏
- `GET /api/user/favorites/products/{id}/check` - 检查商品收藏状态
- `GET /api/user/orders` - 获取订单列表
- `GET /api/user/orders/{id}` - 获取订单详情
- `POST /api/user/orders` - 创建订单
- `POST /api/user/orders/preview` - 订单预览
- `POST /api/user/orders/{id}/pay` - 支付订单
- `GET /api/user/orders/{id}/pay/status` - 获取支付状态
- `PUT /api/user/orders/{id}/cancel` - 取消订单
- `POST /api/user/orders/{id}/refund` - 申请退款
- `PUT /api/user/orders/{id}/confirm` - 确认收货
- `DELETE /api/user/orders/{id}` - 删除订单
- `PUT /api/user/orders/batch-cancel` - 批量取消订单
- `DELETE /api/user/orders/batch-delete` - 批量删除订单

**缺失的端点:**
- `GET /api/user/pets/{id}` - 获取宠物详情
- `GET /api/user/cart` - 获取购物车
- `POST /api/user/cart` - 添加到购物车
- `PUT /api/user/cart` - 更新购物车
- `DELETE /api/user/cart/{id}` - 从购物车删除
- `DELETE /api/user/cart/batch` - 批量从购物车删除
- `GET /api/user/services` - 获取用户购买的服务

### 3. MerchantApiController.java

**已实现的端点:**
- `GET /api/merchant/profile` - 获取商家资料
- `PUT /api/merchant/profile` - 更新商家资料
- `GET /api/merchant/services` - 获取服务列表
- `POST /api/merchant/services` - 添加服务
- `PUT /api/merchant/services/{id}` - 更新服务
- `DELETE /api/merchant/services/{id}` - 删除服务
- `PUT /api/merchant/services/batch/status` - 批量更新服务状态
- `GET /api/merchant/appointments` - 获取预约列表
- `PUT /api/merchant/appointments/{id}/status` - 更新预约状态
- `GET /api/merchant/orders` - 获取商品订单
- `PUT /api/merchant/orders/{id}/status` - 更新订单状态
- `GET /api/merchant/products` - 获取商品列表
- `POST /api/merchant/products` - 添加商品
- `GET /api/merchant/products/{id}` - 获取商品详情
- `PUT /api/merchant/products/{id}` - 更新商品
- `DELETE /api/merchant/products/{id}` - 删除商品
- `GET /api/merchant/products/paged` - 分页获取商品
- `PUT /api/merchant/products/{id}/status` - 更新商品状态
- `PUT /api/merchant/products/batch/status` - 批量更新商品状态
- `DELETE /api/merchant/products/batch` - 批量删除商品
- `GET /api/merchant/categories` - 获取分类列表
- `POST /api/merchant/categories` - 添加分类
- `PUT /api/merchant/categories/{id}` - 更新分类
- `DELETE /api/merchant/categories/{id}` - 删除分类
- `PUT /api/merchant/categories/{id}/status` - 更新分类状态
- `PUT /api/merchant/categories/batch/status` - 批量更新分类状态
- `DELETE /api/merchant/categories/batch` - 批量删除分类
- `GET /api/merchant/reviews` - 获取评价列表
- `GET /api/merchant/reviews/statistics` - 获取评价统计信息
- `GET /api/merchant/reviews/{id}` - 获取评价详情

**缺失的端点:**
- `GET /api/merchant/info` - 获取商家信息
- `PUT /api/merchant/info` - 更新商家信息
- `DELETE /api/merchant/services/batch` - 批量删除服务
- `GET /api/merchant/appointments/recent` - 获取最近预约
- `GET /api/merchant/appointment-stats` - 获取预约统计
- `GET /api/merchant/appointment-stats/export` - 导出预约统计
- `GET /api/merchant/product-orders` - 获取商家商品订单
- `PUT /api/merchant/product-orders/{id}/status` - 更新商品订单状态
- `PUT /api/merchant/product-orders/{id}/logistics` - 更新物流信息
- `PUT /api/merchant/reviews/{id}/reply` - 回复评价
- `DELETE /api/merchant/reviews/{id}` - 删除评价
- `GET /api/merchant/reviews/recent` - 获取最近评价
- `GET /api/merchant/revenue-stats` - 获取营收统计
- `GET /api/merchant/revenue-stats/export` - 导出营收统计
- `GET /api/merchant/settings` - 获取商家设置
- `PUT /api/merchant/settings` - 更新商家设置
- `POST /api/merchant/change-password` - 修改密码
- `POST /api/merchant/bind-phone` - 绑定手机号
- `POST /api/merchant/bind-email` - 绑定邮箱
- `POST /api/merchant/send-verify-code` - 发送验证码
- `GET /api/merchant/dashboard` - 获取商家仪表盘数据

## 检查结果总结

### 已实现的 API 端点

**认证相关:**
- ✅ `POST /api/auth/login` - 登录
- ✅ `POST /api/auth/register` - 用户注册
- ✅ `POST /api/auth/logout` - 登出
- ✅ `GET /api/auth/userinfo` - 获取用户信息
- ✅ `PUT /api/auth/userinfo` - 更新用户信息
- ✅ `PUT /api/auth/password` - 修改密码
- ✅ `POST /api/auth/sendVerifyCode` - 发送验证码
- ✅ `POST /api/auth/resetPassword` - 重置密码

**用户相关:**
- ✅ `GET /api/user/pets` - 获取用户宠物列表
- ✅ `POST /api/user/pets` - 添加宠物
- ✅ `PUT /api/user/pets/{id}` - 更新宠物
- ✅ `DELETE /api/user/pets/{id}` - 删除宠物
- ✅ `GET /api/user/appointments` - 获取用户预约列表
- ✅ `GET /api/user/appointments/{id}` - 获取预约详情
- ✅ `POST /api/user/appointments` - 创建预约
- ✅ `PUT /api/user/appointments/{id}/cancel` - 取消预约
- ✅ `GET /api/user/appointments/stats` - 获取预约统计
- ✅ `GET /api/user/home/stats` - 获取首页统计
- ✅ `GET /api/user/home/activities` - 获取最近活动
- ✅ `GET /api/user/addresses` - 获取地址列表
- ✅ `POST /api/user/addresses` - 添加地址
- ✅ `PUT /api/user/addresses/{id}` - 更新地址
- ✅ `DELETE /api/user/addresses/{id}` - 删除地址
- ✅ `PUT /api/user/addresses/{id}/default` - 设置默认地址
- ✅ `POST /api/user/reviews` - 添加评价
- ✅ `GET /api/user/reviews` - 获取用户评价列表
- ✅ `PUT /api/user/reviews/{id}` - 更新评价
- ✅ `DELETE /api/user/reviews/{id}` - 删除评价
- ✅ `GET /api/user/favorites` - 获取收藏商家列表
- ✅ `POST /api/user/favorites` - 添加商家收藏
- ✅ `DELETE /api/user/favorites/{id}` - 删除商家收藏
- ✅ `GET /api/user/favorites/services` - 获取收藏服务列表
- ✅ `POST /api/user/favorites/services` - 添加服务收藏
- ✅ `DELETE /api/user/favorites/services/{id}` - 删除服务收藏
- ✅ `POST /api/user/favorites/products` - 添加商品收藏
- ✅ `DELETE /api/user/favorites/products/{id}` - 删除商品收藏
- ✅ `GET /api/user/favorites/products/{id}/check` - 检查商品收藏状态
- ✅ `GET /api/user/orders` - 获取订单列表
- ✅ `GET /api/user/orders/{id}` - 获取订单详情
- ✅ `POST /api/user/orders` - 创建订单
- ✅ `POST /api/user/orders/preview` - 订单预览
- ✅ `POST /api/user/orders/{id}/pay` - 支付订单
- ✅ `GET /api/user/orders/{id}/pay/status` - 获取支付状态
- ✅ `PUT /api/user/orders/{id}/cancel` - 取消订单
- ✅ `POST /api/user/orders/{id}/refund` - 申请退款
- ✅ `PUT /api/user/orders/{id}/confirm` - 确认收货
- ✅ `DELETE /api/user/orders/{id}` - 删除订单
- ✅ `PUT /api/user/orders/batch-cancel` - 批量取消订单
- ✅ `DELETE /api/user/orders/batch-delete` - 批量删除订单

**商家相关:**
- ✅ `GET /api/merchant/profile` - 获取商家资料
- ✅ `PUT /api/merchant/profile` - 更新商家资料
- ✅ `GET /api/merchant/services` - 获取服务列表
- ✅ `POST /api/merchant/services` - 添加服务
- ✅ `PUT /api/merchant/services/{id}` - 更新服务
- ✅ `DELETE /api/merchant/services/{id}` - 删除服务
- ✅ `PUT /api/merchant/services/batch/status` - 批量更新服务状态
- ✅ `GET /api/merchant/appointments` - 获取预约列表
- ✅ `PUT /api/merchant/appointments/{id}/status` - 更新预约状态
- ✅ `GET /api/merchant/orders` - 获取商品订单
- ✅ `PUT /api/merchant/orders/{id}/status` - 更新订单状态
- ✅ `GET /api/merchant/products` - 获取商品列表
- ✅ `POST /api/merchant/products` - 添加商品
- ✅ `GET /api/merchant/products/{id}` - 获取商品详情
- ✅ `PUT /api/merchant/products/{id}` - 更新商品
- ✅ `DELETE /api/merchant/products/{id}` - 删除商品
- ✅ `GET /api/merchant/products/paged` - 分页获取商品
- ✅ `PUT /api/merchant/products/{id}/status` - 更新商品状态
- ✅ `PUT /api/merchant/products/batch/status` - 批量更新商品状态
- ✅ `DELETE /api/merchant/products/batch` - 批量删除商品
- ✅ `GET /api/merchant/categories` - 获取分类列表
- ✅ `POST /api/merchant/categories` - 添加分类
- ✅ `PUT /api/merchant/categories/{id}` - 更新分类
- ✅ `DELETE /api/merchant/categories/{id}` - 删除分类
- ✅ `PUT /api/merchant/categories/{id}/status` - 更新分类状态
- ✅ `PUT /api/merchant/categories/batch/status` - 批量更新分类状态
- ✅ `DELETE /api/merchant/categories/batch` - 批量删除分类
- ✅ `GET /api/merchant/reviews` - 获取评价列表
- ✅ `GET /api/merchant/reviews/statistics` - 获取评价统计信息
- ✅ `GET /api/merchant/reviews/{id}` - 获取评价详情

### 缺失的 API 端点

**认证相关:**
- ❌ `POST /api/auth/merchant/register` - 商家注册

**用户相关:**
- ❌ `GET /api/user/pets/{id}` - 获取宠物详情
- ❌ `GET /api/user/cart` - 获取购物车
- ❌ `POST /api/user/cart` - 添加到购物车
- ❌ `PUT /api/user/cart` - 更新购物车
- ❌ `DELETE /api/user/cart/{id}` - 从购物车删除
- ❌ `DELETE /api/user/cart/batch` - 批量从购物车删除
- ❌ `GET /api/user/services` - 获取用户购买的服务
- ❌ `GET /api/services` - 获取服务列表
- ❌ `GET /api/services/{id}` - 获取服务详情
- ❌ `GET /api/services/search` - 搜索服务
- ❌ `GET /api/services/recommended` - 获取推荐服务
- ❌ `GET /api/merchants` - 获取商家列表
- ❌ `GET /api/merchants/search` - 搜索商家
- ❌ `GET /api/merchant/{id}` - 获取商家详情
- ❌ `GET /api/merchant/{id}/services` - 获取商家服务
- ❌ `GET /api/merchant/{id}/products` - 获取商家商品
- ❌ `GET /api/merchant/{id}/reviews` - 获取商家评价
- ❌ `GET /api/merchant/{id}/available-slots` - 获取可用预约时段
- ❌ `GET /api/products` - 获取商品列表
- ❌ `GET /api/products/{id}` - 获取商品详情
- ❌ `GET /api/products/search` - 搜索商品
- ❌ `GET /api/products/{id}/reviews` - 获取商品评价

**商家相关:**
- ❌ `GET /api/merchant/info` - 获取商家信息
- ❌ `PUT /api/merchant/info` - 更新商家信息
- ❌ `DELETE /api/merchant/services/batch` - 批量删除服务
- ❌ `GET /api/merchant/appointments/recent` - 获取最近预约
- ❌ `GET /api/merchant/appointment-stats` - 获取预约统计
- ❌ `GET /api/merchant/appointment-stats/export` - 导出预约统计
- ❌ `GET /api/merchant/product-orders` - 获取商家商品订单
- ❌ `PUT /api/merchant/product-orders/{id}/status` - 更新商品订单状态
- ❌ `PUT /api/merchant/product-orders/{id}/logistics` - 更新物流信息
- ❌ `PUT /api/merchant/reviews/{id}/reply` - 回复评价
- ❌ `DELETE /api/merchant/reviews/{id}` - 删除评价
- ❌ `GET /api/merchant/reviews/recent` - 获取最近评价
- ❌ `GET /api/merchant/revenue-stats` - 获取营收统计
- ❌ `GET /api/merchant/revenue-stats/export` - 导出营收统计
- ❌ `GET /api/merchant/settings` - 获取商家设置
- ❌ `PUT /api/merchant/settings` - 更新商家设置
- ❌ `POST /api/merchant/change-password` - 修改密码
- ❌ `POST /api/merchant/bind-phone` - 绑定手机号
- ❌ `POST /api/merchant/bind-email` - 绑定邮箱
- ❌ `POST /api/merchant/send-verify-code` - 发送验证码
- ❌ `GET /api/merchant/dashboard` - 获取商家仪表盘数据

**其他相关:**
- ❌ 管理员相关 API (admin.ts)
- ❌ 公告相关 API (announcement.ts)
- ❌ 通知相关 API (notification.ts)
- ❌ 搜索相关 API (search.ts)

## 建议

1. **优先实现缺失的核心接口:**
   - 商家注册接口 `/api/auth/merchant/register`
   - 购物车相关接口 `/api/user/cart`
   - 服务和商品相关的公共接口 (`/api/services`, `/api/products`)
   - 商家相关的公共接口 (`/api/merchants`)

2. **完善后端控制器实现:**
   - 检查并实现 MerchantApiController.java 中缺失的接口
   - 检查并实现其他控制器的缺失接口

3. **统一 API 响应格式:**
   - 确保所有接口返回一致的响应格式
   - 统一错误处理机制

4. **添加 API 文档:**
   - 使用 Swagger 等工具为 API 添加文档
   - 提供接口使用说明

## 结论

前端项目已经定义了完整的 API 调用结构，但后端实现存在部分缺失。特别是商家注册、购物车、服务和商品相关的公共接口需要优先实现。建议按照上述建议逐步完善后端实现，确保前端能够正常调用所有必要的 API 端点。
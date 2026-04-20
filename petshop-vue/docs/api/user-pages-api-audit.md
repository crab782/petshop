# 用户端页面 API 审计报告

> 生成日期：2026-04-21
> 审计范围：cg-vue/src/views/user/ 目录下所有页面

---

## 审计概览

| 统计项 | 数量 |
|--------|------|
| 审计页面总数 | 27 |
| 涉及 API 文件 | 5 个 (user.ts, notification.ts, announcement.ts, search.ts, auth.ts) |
| API 调用总数 | 约 60+ 个 |

---

## 1. user-home/index.vue - 用户首页

**页面路径**: `cg-vue/src/views/user/user-home/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/home/stats` | GET | 无 | `HomeStats { petCount, pendingAppointments, reviewCount }` | 正常 |
| 2 | `/api/user/home/activities` | GET | `limit?: number` | `Activity[]` | 正常 |
| 3 | `/api/services/recommended` | GET | `limit?: number` | `Service[]` | 正常 |
| 4 | `/api/user/appointments` | GET | 无 | `Appointment[]` | 正常 |

### 使用说明
- 页面加载时调用 `getHomeStats()` 获取统计概览
- 调用 `getRecentActivities()` 获取最近活动列表
- 调用 `getRecommendedServices()` 获取推荐服务
- 开发环境使用模拟数据，生产环境调用真实 API

---

## 2. user-profile/index.vue - 个人中心

**页面路径**: `cg-vue/src/views/user/user-profile/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/auth/userinfo` | GET | 无 | `UserInfo { id, username, email, phone, avatar, role }` | 正常 |
| 2 | `/api/user/pets` | GET | 无 | `Pet[]` | 正常 |
| 3 | `/api/user/appointments` | GET | 无 | `Appointment[]` | 正常 |
| 4 | `/api/user/reviews` | GET | 无 | `Review[]` | 正常 |

### 使用说明
- 从 userStore 获取用户信息，如不存在则调用 `getUserInfo()`
- 调用 `getUserPets()` 获取宠物数量
- 调用 `getUserAppointments()` 获取预约数量
- 调用 `getUserReviews()` 获取评价数量

---

## 3. profile-edit/index.vue - 编辑个人资料

**页面路径**: `cg-vue/src/views/user/profile-edit/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/auth/userinfo` | GET | 无 | `UserInfo` | 正常 |
| 2 | `/api/auth/userinfo` | PUT | `{ username, email, phone, avatar }` | 更新后的用户信息 | 正常 |

### 使用说明
- 页面加载时调用 `getUserInfo()` 获取当前用户信息
- 保存时调用 `updateUserInfo()` 更新用户信息
- 包含表单验证：用户名必填、邮箱格式验证

---

## 4. user-pets/index.vue - 我的宠物

**页面路径**: `cg-vue/src/views/user/user-pets/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/pets` | GET | 无 | `Pet[]` | 正常 |
| 2 | `/api/user/pets` | POST | `Omit<Pet, 'id'>` | `Pet` | 正常 |
| 3 | `/api/user/pets/${id}` | PUT | `Partial<Pet>` | `Pet` | 正常 |
| 4 | `/api/user/pets/${id}` | DELETE | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getUserPets()` 获取宠物列表
- 添加宠物调用 `addPet()`
- 编辑宠物调用 `updatePet()`
- 删除宠物调用 `deletePet()`
- 支持按宠物名称和类型筛选（前端筛选）

---

## 5. pet-edit/index.vue - 新增/编辑宠物

**页面路径**: `cg-vue/src/views/user/pet-edit/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/pets/${id}` | GET | 无 | `Pet` | 正常 |
| 2 | `/api/user/pets` | POST | `Omit<Pet, 'id'>` | `Pet` | 正常 |
| 3 | `/api/user/pets/${id}` | PUT | `Partial<Pet>` | `Pet` | 正常 |

### 使用说明
- 编辑模式时调用 `getPetById()` 获取宠物详情
- 新增时调用 `addPet()`
- 编辑时调用 `updatePet()`
- 表单字段：name, type, breed, age, gender, avatar, description, weight, bodyType, furColor, personality

---

## 6. user-services/index.vue - 我的服务

**页面路径**: `cg-vue/src/views/user/user-services/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/services` | GET | `{ keyword?, status?, page?, pageSize? }` | `{ data: UserPurchasedService[], total: number }` | 正常 |

### 使用说明
- 页面加载时调用 `getUserPurchasedServices()` 获取已购买服务列表
- 支持按服务名称搜索和状态筛选
- 服务状态：active(待使用), used(已使用), expired(已过期)

---

## 7. service-list/index.vue - 服务列表

**页面路径**: `cg-vue/src/views/user/service-list/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/services` | GET | `{ type?: string }` | `Service[]` | 正常 |
| 2 | `/api/user/favorites/services` | GET | 无 | `FavoriteService[]` | 正常 |
| 3 | `/api/user/favorites/services` | POST | `{ serviceId }` | 收藏结果 | 正常 |
| 4 | `/api/user/favorites/services/${serviceId}` | DELETE | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getServices()` 获取服务列表
- 调用 `getServiceFavorites()` 获取收藏状态
- 收藏/取消收藏调用 `addServiceFavorite()` / `removeServiceFavorite()`
- 支持按价格、评分排序

---

## 8. service-detail/index.vue - 服务详情

**页面路径**: `cg-vue/src/views/user/service-detail/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/services/${id}` | GET | 无 | `Service` | 正常 |
| 2 | `/api/merchant/${id}` | GET | 无 | `MerchantInfo` | 正常 |
| 3 | `/api/user/pets` | GET | 无 | `Pet[]` | 正常 |
| 4 | `/api/merchant/${id}/reviews` | GET | 无 | `MerchantReview[]` | 正常 |
| 5 | `/api/user/favorites/services` | POST | `{ serviceId }` | 收藏结果 | 正常 |
| 6 | `/api/user/favorites/services/${serviceId}` | DELETE | 无 | 无 | 正常 |
| 7 | `/api/user/favorites/services/${serviceId}/check` | GET | 无 | `{ isFavorited: boolean }` | 正常 |

### 使用说明
- 调用 `getServiceById()` 获取服务详情
- 调用 `getMerchantInfo()` 获取商家信息
- 调用 `getUserPets()` 获取用户宠物列表用于预约
- 调用 `getMerchantReviews()` 获取商家评价
- 支持收藏/取消收藏服务

---

## 9. user-appointments/index.vue - 我的预约

**页面路径**: `cg-vue/src/views/user/user-appointments/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/appointments` | GET | 无 | `Appointment[]` | 正常 |
| 2 | `/api/user/appointments/${id}/cancel` | PUT | 无 | 无 | 正常 |
| 3 | `/api/user/appointments/stats` | GET | 无 | `AppointmentStats` | 正常 |

### 使用说明
- 页面加载时调用 `getUserAppointments()` 获取预约列表
- 调用 `getAppointmentStats()` 获取预约统计
- 取消预约调用 `cancelAppointment()`
- 支持按状态筛选：pending, confirmed, completed, cancelled

---

## 10. user-book/index.vue - 我的预约记录

**页面路径**: `cg-vue/src/views/user/user-book/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/appointments` | GET | 无 | `Appointment[]` | 正常 |
| 2 | `/api/user/appointments/${id}` | GET | 无 | `AppointmentDetail` | 正常 |
| 3 | `/api/user/appointments/${id}/cancel` | PUT | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getUserAppointments()` 获取预约列表
- 查看详情调用 `getAppointmentById()`
- 取消预约调用 `cancelAppointment()`
- 与 user-appointments 页面功能类似

---

## 11. appointment-confirm/index.vue - 预约确认页

**页面路径**: `cg-vue/src/views/user/appointment-confirm/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/services/${id}` | GET | 无 | `Service` | 正常 |
| 2 | `/api/merchant/${id}` | GET | 无 | `MerchantInfo` | 正常 |
| 3 | `/api/user/pets` | GET | 无 | `Pet[]` | 正常 |
| 4 | `/api/merchant/${merchantId}/available-slots` | GET | `{ date }` | `AvailableSlotsResponse` | 正常 |
| 5 | `/api/user/appointments` | POST | `{ serviceId, petId, appointmentTime, notes? }` | 预约结果 | 正常 |

### 使用说明
- 调用 `getServiceById()` 获取服务信息
- 调用 `getMerchantInfo()` 获取商家信息
- 调用 `getUserPets()` 获取宠物列表
- 调用 `getAvailableSlots()` 获取可预约时间段
- 提交预约调用 `createAppointment()`

---

## 12. user-orders/index.vue - 我的订单

**页面路径**: `cg-vue/src/views/user/user-orders/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/orders` | GET | 无 | `ProductOrder[]` | 正常 |
| 2 | `/api/user/orders/${id}/cancel` | PUT | 无 | 无 | 正常 |
| 3 | `/api/user/orders/batch-cancel` | PUT | `{ ids: number[] }` | 无 | 正常 |
| 4 | `/api/user/orders/batch-delete` | DELETE | `{ data: { ids } }` | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getProductOrders()` 获取订单列表
- 支持按状态筛选：pending, paid, shipped, completed, cancelled
- 支持批量取消和批量删除

---

## 13. order-detail/index.vue - 订单详情

**页面路径**: `cg-vue/src/views/user/order-detail/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/orders/${id}` | GET | 无 | `OrderDetail` | 正常 |
| 2 | `/api/user/orders/${id}/cancel` | PUT | 无 | 无 | 正常 |
| 3 | `/api/user/orders/${id}/confirm` | PUT | 无 | 无 | 正常 |
| 4 | `/api/user/orders/${id}/refund` | POST | `{ reason }` | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getOrderById()` 获取订单详情
- 取消订单调用 `cancelOrder()`
- 确认收货调用 `confirmReceiveOrder()`
- 申请退款调用 `refundOrder()`

---

## 14. checkout/index.vue - 下单确认页

**页面路径**: `cg-vue/src/views/user/checkout/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/addresses` | GET | 无 | `Address[]` | 正常 |
| 2 | `/api/user/orders/preview` | POST | `{ items: { productId, quantity }[] }` | `OrderPreview` | 正常 |
| 3 | `/api/user/orders` | POST | `{ productId, addressId, quantity }` | 订单结果 | 正常 |

### 使用说明
- 页面加载时调用 `getAddresses()` 获取收货地址列表
- 调用 `previewOrder()` 获取订单预览信息
- 提交订单调用 `createOrder()` 或 `purchaseProduct()`
- 支持选择收货地址和支付方式

---

## 15. pay/index.vue - 支付页

**页面路径**: `cg-vue/src/views/user/pay/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/orders/${id}` | GET | 无 | `OrderDetail` | 正常 |
| 2 | `/api/user/orders/${orderId}/pay` | POST | `{ payMethod }` | 支付结果 | 正常 |
| 3 | `/api/user/orders/${orderId}/pay/status` | GET | 无 | `PayStatusResponse` | 正常 |

### 使用说明
- 页面加载时调用 `getOrderById()` 获取订单信息
- 调用 `payOrder()` 发起支付
- 轮询调用 `getPayStatus()` 查询支付状态
- 支持微信支付、支付宝支付

---

## 16. user-merchant/index.vue - 商家列表

**页面路径**: `cg-vue/src/views/user/user-merchant/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchants` | GET | `{ keyword?, rating?, sortBy?, page?, pageSize? }` | `{ data: MerchantListItem[], total: number }` | 正常 |
| 2 | `/api/user/favorites` | GET | 无 | `FavoriteMerchant[]` | 正常 |
| 3 | `/api/user/favorites` | POST | `{ merchantId }` | `Favorite` | 正常 |
| 4 | `/api/user/favorites/${merchantId}` | DELETE | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getMerchantList()` 获取商家列表
- 调用 `getFavorites()` 获取收藏状态
- 收藏/取消收藏调用 `addFavorite()` / `removeFavorite()`
- 支持按评分、距离筛选和排序

---

## 17. user-shop/index.vue - 店铺详情

**页面路径**: `cg-vue/src/views/user/user-shop/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/merchant/${id}` | GET | 无 | `MerchantInfo` | 正常 |
| 2 | `/api/merchant/${id}/services` | GET | 无 | `Service[]` | 正常 |
| 3 | `/api/merchant/${id}/products` | GET | 无 | `Product[]` | 正常 |
| 4 | `/api/merchant/${id}/reviews` | GET | 无 | `MerchantReview[]` | 正常 |
| 5 | `/api/user/favorites` | POST | `{ merchantId }` | `Favorite` | 正常 |
| 6 | `/api/user/favorites/${merchantId}` | DELETE | 无 | 无 | 正常 |

### 使用说明
- 调用 `getMerchantInfo()` 获取店铺信息
- 调用 `getMerchantServices()` 获取店铺服务
- 调用 `getMerchantProducts()` 获取店铺商品
- 调用 `getMerchantReviews()` 获取店铺评价
- 支持收藏/取消收藏店铺

---

## 18. user-favorites/index.vue - 收藏评价

**页面路径**: `cg-vue/src/views/user/user-favorites/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/favorites` | GET | 无 | `FavoriteMerchant[]` | 正常 |
| 2 | `/api/user/favorites/${merchantId}` | DELETE | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getFavorites()` 获取收藏列表
- 取消收藏调用 `removeFavorite()`
- 支持按商家评分筛选

---

## 19. product-detail/index.vue - 商品详情

**页面路径**: `cg-vue/src/views/user/product-detail/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/products/${id}` | GET | 无 | `Product` | 正常 |
| 2 | `/api/merchant/${id}` | GET | 无 | `MerchantInfo` | 正常 |
| 3 | `/api/products/${productId}/reviews` | GET | 无 | `Review[]` | 正常 |
| 4 | `/api/user/favorites/products` | POST | `{ productId }` | 收藏结果 | 正常 |
| 5 | `/api/user/favorites/products/${productId}` | DELETE | 无 | 无 | 正常 |
| 6 | `/api/user/favorites/products/${productId}/check` | GET | 无 | `{ isFavorited: boolean }` | 正常 |
| 7 | `/api/user/cart` | POST | `{ productId, quantity }` | 加入购物车结果 | 正常 |

### 使用说明
- 调用 `getProductById()` 获取商品信息
- 调用 `getMerchantInfo()` 获取商家信息
- 调用 `getProductReviews()` 获取商品评价
- 支持收藏商品、加入购物车、立即购买

---

## 20. user-cart/index.vue - 购物车

**页面路径**: `cg-vue/src/views/user/user-cart/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/cart` | GET | 无 | `CartItem[]` | 正常 |
| 2 | `/api/user/cart` | PUT | `{ productId, quantity }` | 更新结果 | 正常 |
| 3 | `/api/user/cart/${productId}` | DELETE | 无 | 无 | 正常 |
| 4 | `/api/user/cart/batch` | DELETE | `{ data: { productIds } }` | 无 | 正常 |
| 5 | `/api/user/orders/preview` | POST | `{ items }` | `OrderPreview` | 正常 |

### 使用说明
- 页面加载时调用 `getCart()` 获取购物车列表
- 修改数量调用 `updateCartItem()`
- 删除商品调用 `removeFromCart()`
- 批量删除调用 `batchRemoveFromCart()`
- 结算时调用 `previewOrder()` 预览订单

---

## 21. user-reviews/index.vue - 服务评价

**页面路径**: `cg-vue/src/views/user/user-reviews/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/reviews` | GET | `{ rating?, startDate?, endDate?, page?, pageSize? }` | `{ data: UserReview[], total: number }` | 正常 |

### 使用说明
- 页面加载时调用 `getUserReviewsList()` 获取评价列表
- 支持按评分和日期范围筛选

---

## 22. my-reviews/index.vue - 我的评价

**页面路径**: `cg-vue/src/views/user/my-reviews/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/reviews` | GET | `{ type?, startDate?, endDate? }` | `Review[]` | 正常 |
| 2 | `/api/user/reviews/${id}` | PUT | `{ rating, comment }` | 更新结果 | 正常 |
| 3 | `/api/user/reviews/${id}` | DELETE | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getUserReviews()` 获取评价列表
- 编辑评价调用 `updateReview()`
- 删除评价调用 `deleteReview()`
- 支持按评价类型和日期范围筛选

---

## 23. search/index.vue - 搜索页

**页面路径**: `cg-vue/src/views/user/search/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/products/search` | GET | `{ keyword }` | `Product[]` | 正常 |
| 2 | `/api/services/search` | GET | `{ keyword }` | `Service[]` | 正常 |
| 3 | `/api/merchants/search` | GET | `{ keyword }` | `MerchantInfo[]` | 正常 |
| 4 | `/api/user/search-history` | POST | `{ keyword }` | 保存结果 | 正常 |
| 5 | `/api/user/search-history` | GET | `{ limit? }` | `string[]` | 正常 |
| 6 | `/api/user/search-history` | DELETE | 无 | 无 | 正常 |
| 7 | `/api/search/hot-keywords` | GET | `{ limit? }` | `HotKeyword[]` | 正常 |

### 使用说明
- 搜索时调用 `searchProducts()`, `searchServices()`, `searchMerchants()`
- 搜索历史存储在 localStorage，同时可调用 API 保存到服务端
- 支持热门搜索关键词

---

## 24. notifications/index.vue - 消息通知

**页面路径**: `cg-vue/src/views/user/notifications/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/notifications` | GET | `{ type?, isRead? }` | `Notification[]` | 正常 |
| 2 | `/api/user/notifications/${id}/read` | PUT | 无 | 无 | 正常 |
| 3 | `/api/user/notifications/read-all` | PUT | 无 | 无 | 正常 |
| 4 | `/api/user/notifications/batch-read` | PUT | `{ ids: number[] }` | 无 | 正常 |
| 5 | `/api/user/notifications/${id}` | DELETE | 无 | 无 | 正常 |
| 6 | `/api/user/notifications/batch` | DELETE | `{ data: { ids } }` | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getNotifications()` 获取通知列表
- 支持按类型和已读状态筛选
- 支持单条/批量标记已读
- 支持单条/批量删除

---

## 25. user-announcements/index.vue - 公告通知

**页面路径**: `cg-vue/src/views/user/user-announcements/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/announcements` | GET | 无 | `Announcement[]` | 正常 |
| 2 | `/api/announcements/${id}` | GET | 无 | `Announcement` | 正常 |

### 使用说明
- 页面加载时调用 `getAnnouncements()` 获取公告列表
- 查看详情调用 `getAnnouncementById()`
- 支持按标题搜索和分类筛选
- 已读状态存储在 localStorage

---

## 26. announcement-detail/index.vue - 公告详情

**页面路径**: `cg-vue/src/views/user/announcement-detail/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/announcements/${id}` | GET | 无 | `Announcement` | 正常 |
| 2 | `/api/announcements` | GET | 无 | `Announcement[]` | 正常 |

### 使用说明
- 页面加载时调用 `getAnnouncementById()` 获取公告详情
- 调用 `getAnnouncements()` 获取相关公告列表

---

## 27. addresses/index.vue - 收货地址管理

**页面路径**: `cg-vue/src/views/user/addresses/index.vue`

### API 调用列表

| 序号 | API 路径 | 方法 | 请求参数 | 响应格式 | 状态 |
|------|----------|------|----------|----------|------|
| 1 | `/api/user/addresses` | GET | 无 | `Address[]` | 正常 |
| 2 | `/api/user/addresses` | POST | `Omit<Address, 'id'>` | `Address` | 正常 |
| 3 | `/api/user/addresses/${id}` | PUT | `Partial<Address>` | `Address` | 正常 |
| 4 | `/api/user/addresses/${id}` | DELETE | 无 | 无 | 正常 |
| 5 | `/api/user/addresses/${id}/default` | PUT | 无 | 无 | 正常 |

### 使用说明
- 页面加载时调用 `getAddresses()` 获取地址列表
- 添加地址调用 `addAddress()`
- 编辑地址调用 `updateAddress()`
- 删除地址调用 `deleteAddress()`
- 设置默认地址调用 `setDefaultAddress()`

---

## API 汇总表

### 按 API 文件分类

#### user.ts 中的 API

| API 路径 | 方法 | 使用页面 |
|----------|------|----------|
| `/api/user/pets` | GET | user-home, user-profile, user-pets, pet-edit, service-detail, appointment-confirm |
| `/api/user/pets` | POST | pet-edit |
| `/api/user/pets/${id}` | GET | pet-edit |
| `/api/user/pets/${id}` | PUT | pet-edit, user-pets |
| `/api/user/pets/${id}` | DELETE | user-pets |
| `/api/user/appointments` | GET | user-home, user-profile, user-appointments, user-book |
| `/api/user/appointments` | POST | appointment-confirm |
| `/api/user/appointments/${id}` | GET | user-book |
| `/api/user/appointments/${id}/cancel` | PUT | user-appointments, user-book |
| `/api/user/appointments/stats` | GET | user-appointments |
| `/api/services` | GET | service-list |
| `/api/services/${id}` | GET | service-detail, appointment-confirm |
| `/api/services/search` | GET | search |
| `/api/services/recommended` | GET | user-home |
| `/api/merchant/${id}` | GET | service-detail, appointment-confirm, product-detail, user-shop |
| `/api/merchant/${id}/services` | GET | user-shop |
| `/api/merchant/${id}/products` | GET | user-shop |
| `/api/merchant/${id}/reviews` | GET | service-detail, user-shop |
| `/api/merchant/${id}/available-slots` | GET | appointment-confirm |
| `/api/user/favorites` | GET | user-merchant, user-favorites |
| `/api/user/favorites` | POST | user-merchant, user-shop |
| `/api/user/favorites/${merchantId}` | DELETE | user-merchant, user-favorites, user-shop |
| `/api/user/favorites/services` | GET | service-list |
| `/api/user/favorites/services` | POST | service-list, service-detail |
| `/api/user/favorites/services/${serviceId}` | DELETE | service-list, service-detail |
| `/api/user/favorites/services/${serviceId}/check` | GET | service-detail |
| `/api/user/favorites/products` | POST | product-detail |
| `/api/user/favorites/products/${productId}` | DELETE | product-detail |
| `/api/user/favorites/products/${productId}/check` | GET | product-detail |
| `/api/products/search` | GET | search |
| `/api/products/${id}` | GET | product-detail |
| `/api/products/${productId}/reviews` | GET | product-detail |
| `/api/user/cart` | GET | user-cart |
| `/api/user/cart` | POST | product-detail |
| `/api/user/cart` | PUT | user-cart |
| `/api/user/cart/${productId}` | DELETE | user-cart |
| `/api/user/cart/batch` | DELETE | user-cart |
| `/api/user/orders` | GET | user-orders |
| `/api/user/orders` | POST | checkout |
| `/api/user/orders/${id}` | GET | order-detail, pay |
| `/api/user/orders/${id}/cancel` | PUT | user-orders, order-detail |
| `/api/user/orders/${id}/confirm` | PUT | order-detail |
| `/api/user/orders/${id}/refund` | POST | order-detail |
| `/api/user/orders/${orderId}/pay` | POST | pay |
| `/api/user/orders/${orderId}/pay/status` | GET | pay |
| `/api/user/orders/batch-cancel` | PUT | user-orders |
| `/api/user/orders/batch-delete` | DELETE | user-orders |
| `/api/user/orders/preview` | POST | checkout, user-cart |
| `/api/user/addresses` | GET | checkout, addresses |
| `/api/user/addresses` | POST | addresses |
| `/api/user/addresses/${id}` | PUT | addresses |
| `/api/user/addresses/${id}` | DELETE | addresses |
| `/api/user/addresses/${id}/default` | PUT | addresses |
| `/api/user/reviews` | GET | user-home, user-profile, my-reviews, user-reviews |
| `/api/user/reviews/${id}` | PUT | my-reviews |
| `/api/user/reviews/${id}` | DELETE | my-reviews |
| `/api/merchants` | GET | user-merchant |
| `/api/merchants/search` | GET | search |
| `/api/user/services` | GET | user-services |
| `/api/user/home/stats` | GET | user-home |
| `/api/user/home/activities` | GET | user-home |
| `/api/user/search-history` | GET | search |
| `/api/user/search-history` | POST | search |
| `/api/user/search-history` | DELETE | search |

#### notification.ts 中的 API

| API 路径 | 方法 | 使用页面 |
|----------|------|----------|
| `/api/user/notifications` | GET | notifications |
| `/api/user/notifications/${id}/read` | PUT | notifications |
| `/api/user/notifications/read-all` | PUT | notifications |
| `/api/user/notifications/batch-read` | PUT | notifications |
| `/api/user/notifications/${id}` | DELETE | notifications |
| `/api/user/notifications/batch` | DELETE | notifications |

#### announcement.ts 中的 API

| API 路径 | 方法 | 使用页面 |
|----------|------|----------|
| `/api/announcements` | GET | user-announcements, announcement-detail |
| `/api/announcements/${id}` | GET | user-announcements, announcement-detail |

#### auth.ts 中的 API

| API 路径 | 方法 | 使用页面 |
|----------|------|----------|
| `/api/auth/userinfo` | GET | user-profile, profile-edit |
| `/api/auth/userinfo` | PUT | profile-edit |

---

## 状态说明

| 状态 | 说明 |
|------|------|
| 正常 | API 已定义并在页面中使用 |
| 异常 | API 调用存在问题或未完成 |
| 未使用 | API 已定义但页面中未使用 |

---

## 注意事项

1. **开发环境模拟数据**: 大部分页面在开发环境(`import.meta.env.DEV`)下使用硬编码的模拟数据
2. **搜索历史**: 搜索历史同时存储在 localStorage 和服务端
3. **公告已读状态**: 存储在 localStorage 中
4. **购物车**: 部分购物车操作可能仅存储在前端状态
5. **图片资源**: 部分图片使用 `trae-api-cn.mchost.guru` 的 AI 生成图片服务

---

## 缺失页面检查

用户要求检查 28 个页面，实际在 `cg-vue/src/views/user/` 目录下找到 27 个页面：

1. ✅ user-home/index.vue
2. ✅ user-profile/index.vue
3. ✅ profile-edit/index.vue
4. ✅ user-pets/index.vue
5. ✅ pet-edit/index.vue
6. ✅ user-services/index.vue
7. ✅ service-list/index.vue
8. ✅ service-detail/index.vue
9. ✅ user-appointments/index.vue
10. ✅ user-book/index.vue
11. ✅ appointment-confirm/index.vue
12. ✅ user-orders/index.vue
13. ✅ order-detail/index.vue
14. ✅ checkout/index.vue
15. ✅ pay/index.vue
16. ✅ user-merchant/index.vue
17. ✅ user-shop/index.vue
18. ✅ user-favorites/index.vue
19. ✅ product-detail/index.vue
20. ✅ user-cart/index.vue
21. ✅ user-reviews/index.vue
22. ✅ my-reviews/index.vue
23. ✅ search/index.vue
24. ✅ notifications/index.vue
25. ✅ user-announcements/index.vue
26. ✅ announcement-detail/index.vue
27. ✅ addresses/index.vue

**注**: 用户列表中的第 28 个页面可能是 `UserLayout.vue`，这是一个布局组件而非页面。

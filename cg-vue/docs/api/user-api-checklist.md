# 用户端API接口清单

## 页面与接口对应关系

| 页面名称 | 文件路径 | 所需接口 | 状态 |
|---------|---------|---------|------|
| UserLayout.vue | views/user/UserLayout.vue | getUserInfo, getUnreadCount, logout | ✅ 完整 |
| user-home | views/user/user-home/index.vue | getHomeStats, getRecentActivities, getRecommendedServices | ✅ 完整 |
| user-profile | views/user/user-profile/index.vue | getUserInfo | ✅ 完整 |
| profile-edit | views/user/profile-edit/index.vue | getUserInfo, updateUserInfo | ✅ 完整 |
| user-pets | views/user/user-pets/index.vue | getUserPets, deletePet | ✅ 完整 |
| pet-edit | views/user/pet-edit/index.vue | getPetById, addPet, updatePet | ✅ 完整 |
| addresses | views/user/addresses/index.vue | getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress | ✅ 完整 |
| user-merchant | views/user/user-merchant/index.vue | getMerchantList, addFavorite, removeFavorite | ✅ 完整 |
| user-shop | views/user/user-shop/index.vue | getMerchantInfo, getMerchantServices, getMerchantProducts, getMerchantReviews | ✅ 完整 |
| service-list | views/user/service-list/index.vue | getServices, getServicesByKeyword | ✅ 完整 |
| service-detail | views/user/service-detail/index.vue | getServiceById, getUserPets, createAppointment, getAvailableSlots | ✅ 完整 |
| product-detail | views/user/product-detail/index.vue | getProductById, getProductReviews, addProductFavorite, removeProductFavorite, addToCart | ✅ 完整 |
| user-cart | views/user/user-cart/index.vue | getCart, updateCartItem, removeFromCart, batchRemoveFromCart | ✅ 完整 |
| checkout | views/user/checkout/index.vue | getAddresses, previewOrder, createOrder | ✅ 完整 |
| pay | views/user/pay/index.vue | getOrderById, payOrder, getPayStatus | ✅ 完整 |
| user-orders | views/user/user-orders/index.vue | getProductOrders, getUserAppointments, cancelOrder, deleteOrder, batchCancelOrders, batchDeleteOrders | ✅ 完整 |
| order-detail | views/user/order-detail/index.vue | getOrderById, cancelOrder, refundOrder, confirmReceiveOrder | ✅ 完整 |
| user-appointments | views/user/user-appointments/index.vue | getUserAppointments, cancelAppointment, getAppointmentById | ✅ 完整 |
| user-book | views/user/user-book/index.vue | getUserAppointments, cancelAppointment, getAppointmentStats | ✅ 完整 |
| appointment-confirm | views/user/appointment-confirm/index.vue | getServiceById, getUserPets, getMerchantInfo, createAppointment | ✅ 完整 |
| user-reviews | views/user/user-reviews/index.vue | getUserAppointments, getUserReviewsList, addReview | ✅ 完整 |
| my-reviews | views/user/my-reviews/index.vue | getUserReviews, updateReview, deleteReview | ✅ 完整 |
| notifications | views/user/notifications/index.vue | getNotifications, markAsRead, markAllAsRead, markBatchAsRead, deleteNotification, deleteBatchNotifications | ✅ 完整 |
| user-services | views/user/user-services/index.vue | getUserPurchasedServices | ✅ 完整 |
| user-announcements | views/user/user-announcements/index.vue | getAnnouncements | ✅ 完整 |
| announcement-detail | views/user/announcement-detail/index.vue | getAnnouncementById | ✅ 完整 |
| user-favorites | views/user/user-favorites/index.vue | getFavorites, getServiceFavorites, removeFavorite, removeServiceFavorite | ✅ 完整 |
| search | views/user/search/index.vue | getSearchSuggestions, getHotKeywords, getSearchHistory, saveSearchHistory, clearSearchHistory, searchProducts, searchServices | ✅ 完整 |

---

## 接口详细清单

### 1. 认证相关接口 (auth.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 1 | 用户登录 | POST | /api/auth/login | ✅ 已存在 | Login.vue |
| 2 | 用户注册 | POST | /api/auth/register | ✅ 已存在 | Register.vue |
| 3 | 退出登录 | POST | /api/auth/logout | ✅ 已存在 | UserLayout.vue |
| 4 | 获取用户信息 | GET | /api/auth/userinfo | ✅ 已存在 | UserLayout.vue, user-profile, profile-edit |
| 5 | 更新用户信息 | PUT | /api/auth/userinfo | ✅ 已存在 | profile-edit |
| 6 | 修改密码 | PUT | /api/auth/password | ✅ 已存在 | user-profile |
| 7 | 发送验证码 | POST | /api/auth/sendVerifyCode | ✅ 已存在 | forgot-password |
| 8 | 重置密码 | POST | /api/auth/resetPassword | ✅ 已存在 | forgot-password |

---

### 2. 宠物管理接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 9 | 获取宠物列表 | GET | /api/user/pets | ✅ 已存在 | user-pets, service-detail, appointment-confirm |
| 10 | 获取宠物详情 | GET | /api/user/pets/:id | ✅ 已存在 | pet-edit |
| 11 | 添加宠物 | POST | /api/user/pets | ✅ 已存在 | pet-edit |
| 12 | 更新宠物 | PUT | /api/user/pets/:id | ✅ 已存在 | pet-edit |
| 13 | 删除宠物 | DELETE | /api/user/pets/:id | ✅ 已存在 | user-pets |

---

### 3. 地址管理接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 14 | 获取地址列表 | GET | /api/user/addresses | ✅ 已存在 | addresses, checkout |
| 15 | 添加地址 | POST | /api/user/addresses | ✅ 已存在 | addresses |
| 16 | 更新地址 | PUT | /api/user/addresses/:id | ✅ 已存在 | addresses |
| 17 | 删除地址 | DELETE | /api/user/addresses/:id | ✅ 已存在 | addresses |
| 18 | 设置默认地址 | PUT | /api/user/addresses/:id/default | ✅ 已存在 | addresses |

---

### 4. 商家服务接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 19 | 获取商家列表 | GET | /api/merchants | ✅ 已存在 | user-merchant |
| 20 | 获取商家详情 | GET | /api/merchant/:id | ✅ 已存在 | user-shop, appointment-confirm |
| 21 | 获取商家服务 | GET | /api/merchant/:id/services | ✅ 已存在 | user-shop |
| 22 | 获取商家商品 | GET | /api/merchant/:id/products | ✅ 已存在 | user-shop |
| 23 | 获取商家评价 | GET | /api/merchant/:id/reviews | ✅ 已存在 | user-shop |
| 24 | 获取可用预约时段 | GET | /api/merchant/:id/available-slots | ✅ 已存在 | service-detail |

---

### 5. 服务接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 25 | 获取服务列表 | GET | /api/services | ✅ 已存在 | service-list |
| 26 | 获取服务详情 | GET | /api/services/:id | ✅ 已存在 | service-detail, appointment-confirm |
| 27 | 搜索服务 | GET | /api/services/search | ✅ 已存在 | service-list, search |
| 28 | 获取推荐服务 | GET | /api/services/recommended | ✅ 已存在 | user-home |

---

### 6. 商品购物接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 29 | 获取商品列表 | GET | /api/products | ✅ 已存在 | product-list |
| 30 | 获取商品详情 | GET | /api/products/:id | ✅ 已存在 | product-detail |
| 31 | 搜索商品 | GET | /api/products/search | ✅ 已存在 | search |
| 32 | 获取商品评价 | GET | /api/products/:id/reviews | ✅ 已存在 | product-detail |
| 33 | 添加商品收藏 | POST | /api/user/favorites/products | ✅ 已存在 | product-detail |
| 34 | 取消商品收藏 | DELETE | /api/user/favorites/products/:id | ✅ 已存在 | product-detail |
| 35 | 检查商品收藏状态 | GET | /api/user/favorites/products/:id/check | ✅ 已存在 | product-detail |
| 36 | 加入购物车 | POST | /api/user/cart | ✅ 已存在 | product-detail |
| 37 | 获取购物车 | GET | /api/user/cart | ✅ 已存在 | user-cart |
| 38 | 更新购物车商品 | PUT | /api/user/cart | ✅ 已存在 | user-cart |
| 39 | 删除购物车商品 | DELETE | /api/user/cart/:id | ✅ 已存在 | user-cart |
| 40 | 批量删除购物车商品 | DELETE | /api/user/cart/batch | ✅ 已存在 | user-cart |

---

### 7. 订单管理接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 41 | 获取订单列表 | GET | /api/user/orders | ✅ 已存在 | user-orders |
| 42 | 获取订单详情 | GET | /api/user/orders/:id | ✅ 已存在 | order-detail, pay |
| 43 | 创建订单 | POST | /api/user/orders | ✅ 已存在 | checkout |
| 44 | 预览订单 | POST | /api/user/orders/preview | ✅ 已存在 | checkout |
| 45 | 支付订单 | POST | /api/user/orders/:id/pay | ✅ 已存在 | pay |
| 46 | 获取支付状态 | GET | /api/user/orders/:id/pay/status | ✅ 已存在 | pay |
| 47 | 取消订单 | PUT | /api/user/orders/:id/cancel | ✅ 已存在 | order-detail, user-orders |
| 48 | 退款订单 | POST | /api/user/orders/:id/refund | ✅ 已存在 | order-detail |
| 49 | 确认收货 | PUT | /api/user/orders/:id/confirm | ✅ 已存在 | order-detail |
| 50 | 删除订单 | DELETE | /api/user/orders/:id | ✅ 已存在 | user-orders |
| 51 | 批量取消订单 | PUT | /api/user/orders/batch-cancel | ✅ 已存在 | user-orders |
| 52 | 批量删除订单 | DELETE | /api/user/orders/batch-delete | ✅ 已存在 | user-orders |

---

### 8. 预约管理接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 53 | 获取预约列表 | GET | /api/user/appointments | ✅ 已存在 | user-appointments, user-book, user-reviews |
| 54 | 获取预约详情 | GET | /api/user/appointments/:id | ✅ 已存在 | user-appointments |
| 55 | 创建预约 | POST | /api/user/appointments | ✅ 已存在 | service-detail, appointment-confirm |
| 56 | 取消预约 | PUT | /api/user/appointments/:id/cancel | ✅ 已存在 | user-appointments, user-book |
| 57 | 获取预约统计 | GET | /api/user/appointments/stats | ✅ 已存在 | user-book |

---

### 9. 评价管理接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 58 | 添加评价 | POST | /api/user/reviews | ✅ 已存在 | user-reviews |
| 59 | 获取用户评价列表 | GET | /api/user/reviews | ✅ 已存在 | my-reviews |
| 60 | 获取评价详情列表 | GET | /api/user/reviews | ✅ 已存在 | user-reviews |
| 61 | 更新评价 | PUT | /api/user/reviews/:id | ✅ 已存在 | my-reviews |
| 62 | 删除评价 | DELETE | /api/user/reviews/:id | ✅ 已存在 | my-reviews |

---

### 10. 收藏管理接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 63 | 获取收藏商家列表 | GET | /api/user/favorites | ✅ 已存在 | user-favorites |
| 64 | 添加商家收藏 | POST | /api/user/favorites | ✅ 已存在 | user-merchant, user-shop |
| 65 | 取消商家收藏 | DELETE | /api/user/favorites/:id | ✅ 已存在 | user-favorites, user-merchant |
| 66 | 获取收藏服务列表 | GET | /api/user/favorites/services | ✅ 已存在 | user-favorites |
| 67 | 添加服务收藏 | POST | /api/user/favorites/services | ✅ 已存在 | service-detail |
| 68 | 取消服务收藏 | DELETE | /api/user/favorites/services/:id | ✅ 已存在 | user-favorites |

---

### 11. 通知管理接口 (notification.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 69 | 获取通知列表 | GET | /api/user/notifications | ✅ 已存在 | notifications |
| 70 | 标记已读 | PUT | /api/user/notifications/:id/read | ✅ 已存在 | notifications |
| 71 | 全部标记已读 | PUT | /api/user/notifications/read-all | ✅ 已存在 | notifications |
| 72 | 批量标记已读 | PUT | /api/user/notifications/batch-read | ✅ 已存在 | notifications |
| 73 | 删除通知 | DELETE | /api/user/notifications/:id | ✅ 已存在 | notifications |
| 74 | 批量删除通知 | DELETE | /api/user/notifications/batch | ✅ 已存在 | notifications |
| 75 | 获取未读数量 | GET | /api/user/notifications/unread-count | ✅ 已存在 | UserLayout.vue |

---

### 12. 搜索接口 (search.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 76 | 获取搜索建议 | GET | /api/search/suggestions | ✅ 已存在 | search |
| 77 | 获取热门关键词 | GET | /api/search/hot-keywords | ✅ 已存在 | search |
| 78 | 保存搜索历史 | POST | /api/user/search-history | ✅ 已存在 | search |
| 79 | 获取搜索历史 | GET | /api/user/search-history | ✅ 已存在 | search |
| 80 | 清空搜索历史 | DELETE | /api/user/search-history | ✅ 已存在 | search |

---

### 13. 公告接口 (announcement.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 81 | 获取公告列表 | GET | /api/announcements | ✅ 已存在 | user-announcements |
| 82 | 获取公告详情 | GET | /api/announcements/:id | ✅ 已存在 | announcement-detail |

---

### 14. 用户首页接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 83 | 获取首页统计 | GET | /api/user/home/stats | ✅ 已存在 | user-home |
| 84 | 获取最近活动 | GET | /api/user/home/activities | ✅ 已存在 | user-home |

---

### 15. 用户已购服务接口 (user.ts)

| 序号 | 接口名称 | 方法 | URL | 状态 | 所属页面 |
|-----|---------|------|-----|------|---------|
| 85 | 获取已购服务列表 | GET | /api/user/services | ✅ 已存在 | user-services |

---

## 接口统计

| 分类 | 接口数量 | 状态 |
|------|---------|------|
| 认证相关 | 8 | ✅ 完整 |
| 宠物管理 | 5 | ✅ 完整 |
| 地址管理 | 5 | ✅ 完整 |
| 商家服务 | 6 | ✅ 完整 |
| 服务接口 | 4 | ✅ 完整 |
| 商品购物 | 12 | ✅ 完整 |
| 订单管理 | 12 | ✅ 完整 |
| 预约管理 | 5 | ✅ 完整 |
| 评价管理 | 5 | ✅ 完整 |
| 收藏管理 | 6 | ✅ 完整 |
| 通知管理 | 7 | ✅ 完整 |
| 搜索接口 | 5 | ✅ 完整 |
| 公告接口 | 2 | ✅ 完整 |
| 用户首页 | 2 | ✅ 完整 |
| 已购服务 | 1 | ✅ 完整 |
| **总计** | **85** | **✅ 100%** |

---

## 页面覆盖率

| 页面类型 | 页面数量 | 接口覆盖 | 覆盖率 |
|---------|---------|---------|--------|
| 布局组件 | 1 | ✅ 完整 | 100% |
| 用户首页 | 1 | ✅ 完整 | 100% |
| 个人中心 | 2 | ✅ 完整 | 100% |
| 宠物管理 | 2 | ✅ 完整 | 100% |
| 地址管理 | 1 | ✅ 完整 | 100% |
| 商家浏览 | 2 | ✅ 完整 | 100% |
| 服务相关 | 3 | ✅ 完整 | 100% |
| 商品购物 | 4 | ✅ 完整 | 100% |
| 订单管理 | 3 | ✅ 完整 | 100% |
| 预约管理 | 3 | ✅ 完整 | 100% |
| 评价管理 | 2 | ✅ 完整 | 100% |
| 通知公告 | 3 | ✅ 完整 | 100% |
| 收藏管理 | 1 | ✅ 完整 | 100% |
| **总计** | **28** | **✅ 完整** | **100%** |

---

## 更新日志

| 日期 | 版本 | 更新内容 |
|------|------|---------|
| 2024-01-15 | 1.0.0 | 初始版本，完成用户端28个页面85个API接口清单 |

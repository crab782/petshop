# 用户端 Mock 服务完整使用文档

## 目录

1. [概述](#概述)
2. [架构设计](#架构设计)
3. [快速开始](#快速开始)
4. [API端点对照表](#api端点对照表)
5. [数据结构说明](#数据结构说明)
6. [场景切换使用方法](#场景切换使用方法)
7. [数据自定义方法](#数据自定义方法)
8. [开发者工具使用](#开发者工具使用)
9. [常见问题解答](#常见问题解答)
10. [最佳实践建议](#最佳实践建议)

---

## 概述

用户端 Mock 服务是一个完整的模拟后端 API 系统，用于前端开发和测试。它提供了丰富的数据模拟、场景切换、错误处理等功能，帮助开发者在没有真实后端的情况下进行前端开发。

### 主要特性

- **完整的 API 模拟**：覆盖用户端所有 API 端点
- **场景切换**：支持正常、慢速、错误、空数据等多种场景
- **数据自定义**：支持动态修改和生成测试数据
- **开发者工具**：提供浏览器控制台 API，方便调试
- **快照管理**：保存和恢复测试场景状态
- **模块化设计**：按功能模块组织，易于扩展

### 技术栈

- **Mock.js**：数据模拟和拦截 HTTP 请求
- **TypeScript**：类型安全
- **Vite**：开发环境集成

---

## 架构设计

### 目录结构

```
src/mock/user/
├── config/                      # 配置模块
│   ├── index.ts                 # 统一导出
│   ├── mock-config.ts           # Mock 配置定义
│   ├── data-manager.ts          # 数据管理器
│   ├── scenario-controller.ts   # 场景控制器
│   ├── dev-tools.ts             # 开发者工具 API
│   └── scenarios/               # 场景模块
│       ├── index.ts             # 场景服务入口
│       ├── types.ts             # 场景类型定义
│       ├── config.ts            # 场景配置管理
│       ├── errors.ts            # 错误场景生成
│       ├── delay.ts             # 延迟模拟
│       ├── empty.ts             # 空数据处理
│       └── boundary.ts          # 边界测试
├── data/                        # Mock 数据
│   ├── index.ts                 # 数据统一导出
│   ├── user.ts                  # 用户数据
│   ├── pets.ts                  # 宠物数据
│   ├── services.ts              # 服务数据
│   ├── products.ts              # 商品数据
│   ├── merchants.ts             # 商家数据
│   ├── appointments.ts          # 预约数据
│   ├── orders.ts                # 订单数据
│   ├── reviews.ts               # 评价数据
│   ├── addresses.ts             # 地址数据
│   ├── notifications.ts         # 通知数据
│   ├── announcements.ts         # 公告数据
│   ├── stats.ts                 # 统计数据
│   └── other.ts                 # 其他数据
├── handlers/                    # Mock 处理器
│   ├── index.ts                 # 处理器统一导出
│   ├── user.ts                  # 用户相关处理器
│   ├── pets.ts                  # 宠物相关处理器
│   ├── services.ts              # 服务相关处理器
│   ├── products.ts              # 商品相关处理器
│   ├── merchants.ts             # 商家相关处理器
│   ├── appointments.ts          # 预约相关处理器
│   ├── orders.ts                # 订单相关处理器
│   ├── reviews.ts               # 评价相关处理器
│   ├── addresses.ts             # 地址相关处理器
│   ├── cart.ts                  # 购物车处理器
│   ├── favorites.ts             # 收藏处理器
│   ├── search.ts                # 搜索处理器
│   ├── stats.ts                 # 统计处理器
│   └── auth.ts                  # 认证处理器
├── utils/
│   └── generators.ts            # 数据生成工具
├── types.ts                     # 类型定义
└── index.ts                     # Mock 服务入口
```

### 核心模块说明

#### 1. 配置模块 (config/)

负责 Mock 服务的配置管理、数据管理和场景控制。

- **mock-config.ts**：定义 Mock 配置结构
- **data-manager.ts**：管理所有 Mock 数据，提供 CRUD 操作
- **scenario-controller.ts**：管理测试场景，支持场景切换和快照
- **dev-tools.ts**：提供浏览器控制台 API

#### 2. 数据模块 (data/)

存储和管理各类 Mock 数据，支持动态生成。

#### 3. 处理器模块 (handlers/)

拦截 HTTP 请求并返回 Mock 数据，每个模块对应一类业务功能。

#### 4. 工具模块 (utils/)

提供数据生成、格式化等工具函数。

---

## 快速开始

### 1. 初始化 Mock 服务

在应用入口文件（如 `main.ts`）中初始化：

```typescript
import { setupUserMock } from '@/mock/user'

// 初始化 Mock 服务
setupUserMock({
  enabled: import.meta.env.DEV,  // 仅在开发环境启用
  delay: 200,                     // 响应延迟 200ms
  debug: true                     // 开启调试日志
})
```

### 2. 初始化开发者工具

```typescript
import { initMockDevTools } from '@/mock/user/config'

// 初始化开发者工具
initMockDevTools({
  data: {
    enabled: true,
    debug: true,
    defaultDelay: 200
  },
  generator: {
    userCount: 10,
    merchantCount: 15,
    servicesPerMerchant: 3,
    productsPerMerchant: 5
  }
})
```

### 3. 在浏览器控制台使用

打开浏览器控制台，输入：

```javascript
// 查看帮助
mockDevTools.help()

// 查看调试信息
mockDevTools.debug()

// 快速设置场景
mockDevTools.quickSetup('slow')   // 慢速网络
mockDevTools.quickSetup('error')  // 服务器错误
mockDevTools.quickSetup('empty')  // 空数据
```

---

## API端点对照表

### 用户模块 (User)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getUserPets()` | `/api/user/pets` | GET | 获取用户宠物列表 |
| `getPetById(id)` | `/api/user/pets/:id` | GET | 获取宠物详情 |
| `addPet(data)` | `/api/user/pets` | POST | 添加宠物 |
| `updatePet(id, data)` | `/api/user/pets/:id` | PUT | 更新宠物信息 |
| `deletePet(id)` | `/api/user/pets/:id` | DELETE | 删除宠物 |

### 地址模块 (Address)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getAddresses()` | `/api/user/addresses` | GET | 获取地址列表 |
| `addAddress(data)` | `/api/user/addresses` | POST | 添加地址 |
| `updateAddress(id, data)` | `/api/user/addresses/:id` | PUT | 更新地址 |
| `deleteAddress(id)` | `/api/user/addresses/:id` | DELETE | 删除地址 |
| `setDefaultAddress(id)` | `/api/user/addresses/:id/default` | PUT | 设置默认地址 |

### 服务模块 (Service)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getServices(params)` | `/api/services` | GET | 获取服务列表 |
| `getServiceById(id)` | `/api/services/:id` | GET | 获取服务详情 |
| `getServicesByKeyword(keyword)` | `/api/services/search` | GET | 搜索服务 |
| `getRecommendedServices(limit)` | `/api/services/recommended` | GET | 获取推荐服务 |

### 商品模块 (Product)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getProducts(params)` | `/api/products` | GET | 获取商品列表 |
| `getProductById(id)` | `/api/products/:id` | GET | 获取商品详情 |
| `searchProducts(keyword)` | `/api/products/search` | GET | 搜索商品 |
| `getProductReviews(productId)` | `/api/products/:id/reviews` | GET | 获取商品评价 |

### 商家模块 (Merchant)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getMerchantList(params)` | `/api/merchants` | GET | 获取商家列表 |
| `getMerchantInfo(id)` | `/api/merchant/:id` | GET | 获取商家详情 |
| `getMerchantServices(id)` | `/api/merchant/:id/services` | GET | 获取商家服务 |
| `getMerchantProducts(id)` | `/api/merchant/:id/products` | GET | 获取商家商品 |
| `getMerchantReviews(id)` | `/api/merchant/:id/reviews` | GET | 获取商家评价 |
| `searchMerchants(keyword)` | `/api/merchants/search` | GET | 搜索商家 |
| `getAvailableSlots(merchantId, date)` | `/api/merchant/:id/available-slots` | GET | 获取可用预约时段 |

### 预约模块 (Appointment)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getUserAppointments()` | `/api/user/appointments` | GET | 获取用户预约列表 |
| `getAppointmentById(id)` | `/api/user/appointments/:id` | GET | 获取预约详情 |
| `createAppointment(data)` | `/api/user/appointments` | POST | 创建预约 |
| `cancelAppointment(id)` | `/api/user/appointments/:id/cancel` | PUT | 取消预约 |
| `getAppointmentStats()` | `/api/user/appointments/stats` | GET | 获取预约统计 |

### 订单模块 (Order)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getProductOrders()` | `/api/user/orders` | GET | 获取订单列表 |
| `getOrderById(id)` | `/api/user/orders/:id` | GET | 获取订单详情 |
| `createOrder(data)` | `/api/user/orders` | POST | 创建订单 |
| `payOrder(orderId, payMethod)` | `/api/user/orders/:id/pay` | POST | 支付订单 |
| `cancelOrder(id)` | `/api/user/orders/:id/cancel` | PUT | 取消订单 |
| `refundOrder(id, reason)` | `/api/user/orders/:id/refund` | POST | 申请退款 |
| `confirmReceiveOrder(id)` | `/api/user/orders/:id/confirm` | PUT | 确认收货 |
| `deleteOrder(id)` | `/api/user/orders/:id` | DELETE | 删除订单 |
| `batchCancelOrders(ids)` | `/api/user/orders/batch-cancel` | PUT | 批量取消订单 |
| `batchDeleteOrders(ids)` | `/api/user/orders/batch-delete` | DELETE | 批量删除订单 |
| `previewOrder(items)` | `/api/user/orders/preview` | POST | 预览订单 |
| `getPayStatus(orderId)` | `/api/user/orders/:id/pay/status` | GET | 获取支付状态 |

### 购物车模块 (Cart)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getCart()` | `/api/user/cart` | GET | 获取购物车 |
| `addToCart(data)` | `/api/user/cart` | POST | 添加到购物车 |
| `updateCartItem(productId, quantity)` | `/api/user/cart` | PUT | 更新购物车商品 |
| `removeFromCart(productId)` | `/api/user/cart/:productId` | DELETE | 从购物车移除 |
| `batchRemoveFromCart(productIds)` | `/api/user/cart/batch` | DELETE | 批量移除 |

### 收藏模块 (Favorite)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getFavorites()` | `/api/user/favorites` | GET | 获取收藏列表 |
| `addFavorite(merchantId)` | `/api/user/favorites` | POST | 添加收藏 |
| `removeFavorite(merchantId)` | `/api/user/favorites/:merchantId` | DELETE | 取消收藏 |
| `addServiceFavorite(serviceId)` | `/api/user/favorites/services` | POST | 收藏服务 |
| `getServiceFavorites()` | `/api/user/favorites/services` | GET | 获取服务收藏 |
| `removeServiceFavorite(serviceId)` | `/api/user/favorites/services/:serviceId` | DELETE | 取消服务收藏 |
| `addProductFavorite(productId)` | `/api/user/favorites/products` | POST | 收藏商品 |
| `removeProductFavorite(productId)` | `/api/user/favorites/products/:productId` | DELETE | 取消商品收藏 |
| `checkProductFavorite(productId)` | `/api/user/favorites/products/:productId/check` | GET | 检查商品收藏状态 |

### 评价模块 (Review)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getUserReviews(params)` | `/api/user/reviews` | GET | 获取用户评价列表 |
| `getUserReviewsList(params)` | `/api/user/reviews` | GET | 获取评价列表（分页） |
| `addReview(data)` | `/api/user/reviews` | POST | 添加评价 |
| `updateReview(id, data)` | `/api/user/reviews/:id` | PUT | 更新评价 |
| `deleteReview(id)` | `/api/user/reviews/:id` | DELETE | 删除评价 |

### 首页模块 (Home)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getHomeStats()` | `/api/user/home/stats` | GET | 获取首页统计 |
| `getRecentActivities(limit)` | `/api/user/home/activities` | GET | 获取最近活动 |

### 搜索模块 (Search)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `searchProducts(keyword)` | `/api/products/search` | GET | 搜索商品 |
| `searchServices(keyword)` | `/api/services/search` | GET | 搜索服务 |
| `searchMerchants(keyword)` | `/api/merchants/search` | GET | 搜索商家 |

### 用户服务模块 (User Services)

| 前端 API 调用 | Mock 端点 | HTTP 方法 | 描述 |
|--------------|----------|----------|------|
| `getUserPurchasedServices(params)` | `/api/user/services` | GET | 获取已购买服务 |

---

## 数据结构说明

### 基础类型定义

```typescript
// API 响应格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页响应格式
interface PaginatedResponse<T> {
  code: number
  message: string
  data: {
    data: T[]
    total: number
    page: number
    pageSize: number
  }
}
```

### 用户相关类型

```typescript
// 用户信息
interface MockUser {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  createdAt: string
  updatedAt: string
}

// 宠物信息
interface MockPet {
  id: number
  userId: number
  name: string
  type: string           // 宠物类型：猫、狗、兔子等
  breed: string          // 品种
  age: number
  gender: string         // 性别：male/female
  avatar: string
  description: string
  weight?: number        // 体重(kg)
  bodyType?: string      // 体型
  furColor?: string      // 毛色
  personality?: string   // 性格
  createdAt: string
  updatedAt: string
}

// 收货地址
interface MockAddress {
  id: number
  userId: number
  contactName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
  createdAt: string
  updatedAt: string
}
```

### 商家相关类型

```typescript
// 商家信息
interface MockMerchant {
  id: number
  name: string
  logo: string
  contactPerson: string
  phone: string
  email: string
  address: string
  description: string
  rating: number
  reviewCount: number
  serviceCount: number
  productCount: number
  status: 'pending' | 'approved' | 'rejected'
  createdAt: string
  updatedAt: string
}

// 服务信息
interface MockService {
  id: number
  merchantId: number
  merchantName: string
  name: string
  description: string
  price: number
  duration: number       // 时长(分钟)
  image: string
  category: string
  status: 'enabled' | 'disabled'
  rating: number
  reviewCount: number
  createdAt: string
  updatedAt: string
}

// 商品信息
interface MockProduct {
  id: number
  merchantId: number
  merchantName: string
  name: string
  description: string
  price: number
  stock: number
  image: string
  category: string
  status: 'enabled' | 'disabled'
  sales: number
  rating: number
  reviewCount: number
  createdAt: string
  updatedAt: string
}
```

### 订单相关类型

```typescript
// 预约信息
interface MockAppointment {
  id: number
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  petId: number
  petName: string
  appointmentTime: string
  totalPrice: number
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  notes: string
  createdAt: string
  updatedAt: string
}

// 商品订单
interface MockProductOrder {
  id: number
  orderNo: string
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  totalPrice: number
  freight: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  payMethod: string
  payTime: string | null
  shipTime: string | null
  completeTime: string | null
  cancelTime: string | null
  remark: string
  shippingAddress: MockOrderAddress
  items: MockOrderItem[]
  createdAt: string
  updatedAt: string
}

// 订单商品项
interface MockOrderItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  subtotal: number
}

// 订单地址
interface MockOrderAddress {
  name: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
}
```

### 其他类型

```typescript
// 购物车项
interface MockCartItem {
  id: number
  userId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  merchantId: number
  merchantName: string
  selected: boolean
  createdAt: string
}

// 收藏
interface MockFavorite {
  id: number
  userId: number
  merchantId: number
  merchantName: string
  merchantLogo: string
  merchantAddress: string
  merchantPhone: string
  merchantRating: number
  createdAt: string
}

// 评价
interface MockReview {
  id: number
  userId: number
  userName: string
  userAvatar: string
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  appointmentId: number
  rating: number
  comment: string
  images: string[]
  reply: string | null
  replyTime: string | null
  createdAt: string
}

// 通知
interface MockNotification {
  id: number
  userId: number
  title: string
  content: string
  type: 'system' | 'order' | 'appointment' | 'review'
  isRead: boolean
  createdAt: string
}

// 公告
interface MockAnnouncement {
  id: number
  title: string
  content: string
  summary: string
  author: string
  status: 'published' | 'draft'
  isRead: boolean
  publishedAt: string
  createdAt: string
}

// 首页统计
interface MockDashboardStats {
  petCount: number
  pendingAppointmentCount: number
  reviewCount: number
  favoriteCount: number
}

// 最近活动
interface MockRecentActivity {
  id: number
  type: 'appointment' | 'order' | 'review'
  title: string
  description: string
  time: string
}
```

---

## 场景切换使用方法

### 预设场景列表

| 场景 ID | 名称 | 描述 |
|---------|------|------|
| `normal` | 正常模式 | 所有功能正常运行 |
| `slow-network` | 慢速网络 | 所有请求延迟 2-3 秒 |
| `error-500` | 服务器错误 | 所有请求返回 500 错误 |
| `unauthorized` | 未授权模式 | 返回 401 错误 |
| `empty-data` | 空数据模式 | 所有列表返回空数据 |
| `timeout` | 超时模式 | 所有请求超时 |
| `mixed-scenarios` | 混合场景 | 不同资源不同状态 |
| `stress-test` | 压力测试 | 慢速响应 |
| `network-unstable` | 网络不稳定 | 随机延迟 |
| `service-unavailable` | 服务不可用 | 返回 503 错误 |

### 应用预设场景

```typescript
import { applyProfile, getProfiles } from '@/mock/user/config'

// 查看所有预设场景
const profiles = getProfiles()
console.log(profiles)

// 应用预设场景
applyProfile('slow-network')   // 慢速网络
applyProfile('error-500')      // 服务器错误
applyProfile('empty-data')     // 空数据
applyProfile('normal')         // 恢复正常
```

### 自定义场景设置

```typescript
import { setScenario, clearScenario, resetScenarios } from '@/mock/user/config'

// 设置错误场景
setScenario('service', 'list', 'error', 'internal_error')
setScenario('order', 'detail', 'error', 'not_found')

// 设置延迟场景
setScenario('product', 'list', 'delay', 'slow')
setScenario('merchant', 'detail', 'delay', 'very_slow')

// 设置空数据场景
setScenario('review', 'list', 'empty', 'empty_list')

// 清除特定场景
clearScenario('service', 'list', 'error')

// 清除资源的所有场景
clearScenario('service', 'list')

// 重置所有场景
resetScenarios()
```

### 错误场景类型

| 场景 | HTTP 状态码 | 描述 |
|------|-------------|------|
| `bad_request` | 400 | 请求参数错误 |
| `unauthorized` | 401 | 未授权访问 |
| `forbidden` | 403 | 禁止访问 |
| `not_found` | 404 | 资源不存在 |
| `internal_error` | 500 | 服务器内部错误 |
| `bad_gateway` | 502 | 网关错误 |
| `service_unavailable` | 503 | 服务不可用 |
| `gateway_timeout` | 504 | 网关超时 |

### 延迟场景类型

| 场景 | 延迟时间 | 描述 |
|------|----------|------|
| `none` | 0ms | 无延迟 |
| `slow` | 1-2秒 | 慢速响应 |
| `very_slow` | 3-5秒 | 非常慢 |
| `random` | 100ms-3秒 | 随机延迟 |
| `timeout` | 30秒+ | 超时 |

### 资源类型

```typescript
type ResourceType =
  | 'user'        // 用户
  | 'pet'         // 宠物
  | 'service'     // 服务
  | 'product'     // 商品
  | 'merchant'    // 商家
  | 'appointment' // 预约
  | 'order'       // 订单
  | 'review'      // 评价
  | 'cart'        // 购物车
  | 'favorite'    // 收藏
  | 'notification'// 通知
  | 'address'     // 地址
```

### 操作类型

```typescript
type ActionType =
  | 'list'     // 列表查询
  | 'detail'   // 详情查询
  | 'create'   // 创建
  | 'update'   // 更新
  | 'delete'   // 删除
  | 'search'   // 搜索
```

### 场景快照管理

```typescript
import { createSnapshot, restoreSnapshot, getSnapshots } from '@/mock/user/config'

// 创建快照
const snapshotId = createSnapshot('测试场景快照')
console.log('快照ID:', snapshotId)

// 查看所有快照
const snapshots = getSnapshots()
console.log(snapshots)

// 恢复快照
restoreSnapshot(snapshotId)

// 删除快照（通过 dev-tools）
mockDevTools.scenarios.snapshots.delete(snapshotId)
```

---

## 数据自定义方法

### 获取数据

```typescript
import { getData, getResource, getItem } from '@/mock/user/config'

// 获取所有数据
const allData = getData()

// 获取特定资源
const users = getResource('users')
const services = getResource('services')
const products = getResource('products')
const merchants = getResource('merchants')
const appointments = getResource('appointments')
const orders = getResource('orders')

// 获取特定项目
const user = getItem('users', 1)
const service = getItem('services', 1)
```

### 修改数据

```typescript
import { setItem, addItem, removeItem } from '@/mock/user/config'

// 更新项目
setItem('users', 1, { username: '新用户名' })
setItem('services', 1, { price: 99.99 })
setItem('products', 1, { stock: 100 })

// 添加项目
addItem('users', {
  id: 999,
  username: '测试用户',
  email: 'test@example.com',
  phone: '13800138000',
  avatar: '',
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
})

// 删除项目
removeItem('users', 1)
removeItem('services', 1)
```

### 查询数据

```typescript
import { queryData } from '@/mock/user/config'

// 分页查询
const result = queryData('users', {
  page: 1,
  pageSize: 10
})

// 带筛选条件
const filtered = queryData('services', {
  page: 1,
  pageSize: 10,
  filters: {
    category: '宠物美容',
    status: 'enabled'
  }
})

// 带排序
const sorted = queryData('products', {
  page: 1,
  pageSize: 10,
  sort: { field: 'price', order: 'desc' }
})
```

### 重新生成数据

```typescript
import { regenerateData, resetData, getDataStatistics } from '@/mock/user/config'

// 使用新配置重新生成
regenerateData({
  userCount: 20,
  merchantCount: 30,
  servicesPerMerchant: 5,
  productsPerMerchant: 10,
  petsPerUser: 3,
  appointmentsPerUser: 5,
  ordersPerUser: 5,
  reviewsPerUser: 3
})

// 重置数据到默认状态
resetData()

// 获取数据统计
const stats = getDataStatistics()
console.log(stats)
```

### 使用数据管理器创建数据

```typescript
import { mockDataManager } from '@/mock/user/config'

// 创建用户
const user = mockDataManager.createUser({
  username: '测试用户',
  email: 'test@example.com'
})

// 创建宠物
const pet = mockDataManager.createPet(user.id, {
  name: '小黄',
  type: '狗',
  breed: '金毛'
})

// 创建预约
const appointment = mockDataManager.createAppointment(
  user.id,
  pet.id,
  1, // merchantId
  1, // serviceId
  { appointmentTime: '2024-12-25 10:00:00' }
)

// 创建订单
const order = mockDataManager.createOrder(
  user.id,
  1, // merchantId
  [{ productId: 1, quantity: 2 }],
  { remark: '测试订单' }
)
```

---

## 开发者工具使用

### 浏览器控制台 API

在浏览器控制台中，可以通过全局变量 `mockDevTools` 访问所有功能。

#### 数据操作

```javascript
// 获取所有数据
mockDevTools.data.get()

// 获取特定资源
mockDevTools.data.getResource('users')
mockDevTools.data.getResource('services')
mockDevTools.data.getResource('products')

// 获取单个项目
mockDevTools.data.getItem('users', 1)

// 更新项目
mockDevTools.data.set('users', 1, { username: '新名字' })

// 添加项目
mockDevTools.data.add('users', {
  id: 999,
  username: '新用户',
  email: 'new@example.com',
  phone: '13800138000',
  avatar: '',
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
})

// 删除项目
mockDevTools.data.remove('users', 1)

// 查询数据
mockDevTools.data.query('users', {
  page: 1,
  pageSize: 10,
  filters: { status: 'active' }
})

// 重置数据
mockDevTools.data.reset()

// 重新生成数据
mockDevTools.data.regenerate({
  userCount: 20,
  merchantCount: 30
})

// 获取统计信息
mockDevTools.data.stats()
```

#### 场景操作

```javascript
// 列出所有预设场景
mockDevTools.scenarios.profiles.list()

// 应用预设场景
mockDevTools.scenarios.profiles.apply('slow-network')
mockDevTools.scenarios.profiles.apply('error-500')

// 获取预设详情
mockDevTools.scenarios.profiles.get('slow-network')

// 设置自定义场景
mockDevTools.scenarios.set('service', 'list', 'error', 'internal_error')
mockDevTools.scenarios.set('product', 'list', 'delay', 'slow')
mockDevTools.scenarios.set('order', 'list', 'empty', 'empty_list')

// 清除场景
mockDevTools.scenarios.clear('service', 'list', 'error')
mockDevTools.scenarios.reset()

// 获取当前场景状态
mockDevTools.scenarios.state()

// 快照管理
mockDevTools.scenarios.snapshots.create('测试快照')
mockDevTools.scenarios.snapshots.list()
mockDevTools.scenarios.snapshots.restore('snapshot-id')
mockDevTools.scenarios.snapshots.delete('snapshot-id')
```

#### 配置操作

```javascript
// 获取当前配置
mockDevTools.config.get()

// 设置配置
mockDevTools.config.set({
  data: { enabled: true, debug: true },
  generator: { userCount: 20 }
})

// 重置配置
mockDevTools.config.reset()

// 导出配置
const configJson = mockDevTools.config.export()

// 导入配置
mockDevTools.config.import(configJson)
```

#### 工具函数

```javascript
// 日志输出
mockDevTools.utils.log('测试消息', { data: 'test' })

// 表格显示
mockDevTools.utils.table(mockDevTools.data.getResource('users'))

// 计时
mockDevTools.utils.time('测试')
// ... 一些操作
mockDevTools.utils.timeEnd('测试')

// 性能分析
mockDevTools.utils.profile('测试性能', () => {
  // 执行一些操作
  mockDevTools.data.get()
})
```

#### 快速设置

```javascript
// 正常模式
mockDevTools.quickSetup('normal')

// 慢速网络
mockDevTools.quickSetup('slow')

// 服务器错误
mockDevTools.quickSetup('error')

// 空数据
mockDevTools.quickSetup('empty')

// 超时
mockDevTools.quickSetup('timeout')

// 混合场景
mockDevTools.quickSetup('mixed')
```

#### 调试和帮助

```javascript
// 显示调试信息
mockDevTools.debug()

// 显示帮助
mockDevTools.help()

// 快速启用错误
mockDevTools.enableError('service', 'list', 'internal_error')

// 快速启用延迟
mockDevTools.enableDelay('product', 'list', 'slow')

// 快速启用空数据
mockDevTools.enableEmpty('review', 'list')

// 创建测试数据
mockDevTools.createTestData({
  users: 20,
  merchants: 30,
  services: 100,
  products: 150,
  appointments: 50,
  orders: 50
})
```

---

## 常见问题解答

### 1. Mock 服务不生效怎么办？

**问题**：配置了 Mock 服务但请求没有被拦截。

**解决方案**：

```typescript
// 确保在 main.ts 中正确初始化
import { setupUserMock } from '@/mock/user'

setupUserMock({
  enabled: import.meta.env.DEV,  // 确保在开发环境
  debug: true                    // 开启调试日志
})

// 检查是否初始化成功
console.log('[Mock] Status:', getMockStatus())
```

### 2. 如何在测试中使用 Mock？

**问题**：在单元测试或 E2E 测试中如何使用 Mock 服务。

**解决方案**：

```typescript
// 在测试文件中
import { setupUserMock, resetUserMock } from '@/mock/user'
import { resetData, resetScenarios } from '@/mock/user/config'

describe('MyComponent', () => {
  beforeAll(() => {
    setupUserMock({ enabled: true, debug: false })
  })

  beforeEach(() => {
    resetData()
    resetScenarios()
  })

  afterAll(() => {
    resetUserMock()
  })

  it('should work with mock data', () => {
    // 测试代码
  })
})
```

### 3. 如何模拟特定的错误场景？

**问题**：需要测试特定的错误处理逻辑。

**解决方案**：

```typescript
import { setScenario, clearScenario } from '@/mock/user/config'

// 测试 404 错误
setScenario('service', 'detail', 'error', 'not_found')

// 测试 500 错误
setScenario('order', 'list', 'error', 'internal_error')

// 测试未授权
setScenario('user', 'detail', 'error', 'unauthorized')

// 测试完成后清除
clearScenario('service', 'detail', 'error')
```

### 4. 如何自定义 Mock 数据？

**问题**：需要使用特定的测试数据。

**解决方案**：

```typescript
import { setItem, addItem, regenerateData } from '@/mock/user/config'

// 方式1：修改现有数据
setItem('services', 1, {
  name: '测试服务',
  price: 99.99
})

// 方式2：添加新数据
addItem('services', {
  id: 999,
  name: '自定义服务',
  description: '这是一个自定义服务',
  price: 199.99,
  duration: 60,
  merchantId: 1,
  merchantName: '测试商家',
  category: '宠物美容',
  status: 'enabled',
  rating: 4.8,
  reviewCount: 100,
  image: '',
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
})

// 方式3：重新生成数据
regenerateData({
  userCount: 5,
  merchantCount: 10,
  servicesPerMerchant: 3
})
```

### 5. 如何测试慢速网络？

**问题**：需要测试加载状态和慢速网络情况。

**解决方案**：

```typescript
import { applyProfile, setScenario } from '@/mock/user/config'

// 方式1：应用预设场景
applyProfile('slow-network')

// 方式2：针对特定资源
setScenario('service', 'list', 'delay', 'slow')
setScenario('product', 'list', 'delay', 'very_slow')

// 方式3：使用浏览器控制台
mockDevTools.quickSetup('slow')
```

### 6. 如何测试空数据状态？

**问题**：需要测试空列表、无数据等状态。

**解决方案**：

```typescript
import { applyProfile, setScenario } from '@/mock/user/config'

// 方式1：应用预设场景
applyProfile('empty-data')

// 方式2：针对特定资源
setScenario('order', 'list', 'empty', 'empty_list')
setScenario('review', 'list', 'empty', 'empty_list')

// 方式3：使用浏览器控制台
mockDevTools.quickSetup('empty')
```

### 7. 如何保存和恢复测试场景？

**问题**：配置了复杂的测试场景，希望保存以便后续使用。

**解决方案**：

```typescript
import { createSnapshot, restoreSnapshot, getSnapshots } from '@/mock/user/config'

// 配置复杂场景
setScenario('service', 'list', 'delay', 'slow')
setScenario('product', 'list', 'empty', 'empty_list')
setScenario('order', 'detail', 'error', 'not_found')

// 保存快照
const snapshotId = createSnapshot('复杂测试场景')
console.log('快照已保存:', snapshotId)

// 查看所有快照
const snapshots = getSnapshots()
console.log(snapshots)

// 恢复快照
restoreSnapshot(snapshotId)
```

### 8. Mock 数据会被持久化吗？

**问题**：修改的 Mock 数据是否会保存到本地。

**答案**：不会。Mock 数据存储在内存中，刷新页面后会重置。如果需要持久化，可以：

```typescript
// 导出配置和数据
const configJson = mockDevTools.config.export()
localStorage.setItem('mockConfig', configJson)

// 导入配置和数据
const savedConfig = localStorage.getItem('mockConfig')
if (savedConfig) {
  mockDevTools.config.import(savedConfig)
}
```

### 9. 如何禁用特定模块的 Mock？

**问题**：某些模块需要使用真实 API。

**解决方案**：

```typescript
import { enableModule, setupUserMock } from '@/mock/user'

// 初始化时不加载所有模块
setupUserMock({ enabled: false })

// 只启用需要的模块
enableModule('user')
enableModule('service')
enableModule('product')

// 不启用 'order' 模块，使用真实 API
```

### 10. 如何查看 Mock 请求日志？

**问题**：需要查看 Mock 请求的详细信息。

**解决方案**：

```typescript
// 初始化时开启 debug 模式
setupUserMock({
  enabled: true,
  debug: true  // 开启调试日志
})

// 或在浏览器控制台查看
mockDevTools.debug()
```

---

## 最佳实践建议

### 1. 开发环境初始化

在应用入口处初始化 Mock 服务，确保在开发环境下自动启用：

```typescript
// main.ts
import { setupUserMock } from '@/mock/user'
import { initMockDevTools } from '@/mock/user/config'

// 初始化 Mock 服务
setupUserMock({
  enabled: import.meta.env.DEV,
  delay: 200,
  debug: true
})

// 初始化开发者工具
if (import.meta.env.DEV) {
  initMockDevTools({
    data: {
      enabled: true,
      debug: true,
      defaultDelay: 200
    },
    generator: {
      userCount: 10,
      merchantCount: 15,
      servicesPerMerchant: 3,
      productsPerMerchant: 5
    }
  })
}
```

### 2. 场景隔离

每个测试用例前后重置场景状态，避免测试间相互影响：

```typescript
describe('ServiceList', () => {
  beforeEach(() => {
    resetData()
    resetScenarios()
  })

  afterEach(() => {
    resetScenarios()
  })

  it('should handle slow network', () => {
    setScenario('service', 'list', 'delay', 'slow')
    // 测试代码
  })

  it('should handle empty data', () => {
    setScenario('service', 'list', 'empty', 'empty_list')
    // 测试代码
  })
})
```

### 3. 快照管理

复杂场景使用快照保存和恢复，提高测试效率：

```typescript
// setup-complex-scenario.ts
import { setScenario, createSnapshot } from '@/mock/user/config'

export function setupComplexScenario() {
  setScenario('service', 'list', 'delay', 'slow')
  setScenario('product', 'list', 'empty', 'empty_list')
  setScenario('order', 'detail', 'error', 'not_found')
  setScenario('merchant', 'list', 'delay', 'very_slow')

  return createSnapshot('complex-scenario')
}

// test.ts
import { restoreSnapshot } from '@/mock/user/config'
import { setupComplexScenario } from './setup-complex-scenario'

describe('Complex Scenario Tests', () => {
  let snapshotId: string

  beforeAll(() => {
    snapshotId = setupComplexScenario()
  })

  afterAll(() => {
    restoreSnapshot(snapshotId)
  })

  // 测试代码
})
```

### 4. 数据定制

根据测试需求生成合适数量的测试数据：

```typescript
// 小数据集：快速测试
regenerateData({
  userCount: 5,
  merchantCount: 5,
  servicesPerMerchant: 2,
  productsPerMerchant: 3
})

// 大数据集：性能测试
regenerateData({
  userCount: 100,
  merchantCount: 50,
  servicesPerMerchant: 10,
  productsPerMerchant: 20
})
```

### 5. 调试输出

开启 debug 模式查看详细日志，帮助排查问题：

```typescript
// 开发环境开启
setupUserMock({
  enabled: true,
  debug: true
})

// 生产环境关闭
setupUserMock({
  enabled: false,
  debug: false
})
```

### 6. 类型安全

使用 TypeScript 类型定义，确保数据结构正确：

```typescript
import type { MockService, MockProduct, MockAppointment } from '@/mock/user/types'

// 创建服务时使用类型
const service: Partial<MockService> = {
  name: '测试服务',
  price: 99.99,
  duration: 60
}

addItem('services', service as MockService)
```

### 7. 错误处理测试

全面测试各种错误场景，确保前端错误处理健壮：

```typescript
// 测试各种错误
const errorScenarios = [
  { type: 'bad_request', code: 400 },
  { type: 'unauthorized', code: 401 },
  { type: 'forbidden', code: 403 },
  { type: 'not_found', code: 404 },
  { type: 'internal_error', code: 500 }
]

errorScenarios.forEach(({ type }) => {
  it(`should handle ${type} error`, () => {
    setScenario('service', 'list', 'error', type)
    // 测试代码
    clearScenario('service', 'list', 'error')
  })
})
```

### 8. 性能测试

使用延迟场景测试加载状态和性能：

```typescript
// 测试加载状态
setScenario('service', 'list', 'delay', 'slow')

// 测试超时处理
setScenario('service', 'list', 'delay', 'timeout')

// 测试网络不稳定
applyProfile('network-unstable')
```

### 9. 集成测试

在集成测试中结合多个场景：

```typescript
describe('Integration Test', () => {
  beforeAll(() => {
    // 设置混合场景
    setScenario('service', 'list', 'delay', 'slow')
    setScenario('product', 'list', 'empty', 'empty_list')
    setScenario('order', 'detail', 'error', 'not_found')
  })

  it('should handle mixed scenarios', async () => {
    // 测试服务列表加载
    // 测试商品列表空状态
    // 测试订单详情错误
  })
})
```

### 10. 文档和注释

为自定义场景和数据添加注释，方便团队协作：

```typescript
/**
 * 测试场景：慢速网络下的服务列表加载
 * 用途：测试加载状态和用户体验
 * 影响：服务列表请求延迟 2-3 秒
 */
export function setupSlowServiceList() {
  setScenario('service', 'list', 'delay', 'slow')
  return createSnapshot('slow-service-list')
}
```

---

## 附录

### 完整 API 速查表

#### 数据操作

| 方法 | 描述 |
|------|------|
| `getData()` | 获取所有数据 |
| `getResource(resource)` | 获取特定资源 |
| `getItem(resource, id)` | 获取单个项目 |
| `setItem(resource, id, updates)` | 更新项目 |
| `addItem(resource, item)` | 添加项目 |
| `removeItem(resource, id)` | 删除项目 |
| `queryData(resource, options)` | 查询数据 |
| `resetData()` | 重置数据 |
| `regenerateData(config)` | 重新生成数据 |
| `getDataStatistics()` | 获取统计信息 |

#### 场景操作

| 方法 | 描述 |
|------|------|
| `getProfiles()` | 获取所有预设场景 |
| `applyProfile(id)` | 应用预设场景 |
| `setScenario(resource, action, type, scenario)` | 设置场景 |
| `clearScenario(resource, action, type)` | 清除场景 |
| `resetScenarios()` | 重置所有场景 |
| `getScenarioState()` | 获取当前状态 |
| `createSnapshot(name)` | 创建快照 |
| `restoreSnapshot(id)` | 恢复快照 |
| `getSnapshots()` | 获取所有快照 |

#### Mock 服务控制

| 方法 | 描述 |
|------|------|
| `setupUserMock(config)` | 初始化 Mock 服务 |
| `resetUserMock()` | 重置 Mock 服务 |
| `enableModule(moduleName)` | 启用特定模块 |
| `getMockStatus()` | 获取 Mock 状态 |

---

## 更新日志

### v1.0.0 (2024-01-01)

- 初始版本发布
- 完整的用户端 API Mock
- 场景切换功能
- 开发者工具集成
- 快照管理功能

---

## 联系方式

如有问题或建议，请联系开发团队。

---

**文档版本**: v1.0.0  
**最后更新**: 2024-01-01  
**维护者**: 开发团队

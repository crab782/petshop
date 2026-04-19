# 用户端 Mock 数据管理系统

## 概述

本系统提供了一个完整的 Mock 数据管理解决方案，支持数据自定义、场景切换、状态快照等功能，方便开发测试。

## 目录结构

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
│   └── *.ts                     # 各类数据定义
├── handlers/                    # Mock 处理器
│   └── *.ts                     # 各模块处理器
├── utils/
│   └── generators.ts            # 数据生成工具
└── index.ts                     # Mock 服务入口
```

## 快速开始

### 1. 初始化

```typescript
import { initMockDevTools } from '@/mock/user/config'

// 在应用启动时初始化
initMockDevTools({
  data: {
    enabled: true,
    debug: true,
    defaultDelay: 200
  },
  generator: {
    userCount: 10,
    merchantCount: 15
  }
})
```

### 2. 使用开发者工具

在浏览器控制台中，可以通过全局变量访问：

```javascript
// 获取所有 API
mockDevTools.help()

// 快速设置场景
mockDevTools.quickSetup('slow')  // 慢速网络
mockDevTools.quickSetup('error') // 服务器错误
mockDevTools.quickSetup('empty') // 空数据

// 查看调试信息
mockDevTools.debug()
```

## 核心功能

### 一、数据管理

#### 获取数据

```typescript
import { getData, getResource, getItem } from '@/mock/user/config'

// 获取所有数据
const allData = getData()

// 获取特定资源
const users = getResource('users')
const services = getResource('services')
const products = getResource('products')

// 获取特定项目
const user = getItem('users', 1)
```

#### 修改数据

```typescript
import { setItem, addItem, removeItem } from '@/mock/user/config'

// 更新项目
setItem('users', 1, { username: '新用户名' })

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
```

#### 查询数据

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

#### 重新生成数据

```typescript
import { regenerateData, resetData } from '@/mock/user/config'

// 使用新配置重新生成
regenerateData({
  userCount: 20,
  merchantCount: 30,
  servicesPerMerchant: 5,
  productsPerMerchant: 10
})

// 重置数据
resetData()
```

### 二、场景管理

#### 预设场景

| 场景 ID | 名称 | 描述 |
|---------|------|------|
| normal | 正常模式 | 所有功能正常运行 |
| slow-network | 慢速网络 | 所有请求延迟 2-3 秒 |
| error-500 | 服务器错误 | 所有请求返回 500 错误 |
| unauthorized | 未授权模式 | 返回 401 错误 |
| empty-data | 空数据模式 | 所有列表返回空数据 |
| timeout | 超时模式 | 所有请求超时 |
| mixed-scenarios | 混合场景 | 不同资源不同状态 |
| stress-test | 压力测试 | 慢速响应 |
| network-unstable | 网络不稳定 | 随机延迟 |
| service-unavailable | 服务不可用 | 返回 503 错误 |

#### 应用场景

```typescript
import { applyProfile, getProfiles } from '@/mock/user/config'

// 查看所有预设
const profiles = getProfiles()

// 应用预设场景
applyProfile('slow-network')
applyProfile('error-500')
applyProfile('empty-data')
```

#### 自定义场景

```typescript
import { setScenario, clearScenario } from '@/mock/user/config'
import type { ResourceType } from '@/mock/user/config'

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

#### 场景快照

```typescript
import { createSnapshot, restoreSnapshot, getSnapshots } from '@/mock/user/config'

// 创建快照
const snapshotId = createSnapshot('测试场景快照')

// 查看所有快照
const snapshots = getSnapshots()

// 恢复快照
restoreSnapshot(snapshotId)
```

### 三、错误场景类型

| 场景 | HTTP 状态码 | 描述 |
|------|-------------|------|
| bad_request | 400 | 请求参数错误 |
| unauthorized | 401 | 未授权访问 |
| forbidden | 403 | 禁止访问 |
| not_found | 404 | 资源不存在 |
| internal_error | 500 | 服务器内部错误 |
| bad_gateway | 502 | 网关错误 |
| service_unavailable | 503 | 服务不可用 |
| gateway_timeout | 504 | 网关超时 |

### 四、延迟场景类型

| 场景 | 延迟时间 | 描述 |
|------|----------|------|
| none | 0ms | 无延迟 |
| slow | 1-2秒 | 慢速响应 |
| very_slow | 3-5秒 | 非常慢 |
| random | 100ms-3秒 | 随机延迟 |
| timeout | 30秒+ | 超时 |

### 五、资源类型

```typescript
type ResourceType =
  | 'user'       // 用户
  | 'pet'        // 宠物
  | 'service'    // 服务
  | 'product'    // 商品
  | 'merchant'   // 商家
  | 'appointment'// 预约
  | 'order'      // 订单
  | 'review'     // 评价
  | 'cart'       // 购物车
  | 'favorite'   // 收藏
  | 'notification'// 通知
  | 'address'    // 地址
```

### 六、操作类型

```typescript
type ActionType =
  | 'list'     // 列表查询
  | 'detail'   // 详情查询
  | 'create'   // 创建
  | 'update'   // 更新
  | 'delete'   // 删除
  | 'search'   // 搜索
```

## 完整 API 参考

### mockDevTools API

```typescript
// 数据操作
mockDevTools.data.get()                              // 获取所有数据
mockDevTools.data.getResource(resource)              // 获取资源数据
mockDevTools.data.getItem(resource, id)              // 获取单个项目
mockDevTools.data.set(resource, id, updates)         // 更新项目
mockDevTools.data.add(resource, item)                // 添加项目
mockDevTools.data.remove(resource, id)               // 删除项目
mockDevTools.data.query(resource, options)           // 查询数据
mockDevTools.data.reset()                            // 重置数据
mockDevTools.data.regenerate(config)                 // 重新生成数据
mockDevTools.data.stats()                            // 获取统计信息

// 场景操作
mockDevTools.scenarios.profiles.list()               // 列出所有预设
mockDevTools.scenarios.profiles.apply(id)            // 应用预设
mockDevTools.scenarios.profiles.get(id)              // 获取预设详情
mockDevTools.scenarios.set(resource, action, type, scenario)  // 设置场景
mockDevTools.scenarios.clear(resource, action, type) // 清除场景
mockDevTools.scenarios.reset()                       // 重置所有场景
mockDevTools.scenarios.state()                       // 获取当前状态
mockDevTools.scenarios.snapshots.create(name)        // 创建快照
mockDevTools.scenarios.snapshots.restore(id)         // 恢复快照
mockDevTools.scenarios.snapshots.list()              // 列出快照
mockDevTools.scenarios.snapshots.delete(id)          // 删除快照

// 配置操作
mockDevTools.config.get()                            // 获取配置
mockDevTools.config.set(config)                      // 设置配置
mockDevTools.config.reset()                          // 重置配置
mockDevTools.config.export()                         // 导出配置
mockDevTools.config.import(json)                     // 导入配置

// 工具函数
mockDevTools.utils.log(message, data)                // 日志输出
mockDevTools.utils.table(data)                       // 表格显示
mockDevTools.utils.time(label)                       // 开始计时
mockDevTools.utils.timeEnd(label)                    // 结束计时
mockDevTools.utils.profile(name, fn)                 // 性能分析
```

## 使用示例

### 示例 1：测试慢速网络

```typescript
// 应用慢速网络场景
applyProfile('slow-network')

// 或针对特定资源
setScenario('service', 'list', 'delay', 'slow')
setScenario('product', 'list', 'delay', 'slow')
```

### 示例 2：测试空数据状态

```typescript
// 应用空数据场景
applyProfile('empty-data')

// 或针对特定资源
setScenario('order', 'list', 'empty', 'empty_list')
```

### 示例 3：测试错误处理

```typescript
// 测试 404 错误
setScenario('service', 'detail', 'error', 'not_found')

// 测试 500 错误
setScenario('order', 'list', 'error', 'internal_error')

// 测试未授权
setScenario('user', 'detail', 'error', 'unauthorized')
```

### 示例 4：创建测试数据

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

### 示例 5：场景快照管理

```typescript
// 创建复杂场景
setScenario('service', 'list', 'delay', 'slow')
setScenario('product', 'list', 'empty', 'empty_list')
setScenario('order', 'detail', 'error', 'not_found')

// 保存快照
const snapshotId = createSnapshot('复杂测试场景')

// 重置后恢复
resetScenarios()
restoreSnapshot(snapshotId)
```

## 最佳实践

1. **开发环境初始化**：在应用入口处初始化 Mock 服务
2. **场景隔离**：每个测试用例前后重置场景状态
3. **快照管理**：复杂场景使用快照保存和恢复
4. **数据定制**：根据测试需求生成合适数量的测试数据
5. **调试输出**：开启 debug 模式查看详细日志

## 注意事项

1. Mock 服务仅在开发环境生效
2. 场景设置会影响所有后续请求
3. 数据修改不会持久化，刷新页面后重置
4. 建议在测试完成后重置场景状态

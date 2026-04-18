# 平台端Mock模拟服务 - 产品需求文档

## 概述
- **摘要**：为平台端的20个页面开发基于mock.js的模拟后端服务和测试数据，确保前端开发可以在后端接口未完成的情况下进行独立开发和测试。
- **目的**：使用mock.js模拟真实API接口，提供结构完整、数据类型准确的模拟数据，覆盖正常、边界和异常等多种场景。
- **目标用户**：前端开发人员、测试人员

## 目标
- 为每个平台端页面设计符合实际业务逻辑的API接口
- 使用mock.js语法生成结构完整、数据类型准确的模拟数据
- 确保模拟接口能够正确响应GET、POST、PUT、DELETE等常用HTTP请求方法
- 为每个页面创建独立的mock配置文件
- 模拟数据应覆盖正常、边界和异常等多种场景
- 保证数据的一致性和可扩展性
- 进行接口测试，验证模拟数据符合前端页面的渲染需求

## 非目标（范围外）
- 不修改真实的后端API接口
- 不涉及数据库操作
- 不实现业务逻辑验证（仅模拟数据）

## 平台端20个页面对应的Mock接口

| 序号 | 页面 | API接口 |
|------|------|---------|
| 1 | AdminLayout.vue | 无需API |
| 2 | admin-dashboard/index.vue | GET /api/admin/dashboard/stats |
| 3 | admin-users/index.vue | GET /api/admin/users, POST /api/admin/users/batch, DELETE /api/admin/users/:id |
| 4 | user-detail/index.vue | GET /api/admin/users/:id, PUT /api/admin/users/:id |
| 5 | admin-merchants/index.vue | GET /api/admin/merchants, DELETE /api/admin/merchants/:id |
| 6 | merchant-detail/index.vue | GET /api/admin/merchants/:id, PUT /api/admin/merchants/:id |
| 7 | merchant-audit/index.vue | GET /api/admin/merchants/pending, POST /api/admin/merchants/:id/audit |
| 8 | admin-services/index.vue | GET /api/admin/services, DELETE /api/admin/services/:id |
| 9 | admin-products/index.vue | GET /api/admin/products, DELETE /api/admin/products/:id |
| 10 | product-manage/index.vue | GET /api/admin/products/:id, PUT /api/admin/products/:id |
| 11 | admin-reviews/index.vue | GET /api/admin/reviews, DELETE /api/admin/reviews/:id |
| 12 | review-audit/index.vue | GET /api/admin/reviews/pending, POST /api/admin/reviews/:id/audit |
| 13 | admin-announcements/index.vue | GET /api/admin/announcements, POST /api/admin/announcements, DELETE /api/admin/announcements/:id |
| 14 | announcement-edit/index.vue | GET /api/admin/announcements/:id, POST /api/admin/announcements, PUT /api/admin/announcements/:id |
| 15 | admin-system/index.vue | GET /api/admin/system/settings, PUT /api/admin/system/settings |
| 16 | roles/index.vue | GET /api/admin/roles, POST /api/admin/roles, PUT /api/admin/roles/:id, DELETE /api/admin/roles/:id |
| 17 | logs/index.vue | GET /api/admin/operation-logs, DELETE /api/admin/operation-logs/:id |
| 18 | admin-activities/index.vue | GET /api/admin/activities, POST /api/admin/activities, PUT /api/admin/activities/:id, DELETE /api/admin/activities/:id |
| 19 | admin-tasks/index.vue | GET /api/admin/tasks, POST /api/admin/tasks, PUT /api/admin/tasks/:id, DELETE /api/admin/tasks/:id, POST /api/admin/tasks/:id/execute |
| 20 | shop-audit/index.vue | GET /api/admin/shops/pending, POST /api/admin/shops/:id/audit |

## Mock数据结构设计

### 1. 用户管理 (admin-users)
```typescript
interface User {
  id: number
  username: string
  email: string
  phone: string
  avatar?: string
  status: 'active' | 'disabled'
  createdAt: string
  updatedAt: string
}
```

### 2. 商家管理 (admin-merchants)
```typescript
interface Merchant {
  id: number
  name: string
  contactPerson: string
  phone: string
  email: string
  address: string
  status: 'pending' | 'approved' | 'rejected' | 'disabled'
  createdAt: string
  description?: string
}
```

### 3. 服务管理 (admin-services)
```typescript
interface Service {
  id: number
  name: string
  merchantId: number
  merchantName: string
  category: string
  price: number
  duration: number
  description?: string
  status: 'active' | 'inactive'
  createdAt: string
}
```

### 4. 商品管理 (admin-products)
```typescript
interface Product {
  id: number
  name: string
  merchantId: number
  merchantName: string
  description: string
  price: number
  stock: number
  sales: number
  image?: string
  status: 'active' | 'inactive'
  createdAt: string
}
```

### 5. 评价管理 (admin-reviews)
```typescript
interface Review {
  id: number
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  serviceName?: string
  productName?: string
  rating: number
  content: string
  images?: string[]
  status: 'pending' | 'approved' | 'rejected'
  createdAt: string
}
```

### 6. 公告管理 (admin-announcements)
```typescript
interface Announcement {
  id: number
  title: string
  content: string
  author: string
  status: 'published' | 'draft'
  publishedAt?: string
  createdAt: string
  updatedAt: string
}
```

### 7. 角色权限 (roles)
```typescript
interface Role {
  id: number
  name: string
  description?: string
  permissions: number[]
  permissionCount: number
  createdAt: string
  updatedAt: string
}

interface Permission {
  id: number
  name: string
  code: string
  children?: Permission[]
}
```

### 8. 操作日志 (logs)
```typescript
interface OperationLog {
  id: number
  username: string
  action: string
  module: string
  description: string
  ipAddress: string
  userAgent?: string
  createTime: string
}
```

### 9. 活动管理 (admin-activities)
```typescript
interface Activity {
  id: number
  name: string
  type: string
  description?: string
  startTime: string
  endTime: string
  status: 'pending' | 'active' | 'ended' | 'disabled'
  createdAt: string
}
```

### 10. 任务管理 (admin-tasks)
```typescript
interface Task {
  id: number
  name: string
  type: 'scheduled' | 'onetime' | 'recurring'
  description?: string
  cronExpression?: string
  executeTime?: string
  status: 'pending' | 'running' | 'completed' | 'failed'
  result?: string
  createdAt: string
  updatedAt: string
}
```

## Mock数据场景设计

### 正常场景
- 返回符合数据结构定义的完整数据
- 分页数据返回正确的total和data
- 列表数据返回10-20条记录

### 边界场景
- 空列表：返回空的data数组和total=0
- 单条数据：列表只返回1条记录
- 长文本：字符串字段包含长内容测试截断

### 异常场景
- 401未授权：返回未登录错误
- 403禁止访问：返回权限不足错误
- 404不存在：返回资源不存在错误
- 500服务器错误：返回系统错误

## 技术方案

### 目录结构
```
cg-vue/src/mock/
├── index.js              # Mock主入口
├── admin/
│   ├── dashboard.js       # 仪表盘
│   ├── users.js           # 用户管理
│   ├── merchants.js       # 商家管理
│   ├── services.js        # 服务管理
│   ├── products.js        # 商品管理
│   ├── reviews.js         # 评价管理
│   ├── announcements.js   # 公告管理
│   ├── roles.js           # 角色权限
│   ├── logs.js            # 操作日志
│   ├── activities.js      # 活动管理
│   └── tasks.js           # 任务管理
└── utils/
    ├── random.js          # 随机数据生成工具
    └── dataGenerator.js   # 数据生成器
```

### Mock配置
- 使用mock.js的Mock.mock()方法
- 支持正则表达式匹配URL
- 支持延迟响应模拟网络延迟
- 支持设置全局响应时间

## 验收标准

### AC-1: Mock服务配置完整性
- **Given**：启动前端开发服务器
- **When**：访问任意平台端页面
- **Then**：Mock服务正确拦截API请求并返回模拟数据
- **Verification**：`programmatic` - 接口测试通过

### AC-2: 数据结构正确性
- **Given**：前端调用API获取数据
- **When**：数据返回到前端组件
- **Then**：数据结构符合TypeScript接口定义
- **Verification**：`programmatic` - 数据验证通过

### AC-3: 场景覆盖完整性
- **Given**：测试各种业务场景
- **When**：触发正常、边界、异常场景
- **Then**：Mock返回对应场景的数据
- **Verification**：`programmatic` - 场景测试通过

### AC-4: 响应时间合理
- **Given**：调用Mock API
- **When**：等待响应
- **Then**：响应时间在100ms-500ms之间
- **Verification**：`programmatic` - 性能测试通过

## 约束
- **技术栈**：mock.js
- **Mock语法**：遵循mock.js官方文档
- **数据量**：每个列表接口返回10-50条数据

## 假设
- 前端项目已安装mock.js依赖
- API请求路径与mock配置一致
- 前端能够正确处理Mock返回的数据格式
# Mock 服务使用文档

## 概述

本项目使用 MockJS 作为 Mock 服务框架，为前端开发提供模拟数据支持。Mock 服务在开发环境自动启用，生产环境自动禁用。

## 目录结构

```
src/mock/
├── index.ts              # Mock服务主入口
├── user.ts               # 用户相关Mock
├── merchant.ts           # 商家相关Mock
├── products.ts           # 商品相关Mock
├── services.ts           # 服务相关Mock
├── appointments.ts       # 预约相关Mock
├── orders.ts             # 订单相关Mock
├── reviews.ts            # 评价相关Mock
├── categories.ts         # 分类相关Mock
├── shop.ts               # 店铺相关Mock
├── stats.ts              # 统计相关Mock
└── user/                 # 用户端详细Mock
    ├── index.ts          # 用户端Mock入口
    ├── shopping.ts       # 购物相关Mock
    ├── data/             # Mock数据
    │   ├── user.ts       # 用户数据
    │   ├── pets.ts       # 宠物数据
    │   ├── products.ts   # 商品数据
    │   ├── orders.ts     # 订单数据
    │   └── ...
    └── handlers/         # Mock处理器
        ├── user.ts       # 用户处理器
        ├── cart.ts       # 购物车处理器
        └── ...
```

## 启用/禁用 Mock 服务

### 方式一：环境变量控制

在 `.env.development` 文件中设置：

```env
# 启用 Mock 服务
VITE_USE_MOCK=true

# 禁用 Mock 服务（使用真实后端API）
VITE_USE_MOCK=false
```

### 方式二：代码中控制

在 `src/main.ts` 中：

```typescript
if (import.meta.env.DEV) {
  import('./mock').then(({ setupMock }) => {
    // 启用 Mock 服务
    setupMock({ enabled: true })
    
    // 禁用 Mock 服务
    // setupMock({ enabled: false })
  })
}
```

## 配置 Mock 参数

### 响应延迟

```typescript
setupMock({
  enabled: true,
  delay: 500,  // 设置响应延迟为500ms
  debug: true  // 开启调试日志
})
```

### 调试模式

```typescript
setupMock({
  enabled: true,
  debug: true  // 在控制台显示Mock加载日志
})
```

## 添加新的 Mock 端点

### 步骤一：创建 Mock 文件

在 `src/mock/` 目录下创建新文件，例如 `src/mock/example.ts`：

```typescript
import Mock from 'mockjs'

// GET 请求
Mock.mock('/api/example', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      id: Mock.Random.integer(1, 100),
      name: Mock.Random.ctitle(2, 10),
      created_at: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

// POST 请求
Mock.mock('/api/example', 'post', (req: { body: string }) => {
  const body = JSON.parse(req.body)
  return {
    code: 200,
    message: '创建成功',
    data: {
      id: Mock.Random.integer(1000, 9999),
      ...body
    }
  }
})

// 带参数的 GET 请求
Mock.mock(RegExp('/api/example\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = url.searchParams.get('page') || '1'
  const size = url.searchParams.get('size') || '10'
  
  return {
    code: 200,
    message: 'success',
    data: {
      page: parseInt(page),
      size: parseInt(size),
      list: Mock.mock({
        [`list|${size}`]: [{
          id: '@integer(1, 100)',
          name: '@ctitle(2, 10)'
        }]
      }).list
    }
  }
})

// 带路径参数的请求
Mock.mock(RegExp('/api/example/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/example\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: 'success',
    data: {
      id,
      name: Mock.Random.ctitle(2, 10)
    }
  }
})

export function setupExampleMock() {
  console.log('[Mock] 示例模块已加载')
}
```

### 步骤二：在主入口导入

在 `src/mock/index.ts` 中添加导入：

```typescript
import Mock from 'mockjs'
import './user'
import './user/shopping'
import './merchant'
import './products'
import './services'
import './appointments'
import './orders'
import './reviews'
import './categories'
import './shop'
import './stats'
import './example'  // 添加新模块

// ... 其他代码
```

## MockJS 语法参考

### 数据占位符

```typescript
Mock.mock({
  // 基础类型
  'string': '@string',           // 随机字符串
  'integer': '@integer(1, 100)', // 1-100之间的整数
  'float': '@float(1, 100, 2, 2)', // 浮点数，小数点后2位
  'boolean': '@boolean',         // 随机布尔值
  
  // 日期时间
  'date': '@date',               // 随机日期
  'time': '@time',               // 随机时间
  'datetime': '@datetime',       // 随机日期时间
  
  // 中文文本
  'ctitle': '@ctitle(2, 10)',    // 2-10个字的中文标题
  'cparagraph': '@cparagraph(1, 3)', // 1-3句中文段落
  'cname': '@cname',             // 中文姓名
  
  // 图片
  'image': '@image("200x200", "#50B347", "#FFF", "Mock.js")',
  
  // 颜色
  'color': '@color',             // 随机颜色
  
  // 数组
  'list|10': [{                  // 生成10个元素的数组
    'id': '@integer(1, 100)',
    'name': '@ctitle(2, 5)'
  }],
  
  // 从数组中随机选择
  'status': '@pick(["pending", "approved", "rejected"])'
})
```

### 常用方法

```typescript
// 随机整数
Mock.Random.integer(1, 100)

// 随机浮点数
Mock.Random.float(1, 100, 2, 2)

// 随机中文标题
Mock.Random.ctitle(2, 10)

// 随机中文段落
Mock.Random.cparagraph(1, 3)

// 随机日期时间
Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')

// 随机布尔值
Mock.Random.boolean()

// 从数组中随机选择
Mock.Random.pick(['a', 'b', 'c'])

// 随机图片URL
Mock.Random.image('200x200', '#50B347', '#FFF', 'Mock.js')
```

## 测试 Mock 服务

### 方式一：使用测试页面

打开 `mock-api-test.html` 文件，点击"运行所有测试"按钮，查看所有API端点的测试结果。

### 方式二：浏览器控制台测试

```javascript
// 测试 GET 请求
fetch('http://localhost:8080/api/user/profile')
  .then(res => res.json())
  .then(data => console.log(data))

// 测试 POST 请求
fetch('http://localhost:8080/api/user/pets', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ name: '小猫', type: 'cat' })
})
  .then(res => res.json())
  .then(data => console.log(data))
```

### 方式三：Vue组件中测试

```vue
<script setup lang="ts">
import { onMounted } from 'vue'
import request from '@/api/request'

onMounted(async () => {
  try {
    const response = await request.get('/api/user/profile')
    console.log('Mock数据:', response)
  } catch (error) {
    console.error('请求失败:', error)
  }
})
</script>
```

## 常见问题

### 1. Mock服务没有生效

**检查项：**
- 确认 `.env.development` 中 `VITE_USE_MOCK=true`
- 确认 `import.meta.env.DEV` 为 `true`
- 检查浏览器控制台是否有 `[Mock] Mock服务初始化完成` 日志
- 确认 `src/mock/index.ts` 中已导入对应的Mock文件

### 2. API请求返回404

**原因：** Mock拦截规则没有匹配到请求URL

**解决方案：**
- 检查URL是否正确
- 使用正则表达式匹配带参数的URL：`RegExp('/api/example\\?.*')`
- 使用正则表达式匹配路径参数：`RegExp('/api/example/\\d+$')`

### 3. POST请求获取不到请求体

**解决方案：**
```typescript
Mock.mock('/api/example', 'post', (req: { body: string }) => {
  const body = JSON.parse(req.body)  // 解析请求体
  console.log('请求体:', body)
  return {
    code: 200,
    message: 'success',
    data: body
  }
})
```

### 4. 如何模拟延迟响应

**解决方案：**
```typescript
setupMock({
  enabled: true,
  delay: 500  // 设置500ms延迟
})
```

或在单个Mock中设置：
```typescript
Mock.setup({
  timeout: '200-600'  // 200-600ms随机延迟
})
```

### 5. 如何模拟错误响应

**解决方案：**
```typescript
Mock.mock('/api/example', 'get', () => {
  // 模拟401未授权
  return {
    code: 401,
    message: '未授权，请重新登录',
    data: null
  }
})

Mock.mock('/api/example', 'post', (req: { body: string }) => {
  const { email } = JSON.parse(req.body)
  
  // 模拟业务错误
  if (email === 'existing@example.com') {
    return {
      code: 400,
      message: '邮箱已被注册',
      data: null
    }
  }
  
  return {
    code: 200,
    message: '注册成功',
    data: { id: Mock.Random.integer(1000, 9999) }
  }
})
```

### 6. 如何重置Mock数据

**解决方案：**
```typescript
import { resetMock } from '@/mock'

// 重置所有Mock拦截
resetMock()
```

## Mock数据持久化

Mock数据存储在内存中，页面刷新后会重置。如需持久化数据，可以：

### 方案一：使用localStorage

```typescript
// 保存数据
localStorage.setItem('mockData', JSON.stringify(data))

// 读取数据
const data = JSON.parse(localStorage.getItem('mockData') || '{}')
```

### 方案二：使用sessionStorage

```typescript
// 保存数据（仅在当前会话有效）
sessionStorage.setItem('mockData', JSON.stringify(data))

// 读取数据
const data = JSON.parse(sessionStorage.getItem('mockData') || '{}')
```

## 最佳实践

1. **模块化组织**：按功能模块组织Mock文件，便于维护
2. **统一响应格式**：所有API返回统一格式 `{ code, message, data }`
3. **模拟真实场景**：包括成功、失败、边界情况
4. **添加调试日志**：使用 `console.log` 记录关键操作
5. **定期更新**：根据后端API文档及时更新Mock数据
6. **类型安全**：使用TypeScript定义Mock数据类型

## 相关链接

- [MockJS 官方文档](http://mockjs.com/)
- [MockJS GitHub](https://github.com/nuysoft/Mock)
- [Vite 环境变量](https://vitejs.dev/guide/env-and-mode.html)

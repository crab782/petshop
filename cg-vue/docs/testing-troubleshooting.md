# 测试故障排查指南

## 概述

本文档提供了测试过程中常见问题的排查方法和解决方案，帮助快速定位和解决测试失败。

## 常见测试失败原因

### 1. 组件渲染失败

#### 症状

```
TypeError: Cannot read properties of undefined (reading 'xxx')
```

#### 可能原因

- 缺少必要的 props
- 未正确注册全局组件
- 缺少必要的插件（如 Pinia、Router）

#### 解决方案

```typescript
// ❌ 错误示例
const wrapper = mount(MyComponent)

// ✅ 正确示例
const wrapper = mount(MyComponent, {
  props: {
    userId: 1,
  },
  global: {
    plugins: [createPinia()],
    components: {
      ElButton,
      ElInput,
    },
  },
})
```

### 2. 异步操作未完成

#### 症状

```
AssertionError: expected '' to contain 'loaded data'
```

#### 可能原因

- 未等待异步操作完成
- 未等待 DOM 更新

#### 解决方案

```typescript
// ❌ 错误示例
it('should load data', async () => {
  const wrapper = mount(Component)
  expect(wrapper.find('.data').text()).toContain('loaded data')
})

// ✅ 正确示例
it('should load data', async () => {
  const wrapper = mount(Component)
  await wrapper.vm.$nextTick()
  await new Promise(resolve => setTimeout(resolve, 100))
  expect(wrapper.find('.data').text()).toContain('loaded data')
})
```

### 3. Mock 未生效

#### 症状

```
TypeError: xxx is not a function
```

#### 可能原因

- Mock 路径错误
- Mock 未正确配置
- Mock 被其他测试污染

#### 解决方案

```typescript
// ❌ 错误示例
vi.mock('@/api/user', () => ({
  getUser: vi.fn(),
}))

// ✅ 正确示例
vi.mock('@/api/user', () => ({
  getUser: vi.fn(() => Promise.resolve({ id: 1, name: 'Test User' })),
}))

// 在 beforeEach 中重置
beforeEach(() => {
  vi.clearAllMocks()
})
```

### 4. Store 状态问题

#### 症状

```
TypeError: Cannot read properties of undefined (reading 'user')
```

#### 可能原因

- 未初始化 Pinia
- Store 未正确 Mock

#### 解决方案

```typescript
// ❌ 错误示例
const wrapper = mount(Component)

// ✅ 正确示例
import { createPinia, setActivePinia } from 'pinia'

beforeEach(() => {
  setActivePinia(createPinia())
})

const wrapper = mount(Component, {
  global: {
    plugins: [createPinia()],
  },
})
```

## 常见错误及解决方案

### 1. Element Plus 组件问题

#### 错误：组件未注册

```
[Vue warn]: Failed to resolve component: el-button
```

**解决方案**：

```typescript
import { ElButton, ElInput, ElForm } from 'element-plus'

const wrapper = mount(Component, {
  global: {
    components: {
      ElButton,
      ElInput,
      ElForm,
    },
  },
})
```

#### 错误：ElMessage 未定义

```
TypeError: ElMessage is not a function
```

**解决方案**：

```typescript
// 在 setup.ts 中全局 Mock
vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn(),
    },
  }
})
```

### 2. Router 相关问题

#### 错误：useRouter 未定义

```
TypeError: Cannot read properties of undefined (reading 'push')
```

**解决方案**：

```typescript
// 在 setup.ts 中全局 Mock
vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router')
  return {
    ...actual,
    useRouter: () => ({
      push: vi.fn(),
      replace: vi.fn(),
      go: vi.fn(),
      back: vi.fn(),
    }),
    useRoute: () => ({
      params: {},
      query: {},
      path: '/',
    }),
  }
})
```

#### 错误：路由参数未传递

```
TypeError: Cannot read properties of undefined (reading 'id')
```

**解决方案**：

```typescript
const wrapper = mount(Component, {
  global: {
    mocks: {
      $route: {
        params: { id: '1' },
        query: {},
      },
    },
  },
})
```

### 3. API 调用问题

#### 错误：API 返回 undefined

```
TypeError: Cannot read properties of undefined (reading 'data')
```

**解决方案**：

```typescript
// 确保 Mock 返回正确的数据结构
vi.mock('@/api/user', () => ({
  getUser: vi.fn(() => Promise.resolve({
    id: 1,
    name: 'Test User',
    email: 'test@example.com',
  })),
}))
```

#### 错误：API 调用未触发

```
Error: expect(api.getUser).toHaveBeenCalled()
```

**解决方案**：

```typescript
// 确保等待异步操作
it('should call API', async () => {
  const wrapper = mount(Component)
  await wrapper.find('.btn').trigger('click')
  await wrapper.vm.$nextTick()
  await new Promise(resolve => setTimeout(resolve, 100))
  
  expect(mockApi.getUser).toHaveBeenCalled()
})
```

### 4. DOM 元素未找到

#### 错误：元素不存在

```
Error: wrapper.find('.element').exists() === false
```

**解决方案**：

```typescript
// 1. 检查选择器是否正确
console.log(wrapper.html())

// 2. 使用更通用的选择器
wrapper.find('[data-testid="submit-button"]')

// 3. 等待元素出现
await wrapper.vm.$nextTick()
await new Promise(resolve => setTimeout(resolve, 100))
```

### 5. 事件触发问题

#### 错误：事件未触发

```
Error: Expected event 'submit' to have been emitted
```

**解决方案**：

```typescript
// ❌ 错误示例
await wrapper.find('form').trigger('submit')

// ✅ 正确示例 - 触发表单的原生提交事件
await wrapper.find('form').trigger('submit.prevent')

// 或者触发按钮点击
await wrapper.find('button[type="submit"]').trigger('click')
await wrapper.vm.$nextTick()
```

## 调试技巧

### 1. 使用 console.log

```typescript
it('should work', async () => {
  const wrapper = mount(Component)
  console.log('HTML:', wrapper.html())
  console.log('VM:', wrapper.vm)
  console.log('Props:', wrapper.props())
})
```

### 2. 使用 debug 方法

```typescript
import { mount } from '@vue/test-utils'

it('should work', () => {
  const wrapper = mount(Component)
  wrapper.debug() // 输出组件的 HTML 和状态
})
```

### 3. 查看组件状态

```typescript
it('should work', () => {
  const wrapper = mount(Component)
  console.log('Data:', wrapper.vm.$data)
  console.log('Props:', wrapper.vm.$props)
  console.log('Computed:', Object.keys(wrapper.vm.$options.computed || {}))
})
```

### 4. 检查 Mock 调用

```typescript
it('should call API', async () => {
  const wrapper = mount(Component)
  await wrapper.find('.btn').trigger('click')
  
  console.log('Mock calls:', mockApi.getUser.mock.calls)
  console.log('Mock instances:', mockApi.getUser.mock.instances)
})
```

## 性能问题排查

### 1. 测试运行缓慢

#### 可能原因

- 大量的 setTimeout
- 不必要的等待
- Mock 配置不当

#### 解决方案

```typescript
// 使用 fake timers
vi.useFakeTimers()

// 快进时间
vi.advanceTimersByTime(1000)

// 恢复真实定时器
vi.useRealTimers()
```

### 2. 内存泄漏

#### 可能原因

- 未清理事件监听器
- 未清理定时器
- 未销毁组件

#### 解决方案

```typescript
afterEach(() => {
  wrapper.unmount()
  vi.clearAllTimers()
  vi.clearAllMocks()
})
```

## E2E 测试问题

### 1. 元素未找到

```typescript
// ❌ 错误示例
await page.click('.submit-button')

// ✅ 正确示例 - 使用更可靠的选择器
await page.click('[data-testid="submit-button"]')
await page.click('button:has-text("提交")')
```

### 2. 页面加载超时

```typescript
// 增加超时时间
await page.goto('/dashboard', { timeout: 60000 })

// 或等待特定元素
await page.waitForSelector('.loaded', { timeout: 60000 })
```

### 3. 网络请求问题

```typescript
// Mock 网络请求
await page.route('**/api/users', route => {
  route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify([{ id: 1, name: 'Test User' }]),
  })
})
```

## 测试环境问题

### 1. Node 版本问题

```bash
# 检查 Node 版本
node -v

# 应该 >= 20.19.0 或 >= 22.12.0
```

### 2. 依赖问题

```bash
# 清理并重新安装依赖
rm -rf node_modules package-lock.json
npm install
```

### 3. 缓存问题

```bash
# 清理测试缓存
npm run test -- --clearCache

# 清理构建缓存
rm -rf node_modules/.vite
```

## 日志分析

### 1. 分析测试输出

```bash
# 运行单个测试文件并显示详细输出
npm run test -- path/to/test.spec.ts --reporter=verbose
```

### 2. 分析覆盖率报告

```bash
# 生成覆盖率报告
npm run test:coverage

# 查看未覆盖的代码
cat coverage/lcov-report/index.html | grep "Uncovered"
```

## 常见问题清单

### 测试前检查

- [ ] Node 版本是否符合要求
- [ ] 依赖是否正确安装
- [ ] 测试配置是否正确
- [ ] Mock 是否正确配置
- [ ] 测试数据是否准备完整

### 测试失败检查

- [ ] 查看错误信息
- [ ] 检查组件是否正确挂载
- [ ] 检查异步操作是否等待
- [ ] 检查 Mock 是否生效
- [ ] 检查选择器是否正确

### 覆盖率问题检查

- [ ] 是否有未测试的文件
- [ ] 是否有未覆盖的分支
- [ ] 是否有未测试的错误处理
- [ ] 是否有未测试的边界情况

## 获取帮助

### 1. 查看文档

- [Vitest 文档](https://vitest.dev/)
- [Vue Test Utils 文档](https://test-utils.vuejs.org/)
- [Playwright 文档](https://playwright.dev/)

### 2. 搜索问题

- [Stack Overflow](https://stackoverflow.com/)
- [GitHub Issues](https://github.com/)

### 3. 调试步骤

1. 隔离问题：只运行失败的测试
2. 简化问题：移除不必要的代码
3. 添加日志：使用 console.log 调试
4. 检查文档：确认 API 使用正确
5. 寻求帮助：在团队中讨论

## 相关文档

- [测试指南](./testing-guide.md)
- [测试最佳实践](./testing-best-practices.md)
- [测试命令说明](./testing-commands.md)
- [测试覆盖率说明](./testing-coverage.md)

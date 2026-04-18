# 测试指南

## 概述

本项目采用 Vitest 作为单元测试框架，Playwright 作为端到端测试框架。本文档提供了测试的基本指南和最佳实践。

## 测试框架

### 单元测试 - Vitest

Vitest 是一个基于 Vite 的快速单元测试框架，具有以下特点：

- 快速的测试执行速度
- 原生支持 TypeScript 和 Vue
- 内置代码覆盖率报告
- 支持 Jest 兼容的 API

### 端到端测试 - Playwright

Playwright 用于端到端测试，支持：

- 跨浏览器测试
- 自动等待机制
- 强大的选择器引擎
- 截图和视频录制

## 测试结构

```
cg-vue/
├── src/
│   ├── tests/                    # 测试配置和工具
│   │   ├── setup.ts             # 测试环境设置
│   │   ├── utils/               # 测试工具函数
│   │   ├── mocks/               # Mock 数据
│   │   ├── fixtures/            # 测试固件
│   │   ├── boundary/            # 边界测试
│   │   └── integration/         # 集成测试
│   ├── views/                   # 页面组件
│   │   ├── admin/              # 管理端页面
│   │   │   └── __tests__/      # 管理端测试
│   │   ├── merchant/           # 商家端页面
│   │   │   └── __tests__/      # 商家端测试
│   │   └── user/               # 用户端页面
│   │       └── __tests__/      # 用户端测试
│   └── components/             # 公共组件
│       └── __tests__/          # 组件测试
└── e2e/                        # 端到端测试
    ├── admin/                  # 管理端E2E测试
    ├── user/                   # 用户端E2E测试
    └── merchant-*.spec.ts      # 商家端E2E测试
```

## 测试类型

### 1. 单元测试

测试独立的函数、组件或模块。

```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import MyComponent from '../MyComponent.vue'

describe('MyComponent', () => {
  it('renders correctly', () => {
    const wrapper = mount(MyComponent)
    expect(wrapper.exists()).toBe(true)
  })
})
```

### 2. 集成测试

测试多个组件或模块之间的交互。

```typescript
describe('User Management', () => {
  it('should create and delete user', async () => {
    const wrapper = mount(UserManagement)
    await wrapper.find('.create-btn').trigger('click')
    expect(wrapper.find('.user-form').exists()).toBe(true)
  })
})
```

### 3. 边界测试

测试边界条件和异常情况。

```typescript
describe('Form Validation', () => {
  it('should show error for empty required field', async () => {
    const wrapper = mount(Form)
    await wrapper.find('input').setValue('')
    expect(wrapper.find('.error-message').exists()).toBe(true)
  })
})
```

### 4. 端到端测试

测试完整的用户流程。

```typescript
test('user can login', async ({ page }) => {
  await page.goto('/login')
  await page.fill('[name="username"]', 'testuser')
  await page.fill('[name="password"]', 'password123')
  await page.click('button[type="submit"]')
  await expect(page).toHaveURL('/dashboard')
})
```

## 测试命名规范

### 文件命名

- 单元测试：`*.spec.ts` 或 `*.test.ts`
- 端到端测试：`*.e2e.ts` 或 `*.spec.ts`
- 测试文件应放在被测试文件同级的 `__tests__` 目录中

### 测试描述

```typescript
describe('ComponentName', () => {
  describe('功能模块', () => {
    it('should do something when condition', () => {
      // 测试代码
    })
  })
})
```

## Mock 和 Stub

### API Mock

```typescript
vi.mock('@/api/user', () => ({
  getUser: vi.fn(() => Promise.resolve({ id: 1, name: 'Test User' })),
  updateUser: vi.fn(() => Promise.resolve({ success: true })),
}))
```

### Store Mock

```typescript
vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    user: { id: 1, username: 'testuser' },
    isLoggedIn: true,
    login: vi.fn(),
    logout: vi.fn(),
  }),
}))
```

### Router Mock

```typescript
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
  }),
  useRoute: () => ({
    params: { id: '1' },
    query: {},
  }),
}))
```

## 测试数据

### 使用 Fixtures

```typescript
import { createMerchant, createService } from '@/tests/fixtures'

const merchant = createMerchant({ id: 1, name: 'Test Merchant' })
const service = createService({ id: 1, merchantId: 1 })
```

### 使用数据生成器

```typescript
import { createServiceList } from '@/tests/utils/userTestUtils'

const services = createServiceList(10) // 生成10个服务
```

## 异步测试

### 等待 DOM 更新

```typescript
import { nextTick } from 'vue'

it('should update after async operation', async () => {
  const wrapper = mount(Component)
  await wrapper.find('.btn').trigger('click')
  await nextTick()
  expect(wrapper.find('.result').text()).toBe('Updated')
})
```

### 等待定时器

```typescript
it('should update after delay', async () => {
  const wrapper = mount(Component)
  await wrapper.find('.btn').trigger('click')
  await new Promise(resolve => setTimeout(resolve, 100))
  expect(wrapper.find('.result').exists()).toBe(true)
})
```

## 测试覆盖率

### 运行覆盖率报告

```bash
npm run test:coverage
```

### 覆盖率目标

- 语句覆盖率：≥ 80%
- 分支覆盖率：≥ 70%
- 函数覆盖率：≥ 80%
- 行覆盖率：≥ 80%

### 查看覆盖率报告

覆盖率报告生成在 `coverage/lcov-report/index.html`，可在浏览器中打开查看详细信息。

## 最佳实践

1. **测试应该独立**：每个测试应该独立运行，不依赖其他测试
2. **测试应该可重复**：多次运行应该得到相同的结果
3. **测试应该快速**：避免不必要的等待和延迟
4. **测试应该清晰**：测试名称和结构应该清晰表达测试意图
5. **测试应该全面**：覆盖正常情况、边界情况和异常情况

## 常见问题

### 1. 测试失败但代码正常工作

- 检查 Mock 是否正确配置
- 检查异步操作是否正确等待
- 检查选择器是否正确

### 2. 测试运行缓慢

- 减少不必要的等待时间
- 使用 `vi.useFakeTimers()` 模拟定时器
- 优化 Mock 数据

### 3. 组件无法渲染

- 检查是否正确注册全局组件
- 检查是否正确配置插件
- 检查依赖是否正确 Mock

## 相关文档

- [测试最佳实践](./testing-best-practices.md)
- [测试命令说明](./testing-commands.md)
- [测试覆盖率说明](./testing-coverage.md)
- [故障排查指南](./testing-troubleshooting.md)

# 测试最佳实践

## 概述

本文档提供了编写高质量测试的最佳实践指南，帮助团队编写可维护、可靠和高效的测试代码。

## 测试原则

### 1. FIRST 原则

- **F**ast（快速）：测试应该快速执行
- **I**ndependent（独立）：测试之间不应该有依赖关系
- **R**epeatable（可重复）：测试应该在任何环境下都能重复运行
- **S**elf-validating（自我验证）：测试应该自动判断通过或失败
- **T**imely（及时）：测试应该及时编写，最好在编码之前

### 2. AAA 模式

每个测试应遵循 Arrange-Act-Assert 模式：

```typescript
it('should calculate total price correctly', () => {
  // Arrange - 准备测试数据
  const items = [
    { price: 100, quantity: 2 },
    { price: 50, quantity: 3 },
  ]
  
  // Act - 执行被测试的操作
  const total = calculateTotal(items)
  
  // Assert - 验证结果
  expect(total).toBe(350)
})
```

## 组件测试最佳实践

### 1. 测试用户行为，而非实现细节

```typescript
// ❌ 不好的实践 - 测试实现细节
it('should set data property', async () => {
  const wrapper = mount(Component)
  await wrapper.find('.btn').trigger('click')
  expect(wrapper.vm.isLoading).toBe(true)
})

// ✅ 好的实践 - 测试用户可见的行为
it('should show loading indicator when button clicked', async () => {
  const wrapper = mount(Component)
  await wrapper.find('.btn').trigger('click')
  expect(wrapper.find('.loading-spinner').exists()).toBe(true)
})
```

### 2. 使用语义化的选择器

```typescript
// ❌ 不好的实践 - 使用脆弱的选择器
await wrapper.find('.btn-123').trigger('click')

// ✅ 好的实践 - 使用语义化的选择器
await wrapper.find('[data-testid="submit-button"]').trigger('click')
await wrapper.find('button[type="submit"]').trigger('click')
```

### 3. 测试组件的输入和输出

```typescript
describe('SearchInput', () => {
  it('should emit search event with input value', async () => {
    const wrapper = mount(SearchInput)
    const input = wrapper.find('input')
    
    await input.setValue('test query')
    await input.trigger('keyup.enter')
    
    expect(wrapper.emitted('search')).toBeTruthy()
    expect(wrapper.emitted('search')[0]).toEqual(['test query'])
  })
})
```

### 4. 测试组件的不同状态

```typescript
describe('Button', () => {
  it('should render in default state', () => {
    const wrapper = mount(Button)
    expect(wrapper.classes()).not.toContain('is-loading')
    expect(wrapper.classes()).not.toContain('is-disabled')
  })
  
  it('should render in loading state', () => {
    const wrapper = mount(Button, {
      props: { loading: true }
    })
    expect(wrapper.classes()).toContain('is-loading')
  })
  
  it('should render in disabled state', () => {
    const wrapper = mount(Button, {
      props: { disabled: true }
    })
    expect(wrapper.classes()).toContain('is-disabled')
  })
})
```

## Mock 和 Stub 最佳实践

### 1. 只 Mock 外部依赖

```typescript
// ❌ 不好的实践 - Mock 被测试的模块
vi.mock('./calculator', () => ({
  add: vi.fn(() => 5),
}))

// ✅ 好的实践 - 只 Mock 外部依赖
vi.mock('@/api/user', () => ({
  getUser: vi.fn(() => Promise.resolve({ id: 1 })),
}))
```

### 2. Mock 应该尽可能接近真实实现

```typescript
// ❌ 不好的实践 - 过度简化的 Mock
vi.mock('@/api/user', () => ({
  getUser: vi.fn(() => Promise.resolve({})),
}))

// ✅ 好的实践 - 接近真实实现的 Mock
vi.mock('@/api/user', () => ({
  getUser: vi.fn((id: number) => 
    Promise.resolve({
      id,
      username: 'testuser',
      email: 'test@example.com',
      avatar: 'https://example.com/avatar.png',
      createdAt: '2024-01-01T00:00:00.000Z',
    })
  ),
}))
```

### 3. 在 beforeEach 中重置 Mock

```typescript
describe('UserService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })
  
  it('should fetch user data', async () => {
    const user = await userService.getUser(1)
    expect(mockApi.getUser).toHaveBeenCalledWith(1)
  })
})
```

## 异步测试最佳实践

### 1. 正确等待异步操作

```typescript
// ❌ 不好的实践 - 使用固定的 setTimeout
it('should load data', async () => {
  const wrapper = mount(Component)
  await new Promise(resolve => setTimeout(resolve, 1000))
  expect(wrapper.find('.data').exists()).toBe(true)
})

// ✅ 好的实践 - 使用 nextTick 或 waitFor
it('should load data', async () => {
  const wrapper = mount(Component)
  await wrapper.vm.$nextTick()
  await flushPromises()
  expect(wrapper.find('.data').exists()).toBe(true)
})
```

### 2. 使用 fake timers 控制时间

```typescript
it('should auto-close notification after 3 seconds', async () => {
  vi.useFakeTimers()
  
  const wrapper = mount(Notification)
  wrapper.find('.close-btn').trigger('click')
  
  vi.advanceTimersByTime(3000)
  await wrapper.vm.$nextTick()
  
  expect(wrapper.emitted('close')).toBeTruthy()
  
  vi.useRealTimers()
})
```

## 测试数据最佳实践

### 1. 使用工厂函数创建测试数据

```typescript
// factories/user.ts
export const createUser = (overrides: Partial<User> = {}): User => ({
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  phone: '13800138000',
  avatar: '',
  createdAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

// 使用
const user = createUser({ username: 'customuser' })
```

### 2. 使用 Fixtures 管理测试数据

```typescript
// fixtures/index.ts
export const fixtures = {
  users: [
    createUser({ id: 1, username: 'user1' }),
    createUser({ id: 2, username: 'user2' }),
  ],
  merchants: [
    createMerchant({ id: 1, name: 'Merchant 1' }),
  ],
}
```

### 3. 避免硬编码测试数据

```typescript
// ❌ 不好的实践 - 硬编码数据
const user = {
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
}

// ✅ 好的实践 - 使用工厂函数
const user = createUser()
```

## 测试组织最佳实践

### 1. 使用 describe 组织相关测试

```typescript
describe('UserService', () => {
  describe('getUser', () => {
    it('should return user by id', async () => {
      // ...
    })
    
    it('should throw error when user not found', async () => {
      // ...
    })
  })
  
  describe('createUser', () => {
    it('should create new user', async () => {
      // ...
    })
    
    it('should validate user data', async () => {
      // ...
    })
  })
})
```

### 2. 使用 before/after 钩子

```typescript
describe('DatabaseService', () => {
  beforeAll(async () => {
    await database.connect()
  })
  
  afterAll(async () => {
    await database.disconnect()
  })
  
  beforeEach(async () => {
    await database.clear()
  })
  
  it('should insert user', async () => {
    // ...
  })
})
```

## 边界测试最佳实践

### 1. 测试边界值

```typescript
describe('validateAge', () => {
  it('should accept valid age', () => {
    expect(validateAge(18)).toBe(true)
    expect(validateAge(100)).toBe(true)
  })
  
  it('should reject invalid age', () => {
    expect(validateAge(17)).toBe(false)
    expect(validateAge(101)).toBe(false)
    expect(validateAge(-1)).toBe(false)
  })
})
```

### 2. 测试空值和异常情况

```typescript
describe('formatPrice', () => {
  it('should format valid price', () => {
    expect(formatPrice(100)).toBe('¥100.00')
  })
  
  it('should handle zero price', () => {
    expect(formatPrice(0)).toBe('¥0.00')
  })
  
  it('should handle null/undefined', () => {
    expect(formatPrice(null)).toBe('¥0.00')
    expect(formatPrice(undefined)).toBe('¥0.00')
  })
  
  it('should handle negative price', () => {
    expect(formatPrice(-100)).toBe('-¥100.00')
  })
})
```

## 性能测试最佳实践

### 1. 避免不必要的渲染

```typescript
it('should not re-render unnecessarily', async () => {
  const wrapper = mount(Component)
  const renderSpy = vi.spyOn(wrapper.vm, '$forceUpdate')
  
  await wrapper.setProps({ data: 'same data' })
  
  expect(renderSpy).not.toHaveBeenCalled()
})
```

### 2. 测试大数据量性能

```typescript
it('should handle large list efficiently', async () => {
  const largeList = Array.from({ length: 10000 }, (_, i) => ({ id: i }))
  
  const startTime = performance.now()
  const wrapper = mount(ListComponent, {
    props: { items: largeList }
  })
  const endTime = performance.now()
  
  expect(endTime - startTime).toBeLessThan(1000) // 应该在1秒内完成
})
```

## E2E 测试最佳实践

### 1. 测试关键用户流程

```typescript
test('user can complete purchase flow', async ({ page }) => {
  await page.goto('/products/1')
  await page.click('[data-testid="add-to-cart"]')
  await page.click('[data-testid="checkout"]')
  await page.fill('[name="address"]', '北京市朝阳区')
  await page.click('[data-testid="confirm-order"]')
  await expect(page.locator('.success-message')).toBeVisible()
})
```

### 2. 使用 Page Object Model

```typescript
class LoginPage {
  constructor(private page: Page) {}
  
  async login(username: string, password: string) {
    await this.page.fill('[name="username"]', username)
    await this.page.fill('[name="password"]', password)
    await this.page.click('button[type="submit"]')
  }
}

test('user can login', async ({ page }) => {
  const loginPage = new LoginPage(page)
  await loginPage.login('testuser', 'password123')
  await expect(page).toHaveURL('/dashboard')
})
```

## 测试覆盖率最佳实践

### 1. 关注关键路径

优先测试：
- 核心业务逻辑
- 用户关键操作流程
- 数据处理和验证
- 错误处理和异常情况

### 2. 避免为了覆盖率而测试

```typescript
// ❌ 不好的实践 - 无意义的测试
it('should have property', () => {
  const obj = { prop: 'value' }
  expect(obj.prop).toBe('value')
})

// ✅ 好的实践 - 测试有意义的逻辑
it('should calculate discount correctly', () => {
  const price = 100
  const discount = 0.2
  const finalPrice = calculateDiscount(price, discount)
  expect(finalPrice).toBe(80)
})
```

## 持续集成最佳实践

### 1. 在 CI 中运行测试

```yaml
name: Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: npm ci
      - run: npm run test:coverage
      - run: npm run test:e2e
```

### 2. 设置覆盖率阈值

```typescript
// vitest.config.ts
coverage: {
  lines: 80,
  functions: 80,
  branches: 70,
  statements: 80,
}
```

## 相关文档

- [测试指南](./testing-guide.md)
- [测试命令说明](./testing-commands.md)
- [测试覆盖率说明](./testing-coverage.md)
- [故障排查指南](./testing-troubleshooting.md)

# 测试覆盖率说明

## 概述

测试覆盖率是衡量测试质量的重要指标，表示代码被测试执行覆盖的程度。本文档详细说明了项目的测试覆盖率配置、目标和改进策略。

## 覆盖率类型

### 1. 语句覆盖率（Statements）

衡量代码中每个语句是否被执行。

```typescript
// 示例
function calculate(a: number, b: number) {
  const result = a + b;  // 语句 1
  return result;         // 语句 2
}
```

如果测试调用了 `calculate(1, 2)`，则两个语句都被执行，语句覆盖率为 100%。

### 2. 分支覆盖率（Branches）

衡量代码中每个分支是否被执行。

```typescript
// 示例
function validate(age: number) {
  if (age >= 18) {        // 分支 1: age >= 18
    return 'adult';
  } else {                // 分支 2: age < 18
    return 'minor';
  }
}
```

要达到 100% 分支覆盖率，需要测试两个分支：
- `validate(20)` - 测试 age >= 18 分支
- `validate(10)` - 测试 age < 18 分支

### 3. 函数覆盖率（Functions）

衡量代码中每个函数是否被调用。

```typescript
// 示例
class Calculator {
  add(a: number, b: number) { return a + b; }    // 函数 1
  subtract(a: number, b: number) { return a - b; } // 函数 2
}
```

要达到 100% 函数覆盖率，需要调用两个函数。

### 4. 行覆盖率（Lines）

衡量代码中每一行是否被执行。

```typescript
// 示例
function process(data: any) {
  const result = data.value;  // 行 1
  return result;              // 行 2
}
```

## 覆盖率配置

### Vitest 配置

```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      reportsDirectory: './coverage',
      include: [
        'src/**/*.{js,ts,vue}',
      ],
      exclude: [
        'src/**/__tests__/**',
        'src/**/node_modules/**',
        'src/**/*.d.ts',
        'src/main.ts',
        'src/tests/**',
        'src/mock/**',
      ],
      all: true,
      lines: 80,           // 行覆盖率目标
      functions: 80,       // 函数覆盖率目标
      branches: 70,        // 分支覆盖率目标
      statements: 80,      // 语句覆盖率目标
      perFile: true,       // 每个文件单独检查
      watermarks: {
        lines: [50, 80],      // [低阈值, 高阈值]
        functions: [50, 80],
        branches: [40, 70],
        statements: [50, 80],
      },
    },
  },
})
```

### 覆盖率阈值说明

- **低阈值（50%）**：低于此值显示为红色（低覆盖率）
- **高阈值（80%）**：高于此值显示为绿色（高覆盖率）
- **中间值**：显示为黄色（中等覆盖率）

## 覆盖率目标

### 项目整体目标

| 指标 | 目标 | 说明 |
|------|------|------|
| 语句覆盖率 | ≥ 80% | 核心业务逻辑应达到 90%+ |
| 分支覆盖率 | ≥ 70% | 关键分支应达到 85%+ |
| 函数覆盖率 | ≥ 80% | 公共 API 应达到 100% |
| 行覆盖率 | ≥ 80% | 核心模块应达到 90%+ |

### 模块级别目标

#### 高优先级模块（目标：90%+）

- `src/api/` - API 调用层
- `src/stores/` - 状态管理
- `src/router/` - 路由配置
- 核心业务组件

#### 中优先级模块（目标：80%+）

- `src/views/` - 页面组件
- `src/components/` - 公共组件
- 工具函数

#### 低优先级模块（目标：60%+）

- UI 展示组件
- 配置文件
- 类型定义文件

## 查看覆盖率报告

### 生成覆盖率报告

```bash
npm run test:coverage
```

### 控制台输出

```
 % Coverage report from v8
---------------------------|---------|----------|---------|---------|-------------------
File                       | % Stmts | % Branch | % Funcs | % Lines | Uncovered Line #s
---------------------------|---------|----------|---------|---------|-------------------
All files                  |   85.23 |    78.45 |   82.16 |   85.01 |
 src/api                   |   92.34 |    88.12 |   90.45 |   92.11 |
  admin.ts                 |   95.12 |    90.34 |   93.22 |   95.00 | 45-47
  user.ts                  |   89.56 |    85.90 |   87.68 |   89.22 | 78-82
 src/components            |   78.45 |    72.34 |   75.89 |   78.12 |
  AdminCard.vue            |   82.34 |    78.90 |   80.12 |   82.00 | 23-25
---------------------------|---------|----------|---------|---------|-------------------
```

### HTML 报告

打开 `coverage/lcov-report/index.html` 查看详细的 HTML 报告。

**功能**：
- 按文件查看覆盖率
- 查看未覆盖的代码行
- 按覆盖率排序
- 查看覆盖率趋势

### LCOV 报告

`coverage/lcov.info` 文件可用于：
- 与 SonarQube 等代码质量工具集成
- 与 Codecov 等覆盖率服务集成
- CI/CD 流程中的覆盖率检查

## 提高覆盖率的策略

### 1. 识别未覆盖的代码

#### 使用 HTML 报告

1. 打开 `coverage/lcov-report/index.html`
2. 查看红色标记的文件（低覆盖率）
3. 点击文件查看未覆盖的代码行

#### 使用命令行

```bash
npm run test:coverage 2>&1 | grep -A 5 "Uncovered"
```

### 2. 优先级排序

根据以下标准确定优先级：

1. **业务重要性**：核心业务逻辑优先
2. **代码复杂度**：复杂逻辑优先
3. **变更频率**：频繁变更的代码优先
4. **Bug 历史**：有 Bug 历史的代码优先

### 3. 补充测试用例

#### 测试正常流程

```typescript
it('should create user successfully', async () => {
  const user = await userService.create({
    username: 'testuser',
    email: 'test@example.com',
  })
  expect(user.id).toBeDefined()
  expect(user.username).toBe('testuser')
})
```

#### 测试边界情况

```typescript
describe('validateAge', () => {
  it('should accept valid age', () => {
    expect(validateAge(18)).toBe(true)
    expect(validateAge(100)).toBe(true)
  })
  
  it('should reject invalid age', () => {
    expect(validateAge(17)).toBe(false)
    expect(validateAge(0)).toBe(false)
    expect(validateAge(-1)).toBe(false)
  })
})
```

#### 测试错误处理

```typescript
it('should handle error when user not found', async () => {
  await expect(userService.getById(999))
    .rejects.toThrow('User not found')
})
```

### 4. 重构难以测试的代码

#### 提取纯函数

```typescript
// ❌ 难以测试
class UserService {
  async validateAndCreate(data: any) {
    if (!data.username || data.username.length < 3) {
      throw new Error('Invalid username')
    }
    if (!data.email || !data.email.includes('@')) {
      throw new Error('Invalid email')
    }
    return this.create(data)
  }
}

// ✅ 易于测试
function validateUsername(username: string): boolean {
  return username && username.length >= 3
}

function validateEmail(email: string): boolean {
  return email && email.includes('@')
}

class UserService {
  async validateAndCreate(data: any) {
    if (!validateUsername(data.username)) {
      throw new Error('Invalid username')
    }
    if (!validateEmail(data.email)) {
      throw new Error('Invalid email')
    }
    return this.create(data)
  }
}
```

## 覆盖率报告分析

### 当前项目覆盖率

根据最新的测试运行结果：

```
Statements: 2.48% (243/9768)
Branches: 2.18% (109/4980)
Functions: 0.55% (23/4132)
Lines: 2.48% (224/9014)
```

### 主要问题

1. **覆盖率极低**：远低于 80% 的目标
2. **测试失败**：260 个测试失败
3. **Mock 配置问题**：可能影响覆盖率统计

### 改进计划

#### 第一阶段：修复失败的测试

1. 修复 260 个失败的测试用例
2. 确保 Mock 正确配置
3. 验证测试环境

#### 第二阶段：提高核心模块覆盖率

优先级顺序：

1. `src/api/` - API 层（目标：90%）
2. `src/stores/` - 状态管理（目标：90%）
3. `src/router/` - 路由（目标：85%）
4. 核心业务组件（目标：85%）

#### 第三阶段：全面提高覆盖率

1. 为所有组件补充测试
2. 覆盖边界情况
3. 覆盖错误处理

## 覆盖率监控

### CI/CD 集成

```yaml
# .github/workflows/test.yml
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
      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov.info
```

### 覆盖率徽章

在 README.md 中添加覆盖率徽章：

```markdown
[![Coverage Status](https://codecov.io/gh/user/repo/branch/main/graph/badge.svg)](https://codecov.io/gh/user/repo)
```

## 覆盖率误区

### 1. 高覆盖率 ≠ 高质量

覆盖率只表示代码被执行过，不代表测试有效。

```typescript
// ❌ 高覆盖率但无效的测试
it('should work', () => {
  const result = calculate(1, 2)
  // 没有断言！
})
```

### 2. 不要为了覆盖率而测试

```typescript
// ❌ 无意义的测试
it('should have property', () => {
  const obj = { prop: 'value' }
  expect(obj).toHaveProperty('prop')
})
```

### 3. 关注未覆盖的代码

未覆盖的代码可能隐藏着 Bug。

## 最佳实践

### 1. 定期检查覆盖率

```bash
# 每周运行一次完整覆盖率检查
npm run test:coverage
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

### 3. 在 PR 中检查覆盖率变化

确保新代码不会降低整体覆盖率。

### 4. 关注关键路径

优先保证核心业务逻辑的高覆盖率。

## 相关文档

- [测试指南](./testing-guide.md)
- [测试最佳实践](./testing-best-practices.md)
- [测试命令说明](./testing-commands.md)
- [故障排查指南](./testing-troubleshooting.md)

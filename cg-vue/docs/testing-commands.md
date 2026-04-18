# 测试命令说明

## 概述

本文档详细说明了项目中所有可用的测试命令及其用途。

## 单元测试命令

### 运行所有测试

```bash
npm run test
```

运行所有单元测试，不生成覆盖率报告。

**使用场景**：
- 日常开发时快速验证
- 本地提交前检查

### 运行测试并监听文件变化

```bash
npm run test:watch
```

启动 Vitest 的监听模式，当文件发生变化时自动重新运行相关测试。

**使用场景**：
- 开发过程中实时查看测试结果
- TDD（测试驱动开发）流程

**快捷键**：
- `a` - 运行所有测试
- `f` - 只运行失败的测试
- `p` - 按文件名过滤测试
- `t` - 按测试名过滤测试
- `q` - 退出监听模式

### 运行单元测试

```bash
npm run test:unit
```

运行所有单元测试（与 `npm run test` 相同）。

### 生成测试覆盖率报告

```bash
npm run test:coverage
```

运行所有测试并生成详细的覆盖率报告。

**输出**：
- 控制台输出覆盖率摘要
- `coverage/lcov-report/index.html` - HTML 覆盖率报告
- `coverage/lcov.info` - LCOV 格式报告
- `coverage/coverage-final.json` - JSON 格式报告

**使用场景**：
- 检查测试覆盖率
- CI/CD 流程中的质量检查
- 识别未测试的代码

## 端到端测试命令

### 运行所有 E2E 测试

```bash
npm run test:e2e
```

运行所有 Playwright 端到端测试。

**使用场景**：
- 验证完整的用户流程
- 集成测试
- 发布前的全面测试

### 运行 E2E 测试 UI 模式

```bash
npm run test:e2e:ui
```

启动 Playwright 的 UI 模式，提供可视化的测试运行界面。

**功能**：
- 可视化测试执行过程
- 时间旅行调试
- 查看测试快照
- 逐步执行测试

**使用场景**：
- 调试失败的 E2E 测试
- 开发新的 E2E 测试
- 查看测试执行细节

## 组合测试命令

### 运行所有测试

```bash
npm run test:all
```

依次运行单元测试（含覆盖率）和 E2E 测试。

**使用场景**：
- 发布前的完整测试
- CI/CD 流程中的全面验证

### CI 环境测试

```bash
npm run test:ci
```

为 CI 环境优化的测试命令，生成 JUnit 格式的测试报告。

**输出**：
- `test-results/junit.xml` - JUnit 格式报告
- 覆盖率报告

**使用场景**：
- CI/CD 流水线
- 与 CI 工具集成（如 Jenkins、GitLab CI）

## 特定测试运行

### 运行特定文件的测试

```bash
npm run test -- path/to/test.spec.ts
```

只运行指定文件的测试。

**示例**：
```bash
npm run test -- src/views/user/user-home/__tests__/index.spec.ts
```

### 运行匹配模式的测试

```bash
npm run test -- -t "test name pattern"
```

运行名称匹配指定模式的测试。

**示例**：
```bash
npm run test -- -t "should render"
npm run test -- -t "UserHome"
```

### 运行特定目录的测试

```bash
npm run test -- src/views/admin
```

只运行指定目录下的所有测试。

## 测试报告

### 查看测试报告

```bash
npx vite preview --outDir test-results
```

在浏览器中查看 HTML 格式的测试报告。

**访问地址**：http://localhost:4173

### 查看覆盖率报告

```bash
# Windows
start coverage/lcov-report/index.html

# macOS
open coverage/lcov-report/index.html

# Linux
xdg-open coverage/lcov-report/index.html
```

在浏览器中打开覆盖率报告。

## 调试命令

### 运行测试并显示详细输出

```bash
npm run test -- --reporter=verbose
```

显示每个测试的详细执行信息。

### 运行失败的测试

```bash
npm run test -- --reporter=verbose --bail=1
```

遇到第一个失败就停止测试。

### 更新快照

```bash
npm run test -- -u
```

更新所有快照文件。

**使用场景**：
- 组件 UI 更新后
- 快照测试失败但预期行为已改变

## 性能相关命令

### 运行测试并显示性能信息

```bash
npm run test -- --reporter=verbose --slowTestThreshold=100
```

标记执行时间超过 100ms 的测试为慢测试。

### 并行运行测试

```bash
npm run test -- --threads
```

使用多线程并行运行测试（默认行为）。

### 串行运行测试

```bash
npm run test -- --no-threads
```

单线程串行运行测试。

**使用场景**：
- 测试之间有状态依赖
- 调试并发问题

## E2E 测试特定命令

### 运行特定浏览器的测试

```bash
npm run test:e2e -- --project=chromium
npm run test:e2e -- --project=firefox
npm run test:e2e -- --project=webkit
```

只在指定的浏览器中运行测试。

### 运行特定的 E2E 测试文件

```bash
npm run test:e2e -- e2e/user/auth.spec.ts
```

### 调试 E2E 测试

```bash
npm run test:e2e -- --debug
```

启动调试模式，可以逐步执行测试。

### 生成 E2E 测试代码

```bash
npx playwright codegen
```

启动 Playwright 代码生成器，通过操作浏览器自动生成测试代码。

## 测试环境配置

### 设置测试环境变量

```bash
TEST_ENV=production npm run test
```

在特定的环境变量下运行测试。

### 使用特定的配置文件

```bash
npm run test -- --config=vitest.config.ts
```

## 常用组合命令

### 清理并重新运行测试

```bash
npm run test -- --clearCache && npm run test
```

清除缓存并重新运行测试。

### 运行测试并生成完整报告

```bash
npm run test:coverage && npm run test:e2e
```

生成单元测试覆盖率报告并运行 E2E 测试。

### 检查测试状态

```bash
npm run test -- --passWithNoTests
```

即使没有测试文件也通过（用于 CI）。

## 命令参数说明

### Vitest 常用参数

| 参数 | 说明 |
|------|------|
| `--watch` | 监听模式 |
| `--coverage` | 生成覆盖率报告 |
| `--reporter` | 指定报告格式 |
| `--bail` | 失败时停止 |
| `--threads` | 并行运行 |
| `--no-threads` | 串行运行 |
| `-t` | 按名称过滤测试 |
| `-u` | 更新快照 |
| `--clearCache` | 清除缓存 |

### Playwright 常用参数

| 参数 | 说明 |
|------|------|
| `--ui` | UI 模式 |
| `--debug` | 调试模式 |
| `--project` | 指定浏览器 |
| `--headed` | 有头模式 |
| `--headed=false` | 无头模式 |
| `--workers` | 并发工作进程数 |

## 故障排查命令

### 检查测试配置

```bash
npm run test -- --help
```

显示 Vitest 帮助信息和配置选项。

### 验证测试环境

```bash
node -e "console.log(process.version)"
npm list vitest
npm list @vue/test-utils
```

检查 Node.js 版本和测试依赖。

### 运行单个测试并显示详细日志

```bash
npm run test -- --reporter=verbose path/to/test.spec.ts
```

## 相关文档

- [测试指南](./testing-guide.md)
- [测试最佳实践](./testing-best-practices.md)
- [测试覆盖率说明](./testing-coverage.md)
- [故障排查指南](./testing-troubleshooting.md)

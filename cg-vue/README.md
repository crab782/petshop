# cg-vue

[![Test Status](https://img.shields.io/badge/tests-1643%20tests-brightgreen)](./test-results)
[![Coverage](https://img.shields.io/badge/coverage-2.48%25-red)](./coverage/lcov-report/index.html)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

宠物服务平台前端项目 - 基于 Vue 3 + Vite + TypeScript 构建的三端应用系统

## 项目简介

本项目是一个完整的宠物服务平台，包含三个独立的前端应用：

- **用户端**：面向宠物主人的服务预约、商品购买、宠物管理等功能
- **商家端**：面向商家的店铺管理、服务管理、订单处理等功能
- **平台端**：面向平台管理员的用户管理、商家审核、系统配置等功能

### 技术栈

- **框架**：Vue 3 + TypeScript
- **构建工具**：Vite
- **UI 组件库**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **测试框架**：Vitest + Playwright
- **代码规范**：ESLint + Prettier + Oxlint

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Run Unit Tests with [Vitest](https://vitest.dev/)

```sh
# 运行所有单元测试
npm run test:unit

# 运行测试并监听文件变化
npm run test:watch

# 运行测试并生成覆盖率报告
npm run test:coverage
```

### Run End-to-End Tests with [Playwright](https://playwright.dev)

```sh
# Install browsers for the first run
npx playwright install

# When testing on CI, must build the project first
npm run build

# Runs the end-to-end tests
npm run test:e2e

# Runs the tests in UI mode
npm run test:e2e:ui

# Runs the tests only on Chromium
npm run test:e2e -- --project=chromium

# Runs the tests of a specific file
npm run test:e2e -- tests/example.spec.ts

# Runs the tests in debug mode
npm run test:e2e -- --debug
```

### 测试文档

项目提供了完整的测试文档，帮助开发者编写和维护测试：

- [测试指南](./docs/testing-guide.md) - 测试框架介绍、测试类型、Mock 使用等
- [测试最佳实践](./docs/testing-best-practices.md) - 编写高质量测试的最佳实践
- [测试命令说明](./docs/testing-commands.md) - 所有测试命令的详细说明
- [测试覆盖率说明](./docs/testing-coverage.md) - 覆盖率配置、目标和改进策略
- [故障排查指南](./docs/testing-troubleshooting.md) - 常见测试问题的排查和解决

### 测试覆盖率

当前测试覆盖率：

- **语句覆盖率**：2.48% (243/9768)
- **分支覆盖率**：2.18% (109/4980)
- **函数覆盖率**：0.55% (23/4132)
- **行覆盖率**：2.48% (224/9014)

> 注：覆盖率较低是因为部分测试失败，需要修复测试用例以提高覆盖率

查看详细的覆盖率报告：

```sh
npm run test:coverage
# 然后在浏览器中打开 coverage/lcov-report/index.html
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```

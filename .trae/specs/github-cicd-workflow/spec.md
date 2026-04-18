# GitHub CI/CD 工作流配置规范

## Why
当前项目缺乏自动化持续集成和持续部署（CI/CD）流程，导致代码质量保障依赖人工检查，部署过程繁琐且容易出错。配置 GitHub Actions 工作流可以自动化代码检查、构建验证、测试执行和部署流程，提高开发效率和代码质量。

## What Changes
- 创建前端项目 CI/CD 工作流配置文件（frontend.yml）
- 创建后端项目 CI/CD 工作流配置文件（backend.yml）
- 配置代码检查、构建验证、自动化测试等 CI 步骤
- 配置适当的 CD 部署流程
- **严格遵守约束**：所有配置文件存放于 .github/workflows 目录，不修改任何项目源代码

## Impact
- Affected specs: CI/CD 流程规范
- Affected code: 仅新增 .github/workflows 目录下的工作流配置文件，不修改任何现有代码

## ADDED Requirements

### Requirement: 前端 CI/CD 工作流
系统 SHALL 提供前端项目的 GitHub Actions 工作流配置，包括：
- 触发条件：Push 到 main/develop 分支、Pull Request 事件
- Node.js 环境配置和依赖安装
- 代码检查（ESLint、TypeScript 类型检查）
- 构建验证（Vite 构建）
- 自动化测试（单元测试、E2E 测试）
- 构建产物缓存优化
- 部署流程（可选，根据项目需求）

#### Scenario: 前端代码提交触发 CI
- **WHEN** 开发者推送代码到前端项目或创建 Pull Request
- **THEN** GitHub Actions 自动执行代码检查、构建和测试流程

#### Scenario: 前端构建失败
- **WHEN** 前端构建或测试失败
- **THEN** GitHub Actions 标记工作流失败并通知开发者

### Requirement: 后端 CI/CD 工作流
系统 SHALL 提供后端项目的 GitHub Actions 工作流配置，包括：
- 触发条件：Push 到 main/develop 分支、Pull Request 事件
- Java 环境配置和 Maven 依赖安装
- 代码编译和构建验证
- 自动化测试执行
- 构建产物缓存优化
- 部署流程（可选，根据项目需求）

#### Scenario: 后端代码提交触发 CI
- **WHEN** 开发者推送代码到后端项目或创建 Pull Request
- **THEN** GitHub Actions 自动执行编译、构建和测试流程

#### Scenario: 后端构建失败
- **WHEN** 后端构建或测试失败
- **THEN** GitHub Actions 标记工作流失败并通知开发者

### Requirement: 工作流文件组织
所有工作流配置文件 SHALL 存放于项目根目录下的 `.github/workflows` 目录中，文件命名清晰且符合 GitHub Actions 规范。

#### Scenario: 工作流文件位置
- **WHEN** 查看 GitHub 仓库的 Actions 标签页
- **THEN** 能够看到前端和后端的独立工作流，且配置文件位于 .github/workflows 目录

### Requirement: 约束条件
配置 CI/CD 工作流时 SHALL 严格遵守以下约束：
- 不修改任何项目源代码文件
- 不修改任何配置文件（package.json、pom.xml 等）
- 不修改任何测试文件
- 仅创建 .github/workflows 目录及其下的工作流配置文件

#### Scenario: 验证约束条件
- **WHEN** CI/CD 配置完成后检查项目变更
- **THEN** 仅发现 .github/workflows 目录下的新增文件，无其他文件被修改

## MODIFIED Requirements
无

## REMOVED Requirements
无
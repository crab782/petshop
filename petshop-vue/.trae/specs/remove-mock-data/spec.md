# 移除前端项目Mock数据和服务 - 产品需求文档

## Overview
- **Summary**: 从前端项目（用户端28页、商家端16页、平台端20页）中全面移除所有不必要的Mock后端服务和测试数据，确保应用完全依赖真实后端服务。
- **Purpose**: 消除Mock依赖，避免生产环境中的潜在冲突、性能问题和数据不一致性，确保应用在真实后端环境中正常运行。
- **Target Users**: 开发团队和部署人员

## Goals
- 完全移除所有Mock数据文件和目录
- 移除Mock服务配置和导入代码
- 确保所有API调用指向真实后端服务
- 验证所有页面在移除Mock后能正常工作
- 测试跨三端（用户端、商家端、平台端）的功能完整性

## Non-Goals (Out of Scope)
- 不修改真实后端服务的API结构
- 不删除测试文件和E2E测试
- 不修改业务逻辑和功能实现
- 不影响现有的路由配置和页面结构

## Background & Context
- 项目使用Vite + Vue 3 + TypeScript
- 目前在开发环境中通过`src/main.ts`条件导入Mock服务
- Mock数据存放在`src/mock/`目录，包含用户端、商家端、平台端的模拟数据
- 环境配置中`VITE_USE_MOCK=false`，但Mock服务仍在开发环境中被导入

## Functional Requirements
- **FR-1**: 移除所有Mock数据文件和目录结构
- **FR-2**: 移除Mock服务的导入和配置代码
- **FR-3**: 确保所有API调用正确指向真实后端服务
- **FR-4**: 验证三端所有页面在移除Mock后正常工作

## Non-Functional Requirements
- **NFR-1**: 性能优化 - 移除Mock服务后应减少不必要的内存和CPU消耗
- **NFR-2**: 代码清洁度 - 确保代码库中无Mock相关的冗余代码
- **NFR-3**: 可维护性 - 确保移除Mock后代码结构更清晰，便于后续维护

## Constraints
- **Technical**: 必须确保真实后端服务可用且所有API端点正常工作
- **Business**: 不能影响现有功能的正常运行
- **Dependencies**: 依赖真实后端服务的可用性

## Assumptions
- 真实后端服务已部署并正常运行
- 所有API端点与Mock服务的接口结构一致
- 移除Mock后不会影响前端的业务逻辑

## Acceptance Criteria

### AC-1: Mock目录完全移除
- **Given**: 项目源代码结构
- **When**: 执行Mock移除操作
- **Then**: `src/mock/`目录及其所有子目录和文件被完全删除
- **Verification**: `programmatic`

### AC-2: Mock服务导入代码移除
- **Given**: `src/main.ts`文件
- **When**: 执行代码清理
- **Then**: Mock服务的条件导入代码被完全移除
- **Verification**: `programmatic`

### AC-3: API请求配置正确
- **Given**: `src/api/`目录下的所有API文件
- **When**: 检查API配置
- **Then**: 所有API请求正确指向真实后端服务，无Mock相关配置
- **Verification**: `programmatic`

### AC-4: 三端页面功能正常
- **Given**: 应用启动并访问各端页面
- **When**: 测试用户端、商家端、平台端的所有页面
- **Then**: 所有页面加载正常，API调用成功，功能完整
- **Verification**: `human-judgment`

### AC-5: 构建和部署成功
- **Given**: 执行构建命令
- **When**: 运行`npm run build`
- **Then**: 构建过程无错误，生成的生产版本正常部署
- **Verification**: `programmatic`

## Open Questions
- [ ] 真实后端服务是否已完全部署并测试通过？
- [ ] 是否有特殊的Mock配置在其他文件中需要清理？
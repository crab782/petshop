# 平台端前端Mock服务集成规范

## Why

平台端页面需要使用mock模拟后端接口和测试数据，以便在完全排除后端服务依赖的情况下，独立验证前端页面的功能完整性、交互逻辑正确性和UI展示效果。目前`src/mock/admin/`目录下已有mock文件，但未在`src/mock/index.ts`中导入启用。

## What Changes

- 在`src/mock/index.ts`中导入admin目录下的所有mock文件
- 检查并完善admin mock文件，确保覆盖所有平台端API接口
- 确保mock数据结构与真实后端接口保持一致
- 提供便捷的mock数据管理方式

## Impact

- Affected specs: 平台端所有页面的API调用
- Affected code: 
  - `src/mock/index.ts` - 主入口文件
  - `src/mock/admin/*.js` - 平台端mock文件
  - `src/api/admin.ts` - 平台端API定义

## ADDED Requirements

### Requirement: Mock服务集成
系统应提供完整的平台端mock服务，覆盖所有API接口。

#### Scenario: Mock服务启用
- **WHEN** 开发环境启动时
- **THEN** 自动加载所有平台端mock服务

#### Scenario: API调用拦截
- **WHEN** 前端调用平台端API
- **THEN** mock服务返回模拟数据

### Requirement: Mock数据格式一致性
Mock数据结构应与真实后端接口保持一致。

#### Scenario: 响应格式匹配
- **WHEN** mock服务返回数据
- **THEN** 数据结构与`ApiResponse<T>`格式一致

### Requirement: Mock数据管理
提供便捷的mock数据管理方式。

#### Scenario: 数据生成
- **WHEN** 需要生成测试数据
- **THEN** 使用mockjs工具函数生成随机数据

## MODIFIED Requirements

### Requirement: Mock入口文件
修改`src/mock/index.ts`，添加admin mock文件导入。

## REMOVED Requirements

无移除的需求。

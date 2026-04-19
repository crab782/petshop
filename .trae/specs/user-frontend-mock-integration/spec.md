# 用户端前端页面Mock集成验证与调整 Spec

## Why
用户端mock服务已开发完成，但需要验证前端页面的API调用是否与mock服务完全匹配，确保在完全排除后端服务依赖的情况下，前端页面能够独立运行、展示完整功能和正确交互逻辑。

## What Changes
- 验证所有用户端页面的API调用与mock服务端点的匹配性
- 调整API调用方式，确保与mock服务无缝集成
- 配置适当的mock数据生成规则，确保模拟数据的结构和格式与真实后端接口保持一致
- 提供便捷的mock数据管理方式以便后续测试和调试
- 确保开发环境下mock服务自动启用，生产环境下禁用

## Impact
- Affected specs: user-mock-services, user-frontend-revamp
- Affected code: 
  - cg-vue/src/views/user/**/*.vue (28个用户端页面)
  - cg-vue/src/api/user.ts (API接口定义)
  - cg-vue/src/api/request.ts (请求配置)
  - cg-vue/src/mock/user/**/*.ts (Mock服务)

## ADDED Requirements

### Requirement: API调用验证
The system SHALL ensure all user frontend pages make API calls that match the mock service endpoints.

#### Scenario: API Endpoint Matching
- **WHEN** a user frontend page makes an API call
- **THEN** the mock service intercepts the request and returns appropriate mock data
- **AND** the response format matches the expected API response structure

### Requirement: Mock Data Consistency
The system SHALL provide mock data that matches the structure and format of real backend API responses.

#### Scenario: Data Structure Validation
- **WHEN** mock service returns data
- **THEN** the data structure matches the TypeScript interface definitions
- **AND** all required fields are present and have correct types

### Requirement: Development Environment Configuration
The system SHALL automatically enable mock service in development environment and disable in production.

#### Scenario: Environment-based Mock Activation
- **WHEN** application runs in development mode
- **THEN** mock service is automatically enabled
- **AND** all API calls are intercepted by mock service
- **WHEN** application runs in production mode
- **THEN** mock service is disabled
- **AND** API calls go to real backend server

### Requirement: Mock Data Management
The system SHALL provide convenient mock data management for testing and debugging.

#### Scenario: Data Customization
- **WHEN** developer needs to test specific scenarios
- **THEN** mock data can be easily customized
- **AND** changes to mock data are immediately reflected

## MODIFIED Requirements
None - This is a verification and adjustment task.

## REMOVED Requirements
None - No features are being removed.

# 用户端Mock服务开发 Spec

## Why
用户端28个页面已完成功能开发，但缺乏完整的后端API支持。为了支持前端独立开发、测试和演示，需要开发一套完整的mock模拟后端服务，提供符合业务逻辑的测试数据，确保前端页面能够正常运行和展示。

## What Changes
- 创建用户端mock服务架构，支持API请求拦截和模拟响应
- 为28个用户端页面的所有API接口创建对应的mock端点
- 设计符合业务逻辑的测试数据模型，覆盖用户、商家、服务、商品、订单、预约、评价等核心实体
- 实现测试数据的动态生成和管理机制
- 确保mock服务与前端API调用的无缝集成
- 支持多种场景测试：正常场景、边界情况、异常场景

## Impact
- Affected specs: user-frontend-revamp
- Affected code: 
  - cg-vue/src/api/user.ts (API接口定义)
  - cg-vue/src/mock/ (新增mock服务目录)
  - cg-vue/src/views/user/**/*.vue (28个用户端页面)

## ADDED Requirements

### Requirement: Mock Service Architecture
The system SHALL provide a complete mock service architecture for user frontend that intercepts API requests and returns simulated responses.

#### Scenario: API Request Interception
- **WHEN** frontend makes an API request to user endpoints
- **THEN** the mock service intercepts the request and returns appropriate mock data
- **AND** the response format matches the expected API response structure

### Requirement: User Management Mock
The system SHALL provide mock endpoints for user management operations.

#### Scenario: User Profile Operations
- **WHEN** frontend requests user profile data
- **THEN** mock service returns user information including username, email, phone, avatar
- **AND** supports profile update operations

#### Scenario: Pet Management
- **WHEN** frontend requests pet data
- **THEN** mock service returns pet list with complete information (name, type, breed, age, gender, weight, bodyType, furColor, personality)
- **AND** supports CRUD operations on pets

### Requirement: Merchant & Service Mock
The system SHALL provide mock endpoints for merchant and service operations.

#### Scenario: Merchant List
- **WHEN** frontend requests merchant list
- **THEN** mock service returns paginated merchant data with name, logo, address, rating, service count
- **AND** supports search, filter, and sort operations

#### Scenario: Service Operations
- **WHEN** frontend requests service data
- **THEN** mock service returns service details with name, description, price, duration, merchant info
- **AND** supports service booking and favorites

### Requirement: Product & Shopping Mock
The system SHALL provide mock endpoints for product and shopping operations.

#### Scenario: Product Operations
- **WHEN** frontend requests product data
- **THEN** mock service returns product details with name, description, price, stock, images
- **AND** supports product favorites and reviews

#### Scenario: Shopping Cart
- **WHEN** frontend requests cart operations
- **THEN** mock service manages cart items with add, update, delete operations
- **AND** calculates total amount correctly

#### Scenario: Order Operations
- **WHEN** frontend requests order operations
- **THEN** mock service handles order creation, payment, status updates
- **AND** supports order list with filtering and pagination

### Requirement: Appointment Mock
The system SHALL provide mock endpoints for appointment operations.

#### Scenario: Appointment Management
- **WHEN** frontend requests appointment operations
- **THEN** mock service handles appointment creation, confirmation, cancellation
- **AND** returns appointment list with status filtering

### Requirement: Review & Notification Mock
The system SHALL provide mock endpoints for review and notification operations.

#### Scenario: Review Operations
- **WHEN** frontend requests review operations
- **THEN** mock service handles review creation, update, deletion
- **AND** returns review list with rating and filtering

#### Scenario: Notification Management
- **WHEN** frontend requests notification operations
- **THEN** mock service returns notifications with read/unread status
- **AND** supports batch operations

### Requirement: Test Data Management
The system SHALL provide comprehensive test data management.

#### Scenario: Data Generation
- **WHEN** mock service initializes
- **THEN** generates realistic test data for all entities
- **AND** ensures data relationships are consistent

#### Scenario: Data Scenarios
- **WHEN** testing different scenarios
- **THEN** mock service provides data for normal, boundary, and error cases
- **AND** supports dynamic data configuration

## MODIFIED Requirements
None - This is a new feature addition.

## REMOVED Requirements
None - No features are being removed.

# 用户端API验证与修复 - 产品需求文档

## Why
前端项目28个用户端页面已经定义了85个API端点，虽然之前的spec显示已实现，但需要验证后端项目是否真的包含了所有对应的实现，并修复任何缺失或错误的实现，确保前后端能够正确通信。

## What Changes
- 验证后端项目是否实现了所有85个API端点
- 检查API实现是否与前端定义一致
- 修复缺失或错误的API实现
- 确保所有API端点能够正常工作

## Impact
- Affected specs: user-backend-api, api-integration-analysis
- Affected code:
  - `cg-spring/src/main/java/com/petshop/controller/` - REST Controllers
  - `cg-spring/src/main/java/com/petshop/service/` - Business Logic
  - `cg-spring/src/main/java/com/petshop/repository/` - Data Access

## ADDED Requirements

### Requirement: API端点验证
系统应该验证所有85个API端点是否在后端项目中实现。

#### Scenario: 验证认证API
- **WHEN** 检查认证相关API端点
- **THEN** 确认所有8个认证API端点都已实现
- **AND** 确认实现与前端定义一致

#### Scenario: 验证用户管理API
- **WHEN** 检查用户管理相关API端点
- **THEN** 确认所有用户、宠物、地址等API端点都已实现
- **AND** 确认实现与前端定义一致

#### Scenario: 验证商家和服务API
- **WHEN** 检查商家和服务相关API端点
- **THEN** 确认所有商家、服务、商品API端点都已实现
- **AND** 确认实现与前端定义一致

#### Scenario: 验证订单和预约API
- **WHEN** 检查订单和预约相关API端点
- **THEN** 确认所有订单、预约、购物车API端点都已实现
- **AND** 确认实现与前端定义一致

#### Scenario: 验证其他API
- **WHEN** 检查评价、通知、搜索、公告等API端点
- **THEN** 确认所有相关API端点都已实现
- **AND** 确认实现与前端定义一致

### Requirement: API实现修复
系统应该修复任何缺失或错误的API实现。

#### Scenario: 修复缺失的API端点
- **WHEN** 发现缺失的API端点
- **THEN** 实现对应的Controller方法
- **AND** 实现对应的Service方法
- **AND** 确保返回正确的响应格式

#### Scenario: 修复错误的API实现
- **WHEN** 发现API实现与前端定义不一致
- **THEN** 修改实现使其与前端定义一致
- **AND** 确保返回正确的数据结构

## MODIFIED Requirements
None - 这是新的验证和修复工作。

## REMOVED Requirements
None - 不移除任何功能。

## API端点清单

### 认证API (8个端点)
- POST /api/auth/login
- POST /api/auth/register
- POST /api/auth/logout
- GET /api/auth/userinfo
- PUT /api/auth/userinfo
- PUT /api/auth/password
- POST /api/auth/sendVerifyCode
- POST /api/auth/resetPassword

### 宠物管理API (5个端点)
- GET /api/user/pets
- GET /api/user/pets/{id}
- POST /api/user/pets
- PUT /api/user/pets/{id}
- DELETE /api/user/pets/{id}

### 地址管理API (5个端点)
- GET /api/user/addresses
- POST /api/user/addresses
- PUT /api/user/addresses/{id}
- DELETE /api/user/addresses/{id}
- PUT /api/user/addresses/{id}/default

### 商家和服务API (6个端点)
- GET /api/merchants
- GET /api/merchant/{id}
- GET /api/merchant/{id}/services
- GET /api/merchant/{id}/products
- GET /api/merchant/{id}/reviews
- GET /api/merchant/{id}/available-slots

### 服务API (4个端点)
- GET /api/services
- GET /api/services/{id}
- GET /api/services/search
- GET /api/services/recommended

### 商品和购物车API (12个端点)
- GET /api/products
- GET /api/products/{id}
- GET /api/products/search
- GET /api/products/{id}/reviews
- POST /api/user/cart
- GET /api/user/cart
- PUT /api/user/cart
- DELETE /api/user/cart/{id}
- DELETE /api/user/cart/batch
- POST /api/user/favorites/products
- DELETE /api/user/favorites/products/{id}
- GET /api/user/favorites/products/{id}/check

### 订单管理API (12个端点)
- GET /api/user/orders
- GET /api/user/orders/{id}
- POST /api/user/orders
- POST /api/user/orders/preview
- POST /api/user/orders/{id}/pay
- GET /api/user/orders/{id}/pay/status
- PUT /api/user/orders/{id}/cancel
- POST /api/user/orders/{id}/refund
- PUT /api/user/orders/{id}/confirm
- DELETE /api/user/orders/{id}
- PUT /api/user/orders/batch-cancel
- DELETE /api/user/orders/batch-delete

### 预约API (5个端点)
- GET /api/user/appointments
- GET /api/user/appointments/{id}
- POST /api/user/appointments
- PUT /api/user/appointments/{id}/cancel
- GET /api/user/appointments/stats

### 评价API (5个端点)
- POST /api/user/reviews
- GET /api/user/reviews
- PUT /api/user/reviews/{id}
- DELETE /api/user/reviews/{id}

### 通知API (7个端点)
- GET /api/user/notifications
- PUT /api/user/notifications/{id}/read
- PUT /api/user/notifications/read-all
- PUT /api/user/notifications/batch-read
- DELETE /api/user/notifications/{id}
- DELETE /api/user/notifications/batch
- GET /api/user/notifications/unread-count

### 搜索API (5个端点)
- GET /api/search/suggestions
- GET /api/search/hot-keywords
- POST /api/user/search-history
- GET /api/user/search-history
- DELETE /api/user/search-history

### 公告API (2个端点)
- GET /api/announcements
- GET /api/announcements/{id}

### 首页统计API (2个端点)
- GET /api/user/home/stats
- GET /api/user/home/activities

### 收藏管理API (6个端点)
- GET /api/user/favorites
- POST /api/user/favorites
- DELETE /api/user/favorites/{id}
- GET /api/user/favorites/services
- POST /api/user/favorites/services
- DELETE /api/user/favorites/services/{id}

### 用户服务API (1个端点)
- GET /api/user/services

**总计: 85个API端点**

# 修复 Page 类型混用问题 - 产品需求文档

## Overview
- **Summary**: 修复从 Hibernate 迁移到 MyBatis-Plus 后出现的 Page 类型混用问题，统一使用 MyBatis-Plus 的 Page 类替代 Spring Data 的 Page 类。
- **Purpose**: 消除代码中的 Page 类型转换冗余，提高代码可读性和性能，避免类型不匹配导致的潜在问题。
- **Target Users**: 开发人员和系统维护人员

## Goals
- 统一所有 Service 层方法的返回类型为 MyBatis-Plus 的 Page 类
- 修改对应的 Controller 层调用，适应新的返回类型
- 确保分页功能在修改后正常工作
- 消除冗余的 Page 类型转换代码

## Non-Goals (Out of Scope)
- 不修改数据库结构
- 不修改业务逻辑
- 不影响其他功能模块

## Background & Context
在从 Hibernate 迁移到 MyBatis-Plus 的过程中，部分 Service 方法仍然返回 Spring Data 的 `org.springframework.data.domain.Page`，但内部使用 MyBatis-Plus 的 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`。这种混用导致了大量的类型转换代码，增加了代码复杂度，也可能导致潜在的类型不匹配问题。

## Functional Requirements
- **FR-1**: 统一 Service 层返回类型 - 所有返回分页数据的 Service 方法应返回 MyBatis-Plus 的 Page 类
- **FR-2**: 更新 Controller 调用 - 所有调用这些 Service 方法的 Controller 应适配新的返回类型
- **FR-3**: 保持分页功能 - 确保修改后分页功能正常工作，包括页码、每页大小、总数等信息

## Non-Functional Requirements
- **NFR-1**: 代码可读性 - 消除冗余的类型转换代码，提高代码可读性
- **NFR-2**: 性能 - 减少不必要的类型转换，提高性能
- **NFR-3**: 兼容性 - 确保修改后系统功能不受影响

## Constraints
- **Technical**: 必须使用 MyBatis-Plus 的 Page 类，不能使用 Spring Data 的 Page 类
- **Dependencies**: 依赖 MyBatis-Plus 框架

## Assumptions
- MyBatis-Plus 的 Page 类提供了与 Spring Data Page 类相似的功能
- Controller 层能够适应 MyBatis-Plus Page 类的返回类型

## Acceptance Criteria

### AC-1: Service 层返回类型统一
- **Given**: Service 层方法返回分页数据
- **When**: 检查方法签名和实现
- **Then**: 所有方法应返回 `com.baomidou.mybatisplus.extension.plugins.pagination.Page` 类型
- **Verification**: `programmatic`

### AC-2: 消除类型转换代码
- **Given**: Service 层方法实现
- **When**: 检查方法体
- **Then**: 不应包含 Spring Data Page 与 MyBatis-Plus Page 之间的类型转换代码
- **Verification**: `programmatic`

### AC-3: Controller 调用适配
- **Given**: Controller 调用 Service 方法
- **When**: 检查 Controller 代码
- **Then**: 应正确处理 MyBatis-Plus Page 类型的返回值
- **Verification**: `programmatic`

### AC-4: 分页功能正常
- **Given**: 系统运行
- **When**: 测试分页功能
- **Then**: 分页查询应正常工作，包括页码、每页大小、总数等信息
- **Verification**: `programmatic`

## Open Questions
- 哪些 Controller 方法需要修改以适应新的返回类型？
- 是否需要修改前端代码以适应新的 Page 对象结构？

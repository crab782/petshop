# 用户端登录跳转问题分析 - 产品需求文档

## Overview
- **Summary**: 分析用户端登录后仍跳转回登录页的问题，重点排查登录认证流程、会话管理、权限验证及API响应处理逻辑。
- **Purpose**: 找出导致登录后跳转回登录页的根本原因并提供解决方案。
- **Target Users**: 系统开发人员、测试人员

## Goals
- 分析登录请求返回200 OK但响应为空的问题
- 分析首页统计请求返回200 OK但响应为空的问题
- 分析购物车请求返回401 Unauthorized且响应为空的问题
- 分析活动列表请求响应为空的问题
- 找出导致登录后跳转回登录页的根本原因
- 提供完整的解决方案

## Non-Goals (Out of Scope)
- 不修改前端代码逻辑
- 不重新设计认证架构
- 不涉及其他角色（商家、管理员）的认证问题

## Background & Context
- **现象**: 用户端登录后仍出现跳转回登录页的问题
- **网络请求分析**: 
  1. 登录请求：POST /api/auth/login，200 OK，响应为空
  2. 首页统计请求：GET /api/user/home/stats，200 OK，响应为空
  3. 购物车请求：GET /api/user/cart，401 Unauthorized，响应为空
  4. 活动列表请求：GET /api/user/home/activities?limit=5，响应为空

## Functional Requirements
- **FR-1**: 登录请求应返回有效的JWT token和用户信息
- **FR-2**: 认证成功后，受保护的API请求应返回正确的业务数据
- **FR-3**: 认证失败时，应返回明确的401错误信息
- **FR-4**: 所有API响应应符合统一的响应格式

## Non-Functional Requirements
- **NFR-1**: API响应时间应在合理范围内
- **NFR-2**: 认证流程应安全可靠
- **NFR-3**: 错误处理应友好且一致

## Constraints
- **Technical**: Spring Boot 3.2.0, Spring Security + JWT, MyBatis-Plus 3.5.5
- **Dependencies**: 现有认证架构和API结构

## Assumptions
- 前端登录逻辑正确
- 数据库连接正常
- 服务器运行状态良好

## Acceptance Criteria

### AC-1: 登录请求返回有效响应
- **Given**: 用户提交有效的登录凭证
- **When**: 服务器处理登录请求
- **Then**: 应返回200 OK，包含JWT token和用户信息
- **Verification**: `programmatic`

### AC-2: 认证成功后API请求正常
- **Given**: 用户已登录并持有有效JWT token
- **When**: 请求受保护的API端点
- **Then**: 应返回200 OK，包含正确的业务数据
- **Verification**: `programmatic`

### AC-3: 认证失败返回明确错误
- **Given**: 用户未登录或token无效
- **When**: 请求受保护的API端点
- **Then**: 应返回401 Unauthorized，包含错误信息
- **Verification**: `programmatic`

### AC-4: 所有API响应格式一致
- **Given**: 任何API请求
- **When**: 服务器处理请求
- **Then**: 应返回符合统一格式的响应
- **Verification**: `programmatic`

## Open Questions
- [ ] 登录请求的响应为什么为空？
- [ ] 为什么认证成功后仍返回401错误？
- [ ] API响应处理逻辑是否存在问题？
- [ ] JWT token生成和验证是否正常？
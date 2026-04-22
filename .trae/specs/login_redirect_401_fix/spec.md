# 登录后401跳转问题修复 - 产品需求文档

## Overview
- **Summary**: 修复用户登录后跳转到用户首页，然后立即跳转回登录页的问题，解决API请求返回401 Unauthorized的问题
- **Purpose**: 确保用户登录后能正常访问用户端页面和接口，提升用户体验
- **Target Users**: 平台用户（使用用户端的普通用户）

## Goals
- 修复用户登录后跳转到用户首页，然后立即跳转回登录页的问题
- 确保用户登录后API请求能正确传递token
- 确保用户端接口能正常访问，返回200 OK
- 保持商家端功能不受影响

## Non-Goals (Out of Scope)
- 不修改商家端的登录和权限逻辑
- 不修改平台端的功能
- 不涉及数据库结构变更
- 不涉及第三方API集成

## Background & Context
- 前端技术栈：Vue 3 + TypeScript、Pinia状态管理、Vue Router路由、Axios请求拦截器
- 后端技术栈：Spring Boot、Spring Security、JWT认证
- 问题现象：用户使用13412340002和123456登录用户端后，会先跳转到用户首页，然后立即跳转回登录页，API请求返回401 Unauthorized
- 当前状态：已初步分析，需要进一步修复

## Functional Requirements
- **FR-1**: 修复前端登录逻辑，确保token正确保存
- **FR-2**: 修复前端API请求拦截器，确保正确传递用户token
- **FR-3**: 确保用户端接口能正常访问，返回200 OK
- **FR-4**: 保持商家端登录和接口访问功能正常

## Non-Functional Requirements
- **NFR-1**: 性能：登录跳转时间不超过2秒
- **NFR-2**: 可靠性：登录成功率达到100%
- **NFR-3**: 兼容性：修复后不影响其他端的功能

## Constraints
- **Technical**: 保持现有技术栈不变，仅修改相关配置和代码
- **Business**: 尽快修复，确保用户体验
- **Dependencies**: 依赖现有的后端API和前端架构

## Assumptions
- 后端API本身功能正常，问题出在前端的token传递
- 用户账号13412340002是有效的用户账号
- 后端权限控制逻辑正确，问题出在前端的认证信息传递

## Acceptance Criteria

### AC-1: 用户端登录后不跳回登录页
- **Given**: 用户在用户端登录页面输入正确的用户名和密码
- **When**: 点击登录按钮并登录成功
- **Then**: 页面跳转到用户首页（/user/home），且不再跳回登录页
- **Verification**: `human-judgment`

### AC-2: 用户端接口正常访问
- **Given**: 用户已成功登录用户端
- **When**: 访问用户端相关接口（如/user/home/stats）
- **Then**: 接口返回200 OK，且返回正确的数据
- **Verification**: `programmatic`

### AC-3: 前端token传递正确
- **Given**: 用户已成功登录用户端
- **When**: 发起API请求
- **Then**: 请求头中正确携带用户token
- **Verification**: `programmatic`

### AC-4: 商家端功能不受影响
- **Given**: 商家在商家端登录页面输入正确的用户名和密码
- **When**: 点击登录按钮并登录成功
- **Then**: 页面正常跳转到商家首页，且商家端接口能正常访问
- **Verification**: `human-judgment`

## Open Questions
- [ ] 前端登录逻辑是否正确保存token？
- [ ] 前端请求拦截器是否正确传递token？
- [ ] 后端token验证逻辑是否正确？
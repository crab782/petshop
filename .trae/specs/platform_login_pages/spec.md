# 平台登录页和注册页添加 - 产品需求文档

## Overview
- **Summary**: 在前端项目中添加专门的平台登录页和注册页，类似商家登录页一样，确保http://localhost:5173/login只提供用户登录功能
- **Purpose**: 明确区分不同角色的登录入口，提升用户体验，避免登录入口混淆
- **Target Users**: 平台管理员、商家、普通用户

## Goals
- 添加专门的平台登录页，路径为`/admin/login`
- 添加专门的平台注册页，路径为`/admin/register`
- 保持现有的用户登录页（`/login`）和商家登录页（`/merchant/login`）不变
- 确保各角色登录后能正确跳转到对应的首页
- 保持路由配置和权限控制正确

## Non-Goals (Out of Scope)
- 不修改现有的用户登录和商家登录功能
- 不修改后端API接口
- 不涉及数据库结构变更
- 不修改现有的平台端功能

## Background & Context
- 前端技术栈：Vue 3 + TypeScript、Pinia状态管理、Vue Router路由、Element Plus组件库
- 现有登录页面：
  - 用户登录页：`/login`
  - 商家登录页：`/merchant/login`
- 需要添加：
  - 平台登录页：`/admin/login`
  - 平台注册页：`/admin/register`

## Functional Requirements
- **FR-1**: 添加平台登录页，路径为`/admin/login`
- **FR-2**: 添加平台注册页，路径为`/admin/register`
- **FR-3**: 修改路由配置，确保各角色登录页有独立的路径
- **FR-4**: 确保平台登录成功后跳转到平台首页（`/admin/dashboard`）
- **FR-5**: 保持现有的用户登录和商家登录功能不变

## Non-Functional Requirements
- **NFR-1**: 页面风格与现有登录页面保持一致
- **NFR-2**: 表单验证逻辑与现有登录页面保持一致
- **NFR-3**: 响应式设计，适配不同设备
- **NFR-4**: 登录状态管理与现有逻辑保持一致

## Constraints
- **Technical**: 保持现有技术栈不变，仅添加新页面和修改路由配置
- **Business**: 确保各角色登录入口清晰明确
- **Dependencies**: 依赖现有的后端API和前端架构

## Assumptions
- 后端API已支持平台管理员的登录和注册功能
- 现有的登录状态管理逻辑正确
- 现有的路由守卫逻辑正确

## Acceptance Criteria

### AC-1: 平台登录页访问
- **Given**: 用户访问`/admin/login`
- **When**: 页面加载完成
- **Then**: 显示平台登录页面，包含用户名、密码输入框和登录按钮
- **Verification**: `human-judgment`

### AC-2: 平台注册页访问
- **Given**: 用户访问`/admin/register`
- **When**: 页面加载完成
- **Then**: 显示平台注册页面，包含用户名、密码、邮箱等输入框和注册按钮
- **Verification**: `human-judgment`

### AC-3: 平台登录功能
- **Given**: 用户在平台登录页输入正确的管理员账号和密码
- **When**: 点击登录按钮
- **Then**: 登录成功，跳转到平台首页（`/admin/dashboard`）
- **Verification**: `human-judgment`

### AC-4: 用户登录功能不变
- **Given**: 用户访问`/login`
- **When**: 输入正确的用户账号和密码并登录
- **Then**: 登录成功，跳转到用户首页（`/user/home`）
- **Verification**: `human-judgment`

### AC-5: 商家登录功能不变
- **Given**: 用户访问`/merchant/login`
- **When**: 输入正确的商家账号和密码并登录
- **Then**: 登录成功，跳转到商家首页（`/merchant/home`）
- **Verification**: `human-judgment`

## Open Questions
- [ ] 平台管理员的登录API接口是否与用户和商家相同？
- [ ] 平台管理员的注册逻辑是否与用户和商家相同？
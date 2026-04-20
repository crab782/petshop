# 修复手机号登录字段映射问题 - 产品需求文档

## Why
当前前端登录页面发送的请求体包含 `phone` 字段，但后端 `LoginRequest` DTO 只有 `username` 字段，没有 `phone` 字段。Spring 无法将 JSON 中的 `phone` 绑定到 `username`，导致 `request.getUsername()` 为 null，返回 "Username is required" 错误。需要修复字段映射，使后端能够正确接收并处理包含 `phone` 字段的登录请求。

## What Changes
- 修改 `LoginRequest` DTO，添加 `phone` 字段，并调整字段验证逻辑
- 修改 `AuthApiController.login()` 方法，支持从 `phone` 字段读取登录凭证
- 修改 `AuthService.login()` 方法，适配新的字段映射
- 保持密码验证逻辑不变
- 确保修改后不会影响其他使用用户名登录的功能场景（通过字段兼容性处理）

## Impact
- Affected specs: 用户认证、手机号登录
- Affected code: `LoginRequest.java`, `AuthApiController.java`, `AuthService.java`

## ADDED Requirements
### Requirement: 手机号字段支持
The system SHALL accept `phone` field in login requests.

#### Scenario: 使用手机号登录
- **WHEN** 用户发送登录请求，请求体包含 `phone` 和 `password` 字段
- **THEN** 系统应正确解析 `phone` 字段并使用手机号进行身份验证

#### Scenario: 向后兼容
- **WHEN** 用户发送登录请求，请求体包含 `username` 和 `password` 字段（且 `username` 是手机号格式）
- **THEN** 系统应继续支持该登录方式

## MODIFIED Requirements
### Requirement: 登录接口参数验证
修改 `AuthApiController.login()` 方法的参数验证逻辑：
- 允许使用 `phone` 字段作为登录凭证
- 当 `phone` 字段存在时，优先使用 `phone` 进行验证
- 当 `phone` 字段不存在但 `username` 存在时，回退到使用 `username`
- 错误提示信息应准确反映字段要求（"Phone number is required"）

### Requirement: LoginRequest DTO
修改 `LoginRequest` 类：
- 添加 `phone` 字段
- 保留 `username` 字段以保持向后兼容
- 提供获取有效登录标识的方法（优先返回 `phone`，否则返回 `username`）

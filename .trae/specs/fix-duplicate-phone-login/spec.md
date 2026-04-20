# 修复重复手机号导致的登录失败 - 产品需求文档

## Why
用户登录时返回 401 错误，错误信息为 "Expected one result (or null) to be returned by selectOne(), but found: 2"。这是因为数据库中存在多个用户的手机号都是 `13412340001`，导致 `selectOne()` 方法抛出异常。需要修复数据库中的重复数据，并添加唯一索引约束防止未来出现重复。

## What Changes
- 清理数据库中重复的手机号记录（保留最早注册的用户）
- 在 `user` 表的 `phone` 字段添加唯一索引约束
- 修改 `AuthService.login()` 方法，当发现多条记录时返回更友好的错误信息
- 修改 `AuthService.register()` 方法，在注册前检查手机号是否已存在

## Impact
- Affected specs: 用户认证、手机号登录、用户注册
- Affected code: `AuthService.java`, 数据库 schema

## ADDED Requirements
### Requirement: 数据库唯一索引
The system SHALL prevent duplicate phone numbers in the user table.

#### Scenario: 数据库约束
- **WHEN** 尝试插入重复手机号时
- **THEN** 数据库应拒绝该操作

### Requirement: 友好的错误提示
The system SHALL provide user-friendly error message when duplicate phone is detected.

#### Scenario: 登录时发现重复手机号
- **WHEN** 登录时发现多个用户有相同的手机号
- **THEN** 系统应返回 "系统数据异常，请联系管理员" 而不是暴露内部错误

## MODIFIED Requirements
### Requirement: 登录逻辑健壮性
修改 `AuthService.login()` 方法：
- 使用 `selectList` 替代 `selectOne` 查询用户
- 当返回多条记录时，记录错误日志并返回友好的错误信息
- 当返回一条记录时，正常进行密码验证

### Requirement: 注册逻辑增强
修改 `AuthService.register()` 方法：
- 在插入用户前，再次检查手机号是否已存在（双重检查）
- 捕获数据库唯一索引冲突异常，返回 "手机号已被注册"

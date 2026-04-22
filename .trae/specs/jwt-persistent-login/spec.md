# 登录状态永久化 Spec

## Why
当前JWT令牌配置了24小时过期时间（jwt.expiration=86400000），导致用户、商家和管理员登录状态会在一天后自动失效。用户期望在长时间使用平台时不需要频繁重新登录，特别是管理员用户需要长期保持登录状态以进行持续的系统管理。

## What Changes
- 修改JWT令牌生成逻辑，移除过期时间设置
- 修改JWT令牌验证逻辑，移除过期检查
- 确保三种角色（用户、商家、管理员）的登录状态都永久有效
- 保留其他安全验证机制（签名验证、格式验证等）
- 验证修改不会影响系统其他功能

## Impact
- 受影响的功能：用户登录、商家登录、管理员登录、会话管理
- 受影响的代码：
  - `src/main/java/com/petshop/security/JwtUtils.java` - 移除令牌过期设置
  - `src/main/resources/application.properties` - 可移除jwt.expiration配置
  - 相关测试代码可能需要更新

## Technical Implementation

### 当前实现分析
1. `application.properties` 配置了 `jwt.expiration=86400000`（24小时）
2. `JwtUtils.java` 在生成令牌时调用 `.expiration(new Date(...))` 设置过期时间
3. `validateJwtToken` 方法捕获 `ExpiredJwtException` 并返回false

### 修改方案
1. **JwtUtils.generateJwtToken**: 移除 `.expiration()` 调用
2. **JwtUtils.generateTokenFromUsername**: 移除 `.expiration()` 调用
3. **JwtUtils.validateJwtToken**: 移除 `ExpiredJwtException` 处理逻辑
4. **application.properties**: 注释掉 `jwt.expiration` 配置（或保留供其他用途）

### 保留的安全验证
- 签名验证 (Signature validation)
- 格式验证 (MalformedJwtException)
- 支持验证 (UnsupportedJwtException)
- 空参数验证 (IllegalArgumentException)

## MODIFIED Requirements

### Requirement: JWT令牌生成
**原要求**: 生成带24小时过期时间的JWT令牌
**新要求**: 生成永久有效的JWT令牌（不设置过期时间）

#### Scenario: 用户登录
- **WHEN** 用户输入正确的用户名和密码登录
- **THEN** 系统生成不带过期时间的JWT令牌返回给客户端

#### Scenario: 令牌验证
- **WHEN** 前端携带JWT令牌发送请求
- **THEN** 后端验证令牌签名和格式有效即返回true，不检查过期时间

### Requirement: 登录状态保持
**新要求**: 用户、商家、管理员登录后，令牌永不过期，除非用户主动登出或token被撤销。

## Implementation Steps

### Step 1: 修改JwtUtils.java
- 修改 `generateJwtToken` 方法：移除 `.expiration()` 调用
- 修改 `generateTokenFromUsername` 方法：移除 `.expiration()` 调用
- 修改 `validateJwtToken` 方法：移除 `ExpiredJwtException` catch块

### Step 2: 更新配置文件
- 在 `application.properties` 中注释掉 `jwt.expiration` 行

### Step 3: 验证和测试
- 确认用户登录后token不包含过期时间
- 确认商家登录后token不包含过期时间
- 确认管理员登录后token不包含过期时间
- 确认旧的过期token仍然可以验证通过（如果不包含过期检查）
- 确认无效token仍然被正确拒绝

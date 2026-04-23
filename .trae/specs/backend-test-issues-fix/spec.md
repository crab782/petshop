# 后端测试问题修复 Spec

## Why
在后端测试实施过程中，发现了认证系统问题和商家API异常，导致部分测试失败。需要修复这些问题以提高测试通过率和系统稳定性。

## What Changes
- 修复认证系统问题（401未授权错误）
- 修复商家API异常（500内部服务器错误）
- 重新运行测试验证修复效果

## Impact
- 受影响的规范：后端测试实施方案
- 受影响的代码：
  - 认证相关代码（AuthService、SecurityConfig、JwtUtils）
  - 商家API相关代码（MerchantApiController、MerchantService）
  - 测试代码（集成测试、接口测试）

## ADDED Requirements

### Requirement: 认证系统修复
系统应修复认证系统问题：

#### Scenario: 用户认证
- **WHEN** 用户使用正确的凭证登录
- **THEN** 应返回有效的JWT Token
- **AND** 应能够访问受保护的资源

#### Scenario: 商家认证
- **WHEN** 商家使用正确的凭证登录
- **THEN** 应返回有效的JWT Token
- **AND** 应能够访问商家端API

#### Scenario: 管理员认证
- **WHEN** 管理员使用正确的凭证登录
- **THEN** 应返回有效的JWT Token
- **AND** 应能够访问管理员API

### Requirement: 商家API修复
系统应修复商家API异常：

#### Scenario: 服务管理
- **WHEN** 商家请求服务管理接口
- **THEN** 应返回正确的响应
- **AND** 不应出现500错误

#### Scenario: 商品管理
- **WHEN** 商家请求商品管理接口
- **THEN** 应返回正确的响应
- **AND** 不应出现500错误

#### Scenario: 订单处理
- **WHEN** 商家请求订单处理接口
- **THEN** 应返回正确的响应
- **AND** 不应出现500错误

### Requirement: 测试验证
系统应重新运行测试验证修复效果：

#### Scenario: 测试通过率
- **WHEN** 重新运行所有测试
- **THEN** 测试通过率应达到95%以上
- **AND** 无严重错误

## MODIFIED Requirements
无修改的需求。

## REMOVED Requirements
无移除的需求。

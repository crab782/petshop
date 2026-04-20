# 全面API审计规格说明

## Why
需要对前端项目中所有页面（用户端28个、商家端20个、平台端20个）使用的真实后端API进行全面详细检查，确保API调用的准确性和完整性，为后续开发和维护提供完整的API文档。

## What Changes
- **BREAKING**: 无破坏性变更，仅进行检查和记录
- 检查所有页面的API调用，包括以"api"开头和不以"api"开头的接口
- 记录每个API的完整路径、请求方法、请求参数、响应格式
- 标记每个API的当前使用状态（正常/异常/未使用）

## Impact
- 提供完整的API调用清单
- 发现潜在的API调用问题
- 为后续开发提供参考文档

## ADDED Requirements
### Requirement: API全面审计
The system SHALL provide a comprehensive audit of all API calls used in the frontend project.

#### Scenario: 用户端页面审计
- **WHEN** 检查用户端28个页面
- **THEN** 记录所有API调用的详细信息

#### Scenario: 商家端页面审计
- **WHEN** 检查商家端20个页面
- **THEN** 记录所有API调用的详细信息

#### Scenario: 平台端页面审计
- **WHEN** 检查平台端20个页面
- **THEN** 记录所有API调用的详细信息

## MODIFIED Requirements
无

## REMOVED Requirements
无

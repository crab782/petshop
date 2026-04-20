# 数据库表结构更新 - 产品需求文档

## Overview
- **Summary**: 执行数据库表结构更新，添加评分字段、订单号字段、统计字段，优化索引，并添加其他必要的字段。
- **Purpose**: 提升数据库性能和功能完整性，支持业务逻辑需求。
- **Target Users**: 后端开发人员、数据库管理员

## Goals
- 添加商家、服务、商品的评分字段
- 添加预约单号和订单号字段
- 添加销量和预约次数字段
- 优化高频查询字段的索引
- 添加评价图片字段
- 添加商家审核拒绝原因字段
- 完善管理员角色关联表的外键约束
- 完善活动管理字段

## Non-Goals (Out of Scope)
- 修改现有数据
- 优化数据库性能（除索引外）
- 实现数据迁移

## Background & Context
- 数据库使用MySQL 8.0+
- 项目是一个宠物管理系统，包含商家端、用户端和平台端三个子系统
- 现有数据库表结构需要优化以支持业务需求

## Functional Requirements
- **FR-1**: 添加评分字段到merchant、service和product表
- **FR-2**: 添加订单号字段到appointment表
- **FR-3**: 添加统计字段到product和service表
- **FR-4**: 优化高频查询字段的索引
- **FR-5**: 添加评价图片字段到review表
- **FR-6**: 添加商家审核拒绝原因字段到merchant表
- **FR-7**: 完善管理员角色关联表的外键约束
- **FR-8**: 完善活动管理字段到activity表

## Non-Functional Requirements
- **NFR-1**: 脚本执行过程中不应影响现有数据
- **NFR-2**: 索引优化不应负面影响写入性能
- **NFR-3**: 脚本应包含错误处理和验证步骤

## Constraints
- **Technical**: 基于现有的数据库表结构进行修改
- **Business**: 确保修改后的表结构支持所有业务需求
- **Dependencies**: 依赖MySQL数据库和现有的表结构

## Assumptions
- 数据库连接正常
- 现有表结构与之前分析的一致
- 脚本执行权限足够

## Acceptance Criteria

### AC-1: 评分字段添加完成
- **Given**: 数据库表结构
- **When**: 执行SQL脚本
- **Then**: merchant、service和product表都添加了rating字段
- **Verification**: `programmatic`

### AC-2: 订单号字段添加完成
- **Given**: 数据库表结构
- **When**: 执行SQL脚本
- **Then**: appointment表添加了order_no字段
- **Verification**: `programmatic`

### AC-3: 统计字段添加完成
- **Given**: 数据库表结构
- **When**: 执行SQL脚本
- **Then**: product表添加了sales_volume字段，service表添加了reservation_count字段
- **Verification**: `programmatic`

### AC-4: 索引优化完成
- **Given**: 数据库表结构
- **When**: 执行SQL脚本
- **Then**: 为高频查询字段创建了适当的索引
- **Verification**: `programmatic`

### AC-5: 其他字段添加完成
- **Given**: 数据库表结构
- **When**: 执行SQL脚本
- **Then**: review表添加了image字段，merchant表添加了rejection_reason字段，activity表添加了必要的字段
- **Verification**: `programmatic`

### AC-6: 外键约束添加完成
- **Given**: 数据库表结构
- **When**: 执行SQL脚本
- **Then**: role_permission表添加了适当的外键约束
- **Verification**: `programmatic`

### AC-7: 脚本执行成功
- **Given**: 数据库连接正常
- **When**: 执行SQL脚本
- **Then**: 脚本执行成功，没有错误
- **Verification**: `programmatic`

## Open Questions
- [ ] 执行脚本后是否需要更新相关的后端代码？
- [ ] 是否需要为新添加的字段设置默认值或进行数据初始化？
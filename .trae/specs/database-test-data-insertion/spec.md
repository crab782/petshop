# 数据库测试数据插入规范

## Why
前端项目包含大量 mock.js 测试数据（用户端28个页面、商家端20个页面、平台端20个页面），但这些数据仅存在于前端内存中，未持久化到数据库。需要将这些测试数据插入到 MySQL 数据库中，以便进行完整的端到端测试和演示。

## What Changes
- 分析前端项目所有 mock.js 文件中的测试数据
- 生成与数据库表结构对应的 SQL INSERT 语句
- 使用 MySQL 命令行工具连接到本地数据库（cg）
- 执行 SQL 脚本插入测试数据
- 验证数据插入的完整性和正确性

## Impact
- Affected specs: 数据库测试数据
- Affected code: MySQL 数据库 cg 中的所有表

## ADDED Requirements

### Requirement: Mock 数据分析
系统 SHALL 分析前端项目中所有 mock.js 文件的测试数据结构，包括：
- 用户端 mock 数据（28个页面）
- 商家端 mock 数据（20个页面）
- 平台端 mock 数据（20个页面）
- 数据字段与数据库表字段的映射关系

#### Scenario: 分析 Mock 数据
- **WHEN** 扫描 cg-vue/src/mock 目录下的所有 .js 文件
- **THEN** 能够提取所有测试数据并识别对应的数据表

### Requirement: SQL 脚本生成
系统 SHALL 根据分析结果生成 SQL INSERT 语句，包括：
- 用户表（user）测试数据
- 商家表（merchant）测试数据
- 管理员表（admin）测试数据
- 宠物表（pet）测试数据
- 服务表（service）测试数据
- 商品表（product）测试数据
- 预约订单表（appointment）测试数据
- 商品订单表（product_order）测试数据
- 评价表（review）测试数据
- 其他相关表测试数据

#### Scenario: 生成 SQL 脚本
- **WHEN** Mock 数据分析完成
- **THEN** 能够生成完整的 SQL INSERT 脚本

### Requirement: 数据库连接
系统 SHALL 使用 MySQL 命令行工具连接到本地数据库：
- 连接命令：`mysql -uroot -p123456`
- 目标数据库：cg
- 主机：localhost
- 端口：3306

#### Scenario: 连接数据库
- **WHEN** 执行 MySQL 连接命令
- **THEN** 能够成功连接到 cg 数据库

### Requirement: 数据插入
系统 SHALL 执行 SQL 脚本将测试数据插入数据库：
- 清空现有测试数据（可选）
- 插入新的测试数据
- 确保数据量充足（每个表至少10-20条记录）
- 确保数据关联关系正确

#### Scenario: 插入测试数据
- **WHEN** 执行 SQL INSERT 脚本
- **THEN** 所有测试数据成功插入到对应的数据表中

### Requirement: 数据验证
系统 SHALL 验证数据插入的完整性和正确性：
- 检查每个表的记录数
- 检查外键关联关系
- 检查数据字段类型和格式

#### Scenario: 验证数据
- **WHEN** 数据插入完成
- **THEN** 能够确认所有数据正确插入且关联关系正确

## MODIFIED Requirements
无

## REMOVED Requirements
无
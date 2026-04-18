# 宠物管理系统 - 数据库与实体类一致性检查

## Overview
- **Summary**: 检查宠物管理系统的数据库表结构与后端实体类的一致性，确保实体类与数据库表结构完全对应。
- **Purpose**: 确保后端实体类与数据库表结构一致，避免因结构不匹配导致的运行时错误。
- **Target Users**: 后端开发人员、系统维护人员。

## Goals
- 验证实体类与数据库表结构的一致性
- 识别并记录实体类与数据库表结构的差异
- 提供修改建议，确保实体类与数据库表结构完全对应

## Non-Goals (Out of Scope)
- 修改数据库表结构
- 修改后端业务逻辑
- 优化数据库性能

## Background & Context
- 项目使用 MySQL 数据库，数据库名称为 `cg`
- 数据库连接配置为 `mysql -uroot -p123456`
- 后端使用 Spring Boot + JPA 框架
- 实体类位于 `com.petshop.entity` 包下

## Functional Requirements
- **FR-1**: 实体类应与数据库表结构一一对应，包括表名、字段名、字段类型、约束等
- **FR-2**: 实体类应正确使用 JPA 注解，包括 @Entity、@Table、@Column、@Id、@GeneratedValue 等
- **FR-3**: 实体类应正确处理表之间的关联关系，包括 @ManyToOne、@OneToMany 等

## Non-Functional Requirements
- **NFR-1**: 实体类命名应符合 Java 命名规范
- **NFR-2**: 数据库表命名应符合 SQL 命名规范
- **NFR-3**: 字段类型应在 Java 和 SQL 之间正确映射

## Constraints
- **Technical**: 数据库表结构不可修改，只能修改实体类
- **Dependencies**: Spring Boot 3.2.0、JPA、MySQL 8.0+

## Assumptions
- 数据库表结构已经设计完成，且符合业务需求
- 后端项目已经搭建完成，使用 Spring Boot + JPA 框架

## Acceptance Criteria

### AC-1: 实体类与数据库表名一致
- **Given**: 后端实体类使用 @Table 注解指定表名
- **When**: 检查实体类的 @Table 注解
- **Then**: 实体类的 @Table 注解中的 name 属性应与数据库表名一致
- **Verification**: `programmatic`

### AC-2: 实体类字段与数据库表字段一致
- **Given**: 后端实体类使用 @Column 注解指定字段名
- **When**: 检查实体类的 @Column 注解
- **Then**: 实体类的 @Column 注解中的 name 属性应与数据库表字段名一致
- **Verification**: `programmatic`

### AC-3: 实体类字段类型与数据库表字段类型一致
- **Given**: 后端实体类使用 Java 类型定义字段
- **When**: 检查实体类的字段类型
- **Then**: 实体类的字段类型应与数据库表字段类型正确映射
- **Verification**: `programmatic`

### AC-4: 实体类字段约束与数据库表字段约束一致
- **Given**: 后端实体类使用 @Column 注解指定字段约束
- **When**: 检查实体类的 @Column 注解
- **Then**: 实体类的字段约束应与数据库表字段约束一致，包括 nullable、unique 等
- **Verification**: `programmatic`

### AC-5: 实体类关联关系与数据库表关联关系一致
- **Given**: 后端实体类使用 @ManyToOne、@OneToMany 等注解指定关联关系
- **When**: 检查实体类的关联关系注解
- **Then**: 实体类的关联关系应与数据库表的外键约束一致
- **Verification**: `programmatic`

## Open Questions
- [ ] Service 实体类的 price 字段类型为 Double，而数据库表中为 DECIMAL(10, 2)，是否需要修改？
- [ ] Appointment 实体类的 totalPrice 字段类型为 Double，而数据库表中为 DECIMAL(10, 2)，是否需要修改？

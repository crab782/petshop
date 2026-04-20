# Hibernate 到 MyBatis-Plus 全面迁移 - 产品需求文档

## Why
当前项目使用 Spring Data JPA + Hibernate 作为 ORM 框架，但在处理复杂查询、性能优化和 SQL 控制方面存在局限性。为了提升数据库交互性能、增强 SQL 控制能力并简化数据访问层代码，需要将现有的 Hibernate ORM 框架全面替换为 MyBatis-Plus 框架。

## What Changes
- **移除 Hibernate 相关依赖**：从 pom.xml 中移除 spring-boot-starter-data-jpa 依赖
- **添加 MyBatis-Plus 依赖**：集成 mybatis-plus-boot-starter 及相关插件
- **重构实体类**：保留现有实体类结构，添加 MyBatis-Plus 注解（@TableName、@TableId 等）
- **替换 Repository 层**：将 Spring Data JPA Repository 接口替换为 MyBatis-Plus Mapper 接口
- **重构查询逻辑**：将 HQL 查询转换为 MyBatis-Plus Wrapper 查询或 XML SQL 语句
- **配置调整**：更新应用配置文件，移除 JPA 相关配置，添加 MyBatis-Plus 配置
- **事务管理适配**：确保 Spring 事务管理与 MyBatis-Plus 兼容
- **测试验证**：执行全面的单元测试和集成测试，确保功能正确性

## Impact
- **Affected specs**: 
  - 所有涉及数据访问层的 spec
  - 后端 API 实现相关 spec
  - 测试相关 spec
- **Affected code**:
  - `pom.xml` - 依赖配置
  - `src/main/java/com/petshop/entity/` - 所有实体类
  - `src/main/java/com/petshop/repository/` - 所有 Repository 接口
  - `src/main/java/com/petshop/service/` - 服务层代码（查询逻辑重构）
  - `src/main/resources/application.properties/yml` - 配置文件
  - `src/main/resources/mapper/` - 新增 Mapper XML 文件目录

## ADDED Requirements

### Requirement: 依赖迁移
系统 SHALL 移除所有 Hibernate/JPA 相关依赖，添加 MyBatis-Plus 完整依赖栈，包括：
- mybatis-plus-boot-starter (3.5.5 或更高版本，支持 Spring Boot 3.x)
- mybatis-plus-spring-boot3-starter (如需要)
- mysql-connector-j (保留)
- mybatis-plus-boot-starter-test (测试支持)

#### Scenario: 依赖更新成功
- **WHEN** 更新 pom.xml 并执行 Maven 构建
- **THEN** 所有依赖成功解析，无版本冲突，项目编译通过

### Requirement: 实体类改造
系统 SHALL 为所有现有实体类添加 MyBatis-Plus 注解，包括：
- @TableName 指定表名
- @TableId 指定主键及生成策略
- @TableField 指定字段映射（如需要）
- 保留现有字段和 getter/setter 方法

#### Scenario: 实体类注解正确
- **WHEN** 实体类添加 MyBatis-Plus 注解
- **THEN** 注解语法正确，表名和字段名映射准确，主键策略配置正确

### Requirement: Mapper 接口实现
系统 SHALL 为每个实体创建对应的 Mapper 接口：
- 继承 BaseMapper<T> 接口
- 使用 @Mapper 注解
- 使用 @TableName 注解指定实体类
- 对于复杂查询，创建 XML 映射文件

#### Scenario: Mapper 接口完整
- **WHEN** 创建所有 Mapper 接口
- **THEN** 所有实体都有对应 Mapper，基础 CRUD 方法可用，复杂查询有 XML 支持

### Requirement: 服务层重构
系统 SHALL 重构所有 Service 层代码：
- 将 Repository 依赖替换为 Mapper 依赖
- 将 JPA Repository 方法调用替换为 Mapper 方法调用
- 将 HQL/JPQL 查询转换为 QueryWrapper/LambdaQueryWrapper 或 XML SQL
- 保留现有业务逻辑和事务注解

#### Scenario: 服务层功能正常
- **WHEN** 重构 Service 层代码
- **THEN** 业务逻辑不变，查询结果正确，事务管理正常

### Requirement: 配置更新
系统 SHALL 更新应用配置文件：
- 移除 JPA 相关配置（hibernate.*、spring.jpa.*）
- 添加 MyBatis-Plus 配置（mybatis-plus.*）
- 配置 Mapper XML 文件位置
- 配置类型别名包
- 配置日志（可选）

#### Scenario: 配置正确加载
- **WHEN** 应用启动
- **THEN** MyBatis-Plus 配置正确加载，无警告或错误

### Requirement: 功能验证
系统 SHALL 通过所有测试用例：
- 所有单元测试通过
- 所有集成测试通过
- API 接口功能正常
- 数据库操作结果正确

#### Scenario: 测试全部通过
- **WHEN** 执行 mvn test
- **THEN** 所有测试用例通过，代码覆盖率不低于迁移前水平

### Requirement: 性能验证
系统 SHALL 确保性能不低于迁移前水平：
- 简单查询性能相当或更好
- 复杂查询性能优于迁移前
- 事务处理性能相当
- 连接池配置优化

#### Scenario: 性能测试通过
- **WHEN** 执行性能测试
- **THEN** 查询响应时间不超过迁移前的 120%，复杂查询性能提升 20% 以上

## MODIFIED Requirements

### Requirement: 事务管理
**原要求**: 使用 Spring Data JPA 的事务管理机制
**修改后**: 使用 Spring 声明式事务管理（@Transactional），与 MyBatis-Plus 兼容

**原因**: Spring 的@Transactional 注解对 JPA 和 MyBatis 都适用，无需修改事务管理代码

### Requirement: 分页查询
**原要求**: 使用 Spring Data 的 Pageable 和 Page 接口
**修改后**: 使用 MyBatis-Plus 的 Page 类和 IPage 接口

**原因**: MyBatis-Plus 提供自己的分页机制，需要使用其 API

## REMOVED Requirements

### Requirement: JPA Specification 动态查询
**原因**: MyBatis-Plus 使用 QueryWrapper 实现动态查询，不需要 JPA Specification

**迁移**: 将 Specification 查询转换为 QueryWrapper 或 XML 动态 SQL

### Requirement: JPA Auditing 自动填充
**原因**: MyBatis-Plus 使用 MetaObjectHandler 实现自动填充

**迁移**: 实现 MyBatis-Plus 的 MetaObjectHandler 接口，替代 JPA Auditing

### Requirement: Hibernate 级联操作
**原因**: MyBatis-Plus 不直接支持级联操作，需要在服务层手动处理

**迁移**: 在 Service 层手动管理关联实体的保存和更新

## 技术选型说明

### MyBatis-Plus 版本选择
- **版本**: 3.5.5 或更高
- **原因**: 支持 Spring Boot 3.x 和 Java 17，提供完整的 CRUD 功能和强大的条件构造器

### 共存策略（可选）
- 如需要平滑迁移，可暂时保留 JPA 依赖
- 新代码使用 MyBatis-Plus，旧代码逐步迁移
- 最终完全移除 JPA

### 代码生成器
- 使用 MyBatis-Plus Generator 快速生成 Mapper、XML 和实体类
- 减少手动编写重复代码的工作量

## 迁移策略

### 阶段一：准备阶段
1. 分析现有代码，统计 Repository 方法和查询类型
2. 设计 MyBatis-Plus 架构和包结构
3. 准备测试环境和测试数据

### 阶段二：基础设施搭建
1. 更新 pom.xml 依赖
2. 配置 MyBatis-Plus
3. 创建 Mapper 基础接口和 XML 目录结构

### 阶段三：实体类改造
1. 为所有实体类添加 MyBatis-Plus 注解
2. 验证实体类与数据库表的映射关系

### 阶段四：数据访问层迁移
1. 创建所有 Mapper 接口
2. 对于简单 CRUD，使用 BaseMapper 方法
3. 对于复杂查询，编写 XML SQL 或使用 Wrapper

### 阶段五：服务层重构
1. 修改 Service 层，将 Repository 替换为 Mapper
2. 重构查询逻辑
3. 验证业务逻辑正确性

### 阶段六：测试验证
1. 执行单元测试
2. 执行集成测试
3. 执行性能测试
4. 修复发现的问题

### 阶段七：清理优化
1. 移除 JPA 相关代码和配置
2. 优化 SQL 语句
3. 编写文档和最佳实践

## 风险与缓解

### 风险 1: 迁移过程中引入 bug
**缓解措施**: 
- 保持完整的测试覆盖
- 采用渐进式迁移策略
- 每个模块迁移后立即测试

### 风险 2: 性能下降
**缓解措施**:
- 迁移前后进行性能对比测试
- 优化 SQL 语句和索引
- 合理使用 MyBatis-Plus 缓存

### 风险 3: 团队学习曲线
**缓解措施**:
- 提供 MyBatis-Plus 培训
- 编写详细的开发文档
- 建立代码审查机制

## 成功标准
1. ✅ 所有功能测试通过
2. ✅ 性能指标达到或超过迁移前水平
3. ✅ 代码质量符合项目标准
4. ✅ 文档完整清晰
5. ✅ 团队掌握 MyBatis-Plus 使用方法

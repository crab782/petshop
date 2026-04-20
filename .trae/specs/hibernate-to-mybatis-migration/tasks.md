# Hibernate 到 MyBatis-Plus 全面迁移 - 任务分解

## [x] Task 1: 项目分析与规划
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 分析现有项目结构和所有实体类
  - 统计所有 Repository 接口及其方法
  - 识别复杂查询和特殊映射需求
  - 制定详细的迁移计划和时间表
- **验收标准**: AC-1, AC-2
- **测试需求**:
  - `human-judgment` TR-1.1: 完成项目分析报告
  - `programmatic` TR-1.2: 统计 Repository 方法清单
- **备注**: 重点关注复杂查询和关联关系

## [x] Task 2: 依赖配置更新
- **优先级**: P0
- **依赖**: Task 1
- **描述**:
  - 在 pom.xml 中移除 spring-boot-starter-data-jpa 依赖
  - 添加 mybatis-plus-boot-starter 依赖（版本 3.5.5+）
  - 添加 mybatis-plus-spring-boot3-starter（如需要）
  - 保留 mysql-connector-j 依赖
  - 移除 Hibernate 相关配置
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-2.1: Maven 构建成功，无依赖冲突
  - `programmatic` TR-2.2: 项目编译通过
- **备注**: 选择与 Spring Boot 3.2.0 兼容的 MyBatis-Plus 版本

## [x] Task 3: MyBatis-Plus 基础配置
- **优先级**: P0
- **依赖**: Task 2
- **描述**:
  - 创建 MyBatis-Plus 配置类（MybatisPlusConfig）
  - 配置分页插件 PaginationInnerInterceptor
  - 配置 SQL 性能分析插件（可选）
  - 配置自动填充处理器 MetaObjectHandler（如需要）
  - 更新 application.properties/yml 配置文件
- **验收标准**: AC-2
- **测试需求**:
  - `programmatic` TR-3.1: 配置类正确加载
  - `programmatic` TR-3.2: 应用启动成功
- **备注**: 参考 MyBatis-Plus 官方文档配置

## [x] Task 4: 实体类改造
- **优先级**: P0
- **依赖**: Task 3
- **描述**:
  - 为所有实体类添加 @TableName 注解
  - 为主键字段添加 @TableId 注解
  - 为特殊字段添加 @TableField 注解
  - 移除 JPA 相关注解（@Entity、@Column 等）
  - 保留所有字段和 getter/setter 方法
- **验收标准**: AC-2, AC-3
- **测试需求**:
  - `programmatic` TR-4.1: 所有实体类都有 MyBatis-Plus 注解
  - `programmatic` TR-4.2: 实体类与数据库表映射正确
- **备注**: 可以使用代码生成器辅助生成注解

## [x] Task 5: Mapper 接口创建
- **优先级**: P0
- **依赖**: Task 4
- **描述**:
  - 为每个实体创建 Mapper 接口
  - 继承 BaseMapper<T> 接口
  - 添加 @Mapper 注解
  - 对于简单 CRUD，直接使用 BaseMapper 方法
  - 对于复杂查询，声明自定义方法
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-5.1: 所有实体都有对应 Mapper
  - `programmatic` TR-5.2: Mapper 接口编译通过
- **备注**: 使用@MapperScan 扫描 Mapper 包

## [x] Task 6: XML 映射文件编写
- **优先级**: P1
- **依赖**: Task 5
- **描述**:
  - 创建 resources/mapper/ 目录结构
  - 为复杂查询编写 XML 映射文件
  - 实现动态 SQL（<if>、<choose>、<foreach>等）
  - 配置 ResultMap 映射关系
  - 实现关联查询（一对多、多对一）
- **验收标准**: AC-3, AC-4
- **测试需求**:
  - `programmatic` TR-6.1: XML 文件语法正确
  - `programmatic` TR-6.2: 复杂查询测试通过
- **备注**: 使用 MyBatis-Plus 的 XML 模板

## [x] Task 7: Repository 层替换
- **优先级**: P0
- **依赖**: Task 6
- **描述**:
  - 删除所有 JPA Repository 接口
  - 在 Service 层将 Repository 引用替换为 Mapper
  - 更新依赖注入（@Autowired）
  - 移除 JPA 特有的类型（Pageable、Page 等）
- **验收标准**: AC-3, AC-4
- **测试需求**:
  - `programmatic` TR-7.1: 所有 Repository 都被替换
  - `programmatic` TR-7.2: Service 层编译通过
- **备注**: 保留 Service 层接口不变

## [x] Task 8: Service 层查询逻辑重构
- **优先级**: P0
- **依赖**: Task 7
- **描述**:
  - 将 JPA Repository 方法调用替换为 Mapper 调用
  - 将 HQL/JPQL 查询转换为 QueryWrapper/LambdaQueryWrapper
  - 将复杂查询改为调用 Mapper XML 方法
  - 将 JPA Page 转换为 MyBatis-Plus IPage
  - 保留业务逻辑和事务注解
- **验收标准**: AC-4, AC-5
- **测试需求**:
  - `programmatic` TR-8.1: 查询结果与迁移前一致
  - `programmatic` TR-8.2: 事务管理正常
- **备注**: 使用 LambdaQueryWrapper 提高类型安全性

## [x] Task 9: 特殊功能迁移
- **优先级**: P1
- **依赖**: Task 8
- **描述**:
  - 迁移 JPA Auditing 到 MyBatis-Plus MetaObjectHandler
  - 迁移 JPA Specification 动态查询到 QueryWrapper
  - 处理级联操作（在 Service 层手动管理）
  - 迁移原生 SQL 查询到 XML
- **验收标准**: AC-4
- **测试需求**:
  - `programmatic` TR-9.1: 自动填充功能正常
  - `programmatic` TR-9.2: 动态查询功能正常
- **备注**: 需要特别注意级联操作的处理

## [x] Task 10: 单元测试修复与执行
- **优先级**: P0
- **依赖**: Task 9
- **描述**:
  - 修复因迁移而失败的单元测试
  - 更新测试代码中的 Repository 引用为 Mapper
  - 执行所有单元测试
  - 分析测试覆盖率报告
- **验收标准**: AC-5
- **测试需求**:
  - `programmatic` TR-10.1: 所有单元测试通过
  - `programmatic` TR-10.2: 测试覆盖率不低于迁移前
- **备注**: 可能需要 Mock Mapper 对象

## Task 11: 集成测试执行
- **优先级**: P0
- **依赖**: Task 10
- **描述**:
  - 执行所有 API 集成测试
  - 验证所有 CRUD 操作功能正常
  - 验证复杂查询功能正常
  - 验证事务管理正常
- **验收标准**: AC-5
- **测试需求**:
  - `programmatic` TR-11.1: 所有集成测试通过
  - `programmatic` TR-11.2: API 接口功能正常
- **备注**: 使用真实的数据库连接

## Task 12: 性能测试与优化
- **优先级**: P1
- **依赖**: Task 11
- **描述**:
  - 执行性能基准测试
  - 对比迁移前后的查询性能
  - 优化慢查询 SQL
  - 配置合适的缓存策略
  - 优化连接池配置
- **验收标准**: AC-6
- **测试需求**:
  - `programmatic` TR-12.1: 性能指标达到要求
  - `programmatic` TR-12.2: 复杂查询性能提升 20% 以上
- **备注**: 使用 JMH 或 JMeter 进行性能测试

## Task 13: 代码清理
- **优先级**: P2
- **依赖**: Task 12
- **描述**:
  - 移除未使用的 JPA 导入
  - 移除废弃的 Repository 接口文件
  - 清理 JPA 相关配置代码
  - 整理代码格式
- **验收标准**: AC-5
- **测试需求**:
  - `programmatic` TR-13.1: 无 JPA 相关代码残留
  - `programmatic` TR-13.2: 代码编译无警告
- **备注**: 使用 IDE 的清理功能

## Task 14: 文档编写
- **优先级**: P2
- **依赖**: Task 13
- **描述**:
  - 编写 MyBatis-Plus 开发指南
  - 编写 Mapper XML 编写规范
  - 编写最佳实践文档
  - 更新项目 README
  - 记录常见问题和解决方案
- **验收标准**: AC-5
- **测试需求**:
  - `human-judgment` TR-14.1: 文档完整清晰
  - `human-judgment` TR-14.2: 团队能够根据文档进行开发
- **备注**: 包含代码示例和最佳实践

## Task 15: 代码审查与验收
- **优先级**: P0
- **依赖**: Task 14
- **描述**:
  - 执行代码审查
  - 检查代码质量
  - 验证所有验收标准
  - 准备上线部署
- **验收标准**: AC-1 到 AC-6
- **测试需求**:
  - `human-judgment` TR-15.1: 代码质量符合标准
  - `human-judgment` TR-15.2: 所有验收标准达成
- **备注**: 邀请团队成员参与代码审查

# Task Dependencies
- Task 2 依赖 Task 1
- Task 3 依赖 Task 2
- Task 4 依赖 Task 3
- Task 5 依赖 Task 4
- Task 6 依赖 Task 5
- Task 7 依赖 Task 6
- Task 8 依赖 Task 7
- Task 9 依赖 Task 8
- Task 10 依赖 Task 9
- Task 11 依赖 Task 10
- Task 12 依赖 Task 11
- Task 13 依赖 Task 12
- Task 14 依赖 Task 13
- Task 15 依赖 Task 14

# 并行任务
以下任务可以并行执行以提高效率：
- Task 4（实体类改造）和 Task 6（XML 映射文件编写）的部分工作可以并行
- Task 10（单元测试）可以分模块并行执行
- Task 11（集成测试）可以分 API 模块并行执行

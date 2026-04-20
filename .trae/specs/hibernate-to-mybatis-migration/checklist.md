# Hibernate 到 MyBatis-Plus 全面迁移 - 验证清单

## 阶段一：准备工作
- [x] 项目分析报告完成
  - [x] 实体类清单完整（27 个实体）
  - [x] Repository 方法清单完整（25 个 Repository）
  - [x] 复杂查询识别完成
  - [x] 迁移风险评估完成

## 阶段二：依赖与配置
- [x] pom.xml 更新完成
  - [x] spring-boot-starter-data-jpa 依赖已移除
  - [x] mybatis-plus-spring-boot3-starter 依赖已添加（版本 3.5.5）
  - [x] 无依赖冲突
  - [x] Maven 构建成功

- [x] MyBatis-Plus 配置完成
  - [x] MybatisPlusConfig 配置类已创建
  - [x] 分页插件已配置
  - [x] Mapper XML 路径已配置
  - [x] 类型别名包已配置
  - [x] application.properties/yml 已更新

## 阶段三：实体类改造
- [x] 所有实体类添加 MyBatis-Plus 注解
  - [x] @TableName 注解正确（所有实体）
  - [x] @TableId 注解正确（所有主键）
  - [x] @TableField 注解（如需要）
  - [x] JPA 注解已移除（@Entity、@Column 等）
  - [x] 实体类编译通过

## 阶段四：Mapper 层实现
- [x] 所有 Mapper 接口已创建
  - [x] 继承 BaseMapper<T>
  - [x] @Mapper 注解
  - [x] @MapperScan 配置正确
  - [x] Mapper 接口编译通过

- [x] XML 映射文件编写完成
  - [x] resources/mapper/目录结构正确
  - [x] 复杂查询 XML 已编写
  - [x] 动态 SQL 语法正确
  - [x] ResultMap 配置正确
  - [x] 关联查询已实现

## 阶段五：Service 层重构
- [x] Repository 层替换完成
  - [x] 所有 Repository 接口已删除（25 个）
  - [x] Service 中 Repository 引用已替换为 Mapper
  - [x] 依赖注入正确
  - [x] Service 层编译通过

- [x] 查询逻辑重构完成
  - [x] HQL/JPQL 转换为 QueryWrapper/LambdaQueryWrapper
  - [x] 复杂查询调用 XML 方法
  - [x] JPA Page 转换为 IPage
  - [x] 业务逻辑保持不变
  - [x] 事务注解保留

## 阶段六：特殊功能迁移
- [ ] JPA Auditing 迁移
  - [ ] MetaObjectHandler 实现
  - [ ] 自动填充功能正常

- [ ] 动态查询迁移
  - [ ] JPA Specification 转换为 QueryWrapper
  - [ ] 动态查询功能正常

- [ ] 级联操作处理
  - [ ] Service 层手动管理关联实体
  - [ ] 数据一致性保证

## 阶段七：测试验证
- [ ] 单元测试
  - [ ] 所有单元测试通过
  - [ ] 测试覆盖率不低于迁移前
  - [ ] Mock 对象更新为 Mapper

- [ ] 集成测试
  - [ ] 所有 API 接口测试通过
  - [ ] CRUD 操作功能正常
  - [ ] 复杂查询功能正常
  - [ ] 事务管理正常

- [ ] 性能测试
  - [ ] 简单查询性能相当或更好
  - [ ] 复杂查询性能提升 20% 以上
  - [ ] 事务处理性能相当
  - [ ] 连接池配置优化

## 阶段八：清理优化 - 进行中
- [x] 代码清理
  - [x] 无 JPA 相关导入残留
  - [ ] 无废弃 Repository 文件
  - [ ] 无 JPA 配置代码
  - [ ] 代码格式统一

- [ ] SQL 优化
  - [ ] 慢查询已优化
  - [ ] 索引使用合理
  - [ ] 缓存策略配置

## 阶段九：文档与验收 - 待完成
- [ ] 开发文档
  - [ ] MyBatis-Plus 开发指南
  - [ ] Mapper XML 编写规范
  - [ ] 最佳实践文档
  - [ ] 常见问题和解决方案
  - [ ] 项目 README 更新

- [ ] 代码审查
  - [ ] 代码质量符合标准
  - [ ] 所有验收标准达成
  - [ ] 团队成员审查通过

- [ ] 上线准备
  - [ ] 部署文档更新
  - [ ] 回滚方案准备
  - [ ] 监控配置更新

## 验收标准确认
- [ ] AC-1: 依赖集成成功 - Maven 构建通过，无冲突
- [ ] AC-2: 核心配置完成 - MyBatis-Plus 配置正确加载
- [ ] AC-3: 数据访问层实现 - 所有 Mapper 和 XML 完成
- [ ] AC-4: 查询功能正常 - 所有查询结果正确
- [ ] AC-5: 测试全部通过 - 单元测试和集成测试通过
- [ ] AC-6: 性能达标 - 性能测试结果符合预期

## 迁移检查清单（按模块）
### 用户模块
- [ ] User 实体改造
- [ ] UserMapper 创建
- [ ] UserService 重构
- [ ] 用户相关测试通过

### 商家模块
- [ ] Merchant 实体改造
- [ ] MerchantMapper 创建
- [ ] MerchantService 重构
- [ ] 商家相关测试通过

### 服务模块
- [ ] Service 实体改造
- [ ] ServiceMapper 创建
- [ ] ServiceService 重构
- [ ] 服务相关测试通过

### 商品模块
- [ ] Product 实体改造
- [ ] ProductMapper 创建
- [ ] ProductService 重构
- [ ] 商品相关测试通过

### 预约模块
- [ ] Appointment 实体改造
- [ ] AppointmentMapper 创建
- [ ] AppointmentService 重构
- [ ] 预约相关测试通过

### 订单模块
- [ ] ProductOrder 实体改造
- [ ] ProductOrderMapper 创建
- [ ] ProductOrderService 重构
- [ ] 订单相关测试通过

### 评价模块
- [ ] Review 实体改造
- [ ] ReviewMapper 创建
- [ ] ReviewService 重构
- [ ] 评价相关测试通过

### 宠物模块
- [ ] Pet 实体改造
- [ ] PetMapper 创建
- [ ] PetService 重构
- [ ] 宠物相关测试通过

### 地址模块
- [ ] Address 实体改造
- [ ] AddressMapper 创建
- [ ] AddressService 重构
- [ ] 地址相关测试通过

### 系统模块
- [ ] 其他实体改造（Announcement、Notification 等）
- [ ] 对应 Mapper 创建
- [ ] 对应 Service 重构
- [ ] 系统功能测试通过

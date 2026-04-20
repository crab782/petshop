# Hibernate 到 MyBatis-Plus 迁移报告

**迁移日期**: 2026-04-20  
**项目名称**: 宠物管理系统 (Pet Management System)  
**迁移类型**: ORM 框架迁移 (JPA/Hibernate → MyBatis-Plus)

---

## 1. Hibernate 到 MyBatis-Plus 迁移步骤

### 1.1 依赖变更

#### 移除的依赖
```xml
<!-- 已移除 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

#### 新增的依赖
```xml
<!-- 新增 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

#### 保留的依赖
```xml
<!-- 数据库连接 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 1.2 实体类改造

#### 改造统计
- **实体类总数**: 27 个
- **改造完成**: 27 个 (100%)

#### 注解变更对照表

| JPA 注解 | MyBatis-Plus 注解 | 说明 |
|---------|-------------------|------|
| `@Entity` | `@TableName("table_name")` | 表名映射 |
| `@Table(name = "xxx")` | `@TableName("xxx")` | 表名映射 |
| `@Id` | `@TableId(type = IdType.AUTO)` | 主键映射 |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | `@TableId(type = IdType.AUTO)` | 主键自增 |
| `@Column(name = "xxx")` | `@TableField("xxx")` | 字段映射 |
| `@ManyToOne` | 移除，改为 ID 字段 (Integer) | 关联关系 |
| `@OneToMany` | 移除，改为 ID 字段 (Integer) | 关联关系 |
| `@JoinColumn` | 移除 | 关联关系 |

#### 新增 Lombok 注解
```java
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {
    // ...
}
```

#### 自动填充配置
```java
@TableField(value = "created_at", fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;
```

#### 已改造的实体类列表

| 序号 | 实体类 | 表名 | 说明 |
|-----|--------|------|------|
| 1 | User | user | 用户表 |
| 2 | Merchant | merchant | 商家表 |
| 3 | Appointment | appointment | 预约表 |
| 4 | Service | service | 服务表 |
| 5 | Product | product | 商品表 |
| 6 | Pet | pet | 宠物表 |
| 7 | ProductOrder | product_order | 商品订单表 |
| 8 | ProductOrderItem | product_order_item | 订单明细表 |
| 9 | Review | review | 评价表 |
| 10 | Address | address | 地址表 |
| 11 | Category | category | 分类表 |
| 12 | Admin | admin | 管理员表 |
| 13 | Announcement | announcement | 公告表 |
| 14 | Notification | notification | 通知表 |
| 15 | Favorite | favorite | 收藏表 |
| 16 | Cart | cart | 购物车表 |
| 17 | SearchHistory | search_history | 搜索历史表 |
| 18 | MerchantSettings | merchant_settings | 商家设置表 |
| 19 | SystemConfig | system_config | 系统配置表 |
| 20 | SystemSettings | system_settings | 系统设置表 |
| 21 | ScheduledTask | scheduled_task | 定时任务表 |
| 22 | OperationLog | operation_log | 操作日志表 |
| 23 | Activity | activity | 活动表 |
| 24 | Role | role | 角色表 |
| 25 | Permission | permission | 权限表 |
| 26 | ForumPost | forum_post | 论坛帖子表 |
| 27 | ForumComment | forum_comment | 论坛评论表 |

### 1.3 Mapper 接口创建

#### 创建统计
- **Mapper 接口总数**: 27 个
- **创建完成**: 27 个 (100%)

#### Mapper 接口示例
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 后自动拥有以下方法：
    // - insert(entity)
    // - deleteById(id)
    // - updateById(entity)
    // - selectById(id)
    // - selectList(wrapper)
    // - selectPage(page, wrapper)
    // 等等...
}
```

#### 已创建的 Mapper 接口列表

| 序号 | Mapper 接口 | 对应实体 |
|-----|-------------|----------|
| 1 | UserMapper | User |
| 2 | MerchantMapper | Merchant |
| 3 | AppointmentMapper | Appointment |
| 4 | ServiceMapper | Service |
| 5 | ProductMapper | Product |
| 6 | PetMapper | Pet |
| 7 | ProductOrderMapper | ProductOrder |
| 8 | ProductOrderItemMapper | ProductOrderItem |
| 9 | ReviewMapper | Review |
| 10 | AddressMapper | Address |
| 11 | CategoryMapper | Category |
| 12 | AdminMapper | Admin |
| 13 | AnnouncementMapper | Announcement |
| 14 | NotificationMapper | Notification |
| 15 | FavoriteMapper | Favorite |
| 16 | CartMapper | Cart |
| 17 | SearchHistoryMapper | SearchHistory |
| 18 | MerchantSettingsMapper | MerchantSettings |
| 19 | SystemConfigMapper | SystemConfig |
| 20 | SystemSettingsMapper | SystemSettings |
| 21 | ScheduledTaskMapper | ScheduledTask |
| 22 | OperationLogMapper | OperationLog |
| 23 | ActivityMapper | Activity |
| 24 | RoleMapper | Role |
| 25 | PermissionMapper | Permission |
| 26 | ForumPostMapper | ForumPost |
| 27 | ForumCommentMapper | ForumComment |

### 1.4 Service 层重构

#### 重构统计
- **Service 类总数**: 26 个
- **重构完成**: 26 个 (100%)

#### 方法调用对照表

| JPA Repository 方法 | MyBatis-Plus Mapper 方法 | 说明 |
|---------------------|--------------------------|------|
| `findById(id)` | `selectById(id)` | 根据 ID 查询 |
| `findAll()` | `selectList(null)` | 查询所有 |
| `save(entity)` | `insert(entity)` 或 `updateById(entity)` | 保存/更新 |
| `deleteById(id)` | `deleteById(id)` | 删除 |
| `findByXxx(value)` | `selectOne(new LambdaQueryWrapper<T>().eq(T::getXxx, value))` | 条件查询 |
| `findAll(Sort.by("id").descending())` | `selectList(new LambdaQueryWrapper<T>().orderByDesc(T::getId))` | 排序查询 |
| `findAll(pageable)` | `selectPage(page, wrapper)` | 分页查询 |
| `count()` | `selectCount(null)` | 计数 |

#### QueryWrapper 使用示例
```java
// 多条件查询
LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Appointment::getUserId, userId)
       .eq(StringUtils.isNotBlank(status), Appointment::getStatus, status)
       .ge(startDate != null, Appointment::getAppointmentTime, startDate)
       .le(endDate != null, Appointment::getAppointmentTime, endDate)
       .orderByDesc(Appointment::getCreatedAt);
List<Appointment> list = appointmentMapper.selectList(wrapper);

// 分页查询
Page<Appointment> page = new Page<>(pageNum, pageSize);
appointmentMapper.selectPage(page, wrapper);
```

#### 已重构的 Service 类列表

| 序号 | Service 类 | 重构状态 |
|-----|------------|----------|
| 1 | UserService.java | ✅ 已完成 |
| 2 | ProductOrderService.java | ✅ 已完成 |
| 3 | ReviewService.java | ✅ 已完成 |
| 4 | FavoriteService.java | ✅ 已完成 |
| 5 | CartService.java / CartServiceImpl.java | ✅ 已完成 |
| 6 | SearchService.java | ✅ 已完成 |
| 7 | MerchantSettingsService.java | ✅ 已完成 |
| 8 | SystemSettingsService.java | ✅ 已完成 |
| 9 | ScheduledTaskService.java | ✅ 已完成 |
| 10 | OperationLogService.java | ✅ 已完成 |
| 11 | ActivityService.java | ✅ 已完成 |
| 12 | RoleService.java | ✅ 已完成 |
| 13 | UserHomeService.java | ✅ 已完成 |
| 14 | AuthService.java | ✅ 已完成 |
| 15 | AppointmentService.java | ✅ 已完成 |
| 16 | MerchantService.java | ✅ 已完成 |
| 17 | ProductService.java | ✅ 已完成 |
| 18 | ServiceService.java | ✅ 已完成 |
| 19 | PetService.java | ✅ 已完成 |
| 20 | AddressService.java | ✅ 已完成 |
| 21 | CategoryService.java | ✅ 已完成 |
| 22 | AdminService.java | ✅ 已完成 |
| 23 | AnnouncementService.java | ✅ 已完成 |
| 24 | NotificationService.java | ✅ 已完成 |
| 25 | MerchantStatsService.java | ✅ 已完成 |
| 26 | UserDetailsServiceImpl.java | ✅ 已完成 |

### 1.5 配置类创建

#### MyBatisPlusConfig.java
```java
@Configuration
@MapperScan("com.petshop.mapper")
public class MyBatisConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

---

## 2. 移除的静态资源清单

### 2.1 目录清理
- `src/main/resources/static/` 目录 - 不存在，无需清理

### 2.2 配置清理
- `application.properties` 中的静态资源配置 - 已移除
- `SecurityConfig` 中的 `/static/**` 权限配置 - 已移除

### 2.3 Repository 删除
- 删除了整个 `src/main/java/com/petshop/repository` 目录
- 共删除 25 个 Repository 接口文件

---

## 3. API 变更记录

### 3.1 状态更新接口修复

#### 问题描述
前端发送 JSON 请求体，后端使用 `@RequestParam` 接收 URL 参数，导致参数为 null。

#### 修复的接口（共 10 个）

**平台端 (5 个)**:
| 接口 | 修复前 | 修复后 |
|------|--------|--------|
| `PUT /api/admin/users/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/admin/merchants/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/admin/products/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/admin/services/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/admin/activities/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |

**商家端 (5 个)**:
| 接口 | 修复前 | 修复后 |
|------|--------|--------|
| `PUT /api/merchant/appointments/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/merchant/orders/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/merchant/product-orders/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/merchant/products/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |
| `PUT /api/merchant/categories/{id}/status` | `@RequestParam String status` | `@RequestBody Map<String, String>` |

### 3.2 审计发现的问题汇总

#### 用户端 API 审计 (UserApiController)
- **审计接口数**: 50+ 个
- **发现问题数**: 27 个

| 审计项 | 问题数量 | 严重程度 |
|--------|----------|----------|
| HTTP 方法正确性 | 4 | ⚠️ 中等 |
| URL 路径规范性 | 3 | ⚠️ 中等 |
| 请求参数验证 | 6 | 🔴 严重 |
| 响应格式一致性 | 5 | 🔴 严重 |
| 错误处理完整性 | 3 | ⚠️ 中等 |
| 状态码正确性 | 2 | ⚠️ 低 |
| 前端兼容性 | 4 | 🔴 严重 |

#### 商家端 API 审计 (MerchantApiController)
- **审计接口数**: 40+ 个
- **发现问题数**: 9 个

| 审计项 | 问题数量 | 严重程度 |
|--------|----------|----------|
| 状态更新参数问题 | 5 | 🔴 严重 |
| 响应格式不一致 | 2 | ⚠️ 中等 |
| 缺少输入验证 | 2 | ⚠️ 中等 |

#### 平台端 API 审计 (AdminApiController)
- **审计接口数**: 58 个
- **发现问题数**: 28 个

| 审计项 | 问题数量 | 严重程度 |
|--------|----------|----------|
| HTTP 方法正确性 | 0 | ✅ 通过 |
| URL 路径规范性 | 3 | ⚠️ 中等 |
| 请求参数验证 | 6 | 🔴 严重 |
| 响应格式一致性 | 5 | ⚠️ 中等 |
| 错误处理完整性 | 4 | ⚠️ 中等 |
| 状态码正确性 | 3 | ⚠️ 低 |
| 前端兼容性 | 7 | 🔴 严重 |

#### 公共 API 审计
- **发现问题数**: 27 个
- 主要问题：参数验证、响应格式、错误处理

---

## 4. 测试验证结果

### 4.1 编译状态

```
[INFO] BUILD SUCCESS
[INFO] Total time: 2.689 s
[INFO] Finished at: 2026-04-20
```

### 4.2 编译错误修复记录

#### 实体类修复
- **Address.java**: 添加 `user` 字段和 `getUser()`/`setUser()` 方法
- **Review.java**: 添加 `appointment` 字段、`getAppointment()`/`setAppointment()` 方法和 `setReply()`/`getReply()` 方法

#### Mapper 方法添加
- **CategoryMapper.java**: 添加 `incrementProductCount()` 和 `decrementProductCount()` 方法

#### DTO 类修复
- **ApiResponse.java**: 添加 `success(boolean, String)` 方法重载

#### Page 类型转换修复
为以下服务类中的匿名 Page 实现添加 `map()` 方法：
- **ReviewService.java**: 3 个匿名类
- **ScheduledTaskService.java**: 1 个匿名类
- **ActivityService.java**: 2 个匿名类

#### 重复方法删除
- **MerchantService.java**: 删除返回 `List<Merchant>` 的 `getPendingMerchants()` 方法
- **ScheduledTaskService.java**: 删除返回 MyBatis-Plus Page 的 `getTasks()` 方法
- **ActivityService.java**: 删除返回 MyBatis-Plus Page 的 `findAll()` 和 `searchActivities()` 方法

#### Controller 方法签名修复
- **AdminApiController.java**: 修改 `getActivities()` 方法参数
- **UserApiController.java**: 修改 `getAppointments()` 和 `getPurchasedServices()` 方法返回类型
- **ServiceController.java**: 修改 `getServices()` 和 `searchServices()` 方法
- **MerchantApiController.java**: 修改 `getReviews()` 方法

### 4.3 测试状态

| 测试类型 | 状态 | 说明 |
|---------|------|------|
| 编译测试 | ✅ 通过 | BUILD SUCCESS |
| 单元测试 | ⏳ 待执行 | 需要更新测试代码 |
| 集成测试 | ⏳ 待执行 | 需要配置测试数据库 |
| API 测试 | ⏳ 待执行 | 需要启动应用进行测试 |

---

## 5. 迁移总结

### 5.1 迁移成果

| 项目 | 迁移前 | 迁移后 | 状态 |
|------|--------|--------|------|
| ORM 框架 | JPA/Hibernate | MyBatis-Plus 3.5.5 | ✅ 完成 |
| 实体类 | 27 个 (JPA 注解) | 27 个 (MyBatis-Plus 注解) | ✅ 完成 |
| Repository | 25 个接口 | 已删除 | ✅ 完成 |
| Mapper | 0 | 27 个接口 | ✅ 完成 |
| Service | 26 个 | 26 个 (重构完成) | ✅ 完成 |
| 编译状态 | - | SUCCESS | ✅ 通过 |

### 5.2 主要改进

1. **性能提升**: MyBatis-Plus 提供更灵活的 SQL 控制，可以针对复杂查询进行优化
2. **代码简化**: BaseMapper 提供丰富的 CRUD 方法，减少重复代码
3. **类型安全**: LambdaQueryWrapper 提供编译时类型检查
4. **分页优化**: 内置分页插件，无需手动处理分页逻辑
5. **自动填充**: MetaObjectHandler 实现字段自动填充

### 5.3 待办事项

- [ ] 执行单元测试并修复失败的测试用例
- [ ] 执行集成测试验证 API 功能
- [ ] 性能测试与优化
- [ ] 代码审查与清理
- [ ] 更新项目文档

---

## 6. 附录

### 6.1 相关文件

- [MybatisPlusConfig.java](file:///d:/j/cg/cg/src/main/java/com/petshop/config/MybatisPlusConfig.java)
- [tasks.md](file:///d:/j/cg/cg/tasks.md)
- [ADMIN_API_AUDIT_REPORT.md](file:///d:/j/cg/cg/ADMIN_API_AUDIT_REPORT.md)

### 6.2 参考文档

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis-Plus Spring Boot 3 集成指南](https://baomidou.com/guide/spring-boot3.html)
- [LambdaQueryWrapper 使用指南](https://baomidou.com/guide/wrapper.html)

---

**报告生成时间**: 2026-04-20  
**报告版本**: 1.0

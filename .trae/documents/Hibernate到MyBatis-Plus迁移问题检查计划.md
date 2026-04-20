# Hibernate 到 MyBatis-Plus 迁移问题检查计划

## 任务目标
检查并记录从 Hibernate 迁移到 MyBatis-Plus 后后端代码中的问题，为后续修复提供参考。

## 问题分类总结

### 1. Spring Data Page 与 MyBatis-Plus Page 混用问题（严重）

**问题描述**：多个 Service 层返回 `org.springframework.data.domain.Page`，但内部使用 MyBatis-Plus 的 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`，导致需要手动转换。

**受影响文件**：
- `ServiceService.java` (L37-150) - 手动实现 Page 适配器
- `MerchantService.java` (L197-367) - 手动实现 Page 适配器
- `UserService.java` (L53-77) - 手动实现 Page 适配器
- `ReviewService.java` - 多处手动实现 Page 适配器 (L292-666)
- `ProductServiceImpl.java` - 使用 `PageImpl` 转换 (L83-185)
- `AnnouncementService.java` - 存在类似问题

**问题代码示例**：
```java
// ServiceService.java L37-43
public org.springframework.data.domain.Page<com.petshop.entity.Service> findAll(org.springframework.data.domain.Pageable pageable) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> page =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber(), pageable.getPageSize());
    // 需要手动创建匿名内部类实现 org.springframework.data.domain.Page 接口
}
```

**建议修复方案**：
- 统一使用 MyBatis-Plus 的 `Page<T>` 替代 Spring Data 的 `Page<T>`
- 或者使用 `PageImpl` 进行转换（已部分使用但不一致）

---

### 2. 实体关联加载问题（严重）

**问题描述**：Hibernate 会自动通过 JPA 关联加载相关实体，但 MyBatis-Plus 不会。需要手动处理关联查询。

**受影响的实体和代码**：

| 实体 | 关联字段 | 问题位置 |
|------|---------|---------|
| Service | `merchant` | `Service.java L54` |
| Product | `merchant` | `Product.java L56-57` |
| Appointment | `user`, `service`, `merchant`, `pet` | `Appointment.java L53-63` |
| Review | `user`, `merchant`, `service`, `appointment` | `Review.java L52-62` |
| ProductOrder | `merchant` | `ProductOrder.java L77-78` |
| ProductOrderItem | `product` | `ProductOrderItem.java L34-35` |
| Pet | `user` | `Pet.java L52-53` |

**问题代码示例**：
```java
// AppointmentService.java L138
appointment.setMerchant(service.getMerchant()); // service.getMerchant() 返回 null，因为没有关联查询

// MerchantApiController.java L503
if (!appointment.getMerchant().getId().equals(merchant.getId())) // NullPointerException 风险
```

**建议修复方案**：
- 创建 MyBatis-Plus 的 XML Mapper 文件处理关联查询
- 或在 Service 层显式查询关联数据
- 或使用 `@Result` 注解在 Mapper 中配置 resultMap

---

### 3. Service 接口与实现不一致问题（中等）

**问题描述**：`ProductService` 接口定义的方法返回类型与实现类 `ProductServiceImpl` 不一致。

**问题代码**：
```java
// ProductService.java L11
Product getProductById(Long id);  // 返回 Long

// ProductServiceImpl.java L30
public Product getProductById(Long id) {  // 正确
    return productMapper.selectById(id);
}

// 但 ProductService.java L17
Product findById(Integer id);  // 返回 Integer
```

**建议修复方案**：
- 统一 ID 类型，建议使用 `Long` 类型以保持一致性

---

### 4. 实体类 Lombok 与手动 Getter/Setter 混用（轻微）

**问题描述**：实体类同时使用 Lombok `@Data` 注解和手动编写 getter/setter 方法，造成代码冗余。

**受影响文件**：所有实体类都存在此问题
- `Service.java` (L56-79)
- `Product.java` (L59-84)
- `Merchant.java` (L52-74)
- `User.java` (L46-64)
- `Appointment.java` (L65-94)
- `Review.java` (L64-96)
- 等等...

**建议修复方案**：
- 移除手动的 getter/setter，保留 Lombok 注解
- 注意：如果代码中有 `@Accessors(chain = true)`，链式调用不受影响

---

### 5. Mapper XML 文件缺失（中等）

**问题描述**：复杂的关联查询需要 Mapper XML 文件或 `@Select`/`@Results` 注解，当前项目可能缺少这些配置。

**检查结果**：
- `CategoryMapper.java` 有 `@Update` 注解处理自定义 SQL
- 其他 Mapper 只继承 `BaseMapper`，没有复杂查询配置

**建议修复方案**：
- 为需要关联查询的 Mapper 添加 XML Mapper 文件
- 或使用 MyBatis-Plus 的 `@Select` 注解

---

### 6. Controller 层访问关联对象的问题（严重）

**问题描述**：`MerchantApiController` 中多处代码直接访问实体的关联对象，但这些关联对象未加载。

**问题代码示例**：
```java
// MerchantApiController.java L262
if (!existingService.getMerchant().getId().equals(merchant.getId())) // NPE 风险

// MerchantApiController.java L311
if (!service.getMerchant().getId().equals(merchant.getId())) // NPE 风险

// MerchantApiController.java L503
if (!appointment.getMerchant().getId().equals(merchant.getId())) // NPE 风险

// MerchantApiController.java L719
if (!order.getMerchant().getId().equals(merchant.getId())) // NPE 风险
```

**建议修复方案**：
- 在调用前先查询关联的 Merchant 对象
- 或在 Service 层提供带 Merchant 查询的方法

---

## 详细问题清单

| 序号 | 文件 | 问题类型 | 严重程度 | 描述 |
|------|------|---------|---------|------|
| 1 | ServiceService.java | Page类型转换 | 高 | 手动实现Page适配器 |
| 2 | MerchantService.java | Page类型转换 | 高 | 手动实现Page适配器 |
| 3 | UserService.java | Page类型转换 | 高 | 手动实现Page适配器 |
| 4 | ReviewService.java | Page类型转换 | 高 | 多处手动实现Page适配器 |
| 5 | ProductServiceImpl.java | PageImpl转换 | 中 | 部分使用PageImpl |
| 6 | AppointmentService.java | 关联加载 | 高 | appointment.getMerchant()等返回null |
| 7 | MerchantApiController.java | NPE风险 | 高 | 多处访问未加载的关联对象 |
| 8 | ProductService.java | 类型不一致 | 中 | getProductById返回Long, findById返回Integer |
| 9 | 所有实体类 | 代码冗余 | 低 | Lombok + 手动getter/setter |

## 执行步骤

1. **检查 Entity 关联问题** - 确认哪些关联查询是必需的
2. **检查 Service 层 Page 返回类型** - 确认统一使用哪种 Page 类型
3. **检查 Controller 层关联对象访问** - 列出所有可能的 NPE 风险点
4. **生成完整问题报告** - 整理所有发现的问题

## 注意事项

- 本次任务**不修改代码**，只进行检查和问题记录
- 编译错误修复由另一个 AI 工具负责处理
- 问题报告将输出到 `hibernate-to-mybatis-migration-issues.md`

# Hibernate 到 MyBatis-Plus 迁移问题详细报告

生成时间：2026-04-20
项目路径：d:\j\cg\cg

---

## 一、问题概述

本次检查基于从 Hibernate 迁移到 MyBatis-Plus 的后端 Java 代码。迁移过程中出现了多种典型问题，主要集中在：

1. **Page 类型不兼容问题** - Spring Data Page vs MyBatis-Plus Page
2. **实体关联加载问题** - MyBatis-Plus 不支持懒加载
3. **Controller 层 NPE 风险** - 访问未加载的关联对象
4. **实体类代码冗余** - Lombok 与手写 getter/setter 混用

---

## 二、详细问题分析

### 问题 1：Spring Data Page 与 MyBatis-Plus Page 混用（严重）

#### 1.1 ServiceService.java

**位置**：`src/main/java/com/petshop/service/ServiceService.java` L37-150

**问题描述**：`findAll(Pageable pageable)` 方法返回 `org.springframework.data.domain.Page`，但内部使用 MyBatis-Plus 的 `Page`。需要手动创建匿名内部类实现 Page 接口。

**问题代码**：
```java
// L37-43
public org.springframework.data.domain.Page<com.petshop.entity.Service> findAll(org.springframework.data.domain.Pageable pageable) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> page =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber(), pageable.getPageSize());
    // ... 需要手动实现整个 Page 接口，约 150 行代码
}
```

**修复建议**：
- 方案 A：改用 `com.baomidou.mybatisplus.extension.plugins.pagination.Page` 作为返回类型
- 方案 B：使用 `PageImpl` 转换（参考 ProductServiceImpl 的做法）

---

#### 1.2 MerchantService.java

**位置**：`src/main/java/com/petshop/service/MerchantService.java` L197-367

**问题描述**：`findByStatus()` 方法同样手动实现 Page 接口。

**问题代码**：
```java
// L197-204
public org.springframework.data.domain.Page<Merchant> findByStatus(String status, Pageable pageable) {
    Page<Merchant> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
    // 手动实现 Page 接口，约 170 行
}
```

---

#### 1.3 UserService.java

**位置**：`src/main/java/com/petshop/service/UserService.java` L53-77

**问题描述**：`findAll(Pageable pageable)` 手动实现 Page 接口。

---

#### 1.4 ReviewService.java

**位置**：`src/main/java/com/petshop/service/ReviewService.java`

**问题描述**：多处方法手动实现 Page 接口，包括：
- `getAllReviews(int page, int pageSize)` - L292-406
- `searchAdminReviews()` - L408-543
- `getPendingReviews()` - L545-666

**问题代码**：
```java
// L292-298
public org.springframework.data.domain.Page<Review> getAllReviews(int page, int pageSize) {
    Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
    Page<Review> mpPage = new Page<>(page + 1, pageSize);
    // 手动实现，约 110 行
}
```

---

### 问题 2：实体关联加载问题（严重）

#### 2.1 Appointment 实体关联

**实体位置**：`src/main/java/com/petshop/entity/Appointment.java` L53-63

**关联字段**：
```java
@TableField(exist = false)
private User user;           // L54

@TableField(exist = false)
private com.petshop.entity.Service service;  // L57

@TableField(exist = false)
private Merchant merchant;   // L60

@TableField(exist = false)
private Pet pet;            // L63
```

**问题场景**：当通过 MyBatis-Plus 查询 Appointment 时，这些关联对象不会被自动加载。

---

#### 2.2 Review 实体关联

**实体位置**：`src/main/java/com/petshop/entity/Review.java` L52-62

**关联字段**：
```java
@TableField(exist = false)
private User user;           // L53

@TableField(exist = false)
private Merchant merchant;   // L56

@TableField(exist = false)
private Service service;     // L59

@TableField(exist = false)
private Appointment appointment;  // L62
```

---

#### 2.3 ProductOrder 实体关联

**实体位置**：`src/main/java/com/petshop/entity/ProductOrder.java` L77-78

**关联字段**：
```java
@TableField(exist = false)
private Merchant merchant;
```

---

### 问题 3：Controller 层 NPE 风险（严重）

#### 3.1 MerchantApiController.java

| 行号 | 代码 | 风险描述 |
|------|------|---------|
| 262 | `existingService.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 279 | `service.setMerchant(merchant)` | 直接设置 merchant 对象 |
| 311 | `service.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 434 | `service.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 503 | `appointment.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 719 | `order.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 803 | `order.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 848 | `order.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 915 | `product.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 934 | `product.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 957 | `product.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 1022 | `product.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 1067 | `product.getMerchant().getId()` | `getMerchant()` 可能返回 null |
| 1105 | `product.getMerchant().getId()` | `getMerchant()` 可能返回 null |

**示例问题代码**：
```java
// L262
if (!existingService.getMerchant().getId().equals(merchant.getId())) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(403, "无权操作此服务"));
}

// L503
if (!appointment.getMerchant().getId().equals(merchant.getId())) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(403, "无权操作此预约"));
}
```

---

### 问题 4：Service 接口与实现不一致（中等）

#### 4.1 ProductService.java

**文件位置**：`src/main/java/com/petshop/service/ProductService.java`

**问题描述**：接口定义中 `getProductById` 返回 `Long`，但实现类中 `findById` 返回 `Integer`。

**问题代码**：
```java
// L11 - getProductById 返回 Long
Product getProductById(Long id);

// L17 - findById 返回 Integer
Product findById(Integer id);
```

---

### 问题 5：实体类代码冗余（轻微）

**问题描述**：实体类同时使用 Lombok `@Data` 注解和手动编写 getter/setter。

**受影响文件**：
- `Service.java` (L56-79)
- `Product.java` (L59-84)
- `Merchant.java` (L52-74)
- `User.java` (L46-64)
- `Appointment.java` (L65-94)
- `Review.java` (L64-96)
- `ProductOrder.java` (L80-120)
- `ProductOrderItem.java` (L37-49)
- `Pet.java` (L55-78)
- `Category.java` (L49-68)

**问题代码示例**：
```java
// Service.java - L12-14 同时使用
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
// 但 L56-79 又手写了所有 getter/setter
public Integer getId() { return id; }
public void setId(Integer id) { this.id = id; }
// ... 重复 20+ 行
```

---

### 问题 6：Mapper XML 配置缺失（中等）

**问题描述**：复杂的关联查询（如 JOIN）没有对应的 Mapper XML 文件或 `@Select`/`@Results` 注解配置。

**当前状态**：
- `CategoryMapper.java` 有 `@Update` 注解处理自定义 SQL
- 其他 Mapper 只继承 `BaseMapper<Category>`，没有复杂查询

**建议**：为以下常见关联查询创建 Mapper XML 或使用 `@Select` 注解：
- Service + Merchant
- Appointment + User + Service + Merchant + Pet
- Review + User + Merchant + Service
- ProductOrder + ProductOrderItem + Product

---

## 三、问题严重程度汇总

| 严重程度 | 问题数量 | 说明 |
|---------|---------|------|
| **严重** | 3 | Page 类型混用、实体关联加载、Controller NPE |
| **中等** | 2 | 类型不一致、Mapper XML 缺失 |
| **轻微** | 1 | 代码冗余 |

---

## 四、修复优先级建议

### 第一优先级（必须修复）

1. **解决 Controller 层 NPE 风险**
   - 在 MerchantApiController 中添加空指针检查
   - 或在 Service 层提供带 Merchant 查询的方法

2. **统一 Page 类型**
   - 建议统一使用 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
   - 或使用 `PageImpl` 进行转换

### 第二优先级（建议修复）

3. **处理实体关联加载**
   - 创建关联查询的 Mapper XML
   - 或在 Service 层显式查询关联数据

4. **统一 ID 类型**
   - 建议全部使用 `Long` 类型

### 第三优先级（可选修复）

5. **清理代码冗余**
   - 移除手写的 getter/setter，保留 Lombok 注解

---

## 五、附录

### A. 受影响文件列表

```
src/main/java/com/petshop/
├── entity/
│   ├── Service.java
│   ├── Product.java
│   ├── Merchant.java
│   ├── User.java
│   ├── Appointment.java
│   ├── Review.java
│   ├── ProductOrder.java
│   ├── ProductOrderItem.java
│   ├── Pet.java
│   └── Category.java
├── service/
│   ├── ServiceService.java
│   ├── MerchantService.java
│   ├── UserService.java
│   ├── ReviewService.java
│   ├── ProductService.java
│   ├── ProductServiceImpl.java
│   └── AppointmentService.java
├── mapper/
│   └── CategoryMapper.java (+ 其他 Mapper 缺少关联配置)
└── controller/api/
    ├── MerchantApiController.java
    └── UserApiController.java
```

### B. MyBatis-Plus 迁移检查清单

- [ ] 检查所有 `@TableField(exist = false)` 字段的使用
- [ ] 确认所有 Service 层的 Page 返回类型
- [ ] 检查 Controller 中所有 `.getMerchant().getId()` 调用
- [ ] 确认是否需要 Mapper XML 文件处理关联查询
- [ ] 统一实体 ID 类型（建议 Long）
- [ ] 清理 Lombok 与手写 getter/setter 冗余

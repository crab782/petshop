# 修复 MerchantApiController NPE 风险 - 产品需求文档

## Why
在从 Hibernate 迁移到 MyBatis-Plus 后，`MerchantApiController.java` 中存在 14 处以上直接调用未加载关联对象的代码（如 `entity.getMerchant().getId()`）。由于 MyBatis-Plus 不会像 Hibernate 那样自动加载关联实体，这些代码存在极高的 NullPointerException 风险，可能导致系统运行时异常。

## What Changes
- **在 Service 层实现关联查询方法**：为 Service、Product、Appointment、ProductOrder 等实体创建带关联查询的方法
- **创建 Mapper XML 关联查询**：使用 LEFT JOIN 一次性加载实体及其关联对象
- **修复 Controller 中的 NPE 风险点**：将所有直接访问未加载关联对象的代码改为使用预加载的数据
- **添加空值安全检查**：在必要时添加空值检查，确保代码健壮性

## Impact
- **Affected specs**: hibernate-to-mybatis-migration
- **Affected code**:
  - `src/main/java/com/petshop/controller/api/MerchantApiController.java` - 修复 14+ 处 NPE 风险
  - `src/main/java/com/petshop/service/ServiceService.java` - 添加关联查询方法
  - `src/main/java/com/petshop/service/ProductService.java` / `ProductServiceImpl.java` - 添加关联查询方法
  - `src/main/java/com/petshop/service/AppointmentService.java` - 添加关联查询方法
  - `src/main/java/com/petshop/service/ProductOrderService.java` - 添加关联查询方法
  - `src/main/resources/mapper/` - 新增关联查询 XML

## ADDED Requirements

### Requirement: Service 层关联查询
系统 SHALL 在 Service 层提供带关联查询的方法，确保在数据查询阶段一次性加载所有必要的关联数据。

#### Scenario: 查询服务带商家信息
- **WHEN** 调用 `serviceService.findByIdWithMerchant(id)`
- **THEN** 返回的 Service 对象中 merchant 字段已正确加载，不为 null

#### Scenario: 查询商品带商家信息
- **WHEN** 调用 `productService.findByIdWithMerchant(id)`
- **THEN** 返回的 Product 对象中 merchant 字段已正确加载，不为 null

#### Scenario: 查询预约带关联信息
- **WHEN** 调用 `appointmentService.findByIdWithRelations(id)`
- **THEN** 返回的 Appointment 对象中 user、service、merchant、pet 字段已正确加载

#### Scenario: 查询订单带商家信息
- **WHEN** 调用 `productOrderService.findByIdWithMerchant(id)`
- **THEN** 返回的 ProductOrder 对象中 merchant 字段已正确加载，不为 null

### Requirement: Mapper XML 关联查询
系统 SHALL 创建 Mapper XML 文件，使用 LEFT JOIN 实现关联查询。

#### Scenario: ServiceMapper XML 配置
- **WHEN** 查询服务数据时
- **THEN** 使用 LEFT JOIN 关联 merchant 表，一次性加载服务及其商家信息

#### Scenario: ProductMapper XML 配置
- **WHEN** 查询商品数据时
- **THEN** 使用 LEFT JOIN 关联 merchant 表，一次性加载商品及其商家信息

#### Scenario: AppointmentMapper XML 配置
- **WHEN** 查询预约数据时
- **THEN** 使用 LEFT JOIN 关联 user、service、merchant、pet 表，一次性加载所有关联信息

#### Scenario: ProductOrderMapper XML 配置
- **WHEN** 查询订单数据时
- **THEN** 使用 LEFT JOIN 关联 merchant 表，一次性加载订单及其商家信息

### Requirement: Controller NPE 修复
系统 SHALL 修复 `MerchantApiController` 中所有直接访问未加载关联对象的代码。

#### Scenario: 修复服务相关 NPE
- **WHEN** 访问 `/api/merchant/services/{id}` 端点
- **THEN** 使用 `findByIdWithMerchant` 方法，确保 service.getMerchant() 不为 null

#### Scenario: 修复预约相关 NPE
- **WHEN** 访问 `/api/merchant/appointments` 或 `/api/merchant/appointments/{id}/status` 端点
- **THEN** 使用 `findByIdWithRelations` 方法，确保 appointment.getMerchant() 不为 null

#### Scenario: 修复订单相关 NPE
- **WHEN** 访问 `/api/merchant/orders/{id}/status` 或 `/api/merchant/product-orders/{id}/status` 端点
- **THEN** 使用 `findByIdWithMerchant` 方法，确保 order.getMerchant() 不为 null

#### Scenario: 修复商品相关 NPE
- **WHEN** 访问 `/api/merchant/products/{id}` 或 `/api/merchant/products/{id}/status` 端点
- **THEN** 使用 `findByIdWithMerchant` 方法，确保 product.getMerchant() 不为 null

## MODIFIED Requirements

### Requirement: 现有查询方法
**原要求**: 使用简单的 `selectById` 查询
**修改后**: 根据场景选择使用 `selectById` 或带关联查询的方法

**原因**: 简单查询场景不需要关联数据，但 Controller 中需要验证权限的场景必须加载关联数据

## REMOVED Requirements
无

## 技术实现说明

### Mapper XML 结构
```xml
<!-- ServiceMapper.xml -->
<resultMap id="ServiceWithMerchantMap" type="com.petshop.entity.Service">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <!-- ... 其他字段 -->
    <association property="merchant" javaType="com.petshop.entity.Merchant">
        <id property="id" column="merchant_id"/>
        <result property="name" column="merchant_name"/>
        <!-- ... 其他字段 -->
    </association>
</resultMap>

<select id="selectByIdWithMerchant" resultMap="ServiceWithMerchantMap">
    SELECT s.*, m.id as merchant_id, m.name as merchant_name, ...
    FROM service s
    LEFT JOIN merchant m ON s.merchant_id = m.id
    WHERE s.id = #{id}
</select>
```

### Service 层方法签名
```java
// ServiceService.java
public com.petshop.entity.Service findByIdWithMerchant(Integer id);

// ProductService.java
public Product findByIdWithMerchant(Integer id);

// AppointmentService.java
public Appointment findByIdWithRelations(Integer id);

// ProductOrderService.java
public ProductOrder findByIdWithMerchant(Integer id);
```

## 修复点清单

| 行号 | 原代码 | 修复方案 |
|------|--------|---------|
| 262 | `existingService.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 311 | `service.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 434 | `service.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 503 | `appointment.getMerchant().getId()` | 使用 `findByIdWithRelations` |
| 719 | `order.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 803 | `order.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 848 | `order.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 915 | `product.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 934 | `product.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 957 | `product.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 1022 | `product.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 1067 | `product.getMerchant().getId()` | 使用 `findByIdWithMerchant` |
| 1105 | `product.getMerchant().getId()` | 使用 `findByIdWithMerchant` |

## 验收标准
- [ ] AC-1: 所有 14 处 NPE 风险点已修复
- [ ] AC-2: Service 层新增关联查询方法并正确实现
- [ ] AC-3: Mapper XML 文件正确配置关联查询
- [ ] AC-4: 所有修复点通过单元测试
- [ ] AC-5: 集成测试通过，无 NPE 异常
- [ ] AC-6: 代码审查通过

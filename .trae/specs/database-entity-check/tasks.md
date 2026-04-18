# 宠物管理系统 - 数据库与实体类一致性检查

## [x] Task 1: 检查 User 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 User 实体类与 user 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `programmatic` TR-1.1: User 实体类的 @Table 注解应指定表名为 "user"
  - `programmatic` TR-1.2: User 实体类的字段应与 user 表的字段一一对应
  - `programmatic` TR-1.3: User 实体类的字段类型应与 user 表的字段类型正确映射
  - `programmatic` TR-1.4: User 实体类的字段约束应与 user 表的字段约束一致
- **Notes**: User 实体类已经检查，与数据库表结构一致

## [x] Task 2: 检查 Merchant 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 Merchant 实体类与 merchant 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `programmatic` TR-2.1: Merchant 实体类的 @Table 注解应指定表名为 "merchant"
  - `programmatic` TR-2.2: Merchant 实体类的字段应与 merchant 表的字段一一对应
  - `programmatic` TR-2.3: Merchant 实体类的字段类型应与 merchant 表的字段类型正确映射
  - `programmatic` TR-2.4: Merchant 实体类的字段约束应与 merchant 表的字段约束一致
- **Notes**: Merchant 实体类已经检查，与数据库表结构一致

## [x] Task 3: 检查 Pet 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 Pet 实体类与 pet 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-3.1: Pet 实体类的 @Table 注解应指定表名为 "pet"
  - `programmatic` TR-3.2: Pet 实体类的字段应与 pet 表的字段一一对应
  - `programmatic` TR-3.3: Pet 实体类的字段类型应与 pet 表的字段类型正确映射
  - `programmatic` TR-3.4: Pet 实体类的字段约束应与 pet 表的字段约束一致
  - `programmatic` TR-3.5: Pet 实体类的关联关系应与 pet 表的外键约束一致
- **Notes**: Pet 实体类已经检查，与数据库表结构一致

## [x] Task 4: 检查 Service 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 Service 实体类与 service 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-4.1: Service 实体类的 @Table 注解应指定表名为 "service"
  - `programmatic` TR-4.2: Service 实体类的字段应与 service 表的字段一一对应
  - `programmatic` TR-4.3: Service 实体类的字段类型应与 service 表的字段类型正确映射
  - `programmatic` TR-4.4: Service 实体类的字段约束应与 service 表的字段约束一致
  - `programmatic` TR-4.5: Service 实体类的关联关系应与 service 表的外键约束一致
- **Notes**: Service 实体类的 price 字段类型为 Double，而数据库表中为 DECIMAL(10, 2)，需要修改

## [x] Task 5: 检查 Appointment 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 Appointment 实体类与 appointment 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-5.1: Appointment 实体类的 @Table 注解应指定表名为 "appointment"
  - `programmatic` TR-5.2: Appointment 实体类的字段应与 appointment 表的字段一一对应
  - `programmatic` TR-5.3: Appointment 实体类的字段类型应与 appointment 表的字段类型正确映射
  - `programmatic` TR-5.4: Appointment 实体类的字段约束应与 appointment 表的字段约束一致
  - `programmatic` TR-5.5: Appointment 实体类的关联关系应与 appointment 表的外键约束一致
- **Notes**: Appointment 实体类的 totalPrice 字段类型为 Double，而数据库表中为 DECIMAL(10, 2)，需要修改

## [x] Task 6: 检查 Admin 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 Admin 实体类与 admin 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `programmatic` TR-6.1: Admin 实体类的 @Table 注解应指定表名为 "admin"
  - `programmatic` TR-6.2: Admin 实体类的字段应与 admin 表的字段一一对应
  - `programmatic` TR-6.3: Admin 实体类的字段类型应与 admin 表的字段类型正确映射
  - `programmatic` TR-6.4: Admin 实体类的字段约束应与 admin 表的字段约束一致
- **Notes**: Admin 实体类已经检查，与数据库表结构一致

## [x] Task 7: 检查 Review 实体类
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查 Review 实体类与 review 表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-7.1: Review 实体类的 @Table 注解应指定表名为 "review"
  - `programmatic` TR-7.2: Review 实体类的字段应与 review 表的字段一一对应
  - `programmatic` TR-7.3: Review 实体类的字段类型应与 review 表的字段类型正确映射
  - `programmatic` TR-7.4: Review 实体类的字段约束应与 review 表的字段约束一致
  - `programmatic` TR-7.5: Review 实体类的关联关系应与 review 表的外键约束一致
- **Notes**: Review 实体类已经检查，与数据库表结构一致

## [x] Task 8: 检查其他实体类
- **Priority**: P1
- **Depends On**: None
- **Description**: 
  - 检查其他实体类（Announcement、Favorite、ForumComment、ForumPost、Product、ProductOrder、ProductOrderItem）与对应表的一致性
  - 验证表名、字段名、字段类型、约束等
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-8.1: 其他实体类的 @Table 注解应与对应表名一致
  - `programmatic` TR-8.2: 其他实体类的字段应与对应表的字段一一对应
  - `programmatic` TR-8.3: 其他实体类的字段类型应与对应表的字段类型正确映射
  - `programmatic` TR-8.4: 其他实体类的字段约束应与对应表的字段约束一致
  - `programmatic` TR-8.5: 其他实体类的关联关系应与对应表的外键约束一致
- **Notes**: 已检查其他实体类，其中 Product、ProductOrder、ProductOrderItem 的价格字段需要修改

## [x] Task 9: 修改 Service 实体类
- **Priority**: P0
- **Depends On**: Task 4
- **Description**: 
  - 修改 Service 实体类的 price 字段类型，从 Double 改为 BigDecimal
  - 确保与数据库表中的 DECIMAL(10, 2) 类型对应
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-9.1: Service 实体类的 price 字段类型应改为 BigDecimal
  - `programmatic` TR-9.2: Service 实体类的 price 字段应使用 @Column 注解指定 columnDefinition 为 "DECIMAL(10, 2)"
- **Notes**: 已修改，导入了 java.math.BigDecimal 包

## [x] Task 10: 修改 Appointment 实体类
- **Priority**: P0
- **Depends On**: Task 5
- **Description**: 
  - 修改 Appointment 实体类的 totalPrice 字段类型，从 Double 改为 BigDecimal
  - 确保与数据库表中的 DECIMAL(10, 2) 类型对应
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-10.1: Appointment 实体类的 totalPrice 字段类型应改为 BigDecimal
  - `programmatic` TR-10.2: Appointment 实体类的 totalPrice 字段应使用 @Column 注解指定 columnDefinition 为 "DECIMAL(10, 2)"
- **Notes**: 已修改，导入了 java.math.BigDecimal 包

## [x] Task 11: 修改 Product 实体类
- **Priority**: P0
- **Depends On**: Task 8
- **Description**: 
  - 修改 Product 实体类的 price 字段类型，从 Double 改为 BigDecimal
  - 确保与数据库表中的 DECIMAL(10, 2) 类型对应
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-11.1: Product 实体类的 price 字段类型应改为 BigDecimal
  - `programmatic` TR-11.2: Product 实体类的 price 字段应使用 @Column 注解指定 columnDefinition 为 "decimal(10,2)"
- **Notes**: 已修改，导入了 java.math.BigDecimal 包

## [x] Task 12: 修改 ProductOrder 实体类
- **Priority**: P0
- **Depends On**: Task 8
- **Description**: 
  - 修改 ProductOrder 实体类的 totalPrice 字段类型，从 Double 改为 BigDecimal
  - 确保与数据库表中的 DECIMAL(10, 2) 类型对应
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-12.1: ProductOrder 实体类的 totalPrice 字段类型应改为 BigDecimal
  - `programmatic` TR-12.2: ProductOrder 实体类的 totalPrice 字段应使用 @Column 注解指定 columnDefinition 为 "decimal(10,2)"
- **Notes**: 已修改，导入了 java.math.BigDecimal 包

## [x] Task 13: 修改 ProductOrderItem 实体类
- **Priority**: P0
- **Depends On**: Task 8
- **Description**: 
  - 修改 ProductOrderItem 实体类的 price 字段类型，从 Double 改为 BigDecimal
  - 确保与数据库表中的 DECIMAL(10, 2) 类型对应
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-13.1: ProductOrderItem 实体类的 price 字段类型应改为 BigDecimal
  - `programmatic` TR-13.2: ProductOrderItem 实体类的 price 字段应使用 @Column 注解指定 columnDefinition 为 "decimal(10,2)"
- **Notes**: 已修改，导入了 java.math.BigDecimal 包

# 宠物管理系统 - 数据库与实体类一致性检查清单

- [x] 检查 User 实体类与 user 表的一致性
  - [x] 表名一致：User 实体类的 @Table 注解应指定表名为 "user"
  - [x] 字段名一致：User 实体类的字段应与 user 表的字段一一对应
  - [x] 字段类型一致：User 实体类的字段类型应与 user 表的字段类型正确映射
  - [x] 字段约束一致：User 实体类的字段约束应与 user 表的字段约束一致

- [x] 检查 Merchant 实体类与 merchant 表的一致性
  - [x] 表名一致：Merchant 实体类的 @Table 注解应指定表名为 "merchant"
  - [x] 字段名一致：Merchant 实体类的字段应与 merchant 表的字段一一对应
  - [x] 字段类型一致：Merchant 实体类的字段类型应与 merchant 表的字段类型正确映射
  - [x] 字段约束一致：Merchant 实体类的字段约束应与 merchant 表的字段约束一致

- [x] 检查 Pet 实体类与 pet 表的一致性
  - [x] 表名一致：Pet 实体类的 @Table 注解应指定表名为 "pet"
  - [x] 字段名一致：Pet 实体类的字段应与 pet 表的字段一一对应
  - [x] 字段类型一致：Pet 实体类的字段类型应与 pet 表的字段类型正确映射
  - [x] 字段约束一致：Pet 实体类的字段约束应与 pet 表的字段约束一致
  - [x] 关联关系一致：Pet 实体类的关联关系应与 pet 表的外键约束一致

- [x] 检查 Service 实体类与 service 表的一致性
  - [x] 表名一致：Service 实体类的 @Table 注解应指定表名为 "service"
  - [x] 字段名一致：Service 实体类的字段应与 service 表的字段一一对应
  - [x] 字段类型一致：Service 实体类的 price 字段类型应改为 BigDecimal，与 service 表的 DECIMAL(10, 2) 对应
  - [x] 字段约束一致：Service 实体类的字段约束应与 service 表的字段约束一致
  - [x] 关联关系一致：Service 实体类的关联关系应与 service 表的外键约束一致

- [x] 检查 Appointment 实体类与 appointment 表的一致性
  - [x] 表名一致：Appointment 实体类的 @Table 注解应指定表名为 "appointment"
  - [x] 字段名一致：Appointment 实体类的字段应与 appointment 表的字段一一对应
  - [x] 字段类型一致：Appointment 实体类的 totalPrice 字段类型应改为 BigDecimal，与 appointment 表的 DECIMAL(10, 2) 对应
  - [x] 字段约束一致：Appointment 实体类的字段约束应与 appointment 表的字段约束一致
  - [x] 关联关系一致：Appointment 实体类的关联关系应与 appointment 表的外键约束一致

- [x] 检查 Admin 实体类与 admin 表的一致性
  - [x] 表名一致：Admin 实体类的 @Table 注解应指定表名为 "admin"
  - [x] 字段名一致：Admin 实体类的字段应与 admin 表的字段一一对应
  - [x] 字段类型一致：Admin 实体类的字段类型应与 admin 表的字段类型正确映射
  - [x] 字段约束一致：Admin 实体类的字段约束应与 admin 表的字段约束一致

- [x] 检查 Review 实体类与 review 表的一致性
  - [x] 表名一致：Review 实体类的 @Table 注解应指定表名为 "review"
  - [x] 字段名一致：Review 实体类的字段应与 review 表的字段一一对应
  - [x] 字段类型一致：Review 实体类的字段类型应与 review 表的字段类型正确映射
  - [x] 字段约束一致：Review 实体类的字段约束应与 review 表的字段约束一致
  - [x] 关联关系一致：Review 实体类的关联关系应与 review 表的外键约束一致

- [x] 检查其他实体类与对应表的一致性
  - [x] 表名一致：其他实体类的 @Table 注解应与对应表名一致
  - [x] 字段名一致：其他实体类的字段应与对应表的字段一一对应
  - [x] 字段类型一致：其他实体类的字段类型应与对应表的字段类型正确映射
  - [x] 字段约束一致：其他实体类的字段约束应与对应表的字段约束一致
  - [x] 关联关系一致：其他实体类的关联关系应与对应表的外键约束一致

- [x] 验证数据库连接配置
  - [x] 数据库地址：jdbc:mysql://localhost:3306/cg
  - [x] 用户名：root
  - [x] 密码：123456
  - [x] 驱动类：com.mysql.cj.jdbc.Driver

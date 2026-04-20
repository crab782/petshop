# 修复手机号唯一约束和注册逻辑 Plan

## 问题分析
- 数据库中手机号 `13412340001` 有重复记录，导致登录时 `selectOne()` 抛出异常
- 需要添加数据库唯一索引约束防止重复
- 注册时只验证手机号唯一性，用户名和邮箱可以任意（包括留空）

## 实施步骤

### 步骤 1: 添加数据库唯一索引
- 在 `user` 表的 `phone` 字段添加唯一索引
- SQL: `ALTER TABLE user ADD UNIQUE INDEX uk_phone (phone);`
- 注意：如果数据库中已有重复数据，需要先清理

### 步骤 2: 修改注册逻辑 (AuthService.register)
- 移除用户名和邮箱的必填验证
- 只保留手机号必填验证
- 只检查手机号是否重复，不检查用户名和邮箱
- 用户名和邮箱如果为空，设置默认值

### 步骤 3: 修改注册请求 DTO (RegisterRequest)
- 如果使用了验证注解，移除 username 和 email 的 @NotBlank 等约束
- 只保留 phone 的必填约束

### 步骤 4: 修改控制器验证 (AuthApiController.register)
- 移除 username 和 email 的必填检查
- 只检查 phone 和 password

### 步骤 5: 测试验证
- 测试使用重复手机号注册，应返回 "手机号已被注册"
- 测试使用新手机号、空用户名、空邮箱注册，应成功
- 测试使用新手机号、任意用户名、任意邮箱注册，应成功
- 测试登录功能正常

## 修改的文件清单
1. `src/main/java/com/petshop/service/AuthService.java` - 修改注册逻辑
2. `src/main/java/com/petshop/dto/RegisterRequest.java` - 修改验证注解（如有）
3. `src/main/java/com/petshop/controller/api/AuthApiController.java` - 修改参数验证
4. 数据库迁移脚本 - 添加唯一索引

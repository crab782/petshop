# 测试数据使用文档

## 测试数据概述

本项目已成功移除所有硬编码数据，并在数据库中补充了完整的测试数据。测试数据涵盖了以下表：

- **merchant**：10个商家（含新注册的测试商家）
- **user**：10个用户（含新注册的测试用户）
- **pet**：20个宠物
- **service**：20个服务
- **product**：30个商品
- **appointment**：20个预约
- **product_order**：15个商品订单
- **product_order_item**：30个订单商品
- **favorite**：10个收藏
- **review**：15个评价

## 测试账号信息

### 用户端
- **测试用户**：13999999999
- **密码**：123456
- **角色**：普通用户

### 商家端
- **测试商家**：13999999998
- **密码**：123456
- **状态**：已批准（approved）

### 平台端
- **管理员**：admin
- **密码**：123456（已更新）
- **角色**：管理员

## API调用示例

### 1. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13999999999","password":"123456"}'
```

### 2. 商家登录

```bash
curl -X POST http://localhost:8080/api/auth/merchant/login \
  -H "Content-Type: application/json" \
  -d '{"loginIdentifier":"13999999998","password":"123456"}'
```

### 3. 获取商家列表

```bash
curl -X GET http://localhost:8080/api/merchants
```

### 4. 用户注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138017","password":"123456","username":"新用户"}'
```

### 5. 商家注册

```bash
curl -X POST http://localhost:8080/api/auth/merchant/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138018","password":"123456","name":"新商家","contact_person":"联系人","address":"地址"}'
```

## 前端页面访问

### 用户端
- **首页**：http://localhost:5174/user/home
- **服务列表**：http://localhost:5174/user/services
- **宠物管理**：http://localhost:5174/user/pets
- **预约管理**：http://localhost:5174/user/appointments
- **订单管理**：http://localhost:5174/user/orders

### 商家端
- **登录**：http://localhost:5174/merchant/login
- **首页**：http://localhost:5174/merchant/home
- **服务管理**：http://localhost:5174/merchant/services
- **商品管理**：http://localhost:5174/merchant/products
- **订单管理**：http://localhost:5174/merchant/orders

### 平台端
- **登录**：http://localhost:5174/admin/login
- **仪表盘**：http://localhost:5174/admin/admin-dashboard
- **用户管理**：http://localhost:5174/admin/users
- **商家管理**：http://localhost:5174/admin/merchants

## 注意事项

1. **密码加密**：所有用户、商家和管理员的密码都使用BCrypt算法加密存储
2. **数据关系**：测试数据中的外键关系已验证，确保数据完整性
3. **登录认证**：API登录需要使用正确的手机号/用户名和密码
4. **商家状态**：新注册的商家需要管理员批准后才能登录
5. **数据范围**：测试数据使用ID范围4000+，避免与现有数据冲突
6. **API权限**：部分API需要登录后才能访问，返回403错误表示需要认证

## 数据库验证

### 验证外键关系

```sql
-- 验证服务表中的商家ID
SELECT COUNT(*) FROM service s LEFT JOIN merchant m ON s.merchant_id = m.id WHERE m.id IS NULL;

-- 验证商品表中的商家ID
SELECT COUNT(*) FROM product p LEFT JOIN merchant m ON p.merchant_id = m.id WHERE m.id IS NULL;
```

### 验证数据完整性

```sql
-- 统计各表数据量
SELECT 'merchant' AS table_name, COUNT(*) AS count FROM merchant UNION
SELECT 'user' AS table_name, COUNT(*) AS count FROM user UNION
SELECT 'pet' AS table_name, COUNT(*) AS count FROM pet UNION
SELECT 'service' AS table_name, COUNT(*) AS count FROM service UNION
SELECT 'product' AS table_name, COUNT(*) AS count FROM product UNION
SELECT 'appointment' AS table_name, COUNT(*) AS count FROM appointment UNION
SELECT 'product_order' AS table_name, COUNT(*) AS count FROM product_order UNION
SELECT 'product_order_item' AS table_name, COUNT(*) AS count FROM product_order_item UNION
SELECT 'favorite' AS table_name, COUNT(*) AS count FROM favorite UNION
SELECT 'review' AS table_name, COUNT(*) AS count FROM review;
```

## 故障排除

### 登录失败
- 检查用户名/手机号是否正确
- 检查密码是否正确（默认123456）
- 检查商家状态是否为approved

### API返回403
- 确保已登录并获取了JWT令牌
- 确保请求头中包含Authorization: Bearer {token}

### 前端页面显示错误
- 检查前端服务是否运行（http://localhost:5174）
- 检查后端服务是否运行（http://localhost:8080）
- 检查数据库连接是否正常

## 结论

项目已成功移除所有硬编码数据，补充了完整的测试数据，并验证了数据完整性。前端页面已从数据库加载数据，API调用正常工作。测试账号可以正常登录和使用所有功能。
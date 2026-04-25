# 宠物服务平台 - 后端API接口文档

## 项目概述

本文档详细描述了宠物服务平台后端的所有API接口，包括接口路径、请求参数、响应格式、业务逻辑和实现细节。文档旨在为后续的Rust重构提供完整的参考。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.x
- **安全**: Spring Security + JWT
- **API文档**: SpringDoc OpenAPI (Swagger)
- **Java版本**: 17

## 目录结构

```
com.petshop.controller/
├── api/                 # API控制器
│   ├── AdminApiController.java      # 管理员API
│   ├── AnnouncementApiController.java # 公告API
│   ├── AuthApiController.java       # 认证API
│   ├── CartController.java          # 购物车API
│   ├── MerchantApiController.java   # 商家API
│   ├── MerchantController.java      # 商家控制器
│   ├── ProductController.java       # 商品API
│   ├── PublicApiController.java     # 公共API
│   ├── RedisLockController.java     # Redis锁API
│   ├── SearchApiController.java     # 搜索API
│   ├── ServiceController.java       # 服务API
│   └── UserApiController.java       # 用户API
├── AdminController.java             # 管理员控制器
├── AuthController.java              # 认证控制器
├── HomeController.java              # 首页控制器
├── MerchantController.java          # 商家控制器
└── UserController.java              # 用户控制器
```

## API接口分类

1. **认证接口**: 用户注册、登录、退出等
2. **用户接口**: 用户信息管理、宠物管理、订单管理等
3. **商家接口**: 商家信息管理、服务管理、商品管理等
4. **管理员接口**: 用户管理、商家管理、系统管理等
5. **公共接口**: 公告、搜索等

## 详细接口文档

### 1. 认证接口

#### 1.1 AuthApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/auth/register` | POST | 用户注册 | username, email, password, confirmPassword, phone | 重定向到登录页 |
| `/api/auth/merchant/register` | POST | 商家注册 | name, contactPerson, phone, email, password, confirmPassword, address | 重定向到登录页 |
| `/api/auth/login` | POST | 用户登录 | email, password, type | 重定向到对应角色的仪表板 |
| `/api/auth/logout` | GET | 退出登录 | 无 | 重定向到首页 |

#### 1.2 AuthController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/register/user` | POST | 用户注册 | username, email, password, confirmPassword, phone | 重定向到登录页 |
| `/register/merchant` | POST | 商家注册 | name, contactPerson, phone, email, password, confirmPassword, address | 重定向到登录页 |
| `/login` | POST | 用户登录 | email, password, type | 重定向到对应角色的仪表板 |
| `/logout` | GET | 退出登录 | 无 | 重定向到首页 |

### 2. 用户接口

#### 2.1 UserApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/user/profile` | GET | 获取用户资料 | 无 | 用户资料JSON |
| `/api/user/profile` | PUT | 更新用户资料 | username, email, phone, avatar | 更新结果JSON |
| `/api/user/pets` | GET | 获取用户宠物列表 | 无 | 宠物列表JSON |
| `/api/user/pets` | POST | 添加宠物 | name, type, breed, age, gender, avatar, description | 添加结果JSON |
| `/api/user/pets/{id}` | GET | 获取宠物详情 | id | 宠物详情JSON |
| `/api/user/pets/{id}` | PUT | 更新宠物信息 | id, name, type, breed, age, gender, avatar, description | 更新结果JSON |
| `/api/user/pets/{id}` | DELETE | 删除宠物 | id | 删除结果JSON |

### 3. 商家接口

#### 3.1 MerchantApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/merchant/profile` | GET | 获取商家资料 | 无 | 商家资料JSON |
| `/api/merchant/profile` | PUT | 更新商家资料 | name, contactPerson, phone, email, address, logo | 更新结果JSON |
| `/api/merchant/services` | GET | 获取商家服务列表 | 无 | 服务列表JSON |
| `/api/merchant/services` | POST | 添加服务 | name, description, price, duration, image | 添加结果JSON |
| `/api/merchant/services/{id}` | GET | 获取服务详情 | id | 服务详情JSON |
| `/api/merchant/services/{id}` | PUT | 更新服务信息 | id, name, description, price, duration, image | 更新结果JSON |
| `/api/merchant/services/{id}` | DELETE | 删除服务 | id | 删除结果JSON |
| `/api/merchant/products` | GET | 获取商家商品列表 | 无 | 商品列表JSON |
| `/api/merchant/products` | POST | 添加商品 | name, description, price, stock, image | 添加结果JSON |
| `/api/merchant/products/{id}` | GET | 获取商品详情 | id | 商品详情JSON |
| `/api/merchant/products/{id}` | PUT | 更新商品信息 | id, name, description, price, stock, image | 更新结果JSON |
| `/api/merchant/products/{id}` | DELETE | 删除商品 | id | 删除结果JSON |

#### 3.2 MerchantController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/merchant/dashboard` | GET | 商家仪表板 | 无 | 仪表板页面 |
| `/merchant/services` | GET | 服务管理页面 | 无 | 服务管理页面 |
| `/merchant/products` | GET | 商品管理页面 | 无 | 商品管理页面 |
| `/merchant/appointments` | GET | 预约管理页面 | 无 | 预约管理页面 |
| `/merchant/orders` | GET | 订单管理页面 | 无 | 订单管理页面 |
| `/merchant/reviews` | GET | 评价管理页面 | 无 | 评价管理页面 |
| `/merchant/settings` | GET | 店铺设置页面 | 无 | 店铺设置页面 |

### 4. 管理员接口

#### 4.1 AdminApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/admin/users` | GET | 获取用户列表 | page, size, keyword | 用户列表JSON |
| `/api/admin/users/{id}` | GET | 获取用户详情 | id | 用户详情JSON |
| `/api/admin/users/{id}` | PUT | 更新用户信息 | id, username, email, phone, status | 更新结果JSON |
| `/api/admin/users/{id}` | DELETE | 删除用户 | id | 删除结果JSON |
| `/api/admin/merchants` | GET | 获取商家列表 | page, size, keyword, status | 商家列表JSON |
| `/api/admin/merchants/{id}` | GET | 获取商家详情 | id | 商家详情JSON |
| `/api/admin/merchants/{id}/approve` | PUT | 审核商家 | id, status, reason | 审核结果JSON |
| `/api/admin/merchants/{id}` | DELETE | 删除商家 | id | 删除结果JSON |
| `/api/admin/services` | GET | 获取服务列表 | page, size, keyword, merchantId | 服务列表JSON |
| `/api/admin/services/{id}` | GET | 获取服务详情 | id | 服务详情JSON |
| `/api/admin/services/{id}` | PUT | 更新服务信息 | id, name, description, price, duration, status | 更新结果JSON |
| `/api/admin/services/{id}` | DELETE | 删除服务 | id | 删除结果JSON |
| `/api/admin/products` | GET | 获取商品列表 | page, size, keyword, merchantId | 商品列表JSON |
| `/api/admin/products/{id}` | GET | 获取商品详情 | id | 商品详情JSON |
| `/api/admin/products/{id}` | PUT | 更新商品信息 | id, name, description, price, stock, status | 更新结果JSON |
| `/api/admin/products/{id}` | DELETE | 删除商品 | id | 删除结果JSON |

#### 4.2 AdminController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/admin/dashboard` | GET | 管理员仪表板 | 无 | 仪表板页面 |
| `/admin/users` | GET | 用户管理页面 | 无 | 用户管理页面 |
| `/admin/merchants` | GET | 商家管理页面 | 无 | 商家管理页面 |
| `/admin/merchants/audit` | GET | 商家审核页面 | 无 | 商家审核页面 |
| `/admin/services` | GET | 服务管理页面 | 无 | 服务管理页面 |
| `/admin/products` | GET | 商品管理页面 | 无 | 商品管理页面 |
| `/admin/reviews` | GET | 评价管理页面 | 无 | 评价管理页面 |
| `/admin/announcements` | GET | 公告管理页面 | 无 | 公告管理页面 |
| `/admin/system` | GET | 系统设置页面 | 无 | 系统设置页面 |

### 5. 公共接口

#### 5.1 PublicApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/public/announcements` | GET | 获取公告列表 | page, size | 公告列表JSON |
| `/api/public/announcements/{id}` | GET | 获取公告详情 | id | 公告详情JSON |
| `/api/public/merchants` | GET | 获取商家列表 | page, size, keyword, rating | 商家列表JSON |
| `/api/public/merchants/{id}` | GET | 获取商家详情 | id | 商家详情JSON |
| `/api/public/services` | GET | 获取服务列表 | page, size, keyword, priceMin, priceMax, durationMin, durationMax | 服务列表JSON |
| `/api/public/services/{id}` | GET | 获取服务详情 | id | 服务详情JSON |
| `/api/public/products` | GET | 获取商品列表 | page, size, keyword, priceMin, priceMax, stockMin | 商品列表JSON |
| `/api/public/products/{id}` | GET | 获取商品详情 | id | 商品详情JSON |

#### 5.2 SearchApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/search` | GET | 搜索 | keyword, type | 搜索结果JSON |
| `/api/search/suggestions` | GET | 获取搜索建议 | keyword | 搜索建议JSON |
| `/api/search/history` | GET | 获取搜索历史 | 无 | 搜索历史JSON |
| `/api/search/history` | POST | 添加搜索历史 | keyword | 添加结果JSON |
| `/api/search/history/{id}` | DELETE | 删除搜索历史 | id | 删除结果JSON |
| `/api/search/history/clear` | DELETE | 清空搜索历史 | 无 | 清空结果JSON |

### 6. 其他接口

#### 6.1 CartController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|------|----------|----------|
| `/api/cart` | GET | 获取购物车 | 无 | 购物车JSON |
| `/api/cart` | POST | 添加商品到购物车 | productId, quantity | 添加结果JSON |
| `/api/cart/{id}` | PUT | 更新购物车商品数量 | id, quantity | 更新结果JSON |
| `/api/cart/{id}` | DELETE | 删除购物车商品 | id | 删除结果JSON |
| `/api/cart/clear` | DELETE | 清空购物车 | 无 | 清空结果JSON |

#### 6.2 ProductController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|----------|----------|----------|
| `/api/products` | GET | 获取商品列表 | page, size, keyword, categoryId, priceMin, priceMax, stockMin | 商品列表JSON |
| `/api/products/{id}` | GET | 获取商品详情 | id | 商品详情JSON |
| `/api/products/{id}/reviews` | GET | 获取商品评价 | id, page, size | 评价列表JSON |

#### 6.3 ServiceController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|----------|----------|----------|
| `/api/services` | GET | 获取服务列表 | page, size, keyword, categoryId, priceMin, priceMax, durationMin, durationMax | 服务列表JSON |
| `/api/services/{id}` | GET | 获取服务详情 | id | 服务详情JSON |
| `/api/services/{id}/reviews` | GET | 获取服务评价 | id, page, size | 评价列表JSON |

#### 6.4 AnnouncementApiController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|----------|----------|----------|
| `/api/announcements` | GET | 获取公告列表 | page, size | 公告列表JSON |
| `/api/announcements` | POST | 创建公告 | title, content, status | 创建结果JSON |
| `/api/announcements/{id}` | GET | 获取公告详情 | id | 公告详情JSON |
| `/api/announcements/{id}` | PUT | 更新公告 | id, title, content, status | 更新结果JSON |
| `/api/announcements/{id}` | DELETE | 删除公告 | id | 删除结果JSON |

#### 6.5 RedisLockController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|----------|----------|----------|
| `/api/redis/lock` | POST | 获取Redis锁 | key, expire | 锁获取结果JSON |
| `/api/redis/lock/{key}` | DELETE | 释放Redis锁 | key | 锁释放结果JSON |

### 7. 首页接口

#### 7.1 HomeController

| 路径 | 方法 | 功能 | 请求参数 | 响应格式 |
|------|------|----------|----------|----------|
| `/` | GET | 首页 | 无 | 首页页面 |
| `/home` | GET | 首页 | 无 | 首页页面 |
| `/dashboard` | GET | 仪表板 | 无 | 仪表板页面 |

## 数据传输对象 (DTOs)

### 7.1 认证相关DTO

- `LoginRequest`: 登录请求参数
- `RegisterRequest`: 注册请求参数
- `JwtResponseDTO`: JWT响应
- `LoginResponse`: 登录响应

### 7.2 用户相关DTO

- `UserDTO`: 用户信息
- `UserDetailDTO`: 用户详细信息
- `PetDTO`: 宠物信息

### 7.3 商家相关DTO

- `MerchantDTO`: 商家信息
- `MerchantDetailDTO`: 商家详细信息
- `ServiceDTO`: 服务信息
- `ProductDTO`: 商品信息

### 7.4 订单相关DTO

- `AppointmentDTO`: 预约信息
- `ProductOrderDTO`: 商品订单信息
- `OrderItemDTO`: 订单商品信息
- `OrderPreviewDTO`: 订单预览信息

### 7.5 评价相关DTO

- `ReviewDTO`: 评价信息

### 7.6 公告相关DTO

- `AnnouncementDTO`: 公告信息

### 7.7 其他DTO

- `ApiResponse`: API响应统一格式
- `PageResponse`: 分页响应
- `DashboardStatsDTO`: 仪表板统计信息
- `HomeStatsDTO`: 首页统计信息

## 业务逻辑流程

### 8.1 用户注册流程

1. 接收注册请求
2. 验证密码一致性
3. 检查邮箱是否已注册
4. 创建用户对象
5. 调用用户服务注册
6. 重定向到登录页

### 8.2 用户登录流程

1. 接收登录请求
2. 根据用户类型调用对应服务登录
3. 验证用户凭证
4. 设置会话属性
5. 重定向到对应角色的仪表板

### 8.3 商家注册流程

1. 接收注册请求
2. 验证密码一致性
3. 检查邮箱是否已注册
4. 创建商家对象
5. 调用商家服务注册（状态默认为pending）
6. 重定向到登录页

### 8.4 服务管理流程

1. 商家添加服务
2. 服务保存到数据库
3. 商家更新服务
4. 服务状态更新
5. 商家删除服务
6. 服务从数据库删除

### 8.5 商品管理流程

1. 商家添加商品
2. 商品保存到数据库
3. 商家更新商品
4. 商品信息更新
5. 商家删除商品
6. 商品从数据库删除

### 8.6 预约流程

1. 用户选择服务
2. 用户选择宠物和时间
3. 创建预约订单
4. 商家确认预约
5. 服务完成
6. 用户评价

### 8.7 商品订单流程

1. 用户添加商品到购物车
2. 用户提交订单
3. 支付订单
4. 商家发货
5. 用户确认收货
6. 用户评价

## 安全实现

### 9.1 JWT认证

- 使用jjwt库生成和验证JWT令牌
- 令牌包含用户ID和过期时间
- 令牌用于保护需要认证的API接口

### 9.2 密码加密

- 使用Spring Security的密码编码器
- 密码存储为哈希值
- 登录时验证密码哈希

### 9.3 权限控制

- 基于角色的访问控制
- 角色包括：ROLE_user, ROLE_merchant, ROLE_admin
- 使用Spring Security的注解控制访问权限

## 数据库设计

### 10.1 核心表结构

- **user**: 用户表
- **merchant**: 商家表
- **admin**: 管理员表
- **service**: 服务表
- **product**: 商品表
- **appointment**: 预约表
- **product_order**: 商品订单表
- **product_order_item**: 订单商品表
- **review**: 评价表
- **pet**: 宠物表
- **announcement**: 公告表
- **cart**: 购物车表

### 10.2 表关系

- 用户与宠物：一对多
- 用户与预约：一对多
- 用户与商品订单：一对多
- 用户与评价：一对多
- 商家与服务：一对多
- 商家与商品：一对多
- 商家与预约：一对多
- 商家与商品订单：一对多
- 商家与评价：一对多
- 服务与预约：一对多
- 商品与商品订单：多对多（通过订单商品表）
- 服务与评价：一对多
- 商品与评价：一对多

## 部署配置

### 11.1 环境变量

- `SPRING_DATASOURCE_URL`: 数据库连接URL
- `SPRING_DATASOURCE_USERNAME`: 数据库用户名
- `SPRING_DATASOURCE_PASSWORD`: 数据库密码
- `JWT_SECRET`: JWT密钥

### 11.2 配置文件

- `application.yml`: 应用配置
- `application-test.yml`: 测试配置
- `application-integration.yml`: 集成测试配置

## 监控和日志

### 12.1 监控

- Spring Boot Actuator
- 健康检查端点
- 指标端点

### 12.2 日志

- 系统日志
- 操作日志
- 错误日志

## 测试策略

### 13.1 单元测试

- 服务层测试
- 控制器测试
- 工具类测试

### 13.2 集成测试

- API集成测试
- 数据库集成测试
- 安全集成测试

## 性能优化

### 14.1 数据库优化

- 索引优化
- 查询优化
- 连接池配置

### 14.2 缓存策略

- Redis缓存
- 本地缓存
- 缓存失效策略

### 14.3 并发处理

- 线程池配置
- 异步处理
- 分布式锁

## 未来扩展

### 15.1 功能扩展

- 支付集成
- 消息通知
- 第三方登录
- 数据分析

### 15.2 技术扩展

- 微服务架构
- 容器化部署
- 云服务集成
- CI/CD流程

## 结论

本文档提供了宠物服务平台后端API的详细描述，包括接口路径、请求参数、响应格式、业务逻辑和实现细节。这些信息将为后续的Rust重构提供完整的参考，确保重构后的系统能够保持与原有系统相同的功能和API接口。
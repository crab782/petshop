# 宠物服务平台 - Rust 后端重构项目规范

## 1. 项目概述

### 1.1 背景

宠物服务平台是一个面向宠物主人、商家和管理员的综合性宠物服务平台。当前系统采用 Spring Boot 3.2.0 构建，需要重构为 Rust 后端以提升性能、降低资源消耗并增强内存安全性。

### 1.2 目标

将现有的 Spring Boot 后端项目重构为 Rust 后端，保持与原有系统相同的 API 接口和 MySQL 数据库支持，利用 Rust 的性能优势和内存安全性。

### 1.3 现有系统技术栈

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.x
- **安全**: Spring Security + JWT
- **API文档**: SpringDoc OpenAPI (Swagger)
- **Java版本**: 17

### 1.4 目标技术栈

- **框架**: Actix Web 4.x
- **ORM**: Diesel 2.x
- **数据库**: MySQL 8.x
- **安全**: JWT (jsonwebtoken crate)
- **API文档**: utoipa
- **Rust版本**: 1.94.1+

## 2. 技术选型

### 2.1 Web 框架选择

选择 **Actix Web** 作为 Rust Web 框架，原因如下：

| 框架 | 优点 | 缺点 |
|------|------|------|
| Actix Web | 性能最高、异步支持好、社区活跃 | 学习曲线较陡 |
| Axum | 现代化、与 Tower 生态兼容 | 相对较新 |
| Rocket | 易于使用、类型安全 | 同步为主 |
| Warp | 轻量级、组合式 | 文档较少 |

**最终选择**: Actix Web - 性能最优，广泛用于生产环境

### 2.2 ORM 选择

选择 **Diesel** 作为 ORM，原因如下：

| ORM | 优点 | 缺点 |
|-----|------|------|
| Diesel | 类型安全、查询构建器、迁移工具 | 仅支持 PostgreSQL/MySQL/SQLite |
| SQLx | 异步、编译时检查 | API 较底层 |
| SeaORM | 异步、实体关系支持 | 相对较重 |
| Rusqlite | 轻量级、嵌入式 | 仅支持 SQLite |

**最终选择**: Diesel - 类型安全、功能完整、与 MySQL 兼容

### 2.3 认证方案

使用 **JWT (jsonwebtoken)** 和 **bcrypt** 进行认证：

- **jsonwebtoken**: JWT 令牌生成和验证
- **bcrypt**: 密码哈希和验证

### 2.4 API 文档

使用 **utoipa** 和 **utoipa-swagger-ui** 生成 OpenAPI 文档和 Swagger UI。

## 3. 项目结构

### 3.1 目录结构

```
petshop-rust/
├── src/
│   ├── main.rs                 # 应用入口
│   ├── lib.rs                  # 库入口
│   ├── api/                    # API 路由和控制器
│   │   ├── mod.rs              # API 模块导出
│   │   ├── auth.rs             # 认证相关接口
│   │   ├── user.rs             # 用户相关接口
│   │   ├── merchant.rs         # 商家相关接口
│   │   ├── admin.rs            # 管理员相关接口
│   │   ├── public.rs           # 公共接口
│   │   ├── product.rs          # 商品接口
│   │   ├── service.rs          # 服务接口
│   │   ├── cart.rs             # 购物车接口
│   │   ├── order.rs            # 订单接口
│   │   ├── appointment.rs      # 预约接口
│   │   ├── review.rs           # 评价接口
│   │   ├── announcement.rs     # 公告接口
│   │   └── search.rs           # 搜索接口
│   ├── services/               # 业务逻辑层
│   │   ├── mod.rs              # 服务模块导出
│   │   ├── auth_service.rs     # 认证服务
│   │   ├── user_service.rs     # 用户服务
│   │   ├── merchant_service.rs  # 商家服务
│   │   ├── admin_service.rs    # 管理员服务
│   │   ├── product_service.rs  # 商品服务
│   │   ├── service_service.rs  # 服务服务
│   │   ├── order_service.rs    # 订单服务
│   │   ├── cart_service.rs     # 购物车服务
│   │   └── review_service.rs   # 评价服务
│   ├── models/                 # 数据模型
│   │   ├── mod.rs              # 模型模块导出
│   │   ├── user.rs             # 用户模型
│   │   ├── merchant.rs         # 商家模型
│   │   ├── admin.rs            # 管理员模型
│   │   ├── product.rs          # 商品模型
│   │   ├── service.rs          # 服务模型
│   │   ├── order.rs            # 订单模型
│   │   ├── appointment.rs      # 预约模型
│   │   ├── review.rs           # 评价模型
│   │   ├── cart.rs             # 购物车模型
│   │   ├── pet.rs              # 宠物模型
│   │   ├── announcement.rs    # 公告模型
│   │   ├── favorite.rs         # 收藏模型
│   │   └── notification.rs     # 通知模型
│   ├── schemas/                # 数据传输对象 (DTO)
│   │   ├── mod.rs              # 模式模块导出
│   │   ├── auth.rs             # 认证相关 DTO
│   │   ├── user.rs             # 用户相关 DTO
│   │   ├── merchant.rs          # 商家相关 DTO
│   │   ├── admin.rs            # 管理员相关 DTO
│   │   ├── product.rs          # 商品相关 DTO
│   │   ├── service.rs          # 服务相关 DTO
│   │   ├── order.rs            # 订单相关 DTO
│   │   ├── appointment.rs      # 预约相关 DTO
│   │   ├── review.rs           # 评价相关 DTO
│   │   ├── cart.rs             # 购物车相关 DTO
│   │   ├── announcement.rs     # 公告相关 DTO
│   │   └── common.rs           # 通用 DTO
│   ├── database/                # 数据库相关
│   │   ├── mod.rs              # 数据库模块导出
│   │   ├── connection.rs       # 数据库连接
│   │   ├── pool.rs             # 连接池管理
│   │   └── repository/         # 数据访问层
│   │       ├── mod.rs
│   │       ├── user_repo.rs
│   │       ├── merchant_repo.rs
│   │       └── ...
│   ├── security/               # 安全相关
│   │   ├── mod.rs              # 安全模块导出
│   │   ├── jwt.rs              # JWT 工具
│   │   ├── password.rs         # 密码加密
│   │   ├── middleware.rs        # 认证中间件
│   │   └── guards.rs           # 路由守卫
│   ├── config/                 # 配置
│   │   ├── mod.rs              # 配置模块导出
│   │   ├── app.rs              # 应用配置
│   │   ├── database.rs         # 数据库配置
│   │   └── security.rs         # 安全配置
│   ├── error/                  # 错误处理
│   │   ├── mod.rs              # 错误模块导出
│   │   ├── app_error.rs        # 应用错误
│   │   └── error_handler.rs    # 错误处理器
│   └── utils/                  # 工具函数
│       ├── mod.rs              # 工具模块导出
│       ├── response.rs         # 响应工具
│       ├── validation.rs       # 验证工具
│       └── date.rs             # 日期工具
├── migrations/                  # 数据库迁移 (Diesel)
│   ├── 2024-01-01-000001_create_users/
│   │   ├── up.sql
│   │   └── down.sql
│   └── ...
├── tests/                      # 测试
│   ├── api/
│   │   ├── auth_tests.rs
│   │   ├── user_tests.rs
│   │   └── ...
│   └── integration/
├── Cargo.toml                  # 依赖管理
├── Cargo.lock                  # 依赖锁定
├── .env                        # 环境变量
├── .env.example                # 环境变量示例
├── diesel.toml                 # Diesel 配置
├── rustfmt.toml                # 代码格式化配置
├── .clippy.toml                # Clippy 配置
└── README.md                   # 项目说明
```

### 3.2 依赖管理 (Cargo.toml)

```toml
[package]
name = "petshop-rust"
version = "0.1.0"
edition = "2021"
authors = ["PetShop Team"]
description = "Pet Service Platform Backend API"

[lib]
name = "petshop_rust"
path = "src/lib.rs"

[[bin]]
name = "petshop-rust"
path = "src/main.rs"

[dependencies]
# Web 框架
actix-web = "4.5"
actix-rt = "2.9"
actix-service = "2.0"
actix-utils = "3.0"

# 数据库
diesel = { version = "2.1", features = ["mysql", "r2d2", "chrono", "uuid"] }
diesel_migrations = "2.1"
r2d2 = "0.8"

# 异步运行时
tokio = { version = "1", features = ["full"] }

# 认证
jsonwebtoken = "9.2"
bcrypt = "0.15"

# 配置
dotenv = "0.15"
config = "0.14"

# 序列化
serde = { version = "1.0", features = ["derive"] }
serde_json = "1.0"

# 日期时间
chrono = { version = "0.4", features = ["serde"] }

# UUID
uuid = { version = "1.6", features = ["v4", "serde"] }

# API 文档
utoipa = "4.0"
utoipa-swagger-ui = "4.0"

# 验证
validator = { version = "0.18", features = ["derive"] }

# 日志
log = "0.4"
env_logger = "0.10"
tracing = "0.1"
tracing-subscriber = "0.3"

# 错误处理
thiserror = "1.0"
anyhow = "1.0"

# 密码
ring = "0.17"

# 随机数
rand = "0.8"

[dev-dependencies]
# 测试
actix-rt = "2.9"
mockall = "0.12"
fake = { version = "3", features = ["derive"] }
tempfile = "3"
tokio-test = "0.4"

[profile.release]
opt-level = 3
lto = true
codegen-units = 1
```

## 4. 数据库设计

### 4.1 核心表结构

参考现有的 Spring Boot 项目的数据库设计，创建以下表：

```sql
-- 用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    avatar VARCHAR(255),
    status ENUM('active', 'disabled') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商家表
CREATE TABLE merchants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    logo VARCHAR(255),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 管理员表
CREATE TABLE admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 服务表
CREATE TABLE services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    merchant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration INT NOT NULL COMMENT '服务时长（分钟）',
    image VARCHAR(255),
    status ENUM('enabled', 'disabled') DEFAULT 'enabled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

-- 商品表
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    merchant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    image VARCHAR(255),
    status ENUM('enabled', 'disabled') DEFAULT 'enabled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

-- 宠物表
CREATE TABLE pets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    breed VARCHAR(50),
    age INT,
    gender ENUM('male', 'female'),
    avatar VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 预约表
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    service_id INT NOT NULL,
    pet_id INT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status ENUM('pending', 'confirmed', 'completed', 'cancelled') DEFAULT 'pending',
    total_price DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (pet_id) REFERENCES pets(id)
);

-- 商品订单表
CREATE TABLE product_orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'paid', 'shipped', 'completed', 'cancelled') DEFAULT 'pending',
    shipping_address VARCHAR(255),
    logistics_company VARCHAR(100),
    tracking_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

-- 订单商品表
CREATE TABLE product_order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES product_orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 评价表
CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    service_id INT,
    appointment_id INT,
    product_id INT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    reply TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 购物车表
CREATE TABLE carts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 公告表
CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('draft', 'published') DEFAULT 'draft',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 收藏表
CREATE TABLE favorites (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    merchant_id INT,
    service_id INT,
    product_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 通知表
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 地址表
CREATE TABLE addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 5. API 接口设计

### 5.1 认证接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/auth/login` | POST | 用户登录 | `{loginIdentifier, password}` | `{token, userId, username}` |
| `/api/auth/register` | POST | 用户注册 | `{phone, password, username?, email?}` | `{token, userId, username}` |
| `/api/auth/logout` | POST | 退出登录 | - | `{success: true}` |
| `/api/auth/userinfo` | GET | 获取当前用户 | - | `User` |
| `/api/auth/userinfo` | PUT | 更新用户信息 | `User` | `User` |
| `/api/auth/password` | PUT | 修改密码 | `{oldPassword, newPassword}` | `{success: true}` |
| `/api/auth/sendVerifyCode` | POST | 发送验证码 | `{email}` | `{success: true}` |
| `/api/auth/resetPassword` | POST | 重置密码 | `{email, verifyCode, password}` | `{success: true}` |
| `/api/auth/merchant/login` | POST | 商家登录 | `{loginIdentifier, password}` | `{token, merchantId, name}` |
| `/api/auth/merchant/register` | POST | 商家注册 | `{phone, password, name, contactPerson, address, email?}` | `{success: true}` |
| `/api/auth/admin/login` | POST | 管理员登录 | `{loginIdentifier, password}` | `{token, adminId, username}` |
| `/api/auth/admin/register` | POST | 管理员注册 | `{username, password}` | `{success: true}` |

### 5.2 用户接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/user/profile` | GET | 获取用户资料 | - | `User` |
| `/api/user/profile` | PUT | 更新用户资料 | `User` | `User` |
| `/api/user/home/stats` | GET | 获取首页统计 | - | `HomeStatsDTO` |
| `/api/user/home/activities` | GET | 获取最近活动 | - | `ActivityDTO[]` |
| `/api/user/pets` | GET | 获取宠物列表 | - | `Pet[]` |
| `/api/user/pets` | POST | 添加宠物 | `Pet` | `Pet` |
| `/api/user/pets/{id}` | GET | 获取宠物详情 | - | `Pet` |
| `/api/user/pets/{id}` | PUT | 更新宠物 | `Pet` | `Pet` |
| `/api/user/pets/{id}` | DELETE | 删除宠物 | - | - |
| `/api/user/appointments` | GET | 获取预约列表 | - | `Appointment[]` |
| `/api/user/appointments/{id}` | GET | 获取预约详情 | - | `AppointmentDTO` |
| `/api/user/appointments` | POST | 创建预约 | `CreateAppointmentRequest` | `{id}` |
| `/api/user/appointments/{id}/cancel` | PUT | 取消预约 | - | - |
| `/api/user/orders` | GET | 获取订单列表 | - | `OrderDTO[]` |
| `/api/user/orders/{id}` | GET | 获取订单详情 | - | `OrderDTO` |
| `/api/user/orders` | POST | 创建订单 | `CreateOrderRequest` | `CreateOrderResponse` |
| `/api/user/orders/{id}/pay` | POST | 支付订单 | `PayOrderRequest` | `PayResponse` |
| `/api/user/addresses` | GET | 获取地址列表 | - | `AddressDTO[]` |
| `/api/user/addresses` | POST | 添加地址 | `Address` | `AddressDTO` |
| `/api/user/addresses/{id}` | PUT | 更新地址 | `Address` | `AddressDTO` |
| `/api/user/addresses/{id}` | DELETE | 删除地址 | - | - |
| `/api/user/addresses/{id}/default` | PUT | 设置默认地址 | - | `AddressDTO` |
| `/api/user/favorites` | GET | 获取收藏列表 | - | `FavoriteDTO[]` |
| `/api/user/favorites` | POST | 添加收藏 | `{merchantId}` | `FavoriteDTO` |
| `/api/user/favorites/{id}` | DELETE | 删除收藏 | - | - |
| `/api/user/notifications` | GET | 获取通知列表 | - | `NotificationDTO[]` |
| `/api/user/notifications/{id}/read` | PUT | 标记已读 | - | - |

### 5.3 商家接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/merchant/profile` | GET | 获取商家资料 | - | `Merchant` |
| `/api/merchant/profile` | PUT | 更新商家资料 | `Merchant` | `Merchant` |
| `/api/merchant/services` | GET | 获取服务列表 | - | `Service[]` |
| `/api/merchant/services` | POST | 添加服务 | `Service` | `Service` |
| `/api/merchant/services/{id}` | GET | 获取服务详情 | - | `Service` |
| `/api/merchant/services/{id}` | PUT | 更新服务 | `Service` | `Service` |
| `/api/merchant/services/{id}` | DELETE | 删除服务 | - | - |
| `/api/merchant/services/batch/status` | PUT | 批量更新状态 | `{ids, status}` | - |
| `/api/merchant/products` | GET | 获取商品列表 | - | `Product[]` |
| `/api/merchant/products` | POST | 添加商品 | `Product` | `Product` |
| `/api/merchant/products/{id}` | GET | 获取商品详情 | - | `Product` |
| `/api/merchant/products/{id}` | PUT | 更新商品 | `Product` | `Product` |
| `/api/merchant/products/{id}` | DELETE | 删除商品 | - | - |
| `/api/merchant/appointments` | GET | 获取预约列表 | - | `Appointment[]` |
| `/api/merchant/appointments/{id}/status` | PUT | 更新预约状态 | `{status}` | `Appointment` |
| `/api/merchant/orders` | GET | 获取商品订单 | - | `ProductOrder[]` |
| `/api/merchant/orders/{id}/status` | PUT | 更新订单状态 | `{status}` | `ProductOrder` |
| `/api/merchant/reviews` | GET | 获取评价列表 | - | `Review[]` |
| `/api/merchant/reviews/{id}/reply` | PUT | 回复评价 | `{reply}` | `Review` |
| `/api/merchant/dashboard` | GET | 获取统计数据 | - | `DashboardStats` |
| `/api/merchant/revenue-stats` | GET | 获取营收统计 | - | `RevenueStats` |

### 5.4 管理员接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/admin/users` | GET | 获取用户列表 | - | `User[]` |
| `/api/admin/users/{id}` | GET | 获取用户详情 | - | `UserDetailDTO` |
| `/api/admin/users/{id}/status` | PUT | 更新用户状态 | `{status}` | `User` |
| `/api/admin/users/{id}` | DELETE | 删除用户 | - | - |
| `/api/admin/users/batch/status` | PUT | 批量更新状态 | `{ids, status}` | - |
| `/api/admin/users/batch` | DELETE | 批量删除 | `{ids}` | - |
| `/api/admin/merchants` | GET | 获取商家列表 | - | `Merchant[]` |
| `/api/admin/merchants/pending` | GET | 获取待审核商家 | - | `Merchant[]` |
| `/api/admin/merchants/{id}` | GET | 获取商家详情 | - | `MerchantDetailDTO` |
| `/api/admin/merchants/{id}/audit` | PUT | 审核商家 | `{status, reason?}` | `Merchant` |
| `/api/admin/merchants/{id}` | DELETE | 删除商家 | - | - |
| `/api/admin/merchants/batch/status` | PUT | 批量更新状态 | `{ids, status}` | - |
| `/api/admin/merchants/batch` | DELETE | 批量删除 | `{ids}` | - |
| `/api/admin/dashboard` | GET | 获取平台统计 | - | `DashboardStatsDTO` |
| `/api/admin/announcements` | GET | 获取公告列表 | - | `Announcement[]` |
| `/api/admin/announcements` | POST | 创建公告 | `Announcement` | `Announcement` |
| `/api/admin/announcements/{id}` | PUT | 更新公告 | `Announcement` | `Announcement` |
| `/api/admin/announcements/{id}` | DELETE | 删除公告 | - | - |
| `/api/admin/announcements/{id}/publish` | PUT | 发布公告 | - | `Announcement` |
| `/api/admin/announcements/{id}/unpublish` | PUT | 下架公告 | - | `Announcement` |
| `/api/admin/system/settings` | GET | 获取系统设置 | - | `SystemSettings` |
| `/api/admin/system/settings` | PUT | 更新系统设置 | `SystemSettings` | `SystemSettings` |

### 5.5 公共接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/public/services` | GET | 获取服务列表 | - | `Service[]` |
| `/api/public/services/{id}` | GET | 获取服务详情 | - | `Service` |
| `/api/public/merchants` | GET | 获取商家列表 | - | `Merchant[]` |
| `/api/public/merchants/{id}` | GET | 获取商家详情 | - | `Merchant` |
| `/api/public/merchants/{id}/services` | GET | 获取商家服务 | - | `Service[]` |
| `/api/search/suggestions` | GET | 搜索建议 | `keyword` | `SearchSuggestionsDTO` |
| `/api/search/hot-keywords` | GET | 热门关键字 | - | `HotKeywordDTO[]` |

### 5.6 商品和服务接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/products` | GET | 获取商品列表 | - | `Product[]` |
| `/api/products/{id}` | GET | 获取商品详情 | - | `Product` |
| `/api/products/{id}/reviews` | GET | 获取商品评价 | - | `Review[]` |
| `/api/services` | GET | 获取服务列表 | - | `Service[]` |
| `/api/services/{id}` | GET | 获取服务详情 | - | `Service` |
| `/api/services/{id}/reviews` | GET | 获取服务评价 | - | `Review[]` |
| `/api/services/recommended` | GET | 获取推荐服务 | - | `Service[]` |
| `/api/announcements` | GET | 获取公告列表 | - | `Announcement[]` |
| `/api/announcements/{id}` | GET | 获取公告详情 | - | `Announcement` |

### 5.7 购物车接口

| 路径 | 方法 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| `/api/user/cart` | GET | 获取购物车 | - | `Cart[]` |
| `/api/user/cart` | POST | 添加到购物车 | `{productId, quantity}` | `Cart` |
| `/api/user/cart` | PUT | 更新购物车 | `{cartId, quantity}` | `Cart` |
| `/api/user/cart/{id}` | DELETE | 删除购物车商品 | - | - |
| `/api/user/cart/batch` | DELETE | 批量删除 | `{ids}` | - |

## 6. 安全设计

### 6.1 JWT 认证

```rust
// src/security/jwt.rs
use jsonwebtoken::{encode, decode, Header, Algorithm, Validation};
use serde::{Deserialize, Serialize};
use chrono::{Utc, Duration};

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub sub: String,        // 用户ID
    pub role: String,       // 用户角色
    pub exp: usize,         // 过期时间
    pub iat: usize,         // 签发时间
}

pub fn generate_token(user_id: i32, role: &str, secret: &str) -> Result<String, AppError> {
    let expiration = Utc::now()
        .checked_add_signed(Duration::hours(24))
        .expect("valid timestamp")
        .timestamp() as usize;

    let claims = Claims {
        sub: user_id.to_string(),
        role: role.to_string(),
        exp: expiration,
        iat: Utc::now().timestamp() as usize,
    };

    let token = encode(&Header::new(Algorithm::HS256), &claims, secret.as_bytes())?;
    Ok(token)
}

pub fn validate_token(token: &str, secret: &str) -> Result<Claims, AppError> {
    let validation = Validation::new(Algorithm::HS256);
    let token_data = decode::<Claims>(token, secret.as_bytes(), &validation)?;
    Ok(token_data.claims)
}
```

### 6.2 密码加密

```rust
// src/security/password.rs
use bcrypt::{hash, verify, DEFAULT_COST};

pub fn hash_password(password: &str) -> Result<String, AppError> {
    hash(password, DEFAULT_COST).map_err(|e| AppError::InternalError(e.to_string()))
}

pub fn verify_password(password: &str, hashed: &str) -> Result<bool, AppError> {
    verify(password, hashed).map_err(|e| AppError::InternalError(e.to_string()))
}
```

### 6.3 认证中间件

```rust
// src/security/middleware.rs
use actix_web::{
    dev::{forward_ready, Service, ServiceRequest, ServiceResponse, Transform},
    Error, HttpMessage,
};
use futures::future::{ok, LocalBoxFuture, Ready};
use std::rc::Rc;

pub struct AuthMiddleware;

impl<S, B> Transform<S, ServiceRequest> for AuthMiddleware
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error> + 'static,
    S::Future: 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type InitError = ();
    type Transform = AuthMiddlewareService<S>;
    type Future = Ready<Result<Self::Transform, Self::InitError>>;

    fn new_transform(&self, service: S) -> Self::Future {
        ok(AuthMiddlewareService {
            service: Rc::new(service),
        })
    }
}

pub struct AuthMiddlewareService<S> {
    service: Rc<S>,
}

impl<S, B> Service<ServiceRequest> for AuthMiddlewareService<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error> + 'static,
    S::Future: 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    forward_ready!(service);

    fn call(&self, req: ServiceRequest) -> Self::Future {
        let service = self.service.clone();

        Box::pin(async move {
            // 验证 JWT token
            let token = req
                .headers()
                .get("Authorization")
                .and_then(|h| h.to_str().ok())
                .and_then(|h| h.strip_prefix("Bearer "));

            if let Some(token) = token {
                if let Ok(claims) = validate_token(token, &get_jwt_secret()) {
                    req.extensions_mut().insert(claims);
                }
            }

            service.call(req).await
        })
    }
}
```

## 7. 错误处理

### 7.1 应用错误定义

```rust
// src/error/app_error.rs
use thiserror::Error;
use actix_web::{HttpResponse, ResponseError};
use serde::Serialize;

#[derive(Error, Debug)]
pub enum AppError {
    #[error("认证失败: {0}")]
    Unauthorized(String),

    #[error("禁止访问: {0}")]
    Forbidden(String),

    #[error("资源未找到: {0}")]
    NotFound(String),

    #[error("请求参数错误: {0}")]
    BadRequest(String),

    #[error("内部服务器错误: {0}")]
    InternalError(String),

    #[error("数据库错误: {0}")]
    DatabaseError(String),
}

#[derive(Serialize)]
struct ErrorResponse {
    pub success: bool,
    pub message: String,
    pub code: String,
}

impl ResponseError for AppError {
    fn error_response(&self) -> HttpResponse {
        let (status, code) = match self {
            AppError::Unauthorized(_) => (actix_web::http::StatusCode::UNAUTHORIZED, "UNAUTHORIZED"),
            AppError::Forbidden(_) => (actix_web::http::StatusCode::FORBIDDEN, "FORBIDDEN"),
            AppError::NotFound(_) => (actix_web::http::StatusCode::NOT_FOUND, "NOT_FOUND"),
            AppError::BadRequest(_) => (actix_web::http::StatusCode::BAD_REQUEST, "BAD_REQUEST"),
            AppError::InternalError(_) => (actix_web::http::StatusCode::INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"),
            AppError::DatabaseError(_) => (actix_web::http::StatusCode::INTERNAL_SERVER_ERROR, "DATABASE_ERROR"),
        };

        HttpResponse::build(status).json(ErrorResponse {
            success: false,
            message: self.to_string(),
            code: code.to_string(),
        })
    }
}
```

## 8. 项目创建步骤

### 8.1 创建 Rust 项目

```bash
# 创建新的 Rust 项目
cargo new petshop-rust
cd petshop-rust

# 添加必要的依赖到 Cargo.toml
# (见上文 3.2 节)
```

### 8.2 配置 Diesel

```bash
# 安装 Diesel CLI
cargo install diesel_cli --no-default-features --features mysql

# 初始化 Diesel
diesel setup

# 创建迁移
diesel migration generate create_users
```

### 8.3 配置环境变量

创建 `.env` 文件：

```env
DATABASE_URL=mysql://root:123456@localhost:3306/petshop
JWT_SECRET=your_super_secret_jwt_key_change_in_production
RUST_LOG=info
PORT=8080
```

### 8.4 项目初始化检查清单

- [ ] 创建项目目录结构
- [ ] 配置 Cargo.toml 依赖
- [ ] 配置 Diesel 数据库迁移
- [ ] 创建数据库连接池
- [ ] 实现基础错误处理
- [ ] 实现 JWT 安全模块
- [ ] 实现密码加密模块
- [ ] 验证项目编译通过

## 9. 重构实施计划

### 9.1 阶段一：项目初始化 (1-2天)

1. 创建 Rust 项目结构
2. 配置依赖管理
3. 配置 Diesel 数据库迁移
4. 建立数据库连接
5. 验证项目能正常编译运行

### 9.2 阶段二：核心模块实现 (3-5天)

1. 实现认证功能 (用户、商家、管理员)
2. 实现用户管理模块
3. 实现商家管理模块
4. 实现管理员模块

### 9.3 阶段三：业务模块实现 (5-7天)

1. 实现服务管理
2. 实现商品管理
3. 实现预约管理
4. 实现订单管理
5. 实现评价管理

### 9.4 阶段四：辅助模块实现 (2-3天)

1. 实现搜索功能
2. 实现公告功能
3. 实现通知功能
4. 实现收藏功能

### 9.5 阶段五：测试和部署 (2-3天)

1. 编写单元测试
2. 编写集成测试
3. 性能测试
4. 配置 Docker 部署
5. 上线部署

## 10. 注意事项

### 10.1 数据库兼容性

- 确保 MySQL 版本兼容 (8.x)
- 注意字符集和排序规则
- 处理 datetime 和 timestamp 时区问题

### 10.2 API 兼容性

- 保持与原有 Spring Boot API 路径一致
- 确保请求参数和响应格式一致
- 处理分页、筛选等复杂查询

### 10.3 安全性

- JWT 密钥必须足够复杂
- 密码加密使用 bcrypt
- 防止 SQL 注入 (使用 Diesel 参数化查询)
- 防止 XSS (对输出进行转义)

### 10.4 性能考虑

- 使用连接池管理数据库连接
- 实现适当的缓存策略
- 注意异步处理的使用

## 11. 验收标准

### 11.1 功能验收

- 所有 API 接口能正常响应
- API 路径和参数与原有系统一致
- 错误处理机制完善

### 11.2 性能验收

- 响应时间 < 100ms (简单查询)
- 支持并发请求 > 1000 QPS
- 内存使用合理

### 11.3 安全验收

- JWT 认证正常工作
- 密码加密安全
- 无 SQL 注入漏洞
- 无 XSS 漏洞

### 11.4 代码质量

- 通过 clippy 检查
- 通过 rustfmt 格式化
- 测试覆盖率 > 80%
- 无编译警告
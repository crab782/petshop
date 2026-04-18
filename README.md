# 🐾 宠物服务平台 (Pet Management System)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5.31-4fc08d.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-3178c6.svg)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-4479a1.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个功能完善的宠物服务管理平台，为宠物主人、商家和平台管理员提供全方位的服务管理解决方案。

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [功能特性](#功能特性)
- [API 文档](#api-文档)
- [数据库设计](#数据库设计)
- [开发指南](#开发指南)
- [测试](#测试)
- [部署](#部署)
- [贡献指南](#贡献指南)
- [许可证](#许可证)
- [联系方式](#联系方式)

---

## 项目概述

### 🎯 项目背景

随着宠物经济的快速发展，宠物服务行业对数字化管理的需求日益增长。本项目旨在构建一个集宠物服务预约、商品购买、订单管理、评价系统于一体的综合性平台，连接宠物主人、服务商家和平台运营方。

### ✨ 主要功能特性

#### 👤 用户端（28个页面）
- 🐕 宠物管理 - 添加、编辑、查看宠物信息
- 📅 服务预约 - 浏览服务、在线预约、预约管理
- 🛒 商品购买 - 商品浏览、购物车、下单支付
- 📦 订单管理 - 订单查询、状态跟踪、订单详情
- ⭐ 评价系统 - 服务评价、商品评价、评价管理
- ❤️ 收藏功能 - 收藏商家、收藏服务
- 💬 论坛社区 - 发帖讨论、评论互动

#### 🏪 商家端（16个页面）
- 🎯 服务管理 - 服务发布、编辑、上下架
- 📦 商品管理 - 商品库存、价格、信息管理
- 📋 预约管理 - 预约确认、状态更新
- 🛍️ 订单处理 - 商品订单发货、状态管理
- 💬 评价管理 - 查看评价、回复评价
- 🏠 店铺管理 - 店铺信息、营业状态设置
- 📊 数据统计 - 预约统计、营收分析

#### 🔧 平台端（20个页面）
- 👥 用户管理 - 用户信息、状态管理
- 🏪 商家管理 - 商家审核、状态管理
- 📝 内容审核 - 商家入驻审核、评价审核
- 📢 公告管理 - 发布、编辑系统公告
- ⚙️ 系统设置 - 平台配置、权限管理
- 📈 数据监控 - 平台运营数据统计

### 🌟 项目亮点

- **三端分离架构**：用户端、商家端、平台端独立运行，职责清晰
- **现代化技术栈**：采用 Vue 3 + Spring Boot 3 最新技术
- **完善的权限系统**：基于 Spring Security 的安全认证
- **Mock 数据支持**：前端开发阶段可脱离后端独立运行
- **完善的测试覆盖**：单元测试 + E2E 测试双重保障
- **API 文档自动生成**：Swagger/OpenAPI 文档

---

## 技术栈

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| [Vue](https://vuejs.org/) | 3.5.31 | 渐进式 JavaScript 框架 |
| [TypeScript](https://www.typescriptlang.org/) | 6.0 | JavaScript 超集，类型安全 |
| [Vue Router](https://router.vuejs.org/) | 5.0.4 | Vue.js 官方路由 |
| [Pinia](https://pinia.vuejs.org/) | 3.0.4 | Vue.js 状态管理 |
| [Vite](https://vitejs.dev/) | 8.0.3 | 下一代前端构建工具 |
| [Element Plus](https://element-plus.org/) | 2.13.7 | Vue 3 组件库 |
| [Axios](https://axios-http.com/) | 1.15.0 | HTTP 客户端 |
| [Vitest](https://vitest.dev/) | 4.1.2 | 单元测试框架 |
| [Playwright](https://playwright.dev/) | 1.58.2 | E2E 测试框架 |

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 3.2.0 | 快速开发框架 |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | - | ORM 框架 |
| [Spring Security](https://spring.io/projects/spring-security) | - | 安全框架 |
| [Spring Validation](https://spring.io/) | - | 数据验证 |
| [MySQL](https://www.mysql.com/) | 8.x | 关系型数据库 |
| [SpringDoc OpenAPI](https://springdoc.org/) | 2.3.0 | API 文档生成 |
| [Lombok](https://projectlombok.org/) | - | 代码简化工具 |

### 开发工具

- **Maven** - 项目构建管理
- **Node.js** - JavaScript 运行环境
- **npm** - 包管理器
- **vite-plugin-mock** - Mock 数据服务
- **ESLint + Oxlint** - 代码检查
- **Prettier** - 代码格式化

---

## 快速开始

### 环境要求

| 环境 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | 20.19.0+ 或 22.12.0+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |

### 安装步骤

#### 1. 克隆项目

```bash
git clone <repository-url>
cd cg
```

#### 2. 数据库配置

创建 MySQL 数据库并导入初始数据：

```sql
CREATE DATABASE cg CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

导入数据库脚本：

```bash
mysql -u root -p cg < cg.sql
```

#### 3. 后端配置

修改 `src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/cg?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&createDatabaseIfNotExist=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# 服务器配置
server.port=8080
```

启动后端服务：

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

#### 4. 前端配置

进入前端目录：

```bash
cd cg-vue
```

安装依赖：

```bash
npm install
```

配置环境变量（可选）：

创建 `.env.local` 文件：

```env
VITE_USE_MOCK=false
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=宠物服务平台
```

启动开发服务器：

```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

### 配置说明

#### Mock 模式

前端支持 Mock 数据模式，可在不启动后端的情况下进行开发：

```env
# 开启 Mock 模式
VITE_USE_MOCK=true
```

#### 环境变量说明

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `VITE_USE_MOCK` | 是否使用 Mock 数据 | `true` |
| `VITE_API_BASE_URL` | 后端 API 地址 | `http://localhost:8080` |
| `VITE_APP_TITLE` | 应用标题 | `宠物服务平台` |

---

## 项目结构

```
cg/
├── cg-vue/                    # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口定义
│   │   │   ├── admin.ts       # 管理员 API
│   │   │   ├── auth.ts        # 认证 API
│   │   │   ├── merchant.ts    # 商家 API
│   │   │   └── user.ts        # 用户 API
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── mock/              # Mock 数据
│   │   │   ├── admin/         # 平台端 Mock
│   │   │   ├── user/          # 用户端 Mock
│   │   │   └── merchant.ts    # 商家端 Mock
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   │   ├── admin.ts       # 平台端状态
│   │   │   ├── merchant.ts    # 商家端状态
│   │   │   └── user.ts        # 用户端状态
│   │   ├── tests/             # 测试工具
│   │   └── views/             # 页面组件
│   │       ├── admin/         # 平台端页面
│   │       ├── merchant/      # 商家端页面
│   │       ├── user/          # 用户端页面
│   │       └── login/         # 登录注册
│   ├── public/                # 公共资源
│   ├── e2e/                   # E2E 测试
│   └── package.json           # 前端依赖配置
│
├── src/main/                  # 后端项目
│   ├── java/com/petshop/
│   │   ├── config/            # 配置类
│   │   │   ├── SecurityConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/        # 控制器
│   │   │   ├── api/           # REST API
│   │   │   ├── AdminController.java
│   │   │   ├── MerchantController.java
│   │   │   └── UserController.java
│   │   ├── entity/            # 实体类
│   │   ├── repository/        # 数据访问层
│   │   └── service/           # 业务逻辑层
│   └── resources/
│       ├── static/            # 静态页面
│       └── application.properties
│
├── cg.sql                     # 数据库脚本
├── pom.xml                    # Maven 配置
└── README.md                  # 项目文档
```

### 主要模块介绍

| 模块 | 路径 | 说明 |
|------|------|------|
| 用户端 | `cg-vue/src/views/user/` | 宠物主人使用的功能模块 |
| 商家端 | `cg-vue/src/views/merchant/` | 商家运营管理模块 |
| 平台端 | `cg-vue/src/views/admin/` | 平台管理员后台 |
| Mock 服务 | `cg-vue/src/mock/` | 前端开发模拟数据 |
| API 层 | `cg-vue/src/api/` | 接口请求封装 |

---

## 功能特性

### 用户端功能

<details>
<summary>📖 点击展开详细功能列表</summary>

| 页面 | 路径 | 功能描述 |
|------|------|----------|
| 用户首页 | `user-home/` | 统计概览、最近活动、推荐服务 |
| 我的宠物 | `user-pets/` | 宠物列表、添加/编辑宠物 |
| 服务列表 | `service-list/` | 浏览所有服务、筛选排序 |
| 服务详情 | `service-detail/` | 服务信息、在线预约 |
| 预约确认 | `appointment-confirm/` | 选择宠物、时间、提交预约 |
| 我的预约 | `user-appointments/` | 预约列表、状态管理 |
| 商品详情 | `product-detail/` | 商品信息、加入购物车 |
| 购物车 | `user-cart/` | 商品管理、结算 |
| 下单确认 | `checkout/` | 地址选择、支付方式 |
| 支付页面 | `pay/` | 支付流程、状态展示 |
| 我的订单 | `user-orders/` | 订单列表、状态跟踪 |
| 订单详情 | `order-detail/` | 订单信息、物流跟踪 |
| 我的评价 | `my-reviews/` | 评价管理、编辑删除 |
| 收藏管理 | `user-favorites/` | 收藏商家列表 |
| 收货地址 | `addresses/` | 地址管理、默认设置 |
| 消息通知 | `notifications/` | 系统通知、状态管理 |
| 个人中心 | `user-profile/` | 个人信息、快捷入口 |
| 资料编辑 | `profile-edit/` | 修改个人信息 |
| 商家列表 | `user-merchant/` | 浏览商家、收藏 |
| 店铺详情 | `user-shop/` | 商家信息、服务商品 |
| 搜索页面 | `search/` | 全局搜索、结果分类 |
| 公告列表 | `user-announcements/` | 系统公告浏览 |
| 公告详情 | `announcement-detail/` | 公告内容展示 |

</details>

### 商家端功能

<details>
<summary>📖 点击展开详细功能列表</summary>

| 页面 | 路径 | 功能描述 |
|------|------|----------|
| 商家首页 | `merchant-home/` | 统计概览、最近订单、最新评价 |
| 服务管理 | `services/` | 服务列表、状态管理 |
| 服务编辑 | `service-edit/` | 新增/编辑服务 |
| 商品管理 | `merchant-products/` | 商品列表、库存管理 |
| 商品编辑 | `product-edit/` | 新增/编辑商品 |
| 预约管理 | `merchant-appointments/` | 预约列表、状态处理 |
| 服务订单 | `merchant-orders/` | 服务订单管理 |
| 商品订单 | `merchant-product-orders/` | 商品订单、发货管理 |
| 评价管理 | `merchant-reviews/` | 评价列表、回复功能 |
| 分类管理 | `categories/` | 商品分类管理 |
| 店铺编辑 | `shop-edit/` | 店铺信息修改 |
| 店铺设置 | `shop-settings/` | 营业状态、通知设置 |
| 预约统计 | `stats-appointments/` | 预约数据分析 |
| 营收统计 | `stats-revenue/` | 收入数据分析 |

</details>

### 平台端功能

<details>
<summary>📖 点击展开详细功能列表</summary>

| 页面 | 路径 | 功能描述 |
|------|------|----------|
| 平台首页 | `admin-dashboard/` | 数据统计、待办事项 |
| 用户管理 | `admin-users/` | 用户列表、状态管理 |
| 用户详情 | `user-detail/` | 用户信息、关联数据 |
| 商家管理 | `admin-merchants/` | 商家列表、状态管理 |
| 商家详情 | `merchant-detail/` | 商家信息、关联数据 |
| 商家审核 | `merchant-audit/` | 入驻审核、审批处理 |
| 服务管理 | `admin-services/` | 平台服务监管 |
| 商品管理 | `admin-products/` | 平台商品监管 |
| 商品详情 | `product-manage/` | 商品详情管理 |
| 评价管理 | `admin-reviews/` | 评价列表、审核 |
| 评价审核 | `review-audit/` | 评价内容审核 |
| 公告管理 | `admin-announcements/` | 公告列表、发布管理 |
| 公告编辑 | `announcement-edit/` | 新增/编辑公告 |
| 系统设置 | `admin-system/` | 平台配置管理 |
| 角色管理 | `roles/` | 权限角色配置 |
| 操作日志 | `logs/` | 系统操作记录 |
| 活动管理 | `admin-activities/` | 营销活动管理 |
| 任务管理 | `admin-tasks/` | 定时任务管理 |
| 店铺审核 | `shop-audit/` | 店铺信息审核 |

</details>

---

## API 文档

### Swagger UI 访问

启动后端服务后，访问以下地址查看 API 文档：

```
http://localhost:8080/swagger-ui.html
```

### API 端点概览

| 模块 | 基础路径 | 说明 |
|------|----------|------|
| 认证 | `/api/auth` | 登录、注册、登出 |
| 用户 | `/api/user` | 用户信息、宠物管理 |
| 商家 | `/api/merchant` | 商家信息、服务商品 |
| 管理员 | `/api/admin` | 平台管理功能 |
| 公共 | `/api/public` | 公开数据接口 |

### 主要接口示例

```bash
# 用户登录
POST /api/auth/login
Content-Type: application/json
{
  "email": "user@example.com",
  "password": "password123"
}

# 获取用户宠物列表
GET /api/user/pets
Authorization: Bearer <token>

# 获取服务列表
GET /api/public/services?page=1&size=10
```

---

## 数据库设计

### 数据库表概览

本项目共包含 **14 张数据表**，涵盖用户、商家、服务、商品、订单等核心业务。

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    user     │     │  merchant   │     │    admin    │
│  (用户表)   │     │  (商家表)   │     │ (管理员表)  │
└──────┬──────┘     └──────┬──────┘     └─────────────┘
       │                   │
       │    ┌──────────────┼──────────────┐
       │    │              │              │
       ▼    ▼              ▼              ▼
┌─────────────┐     ┌─────────────┐ ┌─────────────┐
│    pet      │     │   service   │ │   product   │
│  (宠物表)   │     │  (服务表)   │ │  (商品表)   │
└─────────────┘     └──────┬──────┘ └──────┬──────┘
                           │               │
       ┌───────────────────┼───────────────┤
       │                   │               │
       ▼                   ▼               ▼
┌─────────────┐     ┌─────────────┐ ┌─────────────┐
│ appointment │     │   review    │ │product_order│
│  (预约表)   │     │  (评价表)   │ │ (商品订单)  │
└─────────────┘     └─────────────┘ └──────┬──────┘
       │                                        │
       │                                        ▼
       │                                 ┌─────────────┐
       │                                 │order_item   │
       │                                 │(订单明细)   │
       │                                 └─────────────┘
       │
       ├─────────────┐     ┌─────────────┐
       ▼             ▼     ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  favorite   │ │announcement │ │ forum_post  │
│  (收藏表)   │ │  (公告表)   │ │(论坛帖子)   │
└─────────────┘ └─────────────┘ └──────┬──────┘
                                       │
                                       ▼
                                ┌─────────────┐
                                │forum_comment│
                                │(论坛评论)   │
                                └─────────────┘
```

### 核心数据表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user` | 用户信息 | id, username, email, phone, avatar |
| `merchant` | 商家信息 | id, name, contact_person, phone, status |
| `admin` | 管理员 | id, username, password |
| `pet` | 宠物信息 | id, user_id, name, type, breed, age |
| `service` | 服务信息 | id, merchant_id, name, price, duration |
| `product` | 商品信息 | id, merchant_id, name, price, stock |
| `appointment` | 预约记录 | id, user_id, merchant_id, service_id, status |
| `product_order` | 商品订单 | id, user_id, merchant_id, total_price, status |
| `review` | 评价记录 | id, user_id, merchant_id, service_id, rating |
| `favorite` | 收藏记录 | id, user_id, merchant_id |
| `announcement` | 系统公告 | id, title, content |
| `forum_post` | 论坛帖子 | id, user_id, title, content |
| `forum_comment` | 论坛评论 | id, post_id, user_id, content |

### 状态枚举说明

```sql
-- 商家状态
merchant.status: 'pending' | 'approved' | 'rejected'

-- 预约状态
appointment.status: 'pending' | 'confirmed' | 'completed' | 'cancelled'

-- 商品订单状态
product_order.status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'

-- 宠物性别
pet.gender: 'male' | 'female'
```

---

## 开发指南

### 开发环境搭建

1. **安装 IDE 推荐**
   - 后端：IntelliJ IDEA
   - 前端：VS Code + Volar 插件

2. **配置代码风格**
   ```bash
   cd cg-vue
   npm run format
   ```

3. **运行代码检查**
   ```bash
   npm run lint
   ```

### 代码规范

#### 前端代码规范

- 使用 TypeScript 编写，确保类型安全
- 组件命名采用 PascalCase（如 `UserList.vue`）
- 使用 Composition API 编写组件
- 遵循 ESLint 和 Prettier 配置

#### 后端代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- Controller 只负责请求转发，业务逻辑放在 Service 层
- 实体类使用 JPA 注解

### 分支管理

```
main        # 主分支，稳定版本
develop     # 开发分支
feature/*   # 功能分支
bugfix/*    # 修复分支
release/*   # 发布分支
```

### 提交规范

使用约定式提交格式：

```
feat: 新增功能
fix: 修复 Bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建/工具变动
```

示例：

```
feat(user): 添加宠物管理页面
fix(merchant): 修复订单状态更新问题
docs: 更新 README 文档
```

---

## 测试

### 单元测试

前端使用 Vitest 进行单元测试：

```bash
cd cg-vue

# 运行测试
npm run test

# 监听模式
npm run test:watch

# 生成覆盖率报告
npm run test:coverage
```

### E2E 测试

使用 Playwright 进行端到端测试：

```bash
cd cg-vue

# 运行 E2E 测试
npm run test:e2e

# UI 模式运行
npm run test:e2e:ui
```

### 后端测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=UserServiceTest
```

---

## 部署

### 生产环境配置

#### 后端配置

修改 `application-prod.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://prod-db-host:3306/cg
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA 配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# 服务器配置
server.port=8080
```

#### 前端配置

创建 `.env.production`：

```env
VITE_USE_MOCK=false
VITE_API_BASE_URL=https://api.example.com
VITE_APP_TITLE=宠物服务平台
```

### 构建部署

#### 后端构建

```bash
# 打包
./mvnw clean package -DskipTests

# 运行 JAR
java -jar target/pet-management-system-1.0-SNAPSHOT.jar
```

#### 前端构建

```bash
cd cg-vue

# 构建
npm run build

# 预览构建结果
npm run preview
```

构建产物在 `cg-vue/dist/` 目录下，可部署到 Nginx 或其他静态服务器。

### Docker 部署（可选）

```dockerfile
# 后端 Dockerfile 示例
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/pet-management-system-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# 前端 Dockerfile 示例
FROM node:20-alpine as builder
WORKDIR /app
COPY cg-vue/package*.json ./
RUN npm install
COPY cg-vue/ .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
```

---

## 贡献指南

### 如何贡献

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: 添加某功能'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### Pull Request 流程

1. 确保 PR 描述清晰，包含改动说明
2. 关联相关的 Issue
3. 确保所有测试通过
4. 等待代码审查

### 代码风格

- 遵循项目已有的代码风格
- 提交前运行 `npm run lint` 检查代码
- 保持代码简洁，添加必要的注释

---

## 许可证

本项目采用 [MIT](LICENSE) 许可证。

```
MIT License

Copyright (c) 2024 Pet Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 联系方式

### 项目团队

- **项目地址**: [GitHub Repository URL]
- **问题反馈**: [GitHub Issues](../../issues)

### 问题反馈

如果您在使用过程中遇到问题，请通过以下方式反馈：

1. 提交 [Issue](../../issues/new)
2. 描述问题复现步骤
3. 附上相关日志或截图

---

<p align="center">
  Made with ❤️ by Pet Management System Team
</p>

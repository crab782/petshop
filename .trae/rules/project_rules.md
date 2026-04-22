# 项目规则 (Project Rules)

## 项目概述
宠物服务平台 - 电商+预约综合平台（用户端/商家端/平台端三端分离）

## 技术栈

### 后端 (Backend)
- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.x
- **安全**: Spring Security + JWT
- **API文档**: SpringDoc OpenAPI (Swagger)
- **Java版本**: 17

### 前端 (Frontend - petshop-vue)
- **框架**: Vue 3.5 + TypeScript
- **构建工具**: Vite 8.x
- **UI库**: Element Plus 2.13
- **状态管理**: Pinia 3.x
- **路由**: Vue Router 5.x
- **测试**: Vitest + Playwright

## 目录结构

```
e:\g\petshop
├── src/                          # 后端源代码
│   └── main/java/com/petshop/
│       ├── config/               # 配置类
│       ├── controller/           # 控制器
│       │   ├── AdminController.java
│       │   ├── AuthController.java
│       │   ├── HomeController.java
│       │   ├── MerchantController.java
│       │   ├── UserController.java
│       │   └── api/              # API控制器
│       ├── dto/                  # 数据传输对象
│       ├── entity/               # 实体类
│       ├── exception/            # 异常处理
│       ├── mapper/              # MyBatis Mapper接口
│       ├── security/             # 安全相关
│       └── service/              # 服务层
├── petshop-vue/                  # 前端项目
│   ├── src/
│   │   ├── api/                 # API调用
│   │   ├── assets/              # 静态资源
│   │   ├── components/           # 公共组件
│   │   ├── composables/          # 组合式函数
│   │   ├── mock/                # Mock数据
│   │   ├── router/               # 路由配置
│   │   ├── stores/               # Pinia状态
│   │   ├── views/               # 页面组件
│   │   │   ├── admin/           # 平台管理端
│   │   │   ├── merchant/        # 商家端
│   │   │   └── user/            # 用户端
│   │   ├── App.vue
│   │   └── main.ts
│   └── package.json
├── migrations/                   # 数据库迁移
├── docs/                         # 文档
└── pom.xml                       # Maven配置
```

## 开发命令

### 后端
```powershell
# 编译
./mvnw compile

# 运行
./mvnw spring-boot:run

# 测试
./mvnw test

# 打包
./mvnw package
```

### 前端
```powershell
cd petshop-vue

# 开发环境
npm run dev

# 构建生产版本
npm run build

# 类型检查
npm run type-check

# 运行测试
npm run test

# 运行E2E测试
npm run test:e2e

# 代码检查
npm run lint
```

## 数据库配置

**开发环境**:
- Host: localhost
- Port: 3306
- Database: cg
- Username: root
- Password: 123456

**后端端口**: 8080
**前端开发服务器端口**: 5173

## API代理配置

前端开发环境通过 Vite 代理将 `/api` 请求转发到后端 `http://localhost:8080`

## 环境变量

### petshop-vue/.env.development
```
VITE_USE_MOCK=true
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=宠物服务平台
```

### petshop-vue/.env.production
```
VITE_USE_MOCK=false
VITE_API_BASE_URL=https://api.pet-service.com
VITE_APP_TITLE=宠物服务平台
```

## 代码规范

### 后端
- 使用 Lombok 简化代码
- Controller 返回 ApiResponse<T> 统一响应格式
- 使用 MyBatis-Plus 的 IService 和 BaseMapper
- 所有实体类使用 @Data 注解
- DTO类使用 @Data 注解

### 前端
- Vue组件使用 `<script setup lang="ts">` 语法
- 使用 Element Plus 组件库
- API调用通过 src/api/ 目录下的模块
- 使用 Pinia 进行状态管理
- 遵循 ESLint 规则

## 常用操作

### 创建新的实体和API
1. 在 `entity/` 创建实体类
2. 在 `mapper/` 创建 Mapper 接口
3. 在 `service/` 创建 Service 接口和实现
4. 在 `controller/` 创建 Controller
5. 前端在 `api/` 创建对应的API调用模块
6. 前端在 `views/` 创建对应的页面组件

### 添加新的页面
1. 在对应的 views 目录下创建或找到页面组件
2. 在 `router/index.ts` 中添加路由配置
3. 确保API调用正确配置

## 注意事项

1. 前端Mock模式: 开发时可设置 `VITE_USE_MOCK=true` 使用Mock数据
2. CORS配置: 后端已配置允许 localhost:5173 和 localhost:3000
3. JWT密钥: 生产环境必须更换 `jwt.secret` 配置
4. 数据库: 启动前确保MySQL服务运行且数据库存在

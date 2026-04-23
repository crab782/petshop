# Docker容器化部署计划

## 项目分析

### 项目结构
- **后端项目**：Spring Boot 3.2.0应用，使用MyBatis-Plus、MySQL、Redis、JWT
- **数据库**：MySQL 8.0，数据库名：cg，用户名：root，密码：123456
- **中间件**：Redis 7.0+
- **数据库备份**：`e:\g\petshop\petshop_423.sql`

### 依赖分析
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.0+
- **后端**：Java 17 + Spring Boot 3.2.0

## 实施计划

### 1. 创建Dockerfile
- **多阶段构建**：
  - 第一阶段：使用Maven镜像构建应用
  - 第二阶段：使用OpenJDK 17镜像运行应用
- **构建步骤**：
  - 复制pom.xml和源代码
  - 构建应用
  - 复制构建产物到运行镜像

### 2. 创建docker-compose.yml
- **服务编排**：
  - `mysql`：MySQL 8.0容器
  - `redis`：Redis 7.0+容器
  - `app`：后端应用容器
- **网络配置**：创建自定义网络
- **数据持久化**：
  - MySQL数据卷
  - Redis数据卷
- **环境配置**：
  - 数据库连接信息
  - Redis连接信息
  - 应用配置

### 3. 数据库初始化
- **挂载备份文件**：将`petshop_423.sql`挂载到MySQL容器
- **初始化脚本**：配置MySQL容器自动执行备份文件

### 4. 应用配置调整
- **环境变量**：修改application.properties使用环境变量
- **网络配置**：使用容器名称作为服务地址

### 5. 网络代理配置
- **代理环境变量**：在PowerShell中设置HTTP_PROXY和HTTPS_PROXY
- **Docker网络**：配置容器使用代理

## 文件结构

```
petshop/
├── Dockerfile           # 应用容器构建文件
├── docker-compose.yml   # 容器编排文件
├── petshop_423.sql      # 数据库备份文件
├── src/                 # 源代码
└── pom.xml              # Maven配置文件
```

## 实施步骤

1. **创建Dockerfile**
2. **创建docker-compose.yml**
3. **调整应用配置**
4. **启动容器**
5. **验证服务**

## 技术要点

- **多阶段构建**：减小最终镜像大小
- **网络配置**：容器间网络通信
- **数据持久化**：确保数据安全
- **环境变量**：便于配置管理
- **代理配置**：解决网络访问问题

## 预期效果

- 后端应用运行在8080端口
- 数据库运行在3306端口
- Redis运行在6379端口
- 服务之间正常通信
- 数据库数据从备份文件恢复

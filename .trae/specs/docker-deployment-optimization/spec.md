# 宠物服务平台 - Docker 容器化部署优化 PRD

## Overview
- **Summary**: 优化 Docker 容器化部署配置，解决端口冲突问题，确保与本地 MySQL 和 Redis 共存
- **Purpose**: 在 Windows Docker Desktop 环境下部署完整的宠物服务平台，避免与本地服务端口冲突
- **Target Users**: 开发团队和运维人员

## Goals
- 解决容器内 MySQL 和 Redis 与本地服务端口冲突问题
- 使用 Docker Network 和 Docker Volume 管理服务和数据
- 使用 Maven 多阶段构建优化后端镜像
- Nginx 容器在 80 端口提供前端服务
- 确保前端页面、Swagger 文档、后端 API 正常运行
- 导入数据库备份文件 `petshop_423.sql`

## Non-Goals (Out of Scope)
- 不修改本地 MySQL 和 Redis 配置
- 不修改前端和后端业务代码
- 不涉及生产环境部署优化

## Background & Context
- Windows 环境安装 Docker Desktop
- 本地已有 MySQL 运行在 3306 端口
- 本地已有 Redis 运行在 6379 端口
- 已有 Maven 多阶段构建的 Dockerfile
- 已有 docker-compose.yml 配置
- 数据库备份文件位于 `e:\g\petshop\petshop_423.sql`

## Functional Requirements
- **FR-1**: MySQL 容器使用 3307 端口映射，避免与本地 MySQL 冲突
- **FR-2**: Redis 容器使用 6380 端口映射，避免与本地 Redis 冲突
- **FR-3**: 使用 Docker Network 实现容器间通信
- **FR-4**: 使用 Docker Volume 持久化 MySQL 和 Redis 数据
- **FR-5**: Nginx 容器在 80 端口提供前端服务
- **FR-6**: 后端 API 在容器内 8080 端口运行
- **FR-7**: 自动导入数据库初始化脚本

## Non-Functional Requirements
- **NFR-1**: 所有容器启动时间不超过 60 秒
- **NFR-2**: 前端页面加载时间不超过 3 秒
- **NFR-3**: 后端 API 响应时间不超过 500ms
- **NFR-4**: 数据库连接池配置合理

## Constraints
- **Technical**: Docker Desktop on Windows, Docker Compose 3.8+
- **Business**: 不能停止本地 MySQL 和 Redis 服务
- **Dependencies**: 依赖 Maven、Node.js 构建环境

## Assumptions
- Docker Desktop 已正确安装并运行
- 本地 MySQL 占用 3306 端口，Redis 占用 6379 端口
- 数据库备份文件 `petshop_423.sql` 存在且有效

## Acceptance Criteria

### AC-1: Docker Network 创建成功
- **Given**: Docker Desktop 运行正常
- **When**: 执行 docker-compose up
- **Then**: petshop-network 网络创建成功
- **Verification**: `programmatic`

### AC-2: Docker Volume 创建成功
- **Given**: Docker Desktop 运行正常
- **When**: 执行 docker-compose up
- **Then**: mysql-data 和 redis-data 卷创建成功
- **Verification**: `programmatic`

### AC-3: MySQL 容器正常运行
- **Given**: Docker Compose 配置正确
- **When**: 容器启动
- **Then**: MySQL 容器在 3307 端口可访问，数据库初始化完成
- **Verification**: `programmatic`

### AC-4: Redis 容器正常运行
- **Given**: Docker Compose 配置正确
- **When**: 容器启动
- **Then**: Redis 容器在 6380 端口可访问
- **Verification**: `programmatic`

### AC-5: 后端服务正常运行
- **Given**: MySQL 和 Redis 容器正常
- **When**: 后端容器启动
- **Then**: 后端 API 在容器内 8080 端口运行
- **Verification**: `programmatic`

### AC-6: Nginx 前端服务正常
- **Given**: 后端服务正常
- **When**: Nginx 容器启动
- **Then**: 前端页面在 80 端口可访问
- **Verification**: `human-judgment`

### AC-7: Swagger 文档可访问
- **Given**: 所有服务正常
- **When**: 访问 http://localhost/api/swagger-ui.html
- **Then**: Swagger 文档正常显示
- **Verification**: `human-judgment`

### AC-8: API 接口正常响应
- **Given**: 所有服务正常
- **When**: 调用 API 接口
- **Then**: API 返回正确响应
- **Verification**: `programmatic`

## Open Questions
- [ ] 是否需要配置健康检查？
- [ ] 是否需要配置容器重启策略？
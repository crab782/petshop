# 宠物服务平台 - Docker 容器化部署优化 实现计划

## [x] Task 1: 更新 docker-compose.yml 配置
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改 MySQL 端口映射为 3307:3306
  - 修改 Redis 端口映射为 6380:6379
  - 确认 Docker Network 配置正确
  - 确认 Docker Volume 配置正确
  - 添加健康检查配置
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `programmatic` TR-1.1: 验证 docker-compose.yml 语法正确
  - `programmatic` TR-1.2: 验证端口映射配置正确
- **Notes**: 确保端口不与本地服务冲突

## [x] Task 2: 更新后端 Dockerfile 配置
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 确认 Maven 多阶段构建配置正确
  - 优化构建缓存层
  - 确保 JAR 包正确复制
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-2.1: 验证 Dockerfile 构建成功
  - `programmatic` TR-2.2: 验证镜像大小合理
- **Notes**: 使用 Maven 多阶段构建减少镜像大小

## [x] Task 3: 更新前端 Dockerfile 和 nginx.conf 配置
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 确认前端多阶段构建配置正确
  - 确认 nginx 反向代理配置正确
  - 确保 API 请求正确代理到后端
- **Acceptance Criteria Addressed**: AC-6
- **Test Requirements**:
  - `programmatic` TR-3.1: 验证前端构建成功
  - `programmatic` TR-3.2: 验证 nginx 配置正确
- **Notes**: 确保 /api 请求正确转发到后端服务

## [x] Task 4: 验证数据库初始化脚本
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 确认 petshop_423.sql 文件存在
  - 确认数据库初始化脚本正确挂载
  - 验证数据库表结构和数据
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-4.1: 验证 SQL 文件格式正确
  - `programmatic` TR-4.2: 验证数据库表创建成功
- **Notes**: 确保数据库初始化脚本路径正确

## [x] Task 5: 构建和启动所有容器
- **Priority**: P0
- **Depends On**: Task 1, Task 2, Task 3, Task 4
- **Description**:
  - 执行 docker-compose build 构建所有镜像
  - 执行 docker-compose up -d 启动所有容器
  - 检查容器状态
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5, AC-6
- **Test Requirements**:
  - `programmatic` TR-5.1: 验证所有容器运行状态
  - `programmatic` TR-5.2: 验证容器间网络通信正常
- **Notes**: 使用 docker ps 检查容器状态

## [x] Task 6: 测试前端页面访问
- **Priority**: P1
- **Depends On**: Task 5
- **Description**:
  - 访问 http://localhost 验证前端页面
  - 测试页面路由跳转
  - 验证静态资源加载
- **Acceptance Criteria Addressed**: AC-6
- **Test Requirements**:
  - `human-judgment` TR-6.1: 前端页面正常显示
  - `human-judgment` TR-6.2: 页面路由跳转正常
- **Notes**: 使用浏览器访问测试

## [x] Task 7: 测试 Swagger 文档访问
- **Priority**: P1
- **Depends On**: Task 5
- **Description**:
  - 访问 http://localhost/swagger-ui/index.html
  - 验证 API 文档正常显示
  - 测试 API 接口调用
- **Acceptance Criteria Addressed**: AC-7
- **Test Requirements**:
  - `human-judgment` TR-7.1: Swagger 文档正常显示
  - `programmatic` TR-7.2: API 接口返回正确响应
- **Notes**: 使用 Swagger UI 测试 API

## [x] Task 8: 测试后端 API 接口
- **Priority**: P1
- **Depends On**: Task 5
- **Description**:
  - 测试用户登录接口
  - 测试商家登录接口
  - 测试管理员登录接口
  - 测试公开 API 接口
- **Acceptance Criteria Addressed**: AC-8
- **Test Requirements**:
  - `programmatic` TR-8.1: 用户登录接口返回正确响应
  - `programmatic` TR-8.2: 商家登录接口返回正确响应
  - `programmatic` TR-8.3: 管理员登录接口返回正确响应
- **Notes**: 使用 curl 或 Postman 测试 API

## Task Dependencies
- Task 5 depends on Task 1, Task 2, Task 3, Task 4
- Task 6 depends on Task 5
- Task 7 depends on Task 5
- Task 8 depends on Task 5
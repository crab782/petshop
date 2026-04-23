# Docker Nginx前端集成 - 产品需求文档

## Overview
- **Summary**: 修改Docker Compose配置，添加Nginx前端服务，实现前后端通过Docker网络通信，对外暴露80端口提供完整的Web应用访问。
- **Purpose**: 解决前后端集成问题，提供统一的访问入口，确保前端能够正确代理API请求到后端服务。
- **Target Users**: 开发团队和最终用户。

## Goals
- 集成Nginx前端服务到Docker Compose配置
- 确保前后端通过Docker网络通信
- 对外暴露80端口作为应用访问入口
- 实现前端API请求正确代理到后端

## Non-Goals (Out of Scope)
- 前端代码修改
- 后端代码修改
- 数据库结构修改
- 生产环境部署优化

## Background & Context
- 前端使用Vue 3 + Vite开发
- 前端配置了API代理到 http://localhost:8080
- 后端服务运行在8080端口
- 已有的Docker Compose配置包含MySQL、Redis和后端服务

## Functional Requirements
- **FR-1**: 添加Nginx前端服务到Docker Compose
- **FR-2**: 配置Nginx反向代理，将/api请求转发到后端服务
- **FR-3**: 暴露80端口作为应用访问入口
- **FR-4**: 确保前后端通过Docker网络通信

## Non-Functional Requirements
- **NFR-1**: 服务启动时间不超过30秒
- **NFR-2**: 前端页面加载时间不超过3秒
- **NFR-3**: 后端API响应时间不超过500ms

## Constraints
- **Technical**: Docker Compose 3.8+, Nginx, Vue 3 + Vite
- **Dependencies**: 依赖后端服务、MySQL、Redis

## Assumptions
- 前端构建产物正确生成
- 后端服务正常运行
- Docker环境配置正确

## Acceptance Criteria

### AC-1: Nginx服务启动成功
- **Given**: Docker Compose配置正确
- **When**: 执行docker-compose up
- **Then**: Nginx容器正常运行，80端口可访问
- **Verification**: `programmatic`

### AC-2: 前端页面可访问
- **Given**: Nginx服务启动成功
- **When**: 访问http://localhost
- **Then**: 前端页面正常加载
- **Verification**: `human-judgment`

### AC-3: API请求代理成功
- **Given**: 前端页面加载成功
- **When**: 前端发起API请求
- **Then**: 请求正确代理到后端服务并返回数据
- **Verification**: `programmatic`

### AC-4: 网络通信正常
- **Given**: 所有服务启动成功
- **When**: 检查容器间网络连接
- **Then**: 前后端通过Docker网络正常通信
- **Verification**: `programmatic`

## Open Questions
- [ ] 前端构建是否需要特殊配置
- [ ] 后端服务是否需要调整CORS配置

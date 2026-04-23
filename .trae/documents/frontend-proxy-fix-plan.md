# 前端代理配置修复计划

## 问题分析

### 问题现象
- 登录时前端直接请求 `http://localhost:8080/api/auth/login`
- 出现跨域错误，无法连接到后端
- Referrer Policy 提示 strict-origin-when-cross-origin

### 根本原因
- `.env.development` 文件中 `VITE_API_BASE_URL=http://localhost:8080`
- 这导致前端 axios 直接访问后端URL，而不是通过 Vite 开发服务器的代理
- CORS 配置虽然正确，但 OPTIONS 预检请求仍然失败

### 当前配置
- 前端开发服务器：127.0.0.1:80
- Vite 代理配置：/api → http://localhost:8080
- axios baseURL：使用 `VITE_API_BASE_URL` 或默认值 `/api`
- 问题：环境变量覆盖了默认值

## 解决方案

### 方案：修改环境变量配置
- **文件**：`.env.development`
- **修改**：将 `VITE_API_BASE_URL` 改为 `/api` 或删除该行
- **效果**：前端请求会通过 Vite 代理转发到后端

## 实施步骤

1. 修改 `.env.development` 文件
2. 重启前端开发服务器
3. 测试登录功能
4. 验证 API 调用通过代理

## 预期效果

- 前端请求 `/api/auth/login` 通过代理转发到 `http://localhost:8080/api/auth/login`
- 无 CORS 跨域错误
- 登录功能正常

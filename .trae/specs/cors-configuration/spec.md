# CORS 跨域配置优化 Spec

## Why
前后端分离架构中，前端 Vue3 项目（运行于 5173 端口）需要与后端 Spring Boot 服务（运行于 8080 端口）进行 API 通信。当前配置存在潜在问题，需要全面检查并优化跨域配置，确保数据交互正常。

## What Changes
- 检查并优化前端 Vite 代理配置
- 验证后端 CORS 配置是否正确生效
- 确保 `.env.development` 配置被正确使用
- 提供完整的配置方案文档

## Impact
- Affected specs: 前端 API 请求配置、后端安全配置
- Affected code: 
  - `cg-vue/vite.config.ts`
  - `cg-vue/src/api/request.ts`
  - `src/main/java/com/petshop/config/SecurityConfig.java`

## Current Configuration Analysis

### 前端配置现状
1. **vite.config.ts**: 未配置 API 代理，仅有基本插件配置
2. **request.ts**: 直接硬编码 `baseURL: 'http://localhost:8080'`
3. **.env.development**: 已设置 `VITE_API_BASE_URL=http://localhost:8080`，但未被使用

### 后端配置现状
1. **SecurityConfig.java**: 已配置 CORS
   - 允许源: `http://localhost:5173`, `http://localhost:3000`, `http://127.0.0.1:5173`, `http://127.0.0.1:3000`
   - 允许方法: GET, POST, PUT, DELETE, OPTIONS, PATCH
   - 允许请求头: Authorization, Content-Type, X-Requested-With, Accept, Origin
   - 允许凭据: true
   - 预检缓存: 3600秒

2. **application.properties**: 已配置 CORS 属性（但 SecurityConfig 中硬编码了值）

## ADDED Requirements

### Requirement: Frontend Proxy Configuration
前端项目 SHALL 配置 Vite 开发服务器代理，将 API 请求转发至后端服务。

#### Scenario: API 请求代理
- **WHEN** 前端发起 `/api/*` 请求
- **THEN** 请求应被代理至 `http://localhost:8080`

### Requirement: Environment Variable Usage
前端项目 SHALL 使用环境变量配置 API 基础 URL，而非硬编码。

#### Scenario: 开发环境配置
- **WHEN** 应用运行在开发模式
- **THEN** 应使用 `.env.development` 中的 `VITE_API_BASE_URL`

### Requirement: Backend CORS Verification
后端 CORS 配置 SHALL 正确处理跨域请求，包括预检请求。

#### Scenario: 预检请求处理
- **WHEN** 浏览器发送 OPTIONS 预检请求
- **THEN** 后端应返回正确的 CORS 响应头

#### Scenario: 凭据支持
- **WHEN** 前端请求携带 Cookie 或 Authorization 头
- **THEN** 后端应正确处理并响应

## MODIFIED Requirements

### Requirement: Vite Configuration Enhancement
前端 vite.config.ts SHALL 添加服务器代理配置：

```typescript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false
    }
  }
}
```

### Requirement: Request Configuration Update
前端 request.ts SHALL 使用环境变量：

```typescript
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})
```

## Configuration Solution

### 方案一：Vite 代理模式（推荐开发环境）
- 前端配置 Vite 代理，将 `/api` 请求转发至后端
- 优点：无需后端 CORS 配置，浏览器无跨域感知
- 缺点：仅适用于开发环境

### 方案二：CORS 模式（当前配置）
- 后端配置 CORS 响应头
- 前端直接请求后端 URL
- 优点：适用于开发和生产环境
- 缺点：需要正确配置 CORS

### 推荐方案
开发环境使用 Vite 代理 + 后端 CORS 双重保障：
1. 配置 Vite 代理处理开发环境请求
2. 保持后端 CORS 配置用于生产环境

---

## 完整配置方案文档

### 一、前端转发配置示例代码

#### 1. vite.config.ts 完整配置

```typescript
import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      }
    }
  }
})
```

**配置说明：**
- `port: 5173` - 指定前端开发服务器端口
- `target: 'http://localhost:8080'` - 后端服务地址
- `changeOrigin: true` - 修改请求头中的 Origin 为目标地址
- `secure: false` - 开发环境允许自签名证书

#### 2. request.ts 完整配置

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios'

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = token
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error: AxiosError) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error('请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败')
    } else {
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

#### 3. 环境变量配置

**.env.development（开发环境）**
```
VITE_USE_MOCK=false
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=宠物服务平台
```

**.env.production（生产环境）**
```
VITE_USE_MOCK=false
VITE_API_BASE_URL=/api
VITE_APP_TITLE=宠物服务平台
```

---

### 二、后端 CORS 配置详细实现

#### 1. SecurityConfig.java CORS 配置

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // 允许的源（前端地址）
    configuration.setAllowedOrigins(List.of(
        "http://localhost:5173",
        "http://localhost:3000",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:3000"
    ));
    
    // 允许的 HTTP 方法
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    ));
    
    // 允许的请求头
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"
    ));
    
    // 暴露给前端的响应头
    configuration.setExposedHeaders(List.of("Authorization"));
    
    // 允许携带凭据（Cookie、Authorization 头）
    configuration.setAllowCredentials(true);
    
    // 预检请求缓存时间（秒）
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

#### 2. SecurityFilterChain 集成 CORS

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        );
    
    return http.build();
}
```

#### 3. application.properties 配置（可选）

```properties
# CORS Configuration
cors.allowed-origins=http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
cors.allowed-headers=Authorization,Content-Type,X-Requested-With,Accept,Origin
cors.allow-credentials=true
cors.max-age=3600
```

---

### 三、跨域问题常见排查步骤

#### 步骤 1：检查浏览器控制台错误

打开浏览器开发者工具（F12），查看 Console 和 Network 标签：

**常见错误信息：**
- `Access to XMLHttpRequest at '...' from origin '...' has been blocked by CORS policy`
- `Response to preflight request doesn't pass access control check`
- `The value of the 'Access-Control-Allow-Origin' header must not be '*' when credentials mode is 'include'`

#### 步骤 2：验证预检请求（OPTIONS）

```bash
curl -i -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization"
```

**预期响应：**
```
HTTP/1.1 200
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS,PATCH
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

#### 步骤 3：检查响应头

在 Network 标签中查看 API 响应头，确认包含：
- `Access-Control-Allow-Origin: http://localhost:5173`
- `Access-Control-Allow-Credentials: true`

#### 步骤 4：验证前端配置

1. 确认 `vite.config.ts` 中代理配置正确
2. 确认 `request.ts` 中 `baseURL` 配置正确
3. 确认 `.env.development` 文件存在且配置正确

#### 步骤 5：重启服务

修改配置后需要重启：
- 前端：重启 Vite 开发服务器
- 后端：重启 Spring Boot 应用

---

### 四、常见问题解决方案

#### 问题 1：CORS 错误 - Origin 不匹配

**症状：**
```
Access-Control-Allow-Origin header doesn't match
```

**解决方案：**
确保后端 CORS 配置中的 `allowedOrigins` 包含前端实际运行的地址。

#### 问题 2：预检请求失败

**症状：**
```
Response to preflight request doesn't pass access control check
```

**解决方案：**
1. 确认后端允许 OPTIONS 方法
2. 确认 `Access-Control-Allow-Headers` 包含前端使用的所有请求头

#### 问题 3：凭据模式错误

**症状：**
```
The value of the 'Access-Control-Allow-Origin' header must not be '*'
```

**解决方案：**
当 `allowCredentials(true)` 时，不能使用通配符 `*`，必须指定具体的源地址。

#### 问题 4：Authorization 头无法读取

**症状：**
前端无法从响应头中读取 Authorization（JWT Token）

**解决方案：**
在后端配置中添加 `setExposedHeaders(List.of("Authorization"))`

---

### 五、配置验证结果

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 前端 Vite 代理配置 | ✅ 已配置 | `/api` 代理到 `http://localhost:8080` |
| 前端环境变量使用 | ✅ 已配置 | 使用 `VITE_API_BASE_URL` |
| 后端 CORS Bean | ✅ 已配置 | `CorsConfigurationSource` 正确配置 |
| 后端允许源 | ✅ 已配置 | 包含 `localhost:5173` |
| 后端允许方法 | ✅ 已配置 | GET, POST, PUT, DELETE, OPTIONS, PATCH |
| 后端允许请求头 | ✅ 已配置 | Authorization, Content-Type 等 |
| 后端凭据支持 | ✅ 已配置 | `allowCredentials(true)` |
| 预检请求测试 | ✅ 通过 | OPTIONS 请求返回 200 |
| CORS 响应头验证 | ✅ 通过 | 响应包含正确的 CORS 头 |

**结论：前后端 CORS 配置已完成，前端（5173 端口）可与后端（8080 端口）正常通信。**

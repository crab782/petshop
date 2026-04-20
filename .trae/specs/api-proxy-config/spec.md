# API代理配置规格说明

## Why
前端项目运行在5173端口，后端API运行在8080端口，需要配置Vite代理将所有API请求转发到后端服务器，解决跨域和404问题。

## What Changes
- **BREAKING**: 修改vite.config.ts，添加代理配置
- 确保所有以/api开头的请求都被代理到http://localhost:8080
- 保持现有API路径不变

## Impact
- 前端所有页面都能正常访问后端API
- 解决注册页404错误
- 解决登录页404错误
- 解决所有其他API请求的404错误

## ADDED Requirements
### Requirement: API代理配置
The system SHALL provide API proxy configuration in vite.config.ts to forward all /api requests to backend server.

#### Scenario: 注册请求
- **WHEN** 用户在前端注册页面提交注册表单
- **THEN** 请求被代理到后端8080端口，注册成功

#### Scenario: 登录请求
- **WHEN** 用户在前端登录页面提交登录表单
- **THEN** 请求被代理到后端8080端口，登录成功

#### Scenario: 其他API请求
- **WHEN** 前端发起任何/api开头的请求
- **THEN** 请求被代理到后端8080端口，返回正确数据

## MODIFIED Requirements
### Requirement: Vite配置
修改vite.config.ts文件，添加server.proxy配置：
```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

## REMOVED Requirements
无

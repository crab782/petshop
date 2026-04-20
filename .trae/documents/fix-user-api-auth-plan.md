# 修复用户端 API 认证问题 Plan

## 问题分析
- 登录成功并获取 JWT token 后，访问 `/api/user/home/stats` 返回 403 Forbidden
- 原因：`UserApiController` 使用 `HttpSession.getAttribute("user")` 获取用户信息
- 但系统使用 JWT 认证，用户信息存储在 SecurityContext 中，不在 Session 里
- 需要修改 `UserApiController` 从 Spring Security 的 SecurityContext 获取当前用户

## 实施步骤

### 步骤 1: 修改 UserApiController 获取当前用户方式
- 将所有 `HttpSession.getAttribute("user")` 替换为从 SecurityContext 获取
- 使用 `SecurityContextHolder.getContext().getAuthentication()` 获取当前认证信息
- 从 Authentication 中获取用户手机号，再查询用户详情

### 步骤 2: 添加获取当前用户的方法
- 在 UserApiController 中添加私有方法 `getCurrentUser()`
- 从 SecurityContext 获取手机号，查询 User 对象

### 步骤 3: 修改所有使用 Session 的方法
- 修改 `/home/stats` 接口
- 修改 `/home/activities` 接口
- 检查并修改其他使用 Session 获取用户的接口

### 步骤 4: 测试验证
- 登录成功后访问 `/api/user/home/stats`
- 验证返回 200 和正确的统计数据

## 修改的文件清单
1. `src/main/java/com/petshop/controller/api/UserApiController.java` - 修改用户获取方式

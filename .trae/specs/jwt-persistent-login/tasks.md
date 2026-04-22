# Tasks

## Phase 1: 代码修改

- [x] Task 1: 修改JwtUtils.java - 移除令牌过期设置
  - 修改 `generateJwtToken` 方法：移除 `.expiration()` 调用
  - 修改 `generateTokenFromUsername` 方法：移除 `.expiration()` 调用
  - 修改 `validateJwtToken` 方法：移除 `ExpiredJwtException` catch块

- [x] Task 2: 更新application.properties配置
  - 注释掉 `jwt.expiration=86400000` 配置行

## Phase 2: 验证测试

- [x] Task 3: 验证用户登录token生成
  - 检查用户登录返回的token不包含过期时间

- [x] Task 4: 验证商家登录token生成
  - 检查商家登录返回的token不包含过期时间

- [x] Task 5: 验证管理员登录token生成
  - 检查管理员登录返回的token不包含过期时间

- [x] Task 6: 验证token验证逻辑
  - 确认无效token仍被正确拒绝
  - 确认过期token现在可以通过验证（因为移除了过期检查）

## Task Dependencies
- Task 2 依赖 Task 1
- Tasks 3, 4, 5, 6 依赖 Task 1 和 Task 2

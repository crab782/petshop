# Checklist

## JwtUtils.java 修改

- [x] `generateJwtToken` 方法移除了 `.expiration()` 调用
- [x] `generateTokenFromUsername` 方法移除了 `.expiration()` 调用
- [x] `validateJwtToken` 方法移除了 `ExpiredJwtException` catch块
- [x] 代码中没有遗留过期时间相关逻辑

## application.properties 配置

- [x] `jwt.expiration` 配置行已被注释掉

## 功能验证

- [x] 用户登录返回的JWT token不包含过期时间（使用generateJwtToken）
- [x] 商家登录返回的JWT token不包含过期时间（使用generateJwtToken）
- [x] 管理员登录返回的JWT token不包含过期时间（使用generateJwtToken）
- [x] 无效/伪造的token仍被正确拒绝（MalformedJwtException仍被捕获）
- [x] 格式正确的token（即使包含过期声明）仍可验证通过
- [x] 签名无效的token被正确拒绝

## 系统影响评估

- [x] 其他安全验证机制正常工作（签名、格式等）
- [x] Spring Security配置未受影响
- [x] 前后端通信正常
- [x] 无编译错误或警告（JwtUtils.java本身无错误）

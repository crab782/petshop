# 注册页404错误修复计划

## 问题分析

**现象**：在注册页 http://localhost:5173/register 提交注册时，提示注册失败，F12 显示 404 Not Found 错误。

**请求信息**：
- 请求URL：http://localhost:5173/api/auth/register
- 请求方法：POST
- 状态码：404 Not Found

**根因分析**：
1. 前端 Vite 开发服务器运行在 5173 端口
2. 后端 API 服务器运行在 8080 端口
3. 前端没有配置代理，请求直接发到了5173端口（该端口没有后端API）
4. 后端接口实际在8080端口存在且正常工作（Swagger UI测试通过）

## 修复方案

### 配置 Vite 代理

修改 `cg-vue/vite.config.ts` 文件，添加代理配置：

```typescript
export default defineConfig({
  // ... 其他配置
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
```

## 实施步骤

1. **修改 vite.config.ts**：添加代理配置
2. **验证后端服务**：确保后端服务运行在 8080 端口
3. **重启前端开发服务器**：使代理配置生效
4. **测试注册功能**：验证注册接口是否正常工作

## 风险评估

**低风险**：
- 配置 Vite 代理是标准做法
- 不影响现有功能
- 配置简单，易于回滚

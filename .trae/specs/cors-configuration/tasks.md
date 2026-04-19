# Tasks

- [x] Task 1: 前端配置检查与优化
  - [x] SubTask 1.1: 分析 vite.config.ts 当前配置，确认缺少代理配置
  - [x] SubTask 1.2: 为 vite.config.ts 添加 API 代理配置
  - [x] SubTask 1.3: 更新 request.ts 使用环境变量而非硬编码 URL
  - [x] SubTask 1.4: 验证 .env.development 配置正确性

- [x] Task 2: 后端 CORS 配置验证
  - [x] SubTask 2.1: 检查 SecurityConfig.java 中 CORS 配置完整性
  - [x] SubTask 2.2: 验证 application.properties 中 CORS 属性配置
  - [x] SubTask 2.3: 确认预检请求（OPTIONS）处理正确
  - [x] SubTask 2.4: 验证凭据（Credentials）支持配置

- [x] Task 3: 功能验证与问题排查
  - [x] SubTask 3.1: 启动后端服务（8080 端口）
  - [x] SubTask 3.2: 启动前端开发服务器（5173 端口）
  - [x] SubTask 3.3: 测试 API 请求是否正常（登录接口等）
  - [x] SubTask 3.4: 检查浏览器控制台是否有跨域错误

- [x] Task 4: 配置方案文档输出
  - [x] SubTask 4.1: 整理前端配置示例代码
  - [x] SubTask 4.2: 整理后端 CORS 配置说明
  - [x] SubTask 4.3: 编写常见问题排查步骤

# Task Dependencies
- [Task 2] 可与 [Task 1] 并行执行
- [Task 3] 依赖 [Task 1] 和 [Task 2] 完成
- [Task 4] 依赖 [Task 3] 验证通过

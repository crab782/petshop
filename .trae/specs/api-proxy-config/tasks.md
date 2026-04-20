# Tasks

- [x] Task 1: 检查前端项目所有API路径
  - [x] SubTask 1.1: 检查auth.ts中的API路径
  - [x] SubTask 1.2: 检查user.ts中的API路径
  - [x] SubTask 1.3: 检查merchant.ts中的API路径
  - [x] SubTask 1.4: 检查admin.ts中的API路径
  - [x] SubTask 1.5: 检查其他API文件中的路径

- [x] Task 2: 修改vite.config.ts添加代理配置
  - [x] SubTask 2.1: 添加server.proxy配置
  - [x] SubTask 2.2: 配置/api路径代理到8080端口

- [x] Task 3: 验证代理配置
  - [x] SubTask 3.1: 重启前端开发服务器
  - [x] SubTask 3.2: 测试注册接口
  - [x] SubTask 3.3: 测试登录接口
  - [x] SubTask 3.4: 测试其他API接口

# Task Dependencies
- Task 2 依赖 Task 1（需要先了解所有API路径）
- Task 3 依赖 Task 2（需要代理配置生效后才能测试）

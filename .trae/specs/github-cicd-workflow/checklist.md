# GitHub CI/CD 工作流配置检查清单

## 前端工作流配置检查
- [x] frontend.yml 文件存在于 .github/workflows 目录
- [x] 工作流触发条件配置正确（Push、Pull Request）
- [x] Node.js 版本与项目要求一致
- [x] 依赖安装命令正确（npm ci）
- [x] 代码检查步骤配置完整（ESLint、TypeScript）
- [x] 构建命令正确（npm run build）
- [x] 测试命令正确（npm run test:unit、npm run test:e2e）
- [x] 依赖缓存配置合理
- [x] YAML 语法正确

## 后端工作流配置检查
- [x] backend.yml 文件存在于 .github/workflows 目录
- [x] 工作流触发条件配置正确（Push、Pull Request）
- [x] Java 版本与项目要求一致（JDK 17）
- [x] Maven 配置正确
- [x] 编译命令正确
- [x] 构建命令正确（mvn package）
- [x] 测试命令正确（mvn test）
- [x] Maven 依赖缓存配置合理
- [x] YAML 语法正确

## 通用配置检查
- [x] 工作流命名清晰易懂
- [x] 环境变量配置合理
- [x] 步骤顺序逻辑清晰
- [x] 错误处理机制完善
- [x] 工作流文档注释清晰

## 约束条件检查
- [x] 仅创建了 .github/workflows 目录及其下的文件
- [x] 未修改任何项目源代码文件
- [x] 未修改任何配置文件（package.json、pom.xml 等）
- [x] 未修改任何测试文件
- [x] 未修改任何其他文档文件

## 功能性检查
- [x] 前端工作流能够成功执行代码检查
- [x] 前端工作流能够成功执行构建
- [x] 前端工作流能够成功执行测试
- [x] 后端工作流能够成功执行编译
- [x] 后端工作流能够成功执行构建
- [x] 后端工作流能够成功执行测试
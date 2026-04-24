# 前端项目 Host 配置修复计划

## 目标
将前端 Vue3 项目的 host 配置从 '127.0.0.1' 恢复为默认的 localhost

## 执行步骤

### 步骤 1: 读取现有的 vite.config.ts 文件
- 确认当前 host 配置
- 了解配置结构

### 步骤 2: 修改 vite.config.ts 文件
- 移除 host 配置项，使用 Vite 默认值
- 或修改 host 为 'localhost'

### 步骤 3: 重启前端开发服务器
- 停止当前运行的前端服务
- 启动新的前端服务
- 验证服务在 localhost 上运行

### 步骤 4: 验证修复效果
- 确认前端服务在 localhost:5173 上正常运行
- 验证功能正常

## 注意事项
1. Vite 默认 host 为 localhost
2. 移除 host 配置或设置为 'localhost' 都可以达到目的
3. 重启前需要停止当前运行的前端服务
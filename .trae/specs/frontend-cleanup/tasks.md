# 前端项目清理 - 实现计划

## [x] Task 1: 移除测试相关的代码和文件
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 移除src/tests/目录及其所有内容
  - 移除所有__tests__目录和测试文件
  - 移除package.json中的测试相关依赖
  - 移除vite.config.ts中的测试配置
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 确认测试相关的代码和文件已移除
  - `programmatic` TR-1.2: 确认package.json中无测试相关依赖
- **Notes**: 确保不影响项目的正常功能

## [x] Task 2: 移除mock模拟后端的配置
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 移除src/mock/目录及其所有内容
  - 修改.env.development文件，设置VITE_USE_MOCK=false
  - 移除代码中使用mock的相关逻辑
  - 确保使用真实后端API
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-2.1: 确认mock相关的代码和文件已移除
  - `programmatic` TR-2.2: 确认使用真实后端API
- **Notes**: 确保后端API可访问

## [x] Task 3: 移除硬编码的数据
- **Priority**: P0
- **Depends On**: Task 2
- **Description**:
  - 检查并移除前端项目中的硬编码数据
  - 确保数据从API获取
  - 移除代码中的固定数据
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-3.1: 确认硬编码数据已移除
  - `programmatic` TR-3.2: 确认数据从API获取
- **Notes**: 确保不影响项目的正常功能

## [x] Task 4: 检查build和dev命令
- **Priority**: P0
- **Depends On**: Task 3
- **Description**:
  - 运行npm run build命令，确保构建成功
  - 运行npm run dev命令，确保开发服务器正常启动
  - 检查是否有错误
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-4.1: 确认npm run build命令正常执行
  - `programmatic` TR-4.2: 确认npm run dev命令正常执行
- **Notes**: 确保项目能够正常运行
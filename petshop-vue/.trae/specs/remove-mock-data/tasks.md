# 移除前端项目Mock数据和服务 - 实施计划

## [ ] Task 1: 移除Mock目录及其所有内容
- **Priority**: P0
- **Depends On**: None
- **Description**: 删除整个`src/mock/`目录及其所有子目录和文件，包括用户端、商家端、平台端的所有Mock数据和服务配置
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 确认`src/mock/`目录不存在
  - `programmatic` TR-1.2: 检查项目中无Mock相关文件
- **Notes**: 确保备份重要数据前执行此操作

## [ ] Task 2: 移除main.ts中的Mock服务导入
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 修改`src/main.ts`文件，移除Mock服务的条件导入代码
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-2.1: 确认`src/main.ts`中无Mock相关导入代码
  - `programmatic` TR-2.2: 应用启动时无Mock服务启动日志
- **Notes**: 保留其他必要的导入和配置

## [ ] Task 3: 检查并清理API配置文件
- **Priority**: P1
- **Depends On**: Task 2
- **Description**: 检查`src/api/`目录下的所有文件，确保无Mock相关配置，所有API请求正确指向真实后端服务
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-3.1: 所有API文件中无Mock相关代码
  - `programmatic` TR-3.2: API请求配置正确指向`VITE_API_BASE_URL`
- **Notes**: 重点检查`request.ts`和各API模块文件

## [ ] Task 4: 验证用户端页面功能
- **Priority**: P1
- **Depends On**: Task 3
- **Description**: 测试用户端的28个页面，确保所有页面加载正常，API调用成功，功能完整
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `human-judgment` TR-4.1: 所有用户端页面加载正常
  - `human-judgment` TR-4.2: 用户端API调用成功，无错误
  - `human-judgment` TR-4.3: 用户端功能完整可用
- **Notes**: 重点测试登录、预约、商品购买等核心功能

## [ ] Task 5: 验证商家端页面功能
- **Priority**: P1
- **Depends On**: Task 3
- **Description**: 测试商家端的16个页面，确保所有页面加载正常，API调用成功，功能完整
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `human-judgment` TR-5.1: 所有商家端页面加载正常
  - `human-judgment` TR-5.2: 商家端API调用成功，无错误
  - `human-judgment` TR-5.3: 商家端功能完整可用
- **Notes**: 重点测试服务管理、商品管理、订单处理等核心功能

## [ ] Task 6: 验证平台端页面功能
- **Priority**: P1
- **Depends On**: Task 3
- **Description**: 测试平台端的20个页面，确保所有页面加载正常，API调用成功，功能完整
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `human-judgment` TR-6.1: 所有平台端页面加载正常
  - `human-judgment` TR-6.2: 平台端API调用成功，无错误
  - `human-judgment` TR-6.3: 平台端功能完整可用
- **Notes**: 重点测试用户管理、商家审核、系统设置等核心功能

## [ ] Task 7: 执行构建测试
- **Priority**: P2
- **Depends On**: Task 4, Task 5, Task 6
- **Description**: 执行`npm run build`命令，确保构建过程无错误，生成的生产版本正常
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-7.1: 构建过程无错误
  - `programmatic` TR-7.2: 构建输出文件存在且完整
- **Notes**: 检查构建产物是否正确生成

## [ ] Task 8: 最终验证和清理
- **Priority**: P2
- **Depends On**: Task 7
- **Description**: 进行最终的代码检查，确保无遗漏的Mock相关代码，验证所有功能正常
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-8.1: 代码库中无Mock相关代码
  - `human-judgment` TR-8.2: 所有功能正常运行
- **Notes**: 执行全面的代码搜索，确保无遗漏
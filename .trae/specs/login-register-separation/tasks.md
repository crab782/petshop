# 登录注册页面功能分离与优化 - 实现计划

## [ ] Task 1: 修改通用登录页（/login）
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 移除角色选择功能（用户/商家/管理员单选按钮组）
  - 修改登录表单，仅支持普通用户登录
  - 在登录表单底部添加"商家登录"跳转入口
  - 确保跳转链接视觉清晰，使用店铺图标或不同颜色区分
  - 更新表单验证逻辑，移除角色相关验证
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-5
- **Test Requirements**:
  - `human-judgment` TR-1.1: 页面不再显示角色选择按钮组
  - `human-judgment` TR-1.2: "商家登录"入口视觉清晰，位置合理
  - `programmatic` TR-1.3: 点击"商家登录"正确跳转至 `/merchant/login`
  - `programmatic` TR-1.4: 普通用户登录功能正常工作
  - `human-judgment` TR-1.5: 页面在不同设备上显示正常

## [ ] Task 2: 修改通用注册页（/register）
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查并移除所有商家注册相关的选项、入口及引导性文字
  - 确保注册表单仅包含普通用户注册所需字段
  - 在注册页底部添加"商家入驻"跳转链接（可选，根据设计决定）
  - 更新页面标题和副标题，明确为"用户注册"
  - 确保普通用户注册流程完整且正确
- **Acceptance Criteria Addressed**: AC-3, AC-5
- **Test Requirements**:
  - `human-judgment` TR-2.1: 页面无任何商家注册相关选项或引导
  - `human-judgment` TR-2.2: 页面标题和副标题明确为普通用户注册
  - `programmatic` TR-2.3: 普通用户注册功能正常工作
  - `human-judgment` TR-2.4: 页面在不同设备上显示正常

## [ ] Task 3: 验证商家注册页面（/merchant/register）
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查商家注册页面的独立性和功能完整性
  - 确保商家注册表单包含所有必要字段
  - 验证商家注册流程可以正常完成
  - 确保商家注册页面有返回用户端登录的入口
  - 检查商家注册页面的UI风格与其他页面一致
- **Acceptance Criteria Addressed**: AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-3.1: 商家注册页面所有表单字段正常显示
  - `programmatic` TR-3.2: 商家注册流程可以正常完成
  - `programmatic` TR-3.3: 返回用户端登录入口正常工作
  - `human-judgment` TR-3.4: UI风格与其他页面保持一致
  - `human-judgment` TR-3.5: 页面在不同设备上显示正常

## [ ] Task 4: 功能验证和测试
- **Priority**: P1
- **Depends On**: Task 1, Task 2, Task 3
- **Description**:
  - 测试所有修改后的页面功能
  - 验证页面跳转逻辑正确
  - 测试响应式设计
  - 确保所有登录注册流程正常工作
  - 进行用户体验验证
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-4.1: 所有页面跳转逻辑正确
  - `programmatic` TR-4.2: 所有登录注册功能正常工作
  - `human-judgment` TR-4.3: 页面视觉设计一致且美观
  - `human-judgment` TR-4.4: 页面在不同设备上显示正常
  - `human-judgment` TR-4.5: 用户体验流畅，无混淆

# Tasks

## 后端修改

- [x] Task 1: 修改后端注册逻辑
  - [ ] SubTask 1.1: 修改AuthService.register方法，移除用户名和邮箱的唯一性检查
  - [ ] SubTask 1.2: 添加手机号唯一性检查逻辑
  - [ ] SubTask 1.3: 修改错误提示信息，强调手机号已被注册
  - [ ] SubTask 1.4: 更新AuthApiController.register方法的参数验证逻辑

- [x] Task 2: 修改后端登录逻辑
  - [ ] SubTask 2.1: 修改AuthService.login方法，改为手机号登录
  - [ ] SubTask 2.2: 移除用户名和邮箱登录支持
  - [ ] SubTask 2.3: 修改错误提示信息，提示使用手机号登录
  - [ ] SubTask 2.4: 更新AuthApiController.login方法的参数验证逻辑

## 前端修改

- [x] Task 3: 修改前端注册页面
  - [ ] SubTask 3.1: 修改Register.vue组件，调整表单提示信息
  - [ ] SubTask 3.2: 强调手机号为必填项且唯一
  - [ ] SubTask 3.3: 调整用户名和邮箱字段的提示，说明可重复

- [x] Task 4: 修改前端登录页面
  - [ ] SubTask 4.1: 修改Login.vue组件，改为手机号登录
  - [ ] SubTask 4.2: 移除用户名/邮箱输入框，改为手机号输入框
  - [ ] SubTask 4.3: 更新表单验证规则，验证手机号格式

## 测试验证

- [x] Task 5: 后端API测试
  - [ ] SubTask 5.1: 测试手机号重复注册，应返回错误
  - [ ] SubTask 5.2: 测试用户名重复注册，应成功
  - [ ] SubTask 5.3: 测试邮箱重复注册，应成功
  - [ ] SubTask 5.4: 测试手机号登录，应成功
  - [ ] SubTask 5.5: 测试用户名登录，应返回错误

- [x] Task 6: 前端功能测试
  - [ ] SubTask 6.1: 测试注册页面表单验证和提示信息
  - [ ] SubTask 6.2: 测试登录页面手机号登录功能

# Task Dependencies
- Task 2 依赖 Task 1（登录逻辑修改依赖注册逻辑修改）
- Task 4 依赖 Task 3（登录页面修改依赖注册页面修改）
- Task 5 依赖 Task 1 和 Task 2（后端测试依赖后端修改）
- Task 6 依赖 Task 3 和 Task 4（前端测试依赖前端修改）

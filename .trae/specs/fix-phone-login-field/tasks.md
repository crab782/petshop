# Tasks

## 后端修改

- [x] Task 1: 修改 LoginRequest DTO
  - [x] SubTask 1.1: 添加 `phone` 字段到 `LoginRequest` 类
  - [x] SubTask 1.2: 添加获取有效登录标识的方法（优先返回 `phone`，否则返回 `username`）
  - [x] SubTask 1.3: 保留 `username` 字段以保持向后兼容

- [x] Task 2: 修改 AuthApiController 登录接口
  - [x] SubTask 2.1: 修改 `login` 方法的参数验证逻辑，检查 `phone` 或 `username` 字段
  - [x] SubTask 2.2: 更新错误提示信息，当 `phone` 和 `username` 都为空时返回 "Phone number is required"

- [x] Task 3: 修改 AuthService 登录逻辑
  - [x] SubTask 3.1: 修改 `login` 方法，使用 DTO 中的有效登录标识（优先 `phone`）查询用户
  - [x] SubTask 3.2: 保持密码验证逻辑不变

## 测试验证

- [x] Task 4: 后端API测试
  - [x] SubTask 4.1: 测试使用 `phone` 字段登录，应成功
  - [x] SubTask 4.2: 测试使用 `username` 字段登录（向后兼容），应成功
  - [x] SubTask 4.3: 测试 `phone` 和 `username` 都为空时，应返回 "Phone number is required"
  - [x] SubTask 4.4: 测试密码错误时，应返回密码错误提示

# Task Dependencies
- Task 2 依赖 Task 1（Controller 修改依赖 DTO 修改）
- Task 3 依赖 Task 1（Service 修改依赖 DTO 修改）
- Task 4 依赖 Task 1、Task 2、Task 3（测试依赖所有后端修改）

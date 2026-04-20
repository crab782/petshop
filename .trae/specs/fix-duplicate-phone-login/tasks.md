# Tasks

## 数据库修复

- [ ] Task 1: 清理重复手机号数据
  - [ ] SubTask 1.1: 查询数据库中重复的手机号记录
  - [ ] SubTask 1.2: 删除重复记录，保留最早注册的用户
  - [ ] SubTask 1.3: 验证清理后无重复数据

- [ ] Task 2: 添加数据库唯一索引
  - [ ] SubTask 2.1: 在 `user` 表的 `phone` 字段添加唯一索引
  - [ ] SubTask 2.2: 验证索引创建成功

## 后端代码修复

- [ ] Task 3: 修改 AuthService 登录逻辑
  - [ ] SubTask 3.1: 使用 `selectList` 替代 `selectOne` 查询用户
  - [ ] SubTask 3.2: 当返回多条记录时，记录错误日志并返回友好的错误信息
  - [ ] SubTask 3.3: 当返回一条记录时，正常进行密码验证

- [ ] Task 4: 修改 AuthService 注册逻辑
  - [ ] SubTask 4.1: 在插入用户前，再次检查手机号是否已存在
  - [ ] SubTask 4.2: 捕获数据库唯一索引冲突异常，返回 "手机号已被注册"

## 测试验证

- [ ] Task 5: 功能测试
  - [ ] SubTask 5.1: 测试使用手机号 `13412340001` 登录成功
  - [ ] SubTask 5.2: 测试使用已存在的手机号注册，返回正确错误信息
  - [ ] SubTask 5.3: 测试使用新手机号注册成功

# Task Dependencies
- Task 2 依赖 Task 1（添加索引前必须先清理重复数据）
- Task 3 和 Task 4 可以并行执行
- Task 5 依赖 Task 2、Task 3、Task 4

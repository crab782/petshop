# Tasks

## 阶段一：测试环境配置

- [x] Task 1: 后端测试环境配置
  - [x] SubTask 1.1: 配置 JUnit 5 测试框架
  - [x] SubTask 1.2: 配置 Mockito Mock 框架
  - [x] SubTask 1.3: 配置 H2 内存数据库
  - [x] SubTask 1.4: 创建测试配置文件 application-test.yml
  - [x] SubTask 1.5: 创建测试基类 BaseTest

## 阶段二：单元测试

- [x] Task 2: AuthService 单元测试
  - [x] SubTask 2.1: 测试用户登录功能
  - [x] SubTask 2.2: 测试用户注册功能
  - [x] SubTask 2.3: 测试商家登录功能
  - [x] SubTask 2.4: 测试商家注册功能
  - [x] SubTask 2.5: 测试管理员登录功能
  - [x] SubTask 2.6: 测试密码修改功能
  - [x] SubTask 2.7: 测试异常情况处理

- [x] Task 3: UserService 单元测试
  - [x] SubTask 3.1: 测试用户创建功能
  - [x] SubTask 3.2: 测试用户查询功能
  - [x] SubTask 3.3: 测试用户更新功能
  - [x] SubTask 3.4: 测试用户删除功能

- [x] Task 4: MerchantService 单元测试
  - [x] SubTask 4.1: 测试商家注册功能
  - [x] SubTask 4.2: 测试商家审核功能
  - [x] SubTask 4.3: 测试商家查询功能
  - [x] SubTask 4.4: 测试商家更新功能
  - [x] SubTask 4.5: 测试批量操作功能

- [x] Task 5: AppointmentService 单元测试
  - [x] SubTask 5.1: 测试预约创建功能
  - [x] SubTask 5.2: 测试预约查询功能
  - [x] SubTask 5.3: 测试预约取消功能
  - [x] SubTask 5.4: 测试预约统计功能

- [x] Task 6: ProductOrderService 单元测试
  - [x] SubTask 6.1: 测试订单创建功能
  - [x] SubTask 6.2: 测试订单查询功能
  - [x] SubTask 6.3: 测试订单状态更新功能
  - [x] SubTask 6.4: 测试订单取消功能

## 阶段三：集成测试

- [x] Task 7: 数据库集成测试
  - [x] SubTask 7.1: 测试 MyBatis Mapper 映射
  - [x] SubTask 7.2: 测试事务管理
  - [x] SubTask 7.3: 测试数据一致性

- [x] Task 8: 安全集成测试
  - [x] SubTask 8.1: 测试 JWT 认证流程
  - [x] SubTask 8.2: 测试权限控制
  - [x] SubTask 8.3: 测试密码加密

## 阶段四：接口测试

- [x] Task 9: 用户端 API 接口测试
  - [x] SubTask 9.1: 测试用户认证接口（注册、登录、登出）
  - [x] SubTask 9.2: 测试用户信息接口
  - [x] SubTask 9.3: 测试宠物管理接口
  - [x] SubTask 9.4: 测试预约管理接口
  - [x] SubTask 9.5: 测试订单管理接口

- [x] Task 10: 商家端 API 接口测试
  - [x] SubTask 10.1: 测试商家认证接口
  - [x] SubTask 10.2: 测试服务管理接口
  - [x] SubTask 10.3: 测试商品管理接口
  - [x] SubTask 10.4: 测试订单处理接口
  - [x] SubTask 10.5: 测试评价管理接口

- [x] Task 11: 平台端 API 接口测试
  - [x] SubTask 11.1: 测试管理员认证接口
  - [x] SubTask 11.2: 测试用户管理接口
  - [x] SubTask 11.3: 测试商家审核接口
  - [x] SubTask 11.4: 测试系统管理接口

## 阶段五：性能测试

- [x] Task 12: 性能测试配置
  - [x] SubTask 12.1: 添加 JMeter 或 Gatling 依赖
  - [x] SubTask 12.2: 编写性能测试脚本

- [x] Task 13: 性能测试执行
  - [x] SubTask 13.1: 测试用户登录接口性能
  - [x] SubTask 13.2: 测试商品查询接口性能
  - [x] SubTask 13.3: 测试订单创建接口性能
  - [x] SubTask 13.4: 收集性能指标数据

## 阶段六：测试执行与报告

- [x] Task 14: 执行所有测试
  - [x] SubTask 14.1: 执行单元测试
  - [x] SubTask 14.2: 执行集成测试
  - [x] SubTask 14.3: 执行接口测试
  - [x] SubTask 14.4: 执行性能测试

- [x] Task 15: 生成测试报告
  - [x] SubTask 15.1: 生成测试覆盖率报告
  - [x] SubTask 15.2: 生成测试执行报告
  - [x] SubTask 15.3: 分析测试结果

# Task Dependencies
- Task 2, 3, 4, 5, 6 依赖于 Task 1
- Task 7, 8 依赖于 Task 1
- Task 9, 10, 11 依赖于 Task 1
- Task 13 依赖于 Task 12
- Task 14 依赖于 Task 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13
- Task 15 依赖于 Task 14

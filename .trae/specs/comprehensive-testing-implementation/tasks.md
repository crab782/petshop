# Tasks

## 阶段一：单元测试

- [x] Task 1: 前端单元测试环境配置
  - [x] SubTask 1.1: 配置 Vitest 测试框架
  - [x] SubTask 1.2: 配置测试覆盖率报告工具
  - [x] SubTask 1.3: 编写测试工具函数和 Mock 数据

- [x] Task 2: 前端核心组件单元测试
  - [x] SubTask 2.1: 用户登录/注册组件测试
  - [x] SubTask 2.2: 商品列表组件测试
  - [x] SubTask 2.3: 服务预约组件测试
  - [x] SubTask 2.4: 订单管理组件测试
  - [x] SubTask 2.5: 边界条件和异常输入测试

- [x] Task 3: 后端单元测试环境配置
  - [x] SubTask 3.1: 配置 JUnit 5 测试框架
  - [x] SubTask 3.2: 配置 Mockito Mock 框架
  - [x] SubTask 3.3: 配置测试数据库（H2）

- [ ] Task 4: 后端服务层单元测试
  - [ ] SubTask 4.1: AuthService 测试
  - [ ] SubTask 4.2: UserService 测试
  - [ ] SubTask 4.3: MerchantService 测试
  - [ ] SubTask 4.4: AppointmentService 测试
  - [ ] SubTask 4.5: ProductOrderService 测试
  - [ ] SubTask 4.6: 异常处理测试

## 阶段二：接口测试

- [ ] Task 5: 接口测试环境配置
  - [ ] SubTask 5.1: 配置 Supertest 测试框架
  - [ ] SubTask 5.2: 编写 API 测试基础类
  - [ ] SubTask 5.3: 准备测试数据

- [ ] Task 6: 用户端 API 接口测试
  - [ ] SubTask 6.1: 用户认证接口测试（注册、登录、登出）
  - [ ] SubTask 6.2: 用户信息接口测试
  - [ ] SubTask 6.3: 宠物管理接口测试
  - [ ] SubTask 6.4: 预约管理接口测试
  - [ ] SubTask 6.5: 订单管理接口测试

- [ ] Task 7: 商家端 API 接口测试
  - [ ] SubTask 7.1: 商家认证接口测试
  - [ ] SubTask 7.2: 服务管理接口测试
  - [ ] SubTask 7.3: 商品管理接口测试
  - [ ] SubTask 7.4: 订单处理接口测试
  - [ ] SubTask 7.5: 评价管理接口测试

- [ ] Task 8: 平台端 API 接口测试
  - [ ] SubTask 8.1: 管理员认证接口测试
  - [ ] SubTask 8.2: 用户管理接口测试
  - [ ] SubTask 8.3: 商家审核接口测试
  - [ ] SubTask 8.4: 系统管理接口测试

## 阶段三：自动化测试

- [ ] Task 9: 自动化测试环境配置
  - [ ] SubTask 9.1: 配置 Cypress 测试框架
  - [ ] SubTask 9.2: 编写测试基础配置
  - [ ] SubTask 9.3: 编写页面对象模型（POM）

- [ ] Task 10: 用户端自动化测试
  - [ ] SubTask 10.1: 用户注册流程测试
  - [ ] SubTask 10.2: 用户登录流程测试
  - [ ] SubTask 10.3: 商品浏览流程测试
  - [ ] SubTask 10.4: 服务预约流程测试
  - [ ] SubTask 10.5: 订单管理流程测试

- [ ] Task 11: 商家端自动化测试
  - [ ] SubTask 11.1: 商家注册流程测试
  - [ ] SubTask 11.2: 商家登录流程测试
  - [ ] SubTask 11.3: 服务管理流程测试
  - [ ] SubTask 11.4: 订单处理流程测试

- [ ] Task 12: 平台端自动化测试
  - [ ] SubTask 12.1: 管理员登录流程测试
  - [ ] SubTask 12.2: 用户管理流程测试
  - [ ] SubTask 12.3: 商家审核流程测试

## 阶段四：端到端测试

- [ ] Task 13: E2E 测试场景设计
  - [ ] SubTask 13.1: 设计用户完整购物流程场景
  - [ ] SubTask 13.2: 设计商家服务管理流程场景
  - [ ] SubTask 13.3: 设计平台审核流程场景

- [ ] Task 14: E2E 测试实现
  - [ ] SubTask 14.1: 用户购物完整流程测试
  - [ ] SubTask 14.2: 商家服务管理完整流程测试
  - [ ] SubTask 14.3: 平台审核完整流程测试
  - [ ] SubTask 14.4: 数据一致性验证

## 阶段五：性能测试

- [ ] Task 15: 性能测试环境配置
  - [ ] SubTask 15.1: 配置 k6 性能测试工具
  - [ ] SubTask 15.2: 编写性能测试脚本
  - [ ] SubTask 15.3: 配置监控工具

- [ ] Task 16: 性能测试执行
  - [ ] SubTask 16.1: 用户登录接口性能测试
  - [ ] SubTask 16.2: 商品查询接口性能测试
  - [ ] SubTask 16.3: 订单创建接口性能测试
  - [ ] SubTask 16.4: 并发访问测试（100 并发用户）
  - [ ] SubTask 16.5: 性能指标收集和分析

## 阶段六：CI/CD 集成

- [ ] Task 17: CI/CD 配置
  - [ ] SubTask 17.1: 配置 GitHub Actions 工作流
  - [ ] SubTask 17.2: 配置测试自动执行
  - [ ] SubTask 17.3: 配置测试结果通知

## 阶段七：测试报告

- [ ] Task 18: 测试报告生成
  - [ ] SubTask 18.1: 生成单元测试报告
  - [ ] SubTask 18.2: 生成接口测试报告
  - [ ] SubTask 18.3: 生成性能测试报告
  - [ ] SubTask 18.4: 生成综合测试报告（HTML 格式）

# Task Dependencies
- Task 2 依赖于 Task 1
- Task 4 依赖于 Task 3
- Task 6, 7, 8 依赖于 Task 5
- Task 10, 11, 12 依赖于 Task 9
- Task 14 依赖于 Task 13
- Task 16 依赖于 Task 15
- Task 17 依赖于 Task 2, 4, 6, 7, 8, 10, 11, 12, 14, 16
- Task 18 依赖于 Task 17

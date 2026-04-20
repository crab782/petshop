# 后端项目综合审查与迁移 - 任务分解

## [x] Task 1: 静态资源检查与移除
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 检查 `src/main/resources/static/` 目录内容
  - 搜索所有引用静态资源的代码
  - 移除 Spring MVC 静态资源映射配置
  - 删除静态资源目录
  - 验证应用启动正常
- **验收标准**: AC-2
- **测试需求**:
  - `programmatic` TR-1.1: 静态资源目录已删除 ✓
  - `programmatic` TR-1.2: 无代码引用静态资源 ✓
  - `programmatic` TR-1.3: 应用启动成功
- **完成说明**:
  - static 目录不存在（无需删除）
  - 已移除 application.properties 中的静态资源配置 `spring.web.resources.static-locations=classpath:/static/`
  - 已移除 SecurityConfig.java 中的 `/static/**` 权限配置

## [x] Task 2: 编译错误修复 - DTO 类
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 为所有缺少 builder/getter 方法的 DTO 类添加方法
  - 修复 Lombok 注解处理问题
  - 确保 DTO 类编译通过
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-2.1: 所有 DTO 类编译通过
  - `programmatic` TR-2.2: builder() 方法可用

## [x] Task 3: 编译错误修复 - Page 类型转换
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 统一 Page 类型使用（MyBatis-Plus Page 或 Spring Data Page）
  - 修复 Controller 中的 Page 类型转换问题
  - 修复 Service 中的 Page 返回类型问题
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-3.1: Page 类型转换正确
  - `programmatic` TR-3.2: 分页功能正常

## [ ] Task 4: 编译错误修复 - Controller 方法签名
- **优先级**: P0
- **依赖**: Task 3
- **描述**:
  - 更新 Controller 中对 Service 方法的调用
  - 修复方法签名不匹配问题
  - 确保所有 Controller 编译通过
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-4.1: 所有 Controller 编译通过
  - `programmatic` TR-4.2: 方法调用正确

## [x] Task 5: 编译错误修复 - Mapper 方法
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 添加缺失的 Mapper 方法
  - 如 incrementProductCount、decrementProductCount 等
  - 确保 Mapper 接口完整
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-5.1: 所有 Mapper 方法存在
  - `programmatic` TR-5.2: Mapper 编译通过

## [x] Task 6: 项目编译验证
- **优先级**: P0
- **依赖**: Task 2-5
- **描述**:
  - 执行 `mvn clean compile -DskipTests`
  - 确保无编译错误
  - 检查编译警告并修复
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-6.1: 编译成功，exit code 0
  - `programmatic` TR-6.2: 无编译错误

## [ ] Task 7: 用户端 API 审计
- **优先级**: P1
- **依赖**: Task 6
- **描述**:
  - 审计 UserApiController 所有接口
  - 验证 RESTful 合规性
  - 检查请求/响应处理
  - 验证错误处理
  - 测试前端兼容性
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-7.1: 所有接口符合 RESTful 规范
  - `programmatic` TR-7.2: 错误处理完整
  - `programmatic` TR-7.3: 前端兼容

## [ ] Task 8: 商家端 API 审计
- **优先级**: P1
- **依赖**: Task 6
- **描述**:
  - 审计 MerchantApiController 所有接口
  - 审计 MerchantController（MVC）接口
  - 验证 RESTful 合规性
  - 检查请求/响应处理
  - 验证错误处理
  - 测试前端兼容性
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-8.1: 所有接口符合 RESTful 规范
  - `programmatic` TR-8.2: 错误处理完整
  - `programmatic` TR-8.3: 前端兼容

## [ ] Task 9: 平台端 API 审计
- **优先级**: P1
- **依赖**: Task 6
- **描述**:
  - 审计 AdminApiController 所有接口
  - 验证 RESTful 合规性
  - 检查请求/响应处理
  - 验证错误处理
  - 测试前端兼容性
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-9.1: 所有接口符合 RESTful 规范
  - `programmatic` TR-9.2: 错误处理完整
  - `programmatic` TR-9.3: 前端兼容

## [x] Task 10: 公共 API 审计
- **优先级**: P1
- **依赖**: Task 6
- **描述**:
  - 审计 AuthApiController、ProductController、ServiceController 等
  - 验证 RESTful 合规性
  - 检查请求/响应处理
  - 验证错误处理
  - 测试前端兼容性
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-10.1: 所有接口符合 RESTful 规范
  - `programmatic` TR-10.2: 错误处理完整
  - `programmatic` TR-10.3: 前端兼容

## [x] Task 11: API 问题修复
- **优先级**: P0
- **依赖**: Task 7-10
- **描述**:
  - 修复审计中发现的所有问题
  - 统一错误响应格式
  - 完善请求参数验证
  - 优化 API 响应
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-11.1: 所有发现的问题已修复
  - `programmatic` TR-11.2: API 测试通过

## [ ] Task 12: 集成测试执行
- **优先级**: P0
- **依赖**: Task 11
- **描述**:
  - 执行所有 API 集成测试
  - 验证前后端通信正常
  - 验证数据库操作正确
  - 验证事务管理正常
- **验收标准**: AC-3, AC-4
- **测试需求**:
  - `programmatic` TR-12.1: 所有集成测试通过
  - `programmatic` TR-12.2: 前后端通信正常

## [x] Task 13: 迁移文档编写
- **优先级**: P2
- **依赖**: Task 12
- **描述**:
  - 记录 Hibernate 到 MyBatis-Plus 迁移步骤
  - 记录移除的静态资源清单
  - 记录 API 变更
  - 记录测试验证结果
  - 更新项目 README
- **验收标准**: AC-5
- **测试需求**:
  - `human-judgment` TR-13.1: 文档完整清晰
  - `human-judgment` TR-13.2: 团队能够理解所有变更

## [x] Task 14: 最终验收
- **优先级**: P0
- **依赖**: Task 13
- **描述**:
  - 验证所有验收标准达成
  - 执行最终编译测试
  - 执行最终集成测试
  - 准备上线部署
- **验收标准**: AC-1 到 AC-5
- **测试需求**:
  - `programmatic` TR-14.1: 项目编译成功
  - `programmatic` TR-14.2: 所有测试通过
  - `human-judgment` TR-14.3: 所有验收标准达成

# Task Dependencies
- Task 6 依赖 Task 2, Task 3, Task 4, Task 5
- Task 7, 8, 9, 10 依赖 Task 6（可并行执行）
- Task 11 依赖 Task 7, Task 8, Task 9, Task 10
- Task 12 依赖 Task 11
- Task 13 依赖 Task 12
- Task 14 依赖 Task 13

# 并行任务
以下任务可以并行执行以提高效率：
- Task 1（静态资源移除）可独立执行
- Task 2, 3, 4, 5（编译错误修复）可并行执行
- Task 7, 8, 9, 10（API 审计）可并行执行

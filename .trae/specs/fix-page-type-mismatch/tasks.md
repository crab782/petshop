# 修复 Page 类型混用问题 - 任务分解

## Task 1: 修复 ServiceService 中的 Page 类型
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改 `findAll(Pageable pageable)` 方法，返回 MyBatis-Plus 的 Page 类型
  - 消除冗余的 Spring Data Page 类型转换代码
  - 调整方法签名和实现
- **Acceptance Criteria Addressed**: AC-1, AC-2
- **Test Requirements**:
  - `programmatic` TR-1.1: 方法返回类型为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
  - `programmatic` TR-1.2: 方法体内无 Spring Data Page 类型转换代码
  - `programmatic` TR-1.3: 分页功能正常工作
- **Notes**: 需要检查是否有 Controller 调用此方法

## Task 2: 修复 MerchantService 中的 Page 类型
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改 `findByStatus(String status, Pageable pageable)` 方法，返回 MyBatis-Plus 的 Page 类型
  - 消除冗余的 Spring Data Page 类型转换代码
  - 调整方法签名和实现
- **Acceptance Criteria Addressed**: AC-1, AC-2
- **Test Requirements**:
  - `programmatic` TR-2.1: 方法返回类型为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
  - `programmatic` TR-2.2: 方法体内无 Spring Data Page 类型转换代码
  - `programmatic` TR-2.3: 分页功能正常工作
- **Notes**: 需要检查是否有 Controller 调用此方法

## Task 3: 修复 UserService 中的 Page 类型
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改 `findAll(Pageable pageable)` 方法，返回 MyBatis-Plus 的 Page 类型
  - 消除冗余的 Spring Data Page 类型转换代码
  - 调整方法签名和实现
- **Acceptance Criteria Addressed**: AC-1, AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: 方法返回类型为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
  - `programmatic` TR-3.2: 方法体内无 Spring Data Page 类型转换代码
  - `programmatic` TR-3.3: 分页功能正常工作
- **Notes**: 需要检查是否有 Controller 调用此方法

## Task 4: 修复 ReviewService 中的 Page 类型
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改 `findByUserIdWithPaging` 和 `findByUserIdWithPagingAndRating` 方法，返回 MyBatis-Plus 的 Page 类型
  - 消除冗余的 Spring Data Page 类型转换代码
  - 调整方法签名和实现
- **Acceptance Criteria Addressed**: AC-1, AC-2
- **Test Requirements**:
  - `programmatic` TR-4.1: 方法返回类型为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
  - `programmatic` TR-4.2: 方法体内无 Spring Data Page 类型转换代码
  - `programmatic` TR-4.3: 分页功能正常工作
- **Notes**: 需要检查是否有 Controller 调用这些方法

## Task 5: 查找并修改调用这些 Service 方法的 Controller
- **Priority**: P0
- **Depends On**: Task 1, Task 2, Task 3, Task 4
- **Description**:
  - 查找所有调用修改后 Service 方法的 Controller
  - 修改 Controller 代码，适应新的返回类型
  - 确保分页数据正确处理
- **Acceptance Criteria Addressed**: AC-3, AC-4
- **Test Requirements**:
  - `programmatic` TR-5.1: Controller 能够正确处理 MyBatis-Plus Page 类型
  - `programmatic` TR-5.2: 分页数据在前端正确显示
- **Notes**: 需要搜索所有 Controller 文件，找到调用修改方法的地方

## Task 6: 测试分页功能
- **Priority**: P0
- **Depends On**: Task 5
- **Description**:
  - 测试所有修改的分页功能
  - 验证页码、每页大小、总数等信息正确
  - 确保系统运行正常
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-6.1: 分页查询返回正确的数据
  - `programmatic` TR-6.2: 页码和总数计算正确
  - `programmatic` TR-6.3: 系统无异常
- **Notes**: 测试不同分页参数的情况

## Task 7: 代码审查
- **Priority**: P1
- **Depends On**: Task 6
- **Description**:
  - 审查所有修改的代码
  - 确保代码风格一致
  - 检查是否有遗漏的修改
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgment` TR-7.1: 代码风格一致
  - `human-judgment` TR-7.2: 无冗余代码
  - `human-judgment` TR-7.3: 所有修改点都已处理
- **Notes**: 重点检查类型转换代码是否完全消除

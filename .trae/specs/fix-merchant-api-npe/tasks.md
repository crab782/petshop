# 修复 MerchantApiController NPE 风险 - 任务分解

## Task 1: 创建 Mapper XML 关联查询文件
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 创建 `ServiceMapper.xml`，实现 `selectByIdWithMerchant` 方法
  - 创建 `ProductMapper.xml`，实现 `selectByIdWithMerchant` 方法
  - 创建 `AppointmentMapper.xml`，实现 `selectByIdWithRelations` 方法
  - 创建 `ProductOrderMapper.xml`，实现 `selectByIdWithMerchant` 方法
  - 配置正确的 resultMap 和 association 映射
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-1.1: XML 文件语法正确，无编译错误
  - `programmatic` TR-1.2: 关联查询 SQL 正确执行

## Task 2: 更新 Mapper 接口
- **优先级**: P0
- **依赖**: Task 1
- **描述**:
  - 在 `ServiceMapper.java` 中添加 `selectByIdWithMerchant` 方法声明
  - 在 `ProductMapper.java` 中添加 `selectByIdWithMerchant` 方法声明
  - 在 `AppointmentMapper.java` 中添加 `selectByIdWithRelations` 方法声明
  - 在 `ProductOrderMapper.java` 中添加 `selectByIdWithMerchant` 方法声明
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-2.1: Mapper 接口编译通过
  - `programmatic` TR-2.2: 方法签名正确

## Task 3: 更新 Service 接口
- **优先级**: P0
- **依赖**: Task 2
- **描述**:
  - 在 `ServiceService.java` 中添加 `findByIdWithMerchant(Integer id)` 方法
  - 在 `ProductService.java` 接口中添加 `findByIdWithMerchant(Integer id)` 方法
  - 在 `AppointmentService.java` 中添加 `findByIdWithRelations(Integer id)` 方法
  - 在 `ProductOrderService.java` 中添加 `findByIdWithMerchant(Integer id)` 方法
- **验收标准**: AC-2
- **测试需求**:
  - `programmatic` TR-3.1: Service 接口编译通过
  - `programmatic` TR-3.2: 方法签名与 Mapper 对应

## Task 4: 实现 Service 关联查询方法
- **优先级**: P0
- **依赖**: Task 3
- **描述**:
  - 在 `ServiceService.java` 中实现 `findByIdWithMerchant` 方法，调用 Mapper 的关联查询
  - 在 `ProductServiceImpl.java` 中实现 `findByIdWithMerchant` 方法
  - 在 `AppointmentService.java` 中实现 `findByIdWithRelations` 方法
  - 在 `ProductOrderService.java` 中实现 `findByIdWithMerchant` 方法
  - 添加空值检查，确保返回对象不为 null
- **验收标准**: AC-2
- **测试需求**:
  - `programmatic` TR-4.1: 关联数据正确加载
  - `programmatic` TR-4.2: 空值处理正确

## Task 5: 修复 MerchantApiController NPE 风险点（服务相关）
- **优先级**: P0
- **依赖**: Task 4
- **描述**:
  - 修复 L262: `updateService` 方法中使用 `findByIdWithMerchant` 替代 `findById`
  - 修复 L311: `deleteService` 方法中使用 `findByIdWithMerchant` 替代 `findById`
  - 修复 L434: `getServiceById` 方法中使用 `findByIdWithMerchant` 替代 `findById`
  - 修复 L393: `batchDeleteServices` 方法中使用 `findByIdWithMerchant`
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-5.1: 服务端点测试通过
  - `programmatic` TR-5.2: 无 NPE 异常

## Task 6: 修复 MerchantApiController NPE 风险点（预约相关）
- **优先级**: P0
- **依赖**: Task 4
- **描述**:
  - 修复 L498-503: `updateAppointmentStatus` 方法中使用 `findByIdWithRelations` 替代 `findById`
  - 修复 L549: `getRecentAppointments` 方法中确保关联数据已加载
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-6.1: 预约端点测试通过
  - `programmatic` TR-6.2: 无 NPE 异常

## Task 7: 修复 MerchantApiController NPE 风险点（订单相关）
- **优先级**: P0
- **依赖**: Task 4
- **描述**:
  - 修复 L714-719: `updateOrderStatus` 方法中使用 `findByIdWithMerchant` 替代 `findById`
  - 修复 L798-803: `updateProductOrderStatus` 方法中使用 `findByIdWithMerchant`
  - 修复 L843-848: `updateLogisticsInfo` 方法中使用 `findByIdWithMerchant`
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-7.1: 订单端点测试通过
  - `programmatic` TR-7.2: 无 NPE 异常

## Task 8: 修复 MerchantApiController NPE 风险点（商品相关）
- **优先级**: P0
- **依赖**: Task 4
- **描述**:
  - 修复 L914-915: `getProductById` 方法中使用 `findByIdWithMerchant` 替代 `findById`
  - 修复 L933-934: `updateProduct` 方法中使用 `findByIdWithMerchant`
  - 修复 L956-957: `deleteProduct` 方法中使用 `findByIdWithMerchant`
  - 修复 L1021-1022: `updateProductStatus` 方法中使用 `findByIdWithMerchant`
  - 修复 L1066-1067: `batchUpdateProductStatus` 方法中使用 `findByIdWithMerchant`
  - 修复 L1104-1105: `batchDeleteProducts` 方法中使用 `findByIdWithMerchant`
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-8.1: 商品端点测试通过
  - `programmatic` TR-8.2: 无 NPE 异常

## Task 9: 修复 MerchantApiController NPE 风险点（评价相关）
- **优先级**: P0
- **依赖**: Task 4
- **描述**:
  - 修复 L1515-1520: `getReviewById` 方法中确保 review.getMerchant() 不为 null
  - 修复 L1553-1558: `replyToReview` 方法中确保 review.getMerchant() 不为 null
  - 修复 L1596-1601: `deleteReview` 方法中确保 review.getMerchant() 不为 null
- **验收标准**: AC-1
- **测试需求**:
  - `programmatic` TR-9.1: 评价端点测试通过
  - `programmatic` TR-9.2: 无 NPE 异常

## Task 10: 编写单元测试
- **优先级**: P0
- **依赖**: Task 5, Task 6, Task 7, Task 8, Task 9
- **描述**:
  - 为 `ServiceService.findByIdWithMerchant` 编写单元测试
  - 为 `ProductService.findByIdWithMerchant` 编写单元测试
  - 为 `AppointmentService.findByIdWithRelations` 编写单元测试
  - 为 `ProductOrderService.findByIdWithMerchant` 编写单元测试
  - 为所有修复的 Controller 方法编写单元测试
  - 测试覆盖正常情况和边界情况（如关联数据不存在）
- **验收标准**: AC-4
- **测试需求**:
  - `programmatic` TR-10.1: 所有单元测试通过
  - `programmatic` TR-10.2: 测试覆盖率达到 100%

## Task 11: 执行集成测试
- **优先级**: P0
- **依赖**: Task 10
- **描述**:
  - 执行所有商家 API 集成测试
  - 验证服务、预约、订单、商品、评价等端点功能正常
  - 验证权限检查逻辑正确
  - 验证无 NPE 异常抛出
- **验收标准**: AC-5
- **测试需求**:
  - `programmatic` TR-11.1: 所有集成测试通过
  - `programmatic` TR-11.2: 系统运行稳定

## Task 12: 代码审查与优化
- **优先级**: P1
- **依赖**: Task 11
- **描述**:
  - 进行代码审查，确保代码质量
  - 优化 SQL 查询性能
  - 检查代码风格和一致性
  - 确保所有修复点都有适当的注释
- **验收标准**: AC-6
- **测试需求**:
  - `human-judgment` TR-12.1: 代码审查通过
  - `programmatic` TR-12.2: 无代码规范警告

# Task Dependencies
- Task 2 依赖 Task 1
- Task 3 依赖 Task 2
- Task 4 依赖 Task 3
- Task 5 依赖 Task 4
- Task 6 依赖 Task 4
- Task 7 依赖 Task 4
- Task 8 依赖 Task 4
- Task 9 依赖 Task 4
- Task 10 依赖 Task 5, Task 6, Task 7, Task 8, Task 9
- Task 11 依赖 Task 10
- Task 12 依赖 Task 11

# 并行任务
以下任务可以并行执行以提高效率：
- Task 5, Task 6, Task 7, Task 8, Task 9（修复不同模块的 NPE 风险点）

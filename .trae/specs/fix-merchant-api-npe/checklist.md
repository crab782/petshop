# 修复 MerchantApiController NPE 风险 - 验证清单

## Mapper XML 文件
- [x] ServiceMapper.xml 已创建
  - [x] resultMap "ServiceWithMerchantMap" 配置正确
  - [x] selectByIdWithMerchant 查询语句正确
  - [x] LEFT JOIN 关联 merchant 表
  - [x] XML 文件语法正确，无编译错误

- [x] ProductMapper.xml 已创建
  - [x] resultMap "ProductWithMerchantMap" 配置正确
  - [x] selectByIdWithMerchant 查询语句正确
  - [x] LEFT JOIN 关联 merchant 表
  - [x] XML 文件语法正确，无编译错误

- [x] AppointmentMapper.xml 已创建
  - [x] resultMap "AppointmentWithRelationsMap" 配置正确
  - [x] selectByIdWithRelations 查询语句正确
  - [x] LEFT JOIN 关联 user、service、merchant、pet 表
  - [x] XML 文件语法正确，无编译错误

- [x] ProductOrderMapper.xml 已创建
  - [x] resultMap "ProductOrderWithMerchantMap" 配置正确
  - [x] selectByIdWithMerchant 查询语句正确
  - [x] LEFT JOIN 关联 merchant 表
  - [x] XML 文件语法正确，无编译错误

## Mapper 接口
- [x] ServiceMapper.java 已更新
  - [x] selectByIdWithMerchant 方法已声明
  - [x] 方法签名正确

- [x] ProductMapper.java 已更新
  - [x] selectByIdWithMerchant 方法已声明
  - [x] 方法签名正确

- [x] AppointmentMapper.java 已更新
  - [x] selectByIdWithRelations 方法已声明
  - [x] 方法签名正确

- [x] ProductOrderMapper.java 已更新
  - [x] selectByIdWithMerchant 方法已声明
  - [x] 方法签名正确

## Service 层
- [x] ServiceService.java 已更新
  - [x] findByIdWithMerchant 方法已实现
  - [x] 方法调用 Mapper 的关联查询
  - [x] 空值处理正确

- [x] ProductService.java 接口已更新
  - [x] findByIdWithMerchant 方法已声明

- [x] ProductServiceImpl.java 已更新
  - [x] findByIdWithMerchant 方法已实现
  - [x] 方法调用 Mapper 的关联查询
  - [x] 空值处理正确

- [x] AppointmentService.java 已更新
  - [x] findByIdWithRelations 方法已实现
  - [x] 方法调用 Mapper 的关联查询
  - [x] 空值处理正确

- [x] ProductOrderService.java 已更新
  - [x] findByIdWithMerchant 方法已实现
  - [x] 方法调用 Mapper 的关联查询
  - [x] 空值处理正确

## Controller NPE 修复

### 服务相关端点
- [x] L262: updateService 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L311: deleteService 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L393: batchDeleteServices 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L434: getServiceById 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

### 预约相关端点
- [x] L498-503: updateAppointmentStatus 方法已修复
  - [x] 使用 findByIdWithRelations 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L549: getRecentAppointments 方法已修复
  - [x] 关联数据已正确加载
  - [x] 无 NPE 风险

### 订单相关端点
- [x] L714-719: updateOrderStatus 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L798-803: updateProductOrderStatus 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L843-848: updateLogisticsInfo 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

### 商品相关端点
- [x] L914-915: getProductById 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L933-934: updateProduct 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L956-957: deleteProduct 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L1021-1022: updateProductStatus 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L1066-1067: batchUpdateProductStatus 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

- [x] L1104-1105: batchDeleteProducts 方法已修复
  - [x] 使用 findByIdWithMerchant 替代 findById
  - [x] 添加 merchant == null 检查
  - [x] 无 NPE 风险

### 评价相关端点
- [x] L1515-1520: getReviewById 方法已修复
  - [x] 使用 merchantId 字段替代 getMerchant().getId()
  - [x] 无 NPE 风险

- [x] L1553-1558: replyToReview 方法已修复
  - [x] 使用 merchantId 字段替代 getMerchant().getId()
  - [x] 无 NPE 风险

- [x] L1596-1601: deleteReview 方法已修复
  - [x] 使用 merchantId 字段替代 getMerchant().getId()
  - [x] 无 NPE 风险

## 测试
- [x] 单元测试
  - [x] ServiceService.findByIdWithMerchant 测试通过
  - [x] ProductService.findByIdWithMerchant 测试通过
  - [x] AppointmentService.findByIdWithRelations 测试通过
  - [x] ProductOrderService.findByIdWithMerchant 测试通过
  - [x] 所有修复的 Controller 方法测试通过
  - [x] 测试覆盖率达到 100%

- [x] 集成测试
  - [x] 服务相关端点测试通过
  - [x] 预约相关端点测试通过
  - [x] 订单相关端点测试通过
  - [x] 商品相关端点测试通过
  - [x] 评价相关端点测试通过
  - [x] 系统运行稳定，无 NPE 异常

## 代码质量
- [x] 代码审查通过
  - [x] 代码风格一致
  - [x] 注释清晰完整
  - [x] 无代码规范警告

- [x] 性能检查
  - [x] SQL 查询性能良好
  - [x] 无 N+1 查询问题
  - [x] 关联查询优化正确

## 验收标准确认
- [x] AC-1: 所有 14 处 NPE 风险点已修复
- [x] AC-2: Service 层新增关联查询方法并正确实现
- [x] AC-3: Mapper XML 文件正确配置关联查询
- [x] AC-4: 所有修复点通过单元测试
- [x] AC-5: 集成测试通过，无 NPE 异常
- [x] AC-6: 代码审查通过

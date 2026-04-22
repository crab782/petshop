# 测试文件修复 - 任务清单

## 任务 1：修复 Page 类型不兼容问题
- **优先级**：P0
- **依赖**：无
- **描述**：将所有测试文件中的 Spring Data Page 替换为 MyBatis-Plus Page
- **验收标准**：所有 Page 类型错误已修复
- **测试要求**：
  - 编译项目，确认 Page 类型错误已解决
- **涉及文件**：
  - `AdminApiControllerMerchantsTest.java`
  - `UserApiControllerReviewTest.java`
  - 其他使用 Page 的测试文件

## 任务 2：修复 DTO 构建器方法不存在问题
- **优先级**：P0
- **依赖**：无
- **描述**：修复 DTO 构建器方法不存在的问题
- **验收标准**：所有 DTO 构建器错误已修复
- **测试要求**：
  - 编译项目，确认 DTO 构建器错误已解决
- **涉及文件**：
  - `AuthApiControllerTest.java` (businessName, username 方法)

## 任务 3：修复服务层方法不存在问题
- **优先级**：P0
- **依赖**：无
- **描述**：修复服务层方法不存在的问题
- **验收标准**：所有服务层方法错误已修复
- **测试要求**：
  - 编译项目，确认服务层方法错误已解决
- **涉及文件**：
  - `AuthApiControllerTest.java` (registerMerchant 方法)
  - `MerchantApiControllerTest.java` (save 方法)

## 任务 4：修复方法参数不匹配问题
- **优先级**：P0
- **依赖**：无
- **描述**：修复方法参数不匹配的问题
- **验收标准**：所有参数不匹配错误已修复
- **测试要求**：
  - 编译项目，确认参数不匹配错误已解决
- **涉及文件**：
  - `AdminApiControllerActivitiesTest.java` (ActivityService 方法参数)
  - `UserApiControllerTest.java` (TestDataGenerator.generateMerchant 方法参数)

## 任务 5：修复类型转换错误
- **优先级**：P0
- **依赖**：无
- **描述**：修复类型转换错误
- **验收标准**：所有类型转换错误已修复
- **测试要求**：
  - 编译项目，确认类型转换错误已解决
- **涉及文件**：
  - `AuthApiControllerTest.java` (MerchantDTO 到 UserDTO 转换)

## 任务 6：修复其他错误
- **优先级**：P1
- **依赖**：任务 1-5
- **描述**：修复其他剩余的错误
- **验收标准**：所有错误已修复
- **测试要求**：
  - 编译项目，确认所有错误已解决
- **涉及文件**：
  - `TestDataFactory.java` (setMerchant 方法)
  - 其他有错误的测试文件

## 任务 7：验证所有测试通过
- **优先级**：P1
- **依赖**：任务 1-6
- **描述**：运行所有测试，确保通过
- **验收标准**：所有测试通过
- **测试要求**：
  - 运行 `mvn test`，确认所有测试通过

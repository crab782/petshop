# 测试文件修复 Spec

## Why
测试文件存在大量编译错误，主要包括：
1. Page 类型不兼容（Spring Data Page vs MyBatis-Plus Page）
2. 方法不存在错误（DTO 构建器方法、服务层方法）
3. 方法参数不匹配
4. 类型转换错误

这些错误导致 `mvn test` 命令失败，无法验证系统功能的正确性。

## What Changes
- 修复 Page 类型不兼容问题，统一使用 MyBatis-Plus Page
- 修复 DTO 构建器方法不存在问题
- 修复服务层方法不存在问题
- 修复方法参数不匹配问题
- 修复类型转换错误
- 确保所有测试用例通过

## Impact
- 受影响的功能：测试执行
- 受影响的代码：所有测试文件

## 错误分类与解决方案

### 1. Page 类型不兼容
**错误**：`org.springframework.data.domain.Page` 无法转换为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`

**解决方案**：
- 将测试代码中的 `org.springframework.data.domain.Page` 和 `PageImpl` 替换为 MyBatis-Plus 的对应类型
- 导入 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- 调整 Page 创建方式

### 2. 方法不存在错误
**错误**：各种 DTO 构建器方法和服务层方法不存在

**解决方案**：
- 检查实际的 DTO 类和服务层接口，使用正确的方法名
- 对于不存在的方法，需要创建或调整

### 3. 方法参数不匹配
**错误**：方法调用时参数数量或类型不匹配

**解决方案**：
- 检查服务层方法签名，使用正确的参数
- 调整测试代码以匹配实际方法签名

### 4. 类型转换错误
**错误**：`MerchantDTO` 无法转换为 `UserDTO`

**解决方案**：
- 检查类型转换逻辑，确保类型匹配
- 调整测试代码以使用正确的类型

## 技术实现

### 1. 修复 Page 类型问题
- 替换所有测试文件中的 `org.springframework.data.domain.Page` 为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- 替换 `PageImpl` 为 MyBatis-Plus 的分页实现

### 2. 修复 DTO 构建器问题
- 检查实际的 DTO 类，使用正确的构建器方法
- 对于不存在的方法，需要在 DTO 类中添加

### 3. 修复服务层方法问题
- 检查实际的服务层接口，使用正确的方法名和参数
- 对于不存在的方法，需要在服务层接口中添加

### 4. 修复方法参数问题
- 调整测试代码以匹配服务层方法的实际签名
- 确保参数类型和数量正确

### 5. 修复类型转换问题
- 确保类型转换逻辑正确
- 调整测试代码以使用正确的类型

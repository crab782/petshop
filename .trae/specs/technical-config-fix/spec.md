# 技术配置问题修复 - 产品需求文档

## Why
项目在构建、类型兼容性及测试框架方面存在三个关键技术问题：1) Lombok 注解处理器配置不正确导致 DTO 类无法生成 builder/getter 方法；2) AdminApiController 中存在 Page 类型不兼容错误；3) Mockito 测试框架未正确配置 inline mock maker 导致测试失败。这些问题严重影响项目的编译、运行和测试。

## What Changes
- **Lombok 配置修复**：检查并修复 IDE 和 Maven 的 Lombok 注解处理器配置
- **Page 类型兼容性修复**：定位并修复 AdminApiController 中的 Page 类型不兼容错误
- **Mockito 配置修复**：配置 Mockito inline mock maker 以支持 final 类和静态方法模拟

## Impact
- **Affected specs**: 
  - backend-comprehensive-review
  - hibernate-to-mybatis-migration
- **Affected code**:
  - `pom.xml` - Lombok 和 Mockito 依赖配置
  - `src/main/java/com/petshop/controller/api/AdminApiController.java` - Page 类型修复
  - `src/test/resources/mockito-extensions/` - Mockito 配置文件
  - 所有使用 Lombok 注解的 DTO 和实体类

## ADDED Requirements

### Requirement: Lombok 注解处理器配置
系统 SHALL 正确配置 Lombok 注解处理器：
- Maven pom.xml 中包含正确的 Lombok 依赖坐标及版本号
- pom.xml 中配置了正确的注解处理器路径
- 项目构建路径设置中已启用注解处理功能
- Lombok 注解能够被正确处理并生成相应代码

#### Scenario: Lombok 配置正确
- **WHEN** 执行 Maven 编译
- **THEN** 所有 Lombok 注解（@Data、@Builder 等）正确生成代码，无编译错误

### Requirement: Page 类型兼容性
系统 SHALL 确保 Page 类型兼容：
- AdminApiController 中所有 Page 类型使用正确
- 泛型参数声明一致
- 导入路径统一
- 类型转换符合项目规范

#### Scenario: Page 类型兼容
- **WHEN** 编译 AdminApiController
- **THEN** 无 Page 类型不兼容错误，编译成功

### Requirement: Mockito Inline Mock Maker 配置
系统 SHALL 正确配置 Mockito inline mock maker：
- 在测试资源目录下创建 mockito-extensions 配置文件
- 或在 Maven 中添加 mockito-inline 依赖
- 能够正确模拟 final 类、final 方法和静态方法

#### Scenario: Mockito 配置正确
- **WHEN** 运行 Mockito 测试
- **THEN** 所有测试用例正常执行，能够模拟 final 类和静态方法

## MODIFIED Requirements

### Requirement: 项目构建配置
**原要求**: 标准 Maven 项目配置
**修改后**: 添加 Lombok 注解处理器路径和 Mockito inline 配置

**原因**: 需要支持 Lombok 注解处理和 Mockito 高级模拟功能

## REMOVED Requirements

无

## 技术实现说明

### Lombok 配置步骤
1. 检查 pom.xml 中的 Lombok 依赖配置
2. 添加 maven-compiler-plugin 的 annotationProcessorPaths 配置
3. 确保 Lombok 版本与 Java 17 兼容
4. 验证注解处理器启用状态

### Page 类型修复步骤
1. 定位 AdminApiController 中的 Page 类型错误
2. 分析类型不匹配的具体原因
3. 统一导入路径或调整类型声明
4. 验证编译通过

### Mockito 配置步骤
1. 创建 `src/test/resources/mockito-extensions/` 目录
2. 创建 `org.mockito.plugins.MockMaker` 文件
3. 添加 `mock-maker-inline` 内容
4. 或在 pom.xml 中添加 mockito-inline 依赖

## 成功标准
1. ✅ Maven 编译成功，无 Lombok 相关错误
2. ✅ AdminApiController 编译成功，无 Page 类型错误
3. ✅ Mockito 测试正常运行，可模拟 final 类和静态方法
4. ✅ 所有单元测试通过

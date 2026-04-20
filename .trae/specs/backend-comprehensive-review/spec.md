# 后端项目综合审查与迁移 - 产品需求文档

## Why
当前后端项目需要完成三个关键任务：1) 完成 Hibernate 到 MyBatis-Plus 的迁移收尾工作；2) 移除已废弃的静态 HTML 页面托管配置，因为前端已分离为独立的 Vue 项目；3) 对所有 API 接口进行全面审计，确保与新的数据访问层兼容、符合 RESTful 设计原则、并与分离的 Vue 前端保持兼容。

## What Changes
- **完成 MyBatis-Plus 迁移收尾**：修复剩余编译错误，完成测试验证
- **移除静态资源托管**：删除 `src/main/resources/static` 目录配置及相关代码
- **API 接口全面审计**：验证所有 API 的正确性、RESTful 合规性、错误处理和前端兼容性
- **文档更新**：记录所有迁移步骤、API 变更和移除的资源

## Impact
- **Affected specs**: 
  - hibernate-to-mybatis-migration（继续完成）
  - 所有 API 相关 spec
- **Affected code**:
  - `src/main/resources/static/` - 待移除
  - `src/main/java/com/petshop/controller/` - API 审计
  - `src/main/java/com/petshop/service/` - 数据访问层验证
  - `src/main/java/com/petshop/mapper/` - Mapper 验证
  - 配置文件 - 静态资源配置移除

## ADDED Requirements

### Requirement: 静态资源移除
系统 SHALL 移除所有静态 HTML 页面托管配置：
- 删除 `src/main/resources/static/` 目录及其内容
- 移除 Spring MVC 中对静态资源的映射配置
- 验证无任何后端代码引用这些静态资源
- 更新相关配置文件

#### Scenario: 静态资源完全移除
- **WHEN** 删除静态资源目录和配置
- **THEN** 后端不再托管任何静态 HTML 页面，所有前端请求由独立的 Vue 项目处理

### Requirement: API 接口审计
系统 SHALL 对所有 API 接口进行全面审计，确保：
- 与 MyBatis-Plus 数据访问层兼容
- 遵循 RESTful 设计原则
- 包含正确的请求/响应处理
- 包含适当的错误处理和状态码
- 与分离的 Vue 前端保持兼容

#### Scenario: API 审计完成
- **WHEN** 完成所有 API 接口审计
- **THEN** 所有 API 接口符合规范，前后端通信正常

### Requirement: 编译错误修复
系统 SHALL 修复所有剩余的编译错误：
- DTO 类缺少的 builder/getter 方法
- Page 类型转换问题
- Controller 方法调用签名不匹配
- Mapper 方法缺失

#### Scenario: 项目编译成功
- **WHEN** 修复所有编译错误
- **THEN** `mvn compile` 成功，无错误

### Requirement: 迁移文档
系统 SHALL 记录完整的迁移过程：
- Hibernate 到 MyBatis-Plus 迁移步骤
- 移除的静态资源清单
- API 变更记录
- 测试验证结果

#### Scenario: 文档完整
- **WHEN** 完成迁移文档
- **THEN** 团队能够根据文档理解所有变更

## MODIFIED Requirements

### Requirement: 数据访问层迁移
**原要求**: 将 Hibernate 迁移到 MyBatis-Plus
**修改后**: 完成迁移收尾工作，修复剩余编译错误，确保所有功能正常

**原因**: 核心迁移已完成，需要完成收尾工作

## REMOVED Requirements

### Requirement: 静态 HTML 页面托管
**原因**: 前端已分离为独立的 Vue 项目，不再需要后端托管静态页面

**迁移**: 所有前端资源由独立的 Vue 项目提供

## 技术实现说明

### 静态资源移除步骤
1. 检查 `src/main/resources/static/` 目录内容
2. 搜索所有引用静态资源的代码
3. 移除 Spring MVC 静态资源映射配置
4. 删除静态资源目录
5. 验证应用启动正常

### API 审计范围
- 用户端 API（UserApiController）
- 商家端 API（MerchantApiController、MerchantController）
- 平台端 API（AdminApiController）
- 公共 API（AuthApiController、ProductController、ServiceController 等）

### 审计检查项
- [ ] HTTP 方法正确性（GET/POST/PUT/DELETE）
- [ ] URL 路径规范性
- [ ] 请求参数验证
- [ ] 响应格式一致性
- [ ] 错误处理完整性
- [ ] 状态码正确性
- [ ] 前端兼容性

## 成功标准
1. ✅ 项目编译成功，无错误
2. ✅ 静态资源完全移除
3. ✅ 所有 API 接口审计完成
4. ✅ 前后端通信正常
5. ✅ 迁移文档完整

# 前后端API接口对应性检查与自动化测试 Spec

## Why
项目经过多轮迭代后，前后端API接口存在不一致问题（如路径不匹配、参数缺失、响应格式不统一等），需要系统性比对并建立自动化测试体系，确保接口可靠性并防止回归问题。

## What Changes
- 扫描前端所有API调用，建立详细档案
- 扫描后端所有API定义，建立详细档案
- 执行前后端API精确比对，标记所有不匹配项
- 开发基于Spring Boot Test的自动化接口测试套件
- 生成API接口文档和比对报告

## Impact
- Affected specs: comprehensive-api-audit, user-api-audit, merchant-api-audit
- Affected code:
  - `petshop-vue/src/api/` (前端API调用)
  - `src/main/java/com/petshop/controller/` (后端控制器)
  - `src/test/java/com/petshop/` (新增测试代码)

## ADDED Requirements

### Requirement: 前端API调用档案
系统 SHALL 扫描前端项目中所有API调用并建立详细档案，包含接口路径、HTTP方法、请求参数、预期响应结构。

#### Scenario: 前端API扫描
- **WHEN** 扫描 `petshop-vue/src/api/` 下所有TypeScript文件
- **THEN** 提取所有API调用的完整信息并生成档案文档

### Requirement: 后端API定义档案
系统 SHALL 扫描后端项目中所有API定义并建立详细档案，包含接口路径、HTTP方法、参数验证规则、权限策略、响应结构。

#### Scenario: 后端API扫描
- **WHEN** 扫描 `src/main/java/com/petshop/controller/` 下所有Java控制器
- **THEN** 提取所有API定义的完整信息并生成档案文档

### Requirement: 前后端API比对
系统 SHALL 对前后端API进行精确比对，标记所有不匹配项并分类（路径不匹配、方法不匹配、参数不匹配、响应结构不匹配）。

#### Scenario: 比对发现不匹配
- **WHEN** 前端调用的API路径/参数/响应与后端定义不一致
- **THEN** 标记为不匹配项，记录原因分析

### Requirement: 自动化接口测试套件
系统 SHALL 基于Spring Boot Test开发自动化接口测试套件，覆盖所有API接口的正常场景和错误场景。

#### Scenario: 测试覆盖
- **WHEN** 运行自动化测试
- **THEN** 每个API接口至少覆盖正常业务场景、参数验证、错误处理

### Requirement: API接口文档
系统 SHALL 生成完整的API接口文档，包含所有接口的详细信息。

#### Scenario: 文档生成
- **WHEN** 完成API扫描和比对
- **THEN** 生成包含所有接口信息的Markdown文档

## MODIFIED Requirements
无

## REMOVED Requirements
无

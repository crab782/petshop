# 修复前端项目生产构建错误 Spec

## Why
前端项目在运行 `npm run build` 时出现 1364 个 TypeScript 类型检查错误，导致构建失败。虽然 `npm run dev` 能够正常运行，但生产构建对类型检查更严格，需要修复这些类型错误才能成功构建。

## What Changes
- 修复测试文件中的类型错误
- 修复组件中的 API 响应数据类型不匹配问题
- 修复属性不存在错误
- 优化 TypeScript 配置（如果需要）

## Impact
- 受影响的规范：前端项目构建系统
- 受影响的代码：
  - 所有 `__tests__` 目录下的测试文件
  - 多个 Vue 组件文件
  - API 响应处理逻辑

## ADDED Requirements

### Requirement: 修复测试文件类型错误
系统应修复所有测试文件中的 TypeScript 类型错误：

#### Scenario: 修复 MerchantTestData 类型
- **WHEN** 测试文件使用 `createMerchant` 函数
- **THEN** 应该只使用 `MerchantTestData` 类型中定义的属性

#### Scenario: 修复断言表达式
- **WHEN** 测试文件使用 `expect` 断言
- **THEN** 不应使用 `void` 类型进行真值测试

### Requirement: 修复组件数据类型错误
系统应修复所有组件中的数据类型不匹配问题：

#### Scenario: 修复 API 响应数据类型
- **WHEN** 组件接收 API 响应数据
- **THEN** 应正确处理响应拦截器返回的数据结构

#### Scenario: 修复属性访问错误
- **WHEN** 组件访问对象属性
- **THEN** 应确保属性存在于对象类型中

## MODIFIED Requirements

### Requirement: API 响应数据处理
修复多个组件中的 API 响应数据处理逻辑：

**问题**：
- `user-orders/index.vue`：`productOrders.value = res.data || []` 应改为 `productOrders.value = res || []`
- `user-pets/index.vue`：`allPets.value = res || []` 应正确处理响应数据
- `user-reviews/index.vue`：`appointments.value = res.data || res || []` 应简化为 `appointments.value = res || []`
- `UserLayout.vue`：`cartCount.value = data.length` 应改为 `cartCount.value = (data as any[]).length`

### Requirement: 测试文件修复
修复测试文件中的类型错误：

**问题**：
- `user-merchant/__tests__/index.spec.ts`：`serviceCount` 和 `isFavorited` 属性不存在
- `user-shop/__tests__/index.spec.ts`：spread 参数类型错误
- 多个测试文件：`void` 类型真值测试错误

## REMOVED Requirements
无移除的需求。

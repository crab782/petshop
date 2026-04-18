# 平台端页面测试代码开发 - 产品需求文档

## 概述
- **摘要**：为平台端所有20个页面开发全面的测试代码，包括单元测试、集成测试和端到端测试，确保覆盖页面功能、交互逻辑、边界条件和异常场景。
- **目的**：确保平台端页面功能的正确性、稳定性和可靠性，提高代码质量和可维护性。
- **目标用户**：前端开发人员、测试人员、质量保证团队

## 目标
- 为每个平台端页面开发完整的单元测试
- 开发关键业务流程的集成测试
- 开发端到端测试覆盖主要用户流程
- 确保测试覆盖率达到80%以上
- 所有测试通过并提交至版本控制系统

## 非目标（范围外）
- 不测试后端API的实际实现
- 不测试第三方依赖库的功能
- 不测试与平台端无关的页面

## 测试框架与工具
- **单元测试**：Vitest + Vue Test Utils
- **集成测试**：Vitest + Vue Test Utils
- **端到端测试**：Playwright
- **测试数据**：使用现有的Mock服务

## 平台端页面测试范围（20个页面）

| 序号 | 页面 | 核心功能 |
|------|------|----------|
| 1 | AdminLayout.vue | 导航菜单、用户信息、退出登录 |
| 2 | admin-dashboard/index.vue | 统计概览、数据展示 |
| 3 | admin-users/index.vue | 用户管理、CRUD操作 |
| 4 | user-detail/index.vue | 用户详情、关联数据 |
| 5 | admin-merchants/index.vue | 商家管理、CRUD操作 |
| 6 | merchant-detail/index.vue | 商家详情、关联数据 |
| 7 | merchant-audit/index.vue | 商家审核、状态管理 |
| 8 | admin-services/index.vue | 服务管理、CRUD操作 |
| 9 | admin-products/index.vue | 商品管理、CRUD操作 |
| 10 | product-manage/index.vue | 商品详情、编辑操作 |
| 11 | admin-reviews/index.vue | 评价管理、审核操作 |
| 12 | review-audit/index.vue | 评价审核、状态管理 |
| 13 | admin-announcements/index.vue | 公告管理、CRUD操作 |
| 14 | announcement-edit/index.vue | 公告编辑、发布操作 |
| 15 | admin-system/index.vue | 系统设置、配置管理 |
| 16 | roles/index.vue | 角色管理、权限分配 |
| 17 | logs/index.vue | 操作日志、搜索筛选 |
| 18 | admin-activities/index.vue | 活动管理、CRUD操作 |
| 19 | admin-tasks/index.vue | 任务管理、执行操作 |
| 20 | shop-audit/index.vue | 店铺审核、状态管理 |

## 测试策略

### 1. 单元测试
- **组件测试**：测试组件渲染、props传递、事件触发
- **逻辑测试**：测试计算属性、方法逻辑、状态管理
- **Mock API**：使用现有的Mock服务模拟API响应
- **测试覆盖率**：每个页面至少80%的代码覆盖率

### 2. 集成测试
- **页面集成**：测试页面与子组件的交互
- **路由集成**：测试页面间的导航
- **状态集成**：测试Pinia store与页面的交互
- **表单集成**：测试表单提交和验证

### 3. 端到端测试
- **用户流程**：测试完整的用户操作流程
- **数据流程**：测试从API获取数据到页面渲染的完整流程
- **异常场景**：测试错误处理和边界情况
- **响应式测试**：测试不同屏幕尺寸下的页面表现

## 测试文件结构

```
src/
├── views/
│   └── admin/
│       ├── admin-dashboard/
│       │   ├── index.vue
│       │   └── __tests__/
│       │       ├── index.spec.ts       # 单元测试
│       │       └── index.e2e.ts       # E2E测试
│       ├── admin-users/
│       │   ├── index.vue
│       │   └── __tests__/
│       │       └── index.spec.ts
│       └── ... (其他页面)
├── components/
│   └── __tests__/                     # 组件测试
├── stores/
│   └── __tests__/                     # 状态管理测试
└── api/
    └── __tests__/                     # API测试

e2e/
├── admin/                             # 端到端测试
│   ├── dashboard.spec.ts
│   ├── users.spec.ts
│   └── ... (其他页面)
└── playwright.config.ts
```

## 功能测试要点

### 1. AdminLayout.vue
- 导航菜单渲染和点击
- 用户信息显示
- 退出登录功能
- 响应式布局测试

### 2. admin-dashboard/index.vue
- 统计卡片数据渲染
- 最近用户列表渲染
- 待审核商家列表渲染
- 系统公告渲染

### 3. admin-users/index.vue
- 用户表格渲染
- 搜索和筛选功能
- 分页功能
- 批量操作功能
- 单个用户操作（查看、编辑、删除）

### 4. 其他管理页面
- 每个页面的核心功能测试
- 表单提交和验证
- 数据加载和错误处理
- 权限控制测试

## 验收标准

### AC-1: 测试代码完整性
- **Given**：所有平台端页面
- **When**：查看测试文件
- **Then**：每个页面都有对应的测试文件
- **Verification**：`programmatic` - 文件存在性检查

### AC-2: 测试覆盖率
- **Given**：平台端页面代码
- **When**：运行测试覆盖率报告
- **Then**：代码覆盖率达到80%以上
- **Verification**：`programmatic` - 覆盖率报告

### AC-3: 测试通过
- **Given**：测试环境
- **When**：运行所有测试
- **Then**：所有测试通过，无失败
- **Verification**：`programmatic` - 测试执行结果

### AC-4: 测试代码质量
- **Given**：测试代码
- **When**：代码审查
- **Then**：测试代码结构清晰，断言准确，测试用例描述完整
- **Verification**：`human-judgment` - 代码审查

## 约束
- **测试环境**：Node.js 20.19.0或更高版本
- **测试框架**：Vitest 4.1.2, Playwright 1.58.2
- **Mock服务**：使用现有的src/mock/目录下的Mock服务
- **测试数据**：使用Mock数据，不依赖真实后端

## 假设
- 项目的Mock服务已正确配置并运行
- 测试环境已正确设置
- 页面组件结构和API调用方式保持稳定
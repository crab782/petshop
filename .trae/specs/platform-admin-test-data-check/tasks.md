# 平台端页面测试数据检查 - 任务清单

## 20个平台端页面列表

1. admin-activities/index.vue
2. admin-announcements/index.vue
3. admin-dashboard/index.vue
4. admin-merchants/index.vue
5. admin-products/index.vue
6. admin-reviews/index.vue
7. admin-services/index.vue
8. admin-system/index.vue
9. admin-tasks/index.vue
10. admin-users/index.vue
11. announcement-edit/index.vue
12. merchant-audit/index.vue
13. merchant-detail/index.vue
14. product-manage/index.vue
15. review-audit/index.vue
16. roles/index.vue
17. shop-audit/index.vue
18. user-detail/index.vue
19. AdminLayout.vue
20. index.vue (根页面)

## 检查任务

## [ ] 任务1：检查admin-activities页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-activities/index.vue`
  - 验证是否包含活动列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-1.1: 页面是否显示活动列表数据
  - `programmatic` TR-1.2: 是否使用模拟API调用
- **Notes**: 检查是否有新增、编辑、删除活动的测试数据

## [ ] 任务2：检查admin-announcements页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-announcements/index.vue`
  - 验证是否包含公告列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-2.1: 页面是否显示公告列表数据
  - `programmatic` TR-2.2: 是否使用模拟API调用
- **Notes**: 检查是否有发布、下架功能的测试数据

## [ ] 任务3：检查admin-dashboard页面
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-dashboard/index.vue`
  - 验证是否包含统计数据、用户列表、商家列表、公告等测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-3.1: 页面是否显示完整的测试数据
  - `programmatic` TR-3.2: 是否使用模拟API调用
- **Notes**: 这是核心页面，需要特别关注测试数据的完整性

## [ ] 任务4：检查admin-merchants页面
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-merchants/index.vue`
  - 验证是否包含商家列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-4.1: 页面是否显示商家列表数据
  - `programmatic` TR-4.2: 是否使用模拟API调用
- **Notes**: 检查是否有批量操作、状态筛选的测试数据

## [ ] 任务5：检查admin-products页面
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-products/index.vue`
  - 验证是否包含商品列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-5.1: 页面是否显示商品列表数据
  - `programmatic` TR-5.2: 是否使用模拟API调用
- **Notes**: 检查是否有库存状态、价格筛选的测试数据

## [ ] 任务6：检查admin-reviews页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-reviews/index.vue`
  - 验证是否包含评价列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-6.1: 页面是否显示评价列表数据
  - `programmatic` TR-6.2: 是否使用模拟API调用
- **Notes**: 检查是否有评分筛选、审核功能的测试数据

## [ ] 任务7：检查admin-services页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-services/index.vue`
  - 验证是否包含服务列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-7.1: 页面是否显示服务列表数据
  - `programmatic` TR-7.2: 是否使用模拟API调用
- **Notes**: 检查是否有价格、时长筛选的测试数据

## [ ] 任务8：检查admin-system页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-system/index.vue`
  - 验证是否包含系统设置测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-8.1: 页面是否显示系统设置数据
  - `programmatic` TR-8.2: 是否使用模拟API调用
- **Notes**: 检查是否有配置项的测试数据

## [ ] 任务9：检查admin-tasks页面
- **Priority**: P2
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-tasks/index.vue`
  - 验证是否包含任务列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-9.1: 页面是否显示任务列表数据
  - `programmatic` TR-9.2: 是否使用模拟API调用
- **Notes**: 检查是否有任务执行、状态管理的测试数据

## [ ] 任务10：检查admin-users页面
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/admin-users/index.vue`
  - 验证是否包含用户列表测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-10.1: 页面是否显示用户列表数据
  - `programmatic` TR-10.2: 是否使用模拟API调用
- **Notes**: 检查是否有用户状态、批量操作的测试数据

## [ ] 任务11：检查announcement-edit页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/announcement-edit/index.vue`
  - 验证是否包含公告编辑测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-11.1: 页面是否显示公告编辑表单数据
  - `programmatic` TR-11.2: 是否使用模拟API调用
- **Notes**: 检查是否有新建和编辑模式的测试数据

## [ ] 任务12：检查merchant-audit页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/merchant-audit/index.vue`
  - 验证是否包含待审核商家测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-12.1: 页面是否显示待审核商家列表
  - `programmatic` TR-12.2: 是否使用模拟API调用
- **Notes**: 检查是否有审核操作的测试数据

## [ ] 任务13：检查merchant-detail页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/merchant-detail/index.vue`
  - 验证是否包含商家详情测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-13.1: 页面是否显示商家详情数据
  - `programmatic` TR-13.2: 是否使用模拟API调用
- **Notes**: 检查是否有商家服务、商品、订单的测试数据

## [ ] 任务14：检查product-manage页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/product-manage/index.vue`
  - 验证是否包含商品管理测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-14.1: 页面是否显示商品管理数据
  - `programmatic` TR-14.2: 是否使用模拟API调用
- **Notes**: 检查是否有商品编辑、库存管理的测试数据

## [ ] 任务15：检查review-audit页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/review-audit/index.vue`
  - 验证是否包含待审核评价测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-15.1: 页面是否显示待审核评价列表
  - `programmatic` TR-15.2: 是否使用模拟API调用
- **Notes**: 检查是否有评价审核操作的测试数据

## [ ] 任务16：检查roles页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/roles/index.vue`
  - 验证是否包含角色权限测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-16.1: 页面是否显示角色列表数据
  - `programmatic` TR-16.2: 是否使用模拟API调用
- **Notes**: 检查是否有权限分配的测试数据

## [ ] 任务17：检查shop-audit页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/shop-audit/index.vue`
  - 验证是否包含店铺审核测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-17.1: 页面是否显示待审核店铺列表
  - `programmatic` TR-17.2: 是否使用模拟API调用
- **Notes**: 检查是否有店铺审核操作的测试数据

## [ ] 任务18：检查user-detail页面
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/user-detail/index.vue`
  - 验证是否包含用户详情测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-18.1: 页面是否显示用户详情数据
  - `programmatic` TR-18.2: 是否使用模拟API调用
- **Notes**: 检查是否有用户订单、宠物、评价的测试数据

## [ ] 任务19：检查AdminLayout页面
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/AdminLayout.vue`
  - 验证是否包含布局相关的测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-19.1: 布局是否显示正确
  - `programmatic` TR-19.2: 是否使用模拟API调用
- **Notes**: 检查菜单、导航栏的显示

## [ ] 任务20：检查根页面
- **Priority**: P2
- **Depends On**: None
- **Description**:
  - 检查 `src/views/admin/index.vue`（如果存在）
  - 验证是否包含测试数据
  - 检查是否使用模拟后端
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgement` TR-20.1: 页面是否显示测试数据
  - `programmatic` TR-20.2: 是否使用模拟API调用
- **Notes**: 检查根页面的内容和导航

## [ ] 任务21：生成检查报告
- **Priority**: P0
- **Depends On**: Task 1-20
- **Description**:
  - 汇总所有页面的检查结果
  - 生成详细的检查报告
  - 提出改进建议（如果需要）
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `human-judgement` TR-21.1: 检查报告是否完整准确
- **Notes**: 报告应包含每个页面的测试数据状态和使用的模拟后端情况

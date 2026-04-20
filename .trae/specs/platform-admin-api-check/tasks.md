# 平台端页面API检查与调整 - 任务计划

## [ ] 任务1: 审查admin-dashboard页面API调用
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 审查admin-dashboard页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-1.1: 验证仪表盘统计数据API调用正常
  - `programmatic` TR-1.2: 验证最近注册用户API调用正常
  - `programmatic` TR-1.3: 验证待审核商家API调用正常
  - `programmatic` TR-1.4: 验证公告列表API调用正常
- **Notes**: 重点检查 `/api/admin/dashboard` 相关接口

## [ ] 任务2: 审查admin-users页面API调用
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 审查admin-users页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-2.1: 验证用户列表API调用正常
  - `programmatic` TR-2.2: 验证用户删除API调用正常
  - `programmatic` TR-2.3: 验证批量操作API调用正常
- **Notes**: 重点检查 `/api/admin/users` 相关接口

## [ ] 任务3: 审查user-detail页面API调用
- **Priority**: P0
- **Depends On**: 任务2
- **Description**:
  - 审查user-detail页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-3.1: 验证用户详情API调用正常
  - `programmatic` TR-3.2: 验证用户状态更新API调用正常
- **Notes**: 重点检查 `/api/admin/users/{id}` 相关接口

## [ ] 任务4: 审查admin-merchants页面API调用
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 审查admin-merchants页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-4.1: 验证商家列表API调用正常
  - `programmatic` TR-4.2: 验证商家删除API调用正常
  - `programmatic` TR-4.3: 验证批量操作API调用正常
- **Notes**: 重点检查 `/api/admin/merchants` 相关接口

## [ ] 任务5: 审查merchant-detail页面API调用
- **Priority**: P0
- **Depends On**: 任务4
- **Description**:
  - 审查merchant-detail页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-5.1: 验证商家详情API调用正常
- **Notes**: 重点检查 `/api/admin/merchants/{id}` 相关接口

## [ ] 任务6: 审查merchant-audit页面API调用
- **Priority**: P0
- **Depends On**: 任务4
- **Description**:
  - 审查merchant-audit页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-6.1: 验证待审核商家列表API调用正常
  - `programmatic` TR-6.2: 验证商家审核API调用正常
- **Notes**: 重点检查 `/api/admin/merchants/pending` 和 `/api/admin/merchants/{id}/audit` 相关接口

## [ ] 任务7: 审查admin-services页面API调用
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 审查admin-services页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-7.1: 验证服务列表API调用正常
  - `programmatic` TR-7.2: 验证服务操作API调用正常
- **Notes**: 检查是否存在 `/api/admin/services` 相关接口

## [ ] 任务8: 审查admin-products页面API调用
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 审查admin-products页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-8.1: 验证商品列表API调用正常
  - `programmatic` TR-8.2: 验证商品删除API调用正常
  - `programmatic` TR-8.3: 验证批量操作API调用正常
- **Notes**: 重点检查 `/api/admin/products` 相关接口

## [ ] 任务9: 审查product-manage页面API调用
- **Priority**: P0
- **Depends On**: 任务8
- **Description**:
  - 审查product-manage页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-9.1: 验证商品详情API调用正常
  - `programmatic` TR-9.2: 验证商品更新API调用正常
- **Notes**: 重点检查 `/api/admin/products/{id}` 相关接口

## [ ] 任务10: 审查admin-announcements页面API调用
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 审查admin-announcements页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-10.1: 验证公告列表API调用正常
  - `programmatic` TR-10.2: 验证公告操作API调用正常
- **Notes**: 检查是否存在 `/api/admin/announcements` 相关接口

## [ ] 任务11: 审查announcement-edit页面API调用
- **Priority**: P1
- **Depends On**: 任务10
- **Description**:
  - 审查announcement-edit页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-11.1: 验证公告创建API调用正常
  - `programmatic` TR-11.2: 验证公告更新API调用正常
- **Notes**: 检查是否存在 `/api/admin/announcements` 相关接口

## [ ] 任务12: 审查admin-system页面API调用
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 审查admin-system页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-12.1: 验证系统配置API调用正常
- **Notes**: 重点检查 `/api/admin/system/config` 相关接口

## [ ] 任务13: 审查roles页面API调用
- **Priority**: P1
- **Depends On**: None
- **Description**:
  - 审查roles页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-13.1: 验证角色列表API调用正常
  - `programmatic` TR-13.2: 验证权限列表API调用正常
- **Notes**: 检查是否存在 `/api/admin/roles` 和 `/api/admin/permissions` 相关接口

## [ ] 任务14: 审查admin-reviews页面API调用
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 审查admin-reviews页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-14.1: 验证评价列表API调用正常
  - `programmatic` TR-14.2: 验证评价删除API调用正常
  - `programmatic` TR-14.3: 验证批量操作API调用正常
- **Notes**: 重点检查 `/api/admin/reviews` 相关接口

## [ ] 任务15: 审查review-audit页面API调用
- **Priority**: P0
- **Depends On**: 任务14
- **Description**:
  - 审查review-audit页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-15.1: 验证待审核评价列表API调用正常
  - `programmatic` TR-15.2: 验证评价审核API调用正常
- **Notes**: 重点检查 `/api/admin/reviews/pending` 和 `/api/admin/reviews/{id}/audit` 相关接口

## [ ] 任务16: 审查shop-audit页面API调用
- **Priority**: P1
- **Depends On**: 任务4
- **Description**:
  - 审查shop-audit页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-16.1: 验证待审核店铺列表API调用正常
  - `programmatic` TR-16.2: 验证店铺审核API调用正常
- **Notes**: 检查是否存在 `/api/admin/shops/pending` 和 `/api/admin/shops/{id}/audit` 相关接口

## [ ] 任务17: 审查admin-activities页面API调用
- **Priority**: P2
- **Depends On**: None
- **Description**:
  - 审查admin-activities页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-17.1: 验证活动列表API调用正常
- **Notes**: 检查是否存在 `/api/admin/activities` 相关接口

## [ ] 任务18: 审查admin-tasks页面API调用
- **Priority**: P2
- **Depends On**: None
- **Description**:
  - 审查admin-tasks页面的所有功能模块
  - 识别需要API调用的功能点
  - 验证是否存在对应的真实后端API接口
  - 修改前端代码以使用真实后端API
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-18.1: 验证任务列表API调用正常
- **Notes**: 检查是否存在 `/api/admin/tasks` 相关接口

## [ ] 任务19: 测试所有API调用
- **Priority**: P0
- **Depends On**: 任务1-18
- **Description**:
  - 测试所有修改后的API调用
  - 确保功能完整性和用户体验
  - 处理发现的问题和错误
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-19.1: 所有API调用正常工作
  - `human-judgment` TR-19.2: 用户体验不受影响
- **Notes**: 重点测试错误处理和边界情况

## [ ] 任务20: 生成详细报告
- **Priority**: P0
- **Depends On**: 任务1-19
- **Description**:
  - 生成详细报告，总结平台端20个页面所使用的全部真实后端API信息
  - 包括API端点URL、请求方法、主要功能描述及对应页面模块
  - 记录无法找到对应真实API的功能模块
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `human-judgment` TR-20.1: 报告内容完整准确
  - `human-judgment` TR-20.2: 报告格式清晰易读
- **Notes**: 确保报告包含所有必要的API信息
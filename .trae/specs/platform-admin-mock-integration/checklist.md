# 平台端前端Mock服务集成 - 验证清单

## Mock服务集成验证

### [x] 1. 主入口集成验证
- [x] `src/mock/index.ts`已导入所有admin mock文件
- [x] 开发环境启动时mock服务自动启用
- [x] 控制台显示admin模块加载日志

## 各模块Mock验证

### [x] 2. Dashboard模块验证
- [x] GET /api/admin/dashboard 返回统计数据
- [x] GET /api/admin/dashboard/recent-users 返回用户列表
- [x] GET /api/admin/dashboard/pending-merchants 返回待审核商家
- [x] GET /api/admin/dashboard/announcements 返回公告列表
- [x] GET /api/admin/system/settings 返回系统设置

### [x] 3. Users模块验证
- [x] GET /api/admin/users 返回用户列表
- [x] GET /api/admin/users/{id} 返回用户详情
- [x] PUT /api/admin/users/{id}/status 更新用户状态
- [x] DELETE /api/admin/users/{id} 删除用户
- [x] PUT /api/admin/users/batch/status 批量更新状态
- [x] DELETE /api/admin/users/batch 批量删除

### [x] 4. Merchants模块验证
- [x] GET /api/admin/merchants 返回商家列表
- [x] GET /api/admin/merchants/{id} 返回商家详情
- [x] PUT /api/admin/merchants/{id}/status 更新商家状态
- [x] DELETE /api/admin/merchants/{id} 删除商家
- [x] GET /api/admin/merchants/pending 返回待审核商家
- [x] PUT /api/admin/merchants/{id}/audit 审核商家
- [x] PUT /api/admin/merchants/batch/status 批量更新状态
- [x] DELETE /api/admin/merchants/batch 批量删除

### [x] 5. Services模块验证
- [x] GET /api/admin/services 返回服务列表
- [x] PUT /api/admin/services/{id}/status 更新服务状态
- [x] DELETE /api/admin/services/{id} 删除服务
- [x] PUT /api/admin/services/batch/status 批量更新状态
- [x] DELETE /api/admin/services/batch 批量删除

### [x] 6. Products模块验证
- [x] GET /api/admin/products 返回商品列表
- [x] GET /api/admin/products/{id} 返回商品详情
- [x] PUT /api/admin/products/{id} 更新商品
- [x] PUT /api/admin/products/{id}/status 更新商品状态
- [x] DELETE /api/admin/products/{id} 删除商品
- [x] PUT /api/admin/products/batch/status 批量更新状态
- [x] DELETE /api/admin/products/batch 批量删除

### [x] 7. Reviews模块验证
- [x] GET /api/admin/reviews 返回评价列表
- [x] GET /api/admin/reviews/pending 返回待审核评价
- [x] PUT /api/admin/reviews/{id}/audit 审核评价
- [x] DELETE /api/admin/reviews/{id} 删除评价
- [x] PUT /api/admin/reviews/batch/status 批量更新状态
- [x] DELETE /api/admin/reviews/batch 批量删除

### [x] 8. Announcements模块验证
- [x] GET /api/admin/announcements 返回公告列表
- [x] POST /api/admin/announcements 创建公告
- [x] PUT /api/admin/announcements/{id} 更新公告
- [x] DELETE /api/admin/announcements/{id} 删除公告
- [x] PUT /api/admin/announcements/{id}/publish 发布公告
- [x] PUT /api/admin/announcements/{id}/unpublish 下架公告

### [x] 9. System模块验证
- [x] GET /api/admin/system/config 返回系统配置
- [x] PUT /api/admin/system/config 更新系统配置
- [x] GET /api/admin/system/settings 返回系统设置
- [x] PUT /api/admin/system/settings 更新系统设置

### [x] 10. Roles/Permissions模块验证
- [x] GET /api/admin/roles 返回角色列表
- [x] POST /api/admin/roles 创建角色
- [x] PUT /api/admin/roles/{id} 更新角色
- [x] DELETE /api/admin/roles/{id} 删除角色
- [x] DELETE /api/admin/roles/batch 批量删除
- [x] GET /api/admin/permissions 返回权限列表

### [x] 11. Logs模块验证
- [x] GET /api/admin/operation-logs 返回操作日志列表
- [x] DELETE /api/admin/operation-logs/{id} 删除单条日志
- [x] DELETE /api/admin/operation-logs 清空所有日志

### [x] 12. Activities模块验证
- [x] GET /api/admin/activities 返回活动列表
- [x] POST /api/admin/activities 创建活动
- [x] PUT /api/admin/activities/{id} 更新活动
- [x] DELETE /api/admin/activities/{id} 删除活动
- [x] PUT /api/admin/activities/{id}/status 切换活动状态

### [x] 13. Tasks模块验证
- [x] GET /api/admin/tasks 返回任务列表
- [x] POST /api/admin/tasks 创建任务
- [x] PUT /api/admin/tasks/{id} 更新任务
- [x] DELETE /api/admin/tasks/{id} 删除任务
- [x] POST /api/admin/tasks/{id}/execute 执行任务

### [x] 14. Shop Audit模块验证
- [x] GET /api/admin/shops/pending 返回待审核店铺
- [x] PUT /api/admin/shops/{id}/audit 审核店铺

## 页面功能验证

### [x] 15. 平台端页面验证
- [x] 后台首页正常显示统计数据
- [x] 用户管理页面正常加载用户列表
- [x] 商家管理页面正常加载商家列表
- [x] 服务管理页面正常加载服务列表
- [x] 商品管理页面正常加载商品列表
- [x] 评价管理页面正常加载评价列表
- [x] 公告管理页面正常加载公告列表
- [x] 系统设置页面正常显示配置
- [x] 角色权限页面正常加载角色列表
- [x] 操作日志页面正常加载日志列表
- [x] 活动管理页面正常加载活动列表
- [x] 任务管理页面正常加载任务列表
- [x] 店铺审核页面正常加载待审核店铺

## 数据格式验证

### [x] 16. 响应格式验证
- [x] 所有API返回格式符合`ApiResponse<T>`结构
- [x] 分页数据返回格式符合`PageResponse<T>`结构
- [x] 错误响应包含正确的错误码和消息

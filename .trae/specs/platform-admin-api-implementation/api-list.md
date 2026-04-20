# 平台端20个页面使用的真实后端API列表

## 1. 仪表盘相关API
- `GET /api/admin/dashboard` - 获取仪表盘数据

## 2. 用户管理相关API
- `GET /api/admin/users` - 获取用户列表
- `GET /api/admin/users/{id}` - 获取用户详情
- `PUT /api/admin/users/{id}/status` - 更新用户状态
- `DELETE /api/admin/users/{id}` - 删除用户
- `PUT /api/admin/users/batch/status` - 批量更新用户状态
- `DELETE /api/admin/users/batch` - 批量删除用户

## 3. 商家管理相关API
- `GET /api/admin/merchants` - 获取商家列表
- `GET /api/admin/merchants/pending` - 获取待审核商家
- `GET /api/admin/merchants/{id}` - 获取商家详情
- `PUT /api/admin/merchants/{id}/status` - 更新商家状态
- `PUT /api/admin/merchants/{id}/audit` - 审核商家
- `DELETE /api/admin/merchants/{id}` - 删除商家

## 4. 服务管理相关API
- `GET /api/admin/services` - 获取服务列表
- `PUT /api/admin/services/{id}/status` - 更新服务状态
- `DELETE /api/admin/services/{id}` - 删除服务
- `PUT /api/admin/services/batch/status` - 批量更新服务状态
- `DELETE /api/admin/services/batch` - 批量删除服务

## 5. 商品管理相关API
- `GET /api/admin/products` - 获取商品列表
- `PUT /api/admin/products/{id}/status` - 更新商品状态
- `DELETE /api/admin/products/{id}` - 删除商品
- `PUT /api/admin/products/batch/status` - 批量更新商品状态
- `DELETE /api/admin/products/batch` - 批量删除商品

## 6. 评价管理相关API
- `GET /api/admin/reviews` - 获取评价列表
- `GET /api/admin/reviews/audit` - 获取待审核评价
- `DELETE /api/admin/reviews/{id}` - 删除评价
- `PUT /api/admin/reviews/{id}/approve` - 批准评价
- `PUT /api/admin/reviews/{id}/violation` - 标记评价违规

## 7. 系统设置相关API
- `GET /api/admin/system/config` - 获取系统配置
- `PUT /api/admin/system/config` - 更新系统配置

## 8. 店铺审核相关API
- `GET /api/admin/shops/pending` - 获取待审核店铺
- `PUT /api/admin/shops/{id}/audit` - 审核店铺

## 9. 操作日志相关API
- `GET /api/admin/operation-logs` - 获取操作日志
- `DELETE /api/admin/operation-logs/{id}` - 删除操作日志
- `DELETE /api/admin/operation-logs` - 清空操作日志

## 10. 角色管理相关API
- `GET /api/admin/roles` - 获取角色列表
- `POST /api/admin/roles` - 添加角色
- `PUT /api/admin/roles/{id}` - 更新角色
- `DELETE /api/admin/roles/{id}` - 删除角色

## 11. 权限管理相关API
- `GET /api/admin/permissions` - 获取权限列表

## 12. 活动管理相关API
- `GET /api/admin/activities` - 获取活动列表
- `POST /api/admin/activities` - 添加活动
- `PUT /api/admin/activities/{id}` - 更新活动
- `DELETE /api/admin/activities/{id}` - 删除活动
- `PUT /api/admin/activities/{id}/status` - 切换活动状态

## 13. 任务管理相关API
- `GET /api/admin/tasks` - 获取任务列表
- `POST /api/admin/tasks` - 添加任务
- `PUT /api/admin/tasks/{id}` - 更新任务
- `DELETE /api/admin/tasks/{id}` - 删除任务
- `POST /api/admin/tasks/{id}/execute` - 执行任务
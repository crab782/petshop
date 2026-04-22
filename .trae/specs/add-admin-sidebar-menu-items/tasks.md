# Tasks

- [x] Task 1: 在 AdminLayout.vue 中导入所需的图标组件
  - [x] SubTask 1.1: 导入 Audit, Role, Document, Activity, Task 等图标
- [x] Task 2: 在左侧边栏菜单中添加新的菜单项
  - [x] SubTask 2.1: 添加商家审核菜单项（/admin/merchants/audit）
  - [x] SubTask 2.2: 添加角色管理菜单项（/admin/system/roles）
  - [x] SubTask 2.3: 添加操作日志菜单项（/admin/system/logs）
  - [x] SubTask 2.4: 添加评价审核菜单项（/admin/reviews/audit）
  - [x] SubTask 2.5: 添加店铺审核菜单项（/admin/shop/audit）
  - [x] SubTask 2.6: 添加活动管理菜单项（/admin/activities）
  - [x] SubTask 2.7: 添加任务管理菜单项（/admin/tasks）
- [x] Task 3: 优化页面加载逻辑
  - [x] SubTask 3.1: 修改 API 拦截器，移除401错误的自动跳转逻辑
- [x] Task 4: 验证所有菜单项能正确跳转

# Task Dependencies
- Task 2 依赖于 Task 1
- Task 4 依赖于 Task 2 和 Task 3

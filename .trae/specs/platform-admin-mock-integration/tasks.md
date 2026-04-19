# 平台端前端Mock服务集成 - 任务清单

## 任务列表

### [x] 任务1：集成Admin Mock服务到主入口
- **优先级**：P0
- **依赖**：无
- **描述**：
  - 修改`src/mock/index.ts`，导入admin目录下的所有mock文件
  - 确保mock服务在开发环境自动启用
  - 添加admin模块到日志输出

### [x] 任务2：检查并完善Dashboard Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/dashboard.js`是否覆盖所有仪表盘API
  - 确保返回数据格式与`ApiResponse<T>`一致
  - 添加缺失的API mock

### [x] 任务3：检查并完善Users Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/users.js`是否覆盖所有用户管理API
  - 添加批量操作mock接口
  - 添加用户详情mock接口

### [x] 任务4：检查并完善Merchants Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/merchants.js`是否覆盖所有商家管理API
  - 添加商家详情mock接口
  - 添加商家审核mock接口

### [x] 任务5：检查并完善Services Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/services.js`是否覆盖所有服务管理API
  - 添加批量操作mock接口

### [x] 任务6：检查并完善Products Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/products.js`是否覆盖所有商品管理API
  - 添加商品详情mock接口
  - 添加批量操作mock接口

### [x] 任务7：检查并完善Reviews Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/reviews.js`是否覆盖所有评价管理API
  - 添加评价审核mock接口

### [x] 任务8：检查并完善Announcements Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/announcements.js`是否覆盖所有公告管理API
  - 添加公告CRUD mock接口

### [x] 任务9：检查并完善System Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查系统设置相关mock是否完整
  - 添加系统配置mock接口

### [x] 任务10：检查并完善Roles/Permissions Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/roles.js`是否覆盖所有角色权限API
  - 添加权限列表mock接口

### [x] 任务11：检查并完善Logs Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/logs.js`是否覆盖所有操作日志API

### [x] 任务12：检查并完善Activities Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/activities.js`是否覆盖所有活动管理API

### [x] 任务13：检查并完善Tasks Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/tasks.js`是否覆盖所有任务管理API

### [x] 任务14：检查并完善Shop Audit Mock
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 检查`src/mock/admin/shop-audit.js`是否覆盖所有店铺审核API

### [x] 任务15：验证Mock服务集成
- **优先级**：P0
- **依赖**：任务1-14
- **描述**：
  - 启动前端开发服务器
  - 验证所有平台端页面能正常使用mock数据
  - 检查控制台无API错误

## 任务依赖关系

```
任务1 (集成入口)
  ├── 任务2-14 (各模块Mock完善，可并行)
  └── 任务15 (验证集成，依赖所有前置任务)
```

## API接口清单

根据`src/api/admin.ts`，需要mock的API接口：

| 模块 | API路径 | 方法 |
|------|---------|------|
| Dashboard | /api/admin/dashboard | GET |
| Users | /api/admin/users | GET |
| Users | /api/admin/users/{id} | GET |
| Users | /api/admin/users/{id}/status | PUT |
| Users | /api/admin/users/{id} | DELETE |
| Users | /api/admin/users/batch/status | PUT |
| Users | /api/admin/users/batch | DELETE |
| Merchants | /api/admin/merchants | GET |
| Merchants | /api/admin/merchants/{id} | GET |
| Merchants | /api/admin/merchants/{id}/status | PUT |
| Merchants | /api/admin/merchants/{id} | DELETE |
| Merchants | /api/admin/merchants/pending | GET |
| Merchants | /api/admin/merchants/{id}/audit | PUT |
| Merchants | /api/admin/merchants/batch/status | PUT |
| Merchants | /api/admin/merchants/batch | DELETE |
| Services | /api/admin/services | GET |
| Services | /api/admin/services/{id}/status | PUT |
| Services | /api/admin/services/{id} | DELETE |
| Services | /api/admin/services/batch/status | PUT |
| Services | /api/admin/services/batch | DELETE |
| Products | /api/admin/products | GET |
| Products | /api/admin/products/{id} | GET |
| Products | /api/admin/products/{id} | PUT |
| Products | /api/admin/products/{id}/status | PUT |
| Products | /api/admin/products/{id} | DELETE |
| Products | /api/admin/products/batch/status | PUT |
| Products | /api/admin/products/batch | DELETE |
| Reviews | /api/admin/reviews | GET |
| Reviews | /api/admin/reviews/pending | GET |
| Reviews | /api/admin/reviews/{id}/audit | PUT |
| Reviews | /api/admin/reviews/{id} | DELETE |
| Reviews | /api/admin/reviews/batch/status | PUT |
| Reviews | /api/admin/reviews/batch | DELETE |
| System | /api/admin/system/config | GET/PUT |
| System | /api/admin/system/settings | GET/PUT |
| Roles | /api/admin/roles | GET/POST |
| Roles | /api/admin/roles/{id} | PUT/DELETE |
| Roles | /api/admin/roles/batch | DELETE |
| Permissions | /api/admin/permissions | GET |
| Activities | /api/admin/activities | GET/POST |
| Activities | /api/admin/activities/{id} | PUT/DELETE |
| Activities | /api/admin/activities/{id}/status | PUT |
| Tasks | /api/admin/tasks | GET/POST |
| Tasks | /api/admin/tasks/{id} | PUT/DELETE |
| Tasks | /api/admin/tasks/{id}/execute | POST |
| Shop Audit | /api/admin/shops/pending | GET |
| Shop Audit | /api/admin/shops/{id}/audit | PUT |
| Logs | /api/admin/operation-logs | GET |
| Logs | /api/admin/operation-logs/{id} | DELETE |
| Logs | /api/admin/operation-logs | DELETE |

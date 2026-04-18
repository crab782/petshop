# 平台端Mock模拟服务 - 实施计划

## Mock服务开发任务清单

### 第一阶段：基础架构搭建

#### [x] 任务1：安装mock.js依赖
- **优先级**：P0
- **依赖**：无
- **描述**：
  - 检查package.json是否已包含mock.js
  - 如未安装，执行npm install mockjs --save-dev
  - 配置vite或webpack的mock插件（如需要）
- **验收标准**：AC-1
- **测试要求**：
  - `programmatic` TR-1.1：mock.js安装成功
  - `programmatic` TR-1.2：可以在代码中正确引入mockjs
- **已完成**：mockjs已安装成功

#### [x] 任务2：创建Mock目录结构
- **优先级**：P0
- **依赖**：任务1
- **描述**：
  - 创建 src/mock/ 目录
  - 创建 src/mock/utils/ 目录
  - 创建 src/mock/admin/ 目录
- **验收标准**：AC-1
- **测试要求**：
  - `programmatic` TR-2.1：目录结构创建成功
- **已完成**：目录结构已创建

#### [x] 任务3：创建Mock入口文件
- **优先级**：P0
- **依赖**：任务2
- **描述**：
  - 创建 src/mock/index.js 作为Mock主入口
  - 配置Mock.setup()设置全局响应时间
  - 引入所有模块的Mock配置
- **验收标准**：AC-1
- **测试要求**：
  - `programmatic` TR-3.1：Mock服务能正确启动
- **已完成**：mock/index.js已创建并引入所有模块

#### [x] 任务4：创建随机数据生成工具
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 创建 src/mock/utils/random.js
  - 实现随机ID生成
  - 实现随机日期生成
  - 实现随机枚举值选择
  - 实现随机字符串生成
- **验收标准**：AC-2
- **测试要求**：
  - `programmatic` TR-4.1：工具函数正确生成数据
- **已完成**：random.js已创建，包含randomId, randomDate, randomEnum, randomString, randomPhone, randomEmail, randomIP等函数

### 第二阶段：用户管理模块

#### [x] 任务5：创建用户管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/users.js
  - 实现GET /api/admin/users接口
  - 实现GET /api/admin/users/:id接口
  - 实现PUT /api/admin/users/:id接口
  - 实现DELETE /api/admin/users/:id接口
  - 实现POST /api/admin/users/batch接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-5.1：用户CRUD接口正常工作
  - `programmatic` TR-5.2：分页数据正确
  - `programmatic` TR-5.3：批量操作接口正常
- **已完成**：users.js已创建

### 第三阶段：商家管理模块

#### [x] 任务7：创建商家管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/merchants.js
  - 实现商家列表接口
  - 实现商家详情接口
  - 实现商家审核接口
  - 实现商家删除接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-7.1：商家CRUD接口正常
- **已完成**：merchants.js已创建

#### [x] 任务9：创建店铺审核Mock配置
- **优先级**：P1
- **依赖**：任务7
- **描述**：
  - 创建 src/mock/admin/shop-audit.js
  - 实现GET /api/admin/shops/pending接口
  - 实现POST /api/admin/shops/:id/audit接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-9.1：店铺审核接口正常
- **已完成**：shop-audit.js已创建

### 第四阶段：服务商品模块

#### [x] 任务10：创建服务管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/services.js
  - 实现服务列表接口
  - 实现服务详情接口
  - 实现服务删除接口
  - 实现服务状态更新接口
  - 实现服务批量操作接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-10.1：服务CRUD接口正常
- **已完成**：services.js已创建

#### [x] 任务11：创建商品管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/products.js
  - 实现商品列表接口
  - 实现商品详情接口
  - 实现商品编辑接口
  - 实现商品删除接口
  - 实现商品批量操作接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-11.1：商品CRUD接口正常
- **已完成**：products.js已创建

### 第五阶段：评价公告模块

#### [x] 任务12：创建评价管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/reviews.js
  - 实现评价列表接口
  - 实现评价审核接口
  - 实现评价删除接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-12.1：评价管理接口正常
- **已完成**：reviews.js已创建

#### [x] 任务13：创建公告管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/announcements.js
  - 实现公告列表接口
  - 实现公告详情接口
  - 实现公告创建接口
  - 实现公告编辑接口
  - 实现公告删除接口
  - 实现公告发布/下架接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-13.1：公告CRUD接口正常
- **已完成**：announcements.js已创建

### 第六阶段：系统设置模块

#### [x] 任务14：创建角色权限Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/roles.js
  - 实现角色列表接口
  - 实现角色创建接口
  - 实现角色编辑接口
  - 实现角色删除接口
  - 实现权限列表接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-14.1：角色权限接口正常
- **已完成**：roles.js已创建

#### [x] 任务15：创建操作日志Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/logs.js
  - 实现日志列表接口
  - 实现日志删除接口
  - 实现日志清空接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-15.1：日志接口正常
- **已完成**：logs.js已创建

### 第七阶段：活动任务模块

#### [x] 任务17：创建活动管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/activities.js
  - 实现活动列表接口
  - 实现活动创建接口
  - 实现活动编辑接口
  - 实现活动删除接口
  - 实现活动状态切换接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-17.1：活动管理接口正常
- **已完成**：activities.js已创建

#### [x] 任务18：创建任务管理Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/tasks.js
  - 实现任务列表接口
  - 实现任务创建接口
  - 实现任务编辑接口
  - 实现任务删除接口
  - 实现任务执行接口
- **验收标准**：AC-1, AC-2, AC-3
- **测试要求**：
  - `programmatic` TR-18.1：任务管理接口正常
- **已完成**：tasks.js已创建

#### [x] 任务19：创建仪表盘Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/dashboard.js
  - 实现获取统计数据接口
  - 实现获取最近注册用户列表接口
  - 实现获取待审核商家列表接口
  - 实现获取系统公告列表接口
- **验收标准**：AC-1, AC-2
- **测试要求**：
  - `programmatic` TR-19.1：仪表盘接口正常
- **已完成**：dashboard.js已创建，包含stats、recent-users、pending-merchants、announcements、system/settings接口

### 第八阶段：仪表盘模块

#### [ ] 任务19：创建仪表盘Mock配置
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 创建 src/mock/admin/dashboard.js
  - 实现获取统计数据接口
  - 实现获取最近注册用户列表接口
  - 实现获取待审核商家列表接口
  - 实现获取系统公告列表接口
- **验收标准**：AC-1, AC-2
- **测试要求**：
  - `programmatic` TR-19.1：仪表盘接口正常

### 第九阶段：测试与集成

#### [ ] 任务20：集成测试
- **优先级**：P0
- **依赖**：任务1-19
- **描述**：
  - 在main.js或App.vue中引入Mock
  - 测试所有接口是否正常工作
  - 验证数据格式是否正确
  - 检查是否有控制台警告或错误
- **验收标准**：AC-1, AC-2, AC-3, AC-4
- **测试要求**：
  - `programmatic` TR-20.1：所有接口测试通过
  - `programmatic` TR-20.2：响应时间符合要求
  - `programmatic` TR-20.3：控制台无错误

#### [ ] 任务21：文档编写
- **优先级**：P2
- **依赖**：任务20
- **描述**：
  - 编写Mock API使用文档
  - 说明如何启用/禁用Mock
  - 说明如何扩展Mock数据
- **验收标准**：AC-1
- **测试要求**：
  - `human-judgment` TR-21.1：文档完整清晰

## 任务依赖关系
- 任务1-4 为基础架构，必须先完成
- 任务5-19 可以并行进行
- 任务20-21 必须最后完成
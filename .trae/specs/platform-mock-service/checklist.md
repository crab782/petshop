# 平台端Mock模拟服务 - 验证清单

## 第一阶段：基础架构验证

### [x] 1.1 Mock.js安装验证
- [x] package.json中包含mockjs依赖
- [x] mockjs版本正确
- [x] 能够正确导入mockjs模块

### [x] 1.2 目录结构验证
- [x] src/mock/ 目录存在
- [x] src/mock/utils/ 目录存在
- [x] src/mock/admin/ 目录存在

### [x] 1.3 Mock入口文件验证
- [x] src/mock/index.js 文件存在
- [x] Mock.setup()配置正确
- [x] 所有模块正确引入

### [x] 1.4 随机数据工具验证
- [x] random.js 存在
- [x] 随机ID生成函数正常
- [x] 随机日期生成函数正常
- [x] 随机枚举值函数正常
- [x] 随机字符串生成函数正常

## 第二阶段：用户管理模块验证

### [ ] 2.1 用户列表接口验证
- [ ] GET /api/admin/users 返回用户列表
- [ ] 分页参数page, pageSize正确处理
- [ ] 返回数据结构包含id, username, email, phone, status, createdAt
- [ ] 支持关键词搜索参数keyword
- [ ] 支持状态筛选参数status

### [ ] 2.2 用户详情接口验证
- [ ] GET /api/admin/users/:id 返回用户详情
- [ ] 包含宠物列表数据
- [ ] 包含订单记录数据
- [ ] 包含评价记录数据

### [ ] 2.3 用户更新接口验证
- [ ] PUT /api/admin/users/:id 正确更新用户
- [ ] 返回更新后的用户数据
- [ ] 状态码200

### [ ] 2.4 用户删除接口验证
- [ ] DELETE /api/admin/users/:id 正确删除用户
- [ ] 返回成功消息

### [ ] 2.5 批量操作接口验证
- [ ] POST /api/admin/users/batch 支持批量禁用
- [ ] POST /api/admin/users/batch 支持批量启用
- [ ] POST /api/admin/users/batch 支持批量删除
- [ ] 返回成功消息和影响行数

## 第三阶段：商家管理模块验证

### [ ] 3.1 商家列表接口验证
- [ ] GET /api/admin/merchants 返回商家列表
- [ ] 分页参数正确处理
- [ ] 返回数据包含商家完整信息

### [ ] 3.2 商家详情接口验证
- [ ] GET /api/admin/merchants/:id 返回商家详情
- [ ] 包含服务列表
- [ ] 包含商品列表
- [ ] 包含订单记录
- [ ] 包含评价记录

### [ ] 3.3 商家审核接口验证
- [ ] GET /api/admin/merchants/pending 返回待审核商家列表
- [ ] POST /api/admin/merchants/:id/audit 支持通过
- [ ] POST /api/admin/merchants/:id/audit 支持拒绝
- [ ] 支持拒绝原因参数

### [ ] 3.4 店铺审核接口验证
- [ ] GET /api/admin/shops/pending 返回待审核店铺列表
- [ ] POST /api/admin/shops/:id/audit 支持通过/拒绝

## 第四阶段：服务商品模块验证

### [ ] 4.1 服务列表接口验证
- [ ] GET /api/admin/services 返回服务列表
- [ ] 支持商家筛选参数merchantId
- [ ] 支持价格区间筛选参数minPrice, maxPrice
- [ ] 支持状态筛选参数status

### [ ] 4.2 服务批量操作验证
- [ ] 批量启用接口正常
- [ ] 批量禁用接口正常
- [ ] 批量删除接口正常

### [ ] 4.3 商品列表接口验证
- [ ] GET /api/admin/products 返回商品列表
- [ ] 支持商家筛选
- [ ] 支持价格区间筛选
- [ ] 支持库存状态筛选（hasStock, outOfStock）
- [ ] 包含商品图片字段

### [ ] 4.4 商品详情接口验证
- [ ] GET /api/admin/products/:id 返回商品详情
- [ ] PUT /api/admin/products/:id 正确更新商品
- [ ] DELETE /api/admin/products/:id 正确删除商品

## 第五阶段：评价公告模块验证

### [ ] 5.1 评价列表接口验证
- [ ] GET /api/admin/reviews 返回评价列表
- [ ] 支持评分筛选参数rating
- [ ] 支持时间范围筛选参数startDate, endDate
- [ ] 支持状态筛选参数status

### [ ] 5.2 评价审核接口验证
- [ ] GET /api/admin/reviews/pending 返回待审核评价
- [ ] POST /api/admin/reviews/:id/audit 支持通过/拒绝

### [ ] 5.3 公告列表接口验证
- [ ] GET /api/admin/announcements 返回公告列表
- [ ] 支持标题搜索参数keyword
- [ ] 支持状态筛选参数status

### [ ] 5.4 公告CRUD接口验证
- [ ] GET /api/admin/announcements/:id 返回公告详情
- [ ] POST /api/admin/announcements 创建公告
- [ ] PUT /api/admin/announcements/:id 更新公告
- [ ] DELETE /api/admin/announcements/:id 删除公告
- [ ] POST /api/admin/announcements/:id/publish 发布公告
- [ ] POST /api/admin/announcements/:id/unpublish 下架公告

## 第六阶段：系统设置模块验证

### [ ] 6.1 角色列表接口验证
- [ ] GET /api/admin/roles 返回角色列表
- [ ] 支持角色名称搜索
- [ ] 支持分页

### [ ] 6.2 角色CRUD接口验证
- [ ] POST /api/admin/roles 创建角色
- [ ] PUT /api/admin/roles/:id 更新角色
- [ ] DELETE /api/admin/roles/:id 删除角色
- [ ] 支持权限列表参数

### [ ] 6.3 权限列表接口验证
- [ ] GET /api/admin/permissions 返回权限树

### [ ] 6.4 操作日志接口验证
- [ ] GET /api/admin/operation-logs 返回日志列表
- [ ] 支持操作类型筛选参数action
- [ ] 支持操作人筛选参数username
- [ ] 支持时间范围筛选参数startDate, endDate
- [ ] DELETE /api/admin/operation-logs/:id 删除单条
- [ ] DELETE /api/admin/operation-logs 清空日志

### [ ] 6.5 系统设置接口验证
- [ ] GET /api/admin/system/settings 返回系统设置
- [ ] PUT /api/admin/system/settings 保存系统设置
- [ ] 设置包含基本设置、邮件设置、短信设置、支付设置

## 第七阶段：活动任务模块验证

### [ ] 7.1 活动列表接口验证
- [ ] GET /api/admin/activities 返回活动列表
- [ ] 支持名称搜索
- [ ] 支持类型筛选
- [ ] 支持时间范围筛选
- [ ] 支持状态筛选

### [ ] 7.2 活动CRUD接口验证
- [ ] POST /api/admin/activities 创建活动
- [ ] PUT /api/admin/activities/:id 更新活动
- [ ] DELETE /api/admin/activities/:id 删除活动
- [ ] PUT /api/admin/activities/:id/toggle 切换状态

### [ ] 7.3 任务列表接口验证
- [ ] GET /api/admin/tasks 返回任务列表
- [ ] 支持名称搜索
- [ ] 支持类型筛选
- [ ] 支持状态筛选
- [ ] 支持时间范围筛选

### [ ] 7.4 任务CRUD接口验证
- [ ] POST /api/admin/tasks 创建任务
- [ ] PUT /api/admin/tasks/:id 更新任务
- [ ] DELETE /api/admin/tasks/:id 删除任务
- [ ] POST /api/admin/tasks/:id/execute 执行任务

## 第八阶段：仪表盘模块验证

### [ ] 8.1 统计数据接口验证
- [ ] GET /api/admin/dashboard/stats 返回统计数据
- [ ] 包含totalUsers（总用户数）
- [ ] 包含totalMerchants（总商家数）
- [ ] 包含todayOrders（今日订单数）
- [ ] 包含monthlyRevenue（本月营收）

### [ ] 8.2 最近注册用户接口验证
- [ ] GET /api/admin/dashboard/recent-users 返回最近注册用户
- [ ] 返回10条数据

### [ ] 8.3 待审核商家接口验证
- [ ] GET /api/admin/dashboard/pending-merchants 返回待审核商家
- [ ] 返回5条数据

### [ ] 8.4 系统公告接口验证
- [ ] GET /api/admin/dashboard/announcements 返回公告
- [ ] 返回最新5条公告

## 第九阶段：集成测试验证

### [ ] 9.1 Mock服务启动验证
- [ ] Mock服务能正常启动
- [ ] 能拦截API请求
- [ ] 控制台无错误警告

### [ ] 9.2 响应时间验证
- [ ] GET请求响应时间在100-500ms之间
- [ ] POST请求响应时间在100-500ms之间

### [ ] 9.3 数据一致性验证
- [ ] ID字段全局唯一
- [ ] 日期格式统一（YYYY-MM-DD HH:mm:ss）
- [ ] 状态字段值符合枚举定义

### [ ] 9.4 异常场景验证
- [ ] 访问不存在的API返回404
- [ ] 错误响应格式正确

## 第十阶段：文档验证

### [ ] 10.1 Mock使用文档验证
- [ ] 文档说明如何启用Mock
- [ ] 文档说明如何禁用Mock
- [ ] 文档说明如何扩展数据
- [ ] 文档包含API接口清单
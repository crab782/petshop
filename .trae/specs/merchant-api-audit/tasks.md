# 商家端API端点审计与创建 - 实施计划

## [x] Task 1: 审计现有API端点
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - 对比前端API定义和后端API实现
  - 列出所有缺失的API端点
  - 确定需要创建的实体类和Service类
- **验收标准**: AC-1
- **测试要求**:
  - `programmatic` TR-1.1: 生成完整的API端点对比报告
  - `programmatic` TR-1.2: 识别所有缺失的API端点
- **备注**: 这是后续所有任务的基础

## [x] Task 2: 创建Product相关后端代码
- **优先级**: P0
- **依赖**: Task 1
- **描述**: 
  - 创建Product实体类（如不存在）
  - 创建ProductService类
  - 创建ProductRepository接口
- **验收标准**: AC-2
- **测试要求**:
  - `programmatic` TR-2.1: Product实体类字段与数据库表对应
  - `programmatic` TR-2.2: ProductService包含CRUD方法
- **备注**: 商品管理的基础

## [ ] Task 3: 创建Category相关后端代码
- **优先级**: P0
- **依赖**: Task 1
- **描述**: 
  - 创建Category实体类
  - 创建CategoryService类
  - 创建CategoryRepository接口
  - 创建数据库表（如不存在）
- **验收标准**: AC-2
- **测试要求**:
  - `programmatic` TR-3.1: Category实体类字段完整
  - `programmatic` TR-3.2: CategoryService包含CRUD方法
- **备注**: 分类管理的基础

## [x] Task 4: 创建商家信息API端点
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - GET `/api/merchant/info` - 获取商家信息
  - PUT `/api/merchant/info` - 更新商家信息
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-4.1: GET返回商家信息
  - `programmatic` TR-4.2: PUT更新商家信息成功
  - `programmatic` TR-4.3: 未认证返回401
- **备注**: 对应shop-edit页面

## [x] Task 5: 创建服务管理API端点
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - PUT `/api/merchant/services/batch/status` - 批量更新服务状态
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-5.1: 批量更新成功
  - `programmatic` TR-5.2: 未认证返回401
- **备注**: 对应services页面

## [x] Task 6: 创建商品管理API端点
- **优先级**: P0
- **依赖**: Task 2
- **描述**: 
  - GET `/api/merchant/products` - 获取商品列表
  - POST `/api/merchant/products` - 添加商品
  - GET `/api/merchant/products/{id}` - 获取商品详情
  - PUT `/api/merchant/products/{id}` - 更新商品
  - DELETE `/api/merchant/products/{id}` - 删除商品
  - GET `/api/merchant/products/paged` - 分页获取商品
  - PUT `/api/merchant/products/{id}/status` - 更新商品状态
  - PUT `/api/merchant/products/batch/status` - 批量更新商品状态
  - DELETE `/api/merchant/products/batch` - 批量删除商品
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-6.1: 所有CRUD操作正常
  - `programmatic` TR-6.2: 分页功能正常
  - `programmatic` TR-6.3: 批量操作正常
  - `programmatic` TR-6.4: 未认证返回401
- **备注**: 对应merchant-products和product-edit页面

## [x] Task 7: 创建订单管理API端点
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - GET `/api/merchant/orders` - 获取订单列表
  - PUT `/api/merchant/orders/{id}/status` - 更新订单状态
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-7.1: GET返回订单列表
  - `programmatic` TR-7.2: PUT更新订单状态成功
  - `programmatic` TR-7.3: 未认证返回401
- **备注**: 对应merchant-orders页面

## [x] Task 8: 创建商品订单API端点
- **优先级**: P1
- **依赖**: Task 2
- **描述**: 
  - GET `/api/merchant/product-orders` - 获取商品订单列表
  - PUT `/api/merchant/product-orders/{id}/status` - 更新商品订单状态
  - PUT `/api/merchant/product-orders/{id}/logistics` - 更新物流信息
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-8.1: GET返回商品订单列表
  - `programmatic` TR-8.2: 更新状态和物流成功
  - `programmatic` TR-8.3: 未认证返回401
- **备注**: 对应merchant-product-orders页面

## [x] Task 9: 创建评价管理API端点
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - GET `/api/merchant/reviews` - 获取评价列表
  - PUT `/api/merchant/reviews/{id}/reply` - 回复评价
  - DELETE `/api/merchant/reviews/{id}` - 删除评价
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-9.1: GET返回评价列表和统计数据
  - `programmatic` TR-9.2: 回复和删除成功
  - `programmatic` TR-9.3: 未认证返回401
- **备注**: 对应reviews页面

## [x] Task 10: 创建分类管理API端点
- **优先级**: P1
- **依赖**: Task 3
- **描述**: 
  - GET `/api/merchant/categories` - 获取分类列表
  - POST `/api/merchant/categories` - 添加分类
  - PUT `/api/merchant/categories/{id}` - 更新分类
  - DELETE `/api/merchant/categories/{id}` - 删除分类
  - PUT `/api/merchant/categories/{id}/status` - 更新分类状态
  - PUT `/api/merchant/categories/batch/status` - 批量更新分类状态
  - DELETE `/api/merchant/categories/batch` - 批量删除分类
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-10.1: 所有CRUD操作正常
  - `programmatic` TR-10.2: 批量操作正常
  - `programmatic` TR-10.3: 未认证返回401
- **备注**: 对应categories页面

## [x] Task 11: 创建统计分析API端点
- **优先级**: P1
- **依赖**: Task 1, Task 2
- **描述**: 
  - GET `/api/merchant/revenue-stats` - 获取营收统计
  - GET `/api/merchant/revenue-stats/export` - 导出营收统计
  - GET `/api/merchant/appointment-stats` - 获取预约统计
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-11.1: 返回正确的统计数据
  - `programmatic` TR-11.2: 导出功能正常
  - `programmatic` TR-11.3: 未认证返回401
- **备注**: 对应stats-revenue和stats-appointments页面

## [x] Task 12: 创建店铺设置API端点
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - GET `/api/merchant/settings` - 获取店铺设置
  - PUT `/api/merchant/settings` - 更新店铺设置
  - POST `/api/merchant/change-password` - 修改密码
  - POST `/api/merchant/bind-phone` - 绑定手机
  - POST `/api/merchant/bind-email` - 绑定邮箱
  - POST `/api/merchant/send-verify-code` - 发送验证码
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-12.1: 设置读写正常
  - `programmatic` TR-12.2: 密码修改功能正常
  - `programmatic` TR-12.3: 验证码发送功能正常
  - `programmatic` TR-12.4: 未认证返回401
- **备注**: 对应shop-settings页面

## [x] Task 13: 创建首页统计API端点
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - GET `/api/merchant/dashboard` - 获取首页统计数据
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-13.1: 返回正确的统计数据
  - `programmatic` TR-13.2: 未认证返回401
- **备注**: 对应home页面

## [x] Task 14: API文档和测试
- **优先级**: P0
- **依赖**: Task 4-13
- **描述**: 
  - 为所有API端点添加注释
  - 创建API测试用例
  - 验证所有API端点功能
- **验收标准**: AC-5
- **测试要求**:
  - `programmatic` TR-14.1: 所有API测试通过
  - `human-judgment` TR-14.2: API注释清晰完整
- **备注**: 最终验证

## [x] Task 15: 商家端 API 审计
- **优先级**: P0
- **依赖**: Task 1-14
- **描述**: 
  - 审计 MerchantApiController 和 MerchantController 所有接口
  - 检查 HTTP 方法正确性（GET/POST/PUT/DELETE）
  - 检查 URL 路径规范性
  - 检查请求参数验证
  - 检查响应格式一致性
  - 检查错误处理完整性
  - 检查状态码正确性
  - 检查前端兼容性
- **验收标准**: AC-5
- **测试要求**:
  - `programmatic` TR-15.1: 生成完整的审计报告
  - `programmatic` TR-15.2: 识别所有前后端不兼容问题
  - `programmatic` TR-15.3: 提供修复建议
- **备注**: 审计报告已生成：audit-report.md

## 任务依赖关系
- Task 1 是所有任务的基础
- Task 2, Task 3 可以并行执行
- Task 4-5 依赖 Task 1
- Task 6, Task 8 依赖 Task 2
- Task 10 依赖 Task 3
- Task 11 依赖 Task 1, Task 2
- Task 14 依赖所有其他任务
- Task 15 依赖 Task 1-14

## 审计发现的问题

### P0 - 严重问题（需立即修复）
1. **前后端参数传递不匹配**（5个接口）
   - PUT /api/merchant/appointments/{id}/status
   - PUT /api/merchant/orders/{id}/status
   - PUT /api/merchant/product-orders/{id}/status
   - PUT /api/merchant/products/{id}/status
   - PUT /api/merchant/categories/{id}/status
   
   前端使用 JSON Body 发送参数，后端使用 @RequestParam 接收，导致接口无法正常工作。

### P1 - 中等问题（需尽快修复）
1. **重复接口**
   - GET /api/merchant/profile 和 GET /api/merchant/info 功能重复
   - PUT /api/merchant/profile 和 PUT /api/merchant/info 功能重复

2. **HTTP 方法不规范**
   - POST /api/merchant/settings/toggle-status 应使用 PUT
   - POST /api/merchant/change-password 应使用 PUT

### P2 - 低等问题（需计划修复）
1. **参数验证不完整**
   - 多个接口缺少必要的参数验证

2. **错误处理不一致**
   - 异常捕获过于宽泛
   - 错误消息格式不统一

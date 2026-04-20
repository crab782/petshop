# 后端API接口实现 - 实现计划

## [ ] 任务1：检查后端项目结构和API实现情况
- **优先级**：P0
- **依赖**：None
- **描述**：
  - 检查后端项目的目录结构
  - 检查现有的API接口实现
  - 与商家端页面使用的API接口进行对比
  - 识别缺失的API接口
- **验收标准**：AC-1
- **测试需求**：
  - `human-judgment` TR-1.1: 确认后端项目的结构和技术栈
  - `human-judgment` TR-1.2: 识别已实现和未实现的API接口
- **备注**：需要查看后端项目的Controller、Service等代码

## [ ] 任务2：实现商家信息API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取商家信息API (`/merchant/info` GET)
  - 实现更新商家信息API (`/merchant/info` PUT)
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-2.1: 验证API调用是否成功
  - `human-judgment` TR-2.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现商家信息的查询和更新功能

## [ ] 任务3：实现服务管理API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取服务列表API (`/merchant/services` GET)
  - 实现添加服务API (`/merchant/services` POST)
  - 实现更新服务API (`/merchant/services/{id}` PUT)
  - 实现删除服务API (`/merchant/services/{id}` DELETE)
  - 实现批量更新服务状态API
  - 实现批量删除服务API
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-3.1: 验证API调用是否成功
  - `human-judgment` TR-3.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现服务的CRUD操作

## [ ] 任务4：实现商品管理API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取商品列表API (`/merchant/products` GET)
  - 实现添加商品API (`/merchant/products` POST)
  - 实现更新商品API (`/merchant/products/{id}` PUT)
  - 实现删除商品API (`/merchant/products/{id}` DELETE)
  - 实现批量更新商品状态API
  - 实现批量删除商品API
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-4.1: 验证API调用是否成功
  - `human-judgment` TR-4.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现商品的CRUD操作和库存管理

## [ ] 任务5：实现订单管理API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取预约订单列表API (`/merchant/appointments` GET)
  - 实现更新预约订单状态API (`/merchant/appointments/{id}/status` PUT)
  - 实现获取商品订单列表API (`/merchant/product-orders` GET)
  - 实现更新商品订单状态API (`/merchant/product-orders/{id}/status` PUT)
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-5.1: 验证API调用是否成功
  - `human-judgment` TR-5.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现订单状态管理和发货功能

## [ ] 任务6：实现评价管理API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取评价列表API (`/merchant/reviews` GET)
  - 实现获取评价统计API (`/merchant/reviews/statistics` GET)
  - 实现回复评价API (`/merchant/reviews/{id}/reply` PUT)
  - 实现删除评价API (`/merchant/reviews/{id}` DELETE)
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-6.1: 验证API调用是否成功
  - `human-judgment` TR-6.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现评价的查看和回复功能

## [ ] 任务7：实现店铺设置API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取店铺设置API (`/merchant/settings` GET)
  - 实现更新店铺设置API (`/merchant/settings` PUT)
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-7.1: 验证API调用是否成功
  - `human-judgment` TR-7.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现店铺信息的更新和设置管理

## [ ] 任务8：实现统计报表API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现获取预约统计API (`/merchant/statistics/appointments` GET)
  - 实现导出预约统计API (`/merchant/statistics/appointments/export` GET)
  - 实现获取营收统计API (`/merchant/statistics/revenue` GET)
  - 实现导出营收统计API (`/merchant/statistics/revenue/export` GET)
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-8.1: 验证API调用是否成功
  - `human-judgment` TR-8.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现统计数据的计算和导出功能

## [ ] 任务9：实现账户管理API
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 实现修改密码API (`/merchant/account/password` PUT)
  - 实现绑定手机号API (`/merchant/account/phone` PUT)
  - 实现绑定邮箱API (`/merchant/account/email` PUT)
- **验收标准**：AC-2, AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-9.1: 验证API调用是否成功
  - `human-judgment` TR-9.2: 确认API响应数据结构与前端需求匹配
- **备注**：需要实现账户安全相关的功能

## [ ] 任务10：验证API接口功能
- **优先级**：P0
- **依赖**：任务2-9
- **描述**：
  - 测试所有实现的API接口
  - 验证API接口的功能正确性
  - 验证API接口与前端需求的匹配度
- **验收标准**：AC-3, AC-4
- **测试需求**：
  - `programmatic` TR-10.1: 验证所有API接口都能正常工作
  - `human-judgment` TR-10.2: 确认API接口与前端需求匹配
- **备注**：需要对每个API接口进行测试，确保功能正常
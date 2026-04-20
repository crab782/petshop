# 商家端真实API集成 - 实现计划

## [x] 任务1：检查后端API接口实现情况
- **优先级**：P0
- **依赖**：None
- **描述**：
  - 检查后端项目中已实现的API接口
  - 梳理后端API的端点URL、请求方法、参数和响应结构
  - 与前端商家端页面的功能模块进行匹配
- **验收标准**：AC-1
- **测试需求**：
  - `human-judgment` TR-1.1: 确认后端API接口的实现情况
  - `human-judgment` TR-1.2: 验证后端API与前端功能模块的匹配度
- **备注**：需要查看后端项目的Controller、Service等代码

## [x] 任务2：审查商家端页面功能模块
- **优先级**：P0
- **依赖**：任务1
- **描述**：
  - 审查商家端20个页面的所有功能模块
  - 识别每个功能模块需要的API接口
  - 与后端已实现的API接口进行匹配
- **验收标准**：AC-1
- **测试需求**：
  - `human-judgment` TR-2.1: 识别商家端页面的功能模块
  - `human-judgment` TR-2.2: 确认功能模块与后端API的匹配情况
- **备注**：需要查看前端商家端页面的代码

## [x] 任务3：集成真实API接口（布局和首页）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 MerchantLayout.vue 组件，集成真实的商家信息API
  - 修改 merchant-home/index.vue 页面，集成真实的统计数据API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-3.1: 验证API调用是否成功
  - `human-judgment` TR-3.2: 确认页面功能是否正常
- **备注**：关注API调用的错误处理和加载状态

## [/] 任务4：集成真实API接口（服务管理）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 merchant-services/index.vue 页面，集成真实的服务列表API
  - 修改 service-edit/index.vue 页面，集成真实的服务创建/更新API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-4.1: 验证API调用是否成功
  - `human-judgment` TR-4.2: 确认页面功能是否正常
- **备注**：关注服务的CRUD操作

## [ ] 任务5：集成真实API接口（商品管理）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 merchant-products/index.vue 页面，集成真实的商品列表API
  - 修改 product-edit/index.vue 页面，集成真实的商品创建/更新API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-5.1: 验证API调用是否成功
  - `human-judgment` TR-5.2: 确认页面功能是否正常
- **备注**：关注商品的CRUD操作和库存管理

## [ ] 任务6：集成真实API接口（订单管理）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 merchant-orders/index.vue 页面，集成真实的预约订单API
  - 修改 merchant-product-orders/index.vue 页面，集成真实的商品订单API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-6.1: 验证API调用是否成功
  - `human-judgment` TR-6.2: 确认页面功能是否正常
- **备注**：关注订单状态管理和发货功能

## [ ] 任务7：集成真实API接口（评价管理）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 merchant-reviews/index.vue 页面，集成真实的评价列表API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-7.1: 验证API调用是否成功
  - `human-judgment` TR-7.2: 确认页面功能是否正常
- **备注**：关注评价的查看和回复功能

## [ ] 任务8：集成真实API接口（店铺管理）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 shop-edit/index.vue 页面，集成真实的店铺信息更新API
  - 修改 shop-settings/index.vue 页面，集成真实的店铺设置API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-8.1: 验证API调用是否成功
  - `human-judgment` TR-8.2: 确认页面功能是否正常
- **备注**：关注店铺信息的更新和设置管理

## [ ] 任务9：集成真实API接口（统计报表）
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 stats-appointments/index.vue 页面，集成真实的预约统计API
  - 修改 stats-revenue/index.vue 页面，集成真实的营收统计API
- **验收标准**：AC-2
- **测试需求**：
  - `programmatic` TR-9.1: 验证API调用是否成功
  - `human-judgment` TR-9.2: 确认页面功能是否正常
- **备注**：关注统计数据的准确性和图表展示

## [ ] 任务10：生成API使用文档
- **优先级**：P0
- **依赖**：任务3-9
- **描述**：
  - 系统性梳理和总结商家端页面所使用的真实后端API
  - 生成完整的API使用文档，包括API的端点URL、请求方法、主要请求参数及响应数据结构等关键信息
- **验收标准**：AC-3
- **测试需求**：
  - `human-judgment` TR-10.1: 验证文档的完整性
  - `human-judgment` TR-10.2: 评估文档的可读性和实用性
- **备注**：文档应清晰、完整、易于理解
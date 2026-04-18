# 统一早期API响应格式 - 实施计划

## [x] Task 1: 修改商家资料API端点
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - GET `/api/merchant/profile` - 改为返回 `ApiResponse<Merchant>`
  - PUT `/api/merchant/profile` - 改为返回 `ApiResponse<Merchant>`
  - 添加完整的中文注释
  - 完善错误处理
- **验收标准**: AC-1, AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-1.1: GET请求返回统一格式
  - `programmatic` TR-1.2: PUT请求返回统一格式
  - `programmatic` TR-1.3: 未登录返回401
  - `programmatic` TR-1.4: 错误情况返回正确的错误信息

## [x] Task 2: 修改服务管理API端点
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - GET `/api/merchant/services` - 改为返回 `ApiResponse<List<Service>>`
  - POST `/api/merchant/services` - 改为返回 `ApiResponse<Service>`
  - PUT `/api/merchant/services/{id}` - 改为返回 `ApiResponse<Service>`
  - DELETE `/api/merchant/services/{id}` - 改为返回 `ApiResponse<Void>`
  - 添加完整的中文注释
  - 完善错误处理和参数验证
- **验收标准**: AC-1, AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-2.1: 所有端点返回统一格式
  - `programmatic` TR-2.2: 参数验证正确
  - `programmatic` TR-2.3: 未登录返回401
  - `programmatic` TR-2.4: 错误情况返回正确的错误信息

## [x] Task 3: 修改预约管理API端点
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - GET `/api/merchant/appointments` - 改为返回 `ApiResponse<List<Appointment>>`
  - PUT `/api/merchant/appointments/{id}/status` - 改为返回 `ApiResponse<Appointment>`
  - 添加完整的中文注释
  - 完善错误处理和状态验证
- **验收标准**: AC-1, AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-3.1: 所有端点返回统一格式
  - `programmatic` TR-3.2: 状态验证正确
  - `programmatic` TR-3.3: 未登录返回401
  - `programmatic` TR-3.4: 错误情况返回正确的错误信息

## [x] Task 4: 验证所有API端点
- **优先级**: P0
- **依赖**: Task 1, Task 2, Task 3
- **描述**: 
  - 验证所有API响应格式统一
  - 验证所有API注释完整
  - 验证所有API错误处理正确
- **验收标准**: AC-1, AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-4.1: 所有API返回统一格式
  - `human-judgment` TR-4.2: 所有API注释清晰完整
  - `programmatic` TR-4.3: 所有API错误处理正确

## 任务依赖关系
- Task 1, Task 2, Task 3 可以并行执行
- Task 4 依赖 Task 1, Task 2, Task 3

# 登录跳转与权限问题修复 - 实现计划

## [x] Task 1: 分析前端登录逻辑和路由配置
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查前端登录页面的登录逻辑
  - 检查前端路由配置，特别是用户端和商家端的路由
  - 分析登录后跳转逻辑
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `human-judgment` TR-1.1: 确认登录成功后跳转到/user/home
  - `human-judgment` TR-1.2: 确认路由配置正确区分用户端和商家端
- **Notes**: 重点检查Login.vue中的登录成功后的跳转逻辑

## [x] Task 2: 分析前端API调用和认证信息传递
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查前端API请求拦截器的token传递逻辑
  - 分析token存储和优先级
  - 检查Mock服务配置
- **Acceptance Criteria Addressed**: AC-2, AC-4
- **Test Requirements**:
  - `programmatic` TR-2.1: 确认请求头中正确携带用户token
  - `programmatic` TR-2.2: 确认Mock服务配置正确
- **Notes**: 重点检查request.ts中的请求拦截器和响应拦截器

## [x] Task 3: 分析后端权限控制和认证逻辑
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查后端SecurityConfig配置
  - 分析CORS配置
  - 检查用户端接口的权限控制
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: 确认CORS配置包含前端端口
  - `programmatic` TR-3.2: 确认用户端接口权限配置正确
- **Notes**: 重点检查SecurityConfig.java和UserApiController.java

## [x] Task 4: 修复前端登录后路由跳转问题
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 修改前端响应拦截器的401错误处理逻辑
  - 根据用户类型跳转到对应登录页
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `human-judgment` TR-4.1: 登录成功后停留在用户首页
  - `human-judgment` TR-4.2: 401错误时跳转到正确的登录页
- **Notes**: 修改request.ts中的响应拦截器

## [x] Task 5: 修复前端API调用认证信息传递问题
- **Priority**: P0
- **Depends On**: Task 2
- **Description**:
  - 调整token优先级，优先使用用户token
  - 禁用Mock服务，确保请求到达真实后端
- **Acceptance Criteria Addressed**: AC-2, AC-4
- **Test Requirements**:
  - `programmatic` TR-5.1: 确认请求头中携带用户token
  - `programmatic` TR-5.2: 确认Mock服务已禁用
- **Notes**: 修改request.ts和.env.development

## [x] Task 6: 修复后端权限控制问题
- **Priority**: P0
- **Depends On**: Task 3
- **Description**:
  - 更新后端CORS配置，添加前端端口
  - 确保用户端接口权限配置正确
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-6.1: 确认CORS配置包含前端端口
  - `programmatic` TR-6.2: 确认用户端接口返回200 OK
- **Notes**: 修改SecurityConfig.java

## [x] Task 7: 验证修复结果
- **Priority**: P0
- **Depends On**: Task 4, Task 5, Task 6
- **Description**:
  - 测试用户端登录流程
  - 测试用户端接口访问
  - 测试商家端功能是否受影响
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `human-judgment` TR-7.1: 用户端登录后不跳转到商家登录页
  - `programmatic` TR-7.2: 用户端接口返回200 OK
  - `human-judgment` TR-7.3: 商家端功能正常
- **Notes**: 全面测试修复结果，确保所有功能正常
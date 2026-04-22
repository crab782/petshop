# 登录后401跳转问题修复 - 实现计划

## [x] Task 1: 分析前端登录逻辑
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查前端登录页面的登录逻辑
  - 分析token保存逻辑
  - 检查登录成功后的跳转逻辑
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 确认登录成功后token正确保存到localStorage
  - `human-judgment` TR-1.2: 确认登录成功后跳转到/user/home
- **Notes**: 重点检查Login.vue中的登录逻辑

## [x] Task 2: 分析前端API请求拦截器
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查前端API请求拦截器的token传递逻辑
  - 分析token获取顺序和优先级
  - 检查响应拦截器的401处理逻辑
- **Acceptance Criteria Addressed**: AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-2.1: 确认请求头中正确携带用户token
  - `programmatic` TR-2.2: 确认401错误处理逻辑正确
- **Notes**: 重点检查request.ts中的请求拦截器和响应拦截器

## [x] Task 3: 分析后端token验证逻辑
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查后端JWT验证逻辑
  - 分析UserDetailsServiceImpl实现
  - 检查SecurityConfig中的权限配置
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: 确认后端能正确验证用户token
  - `programmatic` TR-3.2: 确认用户端接口权限配置正确
- **Notes**: 重点检查JwtUtils.java和UserDetailsServiceImpl.java

## [x] Task 4: 修复前端登录逻辑
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 修复前端登录成功后的token保存逻辑
  - 确保token正确存储到localStorage
  - 优化登录成功后的跳转逻辑
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-4.1: 确认登录成功后token正确保存
  - `human-judgment` TR-4.2: 确认登录成功后跳转到用户首页
- **Notes**: 修改Login.vue中的登录逻辑

## [x] Task 5: 修复前端API请求拦截器
- **Priority**: P0
- **Depends On**: Task 2
- **Description**:
  - 修复前端请求拦截器的token传递逻辑
  - 确保正确获取和传递用户token
  - 优化响应拦截器的401处理逻辑
- **Acceptance Criteria Addressed**: AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-5.1: 确认请求头中正确携带用户token
  - `programmatic` TR-5.2: 确认401错误处理逻辑正确
- **Notes**: 修改request.ts中的拦截器逻辑

## [x] Task 6: 修复后端token验证逻辑
- **Priority**: P0
- **Depends On**: Task 3
- **Description**:
  - 修复后端JWT验证逻辑
  - 确保正确验证用户token
  - 优化权限配置
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-6.1: 确认后端能正确验证用户token
  - `programmatic` TR-6.2: 确认用户端接口返回200 OK
- **Notes**: 修改JwtUtils.java和SecurityConfig.java

## [/] Task 7: 验证修复结果
- **Priority**: P0
- **Depends On**: Task 4, Task 5, Task 6
- **Description**:
  - 测试用户端登录流程
  - 测试用户端接口访问
  - 测试商家端功能是否受影响
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `human-judgment` TR-7.1: 用户端登录后不跳回登录页
  - `programmatic` TR-7.2: 用户端接口返回200 OK
  - `human-judgment` TR-7.3: 商家端功能正常
- **Notes**: 全面测试修复结果，确保所有功能正常
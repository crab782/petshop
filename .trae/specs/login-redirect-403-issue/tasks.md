# 登录后路由跳转和403问题分析 - 实现计划

## [ ] 任务1: 分析前端登录逻辑和路由配置
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查前端登录组件的逻辑，特别是登录成功后的路由跳转
  - 分析前端路由配置，特别是用户端和商家端的路由权限控制
  - 检查前端状态管理，确认登录后用户信息和角色的存储
- **Acceptance Criteria Addressed**: [AC-1, AC-3]
- **Test Requirements**:
  - `programmatic` TR-1.1: 登录成功后应跳转到用户首页（/user/home）
  - `programmatic` TR-1.2: 商家登录页应只对商家开放
- **Notes**: 重点关注登录组件、路由配置文件和状态管理文件

## [ ] 任务2: 分析前端API调用和认证信息传递
- **Priority**: P0
- **Depends On**: 任务1
- **Description**:
  - 检查前端API调用是否正确传递了认证信息（如JWT token）
  - 分析API调用的拦截器配置，确认认证信息的处理
  - 检查用户端接口的调用方式，确认是否与商家端接口的调用方式不同
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-2.1: 用户端接口调用应正确传递认证信息
  - `programmatic` TR-2.2: 用户端接口应返回200 OK，而不是403 Forbidden
- **Notes**: 重点关注API调用模块和请求拦截器

## [ ] 任务3: 分析后端权限控制和认证逻辑
- **Priority**: P0
- **Depends On**: 任务1, 任务2
- **Description**:
  - 检查后端认证逻辑，确认JWT token的验证
  - 分析后端权限控制，确认用户和商家的权限区分
  - 检查用户端接口的权限配置，确认是否正确配置了权限
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-3.1: 后端应正确验证用户身份和权限
  - `programmatic` TR-3.2: 用户端接口应正确配置权限，允许用户访问
- **Notes**: 重点关注Spring Security配置、控制器权限注解和认证过滤器

## [ ] 任务4: 修复前端登录后路由跳转问题
- **Priority**: P1
- **Depends On**: 任务1
- **Description**:
  - 根据任务1的分析结果，修复前端登录后路由跳转的问题
  - 确保登录成功后跳转到用户首页，而不是商家登录页
  - 优化路由守卫逻辑，确保不同角色访问正确的页面
- **Acceptance Criteria Addressed**: [AC-1, AC-3]
- **Test Requirements**:
  - `programmatic` TR-4.1: 登录成功后应跳转到用户首页
  - `programmatic` TR-4.2: 商家登录页应只对商家开放
- **Notes**: 可能需要修改登录组件和路由守卫的逻辑

## [ ] 任务5: 修复前端API调用认证信息传递问题
- **Priority**: P1
- **Depends On**: 任务2
- **Description**:
  - 根据任务2的分析结果，修复前端API调用认证信息传递的问题
  - 确保用户端接口调用时正确传递认证信息
  - 优化请求拦截器，确保所有API调用都能正确处理认证信息
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-5.1: 用户端接口调用应正确传递认证信息
  - `programmatic` TR-5.2: 用户端接口应返回200 OK
- **Notes**: 可能需要修改API调用模块和请求拦截器

## [ ] 任务6: 修复后端权限控制问题
- **Priority**: P1
- **Depends On**: 任务3
- **Description**:
  - 根据任务3的分析结果，修复后端权限控制的问题
  - 确保用户端接口正确配置了权限，允许用户访问
  - 优化认证过滤器，确保正确验证用户身份和权限
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-6.1: 后端应正确验证用户身份和权限
  - `programmatic` TR-6.2: 用户端接口应返回200 OK
- **Notes**: 可能需要修改Spring Security配置和控制器权限注解

## [ ] 任务7: 验证修复结果
- **Priority**: P0
- **Depends On**: 任务4, 任务5, 任务6
- **Description**:
  - 验证登录后是否正确跳转到用户首页
  - 验证用户端接口是否能正常访问
  - 验证商家登录页是否只对商家开放
  - 验证其他功能是否正常运行
- **Acceptance Criteria Addressed**: [AC-1, AC-2, AC-3]
- **Test Requirements**:
  - `programmatic` TR-7.1: 登录成功后跳转到用户首页
  - `programmatic` TR-7.2: 用户端接口返回200 OK
  - `programmatic` TR-7.3: 商家登录页只对商家开放
- **Notes**: 全面测试登录流程和用户端功能
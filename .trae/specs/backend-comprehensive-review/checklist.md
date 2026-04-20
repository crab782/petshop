# 后端项目综合审查与迁移 - 验证清单

## 阶段一：静态资源移除
- [x] 静态资源目录检查
  - [x] `src/main/resources/static/` 目录内容已检查
  - [x] 静态资源文件清单已记录
  - [x] 无重要文件需要保留

- [x] 静态资源引用检查
  - [x] 无 Controller 引用静态资源
  - [x] 无 Service 引用静态资源
  - [x] 无配置文件引用静态资源
  - [x] 无模板文件引用静态资源

- [x] 静态资源配置移除
  - [x] Spring MVC 静态资源映射已移除
  - [x] WebMvcConfigurer 配置已更新
  - [x] application.properties/yml 配置已更新

- [x] 静态资源删除
  - [x] `src/main/resources/static/` 目录已删除
  - [x] 相关配置代码已删除
  - [x] 应用启动正常

## 阶段二：编译错误修复
- [x] DTO 类修复
  - [x] 所有 DTO 类有 getter/setter 方法
  - [x] 所有 DTO 类有 builder() 方法
  - [x] Lombok 注解处理正常

- [x] Page 类型转换修复
  - [x] MyBatis-Plus Page 与 Spring Data Page 转换正确
  - [x] 分页查询功能正常
  - [x] 分页参数处理正确

- [x] Controller 方法签名修复
  - [x] 所有 Controller 方法调用正确
  - [x] 参数类型匹配
  - [x] 返回类型匹配

- [x] Mapper 方法补充
  - [x] incrementProductCount 方法已添加
  - [x] decrementProductCount 方法已添加
  - [x] 其他缺失方法已添加

- [x] 编译验证
  - [x] `mvn clean compile -DskipTests` 成功
  - [x] 无编译错误
  - [x] 无严重编译警告

## 阶段三：API 接口审计
- [ ] 用户端 API 审计
  - [ ] UserApiController 所有接口已审计
  - [ ] HTTP 方法正确（GET/POST/PUT/DELETE）
  - [ ] URL 路径符合 RESTful 规范
  - [ ] 请求参数验证完整
  - [ ] 响应格式统一
  - [ ] 错误处理完整
  - [ ] 状态码正确
  - [ ] 前端兼容

- [ ] 商家端 API 审计
  - [ ] MerchantApiController 所有接口已审计
  - [ ] MerchantController（MVC）所有接口已审计
  - [ ] HTTP 方法正确
  - [ ] URL 路径符合 RESTful 规范
  - [ ] 请求参数验证完整
  - [ ] 响应格式统一
  - [ ] 错误处理完整
  - [ ] 状态码正确
  - [ ] 前端兼容

- [ ] 平台端 API 审计
  - [ ] AdminApiController 所有接口已审计
  - [ ] HTTP 方法正确
  - [ ] URL 路径符合 RESTful 规范
  - [ ] 请求参数验证完整
  - [ ] 响应格式统一
  - [ ] 错误处理完整
  - [ ] 状态码正确
  - [ ] 前端兼容

- [ ] 公共 API 审计
  - [ ] AuthApiController 所有接口已审计
  - [ ] ProductController 所有接口已审计
  - [ ] ServiceController 所有接口已审计
  - [ ] 其他公共接口已审计
  - [ ] HTTP 方法正确
  - [ ] URL 路径符合 RESTful 规范
  - [ ] 请求参数验证完整
  - [ ] 响应格式统一
  - [ ] 错误处理完整
  - [ ] 状态码正确
  - [ ] 前端兼容

## 阶段四：API 问题修复
- [ ] RESTful 合规性修复
  - [ ] HTTP 方法不正确的接口已修复
  - [ ] URL 路径不规范的接口已修复
  - [ ] 资源命名不规范的接口已修复

- [ ] 请求处理修复
  - [ ] 参数验证不完整的接口已修复
  - [ ] 请求体处理不正确的接口已修复
  - [ ] 请求头处理不正确的接口已修复

- [ ] 响应处理修复
  - [ ] 响应格式不统一的接口已修复
  - [ ] 响应状态码不正确的接口已修复
  - [ ] 响应体不完整的接口已修复

- [ ] 错误处理修复
  - [ ] 异常处理不完整的接口已修复
  - [ ] 错误响应格式统一的接口已修复
  - [ ] 错误日志记录完整的接口已修复

## 阶段五：集成测试
- [ ] 单元测试
  - [ ] 所有单元测试通过
  - [ ] 测试覆盖率达标
  - [ ] 无跳过的测试

- [ ] 集成测试
  - [ ] 所有 API 接口测试通过
  - [ ] CRUD 操作测试通过
  - [ ] 复杂查询测试通过
  - [ ] 事务管理测试通过

- [ ] 前后端联调测试
  - [ ] 用户端前后端通信正常
  - [ ] 商家端前后端通信正常
  - [ ] 平台端前后端通信正常

## 阶段六：文档与验收
- [ ] 迁移文档
  - [ ] Hibernate 到 MyBatis-Plus 迁移步骤已记录
  - [ ] 移除的静态资源清单已记录
  - [ ] API 变更记录已记录
  - [ ] 测试验证结果已记录

- [ ] 项目文档更新
  - [ ] README 已更新
  - [ ] API 文档已更新
  - [ ] 部署文档已更新

- [ ] 最终验收
  - [ ] 项目编译成功
  - [ ] 所有测试通过
  - [ ] 前后端通信正常
  - [ ] 文档完整清晰

## 验收标准确认
- [ ] AC-1: 编译成功 - 项目编译通过，无错误
- [ ] AC-2: 静态资源移除 - 静态 HTML 页面托管配置已完全移除
- [ ] AC-3: API 审计完成 - 所有 API 接口符合规范
- [ ] AC-4: 前后端兼容 - 前后端通信正常
- [ ] AC-5: 文档完整 - 迁移文档完整清晰

## API 审计详细清单

### 用户端 API (UserApiController)
- [ ] GET /api/user/profile - 获取用户信息
- [ ] PUT /api/user/profile - 更新用户信息
- [ ] GET /api/user/pets - 获取宠物列表
- [ ] POST /api/user/pets - 添加宠物
- [ ] PUT /api/user/pets/{id} - 更新宠物
- [ ] DELETE /api/user/pets/{id} - 删除宠物
- [ ] GET /api/user/appointments - 获取预约列表
- [ ] POST /api/user/appointments - 创建预约
- [ ] PUT /api/user/appointments/{id} - 更新预约
- [ ] DELETE /api/user/appointments/{id} - 取消预约
- [ ] GET /api/user/orders - 获取订单列表
- [ ] GET /api/user/orders/{id} - 获取订单详情
- [ ] GET /api/user/addresses - 获取地址列表
- [ ] POST /api/user/addresses - 添加地址
- [ ] PUT /api/user/addresses/{id} - 更新地址
- [ ] DELETE /api/user/addresses/{id} - 删除地址
- [ ] GET /api/user/favorites - 获取收藏列表
- [ ] POST /api/user/favorites - 添加收藏
- [ ] DELETE /api/user/favorites/{id} - 取消收藏
- [ ] GET /api/user/reviews - 获取评价列表
- [ ] POST /api/user/reviews - 添加评价
- [ ] GET /api/user/notifications - 获取通知列表
- [ ] PUT /api/user/notifications/{id}/read - 标记通知已读

### 商家端 API (MerchantApiController)
- [ ] GET /api/merchant/profile - 获取商家信息
- [ ] PUT /api/merchant/profile - 更新商家信息
- [ ] GET /api/merchant/services - 获取服务列表
- [ ] POST /api/merchant/services - 添加服务
- [ ] PUT /api/merchant/services/{id} - 更新服务
- [ ] DELETE /api/merchant/services/{id} - 删除服务
- [ ] GET /api/merchant/products - 获取商品列表
- [ ] POST /api/merchant/products - 添加商品
- [ ] PUT /api/merchant/products/{id} - 更新商品
- [ ] DELETE /api/merchant/products/{id} - 删除商品
- [ ] GET /api/merchant/appointments - 获取预约列表
- [ ] PUT /api/merchant/appointments/{id}/status - 更新预约状态
- [ ] GET /api/merchant/orders - 获取订单列表
- [ ] PUT /api/merchant/orders/{id}/status - 更新订单状态
- [ ] GET /api/merchant/reviews - 获取评价列表
- [ ] POST /api/merchant/reviews/{id}/reply - 回复评价
- [ ] GET /api/merchant/stats - 获取统计数据
- [ ] GET /api/merchant/settings - 获取设置
- [ ] PUT /api/merchant/settings - 更新设置

### 平台端 API (AdminApiController)
- [ ] GET /api/admin/users - 获取用户列表
- [ ] GET /api/admin/users/{id} - 获取用户详情
- [ ] PUT /api/admin/users/{id}/status - 更新用户状态
- [ ] GET /api/admin/merchants - 获取商家列表
- [ ] GET /api/admin/merchants/{id} - 获取商家详情
- [ ] PUT /api/admin/merchants/{id}/status - 更新商家状态
- [ ] PUT /api/admin/merchants/{id}/audit - 审核商家
- [ ] GET /api/admin/services - 获取服务列表
- [ ] PUT /api/admin/services/{id}/status - 更新服务状态
- [ ] GET /api/admin/products - 获取商品列表
- [ ] PUT /api/admin/products/{id}/status - 更新商品状态
- [ ] GET /api/admin/orders - 获取订单列表
- [ ] GET /api/admin/reviews - 获取评价列表
- [ ] PUT /api/admin/reviews/{id}/status - 更新评价状态
- [ ] GET /api/admin/announcements - 获取公告列表
- [ ] POST /api/admin/announcements - 添加公告
- [ ] PUT /api/admin/announcements/{id} - 更新公告
- [ ] DELETE /api/admin/announcements/{id} - 删除公告
- [ ] GET /api/admin/stats - 获取统计数据
- [ ] GET /api/admin/settings - 获取系统设置
- [ ] PUT /api/admin/settings - 更新系统设置

### 公共 API
- [ ] POST /api/auth/login - 用户登录
- [ ] POST /api/auth/register - 用户注册
- [ ] POST /api/auth/logout - 用户登出
- [ ] POST /api/merchant/login - 商家登录
- [ ] POST /api/merchant/register - 商家注册
- [ ] GET /api/products - 获取商品列表
- [ ] GET /api/products/{id} - 获取商品详情
- [ ] GET /api/services - 获取服务列表
- [ ] GET /api/services/{id} - 获取服务详情
- [ ] GET /api/merchants - 获取商家列表
- [ ] GET /api/merchants/{id} - 获取商家详情

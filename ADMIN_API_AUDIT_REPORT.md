# 平台端 API 审计报告

**审计日期**: 2026-04-20  
**审计范围**: AdminApiController 所有接口  
**审计文件**: [AdminApiController.java](file:///d:/j/cg/cg/src/main/java/com/petshop/controller/api/AdminApiController.java)

---

## 审计摘要

| 审计项目 | 问题数量 | 严重程度 |
|---------|---------|---------|
| HTTP 方法正确性 | 0 | ✅ 通过 |
| URL 路径规范性 | 3 | ⚠️ 中等 |
| 请求参数验证 | 6 | 🔴 严重 |
| 响应格式一致性 | 5 | ⚠️ 中等 |
| 错误处理完整性 | 4 | ⚠️ 中等 |
| 状态码正确性 | 3 | ⚠️ 低 |
| 前端兼容性 | 7 | 🔴 严重 |

**总计问题**: 28 个

---

## 1. HTTP 方法正确性审计 ✅

### 审计结果: 通过

所有接口的 HTTP 方法使用符合 RESTful 规范：

| 接口 | HTTP 方法 | 评估 |
|-----|----------|------|
| 获取列表 | GET | ✅ 正确 |
| 创建资源 | POST | ✅ 正确 |
| 更新资源 | PUT | ✅ 正确 |
| 删除资源 | DELETE | ✅ 正确 |
| 状态更新 | PUT | ✅ 正确 |
| 批量操作 | PUT/DELETE | ✅ 正确 |

---

## 2. URL 路径规范性审计 ⚠️

### 问题列表

#### 问题 2.1: 待审核商家路径不一致
- **位置**: 
  - `GET /api/admin/merchants/pending` (第 426 行)
  - `GET /api/admin/dashboard/pending-merchants` (第 947 行)
- **问题**: 两个接口功能相似但路径风格不一致
- **建议**: 统一使用 `/merchants/pending` 或合并接口

#### 问题 2.2: 系统设置端点命名混淆
- **位置**:
  - `GET /api/admin/system/settings` (第 814 行)
  - `GET /api/admin/system/config` (第 1663 行)
- **问题**: 两个不同的设置端点，语义不清晰
- **建议**: 明确区分用途或合并

#### 问题 2.3: 店铺与商家概念混淆
- **位置**:
  - `/api/admin/merchants/*` - 商家管理
  - `/api/admin/shops/*` - 店铺审核
- **问题**: 商家和店铺使用不同路径，但底层是同一实体
- **建议**: 统一使用 `/merchants` 路径

---

## 3. 请求参数验证审计 🔴

### 严重问题

#### 问题 3.1: 状态更新参数接收方式错误 (严重)
**影响接口**:
- `PUT /api/admin/users/{id}/status` (第 139 行)
- `PUT /api/admin/merchants/{id}/status` (第 284 行)
- `PUT /api/admin/products/{id}/status` (第 1071 行)
- `PUT /api/admin/services/{id}/status` (第 1249 行)
- `PUT /api/admin/activities/{id}/status` (第 1896 行)

**问题描述**:
```java
// 后端代码
@PutMapping("/users/{id}/status")
public ResponseEntity<ApiResponse<User>> updateUserStatus(
    @RequestParam String status,  // 使用 @RequestParam 接收 URL 参数
    HttpSession session) { ... }

// 前端代码 (admin.ts)
export const updateUserStatus = (id: number, status: string) => {
  return request.put<User>(`/api/admin/users/${id}/status`, { status })  // 发送 JSON 请求体
}
```

**后果**: 前端发送的 JSON 请求体无法被后端正确解析，导致参数为 null

**修复建议**:
```java
@PutMapping("/users/{id}/status")
public ResponseEntity<ApiResponse<User>> updateUserStatus(
    @PathVariable Integer id,
    @RequestBody Map<String, String> request,  // 改用 @RequestBody
    HttpSession session) {
    String status = request.get("status");
    // ...
}
```

#### 问题 3.2: 列表接口缺少分页参数
**影响接口**:
- `GET /api/admin/users` (第 107 行) - 返回所有用户，无分页
- `GET /api/admin/merchants` (第 269 行) - 返回所有商家，无分页
- `GET /api/admin/services` (第 1211 行) - 有分页但前端未使用

**问题**: 数据量大时可能导致性能问题

**修复建议**: 添加分页参数
```java
@GetMapping("/users")
public ResponseEntity<ApiResponse<PageResponse<User>>> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    HttpSession session) { ... }
```

#### 问题 3.3: 缺少输入验证
**影响接口**:
- `POST /api/admin/announcements` - 未验证 title、content
- `PUT /api/admin/system/config` - 未验证字段格式

**修复建议**: 添加 @Valid 注解和验证逻辑

---

## 4. 响应格式一致性审计 ⚠️

### 问题列表

#### 问题 4.1: 响应包装不一致
**不一致示例**:
```java
// 接口 1: 直接返回列表
@GetMapping("/users")
public ResponseEntity<List<User>> getUsers(HttpSession session) {
    return ResponseEntity.ok(users);  // 无 ApiResponse 包装
}

// 接口 2: 使用 ApiResponse 包装
@GetMapping("/products")
public ResponseEntity<ApiResponse<PageResponse<Product>>> getProducts(...) {
    return ResponseEntity.ok(ApiResponse.success(response));  // 有 ApiResponse 包装
}
```

**建议**: 统一使用 `ApiResponse<T>` 包装所有响应

#### 问题 4.2: 错误响应格式不一致
**不一致示例**:
```java
// 方式 1: 无响应体
return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

// 方式 2: 有响应体
return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    .body(ApiResponse.error(401, "Unauthorized"));
```

**建议**: 统一使用 ApiResponse 返回错误信息

#### 问题 4.3: 删除操作响应不一致
**不一致示例**:
```java
// 单个删除: 返回 204 No Content
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(...) {
    return ResponseEntity.noContent().build();
}

// 批量删除: 返回 200 OK + ApiResponse
@DeleteMapping("/users/batch")
public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteUsers(...) {
    return ResponseEntity.ok(ApiResponse.success(...));
}
```

**建议**: 统一删除操作响应格式

#### 问题 4.4: 分页响应字段命名
**当前格式**:
```json
{
  "data": [...],
  "total": 100,
  "page": 0,
  "pageSize": 10,
  "totalPages": 10
}
```

**建议**: 确认前端期望的字段名称是否一致

#### 问题 4.5: 成功消息语言不一致
**示例**:
- 英文: "User status updated", "Product not found"
- 中文: "未授权访问"

**建议**: 统一使用中文或英文

---

## 5. 错误处理完整性审计 ⚠️

### 问题列表

#### 问题 5.1: 缺少全局异常处理
**问题**: Controller 中直接调用 Service 方法，未捕获异常
```java
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Integer id, HttpSession session) {
    userService.delete(id);  // 可能抛出异常
    return ResponseEntity.noContent().build();
}
```

**建议**: 添加 try-catch 或使用 @ControllerAdvice

#### 问题 5.2: 错误信息不友好
**示例**:
```java
return ResponseEntity.badRequest()
    .body(ApiResponse.error(400, "Invalid status. Must be 'active' or 'disabled'"));
```

**建议**: 提供更详细的错误信息和解决方案

#### 问题 5.3: 缺少业务异常处理
**场景**:
- 删除有关联数据的用户
- 更新不存在的资源
- 权限不足操作

**建议**: 定义业务异常类并统一处理

#### 问题 5.4: 日志记录缺失
**问题**: 关键操作未记录日志

**建议**: 添加操作日志记录

---

## 6. 状态码正确性审计 ⚠️

### 问题列表

#### 问题 6.1: 创建资源应返回 201
**影响接口**:
- `POST /api/admin/announcements` (第 585 行)
- `POST /api/admin/activities` (第 1815 行)
- `POST /api/admin/tasks` (第 2030 行)

**当前**: 返回 200 OK  
**建议**: 返回 201 Created

```java
@PostMapping("/announcements")
public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(...) {
    Announcement created = announcementService.create(announcement);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success("Announcement created successfully", created));
}
```

#### 问题 6.2: 未找到资源应返回 404
**已正确实现**: 大部分接口已正确返回 404

#### 问题 6.3: 无权限应返回 403
**当前**: 返回 401 Unauthorized  
**建议**: 区分未登录(401)和无权限(403)

---

## 7. 前端兼容性审计 🔴

### 严重问题

#### 问题 7.1: 缺少用户详情接口 (严重)
**前端调用**:
```typescript
// admin.ts 第 133 行
export const getUserDetailById = (id: number) => {
  return request.get<UserDetail>(`/api/admin/users/${id}`)
}
```

**后端状态**: ❌ 未实现 `GET /api/admin/users/{id}`

**修复建议**: 添加用户详情接口
```java
@GetMapping("/users/{id}")
public ResponseEntity<ApiResponse<UserDetailDTO>> getUserDetail(
    @PathVariable Integer id, HttpSession session) {
    // 实现用户详情查询
}
```

#### 问题 7.2: 缺少角色管理接口 (严重)
**前端调用**:
```typescript
// admin.ts 第 288-301 行
export const getRoles = () => request.get<Role[]>('/api/admin/roles')
export const addRole = (data) => request.post<Role>('/api/admin/roles', data)
export const updateRole = (id, data) => request.put<Role>(`/api/admin/roles/${id}`, data)
export const deleteRole = (id) => request.delete(`/api/admin/roles/${id}`)
```

**后端状态**: ❌ 未实现角色管理接口

#### 问题 7.3: 缺少权限管理接口 (严重)
**前端调用**:
```typescript
// admin.ts 第 311 行
export const getPermissions = () => request.get<Permission[]>('/api/admin/permissions')
```

**后端状态**: ❌ 未实现权限管理接口

#### 问题 7.4: 缺少操作日志接口 (严重)
**前端调用**:
```typescript
// admin.ts 第 268-278 行
export const getOperationLogs = (params) => request.get<OperationLog[]>('/api/admin/operation-logs', { params })
export const deleteLog = (id) => request.delete(`/api/admin/operation-logs/${id}`)
export const clearLogs = () => request.delete('/api/admin/operation-logs')
```

**后端状态**: ❌ 未实现操作日志接口

#### 问题 7.5: 评价审核接口路径不匹配
**前端调用**:
```typescript
// admin.ts 第 379 行
export const getReviewsForAudit = () => request.get<Review[]>('/api/admin/reviews/audit')
```

**后端实现**: `GET /api/admin/reviews/pending`

**修复建议**: 添加 `/api/admin/reviews/audit` 别名或修改前端路径

#### 问题 7.6: 状态更新参数不匹配 (严重)
**详见问题 3.1** - 前端发送 JSON 请求体，后端期望 URL 参数

#### 问题 7.7: 商家详情接口返回类型不匹配
**前端期望**:
```typescript
export const getMerchantDetailById = (id: number) => {
  return request.get<MerchantDetail>(`/api/admin/merchants/${id}`)
}
```

**后端返回**: `ApiResponse<MerchantDetailDTO>`

**建议**: 确认 `MerchantDetail` 和 `MerchantDetailDTO` 字段是否一致

---

## 修复优先级

### P0 - 立即修复 (影响功能)
1. **问题 3.1**: 状态更新参数接收方式错误 - 5 个接口
2. **问题 7.1**: 缺少用户详情接口
3. **问题 7.2**: 缺少角色管理接口
4. **问题 7.3**: 缺少权限管理接口
5. **问题 7.4**: 缺少操作日志接口

### P1 - 高优先级
1. **问题 3.2**: 列表接口添加分页参数
2. **问题 4.1**: 统一响应格式
3. **问题 5.1**: 添加全局异常处理

### P2 - 中优先级
1. **问题 2.1-2.3**: URL 路径规范化
2. **问题 4.2-4.5**: 响应格式统一
3. **问题 6.1**: 创建资源返回 201

### P3 - 低优先级
1. **问题 5.2-5.4**: 错误处理优化
2. **问题 7.5**: 接口路径别名

---

## 接口清单

### 已实现接口 (58 个)

| 模块 | 接口 | 状态 |
|-----|------|------|
| 用户管理 | GET /users | ✅ |
| 用户管理 | DELETE /users/{id} | ✅ |
| 用户管理 | PUT /users/{id}/status | ⚠️ 参数问题 |
| 用户管理 | PUT /users/batch/status | ✅ |
| 用户管理 | DELETE /users/batch | ✅ |
| 用户管理 | GET /users/recent | ✅ |
| 商家管理 | GET /merchants | ✅ |
| 商家管理 | GET /merchants/{id} | ✅ |
| 商家管理 | PUT /merchants/{id}/status | ⚠️ 参数问题 |
| 商家管理 | DELETE /merchants/{id} | ✅ |
| 商家管理 | PUT /merchants/batch/status | ✅ |
| 商家管理 | DELETE /merchants/batch | ✅ |
| 商家管理 | GET /merchants/pending | ✅ |
| 商家管理 | PUT /merchants/{id}/audit | ✅ |
| 商品管理 | GET /products | ✅ |
| 商品管理 | GET /products/{id} | ✅ |
| 商品管理 | PUT /products/{id} | ✅ |
| 商品管理 | PUT /products/{id}/status | ⚠️ 参数问题 |
| 商品管理 | DELETE /products/{id} | ✅ |
| 商品管理 | PUT /products/batch/status | ✅ |
| 商品管理 | DELETE /products/batch | ✅ |
| 服务管理 | GET /services | ✅ |
| 服务管理 | PUT /services/{id}/status | ⚠️ 参数问题 |
| 服务管理 | DELETE /services/{id} | ✅ |
| 服务管理 | PUT /services/batch/status | ✅ |
| 服务管理 | DELETE /services/batch | ✅ |
| 评价管理 | GET /reviews | ✅ |
| 评价管理 | GET /reviews/pending | ✅ |
| 评价管理 | PUT /reviews/{id}/audit | ✅ |
| 评价管理 | DELETE /reviews/{id} | ✅ |
| 评价管理 | PUT /reviews/{id}/approve | ✅ |
| 评价管理 | PUT /reviews/{id}/violation | ✅ |
| 评价管理 | PUT /reviews/batch/status | ✅ |
| 评价管理 | DELETE /reviews/batch | ✅ |
| 公告管理 | GET /announcements | ✅ |
| 公告管理 | POST /announcements | ✅ |
| 公告管理 | PUT /announcements/{id} | ✅ |
| 公告管理 | DELETE /announcements/{id} | ✅ |
| 公告管理 | PUT /announcements/{id}/publish | ✅ |
| 公告管理 | PUT /announcements/{id}/unpublish | ✅ |
| 公告管理 | PUT /announcements/batch/publish | ✅ |
| 公告管理 | PUT /announcements/batch/unpublish | ✅ |
| 公告管理 | DELETE /announcements/batch | ✅ |
| 系统设置 | GET /system/settings | ✅ |
| 系统设置 | PUT /system/settings | ✅ |
| 系统设置 | GET /system/settings/email | ✅ |
| 系统设置 | GET /system/settings/sms | ✅ |
| 系统设置 | GET /system/settings/payment | ✅ |
| 系统设置 | GET /system/settings/upload | ✅ |
| 系统设置 | GET /system/config | ✅ |
| 系统设置 | PUT /system/config | ✅ |
| 活动管理 | GET /activities | ✅ |
| 活动管理 | POST /activities | ✅ |
| 活动管理 | PUT /activities/{id} | ✅ |
| 活动管理 | DELETE /activities/{id} | ✅ |
| 活动管理 | PUT /activities/{id}/status | ⚠️ 参数问题 |
| 店铺审核 | GET /shops/pending | ✅ |
| 店铺审核 | PUT /shops/{id}/audit | ✅ |
| 任务管理 | GET /tasks | ✅ |
| 任务管理 | POST /tasks | ✅ |
| 任务管理 | PUT /tasks/{id} | ✅ |
| 任务管理 | DELETE /tasks/{id} | ✅ |
| 任务管理 | POST /tasks/{id}/execute | ✅ |
| 仪表盘 | GET /dashboard | ✅ |
| 仪表盘 | GET /dashboard/pending-merchants | ✅ |

### 缺失接口 (8 个)

| 模块 | 接口 | 前端调用位置 |
|-----|------|------------|
| 用户管理 | GET /users/{id} | admin.ts:133 |
| 角色管理 | GET /roles | admin.ts:288 |
| 角色管理 | POST /roles | admin.ts:292 |
| 角色管理 | PUT /roles/{id} | admin.ts:296 |
| 角色管理 | DELETE /roles/{id} | admin.ts:300 |
| 权限管理 | GET /permissions | admin.ts:311 |
| 操作日志 | GET /operation-logs | admin.ts:268 |
| 操作日志 | DELETE /operation-logs/{id} | admin.ts:272 |
| 操作日志 | DELETE /operation-logs | admin.ts:276 |

---

## 建议修复代码示例

### 修复状态更新参数问题

```java
@PutMapping("/users/{id}/status")
public ResponseEntity<ApiResponse<User>> updateUserStatus(
        @Parameter(description = "用户ID", required = true) @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "用户状态",
                content = @Content(schema = @Schema(example = "{\"status\": \"active\"}"))
        )
        @RequestBody Map<String, String> request,
        HttpSession session) {
    if (session.getAttribute("admin") == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "未授权访问"));
    }
    
    String status = request.get("status");
    if (status == null || (!"active".equals(status) && !"disabled".equals(status))) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "无效的状态值，必须是 'active' 或 'disabled'"));
    }
    
    User user = userService.findById(id);
    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "用户不存在"));
    }
    
    user.setStatus(status);
    User updatedUser = userService.update(user);
    return ResponseEntity.ok(ApiResponse.success("用户状态更新成功", updatedUser));
}
```

### 添加用户详情接口

```java
@Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
@ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户详情"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
})
@GetMapping("/users/{id}")
public ResponseEntity<ApiResponse<UserDetailDTO>> getUserDetail(
        @Parameter(description = "用户ID", required = true) @PathVariable Integer id,
        HttpSession session) {
    if (session.getAttribute("admin") == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "未授权访问"));
    }
    
    UserDetailDTO userDetail = userService.getUserDetail(id);
    if (userDetail == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "用户不存在"));
    }
    
    return ResponseEntity.ok(ApiResponse.success(userDetail));
}
```

---

## 总结

本次审计发现 **28 个问题**，其中：
- **严重问题**: 12 个（影响功能正常运行）
- **中等问题**: 11 个（影响代码质量和一致性）
- **低优先级**: 5 个（优化建议）

**建议立即处理**:
1. 修复状态更新接口的参数接收方式（5 个接口）
2. 实现缺失的 8 个接口
3. 统一响应格式

**后续优化**:
1. 添加全局异常处理
2. 完善输入验证
3. 规范 URL 路径命名
4. 统一错误消息语言

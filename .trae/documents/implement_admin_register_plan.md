# 实现平台端注册功能计划

## 问题分析
平台端注册失败，错误信息为 "No static resource api/auth/admin.register"。原因是后端缺少 `/api/auth/admin/register` 端点。

## 现状分析
- 用户注册：`/api/auth/register` - 已实现
- 商家注册：`/api/auth/merchant/register` - 已实现
- 管理员登录：`/api/auth/admin/login` - 已实现
- 管理员注册：`/api/auth/admin/register` - **缺失**

## Admin 实体结构
Admin 表只有以下字段：
- id
- username
- password
- createdAt
- updatedAt

## 前端请求格式
```json
{
  "email": "",
  "password": "123456",
  "phone": "17012340001",
  "username": ""
}
```

## 实施步骤

### 步骤1：创建 AdminRegisterRequest DTO
- 创建 `AdminRegisterRequest.java` 文件
- 包含字段：username、password、phone、email
- 添加 getter、setter 和 builder 方法
- 参考 `MerchantRegisterRequest.java` 的实现

### 步骤2：在 AuthService 中添加 adminRegister 方法
- 添加 `adminRegister` 方法
- 验证用户名是否已存在
- 创建 Admin 实体并保存
- 如果 username 为空，使用 phone 作为 username
- 返回注册成功消息

### 步骤3：在 AuthApiController 中添加注册端点
- 添加 `@PostMapping("/admin/register")` 端点
- 验证必填字段
- 调用 `authService.adminRegister` 方法
- 返回注册成功响应

### 步骤4：测试验证
- 使用 17012340001 和密码 123456 注册
- 验证注册成功后可以使用相同凭据登录

## 技术方案

### AdminRegisterRequest.java
```java
package com.petshop.dto;

public class AdminRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    
    // getter、setter、builder 方法
}
```

### AuthService.adminRegister
```java
@Transactional
public Map<String, String> adminRegister(AdminRegisterRequest request) {
    // 使用 phone 作为 username（如果 username 为空）
    String username = request.getUsername();
    if (username == null || username.isEmpty()) {
        username = request.getPhone();
    }
    
    // 验证用户名是否已存在
    Admin existingAdmin = adminRepository.selectByUsername(username);
    if (existingAdmin != null) {
        throw new BadRequestException("Username already exists");
    }
    
    // 创建 Admin 实体
    Admin admin = new Admin();
    admin.setUsername(username);
    admin.setPassword(passwordEncoder.encode(request.getPassword()));
    admin.setCreatedAt(LocalDateTime.now());
    admin.setUpdatedAt(LocalDateTime.now());
    
    adminRepository.insert(admin);
    
    Map<String, String> result = new HashMap<>();
    result.put("message", "Admin registration successful");
    return result;
}
```

### AuthApiController.adminRegister
```java
@Operation(summary = "管理员注册", description = "注册新管理员账号")
@PostMapping("/admin/register")
public ResponseEntity<ApiResponse<Map<String, String>>> adminRegister(@RequestBody AdminRegisterRequest request) {
    try {
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Password must be at least 6 characters"));
        }
        
        Map<String, String> response = authService.adminRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registration successful", response));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Admin registration failed: " + e.getMessage()));
    }
}
```

## 风险评估
- **低风险**：添加新功能，不影响现有功能
- **兼容性**：保持现有 API 结构不变
- **测试覆盖**：验证注册和登录流程

## 预期效果
- 平台端可以正常注册新管理员账号
- 注册后可以使用相同凭据登录
- 前端请求格式保持兼容
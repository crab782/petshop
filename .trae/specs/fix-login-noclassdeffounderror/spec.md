# 登录功能NoClassDefFoundError修复 Spec

## Why
前端登录请求时返回500错误：`NoClassDefFoundError: com/petshop/dto/LoginRequest$Builder`。这是运行时错误，表明服务器使用的是旧版本的编译代码，缺少 `LoginRequest.Builder` 内部类。源代码本身是正确的，但运行中的后端服务器未更新编译后的类文件。

## What Changes
- 确认 `LoginRequest.java` 源代码正确包含 `Builder` 内部类
- 确认 Lombok 配置正确
- 确认这不是代码问题，而是运行时/部署问题
- 提供正确的重启服务器指导

## Impact
- 受影响的功能：用户登录、商家登录、管理员登录
- 受影响的代码：`src/main/java/com/petshop/dto/LoginRequest.java`（源代码正确）

## 问题分析

### 错误信息
```
java.lang.NoClassDefFoundError: com/petshop/dto/LoginRequest$Builder
```

### 根本原因
这是典型的服务器未重新部署问题：
1. `LoginRequest.java` 源代码包含完整的 `Builder` 内部类（第51-78行）
2. 服务器运行的是旧版本的编译文件
3. 旧编译文件中没有 `Builder` 类

### 源代码验证
`LoginRequest.java` 第47-78行明确定义了 Builder：
```java
public static Builder builder() {
    return new Builder();
}

public static class Builder {
    // ... Builder implementation
}
```

## Solution

### 方案1：重启后端服务器（推荐）
停止当前运行的后端服务器，然后重新启动：
```powershell
# 停止当前服务器 (Ctrl+C)
# 重新编译并启动
mvn spring-boot:run
```

### 方案2：重新编译打包
```powershell
mvn clean compile
mvn spring-boot:run
```

### 方案3：清理并重新构建
```powershell
mvn clean package -DskipTests
java -jar target/pet-management-system-1.0-SNAPSHOT.jar
```

## Verification Steps
1. 重启后端服务器
2. 确认编译过程无错误
3. 再次尝试登录
4. 验证返回200状态码和正确的token

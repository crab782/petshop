# application.properties 转换为 application.yml 计划

## 目标
将 `e:\g\petshop\src\main\resources\application.properties` 转换为 `application.yml` 格式，删除旧的 properties 文件。

## 执行步骤

### 步骤 1: 读取现有的 application.properties 文件
- 确认所有配置项
- 了解配置结构和环境变量使用情况

### 步骤 2: 创建 application.yml 文件
将以下配置转换为 YAML 格式：

```yaml
# Database Configuration
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/cg?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&createDatabaseIfNotExist=true}
    username: ${SPRING_DATASOURCE_USERNAME:root}
    password: ${SPRING_DATASOURCE_PASSWORD:123456}
    driver-class-name: com.mysql.cj.jdbc.Driver

# JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect

# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /

# Thymeleaf Configuration
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML5
    encoding: UTF-8
    servlet:
      content-type: text/html

# JWT Configuration
jwt:
  secret: ${JWT_SECRET:petshop-default-secret-key-for-jwt-token-generation-and-validation-must-be-at-least-256-bits}

# CORS Configuration
cors:
  allowed-origins: http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS,PATCH
  allowed-headers: Authorization,Content-Type,X-Requested-With,Accept,Origin
  allow-credentials: true
  max-age: 3600

# Redis Configuration
  redis:
    host: ${SPRING_REDIS_HOST:localhost}
    port: ${SPRING_REDIS_PORT:6379}
    password: ${SPRING_REDIS_PASSWORD:}
    database: 0
    timeout: 30000
```

### 步骤 3: 删除 application.properties 文件
- 删除旧的 `application.properties` 文件

### 步骤 4: 验证配置
- 确保 Spring Boot 能够正确加载新的 YAML 配置文件

## 注意事项
1. YAML 格式对缩进敏感，需要使用空格缩进（不是 Tab）
2. 保持所有环境变量占位符 `${VAR:default}` 格式不变
3. Spring Boot 默认优先加载 `.properties` 文件，所以需要确保删除旧的 properties 文件
4. YAML 中下划线（如 `format_sql`）在 Spring Boot 中会自动转换为驼峰式（`format-sql`）
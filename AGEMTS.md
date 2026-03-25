# 宠物管理系统项目说明

## 项目概述

这是一个基于Spring Boot的宠物管理系统，包含用户、商家和管理员三种角色，提供宠物服务预约、管理等功能。

## 目录结构

```
c:\Users\Administrator\Desktop\spring boot\cg/
  - .mvn/
    - wrapper/
      - maven-wrapper.properties
  - src/
    - main/
      - java/
        - com/
          - petshop/
            - config/
              - DataInitializer.java        # 数据初始化
              - SecurityConfig.java          # 安全配置
              - SwaggerConfig.java           # Swagger文档配置
            - controller/
              - api/
                - AdminApiController.java    # 管理员API
                - MerchantApiController.java # 商家API
                - PublicApiController.java   # 公共API
                - UserApiController.java     # 用户API
              - AdminController.java         # 管理员控制器
              - AuthController.java          # 认证控制器（登录注册）
              - HomeController.java          # 首页控制器
              - MerchantController.java      # 商家控制器
              - UserController.java          # 用户控制器
            - entity/
              - Admin.java                   # 管理员实体
              - Appointment.java             # 预约实体
              - Merchant.java                # 商家实体
              - Pet.java                     # 宠物实体
              - Review.java                  # 评价实体
              - Service.java                 # 服务实体
              - User.java                    # 用户实体
            - repository/
              - AdminRepository.java         # 管理员数据访问
              - AppointmentRepository.java   # 预约数据访问
              - MerchantRepository.java      # 商家数据访问
              - PetRepository.java           # 宠物数据访问
              - ServiceRepository.java       # 服务数据访问
              - UserRepository.java          # 用户数据访问
            - service/
              - AdminService.java            # 管理员服务
              - AppointmentService.java      # 预约服务
              - MerchantService.java         # 商家服务
              - PetService.java              # 宠物服务
              - ServiceService.java          # 服务服务
              - UserService.java             # 用户服务
            - PetManagementSystemApplication.java # 应用入口
      - resources/
        - static/
          - admin-dashboard.html            # 管理员仪表盘
          - admin-login.html                # 管理员登录
          - index.html                      # 首页
          - login.html                      # 用户登录
          - merchant-add-service.html       # 商家添加服务
          - merchant-dashboard.html         # 商家仪表盘
          - register.html                   # 注册页面
          - service-boarding.html           # 寄养服务
          - service-food.html               # 食品服务
          - service-grooming.html           # 美容服务
          - service-health.html             # 健康服务
          - service-training.html           # 训练服务
          - service-transport.html          # 运输服务
          - user-add-pet.html               # 用户添加宠物
          - user-dashboard.html             # 用户仪表盘
        - application.properties            # 应用配置
    - test/
      - java/
        - com/
          - example/
            - cg/
              - CgApplicationTests.java     # 测试类
  - .gitattributes
  - .gitignore
  - database.sql                          # 数据库脚本
  - mvnw                                 # Maven包装器（Linux）
  - mvnw.cmd                              # Maven包装器（Windows）
  - pom.xml                               # Maven配置
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- Java 17
- Spring Data JPA
- Spring Security
- MySQL
- Swagger (SpringDoc OpenAPI)

### 前端
- HTML5
- Tailwind CSS
- Font Awesome
- JavaScript

## 数据库配置

```properties
# 数据库连接信息
spring.datasource.url=jdbc:mysql://localhost:3306/database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=2510105769
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 服务器配置

```properties
server.port=8080
server.servlet.context-path=/
```

## 主要功能模块

1. **用户模块**
   - 用户注册、登录
   - 个人信息管理
   - 宠物管理
   - 服务预约
   - 评价管理

2. **商家模块**
   - 商家注册、登录
   - 服务管理
   - 预约管理
   - 订单管理

3. **管理员模块**
   - 系统管理
   - 用户管理
   - 商家管理
   - 服务管理

4. **服务模块**
   - 寄养服务
   - 食品服务
   - 美容服务
   - 健康服务
   - 训练服务
   - 运输服务

## 如何运行项目

1. **环境要求**
   - JDK 17+
   - Maven 3.6+
   - MySQL 8.0+

2. **数据库准备**
   - 确保MySQL服务运行
   - 数据库会自动创建（`createDatabaseIfNotExist=true`）

3. **运行项目**
   ```bash
   # 使用Maven运行
   ./mvnw spring-boot:run
   
   # 或使用IDE运行PetManagementSystemApplication.java
   ```

4. **访问项目**
   - 首页：http://localhost:8080
   - 登录：http://localhost:8080/login.html
   - 注册：http://localhost:8080/register.html
   - Swagger文档：http://localhost:8080/swagger-ui.html

## API接口

项目提供RESTful API接口，可通过Swagger文档查看详细信息。

主要API路径：
- 公共API：/api/public/**
- 用户API：/api/user/**
- 商家API：/api/merchant/**
- 管理员API：/api/admin/**

## 注意事项

- 项目使用Spring Security进行权限控制
- 密码使用BCrypt加密存储
- 开发环境下会自动生成安全密码，生产环境需修改配置
- 数据库表结构会自动创建和更新（`ddl-auto=update`）
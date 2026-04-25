# 测试用例编写指南

## 概述

本指南详细介绍了如何在宠物服务平台项目中编写高质量的测试用例。遵循本指南可以确保测试用例清晰、可维护、易于理解。

## 测试用例结构

### 基本结构

一个完整的测试用例应包含以下部分：

1. **测试名称**：清晰描述测试目的
2. **测试准备（Arrange）**：准备测试数据和依赖
3. **测试执行（Act）**：执行被测试的代码
4. **测试断言（Assert）**：验证测试结果
5. **测试清理（Cleanup）**：清理测试资源（如需要）

### 示例

```rust
#[test]
fn test_user_registration_with_valid_data() {
    // Arrange - 准备测试数据
    let conn = establish_test_connection();
    let username = "testuser";
    let email = "test@example.com";
    let password = "Test123456";
    
    // Act - 执行测试
    let result = User::create(&conn, username, email, password);
    
    // Assert - 验证结果
    assert!(result.is_ok());
    let user = result.unwrap();
    assert_eq!(user.username, username);
    assert_eq!(user.email, email);
    
    // Cleanup - 清理资源（事务会自动回滚）
}
```

### 使用 Given-When-Then 模式

```rust
#[test]
fn test_user_login() {
    // Given - 给定条件
    let ctx = TestContext::new();
    let user = create_test_user(&ctx.db, "testuser", "password");
    
    // When - 当执行操作时
    let result = authenticate_user(&ctx.db, "testuser", "password");
    
    // Then - 那么期望结果
    assert!(result.is_ok());
    let token = result.unwrap();
    assert!(!token.is_empty());
}
```

## 测试用例命名规范

### 命名原则

1. **使用 test_ 前缀**：所有测试函数必须以 `test_` 开头
2. **描述性命名**：名称应清楚描述测试场景
3. **使用下划线分隔**：使用 snake_case 命名风格
4. **包含测试场景**：名称应包含被测试的场景

### 命名模式

```rust
// 模式 1: test_<功能>_<场景>
#[test]
fn test_user_registration_success() { }

#[test]
fn test_user_registration_with_duplicate_email() { }

// 模式 2: test_<功能>_<场景>_<期望结果>
#[test]
fn test_user_login_with_invalid_password_should_fail() { }

// 模式 3: should_<期望结果>_when_<条件>
#[test]
fn should_return_error_when_password_is_empty() { }

#[test]
fn should_create_user_when_data_is_valid() { }
```

### 命名示例

```rust
// ✅ 好的命名
#[test]
fn test_user_registration_with_valid_data() { }

#[test]
fn test_user_login_with_invalid_credentials() { }

#[test]
fn test_service_creation_with_missing_name() { }

#[test]
fn test_appointment_cancellation_when_status_is_pending() { }

// ❌ 不好的命名
#[test]
fn test_user() { }  // 太模糊

#[test]
fn test1() { }  // 无意义

#[test]
fn test_registration() { }  // 缺少场景描述
```

## 测试用例编写步骤

### 步骤 1: 确定测试目标

明确要测试的功能点和预期行为：

```rust
// 测试目标：验证用户注册功能
// 正常场景：使用有效数据注册成功
// 异常场景：使用无效数据注册失败
```

### 步骤 2: 准备测试环境

```rust
#[cfg(test)]
mod tests {
    use super::*;
    use diesel::connection::Connection;
    
    // 测试上下文
    struct TestContext {
        db: PgConnection,
    }
    
    impl TestContext {
        fn new() -> Self {
            let db = establish_test_connection();
            db.begin_test_transaction().unwrap();
            Self { db }
        }
    }
}
```

### 步骤 3: 准备测试数据

```rust
// 使用辅助函数创建测试数据
fn create_test_user(conn: &PgConnection) -> User {
    User::create(conn, "testuser", "test@example.com", "password").unwrap()
}

fn create_test_service(conn: &PgConnection, merchant_id: i32) -> Service {
    Service::create(conn, merchant_id, "Test Service", "Description", 100.0, 60).unwrap()
}
```

### 步骤 4: 编写测试用例

```rust
#[test]
fn test_appointment_creation_with_valid_data() {
    // 准备
    let ctx = TestContext::new();
    let user = create_test_user(&ctx.db);
    let merchant = create_test_merchant(&ctx.db);
    let service = create_test_service(&ctx.db, merchant.id);
    let pet = create_test_pet(&ctx.db, user.id);
    
    let appointment_data = NewAppointment {
        user_id: user.id,
        merchant_id: merchant.id,
        service_id: service.id,
        pet_id: pet.id,
        appointment_time: Utc::now() + Duration::days(1),
        notes: Some("Test appointment".to_string()),
    };
    
    // 执行
    let result = Appointment::create(&ctx.db, appointment_data);
    
    // 验证
    assert!(result.is_ok());
    let appointment = result.unwrap();
    assert_eq!(appointment.user_id, user.id);
    assert_eq!(appointment.status, AppointmentStatus::Pending);
}
```

### 步骤 5: 添加边界测试

```rust
#[test]
fn test_appointment_creation_with_past_date_should_fail() {
    let ctx = TestContext::new();
    let user = create_test_user(&ctx.db);
    let merchant = create_test_merchant(&ctx.db);
    let service = create_test_service(&ctx.db, merchant.id);
    let pet = create_test_pet(&ctx.db, user.id);
    
    let appointment_data = NewAppointment {
        user_id: user.id,
        merchant_id: merchant.id,
        service_id: service.id,
        pet_id: pet.id,
        appointment_time: Utc::now() - Duration::days(1),  // 过去的日期
        notes: None,
    };
    
    let result = Appointment::create(&ctx.db, appointment_data);
    
    assert!(result.is_err());
}
```

### 步骤 6: 添加异常测试

```rust
#[test]
fn test_appointment_creation_with_nonexistent_service_should_fail() {
    let ctx = TestContext::new();
    let user = create_test_user(&ctx.db);
    let merchant = create_test_merchant(&ctx.db);
    let pet = create_test_pet(&ctx.db, user.id);
    
    let appointment_data = NewAppointment {
        user_id: user.id,
        merchant_id: merchant.id,
        service_id: 9999,  // 不存在的服务
        pet_id: pet.id,
        appointment_time: Utc::now() + Duration::days(1),
        notes: None,
    };
    
    let result = Appointment::create(&ctx.db, appointment_data);
    
    assert!(result.is_err());
}
```

## 参数化测试

### 使用测试矩阵

```rust
#[test]
fn test_password_validation_with_various_inputs() {
    let test_cases = vec![
        // (password, expected_valid)
        ("Abc123!@", true),
        ("Password123", true),
        ("abc", false),           // 太短
        ("", false),              // 空
        ("12345678", false),      // 只有数字
        ("abcdefgh", false),      // 只有字母
        ("Abc1", false),          // 太短
    ];
    
    for (password, expected) in test_cases {
        let result = validate_password(password);
        assert_eq!(
            result.is_ok(),
            expected,
            "Password '{}' validation failed. Expected: {}, Got: {}",
            password,
            expected,
            result.is_ok()
        );
    }
}
```

### 使用宏进行参数化

```rust
macro_rules! param_test {
    ($name:ident, $input:expr, $expected:expr) => {
        #[test]
        fn $name() {
            let result = function_under_test($input);
            assert_eq!(result, $expected);
        }
    };
}

param_test!(test_add_1, 1, 2);
param_test!(test_add_2, 2, 4);
param_test!(test_add_3, 3, 6);
```

### 使用测试生成器

```rust
fn generate_user_test_cases() -> Vec<(String, String, bool)> {
    vec![
        ("user1".to_string(), "user1@example.com".to_string(), true),
        ("user2".to_string(), "invalid-email".to_string(), false),
        ("".to_string(), "user@example.com".to_string(), false),
        ("user3".to_string(), "".to_string(), false),
    ]
}

#[test]
fn test_user_creation_with_various_inputs() {
    let test_cases = generate_user_test_cases();
    
    for (username, email, expected_success) in test_cases {
        let result = User::validate(&username, &email);
        assert_eq!(
            result.is_ok(),
            expected_success,
            "Failed for username: {}, email: {}",
            username,
            email
        );
    }
}
```

## 测试数据准备

### 使用 Fake 库生成测试数据

```rust
use fake::{Fake, Faker, faker::name::en::*};
use fake::faker::internet::en::*;

#[test]
fn test_with_generated_data() {
    // 生成随机用户名
    let username: String = Name().fake();
    
    // 生成随机邮箱
    let email: String = SafeEmail().fake();
    
    // 生成随机数字
    let age: u8 = (18..60).fake();
    
    // 生成完整的用户对象
    let user = NewUser {
        username,
        email,
        password: "Test123456".to_string(),
        age,
    };
    
    let result = User::create(user);
    assert!(result.is_ok());
}
```

### 使用测试数据构建器

```rust
pub struct UserBuilder {
    username: String,
    email: String,
    password: String,
    phone: Option<String>,
}

impl UserBuilder {
    pub fn new() -> Self {
        Self {
            username: "testuser".to_string(),
            email: "test@example.com".to_string(),
            password: "Test123456".to_string(),
            phone: None,
        }
    }
    
    pub fn with_username(mut self, username: &str) -> Self {
        self.username = username.to_string();
        self
    }
    
    pub fn with_email(mut self, email: &str) -> Self {
        self.email = email.to_string();
        self
    }
    
    pub fn with_phone(mut self, phone: &str) -> Self {
        self.phone = Some(phone.to_string());
        self
    }
    
    pub fn build(self) -> NewUser {
        NewUser {
            username: self.username,
            email: self.email,
            password: self.password,
            phone: self.phone,
        }
    }
}

// 使用构建器
#[test]
fn test_user_with_builder() {
    let user_data = UserBuilder::new()
        .with_username("customuser")
        .with_email("custom@example.com")
        .with_phone("13800138000")
        .build();
    
    let result = User::create(user_data);
    assert!(result.is_ok());
}
```

### 使用 Fixture

```rust
// 定义测试 Fixture
#[cfg(test)]
mod fixtures {
    use super::*;
    
    pub fn user_fixture() -> User {
        User {
            id: 1,
            username: "testuser".to_string(),
            email: "test@example.com".to_string(),
            created_at: Utc::now(),
        }
    }
    
    pub fn service_fixture(merchant_id: i32) -> Service {
        Service {
            id: 1,
            merchant_id,
            name: "Test Service".to_string(),
            description: "Test Description".to_string(),
            price: 100.0,
            duration: 60,
            status: ServiceStatus::Active,
            created_at: Utc::now(),
        }
    }
}

// 使用 Fixture
#[test]
fn test_with_fixtures() {
    let user = fixtures::user_fixture();
    let service = fixtures::service_fixture(1);
    
    // 使用 fixture 进行测试
    assert_eq!(user.username, "testuser");
    assert_eq!(service.price, 100.0);
}
```

## 断言使用方法

### 基本断言

```rust
#[test]
fn test_basic_assertions() {
    // 相等断言
    assert_eq!(2 + 2, 4);
    assert_ne!(2 + 2, 5);
    
    // 布尔断言
    assert!(true);
    assert!(!false);
    
    // 结果断言
    let result: Result<i32, &str> = Ok(42);
    assert!(result.is_ok());
    assert_eq!(result.unwrap(), 42);
    
    let error: Result<i32, &str> = Err("error");
    assert!(error.is_err());
}
```

### 带消息的断言

```rust
#[test]
fn test_assertions_with_messages() {
    let value = 42;
    
    assert_eq!(
        value,
        42,
        "Value should be 42, but got {}",
        value
    );
    
    assert!(
        value > 0,
        "Value should be positive, but got {}",
        value
    );
}
```

### 浮点数断言

```rust
#[test]
fn test_floating_point_assertions() {
    let a = 0.1 + 0.2;
    let b = 0.3;
    
    // 使用近似相等
    assert!((a - b).abs() < 1e-10);
    
    // 或使用 approx 库
    // approx::assert_relative_eq!(a, b, epsilon = 1e-10);
}
```

### 集合断言

```rust
#[test]
fn test_collection_assertions() {
    let vec = vec![1, 2, 3, 4, 5];
    
    // 检查包含
    assert!(vec.contains(&3));
    
    // 检查长度
    assert_eq!(vec.len(), 5);
    
    // 检查是否为空
    assert!(!vec.is_empty());
    
    // 检查第一个和最后一个元素
    assert_eq!(vec.first(), Some(&1));
    assert_eq!(vec.last(), Some(&5));
}
```

### 异常断言

```rust
#[test]
#[should_panic(expected = "division by zero")]
fn test_division_by_zero() {
    panic!("division by zero");
}

#[test]
fn test_error_handling() {
    let result = function_that_fails();
    
    assert!(result.is_err());
    
    let error = result.unwrap_err();
    assert_eq!(error.to_string(), "Expected error message");
}
```

详细的断言使用方法请参考 [断言使用指南](assertion_guide.md)。

## 测试标记使用

### 忽略测试

```rust
#[test]
#[ignore]
fn test_slow_operation() {
    // 这个测试默认会被跳过
    // 使用 cargo test -- --ignored 来运行
}

#[test]
#[ignore = "需要外部服务"]
fn test_external_service() {
    // 需要外部服务的测试
}
```

### 条件编译测试

```rust
#[test]
#[cfg(target_os = "linux")]
fn test_linux_specific() {
    // 只在 Linux 上运行
}

#[test]
#[cfg(feature = "integration")]
fn test_integration() {
    // 只在启用 integration feature 时运行
}
```

### 自定义测试属性

```rust
// 定义自定义测试属性
#[cfg(test)]
mod test_utils {
    pub fn requires_database() -> bool {
        std::env::var("DATABASE_URL").is_ok()
    }
}

#[test]
fn test_with_database() {
    if !test_utils::requires_database() {
        println!("Skipping test: DATABASE_URL not set");
        return;
    }
    
    // 测试代码
}
```

## 测试用例示例

### 示例 1: 用户注册测试

```rust
#[cfg(test)]
mod user_registration_tests {
    use super::*;
    
    #[test]
    fn test_user_registration_with_valid_data() {
        let ctx = TestContext::new();
        
        let user_data = NewUser {
            username: "testuser".to_string(),
            email: "test@example.com".to_string(),
            password: "Test123456".to_string(),
            phone: Some("13800138000".to_string()),
        };
        
        let result = User::create(&ctx.db, user_data);
        
        assert!(result.is_ok());
        let user = result.unwrap();
        assert_eq!(user.username, "testuser");
        assert_eq!(user.email, "test@example.com");
    }
    
    #[test]
    fn test_user_registration_with_duplicate_username() {
        let ctx = TestContext::new();
        
        // 创建第一个用户
        let user1 = NewUser {
            username: "testuser".to_string(),
            email: "user1@example.com".to_string(),
            password: "Test123456".to_string(),
            phone: None,
        };
        User::create(&ctx.db, user1).unwrap();
        
        // 尝试创建同名用户
        let user2 = NewUser {
            username: "testuser".to_string(),
            email: "user2@example.com".to_string(),
            password: "Test123456".to_string(),
            phone: None,
        };
        
        let result = User::create(&ctx.db, user2);
        assert!(result.is_err());
    }
    
    #[test]
    fn test_user_registration_with_invalid_email() {
        let ctx = TestContext::new();
        
        let user_data = NewUser {
            username: "testuser".to_string(),
            email: "invalid-email".to_string(),
            password: "Test123456".to_string(),
            phone: None,
        };
        
        let result = User::create(&ctx.db, user_data);
        assert!(result.is_err());
    }
}
```

### 示例 2: 服务预约测试

```rust
#[cfg(test)]
mod appointment_tests {
    use super::*;
    
    struct AppointmentTestContext {
        db: PgConnection,
        user: User,
        merchant: Merchant,
        service: Service,
        pet: Pet,
    }
    
    impl AppointmentTestContext {
        fn new() -> Self {
            let db = establish_test_connection();
            db.begin_test_transaction().unwrap();
            
            let user = create_test_user(&db);
            let merchant = create_test_merchant(&db);
            let service = create_test_service(&db, merchant.id);
            let pet = create_test_pet(&db, user.id);
            
            Self { db, user, merchant, service, pet }
        }
    }
    
    #[test]
    fn test_create_appointment_success() {
        let ctx = AppointmentTestContext::new();
        
        let appointment = NewAppointment {
            user_id: ctx.user.id,
            merchant_id: ctx.merchant.id,
            service_id: ctx.service.id,
            pet_id: ctx.pet.id,
            appointment_time: Utc::now() + Duration::days(1),
            notes: Some("Test appointment".to_string()),
        };
        
        let result = Appointment::create(&ctx.db, appointment);
        
        assert!(result.is_ok());
        let apt = result.unwrap();
        assert_eq!(apt.status, AppointmentStatus::Pending);
    }
    
    #[test]
    fn test_cancel_appointment() {
        let ctx = AppointmentTestContext::new();
        
        // 创建预约
        let appointment = create_test_appointment(
            &ctx.db,
            ctx.user.id,
            ctx.merchant.id,
            ctx.service.id,
            ctx.pet.id,
        );
        
        // 取消预约
        let result = Appointment::cancel(&ctx.db, appointment.id, ctx.user.id);
        
        assert!(result.is_ok());
        let cancelled = result.unwrap();
        assert_eq!(cancelled.status, AppointmentStatus::Cancelled);
    }
    
    #[test]
    fn test_appointment_status_transition() {
        let ctx = AppointmentTestContext::new();
        
        let mut appointment = create_test_appointment(
            &ctx.db,
            ctx.user.id,
            ctx.merchant.id,
            ctx.service.id,
            ctx.pet.id,
        );
        
        // Pending -> Confirmed
        appointment = Appointment::confirm(&ctx.db, appointment.id).unwrap();
        assert_eq!(appointment.status, AppointmentStatus::Confirmed);
        
        // Confirmed -> Completed
        appointment = Appointment::complete(&ctx.db, appointment.id).unwrap();
        assert_eq!(appointment.status, AppointmentStatus::Completed);
    }
}
```

### 示例 3: API 端点测试

```rust
#[cfg(test)]
mod api_tests {
    use actix_web::test;
    use super::*;
    
    #[actix_rt::test]
    async fn test_user_registration_api() {
        let app = test::init_service(
            App::new()
                .data(create_test_pool())
                .configure(configure_routes)
        ).await;
        
        let req = test::TestRequest::post()
            .uri("/api/auth/register")
            .set_json(&json!({
                "username": "testuser",
                "email": "test@example.com",
                "password": "Test123456"
            }))
            .to_request();
        
        let resp = test::call_service(&app, req).await;
        
        assert!(resp.status().is_success());
        
        let body: ApiResponse<UserResponse> = test::read_body_json(resp).await;
        assert!(body.success);
        assert_eq!(body.data.username, "testuser");
    }
    
    #[actix_rt::test]
    async fn test_user_login_api() {
        let app = test::init_service(
            App::new()
                .data(create_test_pool())
                .configure(configure_routes)
        ).await;
        
        // 先注册用户
        let register_req = test::TestRequest::post()
            .uri("/api/auth/register")
            .set_json(&json!({
                "username": "testuser",
                "email": "test@example.com",
                "password": "Test123456"
            }))
            .to_request();
        test::call_service(&app, register_req).await;
        
        // 然后登录
        let login_req = test::TestRequest::post()
            .uri("/api/auth/login")
            .set_json(&json!({
                "username": "testuser",
                "password": "Test123456"
            }))
            .to_request();
        
        let resp = test::call_service(&app, login_req).await;
        
        assert!(resp.status().is_success());
        
        let body: ApiResponse<LoginResponse> = test::read_body_json(resp).await;
        assert!(body.success);
        assert!(!body.data.token.is_empty());
    }
    
    #[actix_rt::test]
    async fn test_protected_endpoint_without_token() {
        let app = test::init_service(
            App::new()
                .data(create_test_pool())
                .configure(configure_routes)
        ).await;
        
        let req = test::TestRequest::get()
            .uri("/api/user/profile")
            .to_request();
        
        let resp = test::call_service(&app, req).await;
        
        assert_eq!(resp.status(), StatusCode::UNAUTHORIZED);
    }
}
```

## 最佳实践

### 1. 保持测试简单

```rust
// ✅ 好的做法 - 简单直接
#[test]
fn test_user_age_calculation() {
    let user = User {
        birth_date: Utc::now() - Duration::days(365 * 25),
        ..Default::default()
    };
    
    assert_eq!(user.age(), 25);
}

// ❌ 不好的做法 - 过于复杂
#[test]
fn test_user_age_calculation_complex() {
    let mut mock_db = MockDatabase::new();
    mock_db.expect_query()
        .returning(|| { /* 复杂的 mock 逻辑 */ });
    
    let user_service = UserService::new(mock_db);
    let user = user_service.get_user(1).unwrap();
    
    // 太多间接层
    assert_eq!(user.age(), 25);
}
```

### 2. 一个测试一个断言

```rust
// ✅ 好的做法 - 一个测试验证一个行为
#[test]
fn test_user_email_validation() {
    let email = "test@example.com";
    assert!(validate_email(email));
}

#[test]
fn test_user_email_validation_with_invalid_format() {
    let email = "invalid-email";
    assert!(!validate_email(email));
}

// ❌ 不好的做法 - 一个测试多个断言
#[test]
fn test_user_validation() {
    assert!(validate_email("test@example.com"));
    assert!(!validate_email("invalid"));
    assert!(validate_username("testuser"));
    assert!(!validate_username(""));
}
```

### 3. 使用描述性的断言消息

```rust
#[test]
fn test_with_descriptive_messages() {
    let result = calculate_total(vec![10.0, 20.0, 30.0]);
    
    assert_eq!(
        result,
        60.0,
        "Total should be the sum of all items: 10 + 20 + 30 = 60"
    );
}
```

### 4. 测试边界条件

```rust
#[test]
fn test_boundary_conditions() {
    // 最小值
    assert_eq!(calculate_discount(0.0), 0.0);
    
    // 最大值
    assert_eq!(calculate_discount(10000.0), 1000.0);
    
    // 边界值
    assert_eq!(calculate_discount(100.0), 10.0);
    assert_eq!(calculate_discount(99.99), 9.999);
}
```

### 5. 测试错误路径

```rust
#[test]
fn test_error_paths() {
    // 测试空输入
    assert!(process_items(vec![]).is_err());
    
    // 测试无效输入
    assert!(process_items(vec![-1]).is_err());
    
    // 测试溢出
    assert!(process_items(vec![i32::MAX, 1]).is_err());
}
```

## 相关文档

- [断言使用指南](assertion_guide.md)
- [测试数据管理指南](data_management.md)
- [性能测试指南](performance_guide.md)

# 断言使用指南

## 概述

断言是测试用例的核心组成部分，用于验证代码的实际行为是否符合预期。本指南详细介绍了在宠物服务平台项目中如何使用各种断言方法。

## 状态码断言

### HTTP 状态码断言

在 API 测试中，验证 HTTP 状态码是最基本的断言。

```rust
use actix_web::test;
use actix_web::http::StatusCode;

#[actix_rt::test]
async fn test_successful_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言状态码为 200 OK
    assert_eq!(resp.status(), StatusCode::OK);
    
    // 或使用 is_success() 方法
    assert!(resp.status().is_success());
}

#[actix_rt::test]
async fn test_created_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::post()
        .uri("/api/users")
        .set_json(&json!({"username": "test"}))
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言状态码为 201 Created
    assert_eq!(resp.status(), StatusCode::CREATED);
    assert!(resp.status().is_success());
}

#[actix_rt::test]
async fn test_not_found_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users/99999")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言状态码为 404 Not Found
    assert_eq!(resp.status(), StatusCode::NOT_FOUND);
    assert!(resp.status().is_client_error());
}

#[actix_rt::test]
async fn test_unauthorized_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/user/profile")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言状态码为 401 Unauthorized
    assert_eq!(resp.status(), StatusCode::UNAUTHORIZED);
}

#[actix_rt::test]
async fn test_bad_request_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::post()
        .uri("/api/users")
        .set_json(&json!({"invalid": "data"}))
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言状态码为 400 Bad Request
    assert_eq!(resp.status(), StatusCode::BAD_REQUEST);
}
```

### 常用状态码断言方法

```rust
// 成功状态码 (2xx)
assert!(resp.status().is_success());          // 200-299
assert_eq!(resp.status(), StatusCode::OK);     // 200
assert_eq!(resp.status(), StatusCode::CREATED); // 201

// 客户端错误 (4xx)
assert!(resp.status().is_client_error());      // 400-499
assert_eq!(resp.status(), StatusCode::BAD_REQUEST);      // 400
assert_eq!(resp.status(), StatusCode::UNAUTHORIZED);     // 401
assert_eq!(resp.status(), StatusCode::FORBIDDEN);        // 403
assert_eq!(resp.status(), StatusCode::NOT_FOUND);        // 404

// 服务器错误 (5xx)
assert!(resp.status().is_server_error());      // 500-599
assert_eq!(resp.status(), StatusCode::INTERNAL_SERVER_ERROR); // 500

// 重定向 (3xx)
assert!(resp.status().is_redirection());       // 300-399
assert_eq!(resp.status(), StatusCode::FOUND);  // 302
```

## 响应体断言

### JSON 响应体断言

```rust
use serde_json::json;

#[actix_rt::test]
async fn test_json_response_body() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users/1")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 读取 JSON 响应体
    let body: UserResponse = test::read_body_json(resp).await;
    
    // 断言响应字段
    assert_eq!(body.id, 1);
    assert_eq!(body.username, "testuser");
    assert_eq!(body.email, "test@example.com");
}

#[actix_rt::test]
async fn test_json_response_with_serde_json() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users/1")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body = test::read_body(resp).await;
    let json: serde_json::Value = serde_json::from_slice(&body).unwrap();
    
    // 使用 serde_json 断言
    assert_eq!(json["id"], 1);
    assert_eq!(json["username"], "testuser");
    assert!(json["email"].is_string());
}

#[actix_rt::test]
async fn test_api_response_structure() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/services")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body: ApiResponse<Vec<Service>> = test::read_body_json(resp).await;
    
    // 断言 API 响应结构
    assert!(body.success);
    assert!(body.data.is_some());
    assert!(body.message.is_none() || body.message.is_some());
    
    let services = body.data.unwrap();
    assert!(!services.is_empty());
}
```

### 文本响应体断言

```rust
#[actix_rt::test]
async fn test_text_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/health")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body = test::read_body(resp).await;
    
    // 断言文本内容
    assert_eq!(body, "OK");
    
    // 或转换为字符串
    let text = std::str::from_utf8(&body).unwrap();
    assert!(text.contains("OK"));
}
```

### 空响应体断言

```rust
#[actix_rt::test]
async fn test_empty_response() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::delete()
        .uri("/api/users/1")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body = test::read_body(resp).await;
    
    // 断言响应体为空
    assert!(body.is_empty());
}
```

## 响应头断言

### Content-Type 断言

```rust
#[actix_rt::test]
async fn test_content_type_header() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言 Content-Type
    let content_type = resp
        .headers()
        .get("content-type")
        .unwrap()
        .to_str()
        .unwrap();
    
    assert!(content_type.contains("application/json"));
}
```

### 自定义响应头断言

```rust
#[actix_rt::test]
async fn test_custom_headers() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言自定义响应头
    assert!(resp.headers().contains_key("X-Request-Id"));
    assert!(resp.headers().contains_key("X-Rate-Limit"));
    
    // 获取并验证响应头值
    let request_id = resp
        .headers()
        .get("X-Request-Id")
        .unwrap()
        .to_str()
        .unwrap();
    
    assert!(!request_id.is_empty());
}
```

### CORS 头断言

```rust
#[actix_rt::test]
async fn test_cors_headers() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users")
        .insert_header(("Origin", "http://localhost:5173"))
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言 CORS 头
    assert!(resp.headers().contains_key("Access-Control-Allow-Origin"));
    
    let allow_origin = resp
        .headers()
        .get("Access-Control-Allow-Origin")
        .unwrap()
        .to_str()
        .unwrap();
    
    assert_eq!(allow_origin, "http://localhost:5173");
}
```

## JSON Schema 断言

### 使用 serde_json 进行结构验证

```rust
use serde_json::Value;

#[actix_rt::test]
async fn test_json_schema() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users/1")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body = test::read_body(resp).await;
    let json: Value = serde_json::from_slice(&body).unwrap();
    
    // 验证 JSON 结构
    assert!(json.is_object());
    
    let obj = json.as_object().unwrap();
    
    // 验证必需字段存在
    assert!(obj.contains_key("id"));
    assert!(obj.contains_key("username"));
    assert!(obj.contains_key("email"));
    
    // 验证字段类型
    assert!(obj["id"].is_number());
    assert!(obj["username"].is_string());
    assert!(obj["email"].is_string());
    
    // 验证可选字段
    if obj.contains_key("phone") {
        assert!(obj["phone"].is_string());
    }
}
```

### 使用 JSON Schema 验证库

```rust
// 添加依赖: jsonschema = "0.17"

use jsonschema::{JSONSchema, ValidationError};
use serde_json::json;

#[actix_rt::test]
async fn test_with_json_schema_validation() {
    let schema = json!({
        "type": "object",
        "required": ["id", "username", "email"],
        "properties": {
            "id": {"type": "integer"},
            "username": {"type": "string", "minLength": 1},
            "email": {"type": "string", "format": "email"},
            "phone": {"type": "string", "pattern": "^\\d{11}$"}
        }
    });
    
    let compiled = JSONSchema::compile(&schema).unwrap();
    
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users/1")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body = test::read_body(resp).await;
    let json: Value = serde_json::from_slice(&body).unwrap();
    
    // 验证 JSON Schema
    let result = compiled.validate(&json);
    
    if let Err(errors) = result {
        for error in errors {
            eprintln!("Validation error: {}", error);
        }
        panic!("JSON schema validation failed");
    }
}
```

### 数组响应验证

```rust
#[actix_rt::test]
async fn test_array_response_schema() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    let req = test::TestRequest::get()
        .uri("/api/services")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body: ApiResponse<Vec<Service>> = test::read_body_json(resp).await;
    
    assert!(body.success);
    assert!(body.data.is_some());
    
    let services = body.data.unwrap();
    
    // 验证数组不为空
    assert!(!services.is_empty());
    
    // 验证每个元素的必需字段
    for service in services {
        assert!(service.id > 0);
        assert!(!service.name.is_empty());
        assert!(service.price > 0.0);
        assert!(service.duration > 0);
    }
}
```

## 业务逻辑断言

### 数据一致性断言

```rust
#[test]
fn test_user_creation_data_consistency() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let username = "testuser";
    let email = "test@example.com";
    
    // 创建用户
    let user = User::create(&conn, username, email, "password").unwrap();
    
    // 断言数据一致性
    assert_eq!(user.username, username);
    assert_eq!(user.email, email);
    assert!(user.id > 0);
    
    // 从数据库重新查询验证
    let found = User::find_by_id(&conn, user.id).unwrap();
    assert_eq!(found.username, username);
    assert_eq!(found.email, email);
}
```

### 业务规则断言

```rust
#[test]
fn test_appointment_status_transition() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 创建测试数据
    let user = create_test_user(&conn);
    let merchant = create_test_merchant(&conn);
    let service = create_test_service(&conn, merchant.id);
    let pet = create_test_pet(&conn, user.id);
    
    // 创建预约
    let appointment = Appointment::create(&conn, NewAppointment {
        user_id: user.id,
        merchant_id: merchant.id,
        service_id: service.id,
        pet_id: pet.id,
        appointment_time: Utc::now() + Duration::days(1),
        notes: None,
    }).unwrap();
    
    // 断言初始状态
    assert_eq!(appointment.status, AppointmentStatus::Pending);
    
    // 确认预约
    let confirmed = Appointment::confirm(&conn, appointment.id).unwrap();
    assert_eq!(confirmed.status, AppointmentStatus::Confirmed);
    
    // 完成预约
    let completed = Appointment::complete(&conn, appointment.id).unwrap();
    assert_eq!(completed.status, AppointmentStatus::Completed);
    
    // 验证不能取消已完成的预约
    let result = Appointment::cancel(&conn, appointment.id, user.id);
    assert!(result.is_err());
}
```

### 计算逻辑断言

```rust
#[test]
fn test_order_total_calculation() {
    let items = vec![
        OrderItem { product_id: 1, quantity: 2, price: 100.0 },
        OrderItem { product_id: 2, quantity: 1, price: 50.0 },
        OrderItem { product_id: 3, quantity: 3, price: 25.0 },
    ];
    
    let total = calculate_order_total(&items);
    
    // 断言计算结果
    // 2 * 100 + 1 * 50 + 3 * 25 = 200 + 50 + 75 = 325
    assert_eq!(total, 325.0);
}

#[test]
fn test_discount_calculation() {
    let test_cases = vec![
        (100.0, 0.0),    // 低于折扣门槛
        (500.0, 25.0),   // 5% 折扣
        (1000.0, 100.0), // 10% 折扣
        (2000.0, 300.0), // 15% 折扣
    ];
    
    for (amount, expected_discount) in test_cases {
        let discount = calculate_discount(amount);
        assert_eq!(discount, expected_discount);
    }
}
```

### 权限验证断言

```rust
#[actix_rt::test]
async fn test_user_can_only_access_own_data() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    // 用户1 登录
    let token1 = login_user(&app, "user1", "password").await;
    
    // 用户2 登录
    let token2 = login_user(&app, "user2", "password").await;
    
    // 用户1 创建订单
    let req = test::TestRequest::post()
        .uri("/api/appointments")
        .insert_header(("Authorization", format!("Bearer {}", token1)))
        .set_json(&json!({
            "service_id": 1,
            "pet_id": 1,
            "appointment_time": "2024-12-01T10:00:00Z"
        }))
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    let body: ApiResponse<Appointment> = test::read_body_json(resp).await;
    let appointment_id = body.data.unwrap().id;
    
    // 用户2 尝试访问用户1的订单
    let req = test::TestRequest::get()
        .uri(&format!("/api/appointments/{}", appointment_id))
        .insert_header(("Authorization", format!("Bearer {}", token2)))
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    
    // 断言权限拒绝
    assert_eq!(resp.status(), StatusCode::FORBIDDEN);
}
```

## 断言链式调用

### 使用 assert_matches

```rust
// 添加依赖: assert_matches = "1.5"

use assert_matches::assert_matches;

#[test]
fn test_result_matching() {
    let result = some_function();
    
    assert_matches!(result, Ok(_));
    assert_matches!(result, Ok(value) if value > 0);
}

#[test]
fn test_option_matching() {
    let option = some_option_function();
    
    assert_matches!(option, Some(_));
    assert_matches!(option, Some(value) if value.starts_with("test"));
}

#[test]
fn test_error_matching() {
    let result = function_that_fails();
    
    assert_matches!(result, Err(Error::InvalidInput(_)));
    assert_matches!(
        result,
        Err(Error::DatabaseError(msg)) if msg.contains("duplicate")
    );
}
```

### 使用自定义断言宏

```rust
macro_rules! assert_ok {
    ($expr:expr) => {
        match $expr {
            Ok(value) => value,
            Err(e) => panic!("Expected Ok, got Err: {:?}", e),
        }
    };
    ($expr:expr, $msg:expr) => {
        match $expr {
            Ok(value) => value,
            Err(e) => panic!("{}: {:?}", $msg, e),
        }
    };
}

macro_rules! assert_err {
    ($expr:expr) => {
        match $expr {
            Err(e) => e,
            Ok(value) => panic!("Expected Err, got Ok: {:?}", value),
        }
    };
}

#[test]
fn test_with_custom_macros() {
    let result = some_function();
    
    // 使用自定义断言宏
    let value = assert_ok!(result, "Function should succeed");
    assert!(value > 0);
    
    let error_result = function_that_fails();
    let error = assert_err!(error_result);
    assert!(error.to_string().contains("expected"));
}
```

## 自定义断言

### 创建自定义断言函数

```rust
fn assert_valid_email(email: &str) {
    let email_regex = Regex::new(r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$").unwrap();
    
    assert!(
        email_regex.is_match(email),
        "Email '{}' is not valid",
        email
    );
}

fn assert_valid_phone(phone: &str) {
    let phone_regex = Regex::new(r"^\d{11}$").unwrap();
    
    assert!(
        phone_regex.is_match(phone),
        "Phone '{}' is not valid (expected 11 digits)",
        phone
    );
}

#[test]
fn test_user_validation() {
    let user = create_user();
    
    // 使用自定义断言
    assert_valid_email(&user.email);
    
    if let Some(phone) = &user.phone {
        assert_valid_phone(phone);
    }
}
```

### 创建响应断言助手

```rust
async fn assert_success_response(resp: ServiceResponse) -> Value {
    assert!(resp.status().is_success(), "Expected success status, got {}", resp.status());
    
    let body = test::read_body(resp).await;
    serde_json::from_slice(&body).expect("Failed to parse JSON response")
}

async fn assert_error_response(resp: ServiceResponse, expected_status: StatusCode) -> String {
    assert_eq!(resp.status(), expected_status);
    
    let body: ErrorResponse = test::read_body_json(resp).await;
    assert!(!body.success);
    body.message
}

#[actix_rt::test]
async fn test_with_response_helpers() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
    // 测试成功响应
    let req = test::TestRequest::get()
        .uri("/api/users/1")
        .to_request();
    let resp = test::call_service(&app, req).await;
    let json = assert_success_response(resp).await;
    assert!(json["id"].is_number());
    
    // 测试错误响应
    let req = test::TestRequest::get()
        .uri("/api/users/99999")
        .to_request();
    let resp = test::call_service(&app, req).await;
    let error_msg = assert_error_response(resp, StatusCode::NOT_FOUND).await;
    assert!(error_msg.contains("not found"));
}
```

### 创建数据库断言助手

```rust
fn assert_user_exists(conn: &PgConnection, user_id: i32) -> User {
    User::find_by_id(conn, user_id)
        .expect(&format!("User with id {} should exist", user_id))
}

fn assert_user_not_exists(conn: &PgConnection, user_id: i32) {
    assert!(
        User::find_by_id(conn, user_id).is_none(),
        "User with id {} should not exist",
        user_id
    );
}

fn assert_count(conn: &PgConnection, table: &str, expected: i64) {
    let count: i64 = diesel::select(diesel::dsl::count_star())
        .from(table)
        .get_result(conn)
        .unwrap();
    
    assert_eq!(
        count,
        expected,
        "Expected {} rows in {}, got {}",
        expected,
        table,
        count
    );
}

#[test]
fn test_with_database_helpers() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 创建用户
    let user = User::create(&conn, "testuser", "test@example.com", "password").unwrap();
    
    // 使用数据库断言
    assert_user_exists(&conn, user.id);
    
    // 删除用户
    User::delete(&conn, user.id).unwrap();
    
    assert_user_not_exists(&conn, user.id);
    
    // 验证数量
    assert_count(&conn, "users", 0);
}
```

## 断言最佳实践

### 1. 使用描述性断言消息

```rust
#[test]
fn test_with_descriptive_messages() {
    let result = calculate_total(vec![10.0, 20.0, 30.0]);
    
    // ✅ 好的做法 - 提供清晰的断言消息
    assert_eq!(
        result,
        60.0,
        "Total should be sum of all items: 10 + 20 + 30 = 60, got {}",
        result
    );
    
    // ❌ 不好的做法 - 没有消息
    assert_eq!(result, 60.0);
}
```

### 2. 断言具体的值而不是布尔值

```rust
#[test]
fn test_specific_values() {
    let user = get_user();
    
    // ✅ 好的做法 - 断言具体值
    assert_eq!(user.username, "testuser");
    assert_eq!(user.email, "test@example.com");
    
    // ❌ 不好的做法 - 断言布尔值
    assert!(user.username == "testuser");
    assert!(user.email == "test@example.com");
}
```

### 3. 使用 assert_eq! 而不是 assert!

```rust
#[test]
fn test_equality_assertions() {
    let value = 42;
    
    // ✅ 好的做法 - 使用 assert_eq!
    assert_eq!(value, 42);
    
    // ❌ 不好的做法 - 使用 assert!
    assert!(value == 42);
}
```

### 4. 避免在断言中进行复杂计算

```rust
#[test]
fn test_avoid_complex_calculations() {
    let items = vec![10.0, 20.0, 30.0];
    
    // ✅ 好的做法 - 先计算，再断言
    let total = calculate_total(&items);
    assert_eq!(total, 60.0);
    
    // ❌ 不好的做法 - 在断言中计算
    assert_eq!(calculate_total(&items), 60.0);
}
```

### 5. 使用近似相等比较浮点数

```rust
#[test]
fn test_floating_point_comparison() {
    let result = 0.1 + 0.2;
    let expected = 0.3;
    
    // ✅ 好的做法 - 使用近似相等
    assert!((result - expected).abs() < 1e-10);
    
    // ❌ 不好的做法 - 直接比较浮点数
    assert_eq!(result, expected);
}
```

### 6. 断言错误类型和消息

```rust
#[test]
fn test_error_details() {
    let result = function_that_fails();
    
    // ✅ 好的做法 - 断言错误类型和消息
    assert!(result.is_err());
    let error = result.unwrap_err();
    assert_eq!(error.kind(), ErrorKind::InvalidInput);
    assert!(error.to_string().contains("invalid email"));
    
    // ❌ 不好的做法 - 只断言是否错误
    assert!(result.is_err());
}
```

### 7. 使用 should_panic 进行异常测试

```rust
// ✅ 好的做法 - 明确预期的 panic 消息
#[test]
#[should_panic(expected = "division by zero")]
fn test_division_by_zero() {
    divide(1, 0);
}

// ❌ 不好的做法 - 没有指定 panic 消息
#[test]
#[should_panic]
fn test_division_by_zero_vague() {
    divide(1, 0);
}
```

### 8. 组合多个断言

```rust
#[test]
fn test_user_creation_comprehensive() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let user = User::create(&conn, "testuser", "test@example.com", "password").unwrap();
    
    // 组合多个断言验证用户创建
    assert!(user.id > 0, "User ID should be positive");
    assert_eq!(user.username, "testuser", "Username should match");
    assert_eq!(user.email, "test@example.com", "Email should match");
    assert!(user.created_at <= Utc::now(), "Created time should not be in the future");
    
    // 验证密码已加密
    assert_ne!(user.password, "password", "Password should be hashed");
    assert!(verify_password("password", &user.password).unwrap(), "Password should match");
}
```

## 相关文档

- [测试用例编写指南](test_guide.md)
- [测试数据管理指南](data_management.md)
- [性能测试指南](performance_guide.md)

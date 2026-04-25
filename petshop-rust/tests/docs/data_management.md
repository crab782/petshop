# 测试数据管理指南

## 概述

测试数据管理是测试框架的重要组成部分。良好的测试数据管理可以确保测试的可靠性、可重复性和独立性。本指南详细介绍了如何在宠物服务平台项目中管理和使用测试数据。

## 测试数据管理概述

### 为什么需要测试数据管理？

1. **测试独立性**：每个测试应该独立运行，不依赖其他测试的数据
2. **测试可重复性**：测试结果应该可重复，不受外部环境影响
3. **测试隔离性**：测试数据应该与生产数据隔离
4. **测试效率**：快速准备和清理测试数据
5. **测试覆盖率**：覆盖各种边界条件和异常情况

### 测试数据管理原则

1. **最小化原则**：只准备必要的测试数据
2. **隔离原则**：测试数据应该相互隔离
3. **可追溯原则**：测试数据应该可追溯和可理解
4. **自动化原则**：测试数据的准备和清理应该自动化
5. **一致性原则**：测试数据应该保持一致性

## 测试数据构建

### 使用 Fake 库生成测试数据

#### 基本用法

```rust
use fake::{Fake, Faker, faker::name::en::*};
use fake::faker::internet::en::*;
use fake::faker::phone_number::en::*;
use fake::faker::address::en::*;

#[test]
fn test_with_fake_data() {
    // 生成随机用户名
    let username: String = Name().fake();
    println!("Username: {}", username);
    
    // 生成随机邮箱
    let email: String = SafeEmail().fake();
    println!("Email: {}", email);
    
    // 生成随机电话号码
    let phone: String = PhoneNumber().fake();
    println!("Phone: {}", phone);
    
    // 生成随机地址
    let address: String = StreetName().fake();
    println!("Address: {}", address);
}
```

#### 生成复杂数据结构

```rust
use fake::{Fake, Faker};
use fake::faker::name::en::*;
use fake::faker::internet::en::*;
use fake::faker::lorem::en::*;

#[derive(Debug, Clone)]
pub struct User {
    pub id: i32,
    pub username: String,
    pub email: String,
    pub password: String,
    pub phone: Option<String>,
}

impl Default for User {
    fn default() -> Self {
        Self {
            id: 0,
            username: Name().fake(),
            email: SafeEmail().fake(),
            password: "Test123456".to_string(),
            phone: Some(PhoneNumber().fake()),
        }
    }
}

impl fake::Dummy<fake::Faker> for User {
    fn dummy_with_rng<R: rand::Rng + ?Sized>(_: &fake::Faker, rng: &mut R) -> Self {
        Self {
            id: (1..1000).fake_with_rng(rng),
            username: Name().fake_with_rng(rng),
            email: SafeEmail().fake_with_rng(rng),
            password: "Test123456".to_string(),
            phone: Some(PhoneNumber().fake_with_rng(rng)),
        }
    }
}

#[test]
fn test_with_complex_fake_data() {
    // 使用 Default trait
    let user1 = User::default();
    println!("User1: {:?}", user1);
    
    // 使用 Dummy trait
    let user2: User = Faker.fake();
    println!("User2: {:?}", user2);
    
    // 生成多个用户
    let users: Vec<User> = (1..=10).map(|_| Faker.fake()).collect();
    println!("Users: {:?}", users);
}
```

#### 生成业务数据

```rust
use fake::{Fake, Faker};
use chrono::{Utc, Duration};

#[derive(Debug, Clone)]
pub struct Service {
    pub id: i32,
    pub merchant_id: i32,
    pub name: String,
    pub description: String,
    pub price: f64,
    pub duration: i32,
}

impl fake::Dummy<fake::Faker> for Service {
    fn dummy_with_rng<R: rand::Rng + ?Sized>(_: &fake::Faker, rng: &mut R) -> Self {
        Self {
            id: (1..1000).fake_with_rng(rng),
            merchant_id: (1..100).fake_with_rng(rng),
            name: Sentence(3..5).fake_with_rng(rng),
            description: Paragraph(2..4).fake_with_rng(rng),
            price: (50.0..500.0).fake_with_rng(rng),
            duration: [30, 60, 90, 120].fake_with_rng(rng),
        }
    }
}

#[derive(Debug, Clone)]
pub struct Appointment {
    pub id: i32,
    pub user_id: i32,
    pub merchant_id: i32,
    pub service_id: i32,
    pub pet_id: i32,
    pub appointment_time: chrono::DateTime<Utc>,
    pub status: String,
}

impl fake::Dummy<fake::Faker> for Appointment {
    fn dummy_with_rng<R: rand::Rng + ?Sized>(_: &fake::Faker, rng: &mut R) -> Self {
        Self {
            id: (1..10000).fake_with_rng(rng),
            user_id: (1..100).fake_with_rng(rng),
            merchant_id: (1..50).fake_with_rng(rng),
            service_id: (1..200).fake_with_rng(rng),
            pet_id: (1..50).fake_with_rng(rng),
            appointment_time: Utc::now() + Duration::days((1..30).fake_with_rng(rng)),
            status: ["pending", "confirmed", "completed", "cancelled"]
                .fake_with_rng(rng),
        }
    }
}

#[test]
fn test_with_business_data() {
    let service: Service = Faker.fake();
    println!("Service: {:?}", service);
    
    let appointment: Appointment = Faker.fake();
    println!("Appointment: {:?}", appointment);
}
```

### 使用测试数据构建器

#### 基本构建器

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
    
    pub fn with_password(mut self, password: &str) -> Self {
        self.password = password.to_string();
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

#[test]
fn test_user_builder() {
    let user_data = UserBuilder::new()
        .with_username("customuser")
        .with_email("custom@example.com")
        .with_phone("13800138000")
        .build();
    
    assert_eq!(user_data.username, "customuser");
    assert_eq!(user_data.email, "custom@example.com");
    assert_eq!(user_data.phone, Some("13800138000".to_string()));
}
```

#### 高级构建器

```rust
pub struct AppointmentBuilder {
    user_id: i32,
    merchant_id: i32,
    service_id: i32,
    pet_id: i32,
    appointment_time: chrono::DateTime<Utc>,
    notes: Option<String>,
}

impl AppointmentBuilder {
    pub fn new() -> Self {
        Self {
            user_id: 1,
            merchant_id: 1,
            service_id: 1,
            pet_id: 1,
            appointment_time: Utc::now() + Duration::days(1),
            notes: None,
        }
    }
    
    pub fn for_user(mut self, user_id: i32) -> Self {
        self.user_id = user_id;
        self
    }
    
    pub fn with_merchant(mut self, merchant_id: i32) -> Self {
        self.merchant_id = merchant_id;
        self
    }
    
    pub fn with_service(mut self, service_id: i32) -> Self {
        self.service_id = service_id;
        self
    }
    
    pub fn with_pet(mut self, pet_id: i32) -> Self {
        self.pet_id = pet_id;
        self
    }
    
    pub fn at_time(mut self, time: chrono::DateTime<Utc>) -> Self {
        self.appointment_time = time;
        self
    }
    
    pub fn with_notes(mut self, notes: &str) -> Self {
        self.notes = Some(notes.to_string());
        self
    }
    
    pub fn build(self) -> NewAppointment {
        NewAppointment {
            user_id: self.user_id,
            merchant_id: self.merchant_id,
            service_id: self.service_id,
            pet_id: self.pet_id,
            appointment_time: self.appointment_time,
            notes: self.notes,
        }
    }
}

#[test]
fn test_appointment_builder() {
    let appointment_data = AppointmentBuilder::new()
        .for_user(10)
        .with_merchant(5)
        .with_service(20)
        .with_pet(3)
        .with_notes("Test appointment")
        .build();
    
    assert_eq!(appointment_data.user_id, 10);
    assert_eq!(appointment_data.merchant_id, 5);
    assert_eq!(appointment_data.service_id, 20);
}
```

## 测试数据准备

### 使用测试 Fixture

#### 定义 Fixture

```rust
#[cfg(test)]
mod fixtures {
    use super::*;
    
    pub fn user_fixture() -> User {
        User {
            id: 1,
            username: "testuser".to_string(),
            email: "test@example.com".to_string(),
            password: hash_password("Test123456").unwrap(),
            phone: Some("13800138000".to_string()),
            created_at: Utc::now(),
            updated_at: Utc::now(),
        }
    }
    
    pub fn merchant_fixture() -> Merchant {
        Merchant {
            id: 1,
            name: "Test Merchant".to_string(),
            contact_person: "Contact Person".to_string(),
            phone: "13900139000".to_string(),
            email: "merchant@example.com".to_string(),
            address: "Test Address".to_string(),
            status: MerchantStatus::Approved,
            created_at: Utc::now(),
            updated_at: Utc::now(),
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
            image: None,
            status: ServiceStatus::Active,
            created_at: Utc::now(),
            updated_at: Utc::now(),
        }
    }
    
    pub fn pet_fixture(user_id: i32) -> Pet {
        Pet {
            id: 1,
            user_id,
            name: "Test Pet".to_string(),
            pet_type: "dog".to_string(),
            breed: Some("Golden Retriever".to_string()),
            age: 3,
            gender: "male".to_string(),
            avatar: None,
            description: None,
            created_at: Utc::now(),
            updated_at: Utc::now(),
        }
    }
}
```

#### 使用 Fixture

```rust
#[test]
fn test_with_fixtures() {
    let user = fixtures::user_fixture();
    let merchant = fixtures::merchant_fixture();
    let service = fixtures::service_fixture(merchant.id);
    let pet = fixtures::pet_fixture(user.id);
    
    // 使用 fixture 进行测试
    assert_eq!(user.username, "testuser");
    assert_eq!(merchant.status, MerchantStatus::Approved);
    assert_eq!(service.price, 100.0);
    assert_eq!(pet.pet_type, "dog");
}
```

### 使用测试上下文

```rust
pub struct TestContext {
    pub db: PgConnection,
    pub users: Vec<User>,
    pub merchants: Vec<Merchant>,
    pub services: Vec<Service>,
}

impl TestContext {
    pub fn new() -> Self {
        let db = establish_test_connection();
        db.begin_test_transaction().unwrap();
        
        Self {
            db,
            users: Vec::new(),
            merchants: Vec::new(),
            services: Vec::new(),
        }
    }
    
    pub fn create_user(&mut self) -> User {
        let user = User::create(
            &self.db,
            &format!("user_{}", self.users.len()),
            &format!("user{}@example.com", self.users.len()),
            "Test123456",
        ).unwrap();
        
        self.users.push(user.clone());
        user
    }
    
    pub fn create_merchant(&mut self) -> Merchant {
        let merchant = Merchant::create(
            &self.db,
            &format!("Merchant {}", self.merchants.len()),
            &format!("Contact {}", self.merchants.len()),
            &format!("139{:08}", self.merchants.len()),
            &format!("merchant{}@example.com", self.merchants.len()),
            "Test Address",
        ).unwrap();
        
        self.merchants.push(merchant.clone());
        merchant
    }
    
    pub fn create_service(&mut self, merchant_id: i32) -> Service {
        let service = Service::create(
            &self.db,
            merchant_id,
            &format!("Service {}", self.services.len()),
            "Test Description",
            100.0,
            60,
        ).unwrap();
        
        self.services.push(service.clone());
        service
    }
}

impl Drop for TestContext {
    fn drop(&mut self) {
        // 事务会自动回滚
    }
}

#[test]
fn test_with_context() {
    let mut ctx = TestContext::new();
    
    let user = ctx.create_user();
    let merchant = ctx.create_merchant();
    let service = ctx.create_service(merchant.id);
    
    // 测试代码
    assert!(user.id > 0);
    assert!(merchant.id > 0);
    assert!(service.id > 0);
}
```

### 使用测试数据工厂

```rust
pub struct TestDataFactory;

impl TestDataFactory {
    pub fn create_user_with_appointment(conn: &PgConnection) -> (User, Merchant, Service, Pet, Appointment) {
        let user = Self::create_user(conn);
        let merchant = Self::create_merchant(conn);
        let service = Self::create_service(conn, merchant.id);
        let pet = Self::create_pet(conn, user.id);
        let appointment = Self::create_appointment(
            conn,
            user.id,
            merchant.id,
            service.id,
            pet.id,
        );
        
        (user, merchant, service, pet, appointment)
    }
    
    pub fn create_user(conn: &PgConnection) -> User {
        User::create(conn, "testuser", "test@example.com", "Test123456").unwrap()
    }
    
    pub fn create_merchant(conn: &PgConnection) -> Merchant {
        Merchant::create(
            conn,
            "Test Merchant",
            "Contact Person",
            "13900139000",
            "merchant@example.com",
            "Test Address",
        ).unwrap()
    }
    
    pub fn create_service(conn: &PgConnection, merchant_id: i32) -> Service {
        Service::create(
            conn,
            merchant_id,
            "Test Service",
            "Test Description",
            100.0,
            60,
        ).unwrap()
    }
    
    pub fn create_pet(conn: &PgConnection, user_id: i32) -> Pet {
        Pet::create(
            conn,
            user_id,
            "Test Pet",
            "dog",
            Some("Golden Retriever"),
            3,
            "male",
        ).unwrap()
    }
    
    pub fn create_appointment(
        conn: &PgConnection,
        user_id: i32,
        merchant_id: i32,
        service_id: i32,
        pet_id: i32,
    ) -> Appointment {
        Appointment::create(
            conn,
            NewAppointment {
                user_id,
                merchant_id,
                service_id,
                pet_id,
                appointment_time: Utc::now() + Duration::days(1),
                notes: Some("Test appointment".to_string()),
            },
        ).unwrap()
    }
}

#[test]
fn test_with_factory() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let (user, merchant, service, pet, appointment) = 
        TestDataFactory::create_user_with_appointment(&conn);
    
    // 所有相关数据都已准备好
    assert!(user.id > 0);
    assert!(appointment.id > 0);
}
```

## 测试数据隔离

### 使用数据库事务

```rust
#[test]
fn test_with_transaction() {
    let conn = establish_test_connection();
    
    // 开始事务
    conn.begin_test_transaction().unwrap();
    
    // 创建测试数据
    let user = User::create(&conn, "testuser", "test@example.com", "password").unwrap();
    
    // 执行测试
    let found = User::find_by_id(&conn, user.id).unwrap();
    assert_eq!(found.username, "testuser");
    
    // 事务会在测试结束时自动回滚
}
```

### 使用测试数据库

```rust
// 在 .env.test 中配置测试数据库
// DATABASE_URL=postgres://user:password@localhost/petshop_test

#[cfg(test)]
fn establish_test_connection() -> PgConnection {
    dotenv::from_filename(".env.test").ok();
    let database_url = std::env::var("DATABASE_URL")
        .expect("DATABASE_URL must be set");
    
    PgConnection::establish(&database_url)
        .expect(&format!("Error connecting to {}", database_url))
}

#[test]
fn test_with_test_database() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 使用测试数据库进行测试
}
```

### 使用内存数据库

```rust
// 对于简单的测试，可以使用内存数据库
#[cfg(test)]
mod tests {
    use super::*;
    
    fn setup_in_memory_db() -> SqliteConnection {
        let conn = SqliteConnection::establish(":memory:").unwrap();
        run_migrations(&conn).unwrap();
        conn
    }
    
    #[test]
    fn test_with_in_memory_db() {
        let conn = setup_in_memory_db();
        
        // 使用内存数据库进行测试
    }
}
```

## 测试数据清理

### 自动清理

```rust
pub struct TestDataManager {
    conn: PgConnection,
    created_users: Vec<i32>,
    created_merchants: Vec<i32>,
    created_services: Vec<i32>,
}

impl TestDataManager {
    pub fn new(conn: PgConnection) -> Self {
        Self {
            conn,
            created_users: Vec::new(),
            created_merchants: Vec::new(),
            created_services: Vec::new(),
        }
    }
    
    pub fn create_user(&mut self, username: &str, email: &str) -> User {
        let user = User::create(&self.conn, username, email, "password").unwrap();
        self.created_users.push(user.id);
        user
    }
    
    pub fn create_merchant(&mut self, name: &str) -> Merchant {
        let merchant = Merchant::create(
            &self.conn,
            name,
            "Contact",
            "13900139000",
            "merchant@example.com",
            "Address",
        ).unwrap();
        self.created_merchants.push(merchant.id);
        merchant
    }
}

impl Drop for TestDataManager {
    fn drop(&mut self) {
        // 清理创建的数据
        for user_id in &self.created_users {
            let _ = User::delete(&self.conn, *user_id);
        }
        
        for merchant_id in &self.created_merchants {
            let _ = Merchant::delete(&self.conn, *merchant_id);
        }
        
        for service_id in &self.created_services {
            let _ = Service::delete(&self.conn, *service_id);
        }
    }
}

#[test]
fn test_with_auto_cleanup() {
    let conn = establish_test_connection();
    let mut manager = TestDataManager::new(conn);
    
    let user = manager.create_user("testuser", "test@example.com");
    let merchant = manager.create_merchant("Test Merchant");
    
    // 测试代码
    
    // manager 会在测试结束时自动清理数据
}
```

### 手动清理

```rust
#[test]
fn test_with_manual_cleanup() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 创建测试数据
    let user = User::create(&conn, "testuser", "test@example.com", "password").unwrap();
    
    // 测试代码
    
    // 手动清理（如果需要）
    User::delete(&conn, user.id).unwrap();
}
```

## 测试数据 Fixture

### 使用 Rust Fixture

```rust
// 定义测试 Fixture
#[cfg(test)]
mod test_fixtures {
    use super::*;
    
    pub fn setup_test_data(conn: &PgConnection) -> TestDataSetup {
        let user = User::create(conn, "testuser", "test@example.com", "password").unwrap();
        let merchant = Merchant::create(
            conn,
            "Test Merchant",
            "Contact",
            "13900139000",
            "merchant@example.com",
            "Address",
        ).unwrap();
        let service = Service::create(
            conn,
            merchant.id,
            "Test Service",
            "Description",
            100.0,
            60,
        ).unwrap();
        
        TestDataSetup {
            user,
            merchant,
            service,
        }
    }
    
    pub struct TestDataSetup {
        pub user: User,
        pub merchant: Merchant,
        pub service: Service,
    }
}

// 使用 Fixture
#[test]
fn test_with_fixture() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let setup = test_fixtures::setup_test_data(&conn);
    
    // 使用 setup.user, setup.merchant, setup.service
}
```

### 使用 SQL Fixture

```sql
-- tests/fixtures/users.sql
INSERT INTO users (username, email, password) VALUES
    ('user1', 'user1@example.com', '$2b$12$...'),
    ('user2', 'user2@example.com', '$2b$12$...'),
    ('user3', 'user3@example.com', '$2b$12$...');

-- tests/fixtures/merchants.sql
INSERT INTO merchants (name, contact_person, phone, email, address, status) VALUES
    ('Merchant 1', 'Contact 1', '13900139001', 'merchant1@example.com', 'Address 1', 'approved'),
    ('Merchant 2', 'Contact 2', '13900139002', 'merchant2@example.com', 'Address 2', 'approved');

-- tests/fixtures/services.sql
INSERT INTO services (merchant_id, name, description, price, duration, status) VALUES
    (1, 'Service 1', 'Description 1', 100.0, 60, 'active'),
    (1, 'Service 2', 'Description 2', 150.0, 90, 'active'),
    (2, 'Service 3', 'Description 3', 200.0, 120, 'active');
```

```rust
// 加载 SQL Fixture
fn load_sql_fixture(conn: &PgConnection, fixture_path: &str) {
    let sql = std::fs::read_to_string(fixture_path)
        .expect(&format!("Failed to read fixture file: {}", fixture_path));
    
    diesel::sql_query(sql)
        .execute(conn)
        .expect(&format!("Failed to execute fixture: {}", fixture_path));
}

#[test]
fn test_with_sql_fixture() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 加载 SQL Fixture
    load_sql_fixture(&conn, "tests/fixtures/users.sql");
    load_sql_fixture(&conn, "tests/fixtures/merchants.sql");
    load_sql_fixture(&conn, "tests/fixtures/services.sql");
    
    // 使用 fixture 数据进行测试
}
```

### 使用 JSON Fixture

```json
// tests/fixtures/users.json
[
    {
        "username": "user1",
        "email": "user1@example.com",
        "password": "password1"
    },
    {
        "username": "user2",
        "email": "user2@example.com",
        "password": "password2"
    }
]
```

```rust
// 加载 JSON Fixture
fn load_json_fixture<T: serde::de::DeserializeOwned>(fixture_path: &str) -> Vec<T> {
    let json = std::fs::read_to_string(fixture_path)
        .expect(&format!("Failed to read fixture file: {}", fixture_path));
    
    serde_json::from_str(&json)
        .expect(&format!("Failed to parse fixture JSON: {}", fixture_path))
}

#[test]
fn test_with_json_fixture() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 加载 JSON Fixture
    let users: Vec<NewUser> = load_json_fixture("tests/fixtures/users.json");
    
    // 创建测试数据
    for user_data in users {
        User::create(&conn, &user_data.username, &user_data.email, &user_data.password).unwrap();
    }
    
    // 使用 fixture 数据进行测试
}
```

## 测试数据最佳实践

### 1. 使用有意义的测试数据

```rust
// ✅ 好的做法 - 使用有意义的测试数据
let user = UserBuilder::new()
    .with_username("active_user")
    .with_email("active@example.com")
    .build();

// ❌ 不好的做法 - 使用无意义的数据
let user = UserBuilder::new()
    .with_username("user123")
    .with_email("test@test.com")
    .build();
```

### 2. 保持测试数据最小化

```rust
// ✅ 好的做法 - 只准备必要的数据
let user = create_test_user(&conn);
let appointment = create_test_appointment(&conn, user.id);

// ❌ 不好的做法 - 准备过多不必要的数据
let user = create_test_user(&conn);
let merchant = create_test_merchant(&conn);
let service = create_test_service(&conn, merchant.id);
let pet = create_test_pet(&conn, user.id);
let appointment = create_test_appointment(&conn, user.id, merchant.id, service.id, pet.id);
```

### 3. 使用一致的测试数据

```rust
// ✅ 好的做法 - 使用一致的测试数据
const TEST_PASSWORD: &str = "Test123456";

fn create_test_user(conn: &PgConnection) -> User {
    User::create(conn, "testuser", "test@example.com", TEST_PASSWORD).unwrap()
}

// ❌ 不好的做法 - 使用不一致的测试数据
fn create_test_user1(conn: &PgConnection) -> User {
    User::create(conn, "testuser1", "test1@example.com", "password1").unwrap()
}

fn create_test_user2(conn: &PgConnection) -> User {
    User::create(conn, "testuser2", "test2@example.com", "password2").unwrap()
}
```

### 4. 避免硬编码 ID

```rust
// ✅ 好的做法 - 使用动态 ID
let user = create_test_user(&conn);
let appointment = create_test_appointment(&conn, user.id);

// ❌ 不好的做法 - 硬编码 ID
let appointment = Appointment::find_by_id(&conn, 1).unwrap();
```

### 5. 使用测试数据清理策略

```rust
// ✅ 好的做法 - 使用事务自动回滚
#[test]
fn test_with_transaction_rollback() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 测试代码
    
    // 事务会自动回滚
}

// ✅ 好的做法 - 使用 RAII 模式自动清理
#[test]
fn test_with_auto_cleanup() {
    let manager = TestDataManager::new(establish_test_connection());
    
    // 测试代码
    
    // manager 会在作用域结束时自动清理
}
```

### 6. 使用参数化测试数据

```rust
// ✅ 好的做法 - 使用参数化创建测试数据
fn create_test_user_with_status(conn: &PgConnection, status: UserStatus) -> User {
    let mut user = User::create(conn, "testuser", "test@example.com", "password").unwrap();
    user.status = status;
    user.save(conn).unwrap();
    user
}

#[test]
fn test_user_status() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let active_user = create_test_user_with_status(&conn, UserStatus::Active);
    let inactive_user = create_test_user_with_status(&conn, UserStatus::Inactive);
    
    // 测试不同状态的用户
}
```

### 7. 使用测试数据文档

```rust
// ✅ 好的做法 - 添加测试数据文档
/// 创建测试用户
/// 
/// # Arguments
/// * `conn` - 数据库连接
/// * `username` - 用户名（可选，默认为 "testuser"）
/// * `email` - 邮箱（可选，默认为 "test@example.com"）
/// 
/// # Returns
/// 创建的用户对象
fn create_test_user(
    conn: &PgConnection,
    username: Option<&str>,
    email: Option<&str>,
) -> User {
    User::create(
        conn,
        username.unwrap_or("testuser"),
        email.unwrap_or("test@example.com"),
        "Test123456",
    ).unwrap()
}
```

### 8. 避免测试数据依赖

```rust
// ✅ 好的做法 - 每个测试独立准备数据
#[test]
fn test_user_creation() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let user = create_test_user(&conn);
    // 测试用户创建
}

#[test]
fn test_user_deletion() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    let user = create_test_user(&conn);
    // 测试用户删除
}

// ❌ 不好的做法 - 测试之间有数据依赖
static mut TEST_USER: Option<User> = None;

#[test]
fn test_user_creation() {
    let conn = establish_test_connection();
    let user = create_test_user(&conn);
    unsafe { TEST_USER = Some(user); }
}

#[test]
fn test_user_deletion() {
    let conn = establish_test_connection();
    let user = unsafe { TEST_USER.clone().unwrap() };
    // 依赖上一个测试的数据
}
```

## 相关文档

- [测试用例编写指南](test_guide.md)
- [断言使用指南](assertion_guide.md)
- [性能测试指南](performance_guide.md)

# 宠物服务平台测试框架

## 概述

本测试框架为宠物服务平台（PetShop）提供全面的测试支持，包括单元测试、集成测试和性能测试。框架基于 Rust 的内置测试系统，结合 Actix-web 的测试工具，以及 Locust 性能测试框架，确保代码质量和系统性能。

### 测试框架特点

- **完整的测试覆盖**：支持单元测试、集成测试、API测试和性能测试
- **易于使用**：基于 Rust 标准测试框架，学习成本低
- **强大的断言库**：提供丰富的断言方法
- **Mock 支持**：使用 mockall 库进行依赖模拟
- **测试数据管理**：使用 fake 库生成测试数据
- **性能测试**：使用 Locust 进行负载测试和性能基准测试
- **详细的测试报告**：自动生成测试报告和性能分析

### 测试类型

1. **单元测试**：测试单个函数或模块的功能
2. **集成测试**：测试多个模块之间的交互
3. **API 测试**：测试 HTTP API 端点的正确性
4. **性能测试**：测试系统在高负载下的表现

## 目录结构

```
tests/
├── README.md                      # 本文档
├── docs/                          # 测试文档目录
│   ├── test_guide.md             # 测试用例编写指南
│   ├── assertion_guide.md        # 断言使用指南
│   ├── performance_guide.md      # 性能测试指南
│   └── data_management.md        # 测试数据管理指南
├── performance/                   # 性能测试目录
│   ├── __init__.py
│   ├── locustfile.py             # Locust 性能测试场景
│   ├── performance_config.py     # 性能测试配置
│   ├── performance_reporter.py   # 性能测试报告生成器
│   ├── requirements.txt          # Python 依赖
│   └── README.md                 # 性能测试文档
└── integration/                   # 集成测试目录（待创建）
    ├── api_tests.rs              # API 集成测试
    └── mod.rs                    # 模块定义
```

## 快速开始

### 环境准备

#### Rust 测试环境

确保已安装 Rust 工具链：

```bash
# 检查 Rust 版本
rustc --version
cargo --version

# 建议使用最新稳定版
rustup update stable
```

#### 性能测试环境

性能测试需要 Python 环境：

```bash
# 安装 Python 依赖
cd tests/performance
pip install -r requirements.txt
```

### 运行测试

#### 运行所有测试

```bash
# 运行所有单元测试和集成测试
cargo test

# 运行测试并显示输出
cargo test -- --nocapture

# 运行测试并显示所有输出（包括成功用例）
cargo test -- --nocapture --show-output
```

#### 运行特定测试

```bash
# 运行特定测试模块
cargo test api::auth

# 运行特定测试函数
cargo test test_user_login

# 运行匹配模式的测试
cargo test user

# 运行集成测试
cargo test --test '*'
```

#### 运行性能测试

```bash
# 进入性能测试目录
cd tests/performance

# 启动 Locust Web UI
locust -f locustfile.py

# 或使用命令行模式
locust -f locustfile.py --headless -u 100 -r 10 -t 5m --host http://localhost:8080
```

### 测试覆盖率

```bash
# 安装 tarpaulin
cargo install cargo-tarpaulin

# 生成覆盖率报告
cargo tarpaulin --out Html --out Lcov

# 查看报告
# 报告会生成在 tarpaulin-report.html
```

## 测试框架架构

### 单元测试架构

单元测试位于源代码文件中，使用 `#[cfg(test)]` 模块：

```rust
// src/api/auth.rs

#[cfg(test)]
mod tests {
    use super::*;
    
    #[test]
    fn test_password_hashing() {
        let password = "test123";
        let hash = hash_password(password).unwrap();
        assert!(verify_password(password, &hash).unwrap());
    }
}
```

### 集成测试架构

集成测试位于 `tests/` 目录，可以访问公共 API：

```rust
// tests/integration/api_tests.rs

use actix_web::test;
use petshop_rust::*;

#[actix_rt::test]
async fn test_user_registration() {
    let app = test::init_service(App::new().configure(configure_routes)).await;
    
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
}
```

### 性能测试架构

性能测试使用 Locust 框架，支持多种测试场景：

- **UserBehavior**：模拟用户行为
- **MerchantBehavior**：模拟商家行为
- **AdminBehavior**：模拟管理员行为
- **MixedUserBehavior**：混合用户场景

详见 [性能测试指南](docs/performance_guide.md)。

## 测试用例编写指南

### 基本原则

1. **独立性**：每个测试用例应该独立运行，不依赖其他测试
2. **可重复性**：测试结果应该可重复，不受外部环境影响
3. **清晰性**：测试用例应该清晰明了，易于理解
4. **完整性**：测试应该覆盖正常流程和异常流程

### 测试命名规范

```rust
// 使用 test_ 前缀
#[test]
fn test_user_login_success() { }

// 使用描述性名称
#[test]
fn test_should_return_error_when_password_is_invalid() { }

// 使用 should_panic 标记预期失败
#[test]
#[should_panic(expected = "Invalid password")]
fn test_should_panic_when_password_is_empty() { }
```

### 测试组织

```rust
#[cfg(test)]
mod tests {
    use super::*;
    
    // 测试辅助函数
    fn setup() -> TestContext {
        TestContext::new()
    }
    
    fn teardown(ctx: TestContext) {
        ctx.cleanup();
    }
    
    // 测试用例分组
    mod user_tests {
        use super::*;
        
        #[test]
        fn test_user_creation() { }
        
        #[test]
        fn test_user_authentication() { }
    }
    
    mod service_tests {
        use super::*;
        
        #[test]
        fn test_service_creation() { }
    }
}
```

详细的测试用例编写指南请参考 [测试用例编写指南](docs/test_guide.md)。

## 测试执行方法

### 本地测试

```bash
# 运行所有测试
cargo test

# 运行特定测试
cargo test test_name

# 并行运行测试
cargo test -- --test-threads=4

# 串行运行测试
cargo test -- --test-threads=1

# 运行被忽略的测试
cargo test -- --ignored
```

### CI/CD 集成

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: 123456
          MYSQL_DATABASE: petshop_test
        ports:
          - 3306:3306
    
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Rust
        uses: actions-rs/toolchain@v1
        with:
          toolchain: stable
          override: true
      
      - name: Run tests
        run: cargo test --all-features
      
      - name: Generate coverage
        run: cargo tarpaulin --out Xml
      
      - name: Upload coverage
        uses: codecov/codecov-action@v2
```

### 性能测试执行

```bash
# Web UI 模式
locust -f tests/performance/locustfile.py

# 无头模式
locust -f tests/performance/locustfile.py \
    --headless \
    -u 100 \
    -r 10 \
    -t 5m \
    --host http://localhost:8080

# 分布式模式
# 主节点
locust -f tests/performance/locustfile.py --master

# 工作节点
locust -f tests/performance/locustfile.py --worker --master-host=<master-ip>
```

## 测试报告查看

### 单元测试报告

```bash
# 使用 cargo-nextest 生成详细报告
cargo install cargo-nextest
cargo nextest run

# 使用 cargo-tarpaulin 生成覆盖率报告
cargo tarpaulin --out Html
```

### 性能测试报告

性能测试完成后，会自动生成：

- **HTML 报告**：`tests/performance/reports/performance_report_YYYYMMDD_HHMMSS.html`
- **JSON 报告**：`tests/performance/reports/performance_report_YYYYMMDD_HHMMSS.json`

报告内容包括：

- 性能指标概览
- 响应时间统计
- 吞吐量统计
- 错误率统计
- 性能瓶颈分析
- 优化建议

## 故障排查指南

### 常见问题

#### 1. 数据库连接失败

**症状**：测试运行时出现数据库连接错误

**解决方案**：
```bash
# 检查数据库是否运行
# 检查 .env 文件中的数据库配置
# 确保测试数据库存在

# 创建测试数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS petshop_test;"
```

#### 2. 测试超时

**症状**：测试运行时间过长或超时

**解决方案**：
```rust
// 增加测试超时时间
#[actix_rt::test]
async fn test_long_running_operation() {
    let result = tokio::time::timeout(
        Duration::from_secs(10),
        long_operation()
    ).await;
    
    assert!(result.is_ok());
}
```

#### 3. 测试数据污染

**症状**：测试之间相互影响

**解决方案**：
```rust
// 使用事务回滚
#[cfg(test)]
mod tests {
    use super::*;
    
    fn setup_test_db() -> PgConnection {
        let conn = establish_connection();
        conn.begin_test_transaction().unwrap();
        conn
    }
}
```

#### 4. 性能测试连接失败

**症状**：Locust 无法连接到服务器

**解决方案**：
```bash
# 确保服务器正在运行
cargo run

# 检查服务器地址和端口
curl http://localhost:8080/api/health

# 检查防火墙设置
```

### 调试技巧

```rust
// 使用 dbg! 宏调试
#[test]
fn test_debug() {
    let value = calculate();
    dbg!(&value);
    assert_eq!(value, expected);
}

// 使用 println! 打印调试信息
#[test]
fn test_with_print() {
    println!("Starting test...");
    // 测试代码
    println!("Test completed");
}

// 使用 log 日志
#[test]
fn test_with_logging() {
    env_logger::init();
    info!("Test started");
    // 测试代码
}
```

## 最佳实践

### 1. 测试隔离

```rust
// 使用测试上下文
struct TestContext {
    db: PgConnection,
    config: TestConfig,
}

impl TestContext {
    fn new() -> Self {
        let db = setup_test_db();
        let config = TestConfig::default();
        Self { db, config }
    }
}

impl Drop for TestContext {
    fn drop(&mut self) {
        cleanup_test_db(&self.db);
    }
}
```

### 2. 使用测试辅助函数

```rust
// 创建测试辅助函数
fn create_test_user(conn: &PgConnection) -> User {
    User::create(conn, "testuser", "test@example.com", "password").unwrap()
}

fn create_test_service(conn: &PgConnection, merchant_id: i32) -> Service {
    Service::create(conn, merchant_id, "Test Service", 100.0).unwrap()
}

// 在测试中使用
#[test]
fn test_appointment_creation() {
    let ctx = TestContext::new();
    let user = create_test_user(&ctx.db);
    let merchant = create_test_merchant(&ctx.db);
    let service = create_test_service(&ctx.db, merchant.id);
    
    // 测试预约创建
}
```

### 3. 使用 Mock 进行单元测试

```rust
use mockall::automock;

#[automock]
pub trait UserRepository {
    fn find_by_id(&self, id: i32) -> Result<User, Error>;
}

#[test]
fn test_with_mock() {
    let mut mock_repo = MockUserRepository::new();
    mock_repo
        .expect_find_by_id()
        .returning(|_| Ok(User::default()));
    
    let service = UserService::new(Box::new(mock_repo));
    assert!(service.get_user(1).is_ok());
}
```

### 4. 测试异步代码

```rust
#[actix_rt::test]
async fn test_async_operation() {
    let result = async_function().await;
    assert!(result.is_ok());
}

// 使用 tokio::test
#[tokio::test]
async fn test_tokio_async() {
    let result = tokio_function().await;
    assert!(result.is_ok());
}
```

### 5. 参数化测试

```rust
// 使用测试矩阵
#[test]
fn test_password_validation() {
    let test_cases = vec![
        ("Abc123", true),
        ("abc", false),
        ("", false),
        ("12345678", false),
    ];
    
    for (password, expected) in test_cases {
        let result = validate_password(password);
        assert_eq!(result, expected, "Failed for password: {}", password);
    }
}
```

### 6. 测试错误处理

```rust
#[test]
fn test_error_handling() {
    let result = function_that_may_fail();
    
    match result {
        Ok(_) => panic!("Expected error"),
        Err(e) => {
            assert_eq!(e.kind(), ErrorKind::InvalidInput);
        }
    }
}

// 或使用 assert_matches
#[test]
fn test_error_match() {
    let result = function_that_may_fail();
    assert!(result.is_err());
    assert_matches!(result.unwrap_err(), Error::InvalidInput(_));
}
```

## 常见问题解答

### Q1: 如何运行单个测试？

```bash
cargo test test_function_name
```

### Q2: 如何跳过某些测试？

```rust
#[test]
#[ignore]
fn test_slow_operation() {
    // 这个测试会被跳过
}
```

运行被忽略的测试：
```bash
cargo test -- --ignored
```

### Q3: 如何在测试中使用环境变量？

```rust
#[test]
fn test_with_env() {
    dotenv::dotenv().ok();
    let value = std::env::var("TEST_VALUE").unwrap();
    assert!(!value.is_empty());
}
```

### Q4: 如何测试私有函数？

```rust
// 在源文件中
#[cfg(test)]
mod tests {
    use super::*;
    
    #[test]
    fn test_private_function() {
        let result = private_function();
        assert!(result);
    }
}
```

### Q5: 如何处理测试中的异步错误？

```rust
#[actix_rt::test]
async fn test_async_error() {
    let result = async_function().await;
    assert!(result.is_err());
    assert_eq!(result.unwrap_err().to_string(), "Expected error");
}
```

### Q6: 如何生成测试数据？

```rust
use fake::{Fake, Faker};

#[test]
fn test_with_fake_data() {
    let username: String = Faker.fake();
    let email: String = Faker.fake();
    
    let user = User::new(username, email);
    assert!(user.is_valid());
}
```

### Q7: 如何测试数据库操作？

```rust
#[test]
fn test_database_operation() {
    let conn = establish_test_connection();
    conn.begin_test_transaction().unwrap();
    
    // 执行数据库操作
    let user = User::create(&conn, "test", "test@example.com").unwrap();
    
    // 验证结果
    let found = User::find_by_id(&conn, user.id).unwrap();
    assert_eq!(found.username, "test");
    
    // 事务会自动回滚
}
```

### Q8: 如何提高测试性能？

1. 使用并行测试：`cargo test -- --test-threads=4`
2. 使用测试缓存：`cargo test -- --cache`
3. 使用内存数据库进行测试
4. 避免在测试中进行 I/O 操作

### Q9: 如何测试 API 端点？

```rust
#[actix_rt::test]
async fn test_api_endpoint() {
    let app = test::init_service(
        App::new().configure(configure_routes)
    ).await;
    
    let req = test::TestRequest::get()
        .uri("/api/users/1")
        .to_request();
    
    let resp = test::call_service(&app, req).await;
    assert!(resp.status().is_success());
    
    let body: UserResponse = test::read_body_json(resp).await;
    assert_eq!(body.id, 1);
}
```

### Q10: 如何集成性能测试到 CI/CD？

```yaml
# .github/workflows/performance.yml
name: Performance Tests

on:
  schedule:
    - cron: '0 2 * * 0'  # 每周日凌晨2点

jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Python
        uses: actions/setup-python@v2
        with:
          python-version: '3.9'
      
      - name: Install dependencies
        run: |
          pip install -r tests/performance/requirements.txt
      
      - name: Run performance tests
        run: |
          locust -f tests/performance/locustfile.py \
            --headless \
            -u 100 \
            -r 10 \
            -t 5m \
            --host ${{ secrets.TEST_HOST }}
      
      - name: Upload reports
        uses: actions/upload-artifact@v2
        with:
          name: performance-reports
          path: tests/performance/reports/
```

## 相关文档

- [测试用例编写指南](docs/test_guide.md)
- [断言使用指南](docs/assertion_guide.md)
- [性能测试指南](docs/performance_guide.md)
- [测试数据管理指南](docs/data_management.md)

## 联系方式

如有问题或建议，请联系开发团队。

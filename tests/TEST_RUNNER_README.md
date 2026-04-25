# 测试执行脚本使用指南

## 概述

本项目提供了强大的测试执行脚本，支持多种测试套件、环境配置、并行执行、失败重试等功能。

## 文件结构

```
petshop/
├── run_tests.py              # 主测试运行脚本
├── test_config.example.json  # 示例配置文件
└── tests/
    ├── test_runner.py        # 测试运行器核心模块
    └── test_test_runner.py   # 测试运行器的单元测试
```

## 快速开始

### 运行所有测试

```bash
python run_tests.py --all
```

### 运行特定测试套件

```bash
# 单元测试
python run_tests.py --suite unit

# 集成测试
python run_tests.py --suite integration

# 性能测试
python run_tests.py --suite performance

# 安全测试
python run_tests.py --suite security
```

### 运行特定模块测试

```bash
# 用户端测试
python run_tests.py --module user

# 商家端测试
python run_tests.py --module merchant

# 平台端测试
python run_tests.py --module admin

# 公共API测试
python run_tests.py --module public
```

### 运行特定标记测试

```bash
# 冒烟测试
python run_tests.py --marker smoke

# 回归测试
python run_tests.py --marker regression

# 认证测试
python run_tests.py --marker auth

# 服务订单测试
python run_tests.py --marker appointment

# 商品订单测试
python run_tests.py --marker product_order

# 数据隔离测试
python run_tests.py --marker isolation
```

## 高级功能

### 1. 并行测试执行

使用 `--parallel` 参数启用并行测试执行，提高测试效率：

```bash
# 使用4个进程并行执行
python run_tests.py --all --parallel 4

# 使用8个进程并行执行，并启用负载均衡
python run_tests.py --all --parallel 8 --load-balance
```

### 2. 失败重试机制

使用 `--retry` 参数为失败的测试用例自动重试：

```bash
# 失败后重试2次
python run_tests.py --all --retry 2

# 失败后重试3次，每次延迟3秒
python run_tests.py --all --retry 3 --retry-delay 3
```

### 3. 环境配置

支持不同的测试环境：

```bash
# 开发环境（默认）
python run_tests.py --all --env dev

# 测试环境
python run_tests.py --all --env test

# 生产环境
python run_tests.py --all --env prod
```

### 4. 从配置文件加载

使用 JSON 配置文件来管理测试配置：

```bash
# 从配置文件加载
python run_tests.py --config test_config.json
```

配置文件示例（`test_config.example.json`）：

```json
{
  "suite": "unit",
  "module": "user",
  "marker": "smoke",
  "env": "dev",
  "parallel": 2,
  "retry": 1,
  "retry_delay": 2,
  "verbose": true,
  "coverage": true,
  "report_path": "tests/report",
  "base_url": "http://localhost:8080",
  "timeout": 30,
  "execution_mode": "local",
  "custom": {
    "database": {
      "host": "localhost",
      "port": 3306,
      "database": "cg",
      "user": "root",
      "password": "123456"
    }
  }
}
```

### 5. 环境变量配置

支持从环境变量读取配置：

```bash
# 设置环境变量
export TEST_SUITE=unit
export TEST_MODULE=user
export TEST_PARALLEL=4
export TEST_RETRY=2
export TEST_ENV=test

# 运行测试（会自动读取环境变量）
python run_tests.py --all
```

支持的环境变量：
- `TEST_SUITE` - 测试套件
- `TEST_MODULE` - 测试模块
- `TEST_MARKER` - 测试标记
- `TEST_ENV` - 测试环境
- `TEST_PARALLEL` - 并行进程数
- `TEST_RETRY` - 重试次数
- `TEST_RETRY_DELAY` - 重试延迟
- `TEST_VERBOSE` - 详细输出
- `TEST_COVERAGE` - 覆盖率报告
- `TEST_REPORT_PATH` - 报告路径
- `TEST_BASE_URL` - 后端服务地址
- `TEST_TIMEOUT` - 超时时间
- `TEST_EXECUTION_MODE` - 执行模式

### 6. 报告生成

支持多种报告格式：

```bash
# 生成HTML报告
python run_tests.py --all --report

# 生成覆盖率报告
python run_tests.py --all --coverage

# 生成Allure报告（需要安装allure命令行工具）
python run_tests.py --all --allure

# 组合使用
python run_tests.py --all --report --coverage --allure
```

### 7. 测试通知

测试完成后发送通知到 Webhook：

```bash
# 发送通知到Slack或其他Webhook
python run_tests.py --all --notify https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

## 命令行参数完整列表

### 测试选择
- `--suite {unit,integration,performance,security}` - 选择测试套件
- `--module {user,merchant,admin,public}` - 选择测试模块
- `--marker {smoke,regression,auth,appointment,product_order,isolation,performance,security}` - 选择测试标记
- `--all` - 运行所有测试

### 环境配置
- `--env {dev,test,prod}` - 选择测试环境（默认：dev）
- `--config CONFIG` - 从配置文件加载测试配置
- `--base-url BASE_URL` - 后端服务地址（默认：http://localhost:8080）

### 执行选项
- `--parallel PARALLEL` - 并行执行数量（默认：1）
- `--retry RETRY` - 失败重试次数（默认：0）
- `--retry-delay RETRY_DELAY` - 失败重试延迟秒数（默认：2）
- `--load-balance` - 启用负载均衡（并行执行时）
- `--verbose, -v` - 详细输出

### 报告选项
- `--report` - 生成HTML报告
- `--coverage` - 生成覆盖率报告
- `--allure` - 生成Allure报告
- `--report-path REPORT_PATH` - 报告输出路径

### 其他选项
- `--skip-deps` - 跳过依赖检查
- `--skip-backend-check` - 跳过后端服务检查
- `--skip-init` - 跳过测试数据初始化
- `--cleanup` - 测试完成后清理测试数据
- `--notify NOTIFY` - 测试完成后发送通知的Webhook URL

## 使用示例

### 1. 快速冒烟测试

```bash
python run_tests.py --marker smoke --parallel 4
```

### 2. 完整回归测试

```bash
python run_tests.py --all --parallel 8 --retry 2 --coverage --report
```

### 3. 用户端集成测试

```bash
python run_tests.py --suite integration --module user --parallel 4 --report
```

### 4. CI/CD 环境运行

```bash
python run_tests.py --all --env test --parallel 8 --retry 3 --coverage --report --notify $WEBHOOK_URL
```

### 5. 本地开发测试

```bash
python run_tests.py --suite unit --module user --verbose
```

## 编程方式使用

### 使用 TestRunner 类

```python
from tests.test_runner import TestConfiguration, TestRunner, TestSuite

# 创建配置
config = TestConfiguration(
    suite=TestSuite.UNIT,
    parallel=4,
    retry=2,
    coverage=True
)

# 创建运行器
runner = TestRunner(config)

# 执行测试
return_code = runner.run_tests()

# 收集结果
result = runner.collect_results()
stats = result.get_statistics()

# 生成报告
runner.generate_report(format='html')

# 发送通知
runner.send_notification(webhook_url='https://hooks.slack.com/...')

print(f"测试通过率: {stats['pass_rate']}%")
```

### 使用便捷函数

```python
from tests.test_runner import create_test_runner

# 创建测试运行器
runner = create_test_runner(
    suite='unit',
    module='user',
    parallel=4,
    retry=2
)

# 执行测试
return_code = runner.run_tests()
```

## 测试结果

测试结果会保存在 `tests/report/` 目录下：

- `report.html` - HTML测试报告
- `allure-results/` - Allure测试结果
- `allure-report/` - Allure HTML报告
- `coverage/` - 覆盖率报告

## 依赖包

测试运行器依赖以下包（会自动安装）：

- `pytest` - 测试框架
- `pytest-html` - HTML报告生成
- `pytest-cov` - 覆盖率报告
- `pytest-xdist` - 并行测试执行
- `pytest-rerunfailures` - 失败重试
- `allure-pytest` - Allure报告
- `python-dotenv` - 环境变量管理
- `requests` - HTTP请求

## 故障排查

### 1. 后端服务未启动

如果后端服务未运行，脚本会提示是否启动服务。也可以使用 `--skip-backend-check` 跳过检查。

### 2. 依赖包缺失

脚本会自动检查并安装缺失的依赖包。如果安装失败，可以手动安装：

```bash
pip install pytest pytest-html pytest-cov pytest-xdist pytest-rerunfailures allure-pytest python-dotenv requests
```

### 3. Allure报告生成失败

需要安装 Allure 命令行工具：

```bash
npm install -g allure-commandline
```

### 4. 并行测试失败

确保测试用例之间没有依赖关系，或者使用 `--load-balance` 参数进行负载均衡。

## 最佳实践

1. **本地开发**：使用单元测试和详细输出
   ```bash
   python run_tests.py --suite unit --verbose
   ```

2. **提交前检查**：运行冒烟测试
   ```bash
   python run_tests.py --marker smoke --parallel 4
   ```

3. **每日构建**：运行完整测试套件
   ```bash
   python run_tests.py --all --parallel 8 --coverage --report
   ```

4. **CI/CD**：使用配置文件和环境变量
   ```bash
   python run_tests.py --config ci_config.json --notify $WEBHOOK_URL
   ```

## 更多信息

- 测试框架文档：[tests/README.md](tests/README.md)
- 测试数据管理：[tests/testdata/README.md](tests/testdata/README.md)
- 性能测试：[tests/performance/README.md](tests/performance/README.md)

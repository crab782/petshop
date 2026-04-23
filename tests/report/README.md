# 测试报告生成器

完整的测试报告生成解决方案，支持HTML测试报告、性能测试报告、代码覆盖率报告和测试结果通知。

## 功能特性

### 1. HTML测试报告生成器 (HTMLReportGenerator)

- ✅ 美观的HTML格式测试报告
- ✅ 测试概览统计（通过/失败/跳过数量）
- ✅ 通过率可视化
- ✅ 失败测试详情展示
- ✅ 测试详情列表（支持筛选）
- ✅ 执行时间统计
- ✅ 支持从pytest JSON和JUnit XML解析

### 2. 性能测试报告生成器 (PerformanceReportGenerator)

- ✅ 响应时间统计（平均/最小/最大）
- ✅ 百分位响应时间（P50/P95/P99）
- ✅ 吞吐量统计
- ✅ 错误率统计
- ✅ 美观的可视化展示

### 3. 代码覆盖率报告生成器 (CoverageReportGenerator)

- ✅ 总体覆盖率统计
- ✅ 文件级别覆盖率详情
- ✅ 覆盖率可视化条
- ✅ 支持从coverage XML解析

### 4. 测试结果通知发送器 (NotificationSender)

- ✅ 邮件通知支持
- ✅ Webhook通知支持
- ✅ 美观的邮件模板
- ✅ 测试摘要信息

## 目录结构

```
tests/report/
├── __init__.py              # 模块初始化
├── generator.py             # 报告生成器主文件
├── example_usage.py         # 使用示例
├── README.md               # 本文档
├── templates/              # 报告模板
│   ├── html_report_template.html  # HTML报告模板
│   └── email_template.html        # 邮件通知模板
└── styles/                 # 样式文件
    └── report.css          # 报告样式
```

## 安装依赖

```bash
pip install jinja2 requests
```

## 快速开始

### 1. 生成HTML测试报告

```python
from tests.report import HTMLReportGenerator
from tests.report.generator import TestResult

# 创建测试结果
test_results = [
    TestResult('test_user_login', 'passed', 0.5),
    TestResult('test_user_register', 'failed', 1.2, 'AssertionError: ...'),
    TestResult('test_create_order', 'skipped', 0.1),
]

# 生成报告
generator = HTMLReportGenerator()
report_path = generator.generate(
    test_results,
    'reports/test_report.html',
    title='测试报告'
)

print(f"报告已生成: {report_path}")
```

### 2. 从pytest JSON生成报告

```bash
# 运行pytest并生成JSON报告
pytest --json-report --json-report-file=reports/pytest.json

# 使用生成器转换
python -c "
from tests.report.generator import generate_report_from_pytest
generate_report_from_pytest('reports/pytest.json', 'reports/html_report.html')
"
```

### 3. 从JUnit XML生成报告

```bash
# 运行pytest并生成JUnit XML
pytest --junitxml=reports/junit.xml

# 使用生成器转换
python -c "
from tests.report.generator import generate_report_from_junit
generate_report_from_junit('reports/junit.xml', 'reports/html_report.html')
"
```

### 4. 生成性能测试报告

```python
from tests.report import PerformanceReportGenerator
from tests.report.generator import PerformanceMetric

# 创建性能指标
metrics = [
    PerformanceMetric('GET /api/users', 45.2, 200, True),
    PerformanceMetric('POST /api/orders', 156.3, 200, True),
    PerformanceMetric('POST /api/payment', 289.5, 500, False),
]

# 生成报告
generator = PerformanceReportGenerator()
report_path = generator.generate(metrics, title='性能测试报告')

print(f"性能报告已生成: {report_path}")
```

### 5. 生成覆盖率报告

```python
from tests.report import CoverageReportGenerator
from tests.report.generator import CoverageSummary, CoverageData

# 创建覆盖率数据
coverage = CoverageSummary(
    line_rate=0.78,
    lines_covered=2340,
    lines_total=3000,
    files=[
        CoverageData('src/main.py', 0.92, 0.88, 230, 250, 88, 100),
        CoverageData('src/utils.py', 0.65, 0.60, 130, 200, 60, 100),
    ]
)

# 生成报告
generator = CoverageReportGenerator()
report_path = generator.generate(coverage, title='覆盖率报告')

print(f"覆盖率报告已生成: {report_path}")
```

### 6. 发送测试通知

```python
from tests.report import NotificationSender
from tests.report.generator import NotificationConfig, TestSummary

# 配置通知
config = NotificationConfig(
    email_enabled=True,
    email_smtp_host='smtp.example.com',
    email_smtp_port=587,
    email_username='your-email@example.com',
    email_password='your-password',
    email_recipients=['team@example.com'],
    
    webhook_enabled=True,
    webhook_url='https://hooks.slack.com/services/...',
    webhook_headers={'Content-Type': 'application/json'}
)

# 创建发送器
sender = NotificationSender(config)

# 发送通知
summary = TestSummary(
    total=10,
    passed=8,
    failed=2,
    skipped=0,
    duration=15.5
)

results = sender.send_test_notification(
    summary,
    report_url='https://example.com/reports/test_report.html',
    project_name='宠物服务平台'
)

print(f"邮件发送: {'成功' if results.get('email') else '失败'}")
print(f"Webhook发送: {'成功' if results.get('webhook') else '失败'}")
```

## 运行示例

```bash
# 运行完整示例
python tests/report/example_usage.py
```

这将生成以下报告：
- `reports/test_report_example.html` - HTML测试报告
- `reports/pytest_html_report.html` - 从pytest JSON生成的报告
- `reports/performance_report_YYYYMMDD_HHMMSS.html` - 性能测试报告
- `reports/coverage_report_YYYYMMDD_HHMMSS.html` - 覆盖率报告

## 集成到CI/CD

### GitHub Actions示例

```yaml
name: Test Report

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.9'
      
      - name: Install dependencies
        run: |
          pip install -r requirements.txt
          pip install pytest pytest-json-report pytest-cov
      
      - name: Run tests
        run: |
          pytest --json-report --json-report-file=reports/pytest.json
          pytest --cov=src --cov-report=xml:reports/coverage.xml
      
      - name: Generate reports
        run: python tests/report/example_usage.py
      
      - name: Upload reports
        uses: actions/upload-artifact@v3
        with:
          name: test-reports
          path: reports/
```

## 报告特性

### HTML测试报告

- 📊 测试概览卡片（总数、通过、失败、跳过）
- 📈 通过率可视化进度条
- 🔍 失败测试详细错误信息
- 📋 可筛选的测试详情列表
- ⏱️ 执行时间统计
- 📱 响应式设计，支持移动端

### 性能测试报告

- 📊 请求统计（总数、成功、失败）
- ⚡ 响应时间分析（平均、最小、最大）
- 📈 百分位响应时间（P50、P95、P99）
- 🔄 吞吐量统计
- ❌ 错误率统计

### 覆盖率报告

- 📊 总体覆盖率展示
- 📁 文件级别覆盖率详情
- 🎨 颜色编码（绿色>80%，黄色>60%，橙色>40%，红色<40%）
- 📈 可视化覆盖率条

## 自定义模板

可以自定义HTML模板和样式：

1. 修改 `templates/html_report_template.html` 自定义报告布局
2. 修改 `styles/report.css` 自定义报告样式
3. 修改 `templates/email_template.html` 自定义邮件通知模板

## 最佳实践

1. **定期生成报告**: 在每次代码提交或每日构建时生成报告
2. **保留历史报告**: 使用时间戳命名报告文件，便于追踪趋势
3. **设置通知**: 配置邮件或Webhook通知，及时了解测试状态
4. **覆盖率目标**: 设置覆盖率目标（如80%），并在报告中突出显示
5. **性能基准**: 建立性能基准，监控响应时间变化

## 故障排查

### 问题：报告生成失败

**解决方案**：
- 检查模板文件是否存在
- 确保有写入权限
- 查看错误日志

### 问题：邮件发送失败

**解决方案**：
- 检查SMTP配置
- 确认邮箱账号密码正确
- 检查网络连接

### 问题：覆盖率数据不正确

**解决方案**：
- 确认coverage XML格式正确
- 检查文件路径是否匹配
- 验证覆盖率工具配置

## 许可证

本项目仅供宠物服务平台内部使用。

# 性能测试模块

## 概述

本模块提供了宠物服务平台的全面性能测试功能，使用 Locust 框架进行负载测试，支持多种测试场景和详细的性能报告生成。

## 目录结构

```
tests/performance/
├── __init__.py                 # 模块初始化文件
├── performance_config.py       # 性能测试配置
├── performance_reporter.py     # 性能测试报告生成器
├── locustfile.py              # Locust性能测试场景
└── README.md                  # 本文档
```

## 安装依赖

在运行性能测试之前，请确保安装以下依赖：

```bash
pip install locust
```

## 使用方法

### 1. 基本使用

运行默认的性能测试：

```bash
cd tests/performance
locust -f locustfile.py
```

然后在浏览器中打开 `http://localhost:8089` 来配置和启动测试。

### 2. 命令行模式

无头模式运行测试（适用于CI/CD环境）：

```bash
locust -f locustfile.py --headless -u 100 -r 10 -t 5m --host http://localhost:8080
```

参数说明：
- `-u` 或 `--users`: 并发用户数
- `-r` 或 `--spawn-rate`: 每秒启动的用户数
- `-t` 或 `--run-time`: 测试持续时间
- `--host`: 目标服务器地址

### 3. 分布式测试

主节点：
```bash
locust -f locustfile.py --master
```

工作节点（可以启动多个）：
```bash
locust -f locustfile.py --worker --master-host=<master-ip>
```

## 测试场景

### 用户端性能测试

- **UserBehavior**: 模拟用户行为
  - 用户登录
  - 浏览服务列表
  - 查看服务详情
  - 创建预约订单
  - 查询订单列表
  - 搜索服务

### 商家端性能测试

- **MerchantBehavior**: 模拟商家行为
  - 商家登录
  - 管理服务列表
  - 更新服务信息
  - 处理订单
  - 确认预约
  - 查看统计数据

### 平台端性能测试

- **AdminBehavior**: 模拟管理员行为
  - 管理员登录
  - 查询用户列表
  - 审核商家
  - 批准商家
  - 查看仪表板
  - 查询评价

### 混合场景测试

- **MixedUserBehavior**: 完整用户流程
  - 登录 → 浏览 → 下单 → 支付

- **MixedMerchantBehavior**: 完整商家流程
  - 登录 → 查看订单 → 处理订单

- **MixedAdminBehavior**: 完整管理员流程
  - 登录 → 审核 → 查询

## 性能基准

### 响应时间基准

- 简单查询操作: < 500ms
- 复杂操作: < 2000ms
- 平均响应时间: < 300ms
- P95响应时间: < 800ms
- P99响应时间: < 1500ms

### 吞吐量基准

- 最小吞吐量: > 100 requests/second

### 错误率基准

- 最大错误率: < 1%

### 并发用户数

- 支持至少 100 并发用户

## 性能报告

测试完成后，系统会自动生成详细的性能测试报告：

### HTML报告

包含以下内容：
- 性能指标概览
- 响应时间统计
- 吞吐量统计
- 错误率统计
- 性能基准对比
- 性能瓶颈分析
- 优化建议

### JSON报告

结构化的JSON数据，便于集成到其他系统。

报告保存路径：`tests/performance/reports/`

## 配置自定义测试

### 自定义配置示例

```python
from performance_config import create_custom_config, TestScenario

config = create_custom_config(
    scenario=TestScenario.USER_LOGIN,
    concurrent_users=200,
    spawn_rate=20,
    test_duration=600,
    max_response_time_simple=400,
    max_response_time_complex=1500,
)
```

### 自定义测试场景

```python
from locust import HttpUser, task, between

class CustomUserBehavior(HttpUser):
    wait_time = between(1, 3)
    
    @task
    def my_custom_task(self):
        self.client.get("/api/custom-endpoint")
```

## 性能优化建议

根据测试结果，系统会自动生成优化建议：

1. **响应时间过高**
   - 优化数据库查询，添加必要的索引
   - 使用缓存机制（如Redis）
   - 检查是否存在N+1查询问题

2. **吞吐量不足**
   - 增加服务器资源
   - 使用负载均衡
   - 优化数据库连接池配置

3. **错误率过高**
   - 检查错误日志
   - 增加重试机制
   - 检查服务超时配置

4. **并发性能问题**
   - 考虑水平扩展
   - 优化并发处理机制
   - 检查资源竞争问题

## 最佳实践

1. **测试前准备**
   - 确保测试环境与生产环境配置一致
   - 准备足够的测试数据
   - 监控服务器资源使用情况

2. **测试执行**
   - 从小规模开始，逐步增加负载
   - 持续监控系统状态
   - 记录异常情况

3. **结果分析**
   - 关注关键性能指标
   - 识别性能瓶颈
   - 制定优化方案

4. **持续改进**
   - 定期执行性能测试
   - 对比历史数据
   - 验证优化效果

## 故障排查

### 常见问题

1. **连接超时**
   - 检查网络连接
   - 确认服务器地址和端口正确
   - 检查防火墙设置

2. **认证失败**
   - 确认测试用户账号存在
   - 检查密码是否正确
   - 验证JWT配置

3. **数据不存在**
   - 准备测试数据
   - 使用Mock数据
   - 调整测试场景

## 集成到CI/CD

### GitHub Actions 示例

```yaml
name: Performance Tests

on:
  schedule:
    - cron: '0 2 * * 0'  # 每周日凌晨2点运行

jobs:
  performance-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Python
        uses: actions/setup-python@v2
        with:
          python-version: '3.9'
      
      - name: Install dependencies
        run: |
          pip install locust
      
      - name: Run performance tests
        run: |
          cd tests/performance
          locust -f locustfile.py --headless -u 100 -r 10 -t 5m --host ${{ secrets.TEST_HOST }}
      
      - name: Upload reports
        uses: actions/upload-artifact@v2
        with:
          name: performance-reports
          path: tests/performance/reports/
```

## 联系方式

如有问题或建议，请联系开发团队。

# 性能测试指南

## 概述

本指南详细介绍了宠物服务平台的性能测试方法、工具和最佳实践。性能测试使用 Locust 框架，支持多种测试场景和详细的性能报告生成。

## 性能测试概述

### 什么是性能测试？

性能测试是一种非功能性测试，用于评估系统在特定负载下的响应速度、稳定性和可扩展性。性能测试的目标是：

1. **验证性能需求**：确保系统满足性能指标要求
2. **识别性能瓶颈**：发现系统的性能瓶颈和限制
3. **优化系统性能**：为性能优化提供数据支持
4. **容量规划**：确定系统的容量和扩展需求

### 性能测试类型

1. **负载测试（Load Testing）**
   - 测试系统在预期负载下的表现
   - 验证系统是否满足性能要求
   - 示例：测试 100 个并发用户下的响应时间

2. **压力测试（Stress Testing）**
   - 测试系统在超出预期负载下的表现
   - 找到系统的性能极限和崩溃点
   - 示例：逐步增加用户数直到系统崩溃

3. **峰值测试（Spike Testing）**
   - 测试系统在突然增加负载时的表现
   - 验证系统的弹性和恢复能力
   - 示例：突然从 10 个用户增加到 1000 个用户

4. **耐久性测试（Soak Testing）**
   - 测试系统在长时间持续负载下的表现
   - 验证系统的稳定性和资源泄漏
   - 示例：在中等负载下运行 24 小时

5. **容量测试（Volume Testing）**
   - 测试系统处理大量数据的能力
   - 验证数据库和存储的性能
   - 示例：测试处理 100 万条记录的性能

## 性能测试配置

### 环境准备

#### 安装 Locust

```bash
# 安装 Locust
pip install locust

# 验证安装
locust --version

# 安装项目依赖
cd tests/performance
pip install -r requirements.txt
```

#### requirements.txt

```
locust>=2.20.0
requests>=2.31.0
psutil>=5.9.0
```

### 测试配置文件

#### performance_config.py

```python
from dataclasses import dataclass
from enum import Enum
from typing import Optional, Dict, Any


class TestScenario(Enum):
    """测试场景枚举"""
    USER_LOGIN = "user_login"
    USER_BROWSE = "user_browse"
    USER_BOOKING = "user_booking"
    MERCHANT_MANAGE = "merchant_manage"
    ADMIN_AUDIT = "admin_audit"
    MIXED = "mixed"


@dataclass
class UserLoadConfig:
    """用户负载配置"""
    concurrent_users: int = 100
    spawn_rate: int = 10
    test_duration: int = 300  # 秒
    wait_time_min: int = 1
    wait_time_max: int = 3
    
    # 性能基准
    max_response_time_simple: int = 500  # ms
    max_response_time_complex: int = 2000  # ms
    max_avg_response_time: int = 300  # ms
    min_requests_per_second: float = 100.0
    max_error_rate: float = 0.01  # 1%


@dataclass
class MerchantLoadConfig:
    """商家负载配置"""
    concurrent_users: int = 20
    spawn_rate: int = 5
    test_duration: int = 300
    wait_time_min: int = 2
    wait_time_max: int = 8
    
    max_response_time_simple: int = 800
    max_response_time_complex: int = 3000
    max_avg_response_time: int = 500
    min_requests_per_second: float = 50.0
    max_error_rate: float = 0.01


@dataclass
class AdminLoadConfig:
    """管理员负载配置"""
    concurrent_users: int = 5
    spawn_rate: int = 2
    test_duration: int = 300
    wait_time_min: int = 3
    wait_time_max: int = 10
    
    max_response_time_simple: int = 1000
    max_response_time_complex: int = 5000
    max_avg_response_time: int = 800
    min_requests_per_second: float = 20.0
    max_error_rate: float = 0.005


class PerformanceBenchmark:
    """性能基准配置"""
    
    def __init__(self):
        self.response_time_thresholds = {
            "excellent": 200,    # ms
            "good": 500,
            "acceptable": 1000,
            "poor": 2000,
        }
        
        self.throughput_thresholds = {
            "excellent": 500,    # req/s
            "good": 200,
            "acceptable": 100,
            "poor": 50,
        }
        
        self.error_rate_thresholds = {
            "excellent": 0.001,  # 0.1%
            "good": 0.005,       # 0.5%
            "acceptable": 0.01,  # 1%
            "poor": 0.05,        # 5%
        }
    
    def evaluate_response_time(self, response_time: float) -> str:
        """评估响应时间"""
        if response_time <= self.response_time_thresholds["excellent"]:
            return "excellent"
        elif response_time <= self.response_time_thresholds["good"]:
            return "good"
        elif response_time <= self.response_time_thresholds["acceptable"]:
            return "acceptable"
        else:
            return "poor"
    
    def evaluate_throughput(self, throughput: float) -> str:
        """评估吞吐量"""
        if throughput >= self.throughput_thresholds["excellent"]:
            return "excellent"
        elif throughput >= self.throughput_thresholds["good"]:
            return "good"
        elif throughput >= self.throughput_thresholds["acceptable"]:
            return "acceptable"
        else:
            return "poor"
    
    def evaluate_error_rate(self, error_rate: float) -> str:
        """评估错误率"""
        if error_rate <= self.error_rate_thresholds["excellent"]:
            return "excellent"
        elif error_rate <= self.error_rate_thresholds["good"]:
            return "good"
        elif error_rate <= self.error_rate_thresholds["acceptable"]:
            return "acceptable"
        else:
            return "poor"


def create_custom_config(
    scenario: TestScenario,
    concurrent_users: int,
    spawn_rate: int,
    test_duration: int,
    **kwargs
) -> Dict[str, Any]:
    """创建自定义测试配置"""
    config = {
        "scenario": scenario,
        "concurrent_users": concurrent_users,
        "spawn_rate": spawn_rate,
        "test_duration": test_duration,
    }
    config.update(kwargs)
    return config
```

### Locust 配置文件

#### locust.conf

```ini
# Locust 配置文件
host = http://localhost:8080
users = 100
spawn-rate = 10
run-time = 5m
headless = false
html = reports/performance_report.html
csv = reports/performance_report
```

## 性能测试场景

### 用户端性能测试场景

```python
from locust import HttpUser, task, between
import random
from datetime import datetime, timedelta


class UserBehavior(HttpUser):
    """
    用户端性能测试场景
    
    模拟用户的各种行为，包括：
    - 登录
    - 浏览服务
    - 创建订单
    - 查询订单
    """
    
    weight = 10
    wait_time = between(1, 5)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.auth_token = None
        self.test_users = [
            {"username": f"test_user_{i}", "password": "Test123456"}
            for i in range(1, 101)
        ]
    
    def on_start(self):
        """用户开始时登录"""
        user = random.choice(self.test_users)
        self.login(user["username"], user["password"])
    
    def get_headers(self):
        """获取请求头"""
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
        }
        if self.auth_token:
            headers["Authorization"] = f"Bearer {self.auth_token}"
        return headers
    
    def login(self, username: str, password: str):
        """用户登录"""
        with self.client.post(
            "/api/auth/login",
            json={"username": username, "password": password},
            catch_response=True,
            name="user_login",
        ) as response:
            if response.status_code == 200:
                data = response.json()
                self.auth_token = data.get("token") or data.get("access_token")
                response.success()
            else:
                response.failure(f"登录失败: {response.status_code}")
    
    @task(40)
    def browse_services(self):
        """浏览服务列表"""
        params = {
            "page": random.randint(1, 10),
            "page_size": random.choice([10, 20, 50]),
        }
        
        with self.client.get(
            "/api/services",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="browse_services",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"获取服务列表失败: {response.status_code}")
    
    @task(20)
    def view_service_detail(self):
        """查看服务详情"""
        service_id = random.randint(1, 100)
        
        with self.client.get(
            f"/api/services/{service_id}",
            headers=self.get_headers(),
            catch_response=True,
            name="view_service_detail",
        ) as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"获取服务详情失败: {response.status_code}")
    
    @task(20)
    def create_appointment(self):
        """创建预约订单"""
        if not self.auth_token:
            return
        
        appointment_data = {
            "service_id": random.randint(1, 50),
            "pet_id": random.randint(1, 20),
            "appointment_time": (datetime.now() + timedelta(days=random.randint(1, 7))).isoformat(),
            "notes": f"性能测试预约 - {datetime.now().timestamp()}",
        }
        
        with self.client.post(
            "/api/appointments",
            json=appointment_data,
            headers=self.get_headers(),
            catch_response=True,
            name="create_appointment",
        ) as response:
            if response.status_code in [200, 201]:
                response.success()
            else:
                response.failure(f"创建预约失败: {response.status_code}")
    
    @task(30)
    def query_orders(self):
        """查询订单列表"""
        params = {
            "page": random.randint(1, 5),
            "page_size": 10,
            "status": random.choice(["pending", "confirmed", "completed", "all"]),
        }
        
        with self.client.get(
            "/api/appointments",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="query_orders",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"查询订单失败: {response.status_code}")
```

### 商家端性能测试场景

```python
class MerchantBehavior(HttpUser):
    """
    商家端性能测试场景
    
    模拟商家的各种行为，包括：
    - 登录
    - 管理服务
    - 处理订单
    """
    
    weight = 3
    wait_time = between(2, 8)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.auth_token = None
        self.test_merchants = [
            {"username": f"merchant_{i}", "password": "Merchant123"}
            for i in range(1, 21)
        ]
    
    def on_start(self):
        """商家开始时登录"""
        merchant = random.choice(self.test_merchants)
        self.login(merchant["username"], merchant["password"])
    
    @task(40)
    def manage_services(self):
        """管理服务列表"""
        with self.client.get(
            "/api/merchant/services",
            headers=self.get_headers(),
            catch_response=True,
            name="merchant_manage_services",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"获取服务列表失败: {response.status_code}")
    
    @task(50)
    def process_orders(self):
        """处理订单"""
        params = {
            "status": "pending",
            "page": 1,
            "page_size": 20,
        }
        
        with self.client.get(
            "/api/merchant/appointments",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="merchant_query_orders",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"查询订单失败: {response.status_code}")
```

### 平台端性能测试场景

```python
class AdminBehavior(HttpUser):
    """
    平台端性能测试场景
    
    模拟管理员的各种行为，包括：
    - 登录
    - 查询用户
    - 审核商家
    """
    
    weight = 1
    wait_time = between(3, 10)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.auth_token = None
        self.test_admins = [
            {"username": f"admin_{i}", "password": "Admin123!@#"}
            for i in range(1, 6)
        ]
    
    def on_start(self):
        """管理员开始时登录"""
        admin = random.choice(self.test_admins)
        self.login(admin["username"], admin["password"])
    
    @task(40)
    def query_users(self):
        """查询用户列表"""
        params = {
            "page": random.randint(1, 10),
            "page_size": 20,
            "status": random.choice(["active", "inactive", "all"]),
        }
        
        with self.client.get(
            "/api/admin/users",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="admin_query_users",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"查询用户失败: {response.status_code}")
    
    @task(50)
    def audit_merchants(self):
        """审核商家"""
        params = {
            "status": "pending",
            "page": 1,
            "page_size": 20,
        }
        
        with self.client.get(
            "/api/admin/merchants",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="admin_query_merchants",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"查询商家失败: {response.status_code}")
```

### 混合场景测试

```python
class MixedUserBehavior(HttpUser):
    """
    混合用户行为场景
    
    模拟真实用户的完整行为流程：
    登录 → 浏览 → 下单 → 支付
    """
    
    weight = 5
    wait_time = between(2, 6)
    
    @task(5)
    def complete_user_journey(self):
        """完整的用户旅程"""
        # 登录
        if not self.auth_token:
            user = random.choice(self.test_users)
            if not self.login(user["username"], user["password"]):
                return
        
        # 浏览服务
        self.browse_and_select_service()
        
        # 创建预约
        if self.current_service_id:
            self.create_appointment_for_service()
        
        # 支付
        if self.current_appointment_id:
            self.pay_appointment()
```

## 性能测试执行

### Web UI 模式

```bash
# 启动 Locust Web UI
cd tests/performance
locust -f locustfile.py

# 访问 http://localhost:8089
# 在 Web UI 中配置：
# - Number of users: 100
# - Spawn rate: 10
# - Host: http://localhost:8080
```

### 命令行模式

```bash
# 基本命令
locust -f locustfile.py --headless -u 100 -r 10 -t 5m --host http://localhost:8080

# 参数说明
# -f, --locustfile: Locust 文件路径
# --headless: 无头模式（无 Web UI）
# -u, --users: 并发用户数
# -r, --spawn-rate: 每秒启动的用户数
# -t, --run-time: 测试持续时间
# --host: 目标服务器地址

# 生成 HTML 报告
locust -f locustfile.py --headless -u 100 -r 10 -t 5m \
    --host http://localhost:8080 \
    --html reports/performance_report.html

# 生成 CSV 报告
locust -f locustfile.py --headless -u 100 -r 10 -t 5m \
    --host http://localhost:8080 \
    --csv reports/performance_report
```

### 分布式测试

```bash
# 主节点
locust -f locustfile.py --master

# 工作节点（可以启动多个）
locust -f locustfile.py --worker --master-host=<master-ip>

# 分布式测试配置
# 主节点配置
locust -f locustfile.py --master --expect-workers=4

# 工作节点 1
locust -f locustfile.py --worker --master-host=192.168.1.100

# 工作节点 2
locust -f locustfile.py --worker --master-host=192.168.1.100
```

### 不同测试场景执行

```bash
# 负载测试 - 正常负载
locust -f locustfile.py --headless \
    -u 100 -r 10 -t 10m \
    --host http://localhost:8080

# 压力测试 - 高负载
locust -f locustfile.py --headless \
    -u 500 -r 50 -t 15m \
    --host http://localhost:8080

# 峰值测试 - 突发负载
locust -f locustfile.py --headless \
    -u 1000 -r 100 -t 5m \
    --host http://localhost:8080

# 耐久性测试 - 长时间测试
locust -f locustfile.py --headless \
    -u 50 -r 5 -t 24h \
    --host http://localhost:8080
```

## 性能测试报告

### 报告生成器

```python
# performance_reporter.py

from dataclasses import dataclass
from typing import List, Dict, Any, Optional
from datetime import datetime
import json
import os


@dataclass
class PerformanceMetrics:
    """性能指标数据类"""
    total_requests: int
    total_failures: int
    avg_response_time: float
    min_response_time: float
    max_response_time: float
    p50_response_time: float
    p95_response_time: float
    p99_response_time: float
    requests_per_second: float
    failure_rate: float


@dataclass
class PerformanceReport:
    """性能测试报告"""
    test_name: str
    start_time: str
    end_time: str
    duration: float
    user_count: int
    metrics: PerformanceMetrics
    bottlenecks: List[str]
    recommendations: List[str]
    passed: bool


class PerformanceReporter:
    """性能测试报告生成器"""
    
    def __init__(self, benchmark: PerformanceBenchmark):
        self.benchmark = benchmark
    
    def generate_report(self, test_results: Dict[str, Any]) -> PerformanceReport:
        """生成性能测试报告"""
        stats = test_results["stats"]
        
        metrics = PerformanceMetrics(
            total_requests=stats["num_requests"],
            total_failures=stats["num_failures"],
            avg_response_time=stats["avg_response_time"],
            min_response_time=stats["min_response_time"],
            max_response_time=stats["max_response_time"],
            p50_response_time=self._calculate_percentile(stats["response_times"], 50),
            p95_response_time=self._calculate_percentile(stats["response_times"], 95),
            p99_response_time=self._calculate_percentile(stats["response_times"], 99),
            requests_per_second=stats["total_rps"],
            failure_rate=stats["num_failures"] / max(stats["num_requests"], 1),
        )
        
        bottlenecks = self._identify_bottlenecks(metrics)
        recommendations = self._generate_recommendations(metrics, bottlenecks)
        
        passed = self._evaluate_performance(metrics)
        
        return PerformanceReport(
            test_name=test_results["test_name"],
            start_time=test_results["start_time"],
            end_time=test_results["end_time"],
            duration=test_results["duration"],
            user_count=test_results["user_count"],
            metrics=metrics,
            bottlenecks=bottlenecks,
            recommendations=recommendations,
            passed=passed,
        )
    
    def _calculate_percentile(self, response_times: Dict[int, int], percentile: int) -> float:
        """计算响应时间百分位数"""
        if not response_times:
            return 0.0
        
        total_count = sum(response_times.values())
        target_count = total_count * percentile / 100
        
        cumulative_count = 0
        for response_time, count in sorted(response_times.items()):
            cumulative_count += count
            if cumulative_count >= target_count:
                return float(response_time)
        
        return float(max(response_times.keys()))
    
    def _identify_bottlenecks(self, metrics: PerformanceMetrics) -> List[str]:
        """识别性能瓶颈"""
        bottlenecks = []
        
        if metrics.avg_response_time > 1000:
            bottlenecks.append(f"平均响应时间过高: {metrics.avg_response_time:.2f}ms")
        
        if metrics.p95_response_time > 2000:
            bottlenecks.append(f"P95响应时间过高: {metrics.p95_response_time:.2f}ms")
        
        if metrics.requests_per_second < 100:
            bottlenecks.append(f"吞吐量不足: {metrics.requests_per_second:.2f} req/s")
        
        if metrics.failure_rate > 0.01:
            bottlenecks.append(f"错误率过高: {metrics.failure_rate * 100:.2f}%")
        
        return bottlenecks
    
    def _generate_recommendations(
        self,
        metrics: PerformanceMetrics,
        bottlenecks: List[str]
    ) -> List[str]:
        """生成优化建议"""
        recommendations = []
        
        if metrics.avg_response_time > 500:
            recommendations.append("优化数据库查询，添加必要的索引")
            recommendations.append("使用缓存机制（如Redis）减少数据库访问")
        
        if metrics.requests_per_second < 100:
            recommendations.append("增加服务器资源或使用负载均衡")
            recommendations.append("优化数据库连接池配置")
        
        if metrics.failure_rate > 0.01:
            recommendations.append("检查错误日志，修复导致失败的问题")
            recommendations.append("增加重试机制和超时配置")
        
        if metrics.p95_response_time > 2000:
            recommendations.append("检查是否存在慢查询")
            recommendations.append("优化复杂业务逻辑")
        
        return recommendations
    
    def _evaluate_performance(self, metrics: PerformanceMetrics) -> bool:
        """评估性能是否通过"""
        return (
            metrics.avg_response_time <= 500 and
            metrics.p95_response_time <= 2000 and
            metrics.requests_per_second >= 100 and
            metrics.failure_rate <= 0.01
        )
    
    def export_to_html(self, report: PerformanceReport, output_path: str):
        """导出 HTML 报告"""
        html_template = f"""
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>性能测试报告 - {report.test_name}</title>
    <style>
        body {{
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }}
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }}
        h1 {{
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }}
        .metrics {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 20px 0;
        }}
        .metric-card {{
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #007bff;
        }}
        .metric-title {{
            font-weight: bold;
            color: #666;
            margin-bottom: 5px;
        }}
        .metric-value {{
            font-size: 24px;
            font-weight: bold;
            color: #333;
        }}
        .status-passed {{
            color: #28a745;
        }}
        .status-failed {{
            color: #dc3545;
        }}
        .section {{
            margin: 30px 0;
        }}
        .section h2 {{
            color: #333;
            border-bottom: 1px solid #ddd;
            padding-bottom: 10px;
        }}
        .bottleneck, .recommendation {{
            background-color: #fff3cd;
            padding: 10px;
            margin: 10px 0;
            border-left: 4px solid #ffc107;
        }}
        .recommendation {{
            background-color: #d1ecf1;
            border-left-color: #17a2b8;
        }}
    </style>
</head>
<body>
    <div class="container">
        <h1>性能测试报告</h1>
        
        <div class="section">
            <h2>测试概览</h2>
            <p><strong>测试名称:</strong> {report.test_name}</p>
            <p><strong>测试时间:</strong> {report.start_time} - {report.end_time}</p>
            <p><strong>持续时间:</strong> {report.duration:.2f} 秒</p>
            <p><strong>并发用户数:</strong> {report.user_count}</p>
            <p><strong>测试结果:</strong> 
                <span class="{'status-passed' if report.passed else 'status-failed'}">
                    {'通过' if report.passed else '失败'}
                </span>
            </p>
        </div>
        
        <div class="section">
            <h2>性能指标</h2>
            <div class="metrics">
                <div class="metric-card">
                    <div class="metric-title">总请求数</div>
                    <div class="metric-value">{report.metrics.total_requests}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-title">失败请求数</div>
                    <div class="metric-value">{report.metrics.total_failures}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-title">错误率</div>
                    <div class="metric-value">{report.metrics.failure_rate * 100:.2f}%</div>
                </div>
                <div class="metric-card">
                    <div class="metric-title">平均响应时间</div>
                    <div class="metric-value">{report.metrics.avg_response_time:.2f}ms</div>
                </div>
                <div class="metric-card">
                    <div class="metric-title">P95响应时间</div>
                    <div class="metric-value">{report.metrics.p95_response_time:.2f}ms</div>
                </div>
                <div class="metric-card">
                    <div class="metric-title">P99响应时间</div>
                    <div class="metric-value">{report.metrics.p99_response_time:.2f}ms</div>
                </div>
                <div class="metric-card">
                    <div class="metric-title">吞吐量</div>
                    <div class="metric-value">{report.metrics.requests_per_second:.2f} req/s</div>
                </div>
            </div>
        </div>
        
        <div class="section">
            <h2>性能瓶颈</h2>
            {self._generate_bottlenecks_html(report.bottlenecks)}
        </div>
        
        <div class="section">
            <h2>优化建议</h2>
            {self._generate_recommendations_html(report.recommendations)}
        </div>
    </div>
</body>
</html>
        """
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(html_template)
    
    def _generate_bottlenecks_html(self, bottlenecks: List[str]) -> str:
        """生成瓶颈 HTML"""
        if not bottlenecks:
            return "<p>未发现明显的性能瓶颈</p>"
        
        html = ""
        for bottleneck in bottlenecks:
            html += f'<div class="bottleneck">{bottleneck}</div>'
        return html
    
    def _generate_recommendations_html(self, recommendations: List[str]) -> str:
        """生成建议 HTML"""
        if not recommendations:
            return "<p>暂无优化建议</p>"
        
        html = ""
        for recommendation in recommendations:
            html += f'<div class="recommendation">{recommendation}</div>'
        return html
    
    def export_to_json(self, report: PerformanceReport, output_path: str):
        """导出 JSON 报告"""
        report_dict = {
            "test_name": report.test_name,
            "start_time": report.start_time,
            "end_time": report.end_time,
            "duration": report.duration,
            "user_count": report.user_count,
            "passed": report.passed,
            "metrics": {
                "total_requests": report.metrics.total_requests,
                "total_failures": report.metrics.total_failures,
                "avg_response_time": report.metrics.avg_response_time,
                "min_response_time": report.metrics.min_response_time,
                "max_response_time": report.metrics.max_response_time,
                "p50_response_time": report.metrics.p50_response_time,
                "p95_response_time": report.metrics.p95_response_time,
                "p99_response_time": report.metrics.p99_response_time,
                "requests_per_second": report.metrics.requests_per_second,
                "failure_rate": report.metrics.failure_rate,
            },
            "bottlenecks": report.bottlenecks,
            "recommendations": report.recommendations,
        }
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(report_dict, f, indent=2, ensure_ascii=False)
```

### 报告查看

测试完成后，报告会保存在 `tests/performance/reports/` 目录：

- **HTML 报告**：`performance_report_YYYYMMDD_HHMMSS.html`
- **JSON 报告**：`performance_report_YYYYMMDD_HHMMSS.json`

## 性能基准

### 响应时间基准

| 操作类型 | 优秀 | 良好 | 可接受 | 较差 |
|---------|------|------|--------|------|
| 简单查询 | < 200ms | < 500ms | < 1000ms | < 2000ms |
| 复杂查询 | < 500ms | < 1000ms | < 2000ms | < 3000ms |
| 写入操作 | < 300ms | < 800ms | < 1500ms | < 3000ms |

### 吞吐量基准

| 场景 | 优秀 | 良好 | 可接受 | 较差 |
|------|------|------|--------|------|
| 用户端 | > 500 req/s | > 200 req/s | > 100 req/s | > 50 req/s |
| 商家端 | > 200 req/s | > 100 req/s | > 50 req/s | > 20 req/s |
| 平台端 | > 100 req/s | > 50 req/s | > 20 req/s | > 10 req/s |

### 错误率基准

| 等级 | 错误率 |
|------|--------|
| 优秀 | < 0.1% |
| 良好 | < 0.5% |
| 可接受 | < 1% |
| 较差 | < 5% |

### 并发用户数基准

| 场景 | 最小并发 | 推荐并发 | 最大并发 |
|------|----------|----------|----------|
| 用户端 | 100 | 500 | 1000 |
| 商家端 | 20 | 50 | 100 |
| 平台端 | 5 | 10 | 20 |

## 性能优化建议

### 数据库优化

1. **添加索引**
   ```sql
   -- 为常用查询字段添加索引
   CREATE INDEX idx_user_username ON users(username);
   CREATE INDEX idx_appointment_status ON appointments(status);
   CREATE INDEX idx_service_merchant ON services(merchant_id);
   ```

2. **优化查询**
   - 避免 SELECT *
   - 使用 JOIN 代替子查询
   - 使用 LIMIT 限制结果集大小

3. **连接池配置**
   ```rust
   // 配置数据库连接池
   let pool = r2d2::Pool::builder()
       .max_size(20)
       .min_idle(Some(5))
       .connection_timeout(Duration::from_secs(30))
       .build(manager)?;
   ```

### 缓存优化

1. **使用 Redis 缓存**
   ```rust
   // 缓存热点数据
   let cached = redis.get("services:popular").await?;
   if cached.is_none() {
       let services = db.get_popular_services().await?;
       redis.setex("services:popular", 300, &services).await?;
   }
   ```

2. **本地缓存**
   ```rust
   // 使用内存缓存
   use lru::LruCache;
   let mut cache = LruCache::new(1000);
   ```

### 代码优化

1. **异步处理**
   ```rust
   // 使用异步 I/O
   async fn get_user(id: i32) -> Result<User> {
       let user = db.get_user(id).await?;
       Ok(user)
   }
   ```

2. **并发处理**
   ```rust
   // 并发执行多个任务
   let (users, services) = tokio::try_join!(
       get_users(),
       get_services()
   )?;
   ```

### 架构优化

1. **负载均衡**
   - 使用 Nginx 或 HAProxy
   - 部署多个服务实例

2. **微服务拆分**
   - 将不同业务模块拆分为独立服务
   - 使用消息队列解耦

3. **CDN 加速**
   - 静态资源使用 CDN
   - API 响应缓存

## 相关文档

- [测试用例编写指南](test_guide.md)
- [断言使用指南](assertion_guide.md)
- [测试数据管理指南](data_management.md)

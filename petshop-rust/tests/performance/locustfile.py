"""
Locust性能测试场景文件

该文件定义了宠物服务平台的各种性能测试场景，包括：
- 用户端API性能测试
- 商家端API性能测试
- 平台端API性能测试
- 混合场景性能测试
"""

import random
import json
from datetime import datetime, timedelta
from typing import Dict, Any

from locust import HttpUser, task, between, events
from locust.runners import MasterRunner, WorkerRunner

from performance_config import (
    UserLoadConfig,
    MerchantLoadConfig,
    AdminLoadConfig,
    PerformanceBenchmark,
    TestScenario,
)
from performance_reporter import (
    PerformanceReporter,
    create_test_results_from_locust,
)


class BaseUser(HttpUser):
    """
    基础用户类
    
    提供通用的用户行为和工具方法
    """
    
    abstract = True
    wait_time = between(1, 3)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.auth_token = None
        self.user_id = None
        self.config = None
    
    def on_start(self):
        """用户开始时的初始化"""
        pass
    
    def on_stop(self):
        """用户停止时的清理"""
        pass
    
    def get_headers(self) -> Dict[str, str]:
        """获取请求头"""
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
        }
        if self.auth_token:
            headers["Authorization"] = f"Bearer {self.auth_token}"
        return headers
    
    def login(self, username: str, password: str, endpoint: str) -> bool:
        """
        用户登录
        
        Args:
            username: 用户名
            password: 密码
            endpoint: 登录端点
        
        Returns:
            登录是否成功
        """
        payload = {
            "username": username,
            "password": password,
        }
        
        with self.client.post(
            endpoint,
            json=payload,
            headers={"Content-Type": "application/json"},
            catch_response=True,
            name=f"{endpoint.split('/')[-1]}_login",
        ) as response:
            if response.status_code == 200:
                data = response.json()
                self.auth_token = data.get("token") or data.get("access_token")
                self.user_id = data.get("user_id") or data.get("id")
                response.success()
                return True
            else:
                response.failure(f"登录失败: {response.status_code}")
                return False
    
    def logout(self, endpoint: str):
        """
        用户登出
        
        Args:
            endpoint: 登出端点
        """
        if self.auth_token:
            self.client.post(
                endpoint,
                headers=self.get_headers(),
                name=f"{endpoint.split('/')[-1]}_logout",
            )
            self.auth_token = None
            self.user_id = None


class UserBehavior(BaseUser):
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
        self.config = UserLoadConfig()
        self.test_users = [
            {"username": f"test_user_{i}", "password": "Test123456"}
            for i in range(1, 101)
        ]
    
    def on_start(self):
        """用户开始时登录"""
        user = random.choice(self.test_users)
        self.login(user["username"], user["password"], "/api/auth/login")
    
    @task(10)
    def login_task(self):
        """用户登录任务"""
        if self.auth_token:
            self.logout("/api/auth/logout")
        
        user = random.choice(self.test_users)
        self.login(user["username"], user["password"], "/api/auth/login")
    
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
                data = response.json()
                if data and "services" in data or "data" in data:
                    response.success()
                else:
                    response.failure("服务列表数据格式错误")
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
            if response.status_code == 200:
                response.success()
            elif response.status_code == 404:
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
    
    @task(15)
    def search_services(self):
        """搜索服务"""
        keywords = ["洗澡", "美容", "寄养", "医疗", "训练"]
        keyword = random.choice(keywords)
        
        params = {
            "keyword": keyword,
            "page": 1,
            "page_size": 20,
        }
        
        with self.client.get(
            "/api/search",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="search_services",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"搜索服务失败: {response.status_code}")


class MerchantBehavior(BaseUser):
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
        self.config = MerchantLoadConfig()
        self.test_merchants = [
            {"username": f"merchant_{i}", "password": "Merchant123"}
            for i in range(1, 21)
        ]
    
    def on_start(self):
        """商家开始时登录"""
        merchant = random.choice(self.test_merchants)
        self.login(merchant["username"], merchant["password"], "/api/merchant/auth/login")
    
    @task(10)
    def merchant_login_task(self):
        """商家登录任务"""
        if self.auth_token:
            self.logout("/api/merchant/auth/logout")
        
        merchant = random.choice(self.test_merchants)
        self.login(merchant["username"], merchant["password"], "/api/merchant/auth/login")
    
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
    
    @task(30)
    def update_service(self):
        """更新服务信息"""
        service_id = random.randint(1, 50)
        
        update_data = {
            "price": round(random.uniform(50.0, 500.0), 2),
            "description": f"更新服务描述 - {datetime.now().timestamp()}",
        }
        
        with self.client.put(
            f"/api/merchant/services/{service_id}",
            json=update_data,
            headers=self.get_headers(),
            catch_response=True,
            name="merchant_update_service",
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 404:
                response.success()
            else:
                response.failure(f"更新服务失败: {response.status_code}")
    
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
    
    @task(30)
    def confirm_appointment(self):
        """确认预约"""
        appointment_id = random.randint(1, 100)
        
        with self.client.post(
            f"/api/merchant/appointments/{appointment_id}/confirm",
            headers=self.get_headers(),
            catch_response=True,
            name="merchant_confirm_appointment",
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 404:
                response.success()
            else:
                response.failure(f"确认预约失败: {response.status_code}")
    
    @task(20)
    def view_statistics(self):
        """查看统计数据"""
        with self.client.get(
            "/api/merchant/statistics",
            headers=self.get_headers(),
            catch_response=True,
            name="merchant_view_statistics",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"获取统计数据失败: {response.status_code}")


class AdminBehavior(BaseUser):
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
        self.config = AdminLoadConfig()
        self.test_admins = [
            {"username": f"admin_{i}", "password": "Admin123!@#"}
            for i in range(1, 6)
        ]
    
    def on_start(self):
        """管理员开始时登录"""
        admin = random.choice(self.test_admins)
        self.login(admin["username"], admin["password"], "/api/admin/auth/login")
    
    @task(10)
    def admin_login_task(self):
        """管理员登录任务"""
        if self.auth_token:
            self.logout("/api/admin/auth/logout")
        
        admin = random.choice(self.test_admins)
        self.login(admin["username"], admin["password"], "/api/admin/auth/login")
    
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
    
    @task(30)
    def approve_merchant(self):
        """批准商家"""
        merchant_id = random.randint(1, 50)
        
        with self.client.post(
            f"/api/admin/merchants/{merchant_id}/approve",
            headers=self.get_headers(),
            catch_response=True,
            name="admin_approve_merchant",
        ) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 404:
                response.success()
            else:
                response.failure(f"批准商家失败: {response.status_code}")
    
    @task(20)
    def view_dashboard(self):
        """查看仪表板"""
        with self.client.get(
            "/api/admin/dashboard",
            headers=self.get_headers(),
            catch_response=True,
            name="admin_view_dashboard",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"获取仪表板数据失败: {response.status_code}")
    
    @task(15)
    def query_reviews(self):
        """查询评价"""
        params = {
            "page": random.randint(1, 5),
            "page_size": 20,
        }
        
        with self.client.get(
            "/api/admin/reviews",
            params=params,
            headers=self.get_headers(),
            catch_response=True,
            name="admin_query_reviews",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"查询评价失败: {response.status_code}")


class MixedUserBehavior(BaseUser):
    """
    混合用户行为场景
    
    模拟真实用户的完整行为流程：
    登录 → 浏览 → 下单 → 支付
    """
    
    weight = 5
    wait_time = between(2, 6)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.config = UserLoadConfig()
        self.test_users = [
            {"username": f"mixed_user_{i}", "password": "Test123456"}
            for i in range(1, 51)
        ]
        self.current_service_id = None
        self.current_appointment_id = None
    
    def on_start(self):
        """用户开始时登录"""
        user = random.choice(self.test_users)
        self.login(user["username"], user["password"], "/api/auth/login")
    
    @task(5)
    def complete_user_journey(self):
        """完整的用户旅程"""
        if not self.auth_token:
            user = random.choice(self.test_users)
            if not self.login(user["username"], user["password"], "/api/auth/login"):
                return
        
        self.browse_and_select_service()
        
        if self.current_service_id:
            self.create_appointment_for_service()
        
        if self.current_appointment_id:
            self.pay_appointment()
    
    def browse_and_select_service(self):
        """浏览并选择服务"""
        with self.client.get(
            "/api/services",
            params={"page": 1, "page_size": 20},
            headers=self.get_headers(),
            catch_response=True,
            name="mixed_browse_services",
        ) as response:
            if response.status_code == 200:
                data = response.json()
                services = data.get("services", []) or data.get("data", [])
                if services:
                    self.current_service_id = random.choice(services).get("id")
                response.success()
            else:
                response.failure(f"浏览服务失败: {response.status_code}")
    
    def create_appointment_for_service(self):
        """为选定的服务创建预约"""
        if not self.current_service_id:
            return
        
        appointment_data = {
            "service_id": self.current_service_id,
            "pet_id": random.randint(1, 10),
            "appointment_time": (datetime.now() + timedelta(days=random.randint(1, 3))).isoformat(),
            "notes": f"混合场景测试 - {datetime.now().timestamp()}",
        }
        
        with self.client.post(
            "/api/appointments",
            json=appointment_data,
            headers=self.get_headers(),
            catch_response=True,
            name="mixed_create_appointment",
        ) as response:
            if response.status_code in [200, 201]:
                data = response.json()
                self.current_appointment_id = data.get("id") or data.get("appointment_id")
                response.success()
            else:
                response.failure(f"创建预约失败: {response.status_code}")
    
    def pay_appointment(self):
        """支付预约"""
        if not self.current_appointment_id:
            return
        
        with self.client.post(
            f"/api/appointments/{self.current_appointment_id}/pay",
            json={"payment_method": random.choice(["wechat", "alipay", "card"])},
            headers=self.get_headers(),
            catch_response=True,
            name="mixed_pay_appointment",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"支付失败: {response.status_code}")
        
        self.current_service_id = None
        self.current_appointment_id = None


class MixedMerchantBehavior(BaseUser):
    """
    混合商家行为场景
    
    模拟商家的完整行为流程：
    登录 → 查看订单 → 处理订单
    """
    
    weight = 2
    wait_time = between(3, 8)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.config = MerchantLoadConfig()
        self.test_merchants = [
            {"username": f"mixed_merchant_{i}", "password": "Merchant123"}
            for i in range(1, 11)
        ]
        self.pending_appointments = []
    
    def on_start(self):
        """商家开始时登录"""
        merchant = random.choice(self.test_merchants)
        self.login(merchant["username"], merchant["password"], "/api/merchant/auth/login")
    
    @task(5)
    def complete_merchant_journey(self):
        """完整的商家旅程"""
        if not self.auth_token:
            merchant = random.choice(self.test_merchants)
            if not self.login(merchant["username"], merchant["password"], "/api/merchant/auth/login"):
                return
        
        self.fetch_pending_orders()
        
        if self.pending_appointments:
            self.process_random_order()
    
    def fetch_pending_orders(self):
        """获取待处理订单"""
        with self.client.get(
            "/api/merchant/appointments",
            params={"status": "pending", "page": 1, "page_size": 10},
            headers=self.get_headers(),
            catch_response=True,
            name="mixed_fetch_pending_orders",
        ) as response:
            if response.status_code == 200:
                data = response.json()
                appointments = data.get("appointments", []) or data.get("data", [])
                self.pending_appointments = [apt.get("id") for apt in appointments]
                response.success()
            else:
                response.failure(f"获取待处理订单失败: {response.status_code}")
    
    def process_random_order(self):
        """处理随机订单"""
        if not self.pending_appointments:
            return
        
        appointment_id = random.choice(self.pending_appointments)
        
        action = random.choice(["confirm", "complete"])
        
        with self.client.post(
            f"/api/merchant/appointments/{appointment_id}/{action}",
            headers=self.get_headers(),
            catch_response=True,
            name=f"mixed_{action}_appointment",
        ) as response:
            if response.status_code == 200:
                if appointment_id in self.pending_appointments:
                    self.pending_appointments.remove(appointment_id)
                response.success()
            else:
                response.failure(f"{action}订单失败: {response.status_code}")


class MixedAdminBehavior(BaseUser):
    """
    混合管理员行为场景
    
    模拟管理员的完整行为流程：
    登录 → 审核 → 查询
    """
    
    weight = 1
    wait_time = between(5, 15)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.config = AdminLoadConfig()
        self.test_admins = [
            {"username": f"mixed_admin_{i}", "password": "Admin123!@#"}
            for i in range(1, 4)
        ]
        self.pending_merchants = []
    
    def on_start(self):
        """管理员开始时登录"""
        admin = random.choice(self.test_admins)
        self.login(admin["username"], admin["password"], "/api/admin/auth/login")
    
    @task(5)
    def complete_admin_journey(self):
        """完整的管理员旅程"""
        if not self.auth_token:
            admin = random.choice(self.test_admins)
            if not self.login(admin["username"], admin["password"], "/api/admin/auth/login"):
                return
        
        self.fetch_pending_merchants()
        
        if self.pending_merchants:
            self.audit_random_merchant()
        
        self.check_system_health()
    
    def fetch_pending_merchants(self):
        """获取待审核商家"""
        with self.client.get(
            "/api/admin/merchants",
            params={"status": "pending", "page": 1, "page_size": 10},
            headers=self.get_headers(),
            catch_response=True,
            name="mixed_fetch_pending_merchants",
        ) as response:
            if response.status_code == 200:
                data = response.json()
                merchants = data.get("merchants", []) or data.get("data", [])
                self.pending_merchants = [m.get("id") for m in merchants]
                response.success()
            else:
                response.failure(f"获取待审核商家失败: {response.status_code}")
    
    def audit_random_merchant(self):
        """审核随机商家"""
        if not self.pending_merchants:
            return
        
        merchant_id = random.choice(self.pending_merchants)
        
        action = random.choice(["approve", "reject"])
        
        payload = {}
        if action == "reject":
            payload["reason"] = "性能测试拒绝原因"
        
        with self.client.post(
            f"/api/admin/merchants/{merchant_id}/{action}",
            json=payload,
            headers=self.get_headers(),
            catch_response=True,
            name=f"mixed_{action}_merchant",
        ) as response:
            if response.status_code == 200:
                if merchant_id in self.pending_merchants:
                    self.pending_merchants.remove(merchant_id)
                response.success()
            else:
                response.failure(f"{action}商家失败: {response.status_code}")
    
    def check_system_health(self):
        """检查系统健康状态"""
        with self.client.get(
            "/api/admin/system/health",
            headers=self.get_headers(),
            catch_response=True,
            name="mixed_check_system_health",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"检查系统健康失败: {response.status_code}")


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试开始时的回调"""
    print("\n" + "="*50)
    print("性能测试开始")
    print("="*50)
    print(f"测试环境: {environment.host}")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("="*50 + "\n")


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """测试结束时的回调"""
    print("\n" + "="*50)
    print("性能测试结束")
    print("="*50)
    print(f"结束时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("="*50 + "\n")
    
    if isinstance(environment.runner, MasterRunner):
        generate_final_report(environment)


def generate_final_report(environment):
    """生成最终性能测试报告"""
    print("\n正在生成性能测试报告...")
    
    stats = environment.stats
    
    test_results = {
        "test_name": "PetShop Performance Test",
        "start_time": stats.start_time,
        "end_time": datetime.now().isoformat(),
        "duration": (datetime.now() - datetime.fromtimestamp(stats.start_time)).total_seconds(),
        "user_count": environment.runner.user_count,
        "config": {
            "host": environment.host,
        },
        "stats": {
            "num_requests": stats.total.num_requests,
            "num_failures": stats.total.num_failures,
            "avg_response_time": stats.total.avg_response_time,
            "min_response_time": stats.total.min_response_time or 0,
            "max_response_time": stats.total.max_response_time or 0,
            "total_rps": stats.total.total_rps,
            "response_times": dict(stats.total.response_times),
        },
        "history": [],
    }
    
    benchmark = PerformanceBenchmark()
    reporter = PerformanceReporter(benchmark)
    
    report = reporter.generate_report(test_results)
    
    output_dir = "tests/performance/reports"
    import os
    os.makedirs(output_dir, exist_ok=True)
    
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    html_path = os.path.join(output_dir, f"performance_report_{timestamp}.html")
    json_path = os.path.join(output_dir, f"performance_report_{timestamp}.json")
    
    reporter.export_to_html(report, html_path)
    reporter.export_to_json(report, json_path)
    
    print(f"\n性能测试报告已生成:")
    print(f"  HTML报告: {html_path}")
    print(f"  JSON报告: {json_path}")
    
    print("\n性能测试摘要:")
    print(f"  总请求数: {report.metrics.total_requests}")
    print(f"  失败请求数: {report.metrics.total_failures}")
    print(f"  错误率: {report.metrics.failure_rate * 100:.2f}%")
    print(f"  平均响应时间: {report.metrics.avg_response_time:.2f}ms")
    print(f"  P95响应时间: {report.metrics.p95_response_time:.2f}ms")
    print(f"  P99响应时间: {report.metrics.p99_response_time:.2f}ms")
    print(f"  吞吐量: {report.metrics.requests_per_second:.2f} req/s")
    
    if report.bottlenecks:
        print("\n性能瓶颈:")
        for bottleneck in report.bottlenecks:
            print(f"  - {bottleneck}")
    
    if report.recommendations:
        print("\n优化建议:")
        for recommendation in report.recommendations:
            print(f"  - {recommendation}")

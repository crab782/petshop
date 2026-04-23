"""
Locust性能测试文件
包含登录、订单创建、订单查询等接口的性能测试
"""
import json
import random
import string
from datetime import datetime, timedelta

from locust import HttpUser, task, between, events
from locust.runners import MasterRunner, WorkerRunner

from config import performance_config


def generate_random_string(length=8):
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))


def generate_future_datetime():
    future_date = datetime.now() + timedelta(days=random.randint(1, 30))
    future_date = future_date.replace(
        hour=random.randint(9, 18),
        minute=random.choice([0, 15, 30, 45]),
        second=0,
        microsecond=0
    )
    return future_date.strftime("%Y-%m-%dT%H:%M:%S")


class UserLoginUser(HttpUser):
    """
    用户登录接口性能测试
    响应时间基准 < 500ms
    """
    wait_time = between(1, 3)
    
    def on_start(self):
        self.user_credentials = performance_config.TEST_USERS["user"]
        self.thresholds = performance_config.PERFORMANCE_THRESHOLDS["login"]
    
    @task(10)
    def login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        with self.client.post(
            "/api/auth/login",
            json=payload,
            catch_response=True,
            name="[User] Login"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Login failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")


class MerchantLoginUser(HttpUser):
    """
    商家登录接口性能测试
    响应时间基准 < 500ms
    """
    wait_time = between(1, 3)
    
    def on_start(self):
        self.merchant_credentials = performance_config.TEST_USERS["merchant"]
        self.thresholds = performance_config.PERFORMANCE_THRESHOLDS["login"]
    
    @task(10)
    def merchant_login(self):
        payload = {
            "loginIdentifier": self.merchant_credentials["loginIdentifier"],
            "password": self.merchant_credentials["password"]
        }
        
        with self.client.post(
            "/api/auth/merchant/login",
            json=payload,
            catch_response=True,
            name="[Merchant] Login"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Login failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")


class CreateAppointmentUser(HttpUser):
    """
    创建服务预约接口性能测试
    响应时间基准 < 1000ms
    """
    wait_time = between(2, 5)
    
    def on_start(self):
        self.user_credentials = performance_config.TEST_USERS["user"]
        self.test_data = performance_config.TEST_DATA
        self.thresholds = performance_config.PERFORMANCE_THRESHOLDS["create_order"]
        self.token = None
        self.login()
    
    def login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        response = self.client.post(
            "/api/auth/login",
            json=payload,
            name="[CreateAppointment] Login"
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("code") == 200 and data.get("data"):
                self.token = data["data"].get("token")
    
    @task(5)
    def create_appointment(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        payload = {
            "serviceId": self.test_data["service_id"],
            "petId": self.test_data["pet_id"],
            "appointmentTime": generate_future_datetime(),
            "remark": f"Performance test appointment - {generate_random_string()}"
        }
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        with self.client.post(
            "/api/user/appointments",
            json=payload,
            headers=headers,
            catch_response=True,
            name="[User] Create Appointment"
        ) as response:
            if response.status_code in [200, 201]:
                data = response.json()
                if data.get("success") or data.get("id"):
                    response.success()
                else:
                    response.failure(f"Create appointment failed: {data.get('error', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")


class CreateProductOrderUser(HttpUser):
    """
    创建商品订单接口性能测试
    响应时间基准 < 1000ms
    """
    wait_time = between(2, 5)
    
    def on_start(self):
        self.user_credentials = performance_config.TEST_USERS["user"]
        self.test_data = performance_config.TEST_DATA
        self.thresholds = performance_config.PERFORMANCE_THRESHOLDS["create_order"]
        self.token = None
        self.login()
    
    def login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        response = self.client.post(
            "/api/auth/login",
            json=payload,
            name="[CreateProductOrder] Login"
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("code") == 200 and data.get("data"):
                self.token = data["data"].get("token")
    
    @task(5)
    def create_order(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        payload = {
            "items": [
                {
                    "productId": self.test_data["product_id"],
                    "quantity": random.randint(1, 3)
                }
            ],
            "addressId": 1,
            "remark": f"Performance test order - {generate_random_string()}"
        }
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        with self.client.post(
            "/api/user/orders",
            json=payload,
            headers=headers,
            catch_response=True,
            name="[User] Create Product Order"
        ) as response:
            if response.status_code in [200, 201]:
                data = response.json()
                if data.get("orderId") or data.get("success"):
                    response.success()
                else:
                    response.failure(f"Create order failed: {data.get('error', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")


class QueryAppointmentsUser(HttpUser):
    """
    查询预约列表接口性能测试
    响应时间基准 < 300ms
    """
    wait_time = between(1, 2)
    
    def on_start(self):
        self.user_credentials = performance_config.TEST_USERS["user"]
        self.thresholds = performance_config.PERFORMANCE_THRESHOLDS["query_list"]
        self.token = None
        self.login()
    
    def login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        response = self.client.post(
            "/api/auth/login",
            json=payload,
            name="[QueryAppointments] Login"
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("code") == 200 and data.get("data"):
                self.token = data["data"].get("token")
    
    @task(10)
    def query_appointments(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        params = {
            "page": random.randint(0, 5),
            "pageSize": random.choice([10, 20, 50])
        }
        
        with self.client.get(
            "/api/user/appointments",
            params=params,
            headers=headers,
            catch_response=True,
            name="[User] Query Appointments"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Query failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(5)
    def query_appointments_with_filter(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        params = {
            "status": random.choice(["pending", "confirmed", "completed", "cancelled"]),
            "page": random.randint(0, 3),
            "pageSize": 10
        }
        
        with self.client.get(
            "/api/user/appointments",
            params=params,
            headers=headers,
            catch_response=True,
            name="[User] Query Appointments with Filter"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Query failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")


class QueryProductOrdersUser(HttpUser):
    """
    查询商品订单列表接口性能测试
    响应时间基准 < 300ms
    """
    wait_time = between(1, 2)
    
    def on_start(self):
        self.user_credentials = performance_config.TEST_USERS["user"]
        self.thresholds = performance_config.PERFORMANCE_THRESHOLDS["query_list"]
        self.token = None
        self.login()
    
    def login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        response = self.client.post(
            "/api/auth/login",
            json=payload,
            name="[QueryProductOrders] Login"
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("code") == 200 and data.get("data"):
                self.token = data["data"].get("token")
    
    @task(10)
    def query_orders(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        params = {
            "page": random.randint(1, 5),
            "pageSize": random.choice([10, 20, 50])
        }
        
        with self.client.get(
            "/api/user/orders",
            params=params,
            headers=headers,
            catch_response=True,
            name="[User] Query Product Orders"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Query failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(5)
    def query_orders_with_filter(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        params = {
            "status": random.choice(["pending", "paid", "shipped", "completed", "cancelled"]),
            "page": random.randint(1, 3),
            "pageSize": 10
        }
        
        with self.client.get(
            "/api/user/orders",
            params=params,
            headers=headers,
            catch_response=True,
            name="[User] Query Product Orders with Filter"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Query failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")


class MixedScenarioUser(HttpUser):
    """
    混合场景性能测试
    模拟真实用户行为，包含登录、查询、创建订单等操作
    权重比例：
    - 登录: 10%
    - 查询预约: 25%
    - 查询订单: 25%
    - 创建预约: 15%
    - 创建订单: 15%
    - 查看个人信息: 10%
    """
    wait_time = between(1, 3)
    
    def on_start(self):
        self.user_credentials = performance_config.TEST_USERS["user"]
        self.test_data = performance_config.TEST_DATA
        self.token = None
        self.login()
    
    def login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        response = self.client.post(
            "/api/auth/login",
            json=payload,
            name="[Mixed] Login"
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("code") == 200 and data.get("data"):
                self.token = data["data"].get("token")
    
    @task(10)
    def re_login(self):
        payload = {
            "loginIdentifier": self.user_credentials["loginIdentifier"],
            "password": self.user_credentials["password"]
        }
        
        with self.client.post(
            "/api/auth/login",
            json=payload,
            catch_response=True,
            name="[Mixed] Login"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200 and data.get("data"):
                    self.token = data["data"].get("token")
                    response.success()
                else:
                    response.failure(f"Login failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(25)
    def query_appointments(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        params = {
            "page": random.randint(0, 3),
            "pageSize": 10
        }
        
        with self.client.get(
            "/api/user/appointments",
            params=params,
            headers=headers,
            catch_response=True,
            name="[Mixed] Query Appointments"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Query failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(25)
    def query_orders(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        params = {
            "page": random.randint(1, 3),
            "pageSize": 10
        }
        
        with self.client.get(
            "/api/user/orders",
            params=params,
            headers=headers,
            catch_response=True,
            name="[Mixed] Query Orders"
        ) as response:
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 200:
                    response.success()
                else:
                    response.failure(f"Query failed: {data.get('message', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(15)
    def create_appointment(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        payload = {
            "serviceId": self.test_data["service_id"],
            "petId": self.test_data["pet_id"],
            "appointmentTime": generate_future_datetime(),
            "remark": f"Mixed scenario test - {generate_random_string()}"
        }
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        with self.client.post(
            "/api/user/appointments",
            json=payload,
            headers=headers,
            catch_response=True,
            name="[Mixed] Create Appointment"
        ) as response:
            if response.status_code in [200, 201]:
                data = response.json()
                if data.get("success") or data.get("id"):
                    response.success()
                else:
                    response.failure(f"Create appointment failed: {data.get('error', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(15)
    def create_order(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        payload = {
            "items": [
                {
                    "productId": self.test_data["product_id"],
                    "quantity": random.randint(1, 2)
                }
            ],
            "addressId": 1,
            "remark": f"Mixed scenario test - {generate_random_string()}"
        }
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        with self.client.post(
            "/api/user/orders",
            json=payload,
            headers=headers,
            catch_response=True,
            name="[Mixed] Create Order"
        ) as response:
            if response.status_code in [200, 201]:
                data = response.json()
                if data.get("orderId") or data.get("success"):
                    response.success()
                else:
                    response.failure(f"Create order failed: {data.get('error', 'Unknown error')}")
            else:
                response.failure(f"HTTP {response.status_code}")
    
    @task(10)
    def get_profile(self):
        if not self.token:
            self.login()
            if not self.token:
                return
        
        headers = {"Authorization": f"Bearer {self.token}"}
        
        with self.client.get(
            "/api/user/profile",
            headers=headers,
            catch_response=True,
            name="[Mixed] Get Profile"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"HTTP {response.status_code}")


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    print("\n" + "=" * 60)
    print("性能测试开始")
    print("=" * 60)
    print(f"API地址: {performance_config.API_BASE_URL}")
    print(f"测试用户: {performance_config.TEST_USERS['user']['loginIdentifier']}")
    print(f"测试商家: {performance_config.TEST_USERS['merchant']['loginIdentifier']}")
    print("=" * 60 + "\n")


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    print("\n" + "=" * 60)
    print("性能测试结束")
    print("=" * 60)
    
    if hasattr(environment, "stats"):
        stats = environment.stats
        
        print("\n性能测试统计摘要:")
        print("-" * 60)
        
        for name, entry in stats.entries.items():
            if entry.num_requests > 0:
                print(f"\n接口: {name}")
                print(f"  总请求数: {entry.num_requests}")
                print(f"  失败数: {entry.num_failures}")
                print(f"  失败率: {entry.fail_ratio * 100:.2f}%")
                print(f"  平均响应时间: {entry.avg_response_time:.2f}ms")
                print(f"  最小响应时间: {entry.min_response_time:.2f}ms")
                print(f"  最大响应时间: {entry.max_response_time:.2f}ms")
                print(f"  中位数响应时间: {entry.median_response_time:.2f}ms")
                print(f"  95%响应时间: {entry.get_response_time_percentile(0.95):.2f}ms")
                print(f"  RPS: {entry.total_rps:.2f}")
        
        print("\n" + "-" * 60)
        print(f"总请求数: {stats.total.num_requests}")
        print(f"总失败数: {stats.total.num_failures}")
        print(f"总失败率: {stats.total.fail_ratio * 100:.2f}%")
        print(f"总平均响应时间: {stats.total.avg_response_time:.2f}ms")
        print(f"总RPS: {stats.total.total_rps:.2f}")
    
    print("=" * 60 + "\n")

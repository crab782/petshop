"""
测试数据准备脚本
"""

import logging
import random
import string
from dataclasses import dataclass, field
from typing import Any, Dict, List, Optional

from tests.config import config
from tests.utils import HTTPClient, TokenManager

logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s")
logger = logging.getLogger(__name__)


@dataclass
class TestData:
    """测试数据存储类"""

    user_id: Optional[int] = None
    user_token: Optional[str] = None
    merchant_id: Optional[int] = None
    merchant_token: Optional[str] = None
    service_ids: List[int] = field(default_factory=list)
    product_ids: List[int] = field(default_factory=list)
    pet_ids: List[int] = field(default_factory=list)
    address_ids: List[int] = field(default_factory=list)
    appointment_ids: List[int] = field(default_factory=list)
    order_ids: List[int] = field(default_factory=list)
    review_ids: List[int] = field(default_factory=list)

    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            "user_id": self.user_id,
            "user_token": self.user_token,
            "merchant_id": self.merchant_id,
            "merchant_token": self.merchant_token,
            "service_ids": self.service_ids,
            "product_ids": self.product_ids,
            "pet_ids": self.pet_ids,
            "address_ids": self.address_ids,
            "appointment_ids": self.appointment_ids,
            "order_ids": self.order_ids,
            "review_ids": self.review_ids,
        }


class TestDataGenerator:
    """测试数据生成器"""

    TEST_PREFIX = "test_"

    def __init__(self, http_client: HTTPClient = None):
        self.http_client = http_client or HTTPClient()
        self.token_manager = TokenManager()
        self.test_data = TestData()

    def _generate_random_string(self, length: int = 8) -> str:
        """生成随机字符串"""
        return "".join(random.choices(string.ascii_lowercase + string.digits, k=length))

    def _generate_test_username(self) -> str:
        """生成测试用户名"""
        return f"{self.TEST_PREFIX}user_{self._generate_random_string()}"

    def _generate_test_email(self) -> str:
        """生成测试邮箱"""
        return f"{self.TEST_PREFIX}{self._generate_random_string()}@test.com"

    def _generate_test_phone(self) -> str:
        """生成测试手机号"""
        return f"138{random.randint(10000000, 99999999)}"

    def create_test_user(self) -> Dict[str, Any]:
        """创建测试用户"""
        logger.info("Creating test user...")

        user_data = {
            "username": self._generate_test_username(),
            "password": "Test123456",
            "email": self._generate_test_email(),
            "phone": self._generate_test_phone(),
        }

        try:
            response = self.http_client.post("/api/auth/register", json_data=user_data)

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                user_id = data.get("id") or data.get("userId")

                login_response = self.http_client.post(
                    "/api/auth/login", json_data={"username": user_data["username"], "password": user_data["password"]}
                )

                if login_response.status_code == 200:
                    token = login_response.json().get("data", {}).get("token")
                    self.test_data.user_id = user_id
                    self.test_data.user_token = token
                    self.token_manager.set_token("user", token)

                    logger.info(f"Test user created successfully - ID: {user_id}")
                    return {
                        "id": user_id,
                        "token": token,
                        "username": user_data["username"],
                        "email": user_data["email"],
                        "phone": user_data["phone"],
                    }
            else:
                logger.error(f"Failed to create test user: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test user: {str(e)}")
            return {}

        return {}

    def create_test_merchant(self) -> Dict[str, Any]:
        """创建测试商家"""
        logger.info("Creating test merchant...")

        merchant_data = {
            "name": f"{self.TEST_PREFIX}商家_{self._generate_random_string()}",
            "contactPerson": f"{self.TEST_PREFIX}联系人",
            "phone": self._generate_test_phone(),
            "email": self._generate_test_email(),
            "password": "Merchant123456",
            "address": f"{self.TEST_PREFIX}测试地址123号",
        }

        try:
            response = self.http_client.post("/api/auth/merchant/register", json_data=merchant_data)

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                merchant_id = data.get("id") or data.get("merchantId")

                approve_response = self.http_client.post(
                    f"/api/admin/merchants/{merchant_id}/approve", user_type="admin"
                )

                if approve_response.status_code == 200:
                    login_response = self.http_client.post(
                        "/api/auth/merchant/login",
                        json_data={"username": merchant_data["email"], "password": merchant_data["password"]},
                    )

                    if login_response.status_code == 200:
                        token = login_response.json().get("data", {}).get("token")
                        self.test_data.merchant_id = merchant_id
                        self.test_data.merchant_token = token
                        self.token_manager.set_token("merchant", token)

                        logger.info(f"Test merchant created successfully - ID: {merchant_id}")
                        return {
                            "id": merchant_id,
                            "token": token,
                            "name": merchant_data["name"],
                            "email": merchant_data["email"],
                            "phone": merchant_data["phone"],
                        }
            else:
                logger.error(f"Failed to create test merchant: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test merchant: {str(e)}")
            return {}

        return {}

    def create_test_service(self, merchant_id: int = None) -> Dict[str, Any]:
        """创建测试服务"""
        logger.info("Creating test service...")

        merchant_id = merchant_id or self.test_data.merchant_id
        if not merchant_id:
            logger.error("No merchant ID available")
            return {}

        service_data = {
            "merchantId": merchant_id,
            "name": f"{self.TEST_PREFIX}服务_{self._generate_random_string()}",
            "description": f"{self.TEST_PREFIX}测试服务描述",
            "price": round(random.uniform(50, 500), 2),
            "duration": random.randint(30, 120),
            "category": random.choice(["美容", "医疗", "寄养", "训练"]),
            "image": "https://example.com/test-service.jpg",
            "status": "enabled",
        }

        try:
            response = self.http_client.post("/api/merchant/services", json_data=service_data, user_type="merchant")

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                service_id = data.get("id") or data.get("serviceId")

                if service_id:
                    self.test_data.service_ids.append(service_id)
                    logger.info(f"Test service created successfully - ID: {service_id}")
                    return {
                        "id": service_id,
                        "name": service_data["name"],
                        "price": service_data["price"],
                        "duration": service_data["duration"],
                    }
            else:
                logger.error(f"Failed to create test service: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test service: {str(e)}")
            return {}

        return {}

    def create_test_product(self, merchant_id: int = None) -> Dict[str, Any]:
        """创建测试商品"""
        logger.info("Creating test product...")

        merchant_id = merchant_id or self.test_data.merchant_id
        if not merchant_id:
            logger.error("No merchant ID available")
            return {}

        product_data = {
            "merchantId": merchant_id,
            "name": f"{self.TEST_PREFIX}商品_{self._generate_random_string()}",
            "description": f"{self.TEST_PREFIX}测试商品描述",
            "price": round(random.uniform(10, 200), 2),
            "stock": random.randint(10, 100),
            "image": "https://example.com/test-product.jpg",
            "status": "enabled",
            "category": random.choice(["食品", "用品", "玩具", "保健"]),
        }

        try:
            response = self.http_client.post("/api/merchant/products", json_data=product_data, user_type="merchant")

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                product_id = data.get("id") or data.get("productId")

                if product_id:
                    self.test_data.product_ids.append(product_id)
                    logger.info(f"Test product created successfully - ID: {product_id}")
                    return {
                        "id": product_id,
                        "name": product_data["name"],
                        "price": product_data["price"],
                        "stock": product_data["stock"],
                    }
            else:
                logger.error(f"Failed to create test product: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test product: {str(e)}")
            return {}

        return {}

    def create_test_pet(self, user_id: int = None) -> Dict[str, Any]:
        """创建测试宠物"""
        logger.info("Creating test pet...")

        user_id = user_id or self.test_data.user_id
        if not user_id:
            logger.error("No user ID available")
            return {}

        pet_data = {
            "userId": user_id,
            "name": f"{self.TEST_PREFIX}宠物_{self._generate_random_string()}",
            "type": random.choice(["狗", "猫", "兔子", "仓鼠"]),
            "breed": random.choice(["金毛", "哈士奇", "英短", "布偶"]),
            "age": random.randint(1, 10),
            "gender": random.choice(["male", "female"]),
            "avatar": "https://example.com/test-pet.jpg",
            "description": f"{self.TEST_PREFIX}测试宠物描述",
        }

        try:
            response = self.http_client.post("/api/user/pets", json_data=pet_data, user_type="user")

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                pet_id = data.get("id") or data.get("petId")

                if pet_id:
                    self.test_data.pet_ids.append(pet_id)
                    logger.info(f"Test pet created successfully - ID: {pet_id}")
                    return {
                        "id": pet_id,
                        "name": pet_data["name"],
                        "type": pet_data["type"],
                        "breed": pet_data["breed"],
                    }
            else:
                logger.error(f"Failed to create test pet: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test pet: {str(e)}")
            return {}

        return {}

    def create_test_address(self, user_id: int = None) -> Dict[str, Any]:
        """创建测试地址"""
        logger.info("Creating test address...")

        user_id = user_id or self.test_data.user_id
        if not user_id:
            logger.error("No user ID available")
            return {}

        address_data = {
            "userId": user_id,
            "contactName": f"{self.TEST_PREFIX}收货人",
            "phone": self._generate_test_phone(),
            "province": "北京市",
            "city": "北京市",
            "district": "朝阳区",
            "detailAddress": f"{self.TEST_PREFIX}测试详细地址123号",
            "isDefault": False,
        }

        try:
            response = self.http_client.post("/api/user/addresses", json_data=address_data, user_type="user")

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                address_id = data.get("id") or data.get("addressId")

                if address_id:
                    self.test_data.address_ids.append(address_id)
                    logger.info(f"Test address created successfully - ID: {address_id}")
                    return {
                        "id": address_id,
                        "contactName": address_data["contactName"],
                        "phone": address_data["phone"],
                        "detailAddress": address_data["detailAddress"],
                    }
            else:
                logger.error(f"Failed to create test address: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test address: {str(e)}")
            return {}

        return {}

    def create_test_appointment(self, service_id: int = None, pet_id: int = None) -> Dict[str, Any]:
        """创建测试预约"""
        logger.info("Creating test appointment...")

        service_id = service_id or (self.test_data.service_ids[0] if self.test_data.service_ids else None)
        pet_id = pet_id or (self.test_data.pet_ids[0] if self.test_data.pet_ids else None)

        if not service_id or not pet_id:
            logger.error("Service ID or Pet ID not available")
            return {}

        appointment_data = {
            "serviceId": service_id,
            "petId": pet_id,
            "appointmentTime": "2024-12-31 14:00:00",
            "notes": f"{self.TEST_PREFIX}测试预约备注",
        }

        try:
            response = self.http_client.post("/api/user/appointments", json_data=appointment_data, user_type="user")

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                appointment_id = data.get("id") or data.get("appointmentId")

                if appointment_id:
                    self.test_data.appointment_ids.append(appointment_id)
                    logger.info(f"Test appointment created successfully - ID: {appointment_id}")
                    return {"id": appointment_id, "serviceId": service_id, "petId": pet_id}
            else:
                logger.error(f"Failed to create test appointment: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test appointment: {str(e)}")
            return {}

        return {}

    def create_test_order(self, product_id: int = None, address_id: int = None) -> Dict[str, Any]:
        """创建测试订单"""
        logger.info("Creating test order...")

        product_id = product_id or (self.test_data.product_ids[0] if self.test_data.product_ids else None)
        address_id = address_id or (self.test_data.address_ids[0] if self.test_data.address_ids else None)

        if not product_id or not address_id:
            logger.error("Product ID or Address ID not available")
            return {}

        order_data = {
            "items": [{"productId": product_id, "quantity": random.randint(1, 3)}],
            "addressId": address_id,
            "notes": f"{self.TEST_PREFIX}测试订单备注",
        }

        try:
            response = self.http_client.post("/api/user/orders", json_data=order_data, user_type="user")

            if response.status_code in [200, 201]:
                data = response.json().get("data", {})
                order_id = data.get("id") or data.get("orderId")

                if order_id:
                    self.test_data.order_ids.append(order_id)
                    logger.info(f"Test order created successfully - ID: {order_id}")
                    return {"id": order_id, "productId": product_id, "addressId": address_id}
            else:
                logger.error(f"Failed to create test order: {response.text}")
                return {}

        except Exception as e:
            logger.error(f"Error creating test order: {str(e)}")
            return {}

        return {}

    def cleanup_test_data(self) -> None:
        """清理测试数据"""
        logger.info("Cleaning up test data...")

        for order_id in self.test_data.order_ids:
            try:
                self.http_client.delete(f"/api/user/orders/{order_id}", user_type="user")
                logger.info(f"Deleted order: {order_id}")
            except Exception as e:
                logger.warning(f"Failed to delete order {order_id}: {str(e)}")

        for appointment_id in self.test_data.appointment_ids:
            try:
                self.http_client.delete(f"/api/user/appointments/{appointment_id}", user_type="user")
                logger.info(f"Deleted appointment: {appointment_id}")
            except Exception as e:
                logger.warning(f"Failed to delete appointment {appointment_id}: {str(e)}")

        for review_id in self.test_data.review_ids:
            try:
                self.http_client.delete(f"/api/user/reviews/{review_id}", user_type="user")
                logger.info(f"Deleted review: {review_id}")
            except Exception as e:
                logger.warning(f"Failed to delete review {review_id}: {str(e)}")

        for address_id in self.test_data.address_ids:
            try:
                self.http_client.delete(f"/api/user/addresses/{address_id}", user_type="user")
                logger.info(f"Deleted address: {address_id}")
            except Exception as e:
                logger.warning(f"Failed to delete address {address_id}: {str(e)}")

        for pet_id in self.test_data.pet_ids:
            try:
                self.http_client.delete(f"/api/user/pets/{pet_id}", user_type="user")
                logger.info(f"Deleted pet: {pet_id}")
            except Exception as e:
                logger.warning(f"Failed to delete pet {pet_id}: {str(e)}")

        for product_id in self.test_data.product_ids:
            try:
                self.http_client.delete(f"/api/merchant/products/{product_id}", user_type="merchant")
                logger.info(f"Deleted product: {product_id}")
            except Exception as e:
                logger.warning(f"Failed to delete product {product_id}: {str(e)}")

        for service_id in self.test_data.service_ids:
            try:
                self.http_client.delete(f"/api/merchant/services/{service_id}", user_type="merchant")
                logger.info(f"Deleted service: {service_id}")
            except Exception as e:
                logger.warning(f"Failed to delete service {service_id}: {str(e)}")

        if self.test_data.merchant_id:
            try:
                self.http_client.delete(f"/api/admin/merchants/{self.test_data.merchant_id}", user_type="admin")
                logger.info(f"Deleted merchant: {self.test_data.merchant_id}")
            except Exception as e:
                logger.warning(f"Failed to delete merchant {self.test_data.merchant_id}: {str(e)}")

        if self.test_data.user_id:
            try:
                self.http_client.delete(f"/api/admin/users/{self.test_data.user_id}", user_type="admin")
                logger.info(f"Deleted user: {self.test_data.user_id}")
            except Exception as e:
                logger.warning(f"Failed to delete user {self.test_data.user_id}: {str(e)}")

        self.token_manager.clear_all()
        logger.info("Test data cleanup completed")


def setup_test_environment(
    create_services: bool = True,
    create_products: bool = True,
    create_pets: bool = True,
    create_addresses: bool = True,
    create_appointments: bool = False,
    create_orders: bool = False,
) -> TestData:
    """
    初始化测试环境

    Args:
        create_services: 是否创建测试服务
        create_products: 是否创建测试商品
        create_pets: 是否创建测试宠物
        create_addresses: 是否创建测试地址
        create_appointments: 是否创建测试预约
        create_orders: 是否创建测试订单

    Returns:
        TestData: 测试数据对象
    """
    logger.info("=" * 60)
    logger.info("Setting up test environment...")
    logger.info("=" * 60)

    generator = TestDataGenerator()

    user = generator.create_test_user()
    if not user:
        logger.error("Failed to create test user, aborting setup")
        return TestData()

    merchant = generator.create_test_merchant()
    if not merchant:
        logger.error("Failed to create test merchant, aborting setup")
        return TestData()

    if create_services:
        for _ in range(2):
            generator.create_test_service()

    if create_products:
        for _ in range(2):
            generator.create_test_product()

    if create_pets:
        for _ in range(2):
            generator.create_test_pet()

    if create_addresses:
        for _ in range(2):
            generator.create_test_address()

    if create_appointments and generator.test_data.service_ids and generator.test_data.pet_ids:
        generator.create_test_appointment()

    if create_orders and generator.test_data.product_ids and generator.test_data.address_ids:
        generator.create_test_order()

    logger.info("=" * 60)
    logger.info("Test environment setup completed")
    logger.info(f"User ID: {generator.test_data.user_id}")
    logger.info(f"Merchant ID: {generator.test_data.merchant_id}")
    logger.info(f"Services: {len(generator.test_data.service_ids)}")
    logger.info(f"Products: {len(generator.test_data.product_ids)}")
    logger.info(f"Pets: {len(generator.test_data.pet_ids)}")
    logger.info(f"Addresses: {len(generator.test_data.address_ids)}")
    logger.info("=" * 60)

    return generator.test_data


def teardown_test_environment(test_data: TestData = None) -> None:
    """
    清理测试环境

    Args:
        test_data: 要清理的测试数据对象，如果为None则清理所有带test_前缀的数据
    """
    logger.info("=" * 60)
    logger.info("Tearing down test environment...")
    logger.info("=" * 60)

    if test_data:
        generator = TestDataGenerator()
        generator.test_data = test_data
        generator.cleanup_test_data()
    else:
        generator = TestDataGenerator()
        generator.cleanup_test_data()

    logger.info("=" * 60)
    logger.info("Test environment teardown completed")
    logger.info("=" * 60)


class TestDataInitializer:
    """测试数据初始化器"""

    _instance = None
    _initialized = False

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self):
        if not self._initialized:
            self.generator = TestDataGenerator()
            self.test_data = None

    def init_all(self) -> TestData:
        """初始化所有测试数据"""
        logger.info("Initializing all test data...")

        self.test_data = setup_test_environment(
            create_services=True,
            create_products=True,
            create_pets=True,
            create_addresses=True,
            create_appointments=False,
            create_orders=False,
        )

        self._initialized = True
        return self.test_data

    def init_basic(self) -> TestData:
        """初始化基础测试数据（仅用户和商家）"""
        logger.info("Initializing basic test data...")

        user = self.generator.create_test_user()
        merchant = self.generator.create_test_merchant()

        self.test_data = self.generator.test_data
        self._initialized = True
        return self.test_data

    def init_for_appointments(self) -> TestData:
        """初始化预约测试所需的数据"""
        logger.info("Initializing test data for appointments...")

        self.test_data = setup_test_environment(
            create_services=True,
            create_products=False,
            create_pets=True,
            create_addresses=False,
            create_appointments=True,
            create_orders=False,
        )

        self._initialized = True
        return self.test_data

    def init_for_orders(self) -> TestData:
        """初始化订单测试所需的数据"""
        logger.info("Initializing test data for orders...")

        self.test_data = setup_test_environment(
            create_services=False,
            create_products=True,
            create_pets=False,
            create_addresses=True,
            create_appointments=False,
            create_orders=True,
        )

        self._initialized = True
        return self.test_data

    def cleanup(self) -> None:
        """清理测试数据"""
        logger.info("Cleaning up test data...")

        if self.generator:
            self.generator.cleanup_test_data()

        self._initialized = False
        self.test_data = None

    def get_test_data(self) -> Optional[TestData]:
        """获取测试数据"""
        return self.test_data

    def is_initialized(self) -> bool:
        """检查是否已初始化"""
        return self._initialized

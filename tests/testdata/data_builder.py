"""
测试数据构建器模块

使用工厂模式创建各类测试数据，支持自定义字段和默认值。
"""

import random
import string
from datetime import datetime, timedelta
from decimal import Decimal
from typing import Any, Dict, Optional


class TestDataBuilder:
    """
    测试数据构建器

    使用工厂模式创建各类测试数据，支持自定义字段覆盖默认值。

    使用示例：
        builder = TestDataBuilder()

        # 使用默认值构建用户数据
        user_data = builder.build_user()

        # 自定义部分字段
        user_data = builder.build_user(
            username="custom_user",
            email="custom@example.com"
        )
    """

    def __init__(self):
        self._counter = 0

    def _generate_unique_string(self, prefix: str = "test", length: int = 8) -> str:
        """生成唯一字符串"""
        self._counter += 1
        random_str = "".join(random.choices(string.ascii_lowercase + string.digits, k=length))
        return f"{prefix}_{random_str}_{self._counter}"

    def _generate_phone(self) -> str:
        """生成随机手机号"""
        prefixes = ["138", "139", "150", "151", "152", "158", "159", "186", "187", "188"]
        return random.choice(prefixes) + "".join(random.choices(string.digits, k=8))

    def _generate_email(self, username: Optional[str] = None) -> str:
        """生成随机邮箱"""
        if username is None:
            username = self._generate_unique_string("user")
        domains = ["example.com", "test.com", "mail.com", "demo.com"]
        return f"{username}@{random.choice(domains)}"

    def build_user(self, **kwargs) -> Dict[str, Any]:
        """
        构建用户测试数据

        Args:
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 用户数据字典

        默认字段：
            - username: 自动生成的唯一用户名
            - email: 自动生成的邮箱
            - password: 默认密码 "password123"
            - phone: 自动生成的手机号
            - avatar: 默认头像URL
            - status: 默认状态 "active"
        """
        username = kwargs.get("username", self._generate_unique_string("user"))
        defaults = {
            "username": username,
            "email": kwargs.get("email", self._generate_email(username)),
            "password": kwargs.get("password", "password123"),
            "phone": kwargs.get("phone", self._generate_phone()),
            "avatar": kwargs.get("avatar", "https://example.com/avatar/default.jpg"),
            "status": kwargs.get("status", "active"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_merchant(self, **kwargs) -> Dict[str, Any]:
        """
        构建商家测试数据

        Args:
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 商家数据字典

        默认字段：
            - name: 自动生成的商家名称
            - contact_person: 默认联系人
            - phone: 自动生成的手机号
            - email: 自动生成的邮箱
            - password: 默认密码 "password123"
            - address: 默认地址
            - logo: 默认Logo URL
            - status: 默认状态 "approved"
        """
        name = kwargs.get("name", self._generate_unique_string("merchant"))
        defaults = {
            "name": name,
            "contact_person": kwargs.get("contact_person", "测试联系人"),
            "phone": kwargs.get("phone", self._generate_phone()),
            "email": kwargs.get("email", self._generate_email(name)),
            "password": kwargs.get("password", "password123"),
            "address": kwargs.get("address", "测试地址123号"),
            "logo": kwargs.get("logo", "https://example.com/logo/default.jpg"),
            "status": kwargs.get("status", "approved"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_admin(self, **kwargs) -> Dict[str, Any]:
        """
        构建管理员测试数据

        Args:
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 管理员数据字典

        默认字段：
            - username: 自动生成的唯一用户名
            - email: 自动生成的邮箱
            - password: 默认密码 "admin123"
            - phone: 自动生成的手机号
            - role: 默认角色 "admin"
        """
        username = kwargs.get("username", self._generate_unique_string("admin"))
        defaults = {
            "username": username,
            "email": kwargs.get("email", self._generate_email(username)),
            "password": kwargs.get("password", "admin123"),
            "phone": kwargs.get("phone", self._generate_phone()),
            "role": kwargs.get("role", "admin"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_service(self, merchant_id: Optional[int] = None, **kwargs) -> Dict[str, Any]:
        """
        构建服务测试数据

        Args:
            merchant_id: 商家ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 服务数据字典

        默认字段：
            - merchant_id: 商家ID
            - name: 自动生成的服务名称
            - description: 默认描述
            - price: 默认价格 99.00
            - duration: 默认时长 60分钟
            - category: 默认分类
            - image: 默认图片URL
            - status: 默认状态 "enabled"
        """
        defaults = {
            "merchant_id": merchant_id,
            "name": kwargs.get("name", self._generate_unique_string("service")),
            "description": kwargs.get("description", "这是一个测试服务描述"),
            "price": kwargs.get("price", Decimal("99.00")),
            "duration": kwargs.get("duration", 60),
            "category": kwargs.get("category", "宠物美容"),
            "image": kwargs.get("image", "https://example.com/service/default.jpg"),
            "status": kwargs.get("status", "enabled"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_product(self, merchant_id: Optional[int] = None, **kwargs) -> Dict[str, Any]:
        """
        构建商品测试数据

        Args:
            merchant_id: 商家ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 商品数据字典

        默认字段：
            - merchant_id: 商家ID
            - name: 自动生成的商品名称
            - description: 默认描述
            - price: 默认价格 49.99
            - stock: 默认库存 100
            - image: 默认图片URL
            - status: 默认状态 "enabled"
            - low_stock_threshold: 默认低库存阈值 10
            - category: 默认分类
        """
        defaults = {
            "merchant_id": merchant_id,
            "name": kwargs.get("name", self._generate_unique_string("product")),
            "description": kwargs.get("description", "这是一个测试商品描述"),
            "price": kwargs.get("price", Decimal("49.99")),
            "stock": kwargs.get("stock", 100),
            "image": kwargs.get("image", "https://example.com/product/default.jpg"),
            "status": kwargs.get("status", "enabled"),
            "low_stock_threshold": kwargs.get("low_stock_threshold", 10),
            "category": kwargs.get("category", "宠物用品"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_appointment(
        self,
        user_id: Optional[int] = None,
        merchant_id: Optional[int] = None,
        service_id: Optional[int] = None,
        pet_id: Optional[int] = None,
        **kwargs,
    ) -> Dict[str, Any]:
        """
        构建预约测试数据

        Args:
            user_id: 用户ID（可选）
            merchant_id: 商家ID（可选）
            service_id: 服务ID（可选）
            pet_id: 宠物ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 预约数据字典

        默认字段：
            - user_id: 用户ID
            - merchant_id: 商家ID
            - service_id: 服务ID
            - pet_id: 宠物ID
            - appointment_time: 默认预约时间（明天）
            - status: 默认状态 "pending"
            - total_price: 默认总价 99.00
            - notes: 默认备注
        """
        appointment_time = kwargs.get("appointment_time", datetime.now() + timedelta(days=1))
        defaults = {
            "user_id": user_id,
            "merchant_id": merchant_id,
            "service_id": service_id,
            "pet_id": pet_id,
            "appointment_time": appointment_time,
            "status": kwargs.get("status", "pending"),
            "total_price": kwargs.get("total_price", Decimal("99.00")),
            "notes": kwargs.get("notes", "测试预约备注"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_order(self, user_id: Optional[int] = None, merchant_id: Optional[int] = None, **kwargs) -> Dict[str, Any]:
        """
        构建订单测试数据

        Args:
            user_id: 用户ID（可选）
            merchant_id: 商家ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 订单数据字典

        默认字段：
            - order_no: 自动生成的订单号
            - user_id: 用户ID
            - merchant_id: 商家ID
            - total_price: 默认总价 199.00
            - freight: 默认运费 0
            - status: 默认状态 "pending"
            - pay_method: 默认支付方式
            - remark: 默认备注
            - shipping_address: 默认收货地址
        """
        order_no = kwargs.get("order_no", self._generate_order_no())
        defaults = {
            "order_no": order_no,
            "user_id": user_id,
            "merchant_id": merchant_id,
            "total_price": kwargs.get("total_price", Decimal("199.00")),
            "freight": kwargs.get("freight", Decimal("0")),
            "status": kwargs.get("status", "pending"),
            "pay_method": kwargs.get("pay_method", "wechat"),
            "remark": kwargs.get("remark", "测试订单备注"),
            "shipping_address": kwargs.get("shipping_address", "测试收货地址123号"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_review(
        self,
        user_id: Optional[int] = None,
        merchant_id: Optional[int] = None,
        service_id: Optional[int] = None,
        appointment_id: Optional[int] = None,
        **kwargs,
    ) -> Dict[str, Any]:
        """
        构建评价测试数据

        Args:
            user_id: 用户ID（可选）
            merchant_id: 商家ID（可选）
            service_id: 服务ID（可选）
            appointment_id: 预约ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 评价数据字典

        默认字段：
            - user_id: 用户ID
            - merchant_id: 商家ID
            - service_id: 服务ID
            - appointment_id: 预约ID
            - rating: 默认评分 5
            - comment: 默认评价内容
            - status: 默认状态 "pending"
        """
        defaults = {
            "user_id": user_id,
            "merchant_id": merchant_id,
            "service_id": service_id,
            "appointment_id": appointment_id,
            "rating": kwargs.get("rating", 5),
            "comment": kwargs.get("comment", "非常满意的服务！"),
            "reply_content": kwargs.get("reply_content", None),
            "reply_time": kwargs.get("reply_time", None),
            "status": kwargs.get("status", "pending"),
            "created_at": kwargs.get("created_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_pet(self, user_id: Optional[int] = None, **kwargs) -> Dict[str, Any]:
        """
        构建宠物测试数据

        Args:
            user_id: 用户ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 宠物数据字典

        默认字段：
            - user_id: 用户ID
            - name: 自动生成的宠物名称
            - type: 默认类型 "狗"
            - breed: 默认品种
            - age: 默认年龄 2
            - gender: 默认性别 "male"
            - avatar: 默认头像URL
            - description: 默认描述
        """
        pet_types = ["狗", "猫", "兔子", "仓鼠", "鸟"]
        breeds = {
            "狗": ["金毛", "拉布拉多", "柯基", "哈士奇", "泰迪"],
            "猫": ["英短", "美短", "布偶", "暹罗", "橘猫"],
            "兔子": ["荷兰垂耳兔", "安哥拉兔", "迷你兔"],
            "仓鼠": ["金丝熊", "银狐", "布丁"],
            "鸟": ["虎皮鹦鹉", "金丝雀", "文鸟"],
        }

        pet_type = kwargs.get("type", random.choice(pet_types))
        breed = kwargs.get("breed", random.choice(breeds.get(pet_type, ["普通品种"])))

        defaults = {
            "user_id": user_id,
            "name": kwargs.get("name", self._generate_unique_string("pet")),
            "type": pet_type,
            "breed": breed,
            "age": kwargs.get("age", random.randint(1, 10)),
            "gender": kwargs.get("gender", random.choice(["male", "female"])),
            "avatar": kwargs.get("avatar", "https://example.com/pet/default.jpg"),
            "description": kwargs.get("description", "这是一只可爱的宠物"),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def build_address(self, user_id: Optional[int] = None, **kwargs) -> Dict[str, Any]:
        """
        构建地址测试数据

        Args:
            user_id: 用户ID（可选）
            **kwargs: 自定义字段值，将覆盖默认值

        Returns:
            Dict[str, Any]: 地址数据字典

        默认字段：
            - user_id: 用户ID
            - contact_name: 默认联系人
            - phone: 自动生成的手机号
            - province: 默认省份
            - city: 默认城市
            - district: 默认区县
            - detail_address: 默认详细地址
            - is_default: 默认是否为默认地址 False
        """
        provinces_cities = [
            ("北京市", "北京市", "朝阳区"),
            ("上海市", "上海市", "浦东新区"),
            ("广东省", "广州市", "天河区"),
            ("广东省", "深圳市", "南山区"),
            ("浙江省", "杭州市", "西湖区"),
        ]

        province, city, district = random.choice(provinces_cities)

        defaults = {
            "user_id": user_id,
            "contact_name": kwargs.get("contact_name", "测试收货人"),
            "phone": kwargs.get("phone", self._generate_phone()),
            "province": kwargs.get("province", province),
            "city": kwargs.get("city", city),
            "district": kwargs.get("district", district),
            "detail_address": kwargs.get("detail_address", "测试详细地址123号"),
            "is_default": kwargs.get("is_default", False),
            "created_at": kwargs.get("created_at", datetime.now()),
            "updated_at": kwargs.get("updated_at", datetime.now()),
        }
        defaults.update(kwargs)
        return defaults

    def _generate_order_no(self) -> str:
        """生成订单号"""
        timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
        random_str = "".join(random.choices(string.digits, k=6))
        return f"ORD{timestamp}{random_str}"

    def build_batch(self, data_type: str, count: int, **kwargs) -> list:
        """
        批量构建测试数据

        Args:
            data_type: 数据类型（user, merchant, service, product等）
            count: 构建数量
            **kwargs: 传递给构建方法的参数

        Returns:
            list: 测试数据列表

        使用示例：
            builder.build_batch('user', 5, status='active')
        """
        build_methods = {
            "user": self.build_user,
            "merchant": self.build_merchant,
            "service": self.build_service,
            "product": self.build_product,
            "appointment": self.build_appointment,
            "order": self.build_order,
            "review": self.build_review,
            "pet": self.build_pet,
            "address": self.build_address,
        }

        if data_type not in build_methods:
            raise ValueError(f"不支持的数据类型: {data_type}")

        build_method = build_methods[data_type]
        return [build_method(**kwargs) for _ in range(count)]

"""
测试数据管理器模块

管理测试数据的生命周期，包括数据准备、获取、清理和隔离。
支持事务管理和数据隔离装饰器。
"""

import functools
import uuid
from contextlib import contextmanager
from typing import Any, Callable, Dict, List, Optional

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import (
    cleanup_appointments,
    cleanup_merchants,
    cleanup_orders,
    cleanup_products,
    cleanup_reviews,
    cleanup_services,
    cleanup_users,
)


class TestDataManager:
    """
    测试数据管理器

    管理测试数据的完整生命周期，包括：
    - 数据准备：创建测试数据并插入数据库
    - 数据获取：获取已创建的测试数据
    - 数据清理：清理测试数据
    - 数据隔离：使用装饰器实现测试数据隔离

    使用示例：
        manager = TestDataManager()

        # 准备测试数据
        user = manager.prepare_test_data('user', username="test_user")

        # 获取测试数据
        retrieved_user = manager.get_test_data(user['id'])

        # 清理测试数据
        manager.cleanup_test_data(user['id'])

        # 使用装饰器自动管理数据生命周期
        @manager.isolate_test_data
        def test_function():
            user = manager.prepare_test_data('user')
            # 测试代码...
            # 测试结束后自动清理
    """

    def __init__(self):
        self._builder = TestDataBuilder()
        self._test_data_registry: Dict[str, Dict[str, Any]] = {}
        self._data_type_mapping = {
            "user": {"table": "user", "id_field": "id", "cleanup_func": cleanup_users},
            "merchant": {"table": "merchant", "id_field": "id", "cleanup_func": cleanup_merchants},
            "service": {"table": "service", "id_field": "id", "cleanup_func": cleanup_services},
            "product": {"table": "product", "id_field": "id", "cleanup_func": cleanup_products},
            "appointment": {"table": "appointment", "id_field": "id", "cleanup_func": cleanup_appointments},
            "order": {"table": "product_order", "id_field": "id", "cleanup_func": cleanup_orders},
            "review": {"table": "review", "id_field": "id", "cleanup_func": cleanup_reviews},
        }

    def prepare_test_data(
        self, data_type: str, use_db: bool = False, db_connection: Optional[Any] = None, **kwargs
    ) -> Dict[str, Any]:
        """
        准备测试数据

        Args:
            data_type: 数据类型（user, merchant, service, product等）
            use_db: 是否插入数据库（默认False，仅构建数据）
            db_connection: 数据库连接对象（如果use_db为True）
            **kwargs: 传递给数据构建器的参数

        Returns:
            Dict[str, Any]: 测试数据字典

        使用示例：
            # 仅构建数据
            user_data = manager.prepare_test_data('user', username="test")

            # 构建并插入数据库
            user_data = manager.prepare_test_data(
                'user',
                use_db=True,
                db_connection=conn,
                username="test"
            )
        """
        if data_type not in self._data_type_mapping:
            raise ValueError(f"不支持的数据类型: {data_type}")

        build_method = getattr(self._builder, f"build_{data_type}", None)
        if not build_method:
            raise ValueError(f"未找到构建方法: build_{data_type}")

        data = build_method(**kwargs)
        data_id = str(uuid.uuid4())

        if use_db and db_connection:
            data = self._insert_to_db(data_type, data, db_connection)
            if "id" in data:
                data_id = str(data["id"])

        self._test_data_registry[data_id] = {"data_type": data_type, "data": data}

        return data

    def _insert_to_db(self, data_type: str, data: Dict[str, Any], db_connection: Any) -> Dict[str, Any]:
        """
        将数据插入数据库

        Args:
            data_type: 数据类型
            data: 数据字典
            db_connection: 数据库连接对象

        Returns:
            Dict[str, Any]: 插入后的数据（包含ID）

        注意：此方法需要根据实际的数据库连接类型实现
        """
        mapping = self._data_type_mapping[data_type]
        table = mapping["table"]

        try:
            if hasattr(db_connection, "cursor"):
                cursor = db_connection.cursor()

                columns = ", ".join(data.keys())
                placeholders = ", ".join(["%s"] * len(data))
                sql = f"INSERT INTO {table} ({columns}) VALUES ({placeholders})"

                cursor.execute(sql, list(data.values()))
                db_connection.commit()

                if hasattr(cursor, "lastrowid"):
                    data["id"] = cursor.lastrowid

                cursor.close()
            else:
                raise NotImplementedError("不支持的数据库连接类型")
        except Exception as e:
            raise RuntimeError(f"插入数据库失败: {str(e)}")

        return data

    def get_test_data(self, data_id: str) -> Optional[Dict[str, Any]]:
        """
        获取测试数据

        Args:
            data_id: 数据ID

        Returns:
            Optional[Dict[str, Any]]: 测试数据字典，如果不存在返回None
        """
        registry_entry = self._test_data_registry.get(data_id)
        if registry_entry:
            return registry_entry["data"]
        return None

    def cleanup_test_data(self, data_id: str, db_connection: Optional[Any] = None) -> bool:
        """
        清理指定的测试数据

        Args:
            data_id: 数据ID
            db_connection: 数据库连接对象（可选）

        Returns:
            bool: 是否成功清理
        """
        registry_entry = self._test_data_registry.get(data_id)
        if not registry_entry:
            return False

        data_type = registry_entry["data_type"]
        data = registry_entry["data"]

        if db_connection and "id" in data:
            mapping = self._data_type_mapping[data_type]
            cleanup_func = mapping["cleanup_func"]
            cleanup_func([data["id"]], db_connection)

        del self._test_data_registry[data_id]
        return True

    def cleanup_all_test_data(self, db_connection: Optional[Any] = None) -> int:
        """
        清理所有测试数据

        Args:
            db_connection: 数据库连接对象（可选）

        Returns:
            int: 清理的数据数量
        """
        count = 0
        for data_id in list(self._test_data_registry.keys()):
            if self.cleanup_test_data(data_id, db_connection):
                count += 1
        return count

    def isolate_test_data(self, test_func: Callable) -> Callable:
        """
        测试数据隔离装饰器

        自动管理测试数据的生命周期，测试结束后自动清理。

        Args:
            test_func: 测试函数

        Returns:
            Callable: 包装后的函数

        使用示例：
            @manager.isolate_test_data
            def test_user_creation():
                user = manager.prepare_test_data('user')
                # 测试代码...
                # 测试结束后自动清理
        """

        @functools.wraps(test_func)
        def wrapper(*args, **kwargs):
            try:
                result = test_func(*args, **kwargs)
                return result
            finally:
                self.cleanup_all_test_data()

        return wrapper

    @contextmanager
    def transaction_context(self, db_connection: Any):
        """
        事务上下文管理器

        在事务中执行操作，支持自动回滚。

        Args:
            db_connection: 数据库连接对象

        Yields:
            数据库连接对象

        使用示例：
            with manager.transaction_context(conn) as conn:
                user = manager.prepare_test_data('user', use_db=True, db_connection=conn)
                # 测试代码...
                # 事务自动回滚
        """
        try:
            if hasattr(db_connection, "begin"):
                db_connection.begin()
            yield db_connection
        except Exception as e:
            if hasattr(db_connection, "rollback"):
                db_connection.rollback()
            raise e
        finally:
            if hasattr(db_connection, "rollback"):
                db_connection.rollback()

    def get_all_test_data(self) -> Dict[str, Dict[str, Any]]:
        """
        获取所有测试数据

        Returns:
            Dict[str, Dict[str, Any]]: 所有测试数据的注册表
        """
        return self._test_data_registry.copy()

    def get_test_data_by_type(self, data_type: str) -> List[Dict[str, Any]]:
        """
        按类型获取测试数据

        Args:
            data_type: 数据类型

        Returns:
            List[Dict[str, Any]]: 指定类型的所有测试数据
        """
        return [entry["data"] for entry in self._test_data_registry.values() if entry["data_type"] == data_type]

    def register_test_data(self, data_type: str, data: Dict[str, Any], data_id: Optional[str] = None) -> str:
        """
        手动注册测试数据

        Args:
            data_type: 数据类型
            data: 数据字典
            data_id: 数据ID（可选，默认自动生成）

        Returns:
            str: 数据ID
        """
        if data_id is None:
            data_id = str(uuid.uuid4())

        self._test_data_registry[data_id] = {"data_type": data_type, "data": data}

        return data_id

    def unregister_test_data(self, data_id: str) -> bool:
        """
        取消注册测试数据（不清理数据库）

        Args:
            data_id: 数据ID

        Returns:
            bool: 是否成功取消注册
        """
        if data_id in self._test_data_registry:
            del self._test_data_registry[data_id]
            return True
        return False

    def clear_registry(self):
        """
        清空注册表（不清理数据库）
        """
        self._test_data_registry.clear()

    def __enter__(self):
        """上下文管理器入口"""
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """上下文管理器退出，自动清理所有数据"""
        self.cleanup_all_test_data()
        return False

"""
业务逻辑断言工具

提供业务逻辑相关的断言功能，支持独立函数调用和链式调用。

使用示例：
    # 独立函数调用
    from tests.assertions.business_assertions import assert_data_consistency

    assert_data_consistency(data1, data2, ["id", "name"])
    assert_status_transition("pending", "confirmed", allowed_transitions)

    # 链式调用
    from tests.assertions.business_assertions import BusinessAssertionBuilder

    builder = BusinessAssertionBuilder(response)
    builder.assert_data_consistency(expected_data, ["id", "name"])
"""

import json
from typing import Any, Callable, Dict, List, Set, Union

import requests


class BusinessAssertionBuilder:
    """
    业务逻辑断言构建器

    提供链式调用接口验证业务逻辑

    Attributes:
        response: requests.Response对象
        _json_data: 缓存的JSON数据
    """

    def __init__(self, response: requests.Response = None):
        """
        初始化业务逻辑断言构建器

        Args:
            response: requests.Response对象（可选）
        """
        self.response = response
        self._json_data = None

    def _get_json_data(self) -> Any:
        """
        获取JSON数据（带缓存）

        Returns:
            Any: 解析后的JSON数据

        Raises:
            ValueError: 当响应不是有效的JSON时抛出
        """
        if self._json_data is None:
            if self.response is None:
                raise ValueError("未提供response对象")
            try:
                self._json_data = self.response.json()
            except json.JSONDecodeError as e:
                raise ValueError(f"响应不是有效的JSON格式: {e}\n响应内容: {self.response.text[:500]}")
        return self._json_data

    def assert_data_consistency(
        self, data1: Union[Dict, Any], data2: Union[Dict, Any], fields: List[str] = None
    ) -> "BusinessAssertionBuilder":
        """
        验证数据一致性

        比较两个数据对象在指定字段上的一致性

        Args:
            data1: 第一个数据对象
            data2: 第二个数据对象
            fields: 要比较的字段列表，如果为None则比较所有字段

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当数据不一致时抛出

        Example:
            builder.assert_data_consistency(user_data, db_data, ["id", "name", "email"])
        """
        if fields:
            for field in fields:
                value1 = data1.get(field) if isinstance(data1, dict) else getattr(data1, field, None)
                value2 = data2.get(field) if isinstance(data2, dict) else getattr(data2, field, None)

                assert value1 == value2, f"字段 '{field}' 数据不一致:\n" f"data1: {value1}\n" f"data2: {value2}"
        else:
            if isinstance(data1, dict) and isinstance(data2, dict):
                assert data1 == data2, (
                    f"数据不一致:\n"
                    f"data1: {json.dumps(data1, ensure_ascii=False, indent=2)}\n"
                    f"data2: {json.dumps(data2, ensure_ascii=False, indent=2)}"
                )
            else:
                assert data1 == data2, f"数据不一致: {data1} != {data2}"

        return self

    def assert_status_transition(
        self, current_status: str, new_status: str, allowed_transitions: Dict[str, Set[str]]
    ) -> "BusinessAssertionBuilder":
        """
        验证状态转换

        验证状态转换是否符合业务规则

        Args:
            current_status: 当前状态
            new_status: 新状态
            allowed_transitions: 允许的状态转换映射
                {
                    "pending": {"confirmed", "cancelled"},
                    "confirmed": {"completed", "cancelled"},
                    "completed": set(),
                    "cancelled": set()
                }

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态转换不允许时抛出

        Example:
            allowed = {
                "pending": {"confirmed", "cancelled"},
                "confirmed": {"completed", "cancelled"},
                "completed": set(),
                "cancelled": set()
            }
            builder.assert_status_transition("pending", "confirmed", allowed)
        """
        if current_status not in allowed_transitions:
            raise AssertionError(
                f"当前状态 '{current_status}' 不在允许的状态列表中\n" f"允许的状态: {list(allowed_transitions.keys())}"
            )

        allowed_next_states = allowed_transitions[current_status]

        if new_status not in allowed_next_states:
            raise AssertionError(
                f"状态转换不允许:\n"
                f"当前状态: {current_status}\n"
                f"尝试转换到: {new_status}\n"
                f"允许的下一状态: {allowed_next_states}"
            )

        return self

    def assert_business_rule(
        self, data: Any, rule_name: str, rule_checker: Callable[[Any], bool], error_message: str = None
    ) -> "BusinessAssertionBuilder":
        """
        验证业务规则

        使用自定义规则检查器验证业务规则

        Args:
            data: 要验证的数据
            rule_name: 规则名称
            rule_checker: 规则检查函数，返回True表示通过，False表示失败
            error_message: 自定义错误信息

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当业务规则验证失败时抛出

        Example:
            def check_age(user):
                return user.get('age', 0) >= 18

            builder.assert_business_rule(
                user_data,
                "用户年龄必须大于等于18岁",
                check_age
            )
        """
        try:
            result = rule_checker(data)
        except Exception as e:
            raise AssertionError(f"业务规则 '{rule_name}' 检查时发生异常:\n" f"异常信息: {str(e)}")

        if not result:
            default_message = f"业务规则 '{rule_name}' 验证失败"
            raise AssertionError(error_message or default_message)

        return self

    def assert_pagination(
        self, response: requests.Response, page: int, size: int, total: int = None, data_key: str = "data"
    ) -> "BusinessAssertionBuilder":
        """
        验证分页数据

        验证分页响应的数据结构是否正确

        Args:
            response: requests.Response对象
            page: 期望的页码
            size: 期望的每页大小
            total: 期望的总记录数（可选）
            data_key: 数据字段的键名，默认为"data"

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当分页数据验证失败时抛出

        Example:
            builder.assert_pagination(response, page=1, size=10, total=100)
        """
        json_data = response.json()

        if data_key not in json_data:
            raise AssertionError(f"响应中不存在数据字段 '{data_key}'\n" f"可用的字段: {list(json_data.keys())}")

        data = json_data[data_key]

        if not isinstance(data, list):
            raise AssertionError(f"数据字段 '{data_key}' 不是列表类型\n" f"实际类型: {type(data).__name__}")

        actual_size = len(data)

        if total is not None:
            if "total" in json_data:
                actual_total = json_data["total"]
                assert actual_total == total, f"总记录数不匹配:\n" f"期望: {total}\n" f"实际: {actual_total}"

        if "page" in json_data:
            actual_page = json_data["page"]
            assert actual_page == page, f"页码不匹配:\n" f"期望: {page}\n" f"实际: {actual_page}"

        if "size" in json_data:
            actual_size_field = json_data["size"]
            assert actual_size_field == size, f"每页大小不匹配:\n" f"期望: {size}\n" f"实际: {actual_size_field}"

        assert actual_size <= size, f"返回的数据数量超过每页大小:\n" f"每页大小: {size}\n" f"实际返回: {actual_size}"

        return self

    def assert_unique_field(self, data_list: List[Dict], field_name: str) -> "BusinessAssertionBuilder":
        """
        验证列表中字段值唯一性

        Args:
            data_list: 数据列表
            field_name: 字段名

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当字段值不唯一时抛出

        Example:
            builder.assert_unique_field(users, "email")
        """
        values = []
        duplicates = []

        for item in data_list:
            if isinstance(item, dict) and field_name in item:
                value = item[field_name]
                if value in values:
                    duplicates.append(value)
                values.append(value)

        if duplicates:
            raise AssertionError(f"字段 '{field_name}' 的值不唯一:\n" f"重复的值: {set(duplicates)}")

        return self

    def assert_data_integrity(
        self, data: Dict, required_fields: List[str], optional_fields: List[str] = None
    ) -> "BusinessAssertionBuilder":
        """
        验证数据完整性

        验证数据包含所有必需字段，且只包含必需或可选字段

        Args:
            data: 要验证的数据
            required_fields: 必需字段列表
            optional_fields: 可选字段列表

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当数据完整性验证失败时抛出

        Example:
            builder.assert_data_integrity(
                user_data,
                required_fields=["id", "name"],
                optional_fields=["email", "phone"]
            )
        """
        if not isinstance(data, dict):
            raise AssertionError(f"数据不是字典类型，实际类型: {type(data).__name__}")

        missing_fields = [field for field in required_fields if field not in data]

        if missing_fields:
            raise AssertionError(f"缺少必需字段: {missing_fields}\n" f"可用的字段: {list(data.keys())}")

        if optional_fields is not None:
            allowed_fields = set(required_fields) | set(optional_fields)
            extra_fields = [field for field in data.keys() if field not in allowed_fields]

            if extra_fields:
                raise AssertionError(f"包含不允许的字段: {extra_fields}\n" f"允许的字段: {allowed_fields}")

        return self

    def assert_time_order(
        self, data_list: List[Dict], time_field: str, ascending: bool = True
    ) -> "BusinessAssertionBuilder":
        """
        验证时间顺序

        验证列表中的时间字段是否按指定顺序排列

        Args:
            data_list: 数据列表
            time_field: 时间字段名
            ascending: 是否升序排列，True为升序，False为降序

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当时间顺序不正确时抛出

        Example:
            builder.assert_time_order(orders, "created_at", ascending=False)
        """
        from datetime import datetime

        if len(data_list) < 2:
            return self

        times = []
        for item in data_list:
            if isinstance(item, dict) and time_field in item:
                time_value = item[time_field]
                if isinstance(time_value, str):
                    try:
                        time_value = datetime.fromisoformat(time_value.replace("Z", "+00:00"))
                    except ValueError:
                        pass
                times.append(time_value)
            else:
                raise AssertionError(f"数据项中不存在时间字段 '{time_field}'")

        for i in range(len(times) - 1):
            if ascending:
                if times[i] > times[i + 1]:
                    raise AssertionError(
                        f"时间顺序不正确（期望升序）:\n" f"位置 {i}: {times[i]}\n" f"位置 {i + 1}: {times[i + 1]}"
                    )
            else:
                if times[i] < times[i + 1]:
                    raise AssertionError(
                        f"时间顺序不正确（期望降序）:\n" f"位置 {i}: {times[i]}\n" f"位置 {i + 1}: {times[i + 1]}"
                    )

        return self

    def assert_value_in_range(
        self,
        value: Union[int, float],
        min_value: Union[int, float],
        max_value: Union[int, float],
        field_name: str = "value",
    ) -> "BusinessAssertionBuilder":
        """
        验证值在指定范围内

        Args:
            value: 要验证的值
            min_value: 最小值
            max_value: 最大值
            field_name: 字段名（用于错误信息）

        Returns:
            BusinessAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当值不在范围内时抛出

        Example:
            builder.assert_value_in_range(age, 0, 120, "age")
        """
        if not isinstance(value, (int, float)):
            raise AssertionError(f"{field_name} 不是数字类型，实际类型: {type(value).__name__}")

        if value < min_value or value > max_value:
            raise AssertionError(
                f"{field_name} 不在有效范围内:\n" f"有效范围: [{min_value}, {max_value}]\n" f"实际值: {value}"
            )

        return self


def assert_data_consistency(data1: Union[Dict, Any], data2: Union[Dict, Any], fields: List[str] = None) -> None:
    """
    验证数据一致性

    Args:
        data1: 第一个数据对象
        data2: 第二个数据对象
        fields: 要比较的字段列表

    Raises:
        AssertionError: 当数据不一致时抛出
    """
    BusinessAssertionBuilder().assert_data_consistency(data1, data2, fields)


def assert_status_transition(current_status: str, new_status: str, allowed_transitions: Dict[str, Set[str]]) -> None:
    """
    验证状态转换

    Args:
        current_status: 当前状态
        new_status: 新状态
        allowed_transitions: 允许的状态转换映射

    Raises:
        AssertionError: 当状态转换不允许时抛出
    """
    BusinessAssertionBuilder().assert_status_transition(current_status, new_status, allowed_transitions)


def assert_business_rule(
    data: Any, rule_name: str, rule_checker: Callable[[Any], bool], error_message: str = None
) -> None:
    """
    验证业务规则

    Args:
        data: 要验证的数据
        rule_name: 规则名称
        rule_checker: 规则检查函数
        error_message: 自定义错误信息

    Raises:
        AssertionError: 当业务规则验证失败时抛出
    """
    BusinessAssertionBuilder().assert_business_rule(data, rule_name, rule_checker, error_message)


def assert_pagination(
    response: requests.Response, page: int, size: int, total: int = None, data_key: str = "data"
) -> None:
    """
    验证分页数据

    Args:
        response: requests.Response对象
        page: 期望的页码
        size: 期望的每页大小
        total: 期望的总记录数
        data_key: 数据字段的键名

    Raises:
        AssertionError: 当分页数据验证失败时抛出
    """
    BusinessAssertionBuilder(response).assert_pagination(response, page, size, total, data_key)


__all__ = [
    "BusinessAssertionBuilder",
    "assert_data_consistency",
    "assert_status_transition",
    "assert_business_rule",
    "assert_pagination",
]

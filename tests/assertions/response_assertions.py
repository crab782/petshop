"""
响应体断言工具

提供响应体相关的断言功能，支持独立函数调用和链式调用。

使用示例：
    # 独立函数调用
    from tests.assertions.response_assertions import assert_response_key_exists

    assert_response_key_exists(response, "data")
    assert_response_key_value(response, "status", "success")

    # 链式调用
    from tests.assertions.response_assertions import ResponseAssertionBuilder

    builder = ResponseAssertionBuilder(response)
    builder.assert_response_key_exists("data").assert_response_not_empty()
"""

import json
from typing import Any, Dict, List, Union

import requests


class ResponseAssertionBuilder:
    """
    响应体断言构建器

    提供链式调用接口验证响应体内容

    Attributes:
        response: requests.Response对象
        _json_data: 缓存的JSON数据
    """

    def __init__(self, response: requests.Response):
        """
        初始化响应体断言构建器

        Args:
            response: requests.Response对象
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
            try:
                self._json_data = self.response.json()
            except json.JSONDecodeError as e:
                raise ValueError(f"响应不是有效的JSON格式: {e}\n响应内容: {self.response.text[:500]}")
        return self._json_data

    def assert_response_body(self, expected_body: Union[Dict, List]) -> "ResponseAssertionBuilder":
        """
        验证完整响应体

        Args:
            expected_body: 期望的响应体

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应体不匹配时抛出

        Example:
            builder.assert_response_body({"status": "success", "data": {}})
        """
        actual_body = self._get_json_data()
        assert actual_body == expected_body, (
            f"响应体验证失败:\n"
            f"期望: {json.dumps(expected_body, ensure_ascii=False, indent=2)}\n"
            f"实际: {json.dumps(actual_body, ensure_ascii=False, indent=2)}"
        )
        return self

    def assert_response_contains(self, expected_data: Dict) -> "ResponseAssertionBuilder":
        """
        验证响应包含特定数据

        Args:
            expected_data: 期望包含的数据（键值对）

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应不包含指定数据时抛出

        Example:
            builder.assert_response_contains({"status": "success"})
        """
        json_data = self._get_json_data()

        for key, expected_value in expected_data.items():
            assert key in json_data, (
                f"响应不包含键 '{key}'\n" f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
            )
            actual_value = json_data[key]
            assert actual_value == expected_value, (
                f"键 '{key}' 的值不匹配:\n" f"期望: {expected_value}\n" f"实际: {actual_value}"
            )
        return self

    def assert_response_key_exists(self, key: str) -> "ResponseAssertionBuilder":
        """
        验证响应包含特定键

        Args:
            key: 期望存在的键名

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应不包含指定键时抛出

        Example:
            builder.assert_response_key_exists("data")
        """
        json_data = self._get_json_data()
        assert key in json_data, (
            f"响应不包含键 '{key}'\n"
            f"可用的键: {list(json_data.keys()) if isinstance(json_data, dict) else 'N/A'}\n"
            f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
        )
        return self

    def assert_response_key_value(self, key: str, expected_value: Any) -> "ResponseAssertionBuilder":
        """
        验证响应键值

        Args:
            key: 键名
            expected_value: 期望的值

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当键不存在或值不匹配时抛出

        Example:
            builder.assert_response_key_value("status", "success")
        """
        json_data = self._get_json_data()

        assert key in json_data, (
            f"响应不包含键 '{key}'\n" f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
        )

        actual_value = json_data[key]
        assert actual_value == expected_value, (
            f"键 '{key}' 的值不匹配:\n"
            f"期望: {expected_value} (类型: {type(expected_value).__name__})\n"
            f"实际: {actual_value} (类型: {type(actual_value).__name__})"
        )
        return self

    def assert_response_list_length(self, expected_length: int, key: str = None) -> "ResponseAssertionBuilder":
        """
        验证列表长度

        Args:
            expected_length: 期望的列表长度
            key: 可选，指定响应体中的某个键的值作为列表

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当列表长度不匹配时抛出
            TypeError: 当目标不是列表时抛出

        Example:
            builder.assert_response_list_length(10)
            builder.assert_response_list_length(5, key="data.items")
        """
        json_data = self._get_json_data()

        if key:
            keys = key.split(".")
            target = json_data
            for k in keys:
                if isinstance(target, dict) and k in target:
                    target = target[k]
                else:
                    raise AssertionError(
                        f"无法找到键路径 '{key}'\n" f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
                    )
        else:
            target = json_data

        if not isinstance(target, list):
            raise TypeError(
                f"目标不是列表类型，实际类型: {type(target).__name__}\n"
                f"目标内容: {json.dumps(target, ensure_ascii=False, indent=2)}"
            )

        actual_length = len(target)
        assert actual_length == expected_length, (
            f"列表长度验证失败:\n" f"期望长度: {expected_length}\n" f"实际长度: {actual_length}"
        )
        return self

    def assert_response_not_empty(self) -> "ResponseAssertionBuilder":
        """
        验证响应非空

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应为空时抛出

        Example:
            builder.assert_response_not_empty()
        """
        json_data = self._get_json_data()

        if isinstance(json_data, (dict, list)):
            assert len(json_data) > 0, "响应为空"
        elif isinstance(json_data, str):
            assert len(json_data.strip()) > 0, "响应为空字符串"
        else:
            assert json_data is not None, "响应为None"

        return self

    def assert_response_key_not_empty(self, key: str) -> "ResponseAssertionBuilder":
        """
        验证响应中指定键的值非空

        Args:
            key: 键名

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当键不存在或值为空时抛出

        Example:
            builder.assert_response_key_not_empty("data")
        """
        json_data = self._get_json_data()

        assert key in json_data, (
            f"响应不包含键 '{key}'\n" f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
        )

        value = json_data[key]

        if isinstance(value, (dict, list, str)):
            assert len(value) > 0, f"键 '{key}' 的值为空"
        else:
            assert value is not None, f"键 '{key}' 的值为None"

        return self

    def assert_response_key_type(self, key: str, expected_type: type) -> "ResponseAssertionBuilder":
        """
        验证响应中指定键的值类型

        Args:
            key: 键名
            expected_type: 期望的类型

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当键不存在或类型不匹配时抛出

        Example:
            builder.assert_response_key_type("data", dict)
            builder.assert_response_key_type("count", int)
        """
        json_data = self._get_json_data()

        assert key in json_data, (
            f"响应不包含键 '{key}'\n" f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
        )

        actual_value = json_data[key]
        actual_type = type(actual_value)

        assert actual_type == expected_type, (
            f"键 '{key}' 的类型不匹配:\n"
            f"期望类型: {expected_type.__name__}\n"
            f"实际类型: {actual_type.__name__}\n"
            f"实际值: {actual_value}"
        )
        return self

    def assert_response_contains_key_in_list(self, key: str, list_key: str = None) -> "ResponseAssertionBuilder":
        """
        验证响应中的列表包含指定键

        Args:
            key: 期望存在的键名
            list_key: 列表键名，如果为None则直接验证响应体

        Returns:
            ResponseAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当列表中的元素不包含指定键时抛出

        Example:
            builder.assert_response_contains_key_in_list("id", list_key="data.items")
        """
        json_data = self._get_json_data()

        if list_key:
            keys = list_key.split(".")
            target = json_data
            for k in keys:
                if isinstance(target, dict) and k in target:
                    target = target[k]
                else:
                    raise AssertionError(
                        f"无法找到键路径 '{list_key}'\n"
                        f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
                    )
        else:
            target = json_data

        if not isinstance(target, list):
            raise TypeError(f"目标不是列表类型，实际类型: {type(target).__name__}")

        for index, item in enumerate(target):
            if isinstance(item, dict):
                assert key in item, (
                    f"列表中第 {index} 个元素不包含键 '{key}'\n"
                    f"元素内容: {json.dumps(item, ensure_ascii=False, indent=2)}"
                )

        return self


def assert_response_body(response: requests.Response, expected_body: Union[Dict, List]) -> None:
    """
    验证完整响应体

    Args:
        response: requests.Response对象
        expected_body: 期望的响应体

    Raises:
        AssertionError: 当响应体不匹配时抛出
    """
    ResponseAssertionBuilder(response).assert_response_body(expected_body)


def assert_response_contains(response: requests.Response, expected_data: Dict) -> None:
    """
    验证响应包含特定数据

    Args:
        response: requests.Response对象
        expected_data: 期望包含的数据

    Raises:
        AssertionError: 当响应不包含指定数据时抛出
    """
    ResponseAssertionBuilder(response).assert_response_contains(expected_data)


def assert_response_key_exists(response: requests.Response, key: str) -> None:
    """
    验证响应包含特定键

    Args:
        response: requests.Response对象
        key: 期望存在的键名

    Raises:
        AssertionError: 当响应不包含指定键时抛出
    """
    ResponseAssertionBuilder(response).assert_response_key_exists(key)


def assert_response_key_value(response: requests.Response, key: str, expected_value: Any) -> None:
    """
    验证响应键值

    Args:
        response: requests.Response对象
        key: 键名
        expected_value: 期望的值

    Raises:
        AssertionError: 当键不存在或值不匹配时抛出
    """
    ResponseAssertionBuilder(response).assert_response_key_value(key, expected_value)


def assert_response_list_length(response: requests.Response, expected_length: int, key: str = None) -> None:
    """
    验证列表长度

    Args:
        response: requests.Response对象
        expected_length: 期望的列表长度
        key: 可选，指定响应体中的某个键的值作为列表

    Raises:
        AssertionError: 当列表长度不匹配时抛出
    """
    ResponseAssertionBuilder(response).assert_response_list_length(expected_length, key)


def assert_response_not_empty(response: requests.Response) -> None:
    """
    验证响应非空

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当响应为空时抛出
    """
    ResponseAssertionBuilder(response).assert_response_not_empty()


__all__ = [
    "ResponseAssertionBuilder",
    "assert_response_body",
    "assert_response_contains",
    "assert_response_key_exists",
    "assert_response_key_value",
    "assert_response_list_length",
    "assert_response_not_empty",
]

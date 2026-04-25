"""
JSON Schema断言工具

提供JSON Schema验证相关的断言功能，支持独立函数调用和链式调用。

使用示例：
    # 独立函数调用
    from tests.assertions.schema_assertions import assert_json_schema

    schema = {
        "type": "object",
        "properties": {
            "id": {"type": "integer"},
            "name": {"type": "string"}
        },
        "required": ["id", "name"]
    }
    assert_json_schema(response, schema)

    # 链式调用
    from tests.assertions.schema_assertions import SchemaAssertionBuilder

    builder = SchemaAssertionBuilder(response)
    builder.assert_json_schema(schema).assert_required_fields(["id", "name"])
"""

import json
from typing import Any, Dict, List, Type

import requests

try:
    from jsonschema import ValidationError, validate

    JSONSCHEMA_AVAILABLE = True
except ImportError:
    JSONSCHEMA_AVAILABLE = False
    ValidationError = Exception


class SchemaAssertionBuilder:
    """
    JSON Schema断言构建器

    提供链式调用接口验证JSON Schema

    Attributes:
        response: requests.Response对象
        _json_data: 缓存的JSON数据
    """

    def __init__(self, response: requests.Response):
        """
        初始化Schema断言构建器

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

    def assert_json_schema(self, schema: Dict) -> "SchemaAssertionBuilder":
        """
        验证JSON Schema

        使用JSON Schema规范验证响应体

        Args:
            schema: JSON Schema定义

        Returns:
            SchemaAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当JSON Schema验证失败时抛出
            ImportError: 当jsonschema库未安装时抛出

        Example:
            schema = {
                "type": "object",
                "properties": {
                    "id": {"type": "integer"},
                    "name": {"type": "string"}
                },
                "required": ["id", "name"]
            }
            builder.assert_json_schema(schema)
        """
        if not JSONSCHEMA_AVAILABLE:
            raise ImportError("jsonschema库未安装，请运行: pip install jsonschema")

        json_data = self._get_json_data()

        try:
            validate(instance=json_data, schema=schema)
        except ValidationError as e:
            raise AssertionError(
                f"JSON Schema验证失败:\n"
                f"错误路径: {' -> '.join(str(p) for p in e.path)}\n"
                f"错误信息: {e.message}\n"
                f"验证器: {e.validator}\n"
                f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)[:1000]}"
            )

        return self

    def assert_response_format(self, expected_format: Dict) -> "SchemaAssertionBuilder":
        """
        验证响应格式

        简化的格式验证，不需要完整的JSON Schema

        Args:
            expected_format: 期望的格式定义
                {
                    "field_name": "type",  # 类型: "string", "integer", "number", "boolean", "array", "object"
                    "field_name": {"type": "object", "properties": {...}},  # 嵌套对象
                    "field_name": {"type": "array", "items": "type"}  # 数组
                }

        Returns:
            SchemaAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当格式验证失败时抛出

        Example:
            format_def = {
                "id": "integer",
                "name": "string",
                "price": "number",
                "tags": {"type": "array", "items": "string"}
            }
            builder.assert_response_format(format_def)
        """
        json_data = self._get_json_data()
        self._validate_format(json_data, expected_format, "")
        return self

    def _validate_format(self, data: Any, format_def: Dict, path: str) -> None:
        """
        递归验证格式

        Args:
            data: 要验证的数据
            format_def: 格式定义
            path: 当前路径（用于错误信息）

        Raises:
            AssertionError: 当格式验证失败时抛出
        """
        if not isinstance(data, dict):
            raise AssertionError(f"路径 '{path}': 期望对象类型，实际类型: {type(data).__name__}")

        for field_name, field_type in format_def.items():
            current_path = f"{path}.{field_name}" if path else field_name

            if field_name not in data:
                raise AssertionError(f"路径 '{current_path}': 字段不存在")

            value = data[field_name]

            if isinstance(field_type, str):
                self._validate_type(value, field_type, current_path)
            elif isinstance(field_type, dict):
                if field_type.get("type") == "array":
                    if not isinstance(value, list):
                        raise AssertionError(f"路径 '{current_path}': 期望数组类型，实际类型: {type(value).__name__}")
                    items_type = field_type.get("items")
                    if items_type:
                        for index, item in enumerate(value):
                            item_path = f"{current_path}[{index}]"
                            if isinstance(items_type, str):
                                self._validate_type(item, items_type, item_path)
                            elif isinstance(items_type, dict):
                                self._validate_format(item, items_type, item_path)
                elif field_type.get("type") == "object":
                    properties = field_type.get("properties", {})
                    self._validate_format(value, properties, current_path)

    def _validate_type(self, value: Any, expected_type: str, path: str) -> None:
        """
        验证值的类型

        Args:
            value: 要验证的值
            expected_type: 期望的类型字符串
            path: 当前路径

        Raises:
            AssertionError: 当类型不匹配时抛出
        """
        type_mapping = {
            "string": str,
            "integer": int,
            "number": (int, float),
            "boolean": bool,
            "array": list,
            "object": dict,
            "null": type(None),
        }

        if expected_type not in type_mapping:
            raise AssertionError(f"路径 '{path}': 未知的类型 '{expected_type}'")

        expected_python_type = type_mapping[expected_type]

        if expected_type == "number":
            if not isinstance(value, (int, float)):
                raise AssertionError(f"路径 '{path}': 期望数字类型，实际类型: {type(value).__name__}")
        elif expected_type == "integer":
            if not isinstance(value, int) or isinstance(value, bool):
                raise AssertionError(f"路径 '{path}': 期望整数类型，实际类型: {type(value).__name__}")
        else:
            if not isinstance(value, expected_python_type):
                raise AssertionError(f"路径 '{path}': 期望 {expected_type} 类型，实际类型: {type(value).__name__}")

    def assert_field_type(
        self, field_name: str, expected_type: Type, nested_path: str = None
    ) -> "SchemaAssertionBuilder":
        """
        验证字段类型

        Args:
            field_name: 字段名
            expected_type: 期望的Python类型
            nested_path: 嵌套路径，如 "data.items"

        Returns:
            SchemaAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当字段不存在或类型不匹配时抛出

        Example:
            builder.assert_field_type("id", int)
            builder.assert_field_type("name", str)
            builder.assert_field_type("price", float, nested_path="data")
        """
        json_data = self._get_json_data()

        if nested_path:
            keys = nested_path.split(".")
            target = json_data
            for key in keys:
                if isinstance(target, dict) and key in target:
                    target = target[key]
                else:
                    raise AssertionError(
                        f"无法找到嵌套路径 '{nested_path}'\n"
                        f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
                    )
        else:
            target = json_data

        if not isinstance(target, dict):
            raise AssertionError(f"目标不是对象类型，无法验证字段 '{field_name}'")

        if field_name not in target:
            raise AssertionError(f"字段 '{field_name}' 不存在\n" f"可用的字段: {list(target.keys())}")

        actual_value = target[field_name]
        actual_type = type(actual_value)

        if not isinstance(actual_value, expected_type):
            raise AssertionError(
                f"字段 '{field_name}' 的类型不匹配:\n"
                f"期望类型: {expected_type.__name__}\n"
                f"实际类型: {actual_type.__name__}\n"
                f"实际值: {actual_value}"
            )

        return self

    def assert_required_fields(self, required_fields: List[str], nested_path: str = None) -> "SchemaAssertionBuilder":
        """
        验证必需字段存在

        Args:
            required_fields: 必需字段列表
            nested_path: 嵌套路径，如 "data"

        Returns:
            SchemaAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当必需字段不存在时抛出

        Example:
            builder.assert_required_fields(["id", "name", "email"])
            builder.assert_required_fields(["id", "name"], nested_path="data")
        """
        json_data = self._get_json_data()

        if nested_path:
            keys = nested_path.split(".")
            target = json_data
            for key in keys:
                if isinstance(target, dict) and key in target:
                    target = target[key]
                else:
                    raise AssertionError(
                        f"无法找到嵌套路径 '{nested_path}'\n"
                        f"响应内容: {json.dumps(json_data, ensure_ascii=False, indent=2)}"
                    )
        else:
            target = json_data

        if not isinstance(target, dict):
            raise AssertionError("目标不是对象类型，无法验证必需字段")

        missing_fields = []
        for field in required_fields:
            if field not in target:
                missing_fields.append(field)

        if missing_fields:
            raise AssertionError(f"缺少必需字段: {missing_fields}\n" f"可用的字段: {list(target.keys())}")

        return self

    def assert_field_pattern(self, field_name: str, pattern: str, nested_path: str = None) -> "SchemaAssertionBuilder":
        """
        验证字段值匹配正则表达式

        Args:
            field_name: 字段名
            pattern: 正则表达式模式
            nested_path: 嵌套路径

        Returns:
            SchemaAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当字段不存在或值不匹配模式时抛出

        Example:
            builder.assert_field_pattern("email", r'^[\w\.-]+@[\w\.-]+\.\w+$')
        """
        import re

        json_data = self._get_json_data()

        if nested_path:
            keys = nested_path.split(".")
            target = json_data
            for key in keys:
                if isinstance(target, dict) and key in target:
                    target = target[key]
                else:
                    raise AssertionError(f"无法找到嵌套路径 '{nested_path}'")
        else:
            target = json_data

        if not isinstance(target, dict):
            raise AssertionError("目标不是对象类型")

        if field_name not in target:
            raise AssertionError(f"字段 '{field_name}' 不存在")

        value = target[field_name]

        if not isinstance(value, str):
            raise AssertionError(f"字段 '{field_name}' 不是字符串类型，无法验证正则表达式")

        if not re.match(pattern, value):
            raise AssertionError(f"字段 '{field_name}' 的值不匹配模式:\n" f"模式: {pattern}\n" f"实际值: {value}")

        return self

    def assert_field_range(
        self, field_name: str, min_value: Any = None, max_value: Any = None, nested_path: str = None
    ) -> "SchemaAssertionBuilder":
        """
        验证字段值在指定范围内

        Args:
            field_name: 字段名
            min_value: 最小值
            max_value: 最大值
            nested_path: 嵌套路径

        Returns:
            SchemaAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当字段不存在或值不在范围内时抛出

        Example:
            builder.assert_field_range("age", min_value=0, max_value=120)
            builder.assert_field_range("price", min_value=0)
        """
        json_data = self._get_json_data()

        if nested_path:
            keys = nested_path.split(".")
            target = json_data
            for key in keys:
                if isinstance(target, dict) and key in target:
                    target = target[key]
                else:
                    raise AssertionError(f"无法找到嵌套路径 '{nested_path}'")
        else:
            target = json_data

        if not isinstance(target, dict):
            raise AssertionError("目标不是对象类型")

        if field_name not in target:
            raise AssertionError(f"字段 '{field_name}' 不存在")

        value = target[field_name]

        if not isinstance(value, (int, float)):
            raise AssertionError(f"字段 '{field_name}' 不是数字类型，无法验证范围")

        if min_value is not None and value < min_value:
            raise AssertionError(f"字段 '{field_name}' 的值小于最小值:\n" f"最小值: {min_value}\n" f"实际值: {value}")

        if max_value is not None and value > max_value:
            raise AssertionError(f"字段 '{field_name}' 的值大于最大值:\n" f"最大值: {max_value}\n" f"实际值: {value}")

        return self


def assert_json_schema(response: requests.Response, schema: Dict) -> None:
    """
    验证JSON Schema

    Args:
        response: requests.Response对象
        schema: JSON Schema定义

    Raises:
        AssertionError: 当JSON Schema验证失败时抛出
    """
    SchemaAssertionBuilder(response).assert_json_schema(schema)


def assert_response_format(response: requests.Response, expected_format: Dict) -> None:
    """
    验证响应格式

    Args:
        response: requests.Response对象
        expected_format: 期望的格式定义

    Raises:
        AssertionError: 当格式验证失败时抛出
    """
    SchemaAssertionBuilder(response).assert_response_format(expected_format)


def assert_field_type(
    response: requests.Response, field_name: str, expected_type: Type, nested_path: str = None
) -> None:
    """
    验证字段类型

    Args:
        response: requests.Response对象
        field_name: 字段名
        expected_type: 期望的Python类型
        nested_path: 嵌套路径

    Raises:
        AssertionError: 当字段不存在或类型不匹配时抛出
    """
    SchemaAssertionBuilder(response).assert_field_type(field_name, expected_type, nested_path)


def assert_required_fields(response: requests.Response, required_fields: List[str], nested_path: str = None) -> None:
    """
    验证必需字段存在

    Args:
        response: requests.Response对象
        required_fields: 必需字段列表
        nested_path: 嵌套路径

    Raises:
        AssertionError: 当必需字段不存在时抛出
    """
    SchemaAssertionBuilder(response).assert_required_fields(required_fields, nested_path)


__all__ = [
    "SchemaAssertionBuilder",
    "assert_json_schema",
    "assert_response_format",
    "assert_field_type",
    "assert_required_fields",
]

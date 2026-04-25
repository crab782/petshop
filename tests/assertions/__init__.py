"""
断言工具模块

该模块提供了丰富的断言工具，用于API测试中的各种验证场景。
支持链式调用，提供清晰的错误信息。

模块组成：
- status_code_assertions: HTTP状态码断言
- response_assertions: 响应体断言
- header_assertions: 响应头断言
- schema_assertions: JSON Schema断言
- business_assertions: 业务逻辑断言

使用示例：
    from tests.assertions import AssertionBuilder

    # 链式调用
    AssertionBuilder(response) \\
        .assert_status_code(200) \\
        .assert_json_key_exists("data") \\
        .assert_response_time(2.0)

    # 或者使用独立函数
    from tests.assertions import assert_status_code, assert_json_key_exists
    assert_status_code(response, 200)
    assert_json_key_exists(response, "data")
"""

from tests.assertions.business_assertions import (
    BusinessAssertionBuilder,
    assert_business_rule,
    assert_data_consistency,
    assert_pagination,
    assert_status_transition,
)
from tests.assertions.header_assertions import (
    HeaderAssertionBuilder,
    assert_authorization_header,
    assert_content_type,
    assert_header_exists,
    assert_header_value,
)
from tests.assertions.response_assertions import (
    ResponseAssertionBuilder,
    assert_response_body,
    assert_response_contains,
    assert_response_key_exists,
    assert_response_key_value,
    assert_response_list_length,
    assert_response_not_empty,
)
from tests.assertions.schema_assertions import (
    SchemaAssertionBuilder,
    assert_field_type,
    assert_json_schema,
    assert_required_fields,
    assert_response_format,
)
from tests.assertions.status_code_assertions import (
    StatusCodeAssertionBuilder,
    assert_client_error,
    assert_created,
    assert_no_content,
    assert_server_error,
    assert_status_code,
    assert_success,
)


class AssertionBuilder:
    """
    统一的断言构建器，整合所有断言功能

    提供链式调用接口，可以在一个链中调用不同类型的断言

    使用示例：
        builder = AssertionBuilder(response)
        builder.assert_status_code(200) \\
               .assert_json_key_exists("data") \\
               .assert_response_time(2.0)
    """

    def __init__(self, response):
        """
        初始化断言构建器

        Args:
            response: requests.Response对象
        """
        self.response = response
        self.status_builder = StatusCodeAssertionBuilder(response)
        self.response_builder = ResponseAssertionBuilder(response)
        self.header_builder = HeaderAssertionBuilder(response)
        self.schema_builder = SchemaAssertionBuilder(response)

    def assert_status_code(self, expected_code: int) -> "AssertionBuilder":
        """
        验证状态码

        Args:
            expected_code: 期望的状态码

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.status_builder.assert_status_code(expected_code)
        return self

    def assert_success(self) -> "AssertionBuilder":
        """
        验证成功状态码（200, 201）

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.status_builder.assert_success()
        return self

    def assert_client_error(self) -> "AssertionBuilder":
        """
        验证客户端错误（400, 401, 403, 404）

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.status_builder.assert_client_error()
        return self

    def assert_server_error(self) -> "AssertionBuilder":
        """
        验证服务器错误（500, 502, 503）

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.status_builder.assert_server_error()
        return self

    def assert_created(self) -> "AssertionBuilder":
        """
        验证资源创建成功（201）

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.status_builder.assert_created()
        return self

    def assert_no_content(self) -> "AssertionBuilder":
        """
        验证无内容返回（204）

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.status_builder.assert_no_content()
        return self

    def assert_response_body(self, expected_body: dict) -> "AssertionBuilder":
        """
        验证完整响应体

        Args:
            expected_body: 期望的响应体

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.response_builder.assert_response_body(expected_body)
        return self

    def assert_response_contains(self, expected_data: dict) -> "AssertionBuilder":
        """
        验证响应包含特定数据

        Args:
            expected_data: 期望包含的数据

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.response_builder.assert_response_contains(expected_data)
        return self

    def assert_response_key_exists(self, key: str) -> "AssertionBuilder":
        """
        验证响应包含特定键

        Args:
            key: 期望存在的键

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.response_builder.assert_response_key_exists(key)
        return self

    def assert_response_key_value(self, key: str, expected_value) -> "AssertionBuilder":
        """
        验证响应键值

        Args:
            key: 键名
            expected_value: 期望的值

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.response_builder.assert_response_key_value(key, expected_value)
        return self

    def assert_response_list_length(self, expected_length: int) -> "AssertionBuilder":
        """
        验证列表长度

        Args:
            expected_length: 期望的列表长度

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.response_builder.assert_response_list_length(expected_length)
        return self

    def assert_response_not_empty(self) -> "AssertionBuilder":
        """
        验证响应非空

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.response_builder.assert_response_not_empty()
        return self

    def assert_content_type(self, expected_type: str) -> "AssertionBuilder":
        """
        验证Content-Type

        Args:
            expected_type: 期望的Content-Type

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.header_builder.assert_content_type(expected_type)
        return self

    def assert_header_exists(self, header_name: str) -> "AssertionBuilder":
        """
        验证响应头存在

        Args:
            header_name: 响应头名称

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.header_builder.assert_header_exists(header_name)
        return self

    def assert_header_value(self, header_name: str, expected_value: str) -> "AssertionBuilder":
        """
        验证响应头值

        Args:
            header_name: 响应头名称
            expected_value: 期望的值

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.header_builder.assert_header_value(header_name, expected_value)
        return self

    def assert_authorization_header(self) -> "AssertionBuilder":
        """
        验证Authorization头

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.header_builder.assert_authorization_header()
        return self

    def assert_json_schema(self, schema: dict) -> "AssertionBuilder":
        """
        验证JSON Schema

        Args:
            schema: JSON Schema定义

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.schema_builder.assert_json_schema(schema)
        return self

    def assert_response_format(self, expected_format: dict) -> "AssertionBuilder":
        """
        验证响应格式

        Args:
            expected_format: 期望的格式定义

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.schema_builder.assert_response_format(expected_format)
        return self

    def assert_field_type(self, field_name: str, expected_type: type) -> "AssertionBuilder":
        """
        验证字段类型

        Args:
            field_name: 字段名
            expected_type: 期望的类型

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.schema_builder.assert_field_type(field_name, expected_type)
        return self

    def assert_required_fields(self, required_fields: list) -> "AssertionBuilder":
        """
        验证必需字段存在

        Args:
            required_fields: 必需字段列表

        Returns:
            AssertionBuilder: 返回self以支持链式调用
        """
        self.schema_builder.assert_required_fields(required_fields)
        return self


__all__ = [
    "AssertionBuilder",
    "assert_status_code",
    "assert_success",
    "assert_client_error",
    "assert_server_error",
    "assert_created",
    "assert_no_content",
    "StatusCodeAssertionBuilder",
    "assert_response_body",
    "assert_response_contains",
    "assert_response_key_exists",
    "assert_response_key_value",
    "assert_response_list_length",
    "assert_response_not_empty",
    "ResponseAssertionBuilder",
    "assert_content_type",
    "assert_header_exists",
    "assert_header_value",
    "assert_authorization_header",
    "HeaderAssertionBuilder",
    "assert_json_schema",
    "assert_response_format",
    "assert_field_type",
    "assert_required_fields",
    "SchemaAssertionBuilder",
    "assert_data_consistency",
    "assert_status_transition",
    "assert_business_rule",
    "assert_pagination",
    "BusinessAssertionBuilder",
]

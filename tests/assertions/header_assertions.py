"""
响应头断言工具

提供HTTP响应头相关的断言功能，支持独立函数调用和链式调用。

使用示例：
    # 独立函数调用
    from tests.assertions.header_assertions import assert_content_type

    assert_content_type(response, "application/json")
    assert_header_exists(response, "X-Custom-Header")

    # 链式调用
    from tests.assertions.header_assertions import HeaderAssertionBuilder

    builder = HeaderAssertionBuilder(response)
    builder.assert_content_type("application/json").assert_header_exists("Authorization")
"""

from typing import Optional

import requests


class HeaderAssertionBuilder:
    """
    HTTP响应头断言构建器

    提供链式调用接口验证HTTP响应头

    Attributes:
        response: requests.Response对象
    """

    def __init__(self, response: requests.Response):
        """
        初始化响应头断言构建器

        Args:
            response: requests.Response对象
        """
        self.response = response

    def assert_content_type(self, expected_type: str, exact_match: bool = False) -> "HeaderAssertionBuilder":
        """
        验证Content-Type

        Args:
            expected_type: 期望的Content-Type（如 "application/json"）
            exact_match: 是否精确匹配，False时支持部分匹配（如 "application/json" 匹配 "application/json; charset=utf-8"）

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当Content-Type不匹配时抛出

        Example:
            builder.assert_content_type("application/json")
            builder.assert_content_type("application/json; charset=utf-8", exact_match=True)
        """
        content_type = self.response.headers.get("Content-Type")

        assert content_type is not None, "响应头中不存在 'Content-Type'"

        if exact_match:
            assert content_type == expected_type, (
                f"Content-Type验证失败:\n" f"期望: {expected_type}\n" f"实际: {content_type}"
            )
        else:
            assert expected_type in content_type, (
                f"Content-Type验证失败:\n" f"期望包含: {expected_type}\n" f"实际: {content_type}"
            )

        return self

    def assert_header_exists(self, header_name: str) -> "HeaderAssertionBuilder":
        """
        验证响应头存在

        Args:
            header_name: 响应头名称（不区分大小写）

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应头不存在时抛出

        Example:
            builder.assert_header_exists("Authorization")
        """
        header_value = self.response.headers.get(header_name)

        assert header_value is not None, (
            f"响应头中不存在 '{header_name}'\n" f"可用的响应头: {list(self.response.headers.keys())}"
        )

        return self

    def assert_header_value(
        self, header_name: str, expected_value: str, case_sensitive: bool = True
    ) -> "HeaderAssertionBuilder":
        """
        验证响应头值

        Args:
            header_name: 响应头名称（不区分大小写）
            expected_value: 期望的值
            case_sensitive: 是否区分大小写

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应头不存在或值不匹配时抛出

        Example:
            builder.assert_header_value("Server", "nginx")
        """
        header_value = self.response.headers.get(header_name)

        assert header_value is not None, (
            f"响应头中不存在 '{header_name}'\n" f"可用的响应头: {list(self.response.headers.keys())}"
        )

        if case_sensitive:
            assert header_value == expected_value, (
                f"响应头 '{header_name}' 的值不匹配:\n" f"期望: {expected_value}\n" f"实际: {header_value}"
            )
        else:
            assert header_value.lower() == expected_value.lower(), (
                f"响应头 '{header_name}' 的值不匹配（不区分大小写）:\n"
                f"期望: {expected_value}\n"
                f"实际: {header_value}"
            )

        return self

    def assert_authorization_header(self) -> "HeaderAssertionBuilder":
        """
        验证Authorization头

        验证响应头中包含Authorization头，且格式为 "Bearer <token>"

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当Authorization头不存在或格式不正确时抛出

        Example:
            builder.assert_authorization_header()
        """
        auth_header = self.response.headers.get("Authorization")

        assert auth_header is not None, "响应头中不存在 'Authorization'"

        assert auth_header.startswith("Bearer "), (
            f"Authorization头格式不正确，期望以 'Bearer ' 开头\n" f"实际值: {auth_header}"
        )

        token = auth_header[7:]
        assert len(token) > 0, "Authorization头中的token为空"

        return self

    def assert_header_contains(self, header_name: str, expected_substring: str) -> "HeaderAssertionBuilder":
        """
        验证响应头包含特定子字符串

        Args:
            header_name: 响应头名称
            expected_substring: 期望包含的子字符串

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当响应头不存在或不包含指定子字符串时抛出

        Example:
            builder.assert_header_contains("Content-Type", "json")
        """
        header_value = self.response.headers.get(header_name)

        assert header_value is not None, (
            f"响应头中不存在 '{header_name}'\n" f"可用的响应头: {list(self.response.headers.keys())}"
        )

        assert expected_substring in header_value, (
            f"响应头 '{header_name}' 不包含子字符串 '{expected_substring}'\n" f"实际值: {header_value}"
        )

        return self

    def assert_cache_control(self, expected_value: str = None) -> "HeaderAssertionBuilder":
        """
        验证Cache-Control响应头

        Args:
            expected_value: 期望的值，如果为None则只验证存在性

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当Cache-Control不存在或值不匹配时抛出

        Example:
            builder.assert_cache_control()
            builder.assert_cache_control("no-cache")
        """
        cache_control = self.response.headers.get("Cache-Control")

        assert cache_control is not None, "响应头中不存在 'Cache-Control'"

        if expected_value:
            assert cache_control == expected_value, (
                f"Cache-Control验证失败:\n" f"期望: {expected_value}\n" f"实际: {cache_control}"
            )

        return self

    def assert_cors_headers(self, origin: str = "*") -> "HeaderAssertionBuilder":
        """
        验证CORS相关响应头

        Args:
            origin: 期望的允许来源，默认为 '*'

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当CORS响应头不存在或不匹配时抛出

        Example:
            builder.assert_cors_headers("http://localhost:5173")
        """
        access_control_origin = self.response.headers.get("Access-Control-Allow-Origin")

        assert access_control_origin is not None, "响应头中不存在 'Access-Control-Allow-Origin'"

        assert access_control_origin == origin or access_control_origin == "*", (
            f"Access-Control-Allow-Origin验证失败:\n" f"期望: {origin}\n" f"实际: {access_control_origin}"
        )

        return self

    def assert_content_length(
        self, expected_length: Optional[int] = None, min_length: Optional[int] = None, max_length: Optional[int] = None
    ) -> "HeaderAssertionBuilder":
        """
        验证Content-Length响应头

        Args:
            expected_length: 期望的精确长度
            min_length: 最小长度
            max_length: 最大长度

        Returns:
            HeaderAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当Content-Length不满足条件时抛出

        Example:
            builder.assert_content_length(1024)
            builder.assert_content_length(min_length=100, max_length=5000)
        """
        content_length_str = self.response.headers.get("Content-Length")

        assert content_length_str is not None, "响应头中不存在 'Content-Length'"

        try:
            content_length = int(content_length_str)
        except ValueError:
            raise AssertionError(f"Content-Length不是有效的整数: {content_length_str}")

        if expected_length is not None:
            assert content_length == expected_length, (
                f"Content-Length验证失败:\n" f"期望: {expected_length}\n" f"实际: {content_length}"
            )

        if min_length is not None:
            assert content_length >= min_length, (
                f"Content-Length小于最小值:\n" f"最小值: {min_length}\n" f"实际: {content_length}"
            )

        if max_length is not None:
            assert content_length <= max_length, (
                f"Content-Length大于最大值:\n" f"最大值: {max_length}\n" f"实际: {content_length}"
            )

        return self


def assert_content_type(response: requests.Response, expected_type: str, exact_match: bool = False) -> None:
    """
    验证Content-Type

    Args:
        response: requests.Response对象
        expected_type: 期望的Content-Type
        exact_match: 是否精确匹配

    Raises:
        AssertionError: 当Content-Type不匹配时抛出
    """
    HeaderAssertionBuilder(response).assert_content_type(expected_type, exact_match)


def assert_header_exists(response: requests.Response, header_name: str) -> None:
    """
    验证响应头存在

    Args:
        response: requests.Response对象
        header_name: 响应头名称

    Raises:
        AssertionError: 当响应头不存在时抛出
    """
    HeaderAssertionBuilder(response).assert_header_exists(header_name)


def assert_header_value(
    response: requests.Response, header_name: str, expected_value: str, case_sensitive: bool = True
) -> None:
    """
    验证响应头值

    Args:
        response: requests.Response对象
        header_name: 响应头名称
        expected_value: 期望的值
        case_sensitive: 是否区分大小写

    Raises:
        AssertionError: 当响应头不存在或值不匹配时抛出
    """
    HeaderAssertionBuilder(response).assert_header_value(header_name, expected_value, case_sensitive)


def assert_authorization_header(response: requests.Response) -> None:
    """
    验证Authorization头

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当Authorization头不存在或格式不正确时抛出
    """
    HeaderAssertionBuilder(response).assert_authorization_header()


__all__ = [
    "HeaderAssertionBuilder",
    "assert_content_type",
    "assert_header_exists",
    "assert_header_value",
    "assert_authorization_header",
]

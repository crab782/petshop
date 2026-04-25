"""
HTTP状态码断言工具

提供HTTP状态码相关的断言功能，支持独立函数调用和链式调用。

使用示例：
    # 独立函数调用
    from tests.assertions.status_code_assertions import assert_status_code

    assert_status_code(response, 200)
    assert_success(response)

    # 链式调用
    from tests.assertions.status_code_assertions import StatusCodeAssertionBuilder

    builder = StatusCodeAssertionBuilder(response)
    builder.assert_status_code(200).assert_success()
"""

import requests


class StatusCodeAssertionBuilder:
    """
    HTTP状态码断言构建器

    提供链式调用接口验证HTTP状态码

    Attributes:
        response: requests.Response对象
    """

    def __init__(self, response: requests.Response):
        """
        初始化状态码断言构建器

        Args:
            response: requests.Response对象
        """
        self.response = response

    def assert_status_code(self, expected_code: int) -> "StatusCodeAssertionBuilder":
        """
        验证状态码

        Args:
            expected_code: 期望的HTTP状态码

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不匹配时抛出

        Example:
            builder.assert_status_code(200)
        """
        actual_code = self.response.status_code
        assert actual_code == expected_code, (
            f"状态码验证失败: 期望 {expected_code}, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_success(self) -> "StatusCodeAssertionBuilder":
        """
        验证成功状态码（200, 201）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是成功状态码时抛出

        Example:
            builder.assert_success()
        """
        actual_code = self.response.status_code
        assert actual_code in [200, 201], (
            f"成功状态码验证失败: 期望 200 或 201, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_client_error(self) -> "StatusCodeAssertionBuilder":
        """
        验证客户端错误（400, 401, 403, 404）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是客户端错误时抛出

        Example:
            builder.assert_client_error()
        """
        actual_code = self.response.status_code
        assert actual_code in [400, 401, 403, 404], (
            f"客户端错误验证失败: 期望 400/401/403/404, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_server_error(self) -> "StatusCodeAssertionBuilder":
        """
        验证服务器错误（500, 502, 503）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是服务器错误时抛出

        Example:
            builder.assert_server_error()
        """
        actual_code = self.response.status_code
        assert actual_code in [500, 502, 503], (
            f"服务器错误验证失败: 期望 500/502/503, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_created(self) -> "StatusCodeAssertionBuilder":
        """
        验证资源创建成功（201）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是201时抛出

        Example:
            builder.assert_created()
        """
        actual_code = self.response.status_code
        assert actual_code == 201, (
            f"资源创建验证失败: 期望 201, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_no_content(self) -> "StatusCodeAssertionBuilder":
        """
        验证无内容返回（204）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是204时抛出

        Example:
            builder.assert_no_content()
        """
        actual_code = self.response.status_code
        assert actual_code == 204, (
            f"无内容验证失败: 期望 204, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_bad_request(self) -> "StatusCodeAssertionBuilder":
        """
        验证错误请求（400）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是400时抛出
        """
        actual_code = self.response.status_code
        assert actual_code == 400, (
            f"错误请求验证失败: 期望 400, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_unauthorized(self) -> "StatusCodeAssertionBuilder":
        """
        验证未授权（401）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是401时抛出
        """
        actual_code = self.response.status_code
        assert actual_code == 401, (
            f"未授权验证失败: 期望 401, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_forbidden(self) -> "StatusCodeAssertionBuilder":
        """
        验证禁止访问（403）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是403时抛出
        """
        actual_code = self.response.status_code
        assert actual_code == 403, (
            f"禁止访问验证失败: 期望 403, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self

    def assert_not_found(self) -> "StatusCodeAssertionBuilder":
        """
        验证资源不存在（404）

        Returns:
            StatusCodeAssertionBuilder: 返回self以支持链式调用

        Raises:
            AssertionError: 当状态码不是404时抛出
        """
        actual_code = self.response.status_code
        assert actual_code == 404, (
            f"资源不存在验证失败: 期望 404, 实际 {actual_code}\n" f"响应内容: {self.response.text[:500]}"
        )
        return self


def assert_status_code(response: requests.Response, expected_code: int) -> None:
    """
    验证状态码

    Args:
        response: requests.Response对象
        expected_code: 期望的HTTP状态码

    Raises:
        AssertionError: 当状态码不匹配时抛出

    Example:
        assert_status_code(response, 200)
    """
    StatusCodeAssertionBuilder(response).assert_status_code(expected_code)


def assert_success(response: requests.Response) -> None:
    """
    验证成功状态码（200, 201）

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当状态码不是成功状态码时抛出

    Example:
        assert_success(response)
    """
    StatusCodeAssertionBuilder(response).assert_success()


def assert_client_error(response: requests.Response) -> None:
    """
    验证客户端错误（400, 401, 403, 404）

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当状态码不是客户端错误时抛出

    Example:
        assert_client_error(response)
    """
    StatusCodeAssertionBuilder(response).assert_client_error()


def assert_server_error(response: requests.Response) -> None:
    """
    验证服务器错误（500, 502, 503）

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当状态码不是服务器错误时抛出

    Example:
        assert_server_error(response)
    """
    StatusCodeAssertionBuilder(response).assert_server_error()


def assert_created(response: requests.Response) -> None:
    """
    验证资源创建成功（201）

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当状态码不是201时抛出

    Example:
        assert_created(response)
    """
    StatusCodeAssertionBuilder(response).assert_created()


def assert_no_content(response: requests.Response) -> None:
    """
    验证无内容返回（204）

    Args:
        response: requests.Response对象

    Raises:
        AssertionError: 当状态码不是204时抛出

    Example:
        assert_no_content(response)
    """
    StatusCodeAssertionBuilder(response).assert_no_content()


__all__ = [
    "StatusCodeAssertionBuilder",
    "assert_status_code",
    "assert_success",
    "assert_client_error",
    "assert_server_error",
    "assert_created",
    "assert_no_content",
]

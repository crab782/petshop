"""
单元测试模块

该模块提供了单元测试的基础设施，包括：
- BaseAPITest: 所有API测试的基类
- 单元测试专用的fixtures
- 测试数据构建工具
- 断言工具

使用示例：
    from tests.unit import BaseAPITest

    class TestMyAPI(BaseAPITest):
        def test_example(self, unit_http_client):
            response = unit_http_client.get("/api/users")
            self.assert_response_success(response)
"""

from tests.unit.base_test import BaseAPITest

__all__ = [
    "BaseAPITest",
]

"""
测试数据 fixtures 模块

该目录用于存放测试数据 fixture 文件。
Fixture 文件可以包含预定义的测试数据，用于测试用例。

使用示例：
    import pytest
    from tests.testdata.fixtures import user_fixture

    @pytest.fixture
    def test_user():
        return user_fixture.default_user()
"""

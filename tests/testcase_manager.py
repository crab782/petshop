"""
测试用例管理器

提供测试用例的注册、查询、过滤、执行策略等功能。
支持多种测试用例分类、优先级和模块标记。
"""

import json
from dataclasses import asdict, dataclass, field
from datetime import datetime
from enum import Enum
from typing import Any, Callable, Dict, List, Optional, Set

import pytest


class TestTag(Enum):
    """测试用例分类标记"""

    SMOKE = "smoke"
    REGRESSION = "regression"
    API = "api"
    UNIT = "unit"
    INTEGRATION = "integration"
    PERFORMANCE = "performance"
    SECURITY = "security"


class TestPriority(Enum):
    """测试用例优先级标记"""

    P0 = "P0"
    P1 = "P1"
    P2 = "P2"
    P3 = "P3"


class TestModule(Enum):
    """测试用例模块标记"""

    USER = "user"
    MERCHANT = "merchant"
    ADMIN = "admin"
    PUBLIC = "public"
    AUTH = "auth"


class ExecutionStrategyType(Enum):
    """执行策略类型"""

    SEQUENTIAL = "sequential"
    PARALLEL = "parallel"
    PRIORITY_BASED = "priority_based"
    DEPENDENCY_BASED = "dependency_based"


@dataclass
class TestCase:
    """测试用例信息结构"""

    test_id: str
    test_name: str
    test_module: str
    test_tags: List[str]
    test_priority: str
    test_description: str
    test_steps: List[str]
    test_expected_result: str
    test_dependencies: List[str] = field(default_factory=list)
    test_timeout: int = 30
    test_function: Optional[Callable] = None
    test_status: str = "pending"
    test_created_at: str = field(default_factory=lambda: datetime.now().isoformat())
    test_updated_at: str = field(default_factory=lambda: datetime.now().isoformat())
    test_execution_time: Optional[float] = None
    test_error_message: Optional[str] = None

    def to_dict(self, include_function: bool = False) -> Dict[str, Any]:
        """转换为字典格式

        Args:
            include_function: 是否包含测试函数对象（默认不包含，因为函数对象无法JSON序列化）
        """
        result = asdict(self)
        if not include_function:
            result.pop("test_function", None)
        return result

    def update_status(self, status: str, execution_time: Optional[float] = None, error_message: Optional[str] = None):
        """更新测试状态"""
        self.test_status = status
        self.test_updated_at = datetime.now().isoformat()
        if execution_time is not None:
            self.test_execution_time = execution_time
        if error_message is not None:
            self.test_error_message = error_message


@dataclass
class ExecutionPlan:
    """执行计划"""

    plan_id: str
    strategy_type: ExecutionStrategyType
    test_cases: List[str]
    execution_order: List[str]
    created_at: str = field(default_factory=lambda: datetime.now().isoformat())

    def to_dict(self) -> Dict[str, Any]:
        """转换为字典格式"""
        return asdict(self)


class TestCaseManager:
    """测试用例管理器"""

    def __init__(self):
        self._test_cases: Dict[str, TestCase] = {}
        self._tags_index: Dict[str, Set[str]] = {}
        self._priority_index: Dict[str, Set[str]] = {}
        self._module_index: Dict[str, Set[str]] = {}
        self._execution_history: List[Dict[str, Any]] = []

    def register_test_case(self, test_id: str, test_info: Dict[str, Any]) -> None:
        """
        注册测试用例

        Args:
            test_id: 测试用例ID
            test_info: 测试用例信息字典

        Raises:
            ValueError: 如果测试用例ID已存在或信息不完整
        """
        if test_id in self._test_cases:
            raise ValueError(f"测试用例ID '{test_id}' 已存在")

        required_fields = ["test_name", "test_module", "test_tags", "test_priority", "test_description"]
        for field_name in required_fields:
            if field_name not in test_info:
                raise ValueError(f"缺少必需字段: {field_name}")

        test_case = TestCase(
            test_id=test_id,
            test_name=test_info["test_name"],
            test_module=test_info["test_module"],
            test_tags=test_info["test_tags"],
            test_priority=test_info["test_priority"],
            test_description=test_info["test_description"],
            test_steps=test_info.get("test_steps", []),
            test_expected_result=test_info.get("test_expected_result", ""),
            test_dependencies=test_info.get("test_dependencies", []),
            test_timeout=test_info.get("test_timeout", 30),
            test_function=test_info.get("test_function"),
            test_status=test_info.get("test_status", "pending"),
        )

        self._test_cases[test_id] = test_case
        self._update_indexes(test_id, test_case)

    def _update_indexes(self, test_id: str, test_case: TestCase) -> None:
        """更新索引"""
        for tag in test_case.test_tags:
            if tag not in self._tags_index:
                self._tags_index[tag] = set()
            self._tags_index[tag].add(test_id)

        priority = test_case.test_priority
        if priority not in self._priority_index:
            self._priority_index[priority] = set()
        self._priority_index[priority].add(test_id)

        module = test_case.test_module
        if module not in self._module_index:
            self._module_index[module] = set()
        self._module_index[module].add(test_id)

    def get_test_case(self, test_id: str) -> Optional[TestCase]:
        """
        获取测试用例信息

        Args:
            test_id: 测试用例ID

        Returns:
            TestCase对象，如果不存在则返回None
        """
        return self._test_cases.get(test_id)

    def get_test_cases_by_tag(self, tag: str) -> List[TestCase]:
        """
        按标记获取测试用例

        Args:
            tag: 测试标记

        Returns:
            匹配的测试用例列表
        """
        test_ids = self._tags_index.get(tag, set())
        return [self._test_cases[tid] for tid in test_ids if tid in self._test_cases]

    def get_test_cases_by_priority(self, priority: str) -> List[TestCase]:
        """
        按优先级获取测试用例

        Args:
            priority: 优先级 (P0, P1, P2, P3)

        Returns:
            匹配的测试用例列表
        """
        test_ids = self._priority_index.get(priority, set())
        return [self._test_cases[tid] for tid in test_ids if tid in self._test_cases]

    def get_test_cases_by_module(self, module: str) -> List[TestCase]:
        """
        按模块获取测试用例

        Args:
            module: 模块名称

        Returns:
            匹配的测试用例列表
        """
        test_ids = self._module_index.get(module, set())
        return [self._test_cases[tid] for tid in test_ids if tid in self._test_cases]

    def filter_test_cases(
        self, tags: Optional[List[str]] = None, priority: Optional[str] = None, module: Optional[str] = None
    ) -> List[TestCase]:
        """
        过滤测试用例

        Args:
            tags: 标记列表（AND关系）
            priority: 优先级
            module: 模块名称

        Returns:
            匹配的测试用例列表
        """
        result_ids: Optional[Set[str]] = None

        if tags:
            for tag in tags:
                tag_ids = self._tags_index.get(tag, set())
                if result_ids is None:
                    result_ids = tag_ids.copy()
                else:
                    result_ids &= tag_ids

        if priority:
            priority_ids = self._priority_index.get(priority, set())
            if result_ids is None:
                result_ids = priority_ids.copy()
            else:
                result_ids &= priority_ids

        if module:
            module_ids = self._module_index.get(module, set())
            if result_ids is None:
                result_ids = module_ids.copy()
            else:
                result_ids &= module_ids

        if result_ids is None:
            return list(self._test_cases.values())

        return [self._test_cases[tid] for tid in result_ids if tid in self._test_cases]

    def get_test_statistics(self) -> Dict[str, Any]:
        """
        获取测试统计信息

        Returns:
            包含统计信息的字典
        """
        total = len(self._test_cases)
        if total == 0:
            return {"total": 0, "by_priority": {}, "by_module": {}, "by_tag": {}, "by_status": {}}

        by_priority = {}
        for priority, test_ids in self._priority_index.items():
            by_priority[priority] = len(test_ids)

        by_module = {}
        for module, test_ids in self._module_index.items():
            by_module[module] = len(test_ids)

        by_tag = {}
        for tag, test_ids in self._tags_index.items():
            by_tag[tag] = len(test_ids)

        by_status = {"pending": 0, "running": 0, "passed": 0, "failed": 0, "skipped": 0}
        for test_case in self._test_cases.values():
            status = test_case.test_status
            if status in by_status:
                by_status[status] += 1

        return {
            "total": total,
            "by_priority": by_priority,
            "by_module": by_module,
            "by_tag": by_tag,
            "by_status": by_status,
            "execution_history_count": len(self._execution_history),
        }

    def export_test_cases(self, format: str = "json") -> str:
        """
        导出测试用例

        Args:
            format: 导出格式 ('json', 'dict')

        Returns:
            导出的测试用例数据

        Raises:
            ValueError: 不支持的格式
        """
        if format == "json":
            test_cases_dict = {test_id: test_case.to_dict() for test_id, test_case in self._test_cases.items()}
            return json.dumps(test_cases_dict, indent=2, ensure_ascii=False)
        elif format == "dict":
            return {test_id: test_case.to_dict() for test_id, test_case in self._test_cases.items()}
        else:
            raise ValueError(f"不支持的导出格式: {format}")

    def import_test_cases(self, data: str, format: str = "json") -> int:
        """
        导入测试用例

        Args:
            data: 测试用例数据
            format: 数据格式 ('json', 'dict')

        Returns:
            成功导入的测试用例数量
        """
        if format == "json":
            test_cases_dict = json.loads(data)
        elif format == "dict":
            test_cases_dict = data
        else:
            raise ValueError(f"不支持的导入格式: {format}")

        count = 0
        for test_id, test_info in test_cases_dict.items():
            try:
                self.register_test_case(test_id, test_info)
                count += 1
            except ValueError:
                continue

        return count

    def update_test_case(self, test_id: str, updates: Dict[str, Any]) -> bool:
        """
        更新测试用例

        Args:
            test_id: 测试用例ID
            updates: 更新字段字典

        Returns:
            是否更新成功
        """
        test_case = self.get_test_case(test_id)
        if not test_case:
            return False

        for field_name, value in updates.items():
            if hasattr(test_case, field_name):
                setattr(test_case, field_name, value)

        test_case.test_updated_at = datetime.now().isoformat()

        self._rebuild_indexes()
        return True

    def delete_test_case(self, test_id: str) -> bool:
        """
        删除测试用例

        Args:
            test_id: 测试用例ID

        Returns:
            是否删除成功
        """
        if test_id not in self._test_cases:
            return False

        del self._test_cases[test_id]
        self._rebuild_indexes()
        return True

    def _rebuild_indexes(self) -> None:
        """重建索引"""
        self._tags_index.clear()
        self._priority_index.clear()
        self._module_index.clear()

        for test_id, test_case in self._test_cases.items():
            self._update_indexes(test_id, test_case)

    def record_execution(
        self, test_id: str, status: str, execution_time: Optional[float] = None, error_message: Optional[str] = None
    ) -> None:
        """
        记录测试执行结果

        Args:
            test_id: 测试用例ID
            status: 执行状态
            execution_time: 执行时间（秒）
            error_message: 错误信息
        """
        test_case = self.get_test_case(test_id)
        if test_case:
            test_case.update_status(status, execution_time, error_message)

            self._execution_history.append(
                {
                    "test_id": test_id,
                    "status": status,
                    "execution_time": execution_time,
                    "error_message": error_message,
                    "timestamp": datetime.now().isoformat(),
                }
            )

    def get_execution_history(self, test_id: Optional[str] = None) -> List[Dict[str, Any]]:
        """
        获取执行历史

        Args:
            test_id: 测试用例ID（可选，不提供则返回所有历史）

        Returns:
            执行历史记录列表
        """
        if test_id:
            return [record for record in self._execution_history if record["test_id"] == test_id]
        return self._execution_history.copy()

    def clear_execution_history(self) -> None:
        """清空执行历史"""
        self._execution_history.clear()
        for test_case in self._test_cases.values():
            test_case.test_status = "pending"
            test_case.test_execution_time = None
            test_case.test_error_message = None


class ExecutionStrategy:
    """测试用例执行策略"""

    def __init__(self, manager: TestCaseManager):
        self.manager = manager

    def create_execution_plan(
        self,
        strategy: ExecutionStrategyType,
        test_ids: Optional[List[str]] = None,
        tags: Optional[List[str]] = None,
        priority: Optional[str] = None,
        module: Optional[str] = None,
    ) -> ExecutionPlan:
        """
        创建执行计划

        Args:
            strategy: 执行策略类型
            test_ids: 指定的测试用例ID列表
            tags: 标记过滤
            priority: 优先级过滤
            module: 模块过滤

        Returns:
            ExecutionPlan对象
        """
        if test_ids:
            test_cases = [self.manager.get_test_case(tid) for tid in test_ids if self.manager.get_test_case(tid)]
        else:
            test_cases = self.manager.filter_test_cases(tags, priority, module)

        test_id_list = [tc.test_id for tc in test_cases]
        execution_order = self._determine_order(strategy, test_cases)

        plan_id = f"plan_{datetime.now().strftime('%Y%m%d_%H%M%S')}"

        return ExecutionPlan(
            plan_id=plan_id, strategy_type=strategy, test_cases=test_id_list, execution_order=execution_order
        )

    def _determine_order(self, strategy: ExecutionStrategyType, test_cases: List[TestCase]) -> List[str]:
        """确定执行顺序"""
        if strategy == ExecutionStrategyType.SEQUENTIAL:
            return [tc.test_id for tc in test_cases]

        elif strategy == ExecutionStrategyType.PRIORITY_BASED:
            priority_order = {"P0": 0, "P1": 1, "P2": 2, "P3": 3}
            sorted_cases = sorted(test_cases, key=lambda tc: priority_order.get(tc.test_priority, 999))
            return [tc.test_id for tc in sorted_cases]

        elif strategy == ExecutionStrategyType.DEPENDENCY_BASED:
            return self._topological_sort(test_cases)

        elif strategy == ExecutionStrategyType.PARALLEL:
            return [tc.test_id for tc in test_cases]

        return [tc.test_id for tc in test_cases]

    def _topological_sort(self, test_cases: List[TestCase]) -> List[str]:
        """拓扑排序（按依赖关系）"""
        test_dict = {tc.test_id: tc for tc in test_cases}
        visited = set()
        result = []

        def visit(test_id: str):
            if test_id in visited:
                return
            if test_id not in test_dict:
                return

            visited.add(test_id)
            test_case = test_dict[test_id]

            for dep_id in test_case.test_dependencies:
                visit(dep_id)

            result.append(test_id)

        for test_case in test_cases:
            visit(test_case.test_id)

        return result

    def execute_plan(self, plan: ExecutionPlan) -> Dict[str, Any]:
        """
        执行测试计划

        Args:
            plan: 执行计划

        Returns:
            执行结果字典
        """
        results = {
            "plan_id": plan.plan_id,
            "strategy": plan.strategy_type.value,
            "total_tests": len(plan.execution_order),
            "passed": 0,
            "failed": 0,
            "skipped": 0,
            "errors": 0,
            "execution_time": 0.0,
            "test_results": [],
        }

        start_time = datetime.now()

        for test_id in plan.execution_order:
            test_case = self.manager.get_test_case(test_id)
            if not test_case:
                results["errors"] += 1
                continue

            if test_case.test_function:
                try:
                    test_start = datetime.now()
                    test_case.test_function()
                    test_end = datetime.now()
                    execution_time = (test_end - test_start).total_seconds()

                    self.manager.record_execution(test_id, "passed", execution_time)
                    results["passed"] += 1
                    results["test_results"].append(
                        {"test_id": test_id, "status": "passed", "execution_time": execution_time}
                    )
                except Exception as e:
                    self.manager.record_execution(test_id, "failed", None, str(e))
                    results["failed"] += 1
                    results["test_results"].append({"test_id": test_id, "status": "failed", "error": str(e)})
            else:
                self.manager.record_execution(test_id, "skipped")
                results["skipped"] += 1
                results["test_results"].append(
                    {"test_id": test_id, "status": "skipped", "reason": "No test function provided"}
                )

        end_time = datetime.now()
        results["execution_time"] = (end_time - start_time).total_seconds()

        return results


def pytest_configure(config):
    """注册pytest标记"""
    for tag in TestTag:
        config.addinivalue_line("markers", f"{tag.value}: mark test as {tag.value} test")

    for priority in TestPriority:
        config.addinivalue_line("markers", f"{priority.value}: mark test as {priority.value} priority test")

    for module in TestModule:
        config.addinivalue_line("markers", f"{module.value}: mark test as {module.value} module test")


_global_manager: Optional[TestCaseManager] = None


def get_test_case_manager() -> TestCaseManager:
    """获取全局测试用例管理器实例"""
    global _global_manager
    if _global_manager is None:
        _global_manager = TestCaseManager()
    return _global_manager


def test_case(test_id: str, **kwargs):
    """
    测试用例装饰器

    使用示例:
        @test_case(
            test_id='test_user_login_001',
            test_name='用户登录成功',
            test_module='auth',
            test_tags=['smoke', 'auth', 'P0'],
            test_priority='P0',
            test_description='测试用户使用正确的用户名和密码登录'
        )
        def test_user_login():
            # 测试代码
            pass
    """

    def decorator(func):
        manager = get_test_case_manager()

        test_info = {
            "test_name": kwargs.get("test_name", func.__name__),
            "test_module": kwargs.get("test_module", "public"),
            "test_tags": kwargs.get("test_tags", []),
            "test_priority": kwargs.get("test_priority", "P2"),
            "test_description": kwargs.get("test_description", func.__doc__ or ""),
            "test_steps": kwargs.get("test_steps", []),
            "test_expected_result": kwargs.get("test_expected_result", ""),
            "test_dependencies": kwargs.get("test_dependencies", []),
            "test_timeout": kwargs.get("test_timeout", 30),
            "test_function": func,
        }

        try:
            manager.register_test_case(test_id, test_info)
        except ValueError:
            pass

        for tag in test_info["test_tags"]:
            pytest.mark.__getattr__(tag)(func)

        return func

    return decorator


if __name__ == "__main__":
    manager = TestCaseManager()

    test_info_1 = {
        "test_name": "用户登录成功",
        "test_module": "auth",
        "test_tags": ["smoke", "auth", "P0"],
        "test_priority": "P0",
        "test_description": "测试用户使用正确的用户名和密码登录",
        "test_steps": ["发送登录请求", "验证响应状态码", "验证返回Token"],
        "test_expected_result": "登录成功，返回Token",
        "test_dependencies": [],
        "test_timeout": 30,
    }

    test_info_2 = {
        "test_name": "商家创建服务",
        "test_module": "merchant",
        "test_tags": ["regression", "api", "P1"],
        "test_priority": "P1",
        "test_description": "测试商家创建新服务",
        "test_steps": ["发送创建服务请求", "验证响应状态码", "验证服务数据"],
        "test_expected_result": "服务创建成功",
        "test_dependencies": ["test_user_login_001"],
        "test_timeout": 30,
    }

    manager.register_test_case("test_user_login_001", test_info_1)
    manager.register_test_case("test_merchant_create_service_001", test_info_2)

    print("测试统计信息:")
    stats = manager.get_test_statistics()
    print(json.dumps(stats, indent=2, ensure_ascii=False))

    print("\n按优先级获取测试用例 (P0):")
    p0_tests = manager.get_test_cases_by_priority("P0")
    for test in p0_tests:
        print(f"  - {test.test_id}: {test.test_name}")

    print("\n按模块获取测试用例 (merchant):")
    merchant_tests = manager.get_test_cases_by_module("merchant")
    for test in merchant_tests:
        print(f"  - {test.test_id}: {test.test_name}")

    print("\n过滤测试用例 (tag=smoke, priority=P0):")
    filtered_tests = manager.filter_test_cases(tags=["smoke"], priority="P0")
    for test in filtered_tests:
        print(f"  - {test.test_id}: {test.test_name}")

    print("\n导出测试用例 (JSON):")
    exported = manager.export_test_cases("json")
    print(exported[:200] + "...")

    strategy = ExecutionStrategy(manager)

    print("\n创建执行计划 (按优先级):")
    plan = strategy.create_execution_plan(ExecutionStrategyType.PRIORITY_BASED)
    print(f"计划ID: {plan.plan_id}")
    print(f"执行顺序: {plan.execution_order}")

    print("\n创建执行计划 (按依赖关系):")
    dep_plan = strategy.create_execution_plan(ExecutionStrategyType.DEPENDENCY_BASED)
    print(f"计划ID: {dep_plan.plan_id}")
    print(f"执行顺序: {dep_plan.execution_order}")

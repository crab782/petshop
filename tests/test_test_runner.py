#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
测试 test_runner.py 模块的功能
"""

import json
import pytest
from pathlib import Path
from tests.test_runner import (
    TestConfiguration,
    TestResult,
    TestResultItem,
    TestRunner,
    TestSuite,
    TestModule,
    TestMarker,
    Environment,
    ExecutionMode,
    create_test_runner,
)


class TestTestConfiguration:
    def test_default_configuration(self):
        config = TestConfiguration()
        assert config.suite is None
        assert config.module is None
        assert config.marker is None
        assert config.env == Environment.DEV
        assert config.parallel == 1
        assert config.retry == 0
        assert config.verbose is True
        assert config.coverage is False

    def test_configuration_with_parameters(self):
        config = TestConfiguration(
            suite=TestSuite.UNIT,
            module=TestModule.USER,
            marker=TestMarker.SMOKE,
            env=Environment.TEST,
            parallel=4,
            retry=2,
        )
        assert config.suite == TestSuite.UNIT
        assert config.module == TestModule.USER
        assert config.marker == TestMarker.SMOKE
        assert config.env == Environment.TEST
        assert config.parallel == 4
        assert config.retry == 2

    def test_load_from_file(self, tmp_path):
        config_file = tmp_path / "test_config.json"
        config_data = {
            "suite": "integration",
            "module": "merchant",
            "marker": "regression",
            "env": "prod",
            "parallel": 8,
            "retry": 3,
        }
        config_file.write_text(json.dumps(config_data), encoding="utf-8")

        config = TestConfiguration()
        config.load_from_file(config_file)

        assert config.suite == TestSuite.INTEGRATION
        assert config.module == TestModule.MERCHANT
        assert config.marker == TestMarker.REGRESSION
        assert config.env == Environment.PROD
        assert config.parallel == 8
        assert config.retry == 3

    def test_validate_valid_config(self):
        config = TestConfiguration(parallel=2, retry=1, timeout=30, base_url="http://localhost:8080")
        assert config.validate() is True

    def test_validate_invalid_config(self):
        config = TestConfiguration(parallel=0, retry=-1, timeout=0, base_url="invalid-url")
        assert config.validate() is False

    def test_to_dict(self):
        config = TestConfiguration(suite=TestSuite.UNIT, module=TestModule.USER, env=Environment.DEV, parallel=2)
        config_dict = config.to_dict()

        assert config_dict["suite"] == "unit"
        assert config_dict["module"] == "user"
        assert config_dict["env"] == "dev"
        assert config_dict["parallel"] == 2


class TestTestResult:
    def test_add_result(self):
        result = TestResult()
        item = TestResultItem(test_id="test_001", test_name="test_example", status="passed")
        result.add_result("test_001", item)

        assert len(result.results) == 1
        assert result.results[0].test_id == "test_001"

    def test_get_statistics(self):
        result = TestResult()
        result.start_time = 100.0
        result.end_time = 110.0

        result.add_result("test_001", TestResultItem("test_001", "test1", "passed"))
        result.add_result("test_002", TestResultItem("test_002", "test2", "passed"))
        result.add_result("test_003", TestResultItem("test_003", "test3", "failed"))
        result.add_result("test_004", TestResultItem("test_004", "test4", "skipped"))

        stats = result.get_statistics()

        assert stats["total"] == 4
        assert stats["passed"] == 2
        assert stats["failed"] == 1
        assert stats["skipped"] == 1
        assert stats["pass_rate"] == 50.0
        assert stats["duration"] == 10.0

    def test_get_failed_tests(self):
        result = TestResult()
        result.add_result("test_001", TestResultItem("test_001", "test1", "passed"))
        result.add_result("test_002", TestResultItem("test_002", "test2", "failed"))
        result.add_result("test_003", TestResultItem("test_003", "test3", "error"))

        failed_tests = result.get_failed_tests()

        assert len(failed_tests) == 2
        assert failed_tests[0].test_id == "test_002"
        assert failed_tests[1].test_id == "test_003"

    def test_export_json(self, tmp_path):
        result = TestResult()
        result.add_result("test_001", TestResultItem("test_001", "test1", "passed"))

        output_file = tmp_path / "results.json"
        output = result.export(format="json", output_path=output_file)

        assert output_file.exists()
        data = json.loads(output)
        assert data["statistics"]["total"] == 1

    def test_export_junit(self, tmp_path):
        result = TestResult()
        result.add_result("test_001", TestResultItem("test_001", "test1", "passed"))
        result.add_result("test_002", TestResultItem("test_002", "test2", "failed", error_message="Test failed"))

        output_file = tmp_path / "junit.xml"
        output = result.export(format="junit", output_path=output_file)

        assert output_file.exists()
        assert '<?xml version="1.0"' in output
        assert "<testsuite" in output


class TestTestRunner:
    def test_create_runner(self):
        config = TestConfiguration(suite=TestSuite.UNIT, parallel=2)
        runner = TestRunner(config)

        assert runner.config == config
        assert runner.tests_dir.exists()
        assert runner.report_dir.exists()

    def test_load_tests_by_suite(self):
        config = TestConfiguration(suite=TestSuite.UNIT)
        runner = TestRunner(config)

        test_paths = runner.load_tests()

        assert len(test_paths) > 0
        assert "unit" in test_paths[0]

    def test_load_tests_all(self):
        config = TestConfiguration()
        runner = TestRunner(config)

        test_paths = runner.load_tests()

        assert len(test_paths) > 0


class TestCreateTestRunner:
    def test_create_test_runner_with_parameters(self):
        runner = create_test_runner(suite="unit", module="user", marker="smoke", env="dev", parallel=2, retry=1)

        assert isinstance(runner, TestRunner)
        assert runner.config.suite == TestSuite.UNIT
        assert runner.config.module == TestModule.USER
        assert runner.config.marker == TestMarker.SMOKE
        assert runner.config.env == Environment.DEV
        assert runner.config.parallel == 2
        assert runner.config.retry == 1

    def test_create_test_runner_with_config_file(self, tmp_path):
        config_file = tmp_path / "config.json"
        config_data = {"suite": "integration", "parallel": 4, "retry": 2}
        config_file.write_text(json.dumps(config_data), encoding="utf-8")

        runner = create_test_runner(config_file=str(config_file))

        assert runner.config.suite == TestSuite.INTEGRATION
        assert runner.config.parallel == 4
        assert runner.config.retry == 2

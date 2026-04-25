"""
测试报告生成器的单元测试
"""

import json
import tempfile
import xml.etree.ElementTree as ET
from pathlib import Path

import pytest

from tests.report.generator import (
    CoverageData,
    CoverageReportGenerator,
    CoverageSummary,
    HTMLReportGenerator,
    NotificationConfig,
    NotificationSender,
    PerformanceMetric,
    PerformanceReportGenerator,
    TestResult,
    TestSummary,
)


class TestHTMLReportGenerator:
    def setup_method(self):
        self.generator = HTMLReportGenerator()
        self.temp_dir = tempfile.mkdtemp()

    def test_generate_report_with_passed_tests(self):
        test_results = [
            TestResult("test_example_1", "passed", 0.5),
            TestResult("test_example_2", "passed", 0.3),
        ]

        output_path = str(Path(self.temp_dir) / "test_report.html")
        result = self.generator.generate(test_results, output_path)

        assert Path(result).exists()
        content = Path(result).read_text(encoding="utf-8")
        assert "test_example_1" in content
        assert "test_example_2" in content
        assert "通过" in content

    def test_generate_report_with_failed_tests(self):
        test_results = [
            TestResult("test_pass", "passed", 0.5),
            TestResult("test_fail", "failed", 1.2, "AssertionError", "Traceback..."),
        ]

        output_path = str(Path(self.temp_dir) / "test_report.html")
        result = self.generator.generate(test_results, output_path)

        assert Path(result).exists()
        content = Path(result).read_text(encoding="utf-8")
        assert "失败测试详情" in content
        assert "AssertionError" in content

    def test_parse_pytest_json(self):
        pytest_data = {
            "tests": [
                {"name": "test_1", "outcome": "passed", "duration": 0.5},
                {
                    "name": "test_2",
                    "outcome": "failed",
                    "duration": 1.0,
                    "call": {"crash": {"message": "Error", "traceback": "Trace"}},
                },
            ]
        }

        json_path = str(Path(self.temp_dir) / "pytest.json")
        with open(json_path, "w", encoding="utf-8") as f:
            json.dump(pytest_data, f)

        results = self.generator.parse_pytest_json(json_path)

        assert len(results) == 2
        assert results[0].name == "test_1"
        assert results[0].status == "passed"
        assert results[1].status == "failed"
        assert results[1].error == "Error"

    def test_parse_junit_xml(self):
        root = ET.Element("testsuite")
        testcase1 = ET.SubElement(root, "testcase", name="test_1", time="0.5")
        testcase2 = ET.SubElement(root, "testcase", name="test_2", time="1.0")
        ET.SubElement(testcase2, "failure", message="Error message")

        xml_path = str(Path(self.temp_dir) / "junit.xml")
        tree = ET.ElementTree(root)
        tree.write(xml_path, encoding="unicode")

        results = self.generator.parse_junit_xml(xml_path)

        assert len(results) == 2
        assert results[0].status == "passed"
        assert results[1].status == "failed"
        assert results[1].error == "Error message"


class TestPerformanceReportGenerator:
    def setup_method(self):
        self.generator = PerformanceReportGenerator(output_dir=tempfile.mkdtemp())

    def test_generate_performance_report(self):
        metrics = [
            PerformanceMetric("GET /api/test", 50.0, 200, True),
            PerformanceMetric("POST /api/test", 100.0, 200, True),
            PerformanceMetric("GET /api/error", 200.0, 500, False),
        ]

        result = self.generator.generate(metrics)

        assert Path(result).exists()
        content = Path(result).read_text(encoding="utf-8")
        assert "性能测试报告" in content
        assert "3" in content
        assert "2" in content or "成功" in content

    def test_empty_metrics(self):
        result = self.generator.generate([])

        assert Path(result).exists()
        content = Path(result).read_text(encoding="utf-8")
        assert "性能测试报告" in content


class TestCoverageReportGenerator:
    def setup_method(self):
        self.generator = CoverageReportGenerator(output_dir=tempfile.mkdtemp())

    def test_generate_coverage_report(self):
        coverage = CoverageSummary(
            line_rate=0.75,
            lines_covered=750,
            lines_total=1000,
            files=[
                CoverageData("file1.py", 0.8, 0.75, 80, 100, 75, 100),
                CoverageData("file2.py", 0.7, 0.65, 70, 100, 65, 100),
            ],
        )

        result = self.generator.generate(coverage)

        assert Path(result).exists()
        content = Path(result).read_text(encoding="utf-8")
        assert "覆盖率报告" in content
        assert "75.00%" in content
        assert "file1.py" in content

    def test_parse_coverage_xml(self):
        root = ET.Element("coverage")
        package = ET.SubElement(root, "package")
        cls = ET.SubElement(package, "class", {"filename": "test.py", "line-rate": "0.8", "branch-rate": "0.75"})
        lines = ET.SubElement(cls, "lines")
        ET.SubElement(lines, "line", {"hits": "1", "number": "1"})
        ET.SubElement(lines, "line", {"hits": "0", "number": "2"})

        xml_path = str(Path(tempfile.mkdtemp()) / "coverage.xml")
        tree = ET.ElementTree(root)
        tree.write(xml_path, encoding="unicode")

        result = self.generator.parse_coverage_xml(xml_path)

        assert len(result.files) == 1
        assert result.files[0].file_path == "test.py"
        assert result.lines_total == 2


class TestNotificationSender:
    def test_notification_config(self):
        config = NotificationConfig(email_enabled=False, webhook_enabled=False)

        sender = NotificationSender(config)
        assert sender.config.email_enabled is False
        assert sender.config.webhook_enabled is False

    def test_send_disabled_notifications(self):
        config = NotificationConfig(email_enabled=False, webhook_enabled=False)

        sender = NotificationSender(config)
        summary = TestSummary(total=10, passed=8, failed=2)

        results = sender.send_test_notification(summary, "http://example.com/report.html")

        assert "email" not in results
        assert "webhook" not in results


class TestTestDataClasses:
    def test_test_result(self):
        result = TestResult(name="test_example", status="passed", duration=0.5)

        assert result.name == "test_example"
        assert result.status == "passed"
        assert result.duration == 0.5
        assert result.status_text == "通过"

    def test_test_summary(self):
        summary = TestSummary(total=10, passed=8, failed=2, skipped=0, duration=15.5)

        assert summary.total == 10
        assert summary.pass_rate == 80.0

    def test_performance_metric(self):
        metric = PerformanceMetric(name="GET /api/test", response_time=50.0, status_code=200, success=True)

        assert metric.name == "GET /api/test"
        assert metric.response_time == 50.0
        assert metric.success is True

    def test_coverage_data(self):
        data = CoverageData(
            file_path="test.py",
            line_rate=0.8,
            branch_rate=0.75,
            lines_covered=80,
            lines_total=100,
            branches_covered=75,
            branches_total=100,
        )

        assert data.file_path == "test.py"
        assert data.line_rate == 0.8


if __name__ == "__main__":
    pytest.main([__file__, "-v"])

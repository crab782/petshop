"""
测试报告生成器
支持HTML测试报告、性能测试报告、覆盖率报告和通知发送
"""

import json
import os
import smtplib
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from pathlib import Path
from typing import Any, Dict, List, Optional
from urllib.request import urlopen


@dataclass
class TestResult:
    name: str
    status: str
    duration: float
    error: Optional[str] = None
    traceback: Optional[str] = None

    @property
    def status_text(self) -> str:
        status_map = {"passed": "通过", "failed": "失败", "skipped": "跳过"}
        return status_map.get(self.status, self.status)


@dataclass
class TestSummary:
    total: int = 0
    passed: int = 0
    failed: int = 0
    skipped: int = 0
    duration: float = 0.0
    tests: List[TestResult] = field(default_factory=list)

    @property
    def pass_rate(self) -> float:
        if self.total == 0:
            return 0.0
        return round((self.passed / self.total) * 100, 2)


class HTMLReportGenerator:
    def __init__(self, template_dir: Optional[Path] = None):
        self.template_dir = template_dir or Path(__file__).parent / "templates"
        self.styles_dir = Path(__file__).parent / "styles"

    def generate(
        self,
        test_results: List[TestResult],
        output_path: str,
        title: str = "测试报告",
        project_name: str = "宠物服务平台",
    ) -> str:
        summary = self._calculate_summary(test_results)

        template = self._load_template("html_report_template.html")
        styles = self._load_styles("report.css")

        failed_tests = [t for t in test_results if t.status == "failed"]
        slowest_test = max(test_results, key=lambda t: t.duration) if test_results else None
        avg_duration = summary.duration / len(test_results) if test_results else 0

        html = template.render(
            title=title,
            styles=styles,
            generated_at=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            total_duration=f"{summary.duration:.2f}s",
            total_tests=summary.total,
            passed_tests=summary.passed,
            failed_tests=summary.failed,
            skipped_tests=summary.skipped,
            pass_rate=summary.pass_rate,
            failed_test_list=self._format_failed_tests(failed_tests),
            all_tests=self._format_all_tests(test_results),
            slowest_test={
                "name": slowest_test.name if slowest_test else "N/A",
                "duration": f"{slowest_test.duration:.2f}" if slowest_test else "0",
            },
            avg_duration=f"{avg_duration:.2f}",
            year=datetime.now().year,
        )

        output_file = Path(output_path)
        output_file.parent.mkdir(parents=True, exist_ok=True)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def _calculate_summary(self, test_results: List[TestResult]) -> TestSummary:
        summary = TestSummary()
        summary.total = len(test_results)
        summary.tests = test_results

        for test in test_results:
            summary.duration += test.duration
            if test.status == "passed":
                summary.passed += 1
            elif test.status == "failed":
                summary.failed += 1
            elif test.status == "skipped":
                summary.skipped += 1

        return summary

    def _load_template(self, template_name: str):
        from jinja2 import Template

        template_path = self.template_dir / template_name
        content = template_path.read_text(encoding="utf-8")
        return Template(content)

    def _load_styles(self, style_name: str) -> str:
        style_path = self.styles_dir / style_name
        return style_path.read_text(encoding="utf-8")

    def _format_failed_tests(self, tests: List[TestResult]) -> List[Dict]:
        return [
            {"name": t.name, "duration": f"{t.duration:.2f}", "error": t.error or "", "traceback": t.traceback or ""}
            for t in tests
        ]

    def _format_all_tests(self, tests: List[TestResult]) -> List[Dict]:
        return [
            {
                "name": t.name,
                "status": t.status,
                "status_text": t.status_text,
                "duration": f"{t.duration:.2f}",
                "error": t.error or "",
            }
            for t in tests
        ]

    def parse_pytest_json(self, json_path: str) -> List[TestResult]:
        with open(json_path, "r", encoding="utf-8") as f:
            data = json.load(f)

        results = []
        for test in data.get("tests", []):
            result = TestResult(
                name=test.get("name", "Unknown"),
                status=test.get("outcome", "unknown"),
                duration=test.get("duration", 0.0),
            )

            if test.get("outcome") == "failed":
                call = test.get("call", {})
                crash = call.get("crash", {})
                result.error = crash.get("message", "")
                result.traceback = crash.get("traceback", "")

            results.append(result)

        return results

    def parse_junit_xml(self, xml_path: str) -> List[TestResult]:
        tree = ET.parse(xml_path)
        root = tree.getroot()

        results = []
        for testcase in root.iter("testcase"):
            name = testcase.get("name", "Unknown")
            time = float(testcase.get("time", 0))

            failure = testcase.find("failure")
            skipped = testcase.find("skipped")

            if failure is not None:
                status = "failed"
                error = failure.get("message", "")
                traceback = failure.text or ""
            elif skipped is not None:
                status = "skipped"
                error = None
                traceback = None
            else:
                status = "passed"
                error = None
                traceback = None

            results.append(TestResult(name=name, status=status, duration=time, error=error, traceback=traceback))

        return results


@dataclass
class PerformanceMetric:
    name: str
    response_time: float
    status_code: int
    success: bool
    timestamp: float = field(default_factory=lambda: datetime.now().timestamp())


@dataclass
class PerformanceSummary:
    total_requests: int = 0
    successful_requests: int = 0
    failed_requests: int = 0
    avg_response_time: float = 0.0
    min_response_time: float = 0.0
    max_response_time: float = 0.0
    p50_response_time: float = 0.0
    p95_response_time: float = 0.0
    p99_response_time: float = 0.0
    throughput: float = 0.0
    error_rate: float = 0.0
    metrics: List[PerformanceMetric] = field(default_factory=list)


class PerformanceReportGenerator:
    def __init__(self, output_dir: str = "reports/performance"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

    def generate(
        self, metrics: List[PerformanceMetric], output_path: Optional[str] = None, title: str = "性能测试报告"
    ) -> str:
        summary = self._calculate_summary(metrics)

        if output_path is None:
            output_path = str(self.output_dir / f"performance_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")

        html = self._generate_html(summary, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def _calculate_summary(self, metrics: List[PerformanceMetric]) -> PerformanceSummary:
        if not metrics:
            return PerformanceSummary()

        summary = PerformanceSummary()
        summary.metrics = metrics
        summary.total_requests = len(metrics)
        summary.successful_requests = sum(1 for m in metrics if m.success)
        summary.failed_requests = summary.total_requests - summary.successful_requests

        response_times = [m.response_time for m in metrics]
        summary.avg_response_time = sum(response_times) / len(response_times)
        summary.min_response_time = min(response_times)
        summary.max_response_time = max(response_times)

        sorted_times = sorted(response_times)
        summary.p50_response_time = self._percentile(sorted_times, 50)
        summary.p95_response_time = self._percentile(sorted_times, 95)
        summary.p99_response_time = self._percentile(sorted_times, 99)

        if metrics:
            time_span = max(m.timestamp for m in metrics) - min(m.timestamp for m in metrics)
            if time_span > 0:
                summary.throughput = summary.total_requests / time_span

        summary.error_rate = (
            (summary.failed_requests / summary.total_requests * 100) if summary.total_requests > 0 else 0
        )

        return summary

    def _percentile(self, sorted_data: List[float], percentile: int) -> float:
        if not sorted_data:
            return 0.0
        index = int(len(sorted_data) * percentile / 100)
        index = min(index, len(sorted_data) - 1)
        return sorted_data[index]

    def _generate_html(self, summary: PerformanceSummary, title: str) -> str:
        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        .stat-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            text-align: center;
            border-left: 4px solid #667eea;
        }}
        .stat-number {{
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }}
        .stat-label {{
            font-size: 14px;
            color: #666;
        }}
        .metrics-table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        .metrics-table th,
        .metrics-table td {{
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }}
        .metrics-table th {{
            background-color: #f8f9fa;
            font-weight: 600;
        }}
        .success {{ color: #28a745; }}
        .error {{ color: #dc3545; }}
        .warning {{ color: #ffc107; }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <h2>性能概览</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.total_requests}</div>
                    <div class="stat-label">总请求数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number success">{summary.successful_requests}</div>
                    <div class="stat-label">成功请求</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number error">{summary.failed_requests}</div>
                    <div class="stat-label">失败请求</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.error_rate:.2f}%</div>
                    <div class="stat-label">错误率</div>
                </div>
            </div>
            
            <h2>响应时间统计</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.avg_response_time:.2f}ms</div>
                    <div class="stat-label">平均响应时间</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.min_response_time:.2f}ms</div>
                    <div class="stat-label">最小响应时间</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.max_response_time:.2f}ms</div>
                    <div class="stat-label">最大响应时间</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.throughput:.2f}</div>
                    <div class="stat-label">吞吐量 (req/s)</div>
                </div>
            </div>
            
            <h2>百分位响应时间</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.p50_response_time:.2f}ms</div>
                    <div class="stat-label">P50</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number warning">{summary.p95_response_time:.2f}ms</div>
                    <div class="stat-label">P95</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number error">{summary.p99_response_time:.2f}ms</div>
                    <div class="stat-label">P99</div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>"""


@dataclass
class CoverageData:
    file_path: str
    line_rate: float
    branch_rate: float
    lines_covered: int
    lines_total: int
    branches_covered: int
    branches_total: int


@dataclass
class CoverageSummary:
    line_rate: float = 0.0
    branch_rate: float = 0.0
    lines_covered: int = 0
    lines_total: int = 0
    branches_covered: int = 0
    branches_total: int = 0
    files: List[CoverageData] = field(default_factory=list)


class CoverageReportGenerator:
    def __init__(self, output_dir: str = "reports/coverage"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

    def generate(
        self, coverage_data: CoverageSummary, output_path: Optional[str] = None, title: str = "代码覆盖率报告"
    ) -> str:
        if output_path is None:
            output_path = str(self.output_dir / f"coverage_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")

        html = self._generate_html(coverage_data, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def parse_coverage_xml(self, xml_path: str) -> CoverageSummary:
        tree = ET.parse(xml_path)
        root = tree.getroot()

        summary = CoverageSummary()

        for package in root.iter("package"):
            for cls in package.iter("class"):
                coverage = CoverageData(
                    file_path=cls.get("filename", "Unknown"),
                    line_rate=float(cls.get("line-rate", 0)),
                    branch_rate=float(cls.get("branch-rate", 0)),
                    lines_covered=0,
                    lines_total=0,
                    branches_covered=0,
                    branches_total=0,
                )

                lines = cls.find("lines")
                if lines is not None:
                    for line in lines.iter("line"):
                        coverage.lines_total += 1
                        if line.get("hits", "0") != "0":
                            coverage.lines_covered += 1

                summary.files.append(coverage)
                summary.lines_covered += coverage.lines_covered
                summary.lines_total += coverage.lines_total

        if summary.lines_total > 0:
            summary.line_rate = summary.lines_covered / summary.lines_total

        return summary

    def _generate_html(self, summary: CoverageSummary, title: str) -> str:
        files_html = ""
        for file in sorted(summary.files, key=lambda f: f.line_rate):
            color = self._get_coverage_color(file.line_rate * 100)
            files_html += f"""
            <tr>
                <td>{file.file_path}</td>
                <td style="color: {color}; font-weight: bold;">{file.line_rate * 100:.2f}%</td>
                <td>{file.lines_covered}/{file.lines_total}</td>
                <td>
                    <div style="background-color: #e0e0e0; height: 20px; border-radius: 10px; overflow: hidden;">
                        <div style="background-color: {color}; width: {file.line_rate * 100}%; height: 100%;"></div>
                    </div>
                </td>
            </tr>"""

        overall_color = self._get_coverage_color(summary.line_rate * 100)

        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .coverage-badge {{
            display: inline-block;
            padding: 20px 40px;
            border-radius: 10px;
            font-size: 48px;
            font-weight: bold;
            color: white;
            margin: 20px 0;
        }}
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }}
        .stat-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            text-align: center;
            border-left: 4px solid #667eea;
        }}
        .stat-number {{
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }}
        .stat-label {{
            font-size: 14px;
            color: #666;
        }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        th, td {{
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }}
        th {{
            background-color: #f8f9fa;
            font-weight: 600;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <div style="text-align: center;">
                <h2>总体覆盖率</h2>
                <div class="coverage-badge" style="background-color: {overall_color};">
                    {summary.line_rate * 100:.2f}%
                </div>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.lines_covered}</div>
                    <div class="stat-label">覆盖行数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.lines_total}</div>
                    <div class="stat-label">总行数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{len(summary.files)}</div>
                    <div class="stat-label">文件数</div>
                </div>
            </div>
            
            <h2>文件覆盖率详情</h2>
            <table>
                <thead>
                    <tr>
                        <th>文件路径</th>
                        <th>覆盖率</th>
                        <th>覆盖行数</th>
                        <th>覆盖率条</th>
                    </tr>
                </thead>
                <tbody>
                    {files_html}
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>"""

    def _get_coverage_color(self, percentage: float) -> str:
        if percentage >= 80:
            return "#28a745"
        elif percentage >= 60:
            return "#ffc107"
        elif percentage >= 40:
            return "#fd7e14"
        else:
            return "#dc3545"


@dataclass
class NotificationConfig:
    email_enabled: bool = False
    email_smtp_host: str = ""
    email_smtp_port: int = 587
    email_username: str = ""
    email_password: str = ""
    email_recipients: List[str] = field(default_factory=list)

    webhook_enabled: bool = False
    webhook_url: str = ""
    webhook_headers: Dict[str, str] = field(default_factory=dict)


class NotificationSender:
    def __init__(self, config: NotificationConfig):
        self.config = config
        self.template_dir = Path(__file__).parent / "templates"

    def send_test_notification(
        self, summary: TestSummary, report_url: str, project_name: str = "宠物服务平台"
    ) -> Dict[str, bool]:
        results = {}

        if self.config.email_enabled:
            results["email"] = self._send_email(summary, report_url, project_name)

        if self.config.webhook_enabled:
            results["webhook"] = self._send_webhook(summary, report_url, project_name)

        return results

    def _send_email(self, summary: TestSummary, report_url: str, project_name: str) -> bool:
        try:
            template = self._load_email_template()

            status_class = "success" if summary.failed == 0 else "failure"
            status_icon = "✅" if summary.failed == 0 else "❌"
            status_message = "所有测试通过" if summary.failed == 0 else f"{summary.failed} 个测试失败"

            failed_list = [t for t in summary.tests if t.status == "failed"][:5]
            failed_tests_html = "\n".join([f"<li>{t.name}</li>" for t in failed_list])

            email_body = template.render(
                project_name=project_name,
                test_date=datetime.now().strftime("%Y-%m-%d"),
                status_class=status_class,
                status_icon=status_icon,
                status_message=status_message,
                pass_rate=summary.pass_rate,
                total_tests=summary.total,
                passed_tests=summary.passed,
                failed_tests=summary.failed,
                skipped_tests=summary.skipped,
                total_duration=f"{summary.duration:.2f}s",
                failed_test_list=failed_list,
                report_url=report_url,
                year=datetime.now().year,
            )

            msg = MIMEMultipart("alternative")
            msg["Subject"] = f'测试报告 - {project_name} - {datetime.now().strftime("%Y-%m-%d")}'
            msg["From"] = self.config.email_username
            msg["To"] = ", ".join(self.config.email_recipients)

            msg.attach(MIMEText(email_body, "html", "utf-8"))

            with smtplib.SMTP(self.config.email_smtp_host, self.config.email_smtp_port) as server:
                server.starttls()
                server.login(self.config.email_username, self.config.email_password)
                server.send_message(msg)

            return True
        except Exception as e:
            print(f"发送邮件失败: {e}")
            return False

    def _send_webhook(self, summary: TestSummary, report_url: str, project_name: str) -> bool:
        try:
            import requests

            payload = {
                "project_name": project_name,
                "test_date": datetime.now().isoformat(),
                "total_tests": summary.total,
                "passed_tests": summary.passed,
                "failed_tests": summary.failed,
                "skipped_tests": summary.skipped,
                "pass_rate": summary.pass_rate,
                "duration": summary.duration,
                "status": "success" if summary.failed == 0 else "failure",
                "report_url": report_url,
            }

            response = requests.post(
                self.config.webhook_url, json=payload, headers=self.config.webhook_headers, timeout=10
            )

            return response.status_code == 200
        except Exception as e:
            print(f"发送Webhook失败: {e}")
            return False

    def _load_email_template(self):
        from jinja2 import Template

        template_path = self.template_dir / "email_template.html"
        content = template_path.read_text(encoding="utf-8")
        return Template(content)


def generate_report_from_pytest(
    json_report_path: str, output_path: str = "reports/test_report.html", title: str = "测试报告"
) -> str:
    generator = HTMLReportGenerator()
    test_results = generator.parse_pytest_json(json_report_path)
    return generator.generate(test_results, output_path, title)


def generate_report_from_junit(
    xml_report_path: str, output_path: str = "reports/test_report.html", title: str = "测试报告"
) -> str:
    generator = HTMLReportGenerator()
    test_results = generator.parse_junit_xml(xml_report_path)
    return generator.generate(test_results, output_path, title)


@dataclass
class APICoverageData:
    endpoint: str
    method: str
    description: str
    tested: bool
    test_cases: int = 0
    passed_cases: int = 0
    failed_cases: int = 0
    last_test_time: Optional[datetime] = None


@dataclass
class APICoverageSummary:
    total_apis: int = 0
    tested_apis: int = 0
    untested_apis: int = 0
    coverage_rate: float = 0.0
    total_test_cases: int = 0
    passed_test_cases: int = 0
    failed_test_cases: int = 0
    apis: List[APICoverageData] = field(default_factory=list)

    @property
    def pass_rate(self) -> float:
        if self.total_test_cases == 0:
            return 0.0
        return round((self.passed_test_cases / self.total_test_cases) * 100, 2)


class APICoverageAnalyzer:
    def __init__(self, output_dir: str = "reports/api_coverage"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self.template_dir = Path(__file__).parent / "templates"

    def calculate_api_coverage(
        self, all_apis: List[Dict[str, str]], test_results: List[TestResult]
    ) -> APICoverageSummary:
        tested_api_map: Dict[str, APICoverageData] = {}

        for test in test_results:
            api_info = self._extract_api_from_test(test.name)
            if api_info:
                key = f"{api_info['method']}:{api_info['endpoint']}"
                if key not in tested_api_map:
                    tested_api_map[key] = APICoverageData(
                        endpoint=api_info["endpoint"],
                        method=api_info["method"],
                        description=api_info.get("description", ""),
                        tested=True,
                        test_cases=0,
                        passed_cases=0,
                        failed_cases=0,
                        last_test_time=datetime.now(),
                    )

                tested_api_map[key].test_cases += 1
                if test.status == "passed":
                    tested_api_map[key].passed_cases += 1
                elif test.status == "failed":
                    tested_api_map[key].failed_cases += 1

        all_api_data = []
        for api in all_apis:
            key = f"{api.get('method', 'GET')}:{api.get('endpoint', '')}"
            if key in tested_api_map:
                all_api_data.append(tested_api_map[key])
            else:
                all_api_data.append(
                    APICoverageData(
                        endpoint=api.get("endpoint", ""),
                        method=api.get("method", "GET"),
                        description=api.get("description", ""),
                        tested=False,
                    )
                )

        summary = APICoverageSummary()
        summary.total_apis = len(all_api_data)
        summary.tested_apis = sum(1 for api in all_api_data if api.tested)
        summary.untested_apis = summary.total_apis - summary.tested_apis
        summary.coverage_rate = (summary.tested_apis / summary.total_apis * 100) if summary.total_apis > 0 else 0
        summary.total_test_cases = sum(api.test_cases for api in all_api_data)
        summary.passed_test_cases = sum(api.passed_cases for api in all_api_data)
        summary.failed_test_cases = sum(api.failed_cases for api in all_api_data)
        summary.apis = all_api_data

        return summary

    def get_tested_apis(self, summary: APICoverageSummary) -> List[APICoverageData]:
        return [api for api in summary.apis if api.tested]

    def get_untested_apis(self, summary: APICoverageSummary) -> List[APICoverageData]:
        return [api for api in summary.apis if not api.tested]

    def generate_api_coverage_report(
        self, summary: APICoverageSummary, output_path: Optional[str] = None, title: str = "API接口覆盖率报告"
    ) -> str:
        if output_path is None:
            output_path = str(self.output_dir / f"api_coverage_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")

        html = self._generate_api_coverage_html(summary, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def _extract_api_from_test(self, test_name: str) -> Optional[Dict[str, str]]:
        import re

        patterns = [
            r"test_(get|post|put|delete|patch)_(.+)",
            r"test_(.+)_(get|post|put|delete|patch)",
            r"(GET|POST|PUT|DELETE|PATCH)\s+(.+)",
        ]

        for pattern in patterns:
            match = re.search(pattern, test_name, re.IGNORECASE)
            if match:
                groups = match.groups()
                if len(groups) >= 2:
                    method = (
                        groups[0].upper()
                        if groups[0].upper() in ["GET", "POST", "PUT", "DELETE", "PATCH"]
                        else groups[1].upper()
                    )
                    endpoint = (
                        groups[1] if groups[0].upper() in ["GET", "POST", "PUT", "DELETE", "PATCH"] else groups[0]
                    )
                    return {
                        "method": method,
                        "endpoint": f"/api/{endpoint.replace('_', '/')}",
                        "description": f"Test for {method} {endpoint}",
                    }

        return None

    def _generate_api_coverage_html(self, summary: APICoverageSummary, title: str) -> str:
        tested_apis = self.get_tested_apis(summary)
        untested_apis = self.get_untested_apis(summary)

        tested_apis_html = ""
        for api in sorted(tested_apis, key=lambda a: a.endpoint):
            pass_rate = (api.passed_cases / api.test_cases * 100) if api.test_cases > 0 else 0
            color = "#28a745" if pass_rate >= 80 else "#ffc107" if pass_rate >= 60 else "#dc3545"
            tested_apis_html += f"""
            <tr>
                <td><span class="method-badge method-{api.method.lower()}">{api.method}</span></td>
                <td>{api.endpoint}</td>
                <td>{api.description}</td>
                <td>{api.test_cases}</td>
                <td style="color: {color}; font-weight: bold;">{pass_rate:.1f}%</td>
                <td>{api.passed_cases}</td>
                <td>{api.failed_cases}</td>
            </tr>"""

        untested_apis_html = ""
        for api in sorted(untested_apis, key=lambda a: a.endpoint):
            untested_apis_html += f"""
            <tr>
                <td><span class="method-badge method-{api.method.lower()}">{api.method}</span></td>
                <td>{api.endpoint}</td>
                <td>{api.description}</td>
                <td colspan="4" style="text-align: center; color: #dc3545;">未测试</td>
            </tr>"""

        coverage_color = (
            "#28a745" if summary.coverage_rate >= 80 else "#ffc107" if summary.coverage_rate >= 60 else "#dc3545"
        )

        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .coverage-badge {{
            display: inline-block;
            padding: 20px 40px;
            border-radius: 10px;
            font-size: 48px;
            font-weight: bold;
            color: white;
            margin: 20px 0;
        }}
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }}
        .stat-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            text-align: center;
            border-left: 4px solid #667eea;
        }}
        .stat-number {{
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }}
        .stat-label {{
            font-size: 14px;
            color: #666;
        }}
        .method-badge {{
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            color: white;
        }}
        .method-get {{ background-color: #28a745; }}
        .method-post {{ background-color: #007bff; }}
        .method-put {{ background-color: #ffc107; color: #333; }}
        .method-delete {{ background-color: #dc3545; }}
        .method-patch {{ background-color: #17a2b8; }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        th, td {{
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }}
        th {{
            background-color: #f8f9fa;
            font-weight: 600;
        }}
        .chart-container {{
            margin: 30px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 10px;
        }}
        .export-buttons {{
            margin: 20px 0;
            text-align: right;
        }}
        .export-btn {{
            display: inline-block;
            padding: 10px 20px;
            margin-left: 10px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }}
        .export-btn:hover {{
            background-color: #5568d3;
        }}
        .section {{
            margin: 40px 0;
        }}
        .section h2 {{
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <div style="text-align: center;">
                <h2>API接口覆盖率</h2>
                <div class="coverage-badge" style="background-color: {coverage_color};">
                    {summary.coverage_rate:.2f}%
                </div>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.total_apis}</div>
                    <div class="stat-label">总API数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #28a745;">{summary.tested_apis}</div>
                    <div class="stat-label">已测试API</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #dc3545;">{summary.untested_apis}</div>
                    <div class="stat-label">未测试API</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.total_test_cases}</div>
                    <div class="stat-label">总测试用例数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #28a745;">{summary.passed_test_cases}</div>
                    <div class="stat-label">通过用例数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #dc3545;">{summary.failed_test_cases}</div>
                    <div class="stat-label">失败用例数</div>
                </div>
            </div>
            
            <div class="chart-container">
                <canvas id="coverageChart" width="400" height="200"></canvas>
            </div>
            
            <div class="export-buttons">
                <button class="export-btn" onclick="exportToCSV()">导出CSV</button>
                <button class="export-btn" onclick="exportToJSON()">导出JSON</button>
            </div>
            
            <div class="section">
                <h2>已测试API列表</h2>
                <table>
                    <thead>
                        <tr>
                            <th>方法</th>
                            <th>端点</th>
                            <th>描述</th>
                            <th>测试用例数</th>
                            <th>通过率</th>
                            <th>通过数</th>
                            <th>失败数</th>
                        </tr>
                    </thead>
                    <tbody>
                        {tested_apis_html}
                    </tbody>
                </table>
            </div>
            
            <div class="section">
                <h2>未测试API列表</h2>
                <table>
                    <thead>
                        <tr>
                            <th>方法</th>
                            <th>端点</th>
                            <th>描述</th>
                            <th colspan="4">状态</th>
                        </tr>
                    </thead>
                    <tbody>
                        {untested_apis_html}
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <script>
        const ctx = document.getElementById('coverageChart').getContext('2d');
        new Chart(ctx, {{
            type: 'doughnut',
            data: {{
                labels: ['已测试API', '未测试API'],
                datasets: [{{
                    data: [{summary.tested_apis}, {summary.untested_apis}],
                    backgroundColor: ['#28a745', '#dc3545']
                }}]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    legend: {{
                        position: 'bottom'
                    }}
                }}
            }}
        }});
        
        function exportToCSV() {{
            const data = {json.dumps([{{'method': api.method, 'endpoint': api.endpoint, 'description': api.description, 'tested': api.tested, 'test_cases': api.test_cases, 'passed': api.passed_cases, 'failed': api.failed_cases}} for api in summary.apis])};
            let csv = 'Method,Endpoint,Description,Tested,Test Cases,Passed,Failed\\n';
            data.forEach(row => {{
                csv += `${{row.method}},${{row.endpoint}},${{row.description}},${{row.tested}},${{row.test_cases}},${{row.passed}},${{row.failed}}\\n`;
            }});
            const blob = new Blob([csv], {{ type: 'text/csv' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'api_coverage.csv';
            a.click();
        }}
        
        function exportToJSON() {{
            const data = {json.dumps([{{'method': api.method, 'endpoint': api.endpoint, 'description': api.description, 'tested': api.tested, 'test_cases': api.test_cases, 'passed': api.passed_cases, 'failed': api.failed_cases}} for api in summary.apis])};
            const blob = new Blob([JSON.stringify(data, null, 2)], {{ type: 'application/json' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'api_coverage.json';
            a.click();
        }}
    </script>
</body>
</html>"""


@dataclass
class ScenarioCoverageData:
    scenario_id: str
    scenario_name: str
    category: str
    description: str
    tested: bool
    test_cases: int = 0
    passed_cases: int = 0
    failed_cases: int = 0
    priority: str = "medium"
    last_test_time: Optional[datetime] = None


@dataclass
class ScenarioCoverageSummary:
    total_scenarios: int = 0
    tested_scenarios: int = 0
    untested_scenarios: int = 0
    coverage_rate: float = 0.0
    total_test_cases: int = 0
    passed_test_cases: int = 0
    failed_test_cases: int = 0
    categories: Dict[str, int] = field(default_factory=dict)
    scenarios: List[ScenarioCoverageData] = field(default_factory=list)

    @property
    def pass_rate(self) -> float:
        if self.total_test_cases == 0:
            return 0.0
        return round((self.passed_test_cases / self.total_test_cases) * 100, 2)


class ScenarioCoverageAnalyzer:
    def __init__(self, output_dir: str = "reports/scenario_coverage"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self.template_dir = Path(__file__).parent / "templates"

    def calculate_scenario_coverage(
        self, all_scenarios: List[Dict[str, Any]], test_results: List[TestResult]
    ) -> ScenarioCoverageSummary:
        tested_scenario_map: Dict[str, ScenarioCoverageData] = {}

        for test in test_results:
            scenario_info = self._extract_scenario_from_test(test.name)
            if scenario_info:
                scenario_id = scenario_info["scenario_id"]
                if scenario_id not in tested_scenario_map:
                    tested_scenario_map[scenario_id] = ScenarioCoverageData(
                        scenario_id=scenario_id,
                        scenario_name=scenario_info["scenario_name"],
                        category=scenario_info.get("category", "General"),
                        description=scenario_info.get("description", ""),
                        tested=True,
                        test_cases=0,
                        passed_cases=0,
                        failed_cases=0,
                        priority=scenario_info.get("priority", "medium"),
                        last_test_time=datetime.now(),
                    )

                tested_scenario_map[scenario_id].test_cases += 1
                if test.status == "passed":
                    tested_scenario_map[scenario_id].passed_cases += 1
                elif test.status == "failed":
                    tested_scenario_map[scenario_id].failed_cases += 1

        all_scenario_data = []
        categories_count: Dict[str, int] = {}

        for scenario in all_scenarios:
            scenario_id = scenario.get("id", "")
            category = scenario.get("category", "General")
            categories_count[category] = categories_count.get(category, 0) + 1

            if scenario_id in tested_scenario_map:
                all_scenario_data.append(tested_scenario_map[scenario_id])
            else:
                all_scenario_data.append(
                    ScenarioCoverageData(
                        scenario_id=scenario_id,
                        scenario_name=scenario.get("name", ""),
                        category=category,
                        description=scenario.get("description", ""),
                        tested=False,
                        priority=scenario.get("priority", "medium"),
                    )
                )

        summary = ScenarioCoverageSummary()
        summary.total_scenarios = len(all_scenario_data)
        summary.tested_scenarios = sum(1 for s in all_scenario_data if s.tested)
        summary.untested_scenarios = summary.total_scenarios - summary.tested_scenarios
        summary.coverage_rate = (
            (summary.tested_scenarios / summary.total_scenarios * 100) if summary.total_scenarios > 0 else 0
        )
        summary.total_test_cases = sum(s.test_cases for s in all_scenario_data)
        summary.passed_test_cases = sum(s.passed_cases for s in all_scenario_data)
        summary.failed_test_cases = sum(s.failed_cases for s in all_scenario_data)
        summary.categories = categories_count
        summary.scenarios = all_scenario_data

        return summary

    def get_tested_scenarios(self, summary: ScenarioCoverageSummary) -> List[ScenarioCoverageData]:
        return [s for s in summary.scenarios if s.tested]

    def get_untested_scenarios(self, summary: ScenarioCoverageSummary) -> List[ScenarioCoverageData]:
        return [s for s in summary.scenarios if not s.tested]

    def generate_scenario_coverage_report(
        self, summary: ScenarioCoverageSummary, output_path: Optional[str] = None, title: str = "测试场景覆盖率报告"
    ) -> str:
        if output_path is None:
            output_path = str(self.output_dir / f"scenario_coverage_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")

        html = self._generate_scenario_coverage_html(summary, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def _extract_scenario_from_test(self, test_name: str) -> Optional[Dict[str, Any]]:
        import re

        scenario_patterns = [
            (
                r"test_(user|merchant|admin)_(.+)",
                lambda m: {
                    "category": m.group(1).capitalize() + " Module",
                    "scenario_name": m.group(2).replace("_", " ").title(),
                },
            ),
            (
                r"test_(login|register|payment|order|product)",
                lambda m: {"category": "Core Feature", "scenario_name": m.group(1).capitalize()},
            ),
            (
                r"test_(api|integration|unit)_(.+)",
                lambda m: {
                    "category": m.group(1).upper() + " Test",
                    "scenario_name": m.group(2).replace("_", " ").title(),
                },
            ),
        ]

        for pattern, extractor in scenario_patterns:
            match = re.search(pattern, test_name, re.IGNORECASE)
            if match:
                result = extractor(match)
                result["scenario_id"] = f"{result['category']}_{result['scenario_name']}".replace(" ", "_")
                result["description"] = f"Test scenario for {result['scenario_name']}"
                result["priority"] = (
                    "high" if "login" in test_name.lower() or "payment" in test_name.lower() else "medium"
                )
                return result

        return None

    def _generate_scenario_coverage_html(self, summary: ScenarioCoverageSummary, title: str) -> str:
        tested_scenarios = self.get_tested_scenarios(summary)
        untested_scenarios = self.get_untested_scenarios(summary)

        tested_html = ""
        for scenario in sorted(tested_scenarios, key=lambda s: (s.category, s.scenario_name)):
            pass_rate = (scenario.passed_cases / scenario.test_cases * 100) if scenario.test_cases > 0 else 0
            color = "#28a745" if pass_rate >= 80 else "#ffc107" if pass_rate >= 60 else "#dc3545"
            priority_color = (
                "#dc3545" if scenario.priority == "high" else "#ffc107" if scenario.priority == "medium" else "#28a745"
            )
            tested_html += f"""
            <tr>
                <td><span class="priority-badge" style="background-color: {priority_color};">{scenario.priority.upper()}</span></td>
                <td>{scenario.scenario_name}</td>
                <td><span class="category-badge">{scenario.category}</span></td>
                <td>{scenario.description}</td>
                <td>{scenario.test_cases}</td>
                <td style="color: {color}; font-weight: bold;">{pass_rate:.1f}%</td>
                <td>{scenario.passed_cases}</td>
                <td>{scenario.failed_cases}</td>
            </tr>"""

        untested_html = ""
        for scenario in sorted(untested_scenarios, key=lambda s: (s.category, s.scenario_name)):
            priority_color = (
                "#dc3545" if scenario.priority == "high" else "#ffc107" if scenario.priority == "medium" else "#28a745"
            )
            untested_html += f"""
            <tr>
                <td><span class="priority-badge" style="background-color: {priority_color};">{scenario.priority.upper()}</span></td>
                <td>{scenario.scenario_name}</td>
                <td><span class="category-badge">{scenario.category}</span></td>
                <td>{scenario.description}</td>
                <td colspan="4" style="text-align: center; color: #dc3545;">未测试</td>
            </tr>"""

        categories_chart_data = ", ".join([f"'{k}': {v}" for k, v in summary.categories.items()])
        coverage_color = (
            "#28a745" if summary.coverage_rate >= 80 else "#ffc107" if summary.coverage_rate >= 60 else "#dc3545"
        )

        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .coverage-badge {{
            display: inline-block;
            padding: 20px 40px;
            border-radius: 10px;
            font-size: 48px;
            font-weight: bold;
            color: white;
            margin: 20px 0;
        }}
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }}
        .stat-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            text-align: center;
            border-left: 4px solid #667eea;
        }}
        .stat-number {{
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }}
        .stat-label {{
            font-size: 14px;
            color: #666;
        }}
        .priority-badge {{
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
            color: white;
        }}
        .category-badge {{
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            background-color: #e9ecef;
            color: #495057;
        }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        th, td {{
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }}
        th {{
            background-color: #f8f9fa;
            font-weight: 600;
        }}
        .chart-container {{
            margin: 30px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 10px;
        }}
        .export-buttons {{
            margin: 20px 0;
            text-align: right;
        }}
        .export-btn {{
            display: inline-block;
            padding: 10px 20px;
            margin-left: 10px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }}
        .export-btn:hover {{
            background-color: #5568d3;
        }}
        .section {{
            margin: 40px 0;
        }}
        .section h2 {{
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }}
        .charts-row {{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin: 30px 0;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <div style="text-align: center;">
                <h2>测试场景覆盖率</h2>
                <div class="coverage-badge" style="background-color: {coverage_color};">
                    {summary.coverage_rate:.2f}%
                </div>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.total_scenarios}</div>
                    <div class="stat-label">总场景数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #28a745;">{summary.tested_scenarios}</div>
                    <div class="stat-label">已测试场景</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #dc3545;">{summary.untested_scenarios}</div>
                    <div class="stat-label">未测试场景</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.total_test_cases}</div>
                    <div class="stat-label">总测试用例数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #28a745;">{summary.passed_test_cases}</div>
                    <div class="stat-label">通过用例数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #dc3545;">{summary.failed_test_cases}</div>
                    <div class="stat-label">失败用例数</div>
                </div>
            </div>
            
            <div class="charts-row">
                <div class="chart-container">
                    <canvas id="coverageChart"></canvas>
                </div>
                <div class="chart-container">
                    <canvas id="categoryChart"></canvas>
                </div>
            </div>
            
            <div class="export-buttons">
                <button class="export-btn" onclick="exportToCSV()">导出CSV</button>
                <button class="export-btn" onclick="exportToJSON()">导出JSON</button>
            </div>
            
            <div class="section">
                <h2>已测试场景列表</h2>
                <table>
                    <thead>
                        <tr>
                            <th>优先级</th>
                            <th>场景名称</th>
                            <th>分类</th>
                            <th>描述</th>
                            <th>测试用例数</th>
                            <th>通过率</th>
                            <th>通过数</th>
                            <th>失败数</th>
                        </tr>
                    </thead>
                    <tbody>
                        {tested_html}
                    </tbody>
                </table>
            </div>
            
            <div class="section">
                <h2>未测试场景列表</h2>
                <table>
                    <thead>
                        <tr>
                            <th>优先级</th>
                            <th>场景名称</th>
                            <th>分类</th>
                            <th>描述</th>
                            <th colspan="4">状态</th>
                        </tr>
                    </thead>
                    <tbody>
                        {untested_html}
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <script>
        const coverageCtx = document.getElementById('coverageChart').getContext('2d');
        new Chart(coverageCtx, {{
            type: 'doughnut',
            data: {{
                labels: ['已测试场景', '未测试场景'],
                datasets: [{{
                    data: [{summary.tested_scenarios}, {summary.untested_scenarios}],
                    backgroundColor: ['#28a745', '#dc3545']
                }}]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    legend: {{ position: 'bottom' }},
                    title: {{ display: true, text: '场景覆盖率分布' }}
                }}
            }}
        }});
        
        const categoryCtx = document.getElementById('categoryChart').getContext('2d');
        new Chart(categoryCtx, {{
            type: 'bar',
            data: {{
                labels: {json.dumps(list(summary.categories.keys()))},
                datasets: [{{
                    label: '场景数量',
                    data: {json.dumps(list(summary.categories.values()))},
                    backgroundColor: '#667eea'
                }}]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    legend: {{ display: false }},
                    title: {{ display: true, text: '分类场景分布' }}
                }},
                scales: {{
                    y: {{ beginAtZero: true }}
                }}
            }}
        }});
        
        function exportToCSV() {{
            const data = {json.dumps([{{'scenario_id': s.scenario_id, 'scenario_name': s.scenario_name, 'category': s.category, 'description': s.description, 'tested': s.tested, 'test_cases': s.test_cases, 'passed': s.passed_cases, 'failed': s.failed_cases, 'priority': s.priority}} for s in summary.scenarios])};
            let csv = 'ID,Name,Category,Description,Tested,Test Cases,Passed,Failed,Priority\\n';
            data.forEach(row => {{
                csv += `${{row.scenario_id}},${{row.scenario_name}},${{row.category}},${{row.description}},${{row.tested}},${{row.test_cases}},${{row.passed}},${{row.failed}},${{row.priority}}\\n`;
            }});
            const blob = new Blob([csv], {{ type: 'text/csv' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'scenario_coverage.csv';
            a.click();
        }}
        
        function exportToJSON() {{
            const data = {json.dumps([{{'scenario_id': s.scenario_id, 'scenario_name': s.scenario_name, 'category': s.category, 'description': s.description, 'tested': s.tested, 'test_cases': s.test_cases, 'passed': s.passed_cases, 'failed': s.failed_cases, 'priority': s.priority}} for s in summary.scenarios])};
            const blob = new Blob([JSON.stringify(data, null, 2)], {{ type: 'application/json' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'scenario_coverage.json';
            a.click();
        }}
    </script>
</body>
</html>"""


@dataclass
class PerformanceVisualizationData:
    endpoint: str
    method: str
    response_times: List[float]
    status_codes: List[int]
    timestamps: List[float]
    concurrent_users: int = 1
    avg_response_time: float = 0.0
    min_response_time: float = 0.0
    max_response_time: float = 0.0
    p50_response_time: float = 0.0
    p95_response_time: float = 0.0
    p99_response_time: float = 0.0
    throughput: float = 0.0
    error_rate: float = 0.0


@dataclass
class PerformanceVisualizationSummary:
    total_endpoints: int = 0
    total_requests: int = 0
    successful_requests: int = 0
    failed_requests: int = 0
    overall_avg_response_time: float = 0.0
    overall_throughput: float = 0.0
    overall_error_rate: float = 0.0
    endpoints: List[PerformanceVisualizationData] = field(default_factory=list)
    time_series_data: Dict[str, List[float]] = field(default_factory=dict)


class PerformanceVisualizationAnalyzer:
    def __init__(self, output_dir: str = "reports/performance_viz"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self.template_dir = Path(__file__).parent / "templates"

    def visualize_performance_results(
        self, metrics: List[PerformanceMetric], output_path: Optional[str] = None, title: str = "性能测试可视化报告"
    ) -> str:
        summary = self._calculate_visualization_summary(metrics)

        if output_path is None:
            output_path = str(self.output_dir / f"performance_viz_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")

        html = self._generate_visualization_html(summary, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def generate_performance_charts(self, metrics: List[PerformanceMetric], chart_type: str = "all") -> Dict[str, str]:
        summary = self._calculate_visualization_summary(metrics)
        charts = {}

        if chart_type in ["all", "response_time"]:
            charts["response_time_distribution"] = self._generate_response_time_distribution_chart(summary)

        if chart_type in ["all", "throughput"]:
            charts["throughput_trend"] = self._generate_throughput_trend_chart(summary)

        if chart_type in ["all", "error_rate"]:
            charts["error_rate_chart"] = self._generate_error_rate_chart(summary)

        if chart_type in ["all", "comparison"]:
            charts["endpoint_comparison"] = self._generate_endpoint_comparison_chart(summary)

        return charts

    def compare_performance_results(
        self,
        baseline_metrics: List[PerformanceMetric],
        current_metrics: List[PerformanceMetric],
        output_path: Optional[str] = None,
        title: str = "性能测试对比报告",
    ) -> str:
        baseline_summary = self._calculate_visualization_summary(baseline_metrics)
        current_summary = self._calculate_visualization_summary(current_metrics)

        comparison_data = self._calculate_comparison(baseline_summary, current_summary)

        if output_path is None:
            output_path = str(
                self.output_dir / f"performance_comparison_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html"
            )

        html = self._generate_comparison_html(baseline_summary, current_summary, comparison_data, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def _calculate_visualization_summary(self, metrics: List[PerformanceMetric]) -> PerformanceVisualizationSummary:
        if not metrics:
            return PerformanceVisualizationSummary()

        endpoint_map: Dict[str, PerformanceVisualizationData] = {}
        time_series: Dict[str, List[float]] = {"timestamps": [], "response_times": [], "throughput": []}

        for metric in metrics:
            key = f"{metric.name}"
            if key not in endpoint_map:
                endpoint_map[key] = PerformanceVisualizationData(
                    endpoint=metric.name,
                    method="GET",
                    response_times=[],
                    status_codes=[],
                    timestamps=[],
                    concurrent_users=1,
                )

            endpoint_map[key].response_times.append(metric.response_time)
            endpoint_map[key].status_codes.append(metric.status_code)
            endpoint_map[key].timestamps.append(metric.timestamp)

            time_series["timestamps"].append(metric.timestamp)
            time_series["response_times"].append(metric.response_time)

        for endpoint_data in endpoint_map.values():
            if endpoint_data.response_times:
                endpoint_data.avg_response_time = sum(endpoint_data.response_times) / len(endpoint_data.response_times)
                endpoint_data.min_response_time = min(endpoint_data.response_times)
                endpoint_data.max_response_time = max(endpoint_data.response_times)

                sorted_times = sorted(endpoint_data.response_times)
                endpoint_data.p50_response_time = self._percentile(sorted_times, 50)
                endpoint_data.p95_response_time = self._percentile(sorted_times, 95)
                endpoint_data.p99_response_time = self._percentile(sorted_times, 99)

                total = len(endpoint_data.status_codes)
                errors = sum(1 for code in endpoint_data.status_codes if code >= 400)
                endpoint_data.error_rate = (errors / total * 100) if total > 0 else 0

                if endpoint_data.timestamps:
                    time_span = max(endpoint_data.timestamps) - min(endpoint_data.timestamps)
                    if time_span > 0:
                        endpoint_data.throughput = total / time_span

        summary = PerformanceVisualizationSummary()
        summary.endpoints = list(endpoint_map.values())
        summary.total_endpoints = len(endpoint_map)
        summary.total_requests = len(metrics)
        summary.successful_requests = sum(1 for m in metrics if m.success)
        summary.failed_requests = summary.total_requests - summary.successful_requests

        all_response_times = [m.response_time for m in metrics]
        if all_response_times:
            summary.overall_avg_response_time = sum(all_response_times) / len(all_response_times)

        if metrics:
            time_span = max(m.timestamp for m in metrics) - min(m.timestamp for m in metrics)
            if time_span > 0:
                summary.overall_throughput = summary.total_requests / time_span

        summary.overall_error_rate = (
            (summary.failed_requests / summary.total_requests * 100) if summary.total_requests > 0 else 0
        )
        summary.time_series_data = time_series

        return summary

    def _percentile(self, sorted_data: List[float], percentile: int) -> float:
        if not sorted_data:
            return 0.0
        index = int(len(sorted_data) * percentile / 100)
        index = min(index, len(sorted_data) - 1)
        return sorted_data[index]

    def _calculate_comparison(
        self, baseline: PerformanceVisualizationSummary, current: PerformanceVisualizationSummary
    ) -> Dict[str, Any]:
        return {
            "response_time_change": current.overall_avg_response_time - baseline.overall_avg_response_time,
            "response_time_change_percent": (
                (current.overall_avg_response_time - baseline.overall_avg_response_time)
                / baseline.overall_avg_response_time
                * 100
                if baseline.overall_avg_response_time > 0
                else 0
            ),
            "throughput_change": current.overall_throughput - baseline.overall_throughput,
            "throughput_change_percent": (
                (current.overall_throughput - baseline.overall_throughput) / baseline.overall_throughput * 100
                if baseline.overall_throughput > 0
                else 0
            ),
            "error_rate_change": current.overall_error_rate - baseline.overall_error_rate,
            "total_requests_change": current.total_requests - baseline.total_requests,
            "improvement": current.overall_avg_response_time < baseline.overall_avg_response_time,
        }

    def _generate_response_time_distribution_chart(self, summary: PerformanceVisualizationSummary) -> str:
        return "Response time distribution chart data"

    def _generate_throughput_trend_chart(self, summary: PerformanceVisualizationSummary) -> str:
        return "Throughput trend chart data"

    def _generate_error_rate_chart(self, summary: PerformanceVisualizationSummary) -> str:
        return "Error rate chart data"

    def _generate_endpoint_comparison_chart(self, summary: PerformanceVisualizationSummary) -> str:
        return "Endpoint comparison chart data"

    def _generate_visualization_html(self, summary: PerformanceVisualizationSummary, title: str) -> str:
        endpoints_data = json.dumps(
            [
                {
                    "endpoint": e.endpoint,
                    "avg_response_time": round(e.avg_response_time, 2),
                    "min_response_time": round(e.min_response_time, 2),
                    "max_response_time": round(e.max_response_time, 2),
                    "p50": round(e.p50_response_time, 2),
                    "p95": round(e.p95_response_time, 2),
                    "p99": round(e.p99_response_time, 2),
                    "throughput": round(e.throughput, 2),
                    "error_rate": round(e.error_rate, 2),
                    "total_requests": len(e.response_times),
                }
                for e in summary.endpoints
            ]
        )

        time_series_json = json.dumps(
            {
                "timestamps": summary.time_series_data.get("timestamps", []),
                "response_times": summary.time_series_data.get("response_times", []),
            }
        )

        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }}
        .stat-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            text-align: center;
            border-left: 4px solid #667eea;
        }}
        .stat-number {{
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }}
        .stat-label {{
            font-size: 14px;
            color: #666;
        }}
        .chart-container {{
            margin: 30px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 10px;
        }}
        .charts-row {{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin: 30px 0;
        }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }}
        th, td {{
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }}
        th {{
            background-color: #f8f9fa;
            font-weight: 600;
        }}
        .export-buttons {{
            margin: 20px 0;
            text-align: right;
        }}
        .export-btn {{
            display: inline-block;
            padding: 10px 20px;
            margin-left: 10px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }}
        .export-btn:hover {{
            background-color: #5568d3;
        }}
        .section {{
            margin: 40px 0;
        }}
        .section h2 {{
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }}
        .performance-good {{ color: #28a745; }}
        .performance-warning {{ color: #ffc107; }}
        .performance-bad {{ color: #dc3545; }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <h2>性能概览</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number">{summary.total_requests}</div>
                    <div class="stat-label">总请求数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number performance-good">{summary.successful_requests}</div>
                    <div class="stat-label">成功请求</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number performance-bad">{summary.failed_requests}</div>
                    <div class="stat-label">失败请求</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.overall_avg_response_time:.2f}ms</div>
                    <div class="stat-label">平均响应时间</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{summary.overall_throughput:.2f}</div>
                    <div class="stat-label">吞吐量 (req/s)</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number {'performance-good' if summary.overall_error_rate < 5 else 'performance-bad'}">{summary.overall_error_rate:.2f}%</div>
                    <div class="stat-label">错误率</div>
                </div>
            </div>
            
            <div class="charts-row">
                <div class="chart-container">
                    <canvas id="responseTimeChart"></canvas>
                </div>
                <div class="chart-container">
                    <canvas id="throughputChart"></canvas>
                </div>
            </div>
            
            <div class="chart-container">
                <canvas id="responseTimeTrendChart"></canvas>
            </div>
            
            <div class="export-buttons">
                <button class="export-btn" onclick="exportToCSV()">导出CSV</button>
                <button class="export-btn" onclick="exportToJSON()">导出JSON</button>
            </div>
            
            <div class="section">
                <h2>端点性能详情</h2>
                <table>
                    <thead>
                        <tr>
                            <th>端点</th>
                            <th>请求数</th>
                            <th>平均响应时间</th>
                            <th>最小响应时间</th>
                            <th>最大响应时间</th>
                            <th>P50</th>
                            <th>P95</th>
                            <th>P99</th>
                            <th>吞吐量</th>
                            <th>错误率</th>
                        </tr>
                    </thead>
                    <tbody id="endpointsTable">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <script>
        const endpointsData = {endpoints_data};
        const timeSeriesData = {time_series_json};
        
        const tableBody = document.getElementById('endpointsTable');
        endpointsData.forEach(endpoint => {{
            const row = tableBody.insertRow();
            row.innerHTML = `
                <td>${{endpoint.endpoint}}</td>
                <td>${{endpoint.total_requests}}</td>
                <td>${{endpoint.avg_response_time.toFixed(2)}}ms</td>
                <td>${{endpoint.min_response_time.toFixed(2)}}ms</td>
                <td>${{endpoint.max_response_time.toFixed(2)}}ms</td>
                <td>${{endpoint.p50.toFixed(2)}}ms</td>
                <td>${{endpoint.p95.toFixed(2)}}ms</td>
                <td>${{endpoint.p99.toFixed(2)}}ms</td>
                <td>${{endpoint.throughput.toFixed(2)}}</td>
                <td class="${{endpoint.error_rate < 5 ? 'performance-good' : 'performance-bad'}}">${{endpoint.error_rate.toFixed(2)}}%</td>
            `;
        }});
        
        const responseTimeCtx = document.getElementById('responseTimeChart').getContext('2d');
        new Chart(responseTimeCtx, {{
            type: 'bar',
            data: {{
                labels: endpointsData.map(e => e.endpoint),
                datasets: [{{
                    label: '平均响应时间 (ms)',
                    data: endpointsData.map(e => e.avg_response_time),
                    backgroundColor: '#667eea'
                }}]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    title: {{ display: true, text: '端点响应时间对比' }}
                }},
                scales: {{
                    y: {{ beginAtZero: true }}
                }}
            }}
        }});
        
        const throughputCtx = document.getElementById('throughputChart').getContext('2d');
        new Chart(throughputCtx, {{
            type: 'bar',
            data: {{
                labels: endpointsData.map(e => e.endpoint),
                datasets: [{{
                    label: '吞吐量 (req/s)',
                    data: endpointsData.map(e => e.throughput),
                    backgroundColor: '#28a745'
                }}]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    title: {{ display: true, text: '端点吞吐量对比' }}
                }},
                scales: {{
                    y: {{ beginAtZero: true }}
                }}
            }}
        }});
        
        if (timeSeriesData.timestamps.length > 0) {{
            const trendCtx = document.getElementById('responseTimeTrendChart').getContext('2d');
            const labels = timeSeriesData.timestamps.map((t, i) => i);
            new Chart(trendCtx, {{
                type: 'line',
                data: {{
                    labels: labels,
                    datasets: [{{
                        label: '响应时间 (ms)',
                        data: timeSeriesData.response_times,
                        borderColor: '#667eea',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        fill: true,
                        tension: 0.4
                    }}]
                }},
                options: {{
                    responsive: true,
                    plugins: {{
                        title: {{ display: true, text: '响应时间趋势' }}
                    }},
                    scales: {{
                        y: {{ beginAtZero: true }}
                    }}
                }}
            }});
        }}
        
        function exportToCSV() {{
            let csv = 'Endpoint,Total Requests,Avg Response Time,Min Response Time,Max Response Time,P50,P95,P99,Throughput,Error Rate\\n';
            endpointsData.forEach(row => {{
                csv += `${{row.endpoint}},${{row.total_requests}},${{row.avg_response_time}},${{row.min_response_time}},${{row.max_response_time}},${{row.p50}},${{row.p95}},${{row.p99}},${{row.throughput}},${{row.error_rate}}\\n`;
            }});
            const blob = new Blob([csv], {{ type: 'text/csv' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'performance_data.csv';
            a.click();
        }}
        
        function exportToJSON() {{
            const blob = new Blob([JSON.stringify(endpointsData, null, 2)], {{ type: 'application/json' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'performance_data.json';
            a.click();
        }}
    </script>
</body>
</html>"""

    def _generate_comparison_html(
        self,
        baseline: PerformanceVisualizationSummary,
        current: PerformanceVisualizationSummary,
        comparison: Dict[str, Any],
        title: str,
    ) -> str:
        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .comparison-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }}
        .comparison-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            border-left: 4px solid #667eea;
        }}
        .comparison-title {{
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
        }}
        .comparison-values {{
            display: flex;
            justify-content: space-between;
            margin: 10px 0;
        }}
        .value-label {{
            color: #666;
        }}
        .value-number {{
            font-size: 24px;
            font-weight: bold;
            color: #667eea;
        }}
        .change-positive {{
            color: #28a745;
        }}
        .change-negative {{
            color: #dc3545;
        }}
        .chart-container {{
            margin: 30px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 10px;
        }}
        .improvement-badge {{
            display: inline-block;
            padding: 10px 20px;
            border-radius: 5px;
            font-size: 18px;
            font-weight: bold;
            color: white;
            margin: 20px 0;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <div style="text-align: center;">
                <h2>性能对比结果</h2>
                <div class="improvement-badge" style="background-color: {'#28a745' if comparison['improvement'] else '#dc3545'};">
                    {'性能提升 ✓' if comparison['improvement'] else '性能下降 ✗'}
                </div>
            </div>
            
            <div class="comparison-grid">
                <div class="comparison-card">
                    <div class="comparison-title">平均响应时间</div>
                    <div class="comparison-values">
                        <div>
                            <div class="value-label">基线</div>
                            <div class="value-number">{baseline.overall_avg_response_time:.2f}ms</div>
                        </div>
                        <div>
                            <div class="value-label">当前</div>
                            <div class="value-number">{current.overall_avg_response_time:.2f}ms</div>
                        </div>
                    </div>
                    <div class="comparison-values">
                        <div class="value-label">变化</div>
                        <div class="value-number {'change-positive' if comparison['response_time_change'] < 0 else 'change-negative'}">
                            {comparison['response_time_change']:.2f}ms ({comparison['response_time_change_percent']:.2f}%)
                        </div>
                    </div>
                </div>
                
                <div class="comparison-card">
                    <div class="comparison-title">吞吐量</div>
                    <div class="comparison-values">
                        <div>
                            <div class="value-label">基线</div>
                            <div class="value-number">{baseline.overall_throughput:.2f}</div>
                        </div>
                        <div>
                            <div class="value-label">当前</div>
                            <div class="value-number">{current.overall_throughput:.2f}</div>
                        </div>
                    </div>
                    <div class="comparison-values">
                        <div class="value-label">变化</div>
                        <div class="value-number {'change-positive' if comparison['throughput_change'] > 0 else 'change-negative'}">
                            {comparison['throughput_change']:.2f} ({comparison['throughput_change_percent']:.2f}%)
                        </div>
                    </div>
                </div>
                
                <div class="comparison-card">
                    <div class="comparison-title">错误率</div>
                    <div class="comparison-values">
                        <div>
                            <div class="value-label">基线</div>
                            <div class="value-number">{baseline.overall_error_rate:.2f}%</div>
                        </div>
                        <div>
                            <div class="value-label">当前</div>
                            <div class="value-number">{current.overall_error_rate:.2f}%</div>
                        </div>
                    </div>
                    <div class="comparison-values">
                        <div class="value-label">变化</div>
                        <div class="value-number {'change-positive' if comparison['error_rate_change'] < 0 else 'change-negative'}">
                            {comparison['error_rate_change']:.2f}%
                        </div>
                    </div>
                </div>
                
                <div class="comparison-card">
                    <div class="comparison-title">总请求数</div>
                    <div class="comparison-values">
                        <div>
                            <div class="value-label">基线</div>
                            <div class="value-number">{baseline.total_requests}</div>
                        </div>
                        <div>
                            <div class="value-label">当前</div>
                            <div class="value-number">{current.total_requests}</div>
                        </div>
                    </div>
                    <div class="comparison-values">
                        <div class="value-label">变化</div>
                        <div class="value-number">
                            {comparison['total_requests_change']}
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="chart-container">
                <canvas id="comparisonChart"></canvas>
            </div>
        </div>
    </div>
    
    <script>
        const ctx = document.getElementById('comparisonChart').getContext('2d');
        new Chart(ctx, {{
            type: 'bar',
            data: {{
                labels: ['平均响应时间', '吞吐量', '错误率'],
                datasets: [
                    {{
                        label: '基线',
                        data: [{baseline.overall_avg_response_time}, {baseline.overall_throughput}, {baseline.overall_error_rate}],
                        backgroundColor: '#667eea'
                    }},
                    {{
                        label: '当前',
                        data: [{current.overall_avg_response_time}, {current.overall_throughput}, {current.overall_error_rate}],
                        backgroundColor: '#28a745'
                    }}
                ]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    title: {{ display: true, text: '性能指标对比' }}
                }},
                scales: {{
                    y: {{ beginAtZero: true }}
                }}
            }}
        }});
    </script>
</body>
</html>"""


@dataclass
class FailedTestData:
    test_name: str
    error_message: str
    traceback: str
    duration: float
    failure_type: str
    category: str
    severity: str
    suggested_fix: str
    related_tests: List[str] = field(default_factory=list)
    timestamp: Optional[datetime] = None


@dataclass
class FailedTestAnalysis:
    total_failures: int = 0
    categorized_failures: Dict[str, List[FailedTestData]] = field(default_factory=dict)
    severity_distribution: Dict[str, int] = field(default_factory=dict)
    common_patterns: List[str] = field(default_factory=list)
    recommendations: List[str] = field(default_factory=list)
    failed_tests: List[FailedTestData] = field(default_factory=list)


class FailureAnalyzer:
    def __init__(self, output_dir: str = "reports/failure_analysis"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self.template_dir = Path(__file__).parent / "templates"

        self.failure_categories = {
            "AssertionError": "断言错误",
            "TimeoutError": "超时错误",
            "ConnectionError": "连接错误",
            "ValueError": "值错误",
            "TypeError": "类型错误",
            "KeyError": "键错误",
            "AttributeError": "属性错误",
            "ImportError": "导入错误",
            "PermissionError": "权限错误",
            "FileNotFoundError": "文件未找到",
            "Exception": "其他错误",
        }

        self.fix_suggestions = {
            "AssertionError": [
                "检查预期值和实际值是否匹配",
                "验证测试数据是否正确",
                "确认业务逻辑是否符合预期",
                "检查断言条件是否过于严格",
            ],
            "TimeoutError": [
                "增加超时时间设置",
                "优化代码执行效率",
                "检查是否存在死循环或阻塞操作",
                "考虑使用异步处理",
            ],
            "ConnectionError": [
                "检查网络连接状态",
                "验证服务器地址和端口配置",
                "检查防火墙设置",
                "确认服务是否正常运行",
            ],
            "ValueError": [
                "验证输入数据格式",
                "检查数据范围和边界条件",
                "添加数据验证逻辑",
                "确认数据类型转换是否正确",
            ],
            "TypeError": ["检查变量类型", "添加类型检查和转换", "验证函数参数类型", "使用类型提示进行静态检查"],
            "KeyError": ["验证字典键是否存在", "使用 .get() 方法安全访问", "检查数据结构是否正确", "添加默认值处理"],
            "AttributeError": [
                "验证对象属性是否存在",
                "检查对象是否正确初始化",
                "确认属性名称拼写",
                "使用 hasattr() 检查属性",
            ],
            "ImportError": ["检查模块是否已安装", "验证导入路径是否正确", "检查 Python 环境配置", "确认模块名称拼写"],
            "PermissionError": ["检查文件或目录权限", "以管理员身份运行", "修改文件权限设置", "确认用户访问权限"],
            "FileNotFoundError": [
                "验证文件路径是否正确",
                "检查文件是否存在",
                "确认工作目录设置",
                "使用绝对路径或相对路径",
            ],
        }

    def analyze_failed_tests(self, test_results: List[TestResult]) -> FailedTestAnalysis:
        failed_tests = [t for t in test_results if t.status == "failed"]

        analysis = FailedTestAnalysis()
        analysis.total_failures = len(failed_tests)

        for test in failed_tests:
            failed_data = self._create_failed_test_data(test)
            analysis.failed_tests.append(failed_data)

            category = failed_data.category
            if category not in analysis.categorized_failures:
                analysis.categorized_failures[category] = []
            analysis.categorized_failures[category].append(failed_data)

            severity = failed_data.severity
            analysis.severity_distribution[severity] = analysis.severity_distribution.get(severity, 0) + 1

        analysis.common_patterns = self._identify_common_patterns(analysis.failed_tests)
        analysis.recommendations = self._generate_recommendations(analysis)

        return analysis

    def categorize_failures(self, analysis: FailedTestAnalysis) -> Dict[str, List[FailedTestData]]:
        return analysis.categorized_failures

    def generate_failure_report(
        self, analysis: FailedTestAnalysis, output_path: Optional[str] = None, title: str = "失败测试分析报告"
    ) -> str:
        if output_path is None:
            output_path = str(self.output_dir / f"failure_analysis_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")

        html = self._generate_failure_html(analysis, title)

        output_file = Path(output_path)
        output_file.write_text(html, encoding="utf-8")

        return str(output_file.absolute())

    def suggest_fixes(self, failed_test: FailedTestData) -> List[str]:
        suggestions = []

        for error_type, fixes in self.fix_suggestions.items():
            if error_type in failed_test.error_message or error_type in failed_test.traceback:
                suggestions.extend(fixes)

        if not suggestions:
            suggestions = [
                "仔细阅读错误信息和堆栈跟踪",
                "检查相关代码逻辑",
                "查看测试文档和规范",
                "咨询团队成员或查找相关资料",
            ]

        return suggestions[:5]

    def _create_failed_test_data(self, test: TestResult) -> FailedTestData:
        failure_type = self._identify_failure_type(test.error or "", test.traceback or "")
        category = self.failure_categories.get(failure_type, "其他错误")
        severity = self._determine_severity(test.error or "", test.traceback or "")
        suggested_fix = "; ".join(
            self.suggest_fixes(
                FailedTestData(
                    test_name=test.name,
                    error_message=test.error or "",
                    traceback=test.traceback or "",
                    duration=test.duration,
                    failure_type=failure_type,
                    category=category,
                    severity=severity,
                    suggested_fix="",
                )
            )
        )

        return FailedTestData(
            test_name=test.name,
            error_message=test.error or "",
            traceback=test.traceback or "",
            duration=test.duration,
            failure_type=failure_type,
            category=category,
            severity=severity,
            suggested_fix=suggested_fix,
            timestamp=datetime.now(),
        )

    def _identify_failure_type(self, error_message: str, traceback: str) -> str:
        combined = f"{error_message} {traceback}"

        for error_type in self.failure_categories.keys():
            if error_type in combined:
                return error_type

        return "Exception"

    def _determine_severity(self, error_message: str, traceback: str) -> str:
        combined = f"{error_message} {traceback}".lower()

        critical_keywords = ["security", "authentication", "authorization", "data loss", "corruption"]
        high_keywords = ["timeout", "connection", "network", "database"]
        medium_keywords = ["assertion", "validation", "format"]

        for keyword in critical_keywords:
            if keyword in combined:
                return "Critical"

        for keyword in high_keywords:
            if keyword in combined:
                return "High"

        for keyword in medium_keywords:
            if keyword in combined:
                return "Medium"

        return "Low"

    def _identify_common_patterns(self, failed_tests: List[FailedTestData]) -> List[str]:
        patterns = []

        category_counts: Dict[str, int] = {}
        for test in failed_tests:
            category_counts[test.category] = category_counts.get(test.category, 0) + 1

        for category, count in sorted(category_counts.items(), key=lambda x: x[1], reverse=True):
            if count > 1:
                patterns.append(f"{category}出现{count}次")

        severity_counts: Dict[str, int] = {}
        for test in failed_tests:
            severity_counts[test.severity] = severity_counts.get(test.severity, 0) + 1

        for severity, count in severity_counts.items():
            if count > 0:
                patterns.append(f"{severity}级别错误{count}个")

        return patterns[:10]

    def _generate_recommendations(self, analysis: FailedTestAnalysis) -> List[str]:
        recommendations = []

        if analysis.total_failures == 0:
            return ["所有测试通过，继续保持！"]

        if "Critical" in analysis.severity_distribution and analysis.severity_distribution["Critical"] > 0:
            recommendations.append(f"优先处理{analysis.severity_distribution['Critical']}个严重级别的错误")

        if "High" in analysis.severity_distribution and analysis.severity_distribution["High"] > 0:
            recommendations.append(f"尽快修复{analysis.severity_distribution['High']}个高级别错误")

        most_common_category = (
            max(analysis.categorized_failures.items(), key=lambda x: len(x[1]))
            if analysis.categorized_failures
            else None
        )
        if most_common_category:
            recommendations.append(
                f"重点关注{most_common_category[0]}问题，共有{len(most_common_category[1])}个失败案例"
            )

        if analysis.total_failures > 5:
            recommendations.append("建议进行代码审查和重构，减少测试失败率")

        recommendations.append("建议增加单元测试覆盖率，提前发现问题")
        recommendations.append("建议定期运行测试，及时发现和修复问题")

        return recommendations

    def _generate_failure_html(self, analysis: FailedTestAnalysis, title: str) -> str:
        categorized_html = ""
        for category, tests in sorted(analysis.categorized_failures.items()):
            tests_html = ""
            for test in sorted(tests, key=lambda t: t.severity, reverse=True):
                severity_color = {"Critical": "#dc3545", "High": "#fd7e14", "Medium": "#ffc107", "Low": "#28a745"}.get(
                    test.severity, "#667eea"
                )
                tests_html += f"""
                <div class="failure-item">
                    <div class="failure-header">
                        <span class="severity-badge" style="background-color: {severity_color};">{test.severity}</span>
                        <span class="test-name">{test.test_name}</span>
                        <span class="duration">耗时: {test.duration:.2f}s</span>
                    </div>
                    <div class="failure-details">
                        <div class="error-message">
                            <strong>错误信息:</strong><br>
                            <code>{test.error_message}</code>
                        </div>
                        <div class="traceback">
                            <strong>堆栈跟踪:</strong><br>
                            <pre>{test.traceback}</pre>
                        </div>
                        <div class="suggested-fix">
                            <strong>修复建议:</strong><br>
                            <p>{test.suggested_fix}</p>
                        </div>
                    </div>
                </div>"""

            categorized_html += f"""
            <div class="category-section">
                <h3 class="category-title">{category} ({len(tests)}个失败)</h3>
                <div class="failures-list">
                    {tests_html}
                </div>
            </div>"""

        patterns_html = (
            "<li>" + "</li><li>".join(analysis.common_patterns) + "</li>"
            if analysis.common_patterns
            else "<li>无常见模式</li>"
        )
        recommendations_html = (
            "<li>" + "</li><li>".join(analysis.recommendations) + "</li>"
            if analysis.recommendations
            else "<li>无建议</li>"
        )

        severity_chart_data = json.dumps(analysis.severity_distribution)

        return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title}</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {{
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }}
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .content {{
            padding: 40px;
        }}
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }}
        .stat-card {{
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            text-align: center;
            border-left: 4px solid #667eea;
        }}
        .stat-number {{
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }}
        .stat-label {{
            font-size: 14px;
            color: #666;
        }}
        .chart-container {{
            margin: 30px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 10px;
        }}
        .section {{
            margin: 40px 0;
        }}
        .section h2 {{
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }}
        .category-section {{
            margin: 30px 0;
        }}
        .category-title {{
            color: #667eea;
            font-size: 20px;
            margin-bottom: 15px;
        }}
        .failures-list {{
            display: grid;
            gap: 15px;
        }}
        .failure-item {{
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            border-left: 4px solid #dc3545;
        }}
        .failure-header {{
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 15px;
        }}
        .severity-badge {{
            display: inline-block;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            color: white;
        }}
        .test-name {{
            font-weight: bold;
            font-size: 16px;
            color: #333;
        }}
        .duration {{
            margin-left: auto;
            color: #666;
            font-size: 14px;
        }}
        .failure-details {{
            padding-left: 20px;
        }}
        .error-message {{
            margin: 10px 0;
            padding: 10px;
            background-color: #fff;
            border-radius: 4px;
        }}
        .error-message code {{
            color: #dc3545;
            font-family: 'Courier New', monospace;
        }}
        .traceback {{
            margin: 10px 0;
            padding: 10px;
            background-color: #fff;
            border-radius: 4px;
            overflow-x: auto;
        }}
        .traceback pre {{
            margin: 0;
            font-size: 12px;
            color: #666;
            white-space: pre-wrap;
        }}
        .suggested-fix {{
            margin: 10px 0;
            padding: 10px;
            background-color: #d4edda;
            border-radius: 4px;
            border-left: 4px solid #28a745;
        }}
        .suggested-fix p {{
            margin: 5px 0;
            color: #155724;
        }}
        .patterns-list, .recommendations-list {{
            list-style-type: none;
            padding: 0;
        }}
        .patterns-list li, .recommendations-list li {{
            padding: 10px 15px;
            margin: 5px 0;
            background-color: #f8f9fa;
            border-radius: 4px;
            border-left: 4px solid #667eea;
        }}
        .export-buttons {{
            margin: 20px 0;
            text-align: right;
        }}
        .export-btn {{
            display: inline-block;
            padding: 10px 20px;
            margin-left: 10px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }}
        .export-btn:hover {{
            background-color: #5568d3;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{title}</h1>
            <p>生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="content">
            <h2>失败测试概览</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number" style="color: #dc3545;">{analysis.total_failures}</div>
                    <div class="stat-label">总失败数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #dc3545;">{analysis.severity_distribution.get('Critical', 0)}</div>
                    <div class="stat-label">严重级别</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #fd7e14;">{analysis.severity_distribution.get('High', 0)}</div>
                    <div class="stat-label">高级别</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #ffc107;">{analysis.severity_distribution.get('Medium', 0)}</div>
                    <div class="stat-label">中级别</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" style="color: #28a745;">{analysis.severity_distribution.get('Low', 0)}</div>
                    <div class="stat-label">低级别</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{len(analysis.categorized_failures)}</div>
                    <div class="stat-label">错误类型数</div>
                </div>
            </div>
            
            <div class="chart-container">
                <canvas id="severityChart"></canvas>
            </div>
            
            <div class="section">
                <h2>常见失败模式</h2>
                <ul class="patterns-list">
                    {patterns_html}
                </ul>
            </div>
            
            <div class="section">
                <h2>改进建议</h2>
                <ul class="recommendations-list">
                    {recommendations_html}
                </ul>
            </div>
            
            <div class="export-buttons">
                <button class="export-btn" onclick="exportToJSON()">导出JSON</button>
            </div>
            
            <div class="section">
                <h2>失败测试详情</h2>
                {categorized_html}
            </div>
        </div>
    </div>
    
    <script>
        const severityData = {severity_chart_data};
        
        const ctx = document.getElementById('severityChart').getContext('2d');
        new Chart(ctx, {{
            type: 'doughnut',
            data: {{
                labels: Object.keys(severityData),
                datasets: [{{
                    data: Object.values(severityData),
                    backgroundColor: ['#dc3545', '#fd7e14', '#ffc107', '#28a745']
                }}]
            }},
            options: {{
                responsive: true,
                plugins: {{
                    legend: {{ position: 'bottom' }},
                    title: {{ display: true, text: '错误严重程度分布' }}
                }}
            }}
        }});
        
        function exportToJSON() {{
            const data = {json.dumps([{{'test_name': t.test_name, 'error_message': t.error_message, 'traceback': t.traceback, 'duration': t.duration, 'failure_type': t.failure_type, 'category': t.category, 'severity': t.severity, 'suggested_fix': t.suggested_fix}} for t in analysis.failed_tests])};
            const blob = new Blob([JSON.stringify(data, null, 2)], {{ type: 'application/json' }});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'failure_analysis.json';
            a.click();
        }}
    </script>
</body>
</html>"""


if __name__ == "__main__":
    sample_tests = [
        TestResult("test_user_login", "passed", 0.5),
        TestResult("test_user_register", "passed", 0.3),
        TestResult("test_create_order", "failed", 1.2, "AssertionError: Expected 200, got 500", "Traceback..."),
        TestResult("test_payment", "passed", 0.8),
        TestResult("test_search", "skipped", 0.1),
    ]

    html_gen = HTMLReportGenerator()
    report_path = html_gen.generate(sample_tests, "reports/test_report.html")
    print(f"HTML报告已生成: {report_path}")

    perf_metrics = [
        PerformanceMetric("GET /api/users", 45.2, 200, True),
        PerformanceMetric("POST /api/orders", 120.5, 200, True),
        PerformanceMetric("GET /api/products", 38.9, 200, True),
        PerformanceMetric("POST /api/payment", 250.3, 500, False),
    ]

    perf_gen = PerformanceReportGenerator()
    perf_path = perf_gen.generate(perf_metrics)
    print(f"性能报告已生成: {perf_path}")

    coverage_summary = CoverageSummary(
        line_rate=0.75,
        lines_covered=750,
        lines_total=1000,
        files=[
            CoverageData("src/main.py", 0.85, 0.80, 170, 200, 80, 100),
            CoverageData("src/utils.py", 0.65, 0.60, 130, 200, 60, 100),
        ],
    )

    cov_gen = CoverageReportGenerator()
    cov_path = cov_gen.generate(coverage_summary)
    print(f"覆盖率报告已生成: {cov_path}")

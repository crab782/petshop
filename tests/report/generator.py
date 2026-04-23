"""
测试报告生成器
支持HTML测试报告、性能测试报告、覆盖率报告和通知发送
"""
import json
import os
import smtplib
from dataclasses import dataclass, field
from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from pathlib import Path
from typing import Any, Dict, List, Optional
from urllib.request import urlopen
import xml.etree.ElementTree as ET


@dataclass
class TestResult:
    name: str
    status: str
    duration: float
    error: Optional[str] = None
    traceback: Optional[str] = None
    
    @property
    def status_text(self) -> str:
        status_map = {
            'passed': '通过',
            'failed': '失败',
            'skipped': '跳过'
        }
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
        self.template_dir = template_dir or Path(__file__).parent / 'templates'
        self.styles_dir = Path(__file__).parent / 'styles'
    
    def generate(
        self,
        test_results: List[TestResult],
        output_path: str,
        title: str = "测试报告",
        project_name: str = "宠物服务平台"
    ) -> str:
        summary = self._calculate_summary(test_results)
        
        template = self._load_template('html_report_template.html')
        styles = self._load_styles('report.css')
        
        failed_tests = [t for t in test_results if t.status == 'failed']
        slowest_test = max(test_results, key=lambda t: t.duration) if test_results else None
        avg_duration = summary.duration / len(test_results) if test_results else 0
        
        html = template.render(
            title=title,
            styles=styles,
            generated_at=datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            total_duration=f"{summary.duration:.2f}s",
            total_tests=summary.total,
            passed_tests=summary.passed,
            failed_tests=summary.failed,
            skipped_tests=summary.skipped,
            pass_rate=summary.pass_rate,
            failed_test_list=self._format_failed_tests(failed_tests),
            all_tests=self._format_all_tests(test_results),
            slowest_test={
                'name': slowest_test.name if slowest_test else 'N/A',
                'duration': f"{slowest_test.duration:.2f}" if slowest_test else '0'
            },
            avg_duration=f"{avg_duration:.2f}",
            year=datetime.now().year
        )
        
        output_file = Path(output_path)
        output_file.parent.mkdir(parents=True, exist_ok=True)
        output_file.write_text(html, encoding='utf-8')
        
        return str(output_file.absolute())
    
    def _calculate_summary(self, test_results: List[TestResult]) -> TestSummary:
        summary = TestSummary()
        summary.total = len(test_results)
        summary.tests = test_results
        
        for test in test_results:
            summary.duration += test.duration
            if test.status == 'passed':
                summary.passed += 1
            elif test.status == 'failed':
                summary.failed += 1
            elif test.status == 'skipped':
                summary.skipped += 1
        
        return summary
    
    def _load_template(self, template_name: str):
        from jinja2 import Template
        template_path = self.template_dir / template_name
        content = template_path.read_text(encoding='utf-8')
        return Template(content)
    
    def _load_styles(self, style_name: str) -> str:
        style_path = self.styles_dir / style_name
        return style_path.read_text(encoding='utf-8')
    
    def _format_failed_tests(self, tests: List[TestResult]) -> List[Dict]:
        return [
            {
                'name': t.name,
                'duration': f"{t.duration:.2f}",
                'error': t.error or '',
                'traceback': t.traceback or ''
            }
            for t in tests
        ]
    
    def _format_all_tests(self, tests: List[TestResult]) -> List[Dict]:
        return [
            {
                'name': t.name,
                'status': t.status,
                'status_text': t.status_text,
                'duration': f"{t.duration:.2f}",
                'error': t.error or ''
            }
            for t in tests
        ]
    
    def parse_pytest_json(self, json_path: str) -> List[TestResult]:
        with open(json_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        results = []
        for test in data.get('tests', []):
            result = TestResult(
                name=test.get('name', 'Unknown'),
                status=test.get('outcome', 'unknown'),
                duration=test.get('duration', 0.0)
            )
            
            if test.get('outcome') == 'failed':
                call = test.get('call', {})
                crash = call.get('crash', {})
                result.error = crash.get('message', '')
                result.traceback = crash.get('traceback', '')
            
            results.append(result)
        
        return results
    
    def parse_junit_xml(self, xml_path: str) -> List[TestResult]:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        
        results = []
        for testcase in root.iter('testcase'):
            name = testcase.get('name', 'Unknown')
            time = float(testcase.get('time', 0))
            
            failure = testcase.find('failure')
            skipped = testcase.find('skipped')
            
            if failure is not None:
                status = 'failed'
                error = failure.get('message', '')
                traceback = failure.text or ''
            elif skipped is not None:
                status = 'skipped'
                error = None
                traceback = None
            else:
                status = 'passed'
                error = None
                traceback = None
            
            results.append(TestResult(
                name=name,
                status=status,
                duration=time,
                error=error,
                traceback=traceback
            ))
        
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
    def __init__(self, output_dir: str = 'reports/performance'):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
    
    def generate(
        self,
        metrics: List[PerformanceMetric],
        output_path: Optional[str] = None,
        title: str = "性能测试报告"
    ) -> str:
        summary = self._calculate_summary(metrics)
        
        if output_path is None:
            output_path = str(self.output_dir / f"performance_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")
        
        html = self._generate_html(summary, title)
        
        output_file = Path(output_path)
        output_file.write_text(html, encoding='utf-8')
        
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
        
        summary.error_rate = (summary.failed_requests / summary.total_requests * 100) if summary.total_requests > 0 else 0
        
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
    def __init__(self, output_dir: str = 'reports/coverage'):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)
    
    def generate(
        self,
        coverage_data: CoverageSummary,
        output_path: Optional[str] = None,
        title: str = "代码覆盖率报告"
    ) -> str:
        if output_path is None:
            output_path = str(self.output_dir / f"coverage_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html")
        
        html = self._generate_html(coverage_data, title)
        
        output_file = Path(output_path)
        output_file.write_text(html, encoding='utf-8')
        
        return str(output_file.absolute())
    
    def parse_coverage_xml(self, xml_path: str) -> CoverageSummary:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        
        summary = CoverageSummary()
        
        for package in root.iter('package'):
            for cls in package.iter('class'):
                coverage = CoverageData(
                    file_path=cls.get('filename', 'Unknown'),
                    line_rate=float(cls.get('line-rate', 0)),
                    branch_rate=float(cls.get('branch-rate', 0)),
                    lines_covered=0,
                    lines_total=0,
                    branches_covered=0,
                    branches_total=0
                )
                
                lines = cls.find('lines')
                if lines is not None:
                    for line in lines.iter('line'):
                        coverage.lines_total += 1
                        if line.get('hits', '0') != '0':
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
            return '#28a745'
        elif percentage >= 60:
            return '#ffc107'
        elif percentage >= 40:
            return '#fd7e14'
        else:
            return '#dc3545'


@dataclass
class NotificationConfig:
    email_enabled: bool = False
    email_smtp_host: str = ''
    email_smtp_port: int = 587
    email_username: str = ''
    email_password: str = ''
    email_recipients: List[str] = field(default_factory=list)
    
    webhook_enabled: bool = False
    webhook_url: str = ''
    webhook_headers: Dict[str, str] = field(default_factory=dict)


class NotificationSender:
    def __init__(self, config: NotificationConfig):
        self.config = config
        self.template_dir = Path(__file__).parent / 'templates'
    
    def send_test_notification(
        self,
        summary: TestSummary,
        report_url: str,
        project_name: str = "宠物服务平台"
    ) -> Dict[str, bool]:
        results = {}
        
        if self.config.email_enabled:
            results['email'] = self._send_email(summary, report_url, project_name)
        
        if self.config.webhook_enabled:
            results['webhook'] = self._send_webhook(summary, report_url, project_name)
        
        return results
    
    def _send_email(
        self,
        summary: TestSummary,
        report_url: str,
        project_name: str
    ) -> bool:
        try:
            template = self._load_email_template()
            
            status_class = 'success' if summary.failed == 0 else 'failure'
            status_icon = '✅' if summary.failed == 0 else '❌'
            status_message = '所有测试通过' if summary.failed == 0 else f'{summary.failed} 个测试失败'
            
            failed_list = [t for t in summary.tests if t.status == 'failed'][:5]
            failed_tests_html = '\n'.join([f'<li>{t.name}</li>' for t in failed_list])
            
            email_body = template.render(
                project_name=project_name,
                test_date=datetime.now().strftime('%Y-%m-%d'),
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
                year=datetime.now().year
            )
            
            msg = MIMEMultipart('alternative')
            msg['Subject'] = f'测试报告 - {project_name} - {datetime.now().strftime("%Y-%m-%d")}'
            msg['From'] = self.config.email_username
            msg['To'] = ', '.join(self.config.email_recipients)
            
            msg.attach(MIMEText(email_body, 'html', 'utf-8'))
            
            with smtplib.SMTP(self.config.email_smtp_host, self.config.email_smtp_port) as server:
                server.starttls()
                server.login(self.config.email_username, self.config.email_password)
                server.send_message(msg)
            
            return True
        except Exception as e:
            print(f"发送邮件失败: {e}")
            return False
    
    def _send_webhook(
        self,
        summary: TestSummary,
        report_url: str,
        project_name: str
    ) -> bool:
        try:
            import requests
            
            payload = {
                'project_name': project_name,
                'test_date': datetime.now().isoformat(),
                'total_tests': summary.total,
                'passed_tests': summary.passed,
                'failed_tests': summary.failed,
                'skipped_tests': summary.skipped,
                'pass_rate': summary.pass_rate,
                'duration': summary.duration,
                'status': 'success' if summary.failed == 0 else 'failure',
                'report_url': report_url
            }
            
            response = requests.post(
                self.config.webhook_url,
                json=payload,
                headers=self.config.webhook_headers,
                timeout=10
            )
            
            return response.status_code == 200
        except Exception as e:
            print(f"发送Webhook失败: {e}")
            return False
    
    def _load_email_template(self):
        from jinja2 import Template
        template_path = self.template_dir / 'email_template.html'
        content = template_path.read_text(encoding='utf-8')
        return Template(content)


def generate_report_from_pytest(
    json_report_path: str,
    output_path: str = 'reports/test_report.html',
    title: str = '测试报告'
) -> str:
    generator = HTMLReportGenerator()
    test_results = generator.parse_pytest_json(json_report_path)
    return generator.generate(test_results, output_path, title)


def generate_report_from_junit(
    xml_report_path: str,
    output_path: str = 'reports/test_report.html',
    title: str = '测试报告'
) -> str:
    generator = HTMLReportGenerator()
    test_results = generator.parse_junit_xml(xml_report_path)
    return generator.generate(test_results, output_path, title)


if __name__ == '__main__':
    sample_tests = [
        TestResult('test_user_login', 'passed', 0.5),
        TestResult('test_user_register', 'passed', 0.3),
        TestResult('test_create_order', 'failed', 1.2, 'AssertionError: Expected 200, got 500', 'Traceback...'),
        TestResult('test_payment', 'passed', 0.8),
        TestResult('test_search', 'skipped', 0.1),
    ]
    
    html_gen = HTMLReportGenerator()
    report_path = html_gen.generate(sample_tests, 'reports/test_report.html')
    print(f"HTML报告已生成: {report_path}")
    
    perf_metrics = [
        PerformanceMetric('GET /api/users', 45.2, 200, True),
        PerformanceMetric('POST /api/orders', 120.5, 200, True),
        PerformanceMetric('GET /api/products', 38.9, 200, True),
        PerformanceMetric('POST /api/payment', 250.3, 500, False),
    ]
    
    perf_gen = PerformanceReportGenerator()
    perf_path = perf_gen.generate(perf_metrics)
    print(f"性能报告已生成: {perf_path}")
    
    coverage_summary = CoverageSummary(
        line_rate=0.75,
        lines_covered=750,
        lines_total=1000,
        files=[
            CoverageData('src/main.py', 0.85, 0.80, 170, 200, 80, 100),
            CoverageData('src/utils.py', 0.65, 0.60, 130, 200, 60, 100),
        ]
    )
    
    cov_gen = CoverageReportGenerator()
    cov_path = cov_gen.generate(coverage_summary)
    print(f"覆盖率报告已生成: {cov_path}")

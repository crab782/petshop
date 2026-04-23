"""
测试报告生成器使用示例
演示如何使用各种报告生成器功能
"""
from datetime import datetime
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from tests.report import (
    HTMLReportGenerator,
    PerformanceReportGenerator,
    CoverageReportGenerator,
    NotificationSender,
)
from tests.report.generator import (
    TestResult,
    PerformanceMetric,
    CoverageSummary,
    CoverageData,
    NotificationConfig,
    TestSummary,
)


def example_html_report():
    html_gen = HTMLReportGenerator()
    
    test_results = [
        TestResult(
            name='test_user_login_success',
            status='passed',
            duration=0.45
        ),
        TestResult(
            name='test_user_login_invalid_password',
            status='passed',
            duration=0.32
        ),
        TestResult(
            name='test_user_register',
            status='failed',
            duration=1.23,
            error='AssertionError: Expected status 200, got 400',
            traceback='File "test_auth.py", line 45, in test_user_register\n    assert response.status_code == 200'
        ),
        TestResult(
            name='test_create_order',
            status='passed',
            duration=0.89
        ),
        TestResult(
            name='test_payment_integration',
            status='skipped',
            duration=0.01
        ),
        TestResult(
            name='test_product_search',
            status='passed',
            duration=0.67
        ),
        TestResult(
            name='test_merchant_dashboard',
            status='failed',
            duration=2.15,
            error='TimeoutError: Request timed out after 30s',
            traceback='File "test_merchant.py", line 78, in test_merchant_dashboard\n    response = client.get("/api/merchant/dashboard")'
        ),
    ]
    
    output_path = 'reports/test_report_example.html'
    report_file = html_gen.generate(
        test_results,
        output_path,
        title="宠物服务平台 - 测试报告",
        project_name="宠物服务平台"
    )
    
    print(f"✅ HTML测试报告已生成: {report_file}")
    return report_file


def example_parse_pytest_json():
    html_gen = HTMLReportGenerator()
    
    pytest_json = {
        "tests": [
            {
                "name": "test_api_user_login",
                "outcome": "passed",
                "duration": 0.56
            },
            {
                "name": "test_api_user_register",
                "outcome": "failed",
                "duration": 1.23,
                "call": {
                    "crash": {
                        "message": "AssertionError: Email validation failed",
                        "traceback": "Traceback (most recent call last):\n  File..."
                    }
                }
            },
            {
                "name": "test_api_create_order",
                "outcome": "passed",
                "duration": 0.78
            }
        ]
    }
    
    import json
    json_path = 'reports/pytest_report.json'
    Path(json_path).parent.mkdir(parents=True, exist_ok=True)
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(pytest_json, f, indent=2)
    
    test_results = html_gen.parse_pytest_json(json_path)
    report_file = html_gen.generate(test_results, 'reports/pytest_html_report.html')
    
    print(f"✅ 从pytest JSON生成的报告: {report_file}")
    return report_file


def example_performance_report():
    perf_gen = PerformanceReportGenerator()
    
    metrics = [
        PerformanceMetric('GET /api/users', 45.2, 200, True),
        PerformanceMetric('POST /api/users/login', 78.5, 200, True),
        PerformanceMetric('GET /api/products', 32.9, 200, True),
        PerformanceMetric('POST /api/orders', 156.3, 200, True),
        PerformanceMetric('GET /api/merchants', 41.7, 200, True),
        PerformanceMetric('POST /api/payment', 289.5, 500, False),
        PerformanceMetric('GET /api/reviews', 38.2, 200, True),
        PerformanceMetric('PUT /api/user/profile', 95.4, 200, True),
        PerformanceMetric('DELETE /api/cart/item', 67.8, 200, True),
        PerformanceMetric('GET /api/search', 123.6, 200, True),
    ]
    
    report_file = perf_gen.generate(
        metrics,
        title="宠物服务平台 - 性能测试报告"
    )
    
    print(f"✅ 性能测试报告已生成: {report_file}")
    return report_file


def example_coverage_report():
    cov_gen = CoverageReportGenerator()
    
    coverage_summary = CoverageSummary(
        line_rate=0.78,
        branch_rate=0.72,
        lines_covered=2340,
        lines_total=3000,
        branches_covered=720,
        branches_total=1000,
        files=[
            CoverageData(
                file_path='src/main/java/com/petshop/controller/UserController.java',
                line_rate=0.92,
                branch_rate=0.88,
                lines_covered=230,
                lines_total=250,
                branches_covered=88,
                branches_total=100
            ),
            CoverageData(
                file_path='src/main/java/com/petshop/controller/MerchantController.java',
                line_rate=0.85,
                branch_rate=0.80,
                lines_covered=170,
                lines_total=200,
                branches_covered=80,
                branches_total=100
            ),
            CoverageData(
                file_path='src/main/java/com/petshop/service/UserService.java',
                line_rate=0.75,
                branch_rate=0.70,
                lines_covered=150,
                lines_total=200,
                branches_covered=70,
                branches_total=100
            ),
            CoverageData(
                file_path='src/main/java/com/petshop/service/MerchantService.java',
                line_rate=0.68,
                branch_rate=0.62,
                lines_covered=136,
                lines_total=200,
                branches_covered=62,
                branches_total=100
            ),
            CoverageData(
                file_path='petshop-vue/src/api/user.ts',
                line_rate=0.55,
                branch_rate=0.50,
                lines_covered=110,
                lines_total=200,
                branches_covered=50,
                branches_total=100
            ),
        ]
    )
    
    report_file = cov_gen.generate(
        coverage_summary,
        title="宠物服务平台 - 代码覆盖率报告"
    )
    
    print(f"✅ 覆盖率报告已生成: {report_file}")
    return report_file


def example_notification():
    config = NotificationConfig(
        email_enabled=False,
        webhook_enabled=False
    )
    
    sender = NotificationSender(config)
    
    test_results = [
        TestResult('test_user_login', 'passed', 0.5),
        TestResult('test_user_register', 'passed', 0.3),
        TestResult('test_create_order', 'failed', 1.2, 'AssertionError: Expected 200, got 500'),
        TestResult('test_payment', 'passed', 0.8),
    ]
    
    summary = TestSummary(
        total=len(test_results),
        passed=sum(1 for t in test_results if t.status == 'passed'),
        failed=sum(1 for t in test_results if t.status == 'failed'),
        skipped=sum(1 for t in test_results if t.status == 'skipped'),
        duration=sum(t.duration for t in test_results),
        tests=test_results
    )
    
    print("✅ 通知发送器已配置（示例中未启用实际发送）")
    print(f"   - 总测试数: {summary.total}")
    print(f"   - 通过: {summary.passed}")
    print(f"   - 失败: {summary.failed}")
    print(f"   - 通过率: {summary.pass_rate}%")
    
    return summary


def main():
    print("=" * 60)
    print("宠物服务平台 - 测试报告生成器示例")
    print("=" * 60)
    print()
    
    print("1. 生成HTML测试报告...")
    html_report = example_html_report()
    print()
    
    print("2. 从pytest JSON生成报告...")
    pytest_report = example_parse_pytest_json()
    print()
    
    print("3. 生成性能测试报告...")
    perf_report = example_performance_report()
    print()
    
    print("4. 生成覆盖率报告...")
    cov_report = example_coverage_report()
    print()
    
    print("5. 配置通知发送器...")
    notification = example_notification()
    print()
    
    print("=" * 60)
    print("所有报告生成完成！")
    print("=" * 60)
    print()
    print("生成的报告文件:")
    print(f"  - HTML测试报告: {html_report}")
    print(f"  - Pytest报告: {pytest_report}")
    print(f"  - 性能报告: {perf_report}")
    print(f"  - 覆盖率报告: {cov_report}")
    print()
    print("提示: 在浏览器中打开HTML文件查看完整报告")


if __name__ == '__main__':
    main()

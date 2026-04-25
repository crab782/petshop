"""
CI/CD集成示例
展示如何在CI/CD流程中集成自动修复机制
"""

import json
import os
import subprocess
import sys
from datetime import datetime
from pathlib import Path


def run_with_auto_fix():
    """在CI/CD中运行测试并自动修复"""
    print("\n" + "=" * 80)
    print("CI/CD 自动修复测试流程")
    print("=" * 80)

    from tests.auto_fix import AutoFixRunner
    from tests.auto_fix_config import RetryConfig

    retry_config = RetryConfig(
        max_retries=int(os.getenv("MAX_RETRIES", "3")),
        retry_delay=int(os.getenv("RETRY_DELAY", "5")),
        exponential_backoff=True,
        max_delay=int(os.getenv("MAX_DELAY", "60")),
    )

    runner = AutoFixRunner(retry_config=retry_config)

    test_path = os.getenv("TEST_PATH", "tests/")
    pytest_args = os.getenv("PYTEST_ARGS", "").split() if os.getenv("PYTEST_ARGS") else []

    result = runner.run_with_retry(test_path=test_path, extra_args=pytest_args)

    report_file = Path("tests/report/auto_fix_report.json")
    if report_file.exists():
        with open(report_file, "r", encoding="utf-8") as f:
            report_data = json.load(f)

        print("\n" + "=" * 80)
        print("测试摘要")
        print("=" * 80)
        print(f"总失败数: {report_data['total_failures']}")
        print(f"已修复: {report_data['summary']['fixed_count']}")
        print(f"未修复: {report_data['summary']['unfixed_count']}")
        print(f"错误类型分布: {json.dumps(report_data['summary']['error_types'], indent=2)}")

    return result["success"]


def generate_ci_report():
    """生成CI/CD报告"""
    report_file = Path("tests/report/auto_fix_report.json")

    if not report_file.exists():
        print("⚠️  未找到测试报告")
        return

    with open(report_file, "r", encoding="utf-8") as f:
        report_data = json.load(f)

    ci_report = {
        "timestamp": datetime.now().isoformat(),
        "status": "success" if report_data["summary"]["unfixed_count"] == 0 else "failure",
        "summary": {
            "total_failures": report_data["total_failures"],
            "fixed": report_data["summary"]["fixed_count"],
            "unfixed": report_data["summary"]["unfixed_count"],
            "error_types": report_data["summary"]["error_types"],
        },
        "failures": [
            {
                "test_name": f["test_name"],
                "error_type": f["error_type"],
                "fixed": f["fixed"],
                "retry_count": f["retry_count"],
            }
            for f in report_data["failures"]
        ],
    }

    ci_report_file = Path("tests/report/ci_report.json")
    with open(ci_report_file, "w", encoding="utf-8") as f:
        json.dump(ci_report, f, ensure_ascii=False, indent=2)

    print(f"\n✅ CI报告已生成: {ci_report_file}")

    return ci_report


def check_critical_failures():
    """检查是否有严重失败"""
    report_file = Path("tests/report/auto_fix_report.json")

    if not report_file.exists():
        return False

    with open(report_file, "r", encoding="utf-8") as f:
        report_data = json.load(f)

    critical_error_types = ["auth_error", "database_error", "network_error"]

    critical_failures = [
        f for f in report_data["failures"] if f["error_type"] in critical_error_types and not f["fixed"]
    ]

    if critical_failures:
        print("\n⚠️  发现严重失败:")
        for failure in critical_failures:
            print(f"  - {failure['test_name']}: {failure['error_type']}")
        return True

    return False


def send_notification(success: bool, ci_report: dict = None):
    """发送通知（示例）"""
    webhook_url = os.getenv("NOTIFICATION_WEBHOOK")

    if not webhook_url:
        print("\n⚠️  未配置通知Webhook")
        return

    status_emoji = "✅" if success else "❌"
    status_text = "成功" if success else "失败"

    message = {
        "text": f"{status_emoji} 测试自动修复{status_text}",
        "attachments": [
            {
                "title": "测试摘要",
                "fields": [
                    {
                        "title": "总失败数",
                        "value": str(ci_report["summary"]["total_failures"]) if ci_report else "N/A",
                        "short": True,
                    },
                    {
                        "title": "已修复",
                        "value": str(ci_report["summary"]["fixed"]) if ci_report else "N/A",
                        "short": True,
                    },
                    {
                        "title": "未修复",
                        "value": str(ci_report["summary"]["unfixed"]) if ci_report else "N/A",
                        "short": True,
                    },
                ],
                "color": "good" if success else "danger",
            }
        ],
    }

    print(f"\n📧 通知消息: {message['text']}")


def main():
    """主函数"""
    print("\n" + "=" * 80)
    print("开始CI/CD自动修复流程")
    print(f"时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 80)

    try:
        success = run_with_auto_fix()

        ci_report = generate_ci_report()

        has_critical = check_critical_failures()

        send_notification(success and not has_critical, ci_report)

        if success:
            print("\n✅ 所有测试已成功修复！")
            exit_code = 0
        elif has_critical:
            print("\n❌ 存在严重失败，需要人工干预")
            exit_code = 2
        else:
            print("\n⚠️  部分测试修复失败")
            exit_code = 1

        print("\n" + "=" * 80)
        print(f"CI/CD流程完成，退出码: {exit_code}")
        print("=" * 80)

        sys.exit(exit_code)

    except Exception as e:
        print(f"\n❌ CI/CD流程执行失败: {str(e)}")
        import traceback

        traceback.print_exc()
        sys.exit(3)


if __name__ == "__main__":
    main()

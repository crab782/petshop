"""
CI/CD 配置验证脚本

验证所有 CI/CD 配置文件是否正确创建和配置。
"""

import os
from pathlib import Path


def check_file_exists(file_path: str, description: str) -> bool:
    """检查文件是否存在"""
    path = Path(file_path)
    if path.exists():
        print(f"✅ {description}: {file_path}")
        return True
    else:
        print(f"❌ {description}: {file_path} (不存在)")
        return False


def check_file_content(file_path: str, keywords: list, description: str) -> bool:
    """检查文件内容是否包含关键词"""
    path = Path(file_path)
    if not path.exists():
        print(f"❌ {description}: 文件不存在")
        return False
    
    try:
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        missing_keywords = []
        for keyword in keywords:
            if keyword not in content:
                missing_keywords.append(keyword)
        
        if missing_keywords:
            print(f"⚠️  {description}: 缺少关键词 {missing_keywords}")
            return False
        else:
            print(f"✅ {description}: 内容验证通过")
            return True
    except Exception as e:
        print(f"❌ {description}: 读取文件失败 - {e}")
        return False


def main():
    """主验证函数"""
    print("=" * 60)
    print("CI/CD 配置验证")
    print("=" * 60)
    print()
    
    results = []
    
    print("1. 检查 GitHub Actions 工作流文件")
    print("-" * 60)
    
    workflow_files = [
        (".github/workflows/test.yml", "Python 测试工作流", ["python-version", "mysql", "redis", "pytest"]),
        (".github/workflows/deploy-reports.yml", "报告部署工作流", ["github-pages", "allure"]),
        (".github/workflows/notify-failure.yml", "失败通知工作流", ["slack", "email", "issue"]),
    ]
    
    for file_path, desc, keywords in workflow_files:
        exists = check_file_exists(file_path, desc)
        if exists:
            content_ok = check_file_content(file_path, keywords, f"  内容验证")
            results.append(exists and content_ok)
        else:
            results.append(False)
        print()
    
    print("2. 检查测试配置文件")
    print("-" * 60)
    
    config_files = [
        (".env.test", "测试环境配置", ["DB_HOST", "REDIS_HOST", "API_BASE_URL"]),
        ("pytest.ini", "Pytest 配置", ["testpaths", "addopts", "markers"]),
        (".coveragerc", "覆盖率配置", ["[run]", "[report]", "[html]"]),
    ]
    
    for file_path, desc, keywords in config_files:
        exists = check_file_exists(file_path, desc)
        if exists:
            content_ok = check_file_content(file_path, keywords, f"  内容验证")
            results.append(exists and content_ok)
        else:
            results.append(False)
        print()
    
    print("3. 检查测试脚本")
    print("-" * 60)
    
    script_files = [
        ("tests/scripts/init_test_db.py", "数据库初始化脚本", ["create_tables", "insert_test_data", "clean_test_data"]),
    ]
    
    for file_path, desc, keywords in script_files:
        exists = check_file_exists(file_path, desc)
        if exists:
            content_ok = check_file_content(file_path, keywords, f"  内容验证")
            results.append(exists and content_ok)
        else:
            results.append(False)
        print()
    
    print("4. 检查文档文件")
    print("-" * 60)
    
    doc_files = [
        ("docs/github-secrets-setup.md", "GitHub Secrets 配置指南", ["SMTP_SERVER", "SLACK_WEBHOOK"]),
        ("docs/cicd-configuration-report.md", "CI/CD 配置报告", ["工作流", "测试报告", "通知"]),
    ]
    
    for file_path, desc, keywords in doc_files:
        exists = check_file_exists(file_path, desc)
        if exists:
            content_ok = check_file_content(file_path, keywords, f"  内容验证")
            results.append(exists and content_ok)
        else:
            results.append(False)
        print()
    
    print("=" * 60)
    print("验证结果汇总")
    print("=" * 60)
    
    total = len(results)
    passed = sum(results)
    failed = total - passed
    
    print(f"总计: {total} 项")
    print(f"通过: {passed} 项 ✅")
    print(f"失败: {failed} 项 ❌")
    print()
    
    if failed == 0:
        print("🎉 所有 CI/CD 配置验证通过！")
        return 0
    else:
        print("⚠️  部分 CI/CD 配置验证失败，请检查上述错误信息。")
        return 1


if __name__ == "__main__":
    exit(main())

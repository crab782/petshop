# 自动修复和重试机制

## 概述

自动修复和重试机制是一个智能的测试失败处理系统，能够自动检测测试失败、生成修复建议、自动重试失败的测试，并生成详细的错误报告。

## 主要功能

### 1. 测试失败检测
- **FailedTestDetector类**：解析pytest输出，提取失败信息
- 自动分类失败原因（API错误、数据错误、权限错误等）
- 提取详细的堆栈跟踪信息

### 2. 错误日志记录
- **ErrorLogger类**：记录详细的错误日志
- 保存请求和响应数据
- 生成结构化的错误报告

### 3. 自动修复建议生成
- **FixSuggestionGenerator类**：根据错误类型生成修复建议
- 针对不同错误类型提供具体的修复步骤
- 支持自定义修复建议模板

### 4. 自动重试机制
- **AutoFixRunner类**：自动重试失败的测试
- 支持指数退避策略
- 可配置重试次数和延迟时间
- 记录每次重试的结果

### 5. CI/CD集成
- 提供命令行接口
- 支持在CI/CD流程中调用
- 返回适当的退出码
- 生成CI报告

## 快速开始

### 命令行使用

```bash
# 基本使用
python tests/auto_fix.py

# 指定测试路径
python tests/auto_fix.py --test-path tests/test_auth.py

# 自定义重试配置
python tests/auto_fix.py --max-retries 5 --retry-delay 10

# 禁用指数退避
python tests/auto_fix.py --no-exponential-backoff

# 传递额外的pytest参数
python tests/auto_fix.py --pytest-args -k "test_login" -v
```

### Python API使用

```python
from tests.auto_fix import AutoFixRunner
from tests.auto_fix_config import RetryConfig

# 创建重试配置
retry_config = RetryConfig(
    max_retries=3,
    retry_delay=5,
    exponential_backoff=True,
    max_delay=60
)

# 创建运行器
runner = AutoFixRunner(retry_config=retry_config)

# 运行测试并自动修复
result = runner.run_with_retry(test_path="tests/")

# 检查结果
if result["success"]:
    print("✅ 所有测试已成功修复！")
else:
    print("❌ 部分测试修复失败")
    print(f"失败详情: {result['summary']}")
```

## 配置说明

### 环境变量

```bash
# 重试配置
MAX_RETRIES=3              # 最大重试次数
RETRY_DELAY=5             # 重试延迟（秒）
EXPONENTIAL_BACKOFF=true  # 是否启用指数退避
MAX_DELAY=60              # 最大延迟时间（秒）

# 测试配置
TEST_PATH=tests/          # 测试路径
PYTEST_ARGS=              # 额外的pytest参数

# 日志配置
AUTO_FIX_LOG_DIR=tests/logs           # 日志目录
AUTO_FIX_REPORT_DIR=tests/report      # 报告目录

# 通知配置
NOTIFICATION_WEBHOOK=                 # 通知Webhook URL
```

### 错误类型映射

系统支持以下错误类型：

| 错误类型 | 描述 | 是否自动重试 |
|---------|------|------------|
| `network_error` | 网络连接错误 | ✅ |
| `timeout_error` | 请求超时 | ✅ |
| `api_error` | API服务错误 | ✅ |
| `auth_error` | 认证错误 | ❌ |
| `permission_error` | 权限错误 | ❌ |
| `validation_error` | 参数验证错误 | ❌ |
| `database_error` | 数据库错误 | ❌ |
| `data_error` | 数据断言错误 | ❌ |

## 输出文件

### 1. 错误日志 (`tests/logs/errors.log`)
记录所有失败的测试详情，包括：
- 测试名称
- 错误类型
- 错误信息
- 堆栈跟踪
- 请求和响应数据

### 2. 重试日志 (`tests/logs/retries.log`)
记录所有重试操作，包括：
- 重试次数
- 重试结果
- 执行时间

### 3. JSON报告 (`tests/report/auto_fix_report.json`)
结构化的错误报告，包含：
- 总失败数
- 已修复数量
- 未修复数量
- 错误类型分布
- 详细失败列表

### 4. HTML报告 (`tests/report/auto_fix_report.html`)
可视化的HTML报告，包含：
- 测试摘要
- 失败详情
- 修复建议
- 重试历史

## CI/CD集成

### GitHub Actions

项目已包含GitHub Actions工作流配置文件 `.github/workflows/auto_fix_tests.yml`，支持：

- 自动运行测试
- 自动修复和重试
- 上传测试报告
- 发送失败通知

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        MAX_RETRIES = '3'
        RETRY_DELAY = '5'
    }
    
    stages {
        stage('Run Auto Fix Tests') {
            steps {
                sh 'python tests/ci_integration.py'
            }
        }
        
        stage('Publish Reports') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'tests/report',
                    reportFiles: 'auto_fix_report.html',
                    reportName: 'Auto Fix Report'
                ])
            }
        }
    }
    
    post {
        failure {
            echo 'Tests failed after auto-fix attempts'
        }
    }
}
```

### GitLab CI

```yaml
auto_fix_tests:
  stage: test
  script:
    - python tests/ci_integration.py
  artifacts:
    when: always
    paths:
      - tests/report/
      - tests/logs/
    expire_in: 1 week
  variables:
    MAX_RETRIES: "3"
    RETRY_DELAY: "5"
```

## 最佳实践

### 1. 合理设置重试次数
- 对于网络错误：建议3-5次
- 对于超时错误：建议2-3次
- 对于其他错误：建议不重试

### 2. 使用指数退避
启用指数退避可以避免在短时间内频繁重试，给系统恢复时间。

### 3. 监控重试日志
定期查看重试日志，了解哪些测试经常失败，找出根本原因。

### 4. 及时修复非重试错误
对于认证、权限等无法自动重试的错误，应及时人工修复。

### 5. 集成到CI/CD
将自动修复机制集成到CI/CD流程中，提高测试稳定性。

## 故障排查

### 问题1：测试一直重试失败
**原因**：可能是后端服务未启动或配置错误
**解决**：
1. 检查后端服务状态
2. 验证API_BASE_URL配置
3. 查看错误日志了解详情

### 问题2：无法解析pytest输出
**原因**：pytest输出格式可能不标准
**解决**：
1. 确保使用 `-v` 参数
2. 检查pytest版本兼容性
3. 查看原始输出进行调试

### 问题3：重试延迟过长
**原因**：指数退避导致延迟时间过长
**解决**：
1. 调整MAX_DELAY配置
2. 禁用指数退避
3. 减少重试次数

## 扩展开发

### 添加新的错误类型

在 `auto_fix_config.py` 中添加：

```python
ErrorPattern(
    pattern=r"Your custom error pattern",
    error_type=ErrorType.CUSTOM_ERROR,
    description="自定义错误描述",
    fix_suggestions=[
        "修复建议1",
        "修复建议2"
    ]
)
```

### 自定义修复建议

```python
FixSuggestion(
    error_type=ErrorType.CUSTOM_ERROR,
    title="自定义错误",
    description="错误描述",
    actions=[
        "操作步骤1",
        "操作步骤2"
    ],
    priority=1,
    auto_fixable=False
)
```

## 示例

查看 `tests/auto_fix_example.py` 获取更多使用示例。

## 许可证

本项目采用 MIT 许可证。

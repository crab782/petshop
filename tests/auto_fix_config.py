"""
自动修复配置文件
"""

import os
from dataclasses import dataclass, field
from enum import Enum
from typing import Any, Dict, List


class ErrorType(Enum):
    API_ERROR = "api_error"
    DATA_ERROR = "data_error"
    PERMISSION_ERROR = "permission_error"
    AUTH_ERROR = "auth_error"
    TIMEOUT_ERROR = "timeout_error"
    NETWORK_ERROR = "network_error"
    VALIDATION_ERROR = "validation_error"
    DATABASE_ERROR = "database_error"
    UNKNOWN_ERROR = "unknown_error"


class FixAction(Enum):
    RETRY = "retry"
    SKIP = "skip"
    FIX_API = "fix_api"
    FIX_DATA = "fix_data"
    FIX_PERMISSION = "fix_permission"
    FIX_AUTH = "fix_auth"
    FIX_TIMEOUT = "fix_timeout"
    FIX_NETWORK = "fix_network"
    FIX_VALIDATION = "fix_validation"
    FIX_DATABASE = "fix_database"
    MANUAL_FIX = "manual_fix"


@dataclass
class RetryConfig:
    max_retries: int = 3
    retry_delay: int = 5
    retry_on_errors: List[ErrorType] = field(
        default_factory=lambda: [ErrorType.NETWORK_ERROR, ErrorType.TIMEOUT_ERROR, ErrorType.API_ERROR]
    )
    exponential_backoff: bool = True
    max_delay: int = 60


@dataclass
class ErrorPattern:
    pattern: str
    error_type: ErrorType
    description: str
    fix_suggestions: List[str]


@dataclass
class FixSuggestion:
    error_type: ErrorType
    title: str
    description: str
    actions: List[str]
    priority: int = 1
    auto_fixable: bool = False


class AutoFixConfig:
    ERROR_PATTERNS: List[ErrorPattern] = [
        ErrorPattern(
            pattern=r"Connection refused|ConnectionError|Max retries exceeded",
            error_type=ErrorType.NETWORK_ERROR,
            description="网络连接错误",
            fix_suggestions=[
                "检查后端服务是否启动",
                "检查网络连接是否正常",
                "检查防火墙设置",
                "验证API地址配置是否正确",
            ],
        ),
        ErrorPattern(
            pattern=r"timeout|Timeout|timed out",
            error_type=ErrorType.TIMEOUT_ERROR,
            description="请求超时",
            fix_suggestions=["增加请求超时时间", "检查后端服务性能", "优化数据库查询", "检查网络延迟"],
        ),
        ErrorPattern(
            pattern=r"401|Unauthorized|authentication failed|Invalid token",
            error_type=ErrorType.AUTH_ERROR,
            description="认证错误",
            fix_suggestions=["检查用户名和密码是否正确", "检查Token是否过期", "验证认证配置", "重新登录获取新Token"],
        ),
        ErrorPattern(
            pattern=r"403|Forbidden|Permission denied|Access denied",
            error_type=ErrorType.PERMISSION_ERROR,
            description="权限错误",
            fix_suggestions=["检查用户权限配置", "验证角色权限设置", "检查资源访问权限", "确认用户角色是否正确"],
        ),
        ErrorPattern(
            pattern=r"404|Not Found|Resource not found",
            error_type=ErrorType.API_ERROR,
            description="API资源未找到",
            fix_suggestions=["检查API路径是否正确", "验证资源ID是否存在", "检查API版本", "确认资源是否已被删除"],
        ),
        ErrorPattern(
            pattern=r"400|Bad Request|Validation error|Invalid.*parameter",
            error_type=ErrorType.VALIDATION_ERROR,
            description="参数验证错误",
            fix_suggestions=["检查请求参数格式", "验证必填参数是否缺失", "检查参数类型是否正确", "验证参数值范围"],
        ),
        ErrorPattern(
            pattern=r"500|Internal Server Error|Database error|SQLException",
            error_type=ErrorType.DATABASE_ERROR,
            description="数据库错误",
            fix_suggestions=["检查数据库连接", "验证数据库配置", "检查SQL语句", "查看数据库日志"],
        ),
        ErrorPattern(
            pattern=r"AssertionError|assert.*failed|Expected.*but got",
            error_type=ErrorType.DATA_ERROR,
            description="数据断言错误",
            fix_suggestions=["检查测试数据是否正确", "验证预期结果", "检查数据是否被其他测试修改", "确认数据库状态"],
        ),
        ErrorPattern(
            pattern=r"500|502|503|504|Service Unavailable",
            error_type=ErrorType.API_ERROR,
            description="API服务错误",
            fix_suggestions=["检查后端服务状态", "查看后端错误日志", "检查服务资源使用情况", "重启后端服务"],
        ),
    ]

    FIX_SUGGESTIONS: Dict[ErrorType, FixSuggestion] = {
        ErrorType.NETWORK_ERROR: FixSuggestion(
            error_type=ErrorType.NETWORK_ERROR,
            title="网络连接错误",
            description="无法连接到API服务器",
            actions=[
                "1. 检查后端服务是否正在运行",
                "2. 验证API_BASE_URL配置: {api_base_url}",
                "3. 检查防火墙设置",
                "4. 尝试ping服务器地址",
            ],
            priority=1,
            auto_fixable=False,
        ),
        ErrorType.TIMEOUT_ERROR: FixSuggestion(
            error_type=ErrorType.TIMEOUT_ERROR,
            title="请求超时",
            description="API请求超时",
            actions=[
                "1. 增加TIMEOUT配置值（当前: {timeout}秒）",
                "2. 检查后端服务性能",
                "3. 优化慢查询",
                "4. 检查网络延迟",
            ],
            priority=2,
            auto_fixable=False,
        ),
        ErrorType.AUTH_ERROR: FixSuggestion(
            error_type=ErrorType.AUTH_ERROR,
            title="认证失败",
            description="用户认证失败",
            actions=[
                "1. 检查测试用户配置:",
                "   - 用户名: {test_username}",
                "   - 密码: {test_password}",
                "2. 验证用户是否存在于数据库",
                "3. 检查JWT配置",
                "4. 尝试手动登录验证",
            ],
            priority=1,
            auto_fixable=False,
        ),
        ErrorType.PERMISSION_ERROR: FixSuggestion(
            error_type=ErrorType.PERMISSION_ERROR,
            title="权限不足",
            description="用户权限不足",
            actions=["1. 检查用户角色配置", "2. 验证权限设置", "3. 检查资源访问控制", "4. 确认测试用户权限"],
            priority=2,
            auto_fixable=False,
        ),
        ErrorType.API_ERROR: FixSuggestion(
            error_type=ErrorType.API_ERROR,
            title="API错误",
            description="API返回错误响应",
            actions=["1. 检查API路径是否正确", "2. 验证请求方法", "3. 检查请求参数", "4. 查看后端日志"],
            priority=2,
            auto_fixable=False,
        ),
        ErrorType.VALIDATION_ERROR: FixSuggestion(
            error_type=ErrorType.VALIDATION_ERROR,
            title="参数验证失败",
            description="请求参数验证失败",
            actions=["1. 检查必填参数", "2. 验证参数格式", "3. 检查参数类型", "4. 验证参数值范围"],
            priority=3,
            auto_fixable=False,
        ),
        ErrorType.DATABASE_ERROR: FixSuggestion(
            error_type=ErrorType.DATABASE_ERROR,
            title="数据库错误",
            description="数据库操作失败",
            actions=[
                "1. 检查数据库连接配置:",
                "   - 主机: {db_host}",
                "   - 端口: {db_port}",
                "   - 数据库: {db_name}",
                "2. 验证数据库服务是否运行",
                "3. 检查数据库用户权限",
                "4. 查看数据库错误日志",
            ],
            priority=1,
            auto_fixable=False,
        ),
        ErrorType.DATA_ERROR: FixSuggestion(
            error_type=ErrorType.DATA_ERROR,
            title="数据断言失败",
            description="测试数据与预期不符",
            actions=["1. 检查测试数据准备", "2. 验证预期结果", "3. 检查数据隔离", "4. 确认数据库状态"],
            priority=3,
            auto_fixable=False,
        ),
        ErrorType.UNKNOWN_ERROR: FixSuggestion(
            error_type=ErrorType.UNKNOWN_ERROR,
            title="未知错误",
            description="无法识别的错误类型",
            actions=["1. 查看详细错误日志", "2. 检查堆栈跟踪", "3. 查看请求和响应数据", "4. 联系开发团队"],
            priority=4,
            auto_fixable=False,
        ),
    }

    LOG_CONFIG = {
        "log_dir": os.getenv("AUTO_FIX_LOG_DIR", "tests/logs"),
        "error_log_file": "errors.log",
        "retry_log_file": "retries.log",
        "report_file": "auto_fix_report.json",
        "max_log_size": 10 * 1024 * 1024,
        "backup_count": 5,
    }

    REPORT_CONFIG = {
        "report_dir": os.getenv("AUTO_FIX_REPORT_DIR", "tests/report"),
        "html_report": "auto_fix_report.html",
        "json_report": "auto_fix_report.json",
    }

    @classmethod
    def get_retry_config(cls) -> RetryConfig:
        return RetryConfig(
            max_retries=int(os.getenv("MAX_RETRIES", "3")),
            retry_delay=int(os.getenv("RETRY_DELAY", "5")),
            exponential_backoff=os.getenv("EXPONENTIAL_BACKOFF", "true").lower() == "true",
            max_delay=int(os.getenv("MAX_DELAY", "60")),
        )

    @classmethod
    def get_error_type_from_message(cls, error_message: str) -> ErrorType:
        import re

        for pattern in cls.ERROR_PATTERNS:
            if re.search(pattern.pattern, error_message, re.IGNORECASE):
                return pattern.error_type
        return ErrorType.UNKNOWN_ERROR

    @classmethod
    def get_fix_suggestion(cls, error_type: ErrorType) -> FixSuggestion:
        return cls.FIX_SUGGESTIONS.get(error_type, cls.FIX_SUGGESTIONS[ErrorType.UNKNOWN_ERROR])

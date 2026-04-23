"""
性能测试配置文件
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config import Config


class PerformanceConfig:
    API_BASE_URL = os.getenv("API_BASE_URL", Config.API_BASE_URL)
    
    TEST_USERS = {
        "user": {
            "loginIdentifier": os.getenv("PERF_USER_PHONE", Config.TEST_USER["phone"]),
            "password": os.getenv("PERF_USER_PASSWORD", Config.TEST_USER["password"]),
        },
        "merchant": {
            "loginIdentifier": os.getenv("PERF_MERCHANT_PHONE", Config.TEST_MERCHANT["phone"]),
            "password": os.getenv("PERF_MERCHANT_PASSWORD", Config.TEST_MERCHANT["password"]),
        },
        "admin": {
            "loginIdentifier": os.getenv("PERF_ADMIN_USERNAME", Config.TEST_ADMIN["username"]),
            "password": os.getenv("PERF_ADMIN_PASSWORD", Config.TEST_ADMIN["password"]),
        }
    }
    
    PERFORMANCE_THRESHOLDS = {
        "login": {
            "max_response_time_ms": 500,
            "max_avg_response_time_ms": 300,
            "min_rps": 50,
            "max_error_rate": 0.01,
        },
        "create_order": {
            "max_response_time_ms": 1000,
            "max_avg_response_time_ms": 500,
            "min_rps": 20,
            "max_error_rate": 0.01,
        },
        "query_list": {
            "max_response_time_ms": 300,
            "max_avg_response_time_ms": 150,
            "min_rps": 100,
            "max_error_rate": 0.01,
        },
    }
    
    TEST_DATA = {
        "service_id": int(os.getenv("PERF_SERVICE_ID", "1")),
        "pet_id": int(os.getenv("PERF_PET_ID", "1")),
        "product_id": int(os.getenv("PERF_PRODUCT_ID", "1")),
        "merchant_id": int(os.getenv("PERF_MERCHANT_ID", "1")),
    }
    
    LOAD_TEST_CONFIG = {
        "users": int(os.getenv("LOAD_TEST_USERS", "10")),
        "spawn_rate": int(os.getenv("LOAD_TEST_SPAWN_RATE", "2")),
        "run_time": os.getenv("LOAD_TEST_RUN_TIME", "60s"),
    }
    
    STRESS_TEST_CONFIG = {
        "users": int(os.getenv("STRESS_TEST_USERS", "50")),
        "spawn_rate": int(os.getenv("STRESS_TEST_SPAWN_RATE", "5")),
        "run_time": os.getenv("STRESS_TEST_RUN_TIME", "120s"),
    }
    
    SPIKE_TEST_CONFIG = {
        "users": int(os.getenv("SPIKE_TEST_USERS", "100")),
        "spawn_rate": int(os.getenv("SPIKE_TEST_SPAWN_RATE", "20")),
        "run_time": os.getenv("SPIKE_TEST_RUN_TIME", "30s"),
    }
    
    REPORT_CONFIG = {
        "output_dir": os.getenv("PERF_REPORT_DIR", "tests/performance/reports"),
        "html_report": os.getenv("PERF_HTML_REPORT", "tests/performance/reports/performance_report.html"),
        "stats_file": os.getenv("PERF_STATS_FILE", "tests/performance/reports/stats.csv"),
    }


class LoadTestConfig(PerformanceConfig):
    TEST_TYPE = "load"
    USERS = PerformanceConfig.LOAD_TEST_CONFIG["users"]
    SPAWN_RATE = PerformanceConfig.LOAD_TEST_CONFIG["spawn_rate"]
    RUN_TIME = PerformanceConfig.LOAD_TEST_CONFIG["run_time"]


class StressTestConfig(PerformanceConfig):
    TEST_TYPE = "stress"
    USERS = PerformanceConfig.STRESS_TEST_CONFIG["users"]
    SPAWN_RATE = PerformanceConfig.STRESS_TEST_CONFIG["spawn_rate"]
    RUN_TIME = PerformanceConfig.STRESS_TEST_CONFIG["run_time"]


class SpikeTestConfig(PerformanceConfig):
    TEST_TYPE = "spike"
    USERS = PerformanceConfig.SPIKE_TEST_CONFIG["users"]
    SPAWN_RATE = PerformanceConfig.SPIKE_TEST_CONFIG["spawn_rate"]
    RUN_TIME = PerformanceConfig.SPIKE_TEST_CONFIG["run_time"]


CONFIG_MAP = {
    "load": LoadTestConfig,
    "stress": StressTestConfig,
    "spike": SpikeTestConfig,
    "default": PerformanceConfig,
}


def get_performance_config(test_type: str = None) -> PerformanceConfig:
    test_type = test_type or os.getenv("PERF_TEST_TYPE", "default")
    return CONFIG_MAP.get(test_type, PerformanceConfig)()


performance_config = get_performance_config()

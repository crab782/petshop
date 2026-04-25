"""
测试配置文件
"""

import os

from dotenv import load_dotenv

load_dotenv()


class Config:
    API_BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080")

    TIMEOUT = int(os.getenv("TIMEOUT", "30"))

    TEST_USER = {
        "username": os.getenv("TEST_USER_USERNAME", "testuser"),
        "password": os.getenv("TEST_USER_PASSWORD", "testpass123"),
        "email": os.getenv("TEST_USER_EMAIL", "testuser@example.com"),
        "phone": os.getenv("TEST_USER_PHONE", "13800138000"),
    }

    TEST_MERCHANT = {
        "username": os.getenv("TEST_MERCHANT_USERNAME", "testmerchant"),
        "password": os.getenv("TEST_MERCHANT_PASSWORD", "merchant123"),
        "email": os.getenv("TEST_MERCHANT_EMAIL", "merchant@example.com"),
        "phone": os.getenv("TEST_MERCHANT_PHONE", "13900139000"),
        "name": os.getenv("TEST_MERCHANT_NAME", "测试商家"),
        "contact_person": os.getenv("TEST_MERCHANT_CONTACT", "测试联系人"),
    }

    TEST_ADMIN = {
        "username": os.getenv("TEST_ADMIN_USERNAME", "admin"),
        "password": os.getenv("TEST_ADMIN_PASSWORD", "admin123"),
    }

    TEST_SERVICE_ID = int(os.getenv("TEST_SERVICE_ID", "4007"))
    TEST_PET_ID = int(os.getenv("TEST_PET_ID", "4003"))
    TEST_MERCHANT_ID = int(os.getenv("TEST_MERCHANT_ID", "4007"))
    TEST_USER_ID = int(os.getenv("TEST_USER_ID", "4004"))

    PERFORMANCE_CONFIG = {
        "max_response_time": float(os.getenv("MAX_RESPONSE_TIME", "2.0")),
        "min_requests_per_second": float(os.getenv("MIN_RPS", "100")),
        "max_error_rate": float(os.getenv("MAX_ERROR_RATE", "0.01")),
        "concurrent_users": int(os.getenv("CONCURRENT_USERS", "10")),
        "spawn_rate": int(os.getenv("SPAWN_RATE", "2")),
    }

    DATABASE_CONFIG = {
        "host": os.getenv("DB_HOST", "localhost"),
        "port": int(os.getenv("DB_PORT", "3306")),
        "database": os.getenv("DB_NAME", "cg"),
        "user": os.getenv("DB_USER", "root"),
        "password": os.getenv("DB_PASSWORD", "123456"),
    }

    REPORT_CONFIG = {
        "output_dir": os.getenv("REPORT_DIR", "tests/report"),
        "allure_results_dir": os.getenv("ALLURE_RESULTS", "tests/report/allure-results"),
        "html_report": os.getenv("HTML_REPORT", "tests/report/report.html"),
    }


class DevConfig(Config):
    API_BASE_URL = "http://localhost:8080"
    DEBUG = True


class TestConfig(Config):
    API_BASE_URL = "http://test-api.pet-service.com"
    DEBUG = True


class ProdConfig(Config):
    API_BASE_URL = "https://api.pet-service.com"
    DEBUG = False


CONFIG_MAP = {"dev": DevConfig, "test": TestConfig, "prod": ProdConfig}


def get_config(env: str = None) -> Config:
    env = env or os.getenv("ENV", "dev")
    return CONFIG_MAP.get(env, DevConfig)()


config = get_config()

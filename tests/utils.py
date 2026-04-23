"""
测试工具类
"""
import logging
import json
from typing import Any, Dict, Optional
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from tests.config import config


logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class TokenManager:
    _instance = None
    _tokens: Dict[str, str] = {}
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance
    
    def set_token(self, user_type: str, token: str) -> None:
        self._tokens[user_type] = token
        logger.info(f"Token set for {user_type}")
    
    def get_token(self, user_type: str) -> Optional[str]:
        return self._tokens.get(user_type)
    
    def remove_token(self, user_type: str) -> None:
        if user_type in self._tokens:
            del self._tokens[user_type]
            logger.info(f"Token removed for {user_type}")
    
    def clear_all(self) -> None:
        self._tokens.clear()
        logger.info("All tokens cleared")


class HTTPClient:
    def __init__(self, base_url: str = None, timeout: int = None):
        self.base_url = base_url or config.API_BASE_URL
        self.timeout = timeout or config.TIMEOUT
        self.session = self._create_session()
        self.token_manager = TokenManager()
    
    def _create_session(self) -> requests.Session:
        session = requests.Session()
        retry_strategy = Retry(
            total=3,
            backoff_factor=1,
            status_forcelist=[429, 500, 502, 503, 504],
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        return session
    
    def _get_headers(self, user_type: str = None, custom_headers: Dict = None) -> Dict[str, str]:
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        if user_type:
            token = self.token_manager.get_token(user_type)
            if token:
                headers["Authorization"] = f"Bearer {token}"
        
        if custom_headers:
            headers.update(custom_headers)
        
        return headers
    
    def get(self, endpoint: str, params: Dict = None, user_type: str = None, 
            headers: Dict = None, **kwargs) -> requests.Response:
        url = f"{self.base_url}{endpoint}"
        request_headers = self._get_headers(user_type, headers)
        
        logger.info(f"GET {url} - Params: {params}")
        
        response = self.session.get(
            url,
            params=params,
            headers=request_headers,
            timeout=self.timeout,
            **kwargs
        )
        
        logger.info(f"Response Status: {response.status_code}")
        return response
    
    def post(self, endpoint: str, data: Dict = None, json_data: Dict = None,
             user_type: str = None, headers: Dict = None, **kwargs) -> requests.Response:
        url = f"{self.base_url}{endpoint}"
        request_headers = self._get_headers(user_type, headers)
        
        logger.info(f"POST {url} - Data: {json.dumps(data or json_data, ensure_ascii=False)}")
        
        response = self.session.post(
            url,
            data=data,
            json=json_data,
            headers=request_headers,
            timeout=self.timeout,
            **kwargs
        )
        
        logger.info(f"Response Status: {response.status_code}")
        return response
    
    def put(self, endpoint: str, data: Dict = None, json_data: Dict = None,
            user_type: str = None, headers: Dict = None, **kwargs) -> requests.Response:
        url = f"{self.base_url}{endpoint}"
        request_headers = self._get_headers(user_type, headers)
        
        logger.info(f"PUT {url} - Data: {json.dumps(data or json_data, ensure_ascii=False)}")
        
        response = self.session.put(
            url,
            data=data,
            json=json_data,
            headers=request_headers,
            timeout=self.timeout,
            **kwargs
        )
        
        logger.info(f"Response Status: {response.status_code}")
        return response
    
    def delete(self, endpoint: str, user_type: str = None, 
               headers: Dict = None, **kwargs) -> requests.Response:
        url = f"{self.base_url}{endpoint}"
        request_headers = self._get_headers(user_type, headers)
        
        logger.info(f"DELETE {url}")
        
        response = self.session.delete(
            url,
            headers=request_headers,
            timeout=self.timeout,
            **kwargs
        )
        
        logger.info(f"Response Status: {response.status_code}")
        return response
    
    def close(self) -> None:
        self.session.close()
        logger.info("HTTP Client session closed")


def assert_status_code(response: requests.Response, expected_code: int) -> None:
    actual_code = response.status_code
    assert actual_code == expected_code, \
        f"Expected status code {expected_code}, but got {actual_code}. Response: {response.text}"


def assert_response_time(response: requests.Response, max_time: float) -> None:
    actual_time = response.elapsed.total_seconds()
    assert actual_time <= max_time, \
        f"Response time {actual_time}s exceeded maximum {max_time}s"


def assert_json_key_exists(response: requests.Response, key: str) -> None:
    json_data = response.json()
    assert key in json_data, \
        f"Key '{key}' not found in response: {json.dumps(json_data, ensure_ascii=False)}"


def assert_json_value(response: requests.Response, key: str, expected_value: Any) -> None:
    json_data = response.json()
    assert key in json_data, f"Key '{key}' not found in response"
    actual_value = json_data[key]
    assert actual_value == expected_value, \
        f"Expected '{key}' to be {expected_value}, but got {actual_value}"


def assert_json_contains(response: requests.Response, expected_dict: Dict) -> None:
    json_data = response.json()
    for key, value in expected_dict.items():
        assert key in json_data, f"Key '{key}' not found in response"
        assert json_data[key] == value, \
            f"Expected '{key}' to be {value}, but got {json_data[key]}"


def log_test_case(test_name: str, description: str = None) -> None:
    logger.info("=" * 60)
    logger.info(f"Test Case: {test_name}")
    if description:
        logger.info(f"Description: {description}")
    logger.info("=" * 60)


def log_response_details(response: requests.Response) -> None:
    logger.info(f"Status Code: {response.status_code}")
    logger.info(f"Response Time: {response.elapsed.total_seconds()}s")
    logger.info(f"Response Headers: {dict(response.headers)}")
    try:
        logger.info(f"Response Body: {json.dumps(response.json(), ensure_ascii=False, indent=2)}")
    except Exception:
        logger.info(f"Response Text: {response.text}")


http_client = HTTPClient()
token_manager = TokenManager()

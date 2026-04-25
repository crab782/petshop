"""
评价相关测试数据Fixtures

提供评价测试数据的创建、管理和清理功能。
"""

from typing import Any, Dict, List

import pytest

from tests.testdata.data_builder import TestDataBuilder
from tests.testdata.data_cleanup import cleanup_reviews
from tests.testdata.data_manager import TestDataManager


@pytest.fixture(scope="function")
def test_review() -> Dict[str, Any]:
    """
    创建测试评价（function作用域）

    每个测试函数都会创建一个新的测试评价。

    Returns:
        Dict[str, Any]: 评价数据字典

    使用示例:
        def test_review_creation(test_review):
            assert test_review['rating'] >= 1
            assert test_review['rating'] <= 5
            assert test_review['comment'] is not None
    """
    builder = TestDataBuilder()
    review_data = builder.build_review()
    return review_data


@pytest.fixture(scope="function")
def test_reviews() -> List[Dict[str, Any]]:
    """
    创建多个测试评价（function作用域）

    创建一组测试评价，用于需要多个评价的测试场景。

    Returns:
        List[Dict[str, Any]]: 评价数据列表

    使用示例:
        def test_review_list(test_reviews):
            assert len(test_reviews) >= 3
            for review in test_reviews:
                assert review['rating'] is not None
    """
    builder = TestDataBuilder()
    reviews = []

    reviews.append(builder.build_review(rating=5, comment="非常满意的服务！"))
    reviews.append(builder.build_review(rating=4, comment="服务不错，有待改进"))
    reviews.append(builder.build_review(rating=3, comment="一般般，还可以"))

    return reviews


@pytest.fixture(scope="function")
def test_review_with_user(test_user) -> Dict[str, Any]:
    """
    创建带用户的测试评价（function作用域）

    创建一个关联到用户的测试评价。

    Args:
        test_user: 测试用户fixture

    Returns:
        Dict[str, Any]: 包含用户信息的评价数据字典

    使用示例:
        def test_review_user_relation(test_review_with_user):
            assert test_review_with_user['user_id'] is not None
    """
    builder = TestDataBuilder()
    user_id = test_user.get("id", 1)
    review_data = builder.build_review(user_id=user_id)
    return review_data


@pytest.fixture(scope="function")
def clean_reviews(db_connection):
    """
    清理测试评价数据（function作用域）

    测试结束后自动清理创建的评价数据。

    Args:
        db_connection: 数据库连接fixture

    Yields:
        None

    使用示例:
        def test_review_cleanup(clean_reviews):
            # 创建测试评价
            # 测试结束后自动清理
    """
    review_ids = []

    yield review_ids

    if review_ids and db_connection:
        cleanup_reviews(review_ids, db_connection)


@pytest.fixture(scope="function")
def test_review_with_cleanup(db_connection, test_review) -> Dict[str, Any]:
    """
    创建测试评价并自动清理（function作用域）

    创建测试评价并在测试结束后自动清理数据库中的评价记录。

    Args:
        db_connection: 数据库连接fixture
        test_review: 测试评价fixture

    Returns:
        Dict[str, Any]: 评价数据字典
    """
    yield test_review

    if db_connection and "id" in test_review:
        cleanup_reviews([test_review["id"]], db_connection)


@pytest.fixture(scope="function")
def test_review_data_manager() -> TestDataManager:
    """
    获取评价数据管理器（function作用域）

    提供TestDataManager实例，用于更复杂的评价数据管理场景。

    Returns:
        TestDataManager: 数据管理器实例

    使用示例:
        def test_complex_review_scenario(test_review_data_manager):
            review = test_review_data_manager.prepare_test_data('review')
            # 进行复杂操作
            test_review_data_manager.cleanup_all_test_data()
    """
    manager = TestDataManager()
    return manager


@pytest.fixture(scope="function")
def test_review_builder() -> TestDataBuilder:
    """
    获取评价数据构建器（function作用域）

    提供TestDataBuilder实例，用于自定义评价数据构建。

    Returns:
        TestDataBuilder: 数据构建器实例

    使用示例:
        def test_custom_review(test_review_builder):
            review = test_review_builder.build_review(
                rating=5,
                comment="非常棒的服务！"
            )
            assert review['rating'] == 5
    """
    return TestDataBuilder()


@pytest.fixture(scope="function")
def test_five_star_review() -> Dict[str, Any]:
    """
    创建五星好评（function作用域）

    创建一个5星评价。

    Returns:
        Dict[str, Any]: 五星评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=5, comment="非常满意！服务态度好，技术专业，强烈推荐！")
    return review_data


@pytest.fixture(scope="function")
def test_four_star_review() -> Dict[str, Any]:
    """
    创建四星评价（function作用域）

    创建一个4星评价。

    Returns:
        Dict[str, Any]: 四星评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=4, comment="服务不错，整体满意，有小细节可以改进。")
    return review_data


@pytest.fixture(scope="function")
def test_three_star_review() -> Dict[str, Any]:
    """
    创建三星评价（function作用域）

    创建一个3星评价。

    Returns:
        Dict[str, Any]: 三星评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=3, comment="服务一般，没有特别突出的地方。")
    return review_data


@pytest.fixture(scope="function")
def test_two_star_review() -> Dict[str, Any]:
    """
    创建二星评价（function作用域）

    创建一个2星评价。

    Returns:
        Dict[str, Any]: 二星评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=2, comment="服务有待改进，不太满意。")
    return review_data


@pytest.fixture(scope="function")
def test_one_star_review() -> Dict[str, Any]:
    """
    创建一星差评（function作用域）

    创建一个1星评价。

    Returns:
        Dict[str, Any]: 一星评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=1, comment="非常不满意，服务态度差，不会再来了。")
    return review_data


@pytest.fixture(scope="function")
def test_positive_review() -> Dict[str, Any]:
    """
    创建正面评价（function作用域）

    创建一个正面评价（4-5星）。

    Returns:
        Dict[str, Any]: 正面评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(
        rating=5, comment="非常满意的服务，环境干净整洁，工作人员专业负责，价格合理，强烈推荐！"
    )
    return review_data


@pytest.fixture(scope="function")
def test_negative_review() -> Dict[str, Any]:
    """
    创建负面评价（function作用域）

    创建一个负面评价（1-2星）。

    Returns:
        Dict[str, Any]: 负面评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=1, comment="服务态度差，环境脏乱差，价格虚高，不推荐！")
    return review_data


@pytest.fixture(scope="function")
def test_neutral_review() -> Dict[str, Any]:
    """
    创建中性评价（function作用域）

    创建一个中性评价（3星）。

    Returns:
        Dict[str, Any]: 中性评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=3, comment="服务一般，价格适中，没有特别满意也没有特别不满意的地方。")
    return review_data


@pytest.fixture(scope="function")
def test_review_with_reply() -> Dict[str, Any]:
    """
    创建带回复的测试评价（function作用域）

    创建一个商家已回复的评价。

    Returns:
        Dict[str, Any]: 带回复的评价数据字典
    """
    builder = TestDataBuilder()
    from datetime import datetime

    review_data = builder.build_review(
        rating=5,
        comment="非常满意的服务！",
        reply_content="感谢您的好评，我们会继续努力提供更好的服务！",
        reply_time=datetime.now(),
    )
    return review_data


@pytest.fixture(scope="function")
def test_review_without_reply() -> Dict[str, Any]:
    """
    创建未回复的测试评价（function作用域）

    创建一个商家未回复的评价。

    Returns:
        Dict[str, Any]: 未回复的评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=4, comment="服务不错", reply_content=None, reply_time=None)
    return review_data


@pytest.fixture(scope="function")
def test_long_comment_review() -> Dict[str, Any]:
    """
    创建长评测试评价（function作用域）

    创建一个包含长评的评价。

    Returns:
        Dict[str, Any]: 长评评价数据字典
    """
    builder = TestDataBuilder()
    long_comment = """
    这是一次非常棒的体验！从进门开始，工作人员就非常热情地接待。
    环境干净整洁，设备齐全。服务人员专业负责，对宠物很有爱心。
    价格合理，性价比很高。整个服务过程非常顺利，没有任何问题。
    强烈推荐给所有宠物主人！下次还会再来。
    """
    review_data = builder.build_review(rating=5, comment=long_comment.strip())
    return review_data


@pytest.fixture(scope="function")
def test_short_comment_review() -> Dict[str, Any]:
    """
    创建短评测试评价（function作用域）

    创建一个包含短评的评价。

    Returns:
        Dict[str, Any]: 短评评价数据字典
    """
    builder = TestDataBuilder()
    review_data = builder.build_review(rating=4, comment="不错")
    return review_data


@pytest.fixture(scope="function")
def test_review_batch() -> List[Dict[str, Any]]:
    """
    批量创建测试评价（function作用域）

    使用TestDataBuilder的批量构建功能创建多个评价。

    Returns:
        List[Dict[str, Any]]: 评价数据列表

    使用示例:
        def test_batch_reviews(test_review_batch):
            assert len(test_review_batch) == 10
    """
    builder = TestDataBuilder()
    reviews = builder.build_batch("review", 10, status="pending")
    return reviews


@pytest.fixture(scope="function")
def test_review_with_service(test_service) -> Dict[str, Any]:
    """
    创建带服务的测试评价（function作用域）

    创建一个关联到服务的测试评价。

    Args:
        test_service: 测试服务fixture

    Returns:
        Dict[str, Any]: 包含服务信息的评价数据字典
    """
    builder = TestDataBuilder()
    service_id = test_service.get("id", 1)
    review_data = builder.build_review(service_id=service_id)
    return review_data


@pytest.fixture(scope="function")
def test_review_with_appointment(test_appointment) -> Dict[str, Any]:
    """
    创建带预约的测试评价（function作用域）

    创建一个关联到预约的测试评价。

    Args:
        test_appointment: 测试预约fixture

    Returns:
        Dict[str, Any]: 包含预约信息的评价数据字典
    """
    builder = TestDataBuilder()
    appointment_id = test_appointment.get("id", 1)
    review_data = builder.build_review(appointment_id=appointment_id)
    return review_data


@pytest.fixture(scope="function")
def test_review_with_merchant(test_merchant) -> Dict[str, Any]:
    """
    创建带商家的测试评价（function作用域）

    创建一个关联到商家的测试评价。

    Args:
        test_merchant: 测试商家fixture

    Returns:
        Dict[str, Any]: 包含商家信息的评价数据字典
    """
    builder = TestDataBuilder()
    merchant_id = test_merchant.get("id", 1)
    review_data = builder.build_review(merchant_id=merchant_id)
    return review_data


@pytest.fixture(scope="function")
def test_mixed_rating_reviews() -> List[Dict[str, Any]]:
    """
    创建混合评分的测试评价（function作用域）

    创建一组包含不同评分的评价，用于测试评分统计功能。

    Returns:
        List[Dict[str, Any]]: 混合评分评价列表
    """
    builder = TestDataBuilder()
    reviews = []

    for rating in [1, 2, 3, 4, 5]:
        reviews.append(builder.build_review(rating=rating, comment=f"这是{rating}星评价"))

    return reviews

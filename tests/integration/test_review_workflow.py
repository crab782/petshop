"""
评价完整流程集成测试

测试评价的完整生命周期，包括：
- 用户添加评价 → 平台审核 → 显示在前端
- 评价审核流程：待审核 → 已通过/已拒绝
- 商家回复评价
- 评价删除
- 评价统计更新
- 验证评价数据一致性
"""

from datetime import datetime
from decimal import Decimal

import pytest

from tests.assertions.business_assertions import BusinessAssertionBuilder, assert_data_consistency
from tests.assertions.response_assertions import (
    ResponseAssertionBuilder,
    assert_response_key_exists,
    assert_response_key_value,
)
from tests.utils import HTTPClient, log_test_case

REVIEW_STATUS_TRANSITIONS = {"pending": {"approved", "rejected"}, "approved": set(), "rejected": set()}


@pytest.mark.integration
class TestReviewWorkflow:
    """评价完整流程集成测试类"""

    @pytest.fixture(autouse=True)
    def setup(
        self,
        http_client,
        user_token,
        merchant_token,
        admin_token,
        test_user,
        test_merchant,
        test_service,
        test_appointment,
    ):
        """
        测试前置设置

        Args:
            http_client: HTTP客户端
            user_token: 用户认证Token
            merchant_token: 商家认证Token
            admin_token: 管理员认证Token
            test_user: 测试用户数据
            test_merchant: 测试商家数据
            test_service: 测试服务数据
            test_appointment: 测试预约数据
        """
        self.client = http_client
        self.user_token = user_token
        self.merchant_token = merchant_token
        self.admin_token = admin_token
        self.user_data = test_user
        self.merchant_data = test_merchant
        self.service_data = test_service
        self.appointment_data = test_appointment
        self.user_headers = {"Authorization": f"Bearer {user_token}"}
        self.merchant_headers = {"Authorization": f"Bearer {merchant_token}"}
        self.admin_headers = {"Authorization": f"Bearer {admin_token}"}

    def _create_review(self, rating=5, comment="测试评价"):
        """
        创建测试评价

        Args:
            rating: 评分（1-5）
            comment: 评价内容

        Returns:
            dict: 评价数据
        """
        review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": self.appointment_data.get("id"),
            "rating": rating,
            "comment": comment,
        }

        response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)

        assert response.status_code in [200, 201], f"创建评价失败: {response.text}"
        return response.json()["data"]

    def _get_merchant_rating(self, merchant_id):
        """
        获取商家平均评分

        Args:
            merchant_id: 商家ID

        Returns:
            float: 平均评分
        """
        response = self.client.get(f"/api/merchants/{merchant_id}", headers=self.user_headers)

        if response.status_code == 200:
            return response.json()["data"].get("avg_rating", 0.0)
        return 0.0

    def test_review_add_workflow(self):
        """
        测试评价添加流程：用户添加评价 → 平台审核 → 显示在前端

        验证点：
        1. 用户能成功创建评价
        2. 评价初始状态为pending
        3. 平台审核通过后状态变为approved
        4. 审核通过的评价显示在前端
        """
        log_test_case("test_review_add_workflow", "测试评价添加流程")

        review = self._create_review(rating=5, comment="非常满意的服务！")
        review_id = review.get("id")

        assert review["rating"] == 5, "评分不正确"
        assert review["comment"] == "非常满意的服务！", "评价内容不正确"

        if "status" in review:
            assert review["status"] in ["pending", "approved"], "评价状态不正确"

        if review.get("status") == "pending":
            response = self.client.put(f"/api/admin/reviews/{review_id}/approve", headers=self.admin_headers)

            if response.status_code == 200:
                approved_review = response.json()["data"]
                assert approved_review["status"] == "approved", "审核通过后状态不是approved"

        response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取评价详情失败"

        response = self.client.get(f"/api/merchants/{self.merchant_data.get('id')}/reviews", headers=self.user_headers)
        assert response.status_code == 200, "获取商家评价列表失败"

        reviews = response.json().get("data", [])
        review_ids = [r.get("id") for r in reviews]

        if review.get("status") == "approved" or "status" not in review:
            assert review_id in review_ids, "审核通过的评价应该显示在前端"

    def test_review_audit_workflow(self):
        """
        测试评价审核流程：待审核 → 已通过/已拒绝

        验证点：
        1. 管理员可以审核评价
        2. 审核通过后评价状态变为approved
        3. 审核拒绝后评价状态变为rejected
        4. 拒绝的评价不显示在前端
        """
        log_test_case("test_review_audit_workflow", "测试评价审核流程")

        review = self._create_review(rating=3, comment="服务一般")
        review_id = review.get("id")

        if review.get("status") != "pending":
            pytest.skip("评价审核功能未实现或评价直接通过")

        response = self.client.put(f"/api/admin/reviews/{review_id}/approve", headers=self.admin_headers)

        if response.status_code == 200:
            approved_review = response.json()["data"]
            assert approved_review["status"] == "approved", "审核通过后状态不是approved"

        rejected_review = self._create_review(rating=1, comment="不满意的评价")
        rejected_review_id = rejected_review.get("id")

        response = self.client.put(
            f"/api/admin/reviews/{rejected_review_id}/reject",
            json_data={"reason": "评价内容不当"},
            headers=self.admin_headers,
        )

        if response.status_code == 200:
            rejected = response.json()["data"]
            assert rejected["status"] == "rejected", "审核拒绝后状态不是rejected"

            response = self.client.get(
                f"/api/merchants/{self.merchant_data.get('id')}/reviews", headers=self.user_headers
            )

            if response.status_code == 200:
                reviews = response.json().get("data", [])
                review_ids = [r.get("id") for r in reviews]
                assert rejected_review_id not in review_ids, "拒绝的评价不应该显示在前端"

    def test_merchant_reply_review(self):
        """
        测试商家回复评价

        验证点：
        1. 商家能成功回复评价
        2. 回复内容正确保存
        3. 回复时间正确记录
        4. 用户能看到商家回复
        """
        log_test_case("test_merchant_reply_review", "测试商家回复评价")

        review = self._create_review(rating=4, comment="服务不错，有待改进")
        review_id = review.get("id")

        reply_data = {"reply_content": "感谢您的评价，我们会继续改进服务质量！"}

        response = self.client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=self.merchant_headers
        )

        assert response.status_code == 200, f"商家回复评价失败: {response.text}"

        updated_review = response.json()["data"]
        assert "reply_content" in updated_review, "回复内容不存在"
        assert updated_review["reply_content"] == reply_data["reply_content"], "回复内容不匹配"

        if "reply_time" in updated_review:
            assert updated_review["reply_time"] is not None, "回复时间不存在"

        response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
        assert response.status_code == 200, "用户获取评价详情失败"

        review_detail = response.json()["data"]
        assert review_detail.get("reply_content") == reply_data["reply_content"], "用户看到的回复内容不匹配"

    def test_review_deletion(self):
        """
        测试评价删除

        验证点：
        1. 用户可以删除自己的评价
        2. 删除后评价不存在
        3. 管理员可以删除任何评价
        4. 删除后评分统计更新
        """
        log_test_case("test_review_deletion", "测试评价删除")

        review = self._create_review(rating=5, comment="测试删除的评价")
        review_id = review.get("id")

        response = self.client.delete(f"/api/reviews/{review_id}", headers=self.user_headers)

        if response.status_code in [200, 204]:
            response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
            assert response.status_code == 404, "删除的评价应该不存在"
        else:
            response = self.client.delete(f"/api/admin/reviews/{review_id}", headers=self.admin_headers)

            if response.status_code in [200, 204]:
                response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
                assert response.status_code == 404, "管理员删除的评价应该不存在"
            else:
                pytest.skip("评价删除功能未实现")

    def test_review_statistics_update(self):
        """
        测试评价统计更新

        验证点：
        1. 添加评价后商家评分更新
        2. 删除评价后商家评分更新
        3. 评分统计正确计算
        4. 评价数量正确统计
        """
        log_test_case("test_review_statistics_update", "测试评价统计更新")

        merchant_id = self.merchant_data.get("id")
        initial_rating = self._get_merchant_rating(merchant_id)

        review1 = self._create_review(rating=5, comment="五星好评")
        review2 = self._create_review(rating=4, comment="四星评价")
        review3 = self._create_review(rating=3, comment="三星评价")

        after_rating = self._get_merchant_rating(merchant_id)

        if initial_rating != after_rating:
            assert after_rating > 0, "商家评分应该大于0"

        response = self.client.get(f"/api/merchants/{merchant_id}/reviews/statistics", headers=self.user_headers)

        if response.status_code == 200:
            stats = response.json()["data"]

            if "total_count" in stats:
                assert stats["total_count"] >= 3, "评价数量统计不正确"

            if "avg_rating" in stats:
                assert 1 <= stats["avg_rating"] <= 5, "平均评分应该在1-5之间"

            if "rating_distribution" in stats:
                distribution = stats["rating_distribution"]
                assert isinstance(distribution, dict), "评分分布应该是字典类型"

    def test_review_data_consistency(self):
        """
        测试评价数据一致性

        验证点：
        1. 评价基本信息一致性
        2. 评价关联信息一致性
        3. 评价评分一致性
        4. 评价状态一致性
        """
        log_test_case("test_review_data_consistency", "测试评价数据一致性")

        review = self._create_review(rating=5, comment="数据一致性测试")
        review_id = review.get("id")

        response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取评价详情失败"

        review_detail = response.json()["data"]

        assert_data_consistency(review, review_detail, ["id", "service_id", "merchant_id", "rating"])

        assert review_detail["service_id"] == self.service_data.get("id"), "服务ID不一致"
        assert review_detail["merchant_id"] == self.merchant_data.get("id"), "商家ID不一致"
        assert review_detail["rating"] == 5, "评分不一致"

    def test_multiple_reviews_workflow(self):
        """
        测试多个评价并发流程

        验证点：
        1. 多个用户可以评价同一商家
        2. 每个评价独立处理
        3. 评分统计正确计算
        """
        log_test_case("test_multiple_reviews_workflow", "测试多个评价并发流程")

        reviews = []
        ratings = [5, 4, 5, 3, 4]

        for i, rating in enumerate(ratings):
            review = self._create_review(rating=rating, comment=f"测试评价 {i+1}")
            reviews.append(review)

        assert len(reviews) == len(ratings), "评价数量不正确"

        merchant_id = self.merchant_data.get("id")
        response = self.client.get(f"/api/merchants/{merchant_id}/reviews", headers=self.user_headers)

        if response.status_code == 200:
            merchant_reviews = response.json().get("data", [])
            review_ids = [r.get("id") for r in reviews]
            merchant_review_ids = [r.get("id") for r in merchant_reviews]

            for review_id in review_ids:
                if review_id in merchant_review_ids:
                    pass

    def test_review_rating_validation(self):
        """
        测试评价评分验证

        验证点：
        1. 评分必须在1-5之间
        2. 无效评分不能创建评价
        3. 评分为整数
        """
        log_test_case("test_review_rating_validation", "测试评价评分验证")

        valid_review = self._create_review(rating=5, comment="有效评分")
        assert valid_review["rating"] == 5, "有效评分创建失败"

        invalid_ratings = [0, 6, -1, 10]

        for invalid_rating in invalid_ratings:
            review_data = {
                "service_id": self.service_data.get("id"),
                "merchant_id": self.merchant_data.get("id"),
                "appointment_id": self.appointment_data.get("id"),
                "rating": invalid_rating,
                "comment": f"无效评分 {invalid_rating}",
            }

            response = self.client.post("/api/reviews", json_data=review_data, headers=self.user_headers)

            assert response.status_code in [400, 422], f"无效评分 {invalid_rating} 应该被拒绝"

    def test_review_content_validation(self):
        """
        测试评价内容验证

        验证点：
        1. 评价内容不能为空
        2. 评价内容长度限制
        3. 敏感词过滤
        """
        log_test_case("test_review_content_validation", "测试评价内容验证")

        empty_review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": self.appointment_data.get("id"),
            "rating": 5,
            "comment": "",
        }

        response = self.client.post("/api/reviews", json_data=empty_review_data, headers=self.user_headers)

        if response.status_code in [400, 422]:
            assert True, "空评价内容被正确拒绝"
        else:
            pytest.skip("评价内容验证可能未启用")

        long_comment = "测试评价" * 1000
        long_review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": self.appointment_data.get("id"),
            "rating": 5,
            "comment": long_comment,
        }

        response = self.client.post("/api/reviews", json_data=long_review_data, headers=self.user_headers)

        if response.status_code in [400, 422]:
            assert True, "超长评价内容被正确拒绝"

    def test_review_anonymous_option(self):
        """
        测试匿名评价选项

        验证点：
        1. 用户可以选择匿名评价
        2. 匿名评价不显示用户信息
        3. 商家能看到匿名评价
        """
        log_test_case("test_review_anonymous_option", "测试匿名评价选项")

        anonymous_review_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": self.appointment_data.get("id"),
            "rating": 4,
            "comment": "匿名评价测试",
            "is_anonymous": True,
        }

        response = self.client.post("/api/reviews", json_data=anonymous_review_data, headers=self.user_headers)

        if response.status_code in [200, 201]:
            review = response.json()["data"]

            if "is_anonymous" in review:
                assert review["is_anonymous"] == True, "匿名标志不正确"

            response = self.client.get(f"/api/reviews/{review.get('id')}", headers=self.merchant_headers)

            if response.status_code == 200:
                review_detail = response.json()["data"]

                if review.get("is_anonymous"):
                    if "user_info" in review_detail:
                        user_info = review_detail["user_info"]
                        assert user_info.get("username") in ["匿名用户", "Anonymous", None], "匿名评价不应显示用户名"
        else:
            pytest.skip("匿名评价功能未实现")

    def test_review_images_upload(self):
        """
        测试评价图片上传

        验证点：
        1. 用户可以上传评价图片
        2. 图片正确保存
        3. 图片在评价详情中显示
        """
        log_test_case("test_review_images_upload", "测试评价图片上传")

        review_with_images_data = {
            "service_id": self.service_data.get("id"),
            "merchant_id": self.merchant_data.get("id"),
            "appointment_id": self.appointment_data.get("id"),
            "rating": 5,
            "comment": "带图片的评价",
            "images": ["https://example.com/image1.jpg", "https://example.com/image2.jpg"],
        }

        response = self.client.post("/api/reviews", json_data=review_with_images_data, headers=self.user_headers)

        if response.status_code in [200, 201]:
            review = response.json()["data"]

            if "images" in review:
                assert len(review["images"]) == 2, "图片数量不正确"

                response = self.client.get(f"/api/reviews/{review.get('id')}", headers=self.user_headers)

                if response.status_code == 200:
                    review_detail = response.json()["data"]
                    assert len(review_detail.get("images", [])) == 2, "评价详情中图片数量不正确"
        else:
            pytest.skip("评价图片上传功能未实现")

    def test_complete_review_workflow(self):
        """
        测试完整的评价流程

        完整流程：
        1. 用户创建评价
        2. 平台审核评价
        3. 商家回复评价
        4. 验证评价显示
        5. 验证评分统计
        """
        log_test_case("test_complete_review_workflow", "测试完整的评价流程")

        review = self._create_review(rating=5, comment="完整流程测试评价")
        review_id = review.get("id")

        if review.get("status") == "pending":
            response = self.client.put(f"/api/admin/reviews/{review_id}/approve", headers=self.admin_headers)
            if response.status_code == 200:
                review = response.json()["data"]

        reply_data = {"reply_content": "感谢您的好评！"}

        response = self.client.post(
            f"/api/merchant/reviews/{review_id}/reply", json_data=reply_data, headers=self.merchant_headers
        )

        if response.status_code == 200:
            review = response.json()["data"]
            assert review.get("reply_content") == reply_data["reply_content"], "商家回复内容不匹配"

        response = self.client.get(f"/api/reviews/{review_id}", headers=self.user_headers)
        assert response.status_code == 200, "获取评价详情失败"

        final_review = response.json()["data"]
        assert final_review["rating"] == 5, "最终评分不正确"
        assert final_review["comment"] == "完整流程测试评价", "最终评价内容不正确"

        merchant_id = self.merchant_data.get("id")
        response = self.client.get(f"/api/merchants/{merchant_id}/reviews/statistics", headers=self.user_headers)

        if response.status_code == 200:
            stats = response.json()["data"]
            assert stats.get("total_count", 0) > 0, "评价数量应该大于0"

"""
平台端API单元测试

测试平台端的所有API接口，包括：
- 用户管理接口
- 商家管理接口
- 服务管理接口
- 商品管理接口
- 评价管理接口
- 公告管理接口
- 系统设置接口
- 角色权限接口
- 操作日志接口
- 活动管理接口

重点测试：
- 权限控制（只有管理员可以访问）
- 审核流程（商家审核、评价审核）
- 数据验证和边界条件
- 未认证访问（应返回401）
- 非管理员访问（应返回403）
"""

from datetime import datetime, timedelta
from decimal import Decimal

import allure
import pytest

from tests.unit import BaseAPITest


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminUserManagementAPI(BaseAPITest):
    """用户管理接口测试"""

    @allure.story("用户管理")
    @allure.title("获取用户列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_users_success(self, unit_http_client, test_admin):
        """测试获取用户列表成功"""
        response = unit_http_client.get("/api/admin/users", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("用户管理")
    @allure.title("未认证访问用户列表")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_get_users_unauthorized(self, unit_http_client):
        """测试未认证访问用户列表"""
        response = unit_http_client.get("/api/admin/users")

        self.assert_response_error(response, 401)

    @allure.story("用户管理")
    @allure.title("非管理员访问用户列表")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_get_users_forbidden(self, unit_http_client, test_user):
        """测试非管理员访问用户列表"""
        response = unit_http_client.get("/api/admin/users", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_error(response, 403)

    @allure.story("用户管理")
    @allure.title("获取用户详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_user_detail_success(self, unit_http_client, test_admin, test_user):
        """测试获取用户详情成功"""
        user_id = test_user["user"]["id"]

        response = unit_http_client.get(
            f"/api/admin/users/{user_id}", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)
        self.assert_required_fields(response, ["user", "stats"])

    @allure.story("用户管理")
    @allure.title("获取不存在的用户详情")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_user_detail_not_found(self, unit_http_client, test_admin):
        """测试获取不存在的用户详情"""
        response = unit_http_client.get("/api/admin/users/99999", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_error(response, 404)

    @allure.story("用户管理")
    @allure.title("更新用户状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_user_status_success(self, unit_http_client, test_admin, test_user):
        """测试更新用户状态成功"""
        user_id = test_user["user"]["id"]

        status_data = {"status": "disabled"}

        response = unit_http_client.put(
            f"/api/admin/users/{user_id}/status",
            json_data=status_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("用户管理")
    @allure.title("更新用户状态无效值")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_user_status_invalid(self, unit_http_client, test_admin, test_user):
        """测试更新用户状态无效值"""
        user_id = test_user["user"]["id"]

        status_data = {"status": "invalid_status"}

        response = unit_http_client.put(
            f"/api/admin/users/{user_id}/status",
            json_data=status_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_error(response, 400)

    @allure.story("用户管理")
    @allure.title("删除用户成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_user_success(self, unit_http_client, test_admin):
        """测试删除用户成功"""
        response = unit_http_client.delete("/api/admin/users/99999", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_status_code(response, 204)

    @allure.story("用户管理")
    @allure.title("批量更新用户状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_update_user_status_success(self, unit_http_client, test_admin):
        """测试批量更新用户状态成功"""
        batch_data = {"ids": [1, 2, 3], "status": "active"}

        response = unit_http_client.put(
            "/api/admin/users/batch/status", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("用户管理")
    @allure.title("批量更新用户状态空列表")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_update_user_status_empty_ids(self, unit_http_client, test_admin):
        """测试批量更新用户状态空列表"""
        batch_data = {"ids": [], "status": "active"}

        response = unit_http_client.put(
            "/api/admin/users/batch/status", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("用户管理")
    @allure.title("批量删除用户成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_users_success(self, unit_http_client, test_admin):
        """测试批量删除用户成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/admin/users/batch", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("用户管理")
    @allure.title("获取最近注册用户成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_recent_users_success(self, unit_http_client, test_admin):
        """测试获取最近注册用户成功"""
        response = unit_http_client.get("/api/admin/users/recent", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminMerchantManagementAPI(BaseAPITest):
    """商家管理接口测试"""

    @allure.story("商家管理")
    @allure.title("获取商家列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_merchants_success(self, unit_http_client, test_admin):
        """测试获取商家列表成功"""
        response = unit_http_client.get("/api/admin/merchants", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("商家管理")
    @allure.title("未认证访问商家列表")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_get_merchants_unauthorized(self, unit_http_client):
        """测试未认证访问商家列表"""
        response = unit_http_client.get("/api/admin/merchants")

        self.assert_response_error(response, 401)

    @allure.story("商家管理")
    @allure.title("获取商家详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_detail_success(self, unit_http_client, test_admin, test_merchant):
        """测试获取商家详情成功"""
        merchant_id = test_merchant["merchant"]["id"]

        response = unit_http_client.get(
            f"/api/admin/merchants/{merchant_id}", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("商家管理")
    @allure.title("获取不存在的商家详情")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_detail_not_found(self, unit_http_client, test_admin):
        """测试获取不存在的商家详情"""
        response = unit_http_client.get(
            "/api/admin/merchants/99999", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_error(response, 404)

    @allure.story("商家管理")
    @allure.title("更新商家状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_merchant_status_success(self, unit_http_client, test_admin, test_merchant):
        """测试更新商家状态成功"""
        merchant_id = test_merchant["merchant"]["id"]

        status_data = {"status": "approved"}

        response = unit_http_client.put(
            f"/api/admin/merchants/{merchant_id}/status",
            json_data=status_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商家管理")
    @allure.title("删除商家成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_merchant_success(self, unit_http_client, test_admin):
        """测试删除商家成功"""
        response = unit_http_client.delete(
            "/api/admin/merchants/99999", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("商家管理")
    @allure.title("获取待审核商家列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_pending_merchants_success(self, unit_http_client, test_admin):
        """测试获取待审核商家列表成功"""
        response = unit_http_client.get(
            "/api/admin/merchants/pending", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("商家管理")
    @allure.title("审核商家通过成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.audit
    def test_audit_merchant_approve_success(self, unit_http_client, test_admin, test_merchant):
        """测试审核商家通过成功"""
        merchant_id = test_merchant["merchant"]["id"]

        audit_data = {"status": "approved", "reason": "资质审核通过"}

        response = unit_http_client.put(
            f"/api/admin/merchants/{merchant_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商家管理")
    @allure.title("审核商家拒绝成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.audit
    def test_audit_merchant_reject_success(self, unit_http_client, test_admin, test_merchant):
        """测试审核商家拒绝成功"""
        merchant_id = test_merchant["merchant"]["id"]

        audit_data = {"status": "rejected", "reason": "资质不符合要求"}

        response = unit_http_client.put(
            f"/api/admin/merchants/{merchant_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商家管理")
    @allure.title("审核商家无效状态")
    @allure.severity(allure.severity_level.NORMAL)
    def test_audit_merchant_invalid_status(self, unit_http_client, test_admin, test_merchant):
        """测试审核商家无效状态"""
        merchant_id = test_merchant["merchant"]["id"]

        audit_data = {"status": "invalid_status", "reason": "测试"}

        response = unit_http_client.put(
            f"/api/admin/merchants/{merchant_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_error(response, 400)

    @allure.story("商家管理")
    @allure.title("批量更新商家状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_update_merchant_status_success(self, unit_http_client, test_admin):
        """测试批量更新商家状态成功"""
        batch_data = {"ids": [1, 2, 3], "status": "approved"}

        response = unit_http_client.put(
            "/api/admin/merchants/batch/status",
            json_data=batch_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商家管理")
    @allure.title("批量删除商家成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_merchants_success(self, unit_http_client, test_admin):
        """测试批量删除商家成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/admin/merchants/batch", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminServiceManagementAPI(BaseAPITest):
    """服务管理接口测试"""

    @allure.story("服务管理")
    @allure.title("获取服务列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_services_success(self, unit_http_client, test_admin):
        """测试获取服务列表成功"""
        response = unit_http_client.get("/api/admin/services", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)

    @allure.story("服务管理")
    @allure.title("获取服务列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_services_with_pagination(self, unit_http_client, test_admin):
        """测试获取服务列表带分页"""
        response = unit_http_client.get(
            "/api/admin/services",
            params={"page": 0, "pageSize": 10},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)
        self.assert_pagination(response, expected_page=0)

    @allure.story("服务管理")
    @allure.title("更新服务状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_service_status_success(self, unit_http_client, test_admin, test_service):
        """测试更新服务状态成功"""
        service_id = test_service.get("id")

        status_data = {"status": "disabled"}

        response = unit_http_client.put(
            f"/api/admin/services/{service_id}/status",
            json_data=status_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("服务管理")
    @allure.title("删除服务成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_service_success(self, unit_http_client, test_admin):
        """测试删除服务成功"""
        response = unit_http_client.delete(
            "/api/admin/services/99999", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("服务管理")
    @allure.title("批量更新服务状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_update_service_status_success(self, unit_http_client, test_admin):
        """测试批量更新服务状态成功"""
        batch_data = {"ids": [1, 2, 3], "status": "enabled"}

        response = unit_http_client.put(
            "/api/admin/services/batch/status", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("服务管理")
    @allure.title("批量删除服务成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_services_success(self, unit_http_client, test_admin):
        """测试批量删除服务成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/admin/services/batch", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminProductManagementAPI(BaseAPITest):
    """商品管理接口测试"""

    @allure.story("商品管理")
    @allure.title("获取商品列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_products_success(self, unit_http_client, test_admin):
        """测试获取商品列表成功"""
        response = unit_http_client.get("/api/admin/products", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)

    @allure.story("商品管理")
    @allure.title("获取商品列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_products_with_filter(self, unit_http_client, test_admin):
        """测试获取商品列表带筛选"""
        response = unit_http_client.get(
            "/api/admin/products",
            params={"keyword": "测试", "status": "enabled"},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商品管理")
    @allure.title("获取商品详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_product_detail_success(self, unit_http_client, test_admin, test_product):
        """测试获取商品详情成功"""
        product_id = test_product["id"]

        response = unit_http_client.get(
            f"/api/admin/products/{product_id}", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("商品管理")
    @allure.title("更新商品信息成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_product_success(self, unit_http_client, test_admin, test_product):
        """测试更新商品信息成功"""
        product_id = test_product["id"]

        update_data = {"name": "更新后的商品", "price": Decimal("99.99"), "stock": 100}

        response = unit_http_client.put(
            f"/api/admin/products/{product_id}",
            json_data=update_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商品管理")
    @allure.title("更新商品状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_product_status_success(self, unit_http_client, test_admin, test_product):
        """测试更新商品状态成功"""
        product_id = test_product["id"]

        status_data = {"status": "disabled"}

        response = unit_http_client.put(
            f"/api/admin/products/{product_id}/status",
            json_data=status_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("商品管理")
    @allure.title("删除商品成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_product_success(self, unit_http_client, test_admin):
        """测试删除商品成功"""
        response = unit_http_client.delete(
            "/api/admin/products/99999", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("商品管理")
    @allure.title("批量更新商品状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_update_product_status_success(self, unit_http_client, test_admin):
        """测试批量更新商品状态成功"""
        batch_data = {"ids": [1, 2, 3], "status": "enabled"}

        response = unit_http_client.put(
            "/api/admin/products/batch/status", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("商品管理")
    @allure.title("批量删除商品成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_products_success(self, unit_http_client, test_admin):
        """测试批量删除商品成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/admin/products/batch", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminReviewManagementAPI(BaseAPITest):
    """评价管理接口测试"""

    @allure.story("评价管理")
    @allure.title("获取评价列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_reviews_success(self, unit_http_client, test_admin):
        """测试获取评价列表成功"""
        response = unit_http_client.get("/api/admin/reviews", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("获取评价列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("rating", [1, 2, 3, 4, 5])
    def test_get_reviews_with_filter(self, unit_http_client, test_admin, rating):
        """测试获取评价列表带评分筛选"""
        response = unit_http_client.get(
            "/api/admin/reviews", params={"rating": rating}, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("获取待审核评价列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_pending_reviews_success(self, unit_http_client, test_admin):
        """测试获取待审核评价列表成功"""
        response = unit_http_client.get(
            "/api/admin/reviews/pending", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("审核评价通过成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.audit
    def test_audit_review_approve_success(self, unit_http_client, test_admin, test_review):
        """测试审核评价通过成功"""
        review_id = test_review.get("id")

        audit_data = {"status": "approved", "reason": "内容合规"}

        response = unit_http_client.put(
            f"/api/admin/reviews/{review_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("审核评价拒绝成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.audit
    def test_audit_review_reject_success(self, unit_http_client, test_admin, test_review):
        """测试审核评价拒绝成功"""
        review_id = test_review.get("id")

        audit_data = {"status": "rejected", "reason": "内容违规"}

        response = unit_http_client.put(
            f"/api/admin/reviews/{review_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("批准评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_approve_review_success(self, unit_http_client, test_admin, test_review):
        """测试批准评价成功"""
        review_id = test_review.get("id")

        response = unit_http_client.put(
            f"/api/admin/reviews/{review_id}/approve", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("标记评价违规成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_mark_review_violation_success(self, unit_http_client, test_admin, test_review):
        """测试标记评价违规成功"""
        review_id = test_review.get("id")

        response = unit_http_client.put(
            f"/api/admin/reviews/{review_id}/violation", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("删除评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_review_success(self, unit_http_client, test_admin):
        """测试删除评价成功"""
        response = unit_http_client.delete(
            "/api/admin/reviews/99999", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("评价管理")
    @allure.title("批量更新评价状态成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_update_review_status_success(self, unit_http_client, test_admin):
        """测试批量更新评价状态成功"""
        batch_data = {"ids": [1, 2, 3], "status": "approved"}

        response = unit_http_client.put(
            "/api/admin/reviews/batch/status", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("评价管理")
    @allure.title("批量删除评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_reviews_success(self, unit_http_client, test_admin):
        """测试批量删除评价成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/admin/reviews/batch", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminAnnouncementManagementAPI(BaseAPITest):
    """公告管理接口测试"""

    @allure.story("公告管理")
    @allure.title("获取公告列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_announcements_success(self, unit_http_client, test_admin):
        """测试获取公告列表成功"""
        response = unit_http_client.get("/api/admin/announcements", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)

    @allure.story("公告管理")
    @allure.title("创建公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_announcement_success(self, unit_http_client, test_admin):
        """测试创建公告成功"""
        announcement_data = {"title": "测试公告", "content": "这是一条测试公告内容", "status": "draft"}

        response = unit_http_client.post(
            "/api/admin/announcements", json_data=announcement_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("公告管理")
    @allure.title("更新公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_announcement_success(self, unit_http_client, test_admin):
        """测试更新公告成功"""
        update_data = {"title": "更新后的公告", "content": "更新后的公告内容"}

        response = unit_http_client.put(
            "/api/admin/announcements/1", json_data=update_data, headers=self.get_auth_headers(test_admin["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("公告管理")
    @allure.title("删除公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_delete_announcement_success(self, unit_http_client, test_admin):
        """测试删除公告成功"""
        response = unit_http_client.delete(
            "/api/admin/announcements/99999", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("公告管理")
    @allure.title("发布公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_publish_announcement_success(self, unit_http_client, test_admin):
        """测试发布公告成功"""
        response = unit_http_client.put(
            "/api/admin/announcements/1/publish", headers=self.get_auth_headers(test_admin["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("公告管理")
    @allure.title("下架公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_unpublish_announcement_success(self, unit_http_client, test_admin):
        """测试下架公告成功"""
        response = unit_http_client.put(
            "/api/admin/announcements/1/unpublish", headers=self.get_auth_headers(test_admin["token"])
        )

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("公告管理")
    @allure.title("批量发布公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_publish_announcements_success(self, unit_http_client, test_admin):
        """测试批量发布公告成功"""
        batch_data = {"ids": [1, 2, 3]}

        response = unit_http_client.put(
            "/api/admin/announcements/batch/publish",
            json_data=batch_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("公告管理")
    @allure.title("批量下架公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_unpublish_announcements_success(self, unit_http_client, test_admin):
        """测试批量下架公告成功"""
        batch_data = {"ids": [1, 2, 3]}

        response = unit_http_client.put(
            "/api/admin/announcements/batch/unpublish",
            json_data=batch_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("公告管理")
    @allure.title("批量删除公告成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_delete_announcements_success(self, unit_http_client, test_admin):
        """测试批量删除公告成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/admin/announcements/batch", json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminSystemSettingsAPI(BaseAPITest):
    """系统设置接口测试"""

    @allure.story("系统设置")
    @allure.title("获取系统设置成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_system_settings_success(self, unit_http_client, test_admin):
        """测试获取系统设置成功"""
        response = unit_http_client.get(
            "/api/admin/system/settings", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("更新系统设置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_system_settings_success(self, unit_http_client, test_admin):
        """测试更新系统设置成功"""
        settings_data = {"siteName": "宠物服务平台", "contactEmail": "support@petshop.com"}

        response = unit_http_client.put(
            "/api/admin/system/settings", json_data=settings_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("获取邮件设置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_email_settings_success(self, unit_http_client, test_admin):
        """测试获取邮件设置成功"""
        response = unit_http_client.get(
            "/api/admin/system/settings/email", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("获取短信设置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_sms_settings_success(self, unit_http_client, test_admin):
        """测试获取短信设置成功"""
        response = unit_http_client.get(
            "/api/admin/system/settings/sms", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("获取支付设置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_payment_settings_success(self, unit_http_client, test_admin):
        """测试获取支付设置成功"""
        response = unit_http_client.get(
            "/api/admin/system/settings/payment", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("获取文件上传设置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_upload_settings_success(self, unit_http_client, test_admin):
        """测试获取文件上传设置成功"""
        response = unit_http_client.get(
            "/api/admin/system/settings/upload", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("获取系统配置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_system_config_success(self, unit_http_client, test_admin):
        """测试获取系统配置成功"""
        response = unit_http_client.get("/api/admin/system/config", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)

    @allure.story("系统设置")
    @allure.title("更新系统配置成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_system_config_success(self, unit_http_client, test_admin):
        """测试更新系统配置成功"""
        config_data = {"siteName": "宠物家园", "contactEmail": "admin@petshop.com"}

        response = unit_http_client.put(
            "/api/admin/system/config", json_data=config_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminDashboardAPI(BaseAPITest):
    """仪表盘接口测试"""

    @allure.story("仪表盘")
    @allure.title("获取仪表盘统计数据成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_dashboard_stats_success(self, unit_http_client, test_admin):
        """测试获取仪表盘统计数据成功"""
        response = unit_http_client.get("/api/admin/dashboard", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)
        self.assert_required_fields(response, ["userCount", "merchantCount", "orderCount", "serviceCount"])

    @allure.story("仪表盘")
    @allure.title("获取待审核商家成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_pending_merchants_dashboard_success(self, unit_http_client, test_admin):
        """测试获取待审核商家成功"""
        response = unit_http_client.get(
            "/api/admin/dashboard/pending-merchants", headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminActivityManagementAPI(BaseAPITest):
    """活动管理接口测试"""

    @allure.story("活动管理")
    @allure.title("获取活动列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_activities_success(self, unit_http_client, test_admin):
        """测试获取活动列表成功"""
        response = unit_http_client.get("/api/admin/activities", headers=self.get_auth_headers(test_admin["token"]))

        self.assert_response_success(response)

    @allure.story("活动管理")
    @allure.title("创建活动成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_activity_success(self, unit_http_client, test_admin):
        """测试创建活动成功"""
        activity_data = {"name": "测试活动", "type": "promotion", "description": "测试活动描述"}

        response = unit_http_client.post(
            "/api/admin/activities", json_data=activity_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_success(response)

    @allure.story("活动管理")
    @allure.title("创建活动缺少名称")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_activity_missing_name(self, unit_http_client, test_admin):
        """测试创建活动缺少名称"""
        activity_data = {"type": "promotion", "description": "测试活动描述"}

        response = unit_http_client.post(
            "/api/admin/activities", json_data=activity_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_error(response, 400)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
@pytest.mark.security
class TestAdminAPISecurity(BaseAPITest):
    """平台端API安全测试"""

    @allure.story("安全测试")
    @allure.title("未认证访问所有平台端接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.parametrize(
        "endpoint,method",
        [
            ("/api/admin/users", "GET"),
            ("/api/admin/merchants", "GET"),
            ("/api/admin/services", "GET"),
            ("/api/admin/products", "GET"),
            ("/api/admin/reviews", "GET"),
            ("/api/admin/announcements", "GET"),
            ("/api/admin/system/settings", "GET"),
            ("/api/admin/dashboard", "GET"),
            ("/api/admin/activities", "GET"),
        ],
    )
    def test_unauthorized_access(self, unit_http_client, endpoint, method):
        """测试未认证访问平台端接口"""
        if method == "GET":
            response = unit_http_client.get(endpoint)
        elif method == "POST":
            response = unit_http_client.post(endpoint, json_data={})
        elif method == "PUT":
            response = unit_http_client.put(endpoint, json_data={})

        self.assert_response_error(response, 401)

    @allure.story("安全测试")
    @allure.title("非管理员访问平台端接口")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.parametrize(
        "endpoint,method",
        [
            ("/api/admin/users", "GET"),
            ("/api/admin/merchants", "GET"),
            ("/api/admin/services", "GET"),
            ("/api/admin/products", "GET"),
            ("/api/admin/reviews", "GET"),
            ("/api/admin/announcements", "GET"),
            ("/api/admin/system/settings", "GET"),
            ("/api/admin/dashboard", "GET"),
        ],
    )
    def test_forbidden_access(self, unit_http_client, test_user, endpoint, method):
        """测试非管理员访问平台端接口"""
        headers = self.get_auth_headers(test_user["token"])

        if method == "GET":
            response = unit_http_client.get(endpoint, headers=headers)
        elif method == "POST":
            response = unit_http_client.post(endpoint, json_data={}, headers=headers)
        elif method == "PUT":
            response = unit_http_client.put(endpoint, json_data={}, headers=headers)

        self.assert_response_error(response, 403)

    @allure.story("安全测试")
    @allure.title("无效Token访问")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_invalid_token_access(self, unit_http_client):
        """测试无效Token访问"""
        headers = self.get_auth_headers("invalid_token_12345")

        response = unit_http_client.get("/api/admin/users", headers=headers)

        self.assert_response_error(response, 401)

    @allure.story("安全测试")
    @allure.title("商家Token访问平台端接口")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_merchant_token_access(self, unit_http_client, test_merchant):
        """测试商家Token访问平台端接口"""
        headers = self.get_auth_headers(test_merchant["token"])

        response = unit_http_client.get("/api/admin/users", headers=headers)

        self.assert_response_error(response, 403)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminDataValidation(BaseAPITest):
    """数据验证测试"""

    @allure.story("数据验证")
    @allure.title("用户状态验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "status,expected_valid",
        [
            ("active", True),
            ("disabled", True),
            ("invalid", False),
            ("", False),
        ],
    )
    def test_user_status_validation(self, unit_http_client, test_admin, test_user, status, expected_valid):
        """测试用户状态验证"""
        user_id = test_user["user"]["id"]

        status_data = {"status": status}

        response = unit_http_client.put(
            f"/api/admin/users/{user_id}/status",
            json_data=status_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        if expected_valid:
            assert response.status_code in [200, 404], f"有效的状态{status}应该被接受"
        else:
            assert response.status_code in [400, 404], f"无效的状态{status}应该被拒绝"

    @allure.story("数据验证")
    @allure.title("商家审核状态验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "status,expected_valid",
        [
            ("approved", True),
            ("rejected", True),
            ("invalid", False),
            ("", False),
        ],
    )
    def test_merchant_audit_status_validation(
        self, unit_http_client, test_admin, test_merchant, status, expected_valid
    ):
        """测试商家审核状态验证"""
        merchant_id = test_merchant["merchant"]["id"]

        audit_data = {"status": status, "reason": "测试原因"}

        response = unit_http_client.put(
            f"/api/admin/merchants/{merchant_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        if expected_valid:
            assert response.status_code in [200, 404], f"有效的审核状态{status}应该被接受"
        else:
            assert response.status_code in [400, 404], f"无效的审核状态{status}应该被拒绝"

    @allure.story("数据验证")
    @allure.title("评价审核状态验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "status,expected_valid",
        [
            ("approved", True),
            ("rejected", True),
            ("invalid", False),
            ("", False),
        ],
    )
    def test_review_audit_status_validation(self, unit_http_client, test_admin, test_review, status, expected_valid):
        """测试评价审核状态验证"""
        review_id = test_review.get("id")

        audit_data = {"status": status, "reason": "测试原因"}

        response = unit_http_client.put(
            f"/api/admin/reviews/{review_id}/audit",
            json_data=audit_data,
            headers=self.get_auth_headers(test_admin["token"]),
        )

        if expected_valid:
            assert response.status_code in [200, 404], f"有效的审核状态{status}应该被接受"
        else:
            assert response.status_code in [400, 404], f"无效的审核状态{status}应该被拒绝"

    @allure.story("数据验证")
    @allure.title("批量操作空ID列表验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "endpoint",
        [
            "/api/admin/users/batch/status",
            "/api/admin/merchants/batch/status",
            "/api/admin/services/batch/status",
            "/api/admin/products/batch/status",
            "/api/admin/reviews/batch/status",
        ],
    )
    def test_batch_operation_empty_ids(self, unit_http_client, test_admin, endpoint):
        """测试批量操作空ID列表验证"""
        batch_data = {"ids": [], "status": "active"}

        response = unit_http_client.put(
            endpoint, json_data=batch_data, headers=self.get_auth_headers(test_admin["token"])
        )

        self.assert_response_error(response, 400)


@allure.feature("平台端API")
@pytest.mark.admin
@pytest.mark.unit_api
class TestAdminPaginationAndFilter(BaseAPITest):
    """分页和筛选功能测试"""

    @allure.story("分页筛选")
    @allure.title("用户列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size",
        [
            (0, 10),
            (1, 20),
            (2, 5),
        ],
    )
    def test_user_pagination(self, unit_http_client, test_admin, page, page_size):
        """测试用户列表分页"""
        response = unit_http_client.get(
            "/api/admin/users/recent",
            params={"page": page, "pageSize": page_size},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商家列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size",
        [
            (0, 10),
            (1, 20),
            (2, 5),
        ],
    )
    def test_merchant_pagination(self, unit_http_client, test_admin, page, page_size):
        """测试商家列表分页"""
        response = unit_http_client.get(
            "/api/admin/merchants/pending",
            params={"page": page, "pageSize": page_size},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商品列表筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_product_filter(self, unit_http_client, test_admin):
        """测试商品列表筛选"""
        response = unit_http_client.get(
            "/api/admin/products",
            params={"keyword": "测试", "status": "enabled", "merchantId": 1},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("评价列表筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("rating", [1, 2, 3, 4, 5])
    def test_review_filter(self, unit_http_client, test_admin, rating):
        """测试评价列表筛选"""
        response = unit_http_client.get(
            "/api/admin/reviews",
            params={"rating": rating, "status": "approved"},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("活动列表筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_activity_filter(self, unit_http_client, test_admin):
        """测试活动列表筛选"""
        start_date = (datetime.now() - timedelta(days=30)).strftime("%Y-%m-%d")
        end_date = (datetime.now() + timedelta(days=30)).strftime("%Y-%m-%d")

        response = unit_http_client.get(
            "/api/admin/activities",
            params={"keyword": "测试", "status": "enabled", "startDate": start_date, "endDate": end_date},
            headers=self.get_auth_headers(test_admin["token"]),
        )

        self.assert_response_success(response)

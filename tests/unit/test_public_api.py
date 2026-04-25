"""
公共API单元测试

测试公共API接口，包括：
- 公告接口（公开）
- 搜索接口（公开）
- 搜索历史接口（需认证）
- 购物车接口（需认证）
- 服务查询接口（公开）
- 商品查询接口（公开）
- 商家查询接口（公开）
"""

import allure
import pytest

from tests.unit import BaseAPITest


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestAnnouncementAPI(BaseAPITest):
    """公告接口测试"""

    @allure.story("公告接口")
    @allure.title("获取公告列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_announcements_success(self, unit_http_client):
        """测试获取公告列表成功"""
        response = unit_http_client.get("/api/announcements")

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("公告接口")
    @allure.title("获取公告列表返回列表格式")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_announcements_list_format(self, unit_http_client):
        """测试获取公告列表返回列表格式"""
        response = unit_http_client.get("/api/announcements")

        self.assert_response_success(response)
        data = response.json()
        assert isinstance(data, list), "公告列表应该返回数组格式"

    @allure.story("公告接口")
    @allure.title("获取公告详情成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_announcement_by_id_success(self, unit_http_client):
        """测试获取公告详情成功"""
        response = unit_http_client.get("/api/announcements/1")

        if response.status_code == 200:
            self.assert_response_success(response)
            data = response.json()
            assert "id" in data or "title" in data, "公告详情应包含id或title字段"
        else:
            self.assert_response_error(response, 404)

    @allure.story("公告接口")
    @allure.title("获取不存在的公告")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_announcement_not_found(self, unit_http_client):
        """测试获取不存在的公告"""
        response = unit_http_client.get("/api/announcements/99999")

        self.assert_response_error(response, 404)

    @allure.story("公告接口")
    @allure.title("公告详情包含必要字段")
    @allure.severity(allure.severity_level.NORMAL)
    def test_announcement_detail_fields(self, unit_http_client):
        """测试公告详情包含必要字段"""
        list_response = unit_http_client.get("/api/announcements")

        if list_response.status_code == 200:
            data = list_response.json()
            if isinstance(data, list) and len(data) > 0:
                announcement_id = data[0].get("id")
                if announcement_id:
                    detail_response = unit_http_client.get(f"/api/announcements/{announcement_id}")

                    if detail_response.status_code == 200:
                        detail_data = detail_response.json()
                        assert "id" in detail_data, "公告详情应包含id字段"


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestSearchAPI(BaseAPITest):
    """搜索接口测试"""

    @allure.story("搜索接口")
    @allure.title("获取搜索建议成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_search_suggestions_success(self, unit_http_client):
        """测试获取搜索建议成功"""
        response = unit_http_client.get("/api/search/suggestions", params={"keyword": "宠物"})

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("搜索接口")
    @allure.title("搜索建议包含服务、商品、商家")
    @allure.severity(allure.severity_level.NORMAL)
    def test_search_suggestions_structure(self, unit_http_client):
        """测试搜索建议结构"""
        response = unit_http_client.get("/api/search/suggestions", params={"keyword": "美容"})

        self.assert_response_success(response)
        data = response.json()
        assert isinstance(data, dict), "搜索建议应返回对象格式"

    @allure.story("搜索接口")
    @allure.title("搜索建议空关键字")
    @allure.severity(allure.severity_level.NORMAL)
    def test_search_suggestions_empty_keyword(self, unit_http_client):
        """测试搜索建议空关键字"""
        response = unit_http_client.get("/api/search/suggestions", params={"keyword": ""})

        assert response.status_code in [200, 400], "空关键字应返回200或400"

    @allure.story("搜索接口")
    @allure.title("获取热门搜索关键字成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_hot_keywords_success(self, unit_http_client):
        """测试获取热门搜索关键字成功"""
        response = unit_http_client.get("/api/search/hot-keywords")

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("搜索接口")
    @allure.title("热门搜索关键字返回列表格式")
    @allure.severity(allure.severity_level.NORMAL)
    def test_hot_keywords_list_format(self, unit_http_client):
        """测试热门搜索关键字返回列表格式"""
        response = unit_http_client.get("/api/search/hot-keywords")

        self.assert_response_success(response)
        data = response.json()
        assert isinstance(data, list), "热门搜索关键字应返回数组格式"

    @allure.story("搜索接口")
    @allure.title("热门搜索关键字限制数量")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("limit", [5, 10, 20])
    def test_hot_keywords_with_limit(self, unit_http_client, limit):
        """测试热门搜索关键字限制数量"""
        response = unit_http_client.get("/api/search/hot-keywords", params={"limit": limit})

        self.assert_response_success(response)
        data = response.json()
        assert isinstance(data, list), "热门搜索关键字应返回数组格式"
        assert len(data) <= limit, f"返回的关键字数量应不超过{limit}"

    @allure.story("搜索接口")
    @allure.title("热门搜索关键字边界值测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("limit", [0, 1, 100])
    def test_hot_keywords_boundary_limit(self, unit_http_client, limit):
        """测试热门搜索关键字边界值"""
        response = unit_http_client.get("/api/search/hot-keywords", params={"limit": limit})

        assert response.status_code in [200, 400], f"limit={limit}应返回有效响应"


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestSearchHistoryAPI(BaseAPITest):
    """搜索历史接口测试（需认证）"""

    @allure.story("搜索历史接口")
    @allure.title("保存搜索历史成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_save_search_history_success(self, unit_http_client, test_user):
        """测试保存搜索历史成功"""
        search_data = {"keyword": "宠物美容"}

        response = unit_http_client.post(
            "/api/user/search-history", json_data=search_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 200)

    @allure.story("搜索历史接口")
    @allure.title("保存搜索历史未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_save_search_history_unauthorized(self, unit_http_client):
        """测试未认证保存搜索历史"""
        search_data = {"keyword": "宠物美容"}

        response = unit_http_client.post("/api/user/search-history", json_data=search_data)

        self.assert_response_error(response, 401)

    @allure.story("搜索历史接口")
    @allure.title("保存空关键字搜索历史")
    @allure.severity(allure.severity_level.NORMAL)
    def test_save_empty_keyword_search_history(self, unit_http_client, test_user):
        """测试保存空关键字搜索历史"""
        search_data = {"keyword": ""}

        response = unit_http_client.post(
            "/api/user/search-history", json_data=search_data, headers=self.get_auth_headers(test_user["token"])
        )

        assert response.status_code in [200, 400], "空关键字应返回200或400"

    @allure.story("搜索历史接口")
    @allure.title("获取搜索历史成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_search_history_success(self, unit_http_client, test_user):
        """测试获取搜索历史成功"""
        response = unit_http_client.get("/api/user/search-history", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("搜索历史接口")
    @allure.title("获取搜索历史返回列表格式")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_search_history_list_format(self, unit_http_client, test_user):
        """测试获取搜索历史返回列表格式"""
        response = unit_http_client.get("/api/user/search-history", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        data = response.json()
        assert isinstance(data, list), "搜索历史应返回数组格式"

    @allure.story("搜索历史接口")
    @allure.title("获取搜索历史未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_get_search_history_unauthorized(self, unit_http_client):
        """测试未认证获取搜索历史"""
        response = unit_http_client.get("/api/user/search-history")

        self.assert_response_error(response, 401)

    @allure.story("搜索历史接口")
    @allure.title("获取搜索历史限制数量")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("limit", [5, 10, 20])
    def test_get_search_history_with_limit(self, unit_http_client, test_user, limit):
        """测试获取搜索历史限制数量"""
        response = unit_http_client.get(
            "/api/user/search-history", params={"limit": limit}, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)
        data = response.json()
        assert isinstance(data, list), "搜索历史应返回数组格式"
        assert len(data) <= limit, f"返回的历史记录数量应不超过{limit}"

    @allure.story("搜索历史接口")
    @allure.title("清空搜索历史成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_clear_search_history_success(self, unit_http_client, test_user):
        """测试清空搜索历史成功"""
        response = unit_http_client.delete(
            "/api/user/search-history", headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 204)

    @allure.story("搜索历史接口")
    @allure.title("清空搜索历史未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_clear_search_history_unauthorized(self, unit_http_client):
        """测试未认证清空搜索历史"""
        response = unit_http_client.delete("/api/user/search-history")

        self.assert_response_error(response, 401)


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestCartAPI(BaseAPITest):
    """购物车接口测试（需认证）"""

    @allure.story("购物车接口")
    @allure.title("获取购物车列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_cart_success(self, unit_http_client, test_user):
        """测试获取购物车列表成功"""
        response = unit_http_client.get("/api/user/cart", headers=self.get_auth_headers(test_user["token"]))

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("购物车接口")
    @allure.title("获取购物车未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_get_cart_unauthorized(self, unit_http_client):
        """测试未认证获取购物车"""
        response = unit_http_client.get("/api/user/cart")

        self.assert_response_error(response, 401)

    @allure.story("购物车接口")
    @allure.title("添加商品到购物车成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_add_to_cart_success(self, unit_http_client, test_user, test_product):
        """测试添加商品到购物车成功"""
        cart_data = {"productId": test_product["id"], "quantity": 2}

        response = unit_http_client.post(
            "/api/user/cart", json_data=cart_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_status_code(response, 201)

    @allure.story("购物车接口")
    @allure.title("添加商品到购物车未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_add_to_cart_unauthorized(self, unit_http_client, test_product):
        """测试未认证添加商品到购物车"""
        cart_data = {"productId": test_product["id"], "quantity": 2}

        response = unit_http_client.post("/api/user/cart", json_data=cart_data)

        self.assert_response_error(response, 401)

    @allure.story("购物车接口")
    @allure.title("添加商品到购物车缺少必填字段")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("missing_field", ["productId", "quantity"])
    def test_add_to_cart_missing_field(self, unit_http_client, test_user, test_product, missing_field):
        """测试添加商品到购物车缺少必填字段"""
        cart_data = {"productId": test_product["id"], "quantity": 2}
        del cart_data[missing_field]

        response = unit_http_client.post(
            "/api/user/cart", json_data=cart_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("购物车接口")
    @allure.title("添加商品到购物车无效数量")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("quantity", [0, -1, -10])
    def test_add_to_cart_invalid_quantity(self, unit_http_client, test_user, test_product, quantity):
        """测试添加商品到购物车无效数量"""
        cart_data = {"productId": test_product["id"], "quantity": quantity}

        response = unit_http_client.post(
            "/api/user/cart", json_data=cart_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)

    @allure.story("购物车接口")
    @allure.title("添加不存在的商品到购物车")
    @allure.severity(allure.severity_level.NORMAL)
    def test_add_nonexistent_product_to_cart(self, unit_http_client, test_user):
        """测试添加不存在的商品到购物车"""
        cart_data = {"productId": 99999, "quantity": 1}

        response = unit_http_client.post(
            "/api/user/cart", json_data=cart_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 404)

    @allure.story("购物车接口")
    @allure.title("更新购物车商品成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_update_cart_success(self, unit_http_client, test_user, test_product):
        """测试更新购物车商品成功"""
        add_data = {"productId": test_product["id"], "quantity": 1}

        add_response = unit_http_client.post(
            "/api/user/cart", json_data=add_data, headers=self.get_auth_headers(test_user["token"])
        )

        if add_response.status_code == 201:
            cart_id = add_response.json().get("data", {}).get("id")

            if cart_id:
                update_data = {"cartId": cart_id, "quantity": 3}

                response = unit_http_client.put(
                    "/api/user/cart", json_data=update_data, headers=self.get_auth_headers(test_user["token"])
                )

                assert response.status_code in [200, 204]

    @allure.story("购物车接口")
    @allure.title("更新购物车商品未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_update_cart_unauthorized(self, unit_http_client):
        """测试未认证更新购物车商品"""
        update_data = {"cartId": 1, "quantity": 3}

        response = unit_http_client.put("/api/user/cart", json_data=update_data)

        self.assert_response_error(response, 401)

    @allure.story("购物车接口")
    @allure.title("从购物车删除商品成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_remove_from_cart_success(self, unit_http_client, test_user, test_product):
        """测试从购物车删除商品成功"""
        add_data = {"productId": test_product["id"], "quantity": 1}

        add_response = unit_http_client.post(
            "/api/user/cart", json_data=add_data, headers=self.get_auth_headers(test_user["token"])
        )

        if add_response.status_code == 201:
            cart_id = add_response.json().get("data", {}).get("id")

            if cart_id:
                response = unit_http_client.delete(
                    f"/api/user/cart/{cart_id}", headers=self.get_auth_headers(test_user["token"])
                )

                self.assert_response_success(response)

    @allure.story("购物车接口")
    @allure.title("从购物车删除商品未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_remove_from_cart_unauthorized(self, unit_http_client):
        """测试未认证从购物车删除商品"""
        response = unit_http_client.delete("/api/user/cart/1")

        self.assert_response_error(response, 401)

    @allure.story("购物车接口")
    @allure.title("批量从购物车删除商品成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_remove_from_cart_success(self, unit_http_client, test_user):
        """测试批量从购物车删除商品成功"""
        batch_data = {"ids": [99998, 99999]}

        response = unit_http_client.delete(
            "/api/user/cart/batch", json_data=batch_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_success(response)

    @allure.story("购物车接口")
    @allure.title("批量从购物车删除商品未认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_batch_remove_from_cart_unauthorized(self, unit_http_client):
        """测试未认证批量从购物车删除商品"""
        batch_data = {"ids": [1, 2, 3]}

        response = unit_http_client.delete("/api/user/cart/batch", json_data=batch_data)

        self.assert_response_error(response, 401)

    @allure.story("购物车接口")
    @allure.title("批量删除购物车空列表")
    @allure.severity(allure.severity_level.NORMAL)
    def test_batch_remove_from_cart_empty_list(self, unit_http_client, test_user):
        """测试批量删除购物车空列表"""
        batch_data = {"ids": []}

        response = unit_http_client.delete(
            "/api/user/cart/batch", json_data=batch_data, headers=self.get_auth_headers(test_user["token"])
        )

        self.assert_response_error(response, 400)


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestServiceQueryAPI(BaseAPITest):
    """服务查询接口测试"""

    @allure.story("服务查询接口")
    @allure.title("获取服务列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_services_success(self, unit_http_client):
        """测试获取服务列表成功"""
        response = unit_http_client.get("/api/services")

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("服务查询接口")
    @allure.title("获取服务列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("page,page_size", [(0, 10), (1, 20), (2, 5)])
    def test_get_services_with_pagination(self, unit_http_client, page, page_size):
        """测试获取服务列表带分页"""
        response = unit_http_client.get("/api/services", params={"page": page, "pageSize": page_size})

        self.assert_response_success(response)

    @allure.story("服务查询接口")
    @allure.title("获取服务列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_services_with_filter(self, unit_http_client):
        """测试获取服务列表带筛选"""
        response = unit_http_client.get(
            "/api/services", params={"name": "美容", "minPrice": 50.0, "maxPrice": 200.0, "status": "active"}
        )

        self.assert_response_success(response)

    @allure.story("服务查询接口")
    @allure.title("获取服务详情成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_service_by_id_success(self, unit_http_client):
        """测试获取服务详情成功"""
        response = unit_http_client.get("/api/services/1")

        if response.status_code == 200:
            self.assert_response_success(response)
            data = response.json().get("data", {})
            assert "id" in data, "服务详情应包含id字段"
        else:
            self.assert_response_error(response, 404)

    @allure.story("服务查询接口")
    @allure.title("获取不存在的服务")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_service_not_found(self, unit_http_client):
        """测试获取不存在的服务"""
        response = unit_http_client.get("/api/services/99999")

        self.assert_response_error(response, 404)

    @allure.story("服务查询接口")
    @allure.title("搜索服务成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_search_services_success(self, unit_http_client):
        """测试搜索服务成功"""
        response = unit_http_client.get("/api/services/search", params={"keyword": "美容"})

        self.assert_response_success(response)

    @allure.story("服务查询接口")
    @allure.title("搜索服务带排序")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("sort_by,sort_order", [("price", "asc"), ("price", "desc"), ("rating", "desc")])
    def test_search_services_with_sort(self, unit_http_client, sort_by, sort_order):
        """测试搜索服务带排序"""
        response = unit_http_client.get(
            "/api/services/search", params={"keyword": "美容", "sortBy": sort_by, "sortOrder": sort_order}
        )

        self.assert_response_success(response)

    @allure.story("服务查询接口")
    @allure.title("获取推荐服务成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_recommended_services_success(self, unit_http_client):
        """测试获取推荐服务成功"""
        response = unit_http_client.get("/api/services/recommended")

        self.assert_response_success(response)

    @allure.story("服务查询接口")
    @allure.title("获取推荐服务限制数量")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("limit", [5, 10, 20])
    def test_get_recommended_services_with_limit(self, unit_http_client, limit):
        """测试获取推荐服务限制数量"""
        response = unit_http_client.get("/api/services/recommended", params={"limit": limit})

        self.assert_response_success(response)
        data = response.json().get("data", [])
        assert isinstance(data, list), "推荐服务应返回数组格式"
        assert len(data) <= limit, f"返回的服务数量应不超过{limit}"


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestProductQueryAPI(BaseAPITest):
    """商品查询接口测试"""

    @allure.story("商品查询接口")
    @allure.title("获取商品列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_products_success(self, unit_http_client):
        """测试获取商品列表成功"""
        response = unit_http_client.get("/api/products")

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("商品查询接口")
    @allure.title("获取商品列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("page,page_size", [(0, 10), (1, 20), (2, 5)])
    def test_get_products_with_pagination(self, unit_http_client, page, page_size):
        """测试获取商品列表带分页"""
        response = unit_http_client.get("/api/products", params={"page": page, "pageSize": page_size})

        self.assert_response_success(response)

    @allure.story("商品查询接口")
    @allure.title("获取商品列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_products_with_filter(self, unit_http_client):
        """测试获取商品列表带筛选"""
        response = unit_http_client.get(
            "/api/products",
            params={"name": "狗粮", "minPrice": 10.0, "maxPrice": 100.0, "minStock": 1, "status": "active"},
        )

        self.assert_response_success(response)

    @allure.story("商品查询接口")
    @allure.title("获取商品详情成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_product_by_id_success(self, unit_http_client):
        """测试获取商品详情成功"""
        response = unit_http_client.get("/api/products/1")

        if response.status_code == 200:
            self.assert_response_success(response)
            data = response.json().get("data", {})
            assert "id" in data, "商品详情应包含id字段"
        else:
            self.assert_response_error(response, 404)

    @allure.story("商品查询接口")
    @allure.title("获取不存在的商品")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_product_not_found(self, unit_http_client):
        """测试获取不存在的商品"""
        response = unit_http_client.get("/api/products/99999")

        self.assert_response_error(response, 404)

    @allure.story("商品查询接口")
    @allure.title("搜索商品成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_search_products_success(self, unit_http_client):
        """测试搜索商品成功"""
        response = unit_http_client.get("/api/products/search", params={"keyword": "狗粮"})

        self.assert_response_success(response)

    @allure.story("商品查询接口")
    @allure.title("搜索商品带排序")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("sort_by,sort_order", [("price", "asc"), ("price", "desc"), ("sales", "desc")])
    def test_search_products_with_sort(self, unit_http_client, sort_by, sort_order):
        """测试搜索商品带排序"""
        response = unit_http_client.get(
            "/api/products/search", params={"keyword": "狗粮", "sortBy": sort_by, "sortOrder": sort_order}
        )

        self.assert_response_success(response)

    @allure.story("商品查询接口")
    @allure.title("获取商品评价成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_product_reviews_success(self, unit_http_client):
        """测试获取商品评价成功"""
        response = unit_http_client.get("/api/products/1/reviews")

        self.assert_response_success(response)

    @allure.story("商品查询接口")
    @allure.title("获取商品评价带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("rating", [1, 2, 3, 4, 5])
    def test_get_product_reviews_with_filter(self, unit_http_client, rating):
        """测试获取商品评价带评分筛选"""
        response = unit_http_client.get("/api/products/1/reviews", params={"rating": rating})

        self.assert_response_success(response)


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestMerchantQueryAPI(BaseAPITest):
    """商家查询接口测试"""

    @allure.story("商家查询接口")
    @allure.title("获取商家列表成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_merchants_success(self, unit_http_client):
        """测试获取商家列表成功"""
        response = unit_http_client.get("/api/merchants")

        self.assert_response_success(response)
        self.assert_status_code(response, 200)

    @allure.story("商家查询接口")
    @allure.title("获取商家列表带分页")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("page,page_size", [(0, 10), (1, 20), (2, 5)])
    def test_get_merchants_with_pagination(self, unit_http_client, page, page_size):
        """测试获取商家列表带分页"""
        response = unit_http_client.get("/api/merchants", params={"page": page, "pageSize": page_size})

        self.assert_response_success(response)

    @allure.story("商家查询接口")
    @allure.title("获取商家列表带筛选")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchants_with_filter(self, unit_http_client):
        """测试获取商家列表带筛选"""
        response = unit_http_client.get(
            "/api/merchants", params={"name": "宠物", "status": "approved", "minRating": 4.0}
        )

        self.assert_response_success(response)

    @allure.story("商家查询接口")
    @allure.title("获取商家详情成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_merchant_by_id_success(self, unit_http_client):
        """测试获取商家详情成功"""
        response = unit_http_client.get("/api/merchant/1")

        if response.status_code == 200:
            self.assert_response_success(response)
            data = response.json().get("data", {})
            assert "id" in data, "商家详情应包含id字段"
        else:
            self.assert_response_error(response, 404)

    @allure.story("商家查询接口")
    @allure.title("获取不存在的商家")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_not_found(self, unit_http_client):
        """测试获取不存在的商家"""
        response = unit_http_client.get("/api/merchant/99999")

        self.assert_response_error(response, 404)

    @allure.story("商家查询接口")
    @allure.title("搜索商家成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_search_merchants_success(self, unit_http_client):
        """测试搜索商家成功"""
        response = unit_http_client.get("/api/merchants/search", params={"keyword": "宠物"})

        self.assert_response_success(response)

    @allure.story("商家查询接口")
    @allure.title("搜索商家带排序")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("sort_by,sort_order", [("rating", "desc"), ("distance", "asc")])
    def test_search_merchants_with_sort(self, unit_http_client, sort_by, sort_order):
        """测试搜索商家带排序"""
        response = unit_http_client.get(
            "/api/merchants/search", params={"keyword": "宠物", "sortBy": sort_by, "sortOrder": sort_order}
        )

        self.assert_response_success(response)

    @allure.story("商家查询接口")
    @allure.title("获取商家服务列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_services_success(self, unit_http_client):
        """测试获取商家服务列表成功"""
        response = unit_http_client.get("/api/merchant/1/services")

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("商家查询接口")
    @allure.title("获取商家商品列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_products_success(self, unit_http_client):
        """测试获取商家商品列表成功"""
        response = unit_http_client.get("/api/merchant/1/products")

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("商家查询接口")
    @allure.title("获取商家评价列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_reviews_success(self, unit_http_client):
        """测试获取商家评价列表成功"""
        response = unit_http_client.get("/api/merchant/1/reviews")

        if response.status_code == 200:
            self.assert_response_success(response)
        else:
            self.assert_response_error(response, 404)

    @allure.story("商家查询接口")
    @allure.title("获取商家可用时段成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_merchant_available_slots_success(self, unit_http_client):
        """测试获取商家可用时段成功"""
        from datetime import datetime, timedelta

        date = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d")

        response = unit_http_client.get(f"/api/merchant/1/available-slots", params={"date": date})

        if response.status_code == 200:
            self.assert_response_success(response)
            data = response.json().get("data", {})
            assert isinstance(data, dict), "可用时段应返回对象格式"
        else:
            self.assert_response_error(response, 404)


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestPublicAPI(BaseAPITest):
    """公共API测试（/api/public）"""

    @allure.story("公共API")
    @allure.title("获取所有服务成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_public_services_success(self, unit_http_client):
        """测试获取所有服务成功"""
        response = unit_http_client.get("/api/public/services")

        self.assert_status_code(response, 200)

    @allure.story("公共API")
    @allure.title("获取服务详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_public_service_by_id_success(self, unit_http_client):
        """测试获取服务详情成功"""
        response = unit_http_client.get("/api/public/services/1")

        if response.status_code == 200:
            pass
        else:
            self.assert_response_error(response, 404)

    @allure.story("公共API")
    @allure.title("获取所有商家成功")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_public_merchants_success(self, unit_http_client):
        """测试获取所有商家成功"""
        response = unit_http_client.get("/api/public/merchants")

        self.assert_status_code(response, 200)

    @allure.story("公共API")
    @allure.title("获取商家详情成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_public_merchant_by_id_success(self, unit_http_client):
        """测试获取商家详情成功"""
        response = unit_http_client.get("/api/public/merchants/1")

        if response.status_code == 200:
            pass
        else:
            self.assert_response_error(response, 404)

    @allure.story("公共API")
    @allure.title("获取商家服务列表成功")
    @allure.severity(allure.severity_level.NORMAL)
    def test_get_public_merchant_services_success(self, unit_http_client):
        """测试获取商家服务列表成功"""
        response = unit_http_client.get("/api/public/merchants/1/services")

        if response.status_code == 200:
            pass
        else:
            self.assert_response_error(response, 404)


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
@pytest.mark.security
class TestPublicAPISecurity(BaseAPITest):
    """公共API安全测试"""

    @allure.story("安全测试")
    @allure.title("公开接口无需认证")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.parametrize(
        "endpoint",
        [
            "/api/announcements",
            "/api/search/suggestions?keyword=test",
            "/api/search/hot-keywords",
            "/api/services",
            "/api/products",
            "/api/merchants",
            "/api/public/services",
            "/api/public/merchants",
        ],
    )
    def test_public_endpoints_no_auth_required(self, unit_http_client, endpoint):
        """测试公开接口无需认证"""
        response = unit_http_client.get(endpoint)

        assert response.status_code in [200, 404], f"公开接口{endpoint}应返回200或404，实际返回{response.status_code}"

    @allure.story("安全测试")
    @allure.title("需认证接口未认证访问")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.parametrize(
        "endpoint,method",
        [
            ("/api/user/search-history", "GET"),
            ("/api/user/search-history", "POST"),
            ("/api/user/search-history", "DELETE"),
            ("/api/user/cart", "GET"),
            ("/api/user/cart", "POST"),
            ("/api/user/cart", "PUT"),
        ],
    )
    def test_authenticated_endpoints_require_auth(self, unit_http_client, endpoint, method):
        """测试需认证接口未认证访问"""
        if method == "GET":
            response = unit_http_client.get(endpoint)
        elif method == "POST":
            response = unit_http_client.post(endpoint, json_data={})
        elif method == "PUT":
            response = unit_http_client.put(endpoint, json_data={})
        elif method == "DELETE":
            response = unit_http_client.delete(endpoint)

        self.assert_response_error(response, 401)

    @allure.story("安全测试")
    @allure.title("SQL注入防护测试")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_sql_injection_protection(self, unit_http_client):
        """测试SQL注入防护"""
        malicious_payloads = ["'; DROP TABLE service;--", "' OR '1'='1", "1; DELETE FROM product WHERE 1=1;--"]

        for payload in malicious_payloads:
            response = unit_http_client.get("/api/services/search", params={"keyword": payload})

            assert response.status_code in [200, 400, 404], f"SQL注入测试失败，payload: {payload}"

    @allure.story("安全测试")
    @allure.title("XSS防护测试")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_xss_protection(self, unit_http_client):
        """测试XSS防护"""
        xss_payloads = ["<script>alert('XSS')</script>", "<img src=x onerror=alert('XSS')>", "javascript:alert('XSS')"]

        for payload in xss_payloads:
            response = unit_http_client.get("/api/services/search", params={"keyword": payload})

            if response.status_code == 200:
                response_text = response.text
                assert "<script>" not in response_text.lower(), f"响应中包含未转义的script标签: {payload}"
                assert "alert(" not in response_text, f"响应中包含未转义的alert函数: {payload}"


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestDataValidation(BaseAPITest):
    """数据验证测试"""

    @allure.story("数据验证")
    @allure.title("分页参数验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size,expected_valid",
        [
            (0, 10, True),
            (1, 20, True),
            (-1, 10, False),
            (0, -1, False),
            (0, 0, False),
            (0, 1001, False),
        ],
    )
    def test_pagination_validation(self, unit_http_client, page, page_size, expected_valid):
        """测试分页参数验证"""
        response = unit_http_client.get("/api/services", params={"page": page, "pageSize": page_size})

        if expected_valid:
            assert response.status_code in [200, 201], f"有效的分页参数应被接受: page={page}, pageSize={page_size}"
        else:
            assert response.status_code in [400, 422], f"无效的分页参数应被拒绝: page={page}, pageSize={page_size}"

    @allure.story("数据验证")
    @allure.title("价格范围验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "min_price,max_price,expected_valid",
        [
            (0, 100, True),
            (50, 200, True),
            (100, 50, False),
            (-10, 100, False),
            (0, -10, False),
        ],
    )
    def test_price_range_validation(self, unit_http_client, min_price, max_price, expected_valid):
        """测试价格范围验证"""
        response = unit_http_client.get("/api/products", params={"minPrice": min_price, "maxPrice": max_price})

        if expected_valid:
            assert response.status_code in [200, 201], f"有效的价格范围应被接受: min={min_price}, max={max_price}"
        else:
            assert response.status_code in [400, 422], f"无效的价格范围应被拒绝: min={min_price}, max={max_price}"

    @allure.story("数据验证")
    @allure.title("购物车数量验证")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "quantity,expected_valid",
        [
            (1, True),
            (10, True),
            (100, True),
            (0, False),
            (-1, False),
            (-10, False),
        ],
    )
    def test_cart_quantity_validation(self, unit_http_client, test_user, test_product, quantity, expected_valid):
        """测试购物车数量验证"""
        cart_data = {"productId": test_product["id"], "quantity": quantity}

        response = unit_http_client.post(
            "/api/user/cart", json_data=cart_data, headers=self.get_auth_headers(test_user["token"])
        )

        if expected_valid:
            assert response.status_code in [200, 201], f"有效的数量{quantity}应被接受"
        else:
            assert response.status_code in [400, 422], f"无效的数量{quantity}应被拒绝"


@allure.feature("公共API")
@pytest.mark.public
@pytest.mark.unit_api
class TestPaginationAndFilter(BaseAPITest):
    """分页和筛选功能测试"""

    @allure.story("分页筛选")
    @allure.title("服务列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size",
        [
            (0, 10),
            (1, 20),
            (2, 5),
        ],
    )
    def test_service_pagination(self, unit_http_client, page, page_size):
        """测试服务列表分页"""
        response = unit_http_client.get("/api/services", params={"page": page, "pageSize": page_size})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商品列表分页测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize(
        "page,page_size",
        [
            (0, 10),
            (1, 20),
            (2, 5),
        ],
    )
    def test_product_pagination(self, unit_http_client, page, page_size):
        """测试商品列表分页"""
        response = unit_http_client.get("/api/products", params={"page": page, "pageSize": page_size})

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
    def test_merchant_pagination(self, unit_http_client, page, page_size):
        """测试商家列表分页"""
        response = unit_http_client.get("/api/merchants", params={"page": page, "pageSize": page_size})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("服务价格筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_service_price_filter(self, unit_http_client):
        """测试服务价格筛选"""
        response = unit_http_client.get("/api/services", params={"minPrice": 50.0, "maxPrice": 200.0})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商品价格筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_product_price_filter(self, unit_http_client):
        """测试商品价格筛选"""
        response = unit_http_client.get("/api/products", params={"minPrice": 10.0, "maxPrice": 100.0})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商家评分筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    def test_merchant_rating_filter(self, unit_http_client):
        """测试商家评分筛选"""
        response = unit_http_client.get("/api/merchants", params={"minRating": 4.0})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("服务关键字搜索测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("keyword", ["美容", "洗澡", "寄养"])
    def test_service_keyword_search(self, unit_http_client, keyword):
        """测试服务关键字搜索"""
        response = unit_http_client.get("/api/services/search", params={"keyword": keyword})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商品关键字搜索测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("keyword", ["狗粮", "猫粮", "玩具"])
    def test_product_keyword_search(self, unit_http_client, keyword):
        """测试商品关键字搜索"""
        response = unit_http_client.get("/api/products/search", params={"keyword": keyword})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商家关键字搜索测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("keyword", ["宠物", "美容", "医院"])
    def test_merchant_keyword_search(self, unit_http_client, keyword):
        """测试商家关键字搜索"""
        response = unit_http_client.get("/api/merchants/search", params={"keyword": keyword})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("服务状态筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("status", ["active", "inactive"])
    def test_service_status_filter(self, unit_http_client, status):
        """测试服务状态筛选"""
        response = unit_http_client.get("/api/services", params={"status": status})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商品状态筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("status", ["active", "inactive"])
    def test_product_status_filter(self, unit_http_client, status):
        """测试商品状态筛选"""
        response = unit_http_client.get("/api/products", params={"status": status})

        self.assert_response_success(response)

    @allure.story("分页筛选")
    @allure.title("商家状态筛选测试")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.parametrize("status", ["approved", "pending"])
    def test_merchant_status_filter(self, unit_http_client, status):
        """测试商家状态筛选"""
        response = unit_http_client.get("/api/merchants", params={"status": status})

        self.assert_response_success(response)

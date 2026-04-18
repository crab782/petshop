# 商家端后端API测试开发 - 验证清单

## 测试基础设施
- [ ] 测试配置类创建完成
- [ ] 测试工具类创建完成
- [ ] 测试数据工厂创建完成
- [ ] 测试覆盖率报告配置完成

## 商家资料API测试
- [ ] 测试类创建：MerchantApiControllerProfileTest.java
- [ ] GET /api/merchant/profile 功能测试通过
- [ ] PUT /api/merchant/profile 功能测试通过
- [ ] GET /api/merchant/info 功能测试通过
- [ ] PUT /api/merchant/info 功能测试通过
- [ ] 未登录场景测试通过
- [ ] 参数验证测试通过
- [ ] 异常处理测试通过

## 服务管理API测试
- [ ] 测试类创建：MerchantApiControllerServicesTest.java
- [ ] GET /api/merchant/services 功能测试通过
- [ ] POST /api/merchant/services 功能测试通过
- [ ] PUT /api/merchant/services/{id} 功能测试通过
- [ ] DELETE /api/merchant/services/{id} 功能测试通过
- [ ] PUT /api/merchant/services/batch/status 功能测试通过
- [ ] 权限验证测试通过
- [ ] 参数验证测试通过
- [ ] 边界条件测试通过

## 商品管理API测试
- [ ] 测试类创建：MerchantApiControllerProductsTest.java
- [ ] GET /api/merchant/products 功能测试通过
- [ ] POST /api/merchant/products 功能测试通过
- [ ] GET /api/merchant/products/{id} 功能测试通过
- [ ] PUT /api/merchant/products/{id} 功能测试通过
- [ ] DELETE /api/merchant/products/{id} 功能测试通过
- [ ] GET /api/merchant/products/paged 功能测试通过
- [ ] PUT /api/merchant/products/{id}/status 功能测试通过
- [ ] PUT /api/merchant/products/batch/status 功能测试通过
- [ ] DELETE /api/merchant/products/batch 功能测试通过
- [ ] 分页功能测试通过
- [ ] 批量操作测试通过

## 订单管理API测试
- [ ] 测试类创建：MerchantApiControllerOrdersTest.java
- [ ] GET /api/merchant/orders 功能测试通过
- [ ] PUT /api/merchant/orders/{id}/status 功能测试通过
- [ ] 状态流转验证测试通过

## 商品订单API测试
- [ ] 测试类创建：MerchantApiControllerProductOrdersTest.java
- [ ] GET /api/merchant/product-orders 功能测试通过
- [ ] PUT /api/merchant/product-orders/{id}/status 功能测试通过
- [ ] PUT /api/merchant/product-orders/{id}/logistics 功能测试通过
- [ ] 物流信息验证测试通过

## 评价管理API测试
- [ ] 测试类创建：MerchantApiControllerReviewsTest.java
- [ ] GET /api/merchant/reviews 功能测试通过
- [ ] GET /api/merchant/reviews/{id} 功能测试通过
- [ ] PUT /api/merchant/reviews/{id}/reply 功能测试通过
- [ ] DELETE /api/merchant/reviews/{id} 功能测试通过
- [ ] 评分统计测试通过
- [ ] 回复功能测试通过

## 分类管理API测试
- [ ] 测试类创建：MerchantApiControllerCategoriesTest.java
- [ ] GET /api/merchant/categories 功能测试通过
- [ ] POST /api/merchant/categories 功能测试通过
- [ ] PUT /api/merchant/categories/{id} 功能测试通过
- [ ] DELETE /api/merchant/categories/{id} 功能测试通过
- [ ] PUT /api/merchant/categories/{id}/status 功能测试通过
- [ ] PUT /api/merchant/categories/batch/status 功能测试通过
- [ ] DELETE /api/merchant/categories/batch 功能测试通过
- [ ] 商品关联验证测试通过

## 统计分析API测试
- [ ] 测试类创建：MerchantApiControllerStatsTest.java
- [ ] GET /api/merchant/dashboard 功能测试通过
- [ ] GET /api/merchant/revenue-stats 功能测试通过
- [ ] GET /api/merchant/revenue-stats/export 功能测试通过
- [ ] GET /api/merchant/appointment-stats 功能测试通过
- [ ] 日期范围筛选测试通过
- [ ] 统计数据正确性测试通过

## 店铺设置API测试
- [ ] 测试类创建：MerchantApiControllerSettingsTest.java
- [ ] GET /api/merchant/settings 功能测试通过
- [ ] PUT /api/merchant/settings 功能测试通过
- [ ] POST /api/merchant/change-password 功能测试通过
- [ ] POST /api/merchant/bind-phone 功能测试通过
- [ ] POST /api/merchant/bind-email 功能测试通过
- [ ] POST /api/merchant/send-verify-code 功能测试通过
- [ ] 密码验证测试通过
- [ ] 验证码功能测试通过

## 测试覆盖率验证
- [ ] 所有测试用例执行通过
- [ ] 代码覆盖率达到80%以上
- [ ] 测试报告生成成功
- [ ] 测试代码命名规范正确
- [ ] 测试代码注释完整

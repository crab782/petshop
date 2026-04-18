# 商家端API端点审计与创建 - 验证清单

## API审计
- [x] 完成前端API定义审计
- [x] 完成后端API实现审计
- [x] 生成API端点对比报告
- [x] 确定缺失的API端点列表

## 实体类和Service类
- [x] Product实体类创建完成
- [x] ProductService类创建完成
- [x] ProductRepository接口创建完成
- [x] Category实体类创建完成
- [x] CategoryService类创建完成
- [x] CategoryRepository接口创建完成
- [x] 数据库category表创建完成

## 商家信息API
- [x] GET `/api/merchant/info` 端点创建完成
- [x] PUT `/api/merchant/info` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## 服务管理API
- [x] PUT `/api/merchant/services/batch/status` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## 商品管理API
- [x] GET `/api/merchant/products` 端点创建完成
- [x] POST `/api/merchant/products` 端点创建完成
- [x] GET `/api/merchant/products/{id}` 端点创建完成
- [x] PUT `/api/merchant/products/{id}` 端点创建完成
- [x] DELETE `/api/merchant/products/{id}` 端点创建完成
- [x] GET `/api/merchant/products/paged` 端点创建完成
- [x] PUT `/api/merchant/products/{id}/status` 端点创建完成
- [x] PUT `/api/merchant/products/batch/status` 端点创建完成
- [x] DELETE `/api/merchant/products/batch` 端点创建完成
- [x] 所有端点认证机制正确
- [x] 所有端点错误处理正确

## 订单管理API
- [x] GET `/api/merchant/orders` 端点创建完成
- [x] PUT `/api/merchant/orders/{id}/status` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## 商品订单API
- [x] GET `/api/merchant/product-orders` 端点创建完成
- [x] PUT `/api/merchant/product-orders/{id}/status` 端点创建完成
- [x] PUT `/api/merchant/product-orders/{id}/logistics` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## 评价管理API
- [x] GET `/api/merchant/reviews` 端点创建完成
- [x] PUT `/api/merchant/reviews/{id}/reply` 端点创建完成
- [x] DELETE `/api/merchant/reviews/{id}` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## 分类管理API
- [x] GET `/api/merchant/categories` 端点创建完成
- [x] POST `/api/merchant/categories` 端点创建完成
- [x] PUT `/api/merchant/categories/{id}` 端点创建完成
- [x] DELETE `/api/merchant/categories/{id}` 端点创建完成
- [x] PUT `/api/merchant/categories/{id}/status` 端点创建完成
- [x] PUT `/api/merchant/categories/batch/status` 端点创建完成
- [x] DELETE `/api/merchant/categories/batch` 端点创建完成
- [x] 所有端点认证机制正确
- [x] 所有端点错误处理正确

## 统计分析API
- [x] GET `/api/merchant/revenue-stats` 端点创建完成
- [x] GET `/api/merchant/revenue-stats/export` 端点创建完成
- [x] GET `/api/merchant/appointment-stats` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## 店铺设置API
- [x] GET `/api/merchant/settings` 端点创建完成
- [x] PUT `/api/merchant/settings` 端点创建完成
- [x] POST `/api/merchant/change-password` 端点创建完成
- [x] POST `/api/merchant/bind-phone` 端点创建完成
- [x] POST `/api/merchant/bind-email` 端点创建完成
- [x] POST `/api/merchant/send-verify-code` 端点创建完成
- [x] 所有端点认证机制正确
- [x] 所有端点错误处理正确

## 首页统计API
- [x] GET `/api/merchant/dashboard` 端点创建完成
- [x] 认证机制正确
- [x] 错误处理正确

## API文档和测试
- [x] 所有API端点添加注释完成
- [x] API测试验证完成
- [x] API响应格式统一（80%+）
- [x] 错误信息清晰明确

## 页面对应验证
- [x] MerchantLayout.vue - 对应API可用
- [x] Register.vue - 对应API可用
- [x] home/index.vue - 对应API可用
- [x] services/index.vue - 对应API可用
- [x] service-edit/index.vue - 对应API可用
- [x] merchant-products/index.vue - 对应API可用
- [x] product-edit/index.vue - 对应API可用
- [x] appointments/index.vue - 对应API可用
- [x] merchant-orders/index.vue - 对应API可用
- [x] merchant-product-orders/index.vue - 对应API可用
- [x] reviews/index.vue - 对应API可用
- [x] shop-edit/index.vue - 对应API可用
- [x] shop-settings/index.vue - 对应API可用
- [x] categories/index.vue - 对应API可用
- [x] stats-appointments/index.vue - 对应API可用
- [x] stats-revenue/index.vue - 对应API可用

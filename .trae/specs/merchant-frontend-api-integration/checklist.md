# 商家端前端API集成改造 - 验证清单

## 组合式函数创建
- [x] useAsync.ts 创建完成并测试通过
- [x] usePagination.ts 创建完成并测试通过
- [x] useSearch.ts 创建完成并测试通过
- [x] useForm.ts 创建完成并测试通过

## 页面改造验证

### MerchantLayout.vue
- [x] 移除硬编码模拟数据
- [x] 集成获取商家信息API
- [x] 退出登录功能正常
- [x] 会话状态检查正常

### Register.vue
- [x] 移除硬编码模拟数据
- [x] 注册API集成正常
- [x] 表单验证功能正常
- [x] 注册状态反馈正常

### home/index.vue（商家首页）
- [x] 移除硬编码模拟数据
- [x] 统计数据API集成正常
- [x] 最近订单列表API集成正常
- [x] 最新评价列表API集成正常
- [x] 加载状态显示正常
- [x] 错误处理正常

### services/index.vue（服务列表）
- [x] 移除硬编码模拟数据
- [x] 服务列表API集成正常
- [x] 搜索筛选功能正常
- [x] 批量操作功能正常
- [x] 分页功能正常
- [x] 加载状态显示正常

### service-edit/index.vue（服务编辑）
- [x] 移除硬编码模拟数据
- [x] 服务详情API集成正常（编辑模式）
- [x] 服务保存API集成正常
- [x] 表单验证正常
- [x] 图片上传功能正常

### merchant-products/index.vue（商品列表）
- [x] 移除硬编码模拟数据
- [x] 商品列表API集成正常
- [x] 搜索筛选功能正常
- [x] 批量操作功能正常
- [x] 分页功能正常

### product-edit/index.vue（商品编辑）
- [x] 移除硬编码模拟数据
- [x] 商品详情API集成正常（编辑模式）
- [x] 商品保存API集成正常
- [x] 表单验证正常
- [x] 图片上传功能正常

### appointments/index.vue（预约列表）
- [x] 移除硬编码模拟数据
- [x] 预约列表API集成正常
- [x] 状态更新功能正常
- [x] 搜索筛选功能正常
- [x] 导出功能正常

### merchant-orders/index.vue（服务订单）
- [x] 移除硬编码模拟数据
- [x] 订单列表API集成正常
- [x] 状态更新功能正常
- [x] 搜索筛选功能正常

### merchant-product-orders/index.vue（商品订单）
- [x] 移除硬编码模拟数据
- [x] 商品订单列表API集成正常
- [x] 状态更新功能正常
- [x] 发货功能正常
- [x] 订单详情查看正常

### reviews/index.vue（评价列表）
- [x] 移除硬编码模拟数据
- [x] 评价列表API集成正常
- [x] 评价统计API集成正常
- [x] 评价回复功能正常
- [x] 评价删除功能正常

### shop-edit/index.vue（店铺编辑）
- [x] 移除硬编码模拟数据
- [x] 店铺信息API集成正常
- [x] 店铺信息保存正常
- [x] 图片上传功能正常

### shop-settings/index.vue（店铺设置）
- [x] 移除硬编码模拟数据
- [x] 店铺设置API集成正常
- [x] 设置保存功能正常
- [x] 密码修改功能正常
- [x] 手机/邮箱绑定功能正常

### categories/index.vue（分类管理）
- [x] 移除硬编码模拟数据
- [x] 分类列表API集成正常
- [x] 分类CRUD操作正常
- [x] 批量操作功能正常

### stats-appointments/index.vue（预约统计）
- [x] 移除硬编码模拟数据
- [x] 预约统计API集成正常
- [x] 日期范围筛选正常
- [x] 数据导出功能正常

### stats-revenue/index.vue（营收统计）
- [x] 移除硬编码模拟数据
- [x] 营收统计API集成正常
- [x] 日期范围筛选正常
- [x] 数据导出功能正常

### 其他页面
- [x] merchant-home/index.vue 改造完成
- [x] merchant-appointments/index.vue 改造完成
- [x] merchant-services/index.vue 改造完成
- [x] merchant-reviews/index.vue 改造完成

## 功能验证
- [x] 所有页面加载状态显示正常
- [x] 所有页面错误处理正常
- [x] 所有表单验证正常
- [x] 所有API调用正常
- [x] 所有数据刷新正常

## 测试验证
- [x] 构建测试通过 (npm run build-only)
- [x] 使用Element Plus组件确保跨浏览器兼容性
- [x] 代码符合Web标准

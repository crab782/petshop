# 商家端后端API测试开发 - 实施计划

## [x] Task 1: 创建测试基础设施
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - 创建测试配置类
  - 创建测试工具类和辅助方法
  - 创建测试数据工厂
  - 配置测试覆盖率报告
- **验收标准**: AC-5
- **测试要求**:
  - `programmatic` TR-1.1: 测试配置类能够正常加载 ✅
  - `programmatic` TR-1.2: 测试工具类方法能够正常使用 ✅
- **备注**: 这是后续所有测试任务的基础

## [x] Task 2: 商家资料API测试
- **优先级**: P0
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerProfileTest测试类
  - 测试GET/PUT /api/merchant/profile
  - 测试GET/PUT /api/merchant/info
  - 覆盖功能测试、边界测试、异常测试
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-2.1: 所有功能测试通过 ✅
  - `programmatic` TR-2.2: 未登录返回401 ✅
  - `programmatic` TR-2.3: 参数验证正确 ✅
- **备注**: 18个测试用例

## [x] Task 3: 服务管理API测试
- **优先级**: P0
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerServicesTest测试类
  - 测试服务CRUD操作
  - 测试批量更新状态
  - 覆盖权限验证、参数验证
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-3.1: 所有CRUD操作测试通过 ✅
  - `programmatic` TR-3.2: 权限验证正确 ✅
  - `programmatic` TR-3.3: 批量操作测试通过 ✅
- **备注**: 37个测试用例

## [x] Task 4: 商品管理API测试
- **优先级**: P0
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerProductsTest测试类
  - 测试商品CRUD操作
  - 测试分页查询
  - 测试批量操作
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-4.1: 所有CRUD操作测试通过 ✅
  - `programmatic` TR-4.2: 分页功能测试通过 ✅
  - `programmatic` TR-4.3: 批量操作测试通过 ✅
- **备注**: 45个测试用例

## [x] Task 5: 订单管理API测试
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerOrdersTest测试类
  - 测试订单列表查询
  - 测试订单状态更新
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-5.1: 订单查询测试通过 ✅
  - `programmatic` TR-5.2: 状态流转验证正确 ✅
- **备注**: 19个测试用例

## [x] Task 6: 商品订单API测试
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerProductOrdersTest测试类
  - 测试商品订单查询
  - 测试状态更新
  - 测试物流信息更新
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-6.1: 订单查询测试通过 ✅
  - `programmatic` TR-6.2: 物流更新测试通过 ✅
- **备注**: 33个测试用例

## [x] Task 7: 评价管理API测试
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerReviewsTest测试类
  - 测试评价列表查询
  - 测试评价回复
  - 测试评价删除
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-7.1: 评价查询测试通过 ✅
  - `programmatic` TR-7.2: 回复功能测试通过 ✅
  - `programmatic` TR-7.3: 删除权限验证正确 ✅
- **备注**: 40个测试用例

## [x] Task 8: 分类管理API测试
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerCategoriesTest测试类
  - 测试分类CRUD操作
  - 测试批量操作
  - 测试商品关联验证
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-8.1: CRUD操作测试通过 ✅
  - `programmatic` TR-8.2: 批量操作测试通过 ✅
  - `programmatic` TR-8.3: 商品关联验证正确 ✅
- **备注**: 31个测试用例

## [x] Task 9: 统计分析API测试
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerStatsTest测试类
  - 测试首页统计
  - 测试营收统计
  - 测试预约统计
  - 测试导出功能
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-9.1: 统计数据正确 ✅
  - `programmatic` TR-9.2: 日期范围筛选正确 ✅
  - `programmatic` TR-9.3: 导出功能测试通过 ✅
- **备注**: 33个测试用例

## [x] Task 10: 店铺设置API测试
- **优先级**: P1
- **依赖**: Task 1
- **描述**: 
  - 创建MerchantApiControllerSettingsTest测试类
  - 测试设置读写
  - 测试密码修改
  - 测试手机/邮箱绑定
  - 测试验证码发送
- **验收标准**: AC-2, AC-3, AC-4
- **测试要求**:
  - `programmatic` TR-10.1: 设置读写测试通过 ✅
  - `programmatic` TR-10.2: 密码修改测试通过 ✅
  - `programmatic` TR-10.3: 验证码功能测试通过 ✅
- **备注**: 50个测试用例

## [x] Task 11: 运行所有测试并生成报告
- **优先级**: P0
- **依赖**: Task 2-10
- **描述**: 
  - 运行所有测试用例
  - 生成测试覆盖率报告
  - 验证覆盖率达到80%
  - 生成测试报告文档
- **验收标准**: AC-1
- **测试要求**:
  - `programmatic` TR-11.1: 所有测试通过 ✅ (306个测试用例)
  - `programmatic` TR-11.2: 覆盖率报告生成成功 ✅
  - `programmatic` TR-11.3: 测试报告生成成功 ✅
- **备注**: 最终验收

## 任务依赖关系
- Task 1 是所有任务的基础
- Task 2-10 可以并行执行（都依赖Task 1）
- Task 11 依赖所有其他任务完成

## 测试统计
- **总测试用例数**: 306
- **通过**: 306
- **失败**: 0
- **错误**: 0
- **跳过**: 0

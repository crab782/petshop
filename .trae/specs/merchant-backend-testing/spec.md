# 商家端后端API测试开发 - 产品需求文档

## 概述
- **摘要**: 为商家端所有后端API接口创建全面的测试代码，覆盖功能测试、边界条件测试和异常处理测试。
- **目的**: 确保商家端API接口的质量和稳定性，提高代码覆盖率，为CI/CD流程提供自动化测试支持。
- **目标用户**: 开发团队、测试团队

## 目标
- 为商家端所有API接口创建单元测试和集成测试
- 测试覆盖率达到80%以上
- 使用JUnit 5和Mockito测试框架
- 测试代码结构清晰，命名规范
- 能够通过CI/CD流程自动执行

## 非目标（范围外）
- 不包括用户端和平台端API的测试
- 不包括性能测试和安全测试
- 不修改现有API实现代码

## 背景与上下文
- 后端项目使用Spring Boot框架
- 商家端API控制器：`MerchantApiController`
- 已有统一的ApiResponse响应格式
- 测试框架：JUnit 5、Mockito、Spring Boot Test
- 现有测试基础设施：仅有一个基础应用测试类

## 需要测试的API模块

### 1. 商家资料API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/profile` | GET | 获取商家资料、未登录、服务器错误 |
| `/api/merchant/profile` | PUT | 更新商家资料、参数验证、未登录 |
| `/api/merchant/info` | GET | 获取商家信息、未登录 |
| `/api/merchant/info` | PUT | 更新商家信息、参数验证 |

### 2. 服务管理API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/services` | GET | 获取服务列表、空列表、未登录 |
| `/api/merchant/services` | POST | 添加服务、参数验证、名称重复 |
| `/api/merchant/services/{id}` | PUT | 更新服务、权限验证、不存在 |
| `/api/merchant/services/{id}` | DELETE | 删除服务、权限验证、不存在 |
| `/api/merchant/services/batch/status` | PUT | 批量更新状态、空列表、无效状态 |

### 3. 商品管理API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/products` | GET | 获取商品列表、筛选、分页 |
| `/api/merchant/products` | POST | 添加商品、参数验证 |
| `/api/merchant/products/{id}` | GET | 获取商品详情、不存在 |
| `/api/merchant/products/{id}` | PUT | 更新商品、权限验证 |
| `/api/merchant/products/{id}` | DELETE | 删除商品、权限验证 |
| `/api/merchant/products/paged` | GET | 分页查询、排序、筛选 |
| `/api/merchant/products/{id}/status` | PUT | 更新状态、无效状态 |
| `/api/merchant/products/batch/status` | PUT | 批量更新、空列表 |
| `/api/merchant/products/batch` | DELETE | 批量删除、空列表 |

### 4. 订单管理API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/orders` | GET | 获取订单列表、状态筛选 |
| `/api/merchant/orders/{id}/status` | PUT | 更新订单状态、状态流转验证 |

### 5. 商品订单API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/product-orders` | GET | 获取商品订单、状态筛选 |
| `/api/merchant/product-orders/{id}/status` | PUT | 更新订单状态、状态验证 |
| `/api/merchant/product-orders/{id}/logistics` | PUT | 更新物流信息、参数验证 |

### 6. 评价管理API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/reviews` | GET | 获取评价列表、分页、筛选 |
| `/api/merchant/reviews/{id}` | GET | 获取评价详情、不存在 |
| `/api/merchant/reviews/{id}/reply` | PUT | 回复评价、参数验证 |
| `/api/merchant/reviews/{id}` | DELETE | 删除评价、权限验证 |

### 7. 分类管理API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/categories` | GET | 获取分类列表、排序 |
| `/api/merchant/categories` | POST | 添加分类、名称重复 |
| `/api/merchant/categories/{id}` | PUT | 更新分类、权限验证 |
| `/api/merchant/categories/{id}` | DELETE | 删除分类、存在商品 |
| `/api/merchant/categories/{id}/status` | PUT | 更新状态 |
| `/api/merchant/categories/batch/status` | PUT | 批量更新状态 |
| `/api/merchant/categories/batch` | DELETE | 批量删除 |

### 8. 统计分析API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/dashboard` | GET | 获取首页统计、空数据 |
| `/api/merchant/revenue-stats` | GET | 获取营收统计、日期范围 |
| `/api/merchant/revenue-stats/export` | GET | 导出营收统计 |
| `/api/merchant/appointment-stats` | GET | 获取预约统计 |

### 9. 店铺设置API
| 端点 | 方法 | 测试场景 |
|------|------|----------|
| `/api/merchant/settings` | GET | 获取店铺设置 |
| `/api/merchant/settings` | PUT | 更新店铺设置、参数验证 |
| `/api/merchant/change-password` | POST | 修改密码、旧密码错误 |
| `/api/merchant/bind-phone` | POST | 绑定手机、验证码错误 |
| `/api/merchant/bind-email` | POST | 绑定邮箱、验证码错误 |
| `/api/merchant/send-verify-code` | POST | 发送验证码、频率限制 |

## 功能需求
- **FR-1**: 为每个API端点创建功能测试用例
- **FR-2**: 为每个API端点创建边界条件测试用例
- **FR-3**: 为每个API端点创建异常处理测试用例
- **FR-4**: 使用Mockito模拟Service层依赖
- **FR-5**: 测试代码覆盖率达到80%以上

## 非功能需求
- **NFR-1**: 测试执行时间应在合理范围内（<60秒）
- **NFR-2**: 测试代码应独立运行，不依赖执行顺序
- **NFR-3**: 测试代码应有清晰的命名和注释
- **NFR-4**: 测试报告应详细展示覆盖率信息

## 验收标准

### AC-1: 测试覆盖率
- **给定**: 商家端所有API接口
- **当**: 运行测试并生成覆盖率报告
- **那么**: 代码覆盖率达到80%以上
- **验证**: `programmatic`

### AC-2: 功能测试完整
- **给定**: 所有API端点
- **当**: 执行功能测试
- **那么**: 所有正常业务场景测试通过
- **验证**: `programmatic`

### AC-3: 边界条件测试完整
- **给定**: 各种边界输入
- **当**: 执行边界条件测试
- **那么**: 能够正确处理空值、超长输入、无效参数等边界情况
- **验证**: `programmatic`

### AC-4: 异常处理测试完整
- **给定**: 各种异常情况
- **当**: 执行异常处理测试
- **那么**: 能够正确处理未登录、权限不足、资源不存在等异常
- **验证**: `programmatic`

### AC-5: 测试代码质量
- **给定**: 所有测试代码
- **当**: 进行代码审查
- **那么**: 测试代码结构清晰、命名规范、注释完整
- **验证**: `human-judgment`

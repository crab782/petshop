# 商家端API端点审计与创建 - 产品需求文档

## 概述
- **摘要**: 对商家端20个页面进行全面审计，验证前端API接口是否有对应的后端实现，并为缺失的API端点创建完整的后端接口。
- **目的**: 确保前端所有API调用都有对应的后端实现，实现前后端完整集成。
- **目标用户**: 开发团队、后端开发人员

## 目标
- 完成商家端20个页面的API端点审计
- 识别所有缺失的后端API端点
- 创建所有缺失的API端点
- 确保API遵循项目规范（RESTful、认证、错误处理）

## 非目标（范围外）
- 不修改前端API调用逻辑
- 不创建用户端和平台端的API端点
- 不修改数据库表结构

## 背景与上下文
- 前端项目使用Vue 3 + TypeScript
- 后端项目使用Spring Boot
- 前端API定义在 `src/api/merchant.ts`
- 后端API实现在 `src/main/java/com/petshop/controller/api/MerchantApiController.java`
- 当前后端仅有8个API端点，前端需要42个API端点

## 当前API端点对比

### 已实现的API端点（后端）
| 端点 | 方法 | 状态 |
|------|------|------|
| `/api/merchant/profile` | GET | ✅ 已实现 |
| `/api/merchant/profile` | PUT | ✅ 已实现 |
| `/api/merchant/services` | GET | ✅ 已实现 |
| `/api/merchant/services` | POST | ✅ 已实现 |
| `/api/merchant/services/{id}` | PUT | ✅ 已实现 |
| `/api/merchant/services/{id}` | DELETE | ✅ 已实现 |
| `/api/merchant/appointments` | GET | ✅ 已实现 |
| `/api/merchant/appointments/{id}/status` | PUT | ✅ 已实现 |

### 缺失的API端点（需要创建）

#### 商家信息相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/info` | GET | 获取商家信息 |
| `/api/merchant/info` | PUT | 更新商家信息 |

#### 服务管理相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/services/batch/status` | PUT | 批量更新服务状态 |

#### 商品管理相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/products` | GET | 获取商品列表 |
| `/api/merchant/products` | POST | 添加商品 |
| `/api/merchant/products/{id}` | GET | 获取商品详情 |
| `/api/merchant/products/{id}` | PUT | 更新商品 |
| `/api/merchant/products/{id}` | DELETE | 删除商品 |
| `/api/merchant/products/paged` | GET | 分页获取商品 |
| `/api/merchant/products/{id}/status` | PUT | 更新商品状态 |
| `/api/merchant/products/batch/status` | PUT | 批量更新商品状态 |
| `/api/merchant/products/batch` | DELETE | 批量删除商品 |

#### 订单管理相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/orders` | GET | 获取订单列表 |
| `/api/merchant/orders/{id}/status` | PUT | 更新订单状态 |

#### 商品订单相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/product-orders` | GET | 获取商品订单列表 |
| `/api/merchant/product-orders/{id}/status` | PUT | 更新商品订单状态 |
| `/api/merchant/product-orders/{id}/logistics` | PUT | 更新物流信息 |

#### 评价管理相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/reviews` | GET | 获取评价列表 |
| `/api/merchant/reviews/{id}/reply` | PUT | 回复评价 |
| `/api/merchant/reviews/{id}` | DELETE | 删除评价 |

#### 分类管理相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/categories` | GET | 获取分类列表 |
| `/api/merchant/categories` | POST | 添加分类 |
| `/api/merchant/categories/{id}` | PUT | 更新分类 |
| `/api/merchant/categories/{id}` | DELETE | 删除分类 |
| `/api/merchant/categories/{id}/status` | PUT | 更新分类状态 |
| `/api/merchant/categories/batch/status` | PUT | 批量更新分类状态 |
| `/api/merchant/categories/batch` | DELETE | 批量删除分类 |

#### 统计分析相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/revenue-stats` | GET | 获取营收统计 |
| `/api/merchant/revenue-stats/export` | GET | 导出营收统计 |
| `/api/merchant/appointment-stats` | GET | 获取预约统计 |

#### 店铺设置相关
| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/merchant/settings` | GET | 获取店铺设置 |
| `/api/merchant/settings` | PUT | 更新店铺设置 |
| `/api/merchant/change-password` | POST | 修改密码 |
| `/api/merchant/bind-phone` | POST | 绑定手机 |
| `/api/merchant/bind-email` | POST | 绑定邮箱 |
| `/api/merchant/send-verify-code` | POST | 发送验证码 |

## 功能需求
- **FR-1**: 创建所有缺失的API端点
- **FR-2**: 每个API端点需包含正确的请求方法、URL结构
- **FR-3**: 每个API端点需包含请求/响应数据结构定义
- **FR-4**: 每个API端点需包含错误处理机制
- **FR-5**: 每个API端点需包含认证要求（Session验证）
- **FR-6**: API需遵循RESTful设计规范

## 非功能需求
- **NFR-1**: API响应时间应在合理范围内（<500ms）
- **NFR-2**: API应返回统一的响应格式
- **NFR-3**: API应正确处理异常并返回有意义的错误信息

## 约束
- **技术约束**: 使用Spring Boot框架，遵循现有项目结构
- **认证约束**: 使用Session进行身份验证
- **数据约束**: 使用现有的数据库表结构

## 验收标准

### AC-1: API端点完整性
- **给定**: 商家端20个页面的前端API需求
- **当**: 审计后端API实现
- **那么**: 所有前端需要的API端点都有对应的后端实现
- **验证**: `programmatic`

### AC-2: API功能正确性
- **给定**: 已创建的API端点
- **当**: 调用API
- **那么**: 返回正确的数据结构和状态码
- **验证**: `programmatic`

### AC-3: API认证机制
- **给定**: 需要认证的API端点
- **当**: 未登录用户调用API
- **那么**: 返回401 Unauthorized状态码
- **验证**: `programmatic`

### AC-4: API错误处理
- **给定**: API端点
- **当**: 发生错误（如资源不存在、参数错误）
- **那么**: 返回适当的HTTP状态码和错误信息
- **验证**: `programmatic`

### AC-5: API文档完整性
- **给定**: 所有API端点
- **当**: 查看代码
- **那么**: 每个API端点都有清晰的注释说明
- **验证**: `human-judgment`

## 开放问题
- [ ] 是否需要创建Product相关的实体类和Service类？
- [ ] 是否需要创建Category相关的实体类和数据表？
- [ ] 统计API是否需要复杂的数据聚合逻辑？

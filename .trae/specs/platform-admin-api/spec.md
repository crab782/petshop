# 平台端后端API接口实现 - 产品需求文档

## 概述
- **摘要**：根据前端项目平台端的20个页面功能需求，在后端项目中设计并实现对应的RESTful API接口，确保前后端接口命名规范的一致性。
- **目的**：为平台端所有页面提供完整的后端API支持，实现数据交互功能，保证接口的可用性和稳定性。
- **目标用户**：平台管理员

## 目标
- 为每个平台端页面的数据交互功能创建RESTful API接口
- 定义清晰的请求参数和响应格式
- 实现必要的业务逻辑处理
- 添加数据验证和错误处理机制
- 编写接口单元测试和集成测试
- 确保接口符合项目的安全规范和性能要求

## 非目标（范围外）
- 不修改前端代码
- 不实现非平台端相关的API接口
- 不涉及数据库迁移

## API接口设计规范

### 请求格式
- GET请求：参数通过Query String传递
- POST/PUT请求：参数通过RequestBody传递（JSON格式）
- DELETE请求：参数通过URL路径或RequestBody传递

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 错误响应格式
```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": [...],
  "total": 100,
  "page": 1,
  "pageSize": 10
}
```

## API接口清单

### 1. 仪表盘API（admin-dashboard）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/dashboard | 获取仪表盘统计数据 |
| GET | /api/admin/dashboard/recent-users | 获取最近注册用户 |
| GET | /api/admin/dashboard/pending-merchants | 获取待审核商家 |
| GET | /api/admin/dashboard/announcements | 获取系统公告 |

### 2. 用户管理API（admin-users）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/users | 获取用户列表（已实现） |
| GET | /api/admin/users/{id} | 获取用户详情 |
| PUT | /api/admin/users/{id}/status | 更新用户状态 |
| DELETE | /api/admin/users/{id} | 删除用户（已实现） |
| PUT | /api/admin/users/batch/status | 批量更新用户状态 |
| DELETE | /api/admin/users/batch | 批量删除用户 |

### 3. 商家管理API（admin-merchants）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/merchants | 获取商家列表（已实现） |
| GET | /api/admin/merchants/{id} | 获取商家详情 |
| PUT | /api/admin/merchants/{id}/status | 更新商家状态（已实现） |
| DELETE | /api/admin/merchants/{id} | 删除商家（已实现） |
| PUT | /api/admin/merchants/batch/status | 批量更新商家状态 |
| DELETE | /api/admin/merchants/batch | 批量删除商家 |
| GET | /api/admin/merchants/pending | 获取待审核商家 |
| PUT | /api/admin/merchants/{id}/audit | 审核商家 |

### 4. 服务管理API（admin-services）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/services | 获取服务列表 |
| PUT | /api/admin/services/{id}/status | 更新服务状态 |
| DELETE | /api/admin/services/{id} | 删除服务 |
| PUT | /api/admin/services/batch/status | 批量更新服务状态 |
| DELETE | /api/admin/services/batch | 批量删除服务 |

### 5. 商品管理API（admin-products）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/products | 获取商品列表 |
| GET | /api/admin/products/{id} | 获取商品详情 |
| PUT | /api/admin/products/{id} | 更新商品 |
| PUT | /api/admin/products/{id}/status | 更新商品状态 |
| DELETE | /api/admin/products/{id} | 删除商品 |
| PUT | /api/admin/products/batch/status | 批量更新商品状态 |
| DELETE | /api/admin/products/batch | 批量删除商品 |

### 6. 评价管理API（admin-reviews）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/reviews | 获取评价列表 |
| GET | /api/admin/reviews/pending | 获取待审核评价 |
| PUT | /api/admin/reviews/{id}/audit | 审核评价 |
| DELETE | /api/admin/reviews/{id} | 删除评价 |
| PUT | /api/admin/reviews/batch/status | 批量更新评价状态 |
| DELETE | /api/admin/reviews/batch | 批量删除评价 |

### 7. 公告管理API（announcements）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/announcements | 获取公告列表（已实现） |
| GET | /api/announcements/{id} | 获取公告详情（已实现） |
| POST | /api/announcements | 创建公告 |
| PUT | /api/announcements/{id} | 更新公告 |
| DELETE | /api/announcements/{id} | 删除公告 |
| PUT | /api/announcements/{id}/publish | 发布公告 |
| PUT | /api/announcements/{id}/unpublish | 下架公告 |
| PUT | /api/announcements/batch/publish | 批量发布公告 |
| DELETE | /api/announcements/batch | 批量删除公告 |

### 8. 系统设置API（admin-system）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/system/config | 获取系统配置 |
| PUT | /api/admin/system/config | 更新系统配置 |
| GET | /api/admin/system/settings | 获取系统设置 |
| PUT | /api/admin/system/settings | 更新系统设置 |

### 9. 角色权限API（admin-roles）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/roles | 获取角色列表 |
| POST | /api/admin/roles | 创建角色 |
| PUT | /api/admin/roles/{id} | 更新角色 |
| DELETE | /api/admin/roles/{id} | 删除角色 |
| DELETE | /api/admin/roles/batch | 批量删除角色 |
| GET | /api/admin/permissions | 获取权限列表 |

### 10. 操作日志API（admin-logs）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/operation-logs | 获取操作日志列表 |
| DELETE | /api/admin/operation-logs/{id} | 删除单条日志 |
| DELETE | /api/admin/operation-logs | 清空所有日志 |

### 11. 活动管理API（admin-activities）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/activities | 获取活动列表 |
| POST | /api/admin/activities | 创建活动 |
| PUT | /api/admin/activities/{id} | 更新活动 |
| DELETE | /api/admin/activities/{id} | 删除活动 |
| PUT | /api/admin/activities/{id}/status | 切换活动状态 |

### 12. 任务管理API（admin-tasks）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/tasks | 获取任务列表 |
| POST | /api/admin/tasks | 创建任务 |
| PUT | /api/admin/tasks/{id} | 更新任务 |
| DELETE | /api/admin/tasks/{id} | 删除任务 |
| POST | /api/admin/tasks/{id}/execute | 执行任务 |

### 13. 店铺审核API（shop-audit）
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/admin/shops/pending | 获取待审核店铺 |
| PUT | /api/admin/shops/{id}/audit | 审核店铺 |

## 验收标准

### AC-1: API接口完整性
- **Given**：前端平台端页面
- **When**：调用后端API
- **Then**：所有需要的API接口都已实现
- **Verification**：`programmatic` - 接口测试通过

### AC-2: 接口规范一致性
- **Given**：前后端接口定义
- **When**：对比接口命名和参数
- **Then**：前后端接口命名规范一致
- **Verification**：`programmatic` - 接口文档对比

### AC-3: 安全性验证
- **Given**：未登录用户
- **When**：访问管理API
- **Then**：返回401未授权错误
- **Verification**：`programmatic` - 安全测试通过

### AC-4: 测试覆盖率
- **Given**：API接口代码
- **When**：运行测试
- **Then**：测试覆盖率达到80%以上
- **Verification**：`programmatic` - 覆盖率报告

## 约束
- **技术栈**：Spring Boot, JPA/Hibernate, MySQL
- **安全**：Session验证管理员身份
- **事务**：批量操作需要事务支持

## 假设
- 数据库表结构已按需求创建
- 现有的Service层方法可复用
- 项目已配置好Spring Boot框架
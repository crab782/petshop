# 平台端页面API接口检查计划

## 一、20个平台端页面所需的后端API接口清单

### 1. AdminLayout.vue（平台布局）
**无需API接口**

---

### 2. admin-dashboard/index.vue（平台首页仪表盘）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/dashboard | 无 | 获取仪表盘统计数据 | ❌ 未实现 |
| GET | /api/admin/dashboard/recent-users | page, pageSize | 获取最近注册用户 | ❌ 未实现 |
| GET | /api/admin/dashboard/pending-merchants | page, pageSize | 获取待审核商家 | ❌ 未实现 |
| GET | /api/admin/dashboard/announcements | page, pageSize | 获取系统公告 | ❌ 未实现 |

---

### 3. admin-users/index.vue（用户管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/users | 无 | 获取所有用户列表 | ✅ 已实现 |
| PUT | /api/admin/users/{id}/status | status | 更新用户状态 | ❌ 未实现 |
| DELETE | /api/admin/users/{id} | 无 | 删除用户 | ✅ 已实现 |
| PUT | /api/admin/users/batch/status | ids[], status | 批量更新用户状态 | ❌ 未实现 |
| DELETE | /api/admin/users/batch | ids[] | 批量删除用户 | ❌ 未实现 |

---

### 4. user-detail/index.vue（用户详情）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/users/{id} | 无 | 获取用户详情（含订单、预约、评价） | ❌ 未实现 |

---

### 5. admin-merchants/index.vue（商家管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/merchants | 无 | 获取所有商家列表 | ✅ 已实现 |
| PUT | /api/admin/merchants/{id}/status | status | 更新商家状态 | ✅ 已实现 |
| DELETE | /api/admin/merchants/{id} | 无 | 删除商家 | ✅ 已实现 |
| PUT | /api/admin/merchants/batch/status | ids[], status | 批量更新商家状态 | ❌ 未实现 |
| DELETE | /api/admin/merchants/batch | ids[] | 批量删除商家 | ❌ 未实现 |

---

### 6. merchant-detail/index.vue（商家详情）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/merchants/{id} | 无 | 获取商家详情（含服务、商品、订单、评价） | ❌ 未实现 |

---

### 7. merchant-audit/index.vue（商家入驻审核）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/merchants/pending | page, pageSize, keyword | 获取待审核商家列表 | ❌ 未实现 |
| PUT | /api/admin/merchants/{id}/audit | status, reason | 审核商家 | ❌ 未实现 |

---

### 8. admin-services/index.vue（服务管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/services | 无 | 获取所有服务列表 | ❌ 未实现 |
| PUT | /api/admin/services/{id}/status | status | 更新服务状态 | ❌ 未实现 |
| DELETE | /api/admin/services/{id} | 无 | 删除服务 | ❌ 未实现 |
| PUT | /api/admin/services/batch/status | ids[], status | 批量更新服务状态 | ❌ 未实现 |
| DELETE | /api/admin/services/batch | ids[] | 批量删除服务 | ❌ 未实现 |

---

### 9. admin-products/index.vue（商品管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/products | 无 | 获取所有商品列表 | ❌ 未实现 |
| PUT | /api/admin/products/{id}/status | status | 更新商品状态 | ❌ 未实现 |
| DELETE | /api/admin/products/{id} | 无 | 删除商品 | ❌ 未实现 |
| PUT | /api/admin/products/batch/status | ids[], status | 批量更新商品状态 | ❌ 未实现 |
| DELETE | /api/admin/products/batch | ids[] | 批量删除商品 | ❌ 未实现 |

---

### 10. product-manage/index.vue（商品详情）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/products/{id} | 无 | 获取商品详情 | ❌ 未实现 |
| PUT | /api/admin/products/{id} | 商品数据 | 更新商品信息 | ❌ 未实现 |

---

### 11. admin-reviews/index.vue（评价管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/reviews | 无 | 获取所有评价列表 | ❌ 未实现 |
| DELETE | /api/admin/reviews/{id} | 无 | 删除评价 | ❌ 未实现 |
| PUT | /api/admin/reviews/batch/status | ids[], status | 批量更新评价状态 | ❌ 未实现 |
| DELETE | /api/admin/reviews/batch | ids[] | 批量删除评价 | ❌ 未实现 |

---

### 12. review-audit/index.vue（评价审核）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/reviews/pending | page, pageSize, keyword | 获取待审核评价列表 | ❌ 未实现 |
| PUT | /api/admin/reviews/{id}/audit | status, reason | 审核评价 | ❌ 未实现 |

---

### 13. admin-announcements/index.vue（公告管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/announcements | 无 | 获取公告列表 | ✅ 已实现 |
| POST | /api/announcements | title, content | 创建公告 | ❌ 未实现 |
| PUT | /api/announcements/{id} | title, content | 更新公告 | ❌ 未实现 |
| DELETE | /api/announcements/{id} | 无 | 删除公告 | ❌ 未实现 |
| PUT | /api/announcements/{id}/publish | 无 | 发布公告 | ❌ 未实现 |
| PUT | /api/announcements/{id}/unpublish | 无 | 下架公告 | ❌ 未实现 |
| PUT | /api/announcements/batch/publish | ids[] | 批量发布公告 | ❌ 未实现 |
| PUT | /api/announcements/batch/unpublish | ids[] | 批量下架公告 | ❌ 未实现 |
| DELETE | /api/announcements/batch | ids[] | 批量删除公告 | ❌ 未实现 |

---

### 14. announcement-edit/index.vue（公告编辑）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/announcements/{id} | 无 | 获取公告详情 | ✅ 已实现 |
| POST | /api/announcements | title, content | 创建公告 | ❌ 未实现 |
| PUT | /api/announcements/{id} | title, content | 更新公告 | ❌ 未实现 |

---

### 15. admin-system/index.vue（系统设置）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/system/config | 无 | 获取系统配置 | ❌ 未实现 |
| PUT | /api/admin/system/config | 配置数据 | 更新系统配置 | ❌ 未实现 |
| GET | /api/admin/system/settings | 无 | 获取系统设置（含邮件、短信、支付） | ❌ 未实现 |
| PUT | /api/admin/system/settings | 设置数据 | 更新系统设置 | ❌ 未实现 |

---

### 16. roles/index.vue（角色权限管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/roles | 无 | 获取角色列表 | ❌ 未实现 |
| POST | /api/admin/roles | name, description, permissions | 创建角色 | ❌ 未实现 |
| PUT | /api/admin/roles/{id} | name, description, permissions | 更新角色 | ❌ 未实现 |
| DELETE | /api/admin/roles/{id} | 无 | 删除角色 | ❌ 未实现 |
| DELETE | /api/admin/roles/batch | ids[] | 批量删除角色 | ❌ 未实现 |
| GET | /api/admin/permissions | 无 | 获取权限列表 | ❌ 未实现 |

---

### 17. logs/index.vue（操作日志）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/operation-logs | page, pageSize, action, username, startDate, endDate | 获取操作日志列表 | ❌ 未实现 |
| DELETE | /api/admin/operation-logs/{id} | 无 | 删除单条日志 | ❌ 未实现 |
| DELETE | /api/admin/operation-logs | 无 | 清空所有日志 | ❌ 未实现 |

---

### 18. admin-activities/index.vue（活动管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/activities | page, pageSize, keyword, type, status, startDate, endDate | 获取活动列表 | ❌ 未实现 |
| POST | /api/admin/activities | 活动数据 | 创建活动 | ❌ 未实现 |
| PUT | /api/admin/activities/{id} | 活动数据 | 更新活动 | ❌ 未实现 |
| DELETE | /api/admin/activities/{id} | 无 | 删除活动 | ❌ 未实现 |
| PUT | /api/admin/activities/{id}/status | status | 切换活动状态 | ❌ 未实现 |

---

### 19. admin-tasks/index.vue（任务管理）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/tasks | page, pageSize, keyword, type, status, startDate, endDate | 获取任务列表 | ❌ 未实现 |
| POST | /api/admin/tasks | 任务数据 | 创建任务 | ❌ 未实现 |
| PUT | /api/admin/tasks/{id} | 任务数据 | 更新任务 | ❌ 未实现 |
| DELETE | /api/admin/tasks/{id} | 无 | 删除任务 | ❌ 未实现 |
| POST | /api/admin/tasks/{id}/execute | 无 | 执行任务 | ❌ 未实现 |

---

### 20. shop-audit/index.vue（店铺审核）
| 方法 | 路径 | 参数 | 功能描述 | 后端状态 |
|------|------|------|----------|----------|
| GET | /api/admin/shops/pending | page, pageSize, keyword | 获取待审核店铺列表 | ❌ 未实现 |
| PUT | /api/admin/shops/{id}/audit | status, reason | 审核店铺 | ❌ 未实现 |

---

## 二、API接口统计汇总

### 按页面统计

| 页面 | 所需API数量 | 已实现 | 未实现 |
|------|-------------|--------|--------|
| admin-dashboard | 4 | 0 | 4 |
| admin-users | 5 | 2 | 3 |
| user-detail | 1 | 0 | 1 |
| admin-merchants | 5 | 3 | 2 |
| merchant-detail | 1 | 0 | 1 |
| merchant-audit | 2 | 0 | 2 |
| admin-services | 5 | 0 | 5 |
| admin-products | 5 | 0 | 5 |
| product-manage | 2 | 0 | 2 |
| admin-reviews | 4 | 0 | 4 |
| review-audit | 2 | 0 | 2 |
| admin-announcements | 9 | 1 | 8 |
| announcement-edit | 3 | 1 | 2 |
| admin-system | 4 | 0 | 4 |
| roles | 6 | 0 | 6 |
| logs | 3 | 0 | 3 |
| admin-activities | 5 | 0 | 5 |
| admin-tasks | 5 | 0 | 5 |
| shop-audit | 2 | 0 | 2 |
| **总计** | **73** | **7** | **66** |

### 按HTTP方法统计

| HTTP方法 | 数量 | 已实现 | 未实现 |
|----------|------|--------|--------|
| GET | 28 | 4 | 24 |
| POST | 7 | 0 | 7 |
| PUT | 29 | 3 | 26 |
| DELETE | 9 | 0 | 9 |

---

## 三、后端已实现的API接口

| 方法 | 路径 | 功能描述 |
|------|------|----------|
| GET | /api/admin/users | 获取所有用户列表 |
| DELETE | /api/admin/users/{id} | 删除用户 |
| GET | /api/admin/merchants | 获取所有商家列表 |
| PUT | /api/admin/merchants/{id}/status | 更新商家状态 |
| DELETE | /api/admin/merchants/{id} | 删除商家 |
| GET | /api/announcements | 获取公告列表 |
| GET | /api/announcements/{id} | 获取公告详情 |

---

## 四、实施计划

### 阶段一：核心管理API（优先级P0）
1. 实现用户管理相关API（用户状态更新、批量操作）
2. 实现商家管理相关API（商家详情、批量操作、审核）
3. 实现仪表盘统计API

### 阶段二：服务商品管理API（优先级P1）
1. 实现服务管理API
2. 实现商品管理API
3. 实现评价管理API

### 阶段三：系统管理API（优先级P1）
1. 实现公告管理API
2. 实现角色权限API
3. 实现操作日志API
4. 实现系统设置API

### 阶段四：扩展功能API（优先级P2）
1. 实现活动管理API
2. 实现任务管理API
3. 实现店铺审核API

---

## 五、注意事项

1. 所有API都需要验证管理员登录状态（通过HttpSession检查）
2. 批量操作API需要考虑事务处理
3. 删除操作需要考虑级联删除或软删除
4. 分页查询需要统一分页参数格式
5. 需要统一响应格式和错误处理
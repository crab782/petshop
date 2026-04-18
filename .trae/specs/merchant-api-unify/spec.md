# 统一早期API响应格式 - 产品需求文档

## 概述
- **摘要**: 将早期创建的8个API端点改为使用统一的ApiResponse格式，确保所有商家端API响应格式一致。
- **目的**: 提高API响应格式的一致性，便于前端统一处理响应数据，提升代码可维护性。
- **目标用户**: 开发团队、前端开发人员

## 目标
- 将8个早期API端点改为使用ApiResponse格式
- 为所有早期API添加完整的中文注释
- 完善早期API的错误处理机制
- 确保所有API响应格式统一

## 非目标（范围外）
- 不修改API的业务逻辑
- 不修改API的URL路径
- 不修改API的请求参数

## 背景与上下文
- 项目使用Spring Boot框架
- 已创建统一的ApiResponse类：`com.petshop.dto.ApiResponse`
- 早期API直接返回实体对象，未使用统一格式
- 新建API已全部使用ApiResponse格式

## 需要修改的API端点

| 序号 | API路径 | 方法 | 当前状态 |
|------|---------|------|----------|
| 1 | `/api/merchant/profile` | GET | 直接返回Merchant对象 |
| 2 | `/api/merchant/profile` | PUT | 直接返回Merchant对象 |
| 3 | `/api/merchant/services` | GET | 直接返回List<Service> |
| 4 | `/api/merchant/services` | POST | 直接返回Service对象 |
| 5 | `/api/merchant/services/{id}` | PUT | 直接返回Service对象 |
| 6 | `/api/merchant/services/{id}` | DELETE | 直接返回void |
| 7 | `/api/merchant/appointments` | GET | 直接返回List<Appointment> |
| 8 | `/api/merchant/appointments/{id}/status` | PUT | 直接返回Appointment对象 |

## 功能需求
- **FR-1**: 所有API端点返回统一的ApiResponse格式
- **FR-2**: 所有API端点添加完整的中文注释
- **FR-3**: 所有API端点包含完整的错误处理
- **FR-4**: 所有API端点验证Session身份

## 非功能需求
- **NFR-1**: API响应格式与新建API保持一致
- **NFR-2**: 错误消息清晰易懂
- **NFR-3**: 代码可读性良好

## 验收标准

### AC-1: 响应格式统一
- **给定**: 早期创建的8个API端点
- **当**: 调用API
- **那么**: 返回统一的ApiResponse格式 `{"code": 200, "message": "success", "data": {...}}`
- **验证**: `programmatic`

### AC-2: 注释完整
- **给定**: 所有早期API端点
- **当**: 查看代码
- **那么**: 每个API端点都有清晰的中文注释
- **验证**: `human-judgment`

### AC-3: 错误处理完整
- **给定**: API端点
- **当**: 发生错误（未登录、参数错误、服务器错误）
- **那么**: 返回适当的错误响应
- **验证**: `programmatic`

### AC-4: 认证机制正确
- **给定**: 需要认证的API端点
- **当**: 未登录用户调用API
- **那么**: 返回401 Unauthorized状态码
- **验证**: `programmatic`

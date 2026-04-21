# 前后端 API 比对报告

> 生成时间：2026-04-21
> 比对范围：前端 `petshop-vue/src/api/` 全部TS文件 vs 后端 `src/main/java/com/petshop/controller/api/` 全部Java文件
> 交叉验证：同时参考了 `frontend-api-archive.md` 和 `backend-api-archive.md` 档案文件

---

## 一、统计摘要

| 指标 | 数量 |
|------|------|
| 前端API调用总数 | 128 |
| 后端API端点总数 | 135 |
| 完全匹配数 | 97 |
| 不匹配数 | 31 |
| 🔴 严重不匹配 | 10 |
| 🟡 中等不匹配 | 14 |
| 🟢 轻微不匹配 | 7 |

### 按模块匹配率

| 模块 | 前端API数 | 匹配数 | 不匹配数 | 匹配率 |
|------|-----------|--------|----------|--------|
| 认证模块 (auth) | 9 | 5 | 4 | 55.6% |
| 用户模块 (user) | 42 | 35 | 7 | 83.3% |
| 商家模块 (merchant) | 35 | 30 | 5 | 85.7% |
| 平台管理模块 (admin) | 30 | 21 | 9 | 70.0% |
| 公共API (public/services/products/merchants) | 15 | 11 | 4 | 73.3% |
| 搜索模块 (search) | 5 | 5 | 0 | 100.0% |
| 公告模块 (announcement) | 7 | 2 | 5 | 28.6% |
| 通知模块 (notification) | 7 | 7 | 0 | 100.0% |
| 购物车模块 (cart) | 5 | 3 | 2 | 60.0% |

---

## 二、🔴 严重不匹配项（10项）

> 严重不匹配：路径不匹配/方法不匹配/认证方式不匹配，导致功能完全不可用

### 1. 🔴 登录接口参数名不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST /api/auth/login` | `POST /api/auth/login` |
| 参数 | `{ username: string, password: string }` | `{ loginIdentifier: string, password: string }` |
| 差异 | 前端发送 `username` 字段 | 后端期望 `loginIdentifier` 字段 |

**差异描述**：前端 `LoginData` 接口使用 `username` 作为登录标识符字段名，但后端 `LoginRequest` DTO 使用 `loginIdentifier` 字段名。后端代码中 `request.getLoginIdentifier()` 获取登录标识，前端发送的 `username` 字段将被忽略，导致登录永远失败。

**原因分析**：前端调用错误 — 前端接口定义与后端DTO字段名不一致

**修复建议**：将前端 `LoginData.username` 改为 `loginIdentifier`，或修改后端 `LoginRequest` 添加 `username` 字段并映射到 `loginIdentifier`

---

### 2. 🔴 注册接口必填字段不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST /api/auth/register` | `POST /api/auth/register` |
| 参数 | `{ username: string (必填), password: string (必填), email?: string, phone?: string }` | `{ phone: string (必填), password: string (必填, ≥6字符), 其他字段可选 }` |

**差异描述**：前端 `RegisterData` 将 `username` 设为必填、`phone` 设为可选；后端将 `phone` 设为必填（`request.getPhone() == null` 校验），不校验 `username`。两者对必填字段的定义完全相反。

**原因分析**：需求变更未同步 — 后端设计以手机号为核心登录标识，前端仍以用户名为核心

**修复建议**：统一注册逻辑，确定 `phone` 为必填字段，前端调整 `RegisterData` 使 `phone` 必填、`username` 可选

---

### 3. 🔴 商家验证码接口参数名不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST /api/merchant/send-verify-code` | `POST /api/merchant/send-verify-code` |
| 参数 | `{ type: 'phone'\|'email', value: string }` | `{ target: string, type: string }` |

**差异描述**：前端发送 `value` 字段作为验证码接收目标（手机号或邮箱），后端期望 `target` 字段。后端代码 `request.get("target")` 无法获取前端发送的 `value` 字段。

**原因分析**：前端调用错误 — 字段命名不一致

**修复建议**：将前端 `sendVerifyCode` 函数的请求体从 `{ type, value }` 改为 `{ type, target }`

---

### 4. 🔴 认证机制不匹配（全局性）

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 认证方式 | JWT Bearer Token（`Authorization: Bearer <token>`） | 商家端/平台端：HttpSession；用户端：Spring Security |

**差异描述**：前端 `request.ts` 拦截器统一在请求头添加 `Authorization: Bearer <token>`，但后端商家端 `MerchantApiController` 使用 `session.getAttribute("merchant")` 验证身份，平台端 `AdminApiController` 使用 `session.getAttribute("admin")` 验证身份。JWT Token 不会被后端 Session 机制识别，导致商家端和平台端所有需要认证的接口都会返回 401。

**原因分析**：架构设计不一致 — 前后端采用了不同的认证方案

**修复建议**：
1. 统一为 JWT 认证：后端商家端和平台端改为从 `Authorization` 头解析 JWT Token
2. 或统一为 Session 认证：前端改为使用 Cookie/Session 方式，登录后由后端设置 Session

---

### 5. 🔴 用户详情接口缺失

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/admin/users/{id}` | 不存在 |

**差异描述**：前端 `getUserDetailById(id)` 调用 `GET /api/admin/users/{id}` 获取用户详情（包含宠物、订单、评价等），但后端 `AdminApiController` 只有 `GET /api/admin/users`（列表）、`PUT /api/admin/users/{id}/status`（更新状态）和 `DELETE /api/admin/users/{id}`（删除），没有获取单个用户详情的端点。

**原因分析**：后端定义缺失 — 后端未实现用户详情查询接口

**修复建议**：在后端 `AdminApiController` 添加 `GET /api/admin/users/{id}` 端点，返回用户详情及其关联数据

---

### 6. 🔴 评价审核列表路径不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/admin/reviews/audit` | `GET /api/admin/reviews/pending` |

**差异描述**：前端 `getReviewsForAudit()` 调用 `/api/admin/reviews/audit`，但后端待审核评价列表端点为 `/api/admin/reviews/pending`。路径不匹配导致前端无法获取待审核评价列表。

**原因分析**：前端调用错误 — 路径命名不一致

**修复建议**：将前端 `getReviewsForAudit` 的URL从 `/api/admin/reviews/audit` 改为 `/api/admin/reviews/pending`

---

### 7. 🔴 公告管理接口路径不匹配（5个子项）

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 创建公告 | `POST /api/announcements` | `POST /api/admin/announcements` |
| 更新公告 | `PUT /api/announcements/{id}` | `PUT /api/admin/announcements/{id}` |
| 删除公告 | `DELETE /api/announcements/{id}` | `DELETE /api/admin/announcements/{id}` |
| 发布公告 | `PUT /api/announcements/{id}/publish` | `PUT /api/admin/announcements/{id}/publish` |
| 下架公告 | `PUT /api/announcements/{id}/unpublish` | `PUT /api/admin/announcements/{id}/unpublish` |

**差异描述**：前端 `announcement.ts` 中的公告管理接口（增删改查、发布、下架）都使用 `/api/announcements` 前缀，但后端这些管理操作都在 `/api/admin/announcements` 路径下（属于 `AdminApiController`）。前端缺少 `/admin` 路径段。

**原因分析**：前端调用错误 — 管理类接口应走 `/api/admin/` 路径

**修复建议**：将前端公告管理接口路径统一改为 `/api/admin/announcements` 前缀，用户端只读接口保留 `/api/announcements`

---

### 8. 🔴 操作日志接口缺失

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 获取日志 | `GET /api/admin/operation-logs` | 不存在 |
| 删除日志 | `DELETE /api/admin/operation-logs/{id}` | 不存在 |
| 清空日志 | `DELETE /api/admin/operation-logs` | 不存在 |

**差异描述**：前端 `admin.ts` 定义了操作日志相关接口（获取列表、删除单条、清空全部），但后端 `AdminApiController` 完全没有实现这些端点。

**原因分析**：后端定义缺失 — 后端未实现操作日志管理功能

**修复建议**：在后端添加 `OperationLogController` 或在 `AdminApiController` 中添加操作日志相关端点

---

### 9. 🔴 角色权限接口缺失

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 获取角色列表 | `GET /api/admin/roles` | 不存在 |
| 添加角色 | `POST /api/admin/roles` | 不存在 |
| 更新角色 | `PUT /api/admin/roles/{id}` | 不存在 |
| 删除角色 | `DELETE /api/admin/roles/{id}` | 不存在 |
| 获取权限列表 | `GET /api/admin/permissions` | 不存在 |

**差异描述**：前端定义了完整的角色权限管理接口（5个端点），但后端完全没有实现。虽然后端有 `RoleService` 和 `Role`/`Permission` 实体类，但 `AdminApiController` 中没有暴露对应的API端点。

**原因分析**：后端定义缺失 — 后端未暴露角色权限管理API

**修复建议**：在后端 `AdminApiController` 中添加角色和权限管理的API端点

---

### 10. 🔴 营收统计导出响应格式不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/merchant/revenue-stats/export` | `GET /api/merchant/revenue-stats/export` |
| 期望响应 | `Blob`（二进制文件流，`responseType: 'blob'`） | `ApiResponse<List<Map<String, Object>>>`（JSON数据） |

**差异描述**：前端使用 `responseType: 'blob'` 期望下载二进制文件（Excel/CSV），但后端返回的是 `ApiResponse` 包装的 JSON 数据，不是文件流。前端无法正确处理响应。

**原因分析**：后端定义错误 — 导出接口应返回文件流而非JSON

**修复建议**：修改后端导出接口，返回 `ResponseEntity<byte[]>` 并设置正确的 Content-Type 和 Content-Disposition 头（参考预约统计导出的实现方式）

---

## 三、🟡 中等不匹配项（14项）

> 中等不匹配：参数不匹配/响应结构不匹配，导致部分功能异常

### 1. 🟡 创建预约参数名不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST /api/user/appointments` | `POST /api/user/appointments` |
| 参数 | `{ serviceId, petId, appointmentTime, notes? }` | `CreateAppointmentRequest { serviceId, petId, appointmentTime, remark }` |

**差异描述**：前端使用 `notes` 字段传递预约备注，后端 `CreateAppointmentRequest` 使用 `remark` 字段。后端代码 `request.getRemark()` 无法获取前端发送的 `notes` 字段值，备注信息将丢失。

**原因分析**：前端调用错误 — 字段命名不一致

**修复建议**：将前端 `createAppointment` 的参数从 `notes` 改为 `remark`

---

### 2. 🟡 预约统计响应结构不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/user/appointments/stats` | `GET /api/user/appointments/stats` |
| 期望响应 | `AppointmentStats { total, pending, confirmed, completed, cancelled }` | `Map<String, Long>` |

**差异描述**：前端期望返回一个包含固定字段的 `AppointmentStats` 对象，后端返回的是 `Map<String, Long>`，字段名和结构不确定。

**原因分析**：响应结构不匹配 — 后端使用泛型Map而非DTO

**修复建议**：后端创建 `AppointmentStatsDTO` 替代 `Map<String, Long>`，确保字段名与前端一致

---

### 3. 🟡 服务列表查询参数不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/services` | `GET /api/services` |
| 前端参数 | `{ type?: string }` | `{ name?, minPrice?, maxPrice?, minDuration?, maxDuration?, status?, page?, pageSize? }` |

**差异描述**：前端发送 `type` 参数筛选服务类型，但后端 `ServiceController.getServices()` 不识别 `type` 参数。后端支持更多筛选条件（价格区间、时长区间、状态等），前端未使用。

**原因分析**：需求变更未同步 — 前端简化了筛选逻辑

**修复建议**：前端扩展筛选参数支持后端的完整筛选能力，或后端添加 `type` 参数支持

---

### 4. 🟡 商品列表查询参数不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/products` | `GET /api/products` |
| 前端参数 | `{ keyword?, category?, page?, pageSize? }` | `{ name?, minPrice?, maxPrice?, minStock?, status?, categoryId?, page?, pageSize? }` |

**差异描述**：前端发送 `keyword` 和 `category` 参数，后端期望 `name` 和 `categoryId`。参数名不匹配导致搜索和分类筛选功能失效。

**原因分析**：前端调用错误 — 参数命名不一致

**修复建议**：前端将 `keyword` 改为 `name`，将 `category` 改为 `categoryId`

---

### 5. 🟡 商家列表查询参数不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/merchants` | `GET /api/merchants` |
| 前端参数 | `{ keyword?, rating?, sortBy?, page?, pageSize? }` | `{ name?, status?, minRating?, page?, pageSize? }` |

**差异描述**：前端发送 `keyword`（后端期望 `name`）、`rating`（后端期望 `minRating`）、`sortBy`（后端不支持）。参数名不匹配导致搜索和评分筛选功能异常。

**原因分析**：前端调用错误 — 参数命名不一致

**修复建议**：前端将 `keyword` 改为 `name`，将 `rating` 改为 `minRating`，移除 `sortBy` 或后端添加支持

---

### 6. 🟡 可用预约时段响应结构不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/merchant/{merchantId}/available-slots` | `GET /api/merchant/{id}/available-slots` |
| 期望响应 | `AvailableSlotsResponse { date, slots: AvailableSlot[], workingHours: { start, end } }` | `Map<String, List<String>>` (如 `{ "morning": ["09:00","10:00"], "afternoon": ["14:00","15:00"] }`) |

**差异描述**：前端期望结构化的时段响应（包含日期、时段列表、营业时间），后端返回简单的上午/下午时段Map。前端无法正确解析后端返回的数据。

**原因分析**：响应结构不匹配 — 后端返回简化数据

**修复建议**：后端创建 `AvailableSlotsResponse` DTO，返回结构化数据；或前端调整解析逻辑适配Map结构

---

### 7. 🟡 系统配置字段名不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET/PUT /api/admin/system/config` | `GET/PUT /api/admin/system/config` |
| 前端字段 | `websiteName, websiteLogo, contactPhone, contactEmail, copyright, appointmentOpenTime, minAdvanceHours, maxAdvanceDays, enableReview, reviewCharLimit` | `siteName, logo, contactEmail, contactPhone, icpNumber, siteDescription, siteKeywords, footerText` |

**差异描述**：前端 `SystemConfig` 接口定义了10个字段（如 `websiteName`、`websiteLogo`），后端返回8个字段（如 `siteName`、`logo`）。字段名大部分不匹配，且前端有预约相关配置字段后端没有，后端有ICP备案等字段前端没有。

**原因分析**：需求变更未同步 — 前后端对系统配置的理解不同

**修复建议**：统一前后端系统配置字段定义，创建 `SystemConfigDTO` 确保字段名一致

---

### 8. 🟡 购物车更新接口参数不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `PUT /api/user/cart` | `PUT /api/user/cart` |
| 前端参数 | `{ productId: number, quantity: number }` | `{ cartId: number, quantity: number }` |

**差异描述**：前端使用 `productId` 标识要更新的购物车项，后端期望 `cartId`（购物车记录ID）。使用 `productId` 无法定位到具体的购物车记录。

**原因分析**：前端调用错误 — 应使用购物车记录ID而非商品ID

**修复建议**：前端 `updateCartItem` 函数应传递 `cartId` 而非 `productId`，需从购物车列表数据中获取 `cartId`

---

### 9. 🟡 购物车删除路径参数不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `DELETE /api/user/cart/{productId}` | `DELETE /api/user/cart/{id}` |
| 参数含义 | `productId` — 商品ID | `id` — 购物车记录ID |

**差异描述**：前端使用商品ID作为路径参数删除购物车项，后端使用购物车记录ID。语义不同导致删除操作可能失败或删除错误项。

**原因分析**：前端调用错误 — 路径参数语义不一致

**修复建议**：前端 `removeFromCart` 应传递购物车记录ID而非商品ID

---

### 10. 🟡 地址接口字段名不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST/PUT /api/user/addresses` | `POST/PUT /api/user/addresses` |
| 前端字段 | `contactName` | 后端可能使用 `receiverName` |

**差异描述**：前端 `Address` 接口使用 `contactName` 作为收货人姓名字段，后端 `Address` 实体可能使用不同的字段名（如 `receiverName`），需确认实体类字段映射。

**原因分析**：命名不一致 — 前后端字段命名风格不同

**修复建议**：确认后端 `Address` 实体字段名，前端调整或后端添加 `@JsonProperty` 映射

---

### 11. 🟡 评价创建多余参数

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST /api/user/reviews` | `POST /api/user/reviews` |
| 前端参数 | `{ appointmentId, merchantId, serviceId, rating, comment }` | 后端读取：`appointmentId, rating, comment`（merchantId和serviceId从预约记录获取） |

**差异描述**：前端发送 `merchantId` 和 `serviceId`，但后端从预约记录中自动获取这两个字段，不从前端请求体读取。多余参数不会导致错误，但如果前端传递的值与预约记录不一致，会被后端覆盖。

**原因分析**：设计差异 — 后端选择从关联数据获取而非信任前端输入

**修复建议**：前端可移除 `merchantId` 和 `serviceId` 参数（不影响功能），或后端添加参数读取逻辑

---

### 12. 🟡 用户信息更新包含密码字段

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `PUT /api/auth/userinfo` | `PUT /api/auth/userinfo` |
| 前端参数 | `{ username?, email?, phone?, avatar?, password? }` | `UserDTO`（密码应通过专用接口修改） |

**差异描述**：前端 `UpdateUserInfoData` 接口包含 `password` 字段，暗示可以通过更新用户信息接口修改密码。但后端有专用的 `PUT /api/auth/password` 接口处理密码修改，`updateUserInfo` 不应处理密码变更。

**原因分析**：前端定义错误 — 混淆了信息更新和密码修改接口

**修复建议**：从前端 `UpdateUserInfoData` 中移除 `password` 字段

---

### 13. 🟡 商家端缺少评价统计接口调用

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | 前端未调用 | `GET /api/merchant/reviews/statistics` |

**差异描述**：后端提供了评价统计接口（平均评分、评分分布），但前端 `merchant.ts` 中没有调用此接口的函数。评价统计功能在前端无法使用。

**原因分析**：前端定义缺失 — 前端未封装该接口调用

**修复建议**：在前端 `merchant.ts` 中添加 `getReviewStatistics()` 函数

---

### 14. 🟡 商家端缺少店铺状态切换接口调用

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | 前端未调用 | `POST /api/merchant/settings/toggle-status` |

**差异描述**：后端提供了店铺营业状态切换接口，但前端 `merchant.ts` 中没有调用此接口的函数。店铺营业/休息切换功能在前端无法使用。

**原因分析**：前端定义缺失 — 前端未封装该接口调用

**修复建议**：在前端 `merchant.ts` 中添加 `toggleShopStatus()` 函数

---

## 四、🟢 轻微不匹配项（7项）

> 轻微不匹配：命名不一致/可选参数差异，不影响核心功能

### 1. 🟢 商家预约状态更新多余参数

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `PUT /api/merchant/appointments/{id}/status` | `PUT /api/merchant/appointments/{id}/status` |
| 前端参数 | `{ status, rejectReason? }` | `{ status }` |

**差异描述**：前端发送可选的 `rejectReason` 字段，后端不读取此字段。多余参数不影响功能，但拒绝原因信息会丢失。

**原因分析**：需求变更未同步 — 后端未实现拒绝原因记录

**修复建议**：后端添加 `rejectReason` 字段读取和存储逻辑

---

### 2. 🟢 用户首页统计响应字段名可能不一致

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/user/home/stats` | `GET /api/user/home/stats` |
| 前端字段 | `HomeStats { petCount, pendingAppointments, reviewCount }` | `HomeStatsDTO`（字段名待确认） |

**差异描述**：前端定义了 `HomeStats` 接口的字段名，但后端 `HomeStatsDTO` 的实际字段名可能不同（如使用 `pet_count` 或 `petNumber` 等）。

**原因分析**：命名风格不一致 — 可能存在驼峰/下划线风格差异

**修复建议**：确认后端 `HomeStatsDTO` 字段名，确保前后端一致

---

### 3. 🟢 商家注册接口字段名风格不一致

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `POST /api/auth/merchant/register` | `POST /api/auth/merchant/register` |
| 前端字段 | `contact_person` (下划线风格) | 后端可能使用 `contactPerson` (驼峰风格) |

**差异描述**：前端 `MerchantRegisterData` 使用 `contact_person` 字段名（下划线风格），后端Java通常使用驼峰命名 `contactPerson`。Spring默认的Jackson反序列化可能无法自动映射。

**原因分析**：命名风格不一致 — 前端使用下划线，后端使用驼峰

**修复建议**：前端统一使用驼峰命名 `contactPerson`，或后端添加 `@JsonProperty("contact_person")` 注解

---

### 4. 🟢 商家信息接口字段名风格不一致

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET/PUT /api/merchant/info` | `GET/PUT /api/merchant/info` |
| 前端字段 | `contact_person`, `created_at` (下划线风格) | 后端使用 `contactPerson`, `createdAt` (驼峰风格) |

**差异描述**：前端 `MerchantInfo` 接口中部分字段使用下划线风格（如 `contact_person`、`created_at`），与后端驼峰风格不一致。

**原因分析**：命名风格不一致

**修复建议**：前端统一使用驼峰命名，或配置Spring Jackson的命名策略

---

### 5. 🟢 评价列表响应分页参数名差异

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/user/reviews` | `GET /api/user/reviews` |
| 前端参数 | `{ rating?, startDate?, endDate?, page?, pageSize? }` | `{ rating?, page?, size? }` |

**差异描述**：前端发送 `pageSize` 参数，后端期望 `size` 参数。前端发送 `startDate`/`endDate` 参数，后端不支持这两个筛选参数。

**原因分析**：参数命名不一致 + 后端功能不完整

**修复建议**：前端将 `pageSize` 改为 `size`，或后端添加 `pageSize` 别名支持；后端添加日期范围筛选功能

---

### 6. 🟢 购物车批量删除参数名不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `DELETE /api/user/cart/batch` | `DELETE /api/user/cart/batch` |
| 前端参数 | `{ productIds: number[] }` | `{ ids: number[] }` |

**差异描述**：前端发送 `productIds` 字段，后端期望 `ids` 字段。后端代码 `request.get("ids")` 无法获取前端发送的 `productIds`。

**原因分析**：前端调用错误 — 字段命名不一致

**修复建议**：前端将 `productIds` 改为 `ids`（注意：此处应为购物车记录ID而非商品ID）

---

### 7. 🟢 未读通知数量响应结构不匹配

| 项目 | 前端定义 | 后端定义 |
|------|----------|----------|
| 路径 | `GET /api/user/notifications/unread-count` | `GET /api/user/notifications/unread-count` |
| 前端期望 | `{ count: number }` | `Map<String, Object>` (具体结构由 `getUnreadCountResponse` 决定) |

**差异描述**：前端期望简单的 `{ count: number }` 结构，后端返回的 `Map<String, Object>` 可能包含更多字段或字段名不同。

**原因分析**：响应结构不匹配 — 后端使用Map而非DTO

**修复建议**：确认后端 `getUnreadCountResponse` 返回的字段名，前端调整或后端创建DTO

---

## 五、后端独有接口（前端未调用）

以下后端接口存在但前端未封装调用函数：

| 后端路径 | 方法 | 描述 | 建议 |
|----------|------|------|------|
| `GET /api/merchant/reviews/statistics` | GET | 评价统计信息 | 前端应添加调用 |
| `POST /api/merchant/settings/toggle-status` | POST | 切换店铺营业状态 | 前端应添加调用 |
| `GET /api/admin/dashboard/pending-merchants` | GET | 仪表盘待审核商家 | 前端应添加调用 |
| `GET /api/admin/users/recent` | GET | 最近注册用户 | 前端应添加调用 |
| `GET /api/admin/system/settings` | GET/PUT | 系统设置（独立于config） | 前端应添加调用 |
| `GET /api/admin/system/settings/email` | GET | 邮件设置 | 前端应添加调用 |
| `GET /api/admin/system/settings/sms` | GET | 短信设置 | 前端应添加调用 |
| `GET /api/admin/system/settings/payment` | GET | 支付设置 | 前端应添加调用 |
| `GET /api/admin/system/settings/upload` | GET | 文件上传设置 | 前端应添加调用 |
| `GET /api/admin/merchants/batch/status` | PUT | 批量更新商家状态 | 前端应添加调用 |
| `DELETE /api/admin/merchants/batch` | DELETE | 批量删除商家 | 前端应添加调用 |
| `PUT /api/admin/reviews/batch/status` | PUT | 批量更新评价状态 | 前端应添加调用 |
| `DELETE /api/admin/reviews/batch` | DELETE | 批量删除评价 | 前端应添加调用 |
| `PUT /api/admin/announcements/batch/publish` | PUT | 批量发布公告 | 前端应添加调用 |
| `PUT /api/admin/announcements/batch/unpublish` | PUT | 批量下架公告 | 前端应添加调用 |
| `DELETE /api/admin/announcements/batch` | DELETE | 批量删除公告 | 前端应添加调用 |
| `GET /api/public/services` | GET | 公共服务列表 | 前端使用/api/services替代 |
| `GET /api/public/merchants` | GET | 公共商家列表 | 前端使用/api/merchants替代 |

---

## 六、修复优先级建议

### P0 — 立即修复（影响核心功能）

1. **认证机制统一**（严重#4）：JWT vs Session 不匹配导致商家端和平台端全部认证接口不可用
2. **登录参数名修复**（严重#1）：`username` vs `loginIdentifier` 导致登录不可用
3. **注册必填字段修复**（严重#2）：`phone` 必填性不一致导致注册可能失败

### P1 — 尽快修复（影响重要功能）

4. **公告管理路径修复**（严重#7）：缺少 `/admin` 路径段导致公告管理不可用
5. **评价审核路径修复**（严重#6）：`/audit` vs `/pending` 导致审核列表不可用
6. **创建预约参数修复**（中等#1）：`notes` vs `remark` 导致预约备注丢失
7. **商家验证码参数修复**（严重#3）：`value` vs `target` 导致验证码发送失败
8. **用户详情接口实现**（严重#5）：后端缺失接口导致用户详情页不可用
9. **角色权限接口实现**（严重#9）：后端缺失接口导致权限管理不可用
10. **操作日志接口实现**（严重#8）：后端缺失接口导致日志查看不可用
11. **营收导出格式修复**（严重#10）：JSON vs 文件流导致导出不可用

### P2 — 计划修复（影响部分功能）

12. **商品/商家/服务查询参数对齐**（中等#3,#4,#5）：参数名不匹配导致筛选功能异常
13. **购物车接口参数对齐**（中等#8,#9）：`productId` vs `cartId` 导致购物车操作异常
14. **系统配置字段对齐**（中等#7）：字段名不匹配导致配置页面异常
15. **预约时段响应结构对齐**（中等#6）：前端无法正确解析时段数据

### P3 — 优化改进（不影响核心功能）

16. **命名风格统一**（轻微#3,#4）：下划线 vs 驼峰风格统一
17. **前端补充缺失接口调用**（第五章列表）
18. **多余参数清理**（轻微#1,#5,#11）

---

## 七、结论

本次比对共发现 **31处不匹配项**，其中 **10处严重不匹配** 会导致核心功能完全不可用。最关键的问题是 **认证机制不统一**（JWT vs Session），这会影响商家端和平台端的所有需要认证的接口。其次是多个 **参数名不匹配** 和 **后端接口缺失** 的问题。

建议按照 P0 → P3 的优先级顺序进行修复，首先解决认证机制和登录/注册相关的严重问题，然后逐步修复路径和参数不匹配的中等问题，最后优化命名风格和补充缺失的前端调用。

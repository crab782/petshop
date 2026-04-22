# 终端操作要求
- 终端环境是 **PowerShell 7**（Windows PowerShell），不是 Linux bash 终端
- 所有命令必须使用 **PowerShell 语法**，不能使用 bash 或 cmd 语法
- 路径分隔符使用反斜杠 `\`（Windows 风格），例如 `E:\g\petshop`
- 环境变量引用使用 `$env:NAME` 或 `%NAME%` 格式，而非 `$NAME`
- 管道操作使用 PowerShell 的 `|` 而不是 bash 的
- 条件判断使用 PowerShell 的 `-eq`, `-ne`, `-gt`, `-lt` 等操作符
- 禁止使用 `source`, `export`, `alias` 等 bash 特有命令
- 如果不确定某个命令是否为 PowerShell 命令，可以先使用 `Get-Command <command>` 验证


# 商家端页面功能模块需求分析

## 数据库表结构参考

### 商家相关表
- **merchant**: id, name, contact_person, phone, email, password, address, logo, status, created_at, updated_at
  - status: 'pending', 'approved', 'rejected'

### 服务相关表
- **service**: id, merchant_id, name, description, price, duration, image, created_at, updated_at

### 商品相关表
- **product**: id, merchant_id, name, description, price, stock, image, created_at, updated_at

### 订单相关表
- **appointment**: id, user_id, merchant_id, service_id, pet_id, appointment_time, status, total_price, notes, created_at, updated_at
  - status: 'pending', 'confirmed', 'completed', 'cancelled'
- **product_order**: id, user_id, merchant_id, total_price, status, shipping_address, created_at, updated_at
  - status: 'pending', 'paid', 'shipped', 'completed', 'cancelled'
- **product_order_item**: id, order_id, product_id, quantity, price

### 评价相关表
- **review**: id, user_id, merchant_id, service_id, appointment_id, rating, comment, created_at

### 用户相关表
- **user**: id, username, email, password, phone, avatar, created_at, updated_at
- **pet**: id, user_id, name, type, breed, age, gender, avatar, description, created_at, updated_at
- **favorite**: id, user_id, merchant_id, created_at

---

## 1. MerchantLayout.vue - 商家布局组件

### 功能模块
- 顶部导航栏：显示商家Logo、名称、通知图标、用户头像下拉菜单
- 左侧菜单栏：根据权限显示菜单项（首页、服务管理、商品管理、订单管理、评价管理、店铺管理、统计报表）
- 退出登录功能：清除登录状态并跳转到登录页

### 关联数据表
- merchant（获取商家名称、Logo）

---

## 2. Register.vue - 商家注册页面

### 功能模块
- **注册表单**
  - 商家名称（name）：必填，最大100字符
  - 联系人（contact_person）：必填，最大50字符
  - 联系电话（phone）：必填，最大20字符，格式验证
  - 邮箱（email）：必填，最大100字符，邮箱格式验证，唯一性验证
  - 密码（password）：必填，最小6字符
  - 确认密码：必填，与密码一致验证
  - 地址（address）：必填，最大255字符
  - 商家Logo上传（logo）：选填，支持图片上传
- **注册协议**：阅读并同意注册协议复选框
- **表单验证**：实时表单验证，错误提示
- **提交功能**：提交注册申请，状态默认为'pending'

### 关联数据表
- merchant（新增记录，status默认为'pending'）

### 业务逻辑
- 注册后需要平台管理员审核通过才能登录
- 密码需要加密存储

---

## 3. home/index.vue & merchant-home/index.vue - 商家首页

### 功能模块
- **统计概览卡片**
  - 今日订单数（appointment.count）
  - 待处理预约数（appointment.count WHERE status='pending'）
  - 今日收入（appointment.total_price SUM WHERE date=today'）
  - 平均评分（review.avg(rating)）
- **最近订单列表**
  - 显示最近5条预约订单
  - 订单号、用户名称、服务类型、金额、预约时间、状态、操作（查看详情）
- **最新评价列表**
  - 显示最近5条评价
  - 用户头像、用户名称、评分、评价内容、评价时间
- **快捷操作入口**
  - 添加服务、添加商品、查看预约、处理订单

### 关联数据表
- appointment（统计订单数量和金额）
- review（统计评分）
- user（获取用户名称）
- service（获取服务名称）

---

## 4. services/index.vue & merchant-services/index.vue - 服务列表

### 功能模块
- **服务表格**
  - 服务名称（name）
  - 服务描述（description）
  - 价格（price）
  - 时长（duration）：以小时/分钟显示
  - 状态（启用/禁用）：开关组件
  - 创建时间（created_at）
  - 操作：编辑、删除
- **搜索和筛选**
  - 服务名称关键字搜索
  - 按价格区间筛选
  - 按状态筛选（全部/启用/禁用）
- **分页功能**
  - 每页显示条数选择（10/20/50）
  - 页码跳转
- **批量操作**
  - 批量启用
  - 批量禁用
  - 批量删除
- **添加服务按钮**：跳转到服务编辑页面

### 关联数据表
- service（查询、修改、删除）

---

## 5. service-edit/index.vue - 服务编辑（新增/编辑）

### 功能模块
- **服务信息表单**
  - 服务名称（name）：必填，最大100字符
  - 服务描述（description）：选填，文本域，支持富文本
  - 价格（price）：必填，数字输入，最小0，保留2位小数
  - 时长（duration）：必填，数字输入，单位分钟
  - 服务图片（image）：选填，支持图片上传或URL输入，图片预览
- **表单验证**
  - 必填项验证
  - 价格格式验证
  - 时长格式验证
- **保存功能**
  - 新增：创建新服务记录
  - 编辑：更新现有服务记录
- **返回列表**：取消并返回服务列表页

### 关联数据表
- service（新增或更新）
- merchant（关联商家ID）

---

## 6. merchant-products/index.vue - 商品管理

### 功能模块
- **商品表格**
  - 商品图片（image）：缩略图显示
  - 商品名称（name）
  - 商品描述（description）：截断显示
  - 价格（price）：格式化为货币格式
  - 库存（stock）：显示库存数量，库存不足时高亮提示
  - 状态（启用/禁用）：开关组件
  - 创建时间（created_at）
  - 操作：编辑、删除
- **搜索和筛选**
  - 商品名称关键字搜索
  - 按价格区间筛选
  - 按库存状态筛选（全部/有货/缺货）
  - 按状态筛选
- **分页功能**
- **批量操作**
- **添加商品按钮**：跳转到商品编辑页面

### 关联数据表
- product（查询、修改、删除）

---

## 7. product-edit/index.vue - 商品编辑（新增/编辑）

### 功能模块
- **商品信息表单**
  - 商品名称（name）：必填，最大100字符
  - 商品描述（description）：选填，文本域
  - 价格（price）：必填，数字，最小0，保留2位小数
  - 库存（stock）：必填，整数，最小0
  - 商品图片（image）：选填，支持图片上传或URL输入，图片预览
- **表单验证**
- **库存管理**
  - 库存预警设置：当库存低于某值时提醒
  - 库存操作记录
- **保存功能**
- **返回列表**

### 关联数据表
- product（新增或更新）
- merchant（关联商家ID）

---

## 8. appointments/index.vue & merchant-appointments/index.vue - 预约列表

### 功能模块
- **预约表格**
  - 预约编号（id）
  - 用户名称（user.username）：关联查询
  - 宠物名称（pet.name）：关联查询
  - 服务名称（service.name）：关联查询
  - 预约时间（appointment_time）：格式化为日期时间
  - 预约金额（total_price）：格式化为货币格式
  - 状态（status）：待处理（黄色）/已确认（蓝色）/已完成（绿色）/已取消（灰色）
  - 备注（notes）：截断显示
  - 操作：查看详情、确认、完成、取消
- **状态标签**
  - pending：待处理，显示黄色标签
  - confirmed：已确认，显示蓝色标签
  - completed：已完成，显示绿色标签
  - cancelled：已取消，显示灰色标签
- **搜索和筛选**
  - 按预约编号搜索
  - 按用户名称搜索
  - 按日期范围筛选
  - 按状态筛选
- **分页功能**
- **导出功能**：导出预约列表为Excel

### 关联数据表
- appointment（查询、修改状态）
- user（关联查询用户名）
- pet（关联查询宠物名）
- service（关联查询服务名）

---

## 9. merchant-orders/index.vue - 服务订单列表

### 功能模块
- **订单表格**
  - 订单编号（id）
  - 用户名称（user.username）
  - 预约服务（service.name）
  - 预约时间（appointment_time）
  - 订单金额（total_price）
  - 状态（status）：pending/confirmed/completed/cancelled
  - 操作：查看详情、确认、完成、取消
- **状态流转**
  - pending → confirmed → completed
  - pending → cancelled
  - confirmed → cancelled
- **搜索和筛选**
- **分页功能**

### 关联数据表
- appointment（查询、修改状态）
- user、service（关联查询）

---

## 10. merchant-product-orders/index.vue - 商品订单列表

### 功能模块
- **订单表格**
  - 订单编号（id）
  - 用户名称（user.username）
  - 收货地址（shipping_address）
  - 订单总金额（total_price）
  - 订单状态（status）：pending/paid/shipped/completed/cancelled
  - 下单时间（created_at）
  - 操作：查看详情、发货、完成、取消
- **订单状态标签**
  - pending：待支付（黄色）
  - paid：已支付（蓝色）
  - shipped：已发货（橙色）
  - completed：已完成（绿色）
  - cancelled：已取消（灰色）
- **状态流转**
  - pending → paid → shipped → completed
  - pending → cancelled
  - paid → cancelled（退款处理）
- **订单详情弹窗**
  - 订单商品列表（product_order_item + product）
  - 商品名称、数量、单价
  - 收货人信息
  - 物流信息
- **发货功能**
  - 输入物流单号
  - 选择物流公司
- **搜索和筛选**
- **分页功能**

### 关联数据表
- product_order（查询、修改状态）
- product_order_item（订单商品明细）
- product（关联商品信息）
- user（关联用户信息）

---

## 11. reviews/index.vue & merchant-reviews/index.vue - 评价列表

### 功能模块
- **评价表格**
  - 评价ID（id）
  - 用户头像（user.avatar）
  - 用户名称（user.username）
  - 关联服务（service.name）
  - 关联预约（appointment.id）
  - 评分（rating）：1-5星显示
  - 评价内容（comment）
  - 评价时间（created_at）
  - 操作：查看详情、删除
- **评分统计**
  - 平均评分
  - 评分分布（5星/4星/3星/2星/1星数量）
- **评价详情弹窗**
  - 用户信息
  - 服务信息
  - 评分
  - 评价内容
  - 评价图片（如有）
  - 商家回复（如有）
- **回复功能**
  - 输入回复内容
  - 提交回复
- **搜索和筛选**
  - 按评分筛选
  - 按日期范围筛选
  - 关键字搜索评价内容
- **分页功能**

### 关联数据表
- review（查询、删除）
- user（关联用户信息）
- service（关联服务信息）
- appointment（关联预约信息）

---

## 12. shop-edit/index.vue - 店铺编辑

### 功能模块
- **店铺信息表单**
  - 商家名称（name）：必填，最大100字符
  - 联系人（contact_person）：必填，最大50字符
  - 联系电话（phone）：必填，最大20字符
  - 邮箱（email）：必填，最大100字符
  - 地址（address）：必填，最大255字符
  - 商家Logo（logo）：支持图片上传或URL输入
- **表单验证**
- **保存功能**
- **返回设置**

### 关联数据表
- merchant（更新商家信息）

---

## 13. shop-settings/index.vue - 店铺设置

### 功能模块
- **店铺信息展示**
  - 商家名称、Logo、地址、联系方式等（只读）
  - 入驻审核状态（status）：pending/approved/rejected
  - 注册时间
- **店铺状态管理**
  - 营业中/休息中切换
  - 状态说明
- **通知设置**
  - 预约提醒开关
  - 订单提醒开关
  - 评价提醒开关
  - 短信/邮件通知切换
- **账户安全**
  - 修改密码
  - 绑定手机号
  - 绑定邮箱
- **数据备份**
  - 导出店铺数据

### 关联数据表
- merchant（查询和更新店铺信息）

---

## 14. categories/index.vue - 商品分类管理

### 功能模块
- **分类列表**
  - 分类名称
  - 分类图标
  - 商品数量
  - 排序（升序/降序）
  - 状态（启用/禁用）
  - 操作：编辑、删除
- **添加分类**
  - 分类名称：必填
  - 分类图标：选填
  - 排序：数字，默认为0
- **编辑分类**
  - 弹出表单编辑
- **分类排序**
  - 拖拽排序
  - 或手动输入排序值
- **批量操作**
  - 批量启用
  - 批量禁用
- **分页功能**

### 关联数据表
- category（如有分类表，无则需新建或使用product的type字段）

---

## 15. stats-appointments/index.vue - 预约统计

### 功能模块
- **统计概览**
  - 本月预约总数
  - 本月完成数
  - 本月取消数
  - 本月取消率
  - 环比增长率
- **预约趋势图**
  - 折线图/柱状图显示
  - 按日/周/月展示
  - 可选择日期范围
- **预约统计表格**
  - 日期
  - 预约数量
  - 完成数量
  - 取消数量
  - 完成率
  - 总金额
- **服务类型统计**
  - 各服务预约数量占比
  - 饼图展示
- **时段统计**
  - 预约高峰时段分析
  - 柱状图展示
- **导出功能**
  - 导出统计数据为Excel

### 关联数据表
- appointment（统计预约数据）
- service（关联服务类型）

---

## 16. stats-revenue/index.vue - 营收统计

### 功能模块
- **营收概览**
  - 本月总收入
  - 本月订单数
  - 平均客单价
  - 环比增长率
- **营收趋势图**
  - 折线图显示收入趋势
  - 按日/周/月展示
  - 可选择日期范围
- **营收构成**
  - 服务收入占比
  - 商品收入占比
  - 饼图展示
- **营收统计表格**
  - 日期
  - 服务订单数
  - 服务收入
  - 商品订单数
  - 商品收入
  - 总收入
- **top商品/服务排行**
  - 销量最高的商品/服务
  - 收入最高的商品/服务
- **导出功能**
  - 导出营收数据为Excel

### 关联数据表
- appointment（服务收入统计）
- product_order（商品收入统计）
- product_order_item（商品订单明细）

---

## 页面功能模块汇总

| 序号 | 页面名称 | 主要功能模块 |
|------|----------|--------------|
| 1 | MerchantLayout.vue | 导航菜单、用户信息、退出登录 |
| 2 | Register.vue | 注册表单、表单验证、提交注册 |
| 3 | home/index.vue | 统计概览、最近订单、最新评价、快捷入口 |
| 4 | services/index.vue | 服务表格、搜索筛选、分页、批量操作 |
| 5 | service-edit/index.vue | 服务表单、图片上传、表单验证、保存 |
| 6 | merchant-products/index.vue | 商品表格、搜索筛选、分页、库存预警 |
| 7 | product-edit/index.vue | 商品表单、库存管理、图片上传、保存 |
| 8 | appointments/index.vue | 预约表格、状态管理、搜索筛选、导出 |
| 9 | merchant-orders/index.vue | 订单表格、状态流转、搜索筛选 |
| 10 | merchant-product-orders/index.vue | 订单表格、发货管理、物流信息、订单详情 |
| 11 | reviews/index.vue | 评价表格、评分统计、回复功能、筛选 |
| 12 | shop-edit/index.vue | 店铺信息表单、图片上传、保存 |
| 13 | shop-settings/index.vue | 店铺状态、通知设置、账户安全 |
| 14 | categories/index.vue | 分类CRUD、排序、批量操作 |
| 15 | stats-appointments/index.vue | 预约统计概览、趋势图、导出 |
| 16 | stats-revenue/index.vue | 营收统计概览、趋势图、构成分析、导出 |

---

## 待完善的表结构

### 建议新建表

#### 1. category（分类表）
```sql
CREATE TABLE category (
  id INT PRIMARY KEY AUTO_INCREMENT,
  merchant_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  icon VARCHAR(255),
  sort INT DEFAULT 0,
  status ENUM('enabled', 'disabled') DEFAULT 'enabled',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);
```

#### 2. address（收货地址表）
```sql
CREATE TABLE address (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  receiver_name VARCHAR(50) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  province VARCHAR(50) NOT NULL,
  city VARCHAR(50) NOT NULL,
  district VARCHAR(50) NOT NULL,
  detail_address VARCHAR(255) NOT NULL,
  is_default BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

#### 3. notification（消息通知表）
```sql
CREATE TABLE notification (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT,
  type VARCHAR(50),
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

#### 4. log（日志表）
```sql
CREATE TABLE log (
  id INT PRIMARY KEY AUTO_INCREMENT,
  admin_id INT,
  action VARCHAR(100) NOT NULL,
  target_type VARCHAR(50),
  target_id INT,
  detail TEXT,
  ip_address VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (admin_id) REFERENCES admin(id)
);
```

---

# 用户端页面功能模块需求分析

## 1. UserLayout.vue - 用户布局组件

### 功能模块
- 顶部导航栏：显示网站Logo、搜索框、通知图标、用户头像下拉菜单
- 左侧菜单栏：根据用户权限显示菜单项（首页、预约服务、个人中心、我的宠物、收藏评价等）
- 退出登录功能：清除登录状态并跳转到登录页

### 关联数据表
- user（获取用户名称、头像）

---

## 2. user-home/index.vue - 用户首页

### 功能模块
- **欢迎区域**：显示欢迎信息、当前日期
- **统计卡片**：我的宠物数量、待处理预约数量、服务评价数量
- **最近活动列表**：显示最近的预约、评价等活动
- **推荐服务**：展示热门服务推荐
- **快捷入口**：预约服务、查看宠物、查看订单

### 关联数据表
- user（用户信息）
- pet（宠物数量统计）
- appointment（预约统计和最近活动）
- review（评价统计）
- service（推荐服务）

---

## 3. user-services/index.vue - 我的服务

### 功能模块
- **服务列表**：显示用户已购买的服务
- **服务状态**：待使用、已使用、已过期
- **搜索和筛选**：按服务名称、状态筛选
- **详情查看**：点击查看服务详情

### 关联数据表
- service（服务信息）
- appointment（服务预约记录）

---

## 4. user-pets/index.vue - 我的宠物

### 功能模块
- **宠物列表**：显示用户的所有宠物
  - 宠物头像、名称、类型、年龄、性别
- **添加宠物**：跳转到宠物编辑页面
- **编辑宠物**：修改宠物信息
- **删除宠物**：移除宠物记录
- **搜索和筛选**：按宠物名称、类型筛选

### 关联数据表
- pet（宠物信息）

---

## 5. user-profile/index.vue - 个人中心

### 功能模块
- **个人信息展示**：用户名、邮箱、电话、头像
- **快捷操作**：编辑个人资料、管理宠物、查看订单、查看评价
- **安全设置**：修改密码、绑定手机号、绑定邮箱

### 关联数据表
- user（用户信息）
- pet（宠物数量）
- appointment（订单数量）
- review（评价数量）

---

## 6. user-appointments/index.vue - 我的预约

### 功能模块
- **预约列表**：显示所有预约记录
  - 预约编号、服务名称、商家名称、预约时间、状态、金额
- **状态筛选**：待处理、已确认、已完成、已取消
- **日期筛选**：按预约日期范围筛选
- **详情查看**：点击查看预约详情
- **取消预约**：对未完成的预约进行取消操作

### 关联数据表
- appointment（预约记录）
- service（服务信息）
- merchant（商家信息）

---

## 7. checkout/index.vue - 下单确认页

### 功能模块
- **订单信息**：商品列表、数量、单价、总价
- **收货地址**：选择收货地址，新增地址
- **支付方式**：选择支付方式（微信、支付宝、银行卡）
- **订单备注**：添加订单备注
- **提交订单**：生成订单并跳转到支付页面

### 关联数据表
- product_order（订单信息）
- product_order_item（订单商品明细）
- address（收货地址）
- product（商品信息）

---

## 8. pay/index.vue - 支付页

### 功能模块
- **订单信息**：订单编号、总金额、支付截止时间
- **支付方式**：微信支付、支付宝支付、银行卡支付
- **支付状态**：待支付、支付中、支付成功、支付失败
- **扫码支付**：显示支付二维码
- **支付结果**：跳转到支付结果页

### 关联数据表
- product_order（订单信息）

---

## 9. user-merchant/index.vue - 商家列表

### 功能模块
- **商家列表**：显示所有商家
  - 商家名称、Logo、地址、评分、服务数量
- **搜索和筛选**：按商家名称、评分、距离筛选
- **商家详情**：点击进入商家详情页
- **收藏商家**：将商家添加到收藏

### 关联数据表
- merchant（商家信息）
- service（服务数量）
- review（评分统计）
- favorite（收藏状态）

---

## 10. order-detail/index.vue - 订单详情

### 功能模块
- **订单基本信息**：订单编号、下单时间、订单状态、总金额
- **商品列表**：商品名称、数量、单价、小计
- **收货信息**：收货人、电话、地址
- **物流信息**：物流状态、物流单号、物流公司
- **操作**：取消订单、确认收货、申请退款

### 关联数据表
- product_order（订单信息）
- product_order_item（订单商品明细）
- product（商品信息）

---

## 11. user-orders/index.vue - 我的订单

### 功能模块
- **订单列表**：显示所有商品订单
  - 订单编号、下单时间、总金额、订单状态
- **状态筛选**：待支付、已支付、已发货、已完成、已取消
- **日期筛选**：按下单日期范围筛选
- **详情查看**：点击查看订单详情
- **批量操作**：批量取消、批量删除

### 关联数据表
- product_order（订单信息）

---

## 12. user-reviews/index.vue - 服务评价

### 功能模块
- **评价列表**：显示用户对服务的评价
  - 服务名称、商家名称、评分、评价内容、评价时间
- **评分筛选**：按评分筛选
- **日期筛选**：按评价日期范围筛选
- **详情查看**：点击查看评价详情

### 关联数据表
- review（评价信息）
- service（服务信息）
- merchant（商家信息）

---

## 13. my-reviews/index.vue - 我的评价

### 功能模块
- **评价列表**：显示用户所有评价
  - 评价对象（服务/商品）、评分、评价内容、评价时间
- **评价类型筛选**：服务评价、商品评价
- **日期筛选**：按评价日期范围筛选
- **编辑评价**：修改已发布的评价
- **删除评价**：删除评价

### 关联数据表
- review（评价信息）
- service（服务评价）
- product（商品评价）

---

## 14. product-detail/index.vue - 商品详情

### 功能模块
- **商品信息**：商品图片、名称、描述、价格、库存
- **商家信息**：商家名称、评分、地址、联系方式
- **购买选项**：选择数量、规格（如有）
- **加入购物车**：将商品添加到购物车
- **立即购买**：直接跳转到下单页
- **商品评价**：显示商品的用户评价
- **收藏商品**：将商品添加到收藏

### 关联数据表
- product（商品信息）
- merchant（商家信息）
- review（商品评价）

---

## 15. pet-edit/index.vue - 新增/编辑宠物

### 功能模块
- **宠物信息表单**
  - 宠物名称（name）：必填，最大50字符
  - 宠物类型（type）：必填，下拉选择
  - 宠物品种（breed）：选填，最大50字符
  - 宠物年龄（age）：必填，数字
  - 宠物性别（gender）：必填，单选（male/female）
  - 宠物头像（avatar）：选填，支持图片上传
  - 宠物描述（description）：选填，文本域
- **表单验证**
- **保存功能**：新增或更新宠物信息
- **返回列表**

### 关联数据表
- pet（新增或更新）
- user（关联用户ID）

---

## 16. notifications/index.vue - 消息通知

### 功能模块
- **通知列表**：显示所有通知
  - 通知标题、内容、类型、时间、已读状态
- **通知类型筛选**：系统通知、订单通知、预约通知、评价通知
- **已读/未读筛选**
- **标记已读**：将通知标记为已读
- **批量操作**：批量标记已读、批量删除

### 关联数据表
- notification（通知信息）

---

## 17. user-announcements/index.vue - 公告通知

### 功能模块
- **公告列表**：显示所有公告
  - 公告标题、发布时间、摘要
- **搜索**：按公告标题搜索
- **详情查看**：点击查看公告详情
- **已读状态**：标记公告为已读

### 关联数据表
- announcement（公告信息）

---

## 18. user-shop/index.vue - 店铺详情

### 功能模块
- **店铺信息**：店铺名称、Logo、地址、联系方式、评分
- **服务列表**：店铺提供的服务
- **商品列表**：店铺销售的商品
- **评价列表**：用户对店铺的评价
- **收藏店铺**：将店铺添加到收藏
- **联系商家**：直接联系商家

### 关联数据表
- merchant（店铺信息）
- service（服务列表）
- product（商品列表）
- review（评价列表）
- favorite（收藏状态）

---

## 19. addresses/index.vue - 收货地址管理

### 功能模块
- **地址列表**：显示所有收货地址
  - 收货人、电话、地址、是否默认
- **添加地址**：新增收货地址
- **编辑地址**：修改现有地址
- **删除地址**：移除地址
- **设为默认**：设置默认地址

### 关联数据表
- address（地址信息）
- user（关联用户ID）

---

## 20. service-list/index.vue - 服务列表

### 功能模块
- **服务列表**：显示所有服务
  - 服务名称、价格、时长、商家、评分
- **搜索和筛选**：按服务名称、价格、时长、评分筛选
- **排序**：按价格、评分、销量排序
- **服务详情**：点击查看服务详情
- **预约服务**：直接预约服务

### 关联数据表
- service（服务信息）
- merchant（商家信息）
- review（评分统计）

---

## 21. search/index.vue - 搜索页

### 功能模块
- **搜索框**：输入搜索关键词
- **搜索历史**：显示最近搜索记录
- **热门搜索**：显示热门搜索关键词
- **搜索结果**：按商品、服务、商家分类显示
- **筛选和排序**：对搜索结果进行筛选和排序

### 关联数据表
- product（商品搜索）
- service（服务搜索）
- merchant（商家搜索）

---

## 22. announcement-detail/index.vue - 公告详情

### 功能模块
- **公告信息**：公告标题、发布时间、内容
- **相关公告**：推荐相关公告
- **返回列表**

### 关联数据表
- announcement（公告信息）

---

## 23. user-book/index.vue - 我的预约记录

### 功能模块
- **预约记录列表**：显示所有预约
  - 预约编号、服务名称、商家名称、预约时间、状态
- **状态筛选**：待处理、已确认、已完成、已取消
- **日期筛选**：按预约日期范围筛选
- **详情查看**：点击查看预约详情
- **取消预约**：对未完成的预约进行取消

### 关联数据表
- appointment（预约记录）
- service（服务信息）
- merchant（商家信息）

---

## 24. service-detail/index.vue - 服务详情

### 功能模块
- **服务信息**：服务名称、描述、价格、时长、图片
- **商家信息**：商家名称、评分、地址、联系方式
- **预约选项**：选择宠物、预约时间、添加备注
- **立即预约**：提交预约申请
- **服务评价**：显示服务的用户评价
- **收藏服务**：将服务添加到收藏

### 关联数据表
- service（服务信息）
- merchant（商家信息）
- pet（用户宠物列表）
- review（服务评价）

---

## 25. profile-edit/index.vue - 编辑个人资料

### 功能模块
- **个人信息表单**
  - 用户名（username）：必填，最大50字符
  - 邮箱（email）：必填，邮箱格式验证
  - 电话（phone）：必填，电话格式验证
  - 头像（avatar）：选填，支持图片上传
- **表单验证**
- **保存功能**：更新个人信息
- **返回个人中心**

### 关联数据表
- user（更新用户信息）

---

## 26. user-favorites/index.vue - 收藏评价

### 功能模块
- **收藏列表**：显示收藏的商家
  - 商家名称、Logo、地址、评分
- **取消收藏**：移除收藏
- **商家详情**：点击进入商家详情页
- **评分筛选**：按商家评分筛选

### 关联数据表
- favorite（收藏记录）
- merchant（商家信息）
- review（评分统计）

---

## 27. user-cart/index.vue - 购物车

### 功能模块
- **商品列表**：显示购物车中的商品
  - 商品图片、名称、价格、数量、小计
- **数量调整**：增加/减少商品数量
- **删除商品**：从购物车中移除商品
- **全选/取消全选**
- **合计金额**：计算选中商品的总金额
- **结算**：跳转到下单确认页

### 关联数据表
- 购物车（通常存储在前端或session中，如需持久化可创建cart表）
- product（商品信息）

---

## 28. appointment-confirm/index.vue - 预约确认页

### 功能模块
- **服务信息**：服务名称、价格、时长
- **商家信息**：商家名称、地址、联系方式
- **宠物选择**：选择要服务的宠物
- **时间选择**：选择预约时间
- **备注**：添加预约备注
- **费用明细**：显示服务费用
- **提交预约**：生成预约订单

### 关联数据表
- appointment（新增预约记录）
- service（服务信息）
- merchant（商家信息）
- pet（宠物信息）

---

## 用户端页面功能模块汇总

| 序号 | 页面名称 | 主要功能模块 |
|------|----------|--------------|
| 1 | UserLayout.vue | 导航菜单、用户信息、退出登录 |
| 2 | user-home/index.vue | 统计概览、最近活动、推荐服务、快捷入口 |
| 3 | user-services/index.vue | 服务列表、状态管理、筛选 |
| 4 | user-pets/index.vue | 宠物CRUD、搜索筛选 |
| 5 | user-profile/index.vue | 个人信息展示、快捷操作、安全设置 |
| 6 | user-appointments/index.vue | 预约列表、状态管理、筛选 |
| 7 | checkout/index.vue | 订单信息、地址选择、支付方式、提交订单 |
| 8 | pay/index.vue | 支付信息、支付方式、支付状态 |
| 9 | user-merchant/index.vue | 商家列表、搜索筛选、收藏 |
| 10 | order-detail/index.vue | 订单详情、物流信息、操作 |
| 11 | user-orders/index.vue | 订单列表、状态筛选、批量操作 |
| 12 | user-reviews/index.vue | 服务评价列表、筛选 |
| 13 | my-reviews/index.vue | 我的评价管理、编辑删除 |
| 14 | product-detail/index.vue | 商品信息、购买选项、评价、收藏 |
| 15 | pet-edit/index.vue | 宠物表单、图片上传、保存 |
| 16 | notifications/index.vue | 通知列表、类型筛选、批量操作 |
| 17 | user-announcements/index.vue | 公告列表、搜索、详情 |
| 18 | user-shop/index.vue | 店铺信息、服务商品、评价、收藏 |
| 19 | addresses/index.vue | 地址管理、CRUD操作、默认设置 |
| 20 | service-list/index.vue | 服务列表、搜索筛选、排序 |
| 21 | search/index.vue | 搜索功能、结果分类、筛选 |
| 22 | announcement-detail/index.vue | 公告详情、相关推荐 |
| 23 | user-book/index.vue | 预约记录管理、状态筛选 |
| 24 | service-detail/index.vue | 服务信息、预约选项、评价 |
| 25 | profile-edit/index.vue | 个人资料编辑、表单验证 |
| 26 | user-favorites/index.vue | 收藏列表、取消收藏 |
| 27 | user-cart/index.vue | 购物车管理、数量调整、结算 |
| 28 | appointment-confirm/index.vue | 预约信息确认、时间选择、提交 |

---

# 平台端页面功能模块需求分析

## 1. AdminLayout.vue - 平台布局组件

### 功能模块
- 顶部导航栏：显示平台Logo、名称、通知图标、管理员头像下拉菜单
- 左侧菜单栏：根据权限显示菜单项（首页、用户管理、商家管理、服务管理、商品管理、订单管理、评价管理、系统设置等）
- 退出登录功能：清除登录状态并跳转到登录页

### 关联数据表
- admin（获取管理员信息）

---

## 2. admin-dashboard/index.vue - 平台首页

### 功能模块
- **统计概览卡片**
  - 总用户数（user.count）
  - 总商家数（merchant.count）
  - 今日订单数（appointment.count + product_order.count WHERE date=today）
  - 本月营收（appointment.total_price + product_order.total_price SUM WHERE month=current）
- **最近注册用户**：显示最近注册的用户列表
- **待审核商家**：显示待审核的商家列表
- **系统公告**：显示最新的系统公告
- **快捷操作**：查看用户、查看商家、发布公告、系统设置

### 关联数据表
- user（用户统计）
- merchant（商家统计）
- appointment（服务订单统计）
- product_order（商品订单统计）
- announcement（公告信息）

---

## 3. admin-users/index.vue - 用户管理

### 功能模块
- **用户表格**
  - 用户ID、用户名、邮箱、电话、注册时间、状态
  - 操作：查看详情、禁用/启用、删除
- **搜索和筛选**
  - 按用户名、邮箱、电话搜索
  - 按注册时间范围筛选
  - 按状态筛选（全部/启用/禁用）
- **分页功能**
- **批量操作**
  - 批量禁用
  - 批量启用
  - 批量删除

### 关联数据表
- user（用户信息）

---

## 4. user-detail/index.vue - 用户详情

### 功能模块
- **基本信息**：用户名、邮箱、电话、头像、注册时间
- **宠物列表**：显示用户的宠物信息
- **订单记录**：显示用户的服务和商品订单
- **评价记录**：显示用户的评价记录
- **操作**：修改用户信息、禁用/启用用户

### 关联数据表
- user（用户信息）
- pet（用户宠物）
- appointment（服务订单）
- product_order（商品订单）
- review（评价记录）

---

## 5. admin-merchants/index.vue - 商家管理

### 功能模块
- **商家表格**
  - 商家ID、商家名称、联系人、电话、邮箱、状态、注册时间
  - 操作：查看详情、审核、禁用/启用、删除
- **搜索和筛选**
  - 按商家名称、联系人、邮箱搜索
  - 按注册时间范围筛选
  - 按状态筛选（全部/待审核/已通过/已拒绝/启用/禁用）
- **分页功能**
- **批量操作**
  - 批量审核
  - 批量禁用
  - 批量启用
  - 批量删除

### 关联数据表
- merchant（商家信息）

---

## 6. merchant-detail/index.vue - 商家详情

### 功能模块
- **基本信息**：商家名称、联系人、电话、邮箱、地址、Logo、状态
- **服务列表**：显示商家提供的服务
- **商品列表**：显示商家销售的商品
- **订单记录**：显示商家的订单记录
- **评价记录**：显示商家的评价记录
- **操作**：修改商家信息、审核商家、禁用/启用商家

### 关联数据表
- merchant（商家信息）
- service（服务列表）
- product（商品列表）
- appointment（服务订单）
- product_order（商品订单）
- review（评价记录）

---

## 7. merchant-audit/index.vue - 商家入驻审核

### 功能模块
- **待审核商家列表**
  - 商家ID、商家名称、联系人、电话、邮箱、注册时间
  - 操作：查看详情、通过、拒绝
- **搜索和筛选**
  - 按商家名称、联系人搜索
  - 按注册时间范围筛选
- **分页功能**
- **审核操作**
  - 通过：将状态改为'approved'
  - 拒绝：将状态改为'rejected'，并填写拒绝原因

### 关联数据表
- merchant（商家信息，status='pending'）

---

## 8. admin-services/index.vue - 服务管理

### 功能模块
- **服务表格**
  - 服务ID、服务名称、商家、价格、时长、状态、创建时间
  - 操作：查看详情、编辑、删除
- **搜索和筛选**
  - 按服务名称搜索
  - 按商家筛选
  - 按价格区间筛选
  - 按状态筛选（全部/启用/禁用）
- **分页功能**
- **批量操作**
  - 批量启用
  - 批量禁用
  - 批量删除

### 关联数据表
- service（服务信息）
- merchant（关联商家信息）

---

## 9. admin-products/index.vue - 商品管理

### 功能模块
- **商品表格**
  - 商品ID、商品名称、商家、价格、库存、状态、创建时间
  - 操作：查看详情、编辑、删除
- **搜索和筛选**
  - 按商品名称搜索
  - 按商家筛选
  - 按价格区间筛选
  - 按库存状态筛选（全部/有货/缺货）
  - 按状态筛选
- **分页功能**
- **批量操作**
  - 批量启用
  - 批量禁用
  - 批量删除

### 关联数据表
- product（商品信息）
- merchant（关联商家信息）

---

## 10. product-manage/index.vue - 商品管理（详情）

### 功能模块
- **商品信息**：商品名称、描述、价格、库存、图片
- **商家信息**：关联商家名称、联系方式
- **操作**：修改商品信息、启用/禁用商品、删除商品

### 关联数据表
- product（商品信息）
- merchant（关联商家信息）

---

## 11. admin-reviews/index.vue - 评价管理

### 功能模块
- **评价表格**
  - 评价ID、用户、商家、服务/商品、评分、评价内容、评价时间、状态
  - 操作：查看详情、审核、删除
- **搜索和筛选**
  - 按用户、商家、服务/商品搜索
  - 按评分筛选
  - 按评价时间范围筛选
  - 按状态筛选（全部/待审核/已通过/已拒绝）
- **分页功能**
- **批量操作**
  - 批量审核
  - 批量删除

### 关联数据表
- review（评价信息）
- user（用户信息）
- merchant（商家信息）
- service（服务信息）
- product（商品信息）

---

## 12. review-audit/index.vue - 评价审核

### 功能模块
- **待审核评价列表**
  - 评价ID、用户、商家、服务/商品、评分、评价内容、评价时间
  - 操作：查看详情、通过、拒绝
- **搜索和筛选**
  - 按用户、商家搜索
  - 按评价时间范围筛选
  - 按评分筛选
- **分页功能**
- **审核操作**
  - 通过：将评价状态改为已通过
  - 拒绝：将评价状态改为已拒绝，不显示在前端

### 关联数据表
- review（评价信息）
- user（用户信息）
- merchant（商家信息）
- service（服务信息）
- product（商品信息）

---

## 13. admin-announcements/index.vue - 公告管理

### 功能模块
- **公告列表**
  - 公告ID、标题、发布时间、状态
  - 操作：查看详情、编辑、删除、发布/下架
- **搜索和筛选**
  - 按公告标题搜索
  - 按发布时间范围筛选
  - 按状态筛选（全部/已发布/已下架）
- **分页功能**
- **添加公告**：跳转到公告编辑页面
- **批量操作**
  - 批量发布
  - 批量下架
  - 批量删除

### 关联数据表
- announcement（公告信息）

---

## 14. announcement-edit/index.vue - 发布/编辑公告

### 功能模块
- **公告信息表单**
  - 标题（title）：必填，最大255字符
  - 内容（content）：必填，富文本编辑器
  - 状态：发布/草稿
- **表单验证**
- **保存功能**：新增或更新公告
- **返回列表**

### 关联数据表
- announcement（新增或更新）

---

## 15. admin-system/index.vue - 系统设置

### 功能模块
- **基本设置**：网站名称、Logo、联系方式、备案信息
- **邮件设置**：SMTP服务器、端口、用户名、密码
- **短信设置**：短信服务商、API密钥
- **支付设置**：微信支付、支付宝支付配置
- **文件上传设置**：上传路径、文件大小限制
- **保存功能**：更新系统设置

### 关联数据表
- 系统设置表（如需持久化，可创建system_settings表）

---

## 16. roles/index.vue - 角色权限管理

### 功能模块
- **角色列表**
  - 角色ID、角色名称、描述、创建时间
  - 操作：编辑、删除
- **添加角色**：新增角色
- **编辑角色**：修改角色信息和权限
- **权限设置**：为角色分配菜单和操作权限
- **批量操作**：批量删除角色

### 关联数据表
- admin（管理员信息）
- 角色表和权限表（如需实现完整权限管理，需新建）

---

## 17. logs/index.vue - 操作日志

### 功能模块
- **日志表格**
  - 日志ID、操作人、操作类型、操作对象、操作详情、IP地址、操作时间
- **搜索和筛选**
  - 按操作人、操作类型、操作对象搜索
  - 按操作时间范围筛选
- **分页功能**
- **导出功能**：导出日志为Excel

### 关联数据表
- log（操作日志）
- admin（管理员信息）

---

## 18. admin-activities/index.vue - 活动管理

### 功能模块
- **活动列表**
  - 活动ID、活动名称、活动类型、开始时间、结束时间、状态
  - 操作：查看详情、编辑、删除、启用/禁用
- **添加活动**：新增活动
- **搜索和筛选**
  - 按活动名称搜索
  - 按活动类型筛选
  - 按时间范围筛选
  - 按状态筛选
- **分页功能**

### 关联数据表
- 活动表（如需实现活动管理，需新建）

---

## 19. admin-tasks/index.vue - 任务管理

### 功能模块
- **任务列表**
  - 任务ID、任务名称、任务类型、执行时间、状态、执行结果
  - 操作：查看详情、编辑、删除、执行
- **添加任务**：新增定时任务
- **搜索和筛选**
  - 按任务名称搜索
  - 按任务类型筛选
  - 按执行时间范围筛选
  - 按状态筛选
- **分页功能**

### 关联数据表
- 任务表（如需实现任务管理，需新建）

---

## 20. shop-audit/index.vue - 店铺审核

### 功能模块
- **待审核店铺列表**
  - 店铺ID、店铺名称、商家、提交时间、审核状态
  - 操作：查看详情、通过、拒绝
- **搜索和筛选**
  - 按店铺名称、商家搜索
  - 按提交时间范围筛选
  - 按审核状态筛选
- **分页功能**
- **审核操作**
  - 通过：将店铺状态改为已审核
  - 拒绝：将店铺状态改为已拒绝，填写拒绝原因

### 关联数据表
- merchant（店铺信息）

---

## 平台端页面功能模块汇总

| 序号 | 页面名称 | 主要功能模块 |
|------|----------|--------------|
| 1 | AdminLayout.vue | 导航菜单、管理员信息、退出登录 |
| 2 | admin-dashboard/index.vue | 统计概览、快捷操作、最近数据 |
| 3 | admin-users/index.vue | 用户管理、状态管理、批量操作 |
| 4 | user-detail/index.vue | 用户详情、宠物、订单、评价 |
| 5 | admin-merchants/index.vue | 商家管理、状态管理、批量操作 |
| 6 | merchant-detail/index.vue | 商家详情、服务、商品、订单、评价 |
| 7 | merchant-audit/index.vue | 商家入驻审核、状态管理 |
| 8 | admin-services/index.vue | 服务管理、搜索筛选、批量操作 |
| 9 | admin-products/index.vue | 商品管理、搜索筛选、批量操作 |
| 10 | product-manage/index.vue | 商品详情、编辑操作 |
| 11 | admin-reviews/index.vue | 评价管理、审核、批量操作 |
| 12 | review-audit/index.vue | 评价审核、状态管理 |
| 13 | admin-announcements/index.vue | 公告管理、发布、批量操作 |
| 14 | announcement-edit/index.vue | 公告编辑、富文本、保存 |
| 15 | admin-system/index.vue | 系统设置、配置管理 |
| 16 | roles/index.vue | 角色管理、权限分配 |
| 17 | logs/index.vue | 操作日志、搜索筛选、导出 |
| 18 | admin-activities/index.vue | 活动管理、状态管理 |
| 19 | admin-tasks/index.vue | 任务管理、执行状态 |
| 20 | shop-audit/index.vue | 店铺审核、状态管理 |

---

## 三端功能协调

### 数据一致性
- **用户数据**：用户信息、宠物信息在用户端和平台端保持一致
- **商家数据**：商家信息、服务、商品在商家端和平台端保持一致
- **订单数据**：服务预约和商品订单在三端保持状态同步
- **评价数据**：用户评价在三端保持一致，平台端可审核

### 功能互补
- **用户端**：专注于用户体验，提供便捷的服务预约、商品购买、个人管理功能
- **商家端**：专注于商家运营，提供服务管理、商品管理、订单处理、数据分析功能
- **平台端**：专注于系统管理，提供用户管理、商家审核、内容审核、系统配置功能

### 权限管理
- **用户端**：只能访问自己的个人信息、宠物、订单、评价
- **商家端**：只能访问自己的店铺信息、服务、商品、订单、评价
- **平台端**：可以访问所有数据，具有最高权限

### 流程协同
- **商家入驻**：商家注册 → 平台审核 → 商家登录
- **服务预约**：用户预约 → 商家确认 → 服务完成 → 用户评价 → 平台审核（可选）
- **商品购买**：用户下单 → 支付 → 商家发货 → 用户确认收货 → 用户评价 → 平台审核（可选）
- **评价流程**：用户评价 → 平台审核（可选） → 显示在前端

### 通知机制
- **用户通知**：订单状态变更、预约提醒、系统公告
- **商家通知**：新订单、新预约、新评价、审核结果
- **平台通知**：新商家注册、待审核项目、系统预警

通过以上三端功能的协调，形成一个完整的宠物服务平台生态系统，为用户、商家和平台管理员提供各自所需的功能和服务。

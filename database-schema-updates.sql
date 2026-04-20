-- 数据库表结构更新脚本
-- 适用于本地Windows MySQL数据库 (mysql -uroot -p123456)
-- 版本: 1.0
-- 日期: 2026-04-20

-- 开始事务
START TRANSACTION;

-- =============================================
-- 高优先级修改 (立即实施)
-- =============================================

-- 1. 添加评分字段

-- 为merchant表添加商家评分字段
ALTER TABLE merchant ADD COLUMN rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '商家评分';

-- 为service表添加服务评分字段
ALTER TABLE service ADD COLUMN rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '服务评分';

-- 为product表添加商品评分字段
ALTER TABLE product ADD COLUMN rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '商品评分';

-- 2. 添加订单相关字段

-- 为appointment表添加预约单号字段
ALTER TABLE appointment ADD COLUMN order_no VARCHAR(50) UNIQUE COMMENT '预约单号';

-- 检查product_order表是否已存在order_no字段
-- (根据之前的表结构分析，product_order表已有order_no字段，这里仅作为确认)

-- 3. 添加统计字段

-- 为product表添加销量字段
ALTER TABLE product ADD COLUMN sales_volume INT DEFAULT 0 COMMENT '销量';

-- 为service表添加预约次数字段
ALTER TABLE service ADD COLUMN reservation_count INT DEFAULT 0 COMMENT '预约次数';

-- 4. 索引优化

-- 为高频查询字段创建索引

-- merchant表索引
CREATE INDEX idx_merchant_status ON merchant(status);
CREATE INDEX idx_merchant_rating ON merchant(rating);

-- service表索引
CREATE INDEX idx_service_merchant_id ON service(merchant_id);
CREATE INDEX idx_service_status ON service(status);
CREATE INDEX idx_service_rating ON service(rating);

-- product表索引
CREATE INDEX idx_product_merchant_id ON product(merchant_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_rating ON product(rating);
CREATE INDEX idx_product_sales_volume ON product(sales_volume);

-- appointment表索引
CREATE INDEX idx_appointment_merchant_id ON appointment(merchant_id);
CREATE INDEX idx_appointment_user_id ON appointment(user_id);
CREATE INDEX idx_appointment_service_id ON appointment(service_id);
CREATE INDEX idx_appointment_status ON appointment(status);
CREATE INDEX idx_appointment_appointment_time ON appointment(appointment_time);
CREATE INDEX idx_appointment_order_no ON appointment(order_no);

-- product_order表索引
CREATE INDEX idx_product_order_merchant_id ON product_order(merchant_id);
CREATE INDEX idx_product_order_user_id ON product_order(user_id);
CREATE INDEX idx_product_order_status ON product_order(status);
CREATE INDEX idx_product_order_order_no ON product_order(order_no);

-- review表索引
CREATE INDEX idx_review_merchant_id ON review(merchant_id);
CREATE INDEX idx_review_service_id ON review(service_id);
CREATE INDEX idx_review_rating ON review(rating);

-- =============================================
-- 中优先级修改 (近期实施)
-- =============================================

-- 1. 添加评价图片字段
ALTER TABLE review ADD COLUMN image VARCHAR(255) COMMENT '评价图片';

-- 2. 添加商家审核拒绝原因字段
ALTER TABLE merchant ADD COLUMN rejection_reason TEXT COMMENT '审核拒绝原因';

-- 3. 完善管理员角色关联表
-- 检查role_permission表是否已有外键约束
-- 添加外键约束（如果不存在）
ALTER TABLE role_permission ADD CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE;
ALTER TABLE role_permission ADD CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE;

-- 4. 完善活动管理字段
-- 为activity表添加必要的字段
ALTER TABLE activity ADD COLUMN organizer VARCHAR(100) COMMENT '活动组织者';
ALTER TABLE activity ADD COLUMN location VARCHAR(255) COMMENT '活动地点';
ALTER TABLE activity ADD COLUMN max_participants INT DEFAULT 0 COMMENT '最大参与人数';
ALTER TABLE activity ADD COLUMN current_participants INT DEFAULT 0 COMMENT '当前参与人数';

-- =============================================
-- 验证步骤
-- =============================================

-- 验证表结构修改
SHOW COLUMNS FROM merchant;
SHOW COLUMNS FROM service;
SHOW COLUMNS FROM product;
SHOW COLUMNS FROM appointment;
SHOW COLUMNS FROM review;
SHOW COLUMNS FROM activity;

-- 验证索引创建
SHOW INDEX FROM merchant;
SHOW INDEX FROM service;
SHOW INDEX FROM product;
SHOW INDEX FROM appointment;
SHOW INDEX FROM product_order;
SHOW INDEX FROM review;

-- 提交事务
COMMIT;

-- 脚本执行完成
SELECT '数据库表结构更新完成' AS message;
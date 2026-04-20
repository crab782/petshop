-- 数据库测试数据生成脚本
-- 适用于本地Windows MySQL数据库 (mysql -uroot -p123456)
-- 版本: 1.0
-- 日期: 2026-04-20

-- 开始事务
START TRANSACTION;

-- =============================================
-- 1. 用户表 (user) 测试数据
-- =============================================

-- 普通用户
INSERT INTO user (username, email, password, phone, avatar, status) VALUES
('user1', 'user1@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '13800138001', 'https://example.com/avatar1.jpg', 'active'),
('user2', 'user2@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '13800138002', 'https://example.com/avatar2.jpg', 'active'),
('user3', 'user3@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '13800138003', 'https://example.com/avatar3.jpg', 'active'),
('user4', 'user4@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '13800138004', 'https://example.com/avatar4.jpg', 'active'),
('user5', 'user5@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '13800138005', 'https://example.com/avatar5.jpg', 'active');

-- =============================================
-- 2. 商家表 (merchant) 测试数据
-- =============================================

INSERT INTO merchant (name, contact_person, phone, email, password, address, logo, status, rating, rejection_reason) VALUES
('宠物乐园', '张三', '13800138006', 'merchant1@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '北京市朝阳区建国路88号', 'https://example.com/merchant1.jpg', 'approved', 4.8, NULL),
('宠物之家', '李四', '13800138007', 'merchant2@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '北京市海淀区中关村大街1号', 'https://example.com/merchant2.jpg', 'approved', 4.5, NULL),
('宠物王国', '王五', '13800138008', 'merchant3@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '北京市西城区西单北大街120号', 'https://example.com/merchant3.jpg', 'pending', 0.0, NULL),
('宠物天地', '赵六', '13800138009', 'merchant4@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '北京市东城区王府井大街99号', 'https://example.com/merchant4.jpg', 'rejected', 0.0, '不符合经营条件'),
('宠物世界', '钱七', '13800138010', 'merchant5@example.com', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e', '北京市丰台区丰台路18号', 'https://example.com/merchant5.jpg', 'approved', 4.2, NULL);

-- =============================================
-- 3. 管理员表 (admin) 测试数据
-- =============================================

INSERT INTO admin (username, password) VALUES
('admin', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e'),
('operator', '$2a$10$eQV5N0y3s2QrKv7Q9y0qBO9Fz0g1X9P8d8X9eX9eX9eX9eX9eX9eX9eX9eX9eX9eX9e');

-- =============================================
-- 4. 角色表 (role) 测试数据
-- =============================================

INSERT INTO role (name, description) VALUES
('超级管理员', '拥有所有权限'),
('运营管理员', '拥有运营管理权限'),
('商家管理员', '拥有商家管理权限');

-- =============================================
-- 5. 权限表 (permission) 测试数据
-- =============================================

INSERT INTO permission (code, name, description) VALUES
('user:manage', '用户管理', '管理用户信息'),
('merchant:manage', '商家管理', '管理商家信息'),
('service:manage', '服务管理', '管理服务信息'),
('product:manage', '商品管理', '管理商品信息'),
('order:manage', '订单管理', '管理订单信息'),
('review:manage', '评价管理', '管理评价信息'),
('system:manage', '系统管理', '管理系统设置');

-- =============================================
-- 6. 角色权限关联表 (role_permission) 测试数据
-- =============================================

INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), -- 超级管理员拥有用户管理权限
(1, 2), -- 超级管理员拥有商家管理权限
(1, 3), -- 超级管理员拥有服务管理权限
(1, 4), -- 超级管理员拥有商品管理权限
(1, 5), -- 超级管理员拥有订单管理权限
(1, 6), -- 超级管理员拥有评价管理权限
(1, 7), -- 超级管理员拥有系统管理权限
(2, 1), -- 运营管理员拥有用户管理权限
(2, 2), -- 运营管理员拥有商家管理权限
(2, 5), -- 运营管理员拥有订单管理权限
(2, 6), -- 运营管理员拥有评价管理权限
(3, 3), -- 商家管理员拥有服务管理权限
(3, 4), -- 商家管理员拥有商品管理权限
(3, 5), -- 商家管理员拥有订单管理权限
(3, 6); -- 商家管理员拥有评价管理权限

-- =============================================
-- 7. 宠物表 (pet) 测试数据
-- =============================================

INSERT INTO pet (user_id, name, type, breed, age, gender, avatar, description) VALUES
(1, '旺财', '狗', '金毛', 3, 'male', 'https://example.com/pet1.jpg', '温顺可爱'),
(1, '招财', '猫', '英短', 2, 'female', 'https://example.com/pet2.jpg', '活泼好动'),
(2, '小白', '狗', '萨摩耶', 1, 'female', 'https://example.com/pet3.jpg', '雪白毛发'),
(3, '小黑', '猫', '黑猫', 4, 'male', 'https://example.com/pet4.jpg', '神秘高冷'),
(4, '大黄', '狗', '拉布拉多', 5, 'male', 'https://example.com/pet5.jpg', '忠诚可靠');

-- =============================================
-- 8. 服务表 (service) 测试数据
-- =============================================

INSERT INTO service (merchant_id, name, description, price, duration, image, category, status, rating, reservation_count) VALUES
(1, '宠物洗澡', '专业宠物洗澡服务，包含洗护用品', 50.00, 60, 'https://example.com/service1.jpg', '清洁', 'enabled', 4.9, 100),
(1, '宠物美容', '专业宠物美容服务，包含修剪毛发', 100.00, 90, 'https://example.com/service2.jpg', '美容', 'enabled', 4.8, 80),
(1, '宠物寄养', '专业宠物寄养服务，包含日常护理', 80.00, 1440, 'https://example.com/service3.jpg', '寄养', 'enabled', 4.7, 50),
(2, '宠物训练', '专业宠物训练服务，包含基础训练', 150.00, 120, 'https://example.com/service4.jpg', '训练', 'enabled', 4.6, 30),
(2, '宠物医疗', '专业宠物医疗服务，包含基础检查', 200.00, 60, 'https://example.com/service5.jpg', '医疗', 'enabled', 4.5, 20);

-- =============================================
-- 9. 商品分类表 (category) 测试数据
-- =============================================

INSERT INTO category (merchant_id, name, description, icon, sort, status, product_count) VALUES
(1, '宠物粮食', '各种宠物粮食', 'https://example.com/category1.jpg', 1, 'enabled', 10),
(1, '宠物用品', '各种宠物用品', 'https://example.com/category2.jpg', 2, 'enabled', 15),
(1, '宠物玩具', '各种宠物玩具', 'https://example.com/category3.jpg', 3, 'enabled', 8),
(2, '宠物粮食', '各种宠物粮食', 'https://example.com/category4.jpg', 1, 'enabled', 12),
(2, '宠物用品', '各种宠物用品', 'https://example.com/category5.jpg', 2, 'enabled', 20);

-- =============================================
-- 10. 商品表 (product) 测试数据
-- =============================================

INSERT INTO product (merchant_id, name, description, price, stock, image, category, low_stock_threshold, status, rating, sales_volume) VALUES
(1, '皇家狗粮', '高品质狗粮，适合各种犬种', 120.00, 50, 'https://example.com/product1.jpg', '宠物粮食', 10, 'enabled', 4.8, 200),
(1, '皇家猫粮', '高品质猫粮，适合各种猫种', 100.00, 40, 'https://example.com/product2.jpg', '宠物粮食', 10, 'enabled', 4.7, 150),
(1, '宠物牵引绳', '耐用宠物牵引绳，适合各种宠物', 30.00, 100, 'https://example.com/product3.jpg', '宠物用品', 20, 'enabled', 4.6, 80),
(2, '宠物玩具球', '耐咬宠物玩具球，适合各种宠物', 20.00, 200, 'https://example.com/product4.jpg', '宠物玩具', 50, 'enabled', 4.5, 120),
(2, '宠物窝', '舒适宠物窝，适合各种宠物', 80.00, 30, 'https://example.com/product5.jpg', '宠物用品', 5, 'enabled', 4.4, 60);

-- =============================================
-- 11. 预约表 (appointment) 测试数据
-- =============================================

INSERT INTO appointment (user_id, merchant_id, service_id, pet_id, appointment_time, status, total_price, notes, order_no) VALUES
(1, 1, 1, 1, '2026-04-21 10:00:00', 'pending', 50.00, '需要特殊护理', 'APT20260421001'),
(1, 1, 2, 2, '2026-04-22 14:00:00', 'confirmed', 100.00, '需要修剪造型', 'APT20260422001'),
(2, 1, 3, 3, '2026-04-23 09:00:00', 'completed', 80.00, '需要每天遛弯', 'APT20260423001'),
(3, 2, 4, 4, '2026-04-24 11:00:00', 'cancelled', 150.00, '临时有事', 'APT20260424001'),
(4, 2, 5, 5, '2026-04-25 15:00:00', 'pending', 200.00, '需要全面检查', 'APT20260425001');

-- =============================================
-- 12. 商品订单表 (product_order) 测试数据
-- =============================================

INSERT INTO product_order (user_id, merchant_id, total_price, status, shipping_address, order_no, pay_method, remark) VALUES
(1, 1, 150.00, 'pending', '北京市朝阳区建国路88号', 'ORD20260421001', '微信支付', '尽快发货'),
(1, 1, 30.00, 'paid', '北京市朝阳区建国路88号', 'ORD20260422001', '支付宝', '无'),
(2, 2, 100.00, 'shipped', '北京市海淀区中关村大街1号', 'ORD20260423001', '微信支付', '请包装好', '2026-04-23 10:00:00'),
(3, 1, 120.00, 'completed', '北京市西城区西单北大街120号', 'ORD20260424001', '支付宝', '已收到', '2026-04-24 14:00:00'),
(4, 2, 80.00, 'cancelled', '北京市东城区王府井大街99号', 'ORD20260425001', '微信支付', '不想要了', '2026-04-25 09:00:00');

-- =============================================
-- 13. 商品订单项表 (product_order_item) 测试数据
-- =============================================

INSERT INTO product_order_item (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 120.00), -- 皇家狗粮
(1, 3, 1, 30.00),  -- 宠物牵引绳
(2, 3, 1, 30.00),  -- 宠物牵引绳
(3, 4, 5, 20.00),  -- 宠物玩具球
(4, 1, 1, 120.00), -- 皇家狗粮
(5, 5, 1, 80.00);  -- 宠物窝

-- =============================================
-- 14. 评价表 (review) 测试数据
-- =============================================

INSERT INTO review (user_id, merchant_id, service_id, appointment_id, rating, comment, image, status) VALUES
(1, 1, 1, 1, 5, '服务非常好，狗狗洗得很干净', 'https://example.com/review1.jpg', 'approved'),
(1, 1, 2, 2, 4, '美容效果不错，就是等待时间有点长', 'https://example.com/review2.jpg', 'approved'),
(2, 1, 3, 3, 5, '寄养服务很专业，狗狗很开心', 'https://example.com/review3.jpg', 'approved'),
(3, 2, 4, 4, 3, '训练效果一般，希望能再专业一些', NULL, 'approved'),
(4, 2, 5, 5, 4, '医疗服务很专业，医生很耐心', 'https://example.com/review4.jpg', 'pending');

-- =============================================
-- 15. 收藏表 (favorite) 测试数据
-- =============================================

INSERT INTO favorite (user_id, merchant_id, service_id, product_id) VALUES
(1, 1, 1, NULL),  -- 收藏宠物乐园的宠物洗澡服务
(1, 1, NULL, 1),  -- 收藏宠物乐园的皇家狗粮
(2, 2, 4, NULL),  -- 收藏宠物之家的宠物训练服务
(3, 1, NULL, 3),  -- 收藏宠物乐园的宠物牵引绳
(4, 2, NULL, 4);  -- 收藏宠物之家的宠物玩具球

-- =============================================
-- 16. 地址表 (address) 测试数据
-- =============================================

INSERT INTO address (user_id, contact_name, phone, province, city, district, detail_address, is_default) VALUES
(1, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号', 1),
(1, '张三', '13800138001', '北京市', '北京市', '海淀区', '中关村大街1号', 0),
(2, '李四', '13800138002', '北京市', '北京市', '海淀区', '中关村大街1号', 1),
(3, '王五', '13800138003', '北京市', '北京市', '西城区', '西单北大街120号', 1),
(4, '赵六', '13800138004', '北京市', '北京市', '东城区', '王府井大街99号', 1);

-- =============================================
-- 17. 活动表 (activity) 测试数据
-- =============================================

INSERT INTO activity (name, description, start_time, end_time, status, type, organizer, location, max_participants, current_participants) VALUES
('宠物交流会', '宠物爱好者交流活动', '2026-05-01 09:00:00', '2026-05-01 16:00:00', 'active', '线下活动', '宠物乐园', '北京市朝阳区建国路88号', 50, 20),
('宠物训练讲座', '专业宠物训练讲座', '2026-05-15 14:00:00', '2026-05-15 16:00:00', 'active', '线上活动', '宠物之家', '线上直播', 100, 50),
('宠物摄影大赛', '宠物摄影比赛', '2026-06-01 00:00:00', '2026-06-30 23:59:59', 'pending', '线上活动', '宠物王国', '线上平台', 200, 0);

-- =============================================
-- 18. 通知表 (notification) 测试数据
-- =============================================

INSERT INTO notification (user_id, title, content, type, is_read) VALUES
(1, '预约提醒', '您的宠物洗澡预约时间为2026-04-21 10:00:00', 'appointment', 0),
(1, '订单提醒', '您的订单ORD20260421001已支付成功', 'order', 1),
(2, '评价提醒', '您的服务评价已审核通过', 'review', 0),
(3, '活动提醒', '宠物交流会活动即将开始', 'activity', 0),
(4, '系统通知', '系统将于2026-04-30进行维护', 'system', 1);

-- =============================================
-- 19. 搜索历史表 (search_history) 测试数据
-- =============================================

INSERT INTO search_history (user_id, keyword) VALUES
(1, '宠物洗澡'),
(1, '皇家狗粮'),
(2, '宠物训练'),
(3, '宠物牵引绳'),
(4, '宠物玩具');

-- =============================================
-- 20. 商家设置表 (merchant_settings) 测试数据
-- =============================================

INSERT INTO merchant_settings (merchant_id, is_open, appointment_notification, order_notification, review_notification, notification_type) VALUES
(1, 1, 1, 1, 1, 'sms,email'),
(2, 1, 1, 1, 1, 'sms'),
(3, 0, 0, 0, 0, 'email'),
(4, 0, 0, 0, 0, 'sms,email'),
(5, 1, 1, 1, 1, 'email');

-- =============================================
-- 21. 论坛帖子表 (forum_post) 测试数据
-- =============================================

INSERT INTO forum_post (user_id, title, content, view_count, like_count) VALUES
(1, '宠物洗澡技巧分享', '分享一些给宠物洗澡的小技巧...', 100, 10),
(2, '宠物训练经验交流', '交流宠物训练的经验和方法...', 80, 8),
(3, '宠物粮食选择指南', '如何选择适合宠物的粮食...', 120, 15),
(4, '宠物健康护理', '宠物日常健康护理的重要性...', 90, 12),
(5, '宠物玩具推荐', '推荐几款适合宠物的玩具...', 70, 5);

-- =============================================
-- 22. 论坛评论表 (forum_comment) 测试数据
-- =============================================

INSERT INTO forum_comment (post_id, user_id, content) VALUES
(1, 2, '非常实用的技巧，谢谢分享！'),
(1, 3, '请问用什么洗发水比较好？'),
(2, 1, '训练效果确实不错，我试过了！'),
(3, 4, '这个指南很详细，感谢！'),
(4, 5, '健康护理确实很重要，赞同！');

-- =============================================
-- 23. 操作日志表 (operation_log) 测试数据
-- =============================================

INSERT INTO operation_log (admin_id, admin_name, action, target_type, target_id, detail, ip_address) VALUES
(1, 'admin', '添加用户', 'user', 1, '添加了新用户user1', '127.0.0.1'),
(1, 'admin', '审核商家', 'merchant', 1, '审核通过了商家宠物乐园', '127.0.0.1'),
(2, 'operator', '处理订单', 'order', 1, '处理了订单ORD20260421001', '127.0.0.1'),
(1, 'admin', '添加服务', 'service', 1, '添加了新服务宠物洗澡', '127.0.0.1'),
(2, 'operator', '处理评价', 'review', 1, '审核通过了评价', '127.0.0.1');

-- =============================================
-- 24. 公告表 (announcement) 测试数据
-- =============================================

INSERT INTO announcement (title, content, status) VALUES
('系统更新通知', '系统将于2026-04-30进行更新维护，届时系统将暂时无法访问。', 'published'),
('活动预告', '宠物交流会活动将于2026-05-01举行，欢迎大家参加！', 'published'),
('新功能上线', '系统新增了宠物健康管理功能，欢迎使用！', 'draft');

-- =============================================
-- 25. 定时任务表 (scheduled_task) 测试数据
-- =============================================

INSERT INTO scheduled_task (name, description, cron_expression, status, type) VALUES
('每日数据统计', '每日统计系统数据', '0 0 0 * * ?', 'active', 'system'),
('订单状态检查', '检查超时未支付的订单', '0 0 */1 * * ?', 'active', 'order'),
('活动状态更新', '更新活动状态', '0 0 0 * * ?', 'active', 'activity');

-- =============================================
-- 26. 系统配置表 (system_config) 测试数据
-- =============================================

INSERT INTO system_config (site_name, site_description, site_keywords, logo, contact_phone, contact_email, footer_text, icp_number) VALUES
('宠物管理系统', '专业的宠物管理系统', '宠物,管理,服务', 'https://example.com/logo.jpg', '13800138000', 'contact@example.com', '© 2026 宠物管理系统 版权所有', '京ICP备12345678号');

-- =============================================
-- 27. 系统设置表 (system_settings) 测试数据
-- =============================================

INSERT INTO system_settings (wechat_app_id, wechat_app_secret, wechat_mch_id, wechat_pay_key, alipay_app_id, alipay_private_key, alipay_public_key, email_smtp, email_username, email_password, email_port, email_from, sms_provider, sms_api_key, sms_api_secret, sms_sign_name, allowed_file_types, max_file_size, upload_path) VALUES
('wx1234567890', 'secret1234567890', 'mch1234567890', 'key1234567890', 'ali1234567890', 'private_key', 'public_key', 'smtp.qq.com', 'admin@example.com', 'password', 465, 'admin@example.com', 'aliyun', 'api_key', 'api_secret', '宠物管理系统', 'jpg,png,gif', 10485760, '/uploads');

-- =============================================
-- 28. 购物车表 (cart) 测试数据
-- =============================================

INSERT INTO cart (user_id, product_id, quantity) VALUES
(1, 1, 1),  -- 用户1添加皇家狗粮到购物车
(1, 3, 2),  -- 用户1添加宠物牵引绳到购物车
(2, 4, 3),  -- 用户2添加宠物玩具球到购物车
(3, 1, 1),  -- 用户3添加皇家狗粮到购物车
(4, 5, 1);  -- 用户4添加宠物窝到购物车

-- =============================================
-- 测试账号
-- =============================================

-- 平台管理员账号
-- 用户名: admin
-- 密码: 123456
-- 权限: 所有权限

-- 运营管理员账号
-- 用户名: operator
-- 密码: 123456
-- 权限: 用户管理、商家管理、订单管理、评价管理

-- 商家账号
-- 用户名: merchant1@example.com
-- 密码: 123456
-- 权限: 服务管理、商品管理、订单管理、评价管理

-- 普通用户账号
-- 用户名: user1@example.com
-- 密码: 123456
-- 权限: 查看服务、预约服务、购买商品、评价服务

-- =============================================
-- 验证步骤
-- =============================================

-- 验证测试数据生成
SELECT '用户表测试数据' AS table_name, COUNT(*) AS count FROM user;
SELECT '商家表测试数据' AS table_name, COUNT(*) AS count FROM merchant;
SELECT '服务表测试数据' AS table_name, COUNT(*) AS count FROM service;
SELECT '商品表测试数据' AS table_name, COUNT(*) AS count FROM product;
SELECT '预约表测试数据' AS table_name, COUNT(*) AS count FROM appointment;
SELECT '商品订单表测试数据' AS table_name, COUNT(*) AS count FROM product_order;
SELECT '评价表测试数据' AS table_name, COUNT(*) AS count FROM review;
SELECT '宠物表测试数据' AS table_name, COUNT(*) AS count FROM pet;

-- 提交事务
COMMIT;

-- 脚本执行完成
SELECT '测试数据生成完成' AS message;
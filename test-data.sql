-- 测试数据插入脚本
SET NAMES utf8mb4;

-- 1. 商家表
INSERT INTO merchant (id, name, contact_person, phone, email, password, address, status, created_at, updated_at)
VALUES 
(4001, '爱心宠物美容会所', '张经理', '13666666661', 'grooming4@example.com', '$2a$10$eS5vZq7s8j5f6g7h8i9j0k', '北京市朝阳区建国路88号', 'approved', NOW(), NOW()),
(4002, '宠物健康医院', '李医生', '13666666662', 'hospital4@example.com', '$2a$10$eS5vZq7s8j5f6g7h8i9j0k', '北京市海淀区中关村大街1号', 'approved', NOW(), NOW()),
(4003, '快乐宠物寄养中心', '王主管', '13666666663', 'boarding4@example.com', '$2a$10$eS5vZq7s8j5f6g7h8i9j0k', '北京市丰台区丰台路100号', 'approved', NOW(), NOW()),
(4004, '宠物用品专卖店', '赵老板', '13666666664', 'store4@example.com', '$2a$10$eS5vZq7s8j5f6g7h8i9j0k', '北京市西城区西单大街120号', 'approved', NOW(), NOW());

-- 2. 用户表 - 密码: 123456
INSERT INTO user (id, username, password, email, phone, status, created_at, updated_at)
VALUES 
(4001, '测试用户', '$2a$10$Qa7T5jKj9y6yKj9y6yKj9e7e7e7e7e7e7e7e7e7e7e7e7e7e7e7e7e7', 'user4@example.com', '13666666665', '1', NOW(), NOW());

-- 3. 宠物表
INSERT INTO pet (id, user_id, name, type, breed, age, gender, description, created_at, updated_at)
VALUES 
(4001, 4001, '小白', '狗', '萨摩耶', 3, 'male', '活泼可爱的萨摩耶', NOW(), NOW()),
(4002, 4001, '小黑', '猫', '英短', 2, 'female', '温顺的英短黑猫', NOW(), NOW());

-- 4. 服务表
INSERT INTO service (id, merchant_id, name, description, price, duration, status, created_at, updated_at)
VALUES 
(4001, 4001, '宠物洗澡美容套餐', '包含洗澡、剪毛、修指甲等服务', 88, 90, '1', NOW(), NOW()),
(4002, 4002, '宠物健康体检', '全面的宠物健康检查', 150, 60, '1', NOW(), NOW()),
(4003, 4003, '宠物寄养服务', '提供舒适的寄养环境', 50, 1440, '1', NOW(), NOW());

-- 5. 商品表
INSERT INTO product (id, merchant_id, name, description, price, stock, status, created_at, updated_at)
VALUES 
(4001, 4004, '宠物粮食 成犬专用', '高品质成犬粮，营养均衡', 258.9, 100, '1', NOW(), NOW()),
(4002, 4004, '宠物玩具 发声球', '狗狗喜爱的发声玩具', 39.5, 200, '1', NOW(), NOW()),
(4003, 4004, '宠物牵引绳', '结实耐用的牵引绳', 49.9, 150, '1', NOW(), NOW());

-- 6. 服务预约表
INSERT INTO appointment (id, user_id, merchant_id, service_id, pet_id, appointment_time, status, total_price, notes, created_at, updated_at)
VALUES 
(4001, 4001, 4001, 4001, 4001, '2024-01-25 14:00:00', 'pending', 88, '需要给狗狗剪指甲', NOW(), NOW()),
(4002, 4001, 4002, 4002, 4002, '2024-01-20 10:30:00', 'confirmed', 150, '年度体检', NOW(), NOW()),
(4003, 4001, 4003, 4003, 4001, '2024-01-15 09:00:00', 'completed', 50, '寄养3天', NOW(), NOW()),
(4004, 4001, 4001, 4001, 4002, '2024-01-10 15:00:00', 'cancelled', 88, '临时有事取消', NOW(), NOW());

-- 7. 商品订单表
INSERT INTO product_order (id, user_id, merchant_id, order_no, total_price, status, shipping_address, created_at, updated_at)
VALUES 
(4001, 4001, 4004, 'PS202404200001', 258.9, 'pending', '北京市朝阳区建国路88号', NOW(), NOW()),
(4002, 4001, 4004, 'PS202404190002', 79, 'paid', '北京市朝阳区建国路88号', NOW(), NOW()),
(4003, 4001, 4004, 'PS202404180003', 49.9, 'shipped', '北京市朝阳区建国路88号', NOW(), NOW()),
(4004, 4001, 4004, 'PS202404150004', 129.9, 'completed', '北京市朝阳区建国路88号', NOW(), NOW());

-- 8. 商品订单详情表
INSERT INTO product_order_item (id, order_id, product_id, quantity, price)
VALUES 
(4001, 4001, 4001, 1, 258.9),
(4002, 4002, 4002, 2, 39.5),
(4003, 4003, 4003, 1, 49.9),
(4004, 4004, 4001, 0.5, 129.9);

-- 9. 收藏表
INSERT INTO favorite (id, user_id, merchant_id, created_at)
VALUES 
(4001, 4001, 4001, NOW()),
(4002, 4001, 4002, NOW()),
(4003, 4001, 4003, NOW()),
(4004, 4001, 4004, NOW());

-- 10. 评价表
INSERT INTO review (id, user_id, merchant_id, service_id, appointment_id, rating, comment, created_at)
VALUES 
(4001, 4001, 4001, 4001, 4003, 5, '服务非常好，狗狗很喜欢', NOW()),
(4002, 4001, 4002, 4002, 4002, 4, '医生很专业，检查很仔细', NOW()),
(4003, 4001, 4003, 4003, 4003, 5, '寄养环境很好，狗狗很开心', NOW());
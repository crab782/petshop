# 为商家13912340001添加测试数据计划

## 问题分析

### 1. 当前状态
- 商家13912340001 (ID: 4006) 状态为approved
- 但该商家没有任何服务(service)
- 因此无法产生评价(review)

### 2. 商家注册逻辑
- 商家注册时只创建merchant记录
- **不自动创建服务**，服务需要商家手动添加
- 评价数据来源于用户对商家服务的评价

### 3. 数据依赖关系
```
merchant (商家)
    └── service (服务) - 商家手动添加
            └── review (评价) - 用户对服务评价
```

## 解决方案

给商家4006添加测试数据：
1. 添加3个服务
2. 添加3个评价（关联用户、服务）

### 具体步骤

1. 添加服务数据
```sql
INSERT INTO service (merchant_id, name, description, price, duration, status, rating, reservation_count, created_at, updated_at)
VALUES
(4006, '宠物洗澡', '专业宠物洗澡服务', 50.00, 60, 'enabled', 5.00, 10, NOW(), NOW()),
(4006, '宠物美容', '专业宠物美容造型', 80.00, 90, 'enabled', 4.50, 5, NOW(), NOW()),
(4006, '宠物寄养', '安全舒适的宠物寄养服务', 100.00, 1440, 'enabled', 4.00, 3, NOW(), NOW());
```

2. 添加评价数据
```sql
INSERT INTO review (user_id, merchant_id, service_id, rating, content, created_at)
VALUES
(1, 4006, (SELECT id FROM service WHERE merchant_id=4006 AND name='宠物洗澡' LIMIT 1), 5, '服务非常好！', NOW()),
(2, 4006, (SELECT id FROM service WHERE merchant_id=4006 AND name='宠物美容' LIMIT 1), 4, '效果不错', NOW()),
(3, 4006, (SELECT id FROM service WHERE merchant_id=4006 AND name='宠物寄养' LIMIT 1), 5, '很放心在这里寄养', NOW());
```

## 验证步骤

1. 执行SQL后验证数据
2. 使用商家账号登录
3. 访问评价页面 http://localhost:5173/merchant/reviews
4. 检查是否显示评价数据

## 风险评估

### 低风险
- 测试数据使用INSERT SELECT避免硬编码ID
- 商家ID 4006是确定的
- 只添加数据，不删除或修改现有数据
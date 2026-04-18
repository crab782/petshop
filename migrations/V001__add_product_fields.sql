-- Product表字段迁移脚本
-- 添加status、low_stock_threshold、category字段

-- 添加status字段（商品状态：enabled-启用, disabled-禁用）
ALTER TABLE `product` 
ADD COLUMN `status` ENUM('enabled', 'disabled') DEFAULT 'enabled' AFTER `image`;

-- 添加low_stock_threshold字段（库存预警阈值）
ALTER TABLE `product` 
ADD COLUMN `low_stock_threshold` INT DEFAULT 10 AFTER `status`;

-- 添加category字段（商品分类）
ALTER TABLE `product` 
ADD COLUMN `category` VARCHAR(50) AFTER `low_stock_threshold`;

-- 为category字段添加索引以提高查询性能
CREATE INDEX `idx_product_category` ON `product` (`category`);

-- 为status字段添加索引以提高查询性能
CREATE INDEX `idx_product_status` ON `product` (`status`);

-- 为merchant_id和status组合添加索引以提高商家商品查询性能
CREATE INDEX `idx_product_merchant_status` ON `product` (`merchant_id`, `status`);

-- 为merchant_id和category组合添加索引以提高商家分类商品查询性能
CREATE INDEX `idx_product_merchant_category` ON `product` (`merchant_id`, `category`);

-- 更新现有数据，将所有现有商品状态设置为enabled
UPDATE `product` SET `status` = 'enabled' WHERE `status` IS NULL;

-- 更新现有数据，设置默认库存预警阈值
UPDATE `product` SET `low_stock_threshold` = 10 WHERE `low_stock_threshold` IS NULL;

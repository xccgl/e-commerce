-- =====================================================
-- 电商订单模块数据库表结构
-- 数据库: ecommerce
-- 创建时间: 2024年
-- =====================================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 3. 订单主表
CREATE TABLE IF NOT EXISTS `order_main` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 4. 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称(快照)',
    `product_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片(快照)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价(快照)',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 插入测试用户
INSERT INTO `sys_user` (`username`, `password`, `email`, `phone`, `nickname`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '13800138000', '管理员'),
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'user001@example.com', '13800138001', '测试用户');

-- 插入测试商品
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `category`) VALUES
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB 钛金色', 8999.00, 100, '手机'),
('MacBook Pro 14', 'Apple MacBook Pro 14英寸 M3 Pro芯片', 14999.00, 50, '电脑'),
('AirPods Pro 2', 'Apple AirPods Pro 第二代 USB-C', 1899.00, 200, '配件'),
('iPad Air', 'Apple iPad Air 第五代 M1芯片', 4799.00, 80, '平板'),
('Watch Series 9', 'Apple Watch Series 9 45mm 蜂窝版', 3999.00, 60, '手表');

-- 插入测试订单
INSERT INTO `order_main` (`order_no`, `user_id`, `total_amount`, `status`, `receiver_name`, `receiver_phone`, `receiver_address`) VALUES
('ORD202401150001', 1, 8999.00, 'COMPLETED', '张三', '13800138000', '北京市朝阳区建国路88号'),
('ORD202401150002', 1, 1899.00, 'PENDING', '张三', '13800138000', '北京市朝阳区建国路88号'),
('ORD202401150003', 2, 14999.00, 'COMPLETED', '李四', '13800138001', '上海市浦东新区世纪大道100号');

-- 插入测试订单明细
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, 'iPhone 15 Pro', 8999.00, 1, 8999.00),
(2, 3, 'AirPods Pro 2', 1899.00, 1, 1899.00),
(3, 2, 'MacBook Pro 14', 14999.00, 1, 14999.00);

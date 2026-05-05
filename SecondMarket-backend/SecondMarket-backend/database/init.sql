DROP DATABASE IF EXISTS `second_market`;
CREATE DATABASE IF NOT EXISTS `second_market` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `second_market`;

CREATE USER IF NOT EXISTS 'second_market'@'localhost' IDENTIFIED BY 'second_market';
CREATE USER IF NOT EXISTS 'second_market'@'%' IDENTIFIED BY 'second_market';
GRANT ALL PRIVILEGES ON `second_market`.* TO 'second_market'@'localhost';
GRANT ALL PRIVILEGES ON `second_market`.* TO 'second_market'@'%';
FLUSH PRIVILEGES;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `gender` CHAR(1) DEFAULT 'U' COMMENT '性别 (M-男, F-女, U-未知)',
    `age` INT(3) DEFAULT NULL COMMENT '年龄',
    `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `avatar` VARCHAR(255) DEFAULT 'https://api.dicebear.com/7.x/avataaars/svg?seed=default' COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '用户角色 (admin-管理员, user-普通用户)',
    `status` INT(1) NOT NULL DEFAULT 1 COMMENT '账户状态 (0-封禁, 1-正常)',
    `credit_score` INT(3) NOT NULL DEFAULT 100 COMMENT '信用评分',
    `school_id` BIGINT DEFAULT NULL COMMENT '所属学校ID',  -- 直接加在这里
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除 (0-未删除, 1-已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表';

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '发布用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `category` VARCHAR(50) NOT NULL COMMENT '商品分类',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `condition_score` INT(2) NOT NULL DEFAULT 8 COMMENT '成色评分(1-10分)',
    `condition_desc` VARCHAR(500) DEFAULT NULL COMMENT '成色说明',
    `images` TEXT COMMENT '商品图片(JSON数组)',
    `status` INT(1) NOT NULL DEFAULT 1 COMMENT '状态(0-草稿,1-待审核,2-已上架,3-已下架,4-已售出)',
    `view_count` INT(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
    `favorite_count` INT(11) NOT NULL DEFAULT 0 COMMENT '收藏数',
    `ai_analyzed` INT(1) NOT NULL DEFAULT 0 COMMENT '是否AI分析(0-否,1-是)',
    `ai_suggestions` TEXT COMMENT 'AI分析结果(JSON)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_price` (`price`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表';

-- 订单表
DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `buyer_id` BIGINT(20) NOT NULL COMMENT '买家ID',
    `seller_id` BIGINT(20) NOT NULL COMMENT '卖家ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `status` INT(1) NOT NULL DEFAULT 0 COMMENT '状态(0-待支付,1-已支付,2-已完成,3-已取消)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表';

-- 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表';

-- 评价表
DROP TABLE IF EXISTS `review`;
CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
    `reviewer_id` BIGINT(20) NOT NULL COMMENT '评价人ID',
    `reviewee_id` BIGINT(20) NOT NULL COMMENT '被评价人ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `rating` INT(1) NOT NULL COMMENT '评分(1-5分)',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_reviewee_id` (`reviewee_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表';

-- 举报表
DROP TABLE IF EXISTS `report`;
CREATE TABLE IF NOT EXISTS `report` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `reporter_id` BIGINT(20) NOT NULL COMMENT '举报人ID',
    `target_type` INT(1) NOT NULL COMMENT '举报类型(1-商品,2-用户)',
    `target_id` BIGINT(20) NOT NULL COMMENT '举报对象ID',
    `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
    `status` INT(1) NOT NULL DEFAULT 0 COMMENT '状态(0-待处理,1-已处理,2-已驳回)',
    `handle_result` VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_reporter_id` (`reporter_id`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '举报表';

-- 公告表
DROP TABLE IF EXISTS `notice`;
CREATE TABLE IF NOT EXISTS `notice` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(100) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` INT(1) NOT NULL DEFAULT 0 COMMENT '类型(0-系统公告,1-活动公告,2-维护通知)',
    `status` INT(1) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已发布,2-已下架)',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表';

-- 消息表
DROP TABLE IF EXISTS `message`;
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` INT(1) NOT NULL DEFAULT 0 COMMENT '类型(0-系统通知,1-订单通知,2-评价通知)',
    `is_read` INT(1) NOT NULL DEFAULT 0 COMMENT '是否已读(0-未读,1-已读)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表';

-- 文件信息表
DROP TABLE IF EXISTS `sys_file_info`;
CREATE TABLE IF NOT EXISTS `sys_file_info` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '文件原始名称',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件存储名称',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT(20) NOT NULL COMMENT '文件大小（字节）',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
    `extension` VARCHAR(10) DEFAULT NULL COMMENT '文件扩展名',
    `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5值',
    `file_url` VARCHAR(255) DEFAULT NULL COMMENT '文件访问URL',
    `user_id` BIGINT(20) NOT NULL COMMENT '上传用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_md5` (`md5`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件信息表';

-- 插入管理员账户
INSERT INTO `sys_user` (`username`, `nickname`, `password`, `email`, `phone`, `address`, `gender`, `age`, `avatar`, `role`, `status`, `credit_score`, `create_time`, `update_time`, `deleted`) VALUES ('admin', '系统管理员', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'admin@secondmarket.com', '13800138000', '北京市朝阳区', 'M', 30, 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 'admin', 1, 100, '2024-01-15 08:30:00', '2024-01-15 08:30:00', 0);

-- 插入普通用户
INSERT INTO `sys_user` (`username`, `nickname`, `password`, `email`, `phone`, `address`, `gender`, `age`, `avatar`, `role`, `status`, `credit_score`, `create_time`, `update_time`, `deleted`) VALUES ('user01', '张三', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'zhangsan@example.com', '13912345001', '上海市浦东新区', 'M', 28, 'https://api.dicebear.com/7.x/avataaars/svg?seed=user01', 'user', 1, 98, '2024-10-01 09:15:00', '2024-11-09 14:30:00', 0), ('user02', '李四', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'lisi@example.com', '13912345002', '广州市天河区', 'F', 25, 'https://api.dicebear.com/7.x/avataaars/svg?seed=user02', 'user', 1, 100, '2024-10-05 10:20:00', '2024-11-09 16:45:00', 0), ('user03', '王五', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'wangwu@example.com', '13912345003', '深圳市南山区', 'M', 32, 'https://api.dicebear.com/7.x/avataaars/svg?seed=user03', 'user', 1, 95, '2024-10-10 11:30:00', '2024-11-08 08:20:00', 0);

-- 插入商品数据
INSERT INTO `product` (`user_id`, `title`, `description`, `category`, `price`, `original_price`, `condition_score`, `condition_desc`, `images`, `status`, `view_count`, `favorite_count`, `ai_analyzed`, `ai_suggestions`, `create_time`, `update_time`, `deleted`) VALUES (2, 'iPhone 13 Pro 256G 石墨色', '自用iPhone 13 Pro，256G存储，石墨色，9成新，无磕碰，屏幕完好，电池健康度92%，配原装充电线和20W充电头。因换新机出售，诚心转让。', '电子产品', 4800.00, 7999.00, 9, '外观完好无划痕，屏幕无瑕疵，功能正常，电池健康度92%', '["https://img0.baidu.com/it/u=3377345798,789706373&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1422", "https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=800"]', 2, 156, 23, 1, '{"name":"iPhone 13 Pro","brand":"Apple","priceRange":"4500-5200","score":9}', '2024-11-01 09:30:00', '2024-11-09 14:20:00', 0), (3, '索尼WH-1000XM4 无线降噪耳机', '索尼旗舰降噪耳机，黑色，95新，音质出众，降噪效果极佳，续航30小时+，配原装收纳盒和充电线。因升级新款出售。', '电子产品', 1480.00, 2499.00, 9, '外观轻微使用痕迹，耳罩完好，降噪和音质表现优秀', '["https://img1.baidu.com/it/u=1198486088,1233730109&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=666", "https://img1.baidu.com/it/u=731038073,505606146&fm=253&fmt=auto&app=138&f=JPEG?w=667&h=500"]', 2, 89, 15, 1, '{"name":"WH-1000XM4","brand":"Sony","priceRange":"1400-1600","score":9}', '2024-11-02 10:15:00', '2024-11-09 15:30:00', 0), (2, '戴森V8 Fluffy吸尘器', '戴森V8无线吸尘器，使用1年，8成新，吸力强劲，续航正常，配全套吸头和充电底座。搬家闲置出售。', '家用电器', 1299.00, 2990.00, 8, '机身有轻微使用痕迹，吸头完好，功能正常', '["https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800", "https://images.unsplash.com/photo-1585659722983-3a675dabf23d?w=800"]', 2, 67, 12, 0, NULL, '2024-11-03 14:45:00', '2024-11-09 10:25:00', 0), (4, 'Nike Air Max 270 运动鞋', 'Nike Air Max 270，黑白配色，42码，8.5成新，穿过5-6次，鞋面干净，鞋底磨损轻微，适合跑步和日常穿着。', '服装配饰', 380.00, 1099.00, 8, '鞋面清洁，鞋底轻微磨损，无异味', '["https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800", "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=800"]', 2, 125, 18, 0, NULL, '2024-11-04 16:20:00', '2024-11-09 11:40:00', 0), (3, '小米65英寸4K电视', '小米电视4S 65英寸，4K超高清，HDR，人工智能语音，使用2年，9成新，画质清晰，功能正常，配原装遥控器。', '家用电器', 1999.00, 3299.00, 9, '屏幕无坏点，外观完好，功能正常', '["https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800", "https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=800"]', 2, 98, 20, 0, NULL, '2024-11-05 09:00:00', '2024-11-09 13:15:00', 0), (2, '雅马哈电钢琴P-125', '雅马哈P-125电钢琴，88键重锤，黑色，9成新，音色出色，手感接近真钢琴，配琴架和踏板。学生练琴神器。', '运动户外', 2888.00, 4699.00, 9, '琴键完好，音色正常，外观无划痕', '["https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=800", "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?w=800"]', 2, 43, 9, 0, NULL, '2024-11-06 11:30:00', '2024-11-09 09:50:00', 0), (4, '理光GR3相机', '理光GR3便携相机，2400万像素，APS-C画幅，街拍利器，9成新，快门数不到5000，配原装电池和充电器。', '电子产品', 4200.00, 6299.00, 9, '机身成色好，镜头无霉无灰，快门数低', '["https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800", "https://images.unsplash.com/photo-1606980707965-75c0a49c3f6f?w=800"]', 2, 76, 14, 1, '{"name":"GR3","brand":"Ricoh","priceRange":"4000-4500","score":9}', '2024-11-07 13:40:00', '2024-11-09 12:30:00', 0), (3, '《深入理解计算机系统》第三版', 'CSAPP第三版中文版，计算机经典教材，9.5成新，无笔记，书角略有折痕，适合计算机专业学生和程序员。', '图书音像', 68.00, 139.00, 9, '书籍内页完好，无笔记涂写，封面略有使用痕迹', '["https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=800"]', 2, 52, 8, 0, NULL, '2024-11-08 15:10:00', '2024-11-09 08:45:00', 0), (2, '联想拯救者R9000P笔记本', '联想拯救者R9000P，R7-5800H，RTX3060，16G内存，512G固态，2K 165Hz屏，9成新，性能强劲，适合游戏和设计。', '电子产品', 5999.00, 8999.00, 9, '外观完好，屏幕无坏点，性能正常，散热良好', '["https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800", "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800"]', 2, 142, 25, 1, '{"name":"R9000P","brand":"Lenovo","priceRange":"5800-6500","score":9}', '2024-11-08 18:25:00', '2024-11-09 16:10:00', 0), (4, '阿迪达斯三叶草卫衣', '阿迪达斯三叶草经典款卫衣，黑色，M码，8成新，穿过几次，无破损起球，版型好看，适合日常穿着。', '服装配饰', 188.00, 599.00, 8, '衣服清洁，无破损，无明显起球', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800"]', 2, 91, 16, 0, NULL, '2024-11-09 10:00:00', '2024-11-09 14:55:00', 0);

-- 插入订单数据
INSERT INTO `orders` (`order_no`, `buyer_id`, `seller_id`, `product_id`, `amount`, `status`, `remark`, `pay_time`, `finish_time`, `create_time`, `update_time`, `deleted`) VALUES ('202411050930451234567', 3, 2, 1, 4800.00, 2, '已确认收货', '2024-11-05 09:31:20', '2024-11-08 14:20:30', '2024-11-05 09:30:45', '2024-11-08 14:20:30', 0), ('202411061015232345678', 4, 3, 2, 1480.00, 2, '商品完好', '2024-11-06 10:16:10', '2024-11-09 09:15:20', '2024-11-06 10:15:23', '2024-11-09 09:15:20', 0), ('202411071445303456789', 2, 3, 3, 1299.00, 1, '待发货', '2024-11-07 14:46:15', NULL, '2024-11-07 14:45:30', '2024-11-07 14:46:15', 0), ('202411081620454567890', 3, 4, 4, 380.00, 1, '待发货', '2024-11-08 16:21:30', NULL, '2024-11-08 16:20:45', '2024-11-08 16:21:30', 0);

-- 插入收藏数据
INSERT INTO `favorite` (`user_id`, `product_id`, `create_time`, `deleted`) VALUES (2, 2, '2024-11-02 10:20:00', 0), (2, 5, '2024-11-05 09:10:00', 0), (2, 7, '2024-11-07 13:50:00', 0), (3, 1, '2024-11-01 10:00:00', 0), (3, 4, '2024-11-04 16:30:00', 0), (3, 9, '2024-11-08 18:40:00', 0), (4, 3, '2024-11-03 15:00:00', 0), (4, 6, '2024-11-06 11:45:00', 0), (4, 8, '2024-11-08 15:20:00', 0);

-- 插入评价数据
INSERT INTO `review` (`order_id`, `reviewer_id`, `reviewee_id`, `product_id`, `rating`, `content`, `create_time`, `deleted`) VALUES (1, 3, 2, 1, 5, '手机收到了，成色很好，和描述一致，卖家很诚信，推荐！', '2024-11-08 15:30:00', 0), (2, 4, 3, 2, 5, '耳机音质很棒，降噪效果确实好，卖家人很nice，交易愉快！', '2024-11-09 10:00:00', 0);

-- 插入公告数据
INSERT INTO `notice` (`title`, `content`, `type`, `status`, `publish_time`, `create_time`, `update_time`, `deleted`) VALUES ('欢迎使用智能二手商城', '<p>欢迎来到智能二手商城！</p><p>我们是一个基于AI技术的二手交易平台，致力于为您提供便捷、安全、智能的二手商品交易服务。</p><p>平台特色：</p><ul><li>AI智能商品识别：上传图片即可自动识别商品信息</li><li>成色智能评估：AI自动分析商品成色</li><li>价格智能建议：根据市场行情给出合理价格</li><li>描述智能生成：一键生成吸引人的商品描述</li></ul><p>祝您交易愉快！</p>', 0, 1, '2024-11-01 09:00:00', '2024-10-31 18:00:00', '2024-11-01 09:00:00', 0), ('平台交易安全提示', '<p>为保障您的交易安全，请注意以下事项：</p><ol><li>仔细查看商品描述和图片</li><li>与卖家充分沟通商品细节</li><li>选择平台担保交易</li><li>收到商品后及时验货</li><li>发现问题及时联系客服</li></ol><p>如遇到任何问题，请随时联系我们的客服团队。</p>', 0, 1, '2024-11-05 10:00:00', '2024-11-04 17:00:00', '2024-11-05 10:00:00', 0);

-- 插入消息数据
INSERT INTO `message` (`user_id`, `title`, `content`, `type`, `is_read`, `create_time`, `deleted`) VALUES (2, '欢迎注册智能二手商城', '欢迎您注册成为智能二手商城的用户！祝您交易愉快！', 0, 1, '2024-10-01 09:15:00', 0), (2, '订单支付成功', '您购买的商品"戴森V8 Fluffy吸尘器"已支付成功，卖家将尽快发货。', 1, 0, '2024-11-07 14:46:15', 0), (3, '您的商品已售出', '您的商品"索尼WH-1000XM4 无线降噪耳机"已售出，买家已付款，请尽快发货。', 1, 1, '2024-11-06 10:16:10', 0), (3, '收到新评价', '买家给您的商品"索尼WH-1000XM4 无线降噪耳机"评价为5星，快去查看吧！', 2, 0, '2024-11-09 10:00:00', 0), (4, '欢迎注册智能二手商城', '欢迎您注册成为智能二手商城的用户！祝您交易愉快！', 0, 1, '2024-10-10 11:30:00', 0);

DROP TABLE IF EXISTS `school`;
CREATE TABLE IF NOT EXISTS `school` (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
                        school_code VARCHAR(50) NOT NULL UNIQUE COMMENT '学校编码',
                        name_zh VARCHAR(255) NOT NULL COMMENT '学校中文名称',
                        name_ko VARCHAR(255) NOT NULL COMMENT '学校韩文名称',
                        name_en VARCHAR(255) NOT NULL COMMENT '学校英文名称',
                        city_zh VARCHAR(100) DEFAULT NULL COMMENT '城市中文',
                        city_ko VARCHAR(100) DEFAULT NULL COMMENT '城市韩文',
                        city_en VARCHAR(100) DEFAULT NULL COMMENT '城市英文',
                        logo_url VARCHAR(500) DEFAULT NULL COMMENT '学校logo地址',
                        status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
                        deleted INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                        created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '学校信息表';

-- 添加外键约束
ALTER TABLE `sys_user`
    ADD CONSTRAINT `fk_user_school`
        FOREIGN KEY (`school_id`) REFERENCES `school`(`id`);

INSERT INTO school (
    school_code,
    name_zh,
    name_ko,
    name_en,
    city_zh,
    city_ko,
    city_en
) VALUES
      ('SNU', '首尔大学', '서울대학교', 'Seoul National University', '首尔', '서울', 'Seoul'),
      ('KAIST', '韩国科学技术院', '카이스트', 'KAIST', '大田', '대전', 'Daejeon'),
      ('KU', '高丽大学', '고려대학교', 'Korea University', '首尔', '서울', 'Seoul'),
      ('YU', '延世大学', '연세대학교', 'Yonsei University', '首尔', '서울', 'Seoul'),
      ('HYU', '汉阳大学', '한양대학교', 'Hanyang University', '首尔', '서울', 'Seoul'),
      ('CAU', '中央大学', '중앙대학교', 'Chung-Ang University', '首尔', '서울', 'Seoul'),
      ('POSTECH', '浦项工科大学', '포항공과대학교', 'Pohang University of Science and Technology', '浦项', '포항', 'Pohang'),
      ('KNU', '庆北大学', '경북대학교', 'Kyungpook National University', '大邱', '대구', 'Daegu'),
      ('EWHA', '梨花女子大学', '이화여자대학교', 'Ewha Womans University', '首尔', '서울', 'Seoul'),
      ('KHU', '庆熙大学', '경희대학교', 'Kyung Hee University', '首尔', '서울', 'Seoul'),
      ('PNU', '釜山大学', '부산대학교', 'Pusan National University', '釜山', '부산', 'Busan'),
      ('SGU', '西江大学', '서강대학교', 'Sogang University', '首尔', '서울', 'Seoul'),
      ('SKKU', '成均馆大学', '성균관대학교', 'Sungkyunkwan University', '首尔', '서울', 'Seoul'),
      ('SEJONG', '世宗大学', '세종대학교', 'Sejong University', '首尔', '서울', 'Seoul'),
      ('HUFS', '韩国外国语大学', '한국외국어대학교', 'Hankuk University of Foreign Studies', '首尔', '서울', 'Seoul'),
      ('KANGWON', '江原大学', '강원대학교', 'Kangwon National University', '春川', '춘천', 'Chuncheon'),
      ('UOS', '首尔市立大学', '서울시립대학교', 'University of Seoul', '首尔', '서울', 'Seoul'),
      ('KONKUK', '建国大学', '건국대학교', 'Konkuk University', '首尔', '서울', 'Seoul'),
      ('DKU', '檀国大学', '단국대학교', 'Dankook University', '龙仁', '용인', 'Yongin'),
      ('UNIST', '蔚山科学技术院', '울산과학기술원', 'Ulsan National Institute of Science and Technology', '蔚山', '울산', 'Ulsan'),
      ('DONGGUK', '东国大学', '동국대학교', 'Dongguk University', '首尔', '서울', 'Seoul'),
      ('SSU', '崇实大学', '숭실대학교', 'Soongsil University', '首尔', '서울', 'Seoul'),
      ('KOOKMIN', '国民大学', '국민대학교', 'Kookmin University', '首尔', '서울', 'Seoul'),
      ('AJOU', '亚洲大学', '아주대학교', 'Ajou University', '水原', '수원', 'Suwon'),
      ('CNU', '全南大学', '전남대학교', 'Chonnam National University', '光州', '광주', 'Gwangju'),
      ('충남대', '忠南大学', '충남대학교', 'Chungnam National University', '大田', '대전', 'Daejeon'),
      ('YEUNGNAM', '岭南大学', '영남대학교', 'Yeungnam University', '庆山', '경산', 'Gyeongsan'),
      ('HONGIK', '弘益大学', '홍익대학교', 'Hongik University', '首尔', '서울', 'Seoul'),
      ('PKNU', '釜庆大学', '부경대학교', 'Pukyong National University', '釜山', '부산', 'Busan'),
      ('GACHON', '嘉泉大学', '가천대학교', 'Gachon University', '城南', '성남', 'Seongnam'),
      ('MYONGJI', '明知大学', '명지대학교', 'Myongji University', '龙仁', '용인', 'Yongin'),
      ('JBNU', '全北大学', '전북대학교', 'Jeonbuk National University', '全州', '전주', 'Jeonju'),
      ('SMWU', '淑明女子大学', '숙명여자대학교', 'Sookmyung Women University', '首尔', '서울', 'Seoul'),
      ('GNU', '庆尚国立大学', '경상국립대학교', 'Gyeongsang National University', '晋州', '진주', 'Jinju'),
      ('INU', '仁川大学', '인천대학교', 'University of Incheon', '仁川', '인천', 'Incheon'),
      ('CBNU', '忠北大学', '충북대학교', 'Chungbuk National University', '清州', '청주', 'Cheongju'),
      ('INHA', '仁荷大学', '인하대학교', 'Inha University', '仁川', '인천', 'Incheon'),
      ('HALLYM', '翰林大学', '한림대학교', 'Hallym University', '春川', '춘천', 'Chuncheon'),
      ('KEIMYUNG', '启明大学', '계명대학교', 'Keimyung University', '大邱', '대구', 'Daegu'),
      ('KONGJU', '公州大学', '공주대학교', 'Kongju National University', '公州', '공주', 'Gongju'),
      ('WKU', '圆光大学', '원광대학교', 'Wonkwang University', '益山', '익산', 'Iksan'),
      ('DONGA', '东亚大学', '동아대학교', 'Dong-A University', '釜山', '부산', 'Busan'),
      ('KWANGWOON', '光云大学', '광운대학교', 'Kwangwoon University', '首尔', '서울', 'Seoul'),
      ('UOU', '蔚山大学', '울산대학교', 'University of Ulsan', '蔚山', '울산', 'Ulsan'),
      ('CHOSUN', '朝鲜大学', '조선대학교', 'Chosun University', '光州', '광주', 'Gwangju'),
      ('SCH', '顺天乡大学', '순천향대학교', 'Soonchunhyang University', '牙山', '아산', 'Asan'),
      ('SEOULTECH', '首尔科技大学', '서울과학기술대학교', 'Seoul National University of Science and Technology', '首尔', '서울', 'Seoul'),
      ('CUK', '韩国天主教大学', '가톨릭대학교', 'The Catholic University of Korea', '首尔', '서울', 'Seoul'),
      ('HANDONG', '韩东大学', '한동대학교', 'Handong Global University', '浦项', '포항', 'Pohang'),
      ('KYONGGI', '京畿大学', '경기대학교', 'Kyonggi University', '水原', '수원', 'Suwon'),
      ('SWU', '首尔女子大学', '서울여자대학교', 'Seoul Women''s University', '首尔', '서울', 'Seoul'),
      ('KOREATECH', '韩国技术教育大学', '한국기술교육대학교', 'Korea University of Technology and Education', '天安', '천안', 'Cheonan'),
      ('KAI', '韩国航空大学', '한국항공대학교', 'Korea Aerospace University', '高阳', '고양', 'Goyang'),
      ('SNUST', '首尔科学技术大学', '서울과학기술대학교', 'Seoul National University of Science and Technology', '首尔', '서울', 'Seoul'),
      ('KNSU', '韩国体育大学', '한국체육대학교', 'Korea National Sport University', '首尔', '서울', 'Seoul');

UPDATE `sys_user`
SET `school_id` = (SELECT `id` FROM `school` WHERE `school_code` = 'SNU' LIMIT 1)
WHERE `username` = 'user01';

UPDATE `sys_user`
SET `school_id` = (SELECT `id` FROM `school` WHERE `school_code` = 'KAIST' LIMIT 1)
WHERE `username` = 'user02';

UPDATE `sys_user`
SET `school_id` = (SELECT `id` FROM `school` WHERE `school_code` = 'KU' LIMIT 1)
WHERE `username` = 'user03';

UPDATE `sys_user`
SET `school_id` = (SELECT `id` FROM `school` LIMIT 1)
WHERE `school_id` IS NULL;

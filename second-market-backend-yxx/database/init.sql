DROP DATABASE IF EXISTS `second_market`;
CREATE DATABASE IF NOT EXISTS `second_market` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `second_market`;

SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- 1. 创建学校信息表 (父表)
-- ==========================================================
DROP TABLE IF EXISTS `school`;
CREATE TABLE IF NOT EXISTS `school` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
    `school_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '学校编码',
    `name_zh` VARCHAR(255) NOT NULL COMMENT '学校中文名称',
    `name_ko` VARCHAR(255) NOT NULL COMMENT '学校韩文名称',
    `name_en` VARCHAR(255) NOT NULL COMMENT '学校英文名称',
    `city_zh` VARCHAR(100) DEFAULT NULL COMMENT '城市中文',
    `city_ko` VARCHAR(100) DEFAULT NULL COMMENT '城市韩文',
    `city_en` VARCHAR(100) DEFAULT NULL COMMENT '城市英文',
    `logo_url` VARCHAR(500) DEFAULT NULL COMMENT '学校logo地址',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学校信息表';

-- 插入学校数据
INSERT INTO school (school_code, name_zh, name_ko, name_en, city_zh, city_ko, city_en) VALUES
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
      ('SEJONG', '世宗大学', '세종대학교', 'Sejong University', '首尔', '서울', 'Seoul');

-- ==========================================================
-- 2. 创建用户信息表
-- ==========================================================
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
      `school_id` BIGINT DEFAULT NULL COMMENT '所属学校ID',
      `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '是否删除 (0-未删除, 1-已删除)',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_username` (`username`),
      UNIQUE KEY `uk_email` (`email`),
      CONSTRAINT `fk_user_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表';

-- 插入用户数据
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `password`, `role`, `school_id`) VALUES
(1, 'admin', '系统管理员', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'admin', 1),
(2, 'user01', '张三', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'user', 1),
(3, 'user02', '李四', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'user', 2),
(4, 'user03', '王五', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'user', 3);

-- ==========================================================
-- 3. 创建商品表
-- ==========================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
     `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
     `user_id` BIGINT(20) NOT NULL COMMENT '发布用户ID',
     `school_id` BIGINT(20) DEFAULT NULL COMMENT '所属学校ID',
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
     KEY `idx_product_school_id` (`school_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表';

-- 插入商品数据 (统一使用学校ID 1)
INSERT INTO `product` (`user_id`, `school_id`, `title`, `description`, `category`, `price`, `original_price`, `condition_score`, `condition_desc`, `images`, `status`, `create_time`) VALUES
(2, 1, 'iPhone 13 Pro 256G 石墨色', '自用iPhone 13 Pro，256G存储，石墨色，9成新，无磕碰，屏幕完好，电池健康度92%，配原装充电线和20W充电头。', '电子产品', 4800.00, 7999.00, 9, '外观完好无划痕', '["https://img0.baidu.com/it/u=3377345798,789706373&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1422"]', 2, '2024-11-01 09:30:00'),
(3, 1, '索尼WH-1000XM4 无线降噪耳机', '索尼旗舰降噪耳机，黑色，95新，音质出众。', '电子产品', 1480.00, 2499.00, 9, '外观轻微使用痕迹', '["https://img1.baidu.com/it/u=1198486088,1233730109&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=666"]', 2, '2024-11-02 10:15:00'),
(2, 1, '戴森V8 Fluffy吸尘器', '戴森V8无线吸尘器，使用1年，8成新。', '家用电器', 1299.00, 2990.00, 8, '机身有轻微使用痕迹', '["https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800"]', 2, '2024-11-03 14:45:00'),
(4, 1, 'Nike Air Max 270 运动鞋', 'Nike Air Max 270，黑白配色，42码，8.5成新。', '服装配饰', 380.00, 1099.00, 8, '鞋面清洁', '["https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800"]', 2, '2024-11-04 16:20:00'),
(3, 1, '小米65英寸4K电视', '小米电视4S 65英寸，4K超高清，HDR。', '家用电器', 1999.00, 3299.00, 9, '屏幕无坏点', '["https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800"]', 2, '2024-11-05 09:00:00'),
(2, 1, '雅马哈电钢琴P-125', '雅马哈P-125电钢琴，88键重锤，黑色。', '运动户外', 2888.00, 4699.00, 9, '琴键完好', '["https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=800"]', 2, '2024-11-06 11:30:00'),
(4, 1, '理光GR3相机', '理光GR3便携相机，街拍利器，9成新。', '电子产品', 4200.00, 6299.00, 9, '机身成色好', '["https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800"]', 2, '2024-11-07 13:40:00'),
(3, 1, '《深入理解计算机系统》第三版', 'CSAPP第三版中文版，计算机经典教材。', '图书音像', 68.00, 139.00, 9, '书籍内页完好', '["https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=800"]', 2, '2024-11-08 15:10:00'),
(2, 1, '联想拯救者R9000P笔记本', 'R7-5800H，RTX3060，16G内存，512G固态。', '电子产品', 5999.00, 8999.00, 9, '外观完好', '["https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800"]', 2, '2024-11-08 18:25:00'),
(4, 1, '阿迪达斯三叶草卫衣', '经典款卫衣，黑色，M码，8成新。', '服装配饰', 188.00, 599.00, 8, '衣服清洁', '["https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800"]', 2, '2024-11-09 14:55:00');

-- 其他表 (Orders, Favorite, Review, Notice, Message, BizChat)
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(50) NOT NULL,
    `buyer_id` BIGINT(20) NOT NULL,
    `seller_id` BIGINT(20) NOT NULL,
    `product_id` BIGINT(20) NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `status` INT(1) NOT NULL DEFAULT 0,
    `remark` VARCHAR(500) DEFAULT NULL,
    `pay_time` DATETIME DEFAULT NULL,
    `finish_time` DATETIME DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `orders` (`order_no`, `buyer_id`, `seller_id`, `product_id`, `amount`, `status`) VALUES
('202411050930451234567', 3, 2, 1, 4800.00, 2),
('202411061015232345678', 4, 3, 2, 1480.00, 2);

DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL,
    `product_id` BIGINT(20) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `type` INT(1) NOT NULL DEFAULT 1 COMMENT '公告类型',
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `status` INT(1) NOT NULL DEFAULT 1,
    `publisher_id` BIGINT(20) DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `notice` (`title`, `content`) VALUES ('欢迎使用智能二手商城', '欢迎来到智能二手商城！');

DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT(20) DEFAULT NULL COMMENT '订单ID',
    `reviewer_id` BIGINT(20) NOT NULL COMMENT '评价人ID',
    `reviewee_id` BIGINT(20) NOT NULL COMMENT '被评价人ID',
    `product_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
    `campus_service_id` BIGINT(20) DEFAULT NULL COMMENT '校园服务ID',
    `rating` INT(1) NOT NULL COMMENT '评分 1-5',
    `content` TEXT COMMENT '评价内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0,
    KEY `idx_review_order_id` (`order_id`),
    KEY `idx_review_product_id` (`product_id`),
    KEY `idx_review_reviewee_id` (`reviewee_id`),
    KEY `idx_review_campus_service_id` (`campus_service_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '评价表';

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `receiver_id` BIGINT(20) NOT NULL COMMENT '接收人ID',
    `type` INT(1) NOT NULL DEFAULT 1 COMMENT '消息类型',
    `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `is_read` INT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    `read_time` DATETIME DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0,
    KEY `idx_message_receiver_id` (`receiver_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统消息表';

DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `reporter_id` BIGINT(20) NOT NULL COMMENT '举报人ID',
    `target_type` INT(1) NOT NULL COMMENT '举报对象类型 1-商品 2-用户',
    `target_id` BIGINT(20) NOT NULL COMMENT '举报对象ID',
    `reason` TEXT NOT NULL COMMENT '举报原因',
    `status` INT(1) NOT NULL DEFAULT 0 COMMENT '处理状态',
    `handle_result` TEXT DEFAULT NULL COMMENT '处理结果',
    `handle_time` DATETIME DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0,
    KEY `idx_report_reporter_id` (`reporter_id`),
    KEY `idx_report_target` (`target_type`, `target_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '举报表';

DROP TABLE IF EXISTS `sys_file_info`;
CREATE TABLE `sys_file_info` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `original_name` VARCHAR(255) NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `file_size` BIGINT(20) DEFAULT NULL,
    `file_type` VARCHAR(50) DEFAULT NULL,
    `mime_type` VARCHAR(100) DEFAULT NULL,
    `extension` VARCHAR(20) DEFAULT NULL,
    `md5` VARCHAR(64) DEFAULT NULL,
    `file_url` VARCHAR(500) DEFAULT NULL,
    `user_id` BIGINT(20) DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT(1) NOT NULL DEFAULT 0,
    KEY `idx_file_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文件信息表';

DROP TABLE IF EXISTS `biz_chat`;
CREATE TABLE `biz_chat` (
    `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `sender_id` bigint DEFAULT NULL,
    `receiver_id` bigint DEFAULT NULL,
    `content` text,
    `msg_type` INT DEFAULT 0 COMMENT '消息类型 0:文本 1:图片',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `ai_chat_record`;
CREATE TABLE `ai_chat_record` (
    `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` bigint DEFAULT NULL,
    `role` varchar(32) DEFAULT NULL,
    `content` text,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    `deleted` INT(1) NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删除，1-已清空',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_search_history_user_deleted_time` (`user_id`, `deleted`, `create_time`),
    CONSTRAINT `fk_search_history_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';


-- ==========================================================
-- 合并自 browse_history.sql
-- ==========================================================
-- 浏览记录模块数据表
USE `second_market`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 浏览记录表
DROP TABLE IF EXISTS `browse_history`;
CREATE TABLE IF NOT EXISTS `browse_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    CONSTRAINT `fk_browse_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_browse_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '浏览记录表';

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 合并自 forum_module.sql
-- ==========================================================
-- 论坛模块数据表
USE `second_market`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 论坛帖子表
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE IF NOT EXISTS `forum_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
    `school_id` BIGINT DEFAULT NULL COMMENT '所属学校ID',
    `category` VARCHAR(50) NOT NULL COMMENT '帖子分类(二手求购, 校园闲聊, 出闲置)',
    `title` VARCHAR(100) NOT NULL COMMENT '帖子标题',
    `content` TEXT NOT NULL COMMENT '帖子内容',
    `images` TEXT COMMENT '帖子图片(JSON数组)',
    `is_school_only` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否校内可见',
    `is_anonymous` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否匿名',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_forum_post_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛帖子表';

-- 2. 论坛评论表
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE IF NOT EXISTS `forum_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `is_anonymous` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否匿名',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_forum_comment_post` FOREIGN KEY (`post_id`) REFERENCES `forum_post` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_forum_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛评论表';

-- 3. 论坛点赞表
DROP TABLE IF EXISTS `forum_like`;
CREATE TABLE IF NOT EXISTS `forum_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
    CONSTRAINT `fk_forum_like_post` FOREIGN KEY (`post_id`) REFERENCES `forum_post` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_forum_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛点赞表';

-- 4. 论坛收藏表
DROP TABLE IF EXISTS `forum_favorite`;
CREATE TABLE IF NOT EXISTS `forum_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user_fav` (`post_id`, `user_id`),
    CONSTRAINT `fk_forum_fav_post` FOREIGN KEY (`post_id`) REFERENCES `forum_post` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_forum_fav_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛收藏表';

-- 插入一些初始模拟数据
INSERT INTO `forum_post` (`user_id`, `school_id`, `category`, `title`, `content`, `images`, `view_count`, `like_count`, `comment_count`) VALUES
(2, 1, '校园闲聊', '求助！首尔大学附近哪里的炸鸡最好吃？', '刚来这边不久，求各位学长学姐推荐一下！最好是那种有校友优惠的店，或者是适合几个人聚餐的地方。感谢大家！', '["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500"]', 120, 56, 24),
(3, 1, '出闲置', '出一些考研/托福资料，半卖半送', '毕业回国了，很多资料带不走。有需要的同学可以私信我。包括历年真题、自己做的笔记，还有一些专业课参考书。地点在延世大学南门。', '["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500", "https://images.unsplash.com/photo-1555529669-2269763671c0?w=500"]', 85, 12, 8),
(4, 1, '校园闲聊', '避雷！校门口那家新开的咖啡店千万别去', '态度极差，咖啡味道也一般。等了半个小时才上，而且价格比旁边的贵一倍。真的是避雷了各位！', '[]', 230, 89, 45);

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 合并自 campus_service.sql
-- ==========================================================
-- ==========================================================
-- 5. 创建校园周边服务表 (campus_service)
-- ==========================================================
DROP TABLE IF EXISTS `campus_service`;
CREATE TABLE IF NOT EXISTS `campus_service` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务ID',
    `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
    `school_id` BIGINT DEFAULT NULL COMMENT '所属学校ID',
    `type` TINYINT NOT NULL COMMENT '服务类型 (1-跑腿互助, 2-拼单拼车, 3-资源共享)',
    `title` VARCHAR(100) NOT NULL COMMENT '服务标题',
    `content` TEXT COMMENT '服务内容详情',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '地点',
    `reward` DECIMAL(10,2) DEFAULT 0.00 COMMENT '报酬/积分',
    `limit_count` INT DEFAULT NULL COMMENT '人数上限 (针对拼单)',
    `current_count` INT DEFAULT 0 COMMENT '当前人数',
    `deadline` DATETIME DEFAULT NULL COMMENT '截止时间',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态 (1-待接单/招募中, 2-已接单/进行中, 3-已完成, 4-已取消)',
    `accepter_id` BIGINT DEFAULT NULL COMMENT '接单人/参与人ID (如果是跑腿)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_campus_service_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_campus_service_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '校园周边服务表';

-- 插入一些初始化数据
INSERT INTO `campus_service` (`user_id`, `school_id`, `type`, `title`, `content`, `location`, `reward`, `status`) VALUES
(2, 1, 1, '代取快递 - 菜鸟驿站', '在西门菜鸟驿站有三个大件，求顺路同学带到宿舍楼下。', '西门菜鸟驿站', 5.00, 1),
(3, 1, 2, '外卖拼单 - 霸王茶姬', '想喝霸王茶姬，还差一人免运费，速度来！', '宿舍11栋', 0.00, 1),
(4, 1, 3, '图书馆二楼靠窗位共享', '由于临时有课，座位空出，14:00-16:00可借用。', '图书馆二楼', 0.00, 1);

-- ==========================================================
-- 合并自 seed_more_korean_schools.sql
-- ==========================================================
USE `second_market`;

-- 插入更多韩国学校数据
INSERT INTO school (school_code, name_zh, name_ko, name_en, city_zh, city_ko, city_en) VALUES
      ('INHA', '仁荷大学', '인하대학교', 'Inha University', '仁川', '인천', 'Incheon'),
      ('KONKUK', '建国大学', '건국대학교', 'Konkuk University', '首尔', '서울', 'Seoul'),
      ('DANKOOK', '檀国大学', '단국대학교', 'Dankook University', '龙仁', '용인', 'Yongin'),
      ('UOS', '首尔市立大学', '서울시립대학교', 'University of Seoul', '首尔', '서울', 'Seoul'),
      ('HUFS', '韩国外国语大学', '한국외국어대학교', 'Hankuk University of Foreign Studies', '首尔', '서울', 'Seoul'),
      ('CNU', '全南大学', '전남대학교', 'Chonnam National University', '光州', '광주', 'Gwangju'),
      ('JBNU', '全北大学', '전북대학교', 'Chonbuk National University', '全州', '전주', 'Jeonju'),
      ('CNNU', '忠南大学', '충남대학교', 'Chungnam National University', '大田', '대전', 'Daejeon'),
      ('GNU', '庆尚国立大学', '경상국립대학교', 'Gyeongsang National University', '晋州', '진주', 'Jinju'),
      ('JNU', '济州大学', '제주대학교', 'Jeju National University', '济州', '제주', 'Jeju'),
      ('CBNU', '忠北大学', '충북대학교', 'Chungbuk National University', '清州', '청주', 'Cheongju'),
      ('KNU_GANGWON', '江原大学', '강원대학교', 'Kangwon National University', '春川', '춘천', 'Chuncheon');

-- ==========================================================
-- 合并自 seed_demo_users_products.sql
-- ==========================================================
START TRANSACTION;

SET @seed_tag := DATE_FORMAT(NOW(), '%Y%m%d%H%i%S');
SET @users_per_school := 2;
SET @products_per_user := 6;
SET @bcrypt_123456 := '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6';

CREATE TEMPORARY TABLE IF NOT EXISTS `tmp_seq` (`n` INT NOT NULL PRIMARY KEY);
TRUNCATE TABLE `tmp_seq`;
INSERT INTO `tmp_seq` (`n`) VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

INSERT INTO `sys_user` (
  `username`,
  `nickname`,
  `password`,
  `email`,
  `phone`,
  `address`,
  `gender`,
  `age`,
  `avatar`,
  `role`,
  `status`,
  `credit_score`,
  `school_id`,
  `create_time`,
  `update_time`,
  `deleted`
)
SELECT
  CONCAT('demo_', s.`school_code`, '_', @seed_tag, '_', LPAD(t.`n`, 2, '0')),
  CONCAT('演示用户', s.`school_code`, t.`n`),
  @bcrypt_123456,
  CONCAT('demo_', s.`school_code`, '_', @seed_tag, '_', LPAD(t.`n`, 2, '0'), '@example.com'),
  CONCAT('010', LPAD(MOD(CRC32(CONCAT(s.`school_code`, ':', t.`n`, ':', @seed_tag)), 100000000), 8, '0')),
  CONCAT(s.`name_zh`, ' 校园'),
  CASE MOD(CRC32(CONCAT('gender:', s.`school_code`, ':', t.`n`)), 3) WHEN 0 THEN 'M' WHEN 1 THEN 'F' ELSE 'U' END,
  18 + MOD(CRC32(CONCAT('age:', s.`school_code`, ':', t.`n`)), 10),
  CONCAT('https://api.dicebear.com/7.x/avataaars/svg?seed=', 'demo_', s.`school_code`, '_', LPAD(t.`n`, 2, '0')),
  'user',
  1,
  80 + MOD(CRC32(CONCAT('credit:', s.`school_code`, ':', t.`n`)), 21),
  s.`id`,
  NOW(),
  NOW(),
  0
FROM `school` s
JOIN `tmp_seq` t ON t.`n` <= @users_per_school
WHERE s.`deleted` = 0 AND s.`status` = 1;

CREATE TEMPORARY TABLE IF NOT EXISTS `tmp_demo_users` (
  `id` BIGINT NOT NULL PRIMARY KEY,
  `school_id` BIGINT NULL
);
TRUNCATE TABLE `tmp_demo_users`;
INSERT INTO `tmp_demo_users` (`id`, `school_id`)
SELECT u.`id`, u.`school_id`
FROM `sys_user` u
WHERE u.`deleted` = 0 AND u.`role` = 'user' AND u.`username` LIKE CONCAT('demo_%', @seed_tag, '%');

INSERT INTO `product` (
  `user_id`,
  `school_id`,
  `title`,
  `description`,
  `category`,
  `price`,
  `original_price`,
  `condition_score`,
  `condition_desc`,
  `images`,
  `status`,
  `view_count`,
  `favorite_count`,
  `ai_analyzed`,
  `ai_suggestions`,
  `create_time`,
  `update_time`,
  `deleted`
)
SELECT
  u.`id`,
  u.`school_id`,
  CASE MOD(CRC32(CONCAT('item:', u.`id`, ':', t.`n`)), 8)
    WHEN 0 THEN '二手平板 64G（学习用）'
    WHEN 1 THEN '宿舍小冰箱（静音省电）'
    WHEN 2 THEN '运动鞋 42 码（轻微磨损）'
    WHEN 3 THEN '图书音像资料合集（期末备考）'
    WHEN 4 THEN '羽毛球拍（含球）'
    WHEN 5 THEN '美妆护肤套装（未拆封）'
    WHEN 6 THEN '母婴玩具积木（九成新）'
    ELSE '其他：校园通勤双肩包'
  END,
  CONCAT('演示商品（', @seed_tag, '），支持按学校筛选。可私信咨询细节，支持当面交易。'),
  CASE MOD(CRC32(CONCAT('item:', u.`id`, ':', t.`n`)), 8)
    WHEN 0 THEN '电子产品'
    WHEN 1 THEN '家用电器'
    WHEN 2 THEN '服装配饰'
    WHEN 3 THEN '图书音像'
    WHEN 4 THEN '运动户外'
    WHEN 5 THEN '美妆护肤'
    WHEN 6 THEN '母婴玩具'
    ELSE '其他'
  END,
  ROUND(10 + MOD(CRC32(CONCAT('price:', u.`id`, ':', t.`n`)), 490) + MOD(CRC32(CONCAT('price2:', u.`id`, ':', t.`n`)), 99) / 100, 2),
  ROUND(50 + MOD(CRC32(CONCAT('oprice:', u.`id`, ':', t.`n`)), 900) + MOD(CRC32(CONCAT('oprice2:', u.`id`, ':', t.`n`)), 99) / 100, 2),
  6 + MOD(CRC32(CONCAT('score:', u.`id`, ':', t.`n`)), 5),
  '正常使用痕迹，功能完好，适合校园场景。',
  CONCAT('["',
    'https://source.unsplash.com/800x600/?',
    REPLACE(
      (
        CASE MOD(CRC32(CONCAT('item:', u.`id`, ':', t.`n`)), 8)
          WHEN 0 THEN 'tablet,electronics'
          WHEN 1 THEN 'mini-fridge,appliance'
          WHEN 2 THEN 'sneakers,fashion'
          WHEN 3 THEN 'books,study'
          WHEN 4 THEN 'badminton,sports'
          WHEN 5 THEN 'skincare,cosmetics'
          WHEN 6 THEN 'toys'
          ELSE 'backpack,campus'
        END
      ),
      ' ',
      ''
    ),
    '&sig=',
    CRC32(CONCAT('img1:', u.`id`, ':', t.`n`, ':', @seed_tag)),
    '","',
    'https://source.unsplash.com/800x600/?',
    REPLACE(
      (
        CASE MOD(CRC32(CONCAT('item:', u.`id`, ':', t.`n`)), 8)
          WHEN 0 THEN 'tablet,electronics'
          WHEN 1 THEN 'mini-fridge,appliance'
          WHEN 2 THEN 'sneakers,fashion'
          WHEN 3 THEN 'books,study'
          WHEN 4 THEN 'badminton,sports'
          WHEN 5 THEN 'skincare,cosmetics'
          WHEN 6 THEN 'toys'
          ELSE 'backpack,campus'
        END
      ),
      ' ',
      ''
    ),
    '&sig=',
    CRC32(CONCAT('img2:', u.`id`, ':', t.`n`, ':', @seed_tag)),
  '"]'),
  CASE MOD(CRC32(CONCAT('status:', u.`id`, ':', t.`n`)), 10)
    WHEN 0 THEN 3
    WHEN 1 THEN 4
    ELSE 2
  END,
  MOD(CRC32(CONCAT('views:', u.`id`, ':', t.`n`)), 500),
  MOD(CRC32(CONCAT('favs:', u.`id`, ':', t.`n`)), 80),
  CASE WHEN MOD(CRC32(CONCAT('ai:', u.`id`, ':', t.`n`)), 3) = 0 THEN 1 ELSE 0 END,
  NULL,
  NOW() - INTERVAL MOD(CRC32(CONCAT('days:', u.`id`, ':', t.`n`)), 25) DAY,
  NOW(),
  0
FROM `tmp_demo_users` u
JOIN `tmp_seq` t ON t.`n` <= @products_per_user;

COMMIT;

-- ==========================================================
-- 合并自 seed_demo_products_per_school.sql
-- ==========================================================
START TRANSACTION;

SET @seed_tag := DATE_FORMAT(NOW(), '%Y%m%d%H%i%S');
SET @products_per_school := 12;
SET @bcrypt_123456 := '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6';

CREATE TEMPORARY TABLE IF NOT EXISTS `tmp_seq` (`n` INT NOT NULL PRIMARY KEY);
TRUNCATE TABLE `tmp_seq`;
INSERT INTO `tmp_seq` (`n`) VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20);

CREATE TEMPORARY TABLE IF NOT EXISTS `tmp_school_seed_users` (
  `school_id` BIGINT NOT NULL PRIMARY KEY,
  `user_id` BIGINT NOT NULL
);
TRUNCATE TABLE `tmp_school_seed_users`;

INSERT INTO `sys_user` (
  `username`,
  `nickname`,
  `password`,
  `email`,
  `phone`,
  `address`,
  `gender`,
  `age`,
  `avatar`,
  `role`,
  `status`,
  `credit_score`,
  `school_id`,
  `create_time`,
  `update_time`,
  `deleted`
)
SELECT
  CONCAT('demo_school_', s.`id`, '_', @seed_tag),
  CONCAT('演示发布者-', s.`name_zh`),
  @bcrypt_123456,
  CONCAT('demo_school_', s.`id`, '_', @seed_tag, '@example.com'),
  CONCAT('010', LPAD(MOD(CRC32(CONCAT('phone:', s.`id`, ':', @seed_tag)), 100000000), 8, '0')),
  CONCAT(s.`name_zh`, ' 校园'),
  'U',
  20 + MOD(CRC32(CONCAT('age:', s.`id`, ':', @seed_tag)), 8),
  CONCAT('https://api.dicebear.com/7.x/avataaars/svg?seed=school_', s.`id`),
  'user',
  1,
  90 + MOD(CRC32(CONCAT('credit:', s.`id`, ':', @seed_tag)), 11),
  s.`id`,
  NOW(),
  NOW(),
  0
FROM `school` s
WHERE s.`deleted` = 0 AND s.`status` = 1;

INSERT INTO `tmp_school_seed_users` (`school_id`, `user_id`)
SELECT u.`school_id`, MAX(u.`id`) AS `user_id`
FROM `sys_user` u
WHERE u.`deleted` = 0
  AND u.`role` = 'user'
  AND u.`username` LIKE CONCAT('demo_school_%_', @seed_tag)
GROUP BY u.`school_id`;

INSERT INTO `product` (
  `user_id`,
  `school_id`,
  `title`,
  `description`,
  `category`,
  `price`,
  `original_price`,
  `condition_score`,
  `condition_desc`,
  `images`,
  `status`,
  `view_count`,
  `favorite_count`,
  `ai_analyzed`,
  `ai_suggestions`,
  `create_time`,
  `update_time`,
  `deleted`
)
SELECT
  u.`user_id`,
  u.`school_id`,
  CASE MOD(CRC32(CONCAT('item:', u.`school_id`, ':', t.`n`)), 8)
    WHEN 0 THEN '二手平板 64G（学习用）'
    WHEN 1 THEN '宿舍小冰箱（静音省电）'
    WHEN 2 THEN '运动鞋 42 码（轻微磨损）'
    WHEN 3 THEN '图书音像资料合集（期末备考）'
    WHEN 4 THEN '羽毛球拍（含球）'
    WHEN 5 THEN '美妆护肤套装（未拆封）'
    WHEN 6 THEN '母婴玩具积木（九成新）'
    ELSE '其他：校园通勤双肩包'
  END,
  CONCAT('【演示数据】', '学校维度商品展示（', @seed_tag, '）。支持按学校筛选/分类筛选。'),
  CASE MOD(CRC32(CONCAT('item:', u.`school_id`, ':', t.`n`)), 8)
    WHEN 0 THEN '电子产品'
    WHEN 1 THEN '家用电器'
    WHEN 2 THEN '服装配饰'
    WHEN 3 THEN '图书音像'
    WHEN 4 THEN '运动户外'
    WHEN 5 THEN '美妆护肤'
    WHEN 6 THEN '母婴玩具'
    ELSE '其他'
  END,
  ROUND(10 + MOD(CRC32(CONCAT('price:', u.`school_id`, ':', t.`n`)), 490) + MOD(CRC32(CONCAT('price2:', u.`school_id`, ':', t.`n`)), 99) / 100, 2),
  ROUND(50 + MOD(CRC32(CONCAT('oprice:', u.`school_id`, ':', t.`n`)), 900) + MOD(CRC32(CONCAT('oprice2:', u.`school_id`, ':', t.`n`)), 99) / 100, 2),
  7 + MOD(CRC32(CONCAT('score:', u.`school_id`, ':', t.`n`)), 4),
  '正常使用痕迹，功能完好，适合校园场景演示。',
  CONCAT('["',
    'https://source.unsplash.com/800x600/?',
    REPLACE(
      (
        CASE MOD(CRC32(CONCAT('item:', u.`school_id`, ':', t.`n`)), 8)
          WHEN 0 THEN 'tablet,electronics'
          WHEN 1 THEN 'mini-fridge,appliance'
          WHEN 2 THEN 'sneakers,fashion'
          WHEN 3 THEN 'books,study'
          WHEN 4 THEN 'badminton,sports'
          WHEN 5 THEN 'skincare,cosmetics'
          WHEN 6 THEN 'toys'
          ELSE 'backpack,campus'
        END
      ),
      ' ',
      ''
    ),
    '&sig=',
    CRC32(CONCAT('img1:', u.`school_id`, ':', t.`n`, ':', @seed_tag)),
    '","',
    'https://source.unsplash.com/800x600/?',
    REPLACE(
      (
        CASE MOD(CRC32(CONCAT('item:', u.`school_id`, ':', t.`n`)), 8)
          WHEN 0 THEN 'tablet,electronics'
          WHEN 1 THEN 'mini-fridge,appliance'
          WHEN 2 THEN 'sneakers,fashion'
          WHEN 3 THEN 'books,study'
          WHEN 4 THEN 'badminton,sports'
          WHEN 5 THEN 'skincare,cosmetics'
          WHEN 6 THEN 'toys'
          ELSE 'backpack,campus'
        END
      ),
      ' ',
      ''
    ),
    '&sig=',
    CRC32(CONCAT('img2:', u.`school_id`, ':', t.`n`, ':', @seed_tag)),
  '"]'),
  2,
  MOD(CRC32(CONCAT('views:', u.`school_id`, ':', t.`n`)), 500),
  MOD(CRC32(CONCAT('favs:', u.`school_id`, ':', t.`n`)), 80),
  CASE WHEN MOD(CRC32(CONCAT('ai:', u.`school_id`, ':', t.`n`)), 3) = 0 THEN 1 ELSE 0 END,
  NULL,
  NOW() - INTERVAL MOD(CRC32(CONCAT('days:', u.`school_id`, ':', t.`n`)), 25) DAY,
  NOW(),
  0
FROM `tmp_school_seed_users` u
JOIN `tmp_seq` t ON t.`n` <= @products_per_school;

COMMIT;

-- ==========================================================
-- 合并自 seed_random_schools_for_existing_products.sql
-- ==========================================================
START TRANSACTION;

SET @school_count := (
  SELECT COUNT(*)
  FROM `school`
  WHERE `deleted` = 0 AND `status` = 1
);

CREATE TEMPORARY TABLE IF NOT EXISTS `tmp_user_school` (
  `user_id` BIGINT NOT NULL PRIMARY KEY,
  `school_id` BIGINT NULL
);

TRUNCATE TABLE `tmp_user_school`;

INSERT INTO `tmp_user_school` (`user_id`)
SELECT DISTINCT p.`user_id`
FROM `product` p
JOIN `sys_user` u ON u.`id` = p.`user_id`
WHERE p.`deleted` = 0 AND u.`deleted` = 0 AND u.`role` = 'user';

UPDATE `tmp_user_school` tus
SET tus.`school_id` = (
  SELECT s.`id`
  FROM `school` s
  WHERE s.`deleted` = 0 AND s.`status` = 1
  ORDER BY s.`id`
  LIMIT MOD(CRC32(CONCAT(tus.`user_id`, ':seed:2026-05-05')), @school_count), 1
);

UPDATE `sys_user` u
JOIN `tmp_user_school` tus ON tus.`user_id` = u.`id`
SET u.`school_id` = tus.`school_id`
WHERE u.`deleted` = 0 AND u.`role` = 'user';

UPDATE `product` p
JOIN `tmp_user_school` tus ON tus.`user_id` = p.`user_id`
SET p.`school_id` = tus.`school_id`
WHERE p.`deleted` = 0;

COMMIT;

-- ==========================================================
-- 合并自 mock_forum_data.sql
-- ==========================================================
-- 设置编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE `second_market`;

-- 清理之前的乱码数据
DELETE FROM forum_post;
ALTER TABLE forum_post AUTO_INCREMENT = 1;

-- 插入真实的演示数据
INSERT INTO forum_post (user_id, school_id, category, title, content, images, is_school_only, view_count, comment_count, create_time, update_time) VALUES
(2, 1, '校园闲聊', '首尔大学图书馆这周五有座吗？', '想去复习期中考试，不知道周五人多不多，求学长学姐告知！', '[]', 1, 45, 2, NOW(), NOW()),
(3, 1, '二手求购', '求购一个二手显示器', '最近宿舍写代码太费眼了，想收个24寸左右的显示器，有的同学请私聊。', '["https://picsum.photos/800/600?random=10"]', 0, 12, 1, NOW(), NOW()),
(4, 2, '出闲置', '转让一把人体工学椅', 'KAIST西门自取，用了半年，保护得很好，因为要回国了所以低价转让。', '["https://picsum.photos/800/600?random=11"]', 1, 89, 5, NOW(), NOW()),
(5, 4, '校园闲聊', '延世大学校庆活动有人一起吗？', '这周末有校庆表演，希望能组队一起去看，男女不限！', '["https://picsum.photos/800/600?random=12"]', 0, 156, 12, NOW(), NOW()),
(2, 3, '二手求购', '高丽大学附近求租房', '求租一个One-room，最好靠近地铁站，租金在50w左右。', '[]', 0, 34, 0, NOW(), NOW()),
(3, 1, '校园闲聊', '首尔大学食堂哪个最好吃？', '刚入学，感觉食堂太多了选不过来，大家推荐一下呀！', '["https://picsum.photos/800/600?random=13"]', 0, 210, 8, NOW(), NOW()),
(4, 4, '出闲置', '低价出教材，延世大学专业课', '包括经济学原理、中级微观等，保存完好，需要的同学带走。', '["https://picsum.photos/800/600?random=14"]', 1, 56, 3, NOW(), NOW());

-- ==========================================================
-- 合并自 repair_database_encoding.sql
-- ==========================================================
-- 设置数据库连接编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE `second_market`;

-- 关闭外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 3. 修复论坛帖子（随机分配学校并增加匿名测试数据）
DELETE FROM forum_post;
INSERT INTO forum_post (id, user_id, school_id, category, title, content, images, is_school_only, is_anonymous, view_count, comment_count, create_time, update_time) VALUES
(1, 2, 1, '校园闲聊', '首尔大学图书馆这周五有座吗？', '想去复习期中考试，不知道周五人多不多，求学长学姐告知！', '[]', 1, 0, 45, 2, NOW(), NOW()),
(2, 3, 2, '二手求购', '求购一个二手显示器', '最近宿舍写代码太费眼了，想收个24寸左右的显示器，有的同学请私聊。', '["https://picsum.photos/800/600?random=10"]', 0, 0, 12, 1, NOW(), NOW()),
(3, 4, 3, '出闲置', '转让一把人体工学椅', 'KAIST西门自取，用了半年，保护得很好，因为要回国了所以低价转让。', '["https://picsum.photos/800/600?random=11"]', 1, 0, 89, 5, NOW(), NOW()),
(4, 5, 4, '校园闲聊', '延世大学校庆活动有人一起吗？', '这周末有校庆表演，希望能组队一起去看，男女不限！', '["https://picsum.photos/800/600?random=12"]', 0, 1, 156, 12, NOW(), NOW()),
(5, 2, 5, '二手求购', '高丽大学附近求租房', '求租一个One-room，最好靠近地铁站，租金在50w左右。', '[]', 0, 0, 34, 0, NOW(), NOW()),
(6, 3, 1, '校园闲聊', '首尔大学食堂哪个最好吃？', '刚入学，感觉食堂太多了选不过来，大家推荐一下呀！', '["https://picsum.photos/800/600?random=13"]', 0, 1, 210, 8, NOW(), NOW()),
(7, 4, 6, '出闲置', '低价出教材，延世大学专业课', '包括经济学原理、中级微观等，保存完好，需要的同学带走。', '["https://picsum.photos/800/600?random=14"]', 1, 0, 56, 3, NOW(), NOW());

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 合并自 update_demo_product_categories.sql
-- ==========================================================
START TRANSACTION;

UPDATE `product` p
JOIN `sys_user` u ON u.`id` = p.`user_id`
SET p.`category` = CASE p.`category`
  WHEN '图书文具' THEN '图书音像'
  WHEN '图书资料' THEN '图书音像'
  WHEN '美妆护理' THEN '美妆护肤'
  WHEN '生活用品' THEN '其他'
  WHEN '家用用品' THEN '其他'
  ELSE p.`category`
END
WHERE p.`deleted` = 0
  AND u.`deleted` = 0
  AND u.`username` LIKE 'demo%'
  AND p.`category` IN ('图书文具','图书资料','美妆护理','生活用品','家用用品');

COMMIT;

-- ==========================================================
-- 合并自 update_demo_product_images.sql
-- ==========================================================
START TRANSACTION;

UPDATE `product` p
JOIN `sys_user` u ON u.`id` = p.`user_id`
SET p.`images` = CONCAT(
  '["https://source.unsplash.com/800x600/?',
  CASE p.`category`
    WHEN '电子产品' THEN 'electronics,gadget'
    WHEN '家用电器' THEN 'appliance'
    WHEN '图书音像' THEN 'books,study'
    WHEN '服装配饰' THEN 'fashion,clothing'
    WHEN '运动户外' THEN 'sports,outdoor'
    WHEN '美妆护肤' THEN 'skincare,cosmetics'
    WHEN '母婴玩具' THEN 'toys'
    ELSE 'campus,secondhand'
  END,
  '&sig=',
  CRC32(CONCAT('p1:', p.`id`)),
  '","https://source.unsplash.com/800x600/?',
  CASE p.`category`
    WHEN '电子产品' THEN 'electronics,gadget'
    WHEN '家用电器' THEN 'appliance'
    WHEN '图书音像' THEN 'books,study'
    WHEN '服装配饰' THEN 'fashion,clothing'
    WHEN '运动户外' THEN 'sports,outdoor'
    WHEN '美妆护肤' THEN 'skincare,cosmetics'
    WHEN '母婴玩具' THEN 'toys'
    ELSE 'campus,secondhand'
  END,
  '&sig=',
  CRC32(CONCAT('p2:', p.`id`)),
  '"]'
)
WHERE p.`deleted` = 0
  AND u.`deleted` = 0
  AND u.`username` LIKE 'demo%'
  AND (p.`images` IS NULL OR p.`images` = '' OR p.`images` LIKE '%picsum.photos%');

COMMIT;

-- ==========================================================
-- 合并自 fix_all_image_urls.sql
-- ==========================================================
START TRANSACTION;

UPDATE product SET images = CONCAT('["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=', id, '"]') WHERE images LIKE '%unsplash.com%' OR images LIKE '%picsum.photos/photo-%';
UPDATE forum_post SET images = CONCAT('["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=', id, '"]') WHERE images LIKE '%unsplash.com%' OR images LIKE '%picsum.photos/photo-%';
UPDATE sys_user SET avatar = CONCAT('https://api.dicebear.com/7.x/avataaars/svg?seed=', username) WHERE avatar LIKE '%unsplash.com%' OR avatar LIKE '%picsum.photos/photo-%';

-- 4. 淇 campus_service 琛?-- (濡傛灉璇ヨ〃鏈夊浘鐗囧瓧娈碉紝鐩墠鐪嬫潵娌℃湁锛屼絾涓轰簡淇濋櫓璧疯)
-- UPDATE `campus_service` SET ...

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

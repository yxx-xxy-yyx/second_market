DROP DATABASE IF EXISTS `second_market`;
CREATE DATABASE IF NOT EXISTS `second_market` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `second_market`;

-- 鏆傛椂鍏抽棴澶栭敭妫€鏌ワ紝纭繚鍒犺〃閲嶅缓鏃朵笉浼氬洜涓哄閿緷璧栬€屾姤閿?SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- 1. 鍒涘缓瀛︽牎淇℃伅琛?(鐖惰〃)
-- ==========================================================
DROP TABLE IF EXISTS `school`;
CREATE TABLE IF NOT EXISTS `school` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '瀛︽牎ID',
    `school_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '瀛︽牎缂栫爜',
    `name_zh` VARCHAR(255) NOT NULL COMMENT '瀛︽牎涓枃鍚嶇О',
    `name_ko` VARCHAR(255) NOT NULL COMMENT '瀛︽牎闊╂枃鍚嶇О',
    `name_en` VARCHAR(255) NOT NULL COMMENT '瀛︽牎鑻辨枃鍚嶇О',
    `city_zh` VARCHAR(100) DEFAULT NULL COMMENT '鍩庡競涓枃',
    `city_ko` VARCHAR(100) DEFAULT NULL COMMENT '鍩庡競闊╂枃',
    `city_en` VARCHAR(100) DEFAULT NULL COMMENT '鍩庡競鑻辨枃',
    `logo_url` VARCHAR(500) DEFAULT NULL COMMENT '瀛︽牎logo鍦板潃',
    `status` TINYINT DEFAULT 1 COMMENT '鐘舵€侊細1鍚敤 0绂佺敤',
    `deleted` INT DEFAULT 0 COMMENT '鏄惁鍒犻櫎',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '瀛︽牎淇℃伅琛?;

-- 插入学校数据（含更多韩国大学，补齐“江原大学”等）
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
      ('SEJONG', '世宗大学', '세종대학교', 'Sejong University', '首尔', '서울', 'Seoul'),
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
-- 2. 鍒涘缓鐢ㄦ埛淇℃伅琛?-- ==========================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE IF NOT EXISTS `sys_user` (
      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
      `username` VARCHAR(50) NOT NULL COMMENT '鐢ㄦ埛鍚?,
      `nickname` VARCHAR(50) NOT NULL COMMENT '鐢ㄦ埛鏄电О',
      `password` VARCHAR(100) NOT NULL COMMENT '瀵嗙爜',
      `email` VARCHAR(100) DEFAULT NULL COMMENT '閭',
      `phone` VARCHAR(20) DEFAULT NULL COMMENT '鎵嬫満鍙?,
      `address` VARCHAR(255) DEFAULT NULL COMMENT '鍦板潃',
      `gender` CHAR(1) DEFAULT 'U' COMMENT '鎬у埆 (M-鐢? F-濂? U-鏈煡)',
      `age` INT DEFAULT NULL COMMENT '骞撮緞',
      `bio` VARCHAR(500) DEFAULT NULL COMMENT '涓汉绠€浠?,
      `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
      `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '鐢ㄦ埛瑙掕壊 (admin-绠＄悊鍛? user-鏅€氱敤鎴?',
      `status` INT NOT NULL DEFAULT 1 COMMENT '璐︽埛鐘舵€?(0-灏佺, 1-姝ｅ父)',
      `credit_score` INT NOT NULL DEFAULT 100 COMMENT '淇＄敤璇勫垎',
      `school_id` BIGINT DEFAULT NULL COMMENT '鎵€灞炲鏍D',
      `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
      `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
      `deleted` INT NOT NULL DEFAULT 0 COMMENT '鏄惁鍒犻櫎 (0-鏈垹闄? 1-宸插垹闄?',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_username` (`username`),
      UNIQUE KEY `uk_email` (`email`),
      CONSTRAINT `fk_user_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鐢ㄦ埛淇℃伅琛?;

-- 鎻掑叆鐢ㄦ埛鏁版嵁
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `password`, `role`, `school_id`) VALUES
(1, 'admin', '绯荤粺绠＄悊鍛?, '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'admin', 1),
(2, 'user01', '寮犱笁', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'user', 1),
(3, 'user02', '鏉庡洓', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'user', 2),
(4, 'user03', '鐜嬩簲', '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6', 'user', 3);

-- ==========================================================
-- 3. 鍒涘缓鍟嗗搧琛?-- ==========================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鍟嗗搧ID',
      `user_id` BIGINT NOT NULL COMMENT '鍙戝竷鐢ㄦ埛ID',
      `school_id` BIGINT DEFAULT NULL COMMENT '鎵€灞炲鏍D',
      `title` VARCHAR(100) NOT NULL COMMENT '鍟嗗搧鏍囬',
      `description` TEXT COMMENT '鍟嗗搧鎻忚堪',
      `category` VARCHAR(50) NOT NULL COMMENT '鍟嗗搧鍒嗙被',
      `price` DECIMAL(10,2) NOT NULL COMMENT '浠锋牸',
      `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '鍘熶环',
      `condition_score` INT NOT NULL DEFAULT 8 COMMENT '鎴愯壊璇勫垎(1-10鍒?',
      `condition_desc` VARCHAR(500) DEFAULT NULL COMMENT '鎴愯壊璇存槑',
      `images` TEXT COMMENT '鍟嗗搧鍥剧墖(JSON鏁扮粍)',
      `status` INT NOT NULL DEFAULT 1 COMMENT '鐘舵€?0-鑽夌,1-寰呭鏍?2-宸蹭笂鏋?3-宸蹭笅鏋?4-宸插敭鍑?',
      `view_count` INT NOT NULL DEFAULT 0 COMMENT '娴忚閲?,
      `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '鏀惰棌鏁?,
      `ai_analyzed` INT NOT NULL DEFAULT 0 COMMENT '鏄惁AI鍒嗘瀽(0-鍚?1-鏄?',
      `ai_suggestions` TEXT COMMENT 'AI鍒嗘瀽缁撴灉(JSON)',
      `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
      `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
      `deleted` INT NOT NULL DEFAULT 0 COMMENT '鏄惁鍒犻櫎',
      PRIMARY KEY (`id`),
      CONSTRAINT `fk_product_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
      CONSTRAINT `fk_product_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍟嗗搧琛?;

-- 插入商品数据（示例，统一不写外链图片，前端走内置占位图）
INSERT INTO `product` (`user_id`, `school_id`, `title`, `description`, `category`, `price`, `original_price`, `condition_score`, `condition_desc`, `images`, `status`, `create_time`) VALUES
(2, 1, 'iPhone 13 Pro 256G 石墨色', '自用 iPhone 13 Pro，256G，石墨色，9成新，无磕碰。配原装充电线与20W充电头。', '电子产品', 4800.00, 7999.00, 9, '外观完好无明显划痕', '[]', 2, '2024-11-01 09:30:00'),
(3, 1, '索尼 WH-1000XM4 无线降噪耳机', '索尼旗舰降噪耳机，黑色，95新，音质出众。', '电子产品', 1480.00, 2499.00, 9, '轻微使用痕迹', '[]', 2, '2024-11-02 10:15:00'),
(2, 1, '戴森 V8 Fluffy 吸尘器', '戴森 V8 无线吸尘器，使用1年，8成新。', '家用电器', 1299.00, 2990.00, 8, '机身有轻微使用痕迹', '[]', 2, '2024-11-03 14:45:00'),
(4, 1, 'Nike Air Max 270 运动鞋', 'Nike Air Max 270，黑白配色，42码，8.5成新。', '服饰配饰', 380.00, 1099.00, 8, '鞋面干净，正常磨损', '[]', 2, '2024-11-04 16:20:00'),
(3, 1, '小米 65英寸 4K 电视', '小米电视 65英寸，4K超清，支持HDR。', '家用电器', 1999.00, 3299.00, 9, '屏幕无坏点', '[]', 2, '2024-11-05 09:00:00'),
(2, 1, '雅马哈 电钢琴 P-125', '雅马哈 P-125 电钢琴，88键重锤，黑色。', '运动户外', 2888.00, 4699.00, 9, '琴键完好', '[]', 2, '2024-11-06 11:30:00'),
(4, 1, '理光 GR3 相机', '理光 GR3 便携相机，街拍利器，9成新。', '电子产品', 4200.00, 6299.00, 9, '机身成色好', '[]', 2, '2024-11-07 13:40:00'),
(3, 1, '深入理解计算机系统（第三版）', 'CSAPP 第三版中文版，计算机经典教材。', '图书音像', 68.00, 139.00, 9, '书脊与内页完好', '[]', 2, '2024-11-08 15:10:00'),
(2, 1, '联想 拯救者 R9000P 笔记本', 'R7-5800H / RTX3060 / 16G内存 / 512G固态。', '电子产品', 5999.00, 8999.00, 9, '外观完好', '[]', 2, '2024-11-08 18:25:00'),
(4, 1, '阿迪达斯 三叶草 卫衣', '经典款卫衣，黑色，M码，8成新。', '服饰配饰', 188.00, 599.00, 8, '衣物干净', '[]', 2, '2024-11-09 14:55:00');

-- ==========================================================
-- 4. 鍏跺畠鍏宠仈涓氬姟琛紙瀛楃闆嗗榻愪负 utf8mb4_unicode_ci锛?-- ==========================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(50) NOT NULL,
    `buyer_id` BIGINT NOT NULL,
    `seller_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `status` INT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `sys_user` (`id`),
    CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO `orders` (`order_no`, `buyer_id`, `seller_id`, `product_id`, `amount`, `status`) VALUES
('202411050930451234567', 3, 2, 1, 4800.00, 2),
('202411061015232345678', 4, 3, 2, 1480.00, 2);

DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `status` INT NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO `notice` (`title`, `content`) VALUES ('娆㈣繋浣跨敤鏅鸿兘浜屾墜鍟嗗煄', '娆㈣繋鏉ュ埌鏅鸿兘浜屾墜鍟嗗煄锛?);

DROP TABLE IF EXISTS `biz_chat`;
CREATE TABLE `biz_chat` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `sender_id` BIGINT DEFAULT NULL,
    `receiver_id` BIGINT DEFAULT NULL,
    `content` TEXT,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 鎭㈠澶栭敭妫€鏌?SET FOREIGN_KEY_CHECKS = 1;

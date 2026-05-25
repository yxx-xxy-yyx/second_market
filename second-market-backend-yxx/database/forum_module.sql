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



-- 修复 biz_chat 表缺少字段的问题
USE `second_market`;

ALTER TABLE `biz_chat` ADD COLUMN `msg_type` INT DEFAULT 0 COMMENT '消息类型 0:文本 1:图片' AFTER `content`;
ALTER TABLE `biz_chat` ADD COLUMN `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读' AFTER `msg_type`;

-- 确保 forum_post 表有 is_anonymous 字段
ALTER TABLE `forum_post` ADD COLUMN `is_anonymous` TINYINT(1) DEFAULT 0 COMMENT '是否匿名' AFTER `is_school_only`;

-- 确保 forum_comment 表有 is_anonymous 字段
ALTER TABLE `forum_comment` ADD COLUMN `is_anonymous` TINYINT(1) DEFAULT 0 COMMENT '是否匿名' AFTER `content`;

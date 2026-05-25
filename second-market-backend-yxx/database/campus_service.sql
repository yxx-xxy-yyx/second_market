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

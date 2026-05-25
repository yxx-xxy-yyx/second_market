START TRANSACTION;

SET @has_school_id := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'product'
    AND column_name = 'school_id'
);

SET @ddl_add_school_id := IF(
  @has_school_id = 0,
  'ALTER TABLE `product` ADD COLUMN `school_id` BIGINT NULL AFTER `user_id`',
  'SELECT 1'
);

PREPARE stmt FROM @ddl_add_school_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_school_idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'product'
    AND index_name = 'idx_product_school_id'
);

SET @ddl_add_school_idx := IF(
  @has_school_idx = 0,
  'CREATE INDEX `idx_product_school_id` ON `product`(`school_id`)',
  'SELECT 1'
);

PREPARE stmt FROM @ddl_add_school_idx;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

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
    'https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&random=',
    CRC32(CONCAT('img1:', u.`id`, ':', t.`n`, ':', @seed_tag)),
    '","',
    'https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&random=',
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

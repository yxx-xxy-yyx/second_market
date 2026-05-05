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

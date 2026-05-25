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
  CONCAT('婕旂ず鍙戝竷鑰?', s.`name_zh`),
  @bcrypt_123456,
  CONCAT('demo_school_', s.`id`, '_', @seed_tag, '@example.com'),
  CONCAT('010', LPAD(MOD(CRC32(CONCAT('phone:', s.`id`, ':', @seed_tag)), 100000000), 8, '0')),
  CONCAT(s.`name_zh`, ' 鏍″洯'),
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
    WHEN 0 THEN '浜屾墜骞虫澘 64G锛堝涔犵敤锛?
    WHEN 1 THEN '瀹胯垗灏忓啺绠憋紙闈欓煶鐪佺數锛?
    WHEN 2 THEN '杩愬姩闉?42 鐮侊紙杞诲井纾ㄦ崯锛?
    WHEN 3 THEN '鍥句功闊冲儚璧勬枡鍚堥泦锛堟湡鏈鑰冿級'
    WHEN 4 THEN '缇芥瘺鐞冩媿锛堝惈鐞冿級'
    WHEN 5 THEN '缇庡鎶よ偆濂楄锛堟湭鎷嗗皝锛?
    WHEN 6 THEN '姣嶅┐鐜╁叿绉湪锛堜節鎴愭柊锛?
    ELSE '鍏朵粬锛氭牎鍥€氬嫟鍙岃偐鍖?
  END,
  CONCAT('銆愭紨绀烘暟鎹€?, '瀛︽牎缁村害鍟嗗搧灞曠ず锛?, @seed_tag, '锛夈€傛敮鎸佹寜瀛︽牎绛涢€?鍒嗙被绛涢€夈€?),
  CASE MOD(CRC32(CONCAT('item:', u.`school_id`, ':', t.`n`)), 8)
    WHEN 0 THEN '鐢靛瓙浜у搧'
    WHEN 1 THEN '瀹剁敤鐢靛櫒'
    WHEN 2 THEN '鏈嶈閰嶉グ'
    WHEN 3 THEN '鍥句功闊冲儚'
    WHEN 4 THEN '杩愬姩鎴峰'
    WHEN 5 THEN '缇庡鎶よ偆'
    WHEN 6 THEN '姣嶅┐鐜╁叿'
    ELSE '鍏朵粬'
  END,
  ROUND(10 + MOD(CRC32(CONCAT('price:', u.`school_id`, ':', t.`n`)), 490) + MOD(CRC32(CONCAT('price2:', u.`school_id`, ':', t.`n`)), 99) / 100, 2),
  ROUND(50 + MOD(CRC32(CONCAT('oprice:', u.`school_id`, ':', t.`n`)), 900) + MOD(CRC32(CONCAT('oprice2:', u.`school_id`, ':', t.`n`)), 99) / 100, 2),
  7 + MOD(CRC32(CONCAT('score:', u.`school_id`, ':', t.`n`)), 4),
  '姝ｅ父浣跨敤鐥曡抗锛屽姛鑳藉畬濂斤紝閫傚悎鏍″洯鍦烘櫙婕旂ず銆?,
  CONCAT('["',
    'https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=',
    CRC32(CONCAT('img1:', u.`school_id`, ':', t.`n`, ':', @seed_tag)),
    '","',
    'https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=',
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




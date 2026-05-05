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

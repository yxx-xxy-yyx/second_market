ALTER TABLE `product`
ADD COLUMN `school_id` BIGINT NULL AFTER `user_id`;

UPDATE `product` p
JOIN `sys_user` u ON p.`user_id` = u.`id`
SET p.`school_id` = u.`school_id`
WHERE p.`school_id` IS NULL;

CREATE INDEX `idx_product_school_id` ON `product`(`school_id`);

START TRANSACTION;

UPDATE `product` p
JOIN `sys_user` u ON u.`id` = p.`user_id`
SET p.`images` = CONCAT(
  '["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=',
  p.`id`,
  '","https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=',
  p.`id` + 100,
  '"]'
)
WHERE p.`deleted` = 0
  AND u.`deleted` = 0
  AND (
    p.`images` IS NULL 
    OR p.`images` = '' 
    OR p.`images` LIKE '%unsplash.com%'
    OR p.`images` LIKE '%source.unsplash.com%'
    OR p.`images` LIKE '%images.unsplash.com%'
  );

COMMIT;




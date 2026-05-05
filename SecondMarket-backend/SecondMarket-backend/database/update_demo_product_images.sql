START TRANSACTION;

UPDATE `product` p
JOIN `sys_user` u ON u.`id` = p.`user_id`
SET p.`images` = CONCAT(
  '["https://source.unsplash.com/800x600/?',
  CASE p.`category`
    WHEN '电子产品' THEN 'electronics,gadget'
    WHEN '家用电器' THEN 'appliance'
    WHEN '图书音像' THEN 'books,study'
    WHEN '服装配饰' THEN 'fashion,clothing'
    WHEN '运动户外' THEN 'sports,outdoor'
    WHEN '美妆护肤' THEN 'skincare,cosmetics'
    WHEN '母婴玩具' THEN 'toys'
    ELSE 'campus,secondhand'
  END,
  '&sig=',
  CRC32(CONCAT('p1:', p.`id`)),
  '","https://source.unsplash.com/800x600/?',
  CASE p.`category`
    WHEN '电子产品' THEN 'electronics,gadget'
    WHEN '家用电器' THEN 'appliance'
    WHEN '图书音像' THEN 'books,study'
    WHEN '服装配饰' THEN 'fashion,clothing'
    WHEN '运动户外' THEN 'sports,outdoor'
    WHEN '美妆护肤' THEN 'skincare,cosmetics'
    WHEN '母婴玩具' THEN 'toys'
    ELSE 'campus,secondhand'
  END,
  '&sig=',
  CRC32(CONCAT('p2:', p.`id`)),
  '"]'
)
WHERE p.`deleted` = 0
  AND u.`deleted` = 0
  AND u.`username` LIKE 'demo%'
  AND (p.`images` IS NULL OR p.`images` = '' OR p.`images` LIKE '%picsum.photos%');

COMMIT;

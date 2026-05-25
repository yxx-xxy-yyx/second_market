BEGIN;

UPDATE product p
SET category = CASE p.category
  WHEN '图书文具' THEN '图书音像'
  WHEN '图书资料' THEN '图书音像'
  WHEN '美妆护理' THEN '美妆护肤'
  WHEN '生活用品' THEN '其他'
  WHEN '家用用品' THEN '其他'
  ELSE p.category
END
FROM sys_user u
WHERE u.id = p.user_id
  AND p.deleted = 0
  AND u.deleted = 0
  AND u.username LIKE 'demo%'
  AND p.category IN ('图书文具','图书资料','美妆护理','生活用品','家用用品');

COMMIT;

BEGIN;

UPDATE product p
SET images = '[]'
FROM sys_user u
WHERE u.id = p.user_id
  AND p.deleted = 0
  AND u.deleted = 0
  AND (
    p.images IS NULL
    OR p.images = ''
    OR p.images LIKE '%unsplash.com%'
    OR p.images LIKE '%source.unsplash.com%'
    OR p.images LIKE '%images.unsplash.com%'
  );

COMMIT;

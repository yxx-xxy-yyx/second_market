BEGIN;

ALTER TABLE product
  ADD COLUMN IF NOT EXISTS school_id BIGINT;

UPDATE product p
SET school_id = u.school_id
FROM sys_user u
WHERE p.user_id = u.id
  AND p.school_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_product_school_id ON product(school_id);

COMMIT;

BEGIN;

DO $$
DECLARE
  school_count INTEGER;
BEGIN
  SELECT COUNT(*) INTO school_count FROM school WHERE deleted = 0 AND status = 1;
  IF school_count <= 0 THEN
    RETURN;
  END IF;

  CREATE TEMP TABLE IF NOT EXISTS tmp_user_school (
    user_id BIGINT PRIMARY KEY,
    school_id BIGINT
  ) ON COMMIT DROP;

  TRUNCATE TABLE tmp_user_school;

  INSERT INTO tmp_user_school (user_id)
  SELECT DISTINCT p.user_id
  FROM product p
  JOIN sys_user u ON u.id = p.user_id
  WHERE p.deleted = 0 AND u.deleted = 0 AND u.role = 'user';

  UPDATE tmp_user_school tus
  SET school_id = (
    SELECT s.id
    FROM school s
    WHERE s.deleted = 0 AND s.status = 1
    ORDER BY s.id
    OFFSET (abs(hashtext(tus.user_id::text || ':seed:2026-05-05')) % school_count)
    LIMIT 1
  );

  UPDATE sys_user u
  SET school_id = tus.school_id
  FROM tmp_user_school tus
  WHERE tus.user_id = u.id
    AND u.deleted = 0
    AND u.role = 'user';

  UPDATE product p
  SET school_id = tus.school_id
  FROM tmp_user_school tus
  WHERE tus.user_id = p.user_id
    AND p.deleted = 0;
END;
$$;

COMMIT;

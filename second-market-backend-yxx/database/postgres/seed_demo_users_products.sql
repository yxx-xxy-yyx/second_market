BEGIN;

WITH seed_schools AS (
  SELECT id, school_code, name_zh
  FROM school
  WHERE deleted = 0 AND status = 1
),
users_to_insert AS (
  SELECT
    'demo_' || s.school_code || '_' || LPAD(gs::text, 2, '0') AS username,
    '演示用户' || s.school_code || gs::text AS nickname,
    '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6' AS password,
    'demo_' || s.school_code || '_' || LPAD(gs::text, 2, '0') || '@example.com' AS email,
    '010' || LPAD((abs(hashtext(s.school_code || ':' || gs::text)) % 100000000)::text, 8, '0') AS phone,
    s.name_zh || ' 校园' AS address,
    'U' AS gender,
    20 + (abs(hashtext('age:' || s.school_code || ':' || gs::text)) % 8) AS age,
    NULL AS avatar,
    'user' AS role,
    1 AS status,
    90 AS credit_score,
    s.id AS school_id,
    NOW() AS create_time,
    NOW() AS update_time,
    0 AS deleted
  FROM seed_schools s
  CROSS JOIN generate_series(1, 2) gs
)
INSERT INTO sys_user (username, nickname, password, email, phone, address, gender, age, avatar, role, status, credit_score, school_id, create_time, update_time, deleted)
SELECT username, nickname, password, email, phone, address, gender, age, avatar, role, status, credit_score, school_id, create_time, update_time, deleted
FROM users_to_insert
ON CONFLICT (username) DO NOTHING;

WITH demo_users AS (
  SELECT id AS user_id, school_id
  FROM sys_user
  WHERE deleted = 0 AND role = 'user' AND username LIKE 'demo_%'
),
items AS (
  SELECT u.user_id, u.school_id, gs AS n
  FROM demo_users u
  CROSS JOIN generate_series(1, 6) gs
)
INSERT INTO product (
  user_id, school_id, title, description, category, price, original_price, condition_score, condition_desc, images, status, view_count, favorite_count, ai_analyzed, ai_suggestions, create_time, update_time, deleted
)
SELECT
  i.user_id,
  i.school_id,
  CASE (i.n % 8)
    WHEN 0 THEN '二手平板 64G（学习用）'
    WHEN 1 THEN '宿舍小冰箱（静音省电）'
    WHEN 2 THEN '运动鞋 42 码（轻微磨损）'
    WHEN 3 THEN '图书音像资料合集（期末备考）'
    WHEN 4 THEN '羽毛球拍（含球）'
    WHEN 5 THEN '美妆护肤套装（未拆封）'
    WHEN 6 THEN '母婴玩具积木（九成新）'
    ELSE '其他：校园通勤双肩包'
  END,
  '【演示数据】用户与商品展示',
  CASE (i.n % 5)
    WHEN 0 THEN '电子产品'
    WHEN 1 THEN '家用电器'
    WHEN 2 THEN '服饰配饰'
    WHEN 3 THEN '图书音像'
    ELSE '其他'
  END,
  (50 + (i.n * 10))::numeric(10,2),
  (80 + (i.n * 10))::numeric(10,2),
  8,
  '正常使用痕迹',
  '[]',
  2,
  0,
  0,
  0,
  NULL,
  NOW(),
  NOW(),
  0
FROM items i;

COMMIT;

BEGIN;

WITH seed_tag AS (
  SELECT to_char(NOW(), 'YYYYMMDDHH24MISS') AS tag
),
seed_users AS (
  SELECT
    'demo_school_' || s.id::text || '_' || t.tag AS username,
    '演示发布者' || s.name_zh AS nickname,
    '$2a$10$0nl8q9Kr7oyVO4DW65vJUuoc.NKCffPUP4eFjjz4YYdHGmwgflcR6' AS password,
    'demo_school_' || s.id::text || '_' || t.tag || '@example.com' AS email,
    s.id AS school_id
  FROM school s
  CROSS JOIN seed_tag t
  WHERE s.deleted = 0 AND s.status = 1
),
inserted AS (
  INSERT INTO sys_user (username, nickname, password, email, role, status, credit_score, school_id, create_time, update_time, deleted)
  SELECT username, nickname, password, email, 'user', 1, 90, school_id, NOW(), NOW(), 0
  FROM seed_users
  ON CONFLICT (username) DO NOTHING
  RETURNING id, school_id
),
resolved AS (
  SELECT u.id AS user_id, u.school_id
  FROM sys_user u
  JOIN seed_users su ON su.username = u.username
),
items AS (
  SELECT r.user_id, r.school_id, gs AS n
  FROM resolved r
  CROSS JOIN generate_series(1, 12) gs
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
  '【演示数据】学校维度商品展示',
  CASE (i.n % 5)
    WHEN 0 THEN '电子产品'
    WHEN 1 THEN '家用电器'
    WHEN 2 THEN '服饰配饰'
    WHEN 3 THEN '图书音像'
    ELSE '其他'
  END,
  (60 + (i.n * 8))::numeric(10,2),
  (90 + (i.n * 8))::numeric(10,2),
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

START TRANSACTION;

UPDATE product SET images = CONCAT('["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=', id, '"]') WHERE images LIKE '%unsplash.com%' OR images LIKE '%picsum.photos/photo-%';
UPDATE forum_post SET images = CONCAT('["https://images.unsplash.com/photo-1555529669-2269763671c0?w=500&id=', id, '"]') WHERE images LIKE '%unsplash.com%' OR images LIKE '%picsum.photos/photo-%';
UPDATE sys_user SET avatar = CONCAT('https://api.dicebear.com/7.x/avataaars/svg?seed=', username) WHERE avatar LIKE '%unsplash.com%' OR avatar LIKE '%picsum.photos/photo-%';

-- 4. 淇 campus_service 琛?-- (濡傛灉璇ヨ〃鏈夊浘鐗囧瓧娈碉紝鐩墠鐪嬫潵娌℃湁锛屼絾涓轰簡淇濋櫓璧疯)
-- UPDATE `campus_service` SET ...

COMMIT;




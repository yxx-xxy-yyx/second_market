BEGIN;

UPDATE product
SET images = '[]'
WHERE images LIKE '%unsplash.com%' OR images LIKE '%picsum.photos/photo-%';

UPDATE forum_post
SET images = '[]'
WHERE images LIKE '%unsplash.com%' OR images LIKE '%picsum.photos/photo-%';

UPDATE sys_user
SET avatar = NULL
WHERE avatar LIKE '%unsplash.com%' OR avatar LIKE '%picsum.photos/photo-%';

COMMIT;

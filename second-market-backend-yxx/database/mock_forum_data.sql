-- 设置编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE `second_market`;

-- 清理之前的乱码数据
DELETE FROM forum_post;
ALTER TABLE forum_post AUTO_INCREMENT = 1;

-- 插入真实的演示数据
INSERT INTO forum_post (user_id, school_id, category, title, content, images, is_school_only, view_count, comment_count, create_time, update_time) VALUES
(2, 1, '校园闲聊', '首尔大学图书馆这周五有座吗？', '想去复习期中考试，不知道周五人多不多，求学长学姐告知！', '[]', 1, 45, 2, NOW(), NOW()),
(3, 1, '二手求购', '求购一个二手显示器', '最近宿舍写代码太费眼了，想收个24寸左右的显示器，有的同学请私聊。', '["https://picsum.photos/800/600?random=10"]', 0, 12, 1, NOW(), NOW()),
(4, 2, '出闲置', '转让一把人体工学椅', 'KAIST西门自取，用了半年，保护得很好，因为要回国了所以低价转让。', '["https://picsum.photos/800/600?random=11"]', 1, 89, 5, NOW(), NOW()),
(5, 4, '校园闲聊', '延世大学校庆活动有人一起吗？', '这周末有校庆表演，希望能组队一起去看，男女不限！', '["https://picsum.photos/800/600?random=12"]', 0, 156, 12, NOW(), NOW()),
(2, 3, '二手求购', '高丽大学附近求租房', '求租一个One-room，最好靠近地铁站，租金在50w左右。', '[]', 0, 34, 0, NOW(), NOW()),
(3, 1, '校园闲聊', '首尔大学食堂哪个最好吃？', '刚入学，感觉食堂太多了选不过来，大家推荐一下呀！', '["https://picsum.photos/800/600?random=13"]', 0, 210, 8, NOW(), NOW()),
(4, 4, '出闲置', '低价出教材，延世大学专业课', '包括经济学原理、中级微观等，保存完好，需要的同学带走。', '["https://picsum.photos/800/600?random=14"]', 1, 56, 3, NOW(), NOW());

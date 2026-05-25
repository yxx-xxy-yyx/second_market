INSERT INTO school (school_code, name_zh, name_ko, name_en, city_zh, city_ko, city_en) VALUES
('INHA', '仁荷大学', '인하대학교', 'Inha University', '仁川', '인천', 'Incheon'),
('KONKUK', '建国大学', '건국대학교', 'Konkuk University', '首尔', '서울', 'Seoul'),
('DANKOOK', '檀国大学', '단국대학교', 'Dankook University', '龙仁', '용인', 'Yongin'),
('UOS', '首尔市立大学', '서울시립대학교', 'University of Seoul', '首尔', '서울', 'Seoul'),
('HUFS', '韩国外国语大学', '한국외국어대학교', 'Hankuk University of Foreign Studies', '首尔', '서울', 'Seoul'),
('CNU', '全南大学', '전남대학교', 'Chonnam National University', '光州', '광주', 'Gwangju'),
('JBNU', '全北大学', '전북대학교', 'Chonbuk National University', '全州', '전주', 'Jeonju'),
('CNNU', '忠南大学', '충남대학교', 'Chungnam National University', '大田', '대전', 'Daejeon'),
('GNU', '庆尚国立大学', '경상국립대학교', 'Gyeongsang National University', '晋州', '진주', 'Jinju'),
('JNU', '济州大学', '제주대학교', 'Jeju National University', '济州', '제주', 'Jeju'),
('CBNU', '忠北大学', '충북대학교', 'Chungbuk National University', '清州', '청주', 'Cheongju'),
('KNU_GANGWON', '江原大学', '강원대학교', 'Kangwon National University', '春川', '춘천', 'Chuncheon')
ON CONFLICT (school_code) DO NOTHING;

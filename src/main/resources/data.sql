-- 상위 카테고리
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(1, '인문사회', NULL);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(2, '자연과학', NULL);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(3, '공학·기술', NULL);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(4, '경제·경영', NULL);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(5, '예술·문화', NULL);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(6, '스포츠·라이프스타일', NULL);

-- 인문사회 세부
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(7, '철학', 1);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(8, '역사', 1);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(9, '사회학', 1);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(10, '언어', 1);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(11, '심리', 1);

-- 자연과학 세부
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(12, '수학', 2);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(13, '물리', 2);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(14, '화학', 2);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(15, '생물', 2);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(16, '의료', 2);

-- 공학·기술 세부
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(17, 'IT', 3);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(18, 'AI', 3);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(19, '전자', 3);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(20, '기계', 3);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(21, '산업공학', 3);

-- 경제·경영 세부
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(22, '경제', 4);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(23, '비즈니스', 4);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(24, '마케팅', 4);

-- 예술·문화 세부
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(25, '미술', 5);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(26, '음악', 5);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(27, '문학', 5);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(28, 'UI/UX', 5);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(29, '건축', 5);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(30, '영화', 5);

-- 스포츠·라이프스타일 세부
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(31, '건강', 6);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(32, '스포츠', 6);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(33, '여행', 6);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(34, '생활', 6);
INSERT INTO category(category_id, category_name, parent_category_id) VALUES(35, '환경', 6);

-- =============================
-- Balance Game (OX + 선호도 퀴즈)
-- =============================
-- OX 퀴즈 (사회학, 언어, 철학, 심리, 역사) - 각 1문항씩
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (101, 'OX', '기능주의는 사회 구조가 개인의 행동에 미치는 영향을 중요시한다.', true, '사회학', true),
  (102, 'OX', '문화 상대주의는 모든 문화의 관행이 윤리적으로 동등하다고 주장한다.', false, '사회학', true),
  (103, 'OX', '모든 인간 언어에는 보편적인 문법 구조가 존재한다는 것이 촘스키의 주요 주장이다.', true, '언어', true),
  (104, 'OX', '영어의 모든 불규칙 동사는 역사적으로 규칙 동사였다가 변화한 것이다.', false, '언어', true),
  (105, 'OX', '데카르트의 "나는 생각한다, 고로 존재한다"는 회의주의에 대한 반박으로 제시되었다.', true, '철학', true),
  (106, 'OX', '칸트의 정언명령은 결과에 기반한 윤리적 판단을 지지한다.', false, '철학', true),
  (107, 'OX', '프로이트의 초자아는 도덕적 규범과 가치를 담당한다.', true, '심리', true),
  (108, 'OX', '매슬로우의 욕구 위계 이론에서 가장 기본적인 욕구는 사회적 소속감이다.', false, '심리', true),
  (109, 'OX', '한국의 임진왜란은 16세기에 일어났다.', true, '역사', true),
  (110, 'OX', '산업혁명은 아시아에서 시작되어 유럽으로 전파되었다.', false, '역사', true);

-- 선호도 퀴즈 예시 2세트
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (201, 'PREFERENCE', '더 관심가는 주제는?', NULL, '사회학', true),
  (202, 'PREFERENCE', '더 관심가는 주제는?', NULL, '심리', true);

-- 201: A/B (각 옵션은 하위 카테고리 1개 필수)
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  (11001, 201, 'A', '근시 유전자는 왜 사라지지 않았을까?', '생물', 30001),
  (11002, 201, 'B', '무기징역과 사형수의 교도소 생활은 뭐가 다를까?', '사회학', 30002);
-- 202: A/B
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  (11003, 202, 'A', '도시의 젠트리피케이션, 누구를 위한 변화일까?', '사회학', 30003),
  (11004, 202, 'B', '가짜뉴스는 왜 확산되는가?', '언어', 30004);
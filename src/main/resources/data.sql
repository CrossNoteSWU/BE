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
-- OX 퀴즈
-- 인문사회 분야
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  -- 사회학 (5문항)
  (101, 'OX', '기능주의는 사회 구조가 개인의 행동에 미치는 영향을 중요시한다.', true, '사회학', true),
  (102, 'OX', '문화 상대주의는 모든 문화의 관행이 윤리적으로 동등하다고 주장한다.', false, '사회학', true),
  (111, 'OX', '피에르 부르디외의 ''아비투스'' 개념은 개인의 성격이 사회적 위치와 무관하게 형성된다고 본다.', false, '사회학', true),
  (112, 'OX', '사회적 자본이란 개인이 소유한 물질적 자원만을 의미한다.', false, '사회학', true),
  (113, 'OX', '뒤르켐의 ''집합의식'' 개념은 사회 구성원들이 공유하는 신념과 도덕적 태도를 가리킨다.', true, '사회학', true),
  -- 언어 (5문항)
  (103, 'OX', '모든 인간 언어에는 보편적인 문법 구조가 존재한다는 것이 촘스키의 주요 주장이다.', true, '언어', true),
  (104, 'OX', '영어의 모든 불규칙 동사는 역사적으로 규칙 동사였다가 변화한 것이다.', false, '언어', true),
  (114, 'OX', '사피어-워프 가설에 따르면, 언어가 사고방식에 영향을 미친다.', true, '언어', true),
  (115, 'OX', '한국어는 교착어로, 어근에 접사가 붙어 단어가 형성된다.', true, '언어', true),
  (116, 'OX', '언어학에서 ''최소 대립쌍''이란 의미는 비슷하지만 발음이 완전히 다른 단어 쌍을 의미한다.', false, '언어', true),
  -- 철학 (5문항)
  (105, 'OX', '데카르트의 "나는 생각한다, 고로 존재한다"는 회의주의에 대한 반박으로 제시되었다.', true, '철학', true),
  (106, 'OX', '칸트의 정언명령은 결과에 기반한 윤리적 판단을 지지한다.', false, '철학', true),
  (117, 'OX', '니체의 ''신은 죽었다''는 말은 문자 그대로 신의 물리적 죽음을 의미한다.', false, '철학', true),
  (118, 'OX', '플라톤의 ''동굴의 비유''는 지식의 한계와 실재에 대한 인식의 어려움을 설명한다.', true, '철학', true),
  (119, 'OX', '존 로크는 인간이 태어날 때 선천적 지식을 가지고 태어난다고 주장했다.', false, '철학', true),
  -- 심리 (5문항)
  (107, 'OX', '프로이트의 초자아는 도덕적 규범과 가치를 담당한다.', true, '심리', true),
  (108, 'OX', '매슬로우의 욕구 위계 이론에서 가장 기본적인 욕구는 사회적 소속감이다.', false, '심리', true),
  (120, 'OX', '스키너의 조작적 조건화 이론에 따르면, 행동은 그 결과에 의해 강화되거나 약화된다.', true, '심리', true),
  (121, 'OX', '인지 부조화 이론은 서로 모순되는 신념이나 행동을 가질 때 심리적 불편함을 경험한다는 이론이다.', true, '심리', true),
  (122, 'OX', '스톡홀름 증후군은 외상 후 스트레스 장애의 다른 이름이다.', false, '심리', true),
  -- 역사 (5문항)
  (109, 'OX', '한국의 임진왜란은 16세기에 일어났다.', true, '역사', true),
  (110, 'OX', '산업혁명은 아시아에서 시작되어 유럽으로 전파되었다.', false, '역사', true),
  (123, 'OX', '프랑스 혁명의 직접적인 원인은 미국 독립 전쟁이었다.', false, '역사', true),
  (124, 'OX', '오스만 제국의 멸망은 제1차 세계대전 이후에 일어났다.', true, '역사', true),
  (125, 'OX', '마야 문명은 중앙아메리카 지역에서 번성했던 고대 문명이다.', true, '역사', true),
  -- 자연과학 분야
  -- 수학 (5문항)
  (201, 'OX', '미분은 함수의 순간 변화율을 측정하는 도구이다.', true, '수학', true),
  (202, 'OX', '모든 연속 함수는 미분 가능하다.', false, '수학', true),
  (203, 'OX', '소수는 1과 자기 자신 외에는 약수가 없는 자연수다.', true, '수학', true),
  (204, 'OX', '확률 분포의 평균은 항상 분포의 중심값과 같다.', false, '수학', true),
  (205, 'OX', '로그 함수는 지수 함수를 역함수로 가진다.', true, '수학', true),
  -- 물리 (5문항)
  (206, 'OX', '진공에서도 빛은 일정한 속도로 전파된다.', true, '물리', true),
  (207, 'OX', '등속도 운동에서는 가속도가 0이다.', true, '물리', true),
  (208, 'OX', '에너지는 절대 생성되거나 소멸될 수 있다.', false, '물리', true),
  (209, 'OX', '소리는 공기보다 물에서 더 빠르게 전파된다.', true, '물리', true),
  (210, 'OX', '중력은 거리가 멀어질수록 강해진다.', false, '물리', true),
  -- 화학 (5문항)
  (211, 'OX', 'pH 7은 항상 중성을 의미한다.', true, '화학', true),
  (212, 'OX', '이온 결합은 금속과 비금속 사이에서 주로 일어난다.', true, '화학', true),
  (213, 'OX', '촉매는 반응의 활성화 에너지를 낮춘다.', true, '화학', true),
  (214, 'OX', '모든 원자는 동일한 수의 중성자를 가진다.', false, '화학', true),
  (215, 'OX', '산화는 전자를 잃는 반응이다.', true, '화학', true),
  -- 생물 (5문항)
  (216, 'OX', 'DNA는 아데닌-티민, 구아닌-시토신과 짝을 이룬다.', true, '생물', true),
  (217, 'OX', '바이러스는 스스로 단독 번식이 가능하다.', false, '생물', true),
  (218, 'OX', '광합성은 식물이 빛 에너지를 화학 에너지로 전환하는 과정이다.', true, '생물', true),
  (219, 'OX', '적혈구는 핵을 가지고 있다.', false, '생물', true),
  (220, 'OX', '진화는 개인 단위에서 일어난다.', false, '생물', true),
  -- 의료 (5문항)
  (221, 'OX', '백신은 면역 체계를 활성화시켜 특정 질병을 예방한다.', true, '의료', true),
  (222, 'OX', '항생제는 바이러스 감염에도 효과적이다.', false, '의료', true),
  (223, 'OX', '고혈압은 특별한 증상 없이도 발견될 수 있다.', true, '의료', true),
  (224, 'OX', '암은 항상 전이 과정을 거친다.', false, '의료', true),
  (225, 'OX', 'MRI는 X선 방출로 영상을 만드는 장비이다.', false, '의료', true),
  -- 공학·기술 분야
  -- IT (5문항)
  (301, 'OX', '클라우드는 원격 서버에서 데이터를 저장·처리하는 기술이다.', true, 'IT', true),
  (302, 'OX', 'HTTP는 모든 데이터를 자동으로 암호화한다.', false, 'IT', true),
  (303, 'OX', '데이터베이스 쿼리는 정보를 조회하는 데 사용된다.', true, 'IT', true),
  (304, 'OX', 'IPv6는 IPv4보다 주소 공간이 더 좁다.', false, 'IT', true),
  (305, 'OX', '캐시는 데이터를 빠르게 접근하기 위해 사용하는 저장 공간이다.', true, 'IT', true),
  -- AI (5문항)
  (306, 'OX', '머신러닝은 데이터를 기반으로 패턴을 학습하는 기술이다.', true, 'AI', true),
  (307, 'OX', '과적합은 학습 데이터에는 잘 맞지만 새로운 데이터에는 약한 상태이다.', true, 'AI', true),
  (308, 'OX', '신경망의 뉴런 수가 많을수록 무조건 정확도가 높아진다.', false, 'AI', true),
  (309, 'OX', 'AI는 감정이 있다.', false, 'AI', true),
  (310, 'OX', '지도학습은 정답 라벨이 있는 데이터로 학습한다.', true, 'AI', true),
  -- 전자 (5문항)
  (311, 'OX', '반도체는 전기가 흐르기도 하고 흐르지 않기도 하는 특성을 가진다.', true, '전자', true),
  (312, 'OX', '전압은 전기의 흐름을 의미한다.', false, '전자', true),
  (313, 'OX', '저항은 전류 흐름을 방해하는 요소이다.', true, '전자', true),
  (314, 'OX', 'LED는 전류가 흐르면 빛을 낸다.', true, '전자', true),
  (315, 'OX', '배터리는 전자기파를 이용해 전기를 생성한다.', false, '전자', true),
  -- 기계 (5문항)
  (316, 'OX', '모멘트는 회전력을 의미한다.', true, '기계', true),
  (317, 'OX', '마찰력이 0이면 운동은 반드시 멈춘다.', false, '기계', true),
  (318, 'OX', '압력은 단위 면적당 가해지는 힘이다.', true, '기계', true),
  (319, 'OX', '토크는 직선 운동과 직접적 관련이 있다.', false, '기계', true),
  (320, 'OX', '유체는 고체처럼 일정한 형태를 유지한다.', false, '기계', true),
  -- 산업공학 (5문항)
  (321, 'OX', '공정 최적화는 생산성을 향상시키기 위한 핵심 과정이다.', true, '산업공학', true),
  (322, 'OX', '린 생산 방식은 재고를 최대화하는 것이 목표다.', false, '산업공학', true),
  (323, 'OX', '작업 표준화는 품질 편차를 줄이는 데 기여한다.', true, '산업공학', true),
  (324, 'OX', '시뮬레이션은 실제 공정 테스트보다 비용이 크게 든다.', false, '산업공학', true),
  (325, 'OX', 'KPI는 핵심 성과 지표를 의미한다.', true, '산업공학', true),
  -- 경제·경영 분야
  -- 경제 (5문항)
  (401, 'OX', '수요 법칙은 가격이 오르면 수요가 감소한다고 본다.', true, '경제', true),
  (402, 'OX', 'GDP는 한 나라의 모든 경제적 생산 활동을 포함한다.', true, '경제', true),
  (403, 'OX', '인플레이션은 화폐 가치가 상승하는 현상이다.', false, '경제', true),
  (404, 'OX', '독점 시장에는 경쟁자가 많다.', false, '경제', true),
  (405, 'OX', '기회비용은 선택하지 않은 대안의 가치이다.', true, '경제', true),
  -- 마케팅 (5문항)
  (406, 'OX', '브랜드 로열티는 고객이 브랜드에 충성하는 정도를 의미한다.', true, '마케팅', true),
  (407, 'OX', 'A/B 테스트는 두 가지 옵션을 비교하여 더 나은 것을 찾는 방식이다.', true, '마케팅', true),
  (408, 'OX', 'STP에서 P는 Positioning을 의미한다.', true, '마케팅', true),
  (409, 'OX', '제품 포지셔닝은 가격 정책과 무관하다.', false, '마케팅', true),
  (410, 'OX', '바이럴 마케팅은 입소문을 활용하는 방법이다.', true, '마케팅', true),
  -- 비즈니스 (5문항)
  (411, 'OX', '스타트업은 빠른 성장과 확장을 목표로 한다.', true, '비즈니스', true),
  (412, 'OX', '수익 모델은 기업이 이윤을 내는 구체적 방법을 의미한다.', true, '비즈니스', true),
  (413, 'OX', '고정비는 생산량에 따라 달라지는 비용이다.', false, '비즈니스', true),
  (414, 'OX', '벤처 기업은 항상 대기업보다 규모가 크다.', false, '비즈니스', true),
  (415, 'OX', 'MVP는 최소 기능 제품을 의미한다.', true, '비즈니스', true),
  -- 예술·문화 분야
  -- 미술 (5문항)
  (501, 'OX', '인상주의는 빛의 순간적 인상을 포착하는 데 초점을 둔다.', true, '미술', true),
  (502, 'OX', '큐비즘은 관찰자의 시점을 하나로만 제한한다.', false, '미술', true),
  (503, 'OX', '모더니즘은 전통 미술 형식을 벗어나려는 움직임이다.', true, '미술', true),
  (504, 'OX', '수묵화는 유화보다 제작 과정이 느리다.', false, '미술', true),
  (505, 'OX', '팝아트는 대중문화를 예술 소재로 활용한다.', true, '미술', true),
  -- 음악 (5문항)
  (506, 'OX', '클래식 음악의 템포는 곡마다 지정된 속도를 따른다.', true, '음악', true),
  (507, 'OX', '장조는 주로 밝은 느낌, 단조는 어두운 느낌을 준다.', true, '음악', true),
  (508, 'OX', '재즈는 즉흥 연주 요소가 거의 없다.', false, '음악', true),
  (509, 'OX', 'EDM은 전자 악기를 기반으로 한다.', true, '음악', true),
  (510, 'OX', '음역대는 사람마다 동일하다.', false, '음악', true),
  -- 문학 (5문항)
  (511, 'OX', '상징주의 문학은 추상적이고 감각적인 표현을 선호한다.', true, '문학', true),
  (512, 'OX', '플롯은 이야기의 전체 구성 흐름을 의미한다.', true, '문학', true),
  (513, 'OX', '비유는 문자 그대로의 의미를 강조하는 표현이다.', false, '문학', true),
  (514, 'OX', '시는 반드시 운율을 가져야 한다.', false, '문학', true),
  (515, 'OX', '옴니버스 구조는 여러 독립된 이야기를 하나의 테마로 묶은 것이다.', true, '문학', true),
  -- UI/UX (5문항)
  (516, 'OX', 'UX는 사용자 경험을 의미한다.', true, 'UI/UX', true),
  (517, 'OX', 'UI는 제품의 시각적 요소만을 의미한다.', false, 'UI/UX', true),
  (518, 'OX', '사용성 테스트는 제품의 문제점을 파악하는 과정이다.', true, 'UI/UX', true),
  (519, 'OX', '유저 플로우는 사용자의 이동 경로를 시각화한 것이다.', true, 'UI/UX', true),
  (520, 'OX', '좋은 UX는 무조건 화려한 화면 디자인이 필요하다.', false, 'UI/UX', true),
  -- 건축 (5문항)
  (521, 'OX', '고딕 건축은 첨탑과 스테인드글라스를 특징으로 한다.', true, '건축', true),
  (522, 'OX', '바실리카 구조는 종교 건축에서 자주 사용됐다.', true, '건축', true),
  (523, 'OX', '르네상스 건축은 직선과 대칭을 강조했다.', true, '건축', true),
  (524, 'OX', '미니멀리즘 건축은 복잡한 장식을 강조한다.', false, '건축', true),
  (525, 'OX', '건축 설계는 구조적 안정성과 미적 요소를 동시에 고려해야 한다.', true, '건축', true),
  -- 영화 (5문항)
  (526, 'OX', '롱테이크는 컷 없이 긴 시간 촬영하는 기법이다.', true, '영화', true),
  (527, 'OX', '시나리오의 삼막 구조는 도입–전개–결말로 구성된다.', true, '영화', true),
  (528, 'OX', '다큐멘터리는 허구적 요소만으로 이루어진다.', false, '영화', true),
  (529, 'OX', '사운드트랙은 영화 분위기 형성에 중요한 역할을 한다.', true, '영화', true),
  (530, 'OX', '카메라 앵글은 장면의 감정 전달과 무관하다.', false, '영화', true),
  -- 스포츠·라이프스타일 분야
  -- 건강 (5문항)
  (601, 'OX', '수면 부족은 면역력 저하를 유발할 수 있다.', true, '건강', true),
  (602, 'OX', '물은 갈증이 날 때만 마시면 충분하다.', false, '건강', true),
  (603, 'OX', '유산소 운동은 심혈관 건강 개선에 효과적이다.', true, '건강', true),
  (604, 'OX', '가공식품은 항상 건강에 해롭다.', false, '건강', true),
  (605, 'OX', '스트레스는 신체 질환을 악화시킬 수 있다.', true, '건강', true),
  -- 스포츠 (5문항)
  (606, 'OX', '근력 운동은 근섬유를 손상시키고 회복 과정에서 강화된다.', true, '스포츠', true),
  (607, 'OX', '스트레칭은 운동 전이 아니라 후에만 해야 한다.', false, '스포츠', true),
  (608, 'OX', '수영은 전신 운동이다.', true, '스포츠', true),
  (609, 'OX', '빠른 걸음은 칼로리 소모가 거의 없다.', false, '스포츠', true),
  (610, 'OX', '규칙적 운동은 정신 건강에도 긍정적이다.', true, '스포츠', true),
  -- 여행 (10문항)
  (611, 'OX', '스페인 안달루시아의 건축 양식에는 이슬람 문화의 흔적이 남아 있다.', true, '여행', true),
  (612, 'OX', '일본의 료칸에서는 옛 관습 때문에 아침 식사와 저녁 식사를 한 방에서 먹는 것이 금기였다.', false, '여행', true),
  (613, 'OX', '이탈리아 베네치아는 매년 약 1~2mm씩 가라앉고 있다.', true, '여행', true),
  (614, 'OX', '북극권의 오로라는 남극권에서는 관측되지 않는다.', false, '여행', true),
  (615, 'OX', '두바이는 40년 전만 해도 대부분 사막과 어촌으로 이루어져 있었다.', true, '여행', true),
  (616, 'OX', '캄보디아 앙코르와트는 원래 힌두교 사원으로 지어졌고, 이후 불교 사원으로 바뀌었다.', true, '여행', true),
  (617, 'OX', '미국의 그랜드캐니언은 단순히 침식으로만 형성된 지형이다.', false, '여행', true),
  (618, 'OX', '호주의 코알라는 실제로 물을 거의 마시지 않아 이름이 ''물을 마시지 않는 동물''이라는 뜻에서 유래했다.', true, '여행', true),
  (619, 'OX', '남미의 우유니 소금사막은 비가 오는 날 더 안전하게 이동할 수 있다.', false, '여행', true),
  (620, 'OX', '북유럽의 바이킹은 항상 뿔 달린 헬멧을 썼다.', false, '여행', true),
  -- 생활 (5문항)
  (621, 'OX', '미니멀리즘은 불필요한 물건을 줄이는 생활 방식이다.', true, '생활', true),
  (622, 'OX', '규칙적인 생활은 수면의 질에 영향을 준다.', true, '생활', true),
  (623, 'OX', '정리정돈은 뇌의 인지 부담을 줄일 수 있다.', true, '생활', true),
  (624, 'OX', '멀티태스킹은 항상 생산성을 높인다.', false, '생활', true),
  (625, 'OX', '커피는 항상 피로 회복에 도움을 준다.', false, '생활', true),
  -- 환경 (5문항)
  (626, 'OX', '온실가스는 지구 평균 온도를 상승시킨다.', true, '환경', true),
  (627, 'OX', '재활용은 에너지 소비를 감소시키는 데 기여할 수 있다.', true, '환경', true),
  (628, 'OX', '미세먼지는 자연적 요인으로만 발생한다.', false, '환경', true),
  (629, 'OX', '산성비는 생태계에 악영향을 미칠 수 있다.', true, '환경', true),
  (630, 'OX', '해수면 상승은 북반구에만 영향을 준다.', false, '환경', true);

-- 선호도 퀴즈
-- 인문사회 분야 (5세트)
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (701, 'PREFERENCE', '더 관심가는 주제는?', NULL, '언어', true),
  (702, 'PREFERENCE', '더 관심가는 주제는?', NULL, '심리', true),
  (703, 'PREFERENCE', '더 관심가는 주제는?', NULL, '심리', true),
  (704, 'PREFERENCE', '더 관심가는 주제는?', NULL, '심리', true),
  (705, 'PREFERENCE', '더 관심가는 주제는?', NULL, '언어', true);

-- 인문사회 선호도 옵션
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  -- 세트 1
  (12001, 701, 'A', '세계 각 언어는 왜 서로 다른 어순을 갖게 되었을까?', '언어', 30005),
  (12002, 701, 'B', '고대 문명은 왜 비슷한 시기에 갑자기 등장했을까?', '역사', 30006),
  -- 세트 2
  (12003, 702, 'A', '내향성과 외향성은 타고나는 것일까?', '심리', 30007),
  (12004, 702, 'B', '국가는 왜 이것이 필요하다고 느끼는가: ''국경''의 철학적 의미는 무엇일까?', '철학', 30008),
  -- 세트 3
  (12005, 703, 'A', '기억은 왜 왜곡될까?', '심리', 30009),
  (12006, 703, 'B', '민주주의는 어떻게 지금의 형태로 발전했을까?', '역사', 30010),
  -- 세트 4
  (12007, 704, 'A', '인간은 왜 거짓말을 할까?', '심리', 30011),
  (12008, 704, 'B', '여성의 사회적 지위는 역사적으로 어떻게 변화해왔을까?', '역사', 30012),
  -- 세트 5
  (12009, 705, 'A', '언어는 어떻게 처음 생겨났을까?', '언어', 30013),
  (12010, 705, 'B', '철학은 과학과 어떤 방식으로 서로 영향을 주고받았을까?', '철학', 30014);

-- 자연과학 분야 (5세트)
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (801, 'PREFERENCE', '더 관심가는 주제는?', NULL, '생물', true),
  (802, 'PREFERENCE', '더 관심가는 주제는?', NULL, '수학', true),
  (803, 'PREFERENCE', '더 관심가는 주제는?', NULL, '생물', true),
  (804, 'PREFERENCE', '더 관심가는 주제는?', NULL, '의료', true),
  (805, 'PREFERENCE', '더 관심가는 주제는?', NULL, 'AI', true);

-- 자연과학 선호도 옵션
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  -- 세트 1
  (13001, 801, 'A', '인간은 왜 잠을 자야만 할까?', '의료', 30015),
  (13002, 801, 'B', '공룡은 왜 멸종했을까? (유력설 비교)', '생물', 30016),
  -- 세트 2
  (13003, 802, 'A', '숫자 ''0''은 왜 인류 역사에 늦게 등장했을까?', '수학', 30017),
  (13004, 802, 'B', '빛의 속도는 왜 우주의 속도 제한일까?', '물리', 30018),
  -- 세트 3
  (13005, 803, 'A', '인류는 왜 특정 음식을 좋아하도록 진화했을까?', '생물', 30019),
  (13006, 803, 'B', '우주는 왜 가속 팽창하고 있을까?', '물리', 30020),
  -- 세트 4
  (13007, 804, 'A', '추위보다 더 위험한 것은 더위일까?', '의료', 30021),
  (13008, 804, 'B', '물질은 왜 고체·액체·기체 상태를 갖게 되었을까?', '화학', 30022),
  -- 세트 5
  (13009, 805, 'A', '인공지능은 인간의 뇌를 얼마나 따라잡은 걸까?', 'AI', 30023),
  (13010, 805, 'B', '지구의 자기장은 어떻게 생기고 왜 중요할까?', '물리', 30024);

-- 공학·기술 분야 (3세트)
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (901, 'PREFERENCE', '더 관심가는 주제는?', NULL, '전자', true),
  (902, 'PREFERENCE', '더 관심가는 주제는?', NULL, 'IT', true),
  (903, 'PREFERENCE', '더 관심가는 주제는?', NULL, 'IT', true);

-- 공학·기술 선호도 옵션
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  -- 세트 1
  (14001, 901, 'A', '스마트폰 배터리는 왜 쉽게 닳을까?', '전자', 30025),
  (14002, 901, 'B', 'AI는 어떻게 그림을 ''이해''한다고 말할 수 있을까?', 'AI', 30026),
  -- 세트 2
  (14003, 902, 'A', '자율주행차는 왜 완벽하게 구현되기 어려울까?', 'IT', 30027),
  (14004, 902, 'B', '반도체는 왜 오늘날 세계 경제의 핵심일까?', '전자', 30028),
  -- 세트 3
  (14005, 903, 'A', '비밀번호는 왜 복잡해야 할까?', 'IT', 30029),
  (14006, 903, 'B', '알고리즘은 왜 편향될까?', 'AI', 30030);

-- 경제·경영 분야 (3세트)
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (1001, 'PREFERENCE', '더 관심가는 주제는?', NULL, '경제', true),
  (1002, 'PREFERENCE', '더 관심가는 주제는?', NULL, '경제', true),
  (1003, 'PREFERENCE', '더 관심가는 주제는?', NULL, '비즈니스', true);

-- 경제·경영 선호도 옵션
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  -- 세트 1
  (15001, 1001, 'A', '사람들은 왜 할인에 약할까? (소비자 심리학)', '경제', 30031),
  (15002, 1001, 'B', '기업은 왜 플랫폼 모델로 이동하고 있을까?', '비즈니스', 30032),
  -- 세트 2
  (15003, 1002, 'A', '부의 불평등은 왜 심화될까?', '경제', 30033),
  (15004, 1002, 'B', '마케팅은 왜 사람의 무의식을 공략할까?', '마케팅', 30034),
  -- 세트 3
  (15005, 1003, 'A', '재택근무는 왜 논쟁이 될까?', '비즈니스', 30035),
  (15006, 1003, 'B', '회사는 왜 ''평균 성과자''를 중요하게 여길까?', '비즈니스', 30036);

-- 예술·문화 분야 (2세트)
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (1101, 'PREFERENCE', '더 관심가는 주제는?', NULL, '음악', true),
  (1102, 'PREFERENCE', '더 관심가는 주제는?', NULL, '문학', true);

-- 예술·문화 선호도 옵션
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  -- 세트 1
  (16001, 1101, 'A', '음악은 왜 감정을 자극할까?', '음악', 30037),
  (16002, 1101, 'B', '건축은 왜 시대에 따라 전혀 다르게 변할까?', '건축', 30038),
  -- 세트 2
  (16003, 1102, 'A', '소설은 왜 인간의 사고를 바꿀 수 있을까?', '문학', 30039),
  (16004, 1102, 'B', '영화는 왜 특정 장면을 통해 관객 감정을 설계할까?', '영화', 30040);

-- 스포츠·라이프스타일 분야 (2세트)
INSERT INTO balance_quiz(id, type, question, ox_answer, category, active)
VALUES
  (1201, 'PREFERENCE', '더 관심가는 주제는?', NULL, '건강', true),
  (1202, 'PREFERENCE', '더 관심가는 주제는?', NULL, '여행', true);

-- 스포츠·라이프스타일 선호도 옵션
INSERT INTO balance_option(id, quiz_id, label, text, category, curation_id) VALUES
  -- 세트 1
  (17001, 1201, 'A', '아침형 인간이 더 건강하다는 말은 사실일까?', '건강', 30041),
  (17002, 1201, 'B', '걷기는 운동으로 충분할까?', '스포츠', 30042),
  -- 세트 2
  (17003, 1202, 'A', '여행은 왜 사람을 행복하게 만들까?', '여행', 30043),
  (17004, 1202, 'B', '미세먼지는 왜 특정 계절에 심해질까?', '환경', 30044);
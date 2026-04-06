-- ==========================================
-- MUSINSA DDL2: 신규 테이블 및 컬럼 추가
-- (기존 musinsa_ddl1.sql 실행 후 이 파일 실행)
-- ==========================================

-- ① members 테이블에 신규 컬럼 추가 (없는 경우에만)
ALTER TABLE members ADD COLUMN IF NOT EXISTS role    VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER';
ALTER TABLE members ADD COLUMN IF NOT EXISTS grade   VARCHAR(20) NOT NULL DEFAULT 'BRONZE';
ALTER TABLE members ADD COLUMN IF NOT EXISTS status  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE members ADD COLUMN IF NOT EXISTS phone   VARCHAR(20);
ALTER TABLE members ADD COLUMN IF NOT EXISTS address VARCHAR(200);

-- ② items 테이블에 description 컬럼 추가
ALTER TABLE items ADD COLUMN IF NOT EXISTS description TEXT;

-- ③ orders 테이블에 status 컬럼 추가
ALTER TABLE orders ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PAID';

-- ④ 리뷰 테이블
CREATE TABLE IF NOT EXISTS reviews (
    id          SERIAL PRIMARY KEY,
    member_id   INTEGER NOT NULL,
    item_id     INTEGER NOT NULL,
    order_id    INTEGER,
    rating      INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content     TEXT    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_item   FOREIGN KEY (item_id)   REFERENCES items(id)   ON DELETE CASCADE
);

COMMENT ON TABLE  reviews         IS '상품 리뷰';
COMMENT ON COLUMN reviews.rating  IS '평점 1~5';

-- ⑤ Q&A 테이블
CREATE TABLE IF NOT EXISTS questions (
    id                SERIAL PRIMARY KEY,
    member_id         INTEGER NOT NULL,
    item_id           INTEGER,
    title             VARCHAR(200) NOT NULL,
    content           TEXT         NOT NULL,
    is_secret         BOOLEAN NOT NULL DEFAULT FALSE,
    is_answered       BOOLEAN NOT NULL DEFAULT FALSE,
    answer_content    TEXT,
    answer_member_id  INTEGER,
    answered_at       TIMESTAMP,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_item   FOREIGN KEY (item_id)   REFERENCES items(id)   ON DELETE SET NULL
);

COMMENT ON TABLE  questions           IS '상품 Q&A';
COMMENT ON COLUMN questions.is_secret IS '비밀글 여부';

-- ⑥ 더미 회원 데이터 (테스트용 - 비밀번호는 BCrypt 인코딩 필요, AdminInitializer가 처리)
-- admin 계정은 AdminInitializer가 자동 생성
-- 테스트 일반 회원
INSERT INTO members (name, login_id, login_pw, role, grade, status)
VALUES
  ('홍길동', 'user1@test.com',  'test1234', 'ROLE_USER', 'BRONZE', 'ACTIVE'),
  ('김무신', 'user2@test.com',  'test1234', 'ROLE_USER', 'SILVER', 'ACTIVE'),
  ('이사랑', 'user3@test.com',  'test1234', 'ROLE_USER', 'GOLD',   'ACTIVE')
ON CONFLICT (login_id) DO NOTHING;

-- ⑦ 더미 리뷰 (items 데이터 존재 후 삽입 가능)
-- items id가 1~16번 존재한다고 가정
INSERT INTO reviews (member_id, item_id, rating, content)
SELECT m.id, i.id,
       (ARRAY[5,4,5,3,4,5])[((i.id + m.id) % 6) + 1],
       CASE ((i.id + m.id) % 5)
         WHEN 0 THEN '핏이 너무 예뻐요! 소재도 좋고 재구매 의사 100%입니다.'
         WHEN 1 THEN '배송도 빠르고 상품도 사진과 동일해서 만족스럽습니다.'
         WHEN 2 THEN '사이즈는 정사이즈인데 제가 약간 크게 입는 걸 좋아해서 한 사이즈 업해서 샀어요.'
         WHEN 3 THEN '가격대비 퀄리티가 좋네요. 주변 친구들도 어디서 샀냐고 물어봤어요.'
         ELSE '색감이 사진보다 실제로 훨씬 이뻐요. 강추합니다!'
       END
FROM members m
CROSS JOIN items i
WHERE m.login_id LIKE 'user%@test.com'
  AND i.id <= 10
ON CONFLICT DO NOTHING;

-- ⑧ 더미 Q&A
INSERT INTO questions (member_id, item_id, title, content, is_secret, is_answered, answer_content, answered_at)
SELECT m.id, i.id,
       CASE (i.id % 4)
         WHEN 0 THEN '사이즈 문의드립니다'
         WHEN 1 THEN '색상 추가 예정인가요?'
         WHEN 2 THEN '재입고 언제 되나요?'
         ELSE    '교환/환불 가능한가요?'
       END,
       CASE (i.id % 4)
         WHEN 0 THEN '170cm 65kg인데 M이 맞을까요? 아니면 L을 사야 할까요?'
         WHEN 1 THEN '블랙 말고 화이트나 네이비 색상 추가될 예정이 있나요?'
         WHEN 2 THEN 'SOLD OUT인데 재입고 예정 있나요? 너무 갖고 싶어요.'
         ELSE    '단순 변심으로 교환/환불이 가능한지 문의드립니다.'
       END,
       (i.id % 3 = 0),
       (i.id % 2 = 0),
       CASE WHEN i.id % 2 = 0 THEN '안녕하세요! 문의 주셔서 감사합니다. 해당 상품은 ' ||
         CASE (i.id % 4)
           WHEN 0 THEN 'M 사이즈를 추천드립니다. 기장이 약간 기니 참고 부탁드립니다.'
           WHEN 2 THEN '다음 주 중으로 재입고 예정입니다. 알림 신청 해두시면 편리합니다.'
           ELSE    '구매 후 7일 이내 교환/환불 가능하며, 고객센터로 연락 주세요.'
         END ELSE NULL END,
       CASE WHEN i.id % 2 = 0 THEN NOW() ELSE NULL END
FROM members m
CROSS JOIN items i
WHERE m.login_id = 'user1@test.com'
  AND i.id <= 8
ON CONFLICT DO NOTHING;

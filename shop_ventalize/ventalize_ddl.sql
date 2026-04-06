-- ============================================================
-- 벙딸리제 (Ventalize) - PostgreSQL DDL & 초기 데이터
-- DB: ventalize
-- 실행 순서: 1) DB 생성 -> 2) 이 파일 전체 실행
-- ============================================================

-- ============================================================
-- 1. 기존 테이블 초기화 (재실행 시 충돌 방지)
-- ============================================================
DROP TABLE IF EXISTS order_items  CASCADE;
DROP TABLE IF EXISTS orders       CASCADE;
DROP TABLE IF EXISTS carts        CASCADE;
DROP TABLE IF EXISTS reviews      CASCADE;
DROP TABLE IF EXISTS questions    CASCADE;
DROP TABLE IF EXISTS inquiries    CASCADE;
DROP TABLE IF EXISTS faqs         CASCADE;
DROP TABLE IF EXISTS coupons      CASCADE;
DROP TABLE IF EXISTS items        CASCADE;
DROP TABLE IF EXISTS members      CASCADE;

-- ============================================================
-- 2. 회원 테이블
-- ============================================================
CREATE TABLE members (
    id         SERIAL PRIMARY KEY,
    login_id   VARCHAR(100) NOT NULL UNIQUE,
    login_pw   VARCHAR(255) NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    phone      VARCHAR(20),
    address    VARCHAR(500),
    role       VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',
    grade      VARCHAR(20)  NOT NULL DEFAULT 'SAPPHIRE',
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 3. 상품 테이블 (카테고리: SCARVES, READY_TO_WEAR, PERFUME, ACC, BAG, SALE)
-- ============================================================
CREATE TABLE items (
    id           SERIAL PRIMARY KEY,
    brand        VARCHAR(50)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    category     VARCHAR(30),
    img_path     VARCHAR(255) NOT NULL,
    description  TEXT,
    price        INTEGER      NOT NULL,
    discount_per INTEGER      NOT NULL DEFAULT 0,
    stock_count  INTEGER               DEFAULT 0,
    view_count   INTEGER               DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 4. 장바구니
-- ============================================================
CREATE TABLE carts (
    id        SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    item_id   INTEGER NOT NULL REFERENCES items(id)   ON DELETE CASCADE,
    UNIQUE(member_id, item_id)
);

-- ============================================================
-- 5. 주문 (배송 현황 ENUM: PENDING_PAYMENT / PAID / SHIPPING / DELIVERED / CANCELLED)
-- ============================================================
CREATE TABLE orders (
    id          SERIAL PRIMARY KEY,
    member_id   INTEGER      NOT NULL REFERENCES members(id),
    name        VARCHAR(50)  NOT NULL,
    address     VARCHAR(500) NOT NULL,
    payment     VARCHAR(10)  NOT NULL,
    card_number VARCHAR(16),
    amount      BIGINT       NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING_PAYMENT',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 6. 주문 상품
-- ============================================================
CREATE TABLE order_items (
    id       SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    item_id  INTEGER NOT NULL REFERENCES items(id)
);

-- ============================================================
-- 7. 리뷰 (구매 내역 기반)
-- ============================================================
CREATE TABLE reviews (
    id         SERIAL PRIMARY KEY,
    member_id  INTEGER   NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    item_id    INTEGER   NOT NULL REFERENCES items(id)   ON DELETE CASCADE,
    order_id   INTEGER   REFERENCES orders(id)           ON DELETE SET NULL,
    rating     SMALLINT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

-- ============================================================
-- 8. 상품 Q&A (기존 기능 유지)
-- ============================================================
CREATE TABLE questions (
    id               SERIAL PRIMARY KEY,
    member_id        INTEGER      NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    item_id          INTEGER      NOT NULL REFERENCES items(id)   ON DELETE CASCADE,
    title            VARCHAR(200) NOT NULL,
    content          TEXT         NOT NULL,
    is_secret        BOOLEAN      NOT NULL DEFAULT FALSE,
    is_answered      BOOLEAN      NOT NULL DEFAULT FALSE,
    answer_content   TEXT,
    answer_member_id INTEGER,
    answered_at      TIMESTAMP,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP
);

-- ============================================================
-- 9. FAQ 게시판
-- ============================================================
CREATE TABLE faqs (
    id           SERIAL PRIMARY KEY,
    category     VARCHAR(30)  NOT NULL DEFAULT '기타',
    question     VARCHAR(200) NOT NULL,
    answer       TEXT         NOT NULL,
    is_published BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);

-- ============================================================
-- 10. 1:1 문의 게시판
-- ============================================================
CREATE TABLE inquiries (
    id               SERIAL PRIMARY KEY,
    member_id        INTEGER      NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    category         VARCHAR(30)  NOT NULL DEFAULT '기타',
    title            VARCHAR(200) NOT NULL,
    content          TEXT         NOT NULL,
    answer_content   TEXT,
    answer_member_id INTEGER,
    answered_at      TIMESTAMP,
    is_answered      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP
);

-- ============================================================
-- 11. 테스트 회원 데이터
--     (비밀번호 test1234 → BCrypt: $2a$10$...)
--     실제 실행 시 AdminInitializer가 admin@ventalize.com 자동 생성
-- ============================================================
INSERT INTO members (login_id, login_pw, name, phone, role, grade, status) VALUES
('user1@ventalize.com', '$2a$10$H5G.3cxVfJuT0WBSHJdRAuQb4cHuUoT9I1EK.tHJNpjVCXUyQYc5m', '김민지', '010-1111-2222', 'ROLE_USER', 'RUBY',     'ACTIVE'),
('user2@ventalize.com', '$2a$10$H5G.3cxVfJuT0WBSHJdRAuQb4cHuUoT9I1EK.tHJNpjVCXUyQYc5m', '이서연', '010-3333-4444', 'ROLE_USER', 'EMERALD',  'ACTIVE'),
('user3@ventalize.com', '$2a$10$H5G.3cxVfJuT0WBSHJdRAuQb4cHuUoT9I1EK.tHJNpjVCXUyQYc5m', '박지훈', '010-5555-6666', 'ROLE_USER', 'SAPPHIRE', 'ACTIVE');

-- ============================================================
-- 12. 상품 데이터 (카테고리별 16개 = 2페이지 분량)
-- ============================================================

-- ── SCARVES (스카프) ──────────────────────────────────────
INSERT INTO items (brand, name, category, img_path, description, price, discount_per, stock_count) VALUES
('Ventalize', '실크 플로럴 스카프',          'SCARVES', 'https://images.unsplash.com/photo-1590736704728-f4730bb30770?w=600', '순수 실크 소재의 플로럴 패턴 스카프. 계절을 불문하고 활용 가능한 아이템.', 128000, 10, 30),
('Ventalize', '울 체크 머플러',             'SCARVES', 'https://images.unsplash.com/photo-1520903374185-9a4d5a1d13bf?w=600', '포근한 울 소재의 클래식 체크 머플러. 겨울 필수 아이템.', 89000, 0, 25),
('Ventalize', '쉬폰 스트라이프 스카프',      'SCARVES', 'https://images.unsplash.com/photo-1601924994987-69e26d50dc26?w=600', '가볍고 투명한 쉬폰 소재의 스트라이프 스카프.', 65000, 15, 40),
('Ventalize', '캐시미어 솔리드 머플러',       'SCARVES', 'https://images.unsplash.com/photo-1603251579711-3e6d24d7614a?w=600', '100% 캐시미어 소재. 부드럽고 보온성이 뛰어납니다.', 215000, 0, 15),
('Ventalize', '레오파드 실크 스카프',        'SCARVES', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '트렌디한 레오파드 패턴의 실크 스카프.', 145000, 20, 20),
('Ventalize', '그라데이션 니트 스카프',      'SCARVES', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', '아름다운 그라데이션 컬러의 니트 스카프.', 78000, 0, 35),
('Ventalize', '모달 솔리드 스카프',         'SCARVES', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '환경친화적 모달 소재로 제작된 부드러운 스카프.', 58000, 10, 50),
('Ventalize', '트위드 패턴 숄',            'SCARVES', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600', '클래식한 트위드 패턴의 고급스러운 숄.', 195000, 5, 12),
('Ventalize', '기하학 프린트 스카프',       'SCARVES', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=600', '현대적인 기하학 패턴이 돋보이는 스카프.', 72000, 0, 28),
('Ventalize', '빈티지 페이즐리 스카프',     'SCARVES', 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600', '빈티지 감성의 페이즐리 패턴 스카프.', 95000, 10, 22),
('Ventalize', '메리노 울 롱 머플러',        'SCARVES', 'https://images.unsplash.com/photo-1601924994987-69e26d50dc26?w=600', '메리노 울 소재의 긴 머플러. 다양하게 연출 가능.', 125000, 0, 18),
('Ventalize', '플리스 넥워머',             'SCARVES', 'https://images.unsplash.com/photo-1604671801908-6f0c6a092c05?w=600', '부드러운 플리스 소재의 넥워머. 방한에 최적.', 45000, 20, 60),
('Ventalize', '사계절 린넨 스카프',        'SCARVES', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', '통기성 좋은 린넨 소재의 사계절 스카프.', 68000, 0, 45),
('Ventalize', '럭셔리 실크 트윌 스카프',   'SCARVES', 'https://images.unsplash.com/photo-1590736704728-f4730bb30770?w=600', '90×90cm 정사각형 실크 트윌 스카프. 다양한 연출 가능.', 185000, 15, 10),
('Ventalize', '컬러블록 울 머플러',        'SCARVES', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=600', '두 가지 색상이 조화로운 컬러블록 울 머플러.', 88000, 0, 33),
('Ventalize', '에코 퍼 넥워머',           'SCARVES', 'https://images.unsplash.com/photo-1520903374185-9a4d5a1d13bf?w=600', '동물 친화적 에코 퍼 소재의 럭셔리한 넥워머.', 135000, 10, 15);

-- ── READY TO WEAR (기성복) ───────────────────────────────
INSERT INTO items (brand, name, category, img_path, description, price, discount_per, stock_count) VALUES
('Ventalize', '실크 블라우스 크림',          'READY_TO_WEAR', 'https://images.unsplash.com/photo-1594938298603-c8148c4b4cde?w=600', '부드러운 실크 소재의 우아한 블라우스.', 185000, 0, 20),
('Ventalize', '울 트위드 재킷',             'READY_TO_WEAR', 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600', '클래식한 울 트위드 소재의 구조적인 재킷.', 425000, 10, 15),
('Ventalize', '와이드 팬츠 블랙',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1594938374182-a55a4e4d96c5?w=600', '세련된 와이드 실루엣의 팬츠. 포멀&캐주얼 모두 가능.', 168000, 0, 25),
('Ventalize', '플리츠 미디 스커트',         'READY_TO_WEAR', 'https://images.unsplash.com/photo-1583496661160-fb5218a4e42e?w=600', '우아한 플리츠 디테일의 미디 길이 스커트.', 145000, 15, 30),
('Ventalize', '캐시미어 니트 드레스',        'READY_TO_WEAR', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600', '100% 캐시미어 소재의 럭셔리 니트 드레스.', 385000, 0, 10),
('Ventalize', '리넨 셔츠 화이트',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1602810316693-3667c854239a?w=600', '천연 린넨 소재의 클래식한 흰색 셔츠.', 128000, 0, 35),
('Ventalize', '새틴 슬립 드레스',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1539008835657-9e8e9680c956?w=600', '광택 있는 새틴 소재의 슬립 드레스. 레이어드에 최적.', 225000, 10, 18),
('Ventalize', '오버사이즈 코트 카멜',       'READY_TO_WEAR', 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=600', '트렌디한 오버사이즈 핏의 카멜 컬러 코트.', 598000, 0, 8),
('Ventalize', '크롭 트위드 자켓',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600', '세련된 크롭 기장의 트위드 자켓.', 345000, 20, 12),
('Ventalize', '벨벳 테일러드 팬츠',         'READY_TO_WEAR', 'https://images.unsplash.com/photo-1594938374182-a55a4e4d96c5?w=600', '럭셔리한 벨벳 소재의 테일러드 팬츠.', 235000, 0, 20),
('Ventalize', '쉬폰 맥시 드레스',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600', '가볍고 우아한 쉬폰 소재의 맥시 드레스.', 278000, 5, 15),
('Ventalize', '메리노 울 터틀넥',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1561730916-c49d5e7e4ed5?w=600', '보온성 뛰어난 메리노 울 터틀넥 니트.', 195000, 0, 28),
('Ventalize', '데님 와이드 팬츠',           'READY_TO_WEAR', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=600', '편안하고 세련된 데님 와이드 팬츠.', 148000, 10, 40),
('Ventalize', '울 하운즈투스 스커트',        'READY_TO_WEAR', 'https://images.unsplash.com/photo-1583496661160-fb5218a4e42e?w=600', '클래식한 하운즈투스 패턴의 울 스커트.', 178000, 0, 22),
('Ventalize', '코튼 포플린 드레스',          'READY_TO_WEAR', 'https://images.unsplash.com/photo-1539008835657-9e8e9680c956?w=600', '가볍고 시원한 코튼 포플린 소재의 드레스.', 165000, 15, 32),
('Ventalize', '실크 사틴 블라우스 네이비',   'READY_TO_WEAR', 'https://images.unsplash.com/photo-1594938298603-c8148c4b4cde?w=600', '우아한 네이비 컬러의 실크 사틴 블라우스.', 198000, 0, 17);

-- ── PERFUME (향수) ───────────────────────────────────────
INSERT INTO items (brand, name, category, img_path, description, price, discount_per, stock_count) VALUES
('Ventalize', '로즈 앤 우드 오드퍼퓸 50ml',  'PERFUME', 'https://images.unsplash.com/photo-1541643600914-78b084683702?w=600', '장미와 우디 노트가 조화로운 시그니처 향수. 지속력 12시간+', 198000, 0, 25),
('Ventalize', '블랑쉬 오드뚜왈렛 100ml',     'PERFUME', 'https://images.unsplash.com/photo-1512411600720-d26919c6a9da?w=600', '화이트 플로럴과 머스크의 청결한 향. 데일리 퍼퓸.', 145000, 10, 30),
('Ventalize', '앰버 오리엔탈 오드퍼퓸 75ml', 'PERFUME', 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600', '따뜻하고 관능적인 앰버 오리엔탈 계열 향수.', 225000, 0, 18),
('Ventalize', '씨트러스 아쿠아 오드뚜왈렛',  'PERFUME', 'https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=600', '상쾌한 시트러스와 아쿠아 노트. 여름 필수 향수.', 128000, 15, 35),
('Ventalize', '벌가못 앤 베티버 EDP 50ml',  'PERFUME', 'https://images.unsplash.com/photo-1541643600914-78b084683702?w=600', '베르가못의 밝음과 베티버의 깊이가 만나는 향수.', 178000, 0, 22),
('Ventalize', '자스민 누아 오드퍼퓸 30ml',   'PERFUME', 'https://images.unsplash.com/photo-1512411600720-d26919c6a9da?w=600', '자스민과 다크 스파이시 노트의 미스터리한 향.', 165000, 5, 20),
('Ventalize', '프리지아 & 피오니 EDT',       'PERFUME', 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600', '프리지아와 피오니의 봄날 같은 플로럴 향수.', 118000, 0, 40),
('Ventalize', '오우드 앤 로즈 퍼퓸 오일',    'PERFUME', 'https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=600', '순수 퍼퓸 오일. 장시간 지속되는 오우드 앤 로즈.', 285000, 0, 12),
('Ventalize', '그린 티 블렌드 오드뚜왈렛',   'PERFUME', 'https://images.unsplash.com/photo-1541643600914-78b084683702?w=600', '신선한 그린 티와 허브 노트의 청량한 향수.', 95000, 20, 45),
('Ventalize', '무스크 앤 세더우드 EDP',      'PERFUME', 'https://images.unsplash.com/photo-1512411600720-d26919c6a9da?w=600', '부드러운 무스크와 시더우드의 따뜻한 향.', 148000, 0, 28),
('Ventalize', '튜베로즈 솔레일 오드퍼퓸',    'PERFUME', 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600', '강렬하고 관능적인 튜베로즈 향수.', 215000, 10, 15),
('Ventalize', '라벤더 필드 EDT 100ml',       'PERFUME', 'https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=600', '프로방스 라벤더 필드를 담은 편안한 향수.', 125000, 0, 38),
('Ventalize', '체리 블러썸 오드뚜왈렛',      'PERFUME', 'https://images.unsplash.com/photo-1541643600914-78b084683702?w=600', '벚꽃의 섬세하고 로맨틱한 향을 담은 향수.', 138000, 15, 30),
('Ventalize', '바닐라 빈 퍼퓸 크림',        'PERFUME', 'https://images.unsplash.com/photo-1512411600720-d26919c6a9da?w=600', '달콤한 바닐라 빈 향의 퍼퓸 크림. 피부에 촉촉하게.', 88000, 0, 50),
('Ventalize', '시더 앤 바이올렛 EDP',       'PERFUME', 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600', '시더우드와 바이올렛의 우아한 만남.', 178000, 5, 20),
('Ventalize', '오션 브리즈 EDT 75ml',       'PERFUME', 'https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=600', '바다 바람처럼 상쾌하고 자유로운 향수.', 112000, 0, 42);

-- ── ACC (액세서리) ───────────────────────────────────────
INSERT INTO items (brand, name, category, img_path, description, price, discount_per, stock_count) VALUES
('Ventalize', '골드 플레이티드 체인 목걸이',  'ACC', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600', '18K 골드 플레이팅 레이어드 체인 목걸이.', 125000, 0, 30),
('Ventalize', '실버 링 세트 3종',            'ACC', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=600', '925 스털링 실버 링 3종 세트. 스택킹 가능.', 95000, 10, 40),
('Ventalize', '진주 드롭 이어링',            'ACC', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600', '천연 담수 진주 드롭 이어링. 우아한 디자인.', 148000, 0, 25),
('Ventalize', '레더 레이디 벨트',            'ACC', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', '이탈리안 가죽 소재의 클래식 버클 벨트.', 88000, 15, 35),
('Ventalize', '실크 헤어 스카프 밴드',        'ACC', 'https://images.unsplash.com/photo-1556306535-0f09a537f0a3?w=600', '실크 소재 헤어 밴드. 머리에도 목에도 활용 가능.', 58000, 0, 50),
('Ventalize', '크리스탈 스터드 이어링',       'ACC', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600', '영롱한 크리스탈 스터드 이어링. 매일 착용 가능.', 68000, 0, 45),
('Ventalize', '스카프 링 골드',              'ACC', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600', '스카프를 다양하게 연출할 수 있는 골드 스카프 링.', 35000, 0, 60),
('Ventalize', '와이드 레더 팔찌',            'ACC', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=600', '소프트 레더 소재의 와이드 팔찌.', 78000, 10, 28),
('Ventalize', '진주 레이어드 목걸이',         'ACC', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600', '천연 담수 진주 멀티 레이어드 목걸이.', 185000, 0, 18),
('Ventalize', '골드 헤어핀 세트',            'ACC', 'https://images.unsplash.com/photo-1556306535-0f09a537f0a3?w=600', '심플하고 세련된 골드 헤어핀 3종 세트.', 42000, 0, 55),
('Ventalize', '토트백 스카프 참',            'ACC', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', '가방에 묶어 포인트를 주는 미니 스카프 참.', 48000, 15, 40),
('Ventalize', '클로버 골드 브로치',          'ACC', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=600', '행운의 클로버 모티프 골드 브로치.', 112000, 0, 22),
('Ventalize', '선글라스 클래식 스퀘어',       'ACC', 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600', 'UV400 차단 클래식 스퀘어 선글라스.', 135000, 10, 30),
('Ventalize', '실버 앵클릿',               'ACC', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600', '925 스털링 실버 소재의 섬세한 앵클릿.', 75000, 0, 35),
('Ventalize', '모자 울 베레모',             'ACC', 'https://images.unsplash.com/photo-1556306535-0f09a537f0a3?w=600', '부드러운 울 소재의 클래식 베레모.', 88000, 0, 28),
('Ventalize', '새틴 글러브',               'ACC', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600', '우아한 새틴 소재의 엘보 길이 글러브.', 65000, 20, 20);

-- ── BAG (가방) ───────────────────────────────────────────
INSERT INTO items (brand, name, category, img_path, description, price, discount_per, stock_count) VALUES
('Ventalize', '레더 클래식 토트백',           'BAG', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600', '이탈리안 풀그레인 레더 소재의 클래식 토트백.', 585000, 0, 15),
('Ventalize', '스웨이드 미니 크로스백',        'BAG', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '부드러운 스웨이드 소재의 미니 크로스백.', 298000, 10, 20),
('Ventalize', '퀼팅 체인 숄더백',            'BAG', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?w=600', '골드 체인 스트랩의 클래식 퀼팅 숄더백.', 425000, 0, 12),
('Ventalize', '캔버스 토트백 라지',           'BAG', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', '내구성 좋은 캔버스 소재의 대용량 토트백.', 158000, 15, 35),
('Ventalize', '레더 클러치 골드',            'BAG', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600', '골드 하드웨어의 우아한 레더 클러치.', 245000, 0, 18),
('Ventalize', '버킷 백 소프트 레더',          'BAG', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '부드러운 나파 레더의 트렌디한 버킷 백.', 365000, 5, 10),
('Ventalize', '메쉬 미니 백팩',              'BAG', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?w=600', '경량 메쉬 소재의 스타일리시 미니 백팩.', 228000, 0, 25),
('Ventalize', '새들 백 갈색',               'BAG', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', '빈티지 감성의 클래식 새들 백.', 318000, 10, 15),
('Ventalize', '크로코 엠보 미니 백',          'BAG', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600', '악어가죽 패턴 엠보 소재의 미니 백.', 278000, 0, 20),
('Ventalize', '우븐 래피아 토트',            'BAG', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '여름 분위기 물씬 나는 래피아 우븐 토트백.', 175000, 0, 28),
('Ventalize', '탑핸들 박스 백',              'BAG', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?w=600', '구조적인 박스 형태의 탑핸들 백.', 395000, 15, 8),
('Ventalize', '레더 파우치 멀티',            'BAG', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', '수납력 좋은 레더 소재의 멀티 파우치.', 125000, 0, 40),
('Ventalize', '스트로 버킷 핸드백',           'BAG', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600', '천연 소재 스트로 버킷 핸드백. 여름 필수 아이템.', 185000, 10, 22),
('Ventalize', '미니 플랩 백 블랙',           'BAG', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '콤팩트한 사이즈의 플랩 클로저 미니 백.', 258000, 0, 18),
('Ventalize', '벨팅 숄더백 클래식',          'BAG', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?w=600', '클래식한 벨팅 디테일의 레더 숄더백.', 445000, 0, 10),
('Ventalize', '나일론 크로스백 나이트',       'BAG', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600', '가볍고 실용적인 나일론 크로스백.', 148000, 20, 30);

-- ── SALE (세일) ─────────────────────────────────────────
INSERT INTO items (brand, name, category, img_path, description, price, discount_per, stock_count) VALUES
('Ventalize', '[SALE] 실크 스카프 클래식',         'SALE', 'https://images.unsplash.com/photo-1590736704728-f4730bb30770?w=600', '시즌 오프 특가! 정통 실크 스카프.', 128000, 40, 20),
('Ventalize', '[SALE] 울 머플러 와인',             'SALE', 'https://images.unsplash.com/photo-1520903374185-9a4d5a1d13bf?w=600', '시즌 오프 특가! 와인 컬러 울 머플러.', 89000, 35, 15),
('Ventalize', '[SALE] 실크 블라우스 핑크',         'SALE', 'https://images.unsplash.com/photo-1594938298603-c8148c4b4cde?w=600', '시즌 오프 특가! 핑크 실크 블라우스.', 185000, 45, 10),
('Ventalize', '[SALE] 오드뚜왈렛 100ml',           'SALE', 'https://images.unsplash.com/photo-1512411600720-d26919c6a9da?w=600', '시즌 오프 특가! 지난 시즌 인기 향수.', 145000, 30, 25),
('Ventalize', '[SALE] 레더 미니 크로스백',         'SALE', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '시즌 오프 특가! 레더 미니 크로스백.', 298000, 40, 8),
('Ventalize', '[SALE] 골드 체인 목걸이',           'SALE', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600', '시즌 오프 특가! 골드 체인 목걸이.', 125000, 50, 30),
('Ventalize', '[SALE] 플리츠 스커트 그린',         'SALE', 'https://images.unsplash.com/photo-1583496661160-fb5218a4e42e?w=600', '시즌 오프 특가! 그린 컬러 플리츠 스커트.', 145000, 35, 18),
('Ventalize', '[SALE] 앰버 오리엔탈 EDP 30ml',    'SALE', 'https://images.unsplash.com/photo-1541643600914-78b084683702?w=600', '시즌 오프 특가! 인기 향수 소용량.', 225000, 40, 20),
('Ventalize', '[SALE] 퀼팅 숄더백 베이지',         'SALE', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?w=600', '시즌 오프 특가! 베이지 퀼팅 숄더백.', 425000, 30, 5),
('Ventalize', '[SALE] 실버 진주 이어링',           'SALE', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600', '시즌 오프 특가! 담수 진주 이어링.', 148000, 45, 22),
('Ventalize', '[SALE] 린넨 와이드 팬츠',           'SALE', 'https://images.unsplash.com/photo-1594938374182-a55a4e4d96c5?w=600', '시즌 오프 특가! 린넨 소재 와이드 팬츠.', 168000, 40, 15),
('Ventalize', '[SALE] 로즈 퍼퓸 크림',            'SALE', 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600', '시즌 오프 특가! 장미향 퍼퓸 크림.', 88000, 30, 35),
('Ventalize', '[SALE] 레더 토트백 버건디',         'SALE', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600', '시즌 오프 특가! 버건디 레더 토트백.', 585000, 35, 6),
('Ventalize', '[SALE] 크리스탈 스터드 이어링',     'SALE', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600', '시즌 오프 특가! 크리스탈 스터드.', 68000, 40, 40),
('Ventalize', '[SALE] 캐시미어 터틀넥 그레이',     'SALE', 'https://images.unsplash.com/photo-1561730916-c49d5e7e4ed5?w=600', '시즌 오프 특가! 그레이 캐시미어 터틀넥.', 385000, 50, 10),
('Ventalize', '[SALE] 스웨이드 미니 버킷백',       'SALE', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600', '시즌 오프 특가! 스웨이드 버킷백.', 365000, 45, 8);

-- ============================================================
-- 13. 쿠폰 테이블
-- ============================================================
CREATE TABLE coupons (
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    code             VARCHAR(50)  NOT NULL UNIQUE,
    discount_type    VARCHAR(10)  NOT NULL,   -- FIXED | PERCENT
    discount_value   INTEGER      NOT NULL,
    target_grade     VARCHAR(20),             -- NULL = 전체
    min_order_amount BIGINT       NOT NULL DEFAULT 0,
    valid_from       DATE         NOT NULL,
    valid_to         DATE         NOT NULL,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 샘플 쿠폰 데이터
INSERT INTO coupons (name, code, discount_type, discount_value, target_grade, min_order_amount, valid_from, valid_to) VALUES
('신규 회원 웰컴 쿠폰',   'WELCOME10',    'PERCENT', 10, NULL,       0,       CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year'),
('사파이어 등급 할인',    'SAPPHIRE5K',   'FIXED',   5000, 'SAPPHIRE', 50000,  CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months'),
('루비 등급 10% 할인',   'RUBY10PCT',    'PERCENT', 10, 'RUBY',     80000,  CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months'),
('에메랄드 VIP 쿠폰',    'EMERALD15K',   'FIXED',   15000, 'EMERALD', 150000, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months'),
('골드 전용 20% 할인',   'GOLD20PCT',    'PERCENT', 20, 'GOLD',     200000, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months'),
('다이아몬드 VIP 특별',  'DIAMOND30PCT', 'PERCENT', 30, 'DIAMOND',  300000, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months');

-- ============================================================
-- 14. FAQ 샘플 데이터
-- ============================================================
INSERT INTO faqs (category, question, answer) VALUES
('주문/배송', '주문 후 배송까지 얼마나 걸리나요?', '결제 완료 후 영업일 기준 1~3일 내에 발송됩니다. 배송 기간은 지역에 따라 1~3일 추가 소요될 수 있습니다.'),
('주문/배송', '해외 배송이 가능한가요?', '현재 국내 배송만 지원합니다. 해외 배송 서비스는 추후 오픈 예정입니다.'),
('주문/배송', '배송비는 얼마인가요?', '5만원 이상 구매 시 무료배송, 미만 시 3,000원의 배송비가 부과됩니다.'),
('환불/교환', '교환 및 환불은 어떻게 신청하나요?', '수령 후 7일 이내에 마이페이지 > 주문내역에서 신청하거나 1:1 문의를 통해 접수하실 수 있습니다.'),
('환불/교환', '향수 개봉 후 환불이 가능한가요?', '향수는 위생상의 이유로 개봉 후 환불이 불가합니다. 단, 제품 불량의 경우 교환 가능합니다.'),
('환불/교환', '스카프나 의류의 경우 착용 후 반품이 가능한가요?', '착용 흔적이 없는 경우에 한해 수령 후 7일 이내 반품 가능합니다.'),
('회원', '회원 등급은 어떻게 분류되나요?', 'SAPPHIRE → RUBY → EMERALD → GOLD → DIAMOND 순으로 최근 6개월 구매 금액에 따라 자동 산정됩니다.'),
('회원', '비밀번호를 잊어버렸어요.', '로그인 페이지의 "비밀번호 찾기"를 이용하시거나, 1:1 문의로 연락주시면 도움드립니다.'),
('상품', '향수 샘플을 받아볼 수 있나요?', '일부 향수의 경우 2ml 샘플 구매가 가능합니다. 상품 상세 페이지에서 확인하실 수 있습니다.'),
('상품', '실크 스카프 세탁 방법이 궁금합니다.', '실크 제품은 손세탁(30°C 이하) 또는 드라이클리닝을 권장합니다. 세탁기 사용 시 손상될 수 있습니다.'),
('기타', '기업 구매 및 선물 포장이 가능한가요?', '네, 가능합니다. 기업 구매는 별도 문의 주시면 맞춤 견적을 제공해드립니다. 선물 포장은 주문 시 옵션 선택 가능합니다.'),
('기타', '재입고 알림을 받을 수 있나요?', '현재 재입고 알림 서비스는 준비 중입니다. 1:1 문의를 남겨주시면 재입고 시 연락드리겠습니다.');

-- ============================================================
-- 완료 메시지
-- ============================================================
SELECT '✅ Ventalize DB 초기화 완료!' AS message;

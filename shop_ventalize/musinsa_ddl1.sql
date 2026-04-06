-- ==========================================
-- MUSINSA SHOPPING MALL DDL & SEED DATA
-- ==========================================

-- 상품 테이블
CREATE TABLE IF NOT EXISTS items (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(30),
    img_path VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    discount_per INTEGER DEFAULT 0,
    stock_count INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE items IS '무신사 판매 상품 정보';
COMMENT ON COLUMN items.brand IS '브랜드명';
COMMENT ON COLUMN items.discount_per IS '할인율(0~100)';

-- 기존 상품 데이터 초기화
TRUNCATE TABLE order_items RESTART IDENTITY CASCADE;
TRUNCATE TABLE carts RESTART IDENTITY CASCADE;
TRUNCATE TABLE orders RESTART IDENTITY CASCADE;
TRUNCATE TABLE items RESTART IDENTITY CASCADE;

INSERT INTO items (brand, name, category, img_path, price, discount_per, stock_count, view_count) VALUES

-- ============================================================
-- OUTER (아우터) - 16개 / 8개씩 2페이지
-- ============================================================
('노스페이스',     '눕시 하이브리드 다운 자켓 블랙',           'OUTER', 'https://picsum.photos/seed/o001/300/400', 359000,  5,  45, 38200),
('K2',            '알파인 고어텍스 등산 자켓 네이비',          'OUTER', 'https://picsum.photos/seed/o002/300/400', 289000, 15,  40, 26700),
('네파',          '쉘 플리스 집업 자켓 다크그린',             'OUTER', 'https://picsum.photos/seed/o003/300/400', 159000, 20,  80, 19400),
('디스커버리',    '익스페디션 다운 야상 자켓 카키',            'OUTER', 'https://picsum.photos/seed/o004/300/400', 229000,  0,  30, 31000),
('무신사 스탠다드','오버핏 코튼 개버딘 코트 아이보리',         'OUTER', 'https://picsum.photos/seed/o005/300/400', 129000, 30, 100, 44500),
('나이키',        '나이키 테크 플리스 자켓 다크그레이',        'OUTER', 'https://picsum.photos/seed/o006/300/400', 169000,  0,  45, 37200),
('MLB',           'MLB 빅볼 척 숏 패딩 자켓 크림',            'OUTER', 'https://picsum.photos/seed/o007/300/400', 249000, 20,  35, 28900),
('커버낫',        '울 혼방 오버핏 롱 코트 베이지',            'OUTER', 'https://picsum.photos/seed/o008/300/400', 219000, 10,  28, 33100),
('마운틴하드웨어', '스트레치 다운 후드 자켓 블루',            'OUTER', 'https://picsum.photos/seed/o009/300/400', 399000,  0,  20, 17800),
('뉴발란스',      'NB 에센셜 웜업 패딩 자켓 블랙',            'OUTER', 'https://picsum.photos/seed/o010/300/400', 199000, 15,  55, 22400),
('아디다스',      '아디다스 3-스트라입 빈티지 트랙 자켓',     'OUTER', 'https://picsum.photos/seed/o011/300/400', 129000, 10,  70, 25600),
('오아이오아이',   '쉐르파 라이너 플리스 자켓 브라운',        'OUTER', 'https://picsum.photos/seed/o012/300/400', 149000, 25,  65, 16200),
('챔피온',        '리버스 위브 크루 후드 자켓 그레이 멜란지', 'OUTER', 'https://picsum.photos/seed/o013/300/400', 119000, 15,  90, 21300),
('아더에러',      '오버사이즈 스타디움 자켓 멀티 컬러',       'OUTER', 'https://picsum.photos/seed/o014/300/400', 289000,  0,  15, 41700),
('에스피오나지',  '군용 필드 자켓 올리브',                    'OUTER', 'https://picsum.photos/seed/o015/300/400',  99000, 20,  60, 19800),
('컬럼비아',      '오므니테크 방수 하이킹 자켓 레드',         'OUTER', 'https://picsum.photos/seed/o016/300/400', 189000,  5,  38, 14500),

-- ============================================================
-- TOP (상의) - 16개 / 8개씩 2페이지
-- ============================================================
('무신사 스탠다드','릴렉스 핏 크루 넥 반팔 티셔츠 화이트',   'TOP', 'https://picsum.photos/seed/t001/300/400',  19900, 10, 500, 52300),
('커버낫',        '스몰 보스 로고 반팔 티셔츠 블랙',          'TOP', 'https://picsum.photos/seed/t002/300/400',  35000,  0, 200, 38100),
('챔피온',        '리버스 위브 후드 스웨트셔츠 그레이 멜란지','TOP', 'https://picsum.photos/seed/t003/300/400',  89000, 20, 150, 29400),
('폴로 랄프로렌', '클래식 핏 폴로 셔츠 네이비',               'TOP', 'https://picsum.photos/seed/t004/300/400', 129000,  0,  80, 43600),
('예일',          '아치 로고 크루넥 스웨트셔츠 블루',         'TOP', 'https://picsum.photos/seed/t005/300/400',  59000, 30, 180, 31200),
('라코스테',      '레귤러 핏 피케 반팔 폴로 그린',            'TOP', 'https://picsum.photos/seed/t006/300/400', 109000,  5,  90, 22700),
('아더에러',      '오버사이즈 그래픽 무지 후드 블랙',         'TOP', 'https://picsum.photos/seed/t007/300/400', 179000,  0,  40, 47200),
('나이키',        '나이키 스우시 반팔 티셔츠 블랙',            'TOP', 'https://picsum.photos/seed/t008/300/400',  45000, 10, 300, 56100),
('아디다스',      '에센셜 3-스트라입 후드티 그레이',          'TOP', 'https://picsum.photos/seed/t009/300/400',  75000, 15, 200, 34900),
('무신사 스탠다드','헤비 코튼 루즈핏 긴팔 티셔츠 베이지',    'TOP', 'https://picsum.photos/seed/t010/300/400',  29000,  0, 400, 42800),
('에스피오나지',  '드롭숄더 롱슬리브 티셔츠 오프화이트',      'TOP', 'https://picsum.photos/seed/t011/300/400',  49000, 15, 120, 26500),
('칸코',          '빈티지 워싱 스트라이프 옥스포드 셔츠',     'TOP', 'https://picsum.photos/seed/t012/300/400',  65000, 20, 100, 20100),
('노아',          'NYC 오리지널 로고 크루넥 스웨트셔츠',      'TOP', 'https://picsum.photos/seed/t013/300/400', 139000,  0,  25, 35400),
('디스커버리',    '라이트 드라이 쿨링 반팔 티셔츠 화이트',   'TOP', 'https://picsum.photos/seed/t014/300/400',  39000,  0, 150, 18600),
('뉴발란스',      'NB 아치로고 반팔 티셔츠 블랙',             'TOP', 'https://picsum.photos/seed/t015/300/400',  39000, 10, 180, 23900),
('칼하트',        'WIP 체이스 긴팔 티셔츠 화이트',            'TOP', 'https://picsum.photos/seed/t016/300/400',  79000,  0,  60, 29700),

-- ============================================================
-- PANTS (하의) - 16개 / 8개씩 2페이지
-- ============================================================
('리바이즈',      '501 오리지널 셀비지 데님 인디고',           'PANTS', 'https://picsum.photos/seed/p001/300/400', 129000, 15, 120, 47300),
('무신사 스탠다드','워크 릴렉스 카고 팬츠 베이지',            'PANTS', 'https://picsum.photos/seed/p002/300/400',  59000, 20, 200, 35600),
('칼하트',        'WIP 더블 니 팬츠 블랙',                   'PANTS', 'https://picsum.photos/seed/p003/300/400', 149000,  0,  80, 26800),
('게스',          '빈티지 슬림 스트레이트 데님 라이트블루',   'PANTS', 'https://picsum.photos/seed/p004/300/400', 119000, 10,  90, 21400),
('커버낫',        '코튼 트위드 와이드 팬츠 브라운',           'PANTS', 'https://picsum.photos/seed/p005/300/400',  89000,  0, 100, 29200),
('아더에러',      '와이드 코듀로이 팬츠 올리브그린',          'PANTS', 'https://picsum.photos/seed/p006/300/400', 159000, 10,  50, 38700),
('폴로 랄프로렌', '슬림 핏 치노 팬츠 카키',                  'PANTS', 'https://picsum.photos/seed/p007/300/400', 139000,  0,  70, 27500),
('나이키',        '나이키 테크 플리스 조거 팬츠 그레이',      'PANTS', 'https://picsum.photos/seed/p008/300/400',  99000,  0, 150, 51200),
('아디다스',      '파이어버드 트랙 팬츠 블랙',               'PANTS', 'https://picsum.photos/seed/p009/300/400',  79000, 20, 130, 32400),
('브레인데드',    '멀티 포켓 밀리터리 카고 팬츠 올리브',     'PANTS', 'https://picsum.photos/seed/p010/300/400', 199000,  0,  30, 19600),
('무신사 스탠다드','와이드 핏 린넨 블렌드 팬츠 아이보리',    'PANTS', 'https://picsum.photos/seed/p011/300/400',  49000, 25, 180, 41300),
('룩아스',        '드레이프 슬랙스 미디엄 그레이',            'PANTS', 'https://picsum.photos/seed/p012/300/400',  69000,  0, 110, 25800),
('리바이즈',      '550 배기 릴렉스 테이퍼드 데님 스톤',      'PANTS', 'https://picsum.photos/seed/p013/300/400', 149000,  5,  65, 33900),
('컨버스',        '척 테일러 리핏 스웨트 조거 팬츠 블랙',    'PANTS', 'https://picsum.photos/seed/p014/300/400',  79000, 15, 100, 17200),
('챔피온',        '리버스 위브 스웨트 팬츠 네이비',           'PANTS', 'https://picsum.photos/seed/p015/300/400',  79000, 10, 120, 22100),
('칸코',          '코튼 리넨 버뮤다 반바지 베이지',           'PANTS', 'https://picsum.photos/seed/p016/300/400',  55000,  0,  90, 15400),

-- ============================================================
-- SHOES (신발) - 16개 / 8개씩 2페이지
-- ============================================================
('나이키',        '에어 포스 1 07 화이트',                    'SHOES', 'https://picsum.photos/seed/s001/300/400', 139000,  0, 100, 74200),
('아디다스',      '삼바 OG 화이트/블랙',                     'SHOES', 'https://picsum.photos/seed/s002/300/400', 139000,  0,  50, 68500),
('뉴발란스',      '990v6 메이드인USA 그레이',                 'SHOES', 'https://picsum.photos/seed/s003/300/400', 259000,  0,  30, 52300),
('반스',          '올드스쿨 클래식 블랙/화이트',             'SHOES', 'https://picsum.photos/seed/s004/300/400',  99000, 10,  80, 39400),
('컨버스',        '척 테일러 올스타 하이컷 블랙',             'SHOES', 'https://picsum.photos/seed/s005/300/400',  89000,  0,  90, 33600),
('아식스',        '젤-카야노 14 크림/실버',                  'SHOES', 'https://picsum.photos/seed/s006/300/400', 169000,  5,  40, 46800),
('살로몬',        'XT-6 GTX 블랙/올리브',                   'SHOES', 'https://picsum.photos/seed/s007/300/400', 229000,  0,  25, 31200),
('호카',          '클리프톤 9 화이트/블루',                  'SHOES', 'https://picsum.photos/seed/s008/300/400', 189000, 10,  35, 38900),
('온러닝',        '클라우드 5 올화이트',                     'SHOES', 'https://picsum.photos/seed/s009/300/400', 209000,  0,  20, 27600),
('리복',          '클럽 C 85 빈티지 화이트/그린',            'SHOES', 'https://picsum.photos/seed/s010/300/400', 119000, 15,  60, 24300),
('나이키',        '덩크 로우 레트로 판다 블랙/화이트',       'SHOES', 'https://picsum.photos/seed/s011/300/400', 139000,  0,  45, 61700),
('팀버랜드',      '6인치 프리미엄 방수 부츠 위트',           'SHOES', 'https://picsum.photos/seed/s012/300/400', 269000, 10,  55, 22800),
('아디다스',      '가젤 빈티지 블루/화이트',                 'SHOES', 'https://picsum.photos/seed/s013/300/400', 129000,  0,  70, 43100),
('뉴발란스',      '574 클래식 그레이/블랙',                  'SHOES', 'https://picsum.photos/seed/s014/300/400', 109000, 10,  85, 35600),
('나이키',        '에어맥스 90 트리플화이트',                'SHOES', 'https://picsum.photos/seed/s015/300/400', 179000,  0,  40, 49800),
('닥터마틴',      '1460 8홀 레더 부츠 블랙 스무드',         'SHOES', 'https://picsum.photos/seed/s016/300/400', 279000,  5,  30, 28400),

-- ============================================================
-- BAG (가방) - 16개 / 8개씩 2페이지
-- ============================================================
('무신사 스탠다드','유틸리티 미니 크로스백 블랙',             'BAG', 'https://picsum.photos/seed/b001/300/400',  39000, 10, 150, 23400),
('나이키',        'NK 헤리티지 2.0 백팩 올리브',             'BAG', 'https://picsum.photos/seed/b002/300/400',  69000,  0,  80, 27800),
('MLB',           'MLB 모노그램 쇼퍼백 베이지',              'BAG', 'https://picsum.photos/seed/b003/300/400',  89000, 15,  60, 36400),
('아디다스',      '에센셜 린 백팩 3-스트라입 블랙',          'BAG', 'https://picsum.photos/seed/b004/300/400',  79000, 20,  70, 19600),
('살로몬',        'XT 10L 어드밴스드 스킨 러닝 베스트',      'BAG', 'https://picsum.photos/seed/b005/300/400', 159000,  0,  25, 17300),
('커버낫',        '레더 미니 토트백 크로스 화이트',           'BAG', 'https://picsum.photos/seed/b006/300/400',  69000, 10,  40, 22500),
('피스마이너스원', 'PMO 리밋 미니 메신저백 블랙',            'BAG', 'https://picsum.photos/seed/b007/300/400', 189000,  0,  20, 42100),
('디스커버리',    '어드밴스 멀티 포켓 힙색 블랙',            'BAG', 'https://picsum.photos/seed/b008/300/400',  49000,  0, 100, 24700),
('칼하트',        'WIP 델트로이트 캐리어 브라운',            'BAG', 'https://picsum.photos/seed/b009/300/400', 129000,  0,  35, 20100),
('뉴발란스',      'NB 에센셜 웨이스트 힙팩 그레이',          'BAG', 'https://picsum.photos/seed/b010/300/400',  49000, 10,  90, 26300),
('아더에러',      '오버사이즈 쇼퍼 토트백 올리브그린',       'BAG', 'https://picsum.photos/seed/b011/300/400', 159000,  0,  15, 31600),
('무신사 스탠다드','헤비 캔버스 빅 토트백 내추럴',           'BAG', 'https://picsum.photos/seed/b012/300/400',  29000,  0, 200, 38900),
('코르소코모',    '레더 미니 버킷백 블랙',                   'BAG', 'https://picsum.photos/seed/b013/300/400', 249000,  0,  12, 15400),
('MLB',           'MLB 모노그램 미니 백팩 블랙',             'BAG', 'https://picsum.photos/seed/b014/300/400',  99000, 10,  55, 29800),
('챔피온',        '리버스 위브 드로스트링 백팩',             'BAG', 'https://picsum.photos/seed/b015/300/400',  49000, 20,  80, 17600),
('나이키',        '나이키 카고 크로스바디 백 블랙',           'BAG', 'https://picsum.photos/seed/b016/300/400',  59000,  0,  60, 21300),

-- ============================================================
-- ACC (악세사리) - 16개 / 8개씩 2페이지
-- ============================================================
('MLB',           'MLB 뉴욕 양키스 볼캡 블랙',               'ACC', 'https://picsum.photos/seed/a001/300/400',  49000,  0, 200, 56200),
('뉴에라',        '59FIFTY LA 다저스 볼캡 블루',             'ACC', 'https://picsum.photos/seed/a002/300/400',  55000,  0, 150, 47300),
('커버낫',        '리사이클 코튼 버킷햇 베이지',             'ACC', 'https://picsum.photos/seed/a003/300/400',  35000, 15, 120, 28900),
('나이키',        '나이키 드라이핏 스우시 비니 블랙',        'ACC', 'https://picsum.photos/seed/a004/300/400',  35000,  0, 180, 23600),
('무신사 스탠다드','울 크루 삭스 3팩 화이트/그레이',        'ACC', 'https://picsum.photos/seed/a005/300/400',  19900,  0, 500, 63400),
('폴로 랄프로렌', '클래식 리버시블 레더 벨트 블랙',         'ACC', 'https://picsum.photos/seed/a006/300/400',  89000,  0,  60, 19700),
('칼하트',        'WIP 스크립트 와치 비니 그레이',           'ACC', 'https://picsum.photos/seed/a007/300/400',  59000,  0,  70, 32800),
('반스',          '반스 와이드 로드 스케이트 선글라스',       'ACC', 'https://picsum.photos/seed/a008/300/400',  49000, 20,  80, 18200),
('커버낫',        '리사이클 캔버스 미니 키링 그린',           'ACC', 'https://picsum.photos/seed/a009/300/400',  19900,  0, 300, 41600),
('MLB',           'MLB 모노그램 울 크로스 스카프 베이지',    'ACC', 'https://picsum.photos/seed/a010/300/400',  59000, 10, 100, 26400),
('무신사 스탠다드','헤비 코튼 발목 양말 5팩 블랙',           'ACC', 'https://picsum.photos/seed/a011/300/400',  24900,  0, 400, 51800),
('아디다스',      '아디다스 트레이닝 클래식 선바이저 화이트','ACC', 'https://picsum.photos/seed/a012/300/400',  35000,  0,  90, 21300),
('챔피온',        '챔피온 에이펙스 스크립트 비니 네이비',    'ACC', 'https://picsum.photos/seed/a013/300/400',  39000, 10, 130, 24700),
('뉴발란스',      'NB 에센셜 테리 양말 3팩 화이트',          'ACC', 'https://picsum.photos/seed/a014/300/400',  19900,  0, 350, 35100),
('노스페이스',    'TNF 로고 플리스 비니 블랙',               'ACC', 'https://picsum.photos/seed/a015/300/400',  39000,  5,  80, 22800),
('MLB',           'MLB 에센셜 크로스백 미니 파우치',          'ACC', 'https://picsum.photos/seed/a016/300/400',  45000,  0, 110, 29500),

-- ============================================================
-- OUTLET (할인 품목) - 16개 / 8개씩 2페이지
-- ============================================================
('노스페이스',    '에코 라이트 플리스 자켓 블루 - 시즌오프', 'OUTLET', 'https://picsum.photos/seed/x001/300/400', 199000, 45,  30, 28600),
('나이키',        '나이키 에어맥스 90 화이트 - 재고 세일',  'OUTLET', 'https://picsum.photos/seed/x002/300/400', 179000, 40,  20, 23700),
('아디다스',      '울트라부스트 22 코어블랙 - 이월 상품',   'OUTLET', 'https://picsum.photos/seed/x003/300/400', 229000, 50,  15, 19400),
('무신사 스탠다드','코듀로이 셔츠 자켓 카멜 - 시즌오프',   'OUTLET', 'https://picsum.photos/seed/x004/300/400',  79000, 50,  40, 36200),
('리바이즈',      '550 배기 데님 스톤워시 - 이월',         'OUTLET', 'https://picsum.photos/seed/x005/300/400', 149000, 35,  25, 18100),
('MLB',           'MLB 레더 미니 숄더백 블랙 - 재고 세일', 'OUTLET', 'https://picsum.photos/seed/x006/300/400', 129000, 40,  18, 15600),
('커버낫',        '스프링 오버사이즈 블루종 민트 - 시즌오프','OUTLET','https://picsum.photos/seed/x007/300/400', 119000, 40,  22, 14300),
('뉴발란스',      '327 레트로 테리 화이트/그레이 - 이월',  'OUTLET', 'https://picsum.photos/seed/x008/300/400', 139000, 35,  35, 24700),
('챔피온',        '리버스 위브 후드 스웨트셔츠 블랙 - 세일','OUTLET','https://picsum.photos/seed/x009/300/400',  89000, 40,  45, 31200),
('K2',            '쿨론 트레킹 팬츠 미디엄그레이 - 이월',  'OUTLET', 'https://picsum.photos/seed/x010/300/400',  99000, 45,  30, 21900),
('폴로 랄프로렌', '클래식 체크 플란넬 셔츠 레드 - 이월',   'OUTLET', 'https://picsum.photos/seed/x011/300/400', 119000, 35,  28, 16700),
('아더에러',      '스트링 클러치 파우치백 블랙 - 시즌오프','OUTLET', 'https://picsum.photos/seed/x012/300/400',  99000, 40,  12, 38500),
('나이키',        '나이키 클럽 플리스 후드 - 이월 상품',   'OUTLET', 'https://picsum.photos/seed/x013/300/400',  89000, 35,  50, 27800),
('디스커버리',    '라이트 다운 베스트 블랙 - 재고 세일',   'OUTLET', 'https://picsum.photos/seed/x014/300/400',  99000, 45,  25, 19300),
('반스',          '올드스쿨 체커보드 화이트/블랙 - 이월', 'OUTLET', 'https://picsum.photos/seed/x015/300/400',  99000, 30,  40, 22400),
('무신사 스탠다드','우모 패딩 베스트 블랙 - 시즌오프',     'OUTLET', 'https://picsum.photos/seed/x016/300/400',  59000, 50,  60, 33600);


-- ============================================================
-- 회원 테이블
-- ============================================================
CREATE TABLE IF NOT EXISTS members (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    login_pw VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT members_uk UNIQUE (login_id)
);

COMMENT ON TABLE members IS '회원';
COMMENT ON COLUMN members.id IS '아이디';
COMMENT ON COLUMN members.name IS '회원명';
COMMENT ON COLUMN members.login_id IS '로그인 아이디';
COMMENT ON COLUMN members.login_pw IS '로그인 패스워드';
COMMENT ON COLUMN members.created_at IS '생성 일시';

INSERT INTO members (name, login_id, login_pw)
VALUES ('알파', 'alpha@example.com', 'password111')
ON CONFLICT (login_id) DO NOTHING;


-- ============================================================
-- 장바구니 테이블
-- ============================================================
CREATE TABLE IF NOT EXISTS carts (
    id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_cart_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_item   FOREIGN KEY (item_id)   REFERENCES items(id)   ON DELETE CASCADE
);

COMMENT ON TABLE carts IS '장바구니';
COMMENT ON COLUMN carts.member_id IS '회원 아이디';
COMMENT ON COLUMN carts.item_id   IS '상품 아이디';


-- ============================================================
-- 주문 테이블
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    member_id  INTEGER NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    address    VARCHAR(500) NOT NULL,
    payment    VARCHAR(10)  NOT NULL,
    card_number VARCHAR(16),
    amount     BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_order_member FOREIGN KEY (member_id) REFERENCES members(id)
);

COMMENT ON TABLE orders IS '주문';
COMMENT ON COLUMN orders.member_id  IS '주문자 회원 아이디';
COMMENT ON COLUMN orders.payment    IS '결제 수단 (CARD / CASH)';
COMMENT ON COLUMN orders.amount     IS '최종 결제 금액';


-- ============================================================
-- 주문 상품 테이블
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
    id       SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    item_id  INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_item  FOREIGN KEY (item_id)  REFERENCES items(id)
);

COMMENT ON TABLE order_items IS '주문 상품';
COMMENT ON COLUMN order_items.order_id IS '주문 아이디';
COMMENT ON COLUMN order_items.item_id  IS '주문 상품 아이디';

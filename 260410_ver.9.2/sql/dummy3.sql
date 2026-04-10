-- ============================================================
-- Ventalize 주문 더미 데이터 3,000건
-- dummy1.sql(회원 200건) + dummy2.sql(상품 240건) 실행 후 수행하세요.
-- orders + order_items 테이블을 함께 채웁니다.
-- ============================================================

DO $$
DECLARE
  -- 더미 회원 ID 배열 (user001~user200)
  v_member_ids  INTEGER[];
  -- 더미 상품 ID 배열 (마지막 240건)
  v_item_ids    INTEGER[];

  v_member_id   INTEGER;
  v_item_id     INTEGER;
  v_order_id    INTEGER;
  v_amount      BIGINT;
  v_item_price  INTEGER;
  v_quantity    INTEGER;
  v_status      TEXT;
  v_payment     TEXT;
  v_name        TEXT;
  v_address     TEXT;
  v_created_at  TIMESTAMP;
  v_num_items   INTEGER;

  -- 상태별 가중치 (총합 20)
  v_statuses    TEXT[]    := ARRAY[
    'DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED',  -- 35%
    'SHIPPING', 'SHIPPING', 'SHIPPING', 'SHIPPING',                                       -- 20%
    'PAID',     'PAID',     'PAID',     'PAID',                                           -- 20%
    'CANCELLED','CANCELLED','CANCELLED',                                                   -- 15%
    'PENDING_PAYMENT','PENDING_PAYMENT'                                                    -- 10%
  ];
  v_payments    TEXT[]    := ARRAY['카드','카드','카드','계좌이체','카카오페이','네이버페이'];

  v_names       TEXT[]    := ARRAY[
    '김지민','이수연','박미래','최하늘','정예린','강지아','조소연','윤유나','장다은','임수빈',
    '한민준','오서준','서도윤','신예준','권시우','황주원','안하준','송지호','류민재','전우진',
    '한채원','오지우','서나영','신소희','권지현','황준서','안건우','송현우','류지훈','전준혁'
  ];
  v_dists       TEXT[]    := ARRAY[
    '강남구 테헤란로','서초구 서초대로','마포구 홍익로','용산구 이태원로','송파구 올림픽로',
    '강서구 방화대로','노원구 동일로','관악구 관악로','성북구 정릉로','동작구 사당로',
    '종로구 율곡로','중구 명동길','은평구 은평로','서대문구 연세로','양천구 목동로',
    '광진구 능동로','구로구 디지털로','성동구 왕십리로','영등포구 여의대로','중랑구 망우로'
  ];

  i         INTEGER;
  j         INTEGER;
  v_dup_chk INTEGER;
BEGIN
  -- ── 더미 회원 ID 수집 ──────────────────────────────────────
  SELECT ARRAY(
    SELECT id FROM members
    WHERE login_id LIKE 'user%@ventalize.com'
    ORDER BY id
  ) INTO v_member_ids;

  IF array_length(v_member_ids, 1) IS NULL THEN
    RAISE EXCEPTION 'dummy1.sql을 먼저 실행하세요. (회원 데이터 없음)';
  END IF;

  -- ── 더미 상품 ID 수집 ──────────────────────────────────────
  SELECT ARRAY(
    SELECT id FROM items
    ORDER BY id DESC
    LIMIT 240
  ) INTO v_item_ids;

  IF array_length(v_item_ids, 1) IS NULL THEN
    RAISE EXCEPTION 'dummy2.sql을 먼저 실행하세요. (상품 데이터 없음)';
  END IF;

  RAISE NOTICE '회원 % 명 / 상품 % 건 기준으로 주문 3,000건 생성 시작...',
    array_length(v_member_ids,1), array_length(v_item_ids,1);

  -- ── 주문 3,000건 생성 ─────────────────────────────────────
  FOR i IN 1..3000 LOOP

    -- 랜덤 회원
    v_member_id := v_member_ids[
      1 + (floor(random() * array_length(v_member_ids, 1)))::INT
    ];

    -- 수령인 이름 / 주소
    v_name    := v_names[1 + (i % array_length(v_names,1))];
    v_address := '서울시 ' || v_dists[1 + (i % array_length(v_dists,1))]
              || ' ' || (i * 17 % 500 + 1)::TEXT || '번지';

    -- 결제수단
    v_payment := v_payments[1 + (floor(random() * array_length(v_payments,1)))::INT];

    -- 주문 상태 (가중치 배열에서 랜덤)
    v_status  := v_statuses[1 + (floor(random() * array_length(v_statuses,1)))::INT];

    -- 주문 생성일 (최근 2년 내 랜덤)
    v_created_at := NOW() - (floor(random() * 730)::TEXT || ' days')::INTERVAL
                          - (floor(random() * 24)::TEXT || ' hours')::INTERVAL;

    -- 주문 아이템 수 (1~3개)
    v_num_items := 1 + (floor(random() * 3))::INT;
    v_amount    := 0;

    -- ── 주문 헤더 INSERT ────────────────────────────────────
    INSERT INTO orders
      (member_id, name, address, payment, amount, status, created_at)
    VALUES
      (v_member_id, v_name, v_address, v_payment, 0, v_status, v_created_at)
    RETURNING id INTO v_order_id;

    -- ── 주문 상품 INSERT ────────────────────────────────────
    FOR j IN 1..v_num_items LOOP
      v_item_id := v_item_ids[
        1 + (floor(random() * array_length(v_item_ids, 1)))::INT
      ];
      v_quantity := 1 + (floor(random() * 2))::INT;  -- 1~2개

      -- 판매가 계산 (할인 반영)
      SELECT price * (100 - discount_per) / 100 * v_quantity
        INTO v_item_price
        FROM items WHERE id = v_item_id;

      v_amount := v_amount + COALESCE(v_item_price, 150000);

      INSERT INTO order_items (order_id, item_id, quantity, created_at)
      VALUES (v_order_id, v_item_id, v_quantity, v_created_at)
      ON CONFLICT DO NOTHING;

      -- view_count 증가 (인기 상품 시뮬레이션)
      UPDATE items
         SET view_count = view_count + 1
       WHERE id = v_item_id;
    END LOOP;

    -- ── 주문 금액 업데이트 ──────────────────────────────────
    UPDATE orders SET amount = v_amount WHERE id = v_order_id;

  END LOOP;

  RAISE NOTICE '✅ 주문 더미 데이터 3,000건 삽입 완료';
END $$;

-- ============================================================
-- Ventalize 회원 더미 데이터 200건
-- 비밀번호 '1234'는 앱 실행(재시작) 시 AdminInitializer가
-- BCrypt 해시로 자동 변환됩니다.
-- 실행 순서: dummy1.sql → dummy2.sql → dummy3.sql
-- ============================================================

DO $$
DECLARE
  lasts  TEXT[] := ARRAY[
    '김','이','박','최','정','강','조','윤','장','임',
    '한','오','서','신','권','황','안','송','류','전'
  ];
  firsts TEXT[] := ARRAY[
    '지민','수연','미래','하늘','예린','지아','소연','유나','다은','수빈',
    '민준','서준','도윤','예준','시우','주원','하준','지호','민재','우진',
    '채원','지우','나영','소희','지현','준서','건우','현우','지훈','준혁',
    '윤서','은지','시연','민서','보람','다현','태양','현수','성민','유진'
  ];
  grades TEXT[] := ARRAY[
    'SAPPHIRE','SAPPHIRE','SAPPHIRE','SAPPHIRE','SAPPHIRE','SAPPHIRE',
    'RUBY',    'RUBY',    'RUBY',
    'EMERALD', 'EMERALD',
    'GOLD',
    'DIAMOND'
  ];
  dists TEXT[] := ARRAY[
    '강남구','서초구','마포구','용산구','송파구',
    '강서구','노원구','관악구','성북구','동작구',
    '종로구','중구','은평구','서대문구','양천구'
  ];
  cities TEXT[] := ARRAY[
    '서울시','인천시','수원시','성남시','고양시',
    '용인시','부천시','안산시','안양시','남양주시'
  ];
  i       INTEGER;
  v_name  TEXT;
  v_grade TEXT;
  v_stat  TEXT;
  v_phone TEXT;
  v_addr  TEXT;
BEGIN
  FOR i IN 1..200 LOOP
    v_name  := lasts [1 + (i           % array_length(lasts,  1))]
            || firsts[1 + ((i * 7 + 3) % array_length(firsts, 1))];
    v_grade := grades[1 + ((i * 3)     % array_length(grades, 1))];
    v_stat  := CASE WHEN i % 20 = 0 THEN 'SUSPENDED' ELSE 'ACTIVE' END;
    v_phone := '010-'
            || LPAD(((i * 37 + 1000) % 9000 + 1000)::TEXT, 4, '0')
            || '-'
            || LPAD(((i * 73 + 1000) % 9000 + 1000)::TEXT, 4, '0');
    v_addr  := cities[1 + (i % array_length(cities,1))]
            || ' '
            || dists [1 + (i % array_length(dists, 1))]
            || ' '
            || (i * 13 % 500 + 1)::TEXT
            || '번지 '
            || (i % 30 + 1)::TEXT
            || '동 '
            || (i % 20 + 1)::TEXT
            || '호';

    INSERT INTO members
      (name, login_id, login_pw, role, grade, status, phone, address, created_at)
    VALUES (
      v_name,
      'user' || LPAD(i::TEXT, 3, '0') || '@ventalize.com',
      '$2b$10$TTidDYNE5ZFUiXRW95j7A.Hc6y56oaumE3/WEwqrSRxT4sjozgGGW',
      'ROLE_USER',
      v_grade,
      v_stat,
      v_phone,
      v_addr,
      NOW() - ((i * 3 % 730) || ' days')::INTERVAL
             - (random() * INTERVAL '23 hours')
    )
    ON CONFLICT (login_id) DO NOTHING;
  END LOOP;

  RAISE NOTICE '✅ 회원 더미 데이터 삽입 완료 (최대 200건)';
END $$;

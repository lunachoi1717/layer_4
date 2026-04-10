"""
Ventalize - 주문 더미 데이터 생성 스크립트
===========================================
dummy1.sql (회원 200건) + dummy2.sql (상품 240건) 실행 후 수행하세요.
orders + order_items 테이블에 3,000건의 주문 더미 데이터를 삽입합니다.

사용법:
  pip install psycopg2-binary
  python generate_orders.py

DB 설정을 아래 DB_CONFIG에 맞게 수정하세요.
"""

import psycopg2
import random
from datetime import datetime, timedelta

# ── DB 접속 설정 ─────────────────────────────────────────────
DB_CONFIG = {
    "host":     "localhost",
    "port":     5432,
    "dbname":   "ventalize",
    "user":     "postgres",
    "password": "postgres",
}

# ── 상수 ─────────────────────────────────────────────────────
TOTAL_ORDERS = 3000

STATUSES = [
    "DELIVERED", "DELIVERED", "DELIVERED", "DELIVERED", "DELIVERED",
    "DELIVERED", "DELIVERED",   # 35%
    "SHIPPING",  "SHIPPING",  "SHIPPING",  "SHIPPING",  # 20%
    "PAID",      "PAID",      "PAID",      "PAID",      # 20%
    "CANCELLED", "CANCELLED", "CANCELLED",              # 15%
    "PENDING_PAYMENT", "PENDING_PAYMENT",               # 10%
]
PAYMENTS = ["카드", "카드", "카드", "계좌이체", "카카오페이", "네이버페이"]

NAMES = [
    "김지민", "이수연", "박미래", "최하늘", "정예린",
    "강지아", "조소연", "윤유나", "장다은", "임수빈",
    "한민준", "오서준", "서도윤", "신예준", "권시우",
    "황주원", "안하준", "송지호", "류민재", "전우진",
]

ADDRESSES = [
    "서울시 강남구 테헤란로 123 101동 201호",
    "서울시 서초구 서초대로 456 삼성아파트 502호",
    "인천시 남동구 구월동 789번지 3층",
    "경기도 성남시 분당구 정자동 100 현대아파트 1001호",
    "서울시 마포구 합정동 55-3 빌라 2층",
    "부산시 해운대구 우동 300 해운대아파트 305호",
    "서울시 송파구 잠실동 200 롯데캐슬 1503호",
    "경기도 수원시 영통구 영통동 500 광교아파트 601호",
    "서울시 용산구 이태원동 150-7",
    "대구시 수성구 범어동 99 수성아파트 402호",
]


def fetch_members(cur):
    """ROLE_USER 회원만 가져옵니다."""
    cur.execute("SELECT id, name FROM members WHERE role = 'ROLE_USER'")
    return cur.fetchall()  # list of (id, name)


def fetch_items(cur):
    """상품 ID와 가격(할인 적용)을 가져옵니다."""
    cur.execute("""
        SELECT id,
               FLOOR(price * (1.0 - discount_per / 100.0)) AS sale_price
        FROM items
    """)
    return cur.fetchall()  # list of (id, sale_price)


def random_date(days_back=730):
    """최근 days_back일 내 랜덤 날짜 반환."""
    return datetime.now() - timedelta(
        days=random.randint(0, days_back),
        hours=random.randint(0, 23),
        minutes=random.randint(0, 59),
    )


def insert_order(cur, member_id, member_name, items):
    """주문 1건 + 주문 상품을 삽입하고 삽입된 주문 ID를 반환합니다."""
    num_items  = random.randint(1, 3)
    chosen     = random.sample(items, min(num_items, len(items)))
    status     = random.choice(STATUSES)
    payment    = random.choice(PAYMENTS)
    recip_name = random.choice(NAMES)
    address    = random.choice(ADDRESSES)
    created_at = random_date()

    card_number = None
    if payment == "카드":
        card_number = (
            f"{random.randint(1000,9999)}-{random.randint(1000,9999)}"
            f"-{random.randint(1000,9999)}-{random.randint(1000,9999)}"
        )

    # 주문 금액 계산
    quantities = [random.randint(1, 3) for _ in chosen]
    amount = sum(int(item[1]) * qty for item, qty in zip(chosen, quantities))
    if amount <= 0:
        amount = 10000

    cur.execute(
        """
        INSERT INTO orders
            (member_id, name, address, payment, card_number, amount, status, created_at)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        RETURNING id
        """,
        (member_id, recip_name, address, payment, card_number,
         amount, status, created_at),
    )
    order_id = cur.fetchone()[0]

    for (item_id, _), qty in zip(chosen, quantities):
        cur.execute(
            """
            INSERT INTO order_items (order_id, item_id, quantity, created_at)
            VALUES (%s, %s, %s, %s)
            """,
            (order_id, item_id, qty, created_at),
        )

    return order_id


def main():
    conn = psycopg2.connect(**DB_CONFIG)
    conn.autocommit = False
    cur = conn.cursor()

    print("회원 목록 조회 중...")
    members = fetch_members(cur)
    if not members:
        print("ERROR: 회원이 없습니다. dummy1.sql을 먼저 실행하세요.")
        conn.close()
        return

    print("상품 목록 조회 중...")
    items = fetch_items(cur)
    if not items:
        print("ERROR: 상품이 없습니다. dummy2.sql을 먼저 실행하세요.")
        conn.close()
        return

    print(f"회원 {len(members)}명, 상품 {len(items)}건으로 {TOTAL_ORDERS}건 주문 생성 시작...")

    try:
        for i in range(1, TOTAL_ORDERS + 1):
            member_id, member_name = random.choice(members)
            insert_order(cur, member_id, member_name, items)

            if i % 100 == 0:
                conn.commit()
                print(f"  {i}/{TOTAL_ORDERS} 완료...")

        conn.commit()
        print(f"✅ 주문 더미 데이터 {TOTAL_ORDERS}건 삽입 완료!")

    except Exception as e:
        conn.rollback()
        print(f"ERROR: {e}")
        raise

    finally:
        cur.close()
        conn.close()


if __name__ == "__main__":
    main()

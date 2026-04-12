"""
이미지 폴더를 스캔해서 items INSERT SQL을 자동 생성합니다.
사용법: python3 generate_items_sql.py > insert_items.sql

- frontend/public/images/SCARVES/      → category = SCARVES
- frontend/public/images/BAGS/         → category = BAGS
- frontend/public/images/READY_TO_WEAR/ → category = READY_TO_WEAR
- frontend/public/images/ACCESSORIES/  → category = ACC
"""

import os
import random

# ──────────────────────────────────────────────
# 스크립트 위치 기준 이미지 루트 경로
# ──────────────────────────────────────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
IMAGES_ROOT = os.path.join(BASE_DIR, '..', 'frontend', 'public', 'images')

# ──────────────────────────────────────────────
# 카테고리별 설정
# ──────────────────────────────────────────────
CATEGORY_CONFIG = {
    'SCARVES': {
        'db_category': 'SCARVES',
        'name_prefix': 'Ventalize Silk Scarf',
        'price_range': (180000, 450000),
        'price_step':   10000,
        'descriptions': [
            '시그니처 실크 스카프. 이탈리아산 100% 실크로 제작되어 부드럽고 우아한 광택을 자랑합니다.',
            '클래식한 페이즐리 패턴의 럭셔리 스카프. 사계절 다양한 스타일링에 어울립니다.',
            '벙딸리제의 아이코닉 프린트가 담긴 트윌 스카프. 목걸이나 헤어밴드로도 활용 가능합니다.',
            '가볍고 부드러운 쉬폰 스카프. 어깨에 걸치거나 가방 손잡이에 묶어 포인트를 주세요.',
            '모노그램 패턴의 시그니처 스카프. 선물로도 손색없는 고급 포장 제공.',
        ],
    },
    'BAGS': {
        'db_category': 'BAGS',
        'name_prefix': 'Ventalize Leather Bag',
        'price_range': (580000, 2800000),
        'price_step':   10000,
        'descriptions': [
            '이탈리아산 풀그레인 레더로 제작한 미니 토트백. 실용성과 우아함을 동시에.',
            '클래식한 실루엣의 숄더백. 어깨끈 길이 조절 가능, 크로스백으로도 착용 가능.',
            '캐주얼과 포멀을 넘나드는 올데이 캔버스 백. 내부 수납 공간이 넉넉합니다.',
            '부드러운 나파 레더의 체인 클러치백. 저녁 파티부터 데일리룩까지 완성.',
            '오버사이즈 토트백. A4 문서와 노트북이 모두 들어가는 실용적인 디자인.',
        ],
    },
    'READY_TO_WEAR': {
        'db_category': 'READY_TO_WEAR',
        'name_prefix': 'Ventalize RTW',
        'price_range': (280000, 980000),
        'price_step':   10000,
        'descriptions': [
            '고급 울 혼방 소재의 클래식 블레이저. 오피스룩과 캐주얼 모두 소화 가능.',
            '실크 새틴 블라우스. 은은한 광택과 드레이프가 아름다운 고급 소재.',
            '와이드 핏 트라우저. 허리 밴드 처리로 편안함과 스타일을 동시에.',
            '리넨 혼방의 오버사이즈 셔츠 드레스. 시원하고 고급스러운 여름 룩.',
            '미디 길이의 플리츠 스커트. 가볍게 움직이는 실루엣이 매력적.',
        ],
    },
    'ACCESSORIES': {
        'db_category': 'ACC',
        'name_prefix': 'Ventalize Accessory',
        'price_range': (120000, 380000),
        'price_step':   10000,
        'descriptions': [
            '925 스털링 실버 체인 브레이슬릿. 심플하면서도 고급스러운 일상 주얼리.',
            '골드 플레이팅 로고 이어링. 미니멀한 디자인으로 어떤 룩에도 잘 어울립니다.',
            '이탈리아산 가죽 카드 홀더. 얇고 슬림한 디자인으로 지갑 대신 사용 가능.',
            '실크 리본 헤어 스카프. 머리에 묶거나 가방에 장식으로 활용하세요.',
            '클래식 골드 벨트. 34mm 폭의 우아한 레더 벨트로 허리 라인을 강조.',
        ],
    },
}

# ──────────────────────────────────────────────
# 할인율 사이클 (0%, 0%, 10%, 0%, 20% 반복)
# ──────────────────────────────────────────────
DISCOUNT_CYCLE = [0, 0, 10, 0, 20]

def round_price(p, step=10000):
    return (p // step) * step

def scan_images(folder_path):
    """폴더에서 jpg/jpeg/png 이미지 파일 목록 반환 (정렬)"""
    if not os.path.isdir(folder_path):
        return []
    files = sorted([
        f for f in os.listdir(folder_path)
        if f.lower().endswith(('.jpg', '.jpeg', '.png'))
    ])
    return files

def generate_sql():
    rows = []

    for folder_name, cfg in CATEGORY_CONFIG.items():
        folder_path = os.path.join(IMAGES_ROOT, folder_name)
        images = scan_images(folder_path)

        if not images:
            print(f"-- ⚠ {folder_name} 폴더에 이미지 없음 (건너뜀)")
            continue

        print(f"-- ✔ {folder_name}: {len(images)}개 이미지 발견")

        pmin, pmax = cfg['price_range']
        step       = cfg['price_step']
        n          = len(images)

        # 가격을 이미지 수만큼 균등 분배
        if n == 1:
            prices = [round_price((pmin + pmax) // 2, step)]
        else:
            prices = [
                round_price(pmin + (pmax - pmin) * i // (n - 1), step)
                for i in range(n)
            ]

        descs = cfg['descriptions']

        for idx, (img_file, price) in enumerate(zip(images, prices)):
            name        = f"{cfg['name_prefix']} No.{idx + 1}"
            category    = cfg['db_category']
            img_path    = f"/images/{folder_name}/{img_file}"
            description = descs[idx % len(descs)]
            discount    = DISCOUNT_CYCLE[idx % len(DISCOUNT_CYCLE)]
            stock       = random.choice([30, 50, 70, 100])

            # SQL 문자열 이스케이프
            name_esc  = name.replace("'", "''")
            desc_esc  = description.replace("'", "''")

            rows.append(
                f"  ('{name_esc}', '{category}', '{img_path}', '{desc_esc}', {price}, {discount}, {stock})"
            )

    return rows

def main():
    import sys
    # 콘솔 진단 메시지를 stderr로 출력 (SQL 파일에 섞이지 않게)
    import sys
    rows = generate_sql()

    if not rows:
        print("-- 삽입할 데이터가 없습니다. 이미지 폴더를 확인하세요.", file=sys.stderr)
        return

    print(f"\n-- 총 {len(rows)}개 상품 INSERT")
    print("INSERT INTO items (name, category, img_path, description, price, discount_per, stock_count)")
    print("VALUES")
    print(",\n".join(rows) + ";")
    print("\n-- 완료")

if __name__ == '__main__':
    main()

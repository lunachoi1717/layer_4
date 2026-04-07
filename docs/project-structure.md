# Ventalize — 프로젝트 구조

> 최종 수정: 2026-03

---

## 전체 디렉토리

```
shop_musinsa/
├── docs/                        # 개발 문서 (이 폴더)
│   ├── project-structure.md     # 프로젝트 구조 (현재 파일)
│   ├── coding-convention.md     # 코딩 컨벤션
│   └── style-guide.md           # UI 스타일 가이드
│
├── backend/
│   └── shop/                    # Spring Boot 애플리케이션
│
├── frontend/                    # Vue 3 + Vite 애플리케이션
│
├── ventalize_ddl.sql            # DB 초기 스키마
├── 실행가이드.md
└── README.md
```

---

## 백엔드 구조

```
backend/shop/
├── build.gradle                 # Gradle 빌드 설정
├── settings.gradle
└── src/main/
    ├── java/com/ventalize/shop/
    │   ├── ShopApplication.java
    │   │
    │   ├── config/
    │   │   ├── AdminInitializer.java    # 서버 시작 시 초기화 (관리자 계정, DB 마이그레이션)
    │   │   └── SecurityConfig.java      # Spring Security 설정
    │   │
    │   ├── controller/
    │   │   ├── ItemController.java          # 상품 목록/상세/검색 (공개)
    │   │   ├── AdminItemController.java     # 상품 CRUD + 이미지 업로드 (관리자)
    │   │   ├── AdminDashboardController.java# 대시보드 통계
    │   │   ├── CartController.java          # 장바구니
    │   │   ├── OrderController.java         # 주문
    │   │   └── ...                          # 회원, 리뷰, Q&A, 쿠폰 등
    │   │
    │   ├── dto/                             # 요청/응답 DTO
    │   │   ├── item/
    │   │   │   ├── ItemRead.java            # 상품 응답 DTO
    │   │   │   └── ItemCreateRequest.java   # 상품 등록/수정 요청 DTO
    │   │   ├── member/
    │   │   ├── cart/
    │   │   ├── order/
    │   │   └── ...
    │   │
    │   ├── entity/                          # JPA 엔티티
    │   │   ├── Item.java
    │   │   ├── Member.java
    │   │   ├── Order.java
    │   │   ├── OrderItem.java
    │   │   ├── Cart.java
    │   │   ├── Review.java
    │   │   ├── Question.java
    │   │   ├── Coupon.java
    │   │   ├── Faq.java
    │   │   └── Inquiry.java
    │   │
    │   ├── repository/                      # Spring Data JPA
    │   │   ├── ItemRepository.java
    │   │   ├── MemberRepository.java
    │   │   └── ...
    │   │
    │   ├── service/                         # 비즈니스 로직
    │   │   ├── ItemService.java
    │   │   ├── CartService.java
    │   │   └── ...
    │   │
    │   ├── filter/                          # 인증 처리
    │   ├── util/                            # SecurityUtil 등
    │   └── helper/
    │
    └── resources/
        └── application.yml                  # DB, 파일 업로드 설정
```

### 주요 설정 (application.yml)

| 항목 | 값 |
|------|-----|
| 서버 포트 | 8102 |
| DB | PostgreSQL (localhost:5433/ventalize) |
| JPA DDL | update (자동 스키마 업데이트) |
| 파일 업로드 경로 | `uploads/` (프로젝트 루트) |
| 최대 파일 크기 | 10MB |
| 이미지 서빙 URL | `GET /v1/images/{filename}` |

---

## 프론트엔드 구조

```
frontend/
├── index.html
├── package.json
├── vite.config.js               # Vite 설정 + 프록시 (/v1 → localhost:8102)
└── src/
    ├── main.js                  # Vue 앱 진입점
    ├── App.vue                  # 루트 컴포넌트
    ├── style.css                # 전역 디자인 시스템
    │
    ├── router/
    │   └── index.js             # Vue Router 설정
    │
    ├── composables/
    │   └── useAuth.js           # 인증 상태 (전역 싱글톤)
    │
    ├── components/              # 공통 레이아웃 컴포넌트
    │   ├── Header.vue           # 헤더 (네비, 검색, 장바구니 점)
    │   └── Footer.vue           # 푸터 (링크, SNS 아이콘)
    │
    └── views/                   # 페이지 컴포넌트
        ├── Home.vue             # 메인 (히어로, 신상품, 베스트, 추천)
        ├── Category.vue         # 카테고리/검색 결과
        ├── ProductDetail.vue    # 상품 상세
        ├── Cart.vue             # 장바구니
        ├── CartCheckout.vue     # 장바구니 주문 결제
        ├── Checkout.vue         # 바로 구매 결제
        ├── MyPage.vue           # 마이페이지 (주문, 리뷰, Q&A, 쿠폰, 프로필)
        ├── Login.vue
        ├── Register.vue
        │
        ├── admin/               # 관리자 페이지
        │   ├── AdminLayout.vue  # 관리자 레이아웃 (사이드바)
        │   ├── Dashboard.vue    # 대시보드 (통계, 재고 부족, 최근 주문)
        │   ├── Products.vue     # 상품 관리
        │   ├── Orders.vue       # 주문 관리
        │   ├── Members.vue      # 회원 관리
        │   ├── Coupons.vue      # 쿠폰 관리
        │   ├── Reviews.vue      # 리뷰 관리
        │   ├── QnA.vue          # Q&A 관리
        │   └── Board.vue        # 게시판 관리
        │
        └── board/               # 게시판 페이지
            ├── BoardLayout.vue  # 게시판 레이아웃
            ├── Faq.vue
            ├── Inquiry.vue
            ├── InquiryDetail.vue
            └── BoardReviews.vue
```

---

## 라우팅 구조

```
/                          → Home.vue
/category/:name            → Category.vue  (카테고리 or 검색 결과)
/product/:id               → ProductDetail.vue
/login                     → Login.vue
/register                  → Register.vue
/cart                      → Cart.vue
/mypage                    → MyPage.vue              [requiresAuth]
/checkout                  → Checkout.vue            [requiresAuth]
/cart-checkout             → CartCheckout.vue        [requiresAuth]
/board                     → BoardLayout.vue
  /board/faq               → Faq.vue
  /board/inquiry           → Inquiry.vue
  /board/reviews           → BoardReviews.vue
/board/inquiry/:id         → InquiryDetail.vue
/admin                     → AdminLayout.vue          [requiresAdmin]
  /admin/dashboard         → Dashboard.vue
  /admin/products          → Products.vue
  /admin/orders            → Orders.vue
  /admin/members           → Members.vue
  /admin/coupons           → Coupons.vue
  /admin/reviews           → Reviews.vue
  /admin/qna               → QnA.vue
  /admin/board             → Board.vue
```

---

## 주요 API 엔드포인트 목록

### 공개 API

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/items` | 전체/카테고리/정렬/검색 |
| GET | `/v1/api/items?sort=new` | 신상품 5개 |
| GET | `/v1/api/items?sort=best` | 베스트셀러 5개 |
| GET | `/v1/api/items?sort=recommend` | 추천 상품 5개 |
| GET | `/v1/api/items?category={cat}` | 카테고리 필터 |
| GET | `/v1/api/items?keyword={q}` | 키워드 검색 |
| GET | `/v1/api/items/{id}` | 상품 상세 (조회수 +1) |
| GET | `/v1/api/items/{id}/related` | 관련 상품 |
| GET | `/v1/images/{filename}` | 업로드 이미지 서빙 |

### 인증 필요 API

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/cart/items` | 장바구니 목록 |
| POST | `/v1/api/carts` | 장바구니 추가 |
| PATCH | `/v1/api/cart/item/{id}/qty` | 수량 변경 |
| DELETE | `/v1/api/cart/item/{id}` | 장바구니 삭제 |
| GET | `/v1/api/orders` | 내 주문 목록 |
| PATCH | `/v1/api/orders/{id}/cancel` | 주문 취소 |
| GET/POST | `/v1/api/reviews/...` | 리뷰 |
| GET/POST | `/v1/api/qna/...` | Q&A |
| GET/PUT | `/v1/api/account/profile` | 프로필 |
| DELETE | `/v1/api/account/withdraw` | 회원탈퇴 |

### 관리자 API (`/v1/api/admin/`)

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/admin/dashboard` | 대시보드 통계 |
| GET/POST/PUT/DELETE | `/v1/api/admin/items` | 상품 CRUD |
| POST | `/v1/api/admin/items/upload` | 이미지 업로드 |
| PATCH | `/v1/api/admin/items/{id}/stock` | 재고 수정 |
| GET/PATCH | `/v1/api/admin/orders` | 주문 관리 |
| GET/PATCH | `/v1/api/admin/members` | 회원 관리 |

---

## 인증 흐름

```
1. 로그인 성공
   → 서버: 세션 쿠키 발급
   → 클라이언트: localStorage에 loginId, userName, role, grade 저장
   → useAuth: isLoggedIn = true

2. API 요청
   → credentials: 'include' (세션 쿠키 자동 전송)

3. 라우트 가드
   → requiresAuth: localStorage.loginId 확인
   → requiresAdmin: localStorage.role === 'ROLE_ADMIN' 확인

4. 로그아웃
   → POST /v1/api/account/logout
   → localStorage 전체 삭제
   → isLoggedIn = false
```

---

## 데이터베이스 주요 테이블

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `items` | 상품 | id, name, category, img_path, price, discount_per, stock_count, view_count |
| `members` | 회원 | id, login_id, login_pw, role, grade, status |
| `orders` | 주문 | id, member_id, amount, status, name, address |
| `order_items` | 주문 상품 | order_id, item_id, quantity, subtotal |
| `carts` | 장바구니 | member_id, item_id, quantity |
| `reviews` | 리뷰 | item_id, member_id, rating, content |
| `questions` | Q&A | item_id, member_id, title, content, is_answered |
| `coupons` | 쿠폰 | code, discount_type, discount_value, min_order_amount |
| `faqs` | FAQ | title, content, category |
| `inquiries` | 1:1 문의 | member_id, title, content, is_answered |

### 상품 카테고리 코드

| 코드 | 한국어 |
|------|--------|
| `SCARVES` | 스카프 |
| `READY_TO_WEAR` | 기성복 |
| `PERFUME` | 향수 |
| `ACC` | 액세서리 |
| `BAG` | 가방 |
| `SHOES` | 신발 |
| `OUTER` | 아우터 |
| `TOP` | 상의 |
| `PANTS` | 하의 |
| `SALE` | 세일 |

### 회원 등급

`SAPPHIRE` → `RUBY` → `EMERALD` → `GOLD` → `DIAMOND`

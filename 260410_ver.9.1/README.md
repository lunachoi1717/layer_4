# Ventalize — 패션 쇼핑몰 포트폴리오

> Spring Boot 3 + Vue.js 3 기반의 풀스택 쇼핑몰 프로젝트
> 스카프 · 기성복 · 향수 · 액세서리 · 가방 · 슈즈 카테고리를 갖춘 럭셔리 온라인 쇼핑몰

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| **Backend** | Spring Boot 3.2, Java 21, Spring Security (Session), Spring Data JPA |
| **Database** | PostgreSQL 14+ |
| **Frontend** | Vue.js 3 (Composition API), Vite, Vue Router 4 |
| **빌드** | Gradle (backend), npm (frontend) |

---

## 프로젝트 구조

```
shop_musinsa/
├── backend/                        # Spring Boot 백엔드 루트
│   └── shop/                       # 실제 Gradle 프로젝트
│       ├── src/main/java/com/ventalize/shop/
│       │   ├── config/             # Security, CORS, AdminInitializer
│       │   ├── controller/         # REST API 컨트롤러 (일반 + 관리자)
│       │   ├── dto/                # 요청·응답 DTO
│       │   │   ├── cart/           # CartRequest(qty), CartItemResponse
│       │   │   ├── order/          # OrderRequest(quantities), OrderItemDetail
│       │   │   └── item/
│       │   ├── entity/             # JPA 엔티티
│       │   ├── repository/         # Spring Data JPA Repository
│       │   ├── service/            # 비즈니스 로직 (재고 차감, 주문 상세 등)
│       │   └── filter/             # 세션 필터
│       ├── src/main/resources/
│       │   └── application.yml     # DB·포트 설정
│       └── build.gradle
│
├── frontend/                       # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/             # 공통 컴포넌트 (Header, Footer 등)
│   │   ├── composables/useAuth.js  # 인증 상태 관리
│   │   ├── router/index.js         # 라우트 + 네비게이션 가드
│   │   └── views/
│   │       ├── Home.vue
│   │       ├── ProductDetail.vue   # 수량 선택 → 장바구니/바로구매
│   │       ├── Cart.vue
│   │       ├── CartCheckout.vue    # 장바구니 → 수량 반영 주문
│   │       ├── Checkout.vue        # 바로 구매
│   │       ├── MyPage.vue          # 주문상세·리뷰·Q&A·쿠폰·정보수정
│   │       └── admin/              # 관리자 화면
│   ├── package.json
│   └── vite.config.js
│
├── ventalize_ddl.sql               # 테이블 DDL + 더미 데이터 (카테고리 6종)
├── 실행가이드.md
└── README.md
```

---

## 실행 방법

### 1. PostgreSQL 설정

```sql
-- psql 접속 후 실행
CREATE DATABASE shop;
ALTER USER postgres PASSWORD '1004';
\q
```

```bash
# DDL 및 더미 데이터 초기화 (최초 1회)
psql -U postgres -d shop -f ventalize_ddl.sql
```

### 2. 백엔드 실행

```bash
cd backend/shop
./gradlew bootRun
```

- 포트: **8080**
- `backend/shop/src/main/resources/application.yml`에서 DB 비밀번호 확인
- 최초 실행 시 `AdminInitializer`가 자동으로 관리자 계정 생성

### 3. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

- 포트: **5173**
- Vite 프록시 설정으로 `/v1/*` 요청이 자동으로 `http://localhost:8080`으로 전달됩니다

---

## 테스트 계정

| 역할 | 이메일 | 비밀번호 |
|------|--------|---------|
| 관리자 | `admin@ventalize.com` | `admin1234` |
| 일반 회원 | `user1@ventalize.com` | `test1234` |
| 일반 회원 | `user2@ventalize.com` | `test1234` |
| 일반 회원 | `user3@ventalize.com` | `test1234` |

---

## 주요 기능

### 사용자

| 기능 | 설명 |
|------|------|
| 상품 목록 / 검색 | 카테고리·키워드 필터, 신상품·베스트·추천 정렬 |
| 상품 상세 | 이미지, 수량 선택, 리뷰/Q&A 탭, 관련 상품 |
| 장바구니 | 선택 수량 그대로 담기, 수량 수정, 삭제 |
| 바로 구매 | 선택 수량으로 단건 즉시 주문 |
| 장바구니 주문 | 복수 상품 수량 그대로 일괄 주문, 쿠폰 적용 |
| 마이페이지 | 주문 내역(상품명·수량·소계 표시), 리뷰·Q&A 관리, 쿠폰 조회, 정보 수정, 탈퇴 |
| 재고 관리 | 주문 시 자동 재고 차감, 재고 부족 시 주문 차단(409) |

### 관리자

| 기능 | 설명 |
|------|------|
| 대시보드 | 매출·주문·회원 통계 |
| 상품 관리 | CRUD, 이미지 업로드, 재고 수정 |
| 주문 관리 | 주문 목록 + 상품 상세 펼침(▼), 배송 상태 변경 |
| 회원 관리 | 목록 조회, 등급 변경, 강제 탈퇴 |
| 리뷰 / Q&A | 목록 조회, 답변 등록, 삭제 |
| 쿠폰 | 발급, 등급별 타깃 설정 |

---

## 상품 카테고리

| 코드 | 이름 | 더미 데이터 |
|------|------|------------|
| `SCARVES` | 스카프 | 16개 |
| `READY_TO_WEAR` | 기성복 | 16개 |
| `PERFUME` | 향수 | 16개 |
| `ACC` | 액세서리 | 16개 |
| `BAG` | 가방 | 16개 |
| `SHOES` | 슈즈 | 16개 |

---

## 주요 API 엔드포인트

### 인증

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/v1/api/account/join` | 회원가입 |
| POST | `/v1/api/account/login` | 로그인 |
| POST | `/v1/api/account/logout` | 로그아웃 |
| GET | `/v1/api/account/profile` | 내 프로필 조회 |
| PUT | `/v1/api/account/profile` | 프로필 수정 |

### 상품

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/items` | 상품 목록 (`sort`, `category`, `keyword`) |
| GET | `/v1/api/items/{id}` | 상품 상세 (조회수 +1) |
| GET | `/v1/api/items/{id}/related` | 관련 상품 |

### 장바구니

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/cart/items` | 장바구니 목록 (수량 포함) |
| POST | `/v1/api/carts` | 담기 — `{ itemId, qty }` |
| PATCH | `/v1/api/cart/item/{itemId}/qty` | 수량 직접 변경 |
| DELETE | `/v1/api/cart/item/{itemId}` | 상품 삭제 |

### 주문

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/orders` | 내 주문 목록 (아이템 상세 포함) |
| GET | `/v1/api/orders/{id}` | 주문 상세 |
| POST | `/v1/api/orders` | 주문 생성 — `{ itemIds, quantities: {itemId: qty} }` |
| PATCH | `/v1/api/orders/{id}/cancel` | 주문 취소 |

### 관리자

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/v1/api/admin/dashboard` | 통계 조회 |
| GET | `/v1/api/admin/orders` | 전체 주문 (아이템 상세 포함) |
| PATCH | `/v1/api/admin/orders/{id}/status` | 배송 상태 변경 |
| GET/POST/PUT/DELETE | `/v1/api/admin/items/**` | 상품 CRUD |
| GET | `/v1/api/admin/members` | 회원 목록 |

---

## 화면 URL

### 사용자

| URL | 화면 |
|-----|------|
| `/` | 메인 홈 |
| `/category/{category}` | 카테고리 목록 |
| `/product/{id}` | 상품 상세 |
| `/cart` | 장바구니 |
| `/checkout?itemId=&qty=` | 바로 구매 |
| `/cart-checkout?items=` | 장바구니 결제 |
| `/mypage` | 마이페이지 |
| `/login` | 로그인 |
| `/register` | 회원가입 |

### 관리자

| URL | 화면 |
|-----|------|
| `/admin/dashboard` | 통계 대시보드 |
| `/admin/items` | 상품 관리 |
| `/admin/orders` | 주문 관리 |
| `/admin/members` | 회원 관리 |
| `/admin/reviews` | 리뷰 관리 |
| `/admin/qna` | Q&A 관리 |
| `/admin/coupons` | 쿠폰 관리 |

---

## 트러블슈팅

### DB 연결 실패
- PostgreSQL 서비스 실행 여부 확인
- `backend/src/main/resources/application.yml` 비밀번호 확인 (`password: 1004`)
- `shop` DB 존재 여부 확인

### 포트 충돌

```bash
# Linux/macOS
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID {PID} /F
```

### 재고 부족 오류
- 주문 수량 > 재고 시 HTTP **409** 응답 및 에러 메시지 표시
- 관리자 > 상품 관리에서 재고 수정 후 재시도

### CORS 오류
- 백엔드(8080)와 Vite 개발 서버(5173)가 모두 실행 중인지 확인
- `frontend/vite.config.js`의 proxy 설정 확인

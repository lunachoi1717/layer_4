# Ventalize — UI 스타일 가이드

> 컨셉: Luxury Editorial · Hermès × Kinfolk
> 기반 파일: `frontend/src/style.css`
> 최종 수정: 2026-03

---

## 목차

1. [디자인 철학](#1-디자인-철학)
2. [색상 시스템](#2-색상-시스템)
3. [타이포그래피](#3-타이포그래피)
4. [스페이싱 & 레이아웃](#4-스페이싱--레이아웃)
5. [컴포넌트](#5-컴포넌트)
6. [관리자 패널 스타일](#6-관리자-패널-스타일)
7. [반응형 브레이크포인트](#7-반응형-브레이크포인트)
8. [CSS 작성 규칙](#8-css-작성-규칙)

---

## 1. 디자인 철학

- **럭셔리 에디토리얼**: 여백이 풍부하고, 서체와 이미지 중심의 레이아웃
- **절제된 컬러**: 시그니처 딥 그린 + 크림 + 골드 3가지 핵심 색상
- **타입 하이어라키**: 세리프(Cormorant Garamond)로 품격, 산세리프(Inter)로 가독성
- **동작 최소화**: 호버 효과는 subtle하게, 전환은 `0.35s ease` 기준

---

## 2. 색상 시스템

### 2-1. CSS 변수 (`:root`)

```css
/* ── 팔레트 ── */
--c-black:   #111111;    /* 제목 등 강조 텍스트 */
--c-ink:     #1E1E1E;    /* 기본 본문 텍스트 */
--c-forest:  #1B3A2D;    /* 시그니처 딥 그린 (Primary) */
--c-sage:    #4A6741;    /* 세컨더리 그린 (hover) */
--c-cream:   #F5F0E8;    /* 시그니처 크림 (배경 강조) */
--c-sand:    #C9B89A;    /* 웜 액센트 */
--c-gold:    #B89C6E;    /* 골드 액센트 */
--c-stone:   #E8E2D9;    /* 구분선 / 보더 */
--c-mist:    #F8F5F0;    /* 배경 틴트 */
--c-white:   #FFFFFF;    /* 기본 배경 */
--c-muted:   #7A7269;    /* 보조 텍스트 */
--c-sale:    #8B2020;    /* 세일 빨강 */
```

### 2-2. 색상 사용 원칙

| 상황 | 사용 색상 |
|------|-----------|
| 주요 CTA 버튼 배경 | `--c-forest` (#1B3A2D) |
| 버튼 호버 | `--c-sage` (#4A6741) |
| 페이지 배경 강조 영역 | `--c-cream` (#F5F0E8) |
| 보더 / 구분선 | `--c-stone` (#E8E2D9) |
| 보조 텍스트 / 레이블 | `--c-muted` (#7A7269) |
| 할인 / 세일 표시 | `--c-sale` (#8B2020) |
| 골드 등급 / 악센트 | `--c-gold` (#B89C6E) |

### 2-3. 회원 등급 색상

| 등급 | 배경 | 텍스트 | CSS 클래스 |
|------|------|--------|------------|
| SAPPHIRE | `#DBEAFE` | `#1565C0` | `.grade-sapphire` |
| RUBY | `#FEE2E2` | `#B71C1C` | `.grade-ruby` |
| EMERALD | `#DCFCE7` | `#1B5E20` | `.grade-emerald` |
| GOLD | `#FEF3C7` | `#92400E` | `.grade-gold` |
| DIAMOND | 그라디언트 | `#4338CA` | `.grade-diamond` |

### 2-4. 주문 상태 색상

| 상태 | 배경 | 텍스트 | CSS 클래스 |
|------|------|--------|------------|
| PENDING_PAYMENT | `#FEF3C7` | `#92400E` | `.status-pending_payment` |
| PAID | `#DBEAFE` | `#1E40AF` | `.status-paid` |
| SHIPPING | `#EDE9FE` | `#5B21B6` | `.status-shipping` |
| DELIVERED | `#DCFCE7` | `#166534` | `.status-delivered` |
| CANCELLED | `#FEE2E2` | `#991B1B` | `.status-cancelled` |

---

## 3. 타이포그래피

### 3-1. 폰트 패밀리

```css
--font-serif: 'Cormorant Garamond', 'Georgia', serif;
--font-sans:  'Inter', 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;
```

- **세리프 (Cormorant Garamond)**: 헤드라인, 브랜드명, 상품명, 섹션 타이틀
- **산세리프 (Inter)**: 본문, UI 레이블, 버튼, 가격, 관리자 패널 전체

### 3-2. 타입 스케일

| 클래스 | font-size | font-weight | 용도 |
|--------|-----------|-------------|------|
| `.t-display` | clamp(2.8rem, 6vw, 5.5rem) | 300 | 히어로 타이틀 |
| `.t-heading` | clamp(1.6rem, 3vw, 2.4rem) | 400 | 섹션 제목 |
| `.t-sub` | 0.7rem | 500 | 서브 레이블 (letter-spacing: 0.18em, uppercase) |
| `.t-body` | 0.9rem | 400 | 본문 (line-height: 1.75) |
| `.t-caption` | 0.75rem | 400 | 보조 정보, 날짜 |

### 3-3. 페이지 헤더 타이틀 (카테고리 · 마이페이지)

```css
/* 카테고리 페이지 타이틀 — Category.vue */
.v-cat-page__title {
  font-size: clamp(1.4rem, 3vw, 2rem);
}

/* 마이페이지 헤더 타이틀 — MyPage.vue */
.v-mypage-hero-title {
  font-size: clamp(1.4rem, 3vw, 2rem);
}
```

> 두 타이틀은 동일한 `clamp` 값으로 통일되어 있습니다.

---

## 4. 스페이싱 & 레이아웃

### 4-1. 스페이싱 토큰

```css
--space-xs:  4px;
--space-sm:  8px;
--space-md:  16px;
--space-lg:  32px;
--space-xl:  64px;
--space-2xl: 96px;
```

### 4-2. 컨테이너

```css
.v-container {
  max-width: 1320px;   /* --container-max */
  margin: 0 auto;
  padding: 0 32px;     /* --space-lg */
}

/* 모바일 (≤768px) */
.v-container { padding: 0 16px; }
```

### 4-3. 섹션 패딩

- 주요 섹션 (상품 목록 등): `padding: 80px 0 64px`
- 히어로 헤더 영역: `padding: 56px 0 40px`
- 카테고리 헤더 하단 마진: `margin-bottom: 48px`

### 4-4. 그리드 시스템

| 섹션 | 컬럼 수 | gap |
|------|---------|-----|
| 메인 New Arrivals | 5열 | 20px |
| 메인 Best Sellers | 5열 | 20px |
| 메인 Curated for You | 5열 | 20px |
| 카테고리 상품 목록 | 4열 | 20/40px |
| 마이페이지 레이아웃 | `220px 1fr` | 32px |

---

## 5. 컴포넌트

### 5-1. 버튼

```html
<!-- 주요 행동 (구매, 등록) -->
<button class="btn btn-primary">Shop Now</button>

<!-- 보조 행동 (더보기, 링크) -->
<button class="btn btn-outline">View All</button>

<!-- 고스트 (취소, 닫기) -->
<button class="btn btn-ghost">Cancel</button>
```

```css
.btn { padding: 12px 32px; font-size: 0.75rem; letter-spacing: 0.12em; text-transform: uppercase; }
.btn-primary  { background: #1B3A2D; color: #F5F0E8; }
.btn-outline  { background: transparent; border: 1px solid #1E1E1E; }
.btn-ghost    { background: transparent; border: 1px solid #E8E2D9; color: #7A7269; }
```

### 5-2. 상품 카드

```html
<RouterLink to="/product/1" class="v-product-card">
  <div class="v-product-card__img-wrap" style="aspect-ratio: 3/4">
    <img src="..." alt="상품명" />
    <span class="v-product-card__badge">New</span>
    <span class="v-product-card__badge v-product-card__badge--sale">20%</span>
  </div>
  <div class="v-product-card__info">
    <p class="v-product-card__name">상품명</p>
    <div class="v-product-card__price">
      <span class="v-product-card__price-original">100,000원</span>
      <span class="v-product-card__price-sale">80,000원</span>
      <span class="v-product-card__discount">-20%</span>
    </div>
  </div>
</RouterLink>
```

**규칙:**
- 이미지 비율: `aspect-ratio: 3/4` (세로형 패션 이미지)
- 호버 시 이미지 `scale(1.04)` 트랜지션
- 배지: 좌상단 고정 위치

### 5-3. 섹션 레이블

```html
<div class="v-section-label">
  <span>New Arrivals</span>
</div>
```

> 우측에 가로선이 자동으로 그어집니다 (`::after` pseudo).

### 5-4. 토스트 알림

```html
<div v-if="toastMsg" class="v-toast">{{ toastMsg }}</div>
```

```js
function showToast(msg) {
  toastMsg.value = msg
  setTimeout(() => { toastMsg.value = '' }, 2500)
}
```

### 5-5. 스피너 (로딩)

```html
<!-- 쇼핑몰 -->
<div v-if="loading" class="v-spinner"></div>

<!-- 관리자 -->
<div v-if="loading" class="loading-box">
  <div class="spinner"></div>
</div>
```

### 5-6. 뱃지

```html
<!-- 등급 뱃지 -->
<span class="grade-badge grade-sapphire">SAPPHIRE</span>
<span class="grade-badge grade-diamond">DIAMOND</span>

<!-- 상태 뱃지 -->
<span class="order-status status-paid">결제완료</span>
<span class="order-status status-shipping">배송중</span>
```

---

## 6. 관리자 패널 스타일

관리자 패널은 쇼핑몰과 **완전히 별개의 디자인 시스템**을 사용합니다.
CSS 변수는 `AdminLayout.vue`의 `:root`에 정의됩니다.

### 6-1. 관리자 CSS 변수

```css
--vad-sidebar-w: 224px;
--vad-bg:        #F7F8FA;
--vad-surface:   #FFFFFF;
--vad-border:    #E5E7EB;
--vad-text:      #111827;
--vad-text-muted:#6B7280;
--vad-primary:   #1B3A2D;
--vad-danger:    #DC2626;
--vad-warning:   #D97706;
--vad-success:   #16A34A;
--vad-radius:    8px;
```

### 6-2. 관리자 레이아웃 구조

```
vad-layout (flex)
├── vad-sidebar (224px 고정)
│   ├── vad-brand (로고)
│   ├── vad-nav (메뉴)
│   └── vad-sidebar-foot (쇼핑몰로 이동)
└── vad-main (flex: 1, padding: 32px)
    └── <RouterView /> → 각 admin 페이지
```

### 6-3. 관리자 공통 컴포넌트 클래스

| 클래스 | 용도 |
|--------|------|
| `.admin-page` | 페이지 래퍼 |
| `.admin-page-title` | 페이지 제목 |
| `.admin-card` | 카드 컨테이너 |
| `.admin-card-title` | 카드 제목 |
| `.admin-table` | 데이터 테이블 |
| `.admin-toolbar` | 검색/버튼 툴바 |
| `.admin-btn` | 기본 버튼 |
| `.admin-btn--primary` | 주요 버튼 (딥 그린) |
| `.admin-btn--danger` | 삭제 버튼 (빨강) |
| `.admin-btn--warning` | 수정 버튼 (노랑) |
| `.admin-btn--ghost` | 보조 버튼 (회색) |
| `.admin-btn--sm` | 소형 버튼 |
| `.admin-modal-overlay` | 모달 오버레이 |
| `.admin-modal` | 모달 컨테이너 |
| `.admin-form-group` | 폼 필드 그룹 |
| `.stats-grid` | 통계 카드 그리드 |
| `.stat-card` | 통계 카드 |

> 관리자 공통 스타일은 `style.css` 하단부에 정의되며, 별도 `scoped` 없이 전역으로 공유됩니다.

---

## 7. 반응형 브레이크포인트

| 이름 | 너비 | 주요 변경사항 |
|------|------|---------------|
| Desktop | > 1100px | 기본 레이아웃 |
| Tablet L | ≤ 1100px | 상품 그리드 4열 → 3열 |
| Tablet | ≤ 1024px | 스플릿 섹션 1열, 상품 3열 |
| Mobile L | ≤ 768px | 컨테이너 패딩 축소, 상품 2열, 마이페이지 1열 |
| Mobile S | ≤ 480px | 상품 1열 |
| Footer | ≤ 900px | 2열 그리드 |
| Footer | ≤ 560px | 1열, 하단 세로 정렬 |

```css
/* 주요 미디어 쿼리 */
@media (max-width: 1100px) { /* 카테고리 3열 */ }
@media (max-width: 1024px) { /* 홈 3열, 스플릿 1열 */ }
@media (max-width: 768px)  { /* 컨테이너 패딩 16px, 2열 */ }
@media (max-width: 480px)  { /* 1열 */ }
```

---

## 8. CSS 작성 규칙

### 8-1. 클래스 네이밍 체계

**쇼핑몰 (사용자 페이지)**
```
.v-{component}                        → 블록
.v-{component}__{element}            → 요소
.v-{component}--{modifier}           → 변형
```
예: `.v-product-card`, `.v-product-card__img-wrap`, `.v-product-card__badge--sale`

**관리자 패널**
```
.admin-{component}                   → 공통 컴포넌트
.vad-{component}                     → 사이드바/레이아웃 전용
```

### 8-2. scoped vs 전역

| 위치 | scoped 여부 | 이유 |
|------|------------|------|
| `style.css` | 전역 | 디자인 토큰, 공통 컴포넌트 |
| `AdminLayout.vue` `<style>` | 전역 | admin 하위 페이지 공유 |
| 각 `.vue` `<style scoped>` | scoped | 페이지 전용 스타일 |

### 8-3. 트랜지션 표준

```css
/* 기본 전환 */
transition: all 0.35s cubic-bezier(0.25, 0.46, 0.45, 0.94);

/* 이미지 줌 */
transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);

/* UI 빠른 반응 (버튼, 링크) */
transition: color 0.2s, border-color 0.2s;
```

### 8-4. 그림자

```css
--shadow-sm: 0 1px 6px rgba(0,0,0,0.07);
--shadow-md: 0 4px 24px rgba(0,0,0,0.10);
--shadow-lg: 0 16px 48px rgba(0,0,0,0.14);
```

### 8-5. 보더 반경

| 영역 | 값 |
|------|-----|
| 쇼핑몰 버튼 / 입력 | 0 (sharp, 럭셔리 미학) |
| 관리자 버튼 | 6px |
| 관리자 카드 | 10px |
| 관리자 모달 | 12px |
| 뱃지 / 상태 | 20px (pill) |

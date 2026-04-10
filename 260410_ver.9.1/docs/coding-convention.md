# Ventalize — 코딩 컨벤션

> 프로젝트: Ventalize 쇼핑몰
> 스택: Spring Boot 3.2 (Java 21) + Vue 3 (Vite)
> 최종 수정: 2026-03

---

## 목차

1. [공통 규칙](#1-공통-규칙)
2. [백엔드 — Java / Spring Boot](#2-백엔드--java--spring-boot)
3. [프론트엔드 — Vue 3](#3-프론트엔드--vue-3)
4. [API 설계 규칙](#4-api-설계-규칙)
5. [Git 커밋 메시지](#5-git-커밋-메시지)

---

## 1. 공통 규칙

### 1-1. 언어 사용
- **변수명 · 함수명 · 클래스명**: 영어 (camelCase / PascalCase)
- **주석 · 커밋 메시지 · 에러 메시지**: 한국어 허용
- **API 응답 필드**: 영어 camelCase

### 1-2. 들여쓰기
| 영역 | 규칙 |
|------|------|
| Java | 스페이스 4칸 |
| Vue / JS / CSS | 스페이스 2칸 |

### 1-3. 파일 길이
- 단일 파일은 **500줄 이하** 권장
- 초과 시 역할별 분리 (Service → 도메인별 분리 등)

---

## 2. 백엔드 — Java / Spring Boot

### 2-1. 패키지 구조

```
com.ventalize.shop
├── config/          # Security, CORS, 초기화 설정
├── controller/      # REST 컨트롤러
├── dto/             # 요청/응답 DTO (도메인별 하위 패키지)
│   ├── item/
│   ├── member/
│   └── ...
├── entity/          # JPA 엔티티
├── repository/      # Spring Data JPA 인터페이스
├── service/         # 비즈니스 로직
├── filter/          # 인증 필터
├── util/            # 유틸리티 (SecurityUtil 등)
└── helper/          # 도우미 클래스
```

### 2-2. 네이밍

| 대상 | 규칙 | 예시 |
|------|------|------|
| 클래스 | PascalCase | `ItemService`, `AdminItemController` |
| 메서드 | camelCase 동사 시작 | `findAll()`, `updateStock()` |
| 변수 | camelCase | `totalSales`, `uploadDir` |
| 상수 | UPPER_SNAKE_CASE | `TOP5`, `MAX_FILE_SIZE` |
| DB 컬럼 | snake_case | `stock_count`, `view_count` |
| DTO (Read) | `도메인Read` | `ItemRead`, `MemberRead` |
| DTO (Request) | `도메인CreateRequest` / `도메인UpdateRequest` | `ItemCreateRequest` |

### 2-3. 엔티티 규칙

```java
@Entity
@Table(name = "items")          // 테이블명은 복수형 snake_case
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;         // PK는 항상 Integer id

    @Column(nullable = false, length = 100)
    private String name;        // 제약조건 명시

    @Column(name = "stock_count")   // DB 컬럼명이 다를 때만 name 지정
    @ColumnDefault("0")
    private Integer stockCount;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;  // 생성일은 항상 포함

    // 엔티티 → DTO 변환 메서드는 엔티티 내부에 선언
    public ItemRead toRead() { ... }
}
```

**규칙 요약:**
- `@Builder.Default`는 기본값 지정 시 사용
- `@CreationTimestamp` — 생성일 자동 관리
- 엔티티에서 직접 DTO 변환: `toRead()` 메서드 패턴 사용
- 직접 업데이트 쿼리보다 **엔티티 조회 후 setter 수정** 방식 선호

### 2-4. Repository 규칙

```java
public interface ItemRepository extends JpaRepository<Item, Integer> {

    // 단순 조회: Spring Data JPA 메서드명 규칙 사용
    List<Item> findAllByOrderByIdAsc();
    List<Item> findAllByCategoryInOrderByIdAsc(List<String> cts);

    // 복잡한 정렬/필터: @Query + Pageable 사용
    @Query("SELECT i FROM Item i ORDER BY COALESCE(i.viewCount, 0) DESC")
    List<Item> findTop5BestItems(Pageable pageable);
}
```

**규칙 요약:**
- 페이징이 필요한 경우 `Pageable` 파라미터 사용
- `NULL` 처리가 필요한 정렬: `@Query + COALESCE`
- 메서드명에 정렬 포함 시 `OrderByFieldAsc/Desc` 명시

### 2-5. Service 규칙

```java
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // 공통 페이지 설정은 상수로 분리
    private static final PageRequest TOP5 = PageRequest.of(0, 5);

    public List<ItemRead> findAll() {
        return itemRepository.findAllByOrderByIdAsc()
                .stream().map(Item::toRead).toList();
    }
}
```

**규칙 요약:**
- 생성자 주입 (`@RequiredArgsConstructor`)
- `stream().map(Entity::toRead).toList()` 패턴으로 DTO 변환
- 비즈니스 로직은 Service에, DB 접근은 Repository에 위치

### 2-6. Controller 규칙

```java
@RestController
@RequestMapping("/v1/api/items")    // 버전 prefix: /v1
@RequiredArgsConstructor
public class ItemController {

    @GetMapping
    public ResponseEntity<?> readAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort) {
        // ...
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<?> doSomething(@PathVariable Integer id) {
        return itemRepository.findById(id)
                .map(item -> ResponseEntity.ok(item.toRead()))
                .orElse(ResponseEntity.notFound().build());
    }
}
```

**규칙 요약:**
- 모든 API는 `/v1/api/` prefix
- 반환 타입: `ResponseEntity<?>`
- 조회 실패: `ResponseEntity.notFound().build()`
- 파라미터 비교는 `"value".equalsIgnoreCase(param)` 사용 (NPE 방지)
- 관리자 전용: `/v1/api/admin/` prefix, 별도 컨트롤러 분리

### 2-7. DTO 규칙

```java
// Read DTO: @Getter @Builder, 불변 필드
@Getter
@Builder
public class ItemRead {
    private final Integer id;
    private final String name;
    // ...
}

// Request DTO: @Getter @Setter, 가변 필드
@Getter
@Setter
public class ItemCreateRequest {
    private String name;
    private Integer price;
}
```

---

## 3. 프론트엔드 — Vue 3

### 3-1. 파일 구조

```
src/
├── views/               # 페이지 단위 컴포넌트
│   ├── Home.vue
│   ├── Category.vue
│   ├── admin/           # 관리자 페이지
│   └── board/           # 게시판 페이지
├── components/          # 공통 레이아웃 컴포넌트
│   ├── Header.vue
│   └── Footer.vue
├── composables/         # 재사용 로직
│   └── useAuth.js
├── router/
│   └── index.js
├── style.css            # 전역 디자인 시스템
└── main.js
```

### 3-2. 네이밍

| 대상 | 규칙 | 예시 |
|------|------|------|
| Vue 파일 | PascalCase | `ProductDetail.vue`, `MyPage.vue` |
| Composable | `use` 접두어 camelCase | `useAuth.js` |
| ref 변수 | camelCase | `cartCount`, `isLoggedIn` |
| CSS 클래스 (쇼핑몰) | BEM 변형 `.v-` prefix | `.v-product-card`, `.v-header__logo` |
| CSS 클래스 (관리자) | `.admin-` prefix | `.admin-card`, `.admin-btn` |
| 이벤트 핸들러 | `handle` 접두어 | `handleSearch`, `handleLogout` |
| fetch 함수 | `fetch` / `load` 접두어 | `fetchCartCount`, `loadProducts` |

### 3-3. Vue 컴포넌트 구조 순서

```vue
<template>
  <!-- 마크업 -->
</template>

<script setup>
// 1. import
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

// 2. composable 사용
const router = useRouter()
const { isLoggedIn } = useAuth()

// 3. reactive 상태
const items = ref([])
const loading = ref(false)

// 4. computed
const sortedItems = computed(() => [...items.value].sort(...))

// 5. 함수 정의
async function fetchItems() { ... }

// 6. watch
watch(isLoggedIn, fetchItems, { immediate: true })

// 7. lifecycle
onMounted(fetchItems)
</script>

<style scoped>
/* 컴포넌트 전용 스타일 */
</style>
```

### 3-4. API 호출 규칙

```js
// ✅ 올바른 패턴
async function fetchItems() {
  loading.value = true
  try {
    const r = await fetch('/v1/api/items', { credentials: 'include' })
    if (r.ok) items.value = await r.json()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// ✅ 인증 필요 요청: credentials: 'include' 필수
const r = await fetch('/v1/api/orders', { credentials: 'include' })

// ✅ POST/PUT: Content-Type 헤더 명시
const r = await fetch('/v1/api/items', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: JSON.stringify(payload)
})
```

**규칙 요약:**
- 모든 인증 필요 요청에 `credentials: 'include'`
- `loading` 상태는 `finally`에서 해제
- 에러 시 `showToast()` 또는 `console.error()` 처리

### 3-5. 라우터 규칙

```js
// 경로 규칙
// - 페이지: /kebab-case
// - 동적 파라미터: /:name, /:id
// - 인증 필요: meta: { requiresAuth: true }
// - 관리자 전용: meta: { requiresAdmin: true }

{ path: '/product/:id',  component: ProductDetail }
{ path: '/mypage',       component: MyPage, meta: { requiresAuth: true } }
{ path: '/admin',        component: AdminLayout, meta: { requiresAdmin: true } }
```

### 3-6. Composable 규칙

```js
// composables/useAuth.js
// - 모듈 레벨 ref: 전역 싱글톤 상태로 동작
// - return: 필요한 상태와 함수만 노출
export function useAuth() {
  return { isLoggedIn, loginId, userName, grade, isAdmin, setLogin, clearLogin }
}
```

### 3-7. HTML 표준 준수

```vue
<!-- ✅ table 구조: thead/tbody 필수 -->
<table>
  <thead>
    <tr><th>...</th></tr>
  </thead>
  <tbody>
    <tr v-for="item in items" :key="item.id">
      <td>...</td>
    </tr>
  </tbody>
</table>

<!-- ❌ tbody 없이 tr 직접 사용 금지 (Vite 경고 발생) -->
<table>
  <tr>...</tr>
</table>
```

---

## 4. API 설계 규칙

### 4-1. URL 패턴

| 동작 | 메서드 | URL 패턴 | 예시 |
|------|--------|----------|------|
| 목록 조회 | GET | `/v1/api/{resource}` | `/v1/api/items` |
| 단건 조회 | GET | `/v1/api/{resource}/{id}` | `/v1/api/items/1` |
| 등록 | POST | `/v1/api/{resource}` | `/v1/api/items` |
| 전체 수정 | PUT | `/v1/api/{resource}/{id}` | `/v1/api/items/1` |
| 부분 수정 | PATCH | `/v1/api/{resource}/{id}/{action}` | `/v1/api/items/1/stock` |
| 삭제 | DELETE | `/v1/api/{resource}/{id}` | `/v1/api/items/1` |
| 파일 업로드 | POST | `/v1/api/{resource}/upload` | `/v1/api/admin/items/upload` |
| 관리자 | * | `/v1/api/admin/{resource}` | `/v1/api/admin/items` |

### 4-2. 응답 형식

```json
// 성공 — 단건
{ "id": 1, "name": "상품명", ... }

// 성공 — 목록
[ { "id": 1, ... }, { "id": 2, ... } ]

// 파일 업로드 성공
{ "imgPath": "/v1/images/uuid.jpg" }

// 에러 — 문자열 본문
"유효하지 않은 요청입니다."
```

### 4-3. HTTP 상태 코드

| 상황 | 코드 |
|------|------|
| 정상 | 200 |
| 생성 | 200 (현 프로젝트 관례) |
| 잘못된 요청 | 400 |
| 미인증 | 401 |
| 권한 없음 | 403 |
| 찾을 수 없음 | 404 |
| 충돌 (이미 존재) | 409 |
| 서버 에러 | 500 |

---

## 5. Git 커밋 메시지

### 5-1. 형식

```
<type>: <한국어 요약>

<선택적 본문>
```

### 5-2. Type 종류

| Type | 사용 상황 |
|------|-----------|
| `feat` | 새 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변경 없는 코드 개선 |
| `style` | CSS/UI 변경 |
| `docs` | 문서 수정 |
| `chore` | 설정, 의존성 등 기타 |

### 5-3. 예시

```
feat: 관리자 상품 등록 시 이미지 파일 직접 첨부 기능 추가
fix: 장바구니 빨간 점 — 라우트 변경 시 카운트 재조회
refactor: brand 필드 노출 제거 (단일 브랜드 운영)
style: 카테고리 타이틀 폰트 축소 및 페이지 간격 통일
```

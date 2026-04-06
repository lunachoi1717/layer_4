<template>
  <header class="v-header" :class="{ 'v-header--scrolled': scrolled }">

    <!-- ── Announcement Bar ── -->
    <div class="v-header__announce">
      <span>Complimentary shipping on orders over ₩100,000 &nbsp;·&nbsp; 신규 회원 첫 구매 15% 할인</span>
    </div>

    <!-- ── Main Header ── -->
    <div class="v-header__main">
      <div class="v-header__inner">

        <!-- Left: empty placeholder for grid balance -->
        <div class="v-header__left"></div>

        <!-- Center: Logo -->
        <RouterLink to="/" class="v-header__logo">
          <span class="v-header__logo-en">VENTALIZE</span>
          <span class="v-header__logo-kr">벙딸리제</span>
        </RouterLink>

        <!-- Right: Auth + Icons -->
        <div class="v-header__right">
          <!-- Auth links -->
          <template v-if="isLoggedIn">
            <div class="v-header__user-group">
              <span class="v-grade-icon" :class="`v-grade-icon--${gradeKey}`" :title="gradeLabel">{{ gradeInitial }}</span>
              <span class="v-header__username">{{ userName || loginId }}</span>
            </div>
            <RouterLink v-if="isAdmin()" to="/admin" class="v-header__link v-header__link--admin">Admin</RouterLink>
            <button class="v-header__link" @click="handleLogout">Sign out</button>
          </template>
          <template v-else>
            <RouterLink to="/login" class="v-header__link">Sign in</RouterLink>
            <RouterLink to="/register" class="v-header__link">Join</RouterLink>
          </template>

          <div class="v-header__icon-divider"></div>

          <!-- Search toggle -->
          <button class="v-header__icon-btn" @click="searchOpen = !searchOpen" aria-label="Search">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
          </button>
          <!-- MyPage -->
          <RouterLink to="/mypage" class="v-header__icon-btn" aria-label="My page">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </RouterLink>
          <!-- Cart -->
          <RouterLink to="/cart" class="v-header__icon-btn" aria-label="Cart">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
              <line x1="3" y1="6" x2="21" y2="6"/>
              <path d="M16 10a4 4 0 0 1-8 0"/>
            </svg>
          </RouterLink>
        </div>
      </div>
    </div>

    <!-- ── Search Bar (slide down) ── -->
    <div class="v-header__search-bar" :class="{ 'v-header__search-bar--open': searchOpen }">
      <div class="v-header__inner">
        <form class="v-search-form" @submit.prevent="handleSearch">
          <input
            ref="searchInput"
            v-model="searchQuery"
            type="search"
            placeholder="스카프, 향수, 가방을 검색하세요…"
            class="v-search-input"
          />
          <button type="submit" class="v-search-submit">Search</button>
          <button type="button" class="v-search-close" @click="searchOpen = false">✕</button>
        </form>
      </div>
    </div>

    <!-- ── Navigation ── -->
    <nav class="v-header__nav">
      <div class="v-header__inner v-header__nav-inner">
        <RouterLink
          v-for="cat in categories"
          :key="cat.name"
          :to="cat.to"
          class="v-nav-link"
          :class="{
            'v-nav-link--sale':  cat.name === 'SALE',
            'v-nav-link--board': cat.name === 'BOARD'
          }"
          active-class="v-nav-link--active"
        >
          {{ cat.label }}
        </RouterLink>
      </div>
    </nav>

  </header>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

const { isLoggedIn, loginId, userName, grade, isAdmin, clearLogin } = useAuth()
const router = useRouter()

const searchOpen  = ref(false)
const searchQuery = ref('')
const searchInput = ref(null)
const scrolled    = ref(false)

// 등급 아이콘 계산
const GRADE_MAP = {
  SAPPHIRE: { initial: 'S', label: 'Sapphire', key: 'sapphire' },
  RUBY:     { initial: 'R', label: 'Ruby',     key: 'ruby'     },
  EMERALD:  { initial: 'E', label: 'Emerald',  key: 'emerald'  },
  GOLD:     { initial: 'G', label: 'Gold',     key: 'gold'     },
  DIAMOND:  { initial: 'D', label: 'Diamond',  key: 'diamond'  },
  // 구 등급 fallback
  BRONZE:   { initial: 'B', label: 'Bronze',   key: 'sapphire' },
  SILVER:   { initial: 'S', label: 'Silver',   key: 'sapphire' },
  VIP:      { initial: 'V', label: 'VIP',      key: 'diamond'  },
}

const gradeKey     = computed(() => GRADE_MAP[grade.value?.toUpperCase()] ? GRADE_MAP[grade.value.toUpperCase()].key : 'sapphire')
const gradeInitial = computed(() => GRADE_MAP[grade.value?.toUpperCase()]?.initial || 'S')
const gradeLabel   = computed(() => GRADE_MAP[grade.value?.toUpperCase()]?.label   || grade.value || 'Sapphire')

watch(searchOpen, async (val) => {
  if (val) { await nextTick(); searchInput.value?.focus() }
})

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ path: '/category/ALL', query: { keyword: searchQuery.value.trim() } })
    searchOpen.value = false
    searchQuery.value = ''
  }
}

async function handleLogout() {
  try { await fetch('/v1/api/account/logout', { method: 'POST', credentials: 'include' }) } catch {}
  clearLogin()
  router.push('/')
}

const categories = [
  { name: 'SCARVES',       label: 'Scarves',       to: '/category/SCARVES' },
  { name: 'READY_TO_WEAR', label: 'Ready to Wear', to: '/category/READY_TO_WEAR' },
  { name: 'PERFUME',       label: 'Perfume',       to: '/category/PERFUME' },
  { name: 'ACC',           label: 'Accessories',   to: '/category/ACC' },
  { name: 'BAG',           label: 'Bags',          to: '/category/BAG' },
  { name: 'SALE',          label: 'Sale',          to: '/category/SALE' },
  { name: 'BOARD',         label: 'Board',         to: '/board' },
]

function onScroll() { scrolled.value = window.scrollY > 40 }
onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
/* ── Header Shell ── */
.v-header {
  position: sticky;
  top: 0;
  z-index: 200;
  background: #fff;
  transition: box-shadow 0.3s ease;
}
.v-header--scrolled { box-shadow: 0 2px 24px rgba(0,0,0,0.07); }

/* ── Announce Bar ── */
.v-header__announce {
  background: #1B3A2D;
  color: #F5F0E8;
  text-align: center;
  font-size: 1.1rem;
  font-family: 'Inter', sans-serif;
  letter-spacing: 0.06em;
  padding: 8px 16px;
}

/* ── Main Header ── */
.v-header__main {
  border-bottom: 1px solid #E8E2D9;
}
.v-header__inner {
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 32px;
}
.v-header__main .v-header__inner {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  height: 72px;
}

/* Left (empty spacer) */
.v-header__left {
  display: flex;
  align-items: center;
}

/* Logo */
.v-header__logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  justify-self: center;
}
.v-header__logo-en {
  font-family: 'Cormorant Garamond', 'Georgia', serif;
  font-size: 1.35rem;
  font-weight: 400;
  letter-spacing: 0.35em;
  color: #111;
  line-height: 1;
}
.v-header__logo-kr {
  font-size: 1.1rem;
  letter-spacing: 0.2em;
  color: #7A7269;
  margin-top: 3px;
  font-family: 'Inter', sans-serif;
  transform: scale(0.65);
  transform-origin: center top;
  display: block;
}

/* Right */
.v-header__right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 14px;
}
.v-header__icon-divider {
  width: 1px;
  height: 14px;
  background: #E8E2D9;
  margin: 0 2px;
}
.v-header__link {
  font-size: 1.1rem;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: #7A7269;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: color 0.2s;
  text-decoration: none;
  white-space: nowrap;
}
.v-header__link:hover { color: #111; }
.v-header__link--admin { color: #B89C6E; font-weight: 600; }

/* User group (grade icon + name) */
.v-header__user-group {
  display: flex;
  align-items: center;
  gap: 6px;
}
.v-header__username {
  font-size: 1.1rem;
  color: #111;
  font-weight: 500;
}

/* Grade icon */
.v-grade-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  font-size: 0.65rem;
  font-weight: 800;
  letter-spacing: 0;
  color: #fff;
  flex-shrink: 0;
  cursor: default;
}
.v-grade-icon--sapphire { background: #1565C0; }
.v-grade-icon--ruby     { background: #B71C1C; }
.v-grade-icon--emerald  { background: #1B5E20; }
.v-grade-icon--gold     { background: #B89C6E; }
.v-grade-icon--diamond  { background: linear-gradient(135deg, #7B68EE 0%, #C0C0C0 50%, #87CEEB 100%); }

.v-header__icon-btn {
  background: none;
  border: none;
  padding: 6px;
  color: #1E1E1E;
  display: flex;
  align-items: center;
  transition: opacity 0.2s;
  text-decoration: none;
}
.v-header__icon-btn:hover { opacity: 0.5; }

/* ── Search Bar ── */
.v-header__search-bar {
  overflow: hidden;
  max-height: 0;
  transition: max-height 0.35s cubic-bezier(0.25, 0.46, 0.45, 0.94),
              border-color 0.3s;
  border-bottom: 1px solid transparent;
}
.v-header__search-bar--open {
  max-height: 80px;
  border-bottom-color: #E8E2D9;
}
.v-search-form {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
}
.v-search-input {
  flex: 1;
  border: none;
  outline: none;
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.1rem;
  color: #111;
  background: transparent;
}
.v-search-input::placeholder { color: #C9B89A; }
.v-search-submit {
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 8px 20px;
  cursor: pointer;
  transition: background 0.25s;
}
.v-search-submit:hover { background: #4A6741; }
.v-search-close {
  background: none;
  border: none;
  color: #7A7269;
  font-size: 1rem;
  padding: 4px 8px;
  cursor: pointer;
  transition: color 0.2s;
}
.v-search-close:hover { color: #111; }

/* ── Nav ── */
.v-header__nav {
  border-bottom: 1px solid #E8E2D9;
}
.v-header__nav-inner {
  display: flex;
  justify-content: center;
  gap: 0;
}
.v-nav-link {
  display: inline-block;
  padding: 13px 22px;
  font-size: 1.1rem;
  font-weight: 500;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #7A7269;
  text-decoration: none;
  border-bottom: 2px solid transparent;
  transition: color 0.25s, border-color 0.25s;
  white-space: nowrap;
}
.v-nav-link:hover       { color: #111; }
.v-nav-link--active     { color: #111; border-bottom-color: #1B3A2D; }
.v-nav-link--sale       { color: #8B2020; }
.v-nav-link--sale:hover { color: #6b1818; border-bottom-color: #8B2020; }
.v-nav-link--board      { color: #4A5568; }
</style>

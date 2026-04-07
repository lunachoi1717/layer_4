<template>
  <div class="v-cat-page">

    <!-- ── Page Header ── -->
    <div class="v-cat-page__header">
      <div class="v-container">
        <nav class="v-breadcrumb">
          <RouterLink to="/" class="v-breadcrumb__link">Home</RouterLink>
          <span class="v-breadcrumb__sep">—</span>
          <span class="v-breadcrumb__current">{{ categoryLabel }}</span>
        </nav>
        <h1 class="v-cat-page__title t-display">{{ categoryLabel }}</h1>
        <p class="v-cat-page__count t-caption">{{ items.length }} pieces</p>
      </div>
    </div>

    <div class="v-container">
      <!-- ── Sort Bar ── -->
      <div class="v-sort-bar">
        <div class="v-sort-bar__options">
          <button
            v-for="opt in sortOptions"
            :key="opt.value"
            class="v-sort-btn"
            :class="{ 'v-sort-btn--active': sortBy === opt.value }"
            @click="sortBy = opt.value; currentPage = 1"
          >
            {{ opt.label }}
          </button>
        </div>
        <p class="v-sort-bar__count t-caption">{{ pagedItems.length }} / {{ sortedItems.length }}</p>
      </div>

      <!-- ── Loading ── -->
      <div v-if="loading" class="v-spinner"></div>

      <!-- ── Product Grid (3 columns, large images) ── -->
      <div v-else-if="pagedItems.length" class="v-cat-grid">
        <RouterLink
          v-for="item in pagedItems"
          :key="item.id"
          :to="`/product/${item.id}`"
          class="v-product-card"
        >
          <div class="v-product-card__img-wrap v-cat-grid__img">
            <img :src="item.imgPath" :alt="item.name" loading="lazy" />
            <span v-if="item.discountPer > 0" class="v-product-card__badge v-product-card__badge--sale">{{ item.discountPer }}%</span>
            <span v-else-if="item.id % 7 === 0" class="v-product-card__badge">New</span>
            <!-- Quick add button -->
            <button
              class="v-cat-grid__quick-add"
              @click.prevent="addToCart(item)"
              :disabled="item.isSoldOut"
            >
              {{ item.isSoldOut ? 'Sold Out' : '+ Add to Bag' }}
            </button>
          </div>
          <div class="v-product-card__info">
            <p class="v-product-card__brand">{{ item.brand }}</p>
            <p class="v-product-card__name">{{ item.name }}</p>
            <div class="v-product-card__price">
              <span v-if="item.discountPer > 0" class="v-product-card__price-original">{{ fmt(item.price) }}</span>
              <span class="v-product-card__price-sale">{{ fmt(item.salePrice) }}</span>
              <span v-if="item.discountPer > 0" class="v-product-card__discount">-{{ item.discountPer }}%</span>
            </div>
          </div>
        </RouterLink>
      </div>

      <!-- ── Empty ── -->
      <div v-else class="v-cat-empty">
        <p class="t-heading" style="color:#C9B89A; font-weight:300">No items found</p>
        <p class="t-caption" style="margin-top:8px">검색 조건을 변경해보세요.</p>
      </div>

      <!-- ── Pagination ── -->
      <div v-if="totalPages > 1" class="v-pagination">
        <button class="v-pagination__btn" :disabled="currentPage === 1" @click="currentPage--">←</button>
        <button
          v-for="p in totalPages"
          :key="p"
          class="v-pagination__btn v-pagination__btn--num"
          :class="{ 'v-pagination__btn--active': p === currentPage }"
          @click="currentPage = p"
        >{{ p }}</button>
        <button class="v-pagination__btn" :disabled="currentPage === totalPages" @click="currentPage++">→</button>
      </div>
    </div>

    <!-- Toast -->
    <div v-if="toastMsg" class="v-toast">{{ toastMsg }}</div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

const route = useRoute()
const { isLoggedIn } = useAuth()

const ITEMS_PER_PAGE = 12
const loading     = ref(false)
const items       = ref([])
const currentPage = ref(1)
const sortBy      = ref('popular')
const toastMsg    = ref('')

const categoryLabels = {
  SCARVES:       'Scarves',
  READY_TO_WEAR: 'Ready to Wear',
  PERFUME:       'Perfume',
  ACC:           'Accessories',
  BAG:           'Bags',
  SALE:          'Sale',
  ALL:           'All Collections',
}

const sortOptions = [
  { value: 'popular',  label: 'Popular' },
  { value: 'new',      label: 'New In' },
  { value: 'low',      label: 'Price ↑' },
  { value: 'high',     label: 'Price ↓' },
  { value: 'discount', label: 'Sale' },
]

const categoryLabel = computed(() => {
  if (route.query.keyword) return `"${route.query.keyword}"`
  return categoryLabels[route.params.name] || route.params.name
})

const sortedItems = computed(() => {
  const arr = [...items.value]
  switch (sortBy.value) {
    case 'popular':  return arr.sort((a, b) => b.viewCount - a.viewCount)
    case 'new':      return arr.sort((a, b) => b.id - a.id)
    case 'low':      return arr.sort((a, b) => a.salePrice - b.salePrice)
    case 'high':     return arr.sort((a, b) => b.salePrice - a.salePrice)
    case 'discount': return arr.sort((a, b) => b.discountPer - a.discountPer)
    default:         return arr
  }
})

const totalPages = computed(() => Math.max(1, Math.ceil(sortedItems.value.length / ITEMS_PER_PAGE)))
const pagedItems = computed(() => {
  const s = (currentPage.value - 1) * ITEMS_PER_PAGE
  return sortedItems.value.slice(s, s + ITEMS_PER_PAGE)
})

const fmt = (p) => p?.toLocaleString('ko-KR') + '원'

function showToast(msg) {
  toastMsg.value = msg
  setTimeout(() => { toastMsg.value = '' }, 2500)
}

async function addToCart(item) {
  if (!isLoggedIn.value) { showToast('로그인 후 이용하실 수 있습니다.'); return }
  if (item.isSoldOut)    { showToast('품절된 상품입니다.'); return }
  try {
    const r = await fetch('/v1/api/carts', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ itemId: item.id })
    })
    showToast(r.ok ? 'Added to your bag.' : r.status === 409 ? 'Already in your bag.' : 'An error occurred.')
  } catch { showToast('Network error.') }
}

async function fetchItems() {
  loading.value = true
  currentPage.value = 1
  items.value = []
  const cat     = route.params.name
  const keyword = route.query.keyword
  try {
    let url = keyword
      ? `/v1/api/items?keyword=${encodeURIComponent(keyword)}`
      : cat && cat !== 'ALL'
        ? `/v1/api/items?category=${cat}`
        : '/v1/api/items'
    const r = await fetch(url)
    items.value = await r.json()
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

watch([() => route.params.name, () => route.query.keyword], fetchItems, { immediate: true })
</script>

<style scoped>
.v-cat-page { min-height: 70vh; padding-bottom: 80px; }

/* Header */
.v-cat-page__header {
  background: #F5F0E8;
  padding: 56px 0 40px;
  margin-bottom: 48px;
  border-bottom: 1px solid #E8E2D9;
}
.v-breadcrumb { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.v-breadcrumb__link { font-size: 0.72rem; color: #7A7269; text-decoration: none; transition: color 0.2s; }
.v-breadcrumb__link:hover { color: #111; }
.v-breadcrumb__sep   { color: #C9B89A; font-size: 0.7rem; }
.v-breadcrumb__current { font-size: 0.72rem; color: #1B3A2D; font-weight: 500; }
.v-cat-page__title { margin-bottom: 8px; }
.v-cat-page__count { color: #7A7269; }

/* Sort bar */
.v-sort-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #E8E2D9;
  margin-bottom: 40px;
}
.v-sort-bar__options { display: flex; gap: 0; }
.v-sort-btn {
  background: none;
  border: none;
  padding: 7px 16px;
  font-size: 0.72rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7A7269;
  cursor: pointer;
  border-bottom: 1px solid transparent;
  transition: color 0.2s, border-color 0.2s;
}
.v-sort-btn:hover { color: #111; }
.v-sort-btn--active { color: #1B3A2D; border-bottom-color: #1B3A2D; }

/* Product grid — 4 columns */
.v-cat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px 20px;
}
.v-cat-grid__img {
  aspect-ratio: 2 / 3;
  position: relative;
}
/* Quick-add button */
.v-cat-grid__quick-add {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  background: rgba(27,58,45,0.9);
  color: #F5F0E8;
  border: none;
  padding: 13px;
  font-size: 0.7rem;
  font-weight: 500;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  cursor: pointer;
  transform: translateY(100%);
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}
.v-cat-grid__img:hover .v-cat-grid__quick-add { transform: translateY(0); }
.v-cat-grid__quick-add:disabled { background: rgba(100,100,100,0.8); cursor: not-allowed; }

/* Empty */
.v-cat-empty { text-align: center; padding: 100px 0; }

/* Pagination */
.v-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  margin-top: 64px;
}
.v-pagination__btn {
  background: none;
  border: 1px solid transparent;
  padding: 8px 14px;
  font-size: 0.8rem;
  color: #7A7269;
  cursor: pointer;
  transition: all 0.2s;
}
.v-pagination__btn:hover:not(:disabled) { color: #111; border-color: #E8E2D9; }
.v-pagination__btn--active { color: #1B3A2D; border-color: #1B3A2D; font-weight: 600; }
.v-pagination__btn:disabled { opacity: 0.3; cursor: not-allowed; }

@media (max-width: 1100px) { .v-cat-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px)  { .v-cat-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 480px)  { .v-cat-grid { grid-template-columns: 1fr; } }
</style>

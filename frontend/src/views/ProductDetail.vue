<template>
  <div v-if="item" class="v-pd-page">

    <!-- Breadcrumb -->
    <div class="v-pd-crumb v-container">
      <RouterLink to="/" class="v-crumb-link">Home</RouterLink>
      <span class="v-crumb-sep">—</span>
      <RouterLink :to="`/category/${item.category}`" class="v-crumb-link">{{ categoryLabel }}</RouterLink>
      <span class="v-crumb-sep">—</span>
      <span class="v-crumb-current">{{ item.name }}</span>
    </div>

    <!-- Main layout: image | info -->
    <div class="v-container">
      <div class="v-pd-layout">

        <!-- Image -->
        <div class="v-pd-media">
          <div class="v-pd-img-wrap">
            <img :src="item.imgPath" :alt="item.name" class="v-pd-img" />
            <span v-if="item.isSoldOut" class="v-pd-badge v-pd-badge--soldout">Sold Out</span>
            <span v-else-if="item.discountPer > 0" class="v-pd-badge v-pd-badge--sale">{{ item.discountPer }}% Off</span>
          </div>
        </div>

        <!-- Info -->
        <div class="v-pd-info">
          <h1 class="v-pd-name t-display">{{ item.name }}</h1>

          <div class="v-pd-price">
            <span v-if="item.discountPer > 0" class="v-pd-price-original">{{ fmt(item.price) }}</span>
            <span class="v-pd-price-sale">{{ fmt(item.salePrice) }}</span>
            <span v-if="item.discountPer > 0" class="v-pd-discount">{{ item.discountPer }}%</span>
          </div>

          <p class="v-pd-stock" :class="{ 'v-pd-stock--low': item.stockCount <= 5 && !item.isSoldOut }">
            <span v-if="item.isSoldOut">품절</span>
            <span v-else-if="item.stockCount <= 5">재고 {{ item.stockCount }}개 남음</span>
            <span v-else>In Stock</span>
          </p>

          <div class="v-pd-divider"></div>

          <!-- Quantity -->
          <div class="v-pd-qty">
            <span class="v-pd-qty-label t-caption">Quantity</span>
            <div class="v-pd-qty-ctrl">
              <button class="v-pd-qty-btn" @click="qty = Math.max(1, qty - 1)">−</button>
              <span class="v-pd-qty-val">{{ qty }}</span>
              <button class="v-pd-qty-btn" @click="qty = Math.min(item.stockCount || 1, qty + 1)">+</button>
            </div>
          </div>

          <!-- Qty total -->
          <div class="v-pd-qty-total">
            <span class="v-pd-qty-total-label">합계</span>
            <span class="v-pd-qty-total-price">{{ fmt(qty * item.salePrice) }}</span>
          </div>

          <!-- Actions -->
          <div class="v-pd-actions">
            <button class="v-btn-bag" @click="addToCart" :disabled="item.isSoldOut">
              {{ item.isSoldOut ? 'Sold Out' : 'Add to Bag' }}
            </button>
            <button class="v-btn-buy" @click="openOrder" :disabled="item.isSoldOut">
              Buy Now
            </button>
          </div>

          <!-- Description snippet -->
          <div v-if="item.description" class="v-pd-desc-snippet t-body">
            {{ item.description }}
          </div>
        </div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="v-pd-tabs-section">
      <div class="v-container">
        <div class="v-pd-tab-header">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="v-pd-tab-btn"
            :class="{ 'v-pd-tab-btn--active': activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
            <span v-if="tab.key === 'review'" class="v-tab-count">({{ reviews.length }})</span>
            <span v-if="tab.key === 'qna'" class="v-tab-count">({{ questions.length }})</span>
          </button>
        </div>

        <!-- Description tab -->
        <div v-if="activeTab === 'desc'" class="v-pd-tab-body">
          <div class="v-pd-desc-full">
            <img :src="item.imgPath" :alt="item.name" class="v-pd-desc-img" />
            <p class="t-body">{{ item.description || '상품 설명이 없습니다.' }}</p>
            <table class="v-pd-spec-table">
              <tbody>
                <tr><td>Category</td><td>{{ categoryLabel }}</td></tr>
                <tr><td>Price</td><td>{{ fmt(item.price) }}</td></tr>
                <tr v-if="item.discountPer > 0"><td>Discount</td><td>{{ item.discountPer }}%</td></tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Reviews tab -->
        <div v-if="activeTab === 'review'" class="v-pd-tab-body">
          <div v-if="isLoggedIn" class="v-review-write">
            <p class="v-review-write-label t-caption">Write a Review</p>
            <div class="v-star-row">
              <button
                v-for="n in 5"
                :key="n"
                class="v-star-btn"
                :class="{ 'v-star-btn--on': n <= reviewForm.rating }"
                @click="reviewForm.rating = n"
              >★</button>
            </div>
            <textarea v-model="reviewForm.content" class="v-textarea" placeholder="Share your experience…" rows="3" />
            <button class="v-btn-submit" @click="submitReview">Submit</button>
          </div>

          <p v-if="reviews.length === 0" class="v-empty t-caption">No reviews yet.</p>
          <div v-for="r in reviews" :key="r.id" class="v-review-item">
            <div class="v-review-meta">
              <span class="v-review-author">{{ r.memberName }}</span>
              <span class="v-review-stars">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
              <span class="v-review-date">{{ fmtDate(r.createdAt) }}</span>
              <button v-if="r.memberId === currentMemberId" class="v-btn-del" @click="deleteReview(r.id)">Delete</button>
            </div>
            <p class="v-review-body t-body">{{ r.content }}</p>
          </div>
        </div>

        <!-- Q&A tab -->
        <div v-if="activeTab === 'qna'" class="v-pd-tab-body">
          <div v-if="isLoggedIn" class="v-review-write">
            <p class="v-review-write-label t-caption">Ask a Question</p>
            <input v-model="qnaForm.title" class="v-input" placeholder="Subject" />
            <textarea v-model="qnaForm.content" class="v-textarea" placeholder="Your question…" rows="3" />
            <label class="v-check-label">
              <input type="checkbox" v-model="qnaForm.isSecret" /> Private question
            </label>
            <button class="v-btn-submit" @click="submitQna">Submit</button>
          </div>

          <p v-if="questions.length === 0" class="v-empty t-caption">No questions yet.</p>
          <div v-for="q in questions" :key="q.id" class="v-qna-item">
            <div class="v-qna-meta">
              <span v-if="q.isSecret" class="v-qna-lock">🔒</span>
              <span class="v-qna-title">{{ q.title }}</span>
              <span class="v-qna-author">{{ q.memberName }}</span>
              <span class="v-qna-date">{{ fmtDate(q.createdAt) }}</span>
              <span class="v-qna-badge" :class="q.isAnswered ? 'v-qna-badge--answered' : 'v-qna-badge--pending'">
                {{ q.isAnswered ? 'Answered' : 'Pending' }}
              </span>
              <button v-if="q.memberId === currentMemberId" class="v-btn-del" @click="deleteQna(q.id)">Delete</button>
            </div>
            <p class="v-qna-content t-body">{{ q.content }}</p>
            <div v-if="q.isAnswered && q.answerContent" class="v-qna-answer">
              <span class="t-caption">Answer</span>
              <p>{{ q.answerContent }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Related -->
    <div v-if="related.length > 0" class="v-pd-related">
      <div class="v-container">
        <p class="v-section-label">You May Also Like</p>
        <div class="v-related-grid">
          <RouterLink
            v-for="p in related"
            :key="p.id"
            :to="`/product/${p.id}`"
            class="v-product-card"
          >
            <div class="v-product-card__img-wrap">
              <img :src="p.imgPath" :alt="p.name" loading="lazy" />
            </div>
            <div class="v-product-card__info">
              <p class="v-product-card__name">{{ p.name }}</p>
              <div class="v-product-card__price">
                <span class="v-product-card__price-sale">{{ fmt(p.salePrice) }}</span>
              </div>
            </div>
          </RouterLink>
        </div>
      </div>
    </div>

  </div>

  <div v-else class="v-pd-loading v-container">
    <div v-if="notFound" class="t-heading" style="color:#C9B89A">Item not found.</div>
    <div v-else class="v-spinner"></div>
  </div>

  <div v-if="toast" class="v-toast">{{ toast }}</div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

const route = useRoute()
const router = useRouter()
const { isLoggedIn } = useAuth()

const item = ref(null)
const related = ref([])
const reviews = ref([])
const questions = ref([])
const activeTab = ref('desc')
const qty = ref(1)
const notFound = ref(false)
const toast = ref('')
const currentMemberId = ref(null)

const tabs = [
  { key: 'desc',   label: 'Details' },
  { key: 'review', label: 'Reviews' },
  { key: 'qna',    label: 'Q&A' },
]

const reviewForm = ref({ rating: 5, content: '' })
const qnaForm    = ref({ title: '', content: '', isSecret: false })

const categoryLabels = {
  SCARVES: 'Scarves', READY_TO_WEAR: 'Ready to Wear',
  PERFUME: 'Perfume', ACC: 'Accessories', BAGS: 'Bags',
}
const categoryLabel = computed(() => categoryLabels[item.value?.category] || item.value?.category || '')

const fmt     = (p) => p?.toLocaleString('ko-KR') + '원'
const fmtDate = (d) => d ? new Date(d).toLocaleDateString('ko-KR') : ''

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2500)
}

async function loadItem(id) {
  try {
    const res = await fetch(`/v1/api/items/${id}`)
    if (!res.ok) { notFound.value = true; return }
    item.value = await res.json()
    loadRelated(id); loadReviews(id); loadQna(id)
  } catch { notFound.value = true }
}

async function loadRelated(id) {
  try { const r = await fetch(`/v1/api/items/${id}/related`); if (r.ok) related.value = await r.json() } catch {}
}
async function loadReviews(id) {
  try { const r = await fetch(`/v1/api/reviews/item/${id}`); if (r.ok) reviews.value = await r.json() } catch {}
}
async function loadQna(id) {
  try { const r = await fetch(`/v1/api/qna/item/${id}`, { credentials: 'include' }); if (r.ok) questions.value = await r.json() } catch {}
}
async function loadCurrentMember() {
  try { const r = await fetch('/v1/api/account/profile', { credentials: 'include' }); if (r.ok) { const d = await r.json(); currentMemberId.value = d.id } } catch {}
}

async function addToCart() {
  if (!isLoggedIn.value) { showToast('로그인이 필요합니다.'); return }
  const res = await fetch('/v1/api/carts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ itemId: item.value.id, qty: qty.value })
  })
  if (res.ok) showToast(`${qty.value}개를 장바구니에 담았습니다.`)
  else showToast('An error occurred.')
}

function openOrder() {
  if (!isLoggedIn.value) { showToast('로그인이 필요합니다.'); return }
  router.push({ path: '/checkout', query: { itemId: item.value.id, qty: qty.value } })
}

async function submitReview() {
  if (!reviewForm.value.content) { showToast('리뷰 내용을 입력해주세요.'); return }
  const res = await fetch('/v1/api/reviews', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ itemId: item.value.id, ...reviewForm.value })
  })
  if (res.ok) { reviewForm.value = { rating: 5, content: '' }; loadReviews(route.params.id); showToast('Review submitted!') }
  else showToast('Failed to submit review.')
}

async function deleteReview(id) {
  if (!confirm('Delete this review?')) return
  const res = await fetch(`/v1/api/reviews/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadReviews(route.params.id); showToast('Deleted.') }
}

async function submitQna() {
  if (!qnaForm.value.title || !qnaForm.value.content) { showToast('제목과 내용을 입력해주세요.'); return }
  const res = await fetch('/v1/api/qna', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ itemId: item.value.id, ...qnaForm.value })
  })
  if (res.ok) { qnaForm.value = { title: '', content: '', isSecret: false }; loadQna(route.params.id); showToast('Question submitted!') }
  else showToast('Failed to submit question.')
}

async function deleteQna(id) {
  if (!confirm('Delete this question?')) return
  const res = await fetch(`/v1/api/qna/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadQna(route.params.id); showToast('Deleted.') }
}

onMounted(() => {
  loadItem(route.params.id)
  if (isLoggedIn.value) loadCurrentMember()
})

watch(() => route.params.id, (id) => { if (id) { item.value = null; loadItem(id) } })
</script>

<style scoped>
/* Breadcrumb */
.v-pd-crumb {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 24px;
  padding-bottom: 24px;
}
.v-crumb-link { font-size: 0.72rem; color: #7A7269; text-decoration: none; transition: color 0.2s; }
.v-crumb-link:hover { color: #111; }
.v-crumb-sep { color: #C9B89A; font-size: 0.7rem; }
.v-crumb-current { font-size: 0.72rem; color: #1B3A2D; font-weight: 500; }

/* Main layout */
.v-pd-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 72px;
  padding-bottom: 80px;
}
@media (max-width: 768px) { .v-pd-layout { grid-template-columns: 1fr; gap: 40px; } }

/* Media */
.v-pd-img-wrap {
  position: relative;
  aspect-ratio: 1 / 1;
  background: #F5F0E8;
  overflow: hidden;
}
.v-pd-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.v-pd-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  font-size: 0.65rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  padding: 5px 12px;
}
.v-pd-badge--soldout { background: #555; color: #fff; }
.v-pd-badge--sale    { background: #8B2020; color: #fff; }

/* Info panel */
.v-pd-brand {
  color: #7A7269;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}
.v-pd-name {
  font-family: 'Cormorant Garamond', 'Georgia', serif;
  font-size: 2rem;
  font-weight: 400;
  color: #111;
  line-height: 1.2;
  margin-bottom: 24px;
}
.v-pd-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}
.v-pd-price-original {
  font-size: 0.9rem;
  color: #C9B89A;
  text-decoration: line-through;
}
.v-pd-price-sale {
  font-size: 1.4rem;
  font-weight: 500;
  color: #111;
}
.v-pd-discount {
  font-size: 0.75rem;
  font-weight: 600;
  color: #8B2020;
  background: rgba(139,32,32,0.08);
  padding: 3px 8px;
}
.v-pd-stock {
  font-size: 0.75rem;
  color: #7A7269;
  margin-bottom: 8px;
}
.v-pd-stock--low { color: #8B2020; }

.v-pd-divider { border: none; border-top: 1px solid #E8E2D9; margin: 24px 0; }

/* Qty */
.v-pd-qty { display: flex; align-items: center; gap: 20px; margin-bottom: 28px; }
.v-pd-qty-label { color: #7A7269; text-transform: uppercase; letter-spacing: 0.08em; }
.v-pd-qty-ctrl { display: flex; align-items: center; gap: 0; border: 1px solid #E8E2D9; }
.v-pd-qty-btn {
  background: none;
  border: none;
  width: 40px;
  height: 40px;
  font-size: 1.1rem;
  color: #111;
  cursor: pointer;
  transition: background 0.2s;
}
.v-pd-qty-btn:hover { background: #F5F0E8; }
.v-pd-qty-val { min-width: 44px; text-align: center; font-size: 0.9rem; }

/* Qty total */
.v-pd-qty-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-top: 1px solid #E8E2D9;
  border-bottom: 1px solid #E8E2D9;
  margin-bottom: 24px;
}
.v-pd-qty-total-label { font-size: 0.72rem; color: #7A7269; text-transform: uppercase; letter-spacing: 0.08em; }
.v-pd-qty-total-price { font-size: 1.1rem; font-weight: 600; color: #111; }

/* Action buttons */
.v-pd-actions { display: flex; gap: 12px; margin-bottom: 32px; }
.v-btn-bag, .v-btn-buy {
  flex: 1;
  padding: 14px 20px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  border: none;
  cursor: pointer;
  transition: background 0.25s, color 0.25s;
}
.v-btn-bag {
  background: #1B3A2D;
  color: #F5F0E8;
}
.v-btn-bag:hover:not(:disabled) { background: #4A6741; }
.v-btn-buy {
  background: transparent;
  color: #1B3A2D;
  border: 1px solid #1B3A2D;
}
.v-btn-buy:hover:not(:disabled) { background: #1B3A2D; color: #F5F0E8; }
.v-btn-bag:disabled, .v-btn-buy:disabled { opacity: 0.4; cursor: not-allowed; }

.v-pd-desc-snippet {
  color: #7A7269;
  line-height: 1.8;
  border-top: 1px solid #E8E2D9;
  padding-top: 24px;
}

/* Tabs */
.v-pd-tabs-section {
  background: #F5F0E8;
  padding: 56px 0;
  margin-top: 0;
}
.v-pd-tab-header {
  display: flex;
  border-bottom: 1px solid #E8E2D9;
  margin-bottom: 40px;
  gap: 0;
}
.v-pd-tab-btn {
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  padding: 12px 28px;
  font-size: 0.72rem;
  font-weight: 500;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #7A7269;
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;
  margin-bottom: -1px;
}
.v-pd-tab-btn:hover { color: #111; }
.v-pd-tab-btn--active { color: #1B3A2D; border-bottom-color: #1B3A2D; }
.v-tab-count { margin-left: 4px; color: #C9B89A; }

.v-pd-tab-body { min-height: 200px; }

/* Description tab */
.v-pd-desc-full { display: flex; flex-direction: column; align-items: center; gap: 32px; }
.v-pd-desc-img { max-width: 480px; width: 100%; }
.v-pd-spec-table { width: 100%; max-width: 480px; border-collapse: collapse; }
.v-pd-spec-table td { padding: 10px 16px; font-size: 0.8rem; border-bottom: 1px solid #E8E2D9; }
.v-pd-spec-table td:first-child { color: #7A7269; font-weight: 500; letter-spacing: 0.06em; width: 40%; }

/* Review / QnA write */
.v-review-write {
  background: #fff;
  border: 1px solid #E8E2D9;
  padding: 28px;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.v-review-write-label { color: #7A7269; text-transform: uppercase; letter-spacing: 0.1em; }
.v-star-row { display: flex; gap: 4px; }
.v-star-btn { background: none; border: none; font-size: 1.4rem; color: #E8E2D9; cursor: pointer; transition: color 0.15s; padding: 0; }
.v-star-btn--on { color: #B89C6E; }
.v-input {
  border: 1px solid #E8E2D9;
  background: transparent;
  padding: 10px 14px;
  font-size: 0.85rem;
  color: #111;
  outline: none;
  transition: border-color 0.2s;
}
.v-input:focus { border-color: #1B3A2D; }
.v-textarea {
  border: 1px solid #E8E2D9;
  background: transparent;
  padding: 12px 14px;
  font-size: 0.85rem;
  color: #111;
  outline: none;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.2s;
}
.v-textarea:focus { border-color: #1B3A2D; }
.v-check-label { display: flex; align-items: center; gap: 8px; font-size: 0.8rem; color: #7A7269; cursor: pointer; }
.v-btn-submit {
  align-self: flex-start;
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 10px 28px;
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.25s;
}
.v-btn-submit:hover { background: #4A6741; }

/* Reviews */
.v-review-item {
  padding: 24px 0;
  border-bottom: 1px solid #E8E2D9;
}
.v-review-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 10px; flex-wrap: wrap; }
.v-review-author { font-size: 0.8rem; font-weight: 500; color: #111; }
.v-review-stars { color: #B89C6E; font-size: 0.85rem; }
.v-review-date { font-size: 0.72rem; color: #C9B89A; }
.v-review-body { color: #555; line-height: 1.7; font-size: 0.9rem; }

/* QnA */
.v-qna-item {
  padding: 24px 0;
  border-bottom: 1px solid #E8E2D9;
}
.v-qna-meta { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; flex-wrap: wrap; }
.v-qna-title { font-size: 0.85rem; font-weight: 500; color: #111; }
.v-qna-author, .v-qna-date { font-size: 0.72rem; color: #C9B89A; }
.v-qna-badge {
  font-size: 0.65rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  padding: 3px 9px;
}
.v-qna-badge--answered { background: rgba(27,58,45,0.1); color: #1B3A2D; }
.v-qna-badge--pending  { background: rgba(139,32,32,0.08); color: #8B2020; }
.v-qna-content { color: #555; line-height: 1.7; font-size: 0.9rem; }
.v-qna-answer {
  margin-top: 14px;
  padding: 14px 18px;
  background: #fff;
  border-left: 3px solid #1B3A2D;
  font-size: 0.85rem;
  color: #555;
}
.v-qna-answer .t-caption { color: #1B3A2D; margin-bottom: 6px; display: block; }

.v-btn-del {
  background: none;
  border: none;
  font-size: 0.7rem;
  color: #C9B89A;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s;
}
.v-btn-del:hover { color: #8B2020; }

.v-empty { text-align: center; padding: 48px 0; color: #C9B89A; }

/* Related */
.v-pd-related {
  padding: 72px 0 80px;
  border-top: 1px solid #E8E2D9;
}
.v-related-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px;
  margin-top: 32px;
}
@media (max-width: 900px) { .v-related-grid { grid-template-columns: repeat(2, 1fr); } }

/* Loading */
.v-pd-loading { padding: 120px 0; text-align: center; }
</style>

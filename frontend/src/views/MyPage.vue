<template>
  <div class="v-mypage">

    <div class="v-mypage-hero">
      <div class="v-container">
        <h1 class="v-mypage-hero-title t-display">My Account</h1>
        <p v-if="profile" class="v-mypage-hero-sub t-caption">{{ profile.name }} · {{ profile.loginId }}</p>
      </div>
    </div>

    <div class="v-container">
      <div class="v-mypage-layout">

        <aside class="v-mypage-sidebar">

          <div class="v-mypage-avatar-wrap">
            <div class="v-mypage-avatar">{{ (profile?.name || 'U')[0] }}</div>
            <div>
              <p class="v-mypage-avatar-name">{{ profile?.name || '…' }}</p>
              <span class="v-grade-badge" :class="`grade-${(profile?.grade || 'SAPPHIRE').toLowerCase()}`">
                {{ profile?.grade || 'SAPPHIRE' }}
              </span>
            </div>
          </div>

          <nav class="v-mypage-nav">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="v-mypage-nav-btn"
              :class="{ 'v-mypage-nav-btn--active': activeTab === tab.key }"
              @click="activeTab = tab.key"
            >{{ tab.label }}</button>
            <button class="v-mypage-nav-btn v-mypage-nav-btn--logout" @click="handleLogout">Sign Out</button>
          </nav>
        </aside>

        <main class="v-mypage-main">

          <!-- 주문 내역 -->
          <section v-if="activeTab === 'orders'">
            <h2 class="v-mypage-section-title">Order History</h2>
            <div v-if="ordersLoading" class="v-spinner" style="margin:40px auto"></div>
            <p v-else-if="orders.length === 0" class="v-mypage-empty t-caption">No orders yet.</p>
            <div v-else class="v-order-list">
              <div v-for="order in orders" :key="order.id" class="v-order-card">

                <div class="v-order-summary" @click="toggleOrder(order.id)">
                  <div class="v-order-summary-left">
                    <span class="v-order-num"># {{ order.orderNumber || order.id }}</span>
                    <span class="v-order-date t-caption">{{ fmtDate(order.createdAt) }}</span>
                    <span class="v-order-status-pill" :class="`v-status--${(order.status || '').toLowerCase()}`">
                      {{ order.statusLabel || order.status }}
                    </span>
                  </div>
                  <div class="v-order-summary-right">
                    <span class="v-order-amount">{{ order.amount?.toLocaleString() }}원</span>
                    <span class="v-order-toggle-icon">{{ expandedOrderId === order.id ? '▲' : '▼' }}</span>
                  </div>
                </div>

                <div v-if="expandedOrderId === order.id" class="v-order-detail">
                  <div v-if="order.items && order.items.length > 0" class="v-order-items">
                    <div v-for="oi in order.items" :key="oi.itemId" class="v-order-item-row">
                      <img v-if="oi.imgPath" :src="oi.imgPath" :alt="oi.itemName" class="v-order-item-thumb" />
                      <div class="v-order-item-info">
                        <span class="v-order-item-name">{{ oi.itemName }}</span>
                        <span class="v-order-item-qty">수량 {{ oi.quantity }}개</span>
                      </div>
                      <span class="v-order-item-subtotal">{{ (oi.subtotal || 0).toLocaleString() }}원</span>
                      <!-- 배송완료된 주문의 상품에만 리뷰 남기기 버튼 표시 -->
                      <button
                        v-if="order.status === 'DELIVERED' && !hasReview(order.id, oi.itemId)"
                        class="v-btn-review"
                        @click.stop="openReviewModal(order.id, oi.itemId, oi.itemName)"
                      >리뷰 남기기</button>
                      <span
                        v-else-if="order.status === 'DELIVERED' && hasReview(order.id, oi.itemId)"
                        class="v-review-done-badge"
                      >리뷰 작성완료</span>
                    </div>
                  </div>
                  <div class="v-order-meta-rows">
                    <div class="v-order-row"><span>수령인</span><span>{{ order.name }}</span></div>
                    <div class="v-order-row"><span>배송지</span><span>{{ order.address }}</span></div>
                    <div class="v-order-row"><span>결제수단</span><span>{{ order.payment }}</span></div>
                  </div>
                  <div v-if="['PENDING_PAYMENT','PAID'].includes(order.status)" class="v-order-card-foot">
                    <button class="v-btn-cancel" @click.stop="cancelOrder(order.id)">Cancel Order</button>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 내 리뷰 목록 -->
          <section v-if="activeTab === 'reviews'">
            <h2 class="v-mypage-section-title">My Reviews</h2>
            <p v-if="reviews.length === 0" class="v-mypage-empty t-caption">No reviews written yet.</p>
            <div v-else class="v-review-list">
              <div v-for="r in reviews" :key="r.id" class="v-review-item">
                <div class="v-review-item-product">
                  <img v-if="r.imgPath" :src="r.imgPath" :alt="r.itemName" class="v-review-item-thumb" />
                  <span class="v-review-item-name">{{ r.itemName || '(삭제된 상품)' }}</span>
                </div>
                <div class="v-review-meta">
                  <span class="v-review-stars">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
                  <span class="v-review-date t-caption">{{ fmtDate(r.createdAt) }}</span>
                  <button class="v-btn-del" @click="deleteReview(r.id)">Delete</button>
                </div>
                <p class="v-review-body">{{ r.content }}</p>
              </div>
            </div>
          </section>

          <section v-if="activeTab === 'qna'">
            <h2 class="v-mypage-section-title">My Q&A</h2>
            <p v-if="questions.length === 0" class="v-mypage-empty t-caption">No questions written yet.</p>
            <div v-else class="v-qna-list">
              <div v-for="q in questions" :key="q.id" class="v-qna-item">
                <div class="v-qna-meta">
                  <span class="v-qna-title">{{ q.title }}</span>
                  <span class="v-qna-badge" :class="q.isAnswered ? 'v-qna-badge--answered' : 'v-qna-badge--pending'">
                    {{ q.isAnswered ? 'Answered' : 'Pending' }}
                  </span>
                  <span class="v-qna-date t-caption">{{ fmtDate(q.createdAt) }}</span>
                  <button class="v-btn-del" @click="deleteQna(q.id)">Delete</button>
                </div>
                <p class="v-qna-body">{{ q.content }}</p>
                <div v-if="q.isAnswered && q.answerContent" class="v-qna-answer">
                  <span class="t-caption" style="color:#1B3A2D;display:block;margin-bottom:6px">Answer</span>
                  {{ q.answerContent }}
                </div>
              </div>
            </div>
          </section>

          <section v-if="activeTab === 'profile'">
            <h2 class="v-mypage-section-title">Edit Profile</h2>
            <form class="v-profile-form" @submit.prevent="updateProfile">
              <div class="v-field">
                <label class="v-field-label">Name</label>
                <input v-model="profileForm.name" class="v-field-input" type="text" />
              </div>
              <div class="v-field">
                <label class="v-field-label">Phone</label>
                <input v-model="profileForm.phone" class="v-field-input" type="tel" placeholder="010-0000-0000" />
              </div>
              <div class="v-field">
                <label class="v-field-label">Address</label>
                <input v-model="profileForm.address" class="v-field-input" type="text" />
              </div>
              <div class="v-profile-divider"></div>
              <p class="v-field-section-label t-caption">Change Password</p>
              <div class="v-field">
                <label class="v-field-label">Current Password</label>
                <input v-model="profileForm.currentPw" class="v-field-input" type="password" />
              </div>
              <div class="v-field">
                <label class="v-field-label">New Password</label>
                <input v-model="profileForm.newPw" class="v-field-input" type="password" />
              </div>
              <p v-if="profileMsg" class="v-profile-msg" :class="profileMsgType === 'error' ? 'v-profile-msg--error' : 'v-profile-msg--success'">{{ profileMsg }}</p>
              <button type="submit" class="v-btn-save">Save Changes</button>
            </form>
          </section>

          <section v-if="activeTab === 'coupons'">
            <h2 class="v-mypage-section-title">My Coupons</h2>
            <p v-if="coupons.length === 0" class="v-mypage-empty t-caption">사용 가능한 쿠폰이 없습니다.</p>
            <div v-else class="v-coupon-list">
              <div v-for="c in coupons" :key="c.id" class="v-coupon-card">
                <div class="v-coupon-card-left">
                  <p class="v-coupon-name">{{ c.name }}</p>
                  <code class="v-coupon-code">{{ c.code }}</code>
                </div>
                <div class="v-coupon-card-right">
                  <p class="v-coupon-discount">
                    <span v-if="c.discountType === 'FIXED'">{{ c.discountValue?.toLocaleString() }}원 할인</span>
                    <span v-else>{{ c.discountValue }}% 할인</span>
                  </p>
                  <p class="v-coupon-validity t-caption">~ {{ c.validTo }}</p>
                  <p v-if="c.minOrderAmount" class="v-coupon-min t-caption">{{ c.minOrderAmount?.toLocaleString() }}원 이상</p>
                  <span v-if="c.targetGrade" class="grade-badge" :class="`grade-${c.targetGrade.toLowerCase()}`">{{ c.targetGrade }}</span>
                </div>
              </div>
            </div>
          </section>

          <section v-if="activeTab === 'withdraw'">
            <h2 class="v-mypage-section-title">Delete Account</h2>
            <div class="v-withdraw-box">
              <p class="v-withdraw-warn">탈퇴 후에는 계정 복구가 불가능합니다. 신중하게 결정해주세요.</p>
              <div class="v-field" style="max-width:360px">
                <label class="v-field-label">Password Confirmation</label>
                <input v-model="withdrawPw" class="v-field-input" type="password" placeholder="Current password" />
              </div>
              <p v-if="withdrawMsg" class="v-profile-msg v-profile-msg--error">{{ withdrawMsg }}</p>
              <button class="v-btn-withdraw" @click="handleWithdraw">Delete Account</button>
            </div>
          </section>

        </main>
      </div>
    </div>

    <!-- 리뷰 작성 모달 -->
    <div v-if="reviewModal.show" class="v-modal-overlay" @click.self="closeReviewModal">
      <div class="v-modal">
        <p class="v-modal-title">리뷰 남기기</p>
        <p class="v-modal-item-name">{{ reviewModal.itemName }}</p>
        <div class="v-star-row">
          <button
            v-for="n in 5"
            :key="n"
            class="v-star-btn"
            :class="{ 'v-star-btn--on': n <= reviewModal.rating }"
            @click="reviewModal.rating = n"
          >★</button>
        </div>
        <textarea
          v-model="reviewModal.content"
          class="v-modal-textarea"
          placeholder="구매하신 상품은 어떠셨나요?"
          rows="4"
        />
        <p v-if="reviewModal.error" class="v-modal-error">{{ reviewModal.error }}</p>
        <div class="v-modal-actions">
          <button class="v-btn-modal-cancel" @click="closeReviewModal">취소</button>
          <button class="v-btn-modal-submit" @click="submitReview">등록</button>
        </div>
      </div>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

const router = useRouter()
const { clearLogin } = useAuth()

const activeTab = ref('orders')
const tabs = [
  { key: 'orders',   label: 'Order History' },
  { key: 'reviews',  label: 'My Reviews' },
  { key: 'qna',      label: 'My Q&A' },
  { key: 'coupons',  label: 'My Coupons' },
  { key: 'profile',  label: 'Edit Profile' },
  { key: 'withdraw', label: 'Delete Account' },
]

const profile      = ref(null)
const orders       = ref([])
const reviews      = ref([])
const questions    = ref([])
const coupons      = ref([])
const ordersLoading = ref(false)
const expandedOrderId = ref(null)

function toggleOrder(id) {
  expandedOrderId.value = expandedOrderId.value === id ? null : id
}
const profileForm  = ref({ name: '', phone: '', address: '', currentPw: '', newPw: '' })
const profileMsg   = ref('')
const profileMsgType = ref('success')
const withdrawPw   = ref('')
const withdrawMsg  = ref('')
const toast        = ref('')

// 리뷰 작성 모달 상태
const reviewModal = ref({
  show: false,
  orderId: null,
  itemId: null,
  itemName: '',
  rating: 5,
  content: '',
  error: '',
})

function showToast(msg) { toast.value = msg; setTimeout(() => { toast.value = '' }, 2500) }
function fmtDate(dt) { return dt ? new Date(dt).toLocaleDateString('ko-KR') : '' }

// 특정 주문+상품에 대해 이미 리뷰를 작성했는지 확인
function hasReview(orderId, itemId) {
  return reviews.value.some(r => r.orderId === orderId && r.itemId === itemId)
}

function openReviewModal(orderId, itemId, itemName) {
  reviewModal.value = {
    show: true,
    orderId,
    itemId,
    itemName,
    rating: 5,
    content: '',
    error: '',
  }
}

function closeReviewModal() {
  reviewModal.value.show = false
}

async function submitReview() {
  if (!reviewModal.value.content.trim()) {
    reviewModal.value.error = '리뷰 내용을 입력해주세요.'
    return
  }
  const res = await fetch('/v1/api/reviews', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({
      orderId: reviewModal.value.orderId,
      itemId:  reviewModal.value.itemId,
      rating:  reviewModal.value.rating,
      content: reviewModal.value.content,
    }),
  })
  if (res.ok) {
    closeReviewModal()
    loadReviews()
    showToast('리뷰가 등록되었습니다.')
  } else {
    const msg = await res.text()
    reviewModal.value.error = msg || '리뷰 등록에 실패했습니다.'
  }
}

async function loadProfile() {
  try {
    const res = await fetch('/v1/api/account/profile', { credentials: 'include' })
    if (res.ok) {
      profile.value = await res.json()
      profileForm.value.name    = profile.value.name    || ''
      profileForm.value.phone   = profile.value.phone   || ''
      profileForm.value.address = profile.value.address || ''
    }
  } catch {}
}

async function loadOrders() {
  ordersLoading.value = true
  try {
    const res = await fetch('/v1/api/orders', { credentials: 'include' })
    if (res.ok) orders.value = await res.json()
  } catch {} finally { ordersLoading.value = false }
}

async function loadReviews() {
  try { const r = await fetch('/v1/api/reviews/my', { credentials: 'include' }); if (r.ok) reviews.value = await r.json() } catch {}
}

async function loadQna() {
  try { const r = await fetch('/v1/api/qna/my', { credentials: 'include' }); if (r.ok) questions.value = await r.json() } catch {}
}

async function loadCoupons() {
  try { const r = await fetch('/v1/api/coupons/my', { credentials: 'include' }); if (r.ok) coupons.value = await r.json() } catch {}
}

async function cancelOrder(id) {
  if (!confirm('주문을 취소하시겠습니까?')) return
  const res = await fetch(`/v1/api/orders/${id}/cancel`, { method: 'PATCH', credentials: 'include' })
  if (res.ok) { loadOrders(); showToast('Order cancelled.') }
  else showToast('Failed to cancel order.')
}

async function deleteReview(id) {
  if (!confirm('Delete this review?')) return
  const res = await fetch(`/v1/api/reviews/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadReviews(); showToast('Deleted.') }
}

async function deleteQna(id) {
  if (!confirm('Delete this question?')) return
  const res = await fetch(`/v1/api/qna/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadQna(); showToast('Deleted.') }
}

async function updateProfile() {
  profileMsg.value = ''
  const body = {}
  if (profileForm.value.name)    body.name    = profileForm.value.name
  if (profileForm.value.phone)   body.phone   = profileForm.value.phone
  if (profileForm.value.address) body.address = profileForm.value.address
  if (profileForm.value.newPw) {
    body.currentPw = profileForm.value.currentPw
    body.newPw     = profileForm.value.newPw
  }
  const res = await fetch('/v1/api/account/profile', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(body)
  })
  if (res.ok) {
    profileMsg.value = 'Saved successfully.'
    profileMsgType.value = 'success'
    loadProfile()
    profileForm.value.currentPw = ''
    profileForm.value.newPw = ''
  } else {
    profileMsg.value = (await res.text()) || 'Failed to save.'
    profileMsgType.value = 'error'
  }
}

async function handleWithdraw() {
  withdrawMsg.value = ''
  if (!withdrawPw.value) { withdrawMsg.value = '비밀번호를 입력해주세요.'; return }
  if (!confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return
  const res = await fetch(`/v1/api/account/withdraw?password=${encodeURIComponent(withdrawPw.value)}`, {
    method: 'DELETE', credentials: 'include'
  })
  if (res.ok) { clearLogin(); router.push('/') }
  else withdrawMsg.value = (await res.text()) || '탈퇴 처리 중 오류가 발생했습니다.'
}

async function handleLogout() {
  try { await fetch('/v1/api/account/logout', { method: 'POST', credentials: 'include' }) } catch {}
  clearLogin()
  router.push('/')
}

onMounted(() => {
  loadProfile()
  loadOrders()
  loadReviews()
  loadQna()
  loadCoupons()
})
</script>

<style scoped>
.v-mypage { min-height: 80vh; padding-bottom: 80px; }

.v-mypage-hero {
  background: #F5F0E8;
  padding: 56px 0 40px;
  margin-bottom: 48px;
  border-bottom: 1px solid #E8E2D9;
}
.v-mypage-hero-title {
  font-family: var(--font-serif);
  font-size: clamp(1.4rem, 3vw, 2rem);
  font-weight: 400;
  color: #111;
  margin-bottom: 8px;
}
.v-mypage-hero-sub { color: #7A7269; }

.v-mypage-layout {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 32px;
  align-items: start;
}
@media (max-width: 768px) { .v-mypage-layout { grid-template-columns: 1fr; } }

.v-mypage-sidebar { position: sticky; top: 100px; }

.v-mypage-avatar-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 28px;
  border-bottom: 1px solid #E8E2D9;
  margin-bottom: 24px;
}
.v-mypage-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #1B3A2D;
  color: #F5F0E8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-serif);
  font-size: 1.3rem;
  flex-shrink: 0;
}
.v-mypage-avatar-name { font-size: 0.85rem; font-weight: 500; color: #111; margin-bottom: 4px; }

.v-grade-badge {
  font-size: 0.6rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  padding: 3px 8px;
}
.v-grade--bronze  { background: rgba(139,90,43,0.1);  color: #8B5A2B; }
.v-grade--silver  { background: rgba(100,100,120,0.1); color: #6C6C84; }
.v-grade--gold    { background: rgba(184,156,110,0.15); color: #B89C6E; }
.v-grade--vip     { background: rgba(27,58,45,0.1);   color: #1B3A2D; }

.v-mypage-nav { display: flex; flex-direction: column; gap: 0; }
.v-mypage-nav-btn {
  background: none;
  border: none;
  text-align: left;
  padding: 12px 0;
  font-size: 0.88rem;
  font-weight: 400;
  letter-spacing: 0.04em;
  color: #7A7269;
  cursor: pointer;
  border-bottom: 1px solid transparent;
  transition: color 0.2s;
}
.v-mypage-nav-btn:hover { color: #111; }
.v-mypage-nav-btn--active { color: #1B3A2D; font-weight: 600; border-bottom-color: transparent; }
.v-mypage-nav-btn--logout { color: #C9B89A; margin-top: 16px; }
.v-mypage-nav-btn--logout:hover { color: #8B2020; }

.v-mypage-section-title {
  font-family: var(--font-serif);
  font-size: 1.6rem;
  font-weight: 400;
  color: #111;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #E8E2D9;
}
.v-mypage-empty { color: #C9B89A; padding: 40px 0; }

.v-order-list { display: flex; flex-direction: column; gap: 12px; }
.v-order-card { border: 1px solid #E8E2D9; }
.v-order-summary {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 24px; cursor: pointer; gap: 16px;
  transition: background 0.15s;
}
.v-order-summary:hover { background: #FAFAF8; }
.v-order-summary-left { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
.v-order-summary-right { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.v-order-num { font-size: 0.82rem; font-weight: 600; color: #111; }
.v-order-date { color: #C9B89A; font-size: 0.78rem; }
.v-order-status-pill {
  font-size: 0.65rem; font-weight: 700; letter-spacing: 0.1em;
  text-transform: uppercase; padding: 4px 10px;
}
.v-status--pending_payment { background: rgba(139,32,32,0.08);  color: #8B2020; }
.v-status--paid            { background: rgba(27,58,45,0.1);    color: #1B3A2D; }
.v-status--shipping        { background: rgba(74,103,65,0.1);   color: #4A6741; }
.v-status--delivered       { background: rgba(27,58,45,0.15);   color: #1B3A2D; }
.v-status--cancelled       { background: rgba(100,100,100,0.08);color: #888; }
.v-order-amount { font-size: 0.9rem; font-weight: 600; color: #111; }
.v-order-toggle-icon { font-size: 0.7rem; color: #C9B89A; }

.v-order-detail { border-top: 1px solid #E8E2D9; padding: 20px 24px; }
.v-order-items { margin-bottom: 16px; border-bottom: 1px solid #E8E2D9; padding-bottom: 16px; }
.v-order-item-row { display: flex; align-items: center; gap: 12px; padding: 8px 0; font-size: 0.82rem; }
.v-order-item-thumb { width: 48px; height: 48px; object-fit: cover; border: 1px solid #E8E2D9; flex-shrink: 0; }
.v-order-item-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.v-order-item-name { font-weight: 500; color: #111; }
.v-order-item-qty { color: #7A7269; font-size: 0.75rem; }
.v-order-item-subtotal { font-weight: 600; color: #111; white-space: nowrap; }

.v-btn-review {
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 6px 14px;
  font-size: 0.68rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: background 0.2s;
}
.v-btn-review:hover { background: #4A6741; }

.v-review-done-badge {
  font-size: 0.65rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  color: #7A7269;
  background: #F5F0E8;
  border: 1px solid #E8E2D9;
  padding: 5px 10px;
  white-space: nowrap;
  flex-shrink: 0;
}

.v-order-meta-rows { display: flex; flex-direction: column; gap: 8px; }
.v-order-row { display: flex; justify-content: space-between; font-size: 0.82rem; color: #555; }
.v-order-card-foot { margin-top: 16px; padding-top: 16px; border-top: 1px solid #E8E2D9; }
.v-btn-cancel {
  background: none; border: 1px solid #E8E2D9; padding: 8px 20px;
  font-size: 0.7rem; font-weight: 500; letter-spacing: 0.08em; text-transform: uppercase;
  color: #7A7269; cursor: pointer; transition: all 0.2s;
}
.v-btn-cancel:hover { border-color: #8B2020; color: #8B2020; }

/* My Reviews */
.v-review-list { display: flex; flex-direction: column; }
.v-review-item { padding: 20px 0; border-bottom: 1px solid #E8E2D9; }
.v-review-item-product {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.v-review-item-thumb {
  width: 44px;
  height: 44px;
  object-fit: cover;
  border: 1px solid #E8E2D9;
  flex-shrink: 0;
}
.v-review-item-name {
  font-size: 0.82rem;
  font-weight: 600;
  color: #111;
}
.v-review-meta { display: flex; align-items: center; gap: 14px; margin-bottom: 10px; }
.v-review-stars { color: #B89C6E; font-size: 0.85rem; }
.v-review-date { color: #C9B89A; }
.v-review-body { color: #555; font-size: 0.9rem; line-height: 1.7; }

.v-qna-list { display: flex; flex-direction: column; }
.v-qna-item { padding: 20px 0; border-bottom: 1px solid #E8E2D9; }
.v-qna-meta { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; flex-wrap: wrap; }
.v-qna-title { font-size: 0.85rem; font-weight: 500; color: #111; }
.v-qna-badge {
  font-size: 0.62rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  padding: 3px 8px;
}
.v-qna-badge--answered { background: rgba(27,58,45,0.1);  color: #1B3A2D; }
.v-qna-badge--pending  { background: rgba(139,32,32,0.08); color: #8B2020; }
.v-qna-date { color: #C9B89A; }
.v-qna-body { color: #555; font-size: 0.9rem; line-height: 1.7; }
.v-qna-answer {
  margin-top: 12px;
  padding: 14px 18px;
  background: #F5F0E8;
  border-left: 3px solid #1B3A2D;
  font-size: 0.85rem;
  color: #555;
}

.v-btn-del {
  background: none; border: none; font-size: 0.7rem; color: #C9B89A;
  cursor: pointer; padding: 0; transition: color 0.2s; margin-left: auto;
}
.v-btn-del:hover { color: #8B2020; }

.v-profile-form { display: flex; flex-direction: column; gap: 20px; max-width: 480px; }
.v-field { display: flex; flex-direction: column; gap: 8px; }
.v-field-label {
  font-size: 0.68rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #7A7269;
}
.v-field-input {
  border: none;
  border-bottom: 1px solid #E8E2D9;
  padding: 10px 0;
  font-size: 0.95rem;
  color: #111;
  background: transparent;
  outline: none;
  transition: border-color 0.25s;
  font-family: inherit;
}
.v-field-input:focus { border-bottom-color: #1B3A2D; }
.v-profile-divider { border: none; border-top: 1px solid #E8E2D9; margin: 8px 0; }
.v-field-section-label { color: #7A7269; text-transform: uppercase; letter-spacing: 0.12em; }
.v-profile-msg { font-size: 0.78rem; padding: 10px 14px; }
.v-profile-msg--success { background: rgba(27,58,45,0.08);  color: #1B3A2D; }
.v-profile-msg--error   { background: rgba(139,32,32,0.06); color: #8B2020; }
.v-btn-save {
  align-self: flex-start;
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 14px 36px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.25s;
}
.v-btn-save:hover { background: #4A6741; }

.v-coupon-list { display: flex; flex-direction: column; gap: 12px; }
.v-coupon-card {
  display: flex; justify-content: space-between; align-items: flex-start;
  border: 1px solid #E8E2D9; padding: 20px 24px;
  border-left: 4px solid #B89C6E;
}
.v-coupon-card-left { flex: 1; }
.v-coupon-name { font-size: 0.9rem; font-weight: 600; color: #111; margin-bottom: 6px; }
.v-coupon-code {
  font-family: 'Courier New', monospace; font-size: 0.78rem; font-weight: 700;
  background: #F5F0E8; color: #1B3A2D; padding: 3px 8px; border-radius: 3px;
  letter-spacing: 0.06em;
}
.v-coupon-card-right { text-align: right; flex-shrink: 0; margin-left: 16px; }
.v-coupon-discount { font-size: 1rem; font-weight: 700; color: #8B2020; margin-bottom: 4px; }
.v-coupon-validity { color: #C9B89A; margin-bottom: 4px; }
.v-coupon-min { color: #C9B89A; margin-bottom: 6px; }

.v-withdraw-box { display: flex; flex-direction: column; gap: 20px; max-width: 480px; }
.v-withdraw-warn {
  font-size: 0.85rem;
  color: #8B2020;
  background: rgba(139,32,32,0.06);
  padding: 16px 20px;
  line-height: 1.6;
}
.v-btn-withdraw {
  align-self: flex-start;
  background: #8B2020;
  color: #fff;
  border: none;
  padding: 12px 32px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.25s;
}
.v-btn-withdraw:hover { background: #6b1818; }

/* 리뷰 모달 */
.v-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}
.v-modal {
  background: #fff;
  padding: 36px;
  width: 100%;
  max-width: 480px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.v-modal-title {
  font-family: var(--font-serif);
  font-size: 1.2rem;
  font-weight: 400;
  color: #111;
  margin-bottom: 4px;
}
.v-modal-item-name {
  font-size: 0.82rem;
  font-weight: 600;
  color: #1B3A2D;
  padding-bottom: 14px;
  border-bottom: 1px solid #E8E2D9;
}
.v-star-row { display: flex; gap: 4px; }
.v-star-btn {
  background: none; border: none; font-size: 1.6rem;
  color: #E8E2D9; cursor: pointer; transition: color 0.15s; padding: 0;
}
.v-star-btn--on { color: #B89C6E; }
.v-modal-textarea {
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
.v-modal-textarea:focus { border-color: #1B3A2D; }
.v-modal-error { font-size: 0.78rem; color: #8B2020; padding: 8px 12px; background: rgba(139,32,32,0.05); }
.v-modal-actions { display: flex; gap: 10px; justify-content: flex-end; }
.v-btn-modal-cancel {
  background: none;
  border: 1px solid #E8E2D9;
  padding: 10px 24px;
  font-size: 0.7rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  color: #7A7269;
  cursor: pointer;
  transition: all 0.2s;
}
.v-btn-modal-cancel:hover { border-color: #7A7269; color: #111; }
.v-btn-modal-submit {
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 10px 28px;
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.25s;
}
.v-btn-modal-submit:hover { background: #4A6741; }
</style>

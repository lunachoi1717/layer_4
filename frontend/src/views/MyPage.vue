<template>
  <div class="v-mypage">

    <!-- Page header -->
    <div class="v-mypage-hero">
      <div class="v-container">
        <h1 class="v-mypage-hero-title t-display">My Account</h1>
        <p v-if="profile" class="v-mypage-hero-sub t-caption">{{ profile.name }} · {{ profile.loginId }}</p>
      </div>
    </div>

    <div class="v-container">
      <div class="v-mypage-layout">

        <!-- Sidebar -->
        <aside class="v-mypage-sidebar">
          <!-- Avatar -->
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

        <!-- Main -->
        <main class="v-mypage-main">

          <!-- Orders -->
          <section v-if="activeTab === 'orders'">
            <h2 class="v-mypage-section-title">Order History</h2>
            <div v-if="ordersLoading" class="v-spinner" style="margin:40px auto"></div>
            <p v-else-if="orders.length === 0" class="v-mypage-empty t-caption">No orders yet.</p>
            <div v-else class="v-order-list">
              <div v-for="order in orders" :key="order.id" class="v-order-card">
                <div class="v-order-card-head">
                  <span class="v-order-id t-caption"># {{ order.id }}</span>
                  <span class="v-order-date t-caption">{{ fmtDate(order.createdAt) }}</span>
                  <span class="v-order-status-pill" :class="`v-status--${(order.status || '').toLowerCase()}`">
                    {{ order.statusLabel || order.status }}
                  </span>
                </div>
                <div class="v-order-card-body">
                  <!-- 주문 상품 상세 -->
                  <div v-if="order.items && order.items.length > 0" class="v-order-items">
                    <div v-for="oi in order.items" :key="oi.itemId" class="v-order-item-row">
                      <img v-if="oi.imgPath" :src="oi.imgPath" :alt="oi.itemName" class="v-order-item-thumb" />
                      <div class="v-order-item-info">
                        <span class="v-order-item-name">{{ oi.itemName }}</span>
                        <span class="v-order-item-qty">수량 {{ oi.quantity }}개</span>
                      </div>
                      <span class="v-order-item-subtotal">{{ (oi.subtotal || 0).toLocaleString() }}원</span>
                    </div>
                  </div>
                  <div class="v-order-row">
                    <span>수령인</span><span>{{ order.name }}</span>
                  </div>
                  <div class="v-order-row">
                    <span>결제금액</span>
                    <span class="v-order-amount">{{ order.amount?.toLocaleString() }}원</span>
                  </div>
                  <div class="v-order-row">
                    <span>결제수단</span><span>{{ order.payment }}</span>
                  </div>
                </div>
                <div class="v-order-card-foot" v-if="['PENDING_PAYMENT','PAID'].includes(order.status)">
                  <button class="v-btn-cancel" @click="cancelOrder(order.id)">Cancel Order</button>
                </div>
              </div>
            </div>
          </section>

          <!-- Reviews -->
          <section v-if="activeTab === 'reviews'">
            <h2 class="v-mypage-section-title">My Reviews</h2>
            <p v-if="reviews.length === 0" class="v-mypage-empty t-caption">No reviews written yet.</p>
            <div v-else class="v-review-list">
              <div v-for="r in reviews" :key="r.id" class="v-review-item">
                <div class="v-review-meta">
                  <span class="v-review-stars">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
                  <span class="v-review-date t-caption">{{ fmtDate(r.createdAt) }}</span>
                  <button class="v-btn-del" @click="deleteReview(r.id)">Delete</button>
                </div>
                <p class="v-review-body">{{ r.content }}</p>
              </div>
            </div>
          </section>

          <!-- Q&A -->
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

          <!-- Profile -->
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

          <!-- Coupons -->
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

          <!-- Withdraw -->
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
const profileForm  = ref({ name: '', phone: '', address: '', currentPw: '', newPw: '' })
const profileMsg   = ref('')
const profileMsgType = ref('success')
const withdrawPw   = ref('')
const withdrawMsg  = ref('')
const toast        = ref('')

function showToast(msg) { toast.value = msg; setTimeout(() => { toast.value = '' }, 2500) }
function fmtDate(dt) { return dt ? new Date(dt).toLocaleDateString('ko-KR') : '' }

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

/* Hero */
.v-mypage-hero {
  background: #F5F0E8;
  padding: 56px 0 40px;
  margin-bottom: 48px;
  border-bottom: 1px solid #E8E2D9;
}
.v-mypage-hero-title {
  font-family: 'Cormorant Garamond', 'Georgia', serif;
  font-size: 2.2rem;
  font-weight: 400;
  color: #111;
  margin-bottom: 8px;
}
.v-mypage-hero-sub { color: #7A7269; }

/* Layout */
.v-mypage-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 64px;
  align-items: start;
}
@media (max-width: 768px) { .v-mypage-layout { grid-template-columns: 1fr; } }

/* Sidebar */
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
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.3rem;
  flex-shrink: 0;
}
.v-mypage-avatar-name { font-size: 0.85rem; font-weight: 500; color: #111; margin-bottom: 4px; }

/* Grade badge */
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

/* Nav */
.v-mypage-nav { display: flex; flex-direction: column; gap: 0; }
.v-mypage-nav-btn {
  background: none;
  border: none;
  text-align: left;
  padding: 11px 0;
  font-size: 0.78rem;
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

/* Main section */
.v-mypage-section-title {
  font-family: 'Cormorant Garamond', 'Georgia', serif;
  font-size: 1.6rem;
  font-weight: 400;
  color: #111;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #E8E2D9;
}
.v-mypage-empty { color: #C9B89A; padding: 40px 0; }

/* Orders */
.v-order-list { display: flex; flex-direction: column; gap: 16px; }
.v-order-card {
  border: 1px solid #E8E2D9;
  padding: 24px;
}
.v-order-items { margin-bottom: 16px; border-bottom: 1px solid #E8E2D9; padding-bottom: 16px; }
.v-order-item-row {
  display: flex; align-items: center; gap: 12px; padding: 6px 0; font-size: 0.82rem;
}
.v-order-item-thumb {
  width: 48px; height: 48px; object-fit: cover;
  border: 1px solid #E8E2D9; flex-shrink: 0;
}
.v-order-item-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.v-order-item-name { font-weight: 500; color: #111; }
.v-order-item-qty { color: #7A7269; font-size: 0.75rem; }
.v-order-item-subtotal { font-weight: 600; color: #111; white-space: nowrap; }
.v-order-card-head {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.v-order-id   { color: #C9B89A; }
.v-order-date { color: #C9B89A; }
.v-order-status-pill {
  font-size: 0.65rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  padding: 4px 10px;
  margin-left: auto;
}
.v-status--pending_payment { background: rgba(139,32,32,0.08);  color: #8B2020; }
.v-status--paid            { background: rgba(27,58,45,0.1);    color: #1B3A2D; }
.v-status--shipping        { background: rgba(74,103,65,0.1);   color: #4A6741; }
.v-status--delivered       { background: rgba(27,58,45,0.15);   color: #1B3A2D; }
.v-status--cancelled       { background: rgba(100,100,100,0.08);color: #888; }

.v-order-card-body { display: flex; flex-direction: column; gap: 8px; }
.v-order-row { display: flex; justify-content: space-between; font-size: 0.82rem; color: #555; }
.v-order-amount { font-weight: 600; color: #111; }
.v-order-card-foot { margin-top: 16px; padding-top: 16px; border-top: 1px solid #E8E2D9; }
.v-btn-cancel {
  background: none;
  border: 1px solid #E8E2D9;
  padding: 8px 20px;
  font-size: 0.7rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7A7269;
  cursor: pointer;
  transition: all 0.2s;
}
.v-btn-cancel:hover { border-color: #8B2020; color: #8B2020; }

/* Reviews */
.v-review-list { display: flex; flex-direction: column; }
.v-review-item { padding: 20px 0; border-bottom: 1px solid #E8E2D9; }
.v-review-meta { display: flex; align-items: center; gap: 14px; margin-bottom: 10px; }
.v-review-stars { color: #B89C6E; font-size: 0.85rem; }
.v-review-date { color: #C9B89A; }
.v-review-body { color: #555; font-size: 0.9rem; line-height: 1.7; }

/* QnA */
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

/* Profile form */
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

/* Coupons */
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

/* Withdraw */
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
</style>

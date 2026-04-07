<template>
  <div class="checkout-page">
    <div class="container">
      <h1 class="page-title">바로 구매</h1>

      <div v-if="loading" class="loading-box">
        <div class="spinner"></div>
      </div>

      <div v-else-if="!item" class="empty-box">
        <p>상품 정보를 불러올 수 없습니다.</p>
        <RouterLink to="/" class="btn-primary">홈으로</RouterLink>
      </div>

      <div v-else class="checkout-layout">
        <!-- 주문 상품 요약 -->
        <div class="checkout-item-box">
          <h2 class="section-title">주문 상품</h2>
          <div class="checkout-item">
            <img :src="item.imgPath" :alt="item.name" class="checkout-item-img" />
            <div class="checkout-item-info">
              <p class="checkout-item-brand">{{ item.brand }}</p>
              <p class="checkout-item-name">{{ item.name }}</p>
              <div class="checkout-item-price-row">
                <span v-if="item.discountPer > 0" class="price-original">{{ item.price?.toLocaleString() }}원</span>
                <span class="price-sale">{{ item.salePrice?.toLocaleString() }}원</span>
                <span v-if="item.discountPer > 0" class="price-discount-badge">{{ item.discountPer }}%</span>
              </div>
              <p class="checkout-item-qty">수량: {{ qty }}개</p>
            </div>
          </div>

          <!-- 결제 금액 요약 -->
          <div class="price-summary">
            <div class="summary-row">
              <span>상품 금액</span>
              <span>{{ (item.salePrice * qty).toLocaleString() }}원</span>
            </div>
            <div class="summary-row">
              <span>배송비</span>
              <span>{{ item.salePrice * qty >= 50000 ? '무료' : '3,000원' }}</span>
            </div>
            <div v-if="appliedCoupon" class="summary-row summary-row--discount">
              <span>쿠폰 할인 ({{ appliedCoupon.code }})</span>
              <span class="discount-amount">- {{ couponDiscount.toLocaleString() }}원</span>
            </div>
            <div class="summary-divider"></div>
            <div class="summary-row summary-row--total">
              <span>최종 결제 금액</span>
              <span>{{ totalPrice.toLocaleString() }}원</span>
            </div>
          </div>
        </div>

        <!-- 주문 정보 입력 -->
        <div class="checkout-form-box">
          <h2 class="section-title">배송 / 결제 정보</h2>
          <form @submit.prevent="submitOrder">
            <div class="form-group">
              <label class="form-label">받는 분 이름 <span class="required">*</span></label>
              <input v-model="form.name" type="text" class="form-input" placeholder="이름을 입력하세요" required />
            </div>
            <div class="form-group">
              <label class="form-label">배송 주소 <span class="required">*</span></label>
              <input v-model="form.address" type="text" class="form-input" placeholder="배송 주소를 입력하세요" required />
            </div>
            <div class="form-group">
              <label class="form-label">결제 수단</label>
              <select v-model="form.payment" class="form-input">
                <option value="CARD">신용카드</option>
                <option value="KAKAO">카카오페이</option>
                <option value="NAVER">네이버페이</option>
                <option value="TOSS">토스페이</option>
                <option value="BANK_TRANSFER">무통장 입금</option>
                <option value="WIRE_TRANSFER">실시간 계좌이체</option>
              </select>
            </div>
            <div v-if="form.payment === 'CARD'" class="form-group">
              <label class="form-label">카드 번호</label>
              <input v-model="form.cardNumber" type="text" class="form-input" placeholder="0000-0000-0000-0000" maxlength="19" />
            </div>

            <!-- 쿠폰 -->
            <div class="form-group">
              <label class="form-label">쿠폰 코드</label>
              <div class="coupon-row">
                <select v-model="selectedCouponCode" class="form-input coupon-select" @change="applyCouponFromList">
                  <option value="">쿠폰 선택 안 함</option>
                  <option v-for="c in myCoupons" :key="c.id" :value="c.code">
                    {{ c.name }} ({{ c.discountType === 'FIXED' ? c.discountValue.toLocaleString() + '원' : c.discountValue + '%' }} 할인)
                  </option>
                </select>
                <span v-if="appliedCoupon" class="coupon-applied">적용됨 ✓</span>
              </div>
            </div>

            <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>
            <div class="checkout-actions">
              <button type="button" class="btn-cancel" @click="$router.back()">취소</button>
              <button type="submit" class="btn-confirm" :disabled="submitting">
                {{ submitting ? '처리 중...' : `${totalPrice.toLocaleString()}원 결제하기` }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const item = ref(null)
const loading = ref(true)
const submitting = ref(false)
const errorMsg = ref('')
const myCoupons = ref([])
const appliedCoupon = ref(null)
const selectedCouponCode = ref('')

const qty = computed(() => Number(route.query.qty) || 1)

const subtotal = computed(() => item.value ? item.value.salePrice * qty.value : 0)
const shipping  = computed(() => subtotal.value >= 50000 ? 0 : 3000)

const couponDiscount = computed(() => {
  if (!appliedCoupon.value) return 0
  const c = appliedCoupon.value
  if (c.discountType === 'FIXED') return Math.min(c.discountValue, subtotal.value)
  return Math.floor(subtotal.value * c.discountValue / 100)
})

const totalPrice = computed(() => {
  if (!item.value) return 0
  return Math.max(0, subtotal.value + shipping.value - couponDiscount.value)
})

function applyCouponFromList() {
  if (!selectedCouponCode.value) { appliedCoupon.value = null; return }
  const found = myCoupons.value.find(c => c.code === selectedCouponCode.value)
  if (found) {
    if (found.minOrderAmount && subtotal.value < found.minOrderAmount) {
      errorMsg.value = `최소 주문 금액 ${found.minOrderAmount.toLocaleString()}원 이상 주문 시 사용 가능합니다.`
      appliedCoupon.value = null; selectedCouponCode.value = ''
    } else {
      appliedCoupon.value = found; errorMsg.value = ''
    }
  }
}

async function loadMyCoupons() {
  try {
    const res = await fetch('/v1/api/coupons/my', { credentials: 'include' })
    if (res.ok) myCoupons.value = await res.json()
  } catch {}
}

const form = ref({
  name: '',
  address: '',
  payment: 'CARD',
  cardNumber: ''
})

async function loadItem() {
  const itemId = route.query.itemId
  if (!itemId) { loading.value = false; return }
  try {
    const res = await fetch(`/v1/api/items/${itemId}`)
    if (res.ok) item.value = await res.json()
  } catch {}
  finally { loading.value = false }
}

async function submitOrder() {
  errorMsg.value = ''
  submitting.value = true
  try {
    const res = await fetch('/v1/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({
        itemIds: [item.value.id],
        name: form.value.name,
        address: form.value.address,
        payment: form.value.payment,
        cardNumber: form.value.cardNumber
      })
    })
    if (res.ok) {
      alert('주문이 완료되었습니다!')
      router.push('/mypage')
    } else {
      errorMsg.value = '주문 처리 중 오류가 발생했습니다.'
    }
  } catch {
    errorMsg.value = '네트워크 오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}

onMounted(() => { loadItem(); loadMyCoupons() })
</script>

<style scoped>
.checkout-page {
  min-height: 60vh;
  background: #fff;
  padding: 40px 0 80px;
}
.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 32px;
}
.checkout-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  align-items: start;
}
@media (max-width: 768px) {
  .checkout-layout { grid-template-columns: 1fr; }
}
.checkout-item-box,
.checkout-form-box {
  background: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 28px;
}
.section-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.checkout-item {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.checkout-item-img {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
  background: #fff;
}
.checkout-item-info { flex: 1; }
.checkout-item-brand { font-size: 12px; color: #888; margin-bottom: 4px; }
.checkout-item-name { font-size: 15px; font-weight: 600; margin-bottom: 8px; }
.checkout-item-price-row { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.price-original { font-size: 13px; color: #bbb; text-decoration: line-through; }
.price-sale { font-size: 17px; font-weight: 700; color: #111; }
.price-discount-badge {
  background: #ff4757;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
}
.checkout-item-qty { font-size: 13px; color: #666; }
.price-summary { border-top: 1px solid #eee; padding-top: 16px; }
.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 10px;
  color: #555;
}
.summary-divider { border-top: 1px solid #ddd; margin: 12px 0; }
.summary-row--total {
  font-size: 16px;
  font-weight: 700;
  color: #111;
}
.form-group { margin-bottom: 20px; }
.form-label { display: block; font-size: 14px; font-weight: 600; margin-bottom: 6px; color: #333; }
.required { color: #ff4757; }
.form-input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 14px;
  box-sizing: border-box;
  background: #fff;
  transition: border-color 0.2s;
}
.form-input:focus { outline: none; border-color: #111; }
.form-error { color: #ff4757; font-size: 13px; margin-bottom: 12px; }
.checkout-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.btn-cancel {
  flex: 1;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  font-size: 15px;
  cursor: pointer;
  color: #555;
}
.btn-cancel:hover { background: #f5f5f5; }
.btn-confirm {
  flex: 2;
  padding: 14px;
  border: none;
  border-radius: 8px;
  background: #111;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-confirm:hover:not(:disabled) { background: #333; }
.btn-confirm:disabled { background: #aaa; cursor: not-allowed; }
.loading-box { display: flex; justify-content: center; padding: 80px 0; }
.empty-box { text-align: center; padding: 80px 0; }
.btn-primary {
  display: inline-block;
  margin-top: 16px;
  padding: 12px 24px;
  background: #111;
  color: #fff;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
}
.summary-row--discount { color: #c0392b; }
.discount-amount { font-weight: 600; color: #c0392b; }
.coupon-row { display: flex; align-items: center; gap: 8px; }
.coupon-select { flex: 1; }
.coupon-applied { font-size: 13px; color: #16a34a; font-weight: 600; white-space: nowrap; }
</style>

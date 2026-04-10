<template>
  <div class="v-cart-page">

    <!-- Page header -->
    <div class="v-cart-hero">
      <div class="v-container">
        <nav class="v-breadcrumb">
          <RouterLink to="/" class="v-breadcrumb__link">Home</RouterLink>
          <span class="v-breadcrumb__sep">—</span>
          <span class="v-breadcrumb__current">Shopping Bag</span>
        </nav>
        <h1 class="v-cart-title t-display">Shopping Bag</h1>
      </div>
    </div>

    <div class="v-container">

      <!-- Not logged in -->
      <div v-if="!isLoggedIn" class="v-cart-empty">
        <p class="t-heading" style="color:#C9B89A;font-weight:300">Please sign in to view your bag.</p>
        <RouterLink to="/login" class="v-btn-primary" style="margin-top:24px;display:inline-block">Sign In</RouterLink>
      </div>

      <!-- Loading -->
      <div v-else-if="loading" class="v-cart-loading">
        <div class="v-spinner"></div>
      </div>

      <!-- Empty cart -->
      <div v-else-if="cartItems.length === 0" class="v-cart-empty">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#C9B89A" stroke-width="1">
          <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
          <line x1="3" y1="6" x2="21" y2="6"/>
          <path d="M16 10a4 4 0 0 1-8 0"/>
        </svg>
        <p class="t-heading" style="color:#C9B89A;font-weight:300;margin-top:24px">Your bag is empty.</p>
        <RouterLink to="/category/ALL" class="v-btn-primary" style="margin-top:24px;display:inline-block">Explore Collections</RouterLink>
      </div>

      <!-- Cart layout -->
      <div v-else class="v-cart-layout">

        <!-- Items -->
        <div class="v-cart-items">
          <div class="v-cart-items-header">
            <label class="v-cart-check-all">
              <input type="checkbox" v-model="allSelected" @change="toggleAll" />
              <span class="t-caption">Select All ({{ selectedItems.length }}/{{ cartItems.length }})</span>
            </label>
            <button class="v-cart-del-sel" @click="removeSelected">Remove Selected</button>
          </div>

          <div v-for="ci in cartItems" :key="ci.id" class="v-cart-item">
            <label class="v-cart-item-check">
              <input type="checkbox" :value="ci.itemId" v-model="selectedItems" />
            </label>
            <RouterLink :to="`/product/${ci.itemId}`" class="v-cart-item-img">
              <img :src="ci.item?.imgPath" :alt="ci.item?.name" />
            </RouterLink>
            <div class="v-cart-item-info">
              <p class="v-cart-item-name">{{ ci.item?.name }}</p>
              <div class="v-cart-item-price">
                <span v-if="ci.item?.discountPer > 0" class="v-price-original">{{ fmtPrice(ci.item?.price) }}</span>
                <span class="v-price-sale">{{ fmtPrice(ci.item?.salePrice) }}</span>
                <span v-if="ci.item?.discountPer > 0" class="v-price-disc">{{ ci.item?.discountPer }}%</span>
              </div>
              <div class="v-cart-qty-ctrl">
                <button class="v-cart-qty-btn" @click="changeQty(ci, ci.quantity - 1)">−</button>
                <span class="v-cart-qty-val">{{ ci.quantity }}</span>
                <button class="v-cart-qty-btn" @click="changeQty(ci, ci.quantity + 1)">+</button>
              </div>
              <p class="v-cart-item-subtotal">{{ fmtPrice((ci.item?.salePrice || 0) * ci.quantity) }}</p>
            </div>
            <button class="v-cart-item-remove" @click="removeItem(ci.itemId)" aria-label="Remove">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Summary -->
        <div class="v-cart-summary">
          <p class="v-summary-title t-caption">Order Summary</p>
          <div class="v-summary-row">
            <span>Subtotal</span>
            <span>{{ fmtPrice(totalPrice) }}</span>
          </div>
          <div class="v-summary-row">
            <span>Shipping</span>
            <span>{{ totalPrice >= 50000 ? 'Complimentary' : '3,000원' }}</span>
          </div>
          <div class="v-summary-divider"></div>
          <div class="v-summary-row v-summary-row--total">
            <span>Total</span>
            <span>{{ fmtPrice(finalPrice) }}</span>
          </div>
          <button
            class="v-summary-order-btn"
            :disabled="selectedItems.length === 0"
            @click="goToCheckout"
          >
            Place Order ({{ selectedItems.length }})
          </button>
          <p class="v-summary-note t-caption">Complimentary shipping on orders over ₩50,000</p>
        </div>
      </div>
    </div>

    <div v-if="toastMsg" class="v-toast">{{ toastMsg }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

const router = useRouter()
const { isLoggedIn } = useAuth()

const loading      = ref(false)
const cartItems    = ref([])
const selectedItems = ref([])
const allSelected  = ref(false)
const toastMsg     = ref('')

function showToast(msg) {
  toastMsg.value = msg
  setTimeout(() => { toastMsg.value = '' }, 2500)
}

function fmtPrice(p) {
  if (!p) return '0원'
  return p.toLocaleString('ko-KR') + '원'
}

function toggleAll() {
  selectedItems.value = allSelected.value ? cartItems.value.map(c => c.itemId) : []
}

const totalPrice = computed(() =>
  cartItems.value
    .filter(c => selectedItems.value.includes(c.itemId))
    .reduce((s, c) => s + (c.item?.salePrice || 0) * (c.quantity || 1), 0)
)
const finalPrice = computed(() =>
  totalPrice.value + (totalPrice.value >= 50000 ? 0 : 3000)
)

async function fetchCart() {
  if (!isLoggedIn.value) return
  loading.value = true
  try {
    const res = await fetch('/v1/api/cart/items', { credentials: 'include' })
    if (!res.ok) { cartItems.value = []; return }
    cartItems.value = await res.json()
    selectedItems.value = cartItems.value.map(c => c.itemId)
    allSelected.value = true
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function changeQty(ci, newQty) {
  if (newQty < 1) return
  try {
    await fetch(`/v1/api/cart/item/${ci.itemId}/qty`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ quantity: newQty })
    })
    ci.quantity = newQty
  } catch {
    showToast('수량 변경에 실패했습니다.')
  }
}

async function removeItem(itemId) {
  try {
    await fetch(`/v1/api/cart/item/${itemId}`, { method: 'DELETE', credentials: 'include' })
    cartItems.value = cartItems.value.filter(c => c.itemId !== itemId)
    selectedItems.value = selectedItems.value.filter(id => id !== itemId)
    showToast('Removed from bag.')
  } catch {
    showToast('Failed to remove item.')
  }
}

async function removeSelected() {
  for (const itemId of [...selectedItems.value]) {
    await removeItem(itemId)
  }
}

function goToCheckout() {
  router.push({ path: '/cart-checkout', query: { items: selectedItems.value.join(',') } })
}

onMounted(fetchCart)
</script>

<style scoped>
.v-cart-page { min-height: 70vh; padding-bottom: 80px; }

/* Hero */
.v-cart-hero {
  background: #F5F0E8;
  padding: 56px 0 40px;
  margin-bottom: 48px;
  border-bottom: 1px solid #E8E2D9;
}
.v-breadcrumb { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.v-breadcrumb__link { font-size: 0.72rem; color: #7A7269; text-decoration: none; transition: color 0.2s; }
.v-breadcrumb__link:hover { color: #111; }
.v-breadcrumb__sep { color: #C9B89A; font-size: 0.7rem; }
.v-breadcrumb__current { font-size: 0.72rem; color: #1B3A2D; font-weight: 500; }
.v-cart-title {
  font-family: var(--font-serif);
  font-size: 2.2rem;
  font-weight: 400;
  color: #111;
}

/* Empty / loading */
.v-cart-empty { text-align: center; padding: 100px 0; }
.v-cart-loading { text-align: center; padding: 80px 0; }

/* Layout */
.v-cart-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 48px;
  align-items: start;
}
@media (max-width: 900px) { .v-cart-layout { grid-template-columns: 1fr; } }

/* Items header */
.v-cart-items-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0 16px;
  border-bottom: 1px solid #E8E2D9;
  margin-bottom: 0;
}
.v-cart-check-all { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.v-cart-check-all input { accent-color: #1B3A2D; }
.v-cart-del-sel {
  background: none;
  border: none;
  font-size: 0.7rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7A7269;
  cursor: pointer;
  transition: color 0.2s;
}
.v-cart-del-sel:hover { color: #8B2020; }

/* Cart item */
.v-cart-item {
  display: grid;
  grid-template-columns: 24px 100px 1fr 28px;
  gap: 20px;
  align-items: center;
  padding: 24px 0;
  border-bottom: 1px solid #E8E2D9;
}
.v-cart-item-check { display: flex; align-items: center; }
.v-cart-item-check input { accent-color: #1B3A2D; width: 16px; height: 16px; }
.v-cart-item-img {
  aspect-ratio: 3 / 4;
  overflow: hidden;
  background: #F5F0E8;
  display: block;
}
.v-cart-item-img img { width: 100%; height: 100%; object-fit: cover; }
.v-cart-item-info { display: flex; flex-direction: column; gap: 6px; }
.v-cart-item-brand { color: #7A7269; text-transform: uppercase; letter-spacing: 0.08em; }
.v-cart-item-name { font-size: 0.9rem; color: #111; font-weight: 400; line-height: 1.4; }
.v-cart-item-price { display: flex; align-items: baseline; gap: 10px; }
.v-price-original { font-size: 0.78rem; color: #C9B89A; text-decoration: line-through; }
.v-price-sale { font-size: 1rem; font-weight: 500; color: #111; }
.v-price-disc { font-size: 0.68rem; font-weight: 600; color: #8B2020; }
.v-cart-qty-ctrl {
  display: flex;
  align-items: center;
  gap: 0;
  border: 1px solid #E8E2D9;
  width: fit-content;
  margin-top: 8px;
}
.v-cart-qty-btn {
  background: none;
  border: none;
  width: 28px;
  height: 28px;
  font-size: 1rem;
  color: #111;
  cursor: pointer;
  transition: background 0.2s;
}
.v-cart-qty-btn:hover { background: #F5F0E8; }
.v-cart-qty-val { min-width: 32px; text-align: center; font-size: 0.85rem; }
.v-cart-item-subtotal {
  font-size: 0.85rem;
  font-weight: 600;
  color: #111;
  margin-top: 6px;
}

.v-cart-item-remove {
  background: none;
  border: none;
  color: #C9B89A;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.v-cart-item-remove:hover { color: #8B2020; }

/* Summary */
.v-cart-summary {
  border: 1px solid #E8E2D9;
  padding: 32px;
  position: sticky;
  top: 100px;
}
.v-summary-title {
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: #7A7269;
  margin-bottom: 24px;
}
.v-summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: #555;
  margin-bottom: 12px;
}
.v-summary-divider { border: none; border-top: 1px solid #E8E2D9; margin: 20px 0; }
.v-summary-row--total {
  font-size: 1rem;
  font-weight: 500;
  color: #111;
  margin-bottom: 24px;
}
.v-summary-order-btn {
  width: 100%;
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 15px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.25s;
  margin-bottom: 16px;
}
.v-summary-order-btn:hover:not(:disabled) { background: #4A6741; }
.v-summary-order-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.v-summary-note { color: #C9B89A; text-align: center; }

/* Primary link button */
.v-btn-primary {
  background: #1B3A2D;
  color: #F5F0E8;
  text-decoration: none;
  padding: 14px 40px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  transition: background 0.25s;
}
.v-btn-primary:hover { background: #4A6741; }
</style>

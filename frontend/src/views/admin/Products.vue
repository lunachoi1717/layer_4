<template>
  <div class="admin-page">
    <h1 class="admin-page-title">상품 관리</h1>

    <div class="admin-toolbar">
      <input v-model="keyword" class="admin-search-input" placeholder="상품명 검색" @keyup.enter="loadProducts" />
      <button class="admin-btn admin-btn--ghost admin-btn--sm" @click="loadProducts">검색</button>
      <button class="admin-btn admin-btn--primary" @click="openAddModal">+ 상품 등록</button>
    </div>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>이미지</th><th>상품명</th><th>카테고리</th>
            <th>가격</th><th>할인</th><th>재고</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.id">
            <td>#{{ p.id }}</td>
            <td><img :src="p.imgPath" class="product-thumb" /></td>
            <td style="font-weight:600">{{ p.name }}</td>
            <td><span class="grade-badge grade-sapphire">{{ p.category }}</span></td>
            <td>{{ p.price?.toLocaleString() }}원</td>
            <td>{{ p.discountPer }}%</td>
            <td>
              <input type="number" class="stock-input" :value="p.stockCount" min="0"
                @change="updateStock(p.id, $event.target.value)" />
            </td>
            <td>
              <div class="admin-btn-group">
                <button class="admin-btn admin-btn--warning admin-btn--sm" @click="openEditModal(p)">수정</button>
                <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteProduct(p.id)">삭제</button>
              </div>
            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="8" style="text-align:center;color:#9CA3AF;padding:40px">등록된 상품이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 상품 등록/수정 모달 -->
    <div v-if="showModal" class="admin-modal-overlay" @click.self="showModal = false">
      <div class="admin-modal" style="max-width:600px">
        <h3 class="admin-modal-title">{{ editTarget ? '상품 수정' : '상품 등록' }}</h3>
        <form @submit.prevent="submitProduct">
          <div class="admin-form-group">
            <label class="admin-form-label">카테고리</label>
            <select v-model="form.category" class="admin-form-select" required>
              <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
            </select>
          </div>
          <div class="admin-form-group">
            <label class="admin-form-label">상품명</label>
            <input v-model="form.name" class="admin-form-input" required />
          </div>
          <div class="admin-form-group">
            <label class="admin-form-label">상품 설명</label>
            <textarea v-model="form.description" class="admin-form-textarea" rows="3" />
          </div>
          <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:12px">
            <div class="admin-form-group">
              <label class="admin-form-label">가격</label>
              <input v-model.number="form.price" class="admin-form-input" type="number" min="0" required />
            </div>
            <div class="admin-form-group">
              <label class="admin-form-label">할인율(%)</label>
              <input v-model.number="form.discountPer" class="admin-form-input" type="number" min="0" max="100" />
            </div>
            <div class="admin-form-group">
              <label class="admin-form-label">재고</label>
              <input v-model.number="form.stockCount" class="admin-form-input" type="number" min="0" />
            </div>
          </div>
          <div class="admin-form-group">
            <label class="admin-form-label">이미지</label>
            <input type="file" accept="image/*" @change="onImageChange" class="admin-form-input" />
            <img v-if="imagePreview" :src="imagePreview" style="width:80px;height:80px;object-fit:cover;border-radius:6px;margin-top:8px;border:1px solid #E5E7EB" />
          </div>
          <div class="admin-modal-actions">
            <button type="button" class="admin-btn admin-btn--ghost" @click="showModal = false">취소</button>
            <button type="submit" class="admin-btn admin-btn--primary">{{ editTarget ? '수정' : '등록' }}</button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const products = ref([])
const keyword = ref('')
const loading = ref(false)
const showModal = ref(false)
const editTarget = ref(null)
const imageFile = ref(null)
const imagePreview = ref(null)
const toast = ref('')

const categories = ['OUTER', 'TOP', 'PANTS', 'SHOES', 'BAG', 'ACC', 'SCARVES', 'READY_TO_WEAR', 'PERFUME', 'SALE']
const form = ref({ name: '', category: 'TOP', description: '', price: 0, discountPer: 0, stockCount: 0 })

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2000)
}

async function loadProducts() {
  loading.value = true
  try {
    const url = keyword.value
      ? `/v1/api/admin/items?keyword=${encodeURIComponent(keyword.value)}`
      : '/v1/api/admin/items'
    const res = await fetch(url, { credentials: 'include' })
    if (res.ok) products.value = await res.json()
  } catch {} finally { loading.value = false }
}

function openAddModal() {
  editTarget.value = null
  form.value = { name: '', category: 'TOP', description: '', price: 0, discountPer: 0, stockCount: 0 }
  imageFile.value = null
  imagePreview.value = null
  showModal.value = true
}

function openEditModal(p) {
  editTarget.value = p
  form.value = { name: p.name, category: p.category, description: p.description || '', price: p.price, discountPer: p.discountPer, stockCount: p.stockCount }
  imageFile.value = null
  imagePreview.value = p.imgPath
  showModal.value = true
}

function onImageChange(e) {
  imageFile.value = e.target.files[0]
  if (imageFile.value) imagePreview.value = URL.createObjectURL(imageFile.value)
}

async function submitProduct() {
  const fd = new FormData()
  fd.append('data', new Blob([JSON.stringify(form.value)], { type: 'application/json' }))
  if (imageFile.value) fd.append('image', imageFile.value)
  const url = editTarget.value ? `/v1/api/admin/items/${editTarget.value.id}` : '/v1/api/admin/items'
  const method = editTarget.value ? 'PUT' : 'POST'
  const res = await fetch(url, { method, body: fd, credentials: 'include' })
  if (res.ok) { showModal.value = false; loadProducts(); showToast(editTarget.value ? '수정되었습니다.' : '등록되었습니다.') }
  else showToast('처리 중 오류가 발생했습니다.')
}

async function deleteProduct(id) {
  if (!confirm('상품을 삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/items/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadProducts(); showToast('삭제되었습니다.') }
}

async function updateStock(id, stockCount) {
  await fetch(`/v1/api/admin/items/${id}/stock`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ stockCount: Number(stockCount) })
  })
}

onMounted(loadProducts)
</script>

<style scoped>
.product-thumb {
  width: 44px; height: 44px; object-fit: cover;
  border-radius: 6px; border: 1px solid #E5E7EB;
}
.stock-input {
  width: 64px; border: 1px solid #E5E7EB; border-radius: 4px;
  padding: 4px 6px; font-size: 0.8rem; text-align: center;
}
</style>

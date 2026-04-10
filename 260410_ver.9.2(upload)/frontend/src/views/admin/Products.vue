<template>
  <div class="admin-page">
    <h1 class="admin-page-title">상품 관리</h1>

    <div class="admin-toolbar products-toolbar">

      <input v-model="keyword" class="admin-search-input" placeholder="상품명 검색"
        @keyup.enter="applyFilter" @input="applyFilter" />

      <select v-model="categoryFilter" class="admin-form-select filter-select" @change="applyFilter">
        <option value="">전체 카테고리</option>
        <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
      </select>

      <div class="toolbar-right">
        <span class="result-count">총 {{ filteredProducts.length }}개</span>
        <button class="admin-btn admin-btn--primary" @click="openAddModal">+ 상품 등록</button>
      </div>
    </div>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th @click="toggleSort('id')" class="sortable-th">ID <span class="sort-icon">{{ sortField==='id'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>이미지</th>
            <th @click="toggleSort('name')" class="sortable-th">상품명 <span class="sort-icon">{{ sortField==='name'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>카테고리</th>
            <th @click="toggleSort('price')" class="sortable-th">가격 <span class="sort-icon">{{ sortField==='price'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleSort('discountPer')" class="sortable-th">할인 <span class="sort-icon">{{ sortField==='discountPer'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleSort('stockCount')" class="sortable-th">재고 <span class="sort-icon">{{ sortField==='stockCount'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in pagedProducts" :key="p.id">
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
          <tr v-if="pagedProducts.length === 0">
            <td colspan="8" style="text-align:center;color:#9CA3AF;padding:40px">상품이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPages > 1" class="admin-pagination">
      <button class="pg-btn" :disabled="currentPage === 1" @click="currentPage = 1">«</button>
      <button class="pg-btn" :disabled="currentPage === 1" @click="currentPage--">‹</button>
      <button
        v-for="p in pageNumbers"
        :key="p"
        class="pg-btn pg-btn--num"
        :class="{ 'pg-btn--active': p === currentPage }"
        @click="currentPage = p"
      >{{ p }}</button>
      <button class="pg-btn" :disabled="currentPage === totalPages" @click="currentPage++">›</button>
      <button class="pg-btn" :disabled="currentPage === totalPages" @click="currentPage = totalPages">»</button>
      <span class="pg-info">{{ currentPage }} / {{ totalPages }}</span>
    </div>

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
            <div class="img-upload-row">
              <button type="button" class="admin-btn admin-btn--ghost admin-btn--sm"
                @click="$refs.fileInput.click()" :disabled="uploading">
                {{ uploading ? '업로드 중…' : '📎 사진 첨부' }}
              </button>
              <span class="img-upload-hint">또는 아래 목록에서 선택</span>
            </div>
            <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleFileUpload" />
            <select v-model="form.imgPath" class="admin-form-select" style="margin-top:8px">
              <option value="">이미지를 선택하세요</option>
              <option v-for="img in imageList" :key="img" :value="'/images/' + img">{{ img }}</option>
            </select>
            <img v-if="form.imgPath" :src="form.imgPath"
              style="width:100px;height:100px;object-fit:cover;border-radius:6px;margin-top:10px;border:1px solid #E5E7EB;display:block" />
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
import { ref, computed, onMounted } from 'vue'

const ITEMS_PER_PAGE = 10

const allProducts = ref([])
const loading     = ref(false)
const showModal   = ref(false)
const editTarget  = ref(null)
const toast       = ref('')
const uploading   = ref(false)
const fileInput   = ref(null)

const keyword        = ref('')
const categoryFilter = ref('')
const sortField      = ref('id')
const sortAsc        = ref(false)
const currentPage    = ref(1)

function toggleSort(field) {
  if (sortField.value === field) {
    sortAsc.value = !sortAsc.value
  } else {
    sortField.value = field
    sortAsc.value = false
  }
  currentPage.value = 1
}

const categories = ['SCARVES', 'READY_TO_WEAR', 'PERFUME', 'ACC', 'BAGS', 'SALE']
const imageList  = ref([])

const form = ref({ name: '', category: 'SCARVES', description: '', price: 0, discountPer: 0, stockCount: 0, imgPath: '' })

const filteredProducts = computed(() => {
  let list = allProducts.value
  if (categoryFilter.value) {
    list = list.filter(p => p.category === categoryFilter.value)
  }
  if (keyword.value.trim()) {
    const kw = keyword.value.trim().toLowerCase()
    list = list.filter(p => p.name?.toLowerCase().includes(kw))
  }
  return list
})

const sortedProducts = computed(() => {
  const list = [...filteredProducts.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (typeof va === 'string') return sortAsc.value ? va.localeCompare(vb) : vb.localeCompare(va)
    return sortAsc.value ? (va ?? 0) - (vb ?? 0) : (vb ?? 0) - (va ?? 0)
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(sortedProducts.value.length / ITEMS_PER_PAGE)))

const pagedProducts = computed(() => {
  const start = (currentPage.value - 1) * ITEMS_PER_PAGE
  return sortedProducts.value.slice(start, start + ITEMS_PER_PAGE)
})

const pageNumbers = computed(() => {
  const total = totalPages.value
  const cur   = currentPage.value
  let start = Math.max(1, cur - 2)
  let end   = Math.min(total, start + 4)
  if (end - start < 4) start = Math.max(1, end - 4)
  const nums = []
  for (let i = start; i <= end; i++) nums.push(i)
  return nums
})

function applyFilter() { currentPage.value = 1 }

async function loadProducts() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/admin/items', { credentials: 'include' })
    if (res.ok) allProducts.value = await res.json()
  } catch {} finally { loading.value = false }
}

async function loadImageList() {
  try {
    const r = await fetch('/v1/api/admin/items/images', { credentials: 'include' })
    if (r.ok) imageList.value = await r.json()
  } catch {}
}

async function handleFileUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await fetch('/v1/api/admin/items/upload', { method: 'POST', credentials: 'include', body: fd })
    if (res.ok) { form.value.imgPath = (await res.json()).imgPath; showToast('이미지 업로드 완료') }
    else showToast('업로드 실패')
  } catch { showToast('업로드 중 오류 발생') }
  finally { uploading.value = false; e.target.value = '' }
}

function showToast(msg) { toast.value = msg; setTimeout(() => { toast.value = '' }, 2000) }

function openAddModal() {
  editTarget.value = null
  form.value = { name: '', category: 'SCARVES', description: '', price: 0, discountPer: 0, stockCount: 0, imgPath: '' }
  showModal.value = true
}

function openEditModal(p) {
  editTarget.value = p
  form.value = { name: p.name, category: p.category, description: p.description || '',
    price: p.price, discountPer: p.discountPer, stockCount: p.stockCount, imgPath: p.imgPath || '' }
  showModal.value = true
}

async function submitProduct() {
  const url    = editTarget.value ? `/v1/api/admin/items/${editTarget.value.id}` : '/v1/api/admin/items'
  const method = editTarget.value ? 'PUT' : 'POST'
  const res = await fetch(url, {
    method, headers: { 'Content-Type': 'application/json' }, credentials: 'include',
    body: JSON.stringify(form.value)
  })
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
    method: 'PATCH', headers: { 'Content-Type': 'application/json' }, credentials: 'include',
    body: JSON.stringify({ stockCount: Number(stockCount) })
  })
}

onMounted(() => { loadProducts(); loadImageList() })
</script>

<style scoped>
.sortable-th { cursor: pointer; user-select: none; white-space: nowrap; }
.sortable-th:hover { background: #F3F4F6; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }

.products-toolbar {
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.filter-select {
  font-size: 0.78rem;
  padding: 5px 8px;
  min-width: 130px;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}
.result-count {
  font-size: 0.78rem;
  color: #6B7280;
  white-space: nowrap;
}

.product-thumb {
  width: 44px; height: 44px; object-fit: cover;
  border-radius: 6px; border: 1px solid #E5E7EB;
}
.stock-input {
  width: 64px; border: 1px solid #E5E7EB; border-radius: 4px;
  padding: 4px 6px; font-size: 0.8rem; text-align: center;
}

.admin-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 20px;
}
.pg-btn {
  min-width: 32px; height: 32px;
  padding: 0 8px;
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #374151;
  cursor: pointer;
  transition: all 0.15s;
}
.pg-btn:hover:not(:disabled) { background: #F3F4F6; border-color: #D1D5DB; }
.pg-btn--active { background: #1B3A2D; color: #fff; border-color: #1B3A2D; font-weight: 600; }
.pg-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.pg-info {
  font-size: 0.75rem;
  color: #9CA3AF;
  margin-left: 8px;
}

.img-upload-row {
  display: flex; align-items: center; gap: 12px; margin-bottom: 4px;
}
.img-upload-hint { font-size: 0.75rem; color: #9CA3AF; }
</style>

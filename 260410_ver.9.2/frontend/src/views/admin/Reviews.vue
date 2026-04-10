<template>
  <div class="admin-page">
    <h1 class="admin-page-title">리뷰 관리</h1>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th @click="toggleSort('id')" class="sortable-th">ID <span class="sort-icon">{{ sortField==='id'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>작성자</th>
            <th>상품ID</th>
            <th @click="toggleSort('rating')" class="sortable-th">평점 <span class="sort-icon">{{ sortField==='rating'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>내용</th>
            <th @click="toggleSort('createdAt')" class="sortable-th">작성일 <span class="sort-icon">{{ sortField==='createdAt'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="r in sortedReviews" :key="r.id">
            <tr @click="toggleExpand(r.id)" class="clickable-row">
              <td>#{{ r.id }}</td>
              <td>{{ r.memberName }}</td>
              <td style="color:#9CA3AF">{{ r.itemId ? `#${r.itemId}` : '-' }}</td>
              <td><span class="stars">{{ '★'.repeat(r.rating) }}</span></td>
              <td class="review-content">{{ r.content }}</td>
              <td>{{ formatDate(r.createdAt) }}</td>
              <td @click.stop>
                <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteReview(r.id)">삭제</button>
              </td>
            </tr>
            <tr v-if="expandedId === r.id" class="detail-row">
              <td colspan="7">
                <div class="review-detail">
                  <p class="review-detail-label">리뷰 내용</p>
                  <p class="review-detail-text">{{ r.content }}</p>
                  <div class="review-detail-meta">
                    <span>평점: <strong>{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</strong></span>
                    <span>작성자: {{ r.memberName }}</span>
                    <span>상품: #{{ r.itemId }}</span>
                  </div>
                </div>
              </td>
            </tr>
          </template>
          <tr v-if="sortedReviews.length === 0">
            <td colspan="7" style="text-align:center;color:#9CA3AF;padding:40px">등록된 리뷰가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const reviews    = ref([])
const loading    = ref(false)
const toast      = ref('')
const expandedId = ref(null)

const sortField = ref('id')
const sortAsc   = ref(false)

function toggleSort(field) {
  if (sortField.value === field) {
    sortAsc.value = !sortAsc.value
  } else {
    sortField.value = field
    sortAsc.value = false
  }
}

const sortedReviews = computed(() => {
  const list = [...reviews.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return sortAsc.value ? -1 : 1
    if (va > vb) return sortAsc.value ? 1 : -1
    return 0
  })
})

function toggleExpand(id) {
  expandedId.value = expandedId.value === id ? null : id
}

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2000)
}
function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

async function loadReviews() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/admin/reviews', { credentials: 'include' })
    if (res.ok) reviews.value = await res.json()
  } catch {} finally { loading.value = false }
}

async function deleteReview(id) {
  if (!confirm('리뷰를 삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/reviews/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadReviews(); showToast('삭제되었습니다.') }
}

onMounted(loadReviews)
</script>

<style scoped>
.sortable-th { cursor: pointer; user-select: none; white-space: nowrap; }
.sortable-th:hover { background: #F3F4F6; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }
.clickable-row { cursor: pointer; }
.clickable-row:hover td { background: #F9FAFB; }
.stars { color: #F59E0B; font-size: 0.85rem; }
.review-content { max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 0.82rem; color: #6B7280; }
.detail-row td { background: #F9FAFB; padding: 0; border-bottom: 2px solid #E5E7EB; }
.review-detail { padding: 16px 20px; }
.review-detail-label { font-size: 0.65rem; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; color: #6B7280; margin-bottom: 6px; }
.review-detail-text { font-size: 0.85rem; color: #374151; line-height: 1.7; margin-bottom: 10px; }
.review-detail-meta { display: flex; gap: 20px; font-size: 0.78rem; color: #6B7280; }
.review-detail-meta strong { color: #F59E0B; }
</style>

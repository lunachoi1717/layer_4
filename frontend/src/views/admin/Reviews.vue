<template>
  <div class="admin-page">
    <h1 class="admin-page-title">리뷰 관리</h1>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>작성자</th><th>상품ID</th><th>평점</th><th>내용</th><th>작성일</th><th>삭제</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in reviews" :key="r.id">
            <td>#{{ r.id }}</td>
            <td>{{ r.memberName }}</td>
            <td style="color:#9CA3AF">{{ r.itemId ? `#${r.itemId}` : '-' }}</td>
            <td><span class="stars">{{ '★'.repeat(r.rating) }}</span></td>
            <td class="review-content">{{ r.content }}</td>
            <td>{{ formatDate(r.createdAt) }}</td>
            <td>
              <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteReview(r.id)">삭제</button>
            </td>
          </tr>
          <tr v-if="reviews.length === 0">
            <td colspan="7" style="text-align:center;color:#9CA3AF;padding:40px">등록된 리뷰가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const reviews = ref([])
const loading = ref(false)
const toast = ref('')

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
.stars { color: #F59E0B; font-size: 0.85rem; }
.review-content { max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 0.82rem; color: #6B7280; }
</style>

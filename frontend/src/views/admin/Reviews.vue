<template>
  <div class="admin-page">
    <h1 class="admin-page-title">리뷰 관리</h1>

    <div class="admin-card">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>작성자</th><th>상품ID</th><th>평점</th><th>내용</th><th>작성일</th><th>삭제</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in reviews" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.memberName }}</td>
            <td>{{ r.itemId }}</td>
            <td>{{ '★'.repeat(r.rating) }}</td>
            <td class="content-cell">{{ r.content }}</td>
            <td>{{ formatDate(r.createdAt) }}</td>
            <td>
              <button class="btn-admin-danger" @click="deleteReview(r.id)">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="toast" class="toast-msg">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const reviews = ref([])
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
  try {
    const res = await fetch('/v1/api/admin/reviews', { credentials: 'include' })
    if (res.ok) reviews.value = await res.json()
  } catch {}
}

async function deleteReview(id) {
  if (!confirm('리뷰를 삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/reviews/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadReviews(); showToast('삭제되었습니다.') }
}

onMounted(loadReviews)
</script>

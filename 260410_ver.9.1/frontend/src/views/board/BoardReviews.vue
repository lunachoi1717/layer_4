<template>
  <div class="reviews-page">
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="reviews.length === 0" class="empty">등록된 리뷰가 없습니다.</div>
    <div v-else class="review-list">
      <div v-for="r in reviews" :key="r.id" class="review-item">
        <div class="review-head">
          <span class="review-author">{{ r.memberName }}</span>
          <span class="review-stars">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
          <RouterLink v-if="r.itemId" :to="`/product/${r.itemId}`" class="review-product-link">
            상품 보기
          </RouterLink>
          <span class="review-date">{{ formatDate(r.createdAt) }}</span>
        </div>
        <p class="review-content">{{ r.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const reviews = ref([])
const loading = ref(false)

function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

async function loadReviews() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/reviews')
    if (res.ok) reviews.value = await res.json()
  } catch {} finally { loading.value = false }
}

onMounted(loadReviews)
</script>

<style scoped>
.loading, .empty { text-align: center; padding: 60px; color: #888; }
.review-list { border-top: 2px solid #1a1a1a; }
.review-item {
  border-bottom: 1px solid #eee;
  padding: 20px 16px;
}
.review-item:hover { background: #fafafa; }
.review-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.review-author {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}
.review-stars { color: #F59E0B; font-size: 13px; }
.review-product-link {
  font-size: 12px;
  color: #8b6914;
  text-decoration: none;
  background: #f0ebe8;
  padding: 2px 8px;
  border-radius: 10px;
}
.review-product-link:hover { background: #e0d8cc; }
.review-date { font-size: 12px; color: #aaa; margin-left: auto; }
.review-content {
  font-size: 14px;
  color: #555;
  line-height: 1.7;
  margin: 0;
}
</style>

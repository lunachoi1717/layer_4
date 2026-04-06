<template>
  <div class="faq-page">
    <div class="faq-filter">
      <button
        v-for="cat in categories"
        :key="cat"
        class="filter-btn"
        :class="{ 'filter-btn--active': selectedCategory === cat }"
        @click="selectCategory(cat)"
      >
        {{ cat === 'ALL' ? '전체' : cat }}
      </button>
    </div>

    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="faqs.length === 0" class="empty">등록된 FAQ가 없습니다.</div>
    <div v-else class="faq-list">
      <div
        v-for="faq in faqs"
        :key="faq.id"
        class="faq-item"
        :class="{ 'faq-item--open': openId === faq.id }"
      >
        <button class="faq-question" @click="toggle(faq.id)">
          <span class="faq-cat-tag">{{ faq.category }}</span>
          <span class="faq-q-text">{{ faq.question }}</span>
          <span class="faq-arrow">{{ openId === faq.id ? '▲' : '▼' }}</span>
        </button>
        <div v-if="openId === faq.id" class="faq-answer">
          <p>{{ faq.answer }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const categories = ['ALL', '주문/배송', '환불/교환', '회원', '상품', '기타']
const selectedCategory = ref('ALL')
const faqs = ref([])
const loading = ref(false)
const openId = ref(null)

async function fetchFaqs() {
  loading.value = true
  try {
    const url = selectedCategory.value === 'ALL'
      ? '/v1/api/board/faq'
      : `/v1/api/board/faq?category=${encodeURIComponent(selectedCategory.value)}`
    const res = await fetch(url)
    faqs.value = await res.json()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function selectCategory(cat) {
  selectedCategory.value = cat
  openId.value = null
  fetchFaqs()
}

function toggle(id) {
  openId.value = openId.value === id ? null : id
}

onMounted(fetchFaqs)
</script>

<style scoped>
.faq-filter { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 32px; }
.filter-btn {
  padding: 7px 18px; border: 1.5px solid #ddd; border-radius: 20px;
  background: #fff; color: #555; font-size: 13px; cursor: pointer;
  transition: all .2s;
}
.filter-btn:hover, .filter-btn--active {
  background: #1a1a1a; color: #fff; border-color: #1a1a1a;
}
.loading, .empty { text-align: center; padding: 60px; color: #888; }
.faq-list { border-top: 2px solid #1a1a1a; }
.faq-item { border-bottom: 1px solid #eee; }
.faq-item--open { background: #fafafa; }
.faq-question {
  width: 100%; display: flex; align-items: center; gap: 12px;
  padding: 18px 16px; background: none; border: none; cursor: pointer;
  text-align: left; font-size: 14px;
}
.faq-cat-tag {
  background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600;
  padding: 3px 8px; border-radius: 10px; white-space: nowrap;
}
.faq-q-text { flex: 1; color: #1a1a1a; font-weight: 500; }
.faq-arrow { color: #888; font-size: 12px; }
.faq-answer { padding: 0 16px 20px 16px; }
.faq-answer p { color: #555; font-size: 14px; line-height: 1.8; margin: 0; }
</style>

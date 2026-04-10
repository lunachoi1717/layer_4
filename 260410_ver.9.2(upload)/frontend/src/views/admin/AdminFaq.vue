<template>
  <div class="admin-page">
    <h1 class="admin-page-title">FAQ 관리</h1>

    <div class="admin-card">
      <div class="section-toolbar">
        <span class="count-label">총 {{ faqs.length }}건</span>
        <button class="admin-btn admin-btn--primary" @click="openFaqForm()">FAQ 등록</button>
      </div>

      <form v-if="faqForm.show" class="admin-form" @submit.prevent="saveFaq">
        <div class="admin-form-group">
          <label class="admin-form-label">카테고리</label>
          <select v-model="faqForm.category" class="admin-form-select">
            <option v-for="c in faqCategories" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">질문</label>
          <input v-model="faqForm.question" type="text" class="admin-form-input" placeholder="FAQ 질문" required />
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">답변</label>
          <textarea v-model="faqForm.answer" class="admin-form-textarea" rows="4" placeholder="FAQ 답변" required></textarea>
        </div>
        <div class="admin-form-group" style="flex-direction:row;align-items:center;gap:8px">
          <input type="checkbox" v-model="faqForm.isPublished" id="faq-public" />
          <label class="admin-form-label" for="faq-public" style="margin:0">공개</label>
        </div>
        <div class="admin-modal-actions">
          <button type="button" class="admin-btn admin-btn--ghost" @click="faqForm.show = false">취소</button>
          <button type="submit" class="admin-btn admin-btn--primary">{{ faqForm.id ? '수정' : '등록' }}</button>
        </div>
      </form>

      <table class="admin-table">
        <thead>
          <tr>
            <th @click="toggleSort('id')" class="sortable-th">ID <span class="sort-icon">{{ sortField==='id'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>카테고리</th>
            <th>질문</th>
            <th @click="toggleSort('isPublished')" class="sortable-th">공개 <span class="sort-icon">{{ sortField==='isPublished'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleSort('createdAt')" class="sortable-th">등록일 <span class="sort-icon">{{ sortField==='createdAt'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="faq in sortedFaqs" :key="faq.id">
            <td>{{ faq.id }}</td>
            <td><span class="cat-badge">{{ faq.category }}</span></td>
            <td style="text-align:left;max-width:320px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">{{ faq.question }}</td>
            <td><span :class="faq.isPublished ? 'badge-on' : 'badge-off'">{{ faq.isPublished ? '공개' : '비공개' }}</span></td>
            <td>{{ formatDate(faq.createdAt) }}</td>
            <td>
              <div class="admin-btn-group">
                <button class="admin-btn admin-btn--ghost admin-btn--sm" @click="openFaqForm(faq)">수정</button>
                <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteFaq(faq.id)">삭제</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const faqCategories = ['주문/배송', '환불/교환', '회원', '상품', '기타']
const faqs = ref([])

const sortField = ref('id')
const sortAsc   = ref(false)

function toggleSort(field) {
  if (sortField.value === field) { sortAsc.value = !sortAsc.value }
  else { sortField.value = field; sortAsc.value = false }
}

const sortedFaqs = computed(() => {
  const list = [...faqs.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return sortAsc.value ? -1 : 1
    if (va > vb) return sortAsc.value ? 1 : -1
    return 0
  })
})

const faqForm = ref({ show: false, id: null, category: '기타', question: '', answer: '', isPublished: true })

function openFaqForm(faq = null) {
  if (faq) {
    faqForm.value = { show: true, id: faq.id, category: faq.category, question: faq.question, answer: faq.answer, isPublished: faq.isPublished }
  } else {
    faqForm.value = { show: true, id: null, category: '기타', question: '', answer: '', isPublished: true }
  }
}

async function fetchFaqs() {
  const res = await fetch('/v1/api/admin/board/faq', { credentials: 'include' })
  if (res.ok) faqs.value = await res.json()
}

async function saveFaq() {
  const url = faqForm.value.id ? `/v1/api/admin/board/faq/${faqForm.value.id}` : '/v1/api/admin/board/faq'
  const method = faqForm.value.id ? 'PUT' : 'POST'
  const res = await fetch(url, {
    method, credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ category: faqForm.value.category, question: faqForm.value.question, answer: faqForm.value.answer, isPublished: faqForm.value.isPublished })
  })
  if (res.ok) { faqForm.value.show = false; await fetchFaqs() }
  else alert('저장 실패')
}

async function deleteFaq(id) {
  if (!confirm('삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/board/faq/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) await fetchFaqs()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('ko-KR')
}

onMounted(fetchFaqs)
</script>

<style scoped>
.section-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.count-label { font-size: 0.82rem; color: var(--vad-text-muted, #6B7280); }
.sortable-th { cursor: pointer; user-select: none; white-space: nowrap; }
.sortable-th:hover { background: #F3F4F6; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }
.cat-badge { background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 10px; }
.badge-on  { background: #e8f5e9; color: #2e7d32; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.badge-off { background: #fce4ec; color: #c62828; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
</style>

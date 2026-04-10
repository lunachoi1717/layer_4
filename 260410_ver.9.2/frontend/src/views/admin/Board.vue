<template>
  <div class="admin-board">
    <div class="page-header">
      <h1 class="page-title">게시판 관리</h1>
    </div>

    <!-- 탭 -->
    <div class="tabs">
      <button class="tab" :class="{ 'tab--active': activeTab === 'faq' }" @click="activeTab = 'faq'">FAQ 관리</button>
      <button class="tab" :class="{ 'tab--active': activeTab === 'inquiry' }" @click="activeTab = 'inquiry'">1:1 문의 관리</button>
    </div>

    <!-- FAQ 관리 -->
    <div v-if="activeTab === 'faq'">
      <div class="section-toolbar">
        <span class="count-label">총 {{ faqs.length }}건</span>
        <button class="btn-primary" @click="openFaqForm()">FAQ 등록</button>
      </div>

      <!-- FAQ 작성/수정 폼 -->
      <form v-if="faqForm.show" class="admin-form" @submit.prevent="saveFaq">
        <div class="form-row">
          <label>카테고리</label>
          <select v-model="faqForm.category" class="form-select">
            <option v-for="c in faqCategories" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>
        <div class="form-row">
          <label>질문</label>
          <input v-model="faqForm.question" type="text" class="form-input" placeholder="FAQ 질문" required />
        </div>
        <div class="form-row">
          <label>답변</label>
          <textarea v-model="faqForm.answer" class="form-textarea" rows="4" placeholder="FAQ 답변" required></textarea>
        </div>
        <div class="form-row form-row--inline">
          <label><input type="checkbox" v-model="faqForm.isPublished" /> 공개</label>
        </div>
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="faqForm.show = false">취소</button>
          <button type="submit" class="btn-save">{{ faqForm.id ? '수정' : '등록' }}</button>
        </div>
      </form>

      <table class="data-table">
        <thead>
          <tr>
            <th @click="toggleFaqSort('id')" class="sortable-th">ID <span class="sort-icon">{{ faqSortField==='id'?(faqSortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>카테고리</th>
            <th>질문</th>
            <th @click="toggleFaqSort('isPublished')" class="sortable-th">공개 <span class="sort-icon">{{ faqSortField==='isPublished'?(faqSortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleFaqSort('createdAt')" class="sortable-th">등록일 <span class="sort-icon">{{ faqSortField==='createdAt'?(faqSortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="faq in sortedFaqs" :key="faq.id">
            <td>{{ faq.id }}</td>
            <td><span class="cat-badge">{{ faq.category }}</span></td>
            <td class="td-left">{{ faq.question }}</td>
            <td><span :class="faq.isPublished ? 'badge-on' : 'badge-off'">{{ faq.isPublished ? '공개' : '비공개' }}</span></td>
            <td>{{ formatDate(faq.createdAt) }}</td>
            <td>
              <button class="btn-sm btn-edit" @click="openFaqForm(faq)">수정</button>
              <button class="btn-sm btn-delete" @click="deleteFaq(faq.id)">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 1:1 문의 관리 -->
    <div v-if="activeTab === 'inquiry'">
      <div class="section-toolbar">
        <span class="count-label">미답변 <strong>{{ unansweredCount }}</strong>건 / 전체 {{ inquiries.length }}건</span>
        <div style="display:flex;align-items:center;gap:12px">
          <label class="filter-check">
            <input type="checkbox" v-model="filterUnanswered" @change="fetchInquiries" /> 미답변만 보기
          </label>
          <select class="sort-select" v-model="inqSortField" @change="inqSortAsc = false">
            <option value="id">최신순(ID)</option>
            <option value="createdAt">등록일순</option>
            <option value="isAnswered">답변상태</option>
          </select>
          <button class="sort-dir-btn" @click="inqSortAsc = !inqSortAsc">
            {{ inqSortAsc ? '↑ 오름차순' : '↓ 내림차순' }}
          </button>
        </div>
      </div>

      <div v-for="item in sortedInquiries" :key="item.id" class="inquiry-card">
        <div class="inquiry-card-header">
          <span class="cat-badge">{{ item.category }}</span>
          <span class="inquiry-member">{{ item.memberName }}</span>
          <span class="inquiry-title-text">{{ item.title }}</span>
          <span :class="item.isAnswered ? 'badge-on' : 'badge-off'">{{ item.isAnswered ? '답변완료' : '답변대기' }}</span>
          <span class="inquiry-date">{{ formatDate(item.createdAt) }}</span>
        </div>
        <div class="inquiry-content-text">{{ item.content }}</div>

        <!-- 답변 -->
        <div v-if="item.isAnswered" class="answer-box">
          <strong>답변:</strong> {{ item.answerContent }}
        </div>
        <div v-else class="answer-form">
          <textarea v-model="answerTexts[item.id]" class="form-textarea" rows="3" placeholder="답변을 입력하세요..."></textarea>
          <button class="btn-answer" @click="submitAnswer(item.id)">답변 등록</button>
        </div>
        <button class="btn-sm btn-delete" @click="deleteInquiry(item.id)">삭제</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const activeTab = ref('faq')
const faqCategories = ['주문/배송', '환불/교환', '회원', '상품', '기타']
const faqs = ref([])
const inquiries = ref([])
const filterUnanswered = ref(false)
const answerTexts = ref({})

// FAQ 정렬
const faqSortField = ref('id')
const faqSortAsc   = ref(false)

function toggleFaqSort(field) {
  if (faqSortField.value === field) {
    faqSortAsc.value = !faqSortAsc.value
  } else {
    faqSortField.value = field
    faqSortAsc.value = false
  }
}

const sortedFaqs = computed(() => {
  const list = [...faqs.value]
  const f = faqSortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return faqSortAsc.value ? -1 : 1
    if (va > vb) return faqSortAsc.value ? 1 : -1
    return 0
  })
})

// 문의 정렬
const inqSortField = ref('id')
const inqSortAsc   = ref(false)

const sortedInquiries = computed(() => {
  const list = [...inquiries.value]
  const f = inqSortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return inqSortAsc.value ? -1 : 1
    if (va > vb) return inqSortAsc.value ? 1 : -1
    return 0
  })
})

const unansweredCount = computed(() => inquiries.value.filter(i => !i.isAnswered).length)

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

async function fetchInquiries() {
  const url = filterUnanswered.value ? '/v1/api/admin/board/inquiry?unanswered=true' : '/v1/api/admin/board/inquiry'
  const res = await fetch(url, { credentials: 'include' })
  if (res.ok) inquiries.value = await res.json()
}

async function submitAnswer(id) {
  const answerContent = answerTexts.value[id]
  if (!answerContent?.trim()) { alert('답변 내용을 입력하세요.'); return }
  const res = await fetch(`/v1/api/admin/board/inquiry/${id}/answer`, {
    method: 'POST', credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ answerContent })
  })
  if (res.ok) { answerTexts.value[id] = ''; await fetchInquiries() }
  else alert('답변 등록 실패')
}

async function deleteInquiry(id) {
  if (!confirm('삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/board/inquiry/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) await fetchInquiries()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('ko-KR')
}

onMounted(() => { fetchFaqs(); fetchInquiries() })
</script>

<style scoped>
.admin-board { padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 700; margin: 0; }
.tabs { display: flex; gap: 0; border-bottom: 2px solid #eee; margin-bottom: 24px; }
.tab {
  padding: 12px 24px; background: none; border: none; border-bottom: 2px solid transparent;
  margin-bottom: -2px; font-size: 14px; font-weight: 600; color: #888; cursor: pointer;
}
.tab--active { color: #1a1a1a; border-bottom-color: #1a1a1a; }
.section-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.count-label { font-size: 14px; color: #555; }
.btn-primary {
  padding: 8px 18px; background: #1a1a1a; color: #fff; border: none;
  border-radius: 6px; cursor: pointer; font-size: 13px; font-weight: 600;
}
.admin-form {
  background: #f8f9fa; border: 1px solid #eee; border-radius: 8px;
  padding: 20px; margin-bottom: 20px;
}
.form-row { margin-bottom: 12px; }
.form-row--inline { display: flex; align-items: center; }
.form-row label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 5px; color: #444; }
.form-select, .form-input, .form-textarea {
  width: 100%; border: 1.5px solid #ddd; border-radius: 5px; padding: 8px 12px;
  font-size: 14px; outline: none; box-sizing: border-box;
}
.form-textarea { resize: vertical; }
.form-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 12px; }
.btn-save {
  padding: 8px 20px; background: #1a1a1a; color: #fff; border: none;
  border-radius: 5px; cursor: pointer; font-size: 13px; font-weight: 600;
}
.btn-cancel {
  padding: 8px 16px; background: #eee; color: #555; border: none;
  border-radius: 5px; cursor: pointer; font-size: 13px;
}
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th { background: #f5f5f5; padding: 10px 12px; text-align: center; border-bottom: 1px solid #ddd; }
.data-table td { padding: 10px 12px; text-align: center; border-bottom: 1px solid #f0f0f0; }
.td-left { text-align: left; max-width: 280px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cat-badge {
  background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600;
  padding: 2px 8px; border-radius: 10px;
}
.badge-on { background: #e8f5e9; color: #2e7d32; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.badge-off { background: #fce4ec; color: #c62828; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.btn-sm {
  padding: 4px 10px; border: 1px solid #ddd; border-radius: 4px;
  cursor: pointer; font-size: 12px; margin: 0 2px; background: #fff;
}
.btn-edit { color: #1565c0; border-color: #1565c0; }
.btn-delete { color: #c62828; border-color: #c62828; }
.filter-check { font-size: 13px; cursor: pointer; display: flex; align-items: center; gap: 6px; }
.sortable-th { cursor: pointer; user-select: none; white-space: nowrap; }
.sortable-th:hover { background: #ebebeb; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }
.sort-select {
  border: 1px solid #ddd; border-radius: 5px; padding: 5px 10px;
  font-size: 12px; outline: none;
}
.sort-dir-btn {
  padding: 5px 12px; background: #fff; border: 1px solid #ddd;
  border-radius: 5px; cursor: pointer; font-size: 12px; color: #374151;
}
.sort-dir-btn:hover { background: #F3F4F6; }
.inquiry-card {
  border: 1px solid #eee; border-radius: 8px; padding: 16px;
  margin-bottom: 12px; background: #fafafa;
}
.inquiry-card-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; flex-wrap: wrap; }
.inquiry-member { font-weight: 600; font-size: 13px; color: #333; }
.inquiry-title-text { flex: 1; font-size: 14px; font-weight: 500; }
.inquiry-date { font-size: 12px; color: #999; }
.inquiry-content-text { font-size: 14px; color: #555; margin-bottom: 12px; line-height: 1.6; }
.answer-box {
  background: #fffde7; border-left: 3px solid #f9a825; padding: 12px;
  border-radius: 0 6px 6px 0; font-size: 14px; color: #444; margin-bottom: 8px;
}
.answer-form { margin-bottom: 8px; }
.btn-answer {
  margin-top: 8px; padding: 7px 16px; background: #d4af7a; color: #1a1a1a;
  border: none; border-radius: 5px; cursor: pointer; font-weight: 700; font-size: 13px;
}
</style>

<template>
  <div class="admin-page">
    <h1 class="admin-page-title">1:1 문의 관리</h1>

    <div class="admin-card">
      <div class="section-toolbar">
        <span class="count-label">
          미답변 <strong>{{ unansweredCount }}</strong>건 / 전체 {{ inquiries.length }}건
        </span>
        <div style="display:flex;align-items:center;gap:12px">
          <label class="filter-check">
            <input type="checkbox" v-model="filterUnanswered" @change="fetchInquiries" />
            미답변만 보기
          </label>
          <select class="sort-select" v-model="sortField" @change="sortAsc = false">
            <option value="id">최신순(ID)</option>
            <option value="createdAt">등록일순</option>
            <option value="isAnswered">답변상태</option>
          </select>
          <button class="sort-dir-btn" @click="sortAsc = !sortAsc">
            {{ sortAsc ? '↑ 오름차순' : '↓ 내림차순' }}
          </button>
        </div>
      </div>

      <div v-if="sortedInquiries.length === 0" style="text-align:center;padding:48px;color:#9CA3AF">
        문의 내역이 없습니다.
      </div>

      <div v-for="item in sortedInquiries" :key="item.id" class="inquiry-card">
        <div class="inquiry-card-header">
          <span class="cat-badge">{{ item.category }}</span>
          <span class="inquiry-member">{{ item.memberName }}</span>
          <span class="inquiry-title-text">{{ item.title }}</span>
          <span :class="item.isAnswered ? 'badge-on' : 'badge-off'">{{ item.isAnswered ? '답변완료' : '답변대기' }}</span>
          <span class="inquiry-date">{{ formatDate(item.createdAt) }}</span>
          <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteInquiry(item.id)">삭제</button>
        </div>
        <div class="inquiry-content-text">{{ item.content }}</div>

        <div v-if="item.isAnswered" class="answer-box">
          <strong>답변:</strong> {{ item.answerContent }}
        </div>
        <div v-else class="answer-form">
          <textarea v-model="answerTexts[item.id]" class="admin-form-textarea" rows="3" placeholder="답변을 입력하세요..."></textarea>
          <button class="btn-answer" @click="submitAnswer(item.id)">답변 등록</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const inquiries = ref([])
const filterUnanswered = ref(false)
const answerTexts = ref({})

const sortField = ref('id')
const sortAsc   = ref(false)

const sortedInquiries = computed(() => {
  const list = [...inquiries.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return sortAsc.value ? -1 : 1
    if (va > vb) return sortAsc.value ? 1 : -1
    return 0
  })
})

const unansweredCount = computed(() => inquiries.value.filter(i => !i.isAnswered).length)

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

onMounted(fetchInquiries)
</script>

<style scoped>
.section-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 10px; }
.count-label { font-size: 0.82rem; color: var(--vad-text-muted, #6B7280); }
.filter-check { font-size: 13px; cursor: pointer; display: flex; align-items: center; gap: 6px; }
.sort-select { border: 1px solid #E5E7EB; border-radius: 6px; padding: 5px 10px; font-size: 12px; outline: none; }
.sort-dir-btn { padding: 5px 12px; background: #fff; border: 1px solid #E5E7EB; border-radius: 6px; cursor: pointer; font-size: 12px; color: #374151; }
.sort-dir-btn:hover { background: #F3F4F6; }
.cat-badge { background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 10px; }
.badge-on  { background: #e8f5e9; color: #2e7d32; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.badge-off { background: #fff3e0; color: #e65100; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.inquiry-card { border: 1px solid #E5E7EB; border-radius: 8px; padding: 16px; margin-bottom: 12px; background: #FAFAFA; }
.inquiry-card-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; flex-wrap: wrap; }
.inquiry-member { font-weight: 600; font-size: 13px; color: #333; }
.inquiry-title-text { flex: 1; font-size: 14px; font-weight: 500; }
.inquiry-date { font-size: 12px; color: #9CA3AF; }
.inquiry-content-text { font-size: 14px; color: #555; margin-bottom: 12px; line-height: 1.6; white-space: pre-wrap; }
.answer-box { background: #fffde7; border-left: 3px solid #f9a825; padding: 12px; border-radius: 0 6px 6px 0; font-size: 14px; color: #444; margin-bottom: 8px; }
.answer-form { margin-bottom: 8px; }
.btn-answer { margin-top: 8px; padding: 7px 16px; background: #B89C6E; color: #1a1a1a; border: none; border-radius: 5px; cursor: pointer; font-weight: 700; font-size: 13px; }
.btn-answer:hover { background: #a08860; }
</style>

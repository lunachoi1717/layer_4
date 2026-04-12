<template>
  <div class="admin-page">
    <h1 class="admin-page-title">Q&A 관리</h1>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th @click="toggleSort('id')" class="sortable-th">ID <span class="sort-icon">{{ sortField==='id'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>상품</th><th>작성자</th><th>제목</th><th>비밀글</th>
            <th @click="toggleSort('isAnswered')" class="sortable-th">상태 <span class="sort-icon">{{ sortField==='isAnswered'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleSort('createdAt')" class="sortable-th">작성일 <span class="sort-icon">{{ sortField==='createdAt'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="q in sortedQuestions" :key="q.id">
            <tr :class="{ 'row-expanded': expandedId === q.id }">
              <td>#{{ q.id }}</td>
              <td style="color:#9CA3AF">{{ q.itemId ? `#${q.itemId}` : '-' }}</td>
              <td>{{ q.memberName || '-' }}</td>
              <td>
                <button class="link-btn" @click="toggleExpand(q.id)">
                  {{ q.title }}
                </button>
              </td>
              <td>
                <span class="lock-icon">{{ q.isSecret ? '🔒' : '-' }}</span>
              </td>
              <td>
                <span class="qna-status" :class="q.isAnswered ? 'qna-answered' : 'qna-pending'">
                  {{ q.isAnswered ? '답변완료' : '답변대기' }}
                </span>
              </td>
              <td>{{ formatDate(q.createdAt) }}</td>
              <td>
                <div class="admin-btn-group">
                  <button class="admin-btn admin-btn--primary admin-btn--sm" @click="openAnswerModal(q)">답변</button>
                  <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteQna(q.id)">삭제</button>
                </div>
              </td>
            </tr>
            <!-- 펼쳐진 상세 행 -->
            <tr v-if="expandedId === q.id" class="expand-row">
              <td colspan="8">
                <div class="qna-detail">
                  <div class="qna-detail-content">
                    <span class="qna-detail-label">문의 내용</span>
                    <p>{{ q.content }}</p>
                  </div>
                  <div v-if="q.isAnswered && q.answerContent" class="qna-answer">
                    <span class="qna-detail-label answer-label">관리자 답변</span>
                    <p>{{ q.answerContent }}</p>
                  </div>
                </div>
              </td>
            </tr>
          </template>
          <tr v-if="questions.length === 0">
            <td colspan="8" style="text-align:center;color:#9CA3AF;padding:40px">등록된 Q&A가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 답변 모달 -->
    <div v-if="showAnswerModal" class="admin-modal-overlay" @click.self="showAnswerModal = false">
      <div class="admin-modal">
        <h3 class="admin-modal-title">Q&A 답변 작성</h3>
        <div class="qna-modal-question">
          <p class="qna-modal-q-title">{{ answerTarget?.title }}</p>
          <p class="qna-modal-q-content">{{ answerTarget?.content }}</p>
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">답변 내용</label>
          <textarea v-model="answerContent" class="admin-form-textarea" placeholder="답변 내용을 입력하세요..." rows="5" />
        </div>
        <div class="admin-modal-actions">
          <button class="admin-btn admin-btn--ghost" @click="showAnswerModal = false">취소</button>
          <button class="admin-btn admin-btn--primary" @click="submitAnswer">답변 등록</button>
        </div>
      </div>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const questions = ref([])
const loading = ref(false)
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

const sortedQuestions = computed(() => {
  const list = [...questions.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return sortAsc.value ? -1 : 1
    if (va > vb) return sortAsc.value ? 1 : -1
    return 0
  })
})
const showAnswerModal = ref(false)
const answerTarget = ref(null)
const answerContent = ref('')
const toast = ref('')

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2000)
}
function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}
function toggleExpand(id) {
  expandedId.value = expandedId.value === id ? null : id
}

async function loadQna() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/admin/qna', { credentials: 'include' })
    if (res.ok) questions.value = await res.json()
  } catch {} finally { loading.value = false }
}

function openAnswerModal(q) {
  answerTarget.value = q
  answerContent.value = q.answerContent || ''
  showAnswerModal.value = true
}

async function submitAnswer() {
  if (!answerContent.value) { showToast('답변을 입력해주세요.'); return }
  const res = await fetch(`/v1/api/admin/qna/${answerTarget.value.id}/answer`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ answerContent: answerContent.value })
  })
  if (res.ok) {
    showAnswerModal.value = false
    loadQna()
    showToast('답변이 등록되었습니다.')
  } else showToast('오류가 발생했습니다.')
}

async function deleteQna(id) {
  if (!confirm('Q&A를 삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/qna/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) { loadQna(); showToast('삭제되었습니다.') }
}

onMounted(loadQna)
</script>

<style scoped>
.sortable-th { cursor: pointer; user-select: none; white-space: nowrap; }
.sortable-th:hover { background: #F3F4F6; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }
.link-btn {
  background: none; border: none; color: #111827; font-size: 0.82rem;
  font-weight: 500; cursor: pointer; text-align: left; padding: 0;
  transition: color 0.15s;
}
.link-btn:hover { color: #1B3A2D; text-decoration: underline; }
.lock-icon { font-size: 0.8rem; }
.qna-status {
  display: inline-block; font-size: 0.68rem; font-weight: 600;
  padding: 3px 10px; border-radius: 20px;
}
.qna-answered { background: #DCFCE7; color: #166534; }
.qna-pending  { background: #FEF3C7; color: #92400E; }
.row-expanded td { background: #F9FAFB; }
.expand-row td { background: #F9FAFB; padding: 0; border-bottom: 2px solid #E5E7EB; }
.qna-detail { padding: 16px 20px; display: flex; flex-direction: column; gap: 12px; }
.qna-detail-content p, .qna-answer p { font-size: 0.84rem; color: #374151; line-height: 1.7; margin: 6px 0 0; }
.qna-detail-label {
  font-size: 0.65rem; font-weight: 700; letter-spacing: 0.1em;
  text-transform: uppercase; color: #6B7280;
}
.answer-label { color: #1B3A2D; }
.qna-answer { border-left: 3px solid #1B3A2D; padding-left: 14px; }
.qna-modal-question { background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 8px; padding: 14px; margin-bottom: 18px; }
.qna-modal-q-title { font-weight: 600; font-size: 0.9rem; margin-bottom: 6px; }
.qna-modal-q-content { font-size: 0.82rem; color: #6B7280; line-height: 1.6; }
</style>

<template>
  <div class="admin-page">
    <h1 class="admin-page-title">Q&A 관리</h1>

    <div class="admin-card">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>작성자</th><th>제목</th><th>비밀글</th><th>상태</th><th>작성일</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="q in questions" :key="q.id">
            <td>{{ q.id }}</td>
            <td>{{ q.memberName }}</td>
            <td>
              <button class="link-btn" @click="toggleExpand(q.id)">
                {{ q.title }}
              </button>
            </td>
            <td>{{ q.isSecret ? '🔒' : '-' }}</td>
            <td>
              <span class="qna-status" :class="q.isAnswered ? 'answered' : 'pending'">
                {{ q.isAnswered ? '답변완료' : '답변대기' }}
              </span>
            </td>
            <td>{{ formatDate(q.createdAt) }}</td>
            <td class="action-cell">
              <button class="btn-admin-primary" @click="openAnswerModal(q)">답변</button>
              <button class="btn-admin-danger" @click="deleteQna(q.id)">삭제</button>
            </td>
          </tr>
          <!-- 확장 행 -->
          <tr v-if="expandedId === q.id" v-for="q in questions" :key="'exp-'+q.id" class="expand-row">
            <td colspan="7">
              <div class="qna-detail">
                <p><strong>내용:</strong> {{ q.content }}</p>
                <div v-if="q.isAnswered && q.answerContent" class="qna-answer">
                  <strong>답변:</strong> {{ q.answerContent }}
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 답변 모달 -->
    <div v-if="showAnswerModal" class="modal-overlay" @click.self="showAnswerModal = false">
      <div class="modal-box">
        <h3>답변 작성</h3>
        <p class="modal-question">{{ answerTarget?.title }}</p>
        <p class="modal-question-content">{{ answerTarget?.content }}</p>
        <textarea v-model="answerContent" class="review-textarea" placeholder="답변 내용을 입력하세요..." rows="5" />
        <div class="modal-actions">
          <button class="btn-cancel" @click="showAnswerModal = false">취소</button>
          <button class="btn-confirm" @click="submitAnswer">답변 등록</button>
        </div>
      </div>
    </div>

    <div v-if="toast" class="toast-msg">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const questions = ref([])
const expandedId = ref(null)
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
  try {
    const res = await fetch('/v1/api/admin/qna', { credentials: 'include' })
    if (res.ok) questions.value = await res.json()
  } catch {}
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

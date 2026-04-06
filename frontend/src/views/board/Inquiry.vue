<template>
  <div class="inquiry-page">
    <div class="inquiry-toolbar">
      <h2 class="inquiry-page-title">1:1 문의</h2>
      <button v-if="isLoggedIn" class="btn-write" @click="showForm = !showForm">
        {{ showForm ? '취소' : '문의 작성' }}
      </button>
      <RouterLink v-else to="/login" class="btn-write">로그인 후 문의 작성</RouterLink>
    </div>

    <!-- 문의 작성 폼 (로그인 사용자만) -->
    <form v-if="showForm && isLoggedIn" class="inquiry-form" @submit.prevent="submitInquiry">
      <div class="form-row">
        <label>카테고리</label>
        <select v-model="form.category" class="form-select">
          <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>
      <div class="form-row">
        <label>제목</label>
        <input v-model="form.title" type="text" class="form-input" placeholder="문의 제목을 입력하세요" required />
      </div>
      <div class="form-row">
        <label>내용</label>
        <textarea v-model="form.content" class="form-textarea" placeholder="문의 내용을 입력하세요" rows="5" required></textarea>
      </div>
      <div class="form-actions">
        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '제출 중...' : '문의 제출' }}
        </button>
      </div>
    </form>

    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="inquiries.length === 0" class="empty">등록된 문의가 없습니다.</div>
    <div v-else class="inquiry-list">
      <div v-for="item in inquiries" :key="item.id" class="inquiry-item">
        <div class="inquiry-header" @click="toggleDetail(item)">
          <span class="inquiry-cat">{{ item.category }}</span>
          <span class="inquiry-title">{{ item.title }}</span>
          <span class="inquiry-author">{{ item.memberName }}</span>
          <span class="inquiry-status" :class="item.isAnswered ? 'answered' : 'pending'">
            {{ item.isAnswered ? '답변완료' : '답변대기' }}
          </span>
          <span class="inquiry-date">{{ formatDate(item.createdAt) }}</span>
          <span class="inquiry-toggle-icon">{{ expandedId === item.id ? '▲' : '▼' }}</span>
        </div>

        <!-- 상세 내용 (작성자 or 관리자만 content 존재) -->
        <transition name="slide">
          <div v-if="expandedId === item.id" class="inquiry-detail">
            <template v-if="item.content !== null && item.content !== undefined">
              <div class="inquiry-content">{{ item.content }}</div>
              <div v-if="item.isAnswered && item.answerContent" class="inquiry-answer">
                <span class="answer-label">관리자 답변</span>
                <p>{{ item.answerContent }}</p>
                <span class="answer-date">{{ formatDate(item.answeredAt) }}</span>
              </div>
              <div v-if="isOwner(item) && !item.isAnswered" class="inquiry-delete">
                <button class="btn-delete" @click.stop="deleteInquiry(item.id)">삭제</button>
              </div>
            </template>
            <template v-else>
              <div class="inquiry-private-notice">
                <span>🔒 본인과 관리자만 내용을 확인할 수 있습니다.</span>
                <RouterLink v-if="!isLoggedIn" to="/login" class="btn-login-hint">로그인하기</RouterLink>
              </div>
            </template>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuth } from '../../composables/useAuth.js'

const { isLoggedIn, loginId } = useAuth()

const categories = ['주문/배송', '환불/교환', '회원', '상품', '기타']
const inquiries = ref([])
const loading = ref(false)
const showForm = ref(false)
const submitting = ref(false)
const expandedId = ref(null)
const form = ref({ category: '기타', title: '', content: '' })

function isOwner(item) {
  return isLoggedIn.value && item.memberName !== undefined && item.memberId !== undefined
    && loginId.value === item.memberLoginId
}

function toggleDetail(item) {
  expandedId.value = expandedId.value === item.id ? null : item.id
}

async function fetchInquiries() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/board/inquiry', { credentials: 'include' })
    if (res.ok) inquiries.value = await res.json()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function submitInquiry() {
  submitting.value = true
  try {
    const res = await fetch('/v1/api/board/inquiry', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(form.value)
    })
    if (res.ok) {
      form.value = { category: '기타', title: '', content: '' }
      showForm.value = false
      await fetchInquiries()
    } else {
      alert('문의 등록에 실패했습니다.')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

async function deleteInquiry(id) {
  if (!confirm('문의를 삭제하시겠습니까?')) return
  try {
    const res = await fetch(`/v1/api/board/inquiry/${id}`, { method: 'DELETE', credentials: 'include' })
    if (res.ok) { expandedId.value = null; await fetchInquiries() }
    else alert('삭제에 실패했습니다.')
  } catch (e) {
    console.error(e)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('ko-KR')
}

onMounted(fetchInquiries)
</script>

<style scoped>
.inquiry-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.inquiry-page-title { font-size: 18px; font-weight: 700; margin: 0; }
.btn-write {
  padding: 9px 20px; background: #1a1a1a; color: #fff; border: none;
  border-radius: 6px; cursor: pointer; font-size: 13px; font-weight: 600;
  transition: background .2s; text-decoration: none; display: inline-block;
}
.btn-write:hover { background: #333; }
.inquiry-form {
  background: #fafafa; border: 1px solid #eee; border-radius: 10px;
  padding: 24px; margin-bottom: 32px;
}
.form-row { margin-bottom: 16px; display: flex; flex-direction: column; gap: 6px; }
.form-row label { font-size: 13px; font-weight: 600; color: #444; }
.form-select, .form-input, .form-textarea {
  border: 1.5px solid #ddd; border-radius: 6px; padding: 9px 12px;
  font-size: 14px; outline: none; transition: border-color .2s;
}
.form-select:focus, .form-input:focus, .form-textarea:focus { border-color: #1a1a1a; }
.form-textarea { resize: vertical; }
.form-actions { text-align: right; }
.btn-submit {
  padding: 10px 24px; background: #1B3A2D; color: #F5F0E8; border: none;
  border-radius: 6px; cursor: pointer; font-weight: 700; font-size: 14px;
}
.btn-submit:disabled { opacity: .6; cursor: not-allowed; }
.loading, .empty { text-align: center; padding: 60px; color: #888; }
.inquiry-list { border-top: 2px solid #1a1a1a; }
.inquiry-item { border-bottom: 1px solid #eee; }
.inquiry-header {
  display: flex; align-items: center; gap: 10px; padding: 16px 4px;
  cursor: pointer; flex-wrap: wrap; transition: background .15s;
}
.inquiry-header:hover { background: #fafafa; }
.inquiry-cat {
  background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600;
  padding: 3px 8px; border-radius: 10px; white-space: nowrap;
}
.inquiry-title { flex: 1; font-weight: 600; font-size: 14px; color: #1a1a1a; min-width: 100px; }
.inquiry-author { font-size: 12px; color: #999; white-space: nowrap; }
.inquiry-status { font-size: 12px; padding: 3px 10px; border-radius: 10px; font-weight: 600; white-space: nowrap; }
.answered { background: #e8f5e9; color: #2e7d32; }
.pending  { background: #fff3e0; color: #e65100; }
.inquiry-date  { font-size: 12px; color: #999; white-space: nowrap; }
.inquiry-toggle-icon { font-size: 10px; color: #bbb; margin-left: auto; }

/* Detail panel */
.inquiry-detail { padding: 16px 4px 20px; }
.inquiry-content { font-size: 14px; color: #555; line-height: 1.7; white-space: pre-wrap; }
.inquiry-answer {
  background: #f8f9fa; border-left: 3px solid #1B3A2D; padding: 14px 16px;
  margin-top: 12px; border-radius: 0 6px 6px 0;
}
.answer-label { font-size: 12px; font-weight: 700; color: #1B3A2D; display: block; margin-bottom: 8px; }
.inquiry-answer p { font-size: 14px; color: #444; margin: 0 0 6px; line-height: 1.7; }
.answer-date { font-size: 12px; color: #999; }
.inquiry-delete { text-align: right; margin-top: 10px; }
.btn-delete {
  background: none; border: 1px solid #ddd; color: #999; padding: 5px 12px;
  border-radius: 4px; cursor: pointer; font-size: 12px;
}
.btn-delete:hover { color: #c0392b; border-color: #c0392b; }

/* Private notice */
.inquiry-private-notice {
  display: flex; align-items: center; gap: 14px;
  padding: 14px 16px; background: #F5F0E8; border-radius: 6px;
  font-size: 13px; color: #7A7269;
}
.btn-login-hint {
  padding: 6px 14px; background: #1B3A2D; color: #F5F0E8;
  border-radius: 4px; font-size: 12px; font-weight: 600; text-decoration: none;
}

/* Transition */
.slide-enter-active, .slide-leave-active { transition: max-height 0.25s ease, opacity 0.2s; overflow: hidden; }
.slide-enter-from, .slide-leave-to { max-height: 0; opacity: 0; }
.slide-enter-to, .slide-leave-from { max-height: 500px; opacity: 1; }
</style>

<template>
  <div class="inquiry-page">
    <div class="inquiry-toolbar">
      <h2 class="inquiry-page-title">나의 1:1 문의</h2>
      <button class="btn-write" @click="showForm = !showForm">
        {{ showForm ? '취소' : '문의 작성' }}
      </button>
    </div>

    <!-- 문의 작성 폼 -->
    <form v-if="showForm" class="inquiry-form" @submit.prevent="submitInquiry">
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
        <div class="inquiry-header">
          <span class="inquiry-cat">{{ item.category }}</span>
          <span class="inquiry-title">{{ item.title }}</span>
          <span class="inquiry-status" :class="item.isAnswered ? 'answered' : 'pending'">
            {{ item.isAnswered ? '답변완료' : '답변대기' }}
          </span>
          <span class="inquiry-date">{{ formatDate(item.createdAt) }}</span>
        </div>
        <div class="inquiry-content">{{ item.content }}</div>
        <div v-if="item.isAnswered && item.answerContent" class="inquiry-answer">
          <span class="answer-label">관리자 답변</span>
          <p>{{ item.answerContent }}</p>
          <span class="answer-date">{{ formatDate(item.answeredAt) }}</span>
        </div>
        <div v-if="!item.isAnswered" class="inquiry-delete">
          <button class="btn-delete" @click="deleteInquiry(item.id)">삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const categories = ['주문/배송', '환불/교환', '회원', '상품', '기타']
const inquiries = ref([])
const loading = ref(false)
const showForm = ref(false)
const submitting = ref(false)
const form = ref({ category: '기타', title: '', content: '' })

async function fetchInquiries() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/board/inquiry/my', { credentials: 'include' })
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
    if (res.ok) await fetchInquiries()
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
  transition: background .2s;
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
  padding: 10px 24px; background: #d4af7a; color: #1a1a1a; border: none;
  border-radius: 6px; cursor: pointer; font-weight: 700; font-size: 14px;
}
.btn-submit:disabled { opacity: .6; cursor: not-allowed; }
.loading, .empty { text-align: center; padding: 60px; color: #888; }
.inquiry-list { border-top: 2px solid #1a1a1a; }
.inquiry-item { border-bottom: 1px solid #eee; padding: 20px 0; }
.inquiry-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; flex-wrap: wrap; }
.inquiry-cat {
  background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600;
  padding: 3px 8px; border-radius: 10px;
}
.inquiry-title { flex: 1; font-weight: 600; font-size: 14px; color: #1a1a1a; }
.inquiry-status { font-size: 12px; padding: 3px 10px; border-radius: 10px; font-weight: 600; }
.answered { background: #e8f5e9; color: #2e7d32; }
.pending { background: #fff3e0; color: #e65100; }
.inquiry-date { font-size: 12px; color: #999; }
.inquiry-content { font-size: 14px; color: #555; line-height: 1.7; }
.inquiry-answer {
  background: #f8f9fa; border-left: 3px solid #d4af7a; padding: 14px 16px;
  margin-top: 12px; border-radius: 0 6px 6px 0;
}
.answer-label { font-size: 12px; font-weight: 700; color: #8b6914; display: block; margin-bottom: 8px; }
.inquiry-answer p { font-size: 14px; color: #444; margin: 0 0 6px; line-height: 1.7; }
.answer-date { font-size: 12px; color: #999; }
.inquiry-delete { text-align: right; margin-top: 8px; }
.btn-delete {
  background: none; border: 1px solid #ddd; color: #999; padding: 5px 12px;
  border-radius: 4px; cursor: pointer; font-size: 12px;
}
.btn-delete:hover { color: #c0392b; border-color: #c0392b; }
</style>

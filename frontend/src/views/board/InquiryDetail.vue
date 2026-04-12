<template>
  <div class="inquiry-detail-page">

    <div v-if="needPassword" class="pw-gate">
      <div class="pw-gate-box">
        <div class="pw-gate-icon">🔒</div>
        <h3 class="pw-gate-title">비공개 문의입니다</h3>
        <p class="pw-gate-desc">작성자 또는 관리자만 열람할 수 있습니다.<br>비밀번호를 입력하면 열람할 수 있습니다.</p>
        <form class="pw-form" @submit.prevent="verifyPassword">
          <input
            v-model="pwInput"
            type="password"
            class="pw-input"
            placeholder="비밀번호 입력"
            autofocus
          />
          <p v-if="pwError" class="pw-error">{{ pwError }}</p>
          <div class="pw-actions">
            <button type="button" class="btn-back" @click="$router.back()">돌아가기</button>
            <button type="submit" class="btn-verify" :disabled="verifying">
              {{ verifying ? '확인 중...' : '확인' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <template v-else-if="inquiry">
      <div class="detail-container">
      <div class="detail-header">
        <nav class="breadcrumb">
          <RouterLink to="/board/inquiry" class="breadcrumb-link">1:1 문의</RouterLink>
          <span class="breadcrumb-sep">›</span>
          <span class="breadcrumb-current">상세</span>
        </nav>
        <div class="detail-meta">
          <span class="inquiry-cat">{{ inquiry.category }}</span>
          <span class="inquiry-status" :class="inquiry.isAnswered ? 'answered' : 'pending'">
            {{ inquiry.isAnswered ? '답변완료' : '답변대기' }}
          </span>
        </div>
        <h2 class="detail-title">{{ inquiry.title }}</h2>
        <div class="detail-info">
          <span>{{ inquiry.memberName }}</span>
          <span class="info-sep">·</span>
          <span>{{ formatDate(inquiry.createdAt) }}</span>
        </div>
      </div>

      <div class="detail-body">
        <div class="detail-content">
          <p>{{ inquiry.content }}</p>
        </div>

        <div v-if="inquiry.isAnswered && inquiry.answerContent" class="detail-answer">
          <span class="answer-label">관리자 답변</span>
          <p>{{ inquiry.answerContent }}</p>
          <span class="answer-date">{{ formatDate(inquiry.answeredAt) }}</span>
        </div>
      </div>

      <div class="detail-actions">
        <button class="btn-back" @click="$router.back()">목록으로</button>
        <button
          v-if="canDelete"
          class="btn-delete"
          @click="deleteInquiry"
        >삭제</button>
      </div>
      </div>
    </template>

    <div v-else-if="loading" class="loading">로딩 중...</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth.js'

const route = useRoute()
const router = useRouter()
const { isLoggedIn, loginId, isAdmin } = useAuth()

const inquiry = ref(null)
const loading = ref(true)
const needPassword = ref(false)
const pwInput = ref('')
const pwError = ref('')
const verifying = ref(false)
const verifiedPw = ref('')

const canDelete = computed(() => {
  if (!inquiry.value || !isLoggedIn.value) return false
  return inquiry.value.memberLoginId === loginId.value || isAdmin()
})

async function loadDetail(pw) {
  loading.value = true
  const url = pw
    ? `/v1/api/board/inquiry/${route.params.id}?pw=${encodeURIComponent(pw)}`
    : `/v1/api/board/inquiry/${route.params.id}`
  try {
    const res = await fetch(url, { credentials: 'include' })
    if (res.ok) {
      inquiry.value = await res.json()
      needPassword.value = false
    } else if (res.status === 403) {
      needPassword.value = true
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function verifyPassword() {
  if (!pwInput.value) { pwError.value = '비밀번호를 입력하세요.'; return }
  verifying.value = true
  pwError.value = ''
  try {
    const res = await fetch(
      `/v1/api/board/inquiry/${route.params.id}?pw=${encodeURIComponent(pwInput.value)}`,
      { credentials: 'include' }
    )
    if (res.ok) {
      inquiry.value = await res.json()
      verifiedPw.value = pwInput.value
      needPassword.value = false
    } else {
      pwError.value = '비밀번호가 일치하지 않습니다.'
    }
  } catch {
    pwError.value = '오류가 발생했습니다.'
  } finally {
    verifying.value = false
  }
}

async function deleteInquiry() {
  if (!confirm('문의를 삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/board/inquiry/${route.params.id}`, {
    method: 'DELETE',
    credentials: 'include'
  })
  if (res.ok) router.push('/board/inquiry')
  else alert('삭제에 실패했습니다.')
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('ko-KR')
}

onMounted(() => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  loadDetail()
})
</script>

<style scoped>
.inquiry-detail-page { min-height: 60vh; }
.detail-container { max-width: 960px; margin: 0 auto; padding: 0 24px; }

.pw-gate {
  display: flex; justify-content: center; align-items: center;
  padding: 80px 16px;
}
.pw-gate-box {
  background: #fff; border: 1px solid #eee; border-radius: 12px;
  padding: 48px 40px; text-align: center; max-width: 400px; width: 100%;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
}
.pw-gate-icon { font-size: 2.5rem; margin-bottom: 16px; }
.pw-gate-title { font-size: 18px; font-weight: 700; margin-bottom: 8px; }
.pw-gate-desc { font-size: 13px; color: #888; line-height: 1.7; margin-bottom: 28px; }
.pw-form { text-align: left; }
.pw-input {
  width: 100%; border: 1.5px solid #ddd; border-radius: 6px;
  padding: 10px 12px; font-size: 14px; outline: none; box-sizing: border-box;
  transition: border-color .2s;
}
.pw-input:focus { border-color: #1a1a1a; }
.pw-error { color: #c0392b; font-size: 12px; margin-top: 6px; }
.pw-actions { display: flex; gap: 10px; margin-top: 16px; }

.detail-header {
  padding: 32px 0 24px; border-bottom: 1px solid #eee; margin-bottom: 28px;
}
.breadcrumb { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; font-size: 12px; }
.breadcrumb-link { color: #888; text-decoration: none; }
.breadcrumb-link:hover { color: #111; }
.breadcrumb-sep { color: #ccc; }
.breadcrumb-current { color: #555; }
.detail-meta { display: flex; gap: 8px; align-items: center; margin-bottom: 12px; }
.inquiry-cat {
  background: #f0ebe8; color: #8b6914; font-size: 11px; font-weight: 600;
  padding: 3px 8px; border-radius: 10px;
}
.inquiry-status { font-size: 12px; padding: 3px 10px; border-radius: 10px; font-weight: 600; }
.answered { background: #e8f5e9; color: #2e7d32; }
.pending  { background: #fff3e0; color: #e65100; }
.detail-title { font-size: 20px; font-weight: 700; margin-bottom: 10px; }
.detail-info { font-size: 12px; color: #999; display: flex; gap: 6px; align-items: center; }
.info-sep { color: #ddd; }

.detail-body { margin-bottom: 32px; }
.detail-content {
  background: #fafafa; border: 1px solid #eee; border-radius: 8px;
  padding: 20px; margin-bottom: 20px;
}
.detail-content p { font-size: 14px; color: #444; line-height: 1.8; white-space: pre-wrap; margin: 0; }
.detail-answer {
  background: #f0f8f0; border-left: 3px solid #1B3A2D; padding: 16px 18px;
  border-radius: 0 8px 8px 0;
}
.answer-label { font-size: 11px; font-weight: 700; color: #1B3A2D; display: block; margin-bottom: 8px; }
.detail-answer p { font-size: 14px; color: #444; margin: 0 0 6px; line-height: 1.7; }
.answer-date { font-size: 12px; color: #999; }

.detail-actions { display: flex; gap: 10px; }
.btn-back {
  padding: 9px 20px; background: #f5f5f5; color: #555; border: 1px solid #ddd;
  border-radius: 6px; cursor: pointer; font-size: 13px; font-weight: 600;
  transition: background .2s;
}
.btn-back:hover { background: #eee; }
.btn-verify {
  flex: 1; padding: 10px 20px; background: #1B3A2D; color: #F5F0E8; border: none;
  border-radius: 6px; cursor: pointer; font-size: 13px; font-weight: 700;
  transition: background .2s;
}
.btn-verify:hover:not(:disabled) { background: #2a5244; }
.btn-verify:disabled { opacity: .6; cursor: not-allowed; }
.btn-delete {
  padding: 9px 20px; background: none; color: #c0392b; border: 1px solid #c0392b;
  border-radius: 6px; cursor: pointer; font-size: 13px; font-weight: 600;
  transition: all .2s;
}
.btn-delete:hover { background: #c0392b; color: #fff; }
.loading { text-align: center; padding: 80px; color: #888; }
</style>

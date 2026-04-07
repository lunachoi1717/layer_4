<template>
  <div class="admin-page">
    <h1 class="admin-page-title">회원 관리</h1>

    <div class="admin-toolbar">
      <input v-model="keyword" class="admin-search-input" placeholder="이름 또는 이메일 검색" @keyup.enter="loadMembers" />
      <button class="admin-btn admin-btn--ghost admin-btn--sm" @click="loadMembers">검색</button>
    </div>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>이름</th><th>이메일</th><th>등급</th><th>상태</th><th>가입일</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in members" :key="m.id">
            <td>#{{ m.id }}</td>
            <td style="font-weight:600">{{ m.name }}</td>
            <td>{{ m.loginId }}</td>
            <td>
              <select class="admin-form-select" style="font-size:0.78rem;padding:4px 8px" :value="m.grade" @change="changeGrade(m.id, $event.target.value)">
                <option v-for="g in grades" :key="g" :value="g">{{ g }}</option>
              </select>
            </td>
            <td>
              <span class="grade-badge" :class="statusClass(m.status)">{{ m.status }}</span>
            </td>
            <td>{{ formatDate(m.createdAt) }}</td>
            <td>
              <div class="admin-btn-group">
                <button
                  v-if="m.status === 'ACTIVE' && m.role !== 'ROLE_ADMIN'"
                  class="admin-btn admin-btn--danger admin-btn--sm"
                  @click="suspend(m.id)"
                >강퇴</button>
                <button
                  v-if="m.status === 'SUSPENDED'"
                  class="admin-btn admin-btn--primary admin-btn--sm"
                  @click="activate(m.id)"
                >복구</button>
              </div>
            </td>
          </tr>
          <tr v-if="members.length === 0">
            <td colspan="7" style="text-align:center;color:#9CA3AF;padding:40px">조회된 회원이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const members = ref([])
const keyword = ref('')
const loading = ref(false)
const toast = ref('')
const grades = ['SAPPHIRE', 'RUBY', 'EMERALD', 'GOLD', 'DIAMOND']

function statusClass(status) {
  if (status === 'ACTIVE') return 'grade-emerald'
  if (status === 'SUSPENDED') return 'grade-ruby'
  return 'grade-sapphire'
}

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2000)
}
function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

async function loadMembers() {
  loading.value = true
  try {
    const url = keyword.value
      ? `/v1/api/admin/members?keyword=${encodeURIComponent(keyword.value)}`
      : '/v1/api/admin/members'
    const res = await fetch(url, { credentials: 'include' })
    if (res.ok) members.value = await res.json()
  } catch {} finally { loading.value = false }
}

async function suspend(id) {
  if (!confirm('해당 회원을 강퇴(정지)하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/members/${id}/suspend`, { method: 'PATCH', credentials: 'include' })
  if (res.ok) { loadMembers(); showToast('회원이 정지되었습니다.') }
}

async function activate(id) {
  const res = await fetch(`/v1/api/admin/members/${id}/activate`, { method: 'PATCH', credentials: 'include' })
  if (res.ok) { loadMembers(); showToast('회원이 복구되었습니다.') }
}

async function changeGrade(id, grade) {
  const res = await fetch(`/v1/api/admin/members/${id}/grade`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ grade })
  })
  if (res.ok) { loadMembers(); showToast('등급이 변경되었습니다.') }
}

onMounted(loadMembers)
</script>

<template>
  <div class="admin-page">
    <h1 class="admin-page-title">회원 관리</h1>

    <div class="admin-toolbar">
      <input v-model="keyword" class="admin-search" placeholder="이름 또는 이메일 검색" @keyup.enter="loadMembers" />
      <button class="btn-admin-search" @click="loadMembers">검색</button>
    </div>

    <div class="admin-card">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>이름</th><th>이메일</th><th>등급</th><th>상태</th><th>가입일</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in members" :key="m.id">
            <td>{{ m.id }}</td>
            <td>{{ m.name }}</td>
            <td>{{ m.loginId }}</td>
            <td>
              <select class="grade-select" :value="m.grade" @change="changeGrade(m.id, $event.target.value)">
                <option v-for="g in grades" :key="g" :value="g">{{ g }}</option>
              </select>
            </td>
            <td>
              <span class="member-status" :class="`mstatus-${m.status?.toLowerCase()}`">{{ m.status }}</span>
            </td>
            <td>{{ formatDate(m.createdAt) }}</td>
            <td class="action-cell">
              <button
                v-if="m.status === 'ACTIVE' && m.role !== 'ROLE_ADMIN'"
                class="btn-admin-danger"
                @click="suspend(m.id)"
              >강퇴</button>
              <button
                v-if="m.status === 'SUSPENDED'"
                class="btn-admin-success"
                @click="activate(m.id)"
              >복구</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="toast" class="toast-msg">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const members = ref([])
const keyword = ref('')
const toast = ref('')
const grades = ['BRONZE', 'SILVER', 'GOLD', 'VIP']

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2000)
}
function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

async function loadMembers() {
  try {
    const url = keyword.value
      ? `/v1/api/admin/members?keyword=${encodeURIComponent(keyword.value)}`
      : '/v1/api/admin/members'
    const res = await fetch(url, { credentials: 'include' })
    if (res.ok) members.value = await res.json()
  } catch {}
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

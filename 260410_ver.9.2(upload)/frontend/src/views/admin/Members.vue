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
            <th @click="toggleSort('id')" class="sortable-th">
              ID <span class="sort-icon">{{ sortField === 'id' ? (sortAsc ? '↑' : '↓') : '↕' }}</span>
            </th>
            <th @click="toggleSort('name')" class="sortable-th">
              이름 <span class="sort-icon">{{ sortField === 'name' ? (sortAsc ? '↑' : '↓') : '↕' }}</span>
            </th>
            <th>이메일</th>
            <th>등급</th>
            <th>상태</th>
            <th @click="toggleSort('createdAt')" class="sortable-th">
              가입일 <span class="sort-icon">{{ sortField === 'createdAt' ? (sortAsc ? '↑' : '↓') : '↕' }}</span>
            </th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in sortedMembers" :key="m.id" @click="openDetail(m)" class="clickable-row">
            <td>#{{ m.id }}</td>
            <td style="font-weight:600">{{ m.name }}</td>
            <td>{{ m.loginId }}</td>
            <td @click.stop>
              <select class="admin-form-select" style="font-size:0.78rem;padding:4px 8px" :value="m.grade" @change="changeGrade(m.id, $event.target.value)">
                <option v-for="g in grades" :key="g" :value="g">{{ g }}</option>
              </select>
            </td>
            <td>
              <span class="grade-badge" :class="statusClass(m.status)">{{ m.status }}</span>
            </td>
            <td>{{ formatDate(m.createdAt) }}</td>
            <td @click.stop>
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
          <tr v-if="sortedMembers.length === 0">
            <td colspan="7" style="text-align:center;color:#9CA3AF;padding:40px">조회된 회원이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="detailTarget" class="admin-modal-overlay" @click.self="detailTarget = null">
      <div class="admin-modal" style="max-width:480px">
        <h3 class="admin-modal-title">회원 상세 정보</h3>
        <table class="detail-table">
          <tr><th>ID</th><td>#{{ detailTarget.id }}</td></tr>
          <tr><th>이름</th><td>{{ detailTarget.name }}</td></tr>
          <tr><th>이메일</th><td>{{ detailTarget.loginId }}</td></tr>
          <tr><th>전화번호</th><td>{{ detailTarget.phone || '-' }}</td></tr>
          <tr><th>주소</th><td>{{ detailTarget.address || '-' }}</td></tr>
          <tr><th>역할</th><td>{{ detailTarget.role }}</td></tr>
          <tr><th>등급</th><td>{{ detailTarget.grade }}</td></tr>
          <tr><th>상태</th><td>{{ detailTarget.status }}</td></tr>
          <tr><th>가입일</th><td>{{ formatDate(detailTarget.createdAt) }}</td></tr>
        </table>
        <div class="admin-modal-actions">
          <button class="admin-btn admin-btn--ghost" @click="detailTarget = null">닫기</button>
        </div>
      </div>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const members = ref([])
const keyword = ref('')
const loading = ref(false)
const toast = ref('')
const detailTarget = ref(null)
const grades = ['SAPPHIRE', 'RUBY', 'EMERALD', 'GOLD', 'DIAMOND']

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

const sortedMembers = computed(() => {
  const list = [...members.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (f === 'createdAt') { va = new Date(va); vb = new Date(vb) }
    if (va < vb) return sortAsc.value ? -1 : 1
    if (va > vb) return sortAsc.value ? 1 : -1
    return 0
  })
})

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
function openDetail(m) {
  detailTarget.value = m
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

<style scoped>
.sortable-th {
  cursor: pointer;
  user-select: none;
  white-space: nowrap;
}
.sortable-th:hover { background: #F3F4F6; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }
.clickable-row { cursor: pointer; }
.clickable-row:hover td { background: #F9FAFB; }
.detail-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
.detail-table th {
  width: 90px; text-align: left; padding: 8px 12px;
  background: #F9FAFB; color: #6B7280; font-weight: 600;
  border-bottom: 1px solid #F3F4F6;
}
.detail-table td {
  padding: 8px 12px; color: #111827;
  border-bottom: 1px solid #F3F4F6;
}
</style>

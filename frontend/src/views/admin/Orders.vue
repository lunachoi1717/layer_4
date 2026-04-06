<template>
  <div class="admin-page">
    <h1 class="admin-page-title">주문 관리</h1>

    <div class="admin-toolbar">
      <select v-model="statusFilter" class="admin-search" @change="loadOrders">
        <option value="">전체</option>
        <option value="PENDING_PAYMENT">결제대기</option>
        <option value="PAID">결제완료</option>
        <option value="SHIPPING">배송중</option>
        <option value="DELIVERED">배송완료</option>
        <option value="CANCELLED">취소됨</option>
      </select>
      <div class="stats-mini">
        <span>총 매출: <strong>{{ totalSales.toLocaleString() }}원</strong></span>
        <span>건수: <strong>{{ orders.length }}</strong></span>
      </div>
    </div>

    <div class="admin-card">
      <table class="admin-table">
        <thead>
          <tr>
            <th>주문번호</th><th>주문자</th><th>수령인</th><th>결제금액</th>
            <th>결제수단</th><th>상태</th><th>주문일</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td>#{{ o.id }}</td>
            <td>{{ o.memberName }}</td>
            <td>{{ o.name }}</td>
            <td>{{ o.amount?.toLocaleString() }}원</td>
            <td>{{ o.payment }}</td>
            <td>
              <span class="order-status" :class="`status-${o.status?.toLowerCase()}`">
                {{ o.statusLabel || o.status }}
              </span>
            </td>
            <td>{{ formatDate(o.createdAt) }}</td>
            <td class="action-cell">
              <select class="grade-select" :value="o.status" @change="changeStatus(o.id, $event.target.value)">
                <option value="PENDING_PAYMENT">결제대기</option>
                <option value="PAID">결제완료</option>
                <option value="SHIPPING">배송중</option>
                <option value="DELIVERED">배송완료</option>
                <option value="CANCELLED">취소됨</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="toast" class="toast-msg">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const orders = ref([])
const statusFilter = ref('')
const toast = ref('')

const totalSales = computed(() =>
  orders.value.filter(o => ['PAID','SHIPPING','DELIVERED'].includes(o.status)).reduce((s, o) => s + (o.amount || 0), 0)
)

function showToast(msg) {
  toast.value = msg
  setTimeout(() => { toast.value = '' }, 2000)
}
function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

async function loadOrders() {
  try {
    const url = statusFilter.value
      ? `/v1/api/admin/orders?status=${statusFilter.value}`
      : '/v1/api/admin/orders'
    const res = await fetch(url, { credentials: 'include' })
    if (res.ok) orders.value = await res.json()
  } catch {}
}

async function changeStatus(id, status) {
  const res = await fetch(`/v1/api/admin/orders/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ status })
  })
  if (res.ok) { loadOrders(); showToast('상태가 변경되었습니다.') }
}

onMounted(loadOrders)
</script>

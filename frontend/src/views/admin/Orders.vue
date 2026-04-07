<template>
  <div class="admin-page">
    <h1 class="admin-page-title">주문 관리</h1>

    <div class="admin-toolbar">
      <select v-model="statusFilter" class="admin-form-select" style="width:160px" @change="loadOrders">
        <option value="">전체 상태</option>
        <option value="PENDING_PAYMENT">결제대기</option>
        <option value="PAID">결제완료</option>
        <option value="SHIPPING">배송중</option>
        <option value="DELIVERED">배송완료</option>
        <option value="CANCELLED">취소됨</option>
      </select>
      <div class="stats-mini">
        <span>총 매출: <strong>{{ totalSales.toLocaleString() }}원</strong></span>
        <span style="margin-left:16px">건수: <strong>{{ orders.length }}</strong></span>
      </div>
    </div>

    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
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
            <td style="font-weight:600">{{ o.name }}</td>
            <td>{{ o.amount?.toLocaleString() }}원</td>
            <td>{{ o.payment }}</td>
            <td>
              <span class="order-status-badge" :class="`order-${o.status?.toLowerCase()}`">
                {{ statusLabel(o.status) }}
              </span>
            </td>
            <td>{{ formatDate(o.createdAt) }}</td>
            <td>
              <select class="admin-form-select" style="font-size:0.78rem;padding:4px 8px" :value="o.status" @change="changeStatus(o.id, $event.target.value)">
                <option value="PENDING_PAYMENT">결제대기</option>
                <option value="PAID">결제완료</option>
                <option value="SHIPPING">배송중</option>
                <option value="DELIVERED">배송완료</option>
                <option value="CANCELLED">취소됨</option>
              </select>
            </td>
          </tr>
          <tr v-if="orders.length === 0">
            <td colspan="8" style="text-align:center;color:#9CA3AF;padding:40px">주문 내역이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="toast" class="v-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const orders = ref([])
const statusFilter = ref('')
const loading = ref(false)
const toast = ref('')

const STATUS_LABELS = {
  PENDING_PAYMENT: '결제대기',
  PAID: '결제완료',
  SHIPPING: '배송중',
  DELIVERED: '배송완료',
  CANCELLED: '취소됨',
}
function statusLabel(s) { return STATUS_LABELS[s] || s }

const totalSales = computed(() =>
  orders.value.filter(o => ['PAID','SHIPPING','DELIVERED'].includes(o.status))
    .reduce((s, o) => s + (o.amount || 0), 0)
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
  loading.value = true
  try {
    const url = statusFilter.value
      ? `/v1/api/admin/orders?status=${statusFilter.value}`
      : '/v1/api/admin/orders'
    const res = await fetch(url, { credentials: 'include' })
    if (res.ok) orders.value = await res.json()
  } catch {} finally { loading.value = false }
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

<style scoped>
.stats-mini { display: flex; align-items: center; font-size: 0.82rem; color: #6B7280; }
.stats-mini strong { color: #111827; }
.order-status-badge {
  display: inline-block; font-size: 0.68rem; font-weight: 600;
  padding: 3px 10px; border-radius: 20px;
}
.order-pending_payment { background: #FEF3C7; color: #92400E; }
.order-paid           { background: #DCFCE7; color: #166534; }
.order-shipping       { background: #DBEAFE; color: #1E40AF; }
.order-delivered      { background: #F3F4F6; color: #374151; }
.order-cancelled      { background: #FEE2E2; color: #991B1B; }
</style>

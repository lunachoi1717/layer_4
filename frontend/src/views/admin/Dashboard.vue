<template>
  <div class="admin-page">
    <h1 class="admin-page-title">대시보드</h1>

    <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
    <template v-else>

      <div class="stats-grid stats-grid--6">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-body">
            <p class="stat-label">전체 회원</p>
            <p class="stat-value">{{ stats.totalMembers?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📦</div>
          <div class="stat-body">
            <p class="stat-label">전체 상품</p>
            <p class="stat-value">{{ stats.totalItems?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🛒</div>
          <div class="stat-body">
            <p class="stat-label">전체 주문</p>
            <p class="stat-value">{{ stats.totalOrders?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-body">
            <p class="stat-label">총 매출</p>
            <p class="stat-value stat-value--sales">{{ stats.totalSales?.toLocaleString() }}원</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⏳</div>
          <div class="stat-body">
            <p class="stat-label">결제대기</p>
            <p class="stat-value">{{ stats.pendingPaymentOrders?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-body">
            <p class="stat-label">결제완료</p>
            <p class="stat-value">{{ stats.paidOrders?.toLocaleString() }}</p>
          </div>
        </div>
      </div>

      <div class="stats-grid stats-grid--5">
        <div class="stat-card">
          <div class="stat-icon">🚚</div>
          <div class="stat-body">
            <p class="stat-label">배송중</p>
            <p class="stat-value">{{ stats.shippingOrders?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📬</div>
          <div class="stat-body">
            <p class="stat-label">배송완료</p>
            <p class="stat-value">{{ stats.deliveredOrders?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">❌</div>
          <div class="stat-body">
            <p class="stat-label">취소된 주문</p>
            <p class="stat-value">{{ stats.cancelledOrders?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-body">
            <p class="stat-label">전체 리뷰</p>
            <p class="stat-value">{{ stats.totalReviews?.toLocaleString() }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💬</div>
          <div class="stat-body">
            <p class="stat-label">미답변 문의</p>
            <p class="stat-value">{{ stats.unansweredInquiries?.toLocaleString() }}</p>
          </div>
        </div>
      </div>

      <div v-if="stats.lowStockItems?.length" class="admin-card mt-4">
        <h2 class="admin-card-title">⚠️ 재고 부족 상품 (5개 이하)</h2>
        <table class="admin-table">
          <thead><tr><th>ID</th><th>상품명</th><th>재고</th></tr></thead>
          <tbody>
            <tr v-for="item in stats.lowStockItems" :key="item.id">
              <td>#{{ item.id }}</td>
              <td>{{ item.name }}</td>
              <td><span class="low-stock">{{ item.stockCount }}개</span></td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="admin-card mt-4">
        <h2 class="admin-card-title">최근 주문</h2>
        <table class="admin-table">
          <thead>
            <tr>
              <th>주문번호</th>
              <th>주문자</th>
              <th>결제금액</th>
              <th>상태</th>
              <th>일시</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in stats.recentOrders" :key="order.id">
              <td>#{{ order.id }}</td>
              <td>{{ order.name }}</td>
              <td>{{ order.amount?.toLocaleString() }}원</td>
              <td>
                <span class="order-status" :class="`status-${order.status?.toLowerCase()}`">
                  {{ order.statusLabel || order.status }}
                </span>
              </td>
              <td>{{ formatDate(order.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({})
const loading = ref(true)

function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

async function loadStats() {
  try {
    const res = await fetch('/v1/api/admin/dashboard', { credentials: 'include' })
    if (res.ok) stats.value = await res.json()
  } catch {} finally { loading.value = false }
}

onMounted(loadStats)
</script>

<style scoped>
.stats-grid--6 { grid-template-columns: repeat(6, 1fr) !important; margin-bottom: 12px; }
.stats-grid--5 { grid-template-columns: repeat(5, 1fr) !important; margin-bottom: 20px; }
.stat-value--sales { white-space: nowrap; font-size: clamp(0.75rem, 1.2vw, 1rem); }
.low-stock { color: #c62828; font-weight: 700; }
.status-pending_payment { color: #e65100; }
.status-paid { color: #1565c0; }
.status-shipping { color: #6a1b9a; }
.status-delivered { color: #2e7d32; }
.status-cancelled { color: #c62828; }
</style>

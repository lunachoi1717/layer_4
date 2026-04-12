<template>
  <div class="admin-page">
    <h1 class="admin-page-title">쿠폰 관리</h1>

    <!-- Toolbar -->
    <div class="admin-toolbar">
      <button class="admin-btn admin-btn--primary" @click="openCreate">+ 쿠폰 생성</button>
    </div>

    <!-- Table -->
    <div class="admin-card">
      <div v-if="loading" class="loading-box"><div class="spinner"></div></div>
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th @click="toggleSort('id')" class="sortable-th">ID <span class="sort-icon">{{ sortField==='id'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleSort('name')" class="sortable-th">쿠폰명 <span class="sort-icon">{{ sortField==='name'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>코드</th>
            <th @click="toggleSort('discountValue')" class="sortable-th">할인 <span class="sort-icon">{{ sortField==='discountValue'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>대상 등급</th>
            <th>최소금액</th>
            <th @click="toggleSort('validTo')" class="sortable-th">유효기간 <span class="sort-icon">{{ sortField==='validTo'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th @click="toggleSort('isActive')" class="sortable-th">상태 <span class="sort-icon">{{ sortField==='isActive'?(sortAsc?'↑':'↓'):'↕' }}</span></th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in sortedCoupons" :key="c.id">
            <td>#{{ c.id }}</td>
            <td style="font-weight:600">{{ c.name }}</td>
            <td><code class="coupon-code">{{ c.code }}</code></td>
            <td>
              <span v-if="c.discountType === 'FIXED'">{{ c.discountValue?.toLocaleString() }}원</span>
              <span v-else>{{ c.discountValue }}%</span>
            </td>
            <td>
              <span v-if="c.targetGrade" class="grade-badge" :class="`grade-${c.targetGrade.toLowerCase()}`">{{ c.targetGrade }}</span>
              <span v-else class="grade-badge grade-sapphire">전체</span>
            </td>
            <td>{{ c.minOrderAmount ? c.minOrderAmount.toLocaleString() + '원' : '제한없음' }}</td>
            <td>{{ c.validFrom }} ~ {{ c.validTo }}</td>
            <td>
              <span class="status-badge" :class="c.isActive ? 'badge-active' : 'badge-inactive'">
                {{ c.isActive ? '활성' : '비활성' }}
              </span>
            </td>
            <td>
              <div class="admin-btn-group">
                <button class="admin-btn admin-btn--ghost admin-btn--sm" @click="toggleCoupon(c)">
                  {{ c.isActive ? '비활성화' : '활성화' }}
                </button>
                <button class="admin-btn admin-btn--warning admin-btn--sm" @click="openEdit(c)">수정</button>
                <button class="admin-btn admin-btn--danger admin-btn--sm" @click="deleteCoupon(c.id)">삭제</button>
              </div>
            </td>
          </tr>
          <tr v-if="coupons.length === 0">
            <td colspan="9" style="text-align:center;color:#9CA3AF;padding:40px">등록된 쿠폰이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create / Edit Modal -->
    <div v-if="showModal" class="admin-modal-overlay" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal-title">{{ editTarget ? '쿠폰 수정' : '쿠폰 생성' }}</h2>

        <div class="admin-form-group">
          <label class="admin-form-label">쿠폰명 *</label>
          <input v-model="form.name" class="admin-form-input" placeholder="예: 신규 회원 할인 쿠폰" required />
        </div>
        <div v-if="!editTarget" class="admin-form-group">
          <label class="admin-form-label">쿠폰 코드 * (영문 대문자)</label>
          <input v-model="form.code" class="admin-form-input" placeholder="예: WELCOME2025" @input="form.code = form.code.toUpperCase()" />
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">할인 방식 *</label>
          <select v-model="form.discountType" class="admin-form-select">
            <option value="FIXED">정액 (원)</option>
            <option value="PERCENT">정률 (%)</option>
          </select>
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">할인 값 * ({{ form.discountType === 'FIXED' ? '원' : '%' }})</label>
          <input v-model.number="form.discountValue" class="admin-form-input" type="number" min="1" />
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">대상 등급 (미선택 = 전체)</label>
          <select v-model="form.targetGrade" class="admin-form-select">
            <option value="">전체 회원</option>
            <option v-for="g in grades" :key="g" :value="g">{{ g }}</option>
          </select>
        </div>
        <div class="admin-form-group">
          <label class="admin-form-label">최소 주문 금액 (원, 0 = 제한 없음)</label>
          <input v-model.number="form.minOrderAmount" class="admin-form-input" type="number" min="0" />
        </div>
        <div class="form-row-2">
          <div class="admin-form-group">
            <label class="admin-form-label">유효 시작일 *</label>
            <input v-model="form.validFrom" class="admin-form-input" type="date" />
          </div>
          <div class="admin-form-group">
            <label class="admin-form-label">유효 종료일 *</label>
            <input v-model="form.validTo" class="admin-form-input" type="date" />
          </div>
        </div>

        <p v-if="formError" class="form-error">{{ formError }}</p>

        <div class="admin-modal-actions">
          <button class="admin-btn admin-btn--ghost" @click="closeModal">취소</button>
          <button class="admin-btn admin-btn--primary" :disabled="saving" @click="saveCoupon">
            {{ saving ? '저장 중...' : (editTarget ? '수정 완료' : '생성') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const coupons = ref([])
const loading = ref(false)

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

const sortedCoupons = computed(() => {
  const list = [...coupons.value]
  const f = sortField.value
  return list.sort((a, b) => {
    let va = a[f], vb = b[f]
    if (typeof va === 'string' && typeof vb === 'string') {
      return sortAsc.value ? va.localeCompare(vb) : vb.localeCompare(va)
    }
    if (va < vb) return sortAsc.value ? -1 : 1
    if (va > vb) return sortAsc.value ? 1 : -1
    return 0
  })
})
const showModal = ref(false)
const editTarget = ref(null)
const saving = ref(false)
const formError = ref('')

const grades = ['SAPPHIRE', 'RUBY', 'EMERALD', 'GOLD', 'DIAMOND']

const defaultForm = () => ({
  name: '', code: '', discountType: 'FIXED', discountValue: 0,
  targetGrade: '', minOrderAmount: 0, validFrom: '', validTo: ''
})
const form = ref(defaultForm())

async function loadCoupons() {
  loading.value = true
  try {
    const res = await fetch('/v1/api/admin/coupons', { credentials: 'include' })
    if (res.ok) coupons.value = await res.json()
  } catch {} finally { loading.value = false }
}

function openCreate() {
  editTarget.value = null
  form.value = defaultForm()
  formError.value = ''
  showModal.value = true
}

function openEdit(c) {
  editTarget.value = c
  form.value = {
    name: c.name, code: c.code, discountType: c.discountType,
    discountValue: c.discountValue, targetGrade: c.targetGrade || '',
    minOrderAmount: c.minOrderAmount || 0,
    validFrom: c.validFrom, validTo: c.validTo
  }
  formError.value = ''
  showModal.value = true
}

function closeModal() { showModal.value = false }

async function saveCoupon() {
  formError.value = ''
  if (!form.value.name || !form.value.discountValue || !form.value.validFrom || !form.value.validTo) {
    formError.value = '필수 항목을 모두 입력하세요.'; return
  }
  saving.value = true
  try {
    const body = { ...form.value, targetGrade: form.value.targetGrade || null }
    const url = editTarget.value ? `/v1/api/admin/coupons/${editTarget.value.id}` : '/v1/api/admin/coupons'
    const method = editTarget.value ? 'PUT' : 'POST'
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(body)
    })
    if (res.ok) { closeModal(); loadCoupons() }
    else { const t = await res.text(); formError.value = t || '저장에 실패했습니다.' }
  } catch { formError.value = '네트워크 오류가 발생했습니다.' }
  finally { saving.value = false }
}

async function toggleCoupon(c) {
  const res = await fetch(`/v1/api/admin/coupons/${c.id}/toggle`, { method: 'PATCH', credentials: 'include' })
  if (res.ok) loadCoupons()
}

async function deleteCoupon(id) {
  if (!confirm('이 쿠폰을 삭제하시겠습니까?')) return
  const res = await fetch(`/v1/api/admin/coupons/${id}`, { method: 'DELETE', credentials: 'include' })
  if (res.ok) loadCoupons()
}

onMounted(loadCoupons)
</script>

<style scoped>
.sortable-th { cursor: pointer; user-select: none; white-space: nowrap; }
.sortable-th:hover { background: #F3F4F6; }
.sort-icon { font-size: 0.7rem; color: #9CA3AF; margin-left: 4px; }
.coupon-code {
  background: #F3F4F6;
  border: 1px solid #E5E7EB;
  border-radius: 4px;
  padding: 2px 8px;
  font-size: 0.78rem;
  font-family: 'Courier New', monospace;
  color: #1B3A2D;
  font-weight: 700;
  letter-spacing: 0.05em;
}
.status-badge {
  display: inline-block;
  font-size: 0.68rem;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
}
.badge-active   { background: #DCFCE7; color: #166534; }
.badge-inactive { background: #F3F4F6; color: #6B7280; }
.form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.form-error { color: #DC2626; font-size: 0.8rem; margin-top: 4px; }
</style>

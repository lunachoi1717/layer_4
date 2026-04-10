<template>
  <div class="v-auth-page">
    <div class="v-auth-panel">

      <!-- Logo -->
      <RouterLink to="/" class="v-auth-logo">
        <span class="v-auth-logo-en">VENTALIZE</span>
        <span class="v-auth-logo-kr">벙딸리제</span>
      </RouterLink>

      <h1 class="v-auth-title">Sign In</h1>
      <p class="v-auth-sub t-caption">Welcome back</p>

      <form class="v-auth-form" @submit.prevent="handleLogin">
        <div class="v-field">
          <label class="v-field-label">Email</label>
          <input
            v-model="loginId"
            type="email"
            class="v-field-input"
            placeholder="your@email.com"
            required
            autocomplete="email"
          />
        </div>
        <div class="v-field">
          <label class="v-field-label">Password</label>
          <input
            v-model="loginPw"
            type="password"
            class="v-field-input"
            placeholder="••••••••"
            required
            autocomplete="current-password"
          />
        </div>

        <p v-if="error" class="v-auth-error">{{ error }}</p>

        <button type="submit" class="v-auth-btn" :disabled="submitting">
          {{ submitting ? 'Signing in…' : 'Sign In' }}
        </button>
      </form>

      <p class="v-auth-switch t-caption">
        New to Ventalize?&nbsp;
        <RouterLink to="/register" class="v-auth-switch-link">Create account</RouterLink>
      </p>
    </div>

    <!-- Editorial aside -->
    <div class="v-auth-aside">
      <div class="v-auth-aside-overlay"></div>
      <div class="v-auth-aside-text">
        <p class="v-auth-aside-quote">"Crafted with intention,<br>worn with purpose."</p>
        <p class="v-auth-aside-brand t-caption">VENTALIZE · 벙딸리제</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth.js'

const router = useRouter()
const { setLogin } = useAuth()

const loginId   = ref('')
const loginPw   = ref('')
const error     = ref('')
const submitting = ref(false)

async function handleLogin() {
  error.value = ''
  submitting.value = true
  try {
    const res = await fetch('/v1/api/account/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ loginId: loginId.value, loginPw: loginPw.value })
    })
    if (res.ok) {
      const data = await res.json()
      setLogin(data.loginId, data.name, data.role, data.grade)
      router.push(data.role === 'ROLE_ADMIN' ? '/admin/dashboard' : '/')
    } else if (res.status === 404) {
      error.value = '이메일 또는 비밀번호가 올바르지 않습니다.'
    } else {
      error.value = '로그인에 실패했습니다. 다시 시도해주세요.'
    }
  } catch {
    error.value = '네트워크 오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.v-auth-page {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 100vh;
}

/* Left panel */
.v-auth-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 72px 80px;
  background: #fff;
}

.v-auth-logo {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  margin-bottom: 56px;
}
.v-auth-logo-en {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  font-weight: 400;
  letter-spacing: 0.35em;
  color: #111;
}
.v-auth-logo-kr {
  font-size: 0.6rem;
  letter-spacing: 0.25em;
  color: #7A7269;
  margin-top: 2px;
}

.v-auth-title {
  font-family: var(--font-serif);
  font-size: 2.2rem;
  font-weight: 400;
  color: #111;
  margin-bottom: 6px;
}
.v-auth-sub { color: #7A7269; margin-bottom: 40px; }

.v-auth-form { display: flex; flex-direction: column; gap: 20px; margin-bottom: 32px; }

.v-field { display: flex; flex-direction: column; gap: 8px; }
.v-field-label {
  font-size: 0.68rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #7A7269;
}
.v-field-input {
  border: none;
  border-bottom: 1px solid #E8E2D9;
  padding: 10px 0;
  font-size: 0.95rem;
  color: #111;
  background: transparent;
  outline: none;
  transition: border-color 0.25s;
  font-family: inherit;
}
.v-field-input:focus { border-bottom-color: #1B3A2D; }
.v-field-input::placeholder { color: #C9B89A; }

.v-auth-error {
  font-size: 0.78rem;
  color: #8B2020;
  background: rgba(139,32,32,0.06);
  padding: 10px 14px;
  margin-top: -8px;
}

.v-auth-btn {
  margin-top: 8px;
  background: #1B3A2D;
  color: #F5F0E8;
  border: none;
  padding: 15px 32px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.25s;
}
.v-auth-btn:hover:not(:disabled) { background: #4A6741; }
.v-auth-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.v-auth-switch { color: #7A7269; }
.v-auth-switch-link {
  color: #1B3A2D;
  font-weight: 600;
  text-decoration: none;
  transition: opacity 0.2s;
}
.v-auth-switch-link:hover { opacity: 0.7; }

/* Right aside */
.v-auth-aside {
  position: relative;
  background: url('/images/bags2_banner.png') center/cover no-repeat;
  overflow: hidden;
}
.v-auth-aside-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, rgba(27,58,45,0.85) 0%, rgba(0,0,0,0.4) 100%);
}
.v-auth-aside-text {
  position: absolute;
  bottom: 72px;
  left: 56px;
  right: 56px;
}
.v-auth-aside-quote {
  font-family: var(--font-serif);
  font-size: 1.6rem;
  font-weight: 400;
  color: #F5F0E8;
  line-height: 1.5;
  margin-bottom: 16px;
  font-style: italic;
}
.v-auth-aside-brand { color: rgba(245,240,232,0.5); letter-spacing: 0.15em; }

@media (max-width: 768px) {
  .v-auth-page { grid-template-columns: 1fr; }
  .v-auth-aside { display: none; }
  .v-auth-panel { padding: 56px 32px; }
}
</style>

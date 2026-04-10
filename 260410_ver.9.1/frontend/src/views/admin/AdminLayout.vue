<template>
  <div class="vad-layout">
    <!-- Sidebar -->
    <aside class="vad-sidebar">
      <div class="vad-brand">
        <RouterLink to="/admin/dashboard" class="vad-brand-link">
          <span class="vad-brand-name">VENTALIZE</span>
          <span class="vad-brand-sub">Admin Console</span>
        </RouterLink>
      </div>

      <nav class="vad-nav">
        <p class="vad-nav-section">OVERVIEW</p>
        <RouterLink to="/admin/dashboard" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zm0 6a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zm10 0a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z"/></svg>
          대시보드
        </RouterLink>

        <p class="vad-nav-section">MANAGEMENT</p>
        <RouterLink to="/admin/members" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/></svg>
          회원 관리
        </RouterLink>
        <RouterLink to="/admin/products" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 2a4 4 0 00-4 4v1H5a1 1 0 00-.994.89l-1 9A1 1 0 004 18h12a1 1 0 00.994-1.11l-1-9A1 1 0 0015 7h-1V6a4 4 0 00-4-4zm2 5V6a2 2 0 10-4 0v1h4zm-6 3a1 1 0 112 0 1 1 0 01-2 0zm7-1a1 1 0 100 2 1 1 0 000-2z" clip-rule="evenodd"/></svg>
          상품 관리
        </RouterLink>
        <RouterLink to="/admin/orders" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3zM16 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM6.5 18a1.5 1.5 0 100-3 1.5 1.5 0 000 3z"/></svg>
          주문 관리
        </RouterLink>
        <RouterLink to="/admin/coupons" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5 5a3 3 0 015-2.236A3 3 0 0114.83 6H16a2 2 0 110 4h-5V9a1 1 0 10-2 0v1H4a2 2 0 110-4h1.17C5.06 5.687 5 5.35 5 5zm4 1V5a1 1 0 10-1 1h1zm3 0a1 1 0 10-1-1v1h1z" clip-rule="evenodd"/><path d="M9 11H3v5a2 2 0 002 2h4v-7zM11 18h4a2 2 0 002-2v-5h-6v7z"/></svg>
          쿠폰 관리
        </RouterLink>

        <p class="vad-nav-section">COMMUNITY</p>
        <RouterLink to="/admin/reviews" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>
          리뷰 관리
        </RouterLink>
        <RouterLink to="/admin/qna" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clip-rule="evenodd"/></svg>
          Q&A 관리
        </RouterLink>
        <RouterLink to="/admin/board" class="vad-nav-item" active-class="vad-nav-item--active">
          <svg class="vad-nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M2 5a2 2 0 012-2h8a2 2 0 012 2v10a2 2 0 002 2H4a2 2 0 01-2-2V5zm3 1h6v4H5V6zm6 6H5v2h6v-2z" clip-rule="evenodd"/><path d="M15 7h1a2 2 0 012 2v5.5a1.5 1.5 0 01-3 0V7z"/></svg>
          게시판 관리
        </RouterLink>
      </nav>

      <div class="vad-sidebar-foot">
        <RouterLink to="/" class="vad-back-link">
          <svg viewBox="0 0 20 20" fill="currentColor" width="14" height="14"><path fill-rule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd"/></svg>
          쇼핑몰로 이동
        </RouterLink>
      </div>
    </aside>

    <!-- Main content -->
    <div class="vad-main">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
</script>

<style>
/* Global admin styles (not scoped — shared across admin views) */
:root {
  --vad-sidebar-w: 224px;
  --vad-bg: #F7F8FA;
  --vad-surface: #FFFFFF;
  --vad-border: #E5E7EB;
  --vad-text: #111827;
  --vad-text-muted: #6B7280;
  --vad-primary: #1B3A2D;
  --vad-primary-light: rgba(27,58,45,0.08);
  --vad-accent: #B89C6E;
  --vad-danger: #DC2626;
  --vad-warning: #D97706;
  --vad-success: #16A34A;
  --vad-info: #2563EB;
  --vad-radius: 8px;
  --vad-shadow: 0 1px 3px rgba(0,0,0,0.08), 0 1px 2px rgba(0,0,0,0.04);
}
</style>

<style scoped>
.vad-layout {
  display: flex;
  min-height: 100vh;
  background: var(--vad-bg);
  font-family: 'Inter', sans-serif;
}

/* ── Sidebar ── */
.vad-sidebar {
  width: var(--vad-sidebar-w);
  flex-shrink: 0;
  background: var(--vad-primary);
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.vad-brand {
  padding: 24px 20px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.vad-brand-link {
  text-decoration: none;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.vad-brand-name {
  font-family: var(--font-serif);
  font-size: 1.05rem;
  font-weight: 400;
  letter-spacing: 0.28em;
  color: #F5F0E8;
  display: block;
}
.vad-brand-sub {
  font-size: 0.6rem;
  letter-spacing: 0.18em;
  color: rgba(245,240,232,0.5);
  text-transform: uppercase;
  display: block;
}

/* Nav */
.vad-nav {
  flex: 1;
  padding: 16px 0;
}
.vad-nav-section {
  font-size: 0.55rem;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: rgba(245,240,232,0.35);
  text-transform: uppercase;
  padding: 14px 20px 6px;
  margin: 0;
}
.vad-nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  font-size: 0.82rem;
  font-weight: 400;
  color: rgba(245,240,232,0.7);
  text-decoration: none;
  transition: background 0.15s, color 0.15s;
  border-left: 3px solid transparent;
  margin: 1px 0;
}
.vad-nav-item:hover {
  background: rgba(255,255,255,0.06);
  color: #F5F0E8;
}
.vad-nav-item--active {
  background: rgba(255,255,255,0.1);
  color: #F5F0E8;
  border-left-color: #B89C6E;
  font-weight: 600;
}
.vad-nav-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  opacity: 0.8;
}

/* Sidebar footer */
.vad-sidebar-foot {
  padding: 16px 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}
.vad-back-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.75rem;
  color: rgba(245,240,232,0.5);
  text-decoration: none;
  transition: color 0.15s;
}
.vad-back-link:hover { color: #F5F0E8; }

/* Main */
.vad-main {
  flex: 1;
  overflow: auto;
  padding: 32px;
}
</style>

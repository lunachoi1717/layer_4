<template>
  <div class="board-layout">
    <div class="board-header">
      <div class="container">
        <h1 class="board-title">BOARD</h1>
        <p class="board-desc">벙딸리제 고객 서비스 센터</p>
        <nav class="board-nav">
          <RouterLink to="/board/faq" class="board-nav-link" active-class="board-nav-link--active">
            FAQ
          </RouterLink>
          <RouterLink to="/board/inquiry" class="board-nav-link" active-class="board-nav-link--active">
            1:1 문의
          </RouterLink>
        </nav>
      </div>
    </div>
    <div class="board-content container">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.board-layout { min-height: 70vh; }
.board-header {
  background: #1a1a1a;
  color: #fff;
  padding: 48px 0 0;
}
.container { max-width: 960px; margin: 0 auto; padding: 0 24px; }
.board-title {
  font-family: 'Georgia', serif;
  font-size: 32px;
  letter-spacing: 6px;
  margin: 0 0 8px;
}
.board-desc { color: #aaa; font-size: 14px; margin: 0 0 24px; }
.board-nav { display: flex; gap: 0; border-top: 1px solid #333; }
.board-nav-link {
  color: #888;
  text-decoration: none;
  padding: 14px 28px;
  font-size: 14px;
  font-weight: 600;
  border-bottom: 2px solid transparent;
  transition: all .2s;
}
.board-nav-link:hover { color: #fff; }
.board-nav-link--active { color: #fff; border-bottom-color: #d4af7a; }
.board-content { padding-top: 40px; padding-bottom: 80px; }
</style>

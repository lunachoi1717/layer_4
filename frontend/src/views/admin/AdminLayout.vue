<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-logo">
        <RouterLink to="/admin/dashboard">VENTALIZE ADMIN</RouterLink>
      </div>
      <nav class="admin-nav">
        <RouterLink to="/admin/dashboard" class="admin-nav-link" active-class="active">
          📊 대시보드
        </RouterLink>
        <RouterLink to="/admin/members" class="admin-nav-link" active-class="active">
          👥 회원 관리
        </RouterLink>
        <RouterLink to="/admin/products" class="admin-nav-link" active-class="active">
          📦 상품 관리
        </RouterLink>
        <RouterLink to="/admin/orders" class="admin-nav-link" active-class="active">
          🛒 주문 관리
        </RouterLink>
        <RouterLink to="/admin/reviews" class="admin-nav-link" active-class="active">
          ⭐ 리뷰 관리
        </RouterLink>
        <RouterLink to="/admin/qna" class="admin-nav-link" active-class="active">
          💬 Q&A 관리
        </RouterLink>
        <RouterLink to="/admin/board" class="admin-nav-link" active-class="active">
          📋 게시판 관리
        </RouterLink>
      </nav>
      <div class="admin-sidebar-footer">
        <RouterLink to="/" class="admin-nav-link">← 쇼핑몰로 이동</RouterLink>
      </div>
    </aside>
    <div class="admin-content">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
</script>

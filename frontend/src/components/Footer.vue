<template>
  <footer class="v-footer">
    <div class="v-footer__top v-container">

      <!-- Brand column -->
      <div class="v-footer__brand">
        <p class="v-footer__logo-en">VENTALIZE</p>
        <p class="v-footer__logo-kr">벙딸리제</p>
        <p class="v-footer__tagline">Crafted with intention,<br>worn with purpose.</p>
      </div>

      <!-- Nav columns -->
      <div class="v-footer__col">
        <p class="v-footer__col-title">Collections</p>
        <RouterLink to="/category/SCARVES"       class="v-footer__link">Scarves</RouterLink>
        <RouterLink to="/category/READY_TO_WEAR" class="v-footer__link">Ready to Wear</RouterLink>
        <RouterLink to="/category/PERFUME"       class="v-footer__link">Perfume</RouterLink>
        <RouterLink to="/category/ACC"           class="v-footer__link">Accessories</RouterLink>
        <RouterLink to="/category/BAG"           class="v-footer__link">Bags</RouterLink>
        <RouterLink to="/category/SALE"          class="v-footer__link v-footer__link--sale">Sale</RouterLink>
      </div>

      <div class="v-footer__col">
        <p class="v-footer__col-title">Customer Care</p>
        <RouterLink to="/board/faq"     class="v-footer__link">FAQ</RouterLink>
        <RouterLink to="/board/inquiry" class="v-footer__link">1:1 문의</RouterLink>
        <RouterLink to="/mypage"        class="v-footer__link">My Orders</RouterLink>
        <RouterLink to="/cart"          class="v-footer__link">Shopping Bag</RouterLink>
      </div>

      <div class="v-footer__col">
        <p class="v-footer__col-title">Account</p>
        <RouterLink to="/login"    class="v-footer__link">Sign in</RouterLink>
        <RouterLink to="/register" class="v-footer__link">Create Account</RouterLink>
        <RouterLink to="/mypage"   class="v-footer__link">My Page</RouterLink>
      </div>
    </div>

    <div class="v-footer__bottom v-container">
      <p class="v-footer__copy">&copy; {{ year }} Ventalize · 벙딸리제. All rights reserved.</p>
      <p class="v-footer__legal">사업자등록번호 : 000-00-00000 · 대표 : 벙딸리제 · 통신판매업신고 : 제0000-서울-0000호</p>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.v-footer {
  background: #111;
  color: #F5F0E8;
}
.v-footer__top {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 48px;
  padding-top: 72px;
  padding-bottom: 56px;
  border-bottom: 1px solid rgba(245,240,232,0.1);
}
.v-footer__logo-en {
  font-family: 'Cormorant Garamond', 'Georgia', serif;
  font-size: 1.4rem;
  font-weight: 400;
  letter-spacing: 0.3em;
  margin-bottom: 4px;
}
.v-footer__logo-kr {
  font-size: 0.65rem;
  letter-spacing: 0.2em;
  color: #C9B89A;
  margin-bottom: 20px;
}
.v-footer__tagline {
  font-size: 0.8rem;
  color: rgba(245,240,232,0.4);
  line-height: 1.8;
  font-style: italic;
}
.v-footer__col-title {
  font-size: 0.65rem;
  font-weight: 600;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(245,240,232,0.4);
  margin-bottom: 18px;
}
.v-footer__col {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.v-footer__link {
  font-size: 0.82rem;
  color: rgba(245,240,232,0.65);
  text-decoration: none;
  transition: color 0.2s;
}
.v-footer__link:hover       { color: #F5F0E8; }
.v-footer__link--sale       { color: #C9B89A; }
.v-footer__bottom {
  padding-top: 24px;
  padding-bottom: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.v-footer__copy  { font-size: 0.75rem; color: rgba(245,240,232,0.35); }
.v-footer__legal { font-size: 0.68rem; color: rgba(245,240,232,0.2); }

@media (max-width: 900px) {
  .v-footer__top { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 560px) {
  .v-footer__top { grid-template-columns: 1fr; }
  .v-footer__bottom { flex-direction: column; align-items: flex-start; }
}
</style>

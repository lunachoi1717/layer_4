<template>
  <footer class="v-footer">
    <div class="v-footer__top v-container">

      <div class="v-footer__brand">
        <p class="v-footer__logo-en">VENTALIZE</p>
        <p class="v-footer__tagline">Crafted with intention,<br>worn with purpose.</p>
      </div>

      <div class="v-footer__col">
        <p class="v-footer__col-title">Collections</p>
        <RouterLink to="/category/SCARVES"       class="v-footer__link">Scarves</RouterLink>
        <RouterLink to="/category/READY_TO_WEAR" class="v-footer__link">Ready to Wear</RouterLink>
        <RouterLink to="/category/PERFUME"       class="v-footer__link">Perfume</RouterLink>
        <RouterLink to="/category/ACC"           class="v-footer__link">Accessories</RouterLink>
        <RouterLink to="/category/BAGS"          class="v-footer__link">Bags</RouterLink>
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
      <div class="v-footer__bottom-left">
        <p class="v-footer__copy">&copy; {{ year }} Ventalize. All rights reserved.</p>
        <p class="v-footer__legal">사업자등록번호 : 000-00-00000 · 대표 : Ventalize · 통신판매업신고 : 제0000-서울-0000호</p>
      </div>
      <div class="v-footer__sns">

        <a href="#" class="v-footer__sns-btn" aria-label="Instagram">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="2" width="20" height="20" rx="5" ry="5"/>
            <circle cx="12" cy="12" r="4"/>
            <circle cx="17.5" cy="6.5" r="0.5" fill="currentColor" stroke="none"/>
          </svg>
        </a>

        <a href="#" class="v-footer__sns-btn" aria-label="KakaoTalk Channel">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 3C6.48 3 2 6.69 2 11.25c0 2.89 1.77 5.44 4.44 7.01L5.3 22l4.07-2.54A12 12 0 0 0 12 19.5c5.52 0 10-3.69 10-8.25S17.52 3 12 3z"/>
          </svg>
        </a>

        <a href="#" class="v-footer__sns-btn" aria-label="Twitter">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
            <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/>
          </svg>
        </a>
      </div>
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
  font-family: var(--font-serif);
  font-size: 1.4rem;
  font-weight: 400;
  letter-spacing: 0.3em;
  margin-bottom: 4px;
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
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 16px;
}
.v-footer__bottom-left { display: flex; flex-direction: column; gap: 4px; }
.v-footer__copy  { font-size: 0.75rem; color: rgba(245,240,232,0.35); }
.v-footer__legal { font-size: 0.68rem; color: rgba(245,240,232,0.2); }
.v-footer__sns {
  display: flex;
  gap: 12px;
  align-items: center;
}
.v-footer__sns-btn {
  color: rgba(245,240,232,0.4);
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.v-footer__sns-btn:hover { color: #F5F0E8; }

@media (max-width: 900px) {
  .v-footer__top { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 560px) {
  .v-footer__top { grid-template-columns: 1fr; }
  .v-footer__bottom { flex-direction: column; align-items: flex-start; }
}
</style>

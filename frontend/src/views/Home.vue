<template>
  <main class="v-home">

    <!-- ══════════════════════════════════════
         HERO — Full-bleed editorial banner
    ══════════════════════════════════════ -->
    <section class="v-hero">
      <div class="v-hero__track" :style="{ transform: `translateX(-${slide * 100}%)` }">
        <div v-for="(b, i) in banners" :key="i" class="v-hero__slide">
          <div class="v-hero__img-wrap">
            <img :src="b.img" :alt="b.title" class="v-hero__img" loading="eager" />
            <div class="v-hero__overlay" :style="{ background: b.overlay }"></div>
          </div>
          <div class="v-hero__content" :class="`v-hero__content--${b.align}`">
            <p class="v-hero__eyebrow t-sub">{{ b.eyebrow }}</p>
            <h1 class="v-hero__title t-display">{{ b.title }}</h1>
            <p class="v-hero__desc">{{ b.desc }}</p>
            <RouterLink :to="b.link" class="btn btn-outline v-hero__cta">
              {{ b.cta }}
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
            </RouterLink>
          </div>
        </div>
      </div>

      <!-- Controls -->
      <button class="v-hero__arrow v-hero__arrow--l" @click="prevSlide" aria-label="Previous">‹</button>
      <button class="v-hero__arrow v-hero__arrow--r" @click="nextSlide" aria-label="Next">›</button>
      <div class="v-hero__dots">
        <button v-for="(_, i) in banners" :key="i" class="v-hero__dot" :class="{ 'v-hero__dot--on': i === slide }" @click="slide = i" />
      </div>

      <!-- Slide counter -->
      <div class="v-hero__counter">
        <span class="v-hero__counter-num">{{ String(slide + 1).padStart(2, '0') }}</span>
        <span class="v-hero__counter-sep"></span>
        <span class="v-hero__counter-total">{{ String(banners.length).padStart(2, '0') }}</span>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         CATEGORY STRIP
    ══════════════════════════════════════ -->
    <section class="v-cat-strip">
      <div class="v-container">
        <RouterLink v-for="cat in quickCats" :key="cat.name" :to="`/category/${cat.name}`" class="v-cat-strip__item">
          <span class="v-cat-strip__label t-sub">{{ cat.label }}</span>
        </RouterLink>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         NEW ARRIVALS — 5 items/row uniform grid
    ══════════════════════════════════════ -->
    <section class="v-new v-container">
      <div class="v-section-label"><span>New Arrivals</span></div>
      <div v-if="newLoading" class="v-spinner"></div>
      <div v-else class="v-new__row">
        <RouterLink
          v-for="item in newItems"
          :key="item.id"
          :to="`/product/${item.id}`"
          class="v-product-card v-new__card"
        >
          <div class="v-product-card__img-wrap v-new__img">
            <img :src="item.imgPath" :alt="item.name" loading="lazy" />
            <span class="v-product-card__badge">New</span>
          </div>
          <div class="v-product-card__info">
            <p class="v-product-card__brand">{{ item.brand }}</p>
            <p class="v-product-card__name">{{ item.name }}</p>
            <div class="v-product-card__price">
              <span v-if="item.discountPer > 0" class="v-product-card__price-original">{{ fmt(item.price) }}</span>
              <span class="v-product-card__price-sale">{{ fmt(item.salePrice) }}</span>
              <span v-if="item.discountPer > 0" class="v-product-card__discount">-{{ item.discountPer }}%</span>
            </div>
          </div>
        </RouterLink>
      </div>
      <div class="v-editorial__more">
        <RouterLink to="/category/SCARVES" class="btn btn-outline">View All New Arrivals</RouterLink>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         FULL-BLEED PROMO — Perfume
    ══════════════════════════════════════ -->
    <section class="v-promo-full">
      <div class="v-promo-full__img-wrap">
        <img src="https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=1400&q=80" alt="Perfume Collection" />
        <div class="v-promo-full__overlay"></div>
      </div>
      <div class="v-promo-full__body">
        <p class="t-sub" style="color:#C9B89A; margin-bottom:16px">Fragrance · 2025 Collection</p>
        <h2 class="t-display" style="color:#F5F0E8; margin-bottom:20px">The Art of<br>Invisible Luxury</h2>
        <p style="color:rgba(245,240,232,0.7); font-size:0.9rem; margin-bottom:32px; max-width:400px; line-height:1.8">
          향수는 가장 보이지 않는 럭셔리입니다.<br>벙딸리제의 향수 컬렉션을 만나보세요.
        </p>
        <RouterLink to="/category/PERFUME" class="btn btn-outline" style="color:#F5F0E8; border-color:#F5F0E8">
          Discover Perfume
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
        </RouterLink>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         BEST SELLERS — Horizontal scroll
    ══════════════════════════════════════ -->
    <section class="v-best v-container">
      <div class="v-section-label"><span>Best Sellers</span></div>
      <div v-if="bestLoading" class="v-spinner"></div>
      <div v-else class="v-best__row">
        <RouterLink
          v-for="item in bestItems"
          :key="item.id"
          :to="`/product/${item.id}`"
          class="v-product-card v-best__card"
        >
          <div class="v-product-card__img-wrap v-best__img">
            <img :src="item.imgPath" :alt="item.name" loading="lazy" />
            <span v-if="item.discountPer > 0" class="v-product-card__badge v-product-card__badge--sale">{{ item.discountPer }}%</span>
          </div>
          <div class="v-product-card__info">
            <p class="v-product-card__brand">{{ item.brand }}</p>
            <p class="v-product-card__name">{{ item.name }}</p>
            <div class="v-product-card__price">
              <span v-if="item.discountPer > 0" class="v-product-card__price-original">{{ fmt(item.price) }}</span>
              <span class="v-product-card__price-sale">{{ fmt(item.salePrice) }}</span>
            </div>
          </div>
        </RouterLink>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         ASYMMETRIC SPLIT — Scarves & Bags
    ══════════════════════════════════════ -->
    <section class="v-split v-container">
      <!-- Scarves -->
      <div class="v-split__panel">
        <div class="v-split__img-wrap">
          <img src="https://images.unsplash.com/photo-1590736704728-f4730bb30770?w=800&q=80" alt="Scarves" />
        </div>
        <div class="v-split__body">
          <p class="t-sub" style="color:#7A7269; margin-bottom:10px">Scarves</p>
          <h2 class="t-heading" style="margin-bottom:12px">The Perfect<br>Layer</h2>
          <p class="t-body" style="color:#7A7269; margin-bottom:24px">실크부터 캐시미어까지, 계절을 아름답게 완성하는 스카프.</p>
          <RouterLink to="/category/SCARVES" class="btn btn-primary">Shop Scarves</RouterLink>
        </div>
      </div>
      <!-- Bags -->
      <div class="v-split__panel v-split__panel--reverse">
        <div class="v-split__img-wrap">
          <img src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800&q=80" alt="Bags" />
        </div>
        <div class="v-split__body">
          <p class="t-sub" style="color:#7A7269; margin-bottom:10px">Bags</p>
          <h2 class="t-heading" style="margin-bottom:12px">Carry With<br>Intention</h2>
          <p class="t-body" style="color:#7A7269; margin-bottom:24px">이탈리안 레더부터 캔버스까지, 당신의 스타일을 완성하는 가방.</p>
          <RouterLink to="/category/BAG" class="btn btn-primary">Shop Bags</RouterLink>
        </div>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         SALE BANNER — Full-width minimal
    ══════════════════════════════════════ -->
    <section class="v-sale-banner">
      <div class="v-container">
        <RouterLink to="/category/SALE" class="v-sale-banner__inner">
          <div>
            <p class="t-sub" style="color:#B89C6E; margin-bottom:8px">Limited Time</p>
            <h3 style="font-family:'Cormorant Garamond',serif; font-size:1.8rem; font-weight:300; color:#fff; letter-spacing:0.05em">
              시즌 오프 &mdash; 최대 50% 할인
            </h3>
          </div>
          <span class="v-sale-banner__arrow">Shop Sale →</span>
        </RouterLink>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         RECOMMENDATIONS
    ══════════════════════════════════════ -->
    <section class="v-rec v-container">
      <div class="v-section-label"><span>Curated for You</span></div>
      <div v-if="recLoading" class="v-spinner"></div>
      <div v-else class="v-rec__grid">
        <RouterLink
          v-for="item in recItems"
          :key="item.id"
          :to="`/product/${item.id}`"
          class="v-product-card"
        >
          <div class="v-product-card__img-wrap v-rec__img">
            <img :src="item.imgPath" :alt="item.name" loading="lazy" />
          </div>
          <div class="v-product-card__info">
            <p class="v-product-card__brand">{{ item.brand }}</p>
            <p class="v-product-card__name">{{ item.name }}</p>
            <div class="v-product-card__price">
              <span v-if="item.discountPer > 0" class="v-product-card__price-original">{{ fmt(item.price) }}</span>
              <span class="v-product-card__price-sale">{{ fmt(item.salePrice) }}</span>
            </div>
          </div>
        </RouterLink>
      </div>
    </section>

    <!-- ══════════════════════════════════════
         NEWSLETTER / BRAND STORY
    ══════════════════════════════════════ -->
    <section class="v-story">
      <div class="v-container">
        <p class="t-sub" style="color:#C9B89A; margin-bottom:16px; text-align:center">The House of Ventalize</p>
        <h2 class="t-heading" style="text-align:center; margin-bottom:20px; font-weight:300">
          Crafted with intention,<br>worn with purpose.
        </h2>
        <p class="t-body" style="color:#7A7269; text-align:center; max-width:560px; margin:0 auto">
          벙딸리제는 오래 곁에 두고 싶은 물건을 만듭니다. 계절과 트렌드를 초월해, 진정한 가치를 지닌 패션을 큐레이션합니다.
        </p>
      </div>
    </section>

  </main>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

/* ── Slider ── */
const slide = ref(0)
let timer   = null

const banners = [
  {
    img: 'https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=1600&q=80',
    overlay: 'linear-gradient(90deg, rgba(27,58,45,0.72) 0%, rgba(27,58,45,0.1) 100%)',
    eyebrow: '2025 S/S Collection',
    title: 'The Art\nof Draping',
    desc: '실크 스카프 하나로 완성되는 우아함',
    cta: 'Explore Scarves',
    link: '/category/SCARVES',
    align: 'left',
  },
  {
    img: 'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1600&q=80',
    overlay: 'linear-gradient(270deg, rgba(27,58,45,0.68) 0%, rgba(0,0,0,0.05) 100%)',
    eyebrow: 'Ready to Wear',
    title: 'Dressed\nto Last',
    desc: '시간이 지나도 사랑받는 기성복 컬렉션',
    cta: 'Shop Ready to Wear',
    link: '/category/READY_TO_WEAR',
    align: 'right',
  },
  {
    img: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=1600&q=80',
    overlay: 'linear-gradient(90deg, rgba(17,17,17,0.6) 0%, rgba(0,0,0,0.05) 100%)',
    eyebrow: 'Bags · New Season',
    title: 'Carry\nStories',
    desc: '이탈리안 가죽으로 완성한 이번 시즌의 주인공',
    cta: 'Discover Bags',
    link: '/category/BAG',
    align: 'left',
  },
]

function nextSlide() { slide.value = (slide.value + 1) % banners.length }
function prevSlide() { slide.value = (slide.value - 1 + banners.length) % banners.length }

/* ── Category strip ── */
const quickCats = [
  { name: 'SCARVES',       label: 'Scarves'       },
  { name: 'READY_TO_WEAR', label: 'Ready to Wear' },
  { name: 'PERFUME',       label: 'Perfume'       },
  { name: 'ACC',           label: 'Accessories'   },
  { name: 'BAG',           label: 'Bags'          },
  { name: 'SALE',          label: 'Sale'          },
]

/* ── Product data ── */
const newLoading  = ref(true)
const bestLoading = ref(true)
const recLoading  = ref(true)
const newItems    = ref([])
const bestItems   = ref([])
const recItems    = ref([])

const fmt = (p) => p ? p.toLocaleString('ko-KR') + '원' : ''

async function fetchNew() {
  try {
    const r = await fetch('/v1/api/items?sort=new', { credentials: 'include' })
    newItems.value = (await r.json()).slice(0, 5)
  } finally { newLoading.value = false }
}
async function fetchBest() {
  try {
    const r = await fetch('/v1/api/items?sort=best', { credentials: 'include' })
    bestItems.value = (await r.json()).slice(0, 5)
  } finally { bestLoading.value = false }
}
async function fetchRec() {
  try {
    const r = await fetch('/v1/api/items?sort=recommend', { credentials: 'include' })
    recItems.value = (await r.json()).slice(0, 5)
  } finally { recLoading.value = false }
}

onMounted(() => {
  fetchNew(); fetchBest(); fetchRec()
  timer = setInterval(nextSlide, 5000)
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
/* ── Hero ── */
.v-hero {
  position: relative;
  overflow: hidden;
  height: min(90vh, 820px);
}
.v-hero__track {
  display: flex;
  height: 100%;
  transition: transform 0.8s cubic-bezier(0.77, 0, 0.175, 1);
}
.v-hero__slide {
  flex: 0 0 100%;
  position: relative;
  overflow: hidden;
}
.v-hero__img-wrap, .v-hero__img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; }
.v-hero__overlay { position: absolute; inset: 0; }
.v-hero__content {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  padding: 0 80px;
  max-width: 640px;
  z-index: 2;
}
.v-hero__content--right { left: auto; right: 0; text-align: right; }
.v-hero__content--left  { left: 0; }
.v-hero__eyebrow { color: #C9B89A; margin-bottom: 16px; }
.v-hero__title {
  color: #F5F0E8;
  white-space: pre-line;
  margin-bottom: 20px;
}
.v-hero__desc {
  color: rgba(245,240,232,0.75);
  font-size: 0.9rem;
  margin-bottom: 36px;
  line-height: 1.75;
}
.v-hero__cta {
  color: #F5F0E8;
  border-color: rgba(245,240,232,0.5);
  font-size: 0.72rem;
}
.v-hero__cta:hover { background: rgba(245,240,232,0.12); }

/* Hero arrows */
.v-hero__arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(245,240,232,0.12);
  border: 1px solid rgba(245,240,232,0.25);
  color: #F5F0E8;
  font-size: 1.8rem;
  width: 48px; height: 48px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: background 0.25s;
  z-index: 3;
}
.v-hero__arrow:hover { background: rgba(245,240,232,0.25); }
.v-hero__arrow--l { left: 24px; }
.v-hero__arrow--r { right: 24px; }

/* Dots */
.v-hero__dots {
  position: absolute;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 3;
}
.v-hero__dot {
  width: 24px; height: 2px;
  background: rgba(245,240,232,0.35);
  border: none; cursor: pointer;
  transition: background 0.3s, width 0.3s;
  padding: 0;
}
.v-hero__dot--on { background: #F5F0E8; width: 40px; }

/* Counter */
.v-hero__counter {
  position: absolute;
  right: 32px;
  bottom: 28px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 3;
}
.v-hero__counter-num { color: #F5F0E8; font-size: 0.8rem; font-weight: 600; }
.v-hero__counter-sep { width: 28px; height: 1px; background: rgba(245,240,232,0.4); }
.v-hero__counter-total { color: rgba(245,240,232,0.45); font-size: 0.8rem; }

/* ── Category Strip ── */
.v-cat-strip {
  border-bottom: 1px solid #E8E2D9;
}
.v-cat-strip .v-container {
  display: flex;
  justify-content: center;
  gap: 0;
  overflow-x: auto;
  scrollbar-width: none;
}
.v-cat-strip__item {
  display: flex;
  align-items: center;
  padding: 18px 28px;
  text-decoration: none;
  color: #7A7269;
  border-bottom: 2px solid transparent;
  transition: color 0.25s, border-color 0.25s;
  white-space: nowrap;
}
.v-cat-strip__item:hover { color: #111; border-bottom-color: #1B3A2D; }
.v-cat-strip__label { letter-spacing: 0.14em; }

/* ── New Arrivals ── */
.v-new {
  padding: 80px 0 64px;
}
.v-new__row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.v-new__card { flex: 0 0 auto; }
.v-new__img { aspect-ratio: 3 / 4; }
.v-editorial__more {
  text-align: center;
  margin-top: 48px;
}

/* ── Full-bleed Promo ── */
.v-promo-full {
  position: relative;
  height: min(70vh, 640px);
  overflow: hidden;
  margin: 0;
}
.v-promo-full__img-wrap { position: absolute; inset: 0; }
.v-promo-full__img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.v-promo-full__overlay {
  position: absolute; inset: 0;
  background: linear-gradient(100deg, rgba(27,58,45,0.82) 30%, rgba(27,58,45,0.2) 100%);
}
.v-promo-full__body {
  position: absolute;
  top: 50%;
  left: 80px;
  transform: translateY(-50%);
  z-index: 2;
  max-width: 520px;
}

/* ── Best Sellers ── */
.v-best {
  padding: 80px 0 64px;
}
.v-best__row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.v-best__img { aspect-ratio: 3 / 4; }

/* ── Asymmetric Split ── */
.v-split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 64px;
  padding: 80px 0;
  border-top: 1px solid #E8E2D9;
  border-bottom: 1px solid #E8E2D9;
}
.v-split__panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  align-items: center;
}
.v-split__panel--reverse { direction: rtl; }
.v-split__panel--reverse > * { direction: ltr; }
.v-split__img-wrap {
  aspect-ratio: 2 / 3;
  overflow: hidden;
}
.v-split__img-wrap img {
  width: 100%; height: 100%;
  object-fit: cover;
  transition: transform 0.7s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}
.v-split__panel:hover .v-split__img-wrap img { transform: scale(1.04); }

/* ── Sale Banner ── */
.v-sale-banner {
  background: #1B3A2D;
  padding: 48px 0;
}
.v-sale-banner__inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-decoration: none;
}
.v-sale-banner__arrow {
  font-size: 0.78rem;
  font-weight: 500;
  letter-spacing: 0.1em;
  color: #C9B89A;
  transition: letter-spacing 0.25s;
}
.v-sale-banner__inner:hover .v-sale-banner__arrow { letter-spacing: 0.2em; }

/* ── Recommendations ── */
.v-rec {
  padding: 80px 0 64px;
}
.v-rec__grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.v-rec__img { aspect-ratio: 3 / 4; }

/* ── Brand Story ── */
.v-story {
  background: #F5F0E8;
  padding: 100px 0;
}

/* ── Responsive ── */
@media (max-width: 1024px) {
  .v-hero__content { padding: 0 48px; }
  .v-new__row  { grid-template-columns: repeat(3, 1fr); }
  .v-best__row { grid-template-columns: repeat(3, 1fr); }
  .v-rec__grid { grid-template-columns: repeat(3, 1fr); }
  .v-split { grid-template-columns: 1fr; gap: 48px; }
}
@media (max-width: 768px) {
  .v-hero { height: 70vh; }
  .v-hero__content { padding: 0 24px; }
  .v-new__row  { grid-template-columns: repeat(2, 1fr); }
  .v-best__row { grid-template-columns: repeat(2, 1fr); }
  .v-promo-full__body { left: 24px; right: 24px; }
  .v-split__panel { grid-template-columns: 1fr; }
  .v-rec__grid { grid-template-columns: repeat(2, 1fr); }
}
</style>

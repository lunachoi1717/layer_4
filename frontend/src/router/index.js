import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Category from '../views/Category.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Cart from '../views/Cart.vue'
import MyPage from '../views/MyPage.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Checkout from '../views/Checkout.vue'
import CartCheckout from '../views/CartCheckout.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/category/:name', component: Category },
  { path: '/product/:id', component: ProductDetail },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/cart', component: Cart },
  { path: '/mypage', component: MyPage, meta: { requiresAuth: true } },
  { path: '/checkout', component: Checkout, meta: { requiresAuth: true } },
  { path: '/cart-checkout', component: CartCheckout, meta: { requiresAuth: true } },

  {
    path: '/board/inquiry/:id',
    component: () => import('../views/board/InquiryDetail.vue'),
  },

  {
    path: '/board',
    component: () => import('../views/board/BoardLayout.vue'),
    children: [
      { path: '', redirect: '/board/faq' },
      { path: 'faq', component: () => import('../views/board/Faq.vue') },
      { path: 'inquiry', component: () => import('../views/board/Inquiry.vue') },
      { path: 'reviews', component: () => import('../views/board/BoardReviews.vue') },
    ]
  },

  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'members', component: () => import('../views/admin/Members.vue') },
      { path: 'products', component: () => import('../views/admin/Products.vue') },
      { path: 'orders', component: () => import('../views/admin/Orders.vue') },
      { path: 'reviews', component: () => import('../views/admin/Reviews.vue') },
      { path: 'faq', component: () => import('../views/admin/AdminFaq.vue') },
      { path: 'inquiry', component: () => import('../views/admin/AdminInquiry.vue') },
      { path: 'coupons', component: () => import('../views/admin/Coupons.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role')
  const loginId = localStorage.getItem('loginId')

  if (to.meta.requiresAdmin && role !== 'ROLE_ADMIN') {
    return next('/login')
  }
  if (to.meta.requiresAuth && !loginId) {
    return next('/login')
  }
  next()
})

export default router

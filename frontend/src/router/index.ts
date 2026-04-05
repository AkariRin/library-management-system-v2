import { createRouter, createWebHistory } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'

import loginView from '@/views/loginView.vue'
import registerView from '@/views/registerView.vue'
import homeView from '@/views/homeView.vue'
import settingsView from '@/views/settingsView.vue'
import booksView from '@/views/booksView.vue'
import borrowView from '@/views/borrowView.vue'
import adminView from '@/views/adminView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'Home', component: homeView},
    { path: '/login', name: 'login', component: loginView, meta: { isPublic: true } },
    { path: '/register', name: 'register', component: registerView, meta: { isPublic: true } },
    { path: '/books', name: 'Books', component: booksView},
    { path: '/borrow', name: 'Borrow Records', component: borrowView},
    { path: '/admin', name: 'Admin', component: adminView, meta: { requireAdmin: true }},
    { path: '/settings', name: 'Settings', component: settingsView},
  ],
})

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserdataStore()
  const isLoggedIn = userStore.isLoggedIn

  // 公开页面直接放行
  if (to.meta.isPublic) {
    // 如果已登录，访问登录/注册页面时重定向到首页
    if (isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 如果是需要鉴权的页面，检查是否已登录
  if (!isLoggedIn) {
    // 未登录，重定向到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath } // 保存原始路径，登录后可以跳转回来
    })
  } else {
    // 已登录，检查是否需要管理员权限
    if (to.meta.requireAdmin && !userStore.admin) {
      // 需要管理员权限但用户不是管理员，重定向到首页
      next('/')
    } else {
      next()
    }
  }
})

// 全局后置路由守卫，设置页面标题
router.afterEach((to) => {
  document.title = `${String(to.name)} | Library Management System`
})

export default router


import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('../views/auth/LoginView.vue') },
    {
      path: '/',
      component: () => import('../layouts/AdminLayout.vue'),
      children: [
        { path: '', component: () => import('../views/dashboard/DashboardView.vue') },
        { path: 'tenants', component: () => import('../views/tenants/TenantsView.vue') },
        { path: 'sites', component: () => import('../views/sites/SitesView.vue') },
        { path: 'pages', component: () => import('../views/pages/PagesView.vue') },
        { path: 'pages/:id', component: () => import('../views/pages/PageEditor.vue') },
        { path: 'products', component: () => import('../views/products/ProductsView.vue') },
        { path: 'products/:id', component: () => import('../views/products/ProductEditor.vue') },
        { path: 'articles', component: () => import('../views/articles/ArticlesView.vue') },
        { path: 'inquiries', component: () => import('../views/inquiries/InquiriesView.vue') },
        { path: 'media', component: () => import('../views/media/MediaView.vue') },
        { path: 'seo', component: () => import('../views/seo/SeoView.vue') }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.path !== '/login' && !auth.token) {
    return '/login'
  }
  if (to.path === '/login' && auth.token) {
    return '/'
  }
})

export default router

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
        { path: 'theme', component: () => import('../views/theme/ThemeView.vue') },
        { path: 'pages', component: () => import('../views/pages/PagesView.vue') },
        { path: 'pages/:id', component: () => import('../views/pages/PageEditor.vue') },
        { path: 'products', component: () => import('../views/products/ProductsView.vue') },
        { path: 'products/:id', component: () => import('../views/products/ProductEditor.vue') },
        { path: 'articles', component: () => import('../views/articles/ArticlesView.vue') },
        { path: 'inquiries', component: () => import('../views/inquiries/InquiriesView.vue') },
        { path: 'media', component: () => import('../views/media/MediaView.vue') },
        { path: 'seo', component: () => import('../views/seo/SeoView.vue') },
        { path: 'members', component: () => import('../views/members/MembersView.vue'), meta: { permission: 'MEMBERS' } },
        { path: 'audit', component: () => import('../views/audit/AuditView.vue'), meta: { permission: 'AUDIT' } }
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
  const perm = to.meta?.permission as string | undefined
  if (perm && !auth.can(perm)) {
    return '/'
  }
})

export default router

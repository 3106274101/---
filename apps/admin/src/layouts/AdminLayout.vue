<template>
  <div class="layout" :class="{ 'is-builder': isBuilder }">
    <aside class="side">
      <div class="brand">
        <svg class="logo-mark" viewBox="0 0 32 32" fill="none" aria-hidden="true">
          <path d="M16 3 28 10v12L16 29 4 22V10L16 3Z" stroke="currentColor" stroke-width="1.7" />
          <path d="M16 3v26M4 10l12 7 12-7" stroke="currentColor" stroke-width="1.7" />
        </svg>
        <div>TradeHub<small>贸站通 · 独立站中台</small></div>
      </div>
      <div class="nav-group">站点装修</div>
      <a href="javascript:;" :class="{ 'is-active': isActive('/', true) }" @click.prevent="$router.push('/')">
        <el-icon><Odometer /></el-icon>总览
      </a>
      <a v-if="auth.user?.superAdmin" href="javascript:;" :class="{ 'is-active': isActive('/tenants') }" @click.prevent="$router.push('/tenants')">
        <el-icon><OfficeBuilding /></el-icon>租户
      </a>
      <a v-if="auth.can('SITES')" href="javascript:;" :class="{ 'is-active': isActive('/sites') }" @click.prevent="$router.push('/sites')">
        <el-icon><Monitor /></el-icon>站点
      </a>
      <a v-if="auth.can('SITES')" href="javascript:;" :class="{ 'is-active': isActive('/theme') }" @click.prevent="$router.push('/theme')">
        <el-icon><Brush /></el-icon>品牌装修
      </a>
      <a v-if="auth.can('PAGES')" href="javascript:;" :class="{ 'is-active': isActive('/pages') }" @click.prevent="$router.push('/pages')">
        <el-icon><Grid /></el-icon>页面搭建
      </a>
      <div class="nav-group">运营</div>
      <a v-if="auth.can('PRODUCTS')" href="javascript:;" :class="{ 'is-active': isActive('/products') }" @click.prevent="$router.push('/products')">
        <el-icon><Goods /></el-icon>商品
      </a>
      <a v-if="auth.can('ARTICLES')" href="javascript:;" :class="{ 'is-active': isActive('/articles') }" @click.prevent="$router.push('/articles')">
        <el-icon><Document /></el-icon>内容
      </a>
      <a v-if="auth.can('INQUIRIES')" href="javascript:;" :class="{ 'is-active': isActive('/inquiries') }" @click.prevent="$router.push('/inquiries')">
        <el-icon><ChatDotSquare /></el-icon>询盘
        <em v-if="newInquiries">{{ newInquiries }}</em>
      </a>
      <a v-if="auth.can('MEDIA')" href="javascript:;" :class="{ 'is-active': isActive('/media') }" @click.prevent="$router.push('/media')">
        <el-icon><Picture /></el-icon>媒体
      </a>
      <a v-if="auth.can('SEO')" href="javascript:;" :class="{ 'is-active': isActive('/seo') }" @click.prevent="$router.push('/seo')">
        <el-icon><Search /></el-icon>SEO
      </a>
      <div v-if="auth.can('MEMBERS') || auth.can('AUDIT')" class="nav-group">协作</div>
      <a v-if="auth.can('MEMBERS')" href="javascript:;" :class="{ 'is-active': isActive('/members') }" @click.prevent="$router.push('/members')">
        <el-icon><User /></el-icon>成员
      </a>
      <a v-if="auth.can('AUDIT')" href="javascript:;" :class="{ 'is-active': isActive('/audit') }" @click.prevent="$router.push('/audit')">
        <el-icon><Notebook /></el-icon>操作日志
      </a>
    </aside>
    <section class="main">
      <header class="top" v-if="!isBuilder">
        <div class="top-left">
          <el-select v-if="auth.user?.superAdmin && tenants.length" v-model="tenantId" size="small" style="width: 160px" @change="onTenant">
            <el-option v-for="t in tenants" :key="t.id" :label="t.name" :value="String(t.id)" />
          </el-select>
          <el-select v-if="sites.length" v-model="siteId" size="small" style="width: 200px" @change="onSite">
            <el-option v-for="s in sites" :key="s.id" :label="s.name" :value="String(s.id)" />
          </el-select>
          <el-select v-model="locale" size="small" style="width: 148px" title="编辑内容语言" @change="onLocale">
            <el-option v-for="l in contentLocales" :key="l.code" :label="l.native" :value="l.code" />
          </el-select>
        </div>
        <div class="top-right">
          <el-input
            v-model="searchQ"
            size="small"
            placeholder="搜索商品 / 页面 / 询盘  Ctrl+K"
            style="width: 240px"
            clearable
            @focus="openSearch"
            @keyup.enter="runSearch"
          />
          <el-button size="small" @click="openStore">预览独立站</el-button>
          <el-button size="small" text @click="pwdVisible = true">改密</el-button>
          <span class="avatar sm">{{ initials(auth.user?.displayName) }}</span>
          <span class="who">{{ auth.user?.displayName }}</span>
          <el-button size="small" text @click="logout">退出</el-button>
        </div>
      </header>
      <div class="content" :class="{ flush: isBuilder }"><router-view /></div>
    </section>
    <el-dialog v-model="searchVisible" title="全局搜索" width="640px" @opened="focusSearch">
      <el-input ref="searchInput" v-model="searchQ" placeholder="型号、页面标题、询盘邮箱、客户名" clearable @input="onSearchInput" @keyup.enter="runSearch" />
      <div v-if="searching" class="empty-hint" style="margin-top:12px">搜索中…</div>
      <div v-else-if="searchQ && !hasHits" class="empty-hint" style="margin-top:12px">没有匹配结果。</div>
      <div v-for="group in searchGroups" :key="group.key" class="search-group">
        <h4>{{ group.label }}</h4>
        <button v-for="hit in group.items" :key="group.key + hit.id" type="button" class="search-hit" @click="goHit(group.key, hit)">
          <b>{{ hit.title }}</b>
          <span>{{ hit.sub }}</span>
        </button>
      </div>
    </el-dialog>
    <el-dialog v-model="pwdVisible" title="修改密码" width="420px">
      <el-form label-width="90px">
        <el-form-item label="旧密码"><el-input v-model="pwd.oldPassword" type="password" /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwd.newPassword" type="password" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Brush, ChatDotSquare, Document, Goods, Grid, Monitor, Notebook, Odometer, OfficeBuilding, Picture, Search, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { storePreview } from '../config'
import { useAuthStore } from '../stores/auth'
import { CONTENT_LOCALES, localeMeta } from '../utils/locales'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const sites = ref<any[]>([])
const tenants = ref<any[]>([])
const tenantId = ref(localStorage.getItem('th_tenant') || '')
const siteId = ref(localStorage.getItem('th_site') || '')
const locale = ref(localStorage.getItem('th_locale') || 'en')
const contentLocales = CONTENT_LOCALES
const newInquiries = ref(0)
const isBuilder = computed(() => /^\/pages\/\d+/.test(route.path))
const pwdVisible = ref(false)
const pwd = ref({ oldPassword: '', newPassword: '' })
const searchVisible = ref(false)
const searchQ = ref('')
const searching = ref(false)
const searchHits = ref<any>({ products: [], pages: [], articles: [], inquiries: [], sites: [] })
const searchInput = ref<{ focus?: () => void; input?: HTMLInputElement } | null>(null)
let searchTimer: number | undefined
const hasHits = computed(() => searchGroups.value.some((g) => g.items.length))
const searchGroups = computed(() => [
  { key: 'products', label: '商品', items: (searchHits.value.products || []).map((p: any) => ({ id: p.id, title: p.name, sub: p.model || p.slug })) },
  { key: 'pages', label: '页面', items: (searchHits.value.pages || []).map((p: any) => ({ id: p.id, title: p.title, sub: '/' + p.slug })) },
  { key: 'articles', label: '文章', items: (searchHits.value.articles || []).map((p: any) => ({ id: p.id, title: p.title, sub: p.slug })) },
  { key: 'inquiries', label: '询盘', items: (searchHits.value.inquiries || []).map((p: any) => ({ id: p.id, title: p.name, sub: p.email || p.company })) },
  { key: 'sites', label: '站点', items: (searchHits.value.sites || []).map((p: any) => ({ id: p.id, title: p.name, sub: p.code })) }
].filter((g) => g.items.length))

onMounted(async () => {
  try {
    const me: any = await http.get('/admin/auth/me')
    auth.user = me.data
    localStorage.setItem('th_user', JSON.stringify(me.data))
  } catch {
    /* keep cached profile */
  }
  if (auth.user?.superAdmin) {
    try {
      const t: any = await http.get('/admin/tenants')
      tenants.value = t.data || []
    } catch {
      tenants.value = []
    }
  }
  const res: any = await http.get('/admin/sites')
  sites.value = res.data?.list || []
  if (!siteId.value && sites.value[0]) {
    siteId.value = String(sites.value[0].id)
    localStorage.setItem('th_site', siteId.value)
  }
  const current = sites.value.find((s: any) => String(s.id) === siteId.value)
  if (current?.code) localStorage.setItem('th_site_code', current.code)
  try {
    const dash: any = await http.get('/admin/dashboard')
    newInquiries.value = dash.data?.newInquiries || 0
  } catch {
    newInquiries.value = 0
  }
  window.addEventListener('keydown', onHotkey)
})
onUnmounted(() => window.removeEventListener('keydown', onHotkey))

function onHotkey(e: KeyboardEvent) {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
    e.preventDefault()
    openSearch()
  }
}
function openSearch() {
  searchVisible.value = true
}
function focusSearch() {
  searchInput.value?.input?.focus?.()
}
function onSearchInput() {
  window.clearTimeout(searchTimer)
  searchTimer = window.setTimeout(runSearch, 280)
}
async function runSearch() {
  const q = searchQ.value.trim()
  if (!q) {
    searchHits.value = { products: [], pages: [], articles: [], inquiries: [], sites: [] }
    return
  }
  searching.value = true
  try {
    const res: any = await http.get('/admin/search', { params: { q } })
    searchHits.value = res.data || {}
    if (!searchVisible.value) searchVisible.value = true
  } finally {
    searching.value = false
  }
}
function goHit(kind: string, hit: any) {
  searchVisible.value = false
  if (kind === 'products') router.push('/products/' + hit.id)
  else if (kind === 'pages') router.push('/pages/' + hit.id)
  else if (kind === 'articles') router.push({ path: '/articles', query: { edit: String(hit.id) } })
  else if (kind === 'inquiries') router.push('/inquiries')
  else router.push('/sites')
}

function isActive(to: string, exact = false) {
  if (exact) return route.path === to
  return route.path === to || route.path.startsWith(to + '/')
}
function initials(name?: string) {
  const s = String(name || 'TH').trim()
  return s.slice(0, 2).toUpperCase()
}
function onSite(val: string) {
  localStorage.setItem('th_site', val)
  const current = sites.value.find((s: any) => String(s.id) === String(val))
  if (current?.code) localStorage.setItem('th_site_code', current.code)
}
function onTenant(val: string) {
  localStorage.setItem('th_tenant', val)
  localStorage.removeItem('th_site')
  localStorage.removeItem('th_site_code')
  location.reload()
}
function onLocale(val: string) {
  localStorage.setItem('th_locale', val)
  location.reload()
}
function logout() {
  auth.logout()
  router.push('/login')
}
async function changePassword() {
  if (!pwd.value.newPassword || pwd.value.newPassword.length < 6) {
    ElMessage.warning('新密码至少 6 位')
    return
  }
  await http.post('/admin/auth/password', pwd.value)
  ElMessage.success('密码已更新')
  pwdVisible.value = false
  pwd.value = { oldPassword: '', newPassword: '' }
}
function openStore() {
  const current = sites.value.find((s: any) => String(s.id) === siteId.value)
  const code = current?.code || sites.value[0]?.code || ''
  const loc = localeMeta(locale.value).code
  window.open(storePreview('/' + loc, code), '_blank')
}
</script>

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
      <a href="javascript:;" :class="{ 'is-active': isActive('/sites') }" @click.prevent="$router.push('/sites')">
        <el-icon><Monitor /></el-icon>站点
      </a>
      <a href="javascript:;" :class="{ 'is-active': isActive('/theme') }" @click.prevent="$router.push('/theme')">
        <el-icon><Brush /></el-icon>品牌装修
      </a>
      <a href="javascript:;" :class="{ 'is-active': isActive('/pages') }" @click.prevent="$router.push('/pages')">
        <el-icon><Grid /></el-icon>页面搭建
      </a>
      <div class="nav-group">运营</div>
      <a href="javascript:;" :class="{ 'is-active': isActive('/products') }" @click.prevent="$router.push('/products')">
        <el-icon><Goods /></el-icon>商品
      </a>
      <a href="javascript:;" :class="{ 'is-active': isActive('/articles') }" @click.prevent="$router.push('/articles')">
        <el-icon><Document /></el-icon>内容
      </a>
      <a href="javascript:;" :class="{ 'is-active': isActive('/inquiries') }" @click.prevent="$router.push('/inquiries')">
        <el-icon><ChatDotSquare /></el-icon>询盘
        <em v-if="newInquiries">{{ newInquiries }}</em>
      </a>
      <a href="javascript:;" :class="{ 'is-active': isActive('/media') }" @click.prevent="$router.push('/media')">
        <el-icon><Picture /></el-icon>媒体
      </a>
      <a href="javascript:;" :class="{ 'is-active': isActive('/seo') }" @click.prevent="$router.push('/seo')">
        <el-icon><Search /></el-icon>SEO
      </a>
    </aside>
    <section class="main">
      <header class="top" v-if="!isBuilder">
        <div class="top-left">
          <el-select v-if="sites.length" v-model="siteId" size="small" style="width: 200px" @change="onSite">
            <el-option v-for="s in sites" :key="s.id" :label="s.name" :value="String(s.id)" />
          </el-select>
          <div class="lang-switch">
            <button :class="{ on: locale === 'en' }" @click="onLocale('en')">EN</button>
            <button :class="{ on: locale === 'zh' }" @click="onLocale('zh')">中文</button>
          </div>
        </div>
        <div class="top-right">
          <el-button size="small" @click="openStore">预览独立站</el-button>
          <span class="avatar sm">{{ initials(auth.user?.displayName) }}</span>
          <span class="who">{{ auth.user?.displayName }}</span>
          <el-button size="small" text @click="logout">退出</el-button>
        </div>
      </header>
      <div class="content" :class="{ flush: isBuilder }"><router-view /></div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Brush, ChatDotSquare, Document, Goods, Grid, Monitor, Odometer, OfficeBuilding, Picture, Search } from '@element-plus/icons-vue'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const sites = ref<any[]>([])
const siteId = ref(localStorage.getItem('th_site') || '')
const locale = ref(localStorage.getItem('th_locale') || 'en')
const newInquiries = ref(0)
const isBuilder = computed(() => /^\/pages\/\d+/.test(route.path))

onMounted(async () => {
  const res: any = await http.get('/admin/sites')
  sites.value = res.data?.list || []
  if (!siteId.value && sites.value[0]) {
    siteId.value = String(sites.value[0].id)
    localStorage.setItem('th_site', siteId.value)
  }
  try {
    const dash: any = await http.get('/admin/dashboard')
    newInquiries.value = dash.data?.newInquiries || 0
  } catch {
    newInquiries.value = 0
  }
})

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
}
function onLocale(val: string) {
  if (locale.value === val) return
  localStorage.setItem('th_locale', val)
  location.reload()
}
function logout() {
  auth.logout()
  router.push('/login')
}
function openStore() {
  window.open('http://localhost:3000/en', '_blank')
}
</script>

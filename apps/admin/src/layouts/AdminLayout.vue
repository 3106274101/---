<template>
  <div class="layout">
    <aside class="side">
      <div class="brand">TRADEHUB<small>贸站通 · 独立站中台</small></div>
      <router-link to="/">总览</router-link>
      <router-link v-if="auth.user?.superAdmin" to="/tenants">租户</router-link>
      <router-link to="/sites">站点</router-link>
      <router-link to="/pages">页面搭建</router-link>
      <router-link to="/products">商品</router-link>
      <router-link to="/articles">内容</router-link>
      <router-link to="/inquiries">询盘</router-link>
      <router-link to="/media">媒体</router-link>
      <router-link to="/seo">SEO</router-link>
    </aside>
    <section class="main">
      <header class="top">
        <div>
          <el-select v-if="sites.length" v-model="siteId" size="small" style="width: 180px" @change="onSite">
            <el-option v-for="s in sites" :key="s.id" :label="s.name" :value="String(s.id)" />
          </el-select>
          <el-select v-model="locale" size="small" style="width: 110px; margin-left: 8px" @change="onLocale">
            <el-option label="English" value="en" />
            <el-option label="中文" value="zh" />
          </el-select>
        </div>
        <div>
          <span style="margin-right: 12px; color: #445">{{ auth.user?.displayName }}</span>
          <el-button size="small" @click="logout">退出</el-button>
        </div>
      </header>
      <div class="content"><router-view /></div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const sites = ref<any[]>([])
const siteId = ref(localStorage.getItem('th_site') || '')
const locale = ref(localStorage.getItem('th_locale') || 'en')

onMounted(async () => {
  const res: any = await http.get('/admin/sites')
  sites.value = res.data?.list || []
  if (!siteId.value && sites.value[0]) {
    siteId.value = String(sites.value[0].id)
    localStorage.setItem('th_site', siteId.value)
  }
})

function onSite(val: string) {
  localStorage.setItem('th_site', val)
}
function onLocale(val: string) {
  localStorage.setItem('th_locale', val)
  location.reload()
}
function logout() {
  auth.logout()
  router.push('/login')
}
</script>

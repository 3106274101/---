<template>
  <div>
    <div class="page-head">
      <div>
        <h1>页面搭建</h1>
        <p>用区块拼首页、关于我们和落地页，点卡片即可进入装修。</p>
      </div>
      <el-button type="primary" @click="create">新建页面</el-button>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">还没有页面，点击「新建页面」开始搭建。</div>
      <article v-for="row in list" :key="row.id" class="item-card clickable" @click="$router.push('/pages/' + row.id)">
        <div class="item-cover ph page-banner">{{ typeLabel(row.pageType) }}</div>
        <div class="item-body">
          <div class="item-head">
            <h3>{{ row.title }}</h3>
            <span class="pill" :class="row.status === 'live' ? 'pill-live' : row.status === 'scheduled' ? 'pill-building' : 'pill-building'">{{ statusLabel(row.status) }}</span>
          </div>
          <p>/{{ row.slug }}</p>
        </div>
        <div class="item-foot" @click.stop>
          <el-button size="small" type="primary" @click="$router.push('/pages/' + row.id)">装修</el-button>
          <el-button size="small" @click="duplicate(row)">复制</el-button>
          <el-button size="small" @click="preview(row)">预览</el-button>
          <el-button v-if="row.slug !== 'home'" size="small" text type="danger" @click="remove(row)">删除</el-button>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../api/http'
import { storePreview } from '../../config'

const list = ref<any[]>([])
const router = useRouter()
const types: Record<string, string> = {
  home: '首页', about: '关于我们', factory: '工厂', certificates: '证书',
  faq: 'FAQ', contact: '联系', solutions: '方案', custom: '自定义页'
}
onMounted(load)
async function load() {
  const siteId = localStorage.getItem('th_site')
  const res: any = await http.get('/admin/pages', { params: { siteId } })
  list.value = res.data || []
}
function typeLabel(type: string) {
  return types[type] || type || '页面'
}
function statusLabel(status: string) {
  return ({ live: '已发布', draft: '草稿', scheduled: '定时发布' } as any)[status] || status
}
async function create() {
  const siteId = Number(localStorage.getItem('th_site') || 1)
  const res: any = await http.post('/admin/pages', {
    siteId, pageType: 'custom', slug: 'page-' + Date.now(), status: 'draft',
    title: 'New page', seoTitle: 'New page', blocks: []
  })
  router.push('/pages/' + res.data.id)
}
async function duplicate(row: any) {
  const res: any = await http.post('/admin/pages/' + row.id + '/duplicate')
  ElMessage.success('已复制为草稿页')
  router.push('/pages/' + res.data.id)
}
async function remove(row: any) {
  await http.delete('/admin/pages/' + row.id)
  ElMessage.success('已删除')
  load()
}
function preview(row: any) {
  const loc = localStorage.getItem('th_locale') || 'en'
  const path = row.slug === 'home' ? '/' + loc : `/${loc}/${row.slug}`
  const code = localStorage.getItem('th_site_code') || ''
  window.open(storePreview(path, code), '_blank')
}
</script>

<style scoped>
.page-banner {
  height: 88px;
  background: #122033;
  color: #d5e0ea !important;
  font-weight: 650;
  letter-spacing: 0.08em;
}
.item-head h3 { margin: 0; flex: 1; }
</style>

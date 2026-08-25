<template>
  <div>
    <div class="page-head">
      <div>
        <h1>站点</h1>
        <p>每个站点可独立绑定域名、品牌色和页面装修。</p>
      </div>
      <el-button type="primary" @click="open()">新建站点</el-button>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">还没有站点，点击「新建站点」开始建站。</div>
      <article v-for="row in list" :key="row.id" class="item-card">
        <div class="item-body">
          <div class="item-head">
            <span class="avatar lg">{{ initials(row.name) }}</span>
            <span class="pill" :class="pillClass(row.status)">{{ statusLabel(row.status) }}</span>
          </div>
          <h3>{{ row.name }}</h3>
          <p>{{ row.code }}.local</p>
          <p style="margin-top:8px">{{ row.theme }} · {{ localeText(row.locales) }}</p>
        </div>
        <div class="item-foot">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" @click="goTheme(row)">品牌</el-button>
          <el-button size="small" type="primary" @click="goPages(row)">装修</el-button>
        </div>
      </article>
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑站点' : '新建站点'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="主题"><el-input v-model="form.theme" /></el-form-item>
        <el-form-item label="语言"><el-input v-model="form.locales" placeholder="en,zh" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="建设中" value="building" />
            <el-option label="已上线" value="live" />
            <el-option label="停用" value="disabled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../api/http'

const list = ref<any[]>([])
const visible = ref(false)
const router = useRouter()
const form = reactive<any>({ theme: 'industrial-fuel', locales: 'en,zh', status: 'building', defaultLocale: 'en' })

async function load() {
  const res: any = await http.get('/admin/sites')
  list.value = res.data?.list || []
}
function open(row?: any) {
  Object.assign(form, row || { id: undefined, theme: 'industrial-fuel', locales: 'en,zh', status: 'building', defaultLocale: 'en', name: '', code: '' })
  visible.value = true
}
function goTheme(row: any) {
  localStorage.setItem('th_site', String(row.id))
  router.push('/theme')
}
function goPages(row: any) {
  localStorage.setItem('th_site', String(row.id))
  router.push('/pages')
}
async function save() {
  if (form.id) await http.put('/admin/sites/' + form.id, form)
  else await http.post('/admin/sites', form)
  visible.value = false
  load()
}
function initials(name?: string) {
  return String(name || 'TH').replace(/\s+/g, '').slice(0, 2).toUpperCase()
}
function localeText(locales: any) {
  return Array.isArray(locales) ? locales.join(' / ') : locales
}
function statusLabel(status: string) {
  return ({ live: '已上线', building: '建设中', draft: '草稿', disabled: '停用' } as any)[status] || status
}
function pillClass(status: string) {
  if (status === 'live') return 'pill-live'
  if (status === 'building' || status === 'draft') return 'pill-building'
  return 'pill-off'
}
onMounted(load)
</script>

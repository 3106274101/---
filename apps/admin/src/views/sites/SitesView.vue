<template>
  <div>
    <div class="page-head">
      <div>
        <h1>站点</h1>
        <p>创建时选择一套独立站模板，会写入该风格的配色、首页区块和内页。</p>
      </div>
      <el-button type="primary" @click="open()">新建站点</el-button>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">还没有站点，点击「新建站点」开始建站。</div>
      <article v-for="row in list" :key="row.id" class="item-card">
        <div class="item-cover ph tpl-cover" :style="swatch(row.theme)">
          {{ templateName(row.theme) }}
        </div>
        <div class="item-body">
          <div class="item-head">
            <span class="avatar lg">{{ initials(row.name) }}</span>
            <span class="pill" :class="pillClass(row.status)">{{ statusLabel(row.status) }}</span>
          </div>
          <h3>{{ row.name }}</h3>
          <p>{{ row.code }}.local</p>
          <p style="margin-top:8px">{{ templateName(row.theme) }} · {{ localeText(row.locales) }}</p>
        </div>
        <div class="item-foot">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" @click="preview(row)">预览</el-button>
          <el-button size="small" @click="goTheme(row)">品牌</el-button>
          <el-button size="small" type="primary" @click="goPages(row)">装修</el-button>
        </div>
      </article>
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑站点' : '新建站点'" width="760px">
      <el-form :model="form" label-width="100px">
        <el-form-item v-if="!form.id" label="模板" required>
          <div class="tpl-grid">
            <button
              v-for="t in templates"
              :key="t.id"
              type="button"
              class="tpl-card"
              :class="{ on: form.theme === t.id }"
              @click="form.theme = t.id"
            >
              <span class="tpl-swatch" :style="{ background: t.primaryColor }">
                <i :style="{ background: t.accentColor }" />
              </span>
              <b>{{ t.nameZh }}</b>
              <small>{{ t.pitchZh }}</small>
            </button>
          </div>
        </el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" placeholder="如 ZhengHe Catalog" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" placeholder="英文小写，如 zhenghe-catalog" :disabled="!!form.id" /></el-form-item>
        <el-form-item v-if="form.id" label="模板">
          <el-select v-model="form.theme" style="width:100%">
            <el-option v-for="t in templates" :key="t.id" :label="t.nameZh + ' · ' + t.name" :value="t.id" />
          </el-select>
          <div class="form-hint">编辑时只改风格编码和后续品牌色；页面内容不会重灌。</div>
        </el-form-item>
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
import { ElMessage } from 'element-plus'
import http from '../../api/http'
import { storePreview } from '../../config'

const list = ref<any[]>([])
const templates = ref<any[]>([])
const visible = ref(false)
const router = useRouter()
const form = reactive<any>({ theme: 'industrial', locales: 'en,zh', status: 'building', defaultLocale: 'en' })

async function load() {
  const res: any = await http.get('/admin/sites')
  list.value = res.data?.list || []
}
async function loadTemplates() {
  const res: any = await http.get('/admin/templates')
  templates.value = res.data || []
}
function open(row?: any) {
  Object.assign(form, row || { id: undefined, theme: 'industrial', locales: 'en,zh', status: 'building', defaultLocale: 'en', name: '', code: '' })
  visible.value = true
}
function goTheme(row: any) {
  localStorage.setItem('th_site', String(row.id))
  localStorage.setItem('th_site_code', row.code)
  router.push('/theme')
}
function goPages(row: any) {
  localStorage.setItem('th_site', String(row.id))
  localStorage.setItem('th_site_code', row.code)
  router.push('/pages')
}
function preview(row: any) {
  window.open(storePreview('/en', row.code), '_blank')
}
function templateName(theme?: string) {
  const id = !theme || theme === 'industrial-fuel' ? 'industrial' : theme
  return templates.value.find((t) => t.id === id)?.nameZh || id
}
function swatch(theme?: string) {
  const id = !theme || theme === 'industrial-fuel' ? 'industrial' : theme
  const t = templates.value.find((x) => x.id === id)
  return t ? { background: `linear-gradient(135deg, ${t.primaryColor}, ${t.accentColor})`, color: '#fff' } : {}
}
async function save() {
  if (!form.name || !form.code) {
    ElMessage.warning('请填写名称和编码')
    return
  }
  const payload = { ...form, template: form.theme }
  if (form.id) await http.put('/admin/sites/' + form.id, payload)
  else await http.post('/admin/sites', payload)
  ElMessage.success(form.id ? '已更新' : '站点已按模板创建，可点预览查看')
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
onMounted(() => {
  load()
  loadTemplates()
})
</script>

<style scoped>
.tpl-cover { letter-spacing: 0.06em; font-weight: 650; height: 72px; }
.form-hint { margin-top: 6px; color: var(--th-muted); font-size: 12px; line-height: 1.5; }
.tpl-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  width: 100%;
}
.tpl-card {
  text-align: left;
  border: 1px solid var(--th-line);
  background: #fff;
  padding: 10px 12px 12px;
  cursor: pointer;
}
.tpl-card.on { border-color: var(--th-navy); box-shadow: inset 0 0 0 1px var(--th-navy); }
.tpl-card b { display: block; margin: 8px 0 4px; color: var(--th-navy); }
.tpl-card small { color: var(--th-muted); font-size: 12px; line-height: 1.45; }
.tpl-swatch {
  display: block;
  height: 36px;
  position: relative;
}
.tpl-swatch i {
  position: absolute;
  right: 8px;
  bottom: 8px;
  width: 14px;
  height: 14px;
}
</style>

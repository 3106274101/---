<template>
  <div>
    <div class="page-head">
      <div>
        <h1>内容</h1>
        <p>当前编辑语言：{{ localeLabel }}。顶栏切换语言后，标题和正文会按该语言单独保存。</p>
      </div>
      <el-button type="primary" @click="open()">写文章</el-button>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">还没有文章，点击「写文章」发布第一篇内容。</div>
      <article v-for="row in list" :key="row.id" class="item-card clickable" @click="open(row)">
        <img v-if="row.coverUrl" class="item-cover" :src="row.coverUrl" :alt="row.title" />
        <div v-else class="item-cover ph">ARTICLE</div>
        <div class="item-body">
          <div class="item-head">
            <h3>{{ row.title }}</h3>
            <span class="pill" :class="row.status === 'live' ? 'pill-live' : 'pill-building'">{{ statusLabel(row.status) }}</span>
          </div>
          <p>{{ row.summary || row.slug }}</p>
        </div>
        <div class="item-foot" @click.stop>
          <el-button size="small" type="primary" @click="open(row)">编辑</el-button>
          <el-button size="small" @click="duplicate(row)">复制</el-button>
          <el-button size="small" text type="danger" @click="remove(row)">删除</el-button>
        </div>
      </article>
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑文章' : '写文章'" width="720px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" @blur="maybeSlug" /></el-form-item>
        <el-form-item label="Slug"><el-input v-model="form.slug" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" /></el-form-item>
        <el-form-item label="正文"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
        <el-form-item label="封面"><MediaPicker v-model="form.coverUrl" /></el-form-item>
        <el-form-item label="SEO Title"><el-input v-model="form.seoTitle" /></el-form-item>
        <el-form-item label="SEO Description"><el-input v-model="form.seoDescription" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="草稿" value="draft" />
            <el-option label="定时发布" value="scheduled" />
            <el-option label="发布" value="live" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.status === 'scheduled'" label="发布时间">
          <el-date-picker v-model="form.scheduledAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../api/http'
import MediaPicker from '../../components/MediaPicker.vue'
import { suggestSlug } from '../../utils/slug'
import { localeLabel as formatLocale } from '../../utils/locales'

const list = ref<any[]>([])
const visible = ref(false)
const route = useRoute()
const localeLabel = formatLocale(localStorage.getItem('th_locale') || 'en')
const form = reactive<any>({ status: 'draft', siteId: Number(localStorage.getItem('th_site') || 1) })
async function load() {
  const res: any = await http.get('/admin/articles', { params: { siteId: form.siteId } })
  list.value = res.data || []
}
function open(row?: any) {
  Object.assign(form, row || { id: undefined, status: 'draft', siteId: Number(localStorage.getItem('th_site') || 1), title: '', slug: '', summary: '', content: '', coverUrl: '', scheduledAt: '' })
  visible.value = true
}
function maybeSlug() {
  if (!form.slug) form.slug = suggestSlug(form.title)
}
async function duplicate(row: any) {
  await http.post('/admin/articles/' + row.id + '/duplicate')
  ElMessage.success('已复制为草稿')
  load()
}
async function remove(row: any) {
  await http.delete('/admin/articles/' + row.id)
  ElMessage.success('已删除')
  load()
}
async function save() {
  form.siteId = Number(localStorage.getItem('th_site') || 1)
  if (!form.seoTitle) form.seoTitle = form.title
  if (!form.seoDescription) form.seoDescription = form.summary
  if (form.id) await http.put('/admin/articles/' + form.id, form)
  else await http.post('/admin/articles', form)
  visible.value = false
  load()
}
onMounted(async () => {
  await load()
  const editId = Number(route.query.edit)
  if (editId) {
    const hit = list.value.find((x: any) => x.id === editId)
    if (hit) open(hit)
  }
})
function statusLabel(status: string) {
  return ({ live: '已发布', draft: '草稿', scheduled: '定时发布' } as any)[status] || status
}
</script>

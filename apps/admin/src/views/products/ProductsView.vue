<template>
  <div>
    <div class="page-head">
      <div>
        <h1>商品</h1>
        <p>机型、上下架与分类。封面会显示在独立站产品页。</p>
      </div>
      <div class="page-tools">
        <el-input v-model="keyword" placeholder="搜索型号 / 名称" style="width:180px" clearable @clear="load" @keyup.enter="load" />
        <el-select v-model="status" clearable placeholder="状态" style="width:110px" @change="load">
          <el-option label="上架" value="live" />
          <el-option label="定时上架" value="scheduled" />
          <el-option label="草稿" value="draft" />
          <el-option label="下架" value="off" />
        </el-select>
        <el-button @click="catVisible = true">分类</el-button>
        <el-button @click="exportCsv">导出 CSV</el-button>
        <el-button type="primary" @click="$router.push('/products/new')">新建商品</el-button>
      </div>
    </div>
    <div v-if="picked.length" class="bulk-bar">
      已选 {{ picked.length }} 个
      <el-button size="small" @click="bulk('live')">批量上架</el-button>
      <el-button size="small" @click="bulk('off')">批量下架</el-button>
      <el-button size="small" text @click="picked = []">取消</el-button>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">暂无商品，点击右上角「新建商品」开始添加。</div>
      <article v-for="row in list" :key="row.id" class="item-card clickable" @click="$router.push('/products/' + row.id)">
        <img v-if="row.coverUrl" class="item-cover" :src="row.coverUrl" :alt="row.name" />
        <div v-else class="item-cover ph">NO IMAGE</div>
        <div class="item-body">
          <div class="item-head">
            <el-checkbox :model-value="picked.includes(row.id)" @click.stop @change="(v: boolean) => toggle(row.id, v)" />
            <h3 style="flex:1;margin:0">{{ row.name }}</h3>
          </div>
          <p>{{ row.model }} · {{ row.slug }}</p>
          <div class="item-tags">
            <span v-if="row.featured" class="mark">精选</span>
            <span class="pill" :class="row.status === 'live' ? 'pill-live' : row.status === 'draft' ? 'pill-draft' : 'pill-off'">{{ statusLabel(row.status) }}</span>
          </div>
        </div>
        <div class="item-foot" @click.stop>
          <el-button size="small" type="primary" @click="$router.push('/products/' + row.id)">编辑</el-button>
          <el-button size="small" @click="duplicate(row)">复制</el-button>
          <el-button size="small" @click="setStatus(row, 'live')">上架</el-button>
          <el-button size="small" @click="setStatus(row, 'off')">下架</el-button>
        </div>
      </article>
    </div>

    <el-dialog v-model="catVisible" title="商品分类" width="560px">
      <el-table :data="cats">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="slug" label="Slug" />
        <el-table-column prop="status" label="状态" width="90" />
      </el-table>
      <el-form :inline="true" :model="catForm" style="margin-top:12px">
        <el-form-item label="名称"><el-input v-model="catForm.name" /></el-form-item>
        <el-form-item label="Slug"><el-input v-model="catForm.slug" /></el-form-item>
        <el-form-item><el-button type="primary" @click="saveCat">新增分类</el-button></el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const list = ref<any[]>([])
const cats = ref<any[]>([])
const keyword = ref('')
const status = ref('')
const catVisible = ref(false)
const picked = ref<number[]>([])
const catForm = reactive({ name: '', slug: '', status: 'live' })
const router = useRouter()

async function load() {
  const res: any = await http.get('/admin/products', { params: { keyword: keyword.value, status: status.value || undefined } })
  list.value = res.data?.list || []
}
async function loadCats() {
  const c: any = await http.get('/admin/categories')
  cats.value = c.data || []
}
async function setStatus(row: any, next: string) {
  await http.post('/admin/products/' + row.id + '/status', { status: next })
  load()
}
async function duplicate(row: any) {
  const res: any = await http.post('/admin/products/' + row.id + '/duplicate')
  ElMessage.success('已复制为草稿')
  router.push('/products/' + res.data.id)
}
function toggle(id: number, on: boolean) {
  if (on) picked.value = [...new Set([...picked.value, id])]
  else picked.value = picked.value.filter((x) => x !== id)
}
async function bulk(next: string) {
  await http.post('/admin/products/bulk-status', { ids: picked.value, status: next })
  ElMessage.success('已更新 ' + picked.value.length + ' 个商品')
  picked.value = []
  load()
}
async function exportCsv() {
  const res: any = await http.get('/admin/products/export')
  const blob = new Blob([res.data.csv || ''], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = res.data.filename || 'products.csv'
  a.click()
}
async function saveCat() {
  await http.post('/admin/categories', catForm)
  catForm.name = ''
  catForm.slug = ''
  loadCats()
}
function statusLabel(status: string) {
  return ({ live: '上架', draft: '草稿', off: '下架', scheduled: '定时上架', hidden: '隐藏' } as Record<string, string>)[status] || status
}
onMounted(() => {
  load()
  loadCats()
})
</script>

<style scoped>
.bulk-bar {
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid var(--th-line);
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>

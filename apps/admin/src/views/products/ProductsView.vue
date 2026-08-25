<template>
  <div>
    <div class="page-head">
      <div>
        <h1>商品</h1>
        <p>机型、上下架与分类。封面会显示在独立站产品页。</p>
      </div>
      <div class="page-tools">
        <el-input v-model="keyword" placeholder="搜索型号" style="width:180px" clearable @clear="load" @keyup.enter="load" />
        <el-select v-model="status" clearable placeholder="状态" style="width:110px" @change="load">
          <el-option label="上架" value="live" />
          <el-option label="草稿" value="draft" />
          <el-option label="下架" value="off" />
        </el-select>
        <el-button @click="catVisible = true">分类</el-button>
        <el-button type="primary" @click="$router.push('/products/new')">新建商品</el-button>
      </div>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">暂无商品，点击右上角「新建商品」开始添加。</div>
      <article v-for="row in list" :key="row.id" class="item-card clickable" @click="$router.push('/products/' + row.id)">
        <img v-if="row.coverUrl" class="item-cover" :src="row.coverUrl" :alt="row.name" />
        <div v-else class="item-cover ph">NO IMAGE</div>
        <div class="item-body">
          <h3>{{ row.name }}</h3>
          <p>{{ row.model }} · {{ row.slug }}</p>
          <div class="item-tags">
            <span v-if="row.featured" class="mark">精选</span>
            <span class="pill" :class="row.status === 'live' ? 'pill-live' : row.status === 'draft' ? 'pill-draft' : 'pill-off'">{{ statusLabel(row.status) }}</span>
          </div>
        </div>
        <div class="item-foot" @click.stop>
          <el-button size="small" type="primary" @click="$router.push('/products/' + row.id)">编辑</el-button>
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
import http from '../../api/http'

const list = ref<any[]>([])
const cats = ref<any[]>([])
const keyword = ref('')
const status = ref('')
const catVisible = ref(false)
const catForm = reactive({ name: '', slug: '', status: 'live' })

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
async function saveCat() {
  await http.post('/admin/categories', catForm)
  catForm.name = ''
  catForm.slug = ''
  loadCats()
}
function statusLabel(status: string) {
  return ({ live: '上架', draft: '草稿', off: '下架' } as Record<string, string>)[status] || status
}
onMounted(() => {
  load()
  loadCats()
})
</script>

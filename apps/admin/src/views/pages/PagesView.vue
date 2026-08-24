<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between">
        <span>页面</span>
        <el-button type="primary" @click="create">新建页面</el-button>
      </div>
    </template>
    <el-table :data="list">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="slug" label="Slug" />
      <el-table-column prop="pageType" label="类型" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push('/pages/' + row.id)">搭建</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../api/http'

const list = ref<any[]>([])
const router = useRouter()
onMounted(async () => {
  const siteId = localStorage.getItem('th_site')
  const res: any = await http.get('/admin/pages', { params: { siteId } })
  list.value = res.data || []
})
async function create() {
  const siteId = Number(localStorage.getItem('th_site') || 1)
  const res: any = await http.post('/admin/pages', {
    siteId, pageType: 'custom', slug: 'page-' + Date.now(), status: 'draft',
    title: 'New page', seoTitle: 'New page', blocks: []
  })
  router.push('/pages/' + res.data.id)
}
</script>

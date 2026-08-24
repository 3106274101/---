<template>
  <el-card header="询盘">
    <el-table :data="list">
      <el-table-column prop="createdAt" label="时间" width="170" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="company" label="公司" />
      <el-table-column prop="country" label="国家" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="productName" label="产品" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="setStatus(row, 'following')">跟进</el-button>
          <el-button size="small" @click="setStatus(row, 'quoted')">已报价</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="detailVisible" title="询盘详情">
      <pre style="white-space:pre-wrap">{{ current?.message }}</pre>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const list = ref<any[]>([])
const current = ref<any>(null)
const detailVisible = ref(false)
async function load() {
  const res: any = await http.get('/admin/inquiries')
  list.value = res.data?.list || []
}
async function setStatus(row: any, status: string) {
  await http.post('/admin/inquiries/' + row.id + '/status', { status })
  load()
}
onMounted(load)
</script>

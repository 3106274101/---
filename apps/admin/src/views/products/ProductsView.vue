<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between">
        <span>商品</span>
        <el-button type="primary" @click="$router.push('/products/new')">新建商品</el-button>
      </div>
    </template>
    <el-table :data="list">
      <el-table-column prop="model" label="型号" width="120" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push('/products/' + row.id)">编辑</el-button>
          <el-button size="small" @click="setStatus(row, 'live')">上架</el-button>
          <el-button size="small" @click="setStatus(row, 'off')">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const list = ref<any[]>([])
async function load() {
  const res: any = await http.get('/admin/products')
  list.value = res.data?.list || []
}
async function setStatus(row: any, status: string) {
  await http.post('/admin/products/' + row.id + '/status', { status })
  load()
}
onMounted(load)
</script>

<template>
  <div>
    <div class="page-head">
      <div>
        <h1>操作日志</h1>
        <p>后台写入类操作会记一笔，便于追查谁改了站点或询盘。</p>
      </div>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="createdAt" label="时间" width="180" />
      <el-table-column prop="userName" label="操作人" width="140" />
      <el-table-column prop="userId" label="用户 ID" width="90" />
      <el-table-column prop="action" label="动作" />
      <el-table-column prop="targetType" label="对象" width="100" />
      <el-table-column prop="detailJson" label="详情" show-overflow-tooltip />
    </el-table>
    <div style="margin-top:12px;color:var(--th-muted)">共 {{ total }} 条</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await http.get('/admin/audit', { params: { pageSize: 100 } })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
})
</script>

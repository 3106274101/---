<template>
  <el-card header="租户">
    <el-table :data="list">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="packageCode" label="套餐" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="useTenant(row)">进入</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await http.get('/admin/tenants')
  list.value = res.data || []
})
function useTenant(row: any) {
  localStorage.setItem('th_tenant', String(row.id))
  ElMessage.success('已切换到 ' + row.name)
  location.href = '/'
}
</script>

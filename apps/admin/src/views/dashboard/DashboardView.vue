<template>
  <div>
    <div class="stat-row">
      <div class="stat"><b>{{ data.sites || 0 }}</b><span>站点</span></div>
      <div class="stat"><b>{{ data.products || 0 }}</b><span>商品</span></div>
      <div class="stat"><b>{{ data.inquiriesWeek || 0 }}</b><span>本周询盘</span></div>
      <div class="stat"><b>{{ data.languages || 0 }}</b><span>语言</span></div>
    </div>
    <el-card header="站点">
      <el-table :data="data.sitesList || []">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="code" label="编码" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="locales" label="语言" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const data = ref<any>({})
onMounted(async () => {
  const res: any = await http.get('/admin/dashboard')
  data.value = res.data || {}
})
</script>

<template>
  <el-card header="SEO">
    <h3>robots.txt 预览</h3>
    <pre>{{ robots }}</pre>
    <h3>301 重定向</h3>
    <el-form :inline="true" :model="form">
      <el-form-item label="从"><el-input v-model="form.fromPath" /></el-form-item>
      <el-form-item label="到"><el-input v-model="form.toPath" /></el-form-item>
      <el-form-item><el-button type="primary" @click="save">添加 301</el-button></el-form-item>
    </el-form>
    <el-table :data="redirects">
      <el-table-column prop="fromPath" label="From" />
      <el-table-column prop="toPath" label="To" />
      <el-table-column prop="code" label="Code" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import http from '../../api/http'

const robots = ref('')
const redirects = ref<any[]>([])
const form = reactive({ fromPath: '', toPath: '', code: 301, siteId: Number(localStorage.getItem('th_site') || 1) })
async function load() {
  const r: any = await http.get('/admin/seo/robots')
  robots.value = r.data?.content || ''
  const d: any = await http.get('/admin/seo/redirects', { params: { siteId: form.siteId } })
  redirects.value = d.data || []
}
async function save() {
  form.siteId = Number(localStorage.getItem('th_site') || 1)
  await http.post('/admin/seo/redirects', form)
  load()
}
onMounted(load)
</script>

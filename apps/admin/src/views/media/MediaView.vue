<template>
  <el-card header="媒体库">
    <el-upload :http-request="upload" :show-file-list="false">
      <el-button type="primary">上传图片 / PDF</el-button>
    </el-upload>
    <el-table :data="list" style="margin-top:16px">
      <el-table-column prop="originalName" label="文件" />
      <el-table-column prop="url" label="URL" />
      <el-table-column prop="alt" label="ALT" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const list = ref<any[]>([])
async function load() {
  const res: any = await http.get('/admin/media')
  list.value = res.data || []
}
async function upload(opt: any) {
  const fd = new FormData()
  fd.append('file', opt.file)
  fd.append('alt', opt.file.name)
  await http.post('/admin/media/upload', fd)
  load()
}
onMounted(load)
</script>

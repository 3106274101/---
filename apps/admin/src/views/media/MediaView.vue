<template>
  <div>
    <div class="page-head">
      <div>
        <h1>媒体库</h1>
        <p>上传后复制 URL，即可贴到页面装修或商品封面。</p>
      </div>
      <el-upload :http-request="upload" :show-file-list="false" accept="image/*,.pdf">
        <el-button type="primary">上传图片 / PDF</el-button>
      </el-upload>
    </div>
    <div class="media-grid">
      <div v-if="!list.length" class="empty-hint">还没有素材，点击右上角上传第一张图。</div>
      <div v-for="item in list" :key="item.id" class="media-card">
        <img v-if="(item.mime || '').startsWith('image')" :src="item.url" :alt="item.alt" />
        <div v-else style="height:110px;display:grid;place-items:center;background:#f4f6f8">PDF</div>
        <p style="margin:8px 0 4px">{{ item.originalName }}</p>
        <el-button size="small" text @click="copy(item.url)">复制 URL</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
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
async function copy(url: string) {
  await navigator.clipboard.writeText(url)
  ElMessage.success('已复制')
}
onMounted(load)
</script>

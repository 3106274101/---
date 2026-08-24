<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between">
        <span>博客 / 内容</span>
        <el-button type="primary" @click="visible = true">写文章</el-button>
      </div>
    </template>
    <el-table :data="list">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="slug" label="Slug" />
      <el-table-column prop="status" label="状态" />
    </el-table>
    <el-dialog v-model="visible" title="文章" width="720px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="Slug"><el-input v-model="form.slug" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" /></el-form-item>
        <el-form-item label="正文"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
        <el-form-item label="封面"><el-input v-model="form.coverUrl" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="草稿" value="draft" />
            <el-option label="发布" value="live" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import http from '../../api/http'

const list = ref<any[]>([])
const visible = ref(false)
const form = reactive<any>({ status: 'draft', siteId: Number(localStorage.getItem('th_site') || 1) })
async function load() {
  const res: any = await http.get('/admin/articles', { params: { siteId: form.siteId } })
  list.value = res.data || []
}
async function save() {
  form.siteId = Number(localStorage.getItem('th_site') || 1)
  form.seoTitle = form.title
  form.seoDescription = form.summary
  await http.post('/admin/articles', form)
  visible.value = false
  load()
}
onMounted(load)
</script>

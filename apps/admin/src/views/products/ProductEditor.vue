<template>
  <div>
    <div class="page-head">
      <div>
        <h1>商品编辑</h1>
        <p>型号、参数、图集与 SEO 会同步到独立站。</p>
      </div>
    </div>
    <el-card>
    <el-form :model="form" label-width="120px" style="max-width:860px">
      <el-form-item label="型号"><el-input v-model="form.model" /></el-form-item>
      <el-form-item label="Slug"><el-input v-model="form.slug" /></el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" style="width:100%">
          <el-option v-for="c in cats" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" /></el-form-item>
      <el-form-item label="详情 HTML"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
      <el-form-item label="封面 URL">
        <el-input v-model="form.coverUrl" />
        <img v-if="form.coverUrl" :src="form.coverUrl" class="thumb" style="margin-top:8px;width:160px;height:100px" />
      </el-form-item>
      <el-form-item label="图集 URL">
        <el-input v-model="galleryText" type="textarea" :rows="3" placeholder="每行一个图片地址" />
      </el-form-item>
      <el-form-item label="技术参数">
        <div v-for="(row, i) in attrRows" :key="i" style="display:flex;gap:8px;margin-bottom:8px">
          <el-input v-model="row.key" placeholder="如 flow_rate" />
          <el-input v-model="row.value" placeholder="如 40-80 L/min" />
          <el-button @click="attrRows.splice(i,1)">删</el-button>
        </div>
        <el-button size="small" @click="attrRows.push({ key: '', value: '' })">加一行参数</el-button>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.status">
          <el-option label="草稿" value="draft" />
          <el-option label="上架" value="live" />
          <el-option label="隐藏" value="hidden" />
          <el-option label="下架" value="off" />
        </el-select>
      </el-form-item>
      <el-form-item label="SEO Title"><el-input v-model="form.seoTitle" /></el-form-item>
      <el-form-item label="SEO Description"><el-input v-model="form.seoDescription" type="textarea" /></el-form-item>
      <el-form-item label="推荐"><el-switch v-model="form.featured" /></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const route = useRoute()
const router = useRouter()
const cats = ref<any[]>([])
const galleryText = ref('')
const attrRows = ref<{ key: string; value: string }[]>([])
const form = reactive<any>({
  status: 'draft', featured: false, gallery: [], attrs: {}, siteId: Number(localStorage.getItem('th_site') || 1)
})

onMounted(async () => {
  const c: any = await http.get('/admin/categories')
  cats.value = c.data || []
  if (route.params.id !== 'new') {
    const res: any = await http.get('/admin/products/' + route.params.id)
    Object.assign(form, res.data)
    galleryText.value = (res.data.gallery || []).join('\n')
    attrRows.value = Object.entries(res.data.attrs || {}).map(([key, value]) => ({ key, value: String(value) }))
  }
})
async function save() {
  form.siteId = Number(localStorage.getItem('th_site') || 1)
  form.gallery = galleryText.value.split('\n').map((s) => s.trim()).filter(Boolean)
  form.attrs = Object.fromEntries(attrRows.value.filter((r) => r.key).map((r) => [r.key, r.value]))
  if (form.id) await http.put('/admin/products/' + form.id, form)
  else {
    const res: any = await http.post('/admin/products', form)
    router.replace('/products/' + res.data.id)
  }
  ElMessage.success('已保存')
}
</script>

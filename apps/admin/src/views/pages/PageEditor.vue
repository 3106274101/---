<template>
  <el-card v-if="page">
    <template #header>
      <div style="display:flex;gap:8px;align-items:center">
        <span>页面搭建 · {{ page.slug }}</span>
        <el-input v-model="page.title" style="width:240px" size="small" />
        <el-select v-model="page.status" size="small" style="width:120px">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="live" />
        </el-select>
        <el-button type="primary" size="small" @click="save">保存发布</el-button>
        <el-button size="small" @click="addBlock">添加区块</el-button>
      </div>
    </template>
    <el-form label-width="120px" style="max-width:860px">
      <el-form-item label="SEO Title"><el-input v-model="page.seoTitle" /></el-form-item>
      <el-form-item label="SEO Description"><el-input v-model="page.seoDescription" type="textarea" /></el-form-item>
    </el-form>
    <div v-for="(block, idx) in page.blocks" :key="idx" style="border:1px solid #e6ebf0;padding:12px;margin-bottom:12px;background:#fff">
      <div style="display:flex;justify-content:space-between;margin-bottom:8px">
        <b>{{ block.type }}</b>
        <span>
          <el-button size="small" @click="move(idx, -1)">上移</el-button>
          <el-button size="small" @click="move(idx, 1)">下移</el-button>
          <el-button size="small" type="danger" @click="page.blocks.splice(idx,1)">删除</el-button>
        </span>
      </div>
      <el-input v-if="block.props.heading !== undefined" v-model="block.props.heading" placeholder="标题" style="margin-bottom:8px" />
      <el-input v-if="block.props.subtitle !== undefined" v-model="block.props.subtitle" placeholder="副标题" style="margin-bottom:8px" />
      <el-input v-if="block.props.cta !== undefined" v-model="block.props.cta" placeholder="按钮文案" style="margin-bottom:8px" />
      <el-input v-if="block.props.image !== undefined" v-model="block.props.image" placeholder="图片 URL" />
      <el-input v-if="block.props.html !== undefined" v-model="block.props.html" type="textarea" :rows="5" placeholder="HTML" />
      <el-input v-if="block.props.text !== undefined" v-model="block.props.text" type="textarea" />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const route = useRoute()
const page = ref<any>(null)
onMounted(async () => {
  const res: any = await http.get('/admin/pages/' + route.params.id)
  page.value = res.data
  page.value.blocks = page.value.blocks || []
})
function addBlock() {
  page.value.blocks.push({ type: 'richText', props: { html: '<p>New block</p>' } })
}
function move(idx: number, dir: number) {
  const to = idx + dir
  if (to < 0 || to >= page.value.blocks.length) return
  const arr = page.value.blocks
  const tmp = arr[idx]
  arr[idx] = arr[to]
  arr[to] = tmp
}
async function save() {
  await http.put('/admin/pages/' + page.value.id, page.value)
  ElMessage.success('已保存')
}
</script>

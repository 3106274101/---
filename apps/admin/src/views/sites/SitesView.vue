<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>站点</span>
        <el-button type="primary" @click="open()">新建站点</el-button>
      </div>
    </template>
    <el-table :data="list">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="theme" label="模板" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="locales" label="语言" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="visible" title="站点" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="主题"><el-input v-model="form.theme" /></el-form-item>
        <el-form-item label="语言"><el-input v-model="form.locales" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="建设中" value="building" />
            <el-option label="已上线" value="live" />
            <el-option label="停用" value="disabled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
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
const form = reactive<any>({ theme: 'industrial-fuel', locales: 'en,zh', status: 'building', defaultLocale: 'en' })

async function load() {
  const res: any = await http.get('/admin/sites')
  list.value = res.data?.list || []
}
function open(row?: any) {
  Object.assign(form, row || { theme: 'industrial-fuel', locales: 'en,zh', status: 'building', defaultLocale: 'en', name: '', code: '' })
  visible.value = true
}
async function save() {
  if (form.id) await http.put('/admin/sites/' + form.id, form)
  else await http.post('/admin/sites', form)
  visible.value = false
  load()
}
onMounted(load)
</script>

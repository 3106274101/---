<template>
  <div>
    <div class="page-head">
      <div>
        <h1>成员与角色</h1>
        <p>OWNER 管全站，EDITOR 装修内容，SALES 跟询盘。超管才能开租户。</p>
      </div>
      <el-button type="primary" @click="open()">添加成员</el-button>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="displayName" label="姓名" />
      <el-table-column prop="username" label="账号" width="140" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">{{ roleLabel(row.roleCode) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <span class="pill" :class="row.status === 1 ? 'pill-live' : 'pill-off'">{{ row.status === 1 ? '启用' : '停用' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最近登录" width="160">
        <template #default="{ row }">{{ formatTime(row.lastLoginAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" @click="toggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-card header="权限矩阵" style="margin-top:16px">
      <el-table :data="matrix" size="small">
        <el-table-column prop="module" label="模块" width="140" />
        <el-table-column prop="owner" label="OWNER" />
        <el-table-column prop="editor" label="EDITOR" />
        <el-table-column prop="sales" label="SALES" />
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑成员' : '添加成员'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="账号"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.displayName" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item :label="form.id ? '新密码' : '密码'">
          <el-input v-model="form.password" type="password" :placeholder="form.id ? '留空则不修改' : ''" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleCode" style="width:100%">
            <el-option label="OWNER 老板" value="OWNER" />
            <el-option label="EDITOR 编辑" value="EDITOR" />
            <el-option label="SALES 销售" value="SALES" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const list = ref<any[]>([])
const loading = ref(false)
const visible = ref(false)
const form = reactive<any>({})
const matrix = [
  { module: '租户', owner: '—', editor: '—', sales: '—' },
  { module: '站点 / 绑域名', owner: '读写', editor: '站点读写', sales: '只读站点' },
  { module: '页面 / 商品 / 文章', owner: '读写', editor: '读写', sales: '只读' },
  { module: '媒体 / SEO / 主题', owner: '读写', editor: '读写', sales: '—' },
  { module: '询盘', owner: '读写', editor: '—', sales: '读写' },
  { module: '成员 / 操作日志', owner: '读写', editor: '—', sales: '—' }
]

async function load() {
  loading.value = true
  try {
    const res: any = await http.get('/admin/members')
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}
function open(row?: any) {
  Object.assign(form, row || { id: undefined, username: '', displayName: '', email: '', password: '', roleCode: 'EDITOR', status: 1 })
  if (!row) form.password = ''
  else form.password = ''
  visible.value = true
}
async function save() {
  if (!form.username) {
    ElMessage.warning('请填写账号')
    return
  }
  const payload = { ...form }
  if (!payload.password) delete payload.password
  if (form.id) await http.put('/admin/members/' + form.id, payload)
  else await http.post('/admin/members', payload)
  ElMessage.success('已保存')
  visible.value = false
  load()
}
async function toggle(row: any) {
  await http.post('/admin/members/' + row.id + '/status', { status: row.status === 1 ? 0 : 1 })
  load()
}
function roleLabel(code: string) {
  return ({ OWNER: '老板', EDITOR: '编辑', SALES: '销售', SUPER: '超管' } as any)[code] || code
}
function formatTime(v?: string) {
  return v ? String(v).replace('T', ' ').slice(0, 16) : '从未登录'
}
onMounted(load)
</script>

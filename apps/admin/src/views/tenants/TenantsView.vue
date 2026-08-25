<template>
  <div>
    <div class="page-head">
      <div>
        <h1>租户</h1>
        <p>平台级开通客户。创建后点「进入工作台」，即可在该租户下建站和装修。</p>
      </div>
      <el-button type="primary" @click="open()">新建租户</el-button>
    </div>

    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">还没有租户，点击「新建租户」开通第一家客户。</div>
      <article v-for="row in list" :key="row.id" class="item-card">
        <div class="item-body">
          <div class="item-head">
            <span class="avatar lg">{{ initials(row.name) }}</span>
            <span class="pill" :class="row.status === 1 ? 'pill-live' : 'pill-off'">{{ row.status === 1 ? '启用' : '停用' }}</span>
          </div>
          <h3>{{ row.name }}</h3>
          <p>{{ row.code }}</p>
          <p style="margin-top:8px">{{ row.contactEmail || '未填写邮箱' }}</p>
          <div class="item-tags">
            <span class="mark">{{ row.packageCode || 'standard' }}</span>
          </div>
        </div>
        <div class="item-foot">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="primary" @click="useTenant(row)">进入工作台</el-button>
        </div>
      </article>
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑租户' : '新建租户'" width="520px">
      <el-form :model="form" label-width="108px">
        <el-form-item label="公司名称" required>
          <el-input v-model="form.name" placeholder="如 FuelTech Machinery Co., Ltd." />
        </el-form-item>
        <el-form-item label="租户编码" required>
          <el-input v-model="form.code" placeholder="英文小写，如 fueltech" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="form.contactEmail" placeholder="export@example.com" />
        </el-form-item>
        <el-form-item label="套餐">
          <el-select v-model="form.packageCode" style="width:100%">
            <el-option label="试用 trial" value="trial" />
            <el-option label="标准 standard" value="standard" />
            <el-option label="专业 pro" value="pro" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const list = ref<any[]>([])
const visible = ref(false)
const saving = ref(false)
const form = reactive<any>({ name: '', code: '', contactEmail: '', packageCode: 'standard', status: 1 })

async function load() {
  const res: any = await http.get('/admin/tenants')
  list.value = res.data || []
}
function open(row?: any) {
  Object.assign(form, row || { id: undefined, name: '', code: '', contactEmail: '', packageCode: 'standard', status: 1 })
  visible.value = true
}
async function save() {
  if (!form.name || !form.code) {
    ElMessage.warning('请填写公司名称和租户编码')
    return
  }
  saving.value = true
  try {
    if (form.id) await http.put('/admin/tenants/' + form.id, form)
    else await http.post('/admin/tenants', form)
    ElMessage.success(form.id ? '已更新' : '租户已创建，可点击「进入工作台」')
    visible.value = false
    load()
  } finally {
    saving.value = false
  }
}
function initials(name?: string) {
  return String(name || 'TH').replace(/\s+/g, '').slice(0, 2).toUpperCase()
}
function useTenant(row: any) {
  localStorage.setItem('th_tenant', String(row.id))
  localStorage.removeItem('th_site')
  ElMessage.success('已切换到 ' + row.name)
  location.href = '/'
}
onMounted(load)
</script>

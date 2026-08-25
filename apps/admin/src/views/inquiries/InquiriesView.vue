<template>
  <div>
    <div class="page-head">
      <div>
        <h1>询盘</h1>
        <p>跟进采购线索，从待处理到已报价。</p>
      </div>
      <el-radio-group v-model="status" size="small" @change="load">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="new">待跟进</el-radio-button>
        <el-radio-button label="following">跟进中</el-radio-button>
        <el-radio-button label="quoted">已报价</el-radio-button>
      </el-radio-group>
    </div>
    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">当前没有询盘。</div>
      <article v-for="row in list" :key="row.id" class="item-card clickable" @click="open(row)">
        <div class="item-body">
          <div class="item-head">
            <span class="avatar lg">{{ String(row.name || 'NA').slice(0, 2).toUpperCase() }}</span>
            <span class="pill" :class="'pill-' + row.status">{{ statusLabel(row.status) }}</span>
          </div>
          <h3>{{ row.name }}</h3>
          <p>{{ row.company || row.email }}</p>
          <p style="margin-top:8px">{{ row.country || '—' }} · {{ row.productName || '未指定产品' }}</p>
        </div>
        <div class="item-foot">
          <span class="sub">{{ formatTime(row.createdAt) }}</span>
          <el-button size="small" type="primary">查看</el-button>
        </div>
      </article>
    </div>
    <el-drawer v-model="detailVisible" title="询盘详情" size="420px">
      <p><b>{{ current?.name }}</b> · {{ current?.company }}</p>
      <p>{{ current?.email }} · {{ current?.phone || '-' }}</p>
      <p>{{ current?.country }} · WhatsApp {{ current?.whatsapp || '-' }}</p>
      <p>产品 {{ current?.productName || '-' }} · 数量 {{ current?.quantity || '-' }}</p>
      <el-divider />
      <p style="white-space:pre-wrap">{{ current?.message }}</p>
      <el-divider />
      <el-button @click="setStatus(current, 'following')">跟进</el-button>
      <el-button type="primary" @click="setStatus(current, 'quoted')">已报价</el-button>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const list = ref<any[]>([])
const current = ref<any>(null)
const detailVisible = ref(false)
const status = ref('')
async function load() {
  const res: any = await http.get('/admin/inquiries', { params: { status: status.value || undefined } })
  list.value = res.data?.list || []
}
function open(row: any) {
  current.value = row
  detailVisible.value = true
}
async function setStatus(row: any, next: string) {
  if (!row) return
  await http.post('/admin/inquiries/' + row.id + '/status', { status: next })
  detailVisible.value = false
  load()
}
function statusLabel(status: string) {
  return ({ new: '待跟进', following: '跟进中', quoted: '已报价' } as Record<string, string>)[status] || status
}
function formatTime(v?: string) {
  return v ? String(v).replace('T', ' ').slice(0, 16) : ''
}
onMounted(load)
</script>

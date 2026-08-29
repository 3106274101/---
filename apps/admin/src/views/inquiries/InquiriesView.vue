<template>
  <div>
    <div class="page-head">
      <div>
        <h1>询盘</h1>
        <p>跟进采购线索：备注、下次跟进、星标与打印。</p>
      </div>
      <div class="page-tools">
        <el-input v-model="q" placeholder="姓名 / 公司 / 邮箱 / 产品" style="width:200px" clearable @clear="load" @keyup.enter="load" />
        <el-select v-model="country" clearable placeholder="国家" style="width:130px" @change="load">
          <el-option v-for="c in countries" :key="c" :label="c" :value="c" />
        </el-select>
        <el-checkbox v-model="starred" @change="load">仅星标</el-checkbox>
        <el-checkbox v-model="overdue" @change="load">逾期未跟</el-checkbox>
        <el-button size="small" @click="exportCsv">导出 CSV</el-button>
        <el-radio-group v-model="status" size="small" @change="load">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="new">待跟进</el-radio-button>
          <el-radio-button label="following">跟进中</el-radio-button>
          <el-radio-button label="quoted">已报价</el-radio-button>
          <el-radio-button label="lost">已流失</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div class="card-grid">
      <div v-if="!list.length" class="empty-hint">当前没有询盘。</div>
      <article v-for="row in list" :key="row.id" class="item-card clickable" @click="open(row)">
        <div class="item-body">
          <div class="item-head">
            <span class="avatar lg">{{ String(row.name || 'NA').slice(0, 2).toUpperCase() }}</span>
            <span v-if="row.starred" class="mark">★</span>
            <span v-if="(row.repeatCount || 0) > 1" class="mark">老客</span>
            <span class="pill" :class="'pill-' + row.status">{{ statusLabel(row.status) }}</span>
          </div>
          <h3>{{ row.name }}</h3>
          <p>{{ row.company || row.email }}</p>
          <p style="margin-top:8px">{{ row.country || '—' }} · {{ row.productName || '未指定产品' }}</p>
          <p v-if="row.nextFollowAt" class="sub" :class="{ overdue: isOverdue(row) }">下次跟进 {{ formatTime(row.nextFollowAt) }}</p>
        </div>
        <div class="item-foot">
          <span class="sub">{{ formatTime(row.createdAt) }}</span>
          <el-button size="small" type="primary">查看</el-button>
        </div>
      </article>
    </div>
    <el-drawer v-model="detailVisible" title="询盘详情" size="480px">
      <template v-if="current">
        <p><b>{{ current.name }}</b> · {{ current.company }}</p>
        <p>
          <a :href="'mailto:' + current.email">{{ current.email }}</a>
          · {{ current.phone || '-' }}
        </p>
        <p>{{ current.country }} · WhatsApp {{ current.whatsapp || '-' }}</p>
        <p>产品 {{ current.productName || '-' }} · 数量 {{ current.quantity || '-' }}</p>
        <p class="sub">来源 {{ current.source || 'storefront' }} · {{ formatTime(current.createdAt) }}</p>
        <p v-if="(current.repeatCount || 0) > 1" class="mark">同一邮箱已询盘 {{ current.repeatCount }} 次</p>
        <p v-if="utmText(current)" class="sub">投放 {{ utmText(current) }}</p>
        <el-divider />
        <div v-if="extraEntries(current).length" class="extra-box">
          <p v-for="[k, v] in extraEntries(current)" :key="k"><b>{{ k }}</b> {{ v }}</p>
        </div>
        <p style="white-space:pre-wrap">{{ current.message }}</p>
        <el-divider />
        <el-form label-width="90px">
          <el-form-item label="分配给">
            <el-select v-model="assignee" placeholder="成员" style="width:100%" @change="assign">
              <el-option v-for="m in members" :key="m.id" :label="m.displayName || m.username" :value="m.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="下次跟进">
            <el-date-picker v-model="followAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" @change="saveFollow" />
          </el-form-item>
          <el-form-item label="星标">
            <el-switch :model-value="!!current.starred" @change="(v: boolean) => toggleStar(v)" />
          </el-form-item>
        </el-form>
        <div class="note-box">
          <h4>跟进备注</h4>
          <div v-for="(n, i) in notes" :key="i" class="note-item">
            <span>{{ n.at }} · {{ n.user }}</span>
            <p>{{ n.body }}</p>
          </div>
          <el-input v-model="noteBody" type="textarea" :rows="3" placeholder="记录电话、报价要点…" />
          <el-button size="small" type="primary" style="margin-top:8px" @click="addNote">添加备注</el-button>
        </div>
        <div style="display:flex;flex-wrap:wrap;gap:8px;margin-top:16px">
          <el-button @click="setStatus(current, 'following')">跟进</el-button>
          <el-button type="primary" @click="setStatus(current, 'quoted')">已报价</el-button>
          <el-button @click="setStatus(current, 'lost')">流失</el-button>
          <el-button @click="mailTo">写邮件</el-button>
          <el-button @click="printOne">打印</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const list = ref<any[]>([])
const members = ref<any[]>([])
const countries = ref<string[]>([])
const current = ref<any>(null)
const detailVisible = ref(false)
const status = ref('')
const q = ref('')
const country = ref('')
const starred = ref(false)
const overdue = ref(false)
const assignee = ref<number | undefined>()
const followAt = ref('')
const noteBody = ref('')

const notes = computed(() => parseNotes(current.value?.notesJson))

function parseNotes(raw?: string) {
  try {
    const arr = JSON.parse(raw || '[]')
    return Array.isArray(arr) ? arr : []
  } catch {
    return []
  }
}
function isOverdue(row: any) {
  if (!row.nextFollowAt) return false
  if (row.status === 'quoted' || row.status === 'lost') return false
  return new Date(String(row.nextFollowAt).replace(' ', 'T')).getTime() < Date.now()
}

async function load() {
  const res: any = await http.get('/admin/inquiries', {
    params: {
      status: status.value || undefined,
      country: country.value || undefined,
      q: q.value || undefined,
      starred: starred.value || undefined,
      overdue: overdue.value || undefined
    }
  })
  list.value = res.data?.list || []
  try {
    const mem: any = await http.get('/admin/members')
    members.value = mem.data || []
  } catch {
    members.value = []
  }
  try {
    const c: any = await http.get('/admin/inquiries/countries')
    countries.value = c.data || []
  } catch {
    countries.value = []
  }
}
function open(row: any) {
  current.value = row
  assignee.value = row.assignedUserId
  followAt.value = row.nextFollowAt ? String(row.nextFollowAt).replace('T', ' ').slice(0, 19) : ''
  noteBody.value = ''
  detailVisible.value = true
}
async function assign() {
  if (!current.value) return
  await http.post('/admin/inquiries/' + current.value.id + '/assign', { userId: assignee.value })
  ElMessage.success('已分配')
}
async function saveFollow() {
  if (!current.value) return
  const res: any = await http.post('/admin/inquiries/' + current.value.id + '/follow', { nextFollowAt: followAt.value || '' })
  current.value = res.data
  load()
}
async function toggleStar(v: boolean) {
  if (!current.value) return
  const res: any = await http.post('/admin/inquiries/' + current.value.id + '/follow', { starred: v ? 1 : 0 })
  current.value = res.data
  load()
}
async function addNote() {
  if (!current.value || !noteBody.value.trim()) return
  const res: any = await http.post('/admin/inquiries/' + current.value.id + '/notes', { body: noteBody.value.trim() })
  current.value = res.data
  noteBody.value = ''
  ElMessage.success('已记录')
}
async function exportCsv() {
  const res: any = await http.get('/admin/inquiries/export', { params: { status: status.value || undefined } })
  const blob = new Blob([res.data.csv || ''], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = res.data.filename || 'inquiries.csv'
  a.click()
}
async function setStatus(row: any, next: string) {
  if (!row) return
  await http.post('/admin/inquiries/' + row.id + '/status', { status: next })
  detailVisible.value = false
  load()
}
function mailTo() {
  if (!current.value?.email) return
  const subject = encodeURIComponent('Re: quotation · ' + (current.value.productName || 'inquiry'))
  const body = encodeURIComponent(`Hi ${current.value.name},\n\nThanks for your inquiry about ${current.value.productName || 'our products'}.\n\n`)
  window.open(`mailto:${current.value.email}?subject=${subject}&body=${body}`)
}
function printOne() {
  if (!current.value) return
  const w = window.open('', '_blank')
  if (!w) return
  const n = parseNotes(current.value.notesJson).map((x: any) => `<li>${x.at} ${x.user}: ${x.body}</li>`).join('')
  w.document.write(`<!doctype html><title>Inquiry ${current.value.id}</title>
    <h2>${current.value.name} · ${current.value.company || ''}</h2>
    <p>${current.value.email} · ${current.value.phone || ''} · ${current.value.country || ''}</p>
    <p>Product: ${current.value.productName || '-'} Qty: ${current.value.quantity || '-'}</p>
    <pre>${current.value.message || ''}</pre>
    <h3>Notes</h3><ul>${n}</ul>`)
  w.document.close()
  w.print()
}
function statusLabel(status: string) {
  return ({ new: '待跟进', following: '跟进中', quoted: '已报价', lost: '已流失' } as Record<string, string>)[status] || status
}
function formatTime(v?: string) {
  return v ? String(v).replace('T', ' ').slice(0, 16) : ''
}
function extraEntries(row: any): [string, string][] {
  try {
    const raw = row.extra || row.extraJson
    const map = typeof raw === 'string' ? JSON.parse(raw || '{}') : (raw || {})
    return Object.entries(map).filter(([, v]) => v !== null && String(v).trim())
      .map(([k, v]) => [k, String(v)])
  } catch {
    return []
  }
}
function utmText(row: any) {
  try {
    const u = typeof row.utmJson === 'string' ? JSON.parse(row.utmJson || '{}') : row.utm || {}
    const parts = [u.source, u.medium, u.campaign, u.gclid].filter(Boolean)
    return parts.join(' / ')
  } catch {
    return ''
  }
}
onMounted(load)
</script>

<style scoped>
.overdue { color: #b45309; font-weight: 600; }
.note-box h4 { margin: 0 0 8px; }
.note-item { border-bottom: 1px solid var(--th-line); padding: 8px 0; }
.note-item span { color: var(--th-muted); font-size: 12px; }
.note-item p { margin: 4px 0 0; white-space: pre-wrap; }
.extra-box { margin-bottom: 12px; }
.extra-box p { margin: 4px 0; }
.extra-box b { display: inline-block; min-width: 120px; color: var(--th-muted); }
</style>

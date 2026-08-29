<template>
  <div>
    <div class="page-head">
      <div>
        <h1>工作台</h1>
        <p>当前租户的站点、商品与询盘概况。</p>
      </div>
      <div>
        <el-button @click="$router.push('/pages')">页面装修</el-button>
        <el-button type="primary" @click="$router.push('/sites')">新建站点</el-button>
      </div>
    </div>

    <div class="stat-row">
      <div class="stat clickable" @click="$router.push('/sites')">
        <span class="stat-ico"><el-icon><Monitor /></el-icon></span>
        <div><b>{{ data.sites || 0 }}</b><span>站点</span></div>
      </div>
      <div class="stat clickable" @click="$router.push('/products')">
        <span class="stat-ico"><el-icon><Goods /></el-icon></span>
        <div><b>{{ data.products || 0 }}</b><span>商品</span></div>
      </div>
      <div class="stat clickable" @click="$router.push('/inquiries')">
        <span class="stat-ico"><el-icon><ChatDotSquare /></el-icon></span>
        <div><b>{{ data.inquiriesWeek || 0 }}</b><span>本周询盘</span></div>
      </div>
      <div class="stat clickable" @click="$router.push('/inquiries')">
        <span class="stat-ico"><el-icon><Share /></el-icon></span>
        <div><b>{{ data.overdueFollowups || 0 }}</b><span>逾期跟进</span></div>
      </div>
    </div>
    <div class="stat-row" style="margin-top:12px">
      <div class="stat clickable" @click="$router.push('/products')">
        <span class="stat-ico"><el-icon><Goods /></el-icon></span>
        <div><b>{{ data.draftProducts || 0 }}</b><span>草稿商品</span></div>
      </div>
      <div class="stat clickable" @click="$router.push('/products')">
        <span class="stat-ico"><el-icon><Picture /></el-icon></span>
        <div><b>{{ data.missingCover || 0 }}</b><span>缺封面</span></div>
      </div>
      <div class="stat clickable" @click="$router.push('/inquiries')">
        <span class="stat-ico"><el-icon><ChatDotSquare /></el-icon></span>
        <div><b>{{ data.newInquiries || 0 }}</b><span>待处理询盘</span></div>
      </div>
      <div class="stat">
        <span class="stat-ico"><el-icon><Share /></el-icon></span>
        <div><b>{{ (data.funnel && data.funnel.quoted) || 0 }}</b><span>已报价</span></div>
      </div>
      <div class="stat">
        <span class="stat-ico"><el-icon><Share /></el-icon></span>
        <div><b>{{ data.languages || 0 }}</b><span>语言</span></div>
      </div>
    </div>

    <div class="dash-grid">
      <div>
        <div class="block-head">站点</div>
        <div class="card-grid dash-sites">
          <div v-if="!(data.sitesList || []).length" class="empty-hint">还没有站点。</div>
          <article v-for="s in data.sitesList || []" :key="s.id" class="item-card">
            <div class="item-body">
              <div class="item-head">
                <span class="avatar lg">{{ initials(s.name) }}</span>
                <span class="pill" :class="pillClass(s.status)">{{ statusLabel(s.status) }}</span>
              </div>
              <h3>{{ s.name }}</h3>
              <p>{{ s.code }}.local · {{ s.theme }}</p>
            </div>
            <div class="item-foot">
              <el-button size="small" @click="$router.push('/theme')">品牌</el-button>
              <el-button size="small" type="primary" @click="$router.push('/pages')">装修</el-button>
            </div>
          </article>
        </div>
      </div>

      <div>
        <div class="block-head dash-inq-h">
          <span>最新询盘</span>
          <el-button link type="primary" @click="$router.push('/inquiries')">全部</el-button>
        </div>
        <div class="card-grid dash-sites">
          <div v-if="!(data.recentInquiries || []).length" class="empty-hint">暂无询盘。</div>
          <article v-for="row in data.recentInquiries || []" :key="row.id" class="item-card clickable" @click="goInquiry">
            <div class="item-body">
              <div class="item-head">
                <span class="avatar lg">{{ initials(row.name) }}</span>
                <span class="pill" :class="'pill-' + row.status">{{ inquiryLabel(row.status) }}</span>
              </div>
              <h3>{{ row.name }}</h3>
              <p>{{ row.company || row.email }} · {{ row.country }}</p>
            </div>
          </article>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotSquare, Goods, Monitor, Picture, Share } from '@element-plus/icons-vue'
import http from '../../api/http'

const data = ref<any>({})
const router = useRouter()
onMounted(async () => {
  const res: any = await http.get('/admin/dashboard')
  data.value = res.data || {}
})
function goInquiry() {
  router.push('/inquiries')
}
function initials(name?: string) {
  return String(name || 'TH').replace(/\s+/g, '').slice(0, 2).toUpperCase()
}
function statusLabel(status: string) {
  return ({ live: '已上线', building: '建设中', draft: '草稿', disabled: '停用' } as any)[status] || status
}
function pillClass(status: string) {
  if (status === 'live') return 'pill-live'
  if (status === 'building' || status === 'draft') return 'pill-building'
  return 'pill-off'
}
function inquiryLabel(status: string) {
  return ({ new: '待跟进', following: '跟进中', quoted: '已报价', lost: '已流失' } as Record<string, string>)[status] || status
}
</script>

<style scoped>
.dash-grid { display: grid; grid-template-columns: 1.15fr 0.85fr; gap: 16px; align-items: start; }
.dash-sites { grid-template-columns: 1fr; }
.dash-inq-h { display: flex; justify-content: space-between; align-items: center; }
.stat.clickable { cursor: pointer; }
@media (max-width: 1100px) { .dash-grid { grid-template-columns: 1fr; } }
</style>

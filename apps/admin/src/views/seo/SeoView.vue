<template>
  <div>
    <div class="page-head">
      <div>
        <h1>SEO</h1>
        <p>前台自动输出 Title、hreflang 与 JSON-LD。这里管理爬虫入口和旧地址 301。</p>
      </div>
    </div>
    <div class="seo-grid">
      <div class="panel">
        <div class="panel-h">上线健康度</div>
        <div class="panel-pad">
          <div class="health-score">{{ health.score ?? '—' }}</div>
          <p class="form-hint">按首页发布、域名、封面图、GA4 和 SEO Title 打分。</p>
          <ul class="health-list">
            <li v-if="!(health.issues || []).length" class="ok">暂无明显问题</li>
            <li v-for="(issue, i) in health.issues || []" :key="i" :class="issue.level">{{ issue.text }}</li>
          </ul>
          <el-button size="small" @click="loadHealth">重新检查</el-button>
        </div>
      </div>
      <div class="panel">
        <div class="panel-h">robots.txt</div>
        <pre class="code-box">{{ robots || '（暂无）' }}</pre>
        <div class="panel-pad seo-links">
          <a :href="storePreview('/robots.txt')" target="_blank">打开 robots.txt</a>
          <a :href="storePreview('/sitemap.xml')" target="_blank">打开 sitemap.xml</a>
        </div>
      </div>
      <div class="panel">
        <div class="panel-h">301 重定向</div>
        <div class="panel-pad">
          <el-form :inline="true" :model="form">
            <el-form-item label="从"><el-input v-model="form.fromPath" placeholder="/old-pump" /></el-form-item>
            <el-form-item label="到"><el-input v-model="form.toPath" placeholder="/products/t80-fuel-dispenser" /></el-form-item>
            <el-form-item><el-button type="primary" @click="save">添加</el-button></el-form-item>
          </el-form>
        </div>
        <el-table :data="redirects">
          <el-table-column prop="fromPath" label="From" />
          <el-table-column prop="toPath" label="To" />
          <el-table-column prop="code" label="Code" width="80" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import http from '../../api/http'
import { storePreview } from '../../config'

const robots = ref('')
const redirects = ref<any[]>([])
const health = ref<any>({ score: 0, issues: [] })
const form = reactive({ fromPath: '', toPath: '', code: 301, siteId: Number(localStorage.getItem('th_site') || 1) })
async function load() {
  const r: any = await http.get('/admin/seo/robots')
  robots.value = r.data?.content || ''
  const d: any = await http.get('/admin/seo/redirects', { params: { siteId: form.siteId } })
  redirects.value = d.data || []
  await loadHealth()
}
async function loadHealth() {
  form.siteId = Number(localStorage.getItem('th_site') || 1)
  try {
    const h: any = await http.get('/admin/seo/health', { params: { siteId: form.siteId } })
    health.value = h.data || { score: 0, issues: [] }
  } catch {
    health.value = { score: 0, issues: [{ level: 'error', text: '无法读取健康检查' }] }
  }
}
async function save() {
  form.siteId = Number(localStorage.getItem('th_site') || 1)
  await http.post('/admin/seo/redirects', form)
  form.fromPath = ''
  form.toPath = ''
  load()
}
onMounted(load)
</script>

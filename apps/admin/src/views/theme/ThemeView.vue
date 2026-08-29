<template>
  <div class="theme-page" v-if="site">
    <div class="page-head">
      <div>
        <h1>品牌装修</h1>
        <p>各行业通用：公司名、配色、目录文案、导航和询盘字段都按站点配置，不绑定某一类产品。</p>
      </div>
      <el-button type="primary" :loading="saving" @click="save">保存主题</el-button>
    </div>

    <div class="theme-grid">
      <el-card header="实时预览">
        <div class="preview" :style="tokenStyle">
          <div class="preview-top">{{ brand.email }} · {{ brand.phone }}</div>
          <div class="preview-nav">
            <b>{{ logoLeft }}<em>{{ logoRight }}</em></b>
            <span>Home</span><span>Products</span><span class="preview-btn">Get Quote</span>
          </div>
          <div class="preview-hero">
            <div class="preview-hero-img" :style="{ backgroundImage: `url(${brand.heroImage})` }" />
            <div class="preview-hero-copy">
              <h3>{{ brand.tagline || brand.catalogTitle }}</h3>
              <small>{{ brand.trustLine || brand.founded }}</small>
              <i>Get Quote</i>
            </div>
          </div>
        </div>
      </el-card>

      <el-card header="品牌与联系">
        <el-form label-position="top">
          <el-form-item label="站点名称"><el-input v-model="site.name" placeholder="如 Acme Lighting" /></el-form-item>
          <el-form-item label="Logo 文字"><el-input v-model="brand.logoText" placeholder="公司品牌名" /></el-form-item>
          <el-form-item label="口号 / Tagline"><el-input v-model="brand.tagline" placeholder="一句话介绍公司与产品线" /></el-form-item>
          <div class="color-row">
            <el-form-item label="主色">
              <el-color-picker v-model="brand.primaryColor" />
              <el-input v-model="brand.primaryColor" />
            </el-form-item>
            <el-form-item label="强调色 / 按钮">
              <el-color-picker v-model="brand.accentColor" />
              <el-input v-model="brand.accentColor" />
            </el-form-item>
          </div>
          <el-form-item label="主视觉图"><MediaPicker v-model="brand.heroImage" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="brand.email" /></el-form-item>
          <el-form-item label="电话"><el-input v-model="brand.phone" /></el-form-item>
          <el-form-item label="WhatsApp"><el-input v-model="brand.whatsapp" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="brand.address" /></el-form-item>
          <el-form-item label="成立年份"><el-input v-model="brand.founded" /></el-form-item>
          <el-form-item label="出口市场"><el-input v-model="brand.countries" placeholder="如 80+ countries" /></el-form-item>
          <el-form-item label="页头信任条">
            <el-input v-model="brand.trustLine" placeholder="如 ISO 9001 · OEM · Est. 2012" />
            <div class="form-hint">留空则只显示已填写的市场/年份，不会写死某一行业证书。</div>
          </el-form-item>
          <el-form-item label="页脚后缀"><el-input v-model="brand.footerNote" placeholder="可选，如 Machinery / Co., Ltd." /></el-form-item>
          <el-form-item label="GA4 测量 ID">
            <el-input v-model="brand.ga4Id" placeholder="G-XXXXXXXX" />
            <div class="form-hint">填写后独立站会注入 gtag，用于询盘来源与流量。</div>
          </el-form-item>
          <el-form-item label="测试邮件">
            <el-input v-model="testMailTo" placeholder="你的邮箱" />
            <el-button size="small" style="margin-top:8px" :loading="mailSending" @click="sendTestMail">发送测试邮件</el-button>
            <div class="form-hint">需开启 TRADEHUB_MAIL_ENABLED 并配置 SMTP，用于验证询盘通知。</div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="theme-grid" style="margin-top:16px">
      <el-card header="目录文案与导航">
        <el-form label-position="top">
          <el-form-item label="产品列表标题"><el-input v-model="brand.catalogTitle" placeholder="Products" /></el-form-item>
          <el-form-item label="产品列表说明"><el-input v-model="brand.catalogLead" type="textarea" :rows="2" placeholder="浏览目录并提交询盘" /></el-form-item>
          <el-form-item label="询盘页说明"><el-input v-model="brand.inquiryLead" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="询盘提示（每行一条）">
            <el-input v-model="hintsText" type="textarea" :rows="3" placeholder="数量与目的港&#10;关键规格或图纸&#10;是否 OEM" />
          </el-form-item>
          <el-form-item label="底部询盘条"><el-input v-model="brand.stickyHint" placeholder="Need a quotation?" /></el-form-item>
          <el-form-item label="导航显示">
            <div class="nav-checks">
              <el-checkbox v-model="brand.navShow.products">产品</el-checkbox>
              <el-checkbox v-model="brand.navShow.solutions">方案</el-checkbox>
              <el-checkbox v-model="brand.navShow.factory">工厂</el-checkbox>
              <el-checkbox v-model="brand.navShow.about">关于</el-checkbox>
              <el-checkbox v-model="brand.navShow.blog">博客</el-checkbox>
              <el-checkbox v-model="brand.navShow.contact">联系</el-checkbox>
            </div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card header="询盘自定义字段">
        <p class="form-hint" style="margin-top:0">不同行业问不同的问题：灯具问功率，纺织问克重，设备问电压。字段只属于当前站点。</p>
        <div v-for="(row, i) in brand.inquiryFields" :key="i" class="field-row">
          <el-input v-model="row.key" placeholder="字段 key" />
          <el-input v-model="row.label" placeholder="英文标签" />
          <el-input v-model="row.labelZh" placeholder="中文标签" />
          <el-select v-model="row.type" style="width:110px">
            <el-option label="文本" value="text" />
            <el-option label="下拉" value="select" />
          </el-select>
          <el-input v-if="row.type === 'select'" v-model="row.optionsText" placeholder="选项，逗号分隔" />
          <el-input v-else v-model="row.placeholder" placeholder="占位提示" />
          <el-button @click="brand.inquiryFields.splice(i, 1)">删</el-button>
        </div>
        <el-button size="small" @click="addField">加一个字段</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'
import MediaPicker from '../../components/MediaPicker.vue'

const site = ref<any>(null)
const saving = ref(false)
const mailSending = ref(false)
const testMailTo = ref('')
const hintsText = ref('')
const brand = reactive<any>({
  logoText: '',
  tagline: '',
  primaryColor: '#0b1f3a',
  accentColor: '#c2410c',
  email: '',
  phone: '',
  whatsapp: '',
  address: '',
  founded: '',
  countries: '',
  heroImage: '',
  ga4Id: '',
  trustLine: '',
  footerNote: '',
  catalogTitle: '',
  catalogLead: '',
  inquiryLead: '',
  stickyHint: '',
  inquiryFields: [],
  navShow: { products: true, solutions: false, factory: false, about: true, blog: true, contact: true }
})

const tokenStyle = computed(() => ({
  '--p': brand.primaryColor || '#0b1f3a',
  '--a': brand.accentColor || '#e85d04'
}))
const displayName = computed(() => String(brand.logoText || site.value?.name || 'Brand'))
const logoLeft = computed(() => displayName.value.slice(0, Math.max(1, displayName.value.length - 2)))
const logoRight = computed(() => displayName.value.slice(Math.max(1, displayName.value.length - 2)))

onMounted(async () => {
  const id = localStorage.getItem('th_site')
  if (!id) {
    const list: any = await http.get('/admin/sites')
    const first = list.data?.list?.[0]
    if (first) localStorage.setItem('th_site', String(first.id))
  }
  const siteId = localStorage.getItem('th_site')
  const res: any = await http.get('/admin/sites/' + siteId)
  site.value = res.data
  Object.assign(brand, res.data?.brand || {})
  const nav = brand.navShow && typeof brand.navShow === 'object' ? brand.navShow : {}
  brand.navShow = {
    products: true, solutions: true, factory: true, about: true, blog: true, contact: true, ...nav
  }
  const fields = Array.isArray(brand.inquiryFields) ? brand.inquiryFields : []
  brand.inquiryFields = fields.map((row: any) => ({
    key: row.key || '',
    label: row.label || '',
    labelZh: row.labelZh || '',
    type: row.type || 'text',
    placeholder: row.placeholder || '',
    optionsText: Array.isArray(row.options) ? row.options.join(', ') : (row.optionsText || '')
  }))
  hintsText.value = Array.isArray(brand.inquiryHints) ? brand.inquiryHints.join('\n') : ''
})

function addField() {
  brand.inquiryFields.push({ key: '', label: '', labelZh: '', type: 'text', placeholder: '', optionsText: '' })
}

async function save() {
  saving.value = true
  try {
    const payload = {
      ...brand,
      inquiryHints: hintsText.value.split('\n').map((s) => s.trim()).filter(Boolean),
      inquiryFields: (brand.inquiryFields || []).filter((r: any) => r.key).map((r: any) => ({
        key: r.key,
        label: r.label,
        labelZh: r.labelZh,
        type: r.type || 'text',
        placeholder: r.placeholder || '',
        options: String(r.optionsText || '').split(',').map((s: string) => s.trim()).filter(Boolean)
      }))
    }
    await http.put('/admin/sites/' + site.value.id, {
      name: site.value.name,
      brand: payload
    })
    ElMessage.success('主题已保存，刷新独立站即可看到新配色与文案')
  } finally {
    saving.value = false
  }
}
async function sendTestMail() {
  mailSending.value = true
  try {
    const res: any = await http.post('/admin/mail/test', { to: testMailTo.value })
    if (res.data?.ok) ElMessage.success(res.data.message || '已发送')
    else ElMessage.warning(res.data?.message || '未发送，请检查 SMTP')
  } finally {
    mailSending.value = false
  }
}
</script>

<style scoped>
.theme-grid { display: grid; grid-template-columns: 1.1fr 0.9fr; gap: 16px; }
.color-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.color-row :deep(.el-input) { margin-top: 8px; }
.preview { border: 1px solid #e6ebf0; overflow: hidden; }
.preview-top { background: var(--p); color: #d7e2ef; font-size: 12px; padding: 8px 12px; }
.preview-nav { display: flex; gap: 16px; align-items: center; padding: 12px; background: #fff; }
.preview-nav b { color: var(--p); }
.preview-nav em { color: var(--a); font-style: normal; }
.preview-btn { margin-left: auto; background: var(--a); color: #fff; padding: 6px 10px; font-size: 12px; }
.preview-hero { display: grid; grid-template-columns: 1fr 1fr; min-height: 180px; }
.preview-hero-img { background: #ccc center/cover no-repeat; min-height: 180px; }
.preview-hero-copy { padding: 20px; color: var(--p); }
.preview-hero-copy h3 { margin: 0 0 8px; font-size: 18px; }
.preview-hero-copy i { display: inline-block; margin-top: 10px; background: var(--a); color: #fff; font-style: normal; padding: 6px 10px; font-size: 12px; }
.form-hint { margin-top: 6px; color: #7a8694; font-size: 12px; line-height: 1.5; }
.nav-checks { display: flex; flex-wrap: wrap; gap: 8px 16px; }
.field-row { display: grid; grid-template-columns: 90px 1fr 1fr 110px 1fr auto; gap: 8px; margin-bottom: 8px; }
@media (max-width: 980px) {
  .theme-grid { grid-template-columns: 1fr; }
  .field-row { grid-template-columns: 1fr 1fr; }
}
</style>

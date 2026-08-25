<template>
  <div class="theme-page" v-if="site">
    <div class="page-head">
      <div>
        <h1>品牌装修</h1>
        <p>Logo、主色、联系方式会立刻作用到独立站页头、按钮和询盘入口，不用改每一页。</p>
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
              <h3>{{ brand.tagline }}</h3>
              <small>17 years · Xinxiang, Henan</small>
              <i>Get Quote</i>
            </div>
          </div>
        </div>
      </el-card>

      <el-card header="品牌与联系">
        <el-form label-position="top">
          <el-form-item label="站点名称"><el-input v-model="site.name" /></el-form-item>
          <el-form-item label="Logo 文字"><el-input v-model="brand.logoText" placeholder="ZhengHe" /></el-form-item>
          <el-form-item label="口号 / Tagline"><el-input v-model="brand.tagline" /></el-form-item>
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
          <el-form-item label="主视觉图 URL"><el-input v-model="brand.heroImage" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="brand.email" /></el-form-item>
          <el-form-item label="电话"><el-input v-model="brand.phone" /></el-form-item>
          <el-form-item label="WhatsApp"><el-input v-model="brand.whatsapp" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="brand.address" /></el-form-item>
          <el-form-item label="成立年份"><el-input v-model="brand.founded" /></el-form-item>
          <el-form-item label="出口国家"><el-input v-model="brand.countries" /></el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const site = ref<any>(null)
const saving = ref(false)
const brand = reactive<any>({
  logoText: 'ZhengHe',
  tagline: '',
  primaryColor: '#0b1f3a',
  accentColor: '#c2410c',
  email: '',
  phone: '',
  whatsapp: '',
  address: '',
  founded: '',
  countries: '',
  heroImage: ''
})

const tokenStyle = computed(() => ({
  '--p': brand.primaryColor || '#0b1f3a',
  '--a': brand.accentColor || '#e85d04'
}))
const logoLeft = computed(() => {
  const s = String(brand.logoText || 'ZhengHe')
  return s.slice(0, Math.max(1, s.length - 2))
})
const logoRight = computed(() => {
  const s = String(brand.logoText || 'ZhengHe')
  return s.slice(Math.max(1, s.length - 2))
})

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
})

async function save() {
  saving.value = true
  try {
    await http.put('/admin/sites/' + site.value.id, {
      name: site.value.name,
      brand
    })
    ElMessage.success('主题已保存，刷新独立站即可看到新配色')
  } finally {
    saving.value = false
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
@media (max-width: 980px) { .theme-grid { grid-template-columns: 1fr; } }
</style>

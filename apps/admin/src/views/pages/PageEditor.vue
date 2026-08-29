<template>
  <div class="builder" v-if="page">
    <header class="builder-bar">
      <div class="builder-bar-left">
        <el-button text @click="$router.push('/pages')">返回</el-button>
        <strong>页面装修</strong>
        <el-tag size="small">{{ localeLabel }}</el-tag>
        <el-input v-model="page.title" size="small" style="width: 200px" />
        <el-tag size="small">{{ page.slug }}</el-tag>
      </div>
      <div class="device-switch">
        <button :class="{ on: device === 'desktop' }" @click="device = 'desktop'">桌面</button>
        <button :class="{ on: device === 'tablet' }" @click="device = 'tablet'">平板</button>
        <button :class="{ on: device === 'mobile' }" @click="device = 'mobile'">手机</button>
      </div>
      <div class="builder-bar-right">
        <el-select v-model="page.status" size="small" style="width: 120px">
          <el-option label="草稿" value="draft" />
          <el-option label="定时发布" value="scheduled" />
          <el-option label="已发布" value="live" />
        </el-select>
        <el-date-picker
          v-if="page.status === 'scheduled'"
          v-model="page.scheduledAt"
          type="datetime"
          size="small"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="发布时间"
          style="width: 190px"
        />
        <el-button @click="previewSite">预览站点</el-button>
        <el-button type="primary" :loading="saving" @click="save" style="background:#0a2540;border-color:#0a2540">保存</el-button>
      </div>
    </header>

    <div class="builder-body">
      <aside class="palette">
        <h4>组件</h4>
        <p class="hint">点击即可插入到画布，像装修一样搭页面。</p>
        <button v-for="item in catalog" :key="item.type" class="palette-item" @click="insert(item.type)">
          <b>{{ item.label }}</b>
          <span>{{ item.hint }}</span>
        </button>
      </aside>

      <main class="stage">
        <div class="stage-frame" :class="device">
          <div class="drop" :class="{ on: insertPointer === 0 }" @click="insertPointer = 0">+ 在此插入区块</div>
          <div
            v-for="(block, idx) in page.blocks"
            :key="idx"
          >
            <div class="canvas-block" :class="{ selected: selected === idx }" @click="selected = idx">
              <span class="block-tag" v-if="selected === idx">{{ labelOf(block.type) }}</span>
              <div class="block-toolbar" v-if="selected === idx">
                <button @click.stop="move(idx, -1)">上移</button>
                <button @click.stop="move(idx, 1)">下移</button>
                <button @click.stop="duplicate(idx)">复制</button>
                <button @click.stop="remove(idx)">删除</button>
              </div>
              <CanvasBlock :block="block" :products="products" :articles="articles" />
            </div>
            <div class="drop" :class="{ on: insertPointer === idx + 1 }" @click="insertPointer = idx + 1">+ 在此插入区块</div>
          </div>
          <div v-if="!page.blocks.length" class="empty-canvas">从左侧选择组件开始装修首页</div>
        </div>
      </main>

      <aside class="inspector">
        <template v-if="current">
          <h4>{{ labelOf(current.type) }}</h4>
          <el-form label-position="top" size="small">
            <el-form-item v-if="'heading' in current.props" label="标题">
              <el-input v-model="current.props.heading" />
            </el-form-item>
            <el-form-item v-if="'subtitle' in current.props" label="副标题">
              <el-input v-model="current.props.subtitle" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item v-if="'cta' in current.props" label="按钮文案">
              <el-input v-model="current.props.cta" />
            </el-form-item>
            <el-form-item v-if="'ctaTo' in current.props" label="按钮链接">
              <el-input v-model="current.props.ctaTo" />
            </el-form-item>
            <el-form-item v-if="current.type === 'hero'" label="主视觉布局">
              <el-radio-group v-model="current.props.layout">
                <el-radio-button label="overlay">叠图</el-radio-button>
                <el-radio-button label="split">左右分栏</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="'image' in current.props" label="图片 URL">
              <el-input v-model="current.props.image" />
              <img v-if="current.props.image" :src="current.props.image" class="insp-img" />
            </el-form-item>
            <el-form-item v-if="'text' in current.props" label="正文">
              <el-input v-model="current.props.text" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item v-if="'html' in current.props" label="HTML">
              <el-input v-model="current.props.html" type="textarea" :rows="6" />
            </el-form-item>
            <el-form-item v-if="'title' in current.props" label="表单标题">
              <el-input v-model="current.props.title" />
            </el-form-item>
            <el-form-item v-if="Array.isArray(current.props.items) && current.type === 'trustBar'" label="信任项">
              <el-input v-model="trustText" type="textarea" :rows="4" @blur="syncTrust" />
              <p class="hint">每行一项，如 CE / ISO 9001</p>
            </el-form-item>
            <el-form-item v-if="current.type === 'certificates'" label="证书">
              <el-input v-model="certText" type="textarea" :rows="4" @blur="syncCert" />
            </el-form-item>
            <el-form-item v-if="current.type === 'faq'" label="问答">
              <div v-for="(f, i) in current.props.items" :key="i" class="mini-card">
                <el-input v-model="f.q" placeholder="问题" />
                <el-input v-model="f.a" placeholder="回答" type="textarea" :rows="2" style="margin-top:6px" />
                <el-button text type="danger" @click="current.props.items.splice(i,1)">删除</el-button>
              </div>
              <el-button size="small" @click="current.props.items.push({ q: '', a: '' })">加一条</el-button>
            </el-form-item>
            <el-form-item v-if="current.type === 'solutions'" label="场景">
              <div v-for="(s, i) in current.props.items" :key="i" class="mini-card">
                <el-input v-model="s.slug" placeholder="slug，如 gas-station" />
                <el-input v-model="s.title" placeholder="标题" style="margin-top:6px" />
                <el-input v-model="s.text" placeholder="说明" style="margin-top:6px" />
              </div>
            </el-form-item>
            <el-form-item v-if="current.type === 'testimonials'" label="评价">
              <div v-for="(t, i) in current.props.items" :key="i" class="mini-card">
                <el-input v-model="t.quote" placeholder="原话" type="textarea" :rows="2" />
                <el-input v-model="t.name" placeholder="身份" style="margin-top:6px" />
                <el-input v-model="t.country" placeholder="国家" style="margin-top:6px" />
              </div>
              <el-button size="small" @click="current.props.items.push({ quote: '', name: '', country: '' })">加一条</el-button>
            </el-form-item>
            <el-form-item v-if="current.type === 'logoWall'" label="市场/品牌">
              <el-input v-model="logoText" type="textarea" :rows="3" @blur="syncLogo" />
            </el-form-item>
            <el-form-item v-if="current.type === 'specTable'" label="列名">
              <div style="display:flex;gap:8px">
                <el-input v-model="ensureCols(current)[0]" placeholder="列 1" />
                <el-input v-model="ensureCols(current)[1]" placeholder="列 2" />
                <el-input v-model="ensureCols(current)[2]" placeholder="列 3" />
              </div>
            </el-form-item>
            <el-form-item v-if="current.type === 'specTable'" label="对照行">
              <div v-for="(r, i) in current.props.rows" :key="i" class="mini-card">
                <el-input v-model="r.model" placeholder="型号 / SKU" />
                <el-input v-model="r.flow" placeholder="参数一" style="margin-top:6px" />
                <el-input v-model="r.hoses" placeholder="参数二" style="margin-top:6px" />
              </div>
              <el-button size="small" @click="current.props.rows.push({ model: '', flow: '', hoses: '' })">加一行</el-button>
            </el-form-item>
          </el-form>
        </template>
        <template v-else>
          <h4>页面 SEO</h4>
          <p class="hint">未选中区块时，编辑整页搜索信息。</p>
        </template>
        <el-divider />
        <h4>页面 SEO</h4>
        <el-form label-position="top" size="small">
          <el-form-item label="SEO Title"><el-input v-model="page.seoTitle" /></el-form-item>
          <el-form-item label="Meta Description"><el-input v-model="page.seoDescription" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="Canonical"><el-input v-model="page.canonical" /></el-form-item>
          <el-form-item label="OG 图"><el-input v-model="page.ogImage" /></el-form-item>
        </el-form>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../api/http'
import { storePreview } from '../../config'
import { BLOCK_CATALOG, createBlock, type BlockType } from './blockCatalog'
import CanvasBlock from './CanvasBlock.vue'
import { localeLabel as formatLocale } from '../../utils/locales'

const route = useRoute()
const localeLabel = formatLocale(localStorage.getItem('th_locale') || 'en')
const page = ref<any>(null)
const selected = ref(0)
const device = ref<'desktop' | 'tablet' | 'mobile'>('desktop')
const saving = ref(false)
const products = ref<any[]>([])
const articles = ref<any[]>([])
const catalog = BLOCK_CATALOG
const trustText = ref('')
const certText = ref('')
const logoText = ref('')
const insertPointer = ref<number | null>(null)

const current = computed(() => page.value?.blocks?.[selected.value] || null)

onMounted(async () => {
  const res: any = await http.get('/admin/pages/' + route.params.id)
  page.value = res.data
  page.value.blocks = (page.value.blocks || []).map(normalize)
  const p: any = await http.get('/admin/products')
  products.value = (p.data?.list || []).filter((x: any) => x.featured)
  const a: any = await http.get('/admin/articles', { params: { siteId: page.value.siteId } })
  articles.value = (a.data || []).slice(0, 3)
  syncSideTexts()
})

function normalize(block: any) {
  const base = createBlock(block.type)
  return { type: block.type, props: { ...base.props, ...(block.props || {}) } }
}
function labelOf(type: string) {
  return catalog.find((b) => b.type === type)?.label || type
}
function insert(type: BlockType) {
  const index = insertPointer.value != null
    ? insertPointer.value
    : (page.value.blocks.length ? selected.value + 1 : 0)
  insertAt(index, type)
  insertPointer.value = null
}
function insertAt(index: number, type?: BlockType) {
  const block = createBlock(type || 'richText')
  page.value.blocks.splice(index, 0, block)
  selected.value = index
  syncSideTexts()
}
watch(selected, syncSideTexts)
function move(idx: number, dir: number) {
  const to = idx + dir
  if (to < 0 || to >= page.value.blocks.length) return
  const arr = page.value.blocks
  ;[arr[idx], arr[to]] = [arr[to], arr[idx]]
  selected.value = to
}
function duplicate(idx: number) {
  const copy = JSON.parse(JSON.stringify(page.value.blocks[idx]))
  page.value.blocks.splice(idx + 1, 0, copy)
  selected.value = idx + 1
}
function remove(idx: number) {
  page.value.blocks.splice(idx, 1)
  selected.value = Math.max(0, idx - 1)
}
function syncTrust() {
  current.value.props.items = trustText.value.split('\n').map((s) => s.trim()).filter(Boolean)
}
function syncCert() {
  current.value.props.items = certText.value.split('\n').map((s) => s.trim()).filter(Boolean)
}
function syncLogo() {
  current.value.props.items = logoText.value.split('\n').map((s) => s.trim()).filter(Boolean)
}
function ensureCols(block: any) {
  if (!block.props) block.props = {}
  if (!Array.isArray(block.props.columns) || block.props.columns.length < 3) {
    block.props.columns = ['Model', 'Spec A', 'Spec B']
  }
  return block.props.columns
}
function syncSideTexts() {
  if (current.value?.type === 'trustBar') trustText.value = (current.value.props.items || []).join('\n')
  if (current.value?.type === 'certificates') certText.value = (current.value.props.items || []).join('\n')
  if (current.value?.type === 'logoWall') logoText.value = (current.value.props.items || []).join('\n')
  if (current.value?.type === 'specTable' && !Array.isArray(current.value.props.columns)) {
    current.value.props.columns = ['Model', 'Spec A', 'Spec B']
  }
}
async function save() {
  saving.value = true
  try {
    await http.put('/admin/pages/' + page.value.id, page.value)
    ElMessage.success(page.value.status === 'live' ? '已发布，独立站刷新即可看到' : '已保存')
  } finally {
    saving.value = false
  }
}
function previewSite() {
  const code = localStorage.getItem('th_site_code') || ''
  const loc = localStorage.getItem('th_locale') || 'en'
  window.open(storePreview('/' + loc, code), '_blank')
}
</script>

<style scoped>
.builder { height: 100%; display: flex; flex-direction: column; background: #eef1f5; }
.builder-bar {
  height: 56px; background: #fff; border-bottom: 1px solid #e5eaf0;
  display: flex; align-items: center; justify-content: space-between; padding: 0 16px; gap: 12px;
}
.builder-bar-left, .builder-bar-right { display: flex; align-items: center; gap: 8px; }
.builder-bar-left strong { color: #0a2540; }
.device-switch { display: flex; background: #f3f5f8; padding: 3px; border-radius: 8px; }
.device-switch button { border: 0; background: transparent; padding: 6px 14px; cursor: pointer; color: #66788a; border-radius: 6px; }
.device-switch button.on { background: #fff; color: #0a2540; font-weight: 600; box-shadow: 0 1px 2px rgba(10,37,64,.08); }
.builder-body { flex: 1; display: grid; grid-template-columns: 228px 1fr 300px; min-height: 0; }
.palette, .inspector { background: #f8fafb; overflow: auto; padding: 16px; border-right: 1px solid #e5eaf0; }
.inspector { border-right: 0; border-left: 1px solid #e5eaf0; background: #fff; }
.palette h4, .inspector h4 { margin: 0 0 8px; color: #0a2540; font-size: 13px; letter-spacing: 0.04em; }
.hint { color: #7a8694; font-size: 12px; line-height: 1.5; margin: 0 0 12px; }
.palette-item {
  width: 100%; text-align: left; border: 1px solid #e5eaf0; background: #fff;
  padding: 10px 12px; margin-bottom: 8px; cursor: pointer; border-radius: 8px;
}
.palette-item:hover { border-color: #0d9488; }
.palette-item b { display: block; font-size: 13px; color: #0a2540; }
.palette-item span { font-size: 12px; color: #7a8694; }
.stage { overflow: auto; padding: 20px; background: #eef1f5; }
.stage-frame { margin: 0 auto; background: #fff; min-height: 70vh; border: 1px solid #dce3ea; border-radius: 8px; overflow: hidden; }
.stage-frame.desktop { width: min(1080px, 100%); }
.stage-frame.tablet { width: 768px; }
.stage-frame.mobile { width: 390px; }
.drop {
  text-align: center; color: #8b97a6; font-size: 12px; padding: 8px;
  border: 1px dashed #c9d3de; margin: 8px 12px; cursor: pointer; border-radius: 6px;
}
.drop.on { border-color: #0d9488; color: #0d9488; background: #f0fdfa; }
.canvas-block { position: relative; }
.canvas-block.selected { outline: 2px solid #2563eb; outline-offset: -2px; }
.block-tag {
  position: absolute; top: 8px; left: 8px; z-index: 3;
  background: #2563eb; color: #fff; font-size: 12px; padding: 3px 8px; border-radius: 4px;
}
.block-toolbar {
  position: absolute; top: 8px; right: 8px; z-index: 3;
  background: #2563eb; color: #fff; display: flex; gap: 6px; padding: 4px 8px; font-size: 12px; border-radius: 4px;
}
.block-toolbar button { border: 0; background: transparent; color: #fff; cursor: pointer; }
.empty-canvas { padding: 80px 20px; text-align: center; color: #889; }
.insp-img { width: 100%; height: 90px; object-fit: cover; margin-top: 8px; border-radius: 8px; }
.mini-card { border: 1px solid #e6ebf0; padding: 8px; margin-bottom: 8px; border-radius: 8px; }
</style>

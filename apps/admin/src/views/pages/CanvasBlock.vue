<template>
  <div class="cv">
    <div v-if="block.type === 'hero'" class="cv-hero" :class="layout" :style="heroStyle">
      <div v-if="layout === 'split'" class="cv-hero-img" :style="{ backgroundImage: `url(${block.props.image})` }" />
      <div class="cv-hero-inner">
        <h2>{{ block.props.heading }}</h2>
        <p>{{ block.props.subtitle }}</p>
        <span class="cv-btn">{{ block.props.cta }}</span>
      </div>
    </div>
    <div v-else-if="block.type === 'trustBar'" class="cv-trust">
      <span v-for="item in block.props.items" :key="item">{{ item }}</span>
    </div>
    <div v-else-if="block.type === 'productGrid'" class="cv-pad">
      <h3>{{ block.props.heading }}</h3>
      <div class="cv-grid">
        <div v-for="p in products.slice(0, 3)" :key="p.id" class="cv-card">
          <img :src="p.coverUrl" alt="" />
          <b>{{ p.name }}</b>
        </div>
        <div v-if="!products.length" class="cv-card"><b>精选产品位</b></div>
      </div>
    </div>
    <div v-else-if="block.type === 'specTable'" class="cv-pad">
      <h3>{{ block.props.heading }}</h3>
      <table>
        <thead><tr><th v-for="c in specCols" :key="c">{{ c }}</th></tr></thead>
        <tbody>
          <tr v-for="(r, i) in block.props.rows" :key="i"><td>{{ r.model }}</td><td>{{ r.flow }}</td><td>{{ r.hoses }}</td></tr>
        </tbody>
      </table>
    </div>
    <div v-else-if="block.type === 'solutions'" class="cv-pad cv-muted">
      <h3>{{ block.props.heading }}</h3>
      <div class="cv-grid">
        <div v-for="s in block.props.items" :key="s.slug" class="cv-card"><b>{{ s.title }}</b><p>{{ s.text }}</p></div>
      </div>
    </div>
    <div v-else-if="block.type === 'factory'" class="cv-factory">
      <div>
        <h3>{{ block.props.heading }}</h3>
        <p>{{ block.props.text }}</p>
      </div>
      <img :src="block.props.image" alt="" />
    </div>
    <div v-else-if="block.type === 'certificates'" class="cv-pad">
      <h3>{{ block.props.heading || 'Certificates' }}</h3>
      <div class="cv-certs"><span v-for="c in block.props.items" :key="c">{{ c }}</span></div>
    </div>
    <div v-else-if="block.type === 'faq'" class="cv-pad">
      <h3>{{ block.props.heading || 'FAQ' }}</h3>
      <p v-for="f in block.props.items" :key="f.q"><b>{{ f.q }}</b><br>{{ f.a }}</p>
    </div>
    <div v-else-if="block.type === 'cta'" class="cv-cta">
      <h3>{{ block.props.heading }}</h3>
      <span class="cv-btn">{{ block.props.cta }}</span>
    </div>
    <div v-else-if="block.type === 'richText'" class="cv-pad" v-html="block.props.html" />
    <div v-else-if="block.type === 'inquiryForm'" class="cv-pad">
      <h3>{{ block.props.title }}</h3>
      <div class="cv-form"><span /><span /><span class="cv-btn">Send inquiry</span></div>
    </div>
    <div v-else-if="block.type === 'blogTeaser'" class="cv-pad">
      <h3>{{ block.props.heading }}</h3>
      <div class="cv-grid">
        <div v-for="a in articles" :key="a.id" class="cv-card"><b>{{ a.title }}</b></div>
      </div>
    </div>
    <div v-else-if="block.type === 'testimonials'" class="cv-pad cv-muted">
      <h3>{{ block.props.heading }}</h3>
      <p v-for="t in block.props.items" :key="t.name"><b>{{ t.name }}</b> {{ t.quote }}</p>
    </div>
    <div v-else-if="block.type === 'logoWall'" class="cv-trust">
      <span v-for="item in block.props.items" :key="item">{{ item }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{ block: any; products: any[]; articles: any[] }>()
const layout = computed(() => props.block.props.layout || 'split')
const specCols = computed(() => {
  const cols = props.block.props?.columns
  return Array.isArray(cols) && cols.length ? cols : ['Model', 'Spec A', 'Spec B']
})
const heroStyle = computed(() => ({
  backgroundImage: layout.value === 'overlay' ? `url(${props.block.props.image})` : undefined
}))
</script>

<style scoped>
.cv-hero { min-height: 220px; color: #fff; background: #0b1f3a; display: grid; grid-template-columns: 1fr 1fr; overflow: hidden; }
.cv-hero.split { color: #fff; }
.cv-hero-img { min-height: 220px; background: #eef2f6 center/contain no-repeat; }
.cv-hero-inner { padding: 32px; width: 100%; }
.cv-hero.overlay { display: flex; align-items: center; grid-template-columns: 1fr; background: #0b1f3a center/cover no-repeat; }
.cv-hero.overlay .cv-hero-inner { background: none; color: #fff; }
.cv-hero.split .cv-hero-inner { background: #0b1f3a; color: #fff; }
.cv-hero h2 { margin: 0 0 8px; font-size: 26px; }
.cv-btn { display: inline-block; background: #e85d04; color: #fff; padding: 8px 12px; font-size: 13px; }
.cv-trust { display: grid; grid-template-columns: repeat(4, 1fr); text-align: center; padding: 12px 0; font-weight: 600; color: #0b1f3a; border-bottom: 1px solid #eee; }
.cv-pad { padding: 20px; }
.cv-pad h3 { margin: 0 0 12px; color: #0b1f3a; }
.cv-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
.cv-card { border: 1px solid #e6ebf0; padding: 8px; font-size: 12px; }
.cv-card img { width: 100%; height: 80px; object-fit: cover; }
.cv-muted { background: #f6f7f9; }
.cv-factory { display: grid; grid-template-columns: 1.1fr .9fr; gap: 12px; padding: 20px; align-items: center; }
.cv-factory img { width: 100%; height: 140px; object-fit: cover; }
.cv-certs span { display: inline-block; border: 1px solid #dce3ea; padding: 8px 12px; margin: 0 8px 8px 0; }
.cv-cta { background: #0b1f3a; color: #fff; padding: 28px 20px; }
.cv-cta h3 { margin: 0 0 10px; }
.cv-form span { display: block; height: 28px; background: #f3f5f8; margin-bottom: 8px; max-width: 280px; }
table { width: 100%; border-collapse: collapse; font-size: 12px; }
th, td { border-bottom: 1px solid #eee; padding: 6px; text-align: left; }
</style>

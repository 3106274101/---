<template>
  <div>
    <section v-for="(block, i) in blocks" :key="i">
      <div v-if="block.type === 'hero'" class="hero" :class="heroLayout(block)">
        <div class="hero-visual">
          <img :src="block.props.image" :alt="block.props.heading" />
        </div>
        <div class="hero-copy wrap">
          <h1>{{ block.props.heading }}</h1>
          <p>{{ block.props.subtitle }}</p>
          <NuxtLink :to="localePath(block.props.ctaTo || '/inquiry')" class="btn">{{ block.props.cta || $t('cta') }}</NuxtLink>
        </div>
      </div>

      <div v-else-if="block.type === 'trustBar'" class="trust">
        <div v-for="item in block.props.items" :key="item">
          {{ item }}
          <small v-if="trustHint(item)">{{ trustHint(item) }}</small>
        </div>
      </div>

      <div v-else-if="block.type === 'productGrid'" class="section" style="background:var(--bg)">
        <div class="wrap">
          <h2>{{ block.props.heading || $t('featured') }}</h2>
          <div class="grid-3">
            <NuxtLink v-for="p in products" :key="p.id" :to="localePath('/products/' + p.slug)" class="pcard">
              <img :src="p.coverUrl" :alt="p.name" width="280" height="180" />
              <div class="body">
                <h3>{{ p.name }}</h3>
                <p class="muted">{{ p.summary }}</p>
                <span class="btn">{{ $t('inquire') }}</span>
              </div>
            </NuxtLink>
          </div>
        </div>
      </div>

      <div v-else-if="block.type === 'specTable'" class="section">
        <div class="wrap">
          <h2>{{ block.props.heading || 'Model comparison' }}</h2>
          <table class="table">
            <thead><tr><th>Model</th><th>Flow</th><th>Hoses</th></tr></thead>
            <tbody>
              <tr v-for="r in block.props.rows" :key="r.model"><td>{{ r.model }}</td><td>{{ r.flow }}</td><td>{{ r.hoses }}</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else-if="block.type === 'solutions'" class="section">
        <div class="wrap">
          <h2>{{ block.props.heading || 'Solutions' }}</h2>
          <div class="grid-3">
            <NuxtLink v-for="s in block.props.items" :key="s.slug" :to="localePath('/solutions')" class="card">
              <div class="body">
                <h3>{{ s.title }}</h3>
                <p class="muted">{{ s.text }}</p>
              </div>
            </NuxtLink>
          </div>
        </div>
      </div>

      <div v-else-if="block.type === 'factory'" class="section">
        <div class="wrap factory-intro">
          <div>
            <h2>{{ block.props.heading }}</h2>
            <p>{{ block.props.text }}</p>
            <NuxtLink :to="localePath('/factory')" class="btn ghost">{{ $t('nav.factory') }}</NuxtLink>
          </div>
          <img :src="block.props.image" :alt="block.props.heading" width="560" height="320" />
        </div>
        <div class="strengths">
          <div v-for="tile in factoryTiles(block)" :key="tile.title" class="strength" :style="{ backgroundImage: `url(${tile.image})` }">
            <div class="cap">
              <h3>{{ tile.title }}</h3>
              <p>{{ tile.text }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="block.type === 'certificates'" class="section">
        <div class="wrap">
          <h2>{{ block.props.heading || 'Certificates' }}</h2>
          <div class="certs">
            <span v-for="c in block.props.items" :key="c">{{ c }}</span>
          </div>
        </div>
      </div>

      <div v-else-if="block.type === 'faq'" class="section">
        <div class="wrap">
          <h2>{{ block.props.heading || 'FAQ' }}</h2>
          <details v-for="f in block.props.items" :key="f.q">
            <summary><b>{{ f.q }}</b></summary>
            <p>{{ f.a }}</p>
          </details>
        </div>
      </div>

      <div v-else-if="block.type === 'cta'" class="cta-band">
        <div class="wrap">
          <h2>{{ block.props.heading }}</h2>
          <NuxtLink :to="localePath(block.props.ctaTo || '/inquiry')" class="btn">{{ block.props.cta || $t('cta') }}</NuxtLink>
        </div>
      </div>

      <div v-else-if="block.type === 'richText'" class="section">
        <div class="wrap prose" v-html="block.props.html" />
      </div>

      <div v-else-if="block.type === 'inquiryForm'" class="section">
        <div class="wrap">
          <h2>{{ block.props.title || $t('cta') }}</h2>
          <InquiryForm />
        </div>
      </div>

      <div v-else-if="block.type === 'blogTeaser'" class="section" style="background:var(--bg)">
        <div class="wrap">
          <h2>{{ block.props.heading || $t('related') }}</h2>
          <div class="grid-3">
            <NuxtLink v-for="a in articles" :key="a.id" :to="localePath('/blog/' + a.slug)" class="card">
              <img v-if="a.coverUrl" :src="a.coverUrl" :alt="a.title" width="400" height="180" />
              <div class="body">
                <h3>{{ a.title }}</h3>
                <p class="muted">{{ a.summary }}</p>
              </div>
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ blocks: any[]; products?: any[]; articles?: any[] }>()
const localePath = useLocalePath()
const products = computed(() => props.products || [])
const articles = computed(() => props.articles || [])

function heroLayout(block: any) {
  return block.props?.layout || 'split'
}
function trustHint(item: string) {
  const text = String(item).toLowerCase()
  if (text.includes('ce')) return 'European standards'
  if (text.includes('iso')) return 'Quality system certified'
  if (text.includes('countr') || text.includes('全球')) return 'Export markets'
  if (text.includes('year') || text.includes('年')) return 'Petroleum equipment'
  if (text.includes('nozzle') || text.includes('枪')) return 'Flexible layout'
  if (text.includes('gprs')) return 'Remote monitoring'
  if (text.includes('110') || text.includes('380') || text.includes('volt')) return 'Station voltage'
  return ''
}
function factoryTiles(block: any) {
  if (Array.isArray(block.props?.tiles) && block.props.tiles.length) return block.props.tiles
  const img = block.props?.image
  return [
    { title: 'Modern manufacturing', text: 'CNC, painting and explosion-proof assembly.', image: img },
    { title: 'Rigorous testing', text: 'Meter calibration and 72-hour aging.', image: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=800&q=80' },
    { title: 'Global export', text: 'Packed for 80+ destination markets.', image: 'https://images.unsplash.com/photo-1578575437130-527eed3abbec?auto=format&fit=crop&w=800&q=80' },
    { title: 'Secure packaging', text: 'Container photos with every shipment.', image: 'https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80' }
  ]
}
</script>

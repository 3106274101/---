<template>
  <div class="wrap" v-if="p">
    <div class="crumbs">
      <NuxtLink :to="localePath('/')">{{ $t('nav.home') }}</NuxtLink> /
      <NuxtLink :to="localePath('/products')">{{ $t('nav.products') }}</NuxtLink> /
      {{ p.name }}
    </div>
    <div class="grid-3" style="grid-template-columns:1.15fr 0.85fr;align-items:start;gap:28px">
      <div class="gallery">
        <img class="gallery-main" :src="currentImg" :alt="p.name" width="720" height="420" />
        <div class="thumbs" v-if="images.length > 1">
          <img v-for="img in images" :key="img" :src="img" :class="{ on: img === currentImg }" :alt="p.name" @click="currentImg = img" />
        </div>
      </div>
      <aside class="sticky-box">
        <p class="muted">{{ p.model }}</p>
        <h1 style="margin-top:0">{{ p.name }}</h1>
        <p>{{ p.summary }}</p>
        <p><NuxtLink :to="inquiryHref" class="btn">{{ $t('cta') }}</NuxtLink>
          <a v-if="waHref" class="btn ghost" :href="waHref" target="_blank" rel="noopener">WhatsApp</a>
        </p>
        <p class="muted">{{ $t('replyHint') }}</p>
      </aside>
    </div>
    <div class="section">
      <h2>{{ $t('specs') }}</h2>
      <table class="table">
        <tr v-for="(v, k) in p.attrs" :key="k"><td style="width:240px">{{ specLabel(String(k)) }}</td><td>{{ v }}</td></tr>
      </table>
    </div>
    <div class="prose section" v-html="p.content" />
    <div class="section">
      <h2>{{ $t('cta') }}</h2>
      <InquiryForm :product-id="p.id" :product-name="p.name" />
    </div>
    <div v-if="(p.related || []).length" class="section">
      <h2>{{ $t('relatedProducts') }}</h2>
      <div class="grid-4">
        <NuxtLink v-for="r in p.related" :key="r.id" :to="localePath('/products/' + r.slug)" class="pcard">
          <img v-if="r.coverUrl" :src="r.coverUrl" :alt="r.name" width="280" height="160" />
          <div class="body">
            <h3 style="font-size:16px;margin:0 0 6px">{{ r.name }}</h3>
            <p class="muted">{{ r.model }}</p>
          </div>
        </NuxtLink>
      </div>
    </div>
    <div class="sticky-quote">
      <span>{{ p.model }} · {{ p.name }}</span>
      <NuxtLink :to="inquiryHref" class="btn">{{ $t('cta') }}</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const { get, config, siteCode } = useStoreApi()
const slug = String(route.params.slug)
const { data } = await useAsyncData('p-' + slug, () => get('/products/' + slug))
const { data: ctx } = await useAsyncData('ctx-' + siteCode(), () => get('/context'))
const p = computed(() => data.value)
if (!p.value) {
  throw createError({ statusCode: 404, statusMessage: 'Product not found' })
}
const inquiryHref = computed(() => localePath('/inquiry') + '?product=' + encodeURIComponent(p.value.name || '') + '&productId=' + p.value.id)
const waHref = computed(() => {
  const raw = String(ctx.value?.brand?.whatsapp || '').replace(/\D/g, '')
  if (!raw) return ''
  return `https://wa.me/${raw}?text=${encodeURIComponent('Hi, I want a quote for ' + (p.value.name || ''))}`
})
const images = computed(() => {
  const list = [p.value.coverUrl, ...(p.value.gallery || [])].filter(Boolean)
  return [...new Set(list)]
})
const currentImg = ref(images.value[0])
const labels: Record<string, string> = {
  flow_rate: 'Flow rate',
  accuracy: 'Accuracy',
  hose_count: 'Hoses / nozzles',
  product_types: 'Fuel types',
  explosion_proof: 'Explosion-proof',
  voltage: 'Voltage',
  display: 'Display',
  mounting: 'Mounting',
  communication: 'Protocol',
  certification: 'Certifications'
}
function specLabel(key: string) {
  return labels[key] || key.replace(/_/g, ' ')
}
const extra = Object.entries(p.value.attrs || {}).map(([name, value]) => ({
  '@type': 'PropertyValue', name, value
}))
usePageSeo({
  title: p.value.seoTitle || p.value.name,
  description: p.value.seoDescription || p.value.summary,
  path: '/products/' + slug,
  image: p.value.coverUrl,
  type: 'product',
  jsonLd: {
    '@context': 'https://schema.org',
    '@type': 'Product',
    name: p.value.name,
    description: p.value.summary,
    image: p.value.coverUrl,
    sku: p.value.model,
    brand: { '@type': 'Brand', name: 'ZhengHe' },
    additionalProperty: extra,
    offers: {
      '@type': 'Offer',
      availability: 'https://schema.org/InStock',
      url: `${config.public.siteUrl}/en/products/${slug}`,
      description: 'Request quotation'
    }
  }
})
</script>

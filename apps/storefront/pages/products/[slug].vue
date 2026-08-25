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
        <p><NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('cta') }}</NuxtLink></p>
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
      <InquiryForm :product-id="p.id" />
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const { get, config } = useStoreApi()
const slug = String(route.params.slug)
const { data } = await useAsyncData('p-' + slug, () => get('/products/' + slug))
const p = computed(() => data.value)
if (!p.value) {
  throw createError({ statusCode: 404, statusMessage: 'Product not found' })
}
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
    brand: { '@type': 'Brand', name: 'FuelTech' },
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

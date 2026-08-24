<template>
  <div class="wrap" v-if="p">
    <div class="crumbs">
      <NuxtLink :to="localePath('/')">Home</NuxtLink> /
      <NuxtLink :to="localePath('/products')">Products</NuxtLink> /
      {{ p.name }}
    </div>
    <div class="grid-3" style="grid-template-columns:1.1fr 0.9fr;align-items:start">
      <img :src="p.coverUrl" :alt="p.name" width="720" height="420" />
      <div>
        <h1>{{ p.name }}</h1>
        <p>{{ p.summary }}</p>
        <p><NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('cta') }}</NuxtLink></p>
      </div>
    </div>
    <div class="section">
      <h2>{{ $t('specs') }}</h2>
      <table class="table">
        <tr v-for="(v, k) in p.attrs" :key="k"><td style="width:240px">{{ k }}</td><td>{{ v }}</td></tr>
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

<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <div class="crumbs">
      <NuxtLink :to="localePath('/solutions')">{{ $t('nav.solutions') }}</NuxtLink> / {{ title }}
    </div>
    <h1>{{ title }}</h1>
    <p>{{ text }}</p>
    <p><NuxtLink :to="localePath('/inquiry') + '?product=' + encodeURIComponent(title)" class="btn">{{ $t('cta') }}</NuxtLink></p>
  </div>
</template>
<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const { get } = useStoreApi()
const slug = String(route.params.slug)
const { data: page } = await useAsyncData('sol-' + slug, async () => {
  try {
    return await get('/pages/' + slug)
  } catch {
    return null
  }
})
const catalog: Record<string, { title: string; text: string }> = {
  'gas-station': { title: 'Petrol stations', text: 'Island dispensers with POS protocols, 2–8 nozzles, 110/220/380V.' },
  fleet: { title: 'Fleet & mining', text: 'High-flow and skid units for depots and camps.' },
  marine: { title: 'Marine & remote', text: 'Mobile dispensers for docks and islands.' }
}
const fallback = catalog[slug] || { title: slug.replace(/-/g, ' '), text: 'Tell us voltage, hose count and destination port for this application.' }
const title = computed(() => page.value?.title || fallback.title)
const text = computed(() => page.value?.seoDescription || fallback.text)
usePageSeo({
  title: (page.value?.seoTitle || title.value) + ' | ZhengHe Machinery',
  description: text.value,
  path: '/solutions/' + slug
})
</script>

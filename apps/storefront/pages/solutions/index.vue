<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>Solutions</h1>
    <p>Fueling systems for petrol stations, fleets, mining depots and remote sites.</p>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('solutions', () => get('/pages/solutions'))
usePageSeo({
  title: page.value?.seoTitle || 'Fueling Solutions | ZhengHe Machinery',
  description: page.value?.seoDescription || 'Petrol station, fleet and marine fuel dispenser solutions.',
  path: '/solutions'
})
</script>

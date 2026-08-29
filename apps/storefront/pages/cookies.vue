<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>Cookie Policy</h1>
    <p>Essential cookies keep the site working. Analytics cookies load only after consent in production deployments.</p>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { pageTitle } = await useSiteBrand()
const { data: page } = await useAsyncData('cookies', async () => {
  try { return await get('/pages/cookies') } catch { return null }
})
usePageSeo({
  title: page.value?.seoTitle || pageTitle('Cookie Policy'),
  description: page.value?.seoDescription || 'Cookie usage on this website.',
  path: '/cookies'
})
</script>

<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>Cases</h1>
    <p class="muted">{{ copy('catalogLead', 'productLead') }}</p>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { pageTitle, copy } = await useSiteBrand()
const { data: page } = await useAsyncData('projects', async () => {
  try { return await get('/pages/projects') } catch { return null }
})
usePageSeo({
  title: page.value?.seoTitle || pageTitle('Cases'),
  description: page.value?.seoDescription || copy('catalogLead', 'productLead'),
  path: '/projects'
})
</script>

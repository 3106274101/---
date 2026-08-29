<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>{{ $t('nav.solutions') }}</h1>
    <p>{{ copy('catalogLead', 'productLead') }}</p>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { pageTitle, copy } = await useSiteBrand()
const { data: page } = await useAsyncData('solutions', () => get('/pages/solutions'))
usePageSeo({
  title: page.value?.seoTitle || pageTitle('Solutions'),
  description: page.value?.seoDescription || copy('catalogLead', 'productLead'),
  path: '/solutions'
})
</script>

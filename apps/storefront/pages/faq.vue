<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('faq', () => get('/pages/faq'))
usePageSeo({ title: page.value?.seoTitle || 'FAQ | FuelTech', description: page.value?.seoDescription, path: '/faq' })
</script>

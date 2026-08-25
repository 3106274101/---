<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('about', () => get('/pages/about'))
usePageSeo({ title: page.value?.seoTitle || 'About ZhengHe Machinery', description: page.value?.seoDescription, path: '/about' })
</script>

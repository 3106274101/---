<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('factory', () => get('/pages/factory'))
usePageSeo({ title: page.value?.seoTitle || 'Factory | FuelTech', description: page.value?.seoDescription, path: '/factory' })
</script>

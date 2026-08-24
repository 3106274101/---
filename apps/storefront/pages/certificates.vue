<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('certs', () => get('/pages/certificates'))
usePageSeo({ title: page.value?.seoTitle || 'Certificates | FuelTech', description: page.value?.seoDescription, path: '/certificates' })
</script>

<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>Privacy Policy</h1>
    <p>Inquiry data is used only to reply to your request. You may email Cathy@machineryzh.com to access or delete your data.</p>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('privacy', async () => {
  try { return await get('/pages/privacy') } catch { return null }
})
usePageSeo({
  title: page.value?.seoTitle || 'Privacy Policy | ZhengHe Machinery',
  description: page.value?.seoDescription || 'How ZhengHe handles inquiry data.',
  path: '/privacy'
})
</script>

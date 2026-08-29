<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>Privacy Policy</h1>
    <p>Inquiry data is used only to reply to your request. Contact {{ brand.email || 'the site owner' }} to access or delete your data.</p>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { brand, pageTitle } = await useSiteBrand()
const { data: page } = await useAsyncData('privacy', async () => {
  try { return await get('/pages/privacy') } catch { return null }
})
usePageSeo({
  title: page.value?.seoTitle || pageTitle('Privacy Policy'),
  description: page.value?.seoDescription || 'How inquiry data is handled.',
  path: '/privacy'
})
</script>

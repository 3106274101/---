<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <div class="crumbs">
      <NuxtLink :to="localePath('/solutions')">{{ $t('nav.solutions') }}</NuxtLink> / {{ title }}
    </div>
    <h1>{{ title }}</h1>
    <p>{{ text }}</p>
    <p><NuxtLink :to="localePath('/inquiry') + '?product=' + encodeURIComponent(title)" class="btn">{{ $t('cta') }}</NuxtLink></p>
  </div>
</template>
<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const { get } = useStoreApi()
const { pageTitle, copy } = await useSiteBrand()
const slug = String(route.params.slug)
const { data: page } = await useAsyncData('sol-' + slug, async () => {
  try {
    return await get('/pages/' + slug)
  } catch {
    return null
  }
})
const title = computed(() => page.value?.title || slug.replace(/-/g, ' '))
const text = computed(() => page.value?.seoDescription || copy('inquiryLead', 'inquiryLead'))
usePageSeo({
  title: pageTitle(page.value?.seoTitle || title.value),
  description: text.value,
  path: '/solutions/' + slug
})
</script>

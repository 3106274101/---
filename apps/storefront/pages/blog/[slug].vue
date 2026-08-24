<template>
  <article class="wrap section" v-if="a">
    <div class="crumbs"><NuxtLink :to="localePath('/blog')">Blog</NuxtLink> / {{ a.title }}</div>
    <h1>{{ a.title }}</h1>
    <p class="muted">{{ a.summary }}</p>
    <div class="prose" v-html="a.content" />
  </article>
</template>
<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const { get } = useStoreApi()
const slug = String(route.params.slug)
const { data } = await useAsyncData('a-' + slug, () => get('/articles/' + slug))
const a = computed(() => data.value)
if (!a.value) throw createError({ statusCode: 404, statusMessage: 'Article not found' })
usePageSeo({
  title: a.value.seoTitle || a.value.title,
  description: a.value.seoDescription || a.value.summary,
  path: '/blog/' + slug,
  image: a.value.coverUrl,
  type: 'article',
  jsonLd: {
    '@context': 'https://schema.org',
    '@type': 'Article',
    headline: a.value.title,
    description: a.value.summary,
    image: a.value.coverUrl,
    datePublished: a.value.publishedAt
  }
})
</script>

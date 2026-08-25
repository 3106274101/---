<template>
  <div class="wrap section">
    <h1>Blog</h1>
    <div class="grid-3">
      <NuxtLink v-for="a in articles" :key="a.id" :to="localePath('/blog/' + a.slug)" class="card">
        <img :src="a.coverUrl" :alt="a.title" width="400" height="180" />
        <div class="body"><h2 style="font-size:18px">{{ a.title }}</h2><p class="muted">{{ a.summary }}</p></div>
      </NuxtLink>
    </div>
  </div>
</template>
<script setup lang="ts">
const localePath = useLocalePath()
const { get } = useStoreApi()
const { data } = await useAsyncData('blog', () => get('/articles'))
const articles = computed(() => data.value || [])
usePageSeo({ title: 'Fuel Dispenser Guides | ZhengHe Machinery', description: 'Nozzle count, mini stations and GPRS notes from ZhengHe.', path: '/blog' })
</script>

<template>
  <div class="wrap section">
    <h1>Search</h1>
    <form @submit.prevent="go"><input v-model="q" name="q" /><button class="btn" type="submit">Search</button></form>
    <div class="section">
      <div v-for="hit in hits" :key="hit.slug" class="card" style="margin-bottom:12px">
        <div class="body">
          <b>{{ hit.type }}</b>
          <h2 style="font-size:18px">{{ hit.name || hit.title }}</h2>
          <p class="muted">{{ hit.summary }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
const route = useRoute()
const q = ref(String(route.query.q || ''))
const { get } = useStoreApi()
const { data } = await useAsyncData('search-' + q.value, () => q.value ? get('/search?q=' + encodeURIComponent(q.value)) : Promise.resolve([]))
const hits = computed(() => data.value || [])
useHead({ meta: [{ name: 'robots', content: 'noindex,follow' }] })
usePageSeo({ title: 'Search | FuelTech', description: 'Search products and articles.', path: '/search' })
function go() {
  navigateTo({ path: route.path, query: { q: q.value } })
}
</script>

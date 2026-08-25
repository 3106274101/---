<template>
  <div class="wrap section search-page">
    <h1>{{ $t('search') }}</h1>
    <form @submit.prevent="go">
      <input v-model="q" name="q" :placeholder="$t('search')" />
      <button class="btn" type="submit">{{ $t('go') }}</button>
    </form>
    <div class="section">
      <NuxtLink v-for="hit in hits" :key="hit.slug" class="card" style="margin-bottom:12px;display:block" :to="localePath(hit.type === 'article' ? '/blog/' + hit.slug : '/products/' + hit.slug)">
        <div class="body">
          <b>{{ hit.type }}</b>
          <h2 style="font-size:18px">{{ hit.name || hit.title }}</h2>
          <p class="muted">{{ hit.summary }}</p>
        </div>
      </NuxtLink>
    </div>
  </div>
</template>
<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const q = ref(String(route.query.q || ''))
watch(() => route.query.q, (val) => { q.value = String(val || '') })
const { get } = useStoreApi()
const { data } = await useAsyncData(
  'search-hits',
  () => {
    const term = String(route.query.q || q.value || '')
    return term ? get('/search?q=' + encodeURIComponent(term)) : Promise.resolve([])
  },
  { watch: [() => route.query.q] }
)
const hits = computed(() => data.value || [])
useHead({ meta: [{ name: 'robots', content: 'noindex,follow' }] })
usePageSeo({ title: 'Search | FuelTech', description: 'Search products and articles.', path: '/search' })
function go() {
  q.value = String(q.value || '')
  navigateTo({ path: route.path, query: { q: q.value } })
}
</script>

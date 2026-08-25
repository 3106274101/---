<template>
  <div class="wrap">
    <div class="crumbs">
      <NuxtLink :to="localePath('/')">{{ $t('nav.home') }}</NuxtLink> / {{ $t('nav.products') }}
    </div>
    <h1>{{ $t('productTitle') }}</h1>
    <p class="muted">{{ $t('productLead') }}</p>
    <div class="chips">
      <button class="chip" :class="{ on: !category }" type="button" @click="goCat()">{{ $t('allModels') }}</button>
      <button v-for="c in cats" :key="c.id" class="chip" :class="{ on: category === c.slug }" type="button" @click="goCat(c.slug)">{{ c.name }}</button>
    </div>
    <div class="grid-3 section">
      <NuxtLink v-for="p in products" :key="p.id" :to="localePath('/products/' + p.slug)" class="pcard">
        <img :src="p.coverUrl" :alt="p.name" width="400" height="180" />
        <div class="body">
          <h2 style="font-size:18px;margin:0 0 6px">{{ p.name }}</h2>
          <p class="muted">{{ p.summary }}</p>
          <span class="btn">{{ $t('inquire') }}</span>
        </div>
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const localePath = useLocalePath()
const { get } = useStoreApi()
const category = computed(() => String(route.query.category || ''))
const { data: catData } = await useAsyncData('categories', () => get('/categories'))
const { data } = await useAsyncData(
  'products-list',
  () => get('/products' + (category.value ? '?category=' + category.value : '')),
  { watch: [category] }
)
const cats = computed(() => catData.value || [])
const products = computed(() => data.value || [])
usePageSeo({
  title: 'Fuel Dispensers | FuelTech',
  description: 'Browse fuel dispensers, mobile skid units, automatic nozzles and flow meters. OEM available.',
  path: '/products'
})
function goCat(slug?: string) {
  navigateTo({ path: localePath('/products'), query: slug ? { category: slug } : {} })
}
</script>

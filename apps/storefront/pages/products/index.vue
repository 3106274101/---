<template>
  <div class="wrap">
    <div class="crumbs">
      <NuxtLink :to="localePath('/')">Home</NuxtLink> / Products
    </div>
    <h1>Fuel dispensers & accessories</h1>
    <p class="muted">CE-certified island dispensers, mobile skids, nozzles and meters from an OEM factory.</p>
    <div class="grid-3 section">
      <NuxtLink v-for="p in products" :key="p.id" :to="localePath('/products/' + p.slug)" class="card">
        <img :src="p.coverUrl" :alt="p.name" width="400" height="180" />
        <div class="body">
          <h2 style="font-size:18px">{{ p.name }}</h2>
          <p class="muted">{{ p.summary }}</p>
        </div>
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const { get } = useStoreApi()
const { data } = await useAsyncData('products', () => get('/products'))
const products = computed(() => data.value || [])
usePageSeo({
  title: 'Fuel Dispensers | FuelTech',
  description: 'Browse fuel dispensers, mobile skid units, automatic nozzles and flow meters. OEM available.',
  path: '/products'
})
</script>

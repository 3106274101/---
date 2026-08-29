<template>
  <BlockRenderer v-if="page?.blocks" :blocks="page.blocks" />
  <div v-else class="wrap section">
    <h1>Cases</h1>
    <p class="muted">ZhengHe equipment is supplied for petrol stations, mining fuel sites and compact yards. Typical packages match the series on this site.</p>
    <div class="grid-3 section">
      <article class="card">
        <div class="body">
          <h2 style="font-size:18px">Petrol stations</h2>
          <p class="muted">Intelligent, Elite, Aurora and Brilliance island dispensers, 2–8 nozzles, 110/220/380V, optional GPRS and OEM logo.</p>
        </div>
      </article>
      <article class="card">
        <div class="body">
          <h2 style="font-size:18px">Mining &amp; fleet yards</h2>
          <p class="muted">Honesty compact 1–2 nozzle pumps and Prestige-V 3000L–6000L mini stations for camps that cannot build a full island.</p>
        </div>
      </article>
      <article class="card">
        <div class="body">
          <h2 style="font-size:18px">Export packages</h2>
          <p class="muted">OEM/ODM tanks, ATG, solar and GPRS for markets such as Pakistan. Contact Cathy@machineryzh.com for loading photos of your order.</p>
        </div>
      </article>
    </div>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data: page } = await useAsyncData('projects', async () => {
  try { return await get('/pages/projects') } catch { return null }
})
usePageSeo({
  title: page.value?.seoTitle || 'Cases | ZhengHe Machinery',
  description: page.value?.seoDescription || 'ZhengHe fuel dispensers for stations, mines and export mini stations.',
  path: '/projects'
})
</script>

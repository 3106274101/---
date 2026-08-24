<template>
  <div class="wrap section">
    <h1>Solutions</h1>
    <p>Fueling systems for petrol stations, fleets, mining depots and remote sites.</p>
    <div class="grid-3">
      <div class="card" v-for="s in items" :key="s.slug"><div class="body"><h2 style="font-size:18px">{{ s.title }}</h2><p class="muted">{{ s.text }}</p></div></div>
    </div>
  </div>
</template>
<script setup lang="ts">
const items = [
  { slug: 'gas-station', title: 'Petrol stations', text: 'Island dispensers, POS protocols, 2-8 hoses.' },
  { slug: 'fleet', title: 'Fleet & mining', text: 'High-flow and skid-mounted fueling.' },
  { slug: 'marine', title: 'Marine & remote', text: 'Mobile units for docks and islands.' }
]
usePageSeo({ title: 'Fueling Solutions | FuelTech', description: 'Petrol station, fleet and marine fuel dispenser solutions.', path: '/solutions' })
</script>

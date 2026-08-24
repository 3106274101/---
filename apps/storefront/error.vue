<template>
  <div class="wrap section">
    <h1>{{ error.statusCode }} </h1>
    <p>This page is not available. Browse products or send an inquiry.</p>
    <p><NuxtLink to="/en/products">Products</NuxtLink> · <NuxtLink to="/en/contact">Contact</NuxtLink></p>
  </div>
</template>
<script setup lang="ts">
defineProps<{ error: { statusCode: number } }>()
useHead({ meta: [{ name: 'robots', content: 'noindex' }] })
</script>

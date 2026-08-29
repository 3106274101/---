<template>
  <div class="wrap section">
    <h1>{{ error.statusCode === 404 ? $t('notFound') : error.statusCode }}</h1>
    <p>{{ $t('notFoundHint') }}</p>
    <p>
      <NuxtLink :to="localePath('/products')">{{ $t('nav.products') }}</NuxtLink>
      ·
      <NuxtLink :to="localePath('/inquiry')">{{ $t('nav.inquiry') }}</NuxtLink>
      ·
      <NuxtLink :to="localePath('/')">{{ $t('nav.home') }}</NuxtLink>
    </p>
  </div>
</template>
<script setup lang="ts">
defineProps<{ error: { statusCode: number } }>()
const localePath = useLocalePath()
useHead({ meta: [{ name: 'robots', content: 'noindex' }] })
</script>

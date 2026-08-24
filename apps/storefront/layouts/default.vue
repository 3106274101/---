<template>
  <div>
    <header class="header">
      <div class="wrap header-inner">
        <NuxtLink :to="localePath('/')" class="logo">{{ brand.logoText || 'FuelTech' }}</NuxtLink>
        <nav class="nav">
          <NuxtLink :to="localePath('/')">{{ $t('nav.home') }}</NuxtLink>
          <NuxtLink :to="localePath('/products')">{{ $t('nav.products') }}</NuxtLink>
          <NuxtLink :to="localePath('/solutions')">{{ $t('nav.solutions') }}</NuxtLink>
          <NuxtLink :to="localePath('/about')">{{ $t('nav.about') }}</NuxtLink>
          <NuxtLink :to="localePath('/blog')">{{ $t('nav.blog') }}</NuxtLink>
          <NuxtLink :to="localePath('/contact')">{{ $t('nav.contact') }}</NuxtLink>
          <NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('nav.inquiry') }}</NuxtLink>
          <NuxtLink v-if="locale === 'en'" :to="switchLocalePath('zh')">中文</NuxtLink>
          <NuxtLink v-else :to="switchLocalePath('en')">EN</NuxtLink>
        </nav>
      </div>
    </header>
    <slot />
    <footer class="footer">
      <div class="wrap">
        <p><b>{{ brand.logoText || 'FuelTech' }}</b> · {{ brand.address }}</p>
        <p>{{ brand.email }} · {{ brand.phone }}</p>
        <p>
          <NuxtLink :to="localePath('/privacy')">{{ $t('footer.privacy') }}</NuxtLink>
          <NuxtLink :to="localePath('/cookies')">{{ $t('footer.cookies') }}</NuxtLink>
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
const { locale } = useI18n()
const switchLocalePath = useSwitchLocalePath()
const localePath = useLocalePath()
const { get } = useStoreApi()
const { data } = await useAsyncData('ctx', () => get('/context'))
const brand = computed(() => data.value?.brand || {})
</script>

<template>
  <div class="site-root" :data-theme="theme">
    <div class="topbar">
      <div class="wrap topbar-inner">
        <span>{{ brand.email }} · {{ brand.phone }}</span>
        <span>CE / ISO · {{ brand.countries || '80+' }} countries · Est. {{ brand.founded || '2009' }}</span>
      </div>
    </div>
    <header class="header">
      <div class="wrap header-inner">
        <NuxtLink :to="localePath('/')" class="logo">
          <span class="logo-mark" />
          <span>{{ logoLeft }}<em>{{ logoRight }}</em></span>
        </NuxtLink>
        <nav class="nav">
          <NuxtLink :to="localePath('/')">{{ $t('nav.home') }}</NuxtLink>
          <NuxtLink :to="localePath('/products')">{{ $t('nav.products') }}</NuxtLink>
          <NuxtLink :to="localePath('/solutions')">{{ $t('nav.solutions') }}</NuxtLink>
          <NuxtLink :to="localePath('/factory')">{{ $t('nav.factory') }}</NuxtLink>
          <NuxtLink :to="localePath('/about')">{{ $t('nav.about') }}</NuxtLink>
          <NuxtLink :to="localePath('/blog')">{{ $t('nav.blog') }}</NuxtLink>
          <NuxtLink :to="localePath('/contact')">{{ $t('nav.contact') }}</NuxtLink>
        </nav>
        <div class="header-tools">
          <form class="search-mini" @submit.prevent="goSearch">
            <input v-model="q" :placeholder="$t('search')" />
            <button type="submit">{{ $t('go') }}</button>
          </form>
          <NuxtLink v-if="locale === 'en'" :to="switchLocalePath('zh')" class="lang">中文</NuxtLink>
          <NuxtLink v-else :to="switchLocalePath('en')" class="lang">EN</NuxtLink>
          <NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('nav.inquiry') }}</NuxtLink>
          <button class="menu-btn" type="button" @click="open = true">Menu</button>
        </div>
      </div>
    </header>
    <div class="drawer" :class="{ open }" @click.self="open = false">
      <nav>
        <NuxtLink :to="localePath('/')" @click="open = false">{{ $t('nav.home') }}</NuxtLink>
        <NuxtLink :to="localePath('/products')" @click="open = false">{{ $t('nav.products') }}</NuxtLink>
        <NuxtLink :to="localePath('/solutions')" @click="open = false">{{ $t('nav.solutions') }}</NuxtLink>
        <NuxtLink :to="localePath('/factory')" @click="open = false">{{ $t('nav.factory') }}</NuxtLink>
        <NuxtLink :to="localePath('/about')" @click="open = false">{{ $t('nav.about') }}</NuxtLink>
        <NuxtLink :to="localePath('/blog')" @click="open = false">{{ $t('nav.blog') }}</NuxtLink>
        <NuxtLink :to="localePath('/contact')" @click="open = false">{{ $t('nav.contact') }}</NuxtLink>
        <NuxtLink :to="localePath('/inquiry')" class="btn" @click="open = false">{{ $t('nav.inquiry') }}</NuxtLink>
      </nav>
    </div>
    <slot />
    <footer class="footer">
      <div class="wrap">
        <div class="footer-grid">
          <div>
            <h4>{{ brand.logoText || 'ZhengHe' }}</h4>
            <p>{{ brand.tagline }}</p>
            <p>{{ brand.address }}</p>
            <p>{{ brand.email }}<br>{{ brand.phone }}</p>
          </div>
          <div>
            <h4>{{ $t('nav.products') }}</h4>
            <ul>
              <li><NuxtLink :to="localePath('/products')">{{ $t('allModels') }}</NuxtLink></li>
              <li><NuxtLink :to="localePath('/products') + '?category=fuel-dispensers'">Fuel dispensers</NuxtLink></li>
              <li><NuxtLink :to="localePath('/products') + '?category=mini-stations'">Mini gas stations</NuxtLink></li>
            </ul>
          </div>
          <div>
            <h4>{{ $t('company') }}</h4>
            <ul>
              <li><NuxtLink :to="localePath('/about')">{{ $t('nav.about') }}</NuxtLink></li>
              <li><NuxtLink :to="localePath('/factory')">{{ $t('nav.factory') }}</NuxtLink></li>
              <li><NuxtLink :to="localePath('/certificates')">{{ $t('nav.certs') }}</NuxtLink></li>
              <li><NuxtLink :to="localePath('/blog')">{{ $t('nav.blog') }}</NuxtLink></li>
            </ul>
          </div>
          <div>
            <h4>{{ $t('nav.contact') }}</h4>
            <ul>
              <li><NuxtLink :to="localePath('/inquiry')">{{ $t('nav.inquiry') }}</NuxtLink></li>
              <li><NuxtLink :to="localePath('/faq')">FAQ</NuxtLink></li>
              <li><NuxtLink :to="localePath('/privacy')">{{ $t('footer.privacy') }}</NuxtLink></li>
            </ul>
          </div>
        </div>
        <div class="footer-copy">
          <span>© {{ new Date().getFullYear() }} {{ brand.logoText || 'ZhengHe' }} Machinery</span>
          <NuxtLink :to="localePath('/privacy')">{{ $t('footer.privacy') }}</NuxtLink>
          <NuxtLink :to="localePath('/cookies')">{{ $t('footer.cookies') }}</NuxtLink>
        </div>
      </div>
    </footer>
    <a v-if="waLink" class="wa" :href="waLink" target="_blank" rel="noopener">WA</a>
    <div v-if="showSticky" class="sticky-cta">
      <span>{{ $t('stickyHint') }}</span>
      <NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('cta') }}</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const { locale } = useI18n()
const route = useRoute()
const switchLocalePath = useSwitchLocalePath()
const localePath = useLocalePath()
const { get, siteCode } = useStoreApi()
const { data } = await useAsyncData('ctx-' + siteCode(), () => get('/context'))
const brand = computed(() => data.value?.brand || {})
const theme = computed(() => {
  const raw = String(data.value?.site?.theme || 'industrial')
  return raw === 'industrial-fuel' ? 'industrial' : raw
})
const open = ref(false)
const q = ref('')
const logoText = computed(() => String(brand.value.logoText || 'ZhengHe'))
const splitAt = computed(() => Math.max(1, logoText.value.length - 2))
const logoLeft = computed(() => logoText.value.slice(0, splitAt.value))
const logoRight = computed(() => logoText.value.slice(splitAt.value))
const waLink = computed(() => {
  const raw = String(brand.value.whatsapp || '').replace(/\D/g, '')
  if (!raw) return ''
  return `https://wa.me/${raw}?text=${encodeURIComponent('Hi, I would like a quotation from ' + (brand.value.logoText || 'your factory'))}`
})
const showSticky = computed(() => {
  const path = String(route.path || '')
  return !path.includes('/inquiry') && !path.includes('/products/')
})

useHead({
  htmlAttrs: { 'data-theme': theme.value },
  style: [{
    innerHTML: `:root{--navy:${brand.value.primaryColor || '#0b1f3a'};--navy-2:${brand.value.primaryColor || '#0b1f3a'};--cta:${brand.value.accentColor || '#e85d04'};--cta-2:${brand.value.accentColor || '#c44d00'};--radius:${brand.value.radius || '4px'};}`
  }],
  script: ga4Scripts(brand.value.ga4Id)
})

function ga4Scripts(raw?: string) {
  const id = String(raw || '').trim()
  if (!/^G-[A-Z0-9]+$/i.test(id)) return []
  return [
    { src: `https://www.googletagmanager.com/gtag/js?id=${id}`, async: true },
    {
      innerHTML: `window.dataLayer=window.dataLayer||[];function gtag(){dataLayer.push(arguments);}gtag('js',new Date());gtag('config','${id}');`
    }
  ]
}

function goSearch() {
  navigateTo({ path: localePath('/search'), query: { q: q.value } })
}
</script>

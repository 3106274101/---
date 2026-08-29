<template>
  <div class="site-root" :data-theme="themeId">
    <div class="topbar">
      <div class="wrap topbar-inner">
        <span>{{ brand.email }} · {{ brand.phone }}</span>
        <span v-if="trustLine">{{ trustLine }}</span>
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
          <NuxtLink v-if="showNav('products')" :to="localePath('/products')">{{ $t('nav.products') }}</NuxtLink>
          <NuxtLink v-if="showNav('solutions')" :to="localePath('/solutions')">{{ $t('nav.solutions') }}</NuxtLink>
          <NuxtLink v-if="showNav('factory')" :to="localePath('/factory')">{{ $t('nav.factory') }}</NuxtLink>
          <NuxtLink v-if="showNav('about')" :to="localePath('/about')">{{ $t('nav.about') }}</NuxtLink>
          <NuxtLink v-if="showNav('blog')" :to="localePath('/blog')">{{ $t('nav.blog') }}</NuxtLink>
          <NuxtLink v-if="showNav('contact')" :to="localePath('/contact')">{{ $t('nav.contact') }}</NuxtLink>
        </nav>
        <div class="header-tools">
          <form class="search-mini" @submit.prevent="goSearch">
            <input v-model="q" :placeholder="$t('search')" />
            <button type="submit">{{ $t('go') }}</button>
          </form>
          <div class="lang-switch" v-if="langOptions.length > 1">
            <NuxtLink
              v-for="l in langOptions"
              :key="l.code"
              :to="switchLocalePath(l.code)"
              class="lang"
              :class="{ on: locale === l.code }"
            >{{ l.short }}</NuxtLink>
          </div>
          <NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('nav.inquiry') }}</NuxtLink>
          <button class="menu-btn" type="button" @click="open = true">Menu</button>
        </div>
      </div>
    </header>
    <div class="drawer" :class="{ open }" @click.self="open = false">
      <nav>
        <NuxtLink :to="localePath('/')" @click="open = false">{{ $t('nav.home') }}</NuxtLink>
        <NuxtLink v-if="showNav('products')" :to="localePath('/products')" @click="open = false">{{ $t('nav.products') }}</NuxtLink>
        <NuxtLink v-if="showNav('solutions')" :to="localePath('/solutions')" @click="open = false">{{ $t('nav.solutions') }}</NuxtLink>
        <NuxtLink v-if="showNav('factory')" :to="localePath('/factory')" @click="open = false">{{ $t('nav.factory') }}</NuxtLink>
        <NuxtLink v-if="showNav('about')" :to="localePath('/about')" @click="open = false">{{ $t('nav.about') }}</NuxtLink>
        <NuxtLink v-if="showNav('blog')" :to="localePath('/blog')" @click="open = false">{{ $t('nav.blog') }}</NuxtLink>
        <NuxtLink v-if="showNav('contact')" :to="localePath('/contact')" @click="open = false">{{ $t('nav.contact') }}</NuxtLink>
        <NuxtLink :to="localePath('/inquiry')" class="btn" @click="open = false">{{ $t('nav.inquiry') }}</NuxtLink>
        <div class="lang-switch" v-if="langOptions.length > 1">
          <NuxtLink
            v-for="l in langOptions"
            :key="'m-' + l.code"
            :to="switchLocalePath(l.code)"
            class="lang"
            :class="{ on: locale === l.code }"
            @click="open = false"
          >{{ l.short }}</NuxtLink>
        </div>
      </nav>
    </div>
    <slot />
    <footer class="footer">
      <div class="wrap">
        <div class="footer-grid">
          <div>
            <h4>{{ siteName }}</h4>
            <p>{{ brand.tagline }}</p>
            <p>{{ brand.address }}</p>
            <p>{{ brand.email }}<br>{{ brand.phone }}</p>
          </div>
          <div>
            <h4>{{ $t('nav.products') }}</h4>
            <ul>
              <li><NuxtLink :to="localePath('/products')">{{ $t('allModels') }}</NuxtLink></li>
              <li v-for="c in footerCats" :key="c.id">
                <NuxtLink :to="localePath('/products') + '?category=' + c.slug">{{ c.name }}</NuxtLink>
              </li>
            </ul>
          </div>
          <div>
            <h4>{{ $t('company') }}</h4>
            <ul>
              <li v-if="showNav('about')"><NuxtLink :to="localePath('/about')">{{ $t('nav.about') }}</NuxtLink></li>
              <li v-if="showNav('factory')"><NuxtLink :to="localePath('/factory')">{{ $t('nav.factory') }}</NuxtLink></li>
              <li><NuxtLink :to="localePath('/certificates')">{{ $t('nav.certs') }}</NuxtLink></li>
              <li v-if="showNav('blog')"><NuxtLink :to="localePath('/blog')">{{ $t('nav.blog') }}</NuxtLink></li>
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
          <span>© {{ new Date().getFullYear() }} {{ siteName }}<template v-if="brand.footerNote"> {{ brand.footerNote }}</template></span>
          <NuxtLink :to="localePath('/privacy')">{{ $t('footer.privacy') }}</NuxtLink>
          <NuxtLink :to="localePath('/cookies')">{{ $t('footer.cookies') }}</NuxtLink>
        </div>
      </div>
    </footer>
    <a v-if="waLink" class="wa" :href="waLink" target="_blank" rel="noopener">WA</a>
    <div v-if="showSticky" class="sticky-cta">
      <span>{{ copy('stickyHint', 'stickyHint') }}</span>
      <NuxtLink :to="localePath('/inquiry')" class="btn">{{ $t('cta') }}</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CONTENT_LOCALES, parseLocales } from '~/utils/locales'

const { locale } = useI18n()
const route = useRoute()
const switchLocalePath = useSwitchLocalePath()
const localePath = useLocalePath()
const { brand, site, siteName, categories, showNav, copy } = await useSiteBrand()
const langOptions = computed(() => {
  const enabled = parseLocales(site.value.locales)
  return CONTENT_LOCALES.filter((l) => enabled.includes(l.code))
})
const themeId = computed(() => {
  const raw = String(site.value.theme || 'industrial')
  return raw === 'industrial-fuel' ? 'industrial' : raw
})
const open = ref(false)
const q = ref('')
const logoText = computed(() => siteName.value)
const splitAt = computed(() => Math.max(1, logoText.value.length - 2))
const logoLeft = computed(() => logoText.value.slice(0, splitAt.value))
const logoRight = computed(() => logoText.value.slice(splitAt.value))
const trustLine = computed(() => {
  if (brand.value.trustLine) return String(brand.value.trustLine)
  const bits = [brand.value.countries, brand.value.founded ? `Est. ${brand.value.founded}` : ''].filter(Boolean)
  return bits.join(' · ')
})
const footerCats = computed(() => (categories.value || []).slice(0, 6))
const waLink = computed(() => {
  const raw = String(brand.value.whatsapp || '').replace(/\D/g, '')
  if (!raw) return ''
  return `https://wa.me/${raw}?text=${encodeURIComponent('Hi, I would like a quotation from ' + siteName.value)}`
})
const showSticky = computed(() => {
  const path = String(route.path || '')
  return !path.includes('/inquiry') && !path.includes('/products/')
})

useHead({
  htmlAttrs: { 'data-theme': themeId.value },
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

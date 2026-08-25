export default defineNuxtConfig({
  ssr: true,
  compatibilityDate: '2024-08-24',
  app: {
    head: {
      htmlAttrs: { lang: 'en' },
      charset: 'utf-8',
      viewport: 'width=device-width, initial-scale=1',
      link: [{ rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' }]
    }
  },
  css: ['~/assets/css/site.css'],
  modules: ['@nuxtjs/i18n'],
  i18n: {
    locales: [
      { code: 'en', iso: 'en', name: 'English' },
      { code: 'zh', iso: 'zh-CN', name: '中文' }
    ],
    defaultLocale: 'en',
    strategy: 'prefix',
    vueI18n: './i18n.config.ts'
  },
  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080',
      siteCode: process.env.NUXT_PUBLIC_SITE_CODE || 'fueltech',
      siteUrl: process.env.NUXT_PUBLIC_SITE_URL || process.env.RENDER_EXTERNAL_URL || 'http://localhost:3000'
    }
  },
  nitro: {
    preset: process.env.NITRO_PRESET || process.env.SERVER_PRESET || 'node-server',
    routeRules: {
      '/api/**': {
        proxy: `${(process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080').replace(/\/$/, '')}/api/**`
      }
    }
  }
})

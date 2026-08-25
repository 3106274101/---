<template>
  <div>
    <BlockRenderer v-if="home?.page?.blocks" :blocks="home.page.blocks" :products="home.featuredProducts" :articles="home.articles" />
  </div>
</template>

<script setup lang="ts">
const { get, config } = useStoreApi()
const { data: home } = await useAsyncData('home', () => get('/home'))
const brand = home.value?.brand || {}
const seo = home.value?.seo || {}
const faqs = (home.value?.page?.blocks || []).find((b: any) => b.type === 'faq')?.props?.items || []
usePageSeo({
  title: seo.title || 'Fuel Dispenser Manufacturer | ZhengHe Machinery',
  description: seo.description,
  path: '/',
  image: seo.ogImage || brand.heroImage,
  jsonLd: [
    {
      '@context': 'https://schema.org',
      '@type': 'Organization',
      name: brand.logoText || 'ZhengHe',
      url: config.public.siteUrl,
      email: brand.email,
      telephone: brand.phone,
      address: brand.address
    },
    {
      '@context': 'https://schema.org',
      '@type': 'WebSite',
      name: brand.logoText || 'ZhengHe',
      url: config.public.siteUrl,
      potentialAction: {
        '@type': 'SearchAction',
        target: `${config.public.siteUrl}/en/search?q={search_term_string}`,
        'query-input': 'required name=search_term_string'
      }
    },
    faqs.length ? {
      '@context': 'https://schema.org',
      '@type': 'FAQPage',
      mainEntity: faqs.map((f: any) => ({
        '@type': 'Question',
        name: f.q,
        acceptedAnswer: { '@type': 'Answer', text: f.a }
      }))
    } : null
  ].filter(Boolean)
})
</script>

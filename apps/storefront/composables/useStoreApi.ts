export function useStoreApi() {
  const config = useRuntimeConfig()
  const { locale } = useI18n()

  async function get<T = any>(path: string): Promise<T> {
    const res = await $fetch<any>(`${config.public.apiBase}/api/store${path}`, {
      headers: {
        'X-Site-Code': String(config.public.siteCode),
        'X-Locale': locale.value
      }
    })
    if (res && typeof res === 'object' && 'data' in res) {
      return res.data as T
    }
    return res as T
  }

  async function post<T = any>(path: string, body: any): Promise<T> {
    const res = await $fetch<any>(`${config.public.apiBase}/api/store${path}`, {
      method: 'POST',
      body,
      headers: {
        'X-Site-Code': String(config.public.siteCode),
        'X-Locale': locale.value
      }
    })
    return (res?.data ?? res) as T
  }

  return { get, post, config }
}

export function usePageSeo(opts: {
  title?: string
  description?: string
  path?: string
  image?: string
  type?: string
  jsonLd?: any
}) {
  const config = useRuntimeConfig()
  const { locale, locales } = useI18n()
  const siteUrl = String(config.public.siteUrl).replace(/\/$/, '')
  const path = opts.path || '/'
  const canonical = `${siteUrl}/${locale.value}${path === '/' ? '' : path}`
  const list = (locales.value as any[]).map((l) => {
    const code = typeof l === 'string' ? l : l.code
    return {
      rel: 'alternate',
      hreflang: code,
      href: `${siteUrl}/${code}${path === '/' ? '' : path}`
    }
  })
  list.push({ rel: 'alternate', hreflang: 'x-default', href: `${siteUrl}/en${path === '/' ? '' : path}` })
  useHead({
    title: opts.title,
    htmlAttrs: { lang: locale.value },
    meta: [
      { name: 'description', content: opts.description || '' },
      { property: 'og:title', content: opts.title || '' },
      { property: 'og:description', content: opts.description || '' },
      { property: 'og:type', content: opts.type || 'website' },
      { property: 'og:url', content: canonical },
      { property: 'og:image', content: opts.image || '' },
      { property: 'og:locale', content: locale.value === 'zh' ? 'zh_CN' : 'en_US' },
      { name: 'twitter:card', content: 'summary_large_image' }
    ],
    link: [{ rel: 'canonical', href: canonical }, ...list]
  })
  if (opts.jsonLd) {
    useHead({
      script: [{ type: 'application/ld+json', innerHTML: JSON.stringify(opts.jsonLd) }]
    })
  }
}

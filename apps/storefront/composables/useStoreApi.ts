export function useStoreApi() {
  const config = useRuntimeConfig()
  const { locale } = useI18n()
  const route = useRoute()

  function previewSiteCode() {
    const q = route.query.site
    return typeof q === 'string' && q ? q : ''
  }

  function visitorHost() {
    try {
      if (import.meta.server) {
        return useRequestURL().host
      }
    } catch {
      /* ignore */
    }
    if (typeof window !== 'undefined') {
      return window.location.host
    }
    return ''
  }

  function headers() {
    const h: Record<string, string> = {
      'X-Locale': locale.value,
      'X-Site-Host': visitorHost()
    }
    const code = previewSiteCode()
    if (code) h['X-Site-Code'] = code
    return h
  }

  function siteCode() {
    return previewSiteCode() || String(config.public.siteCode || '')
  }

  function apiOrigin() {
    const serverBase = (config as any).apiBase as string | undefined
    if (import.meta.server && serverBase) return String(serverBase).replace(/\/$/, '')
    return String(config.public.apiBase || '').replace(/\/$/, '')
  }

  function rewriteAssets<T>(data: T): T {
    const origin = apiOrigin()
    if (!origin) return data
    if (typeof data === 'string') {
      return data.replace(/https?:\/\/localhost:8080/g, origin) as T
    }
    if (Array.isArray(data)) {
      return data.map((item) => rewriteAssets(item)) as T
    }
    if (data && typeof data === 'object') {
      for (const key of Object.keys(data as object)) {
        ;(data as Record<string, unknown>)[key] = rewriteAssets((data as Record<string, unknown>)[key])
      }
    }
    return data
  }

  async function get<T = any>(path: string): Promise<T> {
    const res = await $fetch<any>(`${apiOrigin()}/api/store${path}`, {
      headers: headers()
    })
    if (res && typeof res === 'object' && 'data' in res) {
      return rewriteAssets(res.data as T)
    }
    return rewriteAssets(res as T)
  }

  async function post<T = any>(path: string, body: any): Promise<T> {
    const res = await $fetch<any>(`${apiOrigin()}/api/store${path}`, {
      method: 'POST',
      body,
      headers: headers()
    })
    return rewriteAssets((res?.data ?? res) as T)
  }

  return { get, post, config, siteCode }
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

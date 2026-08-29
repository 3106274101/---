export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  setHeader(event, 'content-type', 'text/plain; charset=utf-8')
  const fallback = `User-agent: *
Allow: /
Disallow: /api/
Disallow: /search
Sitemap: ${config.public.siteUrl}/sitemap.xml
`
  try {
    const host = getHeader(event, 'host') || ''
    const res = await $fetch<any>(`${config.public.apiBase}/api/store/context`, {
      headers: {
        'X-Site-Code': String(config.public.siteCode),
        'X-Site-Host': host,
        'X-Locale': 'en'
      }
    })
    const custom = res?.data?.seo?.robotsTxt
    if (custom && String(custom).trim()) return String(custom)
  } catch {
    /* use fallback */
  }
  return fallback
})

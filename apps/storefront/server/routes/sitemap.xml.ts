export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const res = await $fetch<any>(`${config.public.apiBase}/api/store/sitemap`, {
    headers: { 'X-Site-Code': String(config.public.siteCode), 'X-Locale': 'en' }
  })
  const data = res.data || {}
  const siteUrl = String(config.public.siteUrl).replace(/\/$/, '')
  const locales: string[] = data.locales || ['en']
  const urls: string[] = []
  for (const loc of locales) {
    urls.push(`${siteUrl}/${loc}`)
    for (const p of data.pages || []) {
      if (p !== 'home') urls.push(`${siteUrl}/${loc}/${p}`)
    }
    for (const p of data.products || []) urls.push(`${siteUrl}/${loc}/products/${p}`)
    for (const p of data.articles || []) urls.push(`${siteUrl}/${loc}/blog/${p}`)
    for (const p of data.categories || []) urls.push(`${siteUrl}/${loc}/products?category=${p}`)
  }
  const body = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${urls.map((u) => `  <url><loc>${u}</loc></url>`).join('\n')}
</urlset>`
  setHeader(event, 'content-type', 'application/xml; charset=utf-8')
  return body
})

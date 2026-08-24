export default defineEventHandler((event) => {
  const config = useRuntimeConfig()
  setHeader(event, 'content-type', 'text/plain; charset=utf-8')
  return `User-agent: *
Allow: /
Disallow: /api/
Disallow: /search
Sitemap: ${config.public.siteUrl}/sitemap.xml
`
})

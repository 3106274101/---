export default defineNuxtRouteMiddleware(async (to) => {
  if (to.path.includes('.') || to.path.startsWith('/api')) return
  const config = useRuntimeConfig()
  try {
    const headers: Record<string, string> = {}
    try {
      headers['X-Site-Host'] = import.meta.server ? useRequestURL().host : window.location.host
    } catch {
      /* ignore */
    }
    const site = typeof to.query.site === 'string' ? to.query.site : ''
    if (site) headers['X-Site-Code'] = site
    const res = await $fetch<any>(`${String(config.public.apiBase).replace(/\/$/, '')}/api/store/redirects`, { headers })
    const list = res?.data || []
    const stripped = to.path.replace(/^\/(en|zh)(?=\/|$)/, '') || '/'
    const hit = list.find((r: any) => {
      const from = String(r.fromPath || '').replace(/\/$/, '') || '/'
      return from === stripped || from === to.path || from === stripped.replace(/\/$/, '')
    })
    if (!hit) return
    const locale = to.path.match(/^\/(en|zh)/)?.[1] || 'en'
    let dest = String(hit.toPath || '/')
    if (dest.startsWith('http')) {
      return navigateTo(dest, { external: true, redirectCode: Number(hit.code) || 301 })
    }
    if (!dest.startsWith('/')) dest = '/' + dest
    if (!/^\/(en|zh)(\/|$)/.test(dest)) dest = '/' + locale + dest
    if (site) dest += (dest.includes('?') ? '&' : '?') + 'site=' + encodeURIComponent(site)
    return navigateTo(dest, { redirectCode: Number(hit.code) || 301 })
  } catch {
    /* keep original page if API is down */
  }
})

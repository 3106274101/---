const trim = (value: string) => value.replace(/\/$/, '')

/** Render 上填完整 API 根地址，如 https://tradehub-api-bxz2.onrender.com；本机留空走 Vite 代理。 */
export const apiOrigin = trim(import.meta.env.VITE_API_BASE || '')
export const apiBase = apiOrigin ? `${apiOrigin}/api` : '/api'
export const storefrontUrl = trim(import.meta.env.VITE_STOREFRONT_URL || 'http://localhost:3000')

export function storePreview(path = '/en', siteCode?: string) {
  const normalized = path.startsWith('/') ? path : `/${path}`
  const url = new URL(normalized, storefrontUrl)
  if (siteCode) url.searchParams.set('site', siteCode)
  return url.toString()
}

export function rewriteAssetUrls<T>(data: T): T {
  if (!apiOrigin) return data
  if (typeof data === 'string') {
    return data.replace(/https?:\/\/localhost:8080/g, apiOrigin) as T
  }
  if (Array.isArray(data)) {
    return data.map((item) => rewriteAssetUrls(item)) as T
  }
  if (data && typeof data === 'object') {
    for (const key of Object.keys(data as object)) {
      ;(data as Record<string, unknown>)[key] = rewriteAssetUrls((data as Record<string, unknown>)[key])
    }
  }
  return data
}

export async function useSiteBrand() {
  const { get, siteCode } = useStoreApi()
  const { t, locale } = useI18n()
  const { data } = await useAsyncData('ctx-' + siteCode(), () => get('/context'))
  const brand = computed(() => data.value?.brand || {})
  const site = computed(() => data.value?.site || {})
  const seo = computed(() => data.value?.seo || {})
  const categories = computed(() => data.value?.categories || [])
  const siteName = computed(() => String(brand.value.logoText || site.value.name || t('brandFallback')))

  function showNav(key: string) {
    const nav = brand.value.navShow
    if (!nav) return true
    return nav[key] !== false
  }

  function copy(field: string, i18nKey: string) {
    const value = brand.value[field]
    return value ? String(value) : t(i18nKey)
  }

  function pageTitle(page?: string) {
    const name = siteName.value
    if (!page) return name
    return `${page} | ${name}`
  }

  function fieldLabel(field: any) {
    const loc = locale.value
    const labels = field.labels || {}
    if (labels[loc]) return labels[loc]
    if (loc === 'zh' && field.labelZh) return field.labelZh
    return field.label || field.key
  }

  const inquiryFields = computed(() => {
    const list = brand.value.inquiryFields
    return Array.isArray(list) && list.length ? list : [
      { key: 'specs', label: 'Key specifications', labelZh: '关键规格', type: 'text', placeholder: 'Size, material…' },
      { key: 'port', label: 'Destination port', labelZh: '目的港', type: 'text' },
      { key: 'incoterm', label: 'Trade terms', labelZh: '贸易条款', type: 'select', options: ['FOB', 'CIF', 'CFR', 'EXW', 'DDP'] }
    ]
  })

  const inquiryHints = computed(() => {
    const list = brand.value.inquiryHints
    return Array.isArray(list) && list.length ? list : [t('hintQty'), t('hintSpecs'), t('hintOem')]
  })

  return { brand, site, seo, categories, siteName, showNav, copy, pageTitle, fieldLabel, inquiryFields, inquiryHints }
}

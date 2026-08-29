export const CONTENT_LOCALES = [
  { code: 'en', name: 'English', native: 'English', short: 'EN', og: 'en_US' },
  { code: 'zh', name: 'Chinese', native: '中文', short: '中文', og: 'zh_CN' },
  { code: 'pt', name: 'Portuguese', native: 'Português', short: 'PT', og: 'pt_BR' },
  { code: 'ja', name: 'Japanese', native: '日本語', short: 'JA', og: 'ja_JP' },
  { code: 'es', name: 'Spanish', native: 'Español', short: 'ES', og: 'es_ES' },
  { code: 'fr', name: 'French', native: 'Français', short: 'FR', og: 'fr_FR' }
] as const

export type LocaleCode = (typeof CONTENT_LOCALES)[number]['code']

export const LOCALE_CODES = CONTENT_LOCALES.map((l) => l.code)

export const LOCALE_PREFIX = new RegExp(`^/(${LOCALE_CODES.join('|')})(?=/|$)`)

export function localeMeta(code?: string) {
  return CONTENT_LOCALES.find((l) => l.code === code) || CONTENT_LOCALES[0]
}

export function parseLocales(raw: unknown): string[] {
  const list = Array.isArray(raw)
    ? raw.map((x) => String(x).trim())
    : String(raw || 'en,zh').split(/[,\s]+/)
  const uniq = [...new Set(list.filter((c) => LOCALE_CODES.includes(c as LocaleCode)))]
  return uniq.length ? uniq : ['en']
}

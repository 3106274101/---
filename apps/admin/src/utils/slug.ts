export function slugify(input: string) {
  const ascii = String(input || '')
    .trim()
    .toLowerCase()
    .replace(/['"]/g, '')
    .replace(/[\s_]+/g, '-')
    .replace(/[^a-z0-9-]/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
  return ascii
}

export function suggestSlug(title: string, fallback = '') {
  return slugify(title) || fallback || 'item-' + Date.now()
}

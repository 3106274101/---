export type BlockType =
  | 'hero'
  | 'trustBar'
  | 'productGrid'
  | 'specTable'
  | 'solutions'
  | 'factory'
  | 'certificates'
  | 'faq'
  | 'inquiryForm'
  | 'cta'
  | 'richText'
  | 'blogTeaser'
  | 'testimonials'
  | 'logoWall'

export interface BlockDef {
  type: BlockType
  label: string
  hint: string
  defaults: Record<string, any>
}

export const BLOCK_CATALOG: BlockDef[] = [
  {
    type: 'hero',
    label: 'Hero 主视觉',
    hint: '首屏大图、标题、询盘按钮',
    defaults: {
      heading: 'Your independent catalog',
      subtitle: 'OEM factory · export packing · request a quotation',
      cta: 'Get a Quote',
      ctaTo: '/inquiry',
      image: 'https://images.unsplash.com/photo-1545558014-8692077e9b5c?auto=format&fit=crop&w=1600&q=80',
      layout: 'split'
    }
  },
  {
    type: 'trustBar',
    label: '信任条',
    hint: '证书、出口国、年限',
    defaults: { items: ['CE', 'ISO 9001', '80+ countries', '15 years'] }
  },
  {
    type: 'productGrid',
    label: '产品栅格',
    hint: '展示精选商品',
    defaults: { heading: 'Featured products', source: 'featured' }
  },
  {
    type: 'specTable',
    label: '参数对照表',
    hint: '多 SKU 参数对照，列名可改',
    defaults: {
      heading: 'Model comparison',
      columns: ['Model', 'Spec A', 'Spec B'],
      rows: [
        { model: 'SKU-01', flow: '—', hoses: '—' },
        { model: 'SKU-02', flow: '—', hoses: '—' }
      ]
    }
  },
  {
    type: 'solutions',
    label: '解决方案',
    hint: '按应用/客群分场景，不限行业',
    defaults: {
      heading: 'Solutions',
      items: [
        { slug: 'wholesale', title: 'Wholesale catalog', text: 'Publish categories and SKUs for importers.' },
        { slug: 'oem', title: 'OEM / private label', text: 'Logo, packing and specs confirmed before mass production.' },
        { slug: 'projects', title: 'Project orders', text: 'Quote by quantity, destination and delivery window.' }
      ]
    }
  },
  {
    type: 'factory',
    label: '工厂实力',
    hint: '产线、检测、发货',
    defaults: {
      heading: 'From production to packing',
      text: 'Describe lines, inspection and export packing for your industry.',
      image: 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?auto=format&fit=crop&w=1200&q=80'
    }
  },
  {
    type: 'certificates',
    label: '证书墙',
    hint: '证书与资质，按公司实际填写',
    defaults: { heading: 'Certificates', items: ['ISO 9001', 'OEM / ODM', 'Third-party test'] }
  },
  {
    type: 'faq',
    label: 'FAQ',
    hint: '采购常见问题',
    defaults: {
      heading: 'FAQ',
      items: [
        { q: 'Do you support OEM / private label?', a: 'Yes. Confirm logo, packing and MOQ in the inquiry.' },
        { q: 'What should I include in an RFQ?', a: 'Quantity, destination, key specs or drawings, and preferred Incoterms.' }
      ]
    }
  },
  {
    type: 'inquiryForm',
    label: '询盘表单',
    hint: '内嵌获取报价',
    defaults: { title: 'Tell us quantity, specs and destination' }
  },
  {
    type: 'cta',
    label: 'CTA 横幅',
    hint: '页中转化条',
    defaults: { heading: 'Ready to quote? Send quantity and key specs.', cta: 'Talk to export team', ctaTo: '/inquiry' }
  },
  {
    type: 'richText',
    label: '富文本',
    hint: '补充说明',
    defaults: { html: '<p>Write factory or product story here.</p>' }
  },
  {
    type: 'blogTeaser',
    label: '博客摘要',
    hint: 'SEO 文章入口',
    defaults: { heading: 'Insights for buyers' }
  },
  {
    type: 'testimonials',
    label: '客户评价',
    hint: '采购商口碑，增强信任',
    defaults: {
      heading: 'Buyers say',
      items: [
        { quote: 'Specs matched the datasheet and packing was export-ready.', name: 'Procurement', country: 'Pakistan' },
        { quote: 'OEM logo and packing were confirmed on the sample.', name: 'Buyer', country: 'Nigeria' }
      ]
    }
  },
  {
    type: 'logoWall',
    label: '合作/市场墙',
    hint: '出口市场或合作品牌文字',
    defaults: { heading: 'Markets', items: ['Pakistan', 'Nigeria', 'Kenya', 'UAE'] }
  }
]

export function createBlock(type: BlockType | string) {
  const def = BLOCK_CATALOG.find((b) => b.type === type)
  if (!def) return { type, props: {} }
  return { type, props: JSON.parse(JSON.stringify(def.defaults)) }
}

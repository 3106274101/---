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
      heading: 'High-Precision Fuel Dispensers',
      subtitle: 'OEM factory · CE / ISO · 80+ countries',
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
    hint: '展示精选机型',
    defaults: { heading: 'Core models', source: 'featured' }
  },
  {
    type: 'specTable',
    label: '参数对照表',
    hint: '多机型流量/枪数对比',
    defaults: {
      heading: 'Model comparison',
      rows: [
        { model: 'T80', flow: '40-80 L/min', hoses: '2 / 4' },
        { model: 'T120', flow: '80-120 L/min', hoses: '4' },
        { model: 'M50', flow: '50 L/min', hoses: '1' }
      ]
    }
  },
  {
    type: 'solutions',
    label: '解决方案',
    hint: '加油站 / 车队 / 船用场景',
    defaults: {
      heading: 'Solutions',
      items: [
        { slug: 'gas-station', title: 'Petrol stations', text: 'Island dispensers with POS protocols.' },
        { slug: 'fleet', title: 'Fleet & mining', text: 'High-flow and skid units for depots.' },
        { slug: 'marine', title: 'Marine & remote', text: 'Mobile dispensers for docks and islands.' }
      ]
    }
  },
  {
    type: 'factory',
    label: '工厂实力',
    hint: '产线、检测、发货',
    defaults: {
      heading: 'From machining to calibration',
      text: 'In-house CNC, painting, meter calibration and explosion-proof assembly.',
      image: 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?auto=format&fit=crop&w=1200&q=80'
    }
  },
  {
    type: 'certificates',
    label: '证书墙',
    hint: 'CE / ISO / ATEX',
    defaults: { heading: 'Certificates', items: ['CE', 'ISO 9001', 'ATEX option', 'OIML-ready'] }
  },
  {
    type: 'faq',
    label: 'FAQ',
    hint: '采购常见问题',
    defaults: {
      heading: 'FAQ',
      items: [
        { q: 'Do you support OEM brand panels?', a: 'Yes. Logo, color and protocol can be customized with MOQ.' },
        { q: 'Which voltages are available?', a: '110V, 220V and 380V. Confirm local standard in the inquiry.' }
      ]
    }
  },
  {
    type: 'inquiryForm',
    label: '询盘表单',
    hint: '内嵌获取报价',
    defaults: { title: 'Tell us voltage, hose count and destination port' }
  },
  {
    type: 'cta',
    label: 'CTA 横幅',
    hint: '页中转化条',
    defaults: { heading: 'Need 4-nozzle 380V units for a new station?', cta: 'Talk to export team', ctaTo: '/inquiry' }
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
  }
]

export function createBlock(type: BlockType | string) {
  const def = BLOCK_CATALOG.find((b) => b.type === type)
  if (!def) return { type, props: {} }
  return { type, props: JSON.parse(JSON.stringify(def.defaults)) }
}

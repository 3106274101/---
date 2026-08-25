export default defineI18nConfig(() => ({
  legacy: false,
  locale: 'en',
  messages: {
    en: {
      nav: { home: 'Home', products: 'Products', solutions: 'Solutions', factory: 'Factory', about: 'About', blog: 'Blog', contact: 'Contact', inquiry: 'Get Quote', certs: 'Certificates' },
      cta: 'Get a Quote',
      inquire: 'Inquire',
      featured: 'Core models',
      specs: 'Specifications',
      related: 'Insights for buyers',
      search: 'Search',
      go: 'Go',
      company: 'Company',
      allModels: 'All models',
      productTitle: 'Fuel dispensers & station equipment',
      productLead: 'Honesty to Brilliance island dispensers, Prestige mini stations 300–6000L, 11A nozzles. OEM factory in Xinxiang, Henan.',
      inquiryLead: 'Tell us voltage, nozzle count, gasoline/diesel and destination port.',
      replyHint: 'Export desk replies within one business day.',
      form: {
        name: 'Name', company: 'Company', email: 'Email', phone: 'Phone', country: 'Country',
        quantity: 'Quantity', message: 'Requirements', submit: 'Send inquiry',
        agree: 'I agree to the processing of this inquiry data.',
        ok: 'Inquiry received. We will reply within 1 business day.'
      },
      footer: { privacy: 'Privacy', cookies: 'Cookies' }
    },
    zh: {
      nav: { home: '首页', products: '产品', solutions: '方案', factory: '工厂', about: '关于', blog: '博客', contact: '联系', inquiry: '获取报价', certs: '证书' },
      cta: '获取报价',
      inquire: '询价',
      featured: '核心机型',
      specs: '技术参数',
      related: '采购指南',
      search: '搜索',
      go: '搜索',
      company: '公司',
      allModels: '全部机型',
      productTitle: '加油机与加油站设备',
      productLead: '诚信至辉煌岛式加油机、尊享 300–6000L 微型站、11A 油枪。河南新乡厂家直供。',
      inquiryLead: '请告知电压、枪数、汽柴油与目的港。',
      replyHint: '出口商务将在一个工作日内回复。',
      form: {
        name: '姓名', company: '公司', email: '邮箱', phone: '电话', country: '国家',
        quantity: '数量', message: '需求', submit: '提交询盘',
        agree: '我同意处理本次询盘数据。',
        ok: '已收到询盘，我们将在一个工作日内回复。'
      },
      footer: { privacy: '隐私政策', cookies: 'Cookie' }
    }
  }
}))

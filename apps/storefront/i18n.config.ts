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
      productTitle: 'Fuel dispensers & accessories',
      productLead: 'CE-certified island dispensers, mobile skids, nozzles and meters from an OEM factory.',
      inquiryLead: 'Tell us voltage, hose count, destination port and OEM needs.',
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
      productTitle: '加油机与配件',
      productLead: 'CE 认证岛式加油机、撬装机组、油枪与流量计，工厂直供。',
      inquiryLead: '请告知电压、枪数、目的港与是否 OEM。',
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

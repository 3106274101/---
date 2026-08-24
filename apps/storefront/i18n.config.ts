export default defineI18nConfig(() => ({
  legacy: false,
  locale: 'en',
  messages: {
    en: {
      nav: { home: 'Home', products: 'Products', solutions: 'Solutions', about: 'About', blog: 'Blog', contact: 'Contact', inquiry: 'Get Quote' },
      cta: 'Get a Quote',
      featured: 'Core models',
      specs: 'Specifications',
      related: 'Related reading',
      form: {
        name: 'Name', company: 'Company', email: 'Email', country: 'Country',
        quantity: 'Quantity', message: 'Requirements', submit: 'Send inquiry', ok: 'Inquiry received. We will reply within 1 business day.'
      },
      footer: { privacy: 'Privacy', cookies: 'Cookies' }
    },
    zh: {
      nav: { home: '首页', products: '产品', solutions: '方案', about: '关于', blog: '博客', contact: '联系', inquiry: '获取报价' },
      cta: '获取报价',
      featured: '核心机型',
      specs: '技术参数',
      related: '相关阅读',
      form: {
        name: '姓名', company: '公司', email: '邮箱', country: '国家',
        quantity: '数量', message: '需求', submit: '提交询盘', ok: '已收到询盘，我们将在一个工作日内回复。'
      },
      footer: { privacy: '隐私政策', cookies: 'Cookie' }
    }
  }
}))

const nav = {
  en: { home: 'Home', products: 'Products', solutions: 'Solutions', factory: 'Factory', about: 'About', blog: 'Blog', contact: 'Contact', inquiry: 'Get Quote', certs: 'Certificates' },
  zh: { home: '首页', products: '产品', solutions: '方案', factory: '工厂', about: '关于', blog: '博客', contact: '联系', inquiry: '获取报价', certs: '证书' },
  pt: { home: 'Início', products: 'Produtos', solutions: 'Soluções', factory: 'Fábrica', about: 'Sobre', blog: 'Blog', contact: 'Contato', inquiry: 'Pedir cotação', certs: 'Certificados' },
  ja: { home: 'ホーム', products: '製品', solutions: 'ソリューション', factory: '工場', about: '会社概要', blog: 'ブログ', contact: 'お問い合わせ', inquiry: '見積依頼', certs: '認証' },
  es: { home: 'Inicio', products: 'Productos', solutions: 'Soluciones', factory: 'Fábrica', about: 'Nosotros', blog: 'Blog', contact: 'Contacto', inquiry: 'Pedir cotización', certs: 'Certificados' },
  fr: { home: 'Accueil', products: 'Produits', solutions: 'Solutions', factory: 'Usine', about: 'À propos', blog: 'Blog', contact: 'Contact', inquiry: 'Demander un devis', certs: 'Certificats' }
}

const form = {
  en: { name: 'Name', company: 'Company', email: 'Email', phone: 'Phone', country: 'Country', quantity: 'Quantity', message: 'Requirements', submit: 'Send inquiry', agree: 'I agree to the processing of this inquiry data.', ok: 'Inquiry received. We will reply within 1 business day.' },
  zh: { name: '姓名', company: '公司', email: '邮箱', phone: '电话', country: '国家', quantity: '数量', message: '需求', submit: '提交询盘', agree: '我同意处理本次询盘数据。', ok: '已收到询盘，我们将在一个工作日内回复。' },
  pt: { name: 'Nome', company: 'Empresa', email: 'E-mail', phone: 'Telefone', country: 'País', quantity: 'Quantidade', message: 'Requisitos', submit: 'Enviar consulta', agree: 'Concordo com o tratamento destes dados.', ok: 'Consulta recebida. Responderemos em 1 dia útil.' },
  ja: { name: 'お名前', company: '会社名', email: 'メール', phone: '電話', country: '国', quantity: '数量', message: 'ご要望', submit: '送信する', agree: '本お問い合わせデータの取り扱いに同意します。', ok: '送信しました。1営業日以内にご連絡します。' },
  es: { name: 'Nombre', company: 'Empresa', email: 'Correo', phone: 'Teléfono', country: 'País', quantity: 'Cantidad', message: 'Requisitos', submit: 'Enviar consulta', agree: 'Acepto el tratamiento de estos datos.', ok: 'Consulta recibida. Responderemos en 1 día hábil.' },
  fr: { name: 'Nom', company: 'Société', email: 'E-mail', phone: 'Téléphone', country: 'Pays', quantity: 'Quantité', message: 'Besoins', submit: 'Envoyer', agree: 'J’accepte le traitement de ces données.', ok: 'Demande reçue. Réponse sous 1 jour ouvrable.' }
}

export default defineI18nConfig(() => ({
  legacy: false,
  locale: 'en',
  fallbackLocale: 'en',
  messages: {
    en: {
      nav: nav.en, cta: 'Get a Quote', inquire: 'Inquire', featured: 'Featured products', specs: 'Specifications',
      related: 'Insights for buyers', search: 'Search', go: 'Go', company: 'Company', allModels: 'All products',
      productTitle: 'Products', productLead: 'Browse the catalog and request a quotation.',
      inquiryLead: 'Tell us quantity, destination and key specifications.',
      replyHint: 'Sales replies within one business day.', relatedProducts: 'Related products',
      notFound: 'Page not found', notFoundHint: 'This page is not available. Browse products or send an inquiry.',
      stickyHint: 'Need a quotation?', brandFallback: 'Company', tradeInfo: 'Ordering info', datasheet: 'Datasheet',
      hintQty: 'Quantity and destination', hintSpecs: 'Key specifications or drawings', hintOem: 'OEM / private label if needed',
      form: form.en, footer: { privacy: 'Privacy', cookies: 'Cookies' }
    },
    zh: {
      nav: nav.zh, cta: '获取报价', inquire: '询价', featured: '精选商品', specs: '规格参数',
      related: '采购指南', search: '搜索', go: '搜索', company: '公司', allModels: '全部商品',
      productTitle: '产品', productLead: '浏览目录并提交询盘。',
      inquiryLead: '请告知数量、目的地与关键规格。',
      replyHint: '销售将在一个工作日内回复。', relatedProducts: '相关商品',
      notFound: '页面不存在', notFoundHint: '该页面不可用，可浏览产品或提交询盘。',
      stickyHint: '需要报价？', brandFallback: '公司', tradeInfo: '采购信息', datasheet: '参数资料',
      hintQty: '数量与目的地', hintSpecs: '关键规格或图纸', hintOem: '是否 OEM / 贴牌',
      form: form.zh, footer: { privacy: '隐私政策', cookies: 'Cookie' }
    },
    pt: {
      nav: nav.pt, cta: 'Pedir cotação', inquire: 'Consultar', featured: 'Produtos em destaque', specs: 'Especificações',
      related: 'Guia do comprador', search: 'Pesquisar', go: 'Ir', company: 'Empresa', allModels: 'Todos os produtos',
      productTitle: 'Produtos', productLead: 'Navegue no catálogo e solicite uma cotação.',
      inquiryLead: 'Informe quantidade, destino e especificações principais.',
      replyHint: 'A equipe comercial responde em um dia útil.', relatedProducts: 'Produtos relacionados',
      notFound: 'Página não encontrada', notFoundHint: 'Esta página não está disponível. Veja os produtos ou envie uma consulta.',
      stickyHint: 'Precisa de uma cotação?', brandFallback: 'Empresa', tradeInfo: 'Informações de pedido', datasheet: 'Ficha técnica',
      hintQty: 'Quantidade e destino', hintSpecs: 'Especificações ou desenhos', hintOem: 'OEM / marca própria, se necessário',
      form: form.pt, footer: { privacy: 'Privacidade', cookies: 'Cookies' }
    },
    ja: {
      nav: nav.ja, cta: '見積を依頼', inquire: '問い合わせ', featured: 'おすすめ製品', specs: '仕様',
      related: 'バイヤー向け情報', search: '検索', go: '検索', company: '会社', allModels: 'すべての製品',
      productTitle: '製品', productLead: 'カタログをご覧のうえ、お見積をご依頼ください。',
      inquiryLead: '数量、納入先、主な仕様をお知らせください。',
      replyHint: '営業日1日以内にご返信します。', relatedProducts: '関連製品',
      notFound: 'ページが見つかりません', notFoundHint: 'このページは利用できません。製品一覧またはお問い合わせをご利用ください。',
      stickyHint: 'お見積が必要ですか？', brandFallback: '会社', tradeInfo: '発注情報', datasheet: 'データシート',
      hintQty: '数量と納入先', hintSpecs: '主な仕様または図面', hintOem: 'OEM / プライベートブランド',
      form: form.ja, footer: { privacy: 'プライバシー', cookies: 'Cookie' }
    },
    es: {
      nav: nav.es, cta: 'Pedir cotización', inquire: 'Consultar', featured: 'Productos destacados', specs: 'Especificaciones',
      related: 'Guía para compradores', search: 'Buscar', go: 'Ir', company: 'Empresa', allModels: 'Todos los productos',
      productTitle: 'Productos', productLead: 'Explore el catálogo y solicite una cotización.',
      inquiryLead: 'Indique cantidad, destino y especificaciones clave.',
      replyHint: 'Ventas responde en un día hábil.', relatedProducts: 'Productos relacionados',
      notFound: 'Página no encontrada', notFoundHint: 'Esta página no está disponible. Vea productos o envíe una consulta.',
      stickyHint: '¿Necesita una cotización?', brandFallback: 'Empresa', tradeInfo: 'Información de pedido', datasheet: 'Ficha técnica',
      hintQty: 'Cantidad y destino', hintSpecs: 'Especificaciones o planos', hintOem: 'OEM / marca blanca si aplica',
      form: form.es, footer: { privacy: 'Privacidad', cookies: 'Cookies' }
    },
    fr: {
      nav: nav.fr, cta: 'Demander un devis', inquire: 'Demander', featured: 'Produits phares', specs: 'Spécifications',
      related: 'Guide acheteur', search: 'Rechercher', go: 'OK', company: 'Entreprise', allModels: 'Tous les produits',
      productTitle: 'Produits', productLead: 'Parcourez le catalogue et demandez un devis.',
      inquiryLead: 'Indiquez quantité, destination et spécifications clés.',
      replyHint: 'Réponse sous un jour ouvrable.', relatedProducts: 'Produits associés',
      notFound: 'Page introuvable', notFoundHint: 'Cette page n’est pas disponible. Consultez les produits ou envoyez une demande.',
      stickyHint: 'Besoin d’un devis ?', brandFallback: 'Entreprise', tradeInfo: 'Infos commande', datasheet: 'Fiche technique',
      hintQty: 'Quantité et destination', hintSpecs: 'Spécifications ou plans', hintOem: 'OEM / marque blanche si besoin',
      form: form.fr, footer: { privacy: 'Confidentialité', cookies: 'Cookies' }
    }
  }
}))

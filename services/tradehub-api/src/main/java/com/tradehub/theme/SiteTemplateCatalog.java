package com.tradehub.theme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Five storefront skins: visual tokens plus starter CMS pages. */
public final class SiteTemplateCatalog {
    public static String files() {
        String base = System.getenv("TRADEHUB_UPLOAD_PUBLIC_BASE");
        if (base == null || base.isBlank()) {
            String render = System.getenv("RENDER_EXTERNAL_URL");
            base = (render == null || render.isBlank() ? "http://localhost:8080" : render) + "/files";
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/demo/";
    }

    public static String img(String fileName) {
        return files() + fileName;
    }

    public static final String ABOUT_EN = "Introduce your company, factory and export markets here. Replace this starter copy in the page editor.";
    public static final String ABOUT_ZH = "在此介绍公司、工厂与出口市场。可在页面编辑器中替换这段示例文案。";

    private SiteTemplateCatalog() {
    }

    public static List<Map<String, Object>> list() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String id : List.of("industrial", "blueprint", "catalog", "midnight", "signal")) {
            list.add(summary(id));
        }
        return list;
    }

    public static String normalize(String id) {
        if (id == null || id.isBlank() || "industrial-fuel".equals(id)) {
            return "industrial";
        }
        return id;
    }

    public static Map<String, Object> summary(String id) {
        Map<String, Object> def = def(id);
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", def.get("id"));
        row.put("name", def.get("name"));
        row.put("nameZh", def.get("nameZh"));
        row.put("pitch", def.get("pitch"));
        row.put("pitchZh", def.get("pitchZh"));
        row.put("primaryColor", def.get("primaryColor"));
        row.put("accentColor", def.get("accentColor"));
        row.put("header", def.get("header"));
        row.put("heroLayout", def.get("heroLayout"));
        row.put("previewImage", def.get("heroImage"));
        return row;
    }

    public static Map<String, Object> def(String id) {
        return switch (normalize(id)) {
            case "blueprint" -> defOf("blueprint", "Engineering Blueprint", "工程蓝图",
                    "Technical, spec-first layout for OEM buyers.", "偏参数与工程感，适合询盘型买家。",
                    "#123a56", "#c9a227", "dark", "overlay", "2px", "tech", img("honesty.png"));
            case "catalog" -> defOf("catalog", "Clean Catalog", "清爽目录",
                    "Bright product-first catalog with soft cards.", "浅色目录风，商品卡片更突出。",
                    "#1a3a32", "#0f766e", "light", "split", "12px", "sans", img("elite.jpg"));
            case "midnight" -> defOf("midnight", "Midnight Export", "暗金出口",
                    "Dark luxury skin for high-end OEM branding.", "深色金点，偏品牌展示。",
                    "#111827", "#c9a227", "dark", "overlay", "2px", "serif", img("brilliance.jpg"));
            case "signal" -> defOf("signal", "Safety Signal", "工地警示",
                    "High-contrast orange for mining and depot sites.", "高对比警示橙，适合矿山与车场。",
                    "#1c1917", "#ea580c", "dark", "split", "0px", "sans", img("prestige-v.jpg"));
            default -> defOf("industrial", "Industrial Navy", "工业海军蓝",
                    "Classic factory site: navy, split hero, clear specs.", "经典工厂站：海军蓝、左右首屏。",
                    "#0b1f3a", "#c2410c", "light", "split", "4px", "sans", img("aurora.jpg"));
        };
    }

    public static Map<String, Object> brand(String id, Map<String, Object> seed) {
        Map<String, Object> tpl = def(id);
        Map<String, Object> brand = new LinkedHashMap<>();
        if (seed != null) {
            brand.putAll(seed);
        }
        brand.put("primaryColor", tpl.get("primaryColor"));
        brand.put("accentColor", tpl.get("accentColor"));
        brand.put("heroImage", tpl.get("heroImage"));
        brand.put("header", tpl.get("header"));
        brand.put("radius", tpl.get("radius"));
        brand.put("font", tpl.get("font"));
        brand.put("heroLayout", tpl.get("heroLayout"));
        if (isBlank(brand.get("logoText"))) {
            brand.put("logoText", "");
        }
        if (isBlank(brand.get("tagline"))) {
            brand.put("tagline", tpl.get("pitch"));
        }
        if (isBlank(brand.get("catalogTitle"))) {
            brand.put("catalogTitle", "Products");
        }
        if (isBlank(brand.get("catalogLead"))) {
            brand.put("catalogLead", "Browse the catalog and request a quotation.");
        }
        if (isBlank(brand.get("inquiryLead"))) {
            brand.put("inquiryLead", "Tell us quantity, destination and key specifications.");
        }
        if (isBlank(brand.get("stickyHint"))) {
            brand.put("stickyHint", "Need a quotation?");
        }
        if (!(brand.get("inquiryHints") instanceof List<?>)) {
            brand.put("inquiryHints", List.of(
                    "Quantity and destination",
                    "Key specifications or drawings",
                    "OEM / private label if needed"));
        }
        if (!(brand.get("inquiryFields") instanceof List<?>)) {
            brand.put("inquiryFields", List.of(
                    field("specs", "Key specifications", "关键规格", "text", "Size, material, voltage…"),
                    field("port", "Destination port", "目的港", "text", "e.g. Lagos, Karachi"),
                    field("incoterm", "Trade terms", "贸易条款", "select", "", List.of("FOB", "CIF", "CFR", "EXW", "DDP"))
            ));
        }
        if (!(brand.get("navShow") instanceof Map<?, ?>)) {
            brand.put("navShow", Map.of(
                    "products", true,
                    "solutions", false,
                    "factory", false,
                    "about", true,
                    "blog", true,
                    "contact", true
            ));
        }
        return brand;
    }

    public static List<PageSeed> pages(String id) {
        return pages(id, null);
    }

    public static List<PageSeed> pages(String id, String companyName) {
        Map<String, Object> tpl = def(id);
        String layout = String.valueOf(tpl.get("heroLayout"));
        String hero = String.valueOf(tpl.get("heroImage"));
        String name = isBlank(companyName) ? "Your company" : companyName;
        List<PageSeed> list = new ArrayList<>(homePages(normalize(id), layout, hero, name));
        list.addAll(sharedPages(layout, hero, name));
        return list;
    }

    public record PageSeed(String slug, String type, String enTitle, String zhTitle, List<?> enBlocks, List<?> zhBlocks) {
    }

    private static List<PageSeed> homePages(String id, String layout, String hero, String name) {
        return switch (id) {
            case "blueprint" -> List.of(new PageSeed("home", "home",
                    name + " | Specs & catalog", name + " | 参数目录",
                    List.of(
                            trust("OEM / ODM", "MOQ ready", "Datasheet on request", "Export packing"),
                            hero(layout, hero, "Lead with specifications buyers need",
                                    name + " — a spec-first catalog for OEM and wholesale inquiries.",
                                    "Request a quote", "/inquiry"),
                            solutions("Applications", solutionsEn()),
                            products("Featured products"),
                            factory("Factory capability", ABOUT_EN),
                            faq(faqEn()),
                            cta("Send quantity, specs and destination port", "Get a Quote")
                    ),
                    List.of(
                            trust("OEM / ODM", "支持起订量", "可提供参数表", "出口包装"),
                            hero(layout, hero, "把买家关心的参数放在最前面",
                                    name + " — 面向 OEM 与批发询盘的参数型目录。",
                                    "获取报价", "/inquiry"),
                            solutions("应用场景", solutionsZh()),
                            products("精选商品"),
                            factory("工厂能力", ABOUT_ZH),
                            faq(faqZh()),
                            cta("请提供数量、规格与目的港", "获取报价")
                    )));
            case "catalog" -> List.of(new PageSeed("home", "home",
                    name + " | Product catalog", name + " | 商品目录",
                    List.of(
                            hero(layout, hero, "Browse the catalog, then request a quote",
                                    "A clean product-first storefront for importers comparing SKUs.",
                                    "View products", "/products"),
                            products("Featured products"),
                            solutions("Shop by use case", solutionsEn()),
                            blog("Buying notes"),
                            factory("Who we are", ABOUT_EN),
                            cta("Need a quotation pack?", "Get a Quote")
                    ),
                    List.of(
                            hero(layout, hero, "先看目录，再提交询盘",
                                    "浅色目录站，方便进口商对比 SKU。",
                                    "查看商品", "/products"),
                            products("精选商品"),
                            solutions("按场景选购", solutionsZh()),
                            blog("采购说明"),
                            factory("厂家介绍", ABOUT_ZH),
                            cta("需要报价资料？", "获取报价")
                    )));
            case "midnight" -> List.of(new PageSeed("home", "home",
                    name + " | Export brand", name + " | 出口品牌",
                    List.of(
                            hero(layout, hero, "Your brand. Your catalog. Your OEM story.",
                                    name + " — a dark luxury skin for high-end export branding.",
                                    "Start OEM brief", "/inquiry"),
                            products("Flagship products"),
                            factory("Manufacturer profile", ABOUT_EN),
                            faq(faqEn()),
                            cta("Logo, color and packing can be confirmed on a sample", "Contact us")
                    ),
                    List.of(
                            hero(layout, hero, "品牌、目录与 OEM 故事",
                                    name + " — 深色金点皮肤，适合高端出口品牌。",
                                    "提交 OEM 需求", "/inquiry"),
                            products("旗舰商品"),
                            factory("厂家介绍", ABOUT_ZH),
                            faq(faqZh()),
                            cta("Logo、颜色与包装可在样品上确认", "联系我们")
                    )));
            case "signal" -> List.of(new PageSeed("home", "home",
                    name + " | Industrial catalog", name + " | 工业目录",
                    List.of(
                            hero(layout, hero, "High-contrast catalog for B2B buyers",
                                    name + " — built for depots, projects and wholesale orders.",
                                    "Get a Quote", "/inquiry"),
                            trust("OEM", "Project orders", "Export packing", "Fast RFQ"),
                            products("Featured products"),
                            cta("Tell us quantity, specs and destination", "Get a Quote"),
                            solutions("Where it works", solutionsEn()),
                            faq(faqEn())
                    ),
                    List.of(
                            hero(layout, hero, "高对比目录，方便 B2B 买家下单",
                                    name + " — 适合工程、批发与出口询盘。",
                                    "获取报价", "/inquiry"),
                            trust("OEM", "工程订单", "出口包装", "快速询盘"),
                            products("精选商品"),
                            cta("请说明数量、规格与目的地", "获取报价"),
                            solutions("适用场景", solutionsZh()),
                            faq(faqZh())
                    )));
            default -> List.of(new PageSeed("home", "home",
                    name + " | Manufacturer", name + " | 厂家独立站",
                    List.of(
                            hero(layout, hero, "Your independent catalog for global buyers",
                                    name + " — publish categories, products and RFQ from one admin.",
                                    "Get a Quote", "/inquiry"),
                            trust("OEM / ODM", "Multi-category", "On / off shelf", "Inquiry CRM"),
                            products("Featured products"),
                            solutions("What we supply", solutionsEn()),
                            factory("About the factory", ABOUT_EN),
                            faq(faqEn()),
                            blog("Buyer notes"),
                            cta("Ready to quote? Send quantity and key specs.", "Get a Quote")
                    ),
                    List.of(
                            hero(layout, hero, "面向全球买家的独立站目录",
                                    name + " — 在一个后台发布类目、商品与询盘。",
                                    "获取报价", "/inquiry"),
                            trust("OEM / ODM", "多类目", "上下架", "询盘跟进"),
                            products("精选商品"),
                            solutions("主要产品", solutionsZh()),
                            factory("工厂介绍", ABOUT_ZH),
                            faq(faqZh()),
                            blog("采购说明"),
                            cta("准备询价？请告知数量与关键规格。", "获取报价")
                    )));
        };
    }

    private static List<PageSeed> sharedPages(String layout, String hero, String name) {
        return List.of(
                new PageSeed("about", "about", "About " + name, "关于" + name,
                        List.of(
                                hero(layout, img("about.jpg"), "About " + name, ABOUT_EN, "Contact Us", "/contact"),
                                rich("<p>" + ABOUT_EN + "</p><p>Edit address, email and factory story in Brand and this page.</p>"),
                                factory("Factory profile", "Describe capability, QC and export markets.")
                        ),
                        List.of(
                                hero(layout, img("about.jpg"), "关于" + name, ABOUT_ZH, "联系我们", "/contact"),
                                rich("<p>" + ABOUT_ZH + "</p><p>请在「品牌装修」和本页补充地址、邮箱与工厂介绍。</p>"),
                                factory("工厂介绍", "补充产能、质检与出口市场。")
                        )),
                new PageSeed("factory", "factory", "Factory & Capability", "工厂与产能",
                        List.of(
                                hero(layout, img("about.jpg"), "Manufacturing for export buyers", "", "Get a Quote", "/inquiry"),
                                factory("From production to packing",
                                        "Describe lines, inspection and container loading. Replace this starter copy.")
                        ),
                        List.of(
                                hero(layout, img("about.jpg"), "面向出口买家的制造能力", "", "获取报价", "/inquiry"),
                                factory("从生产到包装", "补充产线、检验与装柜说明，可替换这段示例文案。")
                        )),
                new PageSeed("certificates", "certificates", "Certificates", "资质证书",
                        List.of(Map.of("type", "certificates", "props", Map.of("heading", "Certificates & options",
                                "items", List.of("ISO 9001", "OEM / ODM", "Third-party test", "Export packing")))),
                        List.of(Map.of("type", "certificates", "props", Map.of("heading", "证书与配置",
                                "items", List.of("ISO 9001", "OEM / ODM", "第三方检测", "出口包装"))))),
                new PageSeed("faq", "faq", "FAQ", "常见问题",
                        List.of(faq(faqEn())), List.of(faq(faqZh()))),
                new PageSeed("contact", "contact", "Contact Us", "联系我们",
                        List.of(
                                hero(layout, img("station.png"), "Talk to sales", "Use Brand settings for email, phone and WhatsApp.", "Send inquiry", "/inquiry"),
                                Map.of("type", "inquiryForm", "props", Map.of("title", "Tell us quantity, specs and destination"))
                        ),
                        List.of(
                                hero(layout, img("station.png"), "联系销售", "邮箱、电话与 WhatsApp 在「品牌装修」中填写。", "提交询盘", "/inquiry"),
                                Map.of("type", "inquiryForm", "props", Map.of("title", "请告知数量、规格与目的地"))
                        )),
                new PageSeed("solutions", "solutions", "Solutions", "解决方案",
                        List.of(solutions("Applications we support", solutionsEn())),
                        List.of(solutions("可服务的场景", solutionsZh())))
        );
    }

    private static Map<String, Object> defOf(String id, String name, String nameZh, String pitch, String pitchZh,
                                             String primary, String accent, String header, String heroLayout,
                                             String radius, String font, String heroImage) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("nameZh", nameZh);
        map.put("pitch", pitch);
        map.put("pitchZh", pitchZh);
        map.put("primaryColor", primary);
        map.put("accentColor", accent);
        map.put("header", header);
        map.put("heroLayout", heroLayout);
        map.put("radius", radius);
        map.put("font", font);
        map.put("heroImage", heroImage);
        return map;
    }

    private static Map<String, Object> hero(String layout, String image, String heading, String subtitle, String cta, String to) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("layout", layout);
        props.put("image", image);
        props.put("heading", heading);
        props.put("subtitle", subtitle);
        props.put("cta", cta);
        props.put("ctaTo", to);
        return Map.of("type", "hero", "props", props);
    }

    private static Map<String, Object> trust(String a, String b, String c, String d) {
        return Map.of("type", "trustBar", "props", Map.of("items", List.of(a, b, c, d)));
    }

    private static Map<String, Object> products(String heading) {
        return Map.of("type", "productGrid", "props", Map.of("source", "featured", "heading", heading));
    }

    private static Map<String, Object> solutions(String heading, List<Map<String, String>> items) {
        return Map.of("type", "solutions", "props", Map.of("heading", heading, "items", items));
    }

    private static Map<String, Object> factory(String heading, String text) {
        return Map.of("type", "factory", "props", Map.of("heading", heading, "text", text, "image", img("advantage.png")));
    }

    private static Map<String, Object> faq(List<Map<String, String>> items) {
        return Map.of("type", "faq", "props", Map.of("items", items));
    }

    private static Map<String, Object> blog(String heading) {
        return Map.of("type", "blogTeaser", "props", Map.of("heading", heading));
    }

    private static Map<String, Object> cta(String heading, String label) {
        return Map.of("type", "cta", "props", Map.of("heading", heading, "cta", label, "ctaTo", "/inquiry"));
    }

    private static Map<String, Object> rich(String html) {
        return Map.of("type", "richText", "props", Map.of("html", html));
    }

    private static List<Map<String, String>> solutionsEn() {
        return List.of(
                Map.of("slug", "wholesale", "title", "Wholesale catalog", "text", "Publish categories and SKUs for importers to browse."),
                Map.of("slug", "oem", "title", "OEM / private label", "text", "Logo, packing and spec sheets confirmed before mass production."),
                Map.of("slug", "projects", "title", "Project orders", "text", "Quote by quantity, destination and delivery window."),
                Map.of("slug", "parts", "title", "Parts & accessories", "text", "Related items can sit in their own category.")
        );
    }

    private static List<Map<String, String>> solutionsZh() {
        return List.of(
                Map.of("slug", "wholesale", "title", "批发目录", "text", "发布类目与 SKU，方便进口商浏览。"),
                Map.of("slug", "oem", "title", "OEM / 贴牌", "text", "量产前确认 Logo、包装与参数表。"),
                Map.of("slug", "projects", "title", "工程订单", "text", "按数量、目的地与交期报价。"),
                Map.of("slug", "parts", "title", "配件", "text", "相关商品可单独建分类。")
        );
    }

    private static List<Map<String, String>> faqEn() {
        return List.of(
                Map.of("q", "Can I order OEM / private label?", "a", "Yes. Confirm logo, packing and MOQ in the inquiry."),
                Map.of("q", "What should I include in an RFQ?", "a", "Quantity, destination port, key specs or drawings, and preferred Incoterms."),
                Map.of("q", "Do you support multiple product categories?", "a", "Yes. Create categories in admin, then list or unlist products per site."),
                Map.of("q", "How do I contact sales?", "a", "Use the inquiry form, or the email and WhatsApp in the site header.")
        );
    }

    private static List<Map<String, String>> faqZh() {
        return List.of(
                Map.of("q", "能否 OEM / 贴牌？", "a", "可以。请在询盘中确认 Logo、包装与起订量。"),
                Map.of("q", "询盘需要提供什么？", "a", "数量、目的港、关键规格或图纸，以及偏好的贸易条款。"),
                Map.of("q", "能否做多类目？", "a", "可以。在后台创建分类，再按站点上下架或隐藏商品。"),
                Map.of("q", "如何联系销售？", "a", "使用询盘表单，或页头中的邮箱与 WhatsApp。")
        );
    }

    private static Map<String, Object> field(String key, String label, String labelZh, String type, String placeholder) {
        return field(key, label, labelZh, type, placeholder, List.of());
    }

    private static Map<String, Object> field(String key, String label, String labelZh, String type, String placeholder, List<String> options) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", key);
        map.put("label", label);
        map.put("labelZh", labelZh);
        map.put("type", type);
        map.put("placeholder", placeholder);
        if (options != null && !options.isEmpty()) {
            map.put("options", options);
        }
        return map;
    }

    private static boolean isBlank(Object value) {
        return value == null || String.valueOf(value).isBlank();
    }
}

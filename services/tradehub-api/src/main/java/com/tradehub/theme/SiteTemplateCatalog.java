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

    public static final String ABOUT_EN = "Zhenghe Machinery Equipment Co., Ltd., a professional Chinese manufacturer with 17-year experience, produces and wholesales fuel dispensers, mining fuel dispensers, LPG dispensers, gas station management systems and related parts.";
    public static final String ABOUT_ZH = "辉县市正和机械设备有限公司是专业的中国制造商，拥有 17 年行业经验，生产并批发加油机、矿用加油机、LPG 加气机、加油站管理系统及相关配件。";

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
            brand.put("logoText", "ZhengHe");
        }
        if (isBlank(brand.get("tagline"))) {
            brand.put("tagline", tpl.get("pitch"));
        }
        return brand;
    }

    public static List<PageSeed> pages(String id) {
        Map<String, Object> tpl = def(id);
        String layout = String.valueOf(tpl.get("heroLayout"));
        String hero = String.valueOf(tpl.get("heroImage"));
        List<PageSeed> list = new ArrayList<>(homePages(normalize(id), layout, hero));
        list.addAll(sharedPages(layout, hero));
        return list;
    }

    public record PageSeed(String slug, String type, String enTitle, String zhTitle, List<?> enBlocks, List<?> zhBlocks) {
    }

    private static List<PageSeed> homePages(String id, String layout, String hero) {
        return switch (id) {
            case "blueprint" -> List.of(new PageSeed("home", "home",
                    "Fuel dispenser specs | ZhengHe", "加油机参数 | 正和机械",
                    List.of(
                            trust("±0.25%", "5–80 L/min", "110/220/380V", "GPRS option"),
                            hero(layout, hero, "Specify voltage, nozzles and protocol first",
                                    "Honesty / Intelligent / Elite island units. Henan OEM, 17 years.",
                                    "Request datasheet", "/inquiry"),
                            solutions("Series map", solutionsEn()),
                            products("Core island models"),
                            factory("Factory capability", ABOUT_EN),
                            faq(faqEn()),
                            cta("Send voltage, hose count and destination port", "Talk to Cathy")
                    ),
                    List.of(
                            trust("±0.25%", "5–80 L/min", "110/220/380V", "可选 GPRS"),
                            hero(layout, hero, "先确认电压、枪数与协议",
                                    "诚信 / 智能 / 精英岛式机。河南 OEM，17 年。",
                                    "索取样册", "/inquiry"),
                            solutions("系列对照", solutionsZh()),
                            products("主力岛式机"),
                            factory("工厂能力", ABOUT_ZH),
                            faq(faqZh()),
                            cta("请提供电压、枪数与目的港", "联系 Cathy")
                    )));
            case "catalog" -> List.of(new PageSeed("home", "home",
                    "Fuel dispenser catalog | ZhengHe", "加油机目录 | 正和机械",
                    List.of(
                            hero(layout, hero, "Browse island dispensers and mini stations",
                                    "Clean catalog for importers comparing Honesty to Prestige-V.",
                                    "View products", "/products"),
                            products("Featured models"),
                            solutions("Shop by use case", solutionsEn()),
                            blog("Buying notes"),
                            factory("Who we are", ABOUT_EN),
                            cta("Need a quotation pack?", "Get a Quote")
                    ),
                    List.of(
                            hero(layout, hero, "浏览岛式加油机与微型站",
                                    "目录型站点，方便进口商对比诚信到尊享-V。",
                                    "查看商品", "/products"),
                            products("精选机型"),
                            solutions("按场景选购", solutionsZh()),
                            blog("采购说明"),
                            factory("厂家介绍", ABOUT_ZH),
                            cta("需要报价包？", "获取报价")
                    )));
            case "midnight" -> List.of(new PageSeed("home", "home",
                    "ZhengHe OEM fuel dispensers", "正和 OEM 加油机",
                    List.of(
                            hero(layout, hero, "OEM fascia. Export voltage. Quiet accuracy.",
                                    "Aurora and Brilliance series for branded station networks.",
                                    "Start OEM brief", "/inquiry"),
                            products("Flagship series"),
                            factory("Henan manufacturer", ABOUT_EN),
                            faq(faqEn()),
                            cta("Logo, color and protocol can be frozen on a sample", "Contact Cathy")
                    ),
                    List.of(
                            hero(layout, hero, "OEM 面板 · 出口电压 · 稳定计量",
                                    "极光与辉煌系列，面向品牌油站网络。",
                                    "提交 OEM 需求", "/inquiry"),
                            products("旗舰系列"),
                            factory("河南厂家", ABOUT_ZH),
                            faq(faqZh()),
                            cta("Logo、颜色与协议可在样机上锁定", "联系 Cathy")
                    )));
            case "signal" -> List.of(new PageSeed("home", "home",
                    "Mining & depot fueling | ZhengHe", "矿山与车场加油 | 正和机械",
                    List.of(
                            hero(layout, hero, "Compact pumps and 300–6000L mini stations",
                                    "Honesty 1–2 nozzle units and Prestige-V for mines, yards and camps.",
                                    "Get a Quote", "/inquiry"),
                            trust("1–2 nozzles", "300–6000L", "DC / AC", "ATG / GPRS"),
                            products("Depot and mining models"),
                            cta("Tell us tank size, fuel type and site power", "Email Cathy"),
                            solutions("Where it works", solutionsEn()),
                            faq(faqEn())
                    ),
                    List.of(
                            hero(layout, hero, "紧凑加油机与 300–6000L 微型站",
                                    "诚信 1–2 枪与尊享-V，服务矿山、车场与营地。",
                                    "获取报价", "/inquiry"),
                            trust("1–2 枪", "300–6000L", "DC / AC", "液位 / GPRS"),
                            products("车场与矿山机型"),
                            cta("请说明罐容、油品与现场电源", "联系 Cathy"),
                            solutions("适用场景", solutionsZh()),
                            faq(faqZh())
                    )));
            default -> List.of(new PageSeed("home", "home",
                    "ZhengHe | Fuel Dispenser Manufacturer", "正和机械 | 加油机厂家",
                    List.of(
                            hero(layout, hero, "Fuel dispensers for gas stations, mines and fleets",
                                    "Huixian Zhenghe Machinery · 17 years · 1–6 nozzles · optional GPRS · 110/220/380V",
                                    "Get a Quote", "/inquiry"),
                            trust("17 years", "1–6 nozzles", "Optional GPRS", "110/220/380V"),
                            products("Honesty · Intelligent · Elite · Aurora · Prestige-V"),
                            solutions("What we supply", solutionsEn()),
                            factory("About ZhengHe", ABOUT_EN),
                            faq(faqEn()),
                            blog("Fuel dispenser buying notes"),
                            cta("Need 2–6 nozzle 220V/380V units or a 3000L mini station?", "Email Cathy")
                    ),
                    List.of(
                            hero(layout, hero, "加油站、矿山与车队加油设备",
                                    "辉县正和机械 · 17 年经验 · 1–6 枪 · 可选 GPRS · 110/220/380V",
                                    "获取报价", "/inquiry"),
                            trust("17 年经验", "1–6 枪可选", "可选 GPRS", "110/220/380V"),
                            products("诚信 · 智能 · 精英 · 极光 · 尊享-V"),
                            solutions("主要产品", solutionsZh()),
                            factory("关于正和", ABOUT_ZH),
                            faq(faqZh()),
                            blog("加油机采购说明"),
                            cta("需要 2–6 枪 220V/380V 整机或 3000L 微型站？", "联系 Cathy")
                    )));
        };
    }

    private static List<PageSeed> sharedPages(String layout, String hero) {
        return List.of(
                new PageSeed("about", "about", "About ZhengHe Machinery", "关于正和机械",
                        List.of(
                                hero(layout, img("about.jpg"), "17 years in petroleum equipment", ABOUT_EN, "Contact Us", "/contact"),
                                rich("<p>" + ABOUT_EN + "</p><p>Factory: 50 Meters West Of Hanying Village, Zhaogu Township, Xinxiang, Henan. Cathy@machineryzh.com · +86 18567535165.</p>"),
                                factory("Xinxiang, Henan OEM factory", "Fuel dispensers, mining units, LPG dispensers and parts.")
                        ),
                        List.of(
                                hero(layout, img("about.jpg"), "深耕石油设备 17 年", ABOUT_ZH, "联系我们", "/contact"),
                                rich("<p>" + ABOUT_ZH + "</p><p>工厂：河南省新乡市赵固乡韩营村西 50 米。Cathy@machineryzh.com · +86 18567535165。</p>"),
                                factory("河南新乡 OEM 工厂", "加油机、矿用机、LPG 加气机与配件。")
                        )),
                new PageSeed("factory", "factory", "Factory & Capability", "工厂与产能",
                        List.of(
                                hero(layout, img("about.jpg"), "Henan manufacturer for global stations", "", "Get a Quote", "/inquiry"),
                                factory("From island dispensers to mini stations",
                                        "Honesty compact pumps, Aurora/Brilliance multi-nozzle islands, Prestige 300–6000L stations.")
                        ),
                        List.of(
                                hero(layout, img("about.jpg"), "面向全球加油站的河南厂家", "", "获取报价", "/inquiry"),
                                factory("从岛式机到微型站", "紧凑诚信系列、极光/辉煌多枪机、尊享 300–6000L 微型站。")
                        )),
                new PageSeed("certificates", "certificates", "Certificates", "资质证书",
                        List.of(Map.of("type", "certificates", "props", Map.of("heading", "Quality & explosion-proof options",
                                "items", List.of("OEM / ODM", "110 / 220 / 380V", "Ex-proof motor", "GPRS option")))),
                        List.of(Map.of("type", "certificates", "props", Map.of("heading", "品质与防爆配置",
                                "items", List.of("OEM / ODM", "110 / 220 / 380V", "防爆电机", "可选 GPRS"))))),
                new PageSeed("faq", "faq", "FAQ", "常见问题",
                        List.of(faq(faqEn())), List.of(faq(faqZh()))),
                new PageSeed("contact", "contact", "Contact Us", "联系我们",
                        List.of(
                                hero(layout, img("station.png"), "Talk to Cathy", "+86 18567535165 · Cathy@machineryzh.com · Xinxiang, Henan", "Send inquiry", "/inquiry"),
                                Map.of("type", "inquiryForm", "props", Map.of("title", "Tell us voltage, nozzle count, fuel type and destination port"))
                        ),
                        List.of(
                                hero(layout, img("station.png"), "联系 Cathy", "+86 18567535165 · Cathy@machineryzh.com · 河南新乡", "提交询盘", "/inquiry"),
                                Map.of("type", "inquiryForm", "props", Map.of("title", "请告知电压、枪数、油品与目的港"))
                        )),
                new PageSeed("solutions", "solutions", "Solutions", "解决方案",
                        List.of(solutions("Equipment for stations, mines and fleets", solutionsEn())),
                        List.of(solutions("面向加油站、矿山与车队", solutionsZh())))
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
                Map.of("slug", "fuel-dispensers", "title", "Fuel dispensers", "text", "Honesty to Brilliance island pumps, 1–8 nozzles."),
                Map.of("slug", "gas-station-equipment", "title", "Gas station equipment", "text", "110/220/380V packages for daily station service."),
                Map.of("slug", "mining", "title", "Mining & mobile", "text", "Honesty compact units and Prestige mini stations."),
                Map.of("slug", "parts", "title", "Nozzles & parts", "text", "11A automatic nozzles, solenoid valves and motors.")
        );
    }

    private static List<Map<String, String>> solutionsZh() {
        return List.of(
                Map.of("slug", "fuel-dispensers", "title", "加油机", "text", "诚信至辉煌岛式机，1–8 枪可选。"),
                Map.of("slug", "gas-station-equipment", "title", "加油站设备", "text", "110/220/380V 整机，适合长期运营。"),
                Map.of("slug", "mining", "title", "矿山与移动加注", "text", "紧凑诚信系列与尊享微型站。"),
                Map.of("slug", "parts", "title", "油枪与配件", "text", "11A 自封油枪、电磁阀与电机。")
        );
    }

    private static List<Map<String, String>> faqEn() {
        return List.of(
                Map.of("q", "How many nozzles can I order?", "a", "Island series typically 1–6 nozzles (Aurora/Brilliance up to 8). Mini stations support 1–3 products."),
                Map.of("q", "Can GPRS be added?", "a", "Yes. Intelligent, Clever, Aurora, Brilliance and Prestige series can add GPRS."),
                Map.of("q", "Which voltages are available?", "a", "110V, 220V and 380V, 50/60Hz."),
                Map.of("q", "How do I contact the factory?", "a", "Cathy@machineryzh.com or +86 18567535165.")
        );
    }

    private static List<Map<String, String>> faqZh() {
        return List.of(
                Map.of("q", "最多几枪？", "a", "岛式机常见 1–6 枪（极光/辉煌可达 8 枪）。微型站支持 1–3 个油品。"),
                Map.of("q", "能否加装 GPRS？", "a", "可以。智能、灵巧、极光、辉煌与尊享系列均可选 GPRS。"),
                Map.of("q", "电压有哪些？", "a", "110V、220V、380V，50/60Hz。"),
                Map.of("q", "如何联系工厂？", "a", "Cathy@machineryzh.com 或 +86 18567535165。")
        );
    }

    private static boolean isBlank(Object value) {
        return value == null || String.valueOf(value).isBlank();
    }
}

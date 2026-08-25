package com.tradehub.seed;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.Category;
import com.tradehub.catalog.CategoryI18n;
import com.tradehub.catalog.CategoryI18nMapper;
import com.tradehub.catalog.CategoryMapper;
import com.tradehub.catalog.Product;
import com.tradehub.catalog.ProductI18n;
import com.tradehub.catalog.ProductI18nMapper;
import com.tradehub.catalog.ProductMapper;
import com.tradehub.catalog.ProductSite;
import com.tradehub.catalog.ProductSiteMapper;
import com.tradehub.cms.Article;
import com.tradehub.cms.ArticleI18n;
import com.tradehub.cms.ArticleI18nMapper;
import com.tradehub.cms.ArticleMapper;
import com.tradehub.cms.CmsPage;
import com.tradehub.cms.CmsPageI18n;
import com.tradehub.cms.CmsPageI18nMapper;
import com.tradehub.cms.CmsPageMapper;
import com.tradehub.common.Jsons;
import com.tradehub.iam.UserAccount;
import com.tradehub.iam.UserAccountMapper;
import com.tradehub.inquiry.Inquiry;
import com.tradehub.inquiry.InquiryMapper;
import com.tradehub.seo.Redirect;
import com.tradehub.seo.RedirectMapper;
import com.tradehub.tenant.Domain;
import com.tradehub.tenant.DomainMapper;
import com.tradehub.tenant.Site;
import com.tradehub.tenant.SiteMapper;
import com.tradehub.tenant.Tenant;
import com.tradehub.tenant.TenantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {
    @Value("${tradehub.upload.public-base:http://localhost:8080/files}")
    private String publicBase;

    private static final String ABOUT_EN = "Zhenghe Machinery Equipment Co., Ltd., a professional Chinese manufacturer with 17-year experience, produces and wholesales fuel dispensers, mining fuel dispensers, LPG dispensers, gas station management systems and related parts. Committed to being a modernized, computerized leader in petroleum equipment, our innovative and strong team solves unsolved problems in the field. Welcome to cooperate!";
    private static final String ABOUT_ZH = "辉县市正和机械设备有限公司是专业的中国制造商，拥有 17 年行业经验，生产并批发加油机、矿用加油机、LPG 加气机、加油站管理系统及相关配件。公司致力于成为石油设备领域现代化、信息化的领先企业，欢迎合作。";

    private final UserAccountMapper userMapper;
    private final TenantMapper tenantMapper;
    private final SiteMapper siteMapper;
    private final DomainMapper domainMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryI18nMapper categoryI18nMapper;
    private final ProductMapper productMapper;
    private final ProductI18nMapper productI18nMapper;
    private final ProductSiteMapper productSiteMapper;
    private final CmsPageMapper pageMapper;
    private final CmsPageI18nMapper pageI18nMapper;
    private final ArticleMapper articleMapper;
    private final ArticleI18nMapper articleI18nMapper;
    private final InquiryMapper inquiryMapper;
    private final RedirectMapper redirectMapper;
    private final PasswordEncoder passwordEncoder;

    private String file(String name) {
        String base = publicBase == null ? "http://localhost:8080/files" : publicBase;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/demo/" + name;
    }

    @Override
    public void run(String... args) {
        Long users = userMapper.selectCount(new LambdaQueryWrapper<UserAccount>());
        if (users != null && users > 0) {
            return;
        }
        Tenant tenant = tenant("fueltech", "Huixian Zhenghe Machinery Equipment Co., Ltd.");
        Site site = site(tenant);
        UserAccount superAdmin = user(null, "admin", "admin123", "Platform Admin", "SUPER", 1);
        user(tenant.getId(), "fueltech", "fueltech123", "ZhengHe Owner", "OWNER", 0);
        log.info("Seeded demo users: admin/admin123 , fueltech/fueltech123 (superAdminId={})", superAdmin.getId());

        Category dispensers = category(tenant, "fuel-dispensers", 1, "Fuel Dispensers", "加油机整机",
                "Honesty, Intelligent, Clever, Elite, Aurora and Brilliance series for petrol stations.",
                "诚信、智能、灵巧、精英、极光、辉煌系列加油站加油机。");
        Category mini = category(tenant, "mini-stations", 2, "Mini Gas Stations", "微型加油站",
                "Prestige-H / Prestige-V compact stations, 300L to 6000L tanks.",
                "尊享卧式/立式微型站，罐容 300L–6000L。");
        Category parts = category(tenant, "nozzles-accessories", 3, "Nozzles & Parts", "油枪与配件",
                "11A automatic nozzles, solenoid valves and dispenser motors.",
                "11A 自封油枪、电磁阀与加油机电机。");

        Map<String, Object> island = Map.of(
                "flow_rate", "5-50 / 5-80 L/min",
                "accuracy", "±0.25%",
                "medium", "Gasoline / Diesel / Kerosene",
                "voltage", "110V / 220V / 380V (50/60Hz)",
                "temperature", "-25°C to +55°C",
                "meter", "4-piston soft type",
                "pump", "Vane / Gear / Submersible",
                "motor", "750W or 1100W",
                "gprs", "Optional",
                "origin", "Henan, China"
        );

        Product honesty = product(tenant, site, dispensers, "honesty-series", "ZH-H", true, file("honesty.png"), island,
                "Honesty Series Fuel Dispenser", "诚信系列加油机",
                "Compact 1–2 nozzle unit for public transport, mining and fleet yards.",
                "1–2 枪紧凑机型，适合公交、矿山与车队油库。",
                contentEn("Honesty series",
                        "This fuel dispenser works for diesel and gasoline. It is comparable to Wayne-style island pumps and is ideal for gas stations as well as compact sites.",
                        "The compact Honesty model is the best choice for public transportation, mining and logistics. Two flow rates (50 and 80 L/min) cover most customers. High-quality electronic and mechanical parts still perform in harsh environments.",
                        "Typical 1-hose package: 1× Ex-proof motor, 1× vane pump, 1× flow meter, 1× LCD, 1× motherboard, 1× totalizer. Power AC220V 50Hz, 5–50 L/min, 136 kg, 3/4\" nozzle with 4 m hose, 700×500×1500 mm.",
                        "Models: ZH-H1111B/G/T (1 nozzle) and ZH-H2222B/G/T (2 nozzles). Flow 5–50 or 5–80 L/min."),
                contentZh("诚信系列",
                        "适用于柴油与汽油，可作为加油站岛式加油机或紧凑加注设备。",
                        "紧凑机型适合公交、矿山与运输车队。50/80 L/min 两种流量，电子与机械部件可在恶劣环境工作。",
                        "单枪配置示例：防爆电机、叶片泵、流量计、液晶屏、主板、累加器。AC220V，5–50 L/min，136 kg，3/4\" 油枪配 4 米胶管，尺寸 700×500×1500 mm。",
                        "型号 ZH-H1111B/G/T（1 枪）、ZH-H2222B/G/T（2 枪）。"));

        product(tenant, site, dispensers, "intelligent-series", "ZH-I", true, file("intelligent.jpg"), island,
                "Intelligent Series Fuel Dispenser", "智能系列加油机",
                "Reliable island dispenser with accurate output and optional GPRS remote monitoring.",
                "计量稳定，可加装 GPRS 远程监控的岛式加油机。",
                contentEn("Intelligent series",
                        "The Intelligent series delivers reliable performance, long service life and accurate oil output. Operation is straightforward for daily station staff.",
                        "Optional GPRS adds remote monitoring for smarter fuel supply.",
                        "2-hose example: 2× Ex-proof motors, 2× Tokheim pumps, 2× Tokheim meters, 2× solenoid valves, 4× LCD, 2× keypads. Hose/nozzle 3/4\", 5–50 L/min, 2.5 kW, AC220V 50Hz, 280 kg, 1020×440×1650 mm.",
                        "Models: ZH-I1111B/G/T and ZH-I2222B/G/T."),
                contentZh("智能系列",
                        "性能可靠、寿命长、出油准确，日常操作简便。",
                        "可选 GPRS，便于远程监控与智能供油。",
                        "双枪示例：2 台防爆电机、Tokheim 泵与流量计、电磁阀、4 块液晶、2 个键盘。3/4\" 油枪，5–50 L/min，2.5 kW，280 kg，1020×440×1650 mm。",
                        "型号 ZH-I1111B/G/T、ZH-I2222B/G/T。"));

        product(tenant, site, dispensers, "clever-series", "ZH-C", false, file("clever.jpg"), island,
                "Clever Series Fuel Dispenser", "灵巧系列加油机",
                "Durable gasoline/diesel dispenser with logo customization and optional GPRS.",
                "汽油/柴油加油机，可定制 Logo，可选 GPRS。",
                contentEn("Clever series",
                        "Built for heavy daily use at gas stations. Accurate fuel output, user-friendly controls, customizable fascia logo.",
                        "Optional GPRS for remote management. Suitable for gasoline and diesel.",
                        "2-hose example: 3/4\" or 1\" nozzle, diesel 5–80 L/min, gasoline 5–50 L/min, 2.2 kW, AC220V 50Hz, about 200 kg, 1010×500×2150 mm.",
                        "Models: ZH-C1111B/G/T and ZH-C2222B/G/T."),
                contentZh("灵巧系列",
                        "面向加油站高强度使用，出油准确，操作简便，面板 Logo 可定制。",
                        "可选 GPRS 远程管理，适用汽油与柴油。",
                        "双枪示例：3/4\" 或 1\" 油枪，柴油 5–80 L/min，汽油 5–50 L/min，2.2 kW，约 200 kg，1010×500×2150 mm。",
                        "型号 ZH-C1111B/G/T、ZH-C2222B/G/T。"));

        product(tenant, site, dispensers, "elite-series", "ZH-E", true, file("elite.jpg"), island,
                "Elite Series Fuel Dispenser", "精英系列加油机",
                "Station dispenser with OEM logo and Bennett / Tatsuno / Tokheim options.",
                "可定制 Logo，可选 Bennett、Tatsuno 或 Tokheim 配置。",
                contentEn("Elite series",
                        "Ideal for gas stations that need a branded fascia. Voltage 110V / 220V / 380V.",
                        "Optional Bennett, Tatsuno or Tokheim pumps and meters to match existing station standards.",
                        "2-hose example: 2× Ex-proof motors, Tokheim pump and meter, solenoid valves, 4× LCD, 2× keypads. Nozzle 3/4\" & 1\", diesel 5–80 L/min, gasoline 5–50 L/min, 2.2 kW, AC220V, 260 kg, 1010×500×2150 mm.",
                        "Models: ZH-E1111B/G/T and ZH-E2222B/G/T."),
                contentZh("精英系列",
                        "适合需要品牌面板的加油站，电压 110/220/380V。",
                        "可选 Bennett、Tatsuno 或 Tokheim 泵与流量计，便于对接现有站标准。",
                        "双枪示例：防爆电机、Tokheim 泵与表、电磁阀、4 块液晶。油枪 3/4\" 与 1\"，柴油 5–80 L/min，汽油 5–50 L/min，2.2 kW，260 kg，1010×500×2150 mm。",
                        "型号 ZH-E1111B/G/T、ZH-E2222B/G/T。"));

        product(tenant, site, dispensers, "aurora-series", "ZH-A", true, file("aurora.jpg"), island,
                "Aurora Series Fuel Dispenser", "极光系列加油机",
                "Elegant 2–8 nozzle island dispenser with optional GPRS.",
                "外观典雅的 2–8 枪岛式机，可选 GPRS。",
                contentEn("Aurora series",
                        "Blends an elegant appearance with firm stability. Flexible 1–8 nozzle layouts save investment for new stations.",
                        "More than ten electronic base configurations. Logo and style can be customized. Optional GPRS.",
                        "2-hose example: 2× 220V Ex-proof motors, 2× Tatsuno pumps, 4× Tatsuno meters, 4× 886 LCD, 2× motherboard, 4× totalizer. AC220V 60Hz, 5–50 L/min, 385 kg, 3/4\" × 4 m hose, 1050×650×2390 mm.",
                        "Models: ZH-A212 / A222 / A424 / A636 / A848 (B/G/T)."),
                contentZh("极光系列",
                        "外观典雅、结构稳固，1–8 枪灵活布局，适合新站控制投资。",
                        "十余种电子基础配置，Logo 与造型可定制，可选 GPRS。",
                        "双枪示例：220V 防爆电机、Tatsuno 泵与流量计、886 液晶。AC220V，5–50 L/min，385 kg，1050×650×2390 mm。",
                        "型号 ZH-A212 / A222 / A424 / A636 / A848（B/G/T）。"));

        product(tenant, site, dispensers, "brilliance-series", "ZH-B", false, file("brilliance.jpg"), island,
                "Brilliance Series Fuel Dispenser", "辉煌系列加油机",
                "4–8 nozzle high-capacity dispenser with optional GPRS.",
                "4–8 枪大容量加油机，可选 GPRS。",
                contentEn("Brilliance series",
                        "Accurate oil output and long service life for busy gas stations. Choose 4–8 nozzles.",
                        "GPRS can be added for monitoring.",
                        "6-hose / 3-product example: 3× 220V Ex-proof motors, 3× Tatsuno pumps, 6× Tatsuno meters, 6× 886 LCD, 3× motherboard, 6× totalizer. AC220V 50Hz, 5–50 L/min, 425 kg, 3/4\" × 4 m hose, 1320×560×2150 mm.",
                        "Models: ZH-B212 / B222 / B424 / B636 / B848 (B/G/T)."),
                contentZh("辉煌系列",
                        "出油准确、寿命长，适合繁忙加油站，4–8 枪可选。",
                        "可加装 GPRS 监控。",
                        "6 枪 3 油品示例：3 台防爆电机、Tatsuno 泵与表、6 块 886 液晶。AC220V，5–50 L/min，425 kg，1320×560×2150 mm。",
                        "型号 ZH-B212 / B222 / B424 / B636 / B848（B/G/T）。"));

        Map<String, Object> miniSpec = Map.of(
                "tank", "300L–6000L",
                "flow_rate", "5–60 L/min",
                "accuracy", "±0.3%",
                "medium", "Diesel / Gasoline / Kerosene",
                "nozzle", "Automatic 3/4\" / 13/16\" / 1\"",
                "pump", "Gear / Vane / Tokheim",
                "voltage", "DC12/24V or AC110/220/380V",
                "oem", "OEM / ODM",
                "options", "ATG, GPRS, solar",
                "origin", "Henan, China"
        );

        product(tenant, site, mini, "prestige-v-series", "ZH-VP", true, file("prestige-v.jpg"), miniSpec,
                "Prestige-V Vertical Mini Gas Station", "尊享-V 立式微型加油站",
                "Stand-up 1800–2100 mm mobile station, 300L–6000L tank, 1–3 products, OEM/ODM.",
                "站立加油高度 1800–2100 mm，罐容 300–6000L，1–3 油品，支持 OEM/ODM。",
                contentEn("Prestige-V series",
                        "Vertical mini fuel station with OEM/ODM customization. Tank 500–6000L, 1–3 fuel products, controller, LCD, keypad, auto nozzle, ATG, optional GPRS/solar.",
                        "Motors: DC12V/24V or AC110V/220V/380V. Bennett-type flow meter. Height 1800–2100 mm so operators can stand and refuel. Widely used in China, Pakistan and similar markets.",
                        "3-hose example: 3× nozzle, Ex-proof motor, vane pump, meter, solenoid, LCD, metal keypad, totalizer, manhole. 3/4\" hose, 5–40 L/min, AC220V 60Hz, 2250W, 1350×1800×1800 mm, 650 kg.",
                        "Models: ZH-VP300L / 500L / 1000L / 1500L / 2000L / 3000L / 6000L."),
                contentZh("尊享-V 系列",
                        "立式微型加油站，支持 OEM/ODM。罐容 500–6000L，1–3 油品，带控制器、液晶、键盘、自封枪、液位，可选 GPRS/太阳能。",
                        "电机 DC12/24V 或 AC110/220/380V。整机高度 1800–2100 mm，可站立加油。适用于矿山、工地与出口市场。",
                        "三枪示例：3/4\" 油枪，5–40 L/min，AC220V，2250W，1350×1800×1800 mm，650 kg。",
                        "型号 ZH-VP300L 至 VP6000L。"));

        product(tenant, site, mini, "prestige-h-series", "ZH-HP", false, file("prestige-h.jpg"), miniSpec,
                "Prestige-H Horizontal Mini Gas Station", "尊享-H 卧式微型加油站",
                "Low-profile horizontal station, customizable logo, 300L–6000L tanks.",
                "卧式低重心微型站，Logo 可定制，罐容 300–6000L。",
                contentEn("Prestige-H series",
                        "Horizontal compact fuel station. Quality accessories at a practical price. Logo and style can be customized.",
                        "Same tank range as Prestige-V: 300L to 6000L, gear/vane/Tokheim pump, automatic nozzles.",
                        "2-hose example: 2× Ex-proof motor, vane pump, meter, solenoid, LCD, metal keypad, ATG, totalizer. 3/4\" hose, 5–40 L/min, AC220V, 0.6 kW, 1000×1500×1100 mm, 330 kg.",
                        "OEM/ODM available for tank size and branding."),
                contentZh("尊享-H 系列",
                        "卧式紧凑加油站，配件性价比高，Logo 与造型可定制。",
                        "罐容同样覆盖 300–6000L，泵可选齿轮/叶片/Tokheim，自封油枪。",
                        "双枪示例：3/4\" 胶管，5–40 L/min，AC220V，0.6 kW，1000×1500×1100 mm，330 kg。",
                        "罐容与品牌支持 OEM/ODM。"));

        product(tenant, site, parts, "fuel-nozzle-11a", "11A", false, file("nozzle.png"),
                Map.of("size", "3/4\"", "type", "Automatic shut-off", "medium", "Gasoline / Diesel"),
                "3/4\" 11A Fuel Dispenser Nozzle", "3/4\" 11A 加油机油枪",
                "Precision automatic nozzle for accurate output, durable sealing and easy handling.",
                "计量准确、密封可靠、握持省力的自封油枪。",
                "<h2>11A automatic nozzle</h2><p>Precision-engineered for accurate oil output. Durable construction, ergonomic grip, reliable sealing — essential for gas-station refueling.</p><p>Fits Honesty, Intelligent, Elite and mini-station packages. 3/4\" thread, gasoline and diesel.</p>",
                "<h2>11A 自封油枪</h2><p>出油准确、结构耐用、握持省力、密封可靠，是加油站加注的基础配件。</p><p>可配诚信/智能/精英系列及微型站。3/4\" 接口，汽油与柴油。</p>");

        seedPages(tenant, site);
        seedArticles(tenant, site);

        Inquiry inq = new Inquiry();
        inq.setTenantId(tenant.getId());
        inq.setSiteId(site.getId());
        inq.setLocale("en");
        inq.setProductId(honesty.getId());
        inq.setProductName("Honesty series ZH-H2222");
        inq.setName("Imran Khan");
        inq.setCompany("Punjab Fuel Retail");
        inq.setEmail("imran@example.com");
        inq.setPhone("+92 300 0000000");
        inq.setWhatsapp("+92 300 0000000");
        inq.setCountry("Pakistan");
        inq.setQuantity("8 units");
        inq.setMessage("Need 2-nozzle Honesty dispensers, 220V 50Hz, diesel + gasoline, OEM logo. Also quote Prestige-V 3000L with ATG and GPRS for a mining site.");
        inq.setStatus("new");
        inquiryMapper.insert(inq);

        Redirect redirect = new Redirect();
        redirect.setTenantId(tenant.getId());
        redirect.setSiteId(site.getId());
        redirect.setFromPath("/old-pump");
        redirect.setToPath("/products/honesty-series");
        redirect.setCode(301);
        redirectMapper.insert(redirect);
    }

    private Tenant tenant(String code, String name) {
        Tenant t = new Tenant();
        t.setCode(code);
        t.setName(name);
        t.setStatus(1);
        t.setContactEmail("Cathy@machineryzh.com");
        t.setPackageCode("standard");
        tenantMapper.insert(t);
        return t;
    }

    private Site site(Tenant tenant) {
        Site site = new Site();
        site.setTenantId(tenant.getId());
        site.setCode("fueltech");
        site.setName("ZhengHe Machinery");
        site.setDefaultLocale("en");
        site.setLocales("en,zh");
        site.setTheme("industrial");
        site.setStatus("live");
        site.setBrandJson(Jsons.toJson(Map.ofEntries(
                Map.entry("logoText", "ZhengHe"),
                Map.entry("tagline", "Fuel dispensers, mining units, LPG dispensers and station equipment"),
                Map.entry("primaryColor", "#0b1f3a"),
                Map.entry("accentColor", "#c2410c"),
                Map.entry("email", "Cathy@machineryzh.com"),
                Map.entry("phone", "+86 18567535165"),
                Map.entry("whatsapp", "+8618567535165"),
                Map.entry("address", "50 Meters West Of Hanying Village, Zhaogu Township, Xinxiang City, Henan Province, China"),
                Map.entry("founded", "2009"),
                Map.entry("countries", "Global"),
                Map.entry("heroImage", file("about.jpg"))
        )));
        site.setSeoJson(Jsons.toJson(Map.of(
                "title", "Fuel Dispenser Manufacturer | Huixian Zhenghe Machinery",
                "description", "17-year OEM factory in Henan: fuel dispensers, mining dispensers, LPG dispensers, mini gas stations and parts. 1–6 nozzles, optional GPRS, 110/220/380V.",
                "ogImage", file("about.jpg")
        )));
        siteMapper.insert(site);
        Domain d1 = new Domain();
        d1.setTenantId(tenant.getId());
        d1.setSiteId(site.getId());
        d1.setHost("localhost");
        d1.setIsPrimary(1);
        domainMapper.insert(d1);
        Domain d2 = new Domain();
        d2.setTenantId(tenant.getId());
        d2.setSiteId(site.getId());
        d2.setHost("zhenghe.local");
        d2.setIsPrimary(0);
        domainMapper.insert(d2);
        return site;
    }

    private UserAccount user(Long tenantId, String username, String password, String name, String role, int superAdmin) {
        UserAccount u = new UserAccount();
        u.setTenantId(tenantId);
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setDisplayName(name);
        u.setEmail(username.equals("fueltech") ? "Cathy@machineryzh.com" : username + "@tradehub.local");
        u.setRoleCode(role);
        u.setStatus(1);
        u.setIsSuperAdmin(superAdmin);
        userMapper.insert(u);
        return u;
    }

    private Category category(Tenant tenant, String slug, int sort, String en, String zh, String enDesc, String zhDesc) {
        Category c = new Category();
        c.setTenantId(tenant.getId());
        c.setParentId(0L);
        c.setSlug(slug);
        c.setSortOrder(sort);
        c.setStatus("live");
        categoryMapper.insert(c);
        i18nCat(c, "en", en, enDesc);
        i18nCat(c, "zh", zh, zhDesc);
        return c;
    }

    private void i18nCat(Category c, String locale, String name, String desc) {
        CategoryI18n row = new CategoryI18n();
        row.setTenantId(c.getTenantId());
        row.setCategoryId(c.getId());
        row.setLocale(locale);
        row.setName(name);
        row.setDescription(desc);
        row.setSeoTitle(name + " | ZhengHe Machinery");
        row.setSeoDescription(desc);
        categoryI18nMapper.insert(row);
    }

    private Product product(Tenant tenant, Site site, Category cat, String slug, String model, boolean featured,
                            String cover, Map<String, Object> attrs, String enName, String zhName,
                            String enSum, String zhSum, String enHtml, String zhHtml) {
        Product p = new Product();
        p.setTenantId(tenant.getId());
        p.setCategoryId(cat.getId());
        p.setSlug(slug);
        p.setModel(model);
        p.setCoverUrl(cover);
        p.setGalleryJson(Jsons.toJson(List.of(cover, file("station.png"), file("advantage.png"))));
        p.setAttrJson(Jsons.toJson(attrs));
        p.setStatus("live");
        p.setFeatured(featured ? 1 : 0);
        p.setSortOrder(featured ? 1 : 10);
        p.setPublishedAt(LocalDateTime.now());
        productMapper.insert(p);
        i18nProd(p, "en", slug, enName, enSum, enHtml);
        i18nProd(p, "zh", slug, zhName, zhSum, zhHtml);
        ProductSite rel = new ProductSite();
        rel.setTenantId(tenant.getId());
        rel.setProductId(p.getId());
        rel.setSiteId(site.getId());
        rel.setVisible(1);
        rel.setSortOrder(p.getSortOrder());
        productSiteMapper.insert(rel);
        return p;
    }

    private void i18nProd(Product p, String locale, String slug, String name, String sum, String html) {
        ProductI18n row = new ProductI18n();
        row.setTenantId(p.getTenantId());
        row.setProductId(p.getId());
        row.setLocale(locale);
        row.setSlug(slug);
        row.setName(name);
        row.setSummary(sum);
        row.setContent(html);
        row.setSeoTitle(name + " | ZhengHe Machinery");
        row.setSeoDescription(sum);
        productI18nMapper.insert(row);
    }

    private void seedPages(Tenant tenant, Site site) {
        page(tenant, site, "home", "home",
                "ZhengHe | Fuel Dispenser Manufacturer", "正和机械 | 加油机厂家",
                homeBlocksEn(), homeBlocksZh());
        page(tenant, site, "about", "about", "About ZhengHe Machinery", "关于正和机械", List.of(
                Map.of("type", "hero", "props", Map.of("heading", "17 years in petroleum equipment", "subtitle", ABOUT_EN, "image", file("about.jpg"), "cta", "Contact Us", "ctaTo", "/contact")),
                Map.of("type", "richText", "props", Map.of("html", "<p>" + ABOUT_EN + "</p><p>Factory address: 50 Meters West Of Hanying Village, Zhaogu Township, Xinxiang City, Henan Province, China. Export contact Cathy@machineryzh.com · +86 18567535165 (phone / WhatsApp / WeChat).</p>")),
                Map.of("type", "factory", "props", Map.of("heading", "Xinxiang, Henan OEM factory", "image", file("advantage.png"), "text", "Fuel dispensers, mining dispensers, LPG dispensers, station management systems and spare parts from one manufacturer."))
        ), List.of(
                Map.of("type", "hero", "props", Map.of("heading", "深耕石油设备 17 年", "subtitle", ABOUT_ZH, "image", file("about.jpg"), "cta", "联系我们", "ctaTo", "/contact")),
                Map.of("type", "richText", "props", Map.of("html", "<p>" + ABOUT_ZH + "</p><p>工厂地址：河南省新乡市赵固乡韩营村西 50 米。出口联系 Cathy@machineryzh.com · +86 18567535165（电话 / WhatsApp / 微信）。</p>")),
                Map.of("type", "factory", "props", Map.of("heading", "河南新乡 OEM 工厂", "image", file("advantage.png"), "text", "加油机、矿用加油机、LPG 加气机、加油站管理系统与配件，厂家直供。"))
        ));
        page(tenant, site, "factory", "factory", "Factory & Capability", "工厂与产能", List.of(
                Map.of("type", "hero", "props", Map.of("heading", "Henan manufacturer for global stations", "image", file("about.jpg"))),
                Map.of("type", "factory", "props", Map.of("heading", "From island dispensers to mini stations", "image", file("advantage.png"),
                        "text", "Honesty compact pumps for mining, Aurora/Brilliance multi-nozzle islands, Prestige 300–6000L mini stations, plus nozzles, motors and solenoid valves.")),
                Map.of("type", "richText", "props", Map.of("html", "<p>Place of origin: Henan, China. Voltage options 110V / 220V / 380V. OEM logo and GPRS available on most series.</p>"))
        ), List.of(
                Map.of("type", "hero", "props", Map.of("heading", "面向全球加油站的河南厂家", "image", file("about.jpg"))),
                Map.of("type", "factory", "props", Map.of("heading", "从岛式机到微型站", "image", file("advantage.png"),
                        "text", "矿山用诚信系列、极光/辉煌多枪岛式机、尊享 300–6000L 微型站，以及油枪、电机与电磁阀。")),
                Map.of("type", "richText", "props", Map.of("html", "<p>产地河南。电压 110/220/380V。多数系列支持 OEM Logo 与 GPRS。</p>"))
        ));
        page(tenant, site, "certificates", "certificates", "Certificates", "资质证书", List.of(
                Map.of("type", "certificates", "props", Map.of("heading", "Quality & explosion-proof options", "items", List.of("OEM / ODM", "110 / 220 / 380V", "Ex-proof motor", "GPRS option")))
        ), List.of(
                Map.of("type", "certificates", "props", Map.of("heading", "品质与防爆配置", "items", List.of("OEM / ODM", "110 / 220 / 380V", "防爆电机", "可选 GPRS")))
        ));
        page(tenant, site, "faq", "faq", "FAQ", "常见问题", List.of(Map.of("type", "faq", "props", Map.of("items", faqEn()))),
                List.of(Map.of("type", "faq", "props", Map.of("items", faqZh()))));
        page(tenant, site, "contact", "contact", "Contact Us", "联系我们", List.of(
                Map.of("type", "hero", "props", Map.of("heading", "Talk to Cathy", "subtitle", "+86 18567535165 · Cathy@machineryzh.com · Xinxiang, Henan", "image", file("station.png"), "cta", "Send inquiry", "ctaTo", "/inquiry")),
                Map.of("type", "inquiryForm", "props", Map.of("title", "Tell us voltage, nozzle count, fuel type and destination port"))
        ), List.of(
                Map.of("type", "hero", "props", Map.of("heading", "联系 Cathy", "subtitle", "+86 18567535165 · Cathy@machineryzh.com · 河南新乡", "image", file("station.png"), "cta", "提交询盘", "ctaTo", "/inquiry")),
                Map.of("type", "inquiryForm", "props", Map.of("title", "请告知电压、枪数、油品与目的港"))
        ));
        page(tenant, site, "solutions", "solutions", "Solutions", "解决方案", List.of(
                Map.of("type", "solutions", "props", Map.of("heading", "Equipment for stations, mines and fleets", "items", solutionsEn()))
        ), List.of(
                Map.of("type", "solutions", "props", Map.of("heading", "面向加油站、矿山与车队", "items", solutionsZh()))
        ));
    }

    private void page(Tenant tenant, Site site, String slug, String type, String enTitle, String zhTitle, List<?> enBlocks, List<?> zhBlocks) {
        CmsPage page = new CmsPage();
        page.setTenantId(tenant.getId());
        page.setSiteId(site.getId());
        page.setSlug(slug);
        page.setPageType(type);
        page.setStatus("live");
        pageMapper.insert(page);
        savePageI18n(tenant, page, "en", enTitle, enBlocks);
        savePageI18n(tenant, page, "zh", zhTitle, zhBlocks);
    }

    private void savePageI18n(Tenant tenant, CmsPage page, String locale, String title, List<?> blocks) {
        CmsPageI18n row = new CmsPageI18n();
        row.setTenantId(tenant.getId());
        row.setPageId(page.getId());
        row.setLocale(locale);
        row.setTitle(title);
        row.setSeoTitle(title);
        row.setSeoDescription(locale.startsWith("zh") ? ABOUT_ZH : ABOUT_EN);
        row.setBlocksJson(Jsons.toJson(blocks));
        pageI18nMapper.insert(row);
    }

    private List<?> homeBlocksEn() {
        return List.of(
                Map.of("type", "hero", "props", Map.of(
                        "heading", "Fuel dispensers for gas stations, mines and fleets",
                        "subtitle", "Huixian Zhenghe Machinery · 17 years · 1–6 nozzles · optional GPRS · 110/220/380V",
                        "cta", "Get a Quote",
                        "ctaTo", "/inquiry",
                        "layout", "split",
                        "image", file("aurora.jpg")
                )),
                Map.of("type", "trustBar", "props", Map.of("items", List.of("17 years", "1–6 nozzles", "Optional GPRS", "110/220/380V"))),
                Map.of("type", "productGrid", "props", Map.of("source", "featured", "heading", "Honesty · Intelligent · Elite · Aurora · Prestige-V")),
                Map.of("type", "solutions", "props", Map.of("heading", "What we supply", "items", solutionsEn())),
                Map.of("type", "factory", "props", Map.of("heading", "About ZhengHe", "image", file("advantage.png"), "text", ABOUT_EN)),
                Map.of("type", "faq", "props", Map.of("items", faqEn())),
                Map.of("type", "blogTeaser", "props", Map.of("heading", "Fuel dispenser buying notes")),
                Map.of("type", "cta", "props", Map.of("heading", "Need 2–6 nozzle 220V/380V units or a 3000L mini station?", "cta", "Email Cathy", "ctaTo", "/inquiry"))
        );
    }

    private List<?> homeBlocksZh() {
        return List.of(
                Map.of("type", "hero", "props", Map.of(
                        "heading", "加油站、矿山与车队加油设备",
                        "subtitle", "辉县正和机械 · 17 年经验 · 1–6 枪 · 可选 GPRS · 110/220/380V",
                        "cta", "获取报价",
                        "ctaTo", "/inquiry",
                        "layout", "split",
                        "image", file("aurora.jpg")
                )),
                Map.of("type", "trustBar", "props", Map.of("items", List.of("17 年经验", "1–6 枪可选", "可选 GPRS", "110/220/380V"))),
                Map.of("type", "productGrid", "props", Map.of("source", "featured", "heading", "诚信 · 智能 · 精英 · 极光 · 尊享-V")),
                Map.of("type", "solutions", "props", Map.of("heading", "主要产品", "items", solutionsZh())),
                Map.of("type", "factory", "props", Map.of("heading", "关于正和", "image", file("advantage.png"), "text", ABOUT_ZH)),
                Map.of("type", "faq", "props", Map.of("items", faqZh())),
                Map.of("type", "blogTeaser", "props", Map.of("heading", "加油机采购说明")),
                Map.of("type", "cta", "props", Map.of("heading", "需要 2–6 枪 220V/380V 整机或 3000L 微型站？", "cta", "联系 Cathy", "ctaTo", "/inquiry"))
        );
    }

    private List<Map<String, String>> faqEn() {
        return List.of(
                Map.of("q", "How many nozzles can I order?", "a", "Island series typically 1–6 nozzles (Aurora/Brilliance up to 8). Mini stations support 1–3 products."),
                Map.of("q", "Can GPRS be added?", "a", "Yes. Intelligent, Clever, Aurora, Brilliance and Prestige series can add GPRS for remote monitoring."),
                Map.of("q", "Which voltages are available?", "a", "110V, 220V and 380V, 50/60Hz. Confirm the local standard in your inquiry."),
                Map.of("q", "Do you support OEM logo?", "a", "Yes. Elite, Clever, Aurora and Prestige series support custom logo and styling."),
                Map.of("q", "How do I contact the factory?", "a", "Cathy@machineryzh.com or +86 18567535165 (phone, WhatsApp, WeChat). Address: Zhaogu Township, Xinxiang, Henan, China.")
        );
    }

    private List<Map<String, String>> faqZh() {
        return List.of(
                Map.of("q", "最多几枪？", "a", "岛式机常见 1–6 枪（极光/辉煌可达 8 枪）。微型站支持 1–3 个油品。"),
                Map.of("q", "能否加装 GPRS？", "a", "可以。智能、灵巧、极光、辉煌与尊享系列均可选 GPRS 远程监控。"),
                Map.of("q", "电压有哪些？", "a", "110V、220V、380V，50/60Hz。询盘时请说明当地标准。"),
                Map.of("q", "能否做 OEM Logo？", "a", "可以。精英、灵巧、极光与尊享系列支持定制标识与造型。"),
                Map.of("q", "如何联系工厂？", "a", "Cathy@machineryzh.com 或 +86 18567535165（电话、WhatsApp、微信）。地址：河南省新乡市赵固乡。")
        );
    }

    private List<Map<String, String>> solutionsEn() {
        return List.of(
                Map.of("slug", "fuel-dispensers", "title", "Fuel dispensers", "text", "Honesty to Brilliance island pumps with advanced metering and 1–8 nozzles."),
                Map.of("slug", "gas-station-equipment", "title", "Gas station equipment", "text", "110/220/380V packages that blend into station design and last in daily service."),
                Map.of("slug", "mining", "title", "Mining & mobile", "text", "Compact Honesty units and Prestige mini stations for mines, yards and construction."),
                Map.of("slug", "parts", "title", "Nozzles & parts", "text", "11A automatic nozzles, solenoid valves, flow meters and dispenser motors.")
        );
    }

    private List<Map<String, String>> solutionsZh() {
        return List.of(
                Map.of("slug", "fuel-dispensers", "title", "加油机", "text", "诚信至辉煌岛式机，计量稳定，1–8 枪可选。"),
                Map.of("slug", "gas-station-equipment", "title", "加油站设备", "text", "110/220/380V 整机方案，外形贴合站房，适合长期运营。"),
                Map.of("slug", "mining", "title", "矿山与移动加注", "text", "紧凑诚信系列与尊享微型站，服务矿山、车场与工地。"),
                Map.of("slug", "parts", "title", "油枪与配件", "text", "11A 自封油枪、电磁阀、流量计与加油机电机。")
        );
    }

    private void seedArticles(Tenant tenant, Site site) {
        article(tenant, site, "choose-nozzle-count-fuel-dispenser", file("aurora.jpg"),
                "How to Choose 1–6 Nozzle Fuel Dispensers", "加油机如何选 1–6 枪",
                "Match Honesty compact pumps to Aurora multi-hose islands using flow rate and peak traffic.",
                "按流量与高峰车流，在诚信紧凑机与极光多枪岛式机之间选型。",
                "<p>Huixian Zhenghe island dispensers share the same metering core: 5–50 or 5–80 L/min, ±0.25% accuracy, gasoline/diesel/kerosene, 110/220/380V.</p>"
                        + "<p>Use <strong>Honesty (ZH-H)</strong> when the site is a mine, bus depot or single-island yard — 1 or 2 nozzles, 700×500×1500 mm. Choose <strong>Intelligent / Elite</strong> for a standard petrol station that may add GPRS or Bennett/Tatsuno/Tokheim hydraulics. Choose <strong>Aurora / Brilliance</strong> when you need 4–8 hoses and a more elegant fascia.</p>"
                        + "<p>Tell Cathy the voltage, hose count, fuel types and whether you need GPRS or OEM logo.</p>",
                "<p>正和岛式机计量平台一致：5–50 或 5–80 L/min，精度 ±0.25%，汽柴油煤油，110/220/380V。</p>"
                        + "<p>矿山、车场用<strong>诚信 ZH-H</strong>（1–2 枪）。常规加油站用<strong>智能/精英</strong>（可加 GPRS 或 Bennett/Tatsuno/Tokheim）。4–8 枪、对外观要求高则选<strong>极光/辉煌</strong>。</p>"
                        + "<p>询盘请说明电压、枪数、油品，以及是否需要 GPRS 与 OEM Logo。</p>");
        article(tenant, site, "mini-gas-station-3000l-6000l", file("prestige-v.jpg"),
                "Mini Gas Stations 300L–6000L for Mines and Yards", "矿山与车场用 300–6000L 微型加油站",
                "Prestige-V stands 1800–2100 mm tall; Prestige-H is the low horizontal pack. Both support OEM/ODM, ATG and GPRS.",
                "尊享-V 高度 1800–2100 mm，尊享-H 为卧式。均可 OEM/ODM，可选液位与 GPRS。",
                "<p>The Prestige series is a compact fuel station, not only a dispenser: tank + pump + meter + automatic nozzle. Tank sizes: 300 / 500 / 1000 / 1500 / 2000 / 3000 / 4000 / 6000 L.</p>"
                        + "<p>Prestige-V (ZH-VP) is vertical so staff can stand and refuel. Prestige-H (ZH-HP) is horizontal, about 1100 mm high. Medium: diesel, gasoline or kerosene. Flow 5–60 L/min, accuracy ±0.3%. Motors from DC12/24V to AC380V, optional solar.</p>"
                        + "<p>These units are used in China, Pakistan and similar markets where a full island is not needed.</p>",
                "<p>尊享系列是罐+泵+表+自封枪的微型站，罐容 300 至 6000L。</p>"
                        + "<p>尊享-V 立式可站立加油；尊享-H 卧式约 1100 mm 高。介质汽柴油煤油，流量 5–60 L/min，精度 ±0.3%。电机从 DC12/24V 到 AC380V，可选太阳能。</p>"
                        + "<p>适合尚不需要完整岛式机的矿山、工地与出口市场。</p>");
        article(tenant, site, "gprs-fuel-dispenser-remote-monitoring", file("intelligent.jpg"),
                "Why Add GPRS to a Fuel Dispenser", "加油机为什么要加 GPRS",
                "Remote monitoring on Intelligent, Aurora, Brilliance and Prestige series for station and fleet owners.",
                "智能、极光、辉煌与尊享系列可加 GPRS，方便油站与车队远程看数。",
                "<p>ZhengHe’s advantage list is practical: elegant appearance, 1–6 nozzles, optional GPRS, accurate output and long service life.</p>"
                        + "<p>GPRS lets an owner see volume and alarms without standing at the island — useful for mining camps and unmanned yards. Ask for GPRS when you order Intelligent, Clever, Aurora, Brilliance or Prestige units.</p>"
                        + "<p>Contact: Cathy@machineryzh.com · +86 18567535165.</p>",
                "<p>正和的卖点很具体：外形、1–6 枪、可选 GPRS、出油准、寿命长。</p>"
                        + "<p>GPRS 让业主不用守在机旁也能看走字与报警，适合矿区与无人值守车场。下单智能、灵巧、极光、辉煌或尊享时可注明加装。</p>"
                        + "<p>联系 Cathy@machineryzh.com · +86 18567535165。</p>");
    }

    private void article(Tenant tenant, Site site, String slug, String cover, String enTitle, String zhTitle,
                         String enSum, String zhSum, String enHtml, String zhHtml) {
        Article a = new Article();
        a.setTenantId(tenant.getId());
        a.setSiteId(site.getId());
        a.setSlug(slug);
        a.setCoverUrl(cover);
        a.setStatus("live");
        a.setPublishedAt(LocalDateTime.now());
        articleMapper.insert(a);
        saveArt(tenant, a, "en", slug, enTitle, enSum, enHtml);
        saveArt(tenant, a, "zh", slug, zhTitle, zhSum, zhHtml);
    }

    private void saveArt(Tenant tenant, Article a, String locale, String slug, String title, String sum, String html) {
        ArticleI18n row = new ArticleI18n();
        row.setTenantId(tenant.getId());
        row.setArticleId(a.getId());
        row.setLocale(locale);
        row.setSlug(slug);
        row.setTitle(title);
        row.setSummary(sum);
        row.setContent(html);
        row.setSeoTitle(title + " | ZhengHe Machinery");
        row.setSeoDescription(sum);
        articleI18nMapper.insert(row);
    }

    private String contentEn(String name, String overview, String points, String pack, String models) {
        return "<h2>" + name + "</h2><p>" + overview + "</p><p>" + points + "</p>"
                + "<h2>Typical package</h2><p>" + pack + "</p><h2>Configuration</h2><p>" + models + "</p>"
                + "<p>Shared specs: flow 5–50 or 5–80 L/min, accuracy ±0.25%, noise ≤80 dB(A), intake vacuum &gt;54 kPa, two-stage filter, 4-piston meter, -25°C to +55°C, humidity &lt;95%. Place of origin: Henan, China.</p>";
    }

    private String contentZh(String name, String overview, String points, String pack, String models) {
        return "<h2>" + name + "</h2><p>" + overview + "</p><p>" + points + "</p>"
                + "<h2>典型配置</h2><p>" + pack + "</p><h2>型号</h2><p>" + models + "</p>"
                + "<p>共用参数：流量 5–50 或 5–80 L/min，精度 ±0.25%，噪声 ≤80 dB(A)，吸程 &gt;54 kPa，两级过滤，四活塞流量计，-25℃～+55℃。产地河南。</p>";
    }
}

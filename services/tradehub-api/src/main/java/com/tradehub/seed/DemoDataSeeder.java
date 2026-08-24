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
    private static final String HERO = "https://images.unsplash.com/photo-1545558014-8692077e9b5c?auto=format&fit=crop&w=1600&q=80";
    private static final String FACTORY = "https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?auto=format&fit=crop&w=1200&q=80";
    private static final String PUMP = "https://images.unsplash.com/photo-1616432043562-3671ea2e5242?auto=format&fit=crop&w=1200&q=80";
    private static final String STATION = "https://images.unsplash.com/photo-1563897539633-7374c276c212?auto=format&fit=crop&w=1200&q=80";

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

    @Override
    public void run(String... args) {
        Long users = userMapper.selectCount(new LambdaQueryWrapper<UserAccount>());
        if (users != null && users > 0) {
            return;
        }
        Tenant tenant = tenant("fueltech", "FuelTech Machinery Co., Ltd.");
        Site site = site(tenant);
        UserAccount superAdmin = user(null, "admin", "admin123", "Platform Admin", "SUPER", 1);
        user(tenant.getId(), "fueltech", "fueltech123", "FuelTech Owner", "OWNER", 0);
        log.info("Seeded demo users: admin/admin123 , fueltech/fueltech123 (superAdminId={})", superAdmin.getId());

        Category dispensers = category(tenant, "fuel-dispensers", 1, "Fuel Dispensers", "加油机整机",
                "Island and skid fuel dispensers for petrol stations.", "加油站岛式与撬装加油机。");
        Category mobile = category(tenant, "mobile-skid", 2, "Mobile / Skid Units", "撬装与移动加注",
                "Containerized and trailer fueling units.", "撬装站与拖车式加油单元。");
        Category nozzles = category(tenant, "nozzles-accessories", 3, "Nozzles & Accessories", "油枪与配件",
                "Automatic nozzles, hoses and swivels.", "自封油枪、胶管与旋转接头。");
        Category meters = category(tenant, "flow-meters", 4, "Flow Meters & Pumps", "流量计与泵",
                "Positive displacement meters and pumping units.", "容积式流量计与泵组。");
        category(tenant, "spare-parts", 5, "Spare Parts", "备件",
                "Wear parts and maintenance kits.", "易损件与保养包。");

        Product t80 = product(tenant, site, dispensers, "t80-fuel-dispenser", "T80", true, PUMP,
                Map.of("flow_rate", "40-80 L/min", "accuracy", "±0.2%", "hose_count", "2/4", "product_types", "Gasoline / Diesel",
                        "explosion_proof", "Ex d", "voltage", "110/220/380V", "certification", "CE, ISO 9001", "oem", "Yes"),
                "T80 Fuel Dispenser 4 Nozzle", "T80 四枪加油机",
                "High-precision island dispenser for urban petrol stations.", "面向城区加油站的高精度岛式加油机。",
                t80ContentEn(), t80ContentZh());
        product(tenant, site, dispensers, "t120-fuel-dispenser", "T120", true, STATION,
                Map.of("flow_rate", "80-120 L/min", "accuracy", "±0.2%", "hose_count", "4", "product_types", "Gasoline / Diesel / AdBlue",
                        "explosion_proof", "Ex d", "voltage", "220/380V", "certification", "CE, ATEX option", "oem", "Yes"),
                "T120 High-Flow Fuel Dispenser", "T120 大流量加油机",
                "Four-hose high-flow unit for busy highways and fleets.", "适合高速路与车队的四枪大流量机型。",
                "<p>T120 is built for high throughput. Optional AdBlue hose and protocol ports for station POS.</p>",
                "<p>T120 面向高吞吐场景，可选 AdBlue 枪与后台协议接口。</p>");
        product(tenant, site, mobile, "m50-mobile-dispenser", "M50", true, FACTORY,
                Map.of("flow_rate", "50 L/min", "accuracy", "±0.3%", "hose_count", "1", "mounting", "Skid / Trailer",
                        "voltage", "12/24/220V", "oem", "Yes"),
                "M50 Mobile Fuel Dispenser", "M50 移动加油机",
                "Skid-mounted mobile fueling for construction and mining.", "工程与矿山用撬装移动加油设备。",
                "<p>M50 packs pump, meter and nozzle on a compact skid with optional solar panel.</p>",
                "<p>M50 将泵、流量计与油枪集成在紧凑撬座上，可选太阳能供电。</p>");
        product(tenant, site, nozzles, "n200-auto-nozzle", "N200", false, PUMP,
                Map.of("flow_rate", "60 L/min", "product_types", "Gasoline / Diesel"),
                "N200 Automatic Nozzle", "N200 自封油枪",
                "ZVA-style automatic shut-off nozzle.", "ZVA 风格自封油枪。",
                "<p>N200 automatic nozzle with replaceable spout and swivel.</p>",
                "<p>N200 自封油枪，枪管与旋转接头可更换。</p>");
        product(tenant, site, meters, "f60-flow-meter", "F60", false, FACTORY,
                Map.of("flow_rate", "60 L/min", "accuracy", "±0.2%"),
                "F60 Flow Meter", "F60 流量计",
                "PD meter for dispenser and tank truck.", "用于加油机与罐车的容积式流量计。",
                "<p>F60 positive displacement meter, cast aluminum housing.</p>",
                "<p>F60 容积式流量计，铸铝壳体。</p>");

        seedPages(tenant, site, t80);
        seedArticles(tenant, site);
        Inquiry inq = new Inquiry();
        inq.setTenantId(tenant.getId());
        inq.setSiteId(site.getId());
        inq.setLocale("en");
        inq.setProductId(t80.getId());
        inq.setProductName("T80");
        inq.setName("Ahmed Hassan");
        inq.setCompany("Gulf Station Group");
        inq.setEmail("ahmed@example.com");
        inq.setCountry("United Arab Emirates");
        inq.setQuantity("20 units");
        inq.setMessage("Need CE certificates and 380V T80 for Dubai stations. OEM panel logo required.");
        inq.setStatus("new");
        inquiryMapper.insert(inq);

        Redirect redirect = new Redirect();
        redirect.setTenantId(tenant.getId());
        redirect.setSiteId(site.getId());
        redirect.setFromPath("/old-pump");
        redirect.setToPath("/products/t80-fuel-dispenser");
        redirect.setCode(301);
        redirectMapper.insert(redirect);
    }

    private Tenant tenant(String code, String name) {
        Tenant t = new Tenant();
        t.setCode(code);
        t.setName(name);
        t.setStatus(1);
        t.setContactEmail("export@fueltech.example");
        t.setPackageCode("standard");
        tenantMapper.insert(t);
        return t;
    }

    private Site site(Tenant tenant) {
        Site site = new Site();
        site.setTenantId(tenant.getId());
        site.setCode("fueltech");
        site.setName("FuelTech Global");
        site.setDefaultLocale("en");
        site.setLocales("en,zh");
        site.setTheme("industrial-fuel");
        site.setStatus("live");
        site.setBrandJson(Jsons.toJson(Map.ofEntries(
                Map.entry("logoText", "FuelTech"),
                Map.entry("tagline", "High-Precision Fuel Dispensers for Global Stations"),
                Map.entry("primaryColor", "#0b1f3a"),
                Map.entry("accentColor", "#e85d04"),
                Map.entry("email", "export@fueltech.example"),
                Map.entry("phone", "+86-400-800-8800"),
                Map.entry("whatsapp", "+86-138-0000-0000"),
                Map.entry("address", "No.18 Machinery Park, Wenzhou, China"),
                Map.entry("founded", "2009"),
                Map.entry("countries", "80+"),
                Map.entry("heroImage", HERO)
        )));
        site.setSeoJson(Jsons.toJson(Map.of(
                "title", "Fuel Dispenser Manufacturer | CE & ISO | FuelTech",
                "description", "CE-certified fuel dispensers, mobile skids and nozzles. OEM factory exporting to 80+ countries.",
                "ogImage", HERO
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
        d2.setHost("fueltech.local");
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
        u.setEmail(username + "@tradehub.local");
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
        row.setSeoTitle(name + " | FuelTech");
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
        p.setGalleryJson(Jsons.toJson(List.of(cover, STATION, FACTORY)));
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
        row.setSeoTitle(name + " | FuelTech");
        row.setSeoDescription(sum);
        productI18nMapper.insert(row);
    }

    private void seedPages(Tenant tenant, Site site, Product t80) {
        page(tenant, site, "home", "home", "FuelTech | Fuel Dispenser Manufacturer", homeBlocks());
        page(tenant, site, "about", "about", "About FuelTech", List.of(
                Map.of("type", "hero", "props", Map.of("heading", "OEM factory since 2009", "subtitle", "Design, machining, calibration and export under one roof.", "image", FACTORY)),
                Map.of("type", "richText", "props", Map.of("html", "<p>FuelTech builds fuel dispensers for petrol stations, fleets and energy retailers. We run CNC, painting, meter calibration and explosion-proof assembly lines.</p>"))
        ));
        page(tenant, site, "factory", "factory", "Factory & Capability", List.of(
                Map.of("type", "hero", "props", Map.of("heading", "18,000 m² manufacturing campus", "image", FACTORY)),
                Map.of("type", "richText", "props", Map.of("html", "<p>Leak test, pulse calibration and 72-hour aging are standard before packing. Container loading photos available for each order.</p>"))
        ));
        page(tenant, site, "certificates", "certificates", "Certificates", List.of(
                Map.of("type", "certificates", "props", Map.of("items", List.of("CE", "ISO 9001", "ATEX option", "OIML-ready")))
        ));
        page(tenant, site, "faq", "faq", "FAQ", List.of(Map.of("type", "faq", "props", Map.of("items", faq()))));
        page(tenant, site, "contact", "contact", "Contact", List.of(
                Map.of("type", "inquiryForm", "props", Map.of("title", "Tell us your voltage, hose count and destination port"))
        ));
        page(tenant, site, "solutions", "solutions", "Solutions", List.of(
                Map.of("type", "solutions", "props", Map.of("items", solutions()))
        ));
    }

    private void page(Tenant tenant, Site site, String slug, String type, String title, List<?> blocks) {
        CmsPage page = new CmsPage();
        page.setTenantId(tenant.getId());
        page.setSiteId(site.getId());
        page.setSlug(slug);
        page.setPageType(type);
        page.setStatus("live");
        pageMapper.insert(page);
        for (String locale : List.of("en", "zh")) {
            CmsPageI18n row = new CmsPageI18n();
            row.setTenantId(tenant.getId());
            row.setPageId(page.getId());
            row.setLocale(locale);
            row.setTitle(title);
            row.setSeoTitle(title);
            row.setSeoDescription("FuelTech industrial fuel dispenser pages.");
            row.setBlocksJson(Jsons.toJson(blocks));
            pageI18nMapper.insert(row);
        }
    }

    private List<?> homeBlocks() {
        return List.of(
                Map.of("type", "hero", "props", Map.of(
                        "heading", "High-Precision Fuel Dispensers for Global Stations",
                        "subtitle", "OEM factory · CE / ISO · 80+ countries · 15 years",
                        "cta", "Get a Quote",
                        "ctaTo", "/inquiry",
                        "image", HERO
                )),
                Map.of("type", "trustBar", "props", Map.of("items", List.of("CE", "ISO 9001", "80+ countries", "15 years"))),
                Map.of("type", "productGrid", "props", Map.of("source", "featured", "heading", "Core models")),
                Map.of("type", "solutions", "props", Map.of("items", solutions())),
                Map.of("type", "factory", "props", Map.of("heading", "From machining to calibration", "image", FACTORY,
                        "text", "In-house CNC, painting, meter calibration and explosion-proof assembly.")),
                Map.of("type", "faq", "props", Map.of("items", faq())),
                Map.of("type", "cta", "props", Map.of("heading", "Need 4-nozzle 380V units for a new station?", "cta", "Talk to export team"))
        );
    }

    private List<Map<String, String>> faq() {
        return List.of(
                Map.of("q", "Do you support OEM brand panels?", "a", "Yes. Logo, color and protocol can be customized with MOQ."),
                Map.of("q", "Which voltages are available?", "a", "110V, 220V and 380V. Confirm local standard in the inquiry."),
                Map.of("q", "Can you ship with ATEX documents?", "a", "Selected models. Tell us the destination country when you RFQ.")
        );
    }

    private List<Map<String, String>> solutions() {
        return List.of(
                Map.of("slug", "gas-station", "title", "Petrol stations", "text", "Island dispensers with POS protocols."),
                Map.of("slug", "fleet", "title", "Fleet & mining", "text", "High-flow and skid units for depots."),
                Map.of("slug", "marine", "title", "Marine & remote", "text", "Mobile dispensers for docks and islands.")
        );
    }

    private void seedArticles(Tenant tenant, Site site) {
        article(tenant, site, "how-to-choose-a-fuel-dispenser", STATION,
                "How to Choose a Fuel Dispenser", "如何选型加油机",
                "Flow rate, hose count, accuracy and explosion-proof class explained.",
                "从流量、枪数、精度与防爆等级讲清选型。",
                "<p>Match flow rate to peak hour throughput. Urban stations often use 40-80 L/min; highway sites prefer 80-120 L/min. Confirm voltage, ATEX needs and whether AdBlue is required.</p><p>Ask the factory for calibration reports and spare parts list before signing.</p>",
                "<p>按高峰小时吞吐匹配流量。城区站常用 40-80L/min，高速路更适合 80-120L/min。确认电压、ATEX 与是否需要尿素枪。</p>");
        article(tenant, site, "oem-fuel-dispenser-checklist", FACTORY,
                "OEM Fuel Dispenser Checklist", "OEM 加油机对接清单",
                "Logo, protocol, packaging and spare kits.", "商标、协议、包装与备件包。",
                "<p>Share your brand artwork, target protocol (IFSF or custom) and destination plug standard. We freeze a sample before mass production.</p>",
                "<p>请提供品牌稿、目标协议与插头标准。量产前会锁定样机。</p>");
        article(tenant, site, "fuel-dispenser-maintenance", PUMP,
                "Fuel Dispenser Maintenance Basics", "加油机保养基础",
                "Filters, pulser and nozzle shut-off.", "过滤器、脉冲器与油枪自封。",
                "<p>Replace filters on schedule, keep the pulser dry and test automatic shut-off weekly. This protects meter accuracy and station uptime.</p>",
                "<p>定期更换过滤器，保持脉冲器干燥，每周测试自封功能，有助于精度与开机率。</p>");
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
        ArticleI18n en = new ArticleI18n();
        en.setTenantId(tenant.getId());
        en.setArticleId(a.getId());
        en.setLocale("en");
        en.setSlug(slug);
        en.setTitle(enTitle);
        en.setSummary(enSum);
        en.setContent(enHtml);
        en.setSeoTitle(enTitle + " | FuelTech");
        en.setSeoDescription(enSum);
        articleI18nMapper.insert(en);
        ArticleI18n zh = new ArticleI18n();
        zh.setTenantId(tenant.getId());
        zh.setArticleId(a.getId());
        zh.setLocale("zh");
        zh.setSlug(slug);
        zh.setTitle(zhTitle);
        zh.setSummary(zhSum);
        zh.setContent(zhHtml);
        zh.setSeoTitle(zhTitle + " | FuelTech");
        zh.setSeoDescription(zhSum);
        articleI18nMapper.insert(zh);
    }

    private String t80ContentEn() {
        return "<h2>Overview</h2><p>The T80 is a CE-certified island fuel dispenser designed for petrol stations that need stable ±0.2% accuracy and OEM branding.</p>"
                + "<h2>Applications</h2><p>Urban stations, dealer networks and fleet yards. Optional 2 or 4 hoses, gasoline and diesel.</p>"
                + "<h2>OEM</h2><p>Custom fascia, color and communication protocol. MOQ applies.</p>";
    }

    private String t80ContentZh() {
        return "<h2>概述</h2><p>T80 是面向加油站的 CE 认证岛式加油机，计量精度 ±0.2%，支持 OEM 面板。</p>"
                + "<h2>应用</h2><p>城区站、经销网络与车队油库。可选 2/4 枪，汽油与柴油。</p>";
    }
}

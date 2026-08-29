package com.tradehub.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.CatalogService;
import com.tradehub.catalog.Category;
import com.tradehub.catalog.CategoryMapper;
import com.tradehub.catalog.Product;
import com.tradehub.cms.Article;
import com.tradehub.cms.CmsPage;
import com.tradehub.cms.CmsService;
import com.tradehub.common.Jsons;
import com.tradehub.common.exception.BizException;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.inquiry.InquiryService;
import com.tradehub.seo.Redirect;
import com.tradehub.seo.RedirectMapper;
import com.tradehub.tenant.Site;
import com.tradehub.tenant.SiteMapper;
import com.tradehub.tenant.Tenant;
import com.tradehub.tenant.TenantMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorefrontService {
    private final TenantMapper tenantMapper;
    private final SiteMapper siteMapper;
    private final CategoryMapper categoryMapper;
    private final CatalogService catalogService;
    private final CmsService cmsService;
    private final InquiryService inquiryService;
    private final RedirectMapper redirectMapper;

    public Map<String, Object> context(Site site, String locale) {
        Tenant tenant = tenantMapper.selectById(site.getTenantId());
        Map<String, Object> data = new HashMap<>();
        data.put("site", Map.of(
                "id", site.getId(),
                "code", site.getCode(),
                "name", site.getName(),
                "theme", site.getTheme(),
                "defaultLocale", site.getDefaultLocale(),
                "locales", List.of(site.getLocales().split(",")),
                "status", site.getStatus()
        ));
        data.put("tenant", Map.of("name", tenant.getName(), "code", tenant.getCode()));
        data.put("brand", Jsons.map(site.getBrandJson()));
        data.put("seo", Jsons.map(site.getSeoJson()));
        data.put("locale", locale);
        data.put("nav", defaultNav());
        data.put("categories", categories(site, locale));
        return data;
    }

    public Map<String, Object> home(Site site, String locale) {
        Map<String, Object> data = context(site, locale);
        CmsPage page = cmsService.findPage(site.getTenantId(), site.getId(), "home");
        if (page != null) {
            data.put("page", cmsService.pageView(page, locale));
        }
        List<Map<String, Object>> featured = new ArrayList<>();
        for (Product product : catalogService.liveProducts(site.getTenantId(), site.getId())) {
            if (Integer.valueOf(1).equals(product.getFeatured())) {
                featured.add(catalogService.productView(product, locale, false));
            }
        }
        data.put("featuredProducts", featured);
        data.put("articles", articles(site, locale).stream().limit(3).toList());
        return data;
    }

    public List<Map<String, Object>> categories(Site site, String locale) {
        List<Category> cats = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getTenantId, site.getTenantId())
                .eq(Category::getStatus, "live")
                .orderByAsc(Category::getSortOrder));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category cat : cats) {
            var i18n = catalogService.pickCategoryI18n(cat.getId(), locale);
            Map<String, Object> map = new HashMap<>();
            map.put("id", cat.getId());
            map.put("slug", cat.getSlug());
            map.put("name", i18n == null ? cat.getSlug() : i18n.getName());
            map.put("description", i18n == null ? null : i18n.getDescription());
            map.put("seoTitle", i18n == null ? null : i18n.getSeoTitle());
            map.put("seoDescription", i18n == null ? null : i18n.getSeoDescription());
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> products(Site site, String locale, String categorySlug) {
        Map<String, Long> slugToId = new HashMap<>();
        for (Category cat : categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getTenantId, site.getTenantId()))) {
            slugToId.put(cat.getSlug(), cat.getId());
        }
        Long categoryId = StringUtils.hasText(categorySlug) ? slugToId.get(categorySlug) : null;
        List<Map<String, Object>> result = new ArrayList<>();
        for (Product product : catalogService.liveProducts(site.getTenantId(), site.getId())) {
            if (categoryId != null && !categoryId.equals(product.getCategoryId())) {
                continue;
            }
            result.add(catalogService.productView(product, locale, false));
        }
        return result;
    }

    public Map<String, Object> product(Site site, String locale, String slug) {
        for (Product product : catalogService.liveProducts(site.getTenantId(), site.getId())) {
            var view = catalogService.productView(product, locale, false);
            if (slug.equals(view.get("slug")) || slug.equals(product.getSlug())) {
                Map<String, Object> data = new HashMap<>(view);
                data.put("category", categories(site, locale).stream()
                        .filter(c -> product.getCategoryId() != null && product.getCategoryId().equals(c.get("id")))
                        .findFirst().orElse(null));
                List<Map<String, Object>> related = new ArrayList<>();
                for (Product other : catalogService.liveProducts(site.getTenantId(), site.getId())) {
                    if (other.getId().equals(product.getId())) {
                        continue;
                    }
                    if (product.getCategoryId() != null && product.getCategoryId().equals(other.getCategoryId())) {
                        related.add(catalogService.productView(other, locale, false));
                    }
                    if (related.size() >= 4) {
                        break;
                    }
                }
                data.put("related", related);
                return data;
            }
        }
        throw new BizException(404, "product not found");
    }

    public Map<String, Object> page(Site site, String locale, String slug) {
        CmsPage page = cmsService.findPage(site.getTenantId(), site.getId(), slug);
        if (page == null) {
            throw new BizException(404, "page not found");
        }
        return cmsService.pageView(page, locale);
    }

    public List<Map<String, Object>> articles(Site site, String locale) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Article article : cmsService.liveArticles(site.getTenantId(), site.getId())) {
            result.add(cmsService.articleView(article, locale));
        }
        return result;
    }

    public Map<String, Object> article(Site site, String locale, String slug) {
        return articles(site, locale).stream()
                .filter(a -> slug.equals(a.get("slug")))
                .findFirst()
                .orElseThrow(() -> new BizException(404, "article not found"));
    }

    public Map<String, Object> submitInquiry(InquiryService.InquiryCreateRequest req, HttpServletRequest http) {
        Site site = currentSite();
        return inquiryService.createFromStore(req, site, TenantContext.getLocale(), http);
    }

    public Map<String, Object> sitemap(Site site) {
        Map<String, Object> data = new HashMap<>();
        data.put("locales", List.of(site.getLocales().split(",")));
        data.put("pages", cmsService.livePages(site.getTenantId(), site.getId()).stream().map(CmsPage::getSlug).toList());
        data.put("products", products(site, site.getDefaultLocale(), null).stream().map(p -> p.get("slug")).toList());
        data.put("articles", articles(site, site.getDefaultLocale()).stream().map(a -> a.get("slug")).toList());
        data.put("categories", categories(site, site.getDefaultLocale()).stream().map(c -> c.get("slug")).toList());
        return data;
    }

    public List<Redirect> redirects(Site site) {
        return redirectMapper.selectList(new LambdaQueryWrapper<Redirect>()
                .eq(Redirect::getSiteId, site.getId()));
    }

    public List<Map<String, Object>> search(Site site, String locale, String q) {
        if (!StringUtils.hasText(q)) {
            return List.of();
        }
        String needle = q.toLowerCase();
        List<Map<String, Object>> hits = new ArrayList<>();
        for (var p : products(site, locale, null)) {
            String blob = ("" + p.get("name") + p.get("summary") + p.get("model")).toLowerCase();
            if (blob.contains(needle)) {
                p.put("type", "product");
                hits.add(p);
            }
        }
        for (var a : articles(site, locale)) {
            String blob = ("" + a.get("title") + a.get("summary")).toLowerCase();
            if (blob.contains(needle)) {
                a.put("type", "article");
                hits.add(a);
            }
        }
        return hits;
    }

    public Site currentSite() {
        Long siteId = TenantContext.getSiteId();
        if (siteId == null) {
            throw new BizException(404, "site not resolved");
        }
        Site site = siteMapper.selectById(siteId);
        if (site == null) {
            throw new BizException(404, "site not resolved");
        }
        return site;
    }

    private List<Map<String, String>> defaultNav() {
        return List.of(
                Map.of("label", "Home", "to", "/"),
                Map.of("label", "Products", "to", "/products"),
                Map.of("label", "Solutions", "to", "/solutions"),
                Map.of("label", "Cases", "to", "/projects"),
                Map.of("label", "About", "to", "/about"),
                Map.of("label", "Blog", "to", "/blog"),
                Map.of("label", "Contact", "to", "/contact")
        );
    }
}

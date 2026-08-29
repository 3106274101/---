package com.tradehub.seo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.CatalogService;
import com.tradehub.catalog.Product;
import com.tradehub.catalog.ProductMapper;
import com.tradehub.cms.CmsService;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.R;
import com.tradehub.tenant.Domain;
import com.tradehub.tenant.DomainMapper;
import com.tradehub.tenant.Site;
import com.tradehub.tenant.SiteMapper;
import com.tradehub.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/seo")
@RequiredArgsConstructor
public class SeoAdminController {
    private final RedirectMapper redirectMapper;
    private final TenantService tenantService;
    private final SiteMapper siteMapper;
    private final DomainMapper domainMapper;
    private final ProductMapper productMapper;
    private final CatalogService catalogService;
    private final CmsService cmsService;

    @GetMapping("/redirects")
    public R<?> redirects(@RequestParam Long siteId) {
        return R.ok(redirectMapper.selectList(new LambdaQueryWrapper<Redirect>()
                .eq(Redirect::getTenantId, tenantService.workingTenantId())
                .eq(Redirect::getSiteId, siteId)));
    }

    @PostMapping("/redirects")
    public R<?> save(@RequestBody Redirect body) {
        body.setTenantId(tenantService.workingTenantId());
        if (body.getCode() == null) {
            body.setCode(301);
        }
        if (body.getId() == null) {
            redirectMapper.insert(body);
        } else {
            redirectMapper.updateById(body);
        }
        return R.ok(body);
    }

    @GetMapping("/robots")
    public R<?> robots() {
        String txt = """
                User-agent: *
                Allow: /
                Disallow: /api/
                Disallow: /admin
                Sitemap: /sitemap.xml
                """;
        return R.ok(java.util.Map.of("content", txt));
    }

    @GetMapping("/health")
    public R<?> health(@RequestParam Long siteId) {
        Site site = siteMapper.selectById(siteId);
        java.util.List<java.util.Map<String, String>> issues = new java.util.ArrayList<>();
        if (site == null) {
            return R.ok(java.util.Map.of("score", 0, "issues", java.util.List.of(java.util.Map.of("level", "error", "text", "站点不存在"))));
        }
        var brand = Jsons.map(site.getBrandJson());
        if (!org.springframework.util.StringUtils.hasText(String.valueOf(brand.getOrDefault("ga4Id", "")))) {
            issues.add(java.util.Map.of("level", "warn", "text", "未配置 GA4，无法统计独立站询盘来源"));
        }
        var domains = domainMapper.selectList(new LambdaQueryWrapper<Domain>().eq(Domain::getSiteId, siteId));
        boolean custom = domains.stream().anyMatch(d -> d.getHost() != null && !d.getHost().endsWith(".local"));
        if (!custom) {
            issues.add(java.util.Map.of("level", "warn", "text", "还没有绑定真实域名"));
        }
        boolean homeLive = cmsService.livePages(site.getTenantId(), siteId).stream().anyMatch(p -> "home".equals(p.getSlug()));
        if (!homeLive) {
            issues.add(java.util.Map.of("level", "error", "text", "首页未发布，搜索引擎看不到主视觉"));
        }
        long noCover = catalogService.countMissingCover(site.getTenantId());
        if (noCover > 0) {
            issues.add(java.util.Map.of("level", "warn", "text", noCover + " 个商品没有封面图"));
        }
        long noSeo = 0;
        for (Product p : productMapper.selectList(new LambdaQueryWrapper<Product>().eq(Product::getTenantId, site.getTenantId()).eq(Product::getStatus, "live"))) {
            var view = catalogService.productView(p, site.getDefaultLocale(), false);
            if (!org.springframework.util.StringUtils.hasText(String.valueOf(view.getOrDefault("seoTitle", "")))) {
                noSeo++;
            }
        }
        if (noSeo > 0) {
            issues.add(java.util.Map.of("level", "warn", "text", noSeo + " 个已上架商品缺少 SEO Title"));
        }
        int score = Math.max(0, 100 - issues.size() * 15);
        return R.ok(java.util.Map.of("score", score, "issues", issues));
    }
}

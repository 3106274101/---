package com.tradehub.theme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.Product;
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
import com.tradehub.tenant.Site;
import com.tradehub.tenant.SiteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SiteTemplateService {
    private final SiteMapper siteMapper;
    private final CmsPageMapper pageMapper;
    private final CmsPageI18nMapper pageI18nMapper;
    private final ProductMapper productMapper;
    private final ProductSiteMapper productSiteMapper;
    private final ArticleMapper articleMapper;
    private final ArticleI18nMapper articleI18nMapper;

    public List<Map<String, Object>> list() {
        return SiteTemplateCatalog.list();
    }

    public void provision(Site site, String templateId) {
        String id = SiteTemplateCatalog.normalize(templateId);
        Map<String, Object> seed = Jsons.map(site.getBrandJson());
        Site sibling = siteMapper.selectOne(new LambdaQueryWrapper<Site>()
                .eq(Site::getTenantId, site.getTenantId())
                .ne(Site::getId, site.getId())
                .last("limit 1"));
        if (sibling != null && (seed == null || seed.isEmpty())) {
            seed = Jsons.map(sibling.getBrandJson());
        }
        Map<String, Object> brand = SiteTemplateCatalog.brand(id, seed);
        site.setTheme(id);
        site.setBrandJson(Jsons.toJson(brand));
        if (!StringUtils.hasText(site.getSeoJson())) {
            site.setSeoJson(Jsons.toJson(Map.of(
                    "title", site.getName() + " | " + SiteTemplateCatalog.def(id).get("name"),
                    "description", SiteTemplateCatalog.def(id).get("pitch"),
                    "ogImage", brand.get("heroImage")
            )));
        }
        siteMapper.updateById(site);
        seedPages(site, id);
        linkProducts(site);
        copyArticles(site, sibling);
    }

    private void seedPages(Site site, String templateId) {
        Long exists = pageMapper.selectCount(new LambdaQueryWrapper<CmsPage>()
                .eq(CmsPage::getSiteId, site.getId()));
        if (exists != null && exists > 0) {
            return;
        }
        for (SiteTemplateCatalog.PageSeed seed : SiteTemplateCatalog.pages(templateId)) {
            CmsPage page = new CmsPage();
            page.setTenantId(site.getTenantId());
            page.setSiteId(site.getId());
            page.setSlug(seed.slug());
            page.setPageType(seed.type());
            page.setStatus("live");
            pageMapper.insert(page);
            saveI18n(site, page, "en", seed.enTitle(), seed.enBlocks());
            saveI18n(site, page, "zh", seed.zhTitle(), seed.zhBlocks());
        }
    }

    private void saveI18n(Site site, CmsPage page, String locale, String title, List<?> blocks) {
        CmsPageI18n row = new CmsPageI18n();
        row.setTenantId(site.getTenantId());
        row.setPageId(page.getId());
        row.setLocale(locale);
        row.setTitle(title);
        row.setSeoTitle(title);
        row.setSeoDescription(String.valueOf(SiteTemplateCatalog.def(site.getTheme()).get("pitch")));
        row.setBlocksJson(Jsons.toJson(blocks));
        pageI18nMapper.insert(row);
    }

    private void linkProducts(Site site) {
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, site.getTenantId()));
        for (Product product : products) {
            Long n = productSiteMapper.selectCount(new LambdaQueryWrapper<ProductSite>()
                    .eq(ProductSite::getProductId, product.getId())
                    .eq(ProductSite::getSiteId, site.getId()));
            if (n != null && n > 0) {
                continue;
            }
            ProductSite rel = new ProductSite();
            rel.setTenantId(site.getTenantId());
            rel.setProductId(product.getId());
            rel.setSiteId(site.getId());
            rel.setVisible(1);
            rel.setSortOrder(product.getSortOrder() == null ? 0 : product.getSortOrder());
            productSiteMapper.insert(rel);
        }
    }

    private void copyArticles(Site site, Site sibling) {
        if (sibling == null) {
            return;
        }
        List<Article> source = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getTenantId, site.getTenantId())
                .eq(Article::getSiteId, sibling.getId()));
        for (Article src : source) {
            Article copy = new Article();
            copy.setTenantId(site.getTenantId());
            copy.setSiteId(site.getId());
            copy.setSlug(src.getSlug());
            copy.setCoverUrl(src.getCoverUrl());
            copy.setStatus(src.getStatus());
            copy.setPublishedAt(src.getPublishedAt());
            articleMapper.insert(copy);
            List<ArticleI18n> rows = articleI18nMapper.selectList(new LambdaQueryWrapper<ArticleI18n>()
                    .eq(ArticleI18n::getArticleId, src.getId()));
            for (ArticleI18n row : rows) {
                ArticleI18n i18n = new ArticleI18n();
                i18n.setTenantId(site.getTenantId());
                i18n.setArticleId(copy.getId());
                i18n.setLocale(row.getLocale());
                i18n.setSlug(row.getSlug());
                i18n.setTitle(row.getTitle());
                i18n.setSummary(row.getSummary());
                i18n.setContent(row.getContent());
                i18n.setSeoTitle(row.getSeoTitle());
                i18n.setSeoDescription(row.getSeoDescription());
                articleI18nMapper.insert(i18n);
            }
        }
    }
}

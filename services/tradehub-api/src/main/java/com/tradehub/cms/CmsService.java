package com.tradehub.cms;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.exception.BizException;
import com.tradehub.tenant.TenantService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CmsService {
    private final CmsPageMapper pageMapper;
    private final CmsPageI18nMapper pageI18nMapper;
    private final ArticleMapper articleMapper;
    private final ArticleI18nMapper articleI18nMapper;
    private final TenantService tenantService;

    public List<Map<String, Object>> listPages(Long siteId, String locale) {
        Long tenantId = tenantService.workingTenantId();
        List<CmsPage> pages = pageMapper.selectList(new LambdaQueryWrapper<CmsPage>()
                .eq(CmsPage::getTenantId, tenantId)
                .eq(siteId != null, CmsPage::getSiteId, siteId)
                .orderByDesc(CmsPage::getId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (CmsPage page : pages) {
            result.add(pageView(page, locale));
        }
        return result;
    }

    public Map<String, Object> getPage(Long id, String locale) {
        CmsPage page = pageMapper.selectById(id);
        if (page == null || !page.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "page not found");
        }
        return pageView(page, locale);
    }

    public Map<String, Object> savePage(PageSaveRequest req, String locale) {
        Long tenantId = tenantService.workingTenantId();
        CmsPage page = req.getId() == null ? new CmsPage() : pageMapper.selectById(req.getId());
        if (page == null) {
            throw new BizException(404, "page not found");
        }
        page.setTenantId(tenantId);
        page.setSiteId(req.getSiteId());
        page.setPageType(req.getPageType());
        page.setSlug(req.getSlug());
        page.setStatus(normalizePublishStatus(req.getStatus(), req.getScheduledAt()));
        page.setScheduledAt(req.getScheduledAt());
        if (page.getId() == null) {
            pageMapper.insert(page);
        } else {
            pageMapper.updateById(page);
        }
        CmsPageI18n row = pageI18nMapper.selectOne(new LambdaQueryWrapper<CmsPageI18n>()
                .eq(CmsPageI18n::getPageId, page.getId())
                .eq(CmsPageI18n::getLocale, locale));
        if (row == null) {
            row = new CmsPageI18n();
            row.setTenantId(tenantId);
            row.setPageId(page.getId());
            row.setLocale(locale);
        }
        row.setTitle(req.getTitle());
        row.setSeoTitle(req.getSeoTitle());
        row.setSeoDescription(req.getSeoDescription());
        row.setCanonical(req.getCanonical());
        row.setOgImage(req.getOgImage());
        row.setBlocksJson(Jsons.toJson(req.getBlocks()));
        if (row.getId() == null) {
            pageI18nMapper.insert(row);
        } else {
            pageI18nMapper.updateById(row);
        }
        return pageView(page, locale);
    }

    public List<Map<String, Object>> listArticles(Long siteId, String locale) {
        Long tenantId = tenantService.workingTenantId();
        List<Article> list = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getTenantId, tenantId)
                .eq(siteId != null, Article::getSiteId, siteId)
                .orderByDesc(Article::getPublishedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Article article : list) {
            result.add(articleView(article, locale));
        }
        return result;
    }

    public Map<String, Object> saveArticle(ArticleSaveRequest req, String locale) {
        Long tenantId = tenantService.workingTenantId();
        Article article = req.getId() == null ? new Article() : articleMapper.selectById(req.getId());
        if (article == null) {
            throw new BizException(404, "article not found");
        }
        article.setTenantId(tenantId);
        article.setSiteId(req.getSiteId());
        article.setSlug(req.getSlug());
        article.setCoverUrl(req.getCoverUrl());
        article.setStatus(normalizePublishStatus(req.getStatus(), req.getScheduledAt()));
        article.setScheduledAt(req.getScheduledAt());
        if ("live".equals(article.getStatus()) && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        if (article.getId() == null) {
            articleMapper.insert(article);
        } else {
            articleMapper.updateById(article);
        }
        ArticleI18n row = articleI18nMapper.selectOne(new LambdaQueryWrapper<ArticleI18n>()
                .eq(ArticleI18n::getArticleId, article.getId())
                .eq(ArticleI18n::getLocale, locale));
        if (row == null) {
            row = new ArticleI18n();
            row.setTenantId(tenantId);
            row.setArticleId(article.getId());
            row.setLocale(locale);
        }
        row.setSlug(req.getI18nSlug());
        row.setTitle(req.getTitle());
        row.setSummary(req.getSummary());
        row.setContent(req.getContent());
        row.setSeoTitle(req.getSeoTitle());
        row.setSeoDescription(req.getSeoDescription());
        if (row.getId() == null) {
            articleI18nMapper.insert(row);
        } else {
            articleI18nMapper.updateById(row);
        }
        return articleView(article, locale);
    }

    public Map<String, Object> duplicatePage(Long id, String locale) {
        CmsPage src = pageMapper.selectById(id);
        if (src == null || !src.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "page not found");
        }
        CmsPage copy = new CmsPage();
        copy.setTenantId(src.getTenantId());
        copy.setSiteId(src.getSiteId());
        copy.setPageType(src.getPageType());
        copy.setSlug(src.getSlug() + "-copy");
        copy.setStatus("draft");
        pageMapper.insert(copy);
        for (CmsPageI18n row : pageI18nMapper.selectList(new LambdaQueryWrapper<CmsPageI18n>().eq(CmsPageI18n::getPageId, src.getId()))) {
            CmsPageI18n n = new CmsPageI18n();
            n.setTenantId(row.getTenantId());
            n.setPageId(copy.getId());
            n.setLocale(row.getLocale());
            n.setTitle((row.getTitle() == null ? "" : row.getTitle()) + " (copy)");
            n.setSeoTitle(row.getSeoTitle());
            n.setSeoDescription(row.getSeoDescription());
            n.setCanonical(row.getCanonical());
            n.setOgImage(row.getOgImage());
            n.setBlocksJson(row.getBlocksJson());
            pageI18nMapper.insert(n);
        }
        return pageView(copy, locale);
    }

    public Map<String, Object> duplicateArticle(Long id, String locale) {
        Article src = articleMapper.selectById(id);
        if (src == null || !src.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "article not found");
        }
        Article copy = new Article();
        copy.setTenantId(src.getTenantId());
        copy.setSiteId(src.getSiteId());
        copy.setSlug(src.getSlug() + "-copy");
        copy.setCoverUrl(src.getCoverUrl());
        copy.setStatus("draft");
        articleMapper.insert(copy);
        for (ArticleI18n row : articleI18nMapper.selectList(new LambdaQueryWrapper<ArticleI18n>().eq(ArticleI18n::getArticleId, src.getId()))) {
            ArticleI18n n = new ArticleI18n();
            n.setTenantId(row.getTenantId());
            n.setArticleId(copy.getId());
            n.setLocale(row.getLocale());
            n.setSlug(row.getSlug() == null ? null : row.getSlug() + "-copy");
            n.setTitle((row.getTitle() == null ? "" : row.getTitle()) + " (copy)");
            n.setSummary(row.getSummary());
            n.setContent(row.getContent());
            n.setSeoTitle(row.getSeoTitle());
            n.setSeoDescription(row.getSeoDescription());
            articleI18nMapper.insert(n);
        }
        return articleView(copy, locale);
    }

    public Map<String, Object> pageView(CmsPage page, String locale) {
        CmsPageI18n i18n = pickPageI18n(page.getId(), locale);
        Map<String, Object> map = new HashMap<>();
        map.put("id", page.getId());
        map.put("siteId", page.getSiteId());
        map.put("pageType", page.getPageType());
        map.put("slug", page.getSlug());
        map.put("status", page.getStatus());
        map.put("scheduledAt", page.getScheduledAt());
        if (i18n != null) {
            map.put("title", i18n.getTitle());
            map.put("seoTitle", i18n.getSeoTitle());
            map.put("seoDescription", i18n.getSeoDescription());
            map.put("canonical", i18n.getCanonical());
            map.put("ogImage", i18n.getOgImage());
            map.put("blocks", Jsons.list(i18n.getBlocksJson()));
            map.put("locale", i18n.getLocale());
        }
        return map;
    }

    public CmsPageI18n pickPageI18n(Long pageId, String locale) {
        List<CmsPageI18n> list = pageI18nMapper.selectList(new LambdaQueryWrapper<CmsPageI18n>()
                .eq(CmsPageI18n::getPageId, pageId));
        if (list.isEmpty()) {
            return null;
        }
        String use = StringUtils.hasText(locale) ? locale : "en";
        return list.stream().filter(i -> use.equals(i.getLocale())).findFirst()
                .orElseGet(() -> list.stream().filter(i -> "en".equals(i.getLocale())).findFirst().orElse(list.get(0)));
    }

    public ArticleI18n pickArticleI18n(Long articleId, String locale) {
        List<ArticleI18n> list = articleI18nMapper.selectList(new LambdaQueryWrapper<ArticleI18n>()
                .eq(ArticleI18n::getArticleId, articleId));
        if (list.isEmpty()) {
            return null;
        }
        String use = StringUtils.hasText(locale) ? locale : "en";
        return list.stream().filter(i -> use.equals(i.getLocale())).findFirst()
                .orElseGet(() -> list.stream().filter(i -> "en".equals(i.getLocale())).findFirst().orElse(list.get(0)));
    }

    public Map<String, Object> articleView(Article article, String locale) {
        ArticleI18n i18n = pickArticleI18n(article.getId(), locale);
        Map<String, Object> map = new HashMap<>();
        map.put("id", article.getId());
        map.put("siteId", article.getSiteId());
        map.put("slug", i18n != null && StringUtils.hasText(i18n.getSlug()) ? i18n.getSlug() : article.getSlug());
        map.put("coverUrl", article.getCoverUrl());
        map.put("status", article.getStatus());
        map.put("publishedAt", article.getPublishedAt());
        map.put("scheduledAt", article.getScheduledAt());
        if (i18n != null) {
            map.put("title", i18n.getTitle());
            map.put("summary", i18n.getSummary());
            map.put("content", i18n.getContent());
            map.put("seoTitle", i18n.getSeoTitle());
            map.put("seoDescription", i18n.getSeoDescription());
            map.put("locale", i18n.getLocale());
        }
        return map;
    }

    public List<CmsPage> livePages(Long tenantId, Long siteId) {
        return pageMapper.selectList(new LambdaQueryWrapper<CmsPage>()
                .eq(CmsPage::getTenantId, tenantId)
                .eq(CmsPage::getSiteId, siteId)
                .eq(CmsPage::getStatus, "live"));
    }

    public List<Article> liveArticles(Long tenantId, Long siteId) {
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getTenantId, tenantId)
                .eq(Article::getSiteId, siteId)
                .eq(Article::getStatus, "live")
                .orderByDesc(Article::getPublishedAt));
    }

    public CmsPage findPage(Long tenantId, Long siteId, String slug) {
        return pageMapper.selectOne(new LambdaQueryWrapper<CmsPage>()
                .eq(CmsPage::getTenantId, tenantId)
                .eq(CmsPage::getSiteId, siteId)
                .eq(CmsPage::getSlug, slug)
                .eq(CmsPage::getStatus, "live")
                .last("limit 1"));
    }

    private String normalizePublishStatus(String status, LocalDateTime scheduledAt) {
        if (!StringUtils.hasText(status)) {
            return scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now()) ? "scheduled" : "draft";
        }
        if ("scheduled".equals(status) && scheduledAt != null && !scheduledAt.isAfter(LocalDateTime.now())) {
            return "live";
        }
        return status;
    }

    @Data
    public static class PageSaveRequest {
        private Long id;
        private Long siteId;
        private String pageType;
        private String slug;
        private String status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduledAt;
        private String title;
        private String seoTitle;
        private String seoDescription;
        private String canonical;
        private String ogImage;
        private List<Object> blocks;
    }

    @Data
    public static class ArticleSaveRequest {
        private Long id;
        private Long siteId;
        private String slug;
        private String i18nSlug;
        private String coverUrl;
        private String status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduledAt;
        private String title;
        private String summary;
        private String content;
        private String seoTitle;
        private String seoDescription;
    }
}

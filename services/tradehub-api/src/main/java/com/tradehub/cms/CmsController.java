package com.tradehub.cms;

import com.tradehub.common.api.R;
import com.tradehub.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class CmsController {
    private final CmsService cmsService;

    @GetMapping("/pages")
    public R<?> pages(@RequestParam(required = false) Long siteId) {
        return R.ok(cmsService.listPages(siteId, TenantContext.getLocale()));
    }

    @GetMapping("/pages/{id}")
    public R<?> page(@PathVariable Long id) {
        return R.ok(cmsService.getPage(id, TenantContext.getLocale()));
    }

    @PostMapping("/pages")
    public R<?> createPage(@RequestBody CmsService.PageSaveRequest req) {
        return R.ok(cmsService.savePage(req, TenantContext.getLocale()));
    }

    @PutMapping("/pages/{id}")
    public R<?> updatePage(@PathVariable Long id, @RequestBody CmsService.PageSaveRequest req) {
        req.setId(id);
        return R.ok(cmsService.savePage(req, TenantContext.getLocale()));
    }

    @PostMapping("/pages/{id}/duplicate")
    public R<?> duplicatePage(@PathVariable Long id) {
        return R.ok(cmsService.duplicatePage(id, TenantContext.getLocale()));
    }

    @DeleteMapping("/pages/{id}")
    public R<?> deletePage(@PathVariable Long id) {
        cmsService.deletePage(id);
        return R.ok();
    }

    @PostMapping("/articles/{id}/duplicate")
    public R<?> duplicateArticle(@PathVariable Long id) {
        return R.ok(cmsService.duplicateArticle(id, TenantContext.getLocale()));
    }

    @DeleteMapping("/articles/{id}")
    public R<?> deleteArticle(@PathVariable Long id) {
        cmsService.deleteArticle(id);
        return R.ok();
    }

    @GetMapping("/articles")
    public R<?> articles(@RequestParam(required = false) Long siteId) {
        return R.ok(cmsService.listArticles(siteId, TenantContext.getLocale()));
    }

    @PostMapping("/articles")
    public R<?> saveArticle(@RequestBody CmsService.ArticleSaveRequest req) {
        return R.ok(cmsService.saveArticle(req, TenantContext.getLocale()));
    }

    @PutMapping("/articles/{id}")
    public R<?> updateArticle(@PathVariable Long id, @RequestBody CmsService.ArticleSaveRequest req) {
        req.setId(id);
        return R.ok(cmsService.saveArticle(req, TenantContext.getLocale()));
    }
}

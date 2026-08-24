package com.tradehub.store;

import com.tradehub.common.api.R;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.inquiry.InquiryService;
import com.tradehub.tenant.Site;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StorefrontController {
    private final StorefrontService storefrontService;

    @GetMapping("/context")
    public R<?> context(HttpServletRequest request) {
        return R.ok(storefrontService.context(site(request), TenantContext.getLocale()));
    }

    @GetMapping("/home")
    public R<?> home(HttpServletRequest request) {
        return R.ok(storefrontService.home(site(request), TenantContext.getLocale()));
    }

    @GetMapping("/categories")
    public R<?> categories(HttpServletRequest request) {
        return R.ok(storefrontService.categories(site(request), TenantContext.getLocale()));
    }

    @GetMapping("/products")
    public R<?> products(HttpServletRequest request, @RequestParam(required = false) String category) {
        return R.ok(storefrontService.products(site(request), TenantContext.getLocale(), category));
    }

    @GetMapping("/products/{slug}")
    public R<?> product(HttpServletRequest request, @PathVariable String slug) {
        return R.ok(storefrontService.product(site(request), TenantContext.getLocale(), slug));
    }

    @GetMapping("/pages/{slug}")
    public R<?> page(HttpServletRequest request, @PathVariable String slug) {
        return R.ok(storefrontService.page(site(request), TenantContext.getLocale(), slug));
    }

    @GetMapping("/articles")
    public R<?> articles(HttpServletRequest request) {
        return R.ok(storefrontService.articles(site(request), TenantContext.getLocale()));
    }

    @GetMapping("/articles/{slug}")
    public R<?> article(HttpServletRequest request, @PathVariable String slug) {
        return R.ok(storefrontService.article(site(request), TenantContext.getLocale(), slug));
    }

    @GetMapping("/search")
    public R<?> search(HttpServletRequest request, @RequestParam String q) {
        return R.ok(storefrontService.search(site(request), TenantContext.getLocale(), q));
    }

    @GetMapping("/sitemap")
    public R<?> sitemap(HttpServletRequest request) {
        return R.ok(storefrontService.sitemap(site(request)));
    }

    @GetMapping("/redirects")
    public R<?> redirects(HttpServletRequest request) {
        return R.ok(storefrontService.redirects(site(request)));
    }

    @PostMapping("/inquiries")
    public R<?> inquiry(HttpServletRequest request, @Valid @RequestBody InquiryService.InquiryCreateRequest body) {
        return R.ok(storefrontService.submitInquiry(body, request));
    }

    @GetMapping("/health")
    public R<?> health() {
        return R.ok(java.util.Map.of("status", "up"));
    }

    private Site site(HttpServletRequest request) {
        Object attr = request.getAttribute(StorefrontSiteFilter.SITE_ATTR);
        if (attr instanceof Site site) {
            return site;
        }
        return storefrontService.currentSite();
    }
}

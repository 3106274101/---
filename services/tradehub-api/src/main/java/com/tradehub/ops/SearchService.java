package com.tradehub.ops;

import com.tradehub.catalog.CatalogService;
import com.tradehub.cms.CmsService;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.inquiry.Inquiry;
import com.tradehub.inquiry.InquiryService;
import com.tradehub.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final CatalogService catalogService;
    private final CmsService cmsService;
    private final InquiryService inquiryService;
    private final TenantService tenantService;

    public Map<String, Object> search(String q) {
        if (!StringUtils.hasText(q)) {
            return Map.of("products", List.of(), "pages", List.of(), "articles", List.of(), "inquiries", List.of());
        }
        String locale = TenantContext.getLocale();
        Long siteId = TenantContext.getSiteId();
        List<Map<String, Object>> products = catalogService.listProducts(locale, q, null, 1, 8).getList();
        List<Map<String, Object>> pages = cmsService.listPages(siteId, locale).stream()
                .filter(p -> ("" + p.get("title") + p.get("slug")).toLowerCase().contains(q.toLowerCase()))
                .limit(8)
                .toList();
        List<Map<String, Object>> articles = cmsService.listArticles(siteId, locale).stream()
                .filter(a -> ("" + a.get("title") + a.get("slug") + a.get("summary")).toLowerCase().contains(q.toLowerCase()))
                .limit(8)
                .toList();
        List<Inquiry> inquiries = inquiryService.adminList(null, null, q, null, null, 1, 8).getList();
        List<Map<String, Object>> inqViews = new ArrayList<>();
        for (Inquiry row : inquiries) {
            inqViews.add(Map.of(
                    "id", row.getId(),
                    "name", row.getName() == null ? "" : row.getName(),
                    "email", row.getEmail() == null ? "" : row.getEmail(),
                    "company", row.getCompany() == null ? "" : row.getCompany()
            ));
        }
        return Map.of(
                "products", products,
                "pages", pages,
                "articles", articles,
                "inquiries", inqViews,
                "sites", tenantService.listSites().getList().stream()
                        .filter(s -> ("" + s.get("name") + s.get("code")).toLowerCase().contains(q.toLowerCase()))
                        .limit(6)
                        .toList()
        );
    }
}

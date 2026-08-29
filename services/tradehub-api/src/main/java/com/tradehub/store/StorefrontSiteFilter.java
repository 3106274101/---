package com.tradehub.store;

import com.tradehub.common.tenant.TenantContext;
import com.tradehub.tenant.Site;
import com.tradehub.tenant.TenantService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class StorefrontSiteFilter extends OncePerRequestFilter {
    public static final String SITE_ATTR = "tradehub.site";
    private final TenantService tenantService;
    private final String defaultSiteCode;

    public StorefrontSiteFilter(TenantService tenantService,
                                @Value("${tradehub.store.default-site-code}") String defaultSiteCode) {
        this.tenantService = tenantService;
        this.defaultSiteCode = defaultSiteCode;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/store")) {
            String code = firstNonBlank(request.getHeader("X-Site-Code"), request.getParameter("site"));
            String host = firstNonBlank(request.getHeader("X-Site-Host"), request.getHeader("Host"));
            String locale = firstNonBlank(request.getHeader("X-Locale"), request.getParameter("locale"), "en");
            Site site = tenantService.resolveByHostOrCode(host, code, defaultSiteCode);
            if (site != null) {
                request.setAttribute(SITE_ATTR, site);
                TenantContext.set(site.getTenantId(), site.getId(), locale, false);
            }
        }
        chain.doFilter(request, response);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}

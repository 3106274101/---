package com.tradehub.iam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AccessInterceptor implements HandlerInterceptor {
    private final AccessService accessService;

    public AccessInterceptor(AccessService accessService) {
        this.accessService = accessService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/admin") || uri.startsWith("/api/admin/auth")) {
            return true;
        }
        boolean write = !"GET".equalsIgnoreCase(request.getMethod())
                && !"HEAD".equalsIgnoreCase(request.getMethod())
                && !"OPTIONS".equalsIgnoreCase(request.getMethod());
        Permission permission = resolve(uri);
        if (permission != null) {
            accessService.require(permission, write);
        }
        return true;
    }

    static Permission resolve(String uri) {
        if (uri.startsWith("/api/admin/tenants")) {
            return Permission.TENANTS;
        }
        if (uri.startsWith("/api/admin/domains")) {
            return Permission.DOMAINS;
        }
        if (uri.startsWith("/api/admin/sites") || uri.startsWith("/api/admin/templates")) {
            return Permission.SITES;
        }
        if (uri.startsWith("/api/admin/pages")) {
            return Permission.PAGES;
        }
        if (uri.startsWith("/api/admin/articles")) {
            return Permission.ARTICLES;
        }
        if (uri.startsWith("/api/admin/products") || uri.startsWith("/api/admin/categories")) {
            return Permission.PRODUCTS;
        }
        if (uri.startsWith("/api/admin/media")) {
            return Permission.MEDIA;
        }
        if (uri.startsWith("/api/admin/seo")) {
            return Permission.SEO;
        }
        if (uri.startsWith("/api/admin/inquiries")) {
            return Permission.INQUIRIES;
        }
        if (uri.startsWith("/api/admin/members")) {
            return Permission.MEMBERS;
        }
        if (uri.startsWith("/api/admin/audit")) {
            return Permission.AUDIT;
        }
        if (uri.startsWith("/api/admin/dashboard")) {
            return Permission.DASHBOARD;
        }
        return Permission.DASHBOARD;
    }
}

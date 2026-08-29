package com.tradehub.iam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditInterceptor implements HandlerInterceptor {
    private final AuditService auditService;

    public AuditInterceptor(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (!uri.startsWith("/api/admin") || uri.startsWith("/api/admin/auth/login")) {
            return;
        }
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return;
        }
        if (ex != null || response.getStatus() >= 400) {
            return;
        }
        auditService.record(method + " " + uri, "http", null,
                "{\"method\":\"" + method + "\",\"uri\":\"" + uri.replace("\"", "") + "\"}");
    }
}

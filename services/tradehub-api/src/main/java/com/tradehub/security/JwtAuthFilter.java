package com.tradehub.security;

import com.tradehub.common.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtUtil.parse(header.substring(7));
                Long uid = claims.get("uid", Number.class).longValue();
                Number tidRaw = claims.get("tid", Number.class);
                Long tid = tidRaw == null ? null : tidRaw.longValue();
                boolean superAdmin = Boolean.TRUE.equals(claims.get("super", Boolean.class));
                String switchTenant = request.getHeader("X-Tenant-Id");
                if (superAdmin && switchTenant != null && !switchTenant.isBlank()) {
                    tid = Long.parseLong(switchTenant);
                }
                LoginUser user = LoginUser.builder()
                        .userId(uid)
                        .tenantId(tid)
                        .username(claims.getSubject())
                        .displayName(claims.get("name", String.class))
                        .roleCode(claims.get("role", String.class))
                        .superAdmin(superAdmin)
                        .build();
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                TenantContext.set(tid, parseLong(request.getHeader("X-Site-Id")),
                        request.getHeader("X-Locale"), superAdmin);
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.parseLong(value);
    }
}

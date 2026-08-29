package com.tradehub.config;

import com.tradehub.iam.AccessInterceptor;
import com.tradehub.iam.AuditInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final AccessInterceptor accessInterceptor;
    private final AuditInterceptor auditInterceptor;

    public InterceptorConfig(AccessInterceptor accessInterceptor, AuditInterceptor auditInterceptor) {
        this.accessInterceptor = accessInterceptor;
        this.auditInterceptor = auditInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor).addPathPatterns("/api/admin/**");
        registry.addInterceptor(auditInterceptor).addPathPatterns("/api/admin/**");
    }
}

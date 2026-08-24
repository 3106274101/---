package com.tradehub.common.tenant;

public final class TenantContext {
    private static final ThreadLocal<Long> TENANT = new ThreadLocal<>();
    private static final ThreadLocal<Long> SITE = new ThreadLocal<>();
    private static final ThreadLocal<String> LOCALE = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SUPER = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(Long tenantId, Long siteId, String locale, boolean superAdmin) {
        TENANT.set(tenantId);
        SITE.set(siteId);
        LOCALE.set(locale);
        SUPER.set(superAdmin);
    }

    public static Long getTenantId() {
        return TENANT.get();
    }

    public static Long requireTenantId() {
        Long id = TENANT.get();
        if (id == null) {
            throw new com.tradehub.common.exception.BizException(401, "tenant not resolved");
        }
        return id;
    }

    public static Long getSiteId() {
        return SITE.get();
    }

    public static String getLocale() {
        String locale = LOCALE.get();
        return locale == null ? "en" : locale;
    }

    public static boolean isSuperAdmin() {
        return Boolean.TRUE.equals(SUPER.get());
    }

    public static void clear() {
        TENANT.remove();
        SITE.remove();
        LOCALE.remove();
        SUPER.remove();
    }
}

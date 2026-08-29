package com.tradehub.iam;

import java.util.Arrays;
import java.util.List;

public final class RolePermissions {
    private RolePermissions() {
    }

    public static boolean allows(boolean superAdmin, String roleCode, Permission permission, boolean write) {
        if (superAdmin || permission == null) {
            return superAdmin || permission == null;
        }
        String role = roleCode == null ? "EDITOR" : roleCode.trim().toUpperCase();
        return switch (role) {
            case "SUPER", "SUPER_ADMIN" -> true;
            case "OWNER" -> permission != Permission.TENANTS;
            case "EDITOR" -> switch (permission) {
                case DASHBOARD, SITES, PAGES, PRODUCTS, ARTICLES, MEDIA, SEO -> true;
                default -> false;
            };
            case "SALES" -> permission == Permission.DASHBOARD
                    || permission == Permission.INQUIRIES
                    || (!write && (permission == Permission.SITES
                    || permission == Permission.PAGES
                    || permission == Permission.PRODUCTS
                    || permission == Permission.ARTICLES
                    || permission == Permission.MEMBERS));
            default -> false;
        };
    }

    public static List<String> readable(boolean superAdmin, String roleCode) {
        return Arrays.stream(Permission.values())
                .filter(p -> allows(superAdmin, roleCode, p, false))
                .map(Enum::name)
                .toList();
    }
}

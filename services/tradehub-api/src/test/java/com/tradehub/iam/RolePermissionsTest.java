package com.tradehub.iam;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RolePermissionsTest {

    @Test
    void superAdminBypassesRole() {
        assertTrue(RolePermissions.allows(true, "SALES", Permission.TENANTS, true));
    }

    @Test
    void ownerManagesMembersButNotTenants() {
        assertFalse(RolePermissions.allows(false, "OWNER", Permission.TENANTS, true));
        assertTrue(RolePermissions.allows(false, "OWNER", Permission.MEMBERS, true));
        assertTrue(RolePermissions.allows(false, "OWNER", Permission.DOMAINS, true));
        assertTrue(RolePermissions.allows(false, "OWNER", Permission.AUDIT, false));
    }

    @Test
    void editorCannotBindDomainOrSeeAudit() {
        assertTrue(RolePermissions.allows(false, "EDITOR", Permission.PAGES, true));
        assertFalse(RolePermissions.allows(false, "EDITOR", Permission.DOMAINS, true));
        assertFalse(RolePermissions.allows(false, "EDITOR", Permission.MEMBERS, true));
        assertFalse(RolePermissions.allows(false, "EDITOR", Permission.INQUIRIES, true));
    }

    @Test
    void salesCanFollowInquiriesAndReadCatalog() {
        assertTrue(RolePermissions.allows(false, "SALES", Permission.INQUIRIES, true));
        assertTrue(RolePermissions.allows(false, "SALES", Permission.PRODUCTS, false));
        assertFalse(RolePermissions.allows(false, "SALES", Permission.PRODUCTS, true));
        assertTrue(RolePermissions.allows(false, "SALES", Permission.MEMBERS, false));
        assertFalse(RolePermissions.allows(false, "SALES", Permission.MEMBERS, true));
        assertFalse(RolePermissions.allows(false, "SALES", Permission.MEDIA, true));
    }
}

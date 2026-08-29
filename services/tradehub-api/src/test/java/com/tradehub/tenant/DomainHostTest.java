package com.tradehub.tenant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainHostTest {

    @Test
    void normalizeHostStripsProtocolPathAndPort() {
        assertEquals("www.example.com", TenantService.normalizeHost("https://www.example.com:443/about"));
        assertEquals("shop.zhenghe.test", TenantService.normalizeHost(" shop.zhenghe.test. "));
        assertEquals("", TenantService.normalizeHost("  "));
    }
}

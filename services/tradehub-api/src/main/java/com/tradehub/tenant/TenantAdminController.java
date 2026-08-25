package com.tradehub.tenant;

import com.tradehub.common.api.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TenantAdminController {
    private final TenantService tenantService;

    @GetMapping("/tenants")
    public R<?> tenants() {
        return R.ok(tenantService.listTenants());
    }

    @PostMapping("/tenants")
    public R<?> createTenant(@RequestBody Tenant body) {
        return R.ok(tenantService.saveTenant(body));
    }

    @PutMapping("/tenants/{id}")
    public R<?> updateTenant(@PathVariable Long id, @RequestBody Tenant body) {
        body.setId(id);
        return R.ok(tenantService.saveTenant(body));
    }

    @GetMapping("/sites")
    public R<?> sites() {
        return R.ok(tenantService.listSites());
    }

    @GetMapping("/sites/{id}")
    public R<?> site(@PathVariable Long id) {
        return R.ok(tenantService.siteView(tenantService.getSite(id)));
    }

    @PostMapping("/sites")
    public R<?> createSite(@RequestBody java.util.Map<String, Object> body) {
        return R.ok(tenantService.saveSitePayload(body));
    }

    @PutMapping("/sites/{id}")
    public R<?> updateSite(@PathVariable Long id, @RequestBody java.util.Map<String, Object> body) {
        body.put("id", id);
        return R.ok(tenantService.saveSitePayload(body));
    }
}

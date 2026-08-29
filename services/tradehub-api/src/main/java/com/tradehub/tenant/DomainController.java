package com.tradehub.tenant;

import com.tradehub.common.api.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/domains")
@RequiredArgsConstructor
public class DomainController {
    private final TenantService tenantService;

    @GetMapping("/check")
    public R<?> check(@RequestParam String host) {
        return R.ok(tenantService.checkHost(host));
    }

    @GetMapping
    public R<?> list(@RequestParam Long siteId) {
        return R.ok(tenantService.listDomains(siteId));
    }

    @PostMapping
    public R<?> bind(@RequestBody BindRequest req) {
        return R.ok(tenantService.bindDomain(req.getSiteId(), req.getHost(), Boolean.TRUE.equals(req.getPrimary())));
    }

    @PostMapping("/{id}/primary")
    public R<?> primary(@PathVariable Long id) {
        return R.ok(tenantService.setPrimaryDomain(id));
    }

    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        tenantService.unbindDomain(id);
        return R.ok();
    }

    @Data
    public static class BindRequest {
        private Long siteId;
        private String host;
        private Boolean primary;
    }
}

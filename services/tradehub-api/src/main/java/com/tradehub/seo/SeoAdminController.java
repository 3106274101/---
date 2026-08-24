package com.tradehub.seo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.api.R;
import com.tradehub.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/seo")
@RequiredArgsConstructor
public class SeoAdminController {
    private final RedirectMapper redirectMapper;
    private final TenantService tenantService;

    @GetMapping("/redirects")
    public R<?> redirects(@RequestParam Long siteId) {
        return R.ok(redirectMapper.selectList(new LambdaQueryWrapper<Redirect>()
                .eq(Redirect::getTenantId, tenantService.workingTenantId())
                .eq(Redirect::getSiteId, siteId)));
    }

    @PostMapping("/redirects")
    public R<?> save(@RequestBody Redirect body) {
        body.setTenantId(tenantService.workingTenantId());
        if (body.getCode() == null) {
            body.setCode(301);
        }
        if (body.getId() == null) {
            redirectMapper.insert(body);
        } else {
            redirectMapper.updateById(body);
        }
        return R.ok(body);
    }

    @GetMapping("/robots")
    public R<?> robots() {
        String txt = """
                User-agent: *
                Allow: /
                Disallow: /api/
                Disallow: /admin
                Sitemap: /sitemap.xml
                """;
        return R.ok(java.util.Map.of("content", txt));
    }
}

package com.tradehub.theme;

import com.tradehub.common.api.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class SiteTemplateController {
    private final SiteTemplateService siteTemplateService;

    @GetMapping
    public R<?> list() {
        return R.ok(siteTemplateService.list());
    }
}

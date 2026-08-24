package com.tradehub.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.Product;
import com.tradehub.catalog.ProductMapper;
import com.tradehub.common.api.R;
import com.tradehub.inquiry.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final TenantService tenantService;
    private final SiteMapper siteMapper;
    private final ProductMapper productMapper;
    private final InquiryService inquiryService;

    @GetMapping
    public R<?> stats() {
        Long tenantId = tenantService.workingTenantId();
        Map<String, Object> data = new HashMap<>();
        data.put("sites", siteMapper.selectCount(new LambdaQueryWrapper<Site>().eq(Site::getTenantId, tenantId)));
        data.put("products", productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getTenantId, tenantId)));
        data.put("inquiries", inquiryService.countAll(tenantId));
        data.put("inquiriesWeek", inquiryService.countThisWeek(tenantId));
        data.put("languages", 2);
        data.put("sitesList", tenantService.listSites().getList());
        return R.ok(data);
    }
}

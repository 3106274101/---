package com.tradehub.catalog;

import com.tradehub.common.api.R;
import com.tradehub.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping("/categories")
    public R<?> categories() {
        return R.ok(catalogService.listCategories(TenantContext.getLocale()));
    }

    @PostMapping("/categories")
    public R<?> saveCategory(@RequestBody CatalogService.CategorySaveRequest req) {
        return R.ok(catalogService.saveCategory(req, TenantContext.getLocale()));
    }

    @GetMapping("/products")
    public R<?> products(@RequestParam(defaultValue = "") String keyword,
                         @RequestParam(required = false) String status,
                         @RequestParam(defaultValue = "1") long page,
                         @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(catalogService.listProducts(TenantContext.getLocale(), keyword, status, page, pageSize));
    }

    @GetMapping("/products/{id}")
    public R<?> product(@PathVariable Long id) {
        return R.ok(catalogService.getProduct(id, TenantContext.getLocale()));
    }

    @PostMapping("/products")
    public R<?> createProduct(@RequestBody CatalogService.ProductSaveRequest req) {
        return R.ok(catalogService.saveProduct(req, TenantContext.getLocale()));
    }

    @PutMapping("/products/{id}")
    public R<?> updateProduct(@PathVariable Long id, @RequestBody CatalogService.ProductSaveRequest req) {
        req.setId(id);
        return R.ok(catalogService.saveProduct(req, TenantContext.getLocale()));
    }

    @PostMapping("/products/{id}/status")
    public R<?> status(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        catalogService.changeStatus(id, body.get("status"));
        return R.ok();
    }
}

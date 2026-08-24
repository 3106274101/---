package com.tradehub.catalog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.exception.BizException;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.tenant.TenantService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final CategoryMapper categoryMapper;
    private final CategoryI18nMapper categoryI18nMapper;
    private final ProductMapper productMapper;
    private final ProductI18nMapper productI18nMapper;
    private final ProductSiteMapper productSiteMapper;
    private final TenantService tenantService;

    public List<Map<String, Object>> listCategories(String locale) {
        Long tenantId = tenantService.workingTenantId();
        List<Category> cats = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getTenantId, tenantId)
                .orderByAsc(Category::getSortOrder));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category cat : cats) {
            result.add(categoryView(cat, locale));
        }
        return result;
    }

    public Map<String, Object> saveCategory(CategorySaveRequest req, String locale) {
        Long tenantId = tenantService.workingTenantId();
        Category cat = req.getId() == null ? new Category() : categoryMapper.selectById(req.getId());
        if (cat == null) {
            throw new BizException(404, "category not found");
        }
        cat.setTenantId(tenantId);
        cat.setSlug(req.getSlug());
        cat.setParentId(req.getParentId() == null ? 0L : req.getParentId());
        cat.setSortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder());
        cat.setStatus(req.getStatus() == null ? "live" : req.getStatus());
        if (cat.getId() == null) {
            categoryMapper.insert(cat);
        } else {
            categoryMapper.updateById(cat);
        }
        upsertCategoryI18n(cat, locale, req);
        return categoryView(cat, locale);
    }

    public PageResult<Map<String, Object>> listProducts(String locale, String keyword, String status, long page, long size) {
        Long tenantId = tenantService.workingTenantId();
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(StringUtils.hasText(status), Product::getStatus, status)
                .like(StringUtils.hasText(keyword), Product::getModel, keyword)
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getId);
        List<Product> all = productMapper.selectList(qw);
        long total = all.size();
        int from = (int) Math.max(0, (page - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        List<Map<String, Object>> slice = new ArrayList<>();
        if (from < to) {
            for (Product p : all.subList(from, to)) {
                slice.add(productView(p, locale, true));
            }
        }
        return new PageResult<>(slice, total, page, size);
    }

    public Map<String, Object> getProduct(Long id, String locale) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "product not found");
        }
        return productView(product, locale, true);
    }

    public Map<String, Object> saveProduct(ProductSaveRequest req, String locale) {
        Long tenantId = tenantService.workingTenantId();
        Product product = req.getId() == null ? new Product() : productMapper.selectById(req.getId());
        if (product == null) {
            throw new BizException(404, "product not found");
        }
        product.setTenantId(tenantId);
        product.setCategoryId(req.getCategoryId());
        product.setSlug(req.getSlug());
        product.setModel(req.getModel());
        product.setCoverUrl(req.getCoverUrl());
        product.setGalleryJson(Jsons.toJson(req.getGallery()));
        product.setAttrJson(Jsons.toJson(req.getAttrs()));
        product.setStatus(req.getStatus() == null ? "draft" : req.getStatus());
        product.setSortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder());
        product.setFeatured(Boolean.TRUE.equals(req.getFeatured()) ? 1 : 0);
        if ("live".equals(product.getStatus()) && product.getPublishedAt() == null) {
            product.setPublishedAt(LocalDateTime.now());
        }
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.updateById(product);
        }
        upsertProductI18n(product, locale, req);
        if (req.getSiteId() != null) {
            ProductSite rel = productSiteMapper.selectOne(new LambdaQueryWrapper<ProductSite>()
                    .eq(ProductSite::getProductId, product.getId())
                    .eq(ProductSite::getSiteId, req.getSiteId()));
            if (rel == null) {
                rel = new ProductSite();
                rel.setTenantId(tenantId);
                rel.setProductId(product.getId());
                rel.setSiteId(req.getSiteId());
                rel.setVisible(1);
                rel.setSortOrder(product.getSortOrder());
                productSiteMapper.insert(rel);
            }
        }
        return productView(product, locale, true);
    }

    public void changeStatus(Long id, String status) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "product not found");
        }
        product.setStatus(status);
        if ("live".equals(status)) {
            product.setPublishedAt(LocalDateTime.now());
        }
        productMapper.updateById(product);
    }

    public Map<String, Object> productView(Product product, String locale, boolean allLocales) {
        ProductI18n i18n = pickProductI18n(product.getId(), locale);
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("categoryId", product.getCategoryId());
        map.put("slug", i18n != null && StringUtils.hasText(i18n.getSlug()) ? i18n.getSlug() : product.getSlug());
        map.put("model", product.getModel());
        map.put("coverUrl", product.getCoverUrl());
        map.put("gallery", Jsons.list(product.getGalleryJson()));
        map.put("attrs", Jsons.map(product.getAttrJson()));
        map.put("status", product.getStatus());
        map.put("featured", Integer.valueOf(1).equals(product.getFeatured()));
        map.put("sortOrder", product.getSortOrder());
        map.put("publishedAt", product.getPublishedAt());
        if (i18n != null) {
            map.put("name", i18n.getName());
            map.put("summary", i18n.getSummary());
            map.put("content", i18n.getContent());
            map.put("seoTitle", i18n.getSeoTitle());
            map.put("seoDescription", i18n.getSeoDescription());
            map.put("locale", i18n.getLocale());
        }
        if (allLocales) {
            List<ProductI18n> list = productI18nMapper.selectList(new LambdaQueryWrapper<ProductI18n>()
                    .eq(ProductI18n::getProductId, product.getId()));
            map.put("translations", list);
        }
        return map;
    }

    public ProductI18n pickProductI18n(Long productId, String locale) {
        List<ProductI18n> list = productI18nMapper.selectList(new LambdaQueryWrapper<ProductI18n>()
                .eq(ProductI18n::getProductId, productId));
        return pick(list, locale, ProductI18n::getLocale);
    }

    public CategoryI18n pickCategoryI18n(Long categoryId, String locale) {
        List<CategoryI18n> list = categoryI18nMapper.selectList(new LambdaQueryWrapper<CategoryI18n>()
                .eq(CategoryI18n::getCategoryId, categoryId));
        return pick(list, locale, CategoryI18n::getLocale);
    }

    public List<Product> liveProducts(Long tenantId) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(Product::getStatus, "live")
                .orderByAsc(Product::getSortOrder));
    }

    private Map<String, Object> categoryView(Category cat, String locale) {
        CategoryI18n i18n = pickCategoryI18n(cat.getId(), locale);
        Map<String, Object> map = new HashMap<>();
        map.put("id", cat.getId());
        map.put("slug", cat.getSlug());
        map.put("parentId", cat.getParentId());
        map.put("sortOrder", cat.getSortOrder());
        map.put("status", cat.getStatus());
        map.put("name", i18n == null ? cat.getSlug() : i18n.getName());
        map.put("description", i18n == null ? null : i18n.getDescription());
        map.put("seoTitle", i18n == null ? null : i18n.getSeoTitle());
        map.put("seoDescription", i18n == null ? null : i18n.getSeoDescription());
        return map;
    }

    private void upsertCategoryI18n(Category cat, String locale, CategorySaveRequest req) {
        CategoryI18n row = categoryI18nMapper.selectOne(new LambdaQueryWrapper<CategoryI18n>()
                .eq(CategoryI18n::getCategoryId, cat.getId())
                .eq(CategoryI18n::getLocale, locale));
        if (row == null) {
            row = new CategoryI18n();
            row.setTenantId(cat.getTenantId());
            row.setCategoryId(cat.getId());
            row.setLocale(locale);
        }
        row.setName(req.getName());
        row.setDescription(req.getDescription());
        row.setSeoTitle(req.getSeoTitle());
        row.setSeoDescription(req.getSeoDescription());
        if (row.getId() == null) {
            categoryI18nMapper.insert(row);
        } else {
            categoryI18nMapper.updateById(row);
        }
    }

    private void upsertProductI18n(Product product, String locale, ProductSaveRequest req) {
        ProductI18n row = productI18nMapper.selectOne(new LambdaQueryWrapper<ProductI18n>()
                .eq(ProductI18n::getProductId, product.getId())
                .eq(ProductI18n::getLocale, locale));
        if (row == null) {
            row = new ProductI18n();
            row.setTenantId(product.getTenantId());
            row.setProductId(product.getId());
            row.setLocale(locale);
        }
        row.setSlug(req.getI18nSlug());
        row.setName(req.getName());
        row.setSummary(req.getSummary());
        row.setContent(req.getContent());
        row.setSeoTitle(req.getSeoTitle());
        row.setSeoDescription(req.getSeoDescription());
        if (row.getId() == null) {
            productI18nMapper.insert(row);
        } else {
            productI18nMapper.updateById(row);
        }
    }

    private <T> T pick(List<T> list, String locale, java.util.function.Function<T, String> fn) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String use = StringUtils.hasText(locale) ? locale : TenantContext.getLocale();
        return list.stream().filter(i -> use.equals(fn.apply(i))).findFirst()
                .orElseGet(() -> list.stream().filter(i -> "en".equals(fn.apply(i))).findFirst()
                        .orElse(list.get(0)));
    }

    @Data
    public static class CategorySaveRequest {
        private Long id;
        private Long parentId;
        private String slug;
        private Integer sortOrder;
        private String status;
        private String name;
        private String description;
        private String seoTitle;
        private String seoDescription;
    }

    @Data
    public static class ProductSaveRequest {
        private Long id;
        private Long categoryId;
        private Long siteId;
        private String slug;
        private String i18nSlug;
        private String model;
        private String coverUrl;
        private List<Object> gallery;
        private Map<String, Object> attrs;
        private String status;
        private Integer sortOrder;
        private Boolean featured;
        private String name;
        private String summary;
        private String content;
        private String seoTitle;
        private String seoDescription;
    }
}

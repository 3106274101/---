package com.tradehub.catalog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.exception.BizException;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.tenant.TenantService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<Long, List<CategoryI18n>> i18nByCat = loadCategoryI18n(cats.stream().map(Category::getId).toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category cat : cats) {
            result.add(categoryView(cat, locale, i18nByCat.getOrDefault(cat.getId(), List.of())));
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
        CategoryI18n saved = pickCategoryI18n(cat.getId(), locale);
        return categoryView(cat, locale, saved == null ? List.of() : List.of(saved));
    }

    public PageResult<Map<String, Object>> listProducts(String locale, String keyword, String status, long page, long size) {
        Long tenantId = tenantService.workingTenantId();
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(StringUtils.hasText(status), Product::getStatus, status)
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getId);
        List<Product> all = productMapper.selectList(qw);
        if (StringUtils.hasText(keyword)) {
            String needle = keyword.toLowerCase();
            Map<Long, List<ProductI18n>> i18nAll = loadProductI18n(all.stream().map(Product::getId).toList());
            all = all.stream().filter(p -> {
                if ((p.getModel() != null && p.getModel().toLowerCase().contains(needle))
                        || (p.getSlug() != null && p.getSlug().toLowerCase().contains(needle))) {
                    return true;
                }
                return i18nAll.getOrDefault(p.getId(), List.of()).stream()
                        .anyMatch(i -> i.getName() != null && i.getName().toLowerCase().contains(needle));
            }).toList();
        }
        long total = all.size();
        int from = (int) Math.max(0, (page - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        List<Map<String, Object>> slice = new ArrayList<>();
        if (from < to) {
            List<Product> pageItems = all.subList(from, to);
            Map<Long, List<ProductI18n>> i18nByProduct = loadProductI18n(pageItems.stream().map(Product::getId).toList());
            for (Product p : pageItems) {
                slice.add(productView(p, locale, true, i18nByProduct.getOrDefault(p.getId(), List.of())));
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
        product.setStatus(normalizePublishStatus(req.getStatus(), req.getScheduledAt()));
        product.setScheduledAt(req.getScheduledAt());
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

    public Map<String, Object> duplicate(Long id, String locale) {
        Product src = productMapper.selectById(id);
        if (src == null || !src.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "product not found");
        }
        Product copy = new Product();
        copy.setTenantId(src.getTenantId());
        copy.setCategoryId(src.getCategoryId());
        copy.setSlug(src.getSlug() + "-copy");
        copy.setModel(src.getModel() == null ? "COPY" : src.getModel() + "-COPY");
        copy.setCoverUrl(src.getCoverUrl());
        copy.setGalleryJson(src.getGalleryJson());
        copy.setAttrJson(src.getAttrJson());
        copy.setStatus("draft");
        copy.setSortOrder(src.getSortOrder());
        copy.setFeatured(0);
        productMapper.insert(copy);
        for (ProductI18n row : productI18nMapper.selectList(new LambdaQueryWrapper<ProductI18n>()
                .eq(ProductI18n::getProductId, src.getId()))) {
            ProductI18n n = new ProductI18n();
            n.setTenantId(row.getTenantId());
            n.setProductId(copy.getId());
            n.setLocale(row.getLocale());
            n.setSlug(row.getSlug() == null ? null : row.getSlug() + "-copy");
            n.setName((row.getName() == null ? "" : row.getName()) + " (copy)");
            n.setSummary(row.getSummary());
            n.setContent(row.getContent());
            n.setSeoTitle(row.getSeoTitle());
            n.setSeoDescription(row.getSeoDescription());
            productI18nMapper.insert(n);
        }
        return getProduct(copy.getId(), locale);
    }

    public int bulkStatus(List<Long> ids, String status) {
        int n = 0;
        if (ids == null) {
            return 0;
        }
        for (Long id : ids) {
            changeStatus(id, status);
            n++;
        }
        return n;
    }

    public String exportCsv(String locale) {
        List<Map<String, Object>> list = listProducts(locale, "", null, 1, 10_000).getList();
        StringBuilder sb = new StringBuilder();
        sb.append("id,model,slug,name,status,featured,coverUrl\n");
        for (Map<String, Object> row : list) {
            sb.append(row.get("id")).append(',')
                    .append(csv(row.get("model"))).append(',')
                    .append(csv(row.get("slug"))).append(',')
                    .append(csv(row.get("name"))).append(',')
                    .append(csv(row.get("status"))).append(',')
                    .append(row.get("featured")).append(',')
                    .append(csv(row.get("coverUrl"))).append('\n');
        }
        return sb.toString();
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value).replace("\"", "\"\"");
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text + "\"";
        }
        return text;
    }

    public long countDraft(Long tenantId) {
        return productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(Product::getStatus, "draft"));
    }

    public long countMissingCover(Long tenantId) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>().eq(Product::getTenantId, tenantId))
                .stream()
                .filter(p -> !StringUtils.hasText(p.getCoverUrl()))
                .count();
    }

    public Map<String, Object> productView(Product product, String locale, boolean allLocales) {
        List<ProductI18n> translations = productI18nMapper.selectList(new LambdaQueryWrapper<ProductI18n>()
                .eq(ProductI18n::getProductId, product.getId()));
        return productView(product, locale, allLocales, translations);
    }

    private Map<String, Object> productView(Product product, String locale, boolean allLocales, List<ProductI18n> translations) {
        ProductI18n i18n = pick(translations, locale, ProductI18n::getLocale);
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
        map.put("scheduledAt", product.getScheduledAt());
        if (i18n != null) {
            map.put("name", i18n.getName());
            map.put("summary", i18n.getSummary());
            map.put("content", i18n.getContent());
            map.put("seoTitle", i18n.getSeoTitle());
            map.put("seoDescription", i18n.getSeoDescription());
            map.put("locale", i18n.getLocale());
        }
        if (allLocales) {
            map.put("translations", translations);
        }
        return map;
    }

    private Map<Long, List<ProductI18n>> loadProductI18n(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        return productI18nMapper.selectList(new LambdaQueryWrapper<ProductI18n>()
                        .in(ProductI18n::getProductId, productIds))
                .stream()
                .collect(Collectors.groupingBy(ProductI18n::getProductId));
    }

    private Map<Long, List<CategoryI18n>> loadCategoryI18n(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Map.of();
        }
        return categoryI18nMapper.selectList(new LambdaQueryWrapper<CategoryI18n>()
                        .in(CategoryI18n::getCategoryId, categoryIds))
                .stream()
                .collect(Collectors.groupingBy(CategoryI18n::getCategoryId));
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

    private Map<String, Object> categoryView(Category cat, String locale, List<CategoryI18n> translations) {
        CategoryI18n i18n = pick(translations, locale, CategoryI18n::getLocale);
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

    private String normalizePublishStatus(String status, LocalDateTime scheduledAt) {
        if (!StringUtils.hasText(status)) {
            return scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now()) ? "scheduled" : "draft";
        }
        if ("scheduled".equals(status) && scheduledAt != null && !scheduledAt.isAfter(LocalDateTime.now())) {
            return "live";
        }
        return status;
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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduledAt;
        private String name;
        private String summary;
        private String content;
        private String seoTitle;
        private String seoDescription;
    }
}

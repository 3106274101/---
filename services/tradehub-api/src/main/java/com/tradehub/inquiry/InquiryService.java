package com.tradehub.inquiry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.Product;
import com.tradehub.catalog.ProductMapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.exception.BizException;
import com.tradehub.tenant.Site;
import com.tradehub.tenant.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryMapper inquiryMapper;
    private final ProductMapper productMapper;
    private final TenantService tenantService;
    private final ConcurrentHashMap<String, Long> lastHit = new ConcurrentHashMap<>();

    public Map<String, Object> createFromStore(InquiryCreateRequest req, Site site, String locale, HttpServletRequest http) {
        if (StringUtils.hasText(req.getWebsite())) {
            throw new BizException(422, "invalid form");
        }
        String ip = clientIp(http);
        Long last = lastHit.get(ip);
        long now = System.currentTimeMillis();
        if (last != null && now - last < 15_000) {
            throw new BizException(429, "too many inquiries, please wait");
        }
        lastHit.put(ip, now);
        Inquiry inquiry = new Inquiry();
        inquiry.setTenantId(site.getTenantId());
        inquiry.setSiteId(site.getId());
        inquiry.setLocale(locale);
        inquiry.setProductId(req.getProductId());
        if (req.getProductId() != null) {
            Product product = productMapper.selectById(req.getProductId());
            if (product != null) {
                inquiry.setProductName(product.getModel());
            }
        }
        inquiry.setName(req.getName());
        inquiry.setCompany(req.getCompany());
        inquiry.setEmail(req.getEmail());
        inquiry.setPhone(req.getPhone());
        inquiry.setCountry(req.getCountry());
        inquiry.setWhatsapp(req.getWhatsapp());
        inquiry.setQuantity(req.getQuantity());
        inquiry.setMessage(req.getMessage());
        inquiry.setUtmJson(Jsons.toJson(req.getUtm()));
        inquiry.setStatus("new");
        inquiry.setIp(ip);
        inquiryMapper.insert(inquiry);
        return Map.of("id", inquiry.getId(), "ok", true);
    }

    public PageResult<Inquiry> adminList(String status, long page, long size) {
        Long tenantId = tenantService.workingTenantId();
        LambdaQueryWrapper<Inquiry> qw = new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getTenantId, tenantId)
                .eq(StringUtils.hasText(status), Inquiry::getStatus, status)
                .orderByDesc(Inquiry::getId);
        var all = inquiryMapper.selectList(qw);
        int from = (int) Math.max(0, (page - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        return new PageResult<>(from < to ? all.subList(from, to) : java.util.List.of(), all.size(), page, size);
    }

    public void updateStatus(Long id, String status) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !inquiry.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "inquiry not found");
        }
        inquiry.setStatus(status);
        inquiryMapper.updateById(inquiry);
    }

    public long countThisWeek(Long tenantId) {
        return inquiryMapper.selectCount(new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getTenantId, tenantId)
                .ge(Inquiry::getCreatedAt, LocalDateTime.now().minusDays(7)));
    }

    public long countAll(Long tenantId) {
        return inquiryMapper.selectCount(new LambdaQueryWrapper<Inquiry>().eq(Inquiry::getTenantId, tenantId));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Data
    public static class InquiryCreateRequest {
        private Long productId;
        @NotBlank
        private String name;
        private String company;
        @NotBlank
        @Email
        private String email;
        private String phone;
        private String country;
        private String whatsapp;
        private String quantity;
        private String message;
        private String website;
        private Map<String, Object> utm;
    }
}

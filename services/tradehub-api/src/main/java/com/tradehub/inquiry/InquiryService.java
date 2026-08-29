package com.tradehub.inquiry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.Product;
import com.tradehub.catalog.ProductMapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.exception.BizException;
import com.tradehub.notify.MailNotifyService;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryMapper inquiryMapper;
    private final ProductMapper productMapper;
    private final TenantService tenantService;
    private final MailNotifyService mailNotifyService;
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
        if (StringUtils.hasText(req.getProductName())) {
            inquiry.setProductName(req.getProductName());
        } else if (req.getProductId() != null) {
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
        inquiry.setStarred(0);
        inquiry.setSource("storefront");
        inquiry.setIp(ip);
        inquiryMapper.insert(inquiry);
        long repeats = inquiryMapper.selectCount(new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getTenantId, site.getTenantId())
                .eq(Inquiry::getEmail, inquiry.getEmail()));
        mailNotifyService.notifyInquiry(inquiry, site);
        return Map.of("id", inquiry.getId(), "ok", true, "repeatBuyer", repeats > 1);
    }

    public PageResult<Inquiry> adminList(String status, long page, long size) {
        return adminList(status, null, null, null, null, page, size);
    }

    public PageResult<Inquiry> adminList(String status, String country, String q, Boolean starred, Boolean overdue, long page, long size) {
        Long tenantId = tenantService.workingTenantId();
        LambdaQueryWrapper<Inquiry> qw = new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getTenantId, tenantId)
                .eq(StringUtils.hasText(status), Inquiry::getStatus, status)
                .eq(StringUtils.hasText(country), Inquiry::getCountry, country)
                .eq(starred != null && starred, Inquiry::getStarred, 1)
                .orderByDesc(Inquiry::getId);
        List<Inquiry> all = inquiryMapper.selectList(qw);
        if (StringUtils.hasText(q)) {
            String needle = q.toLowerCase();
            all = all.stream().filter(row -> blob(row).contains(needle)).toList();
        }
        if (Boolean.TRUE.equals(overdue)) {
            LocalDateTime now = LocalDateTime.now();
            all = all.stream()
                    .filter(row -> row.getNextFollowAt() != null && row.getNextFollowAt().isBefore(now)
                            && !"quoted".equals(row.getStatus()) && !"lost".equals(row.getStatus()))
                    .toList();
        }
        int from = (int) Math.max(0, (page - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        List<Inquiry> slice = from < to ? all.subList(from, to) : java.util.List.of();
        java.util.Map<String, Long> emailHits = inquiryMapper.selectList(new LambdaQueryWrapper<Inquiry>()
                        .eq(Inquiry::getTenantId, tenantId))
                .stream()
                .filter(row -> StringUtils.hasText(row.getEmail()))
                .collect(java.util.stream.Collectors.groupingBy(row -> row.getEmail().toLowerCase(), java.util.stream.Collectors.counting()));
        for (Inquiry row : slice) {
            if (StringUtils.hasText(row.getEmail())) {
                row.setRepeatCount(emailHits.getOrDefault(row.getEmail().toLowerCase(), 1L).intValue());
            } else {
                row.setRepeatCount(1);
            }
        }
        return new PageResult<>(slice, all.size(), page, size);
    }

    private String blob(Inquiry row) {
        return ("" + row.getName() + row.getCompany() + row.getEmail() + row.getProductName() + row.getMessage() + row.getCountry()).toLowerCase();
    }

    public void updateStatus(Long id, String status) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !inquiry.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "inquiry not found");
        }
        inquiry.setStatus(status);
        inquiryMapper.updateById(inquiry);
    }

    public void assign(Long id, Long userId) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !inquiry.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "inquiry not found");
        }
        inquiry.setAssignedUserId(userId);
        inquiryMapper.updateById(inquiry);
    }

    public Inquiry addNote(Long id, String body, String userName) {
        Inquiry inquiry = owned(id);
        List<Object> notes;
        try {
            notes = new java.util.ArrayList<>(Jsons.list(inquiry.getNotesJson()));
        } catch (Exception e) {
            notes = new java.util.ArrayList<>();
        }
        notes.add(Map.of(
                "at", LocalDateTime.now().toString().replace('T', ' ').substring(0, 19),
                "user", userName == null ? "" : userName,
                "body", body == null ? "" : body
        ));
        inquiry.setNotesJson(Jsons.toJson(notes));
        inquiryMapper.updateById(inquiry);
        return inquiry;
    }

    public Inquiry patch(Long id, String nextFollowAt, Integer starred) {
        Inquiry inquiry = owned(id);
        if (nextFollowAt != null) {
            if (nextFollowAt.isBlank()) {
                inquiry.setNextFollowAt(null);
            } else {
                String iso = nextFollowAt.trim().replace(' ', 'T');
                if (iso.length() == 16) {
                    iso += ":00";
                }
                inquiry.setNextFollowAt(LocalDateTime.parse(iso));
            }
        }
        if (starred != null) {
            inquiry.setStarred(starred);
        }
        inquiryMapper.updateById(inquiry);
        return inquiry;
    }

    public long countOverdue(Long tenantId) {
        LocalDateTime now = LocalDateTime.now();
        return inquiryMapper.selectList(new LambdaQueryWrapper<Inquiry>().eq(Inquiry::getTenantId, tenantId))
                .stream()
                .filter(row -> row.getNextFollowAt() != null && row.getNextFollowAt().isBefore(now)
                        && !"quoted".equals(row.getStatus()) && !"lost".equals(row.getStatus()))
                .count();
    }

    public List<String> countries() {
        return inquiryMapper.selectList(new LambdaQueryWrapper<Inquiry>()
                        .eq(Inquiry::getTenantId, tenantService.workingTenantId()))
                .stream()
                .map(Inquiry::getCountry)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted()
                .toList();
    }

    private Inquiry owned(Long id) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !inquiry.getTenantId().equals(tenantService.workingTenantId())) {
            throw new BizException(404, "inquiry not found");
        }
        return inquiry;
    }

    public String exportCsv(String status) {
        List<Inquiry> list = adminList(status, 1, 10_000).getList();
        StringBuilder sb = new StringBuilder();
        sb.append("id,createdAt,status,source,starred,name,company,email,phone,country,whatsapp,product,quantity,utm,message\n");
        for (Inquiry row : list) {
            sb.append(csv(row.getId())).append(',')
                    .append(csv(row.getCreatedAt())).append(',')
                    .append(csv(row.getStatus())).append(',')
                    .append(csv(row.getSource())).append(',')
                    .append(csv(row.getStarred())).append(',')
                    .append(csv(row.getName())).append(',')
                    .append(csv(row.getCompany())).append(',')
                    .append(csv(row.getEmail())).append(',')
                    .append(csv(row.getPhone())).append(',')
                    .append(csv(row.getCountry())).append(',')
                    .append(csv(row.getWhatsapp())).append(',')
                    .append(csv(row.getProductName())).append(',')
                    .append(csv(row.getQuantity())).append(',')
                    .append(csv(row.getUtmJson())).append(',')
                    .append(csv(row.getMessage()))
                    .append('\n');
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

    public long countThisWeek(Long tenantId) {
        return inquiryMapper.selectCount(new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getTenantId, tenantId)
                .ge(Inquiry::getCreatedAt, LocalDateTime.now().minusDays(7)));
    }

    public long countAll(Long tenantId) {
        return inquiryMapper.selectCount(new LambdaQueryWrapper<Inquiry>().eq(Inquiry::getTenantId, tenantId));
    }

    public Map<String, Long> funnel(Long tenantId) {
        List<Inquiry> all = inquiryMapper.selectList(new LambdaQueryWrapper<Inquiry>().eq(Inquiry::getTenantId, tenantId));
        Map<String, Long> out = new java.util.LinkedHashMap<>();
        out.put("new", 0L);
        out.put("following", 0L);
        out.put("quoted", 0L);
        out.put("lost", 0L);
        for (Inquiry row : all) {
            String key = row.getStatus() == null ? "new" : row.getStatus();
            out.put(key, out.getOrDefault(key, 0L) + 1);
        }
        out.put("total", (long) all.size());
        return out;
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
        private String productName;
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

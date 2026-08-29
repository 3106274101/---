package com.tradehub.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.exception.BizException;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.config.SecurityConfig;
import com.tradehub.security.LoginUser;
import com.tradehub.theme.SiteTemplateCatalog;
import com.tradehub.theme.SiteTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenantService {
    private final TenantMapper tenantMapper;
    private final SiteMapper siteMapper;
    private final DomainMapper domainMapper;
    private final SiteTemplateService siteTemplateService;

    public List<Tenant> listTenants() {
        requireSuper();
        return tenantMapper.selectList(new LambdaQueryWrapper<Tenant>().orderByDesc(Tenant::getId));
    }

    public Tenant saveTenant(Tenant body) {
        requireSuper();
        if (body.getStatus() == null) {
            body.setStatus(1);
        }
        if (!StringUtils.hasText(body.getPackageCode())) {
            body.setPackageCode("standard");
        }
        Tenant dup = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getCode, body.getCode())
                .ne(body.getId() != null, Tenant::getId, body.getId())
                .last("limit 1"));
        if (dup != null) {
            throw new BizException(422, "tenant code already exists");
        }
        if (body.getId() == null) {
            tenantMapper.insert(body);
        } else {
            tenantMapper.updateById(body);
        }
        return body;
    }

    public PageResult<Map<String, Object>> listSites() {
        Long tenantId = workingTenantId();
        List<Site> list = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .eq(Site::getTenantId, tenantId)
                .orderByDesc(Site::getId));
        List<Map<String, Object>> views = list.stream().map(this::siteView).toList();
        return new PageResult<>(views, views.size(), 1, views.size());
    }

    public Site saveSite(Site body) {
        Long tenantId = workingTenantId();
        body.setTenantId(tenantId);
        if (body.getId() == null) {
            siteMapper.insert(body);
            Domain domain = new Domain();
            domain.setTenantId(tenantId);
            domain.setSiteId(body.getId());
            domain.setHost(body.getCode() + ".local");
            domain.setIsPrimary(1);
            domainMapper.insert(domain);
        } else {
            Site db = siteMapper.selectById(body.getId());
            assertOwned(db);
            if (!StringUtils.hasText(body.getBrandJson())) {
                body.setBrandJson(db.getBrandJson());
            }
            if (!StringUtils.hasText(body.getSeoJson())) {
                body.setSeoJson(db.getSeoJson());
            }
            siteMapper.updateById(body);
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> saveSitePayload(Map<String, Object> body) {
        Site site;
        Object idRaw = body.get("id");
        boolean creating = idRaw == null;
        if (creating) {
            site = new Site();
            site.setTenantId(workingTenantId());
            site.setTheme(SiteTemplateCatalog.normalize(
                    body.get("theme") != null ? body.get("theme").toString() : "industrial"));
            site.setDefaultLocale("en");
            site.setLocales("en,zh");
            site.setStatus("building");
        } else {
            site = getSite(Long.valueOf(idRaw.toString()));
        }
        if (body.get("name") != null) {
            site.setName(body.get("name").toString());
        }
        if (body.get("code") != null) {
            site.setCode(body.get("code").toString());
        }
        if (body.get("theme") != null) {
            site.setTheme(SiteTemplateCatalog.normalize(body.get("theme").toString()));
        }
        if (body.get("status") != null) {
            site.setStatus(body.get("status").toString());
        }
        if (body.get("defaultLocale") != null) {
            site.setDefaultLocale(body.get("defaultLocale").toString());
        }
        if (body.get("locales") instanceof List<?> list) {
            String joined = list.stream()
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(java.util.stream.Collectors.joining(","));
            site.setLocales(joined.isBlank() ? "en" : joined);
        } else if (body.get("locales") != null) {
            String joined = java.util.Arrays.stream(body.get("locales").toString().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(java.util.stream.Collectors.joining(","));
            site.setLocales(joined.isBlank() ? "en" : joined);
        }
        if (body.get("brand") instanceof Map<?, ?> brand) {
            site.setBrandJson(Jsons.toJson(brand));
        }
        if (body.get("seo") instanceof Map<?, ?> seo) {
            site.setSeoJson(Jsons.toJson(seo));
        }
        Site saved = saveSite(site);
        if (creating) {
            String templateId = body.get("template") != null ? body.get("template").toString() : saved.getTheme();
            siteTemplateService.provision(saved, templateId);
        }
        return siteView(saved);
    }

    public Site getSite(Long id) {
        Site site = siteMapper.selectById(id);
        assertOwned(site);
        return site;
    }

    public Map<String, Object> siteView(Site site) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", site.getId());
        map.put("tenantId", site.getTenantId());
        map.put("code", site.getCode());
        map.put("name", site.getName());
        map.put("defaultLocale", site.getDefaultLocale());
        map.put("locales", List.of(site.getLocales().split(",")));
        map.put("theme", site.getTheme());
        map.put("status", site.getStatus());
        map.put("brand", Jsons.map(site.getBrandJson()));
        map.put("seo", Jsons.map(site.getSeoJson()));
        List<Domain> domains = domainMapper.selectList(new LambdaQueryWrapper<Domain>()
                .eq(Domain::getSiteId, site.getId()));
        map.put("domains", domains);
        return map;
    }

    public List<Domain> listDomains(Long siteId) {
        getSite(siteId);
        return domainMapper.selectList(new LambdaQueryWrapper<Domain>()
                .eq(Domain::getSiteId, siteId)
                .orderByDesc(Domain::getIsPrimary)
                .orderByDesc(Domain::getId));
    }

    public Domain bindDomain(Long siteId, String host, boolean primary) {
        Site site = getSite(siteId);
        String normalized = normalizeHost(host);
        if (!StringUtils.hasText(normalized)) {
            throw new BizException(422, "invalid host");
        }
        Domain dup = domainMapper.selectOne(new LambdaQueryWrapper<Domain>()
                .eq(Domain::getHost, normalized)
                .last("limit 1"));
        if (dup != null && !dup.getSiteId().equals(siteId)) {
            throw new BizException(422, "host already bound");
        }
        if (dup != null) {
            if (primary) {
                clearPrimary(siteId);
                dup.setIsPrimary(1);
                domainMapper.updateById(dup);
            }
            return dup;
        }
        if (primary) {
            clearPrimary(siteId);
        }
        Domain domain = new Domain();
        domain.setTenantId(site.getTenantId());
        domain.setSiteId(siteId);
        domain.setHost(normalized);
        domain.setIsPrimary(primary || listDomains(siteId).isEmpty() ? 1 : 0);
        domainMapper.insert(domain);
        return domain;
    }

    public Domain setPrimaryDomain(Long id) {
        Domain domain = domainMapper.selectById(id);
        if (domain == null) {
            throw new BizException(404, "domain not found");
        }
        getSite(domain.getSiteId());
        clearPrimary(domain.getSiteId());
        domain.setIsPrimary(1);
        domainMapper.updateById(domain);
        return domain;
    }

    public void unbindDomain(Long id) {
        Domain domain = domainMapper.selectById(id);
        if (domain == null) {
            throw new BizException(404, "domain not found");
        }
        getSite(domain.getSiteId());
        domainMapper.deleteById(id);
    }

    public static String normalizeHost(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "";
        }
        String host = raw.trim().toLowerCase();
        host = host.replaceFirst("^https?://", "");
        int slash = host.indexOf('/');
        if (slash >= 0) {
            host = host.substring(0, slash);
        }
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        if (host.contains(":") && !host.startsWith("[")) {
            host = host.split(":")[0];
        }
        return host.trim();
    }

    public Map<String, Object> checkHost(String raw) {
        String host = normalizeHost(raw);
        Map<String, Object> out = new HashMap<>();
        out.put("host", host);
        if (!StringUtils.hasText(host)) {
            out.put("ok", false);
            out.put("message", "empty host");
            return out;
        }
        try {
            java.net.InetAddress[] addrs = java.net.InetAddress.getAllByName(host);
            out.put("ok", true);
            out.put("addresses", java.util.Arrays.stream(addrs).map(java.net.InetAddress::getHostAddress).toList());
            Domain bound = domainMapper.selectOne(new LambdaQueryWrapper<Domain>().eq(Domain::getHost, host).last("limit 1"));
            out.put("bound", bound != null);
            if (bound != null) {
                out.put("siteId", bound.getSiteId());
            }
        } catch (Exception e) {
            out.put("ok", false);
            out.put("message", e.getMessage());
        }
        return out;
    }

    private void clearPrimary(Long siteId) {
        List<Domain> list = domainMapper.selectList(new LambdaQueryWrapper<Domain>().eq(Domain::getSiteId, siteId));
        for (Domain item : list) {
            if (Integer.valueOf(1).equals(item.getIsPrimary())) {
                item.setIsPrimary(0);
                domainMapper.updateById(item);
            }
        }
    }

    public Site resolveByHostOrCode(String host, String code, String fallbackCode) {
        if (StringUtils.hasText(code)) {
            Site byCode = siteMapper.selectOne(new LambdaQueryWrapper<Site>().eq(Site::getCode, code).last("limit 1"));
            if (byCode != null) {
                return byCode;
            }
        }
        if (StringUtils.hasText(host)) {
            String hostname = host.split(":")[0];
            Domain domain = domainMapper.selectOne(new LambdaQueryWrapper<Domain>().eq(Domain::getHost, hostname));
            if (domain != null) {
                return siteMapper.selectById(domain.getSiteId());
            }
        }
        String use = StringUtils.hasText(fallbackCode) ? fallbackCode : code;
        return siteMapper.selectOne(new LambdaQueryWrapper<Site>().eq(Site::getCode, use).last("limit 1"));
    }

    public Long workingTenantId() {
        LoginUser user = SecurityConfig.currentUser();
        if (user != null && user.isSuperAdmin()) {
            if (TenantContext.getTenantId() != null) {
                return TenantContext.getTenantId();
            }
            Tenant first = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>().last("limit 1"));
            if (first != null) {
                return first.getId();
            }
        }
        if (user != null && user.getTenantId() != null) {
            return user.getTenantId();
        }
        if (TenantContext.getTenantId() != null) {
            return TenantContext.getTenantId();
        }
        throw new BizException(401, "tenant not resolved");
    }

    private void requireSuper() {
        LoginUser user = SecurityConfig.currentUser();
        if (user == null || !user.isSuperAdmin()) {
            throw new BizException(403, "super admin only");
        }
    }

    private void assertOwned(Site site) {
        if (site == null) {
            throw new BizException(404, "site not found");
        }
        LoginUser user = SecurityConfig.currentUser();
        if (user != null && !user.isSuperAdmin() && !site.getTenantId().equals(user.getTenantId())) {
            throw new BizException(403, "forbidden");
        }
    }
}

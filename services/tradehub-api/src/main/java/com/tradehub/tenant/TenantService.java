package com.tradehub.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.Jsons;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.exception.BizException;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.config.SecurityConfig;
import com.tradehub.security.LoginUser;
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

    public List<Tenant> listTenants() {
        requireSuper();
        return tenantMapper.selectList(new LambdaQueryWrapper<Tenant>().orderByDesc(Tenant::getId));
    }

    public Tenant saveTenant(Tenant body) {
        requireSuper();
        if (body.getId() == null) {
            tenantMapper.insert(body);
        } else {
            tenantMapper.updateById(body);
        }
        return body;
    }

    public PageResult<Site> listSites() {
        Long tenantId = workingTenantId();
        List<Site> list = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .eq(Site::getTenantId, tenantId)
                .orderByDesc(Site::getId));
        return new PageResult<>(list, list.size(), 1, list.size());
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
            siteMapper.updateById(body);
        }
        return body;
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

    public Site resolveByHostOrCode(String host, String code, String fallbackCode) {
        if (StringUtils.hasText(host)) {
            String hostname = host.split(":")[0];
            Domain domain = domainMapper.selectOne(new LambdaQueryWrapper<Domain>().eq(Domain::getHost, hostname));
            if (domain != null) {
                return siteMapper.selectById(domain.getSiteId());
            }
        }
        String use = StringUtils.hasText(code) ? code : fallbackCode;
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

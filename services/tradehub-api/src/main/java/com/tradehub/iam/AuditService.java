package com.tradehub.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.api.PageResult;
import com.tradehub.common.tenant.TenantContext;
import com.tradehub.config.SecurityConfig;
import com.tradehub.security.LoginUser;
import com.tradehub.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogMapper auditLogMapper;
    private final TenantService tenantService;
    private final UserAccountMapper userAccountMapper;

    public void record(String action, String targetType, String targetId, String detail) {
        try {
            LoginUser user = SecurityConfig.currentUser();
            AuditLog row = new AuditLog();
            Long tenantId = TenantContext.getTenantId();
            if (tenantId == null && user != null) {
                tenantId = user.getTenantId();
            }
            if (tenantId == null) {
                try {
                    tenantId = tenantService.workingTenantId();
                } catch (Exception ignored) {
                    tenantId = null;
                }
            }
            row.setTenantId(tenantId);
            row.setUserId(user == null ? null : user.getUserId());
            row.setAction(action);
            row.setTargetType(targetType);
            row.setTargetId(targetId);
            row.setDetailJson(detail);
            row.setCreatedAt(LocalDateTime.now());
            auditLogMapper.insert(row);
        } catch (Exception e) {
            log.warn("audit log failed: {}", e.getMessage());
        }
    }

    public PageResult<AuditLog> list(String action, long page, long size) {
        LoginUser user = SecurityConfig.currentUser();
        LambdaQueryWrapper<AuditLog> qw = new LambdaQueryWrapper<AuditLog>().orderByDesc(AuditLog::getId);
        if (user == null || !user.isSuperAdmin()) {
            qw.eq(AuditLog::getTenantId, tenantService.workingTenantId());
        } else {
            try {
                qw.eq(AuditLog::getTenantId, tenantService.workingTenantId());
            } catch (Exception ignored) {
                // super admin without tenant still sees recent logs
            }
        }
        if (StringUtils.hasText(action)) {
            qw.eq(AuditLog::getAction, action);
        }
        List<AuditLog> all = auditLogMapper.selectList(qw);
        java.util.Set<Long> userIds = all.stream().map(AuditLog::getUserId).filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        java.util.Map<Long, String> names = new java.util.HashMap<>();
        if (!userIds.isEmpty()) {
            for (UserAccount u : userAccountMapper.selectList(new LambdaQueryWrapper<UserAccount>().in(UserAccount::getId, userIds))) {
                names.put(u.getId(), u.getDisplayName() == null ? u.getUsername() : u.getDisplayName());
            }
        }
        for (AuditLog row : all) {
            row.setUserName(names.getOrDefault(row.getUserId(), row.getUserId() == null ? "system" : "#" + row.getUserId()));
        }
        int from = (int) Math.max(0, (page - 1) * size);
        int to = (int) Math.min(all.size(), from + size);
        return new PageResult<>(from < to ? all.subList(from, to) : List.of(), all.size(), page, size);
    }
}

package com.tradehub.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.exception.BizException;
import com.tradehub.config.SecurityConfig;
import com.tradehub.security.LoginUser;
import com.tradehub.tenant.TenantService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {
    private static final Set<String> ROLES = Set.of("OWNER", "EDITOR", "SALES");
    private final UserAccountMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;

    public List<Map<String, Object>> list() {
        return userMapper.selectList(new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getTenantId, tenantService.workingTenantId())
                        .orderByDesc(UserAccount::getId))
                .stream()
                .map(this::view)
                .toList();
    }

    public Map<String, Object> save(MemberSaveRequest req) {
        LoginUser actor = SecurityConfig.currentUser();
        if (req.getId() == null) {
            if (!StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
                throw new BizException(422, "username and password required");
            }
        }
        UserAccount user = req.getId() == null ? new UserAccount() : userMapper.selectById(req.getId());
        if (user == null) {
            throw new BizException(404, "member not found");
        }
        Long tenantId = req.getTenantId() != null && actor != null && actor.isSuperAdmin()
                ? req.getTenantId()
                : tenantService.workingTenantId();
        assertSameTenant(user.getId() == null ? tenantId : user.getTenantId(), tenantId, actor);
        if (user.getId() == null) {
            if (userMapper.selectOne(new LambdaQueryWrapper<UserAccount>()
                    .eq(UserAccount::getUsername, req.getUsername())) != null) {
                throw new BizException(422, "username already exists");
            }
            user.setUsername(req.getUsername());
            user.setTenantId(tenantId);
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            user.setIsSuperAdmin(0);
        } else if (!actor.isSuperAdmin() && !tenantId.equals(user.getTenantId())) {
            throw new BizException(403, "forbidden");
        }
        if (StringUtils.hasText(req.getPassword()) && user.getId() != null) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        if (StringUtils.hasText(req.getDisplayName()) || user.getId() == null) {
            user.setDisplayName(req.getDisplayName());
        }
        if (req.getEmail() != null || user.getId() == null) {
            user.setEmail(req.getEmail());
        }
        if (StringUtils.hasText(req.getRoleCode()) || user.getId() == null) {
            user.setRoleCode(normalizeRole(req.getRoleCode(), actor));
        }
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        } else if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getId() != null && actor != null && user.getId().equals(actor.getUserId()) && Integer.valueOf(0).equals(user.getStatus())) {
            throw new BizException(422, "cannot disable yourself");
        }
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.updateById(user);
        }
        return view(user);
    }

    public Map<String, Object> updateStatus(Long id, Integer status) {
        UserAccount user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "member not found");
        }
        LoginUser actor = SecurityConfig.currentUser();
        if (actor != null && !actor.isSuperAdmin() && !tenantService.workingTenantId().equals(user.getTenantId())) {
            throw new BizException(403, "forbidden");
        }
        if (actor != null && id.equals(actor.getUserId()) && Integer.valueOf(0).equals(status)) {
            throw new BizException(422, "cannot disable yourself");
        }
        user.setStatus(status == null ? 0 : status);
        userMapper.updateById(user);
        return view(user);
    }

    public void changeOwnPassword(String oldPassword, String newPassword) {
        LoginUser actor = SecurityConfig.currentUser();
        if (actor == null) {
            throw new BizException(401, "unauthorized");
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new BizException(422, "password too short");
        }
        UserAccount user = userMapper.selectById(actor.getUserId());
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BizException(422, "old password incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private String normalizeRole(String roleCode, LoginUser actor) {
        String role = StringUtils.hasText(roleCode) ? roleCode.trim().toUpperCase() : "EDITOR";
        if ("SUPER".equals(role) || "SUPER_ADMIN".equals(role)) {
            if (actor == null || !actor.isSuperAdmin()) {
                throw new BizException(403, "cannot assign super admin");
            }
            return "OWNER";
        }
        if (!ROLES.contains(role)) {
            throw new BizException(422, "invalid role");
        }
        return role;
    }

    private void assertSameTenant(Long existingTenant, Long targetTenant, LoginUser actor) {
        if (actor != null && actor.isSuperAdmin()) {
            return;
        }
        if (existingTenant != null && !existingTenant.equals(targetTenant)) {
            throw new BizException(403, "forbidden");
        }
    }

    private Map<String, Object> view(UserAccount user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("tenantId", user.getTenantId());
        map.put("username", user.getUsername());
        map.put("displayName", user.getDisplayName());
        map.put("email", user.getEmail());
        map.put("roleCode", user.getRoleCode());
        map.put("status", user.getStatus());
        map.put("superAdmin", Integer.valueOf(1).equals(user.getIsSuperAdmin()));
        map.put("lastLoginAt", user.getLastLoginAt());
        map.put("permissions", RolePermissions.readable(Integer.valueOf(1).equals(user.getIsSuperAdmin()), user.getRoleCode()));
        return map;
    }

    @Data
    public static class MemberSaveRequest {
        private Long id;
        private Long tenantId;
        private String username;
        private String password;
        private String displayName;
        private String email;
        private String roleCode;
        private Integer status;
    }
}

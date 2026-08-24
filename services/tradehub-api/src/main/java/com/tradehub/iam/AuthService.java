package com.tradehub.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.exception.BizException;
import com.tradehub.security.JwtUtil;
import com.tradehub.security.LoginUser;
import com.tradehub.tenant.Tenant;
import com.tradehub.tenant.TenantMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountMapper userMapper;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginRequest req) {
        UserAccount user = userMapper.selectOne(new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BizException(401, "invalid username or password");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(403, "account disabled");
        }
        LoginUser principal = LoginUser.builder()
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .roleCode(user.getRoleCode())
                .superAdmin(Integer.valueOf(1).equals(user.getIsSuperAdmin()))
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("token", jwtUtil.create(principal));
        data.put("user", profile(principal, user));
        return data;
    }

    public Map<String, Object> profile(LoginUser principal, UserAccount user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", principal.getUserId());
        map.put("username", principal.getUsername());
        map.put("displayName", principal.getDisplayName());
        map.put("roleCode", principal.getRoleCode());
        map.put("superAdmin", principal.isSuperAdmin());
        map.put("tenantId", principal.getTenantId());
        if (principal.getTenantId() != null) {
            Tenant tenant = tenantMapper.selectById(principal.getTenantId());
            if (tenant != null) {
                map.put("tenantName", tenant.getName());
                map.put("tenantCode", tenant.getCode());
            }
        }
        if (user != null) {
            map.put("email", user.getEmail());
        }
        return map;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }
}

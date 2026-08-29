package com.tradehub.iam;

import com.tradehub.common.exception.BizException;
import com.tradehub.config.SecurityConfig;
import com.tradehub.security.LoginUser;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    public LoginUser requireLogin() {
        LoginUser user = SecurityConfig.currentUser();
        if (user == null) {
            throw new BizException(401, "unauthorized");
        }
        return user;
    }

    public LoginUser require(Permission permission, boolean write) {
        LoginUser user = requireLogin();
        if (!RolePermissions.allows(user.isSuperAdmin(), user.getRoleCode(), permission, write)) {
            throw new BizException(403, "insufficient role: " + permission.name());
        }
        return user;
    }

    public boolean can(Permission permission, boolean write) {
        LoginUser user = SecurityConfig.currentUser();
        if (user == null) {
            return false;
        }
        return RolePermissions.allows(user.isSuperAdmin(), user.getRoleCode(), permission, write);
    }
}

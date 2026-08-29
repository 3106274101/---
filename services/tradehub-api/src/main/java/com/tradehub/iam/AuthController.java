package com.tradehub.iam;

import com.tradehub.common.api.R;
import com.tradehub.config.SecurityConfig;
import com.tradehub.security.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserAccountMapper userMapper;
    private final MemberService memberService;

    @PostMapping("/login")
    public R<?> login(@Valid @RequestBody AuthService.LoginRequest req) {
        return R.ok(authService.login(req));
    }

    @GetMapping("/me")
    public R<?> me() {
        LoginUser user = SecurityConfig.currentUser();
        if (user == null) {
            return R.fail(401, "unauthorized");
        }
        UserAccount account = userMapper.selectById(user.getUserId());
        return R.ok(authService.profile(user, account));
    }

    @PostMapping("/password")
    public R<?> password(@Valid @RequestBody AuthService.PasswordRequest req) {
        memberService.changeOwnPassword(req.getOldPassword(), req.getNewPassword());
        return R.ok();
    }
}

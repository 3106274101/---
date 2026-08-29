package com.tradehub.iam;

import com.tradehub.common.api.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public R<?> list() {
        return R.ok(memberService.list());
    }

    @PostMapping
    public R<?> create(@RequestBody MemberService.MemberSaveRequest req) {
        req.setId(null);
        return R.ok(memberService.save(req));
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody MemberService.MemberSaveRequest req) {
        req.setId(id);
        return R.ok(memberService.save(req));
    }

    @PostMapping("/{id}/status")
    public R<?> status(@PathVariable Long id, @RequestBody StatusBody body) {
        return R.ok(memberService.updateStatus(id, body.getStatus()));
    }

    @Data
    public static class StatusBody {
        private Integer status;
    }
}

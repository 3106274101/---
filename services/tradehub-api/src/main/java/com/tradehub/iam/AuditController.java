package com.tradehub.iam;

import com.tradehub.common.api.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping
    public R<?> list(@RequestParam(required = false) String action,
                     @RequestParam(defaultValue = "1") long page,
                     @RequestParam(defaultValue = "50") long pageSize) {
        return R.ok(auditService.list(action, page, pageSize));
    }
}

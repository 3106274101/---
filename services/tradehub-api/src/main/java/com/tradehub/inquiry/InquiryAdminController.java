package com.tradehub.inquiry;

import com.tradehub.common.api.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/inquiries")
@RequiredArgsConstructor
public class InquiryAdminController {
    private final InquiryService inquiryService;
    private final InquiryMapper inquiryMapper;

    @GetMapping
    public R<?> list(@RequestParam(required = false) String status,
                     @RequestParam(required = false) String country,
                     @RequestParam(required = false) String q,
                     @RequestParam(required = false) Boolean starred,
                     @RequestParam(required = false) Boolean overdue,
                     @RequestParam(defaultValue = "1") long page,
                     @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(inquiryService.adminList(status, country, q, starred, overdue, page, pageSize));
    }

    @GetMapping("/countries")
    public R<?> countries() {
        return R.ok(inquiryService.countries());
    }

    @GetMapping("/export")
    public R<?> export(@RequestParam(required = false) String status) {
        return R.ok(java.util.Map.of(
                "filename", "inquiries.csv",
                "csv", inquiryService.exportCsv(status)
        ));
    }

    @GetMapping("/{id}")
    public R<?> one(@PathVariable Long id) {
        return R.ok(inquiryMapper.selectById(id));
    }

    @PostMapping("/{id}/status")
    public R<?> status(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        inquiryService.updateStatus(id, body.get("status"));
        return R.ok();
    }

    @PostMapping("/{id}/assign")
    public R<?> assign(@PathVariable Long id, @RequestBody java.util.Map<String, Long> body) {
        inquiryService.assign(id, body.get("userId"));
        return R.ok();
    }

    @PostMapping("/{id}/notes")
    public R<?> note(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        var user = com.tradehub.config.SecurityConfig.currentUser();
        return R.ok(inquiryService.addNote(id, body.get("body"), user == null ? "" : user.getDisplayName()));
    }

    @PostMapping("/{id}/follow")
    public R<?> follow(@PathVariable Long id, @RequestBody java.util.Map<String, Object> body) {
        Integer starred = body.get("starred") instanceof Number n ? n.intValue() : null;
        String next = body.get("nextFollowAt") == null ? null : String.valueOf(body.get("nextFollowAt"));
        return R.ok(inquiryService.patch(id, next, starred));
    }
}

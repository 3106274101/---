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
                     @RequestParam(defaultValue = "1") long page,
                     @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(inquiryService.adminList(status, page, pageSize));
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
}

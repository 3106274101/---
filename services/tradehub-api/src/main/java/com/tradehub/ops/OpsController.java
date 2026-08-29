package com.tradehub.ops;

import com.tradehub.common.api.R;
import com.tradehub.notify.MailNotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OpsController {
    private final SearchService searchService;
    private final MailNotifyService mailNotifyService;

    @GetMapping("/search")
    public R<?> search(@RequestParam String q) {
        return R.ok(searchService.search(q));
    }

    @PostMapping("/mail/test")
    public R<?> testMail(@RequestBody Map<String, String> body) {
        return R.ok(mailNotifyService.sendTest(body.get("to")));
    }
}

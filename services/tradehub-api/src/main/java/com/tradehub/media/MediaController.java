package com.tradehub.media;

import com.tradehub.common.api.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @GetMapping
    public R<?> list() {
        return R.ok(mediaService.list());
    }

    @PostMapping("/upload")
    public R<?> upload(@RequestParam("file") MultipartFile file,
                       @RequestParam(required = false) String alt) throws Exception {
        return R.ok(mediaService.upload(file, alt));
    }
}

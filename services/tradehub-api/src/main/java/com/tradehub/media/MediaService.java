package com.tradehub.media;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.common.exception.BizException;
import com.tradehub.storage.ObjectStorage;
import com.tradehub.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {
    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "application/pdf");
    private final AssetMapper assetMapper;
    private final TenantService tenantService;
    private final ObjectStorage objectStorage;

    public Asset upload(MultipartFile file, String alt) throws IOException {
        if (file.isEmpty()) {
            throw new BizException("empty file");
        }
        String mime = file.getContentType();
        if (mime == null || !ALLOWED.contains(mime)) {
            throw new BizException("unsupported file type");
        }
        Long tenantId = tenantService.workingTenantId();
        String ext = ext(file.getOriginalFilename());
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        String key = tenantId + "/" + name;
        var stored = objectStorage.put(key, file.getInputStream(), file.getSize(), mime);
        Asset asset = new Asset();
        asset.setTenantId(tenantId);
        asset.setUrl(stored.url());
        asset.setOriginalName(file.getOriginalFilename());
        asset.setMime(mime);
        asset.setSizeBytes(file.getSize());
        asset.setAlt(alt);
        assetMapper.insert(asset);
        return asset;
    }

    public List<Asset> list() {
        return assetMapper.selectList(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, tenantService.workingTenantId())
                .orderByDesc(Asset::getId));
    }

    private String ext(String original) {
        if (original == null || !original.contains(".")) {
            return "";
        }
        return original.substring(original.lastIndexOf('.')).toLowerCase();
    }
}

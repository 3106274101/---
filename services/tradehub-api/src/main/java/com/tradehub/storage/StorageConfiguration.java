package com.tradehub.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class StorageConfiguration {

    @Bean
    public ObjectStorage objectStorage(
            @Value("${tradehub.storage.type:local}") String type,
            @Value("${tradehub.upload.dir}") String uploadDir,
            @Value("${tradehub.upload.public-base}") String publicBase,
            @Value("${tradehub.storage.s3-endpoint:}") String endpoint,
            @Value("${tradehub.storage.s3-region:us-east-1}") String region,
            @Value("${tradehub.storage.s3-bucket:tradehub}") String bucket,
            @Value("${tradehub.storage.s3-access-key:}") String accessKey,
            @Value("${tradehub.storage.s3-secret-key:}") String secretKey,
            @Value("${tradehub.storage.s3-public-base:}") String s3PublicBase
    ) {
        if ("s3".equalsIgnoreCase(type) && StringUtils.hasText(endpoint) && StringUtils.hasText(accessKey)) {
            return new S3ObjectStorage(endpoint, region, bucket, accessKey, secretKey, s3PublicBase);
        }
        return new LocalObjectStorage(uploadDir, publicBase);
    }
}

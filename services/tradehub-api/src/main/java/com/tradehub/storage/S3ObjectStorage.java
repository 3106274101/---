package com.tradehub.storage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;

public class S3ObjectStorage implements ObjectStorage {
    private final S3Client client;
    private final String bucket;
    private final String publicBase;

    public S3ObjectStorage(String endpoint, String region, String bucket, String accessKey, String secretKey, String publicBase) {
        this.bucket = bucket;
        String base = publicBase;
        if (base == null || base.isBlank()) {
            String ep = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
            base = ep + "/" + bucket;
        }
        this.publicBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        this.client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region == null || region.isBlank() ? "us-east-1" : region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Override
    public StoredObject put(String key, InputStream data, long size, String contentType) {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key);
        if (contentType != null) {
            builder.contentType(contentType);
        }
        client.putObject(builder.build(), RequestBody.fromInputStream(data, size));
        return new StoredObject(key, publicBase + "/" + key);
    }
}

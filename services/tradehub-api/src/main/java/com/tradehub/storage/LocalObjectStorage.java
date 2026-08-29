package com.tradehub.storage;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
public class LocalObjectStorage implements ObjectStorage {
    private final Path root;
    private final String publicBase;

    public LocalObjectStorage(String uploadDir, String publicBase) {
        this.root = Path.of(uploadDir);
        this.publicBase = publicBase.endsWith("/") ? publicBase.substring(0, publicBase.length() - 1) : publicBase;
    }

    @Override
    public StoredObject put(String key, InputStream data, long size, String contentType) {
        try {
            Path dest = root.toAbsolutePath().normalize().resolve(key).normalize();
            if (!dest.startsWith(root.toAbsolutePath().normalize())) {
                throw new IllegalArgumentException("invalid storage key");
            }
            Files.createDirectories(dest.getParent());
            Files.copy(data, dest, StandardCopyOption.REPLACE_EXISTING);
            return new StoredObject(key, publicBase + "/" + key.replace('\\', '/'));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

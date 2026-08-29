package com.tradehub.storage;

import java.io.InputStream;

public interface ObjectStorage {
    StoredObject put(String key, InputStream data, long size, String contentType);

    record StoredObject(String key, String url) {
    }
}

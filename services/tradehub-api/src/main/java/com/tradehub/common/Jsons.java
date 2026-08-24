package com.tradehub.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Jsons {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Jsons() {}

    @SneakyThrows
    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String s) {
            return s;
        }
        return MAPPER.writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, TypeReference<T> type) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return MAPPER.readValue(json, type);
    }

    public static Map<String, Object> map(String json) {
        Map<String, Object> value = fromJson(json, new TypeReference<>() {});
        return value == null ? Collections.emptyMap() : value;
    }

    public static List<Object> list(String json) {
        List<Object> value = fromJson(json, new TypeReference<>() {});
        return value == null ? Collections.emptyList() : value;
    }
}

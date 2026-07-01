package com.xxw.engine;
import java.util.List;
import java.util.Map;

public interface KVEngine {
    void put(String key, String value);
    String get(String key);
    String remove(String key);
    int size();
    List<String> keys();
    Map<String, String> rangeQuery(String min, String max);
}

package com.xxw.engine;
import com.xxw.collection.MyLruCache;
import com.xxw.collection.MySkipList;

import java.util.List;
import java.util.Map;

public class KVEngineImpl implements KVEngine{

    private final MyLruCache cache;
    private final MySkipList skipList;

    public KVEngineImpl(int capacity) {
        this.skipList = new MySkipList();
        this.cache = new MyLruCache(capacity, skipList);
    }


    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public String remove(String key) {
        cache.remove(key);
        return key;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public List<String> keys() {
        return skipList.keysInorder();
    }

    @Override
    public Map<String, String> rangeQuery(String min, String max) {
        return skipList.rangeQuery(min, max);
    }
}

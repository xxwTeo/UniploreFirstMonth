package com.xxw.simpleloganalysissystem.service.impl;
import com.xxw.simpleloganalysissystem.entity.LogRecord;
import com.xxw.simpleloganalysissystem.service.LogAnalyzer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CountBasedAnalyzer extends LogAnalyzer {

    private final Map<String, Integer> map = new ConcurrentHashMap<>();

    @Override
    public void analyze(LogRecord logRecord) {
        String level = logRecord.getLevel();
        map.merge(level,1, Integer::sum);
    }

    @Override
    public Map<String, Integer> getCount() {
        return map;
    }
}

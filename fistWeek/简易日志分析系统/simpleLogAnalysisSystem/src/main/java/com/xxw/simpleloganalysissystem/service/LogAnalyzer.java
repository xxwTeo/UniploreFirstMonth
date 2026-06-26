package com.xxw.simpleloganalysissystem.service;

import com.xxw.simpleloganalysissystem.entity.LogRecord;
import org.springframework.boot.logging.LogLevel;

import java.util.Map;

public abstract class LogAnalyzer {

    public abstract void analyze(LogRecord logRecord);

    public abstract Map<String, Integer> getCount();
}

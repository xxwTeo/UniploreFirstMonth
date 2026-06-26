package com.xxw.simpleloganalysissystem.util;

import com.xxw.simpleloganalysissystem.entity.LogRecord;
import com.xxw.simpleloganalysissystem.service.LogAnalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoggerAnalyzer {
    public final LogAnalyzer logAnalyzer;
    public final ExecutorService pool;

    public LoggerAnalyzer(LogAnalyzer logAnalyzer, int threadCount) {
        this.logAnalyzer = logAnalyzer;
        this.pool = Executors.newFixedThreadPool(threadCount);
    }

    public void analyzer(String filePath) throws IOException, InterruptedException  {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null){
                String finalLine = line;
                pool.submit( () -> logAnalyzer.analyze(new LogRecord(finalLine)));
            }
        }finally {
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}

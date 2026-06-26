package com.xxw.simpleloganalysissystem.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResultWriter {
    public static void write(Map<String, Integer> result , String filePath)
            throws IOException {
        try(BufferedWriter writer = new BufferedWriter( new FileWriter(filePath, StandardCharsets.UTF_8))
                ) {
            result.forEach((level, count) ->{
                try {
                    writer.write(level + ":" + count);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

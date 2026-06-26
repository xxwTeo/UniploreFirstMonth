package com.xxw.simpleloganalysissystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;


class SimpleLogAnalysisSystemApplicationTests {

    @Test
    void contextLoads() throws IOException {
        String logFile = "app.log";
        try (
                BufferedWriter write = new BufferedWriter(new FileWriter(logFile));
        ){
            write.write("[INFO] 2025-01-01 10:00:00 User login success");
        }

    }

}

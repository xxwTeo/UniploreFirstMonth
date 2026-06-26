package com.xxw.simpleloganalysissystem;

import com.xxw.simpleloganalysissystem.factory.AnalyzerFactory;
import com.xxw.simpleloganalysissystem.service.LogAnalyzer;
import com.xxw.simpleloganalysissystem.util.LoggerAnalyzer;
import com.xxw.simpleloganalysissystem.util.ResultWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class SimpleLogAnalysisSystemApplication {

    public static void main(String[] args) throws Exception {

        String logFile = "app.log";
        String result = "result.txt";

        try {
            //1.用反射创建分析器(LogAnalyzer)
            LogAnalyzer analyzer = AnalyzerFactory.creatAnalyzer("com.xxw.simpleloganalysissystem.service.impl.CountBasedAnalyzer");

            //2.多线程分析
            LoggerAnalyzer loggerAnalyzer = new LoggerAnalyzer(analyzer, 4);
            loggerAnalyzer.analyzer(logFile);

            //3.写结果
            ResultWriter.write(analyzer.getCount(), result);

            System.out.println("分析成功" + result);
        } catch (Exception e) {
            System.out.println("分析失败" + e.getLocalizedMessage());
        }

    }

}

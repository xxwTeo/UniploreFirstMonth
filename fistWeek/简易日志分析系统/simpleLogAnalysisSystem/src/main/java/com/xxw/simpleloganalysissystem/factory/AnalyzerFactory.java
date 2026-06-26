package com.xxw.simpleloganalysissystem.factory;

import com.xxw.simpleloganalysissystem.service.LogAnalyzer;

import java.lang.reflect.Constructor;

public class AnalyzerFactory {

    public static LogAnalyzer creatAnalyzer(String className) throws Exception{
        Class clazz = Class.forName(className);
        return (LogAnalyzer) clazz.getDeclaredConstructor().newInstance();
    }
}

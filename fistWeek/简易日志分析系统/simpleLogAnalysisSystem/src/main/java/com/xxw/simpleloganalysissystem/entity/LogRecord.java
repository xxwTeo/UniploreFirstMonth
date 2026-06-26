package com.xxw.simpleloganalysissystem.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LogRecord {
    private final String logLine;

    public String getLevel(){
        if(logLine == null || !logLine.startsWith("[")){
            return "UNKNOWN";
        }

        int end =logLine.indexOf(']');
        if(end == -1)
            return "UNKNOWN";

        //substring返回新String,保证了多线程环境下的安全性
        //substring为左闭右开
        return logLine.substring(1,end);
    }


}

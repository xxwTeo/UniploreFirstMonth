package com.xxw.coedit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.xxw.coedit.mapper")
public class CoEditApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoEditApplication.class, args);
    }
}
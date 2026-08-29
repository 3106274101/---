package com.tradehub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "com.tradehub", annotationClass = org.apache.ibatis.annotations.Mapper.class)
@EnableScheduling
@EnableAsync
public class TradehubApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradehubApplication.class, args);
    }
}

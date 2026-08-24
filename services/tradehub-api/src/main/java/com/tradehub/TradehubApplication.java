package com.tradehub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tradehub")
public class TradehubApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradehubApplication.class, args);
    }
}

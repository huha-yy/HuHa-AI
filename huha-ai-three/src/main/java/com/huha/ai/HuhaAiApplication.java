package com.huha.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.huha.ai.mapper")
@SpringBootApplication
public class HuhaAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuhaAiApplication.class, args);
    }

}

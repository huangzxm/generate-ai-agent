package com.yupi.generateaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yupi.generateaiagent.mapper")
public class GenerateAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenerateAiAgentApplication.class, args);
    }

}

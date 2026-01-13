package com.example.aicosbackendspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling // 取代 Node.js 的 setInterval
//@EnableCaching    // 取代 Node.js 的 cache 物件
@SpringBootApplication
public class AiCosBackendSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCosBackendSpringBootApplication.class, args);
    }

}

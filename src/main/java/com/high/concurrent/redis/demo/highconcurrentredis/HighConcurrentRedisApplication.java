package com.high.concurrent.redis.demo.highconcurrentredis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.high.concurrent.redis.demo.highconcurrentredis.mapper")
public class HighConcurrentRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighConcurrentRedisApplication.class, args);
    }

}

package com.zjft.monitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.zjft.monitor.mapper")
public class DemoApplication {

    /**
     * 应用入口
     */
    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
    }
}

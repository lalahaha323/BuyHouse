package com.lala;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@MapperScan("com.lala.mapper")
public class LalaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LalaApplication.class, args);
    }

}

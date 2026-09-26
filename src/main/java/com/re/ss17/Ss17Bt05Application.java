package com.re.ss17;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Ss17Bt05Application {

    public static void main(String[] args) {
        SpringApplication.run(Ss17Bt05Application.class, args);
    }
}

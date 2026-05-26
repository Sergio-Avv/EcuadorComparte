package com.ecuadorcomparte.ecuador_comparte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcuadorcomparteApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcuadorcomparteApplication.class, args);
    }
}

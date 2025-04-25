package com.example;

import com.example.controller.MinioController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MinioExample {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MinioExample.class, args);
        MinioController controller = context.getBean(MinioController.class);
        controller.start();
    }
}
package com.picksy.categoryservice;

import org.springframework.boot.SpringApplication;

public class TestCategoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(CategoryServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

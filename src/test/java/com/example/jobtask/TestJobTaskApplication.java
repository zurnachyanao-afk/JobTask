package com.example.jobtask;

import org.springframework.boot.SpringApplication;

public class TestJobTaskApplication {

    public static void main(String[] args) {
        SpringApplication.from(JobTaskApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

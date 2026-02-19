package com.example.jobtask;

import com.example.jobtask.wallet.config.WalletProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(WalletProperties.class)
@SpringBootApplication
public class JobTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobTaskApplication.class, args);
    }

}

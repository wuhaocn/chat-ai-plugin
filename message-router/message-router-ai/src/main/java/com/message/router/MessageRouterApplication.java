package com.message.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.message.router.config.RongCloudConfig;

/**
 * 消息路由应用主类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@SpringBootApplication
@EnableConfigurationProperties(RongCloudConfig.class)
public class MessageRouterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageRouterApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 
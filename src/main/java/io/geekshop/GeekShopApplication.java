/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop;

import io.geekshop.config.ElasticsearchConfig;
import io.geekshop.options.ConfigOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on Nov, 2020 by @author bobo
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableConfigurationProperties({ConfigOptions.class})
@RestController
@Slf4j
public class GeekShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeekShopApplication.class, args);
        log.info("GeekShop Started! Have Fun!");
    }

    @GetMapping("/health")
    public String hello() {return "Hello GeekShop!";}
}

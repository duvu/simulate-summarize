package com.deemerge.enrichment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EnrichmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnrichmentServiceApplication.class, args);
    }
}

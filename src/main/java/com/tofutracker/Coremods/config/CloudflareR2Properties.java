package com.tofutracker.Coremods.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cloudflare.r2")
public class CloudflareR2Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}

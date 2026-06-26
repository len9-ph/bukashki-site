package com.lgadetsky.bukashki.minio_config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
        String url,
        String publicUrl,
        String username,
        String password,
        String bucket
) {}

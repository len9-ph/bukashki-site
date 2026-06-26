package com.lgadetsky.bukashki.minio_config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.url())
                .credentials(
                        properties.username(),
                        properties.password())
                .build();
    }
}

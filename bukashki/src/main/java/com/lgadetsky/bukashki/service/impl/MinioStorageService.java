package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.StorageException;
import com.lgadetsky.bukashki.minio_config.MinioProperties;
import com.lgadetsky.bukashki.service.StorageService;
import io.minio.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public void upload(String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.bucket())
                            .object(objectKey)
                            .stream(
                                    inputStream,
                                    size,
                                    -1
                            )
                            .contentType(contentType)
                            .build()
            );
        } catch (
                ServerException
                | IOException
                | InsufficientDataException
                | ErrorResponseException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | InvalidResponseException
                | XmlParserException
                | InternalException e) {
            throw new StorageException("Failed to upload object", e);
        }
    }

    @Override
    public InputStream download(String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .build());
        } catch (
                ServerException
                | IOException
                | InsufficientDataException
                | ErrorResponseException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | InvalidResponseException
                | XmlParserException
                | InternalException e) {
            throw new StorageException("Failed to upload object", e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .build());
        } catch (
                ServerException
                | IOException
                | InsufficientDataException
                | ErrorResponseException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | InvalidResponseException
                | XmlParserException
                | InternalException e) {
            throw new StorageException("Failed to upload object", e);
        }
    }

}

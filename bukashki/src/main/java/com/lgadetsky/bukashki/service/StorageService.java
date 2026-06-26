package com.lgadetsky.bukashki.service;

import java.io.InputStream;

public interface StorageService {
    void upload(String objectKey, InputStream inputStream, long size, String contentType);

    InputStream download(String objectKey);

    void delete(String objectKey);
}

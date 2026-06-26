package com.lgadetsky.bukashki.service;

import java.io.InputStream;

public interface StorageService {
    String upload(String objectKey, InputStream inputStream, long size, String contentType);

    InputStream download(String objectKey);

    void delete(String objectKey);

    String getUrl(String objectKey);
}

package com.lgadetsky.bukashki.service;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void upload(String objectkey, MultipartFile file);

    InputStream download(String objectKey);

    void delete(String objectKey);
}

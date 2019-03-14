package com.s3upload.uploadservice.strategy;

import org.springframework.web.multipart.MultipartFile;

public interface S3Upload {

    Integer MINUTES = 60 * 1000;
    
    String upload(MultipartFile multipartFile);
    
}
package com.s3upload.uploadservice.utils;

import com.s3upload.uploadservice.config.S3MultiThreadConfig;
import org.springframework.web.multipart.MultipartFile;

public final class UploadInfo {

    private S3MultiThreadConfig s3Config;
    private String fileKey;
    private String uploadId;
    private MultipartFile multipartFile;

    public static UploadInfo build(){
       return new UploadInfo();
    }

    public S3MultiThreadConfig getS3Config() {
        return s3Config;
    }

    public UploadInfo s3Config(S3MultiThreadConfig s3Config) {
        this.s3Config = s3Config;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public UploadInfo fileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public String getUploadId() {
        return uploadId;
    }

    public UploadInfo uploadId(String uploadId) {
        this.uploadId = uploadId;
        return this;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public UploadInfo multipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
        return this;
    }
}
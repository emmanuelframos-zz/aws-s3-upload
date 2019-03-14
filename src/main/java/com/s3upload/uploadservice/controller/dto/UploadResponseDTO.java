package com.s3upload.uploadservice.controller.dto;

public class UploadResponseDTO {

    public String bucket;
    public String fileKey;
    public Long size;
    public String fileName;
    public String contentType;

    public UploadResponseDTO(){}

}
package com.s3upload.uploadservice.utils;

import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;

public final class FileKeyGenerator {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static String generateKey(Long currentTime, MultipartFile multipartFile){
        return new StringBuilder()
                .append(currentTime.toString())
                .append("-")
                .append(sdf.format(currentTime))
                .append("_")
                .append(multipartFile.getOriginalFilename())
                .toString();
    }
}
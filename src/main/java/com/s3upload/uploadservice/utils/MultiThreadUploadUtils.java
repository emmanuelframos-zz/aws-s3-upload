package com.s3upload.uploadservice.utils;

import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.s3upload.uploadservice.config.S3MultiThreadConfig;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadUploadUtils {

    public static List<UploadPartRequest> getUploadRequests(UploadInfo uploadInfo) {

        long fileSize = uploadInfo.getMultipartFile().getSize();
        long partSize = uploadInfo.getS3Config().partSize * 1024 * 1024;
        long filePosition = 0;

        List<UploadPartRequest> uploadRequests = new ArrayList<>();

        for (int part = 1; filePosition < fileSize; part++) {

            partSize = Math.min(partSize, fileSize - filePosition);

            try {
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(uploadInfo.getS3Config().bucketName)
                        .withKey(uploadInfo.getFileKey())
                        .withUploadId(uploadInfo.getUploadId())
                        .withPartNumber(part)
                        .withFileOffset(filePosition)
                        .withInputStream(uploadInfo.getMultipartFile().getInputStream())
                        .withPartSize(partSize);

                uploadRequests.add(uploadRequest);
            }catch (Exception ex){
                throw new RuntimeException("Error on file split.");
            }

            filePosition += partSize;
        }
        return uploadRequests;
    }

    public static String getUploadId(S3MultiThreadConfig s3Config, String fileKey){
        try {
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(s3Config.bucketName, fileKey);
            InitiateMultipartUploadResult initResponse = s3Config.amazonS3().initiateMultipartUpload(initRequest);
            return initResponse.getUploadId();
        }catch (Exception ex){
            throw new RuntimeException("Error on get upload id.");
        }
    }
}
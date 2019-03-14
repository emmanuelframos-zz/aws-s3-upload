package com.s3upload.uploadservice.strategy;

import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.s3upload.uploadservice.config.S3MultiThreadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

class UploadPartCallable implements Callable<UploadPartResult> {

    private Logger logger = LoggerFactory.getLogger(UploadPartCallable.class);

    private S3MultiThreadConfig s3Config;

    private UploadPartRequest uploadPartRequest;

    public UploadPartCallable(S3MultiThreadConfig s3Config, UploadPartRequest uploadPartRequest){
        this.s3Config = s3Config;
        this.uploadPartRequest = uploadPartRequest;
    }

    @Override
    public UploadPartResult call() {
        logger.info("MultiPart - {} - Uploading part {} - Thread {}", uploadPartRequest.getKey(), uploadPartRequest.getPartNumber(), Thread.currentThread().getName());
        return s3Config.amazonS3().uploadPart(uploadPartRequest);
    }
}
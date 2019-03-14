package com.s3upload.uploadservice.strategy;

import com.s3upload.uploadservice.domain.UploadStrategy;
import org.springframework.stereotype.Component;

@Component
public class S3UploadSwitch {
    
    private final S3MultiThreadUpload multiThreadUpload;

    private final S3SingleThreadUpload singleThreadUpload;

    public S3UploadSwitch(S3MultiThreadUpload multiThreadUpload, S3SingleThreadUpload singleThreadUpload) {
        this.multiThreadUpload = multiThreadUpload;
        this.singleThreadUpload = singleThreadUpload;
    }

    public S3Upload switchStrategy(UploadStrategy uploadStrategy){
        return uploadStrategy.isSingleThread()
                    ? singleThreadUpload
                    : multiThreadUpload;
    }
}
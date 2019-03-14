package com.s3upload.uploadservice.domain;

public enum UploadStrategy {

    SINGLE_THREAD,
    MULTI_THREAD;

    public boolean isSingleThread(){
        return UploadStrategy.SINGLE_THREAD.equals(this);
    }
}
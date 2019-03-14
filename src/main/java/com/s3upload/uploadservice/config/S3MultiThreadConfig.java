package com.s3upload.uploadservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class S3MultiThreadConfig extends S3Config {

    @Value("${aws.s3.multiThread.threads}")
    public Integer threads;

    @Value("${aws.s3.multiThread.partSize}")
    public Byte partSize;

    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(threads);
    }
}
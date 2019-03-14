package com.s3upload.uploadservice.config;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3SingleThreadConfig extends S3Config {

    @Bean
    public TransferManager transferManager(){
        return TransferManagerBuilder
            .standard()
            .withS3Client(amazonS3())
            .build();
    }
}
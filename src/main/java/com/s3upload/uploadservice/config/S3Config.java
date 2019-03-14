package com.s3upload.uploadservice.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    public String region;

    @Value("${aws.credentials.accessKey}")
    public String accessKey;

    @Value("${aws.credentials.secretKey}")
    public String secretKey;

    @Value("${aws.s3.bucketName}")
    public String bucketName;

    @Value("${aws.s3.accelerateMode}")
    public Boolean accelerateMode;

    @Value("${aws.s3.retries}")
    public Byte retries;

    @Value("${aws.s3.timeout}")
    public Integer timeout;

    private AWSStaticCredentialsProvider credentials(){
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(credentials())
                .withClientConfiguration(clientConfiguration())
                .withAccelerateModeEnabled(accelerateMode)
                .build();
    }

    private ClientConfiguration clientConfiguration(){
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicyWithCustomMaxRetries(retries));
        clientConfiguration.setConnectionTimeout(timeout);
        clientConfiguration.setSocketTimeout(timeout);
        clientConfiguration.setRequestTimeout(timeout);
        clientConfiguration.setConnectionMaxIdleMillis(timeout);
        clientConfiguration.setClientExecutionTimeout(timeout);
        return clientConfiguration;
    }
}
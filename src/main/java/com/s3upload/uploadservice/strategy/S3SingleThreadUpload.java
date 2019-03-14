package com.s3upload.uploadservice.strategy;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.s3upload.uploadservice.config.S3SingleThreadConfig;
import com.s3upload.uploadservice.utils.FileKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.amazonaws.services.s3.internal.Constants.MB;

@Service
public class S3SingleThreadUpload implements S3Upload {

    private final Logger logger = LoggerFactory.getLogger(S3SingleThreadUpload.class);

    private final S3SingleThreadConfig s3Config;

    public S3SingleThreadUpload(S3SingleThreadConfig s3Config) {
        this.s3Config = s3Config;
    }

    public String upload(MultipartFile multipartFile) {

        Long init = System.currentTimeMillis();

        String fileKey = FileKeyGenerator.generateKey(init, multipartFile);

        logger.info("PutObject - Initializing the upload process of file {}", fileKey);

        PutObjectRequest putObjectRequest = getPutObjectRequest(fileKey, multipartFile);

        Upload upload = null;

        try {

            upload = s3Config.transferManager().upload(putObjectRequest);

            while (!upload.isDone()) {

                TransferProgress progress = upload.getProgress();

                Long transferred = progress.getBytesTransferred() / MB;
                Long total = progress.getTotalBytesToTransfer() / MB;
                Integer percent = Double.valueOf(progress.getPercentTransferred()).intValue();

                logger.info("PutObject - {} - Upload progress {}% - Transferred [{}MB] of [{}MB] ", fileKey, percent, transferred, total);

                Thread.currentThread().sleep(3000);
            }

            Long totalTime = System.currentTimeMillis() - init;

            logger.info("PutObject - {} - Final transfer state [{}] - Process finished in {} mins {} secs", fileKey, upload.getState(), totalTime / MINUTES, (totalTime % MINUTES) / 1000);

            return fileKey;
        }catch(Exception e){
            logger.error("PutObject - {} - Error on file upload - Aborting", fileKey, e);

            if (upload != null)
                upload.abort();

            throw new RuntimeException("Error on file upload.");
        }
    }

    private PutObjectRequest getPutObjectRequest(String fileKey, MultipartFile multipartFile){
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getSize());

            return new PutObjectRequest(
                    s3Config.bucketName,
                    fileKey,
                    multipartFile.getInputStream(),
                    objectMetadata
            );
        }catch (Exception ex) {
            throw new RuntimeException("Error on put object.");
        }
    }
}
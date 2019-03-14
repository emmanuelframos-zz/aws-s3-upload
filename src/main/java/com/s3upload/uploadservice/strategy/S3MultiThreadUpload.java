package com.s3upload.uploadservice.strategy;

import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.s3upload.uploadservice.config.S3MultiThreadConfig;
import com.s3upload.uploadservice.utils.FileKeyGenerator;
import com.s3upload.uploadservice.utils.MultiThreadUploadUtils;
import com.s3upload.uploadservice.utils.UploadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;

@Service
public class S3MultiThreadUpload implements S3Upload {

    private final Logger logger = LoggerFactory.getLogger(S3MultiThreadUpload.class);

    private final S3MultiThreadConfig s3Config;

    public S3MultiThreadUpload(S3MultiThreadConfig s3Config) {
        this.s3Config = s3Config;
    }

    public String upload(MultipartFile multipartFile) {

        long init = System.currentTimeMillis();

        String fileKey = FileKeyGenerator.generateKey(init, multipartFile);

        logger.info("MultiPart - {} - Initializing the upload process", fileKey);

        String uploadId = MultiThreadUploadUtils
                .getUploadId(s3Config, fileKey);

        UploadInfo uploadInfo = UploadInfo
                .build()
                .fileKey(fileKey)
                .uploadId(uploadId)
                .multipartFile(multipartFile)
                .s3Config(s3Config);

        List<UploadPartRequest> uploadRequests = MultiThreadUploadUtils
                .getUploadRequests(uploadInfo);

        logger.info("MultiPart - {} - Number of parts [{}]", fileKey, uploadRequests.size());

        try{
            List<UploadPartCallable> callables = uploadRequests
                    .stream()
                    .map(uploadRequest -> new UploadPartCallable(s3Config, uploadRequest))
                    .collect(toList());

            List<Future<UploadPartResult>> uploadResults = s3Config.executorService().invokeAll(callables);

            List<PartETag> partETags = getParts(uploadResults);

            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
                    s3Config.bucketName,
                    fileKey,
                    uploadId,
                    partETags
            );

            s3Config.amazonS3().completeMultipartUpload(completeMultipartUploadRequest);
        } catch (Exception e) {
            logger.error("MultiPart - {} - Upload failed", fileKey, e);
            s3Config.amazonS3().abortMultipartUpload(new AbortMultipartUploadRequest(s3Config.bucketName, fileKey, uploadId));
            logger.error("MultiPart - {} - Upload aborted", fileKey);
            throw new RuntimeException("Error on multipart upload");
        }

        long totalTime = System.currentTimeMillis() - init;

        logger.info("MultiPart - {} - Process finished in {} mins {} secs", fileKey, totalTime / MINUTES, (totalTime % MINUTES) / 1000);

        return fileKey;
    }

    private List<PartETag> getParts(List<Future<UploadPartResult>> uploadResults) {
        return uploadResults
            .stream()
            .map(uploadResult -> {
                try {
                    return uploadResult.get().getPartETag();
                }catch (Exception ex){
                    throw new RuntimeException("Error on get parts");
                }
            })
            .collect(toList());
    }
}
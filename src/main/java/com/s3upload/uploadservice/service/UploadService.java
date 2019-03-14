package com.s3upload.uploadservice.service;

import com.s3upload.uploadservice.config.S3Config;
import com.s3upload.uploadservice.controller.dto.UploadResponseDTO;
import com.s3upload.uploadservice.domain.UploadStrategy;
import com.s3upload.uploadservice.strategy.S3Upload;
import com.s3upload.uploadservice.strategy.S3UploadSwitch;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.s3upload.uploadservice.domain.UploadStrategy.SINGLE_THREAD;
import static java.util.stream.Collectors.toList;

@Service
public class UploadService {

    private final S3Config s3Config;

    private final S3UploadSwitch s3UploadSwitch;

    public UploadService(S3Config s3Config, S3UploadSwitch s3UploadSwitch) {
        this.s3Config = s3Config;
        this.s3UploadSwitch = s3UploadSwitch;
    }

    public List<UploadResponseDTO> upload(List<MultipartFile> multipartFiles, UploadStrategy strategy){

        UploadStrategy uploadStrategy = Objects.isNull(strategy)
                ? SINGLE_THREAD
                : strategy;

        S3Upload s3Upload = s3UploadSwitch.switchStrategy(uploadStrategy);

        List<UploadResponseDTO> uploadResponses = multipartFiles
                .parallelStream()
                .map(multipartFile -> {

                    String fileKey = s3Upload.upload(multipartFile);

                    UploadResponseDTO uploadResponseDTO = new UploadResponseDTO();
                    uploadResponseDTO.fileKey = fileKey;
                    uploadResponseDTO.bucket = s3Config.bucketName;
                    uploadResponseDTO.fileName = multipartFile.getOriginalFilename();
                    uploadResponseDTO.size = multipartFile.getSize();
                    uploadResponseDTO.contentType = multipartFile.getContentType();
                    return uploadResponseDTO;
                })
                .collect(toList());
        return uploadResponses;
    }
}
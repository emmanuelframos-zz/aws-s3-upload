package com.s3upload.uploadservice.controller;

import com.s3upload.uploadservice.controller.dto.UploadResponseDTO;
import com.s3upload.uploadservice.domain.UploadStrategy;
import com.s3upload.uploadservice.service.UploadService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class UploadRestController {

    private final UploadService uploadService;

    public UploadRestController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @ApiOperation(value = "upload", nickname = "upload")
    @PostMapping(value = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UploadResponseDTO> upload(@RequestParam(value = "files") List<MultipartFile> multipartFiles,
                                          @RequestParam(value = "strategy", required = false) UploadStrategy strategy){
        return uploadService.upload(multipartFiles, strategy);
    }
}
package com.example.pinterestclone.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.pinterestclone.controller.response.FileResponseDto;
import com.example.pinterestclone.controller.response.ResponseDto;
import com.example.pinterestclone.domain.Files;
import com.example.pinterestclone.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    private final FileRepository fileRepository;

    public ResponseDto<?> createPostImage(MultipartFile multipartFile) throws IOException, URISyntaxException {
        String fileUrl = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename(); //저장되는 파일의 이름이 중복되지 않기 위해 랜덤값 + 파일이름

        ObjectMetadata objMeta = new ObjectMetadata(); //ContentLength로 S3에 알려주기위해 사용
        objMeta.setContentLength(multipartFile.getInputStream().available());

        amazonS3.putObject(bucket, fileUrl, multipartFile.getInputStream(), objMeta); //S3의 API메서드인 putObject를 이용해 파일 Stream을 열어 S3에 파일 업로드

        Files files = Files.builder()
                .url(amazonS3.getUrl(bucket, fileUrl).toString())
                .build();
        fileRepository.save(files);
        FileResponseDto fileResponseDto = FileResponseDto.builder()
                .fileId(files.getId())
                .imageUrl(files.getUrl())
                .build();

        return ResponseDto.success(fileResponseDto);
    }
}


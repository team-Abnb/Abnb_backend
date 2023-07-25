package com.sparta.abnb.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.abnb.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${s3.host.url}")
    private String s3HostUrl;

    // putObject가 등록뿐 아니라 수정도 되는 것으로 추정
    // urlLink 반환 메서드
    public String uploadImage(MultipartFile multipartFile, String folderName) {
        if(multipartFile == null){
            throw new IllegalArgumentException("사진 없이는 등록이 불가능합니다.");
        }

        String randomUuid = UUID.randomUUID().toString();
        String key = folderName + "/" + randomUuid;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucket, key, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("이미지 등록중 에러 발생 : " + e.getMessage());
        }
        return amazonS3.getUrl(bucket, key).toString();
    }

    //이미지 삭제
    public void deleteImage(String urlLink)  {

        String key = urlLink.substring(s3HostUrl.length());
        if(!amazonS3.doesObjectExist(bucket,key)){
            throw new NullPointerException("해당 이미지가 존재하지 않습니다.");
        }
        amazonS3.deleteObject(bucket, key);
    }

    //이미지 수정
    public String updateImage(String urlLink, MultipartFile newFile, String folderName) {
        deleteImage(urlLink);
        return uploadImage(newFile, folderName);
    }
}
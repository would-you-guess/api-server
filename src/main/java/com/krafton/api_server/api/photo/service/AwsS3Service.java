package com.krafton.api_server.api.photo.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.krafton.api_server.api.photo.domain.AwsS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloud_front_url}")
    private String frontCloudUrl;

    public AwsS3 upload(MultipartFile multipartFile, String dirName) throws IOException {
        File file = convertMultipartFileToFile(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert fail"));

        String objectName = randomFileName(file, dirName);

        // put s3
        amazonS3.putObject(new PutObjectRequest(bucket, objectName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        String path = amazonS3.getUrl(bucket, objectName).toString();
        file.delete();

        return AwsS3
                .builder()
                .key(objectName)
                .path(frontCloudUrl + objectName)
                .build();
    }

    public Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

        if (file.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(file)){
                fos.write(multipartFile.getBytes());
            }            return Optional.of(file);
        }
        return Optional.empty();
    }

    private String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }

    public void delete(String key) {
        try {
            if (!amazonS3.doesObjectExist(bucket, key)) {
                throw new AmazonS3Exception("Object " + key + " does not exist!");
            }
            amazonS3.deleteObject(bucket, key);
        } catch (SdkClientException e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<InputStreamResource> getImage(String key) throws FileNotFoundException {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
            S3Object s3Object = amazonS3.getObject(getObjectRequest);
            InputStream inputStream = s3Object.getObjectContent();
            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (SdkClientException e) {
            log.error("File not found in S3: {}", key);
            throw new FileNotFoundException("File not found in S3: " + key);
        }
    }

}

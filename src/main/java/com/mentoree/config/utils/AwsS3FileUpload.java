package com.mentoree.config.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Profile("real")
public class AwsS3FileUpload implements FileUtils {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    private AmazonS3 amazonS3;

    public AwsS3FileUpload() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String saveFilename) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        ContentType contentType = ContentType.valueOf(extension.toUpperCase());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, saveFilename, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonServiceException | IOException e) {
            e.printStackTrace();
        }

        return amazonS3.getUrl(bucket, saveFilename).toString();
    }

    @Override
    public boolean deleteFile(String fileName) {
        if(!amazonS3.doesObjectExist(bucket, fileName)) {
            throw new AmazonS3Exception("Object " + fileName + " does not exist!");
        }
        amazonS3.deleteObject(bucket, fileName);
        return true;
    }

    @Override
    public String getSaveFilename(String file) {
        String extension = FilenameUtils.getExtension(file);
        return UUID.randomUUID().toString().concat(".").concat(extension);
    }
}

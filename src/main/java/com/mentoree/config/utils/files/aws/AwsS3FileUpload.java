package com.mentoree.config.utils.files.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mentoree.config.utils.ContentType;
import com.mentoree.config.utils.files.FileInfo;
import com.mentoree.config.utils.files.FileUtils;
import com.mentoree.config.utils.files.MultipartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3FileUpload implements FileUtils {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    @Override
    public String uploadFile(MultipartFile multipartFile, String saveFilename) {
        FileInfo fileInfo = FileInfo.multipartOf(multipartFile, saveFilename);
        File file = new File(MultipartUtils.getLocalHomeDirectory(), fileInfo.getPath());
        try {
            multipartFile.transferTo(file);
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileInfo.getPath(), file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonServiceException | IOException e) {
            e.printStackTrace();
        } finally {
            if(file.exists())
                file.delete();
        }
        return amazonS3Client.getUrl(bucket, fileInfo.getPath()).toString();
    }

    @Override
    public boolean deleteFile(String filePath) {
        if(!amazonS3Client.doesObjectExist(bucket, filePath)) {
            throw new AmazonS3Exception("Object " + filePath + " does not exist!");
        }
        amazonS3Client.deleteObject(bucket, filePath);
        return true;
    }

    @Override
    public String getSaveFilename(String file) {
        return UUID.randomUUID().toString();
    }
}

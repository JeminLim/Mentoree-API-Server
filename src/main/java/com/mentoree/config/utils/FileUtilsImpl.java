package com.mentoree.config.utils;

import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.exception.FileUploadFailedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"local", "test"})
public class FileUtilsImpl implements FileUtils {

    @Value("${files.upload.path}")
    private String UPLOAD_PATH;

    @Override
    public String uploadFile(MultipartFile file, String saveFilename) {
        try {
            String dateDirPath = getDateDirPath();
            String parentDirectoryPath = getParentDirectoryPath();

            File targetFile = new File(parentDirectoryPath, saveFilename);
            file.transferTo(targetFile);

            return dateDirPath.concat(saveFilename);
        } catch (IOException e) {
            throw new FileUploadFailedException(e.getMessage(), e, ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        FilePathBuilder pathBuilder = new FilePathBuilder();
        String targetPath = pathBuilder.addPath(UPLOAD_PATH)
                .addPath(filePath)
                .build();
        return new File(targetPath).delete();
    }

    @Override
    public String getSaveFilename(String file) {
        String extension = FilenameUtils.getExtension(file);
        return UUID.randomUUID().toString().concat(".").concat(extension);
    }

    private String getParentDirectoryPath() {
        String dateDirPath = getDateDirPath();
        FilePathBuilder pathBuilder = new FilePathBuilder();

        String parentDirPath = pathBuilder.addPath(UPLOAD_PATH)
                .addPath(dateDirPath)
                .build();

        File parentDir = new File(parentDirPath);

        if(!parentDir.exists())
           parentDir.mkdirs();

        return parentDirPath;
    }

    private String getDateDirPath() {
        LocalDate today = LocalDate.now();

        String year = String.valueOf(today.getYear());
        String month = String.valueOf(today.getMonthValue());
        String day = String.valueOf(today.getDayOfMonth());

        FilePathBuilder pathBuilder = new FilePathBuilder();

        return pathBuilder.addPath(year)
                .addPath(month)
                .addPath(day)
                .build();
    }


    @Getter
    class FilePathBuilder {
        String path = File.separator;
        public FilePathBuilder addPath(String path) {
            this.path = this.path.concat(path).concat(File.separator);
            return this;
        }

        public String build() {
            return this.path;
        }
    }
}

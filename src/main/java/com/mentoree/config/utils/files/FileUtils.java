package com.mentoree.config.utils.files;

import org.springframework.web.multipart.MultipartFile;

public interface FileUtils {

    String uploadFile(MultipartFile file, String saveFilename);
    boolean deleteFile(String fileName);
    String getSaveFilename(String file);
}

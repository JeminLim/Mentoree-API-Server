package com.mentoree.config.utils.files;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class MultipartUtils {

    private static final String BASE_DIR = "images";

    public static String getLocalHomeDirectory() {
        return System.getProperty("user.home");
    }

    public static String getExtension(String fileName) {
        if (StringUtils.hasText(fileName))
            return FilenameUtils.getExtension(fileName);
        return null;
    }

    public static String createPath(String savedFileName, String extension) {
        return String.format("%s/%s.%s", BASE_DIR, savedFileName, extension);
    }

}

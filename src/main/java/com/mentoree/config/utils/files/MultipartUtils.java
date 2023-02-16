package com.mentoree.config.utils.files;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDate;


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

    public static String mkdirDateDir() {
        FilePathBuilder pathBuilder = new FilePathBuilder();

        // Home image directory
        String rootDir = System.getProperty("user.home");

        // Date directory
        String imageDir = BASE_DIR;
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.valueOf(LocalDate.now().getMonthValue());
        String day = String.valueOf(LocalDate.now().getDayOfMonth());

        String path = pathBuilder.addPath(imageDir)
                .addPath(year)
                .addPath(month)
                .addPath(day)
                .build();

        File parentDir = new File(rootDir, path);

        if(!parentDir.exists())
            parentDir.mkdirs();

        return path;
    }


}

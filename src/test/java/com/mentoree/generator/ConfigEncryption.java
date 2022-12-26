package com.mentoree.generator;

import com.mentoree.config.utils.AESUtils;
import com.mentoree.config.utils.EncryptUtils;
import com.mentoree.domain.entity.AttachImage;
import com.mentoree.domain.entity.ImageFile;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigEncryption {

    @Test
    void encrypt() {

        String db = "1234";
        String jwtKey = "jwt-secret-key";
        String encryptKey = "yPhQoubTfmmyyEEzluVo0HiEmu5VUKat";

        System.out.println("DB enc = ENC(" + jasyptEncoding(db) + ")");
        System.out.println("jwtSecret enc = ENC(" + jasyptEncoding(jwtKey) + ")");
        System.out.println("encrypt key enc = ENC(" + jasyptEncoding(encryptKey) + ")");

    }

    @Test
    void regexTest() {

        String content = "### 게시글 테스트 \n\n ![logo.png](http://localhost:8080/images/2022/12/22/78ce3949-03d0-4362-830c-fa7633795748.png)" +
                "\n\n ![btn_big.png](http://localhost:8080/images/2022/12/22/759eaee40-f54e-4b2d-be31-07750829d25c.png)";

        String regex = "\\!\\[[\\S]*\\]\\([\\S]*\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()) {
            String find = matcher.group();
            System.out.println(find.substring(find.indexOf("/images") + 7, find.length()-1));
        }

        System.out.println(new File("/Users/jeminlim/Desktop/Mentoree/src/main/resources/static/images/2022/12/22/test.png").delete());
    }

    @Test
    void listRemoveTest() {

        List<AttachImage> imgList = new ArrayList<>();
        List<String> pathList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AttachImage img = AttachImage.builder()
                    .image(ImageFile.builder().filePath("path" + i).originFileName("file" + i).saveFileName("saveFile" + i).mediaType("png").build())
                    .build();

            imgList.add(img);

            if(i % 2 == 0)
                pathList.add("path" + i);
        }

        List<AttachImage> confirm = new ArrayList<>();
        List<AttachImage> notIncludeImg = new ArrayList<>();

        for (AttachImage img : imgList) {
            boolean flag = false;
            for (String path : pathList) {
                if(img.getImage().getFilePath().equals(path))
                    flag = true;
            }
            if(flag) confirm.add(img);
            else notIncludeImg.add(img);
        }

        for (AttachImage attachImage : confirm) {
            System.out.println(attachImage.getImage().getFilePath());
        }
        System.out.println("will removed");
        for (AttachImage attachImage : notIncludeImg) {
            System.out.println(attachImage.getImage().getFilePath());
        }

    }

    public String jasyptEncoding(String value) {

        String key = "M16aXng55CXtjPm9n2pNpGjAa";
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setAlgorithm("PBEWithMD5AndDES");
        enc.setPassword(key);
        return enc.encrypt(value);
    }




}

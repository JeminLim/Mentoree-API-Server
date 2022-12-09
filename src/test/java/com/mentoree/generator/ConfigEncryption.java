package com.mentoree.generator;

import com.mentoree.config.utils.AESUtils;
import com.mentoree.config.utils.EncryptUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

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

    public String jasyptEncoding(String value) {

        String key = "M16aXng55CXtjPm9n2pNpGjAa";
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setAlgorithm("PBEWithMD5AndDES");
        enc.setPassword(key);
        return enc.encrypt(value);
    }




}

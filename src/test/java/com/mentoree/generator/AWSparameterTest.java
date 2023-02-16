package com.mentoree.generator;

import com.mentoree.config.JpaJasyptConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"/schema-test.sql", "/setUpData.sql"})
public class AWSparameterTest {

    @Autowired
    JpaJasyptConfig jpaJasyptConfig;

    @Test
    void getPropertyTest() {
        System.out.println(jpaJasyptConfig.getPassword());
    }
}

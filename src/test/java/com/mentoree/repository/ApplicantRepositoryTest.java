package com.mentoree.repository;

import com.mentoree.domain.entity.Applicant;
import com.mentoree.domain.repository.ApplicantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql({"/init.sql", "/setUpData.sql"})
public class ApplicantRepositoryTest {

    @Autowired
    ApplicantRepository applicantRepository;

    @Test
    @DisplayName("신청자 목록 가져오기")
    void getApplicantsListQuery() {

        List<Applicant> result = applicantRepository.findAllByProgramId(1L);

        assertThat(result.size()).isEqualTo(2);

    }



}

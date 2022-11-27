package com.mentoree.repository;

import com.mentoree.domain.entity.Program;
import com.mentoree.domain.repository.ProgramRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql({"/init.sql", "/setUpData.sql"})
public class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;

    @Test
    @DisplayName("프로그램 필터링 테스트-전체 중 페이지 0")
    void getAllList() {
        Pageable page = PageRequest.of(0, 8);
        Slice<Program> programList = programRepository.getProgramList(10L, null, null, page);
        assertThat(programList.getContent().size()).isEqualTo(8);

        Slice<Program> recentProgram = programRepository.getRecentProgramList(0L, null, null);
        assertThat(recentProgram.getContent().size()).isEqualTo(8);
    }


    @Test
    @DisplayName("프로그램 필터링 테스트- 대분류")
    void getProgrammingProgramList() {
        Pageable page = PageRequest.of(0, 8);
        Slice<Program> programList = programRepository.getProgramList( 10L, "Programming", null, page);
        assertThat(programList.getContent().size()).isEqualTo(8);


        Slice<Program> recentProgram = programRepository.getRecentProgramList( 0L, "Programming", null);
        assertThat(programList.getContent().size()).isEqualTo(8);
    }


    @Test
    @DisplayName("프로그램 필터링 테스트- 소분류")
    void getJavaProgramList() {
        Pageable page = PageRequest.of(0, 8);
        Slice<Program> programList = programRepository.getProgramList( 10L,"Programming", List.of("JAVA"), page);
        assertThat(programList.getContent().size()).isEqualTo(5);

        Slice<Program> recentProgram = programRepository.getRecentProgramList( 0L, "Programming", List.of("JAVA"));
        assertThat(programList.getContent().size()).isEqualTo(5);
    }

}

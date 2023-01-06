package com.mentoree.repository;

import com.mentoree.domain.entity.Category;
import com.mentoree.domain.entity.Program;
import com.mentoree.domain.repository.CategoryRepository;
import com.mentoree.domain.repository.ProgramRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@Sql({"classpath:/schema-test.sql"})
public class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        Category programming = generateCategory("Programming");
        Category music = generateCategory("Music");
        Category java = generateSecondCategory("JAVA", programming);
        Category python = generateSecondCategory("PYTHON", programming);
        Category nodejs = generateSecondCategory("NODEJS", programming);
        Category guitar = generateSecondCategory("GUITAR", music);

        List<Program> bulkProgram = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            if(i % 7 == 0) {
                Program guitarProgram = Program.builder()
                        .description("test description")
                        .programName("test" + i)
                        .category(guitar)
                        .dueDate(LocalDate.now().plusDays(50))
                        .maxMember(5)
                        .price(10000)
                        .build();
                bulkProgram.add(guitarProgram);
            } // 14 개, Guitar
            else if( i % 5 == 0) {
                Program nodeProgram = Program.builder()
                        .description("test description")
                        .programName("test" + i)
                        .category(nodejs)
                        .dueDate(LocalDate.now().plusDays(50))
                        .maxMember(5)
                        .price(10000)
                        .build();
                bulkProgram.add(nodeProgram);
            } // 18 개, Node
            else if(i % 3 == 0) {
                Program pythonProgram = Program.builder()
                        .description("test description")
                        .programName("test" + i)
                        .category(python)
                        .dueDate(LocalDate.now().plusDays(50))
                        .maxMember(5)
                        .price(10000)
                        .build();
                bulkProgram.add(pythonProgram);
            } // 23 개, Python
            else {
                Program javaProgram = Program.builder()
                        .description("test description")
                        .programName("test" + i)
                        .category(java)
                        .dueDate(LocalDate.now().plusDays(50))
                        .maxMember(5)
                        .price(10000)
                        .build();
                bulkProgram.add(javaProgram);
            } // 45 개, Java
        }
        programRepository.saveAll(bulkProgram);
    }

    private Category generateSecondCategory(String categoryName, Category parent) {
        Category cat = Category.builder().categoryName(categoryName).build();
        cat.setParent(parent);
        categoryRepository.save(cat);
        return cat;
    }

    private Category generateCategory(String categoryName) {
        Category cat = Category.builder().categoryName(categoryName).build();
        categoryRepository.save(cat);
        return cat;
    }



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

        Slice<Program> recentProgram = programRepository.getRecentProgramList( 95L, "Programming", null);

        for (Program program : recentProgram) {
            log.info("program id = {} " , program.getId());
        }

        Pageable page = PageRequest.of(0, 8 - recentProgram.getContent().size());
        Long minId = recentProgram.getContent().get(recentProgram.getContent().size() - 1).getId();
        Slice<Program> programList = programRepository.getProgramList( minId, "Programming", null, page);

        for (Program program : programList) {
            log.info("program id = {} " , program.getId());
        }

        assertThat(recentProgram.getContent().size()).isEqualTo(4);
        assertThat(programList.getContent().size()).isEqualTo(4);

    }


    @Test
    @DisplayName("프로그램 필터링 테스트- 소분류")
    void getJavaProgramList() {

        Slice<Program> recentProgram = programRepository.getRecentProgramList( 95L, "Programming", List.of("JAVA"));
        Pageable page = PageRequest.of(0, 8 - recentProgram.getContent().size());
        Long minId = recentProgram.getContent().get(recentProgram.getContent().size() - 1).getId();
        Slice<Program> programList = programRepository.getProgramList( minId,"Programming", List.of("JAVA"), page);

        for (Program program : recentProgram) {
            log.info("program id = {} " , program.getId());
        }
        for (Program program : programList) {
            log.info("program id = {} " , program.getId());
        }



        assertThat(recentProgram.getContent().size()).isEqualTo(1);
        assertThat(programList.getContent().size()).isEqualTo(7);

    }

}

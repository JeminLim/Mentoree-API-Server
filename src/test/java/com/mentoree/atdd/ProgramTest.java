package com.mentoree.atdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.CategoryRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.ProgramRepository;
import com.mentoree.service.dto.ProgramCreateRequestDto;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProgramTest {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MentorRepository mentorRepository;


    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        initData();
    }

    @Test
    @DisplayName("프로그램 생성 요청")
    void ProgramCreateTest() {

        // given
        //request form
        ProgramCreateRequestDto createRequest = ProgramCreateRequestDto.builder()
                .programName("testProgram")
                .description("this is test")
                .dueDate(LocalDate.now().plusDays(5))
                .price(10000)
                .categoryName("JAVA")
                .maxMember(5)
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/programs/create")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getLong("mentor.programId")).isEqualTo(1L);
        assertThat(response.jsonPath().getLong("mentor.memberId")).isEqualTo(1L);
        assertThat(response.jsonPath().getBoolean("mentor.host")).isTrue();
    }

    @Test
    @DisplayName("프로그램 수정 요청")
    void updateProgramTest() {

        ProgramCreateRequestDto updateRequest = ProgramCreateRequestDto.builder()
                .programName("newProgram")
                .description("this is renew test program")
                .dueDate(LocalDate.now().plusDays(5))
                .price(15000)
                .categoryName("JAVA")
                .maxMember(5)
                .build();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/programs/update/{id}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("program.programName")).isEqualTo(updateRequest.getProgramName());
        assertThat(response.jsonPath().getInt("program.price")).isEqualTo(updateRequest.getPrice());
        assertThat(response.jsonPath().getList("program.mentorList").size()).isEqualTo(1);
    }


    private void initData() {

        Category firstCategory = Category.builder().categoryName("Programming").build();
        categoryRepository.saveAndFlush(firstCategory);
        Category secondCategory = Category.builder().categoryName("JAVA").build();
        secondCategory.setParent(firstCategory);
        categoryRepository.saveAndFlush(secondCategory);

        Program program = Program.builder()
                .programName("testProgram")
                .category(secondCategory)
                .maxMember(5)
                .description("test program description")
                .dueDate(LocalDate.now().plusDays(7))
                .price(5000)
                .build();
        Program savedProgram = programRepository.save(program);

        Member memberA = Member.builder()
                .email("memberA@email.com")
                .nickname("memberANickname")
                .userPassword("1234QWer!@")
                .username("memberA")
                .oAuthId("FORM")
                .role(UserRole.MENTOR)
                .build();
        Member saved = memberRepository.saveAndFlush(memberA);
        Mentor mentor = Mentor.builder().member(saved)
                .host(true)
                .build();
        mentor.setProgram(savedProgram);
        mentorRepository.save(mentor);

        List<History> histories = new ArrayList<>();
        histories.add(new History("testCompany",
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2022, 1, 1),
                "testPosition"));

        saved.updateCareer(histories);
        memberRepository.save(saved);


    }


}

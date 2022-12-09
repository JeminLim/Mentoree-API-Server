package com.mentoree.atdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.config.utils.JwtUtils;
import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.CategoryRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.ProgramRepository;
import com.mentoree.service.dto.ProgramApplyDto;
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
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/init.sql", "/setUpData.sql"})
public class ProgramTest {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtils jwtUtils;
    private String accessToken;
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accessToken = "Bearer " + jwtUtils.generateToken(1L, "memberA@email.com", "ROLE_MENTOR");
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
                .header("Authorization", accessToken)
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/programs/create")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getLong("mentor.programId")).isEqualTo(11L);
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
                .header("Authorization", accessToken)
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

    @Test
    @DisplayName("프로그램 참가 신청")
    void applyProgramTest() {

        ProgramApplyDto applyRequest = ProgramApplyDto.builder()
                .programId(1L)
                .message("I want to join in")
                .role("MENTEE")
                .build();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .body(applyRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/programs/apply")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("result")).isEqualTo("success");
    }

    @Test
    @DisplayName("프로그램 참가자 관리 목록")
    void manageApplicantsTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .get("/api/programs/applicants/{id}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("applicants").size()).isEqualTo(2);

    }

    @Test
    @DisplayName("참가 신청 승인 요청")
    void acceptApplicantTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .post("/api/programs/{programId}/applicants/accept/{applicantId}", 1L,1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("참가 신청 거절 요청")
    void rejectApplicantTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .delete("/api/programs/{programId}/applicants/reject/{applicantId}", 1L, 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("프로그램 상세 정보 조회")
    void getProgramInfoTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .get("/api/programs/{programId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("program.id")).isEqualTo(1L);
        assertThat(response.jsonPath().getString("program.programName")).isEqualTo("testProgram");
        assertThat(response.jsonPath().getString("program.description")).isEqualTo("Test program description");
        assertThat(response.jsonPath().getInt("program.maxMember")).isEqualTo(5);
        assertThat(response.jsonPath().getInt("program.price")).isEqualTo(10000);

    }

    @Test
    @DisplayName("프로그램 중도 폐지")
    void withdrawProgramTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .post("/api/programs/withdraw/{programId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("프로그램 목록 필터링 - No Filter, First filter, Second filter")
    void getProgramListWithFilter() {
        // No Filtering (request all)
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when().get("/api/programs/list")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("programList").size()).isEqualTo(8);
    }

    @Test
    @DisplayName("프로그램 필터링 - 대분류")
    void getProgramListWithFirstFilter() {
        // Select first category
        ExtractableResponse<Response> responseFirstFilter = RestAssured.given()
                .header("Authorization", accessToken)
                .queryParam("first", "Programming")
                .log().all()
                .when().get("/api/programs/list")
                .then().log().all()
                .extract();

        assertThat(responseFirstFilter.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseFirstFilter.jsonPath().getList("programList").size()).isEqualTo(8);
    }

    @Test
    @DisplayName("프로그램 필터링 - 소분류")
    void getProgramListWithSecondFilter() {
        // Select second category
        ExtractableResponse<Response> responseSecondFilter = RestAssured.given()
                .header("Authorization", accessToken)
                .queryParam("first", "Programming")
                .queryParam("second", "JAVA")
                .log().all()
                .when().get("/api/programs/list")
                .then().log().all()
                .extract();

        assertThat(responseSecondFilter.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseSecondFilter.jsonPath().getList("programList").size()).isEqualTo(5);
    }

}

package com.mentoree.atdd;

import com.mentoree.config.utils.JwtUtils;
import com.mentoree.domain.entity.History;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.service.dto.MemberProfileDto;
import com.mentoree.service.dto.MemberSignUpRequestDto;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/schema.sql", "/setUpData.sql"})
public class MemberTest {

    private final String EXIST_MEMBER_EMAIL = "memberA@email.com";
    private final String EXIST_MEMBER_NICKNAME = "memberA";
    private final String EXIST_MEMBER_PASSWORD = "1234QWer!@";
    private final String EXIST_MEMBER_NAME = "memberName";

    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accessToken = "Bearer " + jwtUtils.generateToken(1L, "memberA@email.com", "ROLE_MENTOR");
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginMember() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .param("email", "memberA@email.com")
                .param("password", "1234QWer!@")
                .when()
                .post("/api/login")
                .then().log().all()
                .extract();

        assertThat(response.jsonPath().getString("accessToken")).isNotNull();
    }

    @DisplayName("회원가입_요청_정상_동작")
    @Test
    void signUpMember() {
        //given
        final MemberSignUpRequestDto signUp = MemberSignUpRequestDto.builder()
                .email("test@email.com")
                .password("1234QWer!@")
                .nickname("tester")
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(signUp)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/members/join")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("이메일_중복_요청_예외_발생")
    void signUpMember_DuplicateEmail() {
        //given
        final MemberSignUpRequestDto signUp = MemberSignUpRequestDto.builder()
                .email(EXIST_MEMBER_EMAIL)
                .nickname("newNickname")
                .password("QWer1234!@")
                .build();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all().body(signUp)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/members/join")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("회원 정보 열람")
    void getProfileTest() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when().get("/api/members/profiles/{id}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("member.id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("member.email")).isEqualTo(EXIST_MEMBER_EMAIL);
        assertThat(response.jsonPath().getList("member.histories").size()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원 정보 수정")
    void updateProfileTest() {
        MemberProfileDto updateProfile = MemberProfileDto.builder()
                .id(1L)
                .email(EXIST_MEMBER_EMAIL)
                .nickname("newNickname")
                .username(EXIST_MEMBER_NAME)
                .histories(List.of(
                        new History("newCompany", LocalDate.of(2021, 1, 1),
                                LocalDate.of(2022,1,1), "newPosition")))
                .build();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .body(updateProfile)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/members/profiles")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("member.id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("member.nickname")).isEqualTo("newNickname");

    }

    @Test
    @DisplayName("멘토 회원으로 전환")
    void changeToMentorMemberTest() {

        MemberProfileDto updateProfile = MemberProfileDto.builder()
                .id(1L)
                .email(EXIST_MEMBER_EMAIL)
                .nickname(EXIST_MEMBER_NICKNAME)
                .username(EXIST_MEMBER_NAME)
                .histories(List.of(
                        new History("testCompany",
                                LocalDate.of(2021,1,1),
                                LocalDate.of(2022,1,1),
                                "testPosition")))
                .build();

        ExtractableResponse<Response> response = RestAssured.given().log().all().body(updateProfile)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/members/transform")
                .then().log().all()
                .extract();


        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 탈퇴 요청")
    void MemberDeleteTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .pathParam("id", 1L)
                .when().post("/api/members/withdraw/{id}")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("result")).isEqualTo("success");
        assertThat(memberRepository.findById(1L).get().getWithdrawal()).isTrue();
    }

}

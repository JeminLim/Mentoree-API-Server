package com.mentoree.atdd;

import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.UserRole;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.service.dto.MemberSignUpRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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

import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MemberTest {

    private final String EXIST_MEMBER_EMAIL = "member@email.com";
    private final String EXIST_MEMBER_NICKNAME = "memberA";
    private final String EXIST_MEMBER_PASSWORD = "1234QWer!@";

    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        memberRepository.deleteAllInBatch();
        initMemberDB();
    }

    @DisplayName("회원가입_요청_정상_동작")
    @Test
    void signUpMember() {
        //given
        final MemberSignUpRequest signUp = MemberSignUpRequest.builder()
                .email("teset@email.com")
                .password("QWer1234!@")
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
        final MemberSignUpRequest signUp = MemberSignUpRequest.builder()
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


    private void initMemberDB() {
        Member memberA = Member.builder()
                .email(EXIST_MEMBER_EMAIL)
                .nickname(EXIST_MEMBER_NICKNAME)
                .userPassword(EXIST_MEMBER_PASSWORD)
                .oAuthId("FORM")
                .role(UserRole.MENTEE)
                .build();
        memberRepository.save(memberA);
    }

}

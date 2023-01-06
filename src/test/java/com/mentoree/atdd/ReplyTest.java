package com.mentoree.atdd;

import com.mentoree.config.utils.JwtUtils;
import com.mentoree.service.dto.ReplyCreateRequestDto;
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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/schema-test.sql", "/setUpData.sql"})
public class ReplyTest {

    @LocalServerPort
    int port;

    @Autowired
    JwtUtils jwtUtils;
    private String accessToken;
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accessToken = "Bearer " + jwtUtils.generateToken(1L, "memberA@email.com", "ROLE_MENTOR");
    }

    @Test
    @DisplayName("댓글 작성 요청")
    void createReplyTest() {

        ReplyCreateRequestDto createRequest = ReplyCreateRequestDto.builder()
                .boardId(1L)
                .content("test reply")
                .build();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/replies/create")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getLong("reply.id")).isEqualTo(2);
        assertThat(response.jsonPath().getLong("reply.memberId")).isEqualTo(1);
        assertThat(response.jsonPath().getString("reply.content")).isEqualTo(createRequest.getContent());
    }

    @Test
    @DisplayName("댓글 수정 요청")
    void replyUpdateTest() {

        Map<String, Object> updateContent = new HashMap<>();
        updateContent.put("content", "renewed content");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .body(updateContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .patch("/api/replies/update/{replyId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("reply.id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("reply.content")).isEqualTo(updateContent.get("content"));
    }

    @Test
    @DisplayName("댓글 삭제 요청")
    void replyDeleteTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .post("/api/replies/remove/{replyId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("댓글 리스트 요청")
    void getReplyListTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .get("/api/replies/list/{boardId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("replyList").size()).isEqualTo(1);


    }

}

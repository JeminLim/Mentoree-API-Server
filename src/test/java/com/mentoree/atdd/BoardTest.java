package com.mentoree.atdd;

import com.mentoree.config.utils.JwtUtils;
import com.mentoree.service.dto.BoardCreateRequestDto;
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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/schema.sql", "/setUpData.sql"})
public class BoardTest {

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
    @DisplayName("게시글 생성 요청")
    void createBoardTest() {
        // given
        BoardCreateRequestDto createRequest = BoardCreateRequestDto.builder()
                .missionId(1L)
                .title("boardTitle")
                .description("board Description")
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/boards/create")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    @Test
    @DisplayName("게시글 수정")
    void updateBoardTest() {

        // given
        BoardCreateRequestDto createRequest = BoardCreateRequestDto.builder()
                .missionId(1L)
                .title("newTitle")
                .description("new board description")
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/boards/update/{boardId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("게시글 삭제 요청")
    void deleteBoardTest() {
        // 외래키 제약사항 -> 댓글!
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .delete("/api/boards/{boardId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("게시글 정보 열람")
    void getBoardDtoTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .get("/api/boards/{boardId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("board.id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("board.title")).isEqualTo("testBoard");
        assertThat(response.jsonPath().getString("board.content")).isEqualTo("test board description");
        assertThat(response.jsonPath().getLong("board.missionId")).isEqualTo(1);
        assertThat(response.jsonPath().getString("board.missionTitle")).isEqualTo("testMission");
        assertThat(response.jsonPath().getLong("board.writerId")).isEqualTo(1);
        assertThat(response.jsonPath().getString("board.writerNickname")).isEqualTo("memberANickname");
    }

    @Test
    @DisplayName("게시글 리스트 요청")
    void getBoardListTest() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .header("Authorization", accessToken)
                .when()
                .get("/api/boards/list/{missionId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("boardList").size()).isEqualTo(1);
    }

}

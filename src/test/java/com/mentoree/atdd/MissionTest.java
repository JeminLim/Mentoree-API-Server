package com.mentoree.atdd;

import com.mentoree.service.dto.MissionCreateRequestDto;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/init.sql", "/setUpData.sql"})
public class MissionTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("미션 생성 요청")
    void createMissionTest() {

        // given
        MissionCreateRequestDto createRequest = MissionCreateRequestDto.builder()
                .title("missionTest")
                .description("description")
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/missions/create")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    @Test
    @DisplayName("미션 수정 요청")
    void updateMissionTest() {

        // given
        MissionCreateRequestDto createRequest = MissionCreateRequestDto.builder()
                .title("updatedMission")
                .description("new description")
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(createRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/missions/update/{missionId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("미션 삭제 요청")
    void deleteMissionTest() {
        //이미 작성된 게시글이 있는 경우에 대한 대비
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/api/missions/{missionId}", 2L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("작성 게시글 있는 미션 삭제-실패")
    void deleteMissionTest_Failed() {
        //이미 작성된 게시글이 있는 경우에 대한 대비
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/api/missions/{missionId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("미션 정보 열람")
    void getMissionDtoTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/api/missions/{missionId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("mission.id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("mission.title")).isEqualTo("testMission");
        assertThat(response.jsonPath().getString("mission.content")).isEqualTo("testMission description");
        assertThat(response.jsonPath().getString("mission.dueDate")).isEqualTo("2022-12-05");
        assertThat(response.jsonPath().getLong("mission.programId")).isEqualTo(1);
        assertThat(response.jsonPath().getLong("mission.writerId")).isEqualTo(1);
        assertThat(response.jsonPath().getString("mission.writerNickname")).isEqualTo("memberANickname");
    }

    @Test
    @DisplayName("해당 프로그램 소속 미션 리스트")
    void getMissionInfoListTest() {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/api/missions/list/{programId}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("missionList").size()).isEqualTo(2);
    }

}

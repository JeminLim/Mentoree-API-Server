package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.mock.WithMockCustomUser;
import com.mentoree.config.WebConfig;
import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.service.BoardService;
import com.mentoree.service.dto.BoardCreateRequestDto;
import com.mentoree.service.dto.BoardInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardApiController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, AuthorityInterceptor.class})
})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class BoardApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BoardService boardService;

    @Test
    @DisplayName("게시글 생성 요청")
    @WithMockCustomUser
    void BoardCreateTest() throws Exception {

        BoardCreateRequestDto createRequest = BoardCreateRequestDto.builder()
                .missionId(1L)
                .title("testMission")
                .description("test mission")
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        doNothing().when(boardService).create(any(), any());

        mockMvc.perform(
                        post("/api/boards/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andDo(
                        document("post-board-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("missionId").description("Mission pk that board is belonging to"),
                                        fieldWithPath("title").description("Board name"),
                                        fieldWithPath("description").description("Board description")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 수정 요청")
    void BoardUpdateTest() throws Exception {

        BoardCreateRequestDto createRequest = BoardCreateRequestDto.builder()
                .missionId(1L)
                .title("testMission")
                .description("test mission")
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        doNothing().when(boardService).update(any(), any());

        mockMvc.perform(
                        post("/api/boards/update/{boardId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andDo(
                        document("post-board-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("missionId").description("Mission pk that board is belonging to"),
                                        fieldWithPath("title").description("Updated title"),
                                        fieldWithPath("description").description("Updated description")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 삭제 요청")
    void BoardDeleteTest() throws Exception {

        doNothing().when(boardService).delete(any());

        mockMvc.perform(
                        delete("/api/boards/{boardId}", 1L)
                ).andExpect(status().isOk())
                .andDo(
                        document("delete-board",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("boardId").description("board id for delete")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 정보 요청")
    void getBoardTest() throws Exception {
        BoardInfoDto expected = BoardInfoDto.builder()
                .id(1L)
                .title("testBoard")
                .writerId(1L)
                .writerNickname("memberA")
                .missionId(1L)
                .missionTitle("testMission")
                .content("test board for test mission")
                .build();

        when(boardService.getBoardInfo(any())).thenReturn(expected);

        mockMvc.perform(
                        get("/api/boards/{boardId}", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("board.id").value(1))
                .andExpect(jsonPath("board.title").value("testBoard"))
                .andDo(
                        document("delete-boards",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("boardId").description("board id to get")
                                ),
                                responseFields(
                                        fieldWithPath("board.id").description("board pk"),
                                        fieldWithPath("board.missionId").description("mission pk that board is belonged to"),
                                        fieldWithPath("board.missionTitle").description("mission title that board is belonged to"),
                                        fieldWithPath("board.writerId").description("writer's member pk"),
                                        fieldWithPath("board.writerNickname").description("writer's nickname"),
                                        fieldWithPath("board.title").description("board title"),
                                        fieldWithPath("board.content").description("board content")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 리스트 요청")
    void getBoardListTest() throws Exception {
        BoardInfoDto expected = BoardInfoDto.builder()
                .id(1L)
                .title("testBoard")
                .writerId(1L)
                .writerNickname("memberA")
                .missionId(1L)
                .missionTitle("testMission")
                .content("test board for test mission")
                .build();

        when(boardService.getBoardInfoList(any())).thenReturn(List.of(expected));

        mockMvc.perform(
                        get("/api/boards/list/{missionId}", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("boardList.size()").value(1))
                .andDo(
                        document("get-missions",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("missionId").description("mission id which board list is belonged to")
                                ),
                                responseFields(
                                        fieldWithPath("boardList[]").description("board list"),
                                        fieldWithPath("boardList[].id").description("board pk"),
                                        fieldWithPath("boardList[].missionId").description("mission pk that board is belonged to"),
                                        fieldWithPath("boardList[].missionTitle").description("mission title that board is belonged to"),
                                        fieldWithPath("boardList[].writerId").description("writer's member pk"),
                                        fieldWithPath("boardList[].writerNickname").description("writer's nickname"),
                                        fieldWithPath("boardList[].title").description("board title"),
                                        fieldWithPath("boardList[].content").description("board content")
                                )
                        )
                );
    }
}

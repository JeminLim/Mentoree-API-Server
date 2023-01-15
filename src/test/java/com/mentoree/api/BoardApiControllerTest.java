package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.mock.WithMockCustomUser;
import com.mentoree.config.WebConfig;
import com.mentoree.config.interceptors.AuthorityInterceptor;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

    @Autowired
    ResourceLoader loader;

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

        when(boardService.create(any(), any(), anyBoolean())).thenReturn(1L);

        mockMvc.perform(
                        post("/api/boards/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("boardId").value(1))
                .andDo(
                        document("post-board-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("missionId").description("Mission pk that board is belonging to"),
                                        fieldWithPath("title").description("Board name"),
                                        fieldWithPath("description").description("Board description")
                                ),
                                responseFields(
                                        fieldWithPath("boardId").description("board pk")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 생성 임시 저장 요청")
    @WithMockCustomUser
    void BoardTempCreateTest() throws Exception {

        BoardCreateRequestDto createRequest = BoardCreateRequestDto.builder()
                .missionId(1L)
                .title("testMission")
                .description("test mission")
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        when(boardService.create(any(), any(), anyBoolean())).thenReturn(1L);

        mockMvc.perform(
                        post("/api/boards/create/temp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("boardId").value(1))
                .andDo(
                        document("post-board-create-temp",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("missionId").description("Mission pk that board is belonging to"),
                                        fieldWithPath("title").description("Board name"),
                                        fieldWithPath("description").description("Board description")
                                ),
                                responseFields(
                                        fieldWithPath("boardId").description("board pk")
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
                        document("get-board",
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
                                        fieldWithPath("board.content").description("board content"),
                                        fieldWithPath("board.temporal").description("Whether board is temporal writing or not")
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
                        document("get-missions-list",
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
                                        fieldWithPath("boardList[].content").description("board content"),
                                        fieldWithPath("boardList[].temporal").description("Whether board is temporal writing or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("임시 저장 테스트")
    @WithMockCustomUser
    void temporallySaveTest() throws Exception{
        BoardCreateRequestDto createRequest = BoardCreateRequestDto.builder()
                .missionId(1L)
                .title("testMission")
                .description("test mission")
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        when(boardService.create(anyLong(), any(), anyBoolean())).thenReturn(1L);

        mockMvc.perform(
                        post("/api/boards/create/temp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andDo(
                        document("post-board-create-temp",
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
    @DisplayName("임시 저장 글 요청 테스트 - 새로운 글 작성")
    @WithMockCustomUser
    void getTemporalWritingTest_first() throws Exception {

        when(boardService.getTemporalWriting(any(), any())).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(
                        get("/api/boards/{missionId}/temp", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("writingBoard").isEmpty());
    }

    @Test
    @DisplayName("임시 저장 글 요청")
    @WithMockCustomUser
    void getTemporalWriting_Has_Temporal_writing() throws Exception {
        BoardInfoDto expected = BoardInfoDto.builder()
                .id(1L)
                .title("testBoard")
                .writerId(1L)
                .writerNickname("memberA")
                .missionId(1L)
                .missionTitle("testMission")
                .content("test board for test mission")
                .build();

        when(boardService.getTemporalWriting(any(), any())).thenReturn(Optional.ofNullable(expected));

        mockMvc.perform(
                        get("/api/boards/{missionId}/temp", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("writingBoard.id").value(1L))
                .andExpect(jsonPath("writingBoard.title").value("testBoard"))
                .andDo(
                        document("get-board-temp",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("missionId").description("Mission pk which is writing")
                                ),
                                responseFields(
                                        fieldWithPath("writingBoard.id").description("board pk"),
                                        fieldWithPath("writingBoard.missionId").description("mission pk that board is belonged to"),
                                        fieldWithPath("writingBoard.missionTitle").description("mission title that board is belonged to"),
                                        fieldWithPath("writingBoard.writerId").description("writer's member pk"),
                                        fieldWithPath("writingBoard.writerNickname").description("writer's nickname"),
                                        fieldWithPath("writingBoard.title").description("board title"),
                                        fieldWithPath("writingBoard.content").description("board content"),
                                        fieldWithPath("writingBoard.temporal").description("Whether board is temporal writing or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 이미지 업로드")
    @WithMockCustomUser
    void uploadImageTest() throws Exception {
        Resource resource = loader.getResource("classpath:/static/images/test.png");
        MockMultipartFile mockFile
                = new MockMultipartFile("image",
                "logo.png",
                "image/png",
                resource.getInputStream());


        when(boardService.uploadImages(anyLong(), any())).thenReturn("tempPath");
        mockMvc.perform(
                    multipart("/api/boards/{boardId}/images", 1L)
                            .file(mockFile)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("path").value("tempPath"))
                .andExpect(jsonPath("filename").value("logo.png"))
                .andDo(
                        document("post-board-upload-image",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("boardId").description("Board pk which is writing")
                                ),
                                requestParts(
                                        partWithName("image").description("image file to upload")
                                ),
                                responseFields(
                                        fieldWithPath("path").description("Resource path which is saved"),
                                        fieldWithPath("filename").description("Filename of image file")
                                )
                        )
                );
    }

}

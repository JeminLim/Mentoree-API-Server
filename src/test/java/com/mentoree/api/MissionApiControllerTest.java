package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.mock.WithMockCustomUser;
import com.mentoree.config.WebConfig;
import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.service.MissionService;
import com.mentoree.service.dto.*;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MissionApiController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, AuthorityInterceptor.class})
})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class MissionApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MissionService missionService;

    @Test
    @DisplayName("미션 생성 요청")
    @WithMockCustomUser
    void MissionCreateTest() throws Exception {

        MissionCreateRequestDto createRequest = MissionCreateRequestDto.builder()
                .programId(1L)
                .title("testMission")
                .description("test mission")
                .dueDate(LocalDate.now().plusDays(7))
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        doNothing().when(missionService).create(any(), any());

        mockMvc.perform(
                        post("/api/missions/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(
                        document("post-missions-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("programId").description("Program pk that mission is belonging to"),
                                        fieldWithPath("title").description("Mission name"),
                                        fieldWithPath("description").description("Mission description"),
                                        fieldWithPath("dueDate").description("Mission due date")
                                )
                        )
                );
    }

    @Test
    @DisplayName("미션 수정 요청")
    @WithMockCustomUser
    void MissionUpdateTest() throws Exception {

        MissionCreateRequestDto createRequest = MissionCreateRequestDto.builder()
                .programId(1L)
                .title("renewMission")
                .description("test mission")
                .dueDate(LocalDate.now().plusDays(7))
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        doNothing().when(missionService).update(any(), any());

        mockMvc.perform(
                        post("/api/missions/update/{missionId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document("post-missions-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("missionId").description("program id for update")
                                ),
                                requestFields(
                                        fieldWithPath("programId").description("Program pk that mission is belonging to"),
                                        fieldWithPath("title").description("Updated title"),
                                        fieldWithPath("description").description("Updated description"),
                                        fieldWithPath("dueDate").description("Updated due date")
                                )
                        )
                );
    }

    @Test
    @DisplayName("미션 삭제 요청")
    @WithMockCustomUser
    void MissionDeleteTest() throws Exception {

        doNothing().when(missionService).delete(any());

        mockMvc.perform(
                    delete("/api/missions/{missionId}", 1L).with(csrf())
                ).andExpect(status().isOk())
                .andDo(
                        document("delete-missions",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("missionId").description("program id for delete")
                                )
                        )
                );
    }

    @Test
    @DisplayName("미션 정보 요청")
    @WithMockCustomUser
    void getMissionInfoTest() throws Exception {
        MissionInfoDto expected = MissionInfoDto.builder()
                .id(1L)
                .title("testMission")
                .writerNickname("memberA")
                .writerId(1L)
                .programId(1L)
                .content("test mission")
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        when(missionService.getMissionInfo(any())).thenReturn(expected);

        mockMvc.perform(
                        get("/api/missions/{missionId}", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("mission.id").value(1))
                .andExpect(jsonPath("mission.title").value("testMission"))
                .andDo(
                        document("delete-missions",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("missionId").description("mission id to get")
                                ),
                                responseFields(
                                        fieldWithPath("mission.id").description("mission pk"),
                                        fieldWithPath("mission.programId").description("program pk that mission is belonged to"),
                                        fieldWithPath("mission.writerId").description("writer's pk"),
                                        fieldWithPath("mission.writerNickname").description("writer's nickname"),
                                        fieldWithPath("mission.title").description("mission title"),
                                        fieldWithPath("mission.content").description("mission content"),
                                        fieldWithPath("mission.dueDate").description("mission due date")
                                )
                        )
                );
    }

    @Test
    @DisplayName("미션 리스트 요청")
    @WithMockCustomUser
    void getMissionListTest() throws Exception {
        MissionInfoDto expected = MissionInfoDto.builder()
                .id(1L)
                .title("testMission")
                .writerNickname("memberA")
                .writerId(1L)
                .programId(1L)
                .content("test mission")
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        when(missionService.getMissionInfoList(any(), any())).thenReturn(List.of(expected));

        mockMvc.perform(
                        get("/api/missions/list/{programId}", 1L)
                        .param("expiration", "false")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("missionList.size()").value(1))
                .andDo(
                        document("get-missions-list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program id that missions are belonged")
                                ),requestParameters(
                                        parameterWithName("expiration").description("Whether mission due date is over or not")
                                ),
                                responseFields(
                                        fieldWithPath("missionList[]").description("Mission list"),
                                        fieldWithPath("missionList[].id").description("mission pk"),
                                        fieldWithPath("missionList[].programId").description("program pk that mission is belonged to"),
                                        fieldWithPath("missionList[].writerId").description("writer's pk"),
                                        fieldWithPath("missionList[].writerNickname").description("writer's nickname"),
                                        fieldWithPath("missionList[].title").description("mission title"),
                                        fieldWithPath("missionList[].content").description("mission content"),
                                        fieldWithPath("missionList[].dueDate").description("mission due date")
                                )
                        )
                );

    }

}

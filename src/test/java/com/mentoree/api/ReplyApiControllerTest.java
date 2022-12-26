package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.mock.WithMockCustomUser;
import com.mentoree.config.WebConfig;
import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.generator.DummyDataBuilder;
import com.mentoree.service.ReplyService;
import com.mentoree.service.dto.ReplyCreateRequestDto;
import com.mentoree.service.dto.ReplyInfoDto;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReplyApiController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, AuthorityInterceptor.class})
})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class ReplyApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ReplyService replyService;

    DummyDataBuilder builder = new DummyDataBuilder();

    @Test
    @DisplayName("댓글 등록 테스트")
    @WithMockCustomUser
    void createReplyTest() throws Exception{
        ReplyCreateRequestDto createRequest = ReplyCreateRequestDto.builder()
                .boardId(1L)
                .content("test reply content").build();
        String requestBody = objectMapper.writeValueAsString(createRequest);
        ReplyInfoDto expected = ReplyInfoDto.builder()
                .boardId(1L)
                .memberId(1L)
                .memberNickname("memberANickname")
                .id(1L)
                .modifiedDate(LocalDateTime.now())
                .content(createRequest.getContent())
                .isModified(false)
                .build();

        when(replyService.create(anyLong(), any())).thenReturn(expected);

        mockMvc.perform(
                        post("/api/replies/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("reply.id").value(1L))
                .andExpect(jsonPath("reply.boardId").value(1L))
                .andExpect(jsonPath("reply.memberNickname").value("memberANickname"))
                .andDo(
                        document("post-replies-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("boardId").description("Board id which reply is belonging to"),
                                        fieldWithPath("content").description("Reply content")
                                ),
                                responseFields(
                                        fieldWithPath("reply.id").description("Reply pk"),
                                        fieldWithPath("reply.boardId").description("Board id that reply is belonged to"),
                                        fieldWithPath("reply.memberId").description("Reply writer's pk"),
                                        fieldWithPath("reply.memberNickname").description("Reply writer's nickname"),
                                        fieldWithPath("reply.content").description("Reply content"),
                                        fieldWithPath("reply.modifiedDate").description("Last modified time"),
                                        fieldWithPath("reply.isModified").description("Whether reply is updated"),
                                        fieldWithPath("reply.removal").description("Whether reply is removed or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateReplyTest() throws Exception{

        Map<String, Object> content = new HashMap<>();
        content.put("content", "updated content");
        String requestBody = objectMapper.writeValueAsString(content);

        ReplyInfoDto expected = ReplyInfoDto.builder()
                .boardId(1L)
                .memberId(1L)
                .memberNickname("memberANickname")
                .id(1L)
                .modifiedDate(LocalDateTime.now())
                .content("updated content")
                .isModified(true)
                .build();

        when(replyService.update(anyLong(), any())).thenReturn(expected);

        mockMvc.perform(
                        patch("/api/replies/update/{replyId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("reply.content").value("updated content"))
                .andExpect(jsonPath("reply.isModified").value(true))
                .andDo(
                        document("patch-replies-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                  parameterWithName("replyId").description("Reply id wants to update")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("Update content")
                                ),
                                responseFields(
                                        fieldWithPath("reply.id").description("Reply pk"),
                                        fieldWithPath("reply.boardId").description("Board id that reply is belonged to"),
                                        fieldWithPath("reply.memberId").description("Reply writer's pk"),
                                        fieldWithPath("reply.memberNickname").description("Reply writer's nickname"),
                                        fieldWithPath("reply.content").description("Reply content"),
                                        fieldWithPath("reply.modifiedDate").description("Last modified time"),
                                        fieldWithPath("reply.isModified").description("Whether reply is updated"),
                                        fieldWithPath("reply.removal").description("Whether reply is removed or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 삭제 요청")
    void deleteReplyTest() throws Exception {

        doNothing().when(replyService).remove(anyLong());

        mockMvc.perform(
                        post("/api/replies/remove/{replyId}", 1L)
                ).andExpect(status().isOk())
                .andDo(
                        document("post-replies-remove",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("replyId").description("Reply id to remove")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 리스트 요청")
    void getReplyListTest() throws Exception {
        ReplyInfoDto expected = ReplyInfoDto.builder()
                .removal(false)
                .isModified(false)
                .content("test reply")
                .memberNickname("memberANickname")
                .boardId(1L)
                .memberId(1L)
                .id(1L)
                .build();

        when(replyService.getReplyList(anyLong())).thenReturn(List.of(expected));

        mockMvc.perform(
                        get("/api/replies/list/{boardId}", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("replyList.size()").value(1))
                .andExpect(jsonPath("replyList[0].boardId").value(1))
                .andDo(
                        document("get-replies-list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("boardId").description("Board id that reply is belonged to")
                                ),
                                responseFields(
                                        fieldWithPath("replyList[]").description("Reply list"),
                                        fieldWithPath("replyList[].id").description("Reply pk"),
                                        fieldWithPath("replyList[].boardId").description("Board id that reply is belonged to"),
                                        fieldWithPath("replyList[].memberId").description("Reply writer's pk"),
                                        fieldWithPath("replyList[].memberNickname").description("Reply writer's nickname"),
                                        fieldWithPath("replyList[].content").description("Reply content"),
                                        fieldWithPath("replyList[].modifiedDate").description("Last modified time"),
                                        fieldWithPath("replyList[].isModified").description("Whether reply is updated"),
                                        fieldWithPath("replyList[].removal").description("Whether reply is removed or not")
                                )
                        )
                );


    }


}

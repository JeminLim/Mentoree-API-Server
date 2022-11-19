package com.mentoree.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.domain.entity.Member;
import com.mentoree.exception.DuplicateDataException;
import com.mentoree.service.MemberService;
import com.mentoree.service.dto.MemberSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberAPIController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class MemberAPIControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("이메일 중복 체크")
    void duplicateEmailCheck() throws Exception {
        //given
        String testEmail = "memberA@email.com";
        //when
        when(memberService.duplicateEmailCheck(any())).thenReturn(false);

        //then
        mockMvc.perform(
                get("/api/members/join/check/email")
                .param("email", testEmail))
                .andExpect(status().isOk())
                .andDo(
                        document("get-member-email-check",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParameters(
                                    parameterWithName("email").description("Email that wants to check")
                            ),
                            responseFields(
                                    fieldWithPath("result").description("true - Duplicate email exists, false - Able to sign up")
                            )
                        )
                );
    }

    @Test
    @DisplayName("닉네임 중복 체크")
    void duplicateNicknameCheck() throws Exception {
        //given
        String testNickname = "memberA";
        //when
        when(memberService.duplicateNicknameCheck(any())).thenReturn(false);

        //then
        mockMvc.perform(
                        get("/api/members/join/check/nickname")
                                .param("nickname", testNickname))
                .andExpect(status().isOk())
                .andDo(
                        document("get-member-nickname-check",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("nickname").description("Nickname that wants to check")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("true - Duplicate nickname exists," +
                                                " false - Able to sign up")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원 가입")
    void singUpTest() throws Exception {

        MemberSignUpRequest signUpRequest = MemberSignUpRequest.builder()
                .email("member@email.com")
                .password("1234QWer!@")
                .nickname("memberA")
                .build();

        doNothing().when(memberService).signUp(any());

        mockMvc.perform(
                post("/api/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest))
                ).andExpect(status().isCreated())
                .andDo(
                    document("post-member-join",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("email").description("User email"),
                                    fieldWithPath("password").description("User password"),
                                    fieldWithPath("nickname").description("User nickname")
                            )
                    )
                );
    }

}
